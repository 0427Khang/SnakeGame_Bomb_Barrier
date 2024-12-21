package Main;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
	// Size game unit
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNIT = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static final int DELAY = 200;
    Timer timer1;
    //Snake
    final int x[] = new int[GAME_UNIT];
    final int y[] = new int[GAME_UNIT];
    int bodyParts = 3;
    Image snakeHeadUp;
    Image snakeHeadDown;
    Image snakeHeadLeft;
    Image snakeHeadRight;
    Image snakeTailUp;
    Image snakeTailDown;
    Image snakeTailLeft;
    Image snakeTailRight;    
    Image snakeBodyUp;
    Image snakeBodyDown;
    Image snakeBodyLeft;
    Image snakeBodyRight;
    //Apple
    Image appleImage;
    int appleEaten;
    int appleX;
    int appleY;
    //Barrier
    int numBarriers;
    int barrierX[] = new int[numBarriers];
    int barrierY[] = new int[numBarriers];
    Image barrierImage;
    //Direction
    long lastDirectionChangeTime = 0;
    static final long DIRECTION_CHANGE_DELAY = 150; // milliseconds
    //Level
    int level = 1;
    int applesForNextLevel = 3;
    static final int APPLES_PER_LEVEL = 3;
    static final int MAX_LEVEL = 4;
    
    // Bomb variables
    static int NUM_BOMBS = 4;
    int bombX[] = new int[NUM_BOMBS];
    int bombY[] = new int[NUM_BOMBS];
    boolean bombExploded[] = new boolean[NUM_BOMBS];
    long bombSpawnTime[] = new long[NUM_BOMBS];
    Image bombImage;
    Image explorecenterImage;
    Image exploreupImage;
    Image exploredownImage;
    Image explorerightImage;
    Image exploreleftImage;
    // Explosion radius multiplier
    private int explosionRadiusMultiplier = 1;
    
    char direction = 'D';
    boolean running = false;
    private Timer timer;
    Random random;
    //Background
    Image backgroundImage;

    
//////////////////////////////////////////////////////////////////////////////////
    GamePanel() throws IOException {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(new Color(32,32,32));
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        appleImage = new ImageIcon("C:\\Users\\84333\\eclipse-workspace\\SnakeGameProject\\src\\resources\\apple.png").getImage();
        snakeHeadUp = new ImageIcon("C:\\Users\\84333\\eclipse-workspace\\SnakeGameProject\\src\\resources\\snakeheadup.png").getImage();
        snakeHeadDown = new ImageIcon("C:\\Users\\84333\\eclipse-workspace\\SnakeGameProject\\src\\resources\\snakeheaddown.png").getImage();
        snakeHeadLeft = new ImageIcon("C:\\Users\\84333\\eclipse-workspace\\SnakeGameProject\\src\\resources\\snakeheadleft.png").getImage();
        snakeHeadRight = new ImageIcon("C:\\Users\\84333\\eclipse-workspace\\SnakeGameProject\\src\\resources\\snakeheadright.png").getImage();
        snakeBodyRight = new ImageIcon("C:\\Users\\84333\\eclipse-workspace\\SnakeGameProject\\src\\resources\\snakebodyleftright.png").getImage();
        snakeBodyUp = new ImageIcon("C:\\Users\\84333\\eclipse-workspace\\SnakeGameProject\\src\\resources\\snakebodyupdown.png").getImage();
        snakeBodyDown= new ImageIcon("C:\\Users\\84333\\eclipse-workspace\\SnakeGameProject\\src\\resources\\snakebodyupdown.png").getImage();
        snakeBodyLeft = new ImageIcon("C:\\Users\\84333\\eclipse-workspace\\SnakeGameProject\\src\\resources\\snakebodyleftright.png").getImage();
        snakeTailLeft = new ImageIcon("C:\\Users\\84333\\eclipse-workspace\\SnakeGameProject\\src\\resources\\snaketailright.png").getImage();
        snakeTailDown = new ImageIcon("C:\\Users\\84333\\eclipse-workspace\\SnakeGameProject\\src\\resources\\snaketailup.png").getImage();
        snakeTailUp = new ImageIcon("C:\\Users\\84333\\eclipse-workspace\\SnakeGameProject\\src\\resources\\snaketaildown.png").getImage();
        snakeTailRight = new ImageIcon("C:\\Users\\84333\\eclipse-workspace\\SnakeGameProject\\src\\resources\\snaketailleft.png").getImage();
        barrierImage = new ImageIcon("C:\\Users\\84333\\eclipse-workspace\\SnakeGameProject\\src\\resources\\barrier.png").getImage();
        bombImage = new ImageIcon("C:\\Users\\84333\\eclipse-workspace\\SnakeGameProject\\src\\resources\\bomb.gif").getImage();
        explorecenterImage = new ImageIcon("C:\\Users\\84333\\eclipse-workspace\\SnakeGameProject\\src\\resources\\exploding.png").getImage();
        exploreupImage = new ImageIcon("C:\\Users\\84333\\eclipse-workspace\\SnakeGameProject\\src\\resources\\exploding.png").getImage();
        exploredownImage = new ImageIcon("C:\\Users\\84333\\eclipse-workspace\\SnakeGameProject\\src\\resources\\exploding.png").getImage();
        explorerightImage = new ImageIcon("C:\\Users\\84333\\eclipse-workspace\\SnakeGameProject\\src\\resources\\exploding.png").getImage();
        exploreleftImage = new ImageIcon("C:\\Users\\84333\\eclipse-workspace\\SnakeGameProject\\src\\resources\\exploding.png").getImage();
        backgroundImage = new ImageIcon("C:\\Users\\84333\\eclipse-workspace\\SnakeGameProject\\src\\resources\\background.png").getImage();
        timer = new Timer(100, e -> updateUI());
        timer.start();
        startGame();
    }
