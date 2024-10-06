import javax.swing.*;

public class App {
    public static void main(String[] args) {
        // Create the game window (JFrame)
        JFrame frame = new JFrame("Flappy Bird");

        // Create an instance of the FlappyBird game panel
        FlappyBird game = new FlappyBird();

        // Set up the frame properties
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(game);                // Add the FlappyBird panel to the frame
        frame.pack();                   // Automatically size the window based on its content
        frame.setVisible(true);          // Make the window visible
        frame.setResizable(false);       // Prevent resizing
        frame.setLocationRelativeTo(null); // Center the window on the screen
    }
}
