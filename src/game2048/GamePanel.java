package game2048;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GamePanel extends JPanel {
    private int[][] board;
    private JLabel[][] tiles;
    private final int size = 4;
    private int score;

    public GamePanel() {
        // Initialisation des tableaux en premier
        board = new int[size][size];
        tiles = new JLabel[size][size];
        
        setPreferredSize(new Dimension(400, 400));
        setLayout(new GridLayout(size, size));
        score = 0;

        initializeBoard();
        addInitialTiles();
        setupUI();
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                boolean moved = false;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:  moved = moveLeft();  break;
                    case KeyEvent.VK_RIGHT: moved = moveRight(); break;
                    case KeyEvent.VK_UP:    moved = moveUp();    break;
                    case KeyEvent.VK_DOWN:  moved = moveDown();  break;
                }
                
                if (moved) {
                    addNewTile();
                    refreshTiles();
                    if (isGameOver()) {
                        JOptionPane.showMessageDialog(GamePanel.this, "Game Over! Score: " + score);
                    }
                }
            }
        });
        setFocusable(true);
    }

    private void initializeBoard() {
        for (int[] row : board) {
            Arrays.fill(row, 0);
        }
    }

    private void addInitialTiles() {
        addNewTile();
        addNewTile();
    }

    private void setupUI() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                JLabel label = new JLabel("", SwingConstants.CENTER);
                label.setPreferredSize(new Dimension(100, 100));
                label.setOpaque(true);
                label.setBackground(new Color(0xcdc1b4));
                label.setFont(new Font("Arial", Font.BOLD, 32));
                tiles[i][j] = label;
                add(label);
            }
        }
        refreshTiles();
    }

    private void refreshTiles() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int value = board[i][j];
                JLabel label = tiles[i][j];
                label.setText(value == 0 ? "" : String.valueOf(value));
                label.setBackground(getTileColor(value));
                label.setForeground(value < 16 ? new Color(0x776e65) : new Color(0xf9f6f2));
            }
        }
        repaint();
    }

    private Color getTileColor(int value) {
        switch (value) {
            case 2:    return new Color(0xeee4da);
            case 4:    return new Color(0xede0c8);
            case 8:    return new Color(0xf2b179);
            case 16:   return new Color(0xf59563);
            case 32:   return new Color(0xf67c5f);
            case 64:   return new Color(0xf65e3b);
            case 128:  return new Color(0xedcf72);
            case 256:  return new Color(0xedcc61);
            case 512:  return new Color(0xedc850);
            case 1024: return new Color(0xedc53f);
            case 2048: return new Color(0xedc22e);
            default:   return new Color(0xcdc1b4);
        }
    }

    private boolean moveLeft() {
        boolean moved = false;
        for (int i = 0; i < size; i++) {
            int[] newRow = processRow(board[i]);
            if (!Arrays.equals(board[i], newRow)) {
                board[i] = newRow;
                moved = true;
            }
        }
        return moved;
    }

    private int[] processRow(int[] row) {
        List<Integer> nonZero = new ArrayList<>();
        for (int num : row) {
            if (num != 0) nonZero.add(num);
        }

        for (int i = 0; i < nonZero.size() - 1; i++) {
            if (nonZero.get(i).equals(nonZero.get(i + 1))) {
                nonZero.set(i, nonZero.get(i) * 2);
                score += nonZero.get(i);
                nonZero.remove(i + 1);
            }
        }

        int[] newRow = new int[size];
        Arrays.fill(newRow, 0);
        for (int i = 0; i < nonZero.size(); i++) {
            newRow[i] = nonZero.get(i);
        }
        return newRow;
    }

    private boolean moveRight() {
        reverseRows();
        boolean moved = moveLeft();
        reverseRows();
        return moved;
    }

    private boolean moveUp() {
        transpose();
        boolean moved = moveLeft();
        transpose();
        return moved;
    }

    private boolean moveDown() {
        transpose();
        boolean moved = moveRight();
        transpose();
        return moved;
    }

    private void transpose() {
        int[][] newBoard = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                newBoard[j][i] = board[i][j];
            }
        }
        board = newBoard;
    }

    private void reverseRows() {
        for (int[] row : board) {
            for (int i = 0; i < size / 2; i++) {
                int temp = row[i];
                row[i] = row[size - 1 - i];
                row[size - 1 - i] = temp;
            }
        }
    }

    private void addNewTile() {
        List<Point> emptyCells = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == 0) {
                    emptyCells.add(new Point(i, j));
                }
            }
        }
        
        if (!emptyCells.isEmpty()) {
            Point cell = emptyCells.get(new Random().nextInt(emptyCells.size()));
            board[cell.x][cell.y] = Math.random() < 0.9 ? 2 : 4;
        }
    }

    private boolean isGameOver() {
        if (hasEmptyTile()) return false;
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if ((j < size - 1 && board[i][j] == board[i][j + 1]) ||
                    (i < size - 1 && board[i][j] == board[i + 1][j])) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean hasEmptyTile() {
        for (int[] row : board) {
            for (int num : row) {
                if (num == 0) return true;
            }
        }
        return false;
    }
}