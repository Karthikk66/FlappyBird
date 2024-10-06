import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;

    // images
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    // bird class
    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird {
        int x;
        int y;
        int width;
        int height;
        Image img;

        Bird(Image img) {
            this.img = img;
            this.x = birdX;
            this.y = birdY;
            this.width = birdWidth;
            this.height = birdHeight;
        }
    }

    // pipe class
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64; // scaled by 1/6
    int pipeHeight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    // game logic
    Bird bird;
    int velocityX = -4; // move pipes to the left speed
    int velocityY = 0;  // initial vertical velocity of the bird
    int gravity = 1; // gravity strength

    ArrayList<Pipe> pipes;

    Timer gameLoop;
    Timer placePipeTimer;
    boolean gameOver = false;
    double score = 0;

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this); // Uncommented to capture key events

        // Load images (add error handling)
        backgroundImg = loadImage("./flappybirdbg.png");
        birdImg = loadImage("./flappybird.png");
        topPipeImg = loadImage("./toppipe.png");
        bottomPipeImg = loadImage("./bottompipe.png");

        // Initialize bird
        bird = new Bird(birdImg);
        pipes = new ArrayList<>();

        // Place pipes timer
        placePipeTimer = new Timer(1500, e -> placePipes());
        placePipeTimer.start();

        // Game timer
        gameLoop = new Timer(1000 / 60, this); // 60 frames per second
        gameLoop.start();

        requestFocusInWindow(); // Ensure the game gets focus to capture key events
    }

    // Utility to load images
    private Image loadImage(String path) {
        Image img = null;
        try {
            img = new ImageIcon(getClass().getResource(path)).getImage();
        } catch (Exception e) {
            System.out.println("Error loading image: " + path);
        }
        return img;
    }

    void placePipes() {
        int randomPipeY = (int) (Math.random() * (boardHeight / 2));
        int openingSpace = boardHeight / 4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY - pipeHeight; // Adjusting top pipe position
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = randomPipeY + openingSpace; // Adjusting bottom pipe position
        pipes.add(bottomPipe);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // Draw background
        g.drawImage(backgroundImg, 0, 0, this.boardWidth, this.boardHeight, null);

        // Draw bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        // Draw pipes
        for (Pipe pipe : pipes) {
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // Draw score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("Game Over: " + (int) score, 10, 35);
        } else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    public void move() {
        // Apply gravity to bird
        velocityY += gravity; // Apply gravity
        bird.y += velocityY; // Update bird's vertical position

        // Prevent bird from moving below the bottom
        if (bird.y > boardHeight - bird.height) {
            bird.y = boardHeight - bird.height;
            gameOver = true; // Game over if the bird hits the ground
        }

        // Pipe movement
        for (Pipe pipe : pipes) {
            pipe.x += velocityX;

            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                score += 0.5;
                pipe.passed = true;
            }

            if (collision(bird, pipe)) {
                gameOver = true;
            }
        }
    }

    boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width && a.x + a.width > b.x &&
               a.y < b.y + b.height && a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();

        if (gameOver) {
            placePipeTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (gameOver) {
                resetGame(); // Restart game if it's over
            } else {
                velocityY = -10; // Jump when space is pressed
            }
        }
    }

    private void resetGame() {
        bird.y = boardHeight / 2; // Reset bird's position
        velocityY = 0; // Reset velocity
        pipes.clear(); // Clear pipes
        gameOver = false; // Reset game over flag
        score = 0; // Reset score
        gameLoop.start(); // Restart game loop
        placePipeTimer.start(); // Restart pipe placement
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}

    // Main method to run the game
    public static void main(String[] args) {
        JFrame frame = new JFrame("Flappy Bird");
        FlappyBird game = new FlappyBird();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(game);
        frame.pack();
        frame.setVisible(true);
    }
}
