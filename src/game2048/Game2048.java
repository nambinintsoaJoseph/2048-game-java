package game2048;

import javax.swing.*;

public class Game2048 extends JFrame {
    
    public Game2048() {
        setTitle("2048");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Ajout de la sélection de niveau
        Object[] options = {"Niveau 1 (512)", "Niveau 2 (1024)", "Niveau 3 (2048)"};
        int choice = JOptionPane.showOptionDialog(this, 
            "Choisissez votre niveau:", 
            "Sélection de niveau",
            JOptionPane.DEFAULT_OPTION, 
            JOptionPane.QUESTION_MESSAGE,
            null,
            options, 
            options[0]);
        
        int target = 2048; // Par défaut niveau 3
        int timeLimit = 300; // 5 minutes pour le niveau 3
        if (choice == 0) {
            target = 512;
            timeLimit = 180; // 3 minutes pour le niveau 1
        } else if (choice == 1) {
            target = 1024;
            timeLimit = 240; // 4 minutes pour le niveau 2
        }
        
        GamePanel gamePanel = new GamePanel(target, timeLimit);
        add(gamePanel);
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Game2048();
        });
    }
}