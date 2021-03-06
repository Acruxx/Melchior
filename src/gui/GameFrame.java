package gui;

import controllers.GameController;
import handlers.SoundHandler;
import score.HighscoreEntry;
import score.HighscoreList;
import towers.BasicTowerFactory;
import towers.PlagueTowerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import static controllers.GameConstants.*;

/**
 * Created by Holmgr 2015-03-04
 * The Frame that holds our GameComponent and handles all menus and such. Also redirect some mouse and keyboard
 * events to the controller for buying, selling and upgrading towers etc.
 */
public class GameFrame extends JFrame {

    private JLabel healthLabel, cashLabel, roundLabel, counterLabel;

    private JButton buyBasicTowerButton, buyPlagueTowerButton,
            upgradeTowerButton, sellTowerButton;
    private GameController controller;

    public GameFrame(GameComponent gameComponent, int health, int cash, GameController controller) throws HeadlessException {
        super("Melchior");

        this.controller = controller;
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.setLayout(new BorderLayout());
        this.add(gameComponent, BorderLayout.CENTER);
        createMenus();
        createStatusPanel(health, cash);
        setupBindings();

        this.pack();
        this.setVisible(true);
        this.setResizable(RESIZABLE);
    }

    private void setupBindings() {

        ActionListener buyBasicTowerAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.buyTower(new BasicTowerFactory());
            }
        };
        buyBasicTowerButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("Q"), "buyBasic");
        buyBasicTowerButton.getActionMap().put("buyBasic", (Action) buyBasicTowerAction);
        buyBasicTowerButton.addActionListener(buyBasicTowerAction);

        ActionListener buyPlagueTowerAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.buyTower(new PlagueTowerFactory());
            }
        };
        buyPlagueTowerButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("W"), "buyPlague");
        buyPlagueTowerButton.getActionMap().put("buyPlague", (Action) buyPlagueTowerAction);
        buyPlagueTowerButton.addActionListener(buyPlagueTowerAction);

        ActionListener upgradeTowerAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.upgradeTower();
            }
        };
        upgradeTowerButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("U"), "upgrade");
        upgradeTowerButton.getActionMap().put("upgrade", (Action) upgradeTowerAction);
        upgradeTowerButton.addActionListener(upgradeTowerAction);

        ActionListener sellTowerAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.sellTower();
            }
        };
        sellTowerButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("S"), "sell");
        sellTowerButton.getActionMap().put("sell", (Action) sellTowerAction);
        sellTowerButton.addActionListener(sellTowerAction);
    }

    private void createStatusPanel(int health, int cash) {

        final Color backgroundColor = new Color(69, 69, 69); // Some fancy color

        JPanel panel = new JPanel();
        panel.setBackground(backgroundColor);
        panel.setLayout(new BorderLayout());

        JPanel upperPanel = new JPanel();
        upperPanel.setLayout(new BoxLayout(upperPanel, BoxLayout.Y_AXIS));
        upperPanel.setBackground(backgroundColor);
        panel.add(upperPanel, BorderLayout.NORTH);

        this.healthLabel = new JLabel("Health: " + health);
        this.cashLabel = new JLabel("Cash: " + cash);
        this.roundLabel = new JLabel("Round: 1"); // Starting round is 1


        this.counterLabel = new JLabel("Counter: 0");
        this.counterLabel.setVisible(false);

        healthLabel.setForeground(Color.LIGHT_GRAY);
        cashLabel.setForeground(Color.LIGHT_GRAY);
        roundLabel.setForeground(Color.LIGHT_GRAY);
        counterLabel.setForeground(Color.LIGHT_GRAY);

        upperPanel.add(healthLabel);
        upperPanel.add(cashLabel);
        upperPanel.add(roundLabel);
        upperPanel.add(counterLabel);


        JToggleButton muteMusicButton = new JToggleButton();
        muteMusicButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PLAY_MUSIC = !PLAY_MUSIC; // Need to modify static, only used by this class during runtime
                if (PLAY_MUSIC) {
                    SoundHandler.getInstance().playMusic();
                } else {
                    SoundHandler.getInstance().stopMusic();
                }
            }
        });

        JToggleButton muteSoundEffectsButton = new JToggleButton();
        muteSoundEffectsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PLAY_SOUND_EFFECTS = !PLAY_SOUND_EFFECTS; // Need to modify static, only used by this class during runtime
            }
        });

        loadButtonImages(muteMusicButton, muteSoundEffectsButton);

        this.buyBasicTowerButton = new JButton("Buy Basic Tower");
        this.buyPlagueTowerButton = new JButton("Buy Plague Tower");
        this.upgradeTowerButton = new JButton("Upgrade");
        this.sellTowerButton = new JButton("Sell");

        JPanel lowerPanel = new JPanel();
        lowerPanel.setLayout(new BoxLayout(lowerPanel, BoxLayout.Y_AXIS));
        lowerPanel.setBackground(backgroundColor);
        panel.add(lowerPanel, BorderLayout.SOUTH);

        lowerPanel.add(muteMusicButton);
        lowerPanel.add(muteSoundEffectsButton);
        lowerPanel.add(buyBasicTowerButton);
        lowerPanel.add(buyPlagueTowerButton);
        lowerPanel.add(upgradeTowerButton);
        lowerPanel.add(sellTowerButton);

        this.add(panel, BorderLayout.LINE_START);
    }

    private void loadButtonImages(JToggleButton muteMusicButton, JToggleButton muteSoundEffectsButton) {
        try {
            BufferedImage soundButtons = ImageIO.read(getClass().getResourceAsStream("/images/buttons.png"));

            // Size of sound and music buttons
            final int buttonHeight = 56;
            final int buttonWidth = 64;

            Icon musicEnabled = new ImageIcon(soundButtons.getSubimage(buttonWidth, 0, buttonWidth, buttonHeight));
            Icon musicDisabled = new ImageIcon(soundButtons.getSubimage(0, 0, buttonWidth, buttonHeight));
            muteMusicButton.setPressedIcon(musicDisabled);
            muteMusicButton.setSelectedIcon(musicDisabled);
            muteMusicButton.setIcon(musicEnabled);
            muteMusicButton.setBorderPainted(false);
            muteMusicButton.setContentAreaFilled(false);

            Icon soundEnabled = new ImageIcon(soundButtons.getSubimage(3 * buttonWidth, 0, buttonWidth, buttonHeight));
            Icon soundDisabled = new ImageIcon(soundButtons.getSubimage(2 * buttonWidth, 0, buttonWidth, buttonHeight));
            muteSoundEffectsButton.setPressedIcon(soundDisabled);
            muteSoundEffectsButton.setSelectedIcon(soundDisabled);
            muteSoundEffectsButton.setIcon(soundEnabled);
            muteSoundEffectsButton.setBorderPainted(false);
            muteSoundEffectsButton.setContentAreaFilled(false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void createMenus(){
        final JMenu file = new JMenu("File");

        file.addSeparator();
        final JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(new ActionListener() {
            @Override public void actionPerformed(final ActionEvent e) {
                System.exit(0); // Kill program
            }
        });
        file.add(exit);

        final JMenuBar menuBar = new JMenuBar();
        menuBar.add(file);
        this.setJMenuBar(menuBar);
    }

    public void launchHighScore(int score){
        String name = JOptionPane.showInputDialog(this, "Your score is " + score);
        System.out.println("Highscore: " + name + " " + "score: " + score);
        HighscoreList highscoreList = new HighscoreList();
        if (name != null && !name.isEmpty()) {
            highscoreList.add(name, score);
            highscoreList.saveToFile();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Score \t Name");
        List<HighscoreEntry> scores = highscoreList.getScoreList();
        for (int i = 0; i < scores.size(); i++){
            sb.append("\n").append(i + 1).append(": ").append(scores.get(i).getScore()).append(" \t ").append(scores.get(i).getName());
        }
        JOptionPane.showMessageDialog(this, new JTextArea(sb.toString()));
        controller.resetBoard();


    }

    public void setCashLabel(int cash) {
        this.cashLabel.setText("Cash: " + cash);
    }

    public void setHealthLabel(int health) {
        this.healthLabel.setText("Health: " + health);
    }

    public void setRoundLabel(int round) {
        this.roundLabel.setText("Round: " + (round-1)); // Needed to display correct wave
    }

    public void setCounterLabel(int counter) {
        if (counter == 0){
            counterLabel.setVisible(false);
        }
        else {
            counterLabel.setVisible(true);
            counterLabel.setText("Counter: " + counter);
        }
    }
}
