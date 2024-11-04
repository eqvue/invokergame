import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Main {

    // Declare spell combinations and their names
    private static final Map<String, String> spells = new HashMap<>();
    private static final String[] spellKeys;
    private static final Random random = new Random();
    private static Timer timer; // Timer for casting spells
    private static int timeLeft; // Time left for casting
    private static boolean isTimerRunning = false; // To track if the timer is running
    private static DefaultListModel<String> comboListModel; // Model for the JList of completed coqqqqmbos
    private static boolean gameOver = false; // To track if the game is over
    private static int completedComboCount = 0; // Counter for completed combos

    private static int initialTimeLimit = 10; // Initial timer duration in seconds

    private static final int BRONZE_THRESHOLD = 5; // Combos for Bronze badge
    private static final int SILVER_THRESHOLD = 10; // Combos for Silver badge
    private static final int GOLD_THRESHOLD = 15; // Combos for Gold badge
    private static final int PLATINUM_THRESHOLD = 20; // Combos for Platinum badge
    private static final int MASTER_THRESHOLD = 25; // Combos for Master badge
    private static final int GRANDMASTER_THRESHOLD = 30; // Combos for Grandmaster badge


    static {
        // Initialize spell combinations with variations
        spells.put("qqqr", "coldsnap");
        spells.put("qqwr", "ghostwalk");
        spells.put("wqqr", "ghostwalk");
        spells.put("qwqr", "ghostwalk");
        spells.put("wqwr", "tornado");
        spells.put("qwwr", "tornado");
        spells.put("wwqr", "tornado");
        spells.put("qqer", "icewall");
        spells.put("eqqr", "icewall");
        spells.put("qeqr", "icewall");
        spells.put("wwwr", "emp");
        spells.put("wwer", "alacrity");
        spells.put("wewr", "alacrity");
        spells.put("ewwr", "alacrity");
        spells.put("eeer", "sunstrike");
        spells.put("eeqr", "forgespirit");
        spells.put("qeer", "forgespirit");
        spells.put("eqer", "forgespirit");
        spells.put("eewr", "chaosmeteor");
        spells.put("weer", "chaosmeteor");
        spells.put("ewer", "chaosmeteor");
        spells.put("qwer", "deafeningblast");
        spells.put("qewr", "deafeningblast");
        spells.put("ewqr", "deafeningblast");
        spells.put("eqwr", "deafeningblast");
        spells.put("wqer", "deafeningblast");
        spells.put("weqr", "deafeningblast");

        spellKeys = spells.keySet().toArray(new String[0]);
    }

    private static String currentSpell; // To store the current spell to cast
    private static JButton startButton; // Button to start/restart the game
    private static String currentSpellName; // To store the current spell's name
    private static JTextField inputField; // Input field for user spells
    private static JLabel comboCountLabel; // Label to display completed combo count
    private static JLabel promptLabel; // Label for displaying spell images
    private static JLabel timerLabel; // Label for displaying timer
    private static JLabel outputLabel; // Declare outputLabel as a class-level variable
    private static JLabel badgeLabel; // Label to display the badge


    public static void main(String[] args) {
        // Create a JFrame for the GUI
        JFrame frame = new JFrame("Invoker Spell Trainer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setResizable(false); // Prevent resizing
        frame.setLayout(new FlowLayout());

        // Create components
        inputField = new JTextField(15);
        outputLabel = new JLabel("Type a spell combination..."); // Initialize outputLabel here
        promptLabel = new JLabel(); // Initialize promptLabel for displaying spell images
        timerLabel = new JLabel("Time left: " + initialTimeLimit); // Timer label
        badgeLabel = new JLabel(); // Initialize badge label

        // Create the list model for storing completed combos
        comboListModel = new DefaultListModel<>();
        JList<String> comboList = new JList<>(comboListModel);
        JScrollPane scrollPane = new JScrollPane(comboList);
        scrollPane.setPreferredSize(new Dimension(200, 200));

        // Label to display the completed combo count
        comboCountLabel = new JLabel("Completed Combos: 0");

        // Initialize the timer
        initializeTimer(outputLabel);

        // "Q" Image Button
        ImageIcon qImageIcon = new ImageIcon(Main.class.getResource("/resources/quas.png"));
        JLabel qImageLabel = new JLabel(qImageIcon);
        qImageLabel.setPreferredSize(new Dimension(100, 100));
        qImageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                inputField.setText(inputField.getText() + "q");
            }
        });

        // "W" Image Button
        ImageIcon wImageIcon = new ImageIcon(Main.class.getResource("/resources/wex.png"));
        JLabel wImageLabel = new JLabel(wImageIcon);
        wImageLabel.setPreferredSize(new Dimension(100, 100));
        wImageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                inputField.setText(inputField.getText() + "w");
            }
        });

        // "E" Image Button
        ImageIcon eImageIcon = new ImageIcon(Main.class.getResource("/resources/exort.png"));
        JLabel eImageLabel = new JLabel(eImageIcon);
        eImageLabel.setPreferredSize(new Dimension(100, 100));
        eImageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                inputField.setText(inputField.getText() + "e");
            }
        });

        // "R" Image Button
        ImageIcon rImageIcon = new ImageIcon(Main.class.getResource("/resources/rImage.png"));
        JLabel rImageLabel = new JLabel(rImageIcon);
        rImageLabel.setPreferredSize(new Dimension(100, 100));
        rImageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                inputField.setText(inputField.getText() + "r");
            }
        });

        // Create start/restart button
        startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isTimerRunning && !gameOver) {
                    startGame();
                } else if (gameOver) {
                    resetGame();
                }
            }
        });

        // Add KeyListener for spell casting
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (gameOver) return;

                String input = inputField.getText().trim(); // Get current input
                String spellName = spells.get(input); // Check for known spells
                if (spellName != null) {
                    outputLabel.setText("You cast: " + spellName);
                    inputField.setText(""); // Clear the input field after casting the spell
                    resetTimer(); // Reset the timer after casting
                    generateRandomSpell(); // Generate a new spell after casting

                    // Add the completed combo to the list
                    comboListModel.addElement(spellName + " (" + input + ")");
                    completedComboCount++; // Increment the completed combo count
                    comboCountLabel.setText("Completed Combos: " + completedComboCount); // Update the combo count label

                    // Check for badge level
                    updateBadge();

                    // Update the displayed image of the cast spell
                    try {
                        ImageIcon spellImage = new ImageIcon(Main.class.getResource("/resources/" + currentSpellName + ".png"));
                        promptLabel.setIcon(spellImage); // Show the image of the cast spell
                    } catch (Exception ex) {
                        System.out.println("Image for cast spell " + currentSpellName + " not found.");
                        promptLabel.setIcon(null); // If image not found, clear the promptLabel icon
                    }

                } else if (input.length() > 4) {
                    // End the game if the spell is unknown and has more than 4 letters
                    inputField.setText("");
                }
            }
        });

        // Add components to the frame
        frame.getContentPane().setBackground(Color.WHITE); // Set JFrame background to black
        frame.add(qImageLabel);
        frame.add(wImageLabel);
        frame.add(eImageLabel);
        frame.add(rImageLabel);
        frame.add(inputField);
        frame.add(outputLabel);
        frame.add(promptLabel); // Add the promptLabel to the frame
        frame.add(timerLabel); // Add timer label to the frame
        frame.add(startButton);
        frame.add(scrollPane); // Add the scroll pane containing the combo list
        frame.add(comboCountLabel); // Add the combo count label
        frame.add(badgeLabel); // Add the badge label

        // Make frame visible
        frame.setVisible(true);
    }

    private static void generateRandomSpell() {
        int randomIndex = random.nextInt(spellKeys.length); // Get a random index
        currentSpell = spellKeys[randomIndex]; // Get the spell at that index
        currentSpellName = spells.get(currentSpell); // Get the name of the spell

        // Display the current spell name in the promptLabel
        promptLabel.setText("Cast: " + currentSpellName);

        // Display the corresponding image for the current spell
        try {
            ImageIcon spellImage = new ImageIcon(Main.class.getResource("/resources/" + currentSpellName + ".png"));
            promptLabel.setIcon(spellImage); // Set the image in the promptLabel
        } catch (Exception e) {
            System.out.println("Image for spell " + currentSpellName + " not found.");
            promptLabel.setIcon(null); // If image not found, don't display anything
        }

        resetTimer(); // Reset the timer when a new spell is generated
    }

    private static void initializeTimer(JLabel outputLabel) {
        // Create a Timer object that fires an ActionEvent every second (1000ms)
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeLeft--;
                timerLabel.setText("Time left: " + timeLeft);

                // Check if the time has run out
                if (timeLeft <= 0) {
                    timer.stop();
                    isTimerRunning = false;
                    endGame("Time's up!", outputLabel);
                }
            }
        });
    }
    private static void reduceTimer(int reduction) {
        // Reduce the initial time limit by the specified reduction
        if (initialTimeLimit > 1) {
            initialTimeLimit -= reduction;
        }

        // Ensure the time limit does not fall below 1 second
        if (initialTimeLimit < 1) {
            initialTimeLimit = 1;
        }

        timerLabel.setText("Time left: " + initialTimeLimit); // Update the timer label
    }
    private static void resetTimer() {
        // Reset the time left to the current level's time limit

            timeLeft = initialTimeLimit;

        timerLabel.setText("Time left: " + timeLeft);



        // Restart the timer if it's not already running
        if (!isTimerRunning) {
            timer.start();
            isTimerRunning = true;
        }
    }

    private static void startGame() {
        // Reset combo count and level
        completedComboCount = 0;
        initialTimeLimit = 10;

        // Generate the first spell
        generateRandomSpell();

        // Start the timer
        resetTimer();

        // Disable the start button during the game
        startButton.setEnabled(false);

        gameOver = false; // Reset the game over flag
    }

    private static void resetGame() {
        // Clear the list of completed combos
        comboListModel.clear();

        // Reset the badge label
        badgeLabel.setIcon(null);

        // Start a new game
        startGame();
    }


    private static void updateBadge() {
        // Update the badge based on the number of completed combos
        if (completedComboCount >= GRANDMASTER_THRESHOLD) {
            badgeLabel.setIcon(new ImageIcon(Main.class.getResource("/resources/grandmaster.png")));
            reduceTimer(1);
        } else if (completedComboCount >= MASTER_THRESHOLD) {
            badgeLabel.setIcon(new ImageIcon(Main.class.getResource("/resources/master.png")));
            reduceTimer(1);
        } else if (completedComboCount >= PLATINUM_THRESHOLD) {
            badgeLabel.setIcon(new ImageIcon(Main.class.getResource("/resources/platinum.png")));
            reduceTimer(1);
        } else if (completedComboCount >= GOLD_THRESHOLD) {
            badgeLabel.setIcon(new ImageIcon(Main.class.getResource("/resources/gold.png")));
            reduceTimer(1);
        } else if (completedComboCount >= SILVER_THRESHOLD) {
            badgeLabel.setIcon(new ImageIcon(Main.class.getResource("/resources/silver.png")));
            reduceTimer(1);
        } else if (completedComboCount >= BRONZE_THRESHOLD) {
            badgeLabel.setIcon(new ImageIcon(Main.class.getResource("/resources/bronze.png")));
            reduceTimer(1);
        } else {
            badgeLabel.setIcon(null); // No badge if the count is less than the bronze threshold
        }
    }

    private static void endGame(String message, JLabel outputLabel) {
        // Display the game over message
        outputLabel.setText(message);

        // Stop the timer and reset timer-related variables
        timer.stop();
        isTimerRunning = false;

        // Set the game over flag
        gameOver = true;

        // Enable the start button to allow restarting
        startButton.setEnabled(true);
    }
}
