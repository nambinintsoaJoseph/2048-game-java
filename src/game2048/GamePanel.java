package game2048;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.swing.Timer;

public class GamePanel extends JPanel {
    private int[][] board;
    private JLabel[][] tiles;
    private final int size = 4;
    private int score;
    private int target;
    private int timeLeft;
    private Timer timer;
    private JLabel timeLabel;
    private boolean gameActive;

    public GamePanel(int target, int timeLimit) {
        this.target = target;
        this.timeLeft = timeLimit;
        this.gameActive = true;
        
        setLayout(new BorderLayout());
        
        // Panel pour le jeu
        JPanel gameBoard = new JPanel();
        gameBoard.setLayout(new GridLayout(size, size));
        gameBoard.setPreferredSize(new Dimension(400, 400));
        
        // Panel pour les informations (score et temps)
        JPanel infoPanel = new JPanel(new GridLayout(1, 2));
        timeLabel = new JLabel("Temps: " + formatTime(timeLeft), SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        JLabel scoreLabel = new JLabel("Score: " + score, SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        infoPanel.add(timeLabel);
        infoPanel.add(scoreLabel);
        
        add(infoPanel, BorderLayout.NORTH);
        add(gameBoard, BorderLayout.CENTER);

        // Initialisation des tableaux
        board = new int[size][size];
        tiles = new JLabel[size][size];
        score = 0;

        initializeBoard();
        addInitialTiles();
        setupUI(gameBoard);
        
        // Configuration du timer
        timer = new Timer(1000, e -> {
            if (gameActive) {
                timeLeft--;
                timeLabel.setText("Temps: " + formatTime(timeLeft));
                if (timeLeft <= 0) {
                    gameActive = false;
                    timer.stop();
                    JOptionPane.showMessageDialog(this, "Temps écoulé! Game Over! Score: " + score);
                }
            }
        });
        timer.start();
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!gameActive) return;
                
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
                    scoreLabel.setText("Score: " + score);
                    if (hasWon()) {
                        gameActive = false;
                        timer.stop();
                        JOptionPane.showMessageDialog(GamePanel.this, 
                            "Félicitations! Vous avez atteint " + target + "! Score: " + score);
                    } else if (isGameOver()) {
                        gameActive = false;
                        timer.stop();
                        JOptionPane.showMessageDialog(GamePanel.this, "Game Over! Score: " + score);
                    }
                }
            }
        });
        setFocusable(true);
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }

    private void setupUI(JPanel gameBoard) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                JLabel label = new JLabel("", SwingConstants.CENTER);
                label.setPreferredSize(new Dimension(100, 100));
                label.setOpaque(true);
                label.setBackground(new Color(0xcdc1b4));
                label.setFont(new Font("Arial", Font.BOLD, 32));
                tiles[i][j] = label;
                gameBoard.add(label);
            }
        }
        refreshTiles();
    }

    private boolean hasWon() {
        for (int[] row : board) {
            for (int num : row) {
                if (num >= target) {
                    return true;
                }
            }
        }
        return false;
    }

    // Le reste des méthodes reste inchangé...
    private void initializeBoard() {
        for (int[] row : board) {
            Arrays.fill(row, 0);
        }
    }

    private void addInitialTiles() {
        addNewTile();
        addNewTile();
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