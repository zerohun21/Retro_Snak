import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.File;

public class SnakeGame extends JFrame {

    public SnakeGame() {
        this.add(new GamePanel());
        this.setTitle("Snake Game");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        playBackgroundMusic("resources/sounds/sbg.wav");
    }

    private void playBackgroundMusic(String filePath) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }




    public static void main(String[] args) {
        new SnakeGame();
    }
}

class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 800;
    static final int SCREEN_HEIGHT = 800;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    int DELAY = 100;
    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;
    int animal;

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }
    private void playSoundEffect(String filePath) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start(); // 단회 재생
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void startGame() {
        for (int i = 0; i < bodyParts; i++) {
            x[i] = SCREEN_WIDTH / 2 - i * UNIT_SIZE;
            y[i] = SCREEN_HEIGHT / 2;
        }
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
        animal = 2;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    switch (animal) {
                        case 0:
                            g.setColor(Color.darkGray); // 검정색 대체
                            break;
                        case 1:
                            g.setColor(Color.white);
                            break;
                        case 2:
                            g.setColor(Color.pink);
                            break;
                        case 3:
                            g.setColor(Color.blue);
                            break;
                        default:
                            g.setColor(new Color(255, 165, 0)); // 기본 주황색
                            break;
                    }
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    switch (animal) {
                        case 0:
                            g.setColor(Color.darkGray.darker());
                            break;
                        case 1:
                            g.setColor(Color.white.darker());
                            break;
                        case 2:
                            g.setColor(Color.pink.darker());
                            break;
                        case 3:
                            g.setColor(Color.blue.darker());
                            break;
                        default:
                            g.setColor(new Color(255, 165, 0).darker());
                            break;
                    }
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }

            g.setColor(Color.green);
            g.fillRect(0, 0, SCREEN_WIDTH, UNIT_SIZE);
            g.fillRect(0, 0, UNIT_SIZE, SCREEN_HEIGHT);
            g.fillRect(SCREEN_WIDTH - UNIT_SIZE, 0, UNIT_SIZE, SCREEN_HEIGHT);
            g.fillRect(0, SCREEN_HEIGHT - UNIT_SIZE, SCREEN_WIDTH, UNIT_SIZE);

        } else {
            gameOver(g);
        }
    }

    public void newApple() {
        appleX = random.nextInt((SCREEN_WIDTH / UNIT_SIZE) - 2) * UNIT_SIZE + UNIT_SIZE;
        appleY = random.nextInt((SCREEN_HEIGHT / UNIT_SIZE) - 2) * UNIT_SIZE + UNIT_SIZE;
    }

    public void move() {
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

    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
            if (DELAY > 10) {
                DELAY -= 5;
                timer.setDelay(DELAY);
            }
            playSoundEffect("resources/sounds/sitem.wav");
        }
    }

    public void checkCollisions() {
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }

        if (x[0] < UNIT_SIZE || x[0] >= SCREEN_WIDTH - UNIT_SIZE || y[0] < UNIT_SIZE || y[0] >= SCREEN_HEIGHT - UNIT_SIZE) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {
        // Game Over text and score display
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        int finalScore = applesEaten * 100;
        g.drawString("Coin: " + finalScore, (SCREEN_WIDTH - metrics2.stringWidth("Coin: " + finalScore)) / 2, g.getFont().getSize() + SCREEN_HEIGHT / 2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
        }
    }
}