///////////////////////////////////////////////////////////////////////////////    
    public void startGame() {
        if (timer1 != null) {
            timer1.stop();
        }
        level = 1;
        applesForNextLevel = APPLES_PER_LEVEL;
        explosionRadiusMultiplier = 1; // Reset explosion radius multiplier
        newApple();
        newBarrier();
        newBombs();
        running = true;
        timer1 = new Timer(DELAY, this);
        timer1.start();
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void increaseLevel() {
        if (level < MAX_LEVEL) {
            level++;
            applesForNextLevel += APPLES_PER_LEVEL;
            timer1.setDelay(DELAY - (level - 1) * 40); // Increase speed
         // Clear previous barriers 
            barrierX = new int[0];
            barrierY = new int[0];
            
            updateBombExplosionRadius(); 
            newBarrier(); 
        }
    }
    
    public void updateBombExplosionRadius() {
        explosionRadiusMultiplier = level; // Radius increases with each level
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g) {
        if (running) {
        	//Background
        	g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        	// Apple
        	g.drawImage(appleImage, appleX, appleY, UNIT_SIZE, UNIT_SIZE, this);
        	// Draw Snake Head, Body, and Tail
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) { // Snake Head
                    if (direction == 'U') {
                        g.drawImage(snakeHeadUp, x[i], y[i], UNIT_SIZE, UNIT_SIZE, this);
                    } else if (direction == 'D') {
                        g.drawImage(snakeHeadDown, x[i], y[i], UNIT_SIZE, UNIT_SIZE, this);
                    } else if (direction == 'L') {
                        g.drawImage(snakeHeadLeft, x[i], y[i], UNIT_SIZE, UNIT_SIZE, this);
                    } else if (direction == 'R') {
                        g.drawImage(snakeHeadRight, x[i], y[i], UNIT_SIZE, UNIT_SIZE, this);
                    }
                } else if (i == bodyParts - 1) { // Snake Tail
                    // Determine direction of the tail (facing opposite of second-to-last part)
                    if (x[i] < x[i - 1]) {
                        g.drawImage(snakeTailLeft, x[i], y[i], UNIT_SIZE, UNIT_SIZE, this);
                    } else if (x[i] > x[i - 1]) {
                        g.drawImage(snakeTailRight, x[i], y[i], UNIT_SIZE, UNIT_SIZE, this);
                    } else if (y[i] < y[i - 1]) {
                        g.drawImage(snakeTailUp, x[i], y[i], UNIT_SIZE, UNIT_SIZE, this);
                    } else if (y[i] > y[i - 1]) {
                        g.drawImage(snakeTailDown, x[i], y[i], UNIT_SIZE, UNIT_SIZE, this);
                    }
                } else { // Snake Body
                    if (x[i] < x[i - 1]) { // Moving Left
                        g.drawImage(snakeBodyLeft, x[i], y[i], UNIT_SIZE, UNIT_SIZE, this);
                    } else if (x[i] > x[i - 1]) { // Moving Right
                        g.drawImage(snakeBodyRight, x[i], y[i], UNIT_SIZE, UNIT_SIZE, this);
                    } else if (y[i] < y[i - 1]) { // Moving Up
                        g.drawImage(snakeBodyUp, x[i], y[i], UNIT_SIZE, UNIT_SIZE, this);
                    } else if (y[i] > y[i - 1]) { // Moving Down
                        g.drawImage(snakeBodyDown, x[i], y[i], UNIT_SIZE, UNIT_SIZE, this);
                    }
                }
            }
            
            // Barrier
            for (int u = 0; u < numBarriers; u++) {
                g.drawImage(barrierImage,barrierX[u], barrierY[u], UNIT_SIZE + 5, UNIT_SIZE + 5, this);
            }

            // Draw bombs
            for (int i = 0; i < NUM_BOMBS; i++) {
                if (!bombExploded[i]) {
                    g.drawImage(bombImage,bombX[i], bombY[i], UNIT_SIZE + 10, UNIT_SIZE + 10, this);
                } else {
                    // Draw explosion
                    for (int r = 0; r <= explosionRadiusMultiplier; r++) {
                    	g.drawImage(explorecenterImage, bombX[i], bombY[i], UNIT_SIZE, UNIT_SIZE, this); //center
                        g.drawImage(exploreleftImage, bombX[i] - UNIT_SIZE * r, bombY[i], UNIT_SIZE, UNIT_SIZE, this); // left
                        g.drawImage(explorerightImage, bombX[i] + UNIT_SIZE * r, bombY[i], UNIT_SIZE, UNIT_SIZE, this); // right
                        g.drawImage(exploreupImage, bombX[i], bombY[i] - UNIT_SIZE * r, UNIT_SIZE, UNIT_SIZE, this); // up
                        g.drawImage(exploredownImage, bombX[i], bombY[i] + UNIT_SIZE * r, UNIT_SIZE, UNIT_SIZE, this); // down
                    }
                }
            }
            //Draw Level
            g.setColor(Color.white);
            g.setFont(new Font("Nordic Light", Font.PLAIN, 25));
            g.drawString("Level: " + level, 10, 25);
            //Score
            g.setColor(Color.red);
            g.setFont(new Font("Nordic Light", Font.PLAIN, 35));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + appleEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + appleEaten)) / 2, g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void movement() {
        // Move the body parts starting from the tail (last part)
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        // Move the head in the current direction
        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////
    private boolean isPositionOccupied(int x, int y) {
        // Make sure position don't overlaps with snake
        for (int i = 0; i < bodyParts; i++) {
            if (x == this.x[i] && y == this.y[i]) {
                return true;
            }
        }
        
        // Make sure position don't overlaps with apple
        if (x == appleX && y == appleY) {
            return true;
        }
        
        // Make sure position don't overlaps with barriers
        for (int i = 0; i < numBarriers; i++) {
            if (x == barrierX[i] && y == barrierY[i]) {
                return true;
            }
        }
        
        // Make sure position don't overlaps with bombs
        for (int i = 0; i < NUM_BOMBS; i++) {
            if (x == bombX[i] && y == bombY[i]) {
                return true;
            }
        }
        
        return false;
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////
    private boolean isNearSnakeHead(int x, int y) {
        // Check if the position is within 2 unit of the snake head
        return (Math.abs(x - this.x[0]) <= 2*UNIT_SIZE && Math.abs(y - this.y[0]) <= 2*UNIT_SIZE);
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////    
    public void newApple() {
        int x, y;
        do {
            x = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
            y = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
        } while (isPositionOccupied(x, y));
        
        appleX = x;
        appleY = y;
    }

    public void newBarrier() {
    	//Generate random from 0 to 8 with minimum 10 barrier
        numBarriers = random.nextInt(8) + 10 + (level - 1) * 2; // Add 2 more barriers per level
        barrierX = new int[numBarriers];
        barrierY = new int[numBarriers];
        for (int i = 0; i < numBarriers; i++) {
            int x, y;
            do {
                x = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE - 5)) * UNIT_SIZE + 2 * UNIT_SIZE;
                y = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE - 5)) * UNIT_SIZE + 2 * UNIT_SIZE;
            } while (isPositionOccupied(x, y) || isNearSnakeHead(x, y)); // make sure these do not spawn near the head
            
            barrierX[i] = x;
            barrierY[i] = y;
        }
    }

    public void newBombs() {
        int numBombs = NUM_BOMBS ;
        bombX = new int[numBombs];
        bombY = new int[numBombs];
        bombExploded = new boolean[numBombs];
        bombSpawnTime = new long[numBombs];
        for (int i = 0; i < numBombs; i++) {
            int x, y;
            do {
                x = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
                y = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
            } while (isPositionOccupied(x, y) || isNearSnakeHead(x, y)); // make sure these do not spawn near the head
            
            bombX[i] = x;
            bombY[i] = y;
            bombExploded[i] = false;
            bombSpawnTime[i] = System.currentTimeMillis();
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void move1() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }   
//////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            appleEaten++;
            newApple();
            newBombs();
            if (appleEaten >= applesForNextLevel) {
                increaseLevel();
            }
        }
    }

    public void checkBarrierCollision() {
        for (int i = 0; i < numBarriers; i++) {
            if (x[0] == barrierX[i] && y[0] == barrierY[i]) {
                running = false;
                break; // exit the loop if a collision is detected
            }
        }
    }
    
    public void checkBombCollision() {
        long currentTime = System.currentTimeMillis();

        for (int i = 0; i < bombX.length; i++) {
            // Check if the bomb has exploded after its timer
            if (!bombExploded[i] && (currentTime - bombSpawnTime[i] >= 2000)) {
                bombExploded[i] = true;
            }

            if (bombExploded[i]) {
                // Check collision with the extended explosion area
                for (int r = -explosionRadiusMultiplier; r <= explosionRadiusMultiplier; r++) {
                    if ((Math.abs(x[0] - bombX[i]) == UNIT_SIZE * Math.abs(r) && y[0] == bombY[i]) || // horizontal explosion
                        (Math.abs(y[0] - bombY[i]) == UNIT_SIZE * Math.abs(r) && x[0] == bombX[i]) || // vertical explosion
                        (x[0] == bombX[i] && y[0] == bombY[i])) { // center
                        running = false; // Game over
                        break;
                    }
                }
            } else {
                // Check collision with the bomb before it explodes
                if (x[0] == bombX[i] && y[0] == bombY[i]) {
                    running = false; // Game over
                    break;
                }
            }
        }
    }


    public void checkCollisions() {
        // checks if head collides with body
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }
        // checks if head touches left border
        if (x[0] < 0) {
            running = false;
        }
        // checks if head touches right border
        if (x[0] >= SCREEN_WIDTH) {
            running = false;
        }
        // checks if head touches top border
        if (y[0] < 0) {
            running = false;
        }
        // checks if head touches bottom border
        if (y[0] >= SCREEN_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer1.stop();
        }
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void gameOver(Graphics g) {
        // Game Over text
        g.setColor(Color.red);
        g.setFont(new Font("Nordic Light", Font.BOLD, 75));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics1.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 3);

        // Final Score
        g.setColor(Color.red);
        g.setFont(new Font("Nordic Light", Font.PLAIN, 35));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Score: " + appleEaten, (SCREEN_WIDTH - metrics2.stringWidth("Score: " + appleEaten)) / 2, SCREEN_HEIGHT / 2);

        // Final Level
        g.setColor(Color.white);
        g.setFont(new Font("Nordic Light", Font.PLAIN, 35));
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        g.drawString("Level: " + level, (SCREEN_WIDTH - metrics3.stringWidth("Level: " + level)) / 2, SCREEN_HEIGHT / 2 + 40);

        // Press to restart
        g.setColor(Color.white);
        g.setFont(new Font("Nordic Light", Font.PLAIN, 35));
        FontMetrics metrics4 = getFontMetrics(g.getFont());
        g.drawString("Press Enter to Restart", (SCREEN_WIDTH - metrics4.stringWidth("Press Enter to Restart")) / 2, SCREEN_HEIGHT * 3 / 4);
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move1();
            checkApple();
            checkBarrierCollision();
            checkBombCollision();
            checkCollisions();
        }
        repaint();
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {

            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                timer1.start();
            
                bodyParts = 4;
                direction = 'D'; // Reset direction 
                x[0] = SCREEN_WIDTH / 2; // Reset snake head 
                y[0] = SCREEN_HEIGHT / 2;

                // Reset score
                appleEaten = 0;

                // Start the game
                startGame();

                // Request focus for key events
                requestFocusInWindow();
            }
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastDirectionChangeTime < DIRECTION_CHANGE_DELAY) {
                return; // Ignore rapid key presses
            }


            switch (e.getKeyCode()) {
                case KeyEvent.VK_A:
                    if (direction != 'R') {
                        direction = 'L';
                        lastDirectionChangeTime = currentTime;
                    }
                    break;
                case KeyEvent.VK_D:
                    if (direction != 'L') {
                        direction = 'R';
                        lastDirectionChangeTime = currentTime;
                    }
                    break;
                case KeyEvent.VK_W:
                    if (direction != 'D') {
                        direction = 'U';
                        lastDirectionChangeTime = currentTime;
                    }
                    break;
                case KeyEvent.VK_S:
                    if (direction != 'U') {
                        direction = 'D';
                        lastDirectionChangeTime = currentTime;
                    }
                    break;
            }
        }
    }
}

