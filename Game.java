import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

//Represents the main game window and logic for a Poker game.
public class Game extends JFrame {
    Deck deck = new Deck(); // The deck of cards used in the game
    List<Card> deskCards = new ArrayList<>(); // The cards on the desk (community cards)
    Player player1 = new Player(); // Player 1
    Player player2 = new Player(); // Player 2
    boolean player1Changed = false; // Tracks if Player 1 has changed cards
    boolean player2Changed = false; // Tracks if Player 2 has changed cards
    boolean isPlayer1Turn = true; // Tracks whose turn it is (Player 1 or Player 2)

    JLabel player1PointsLabel, player2PointsLabel; // Labels to display player scores
    JPanel player1Panel, deskPanel, player2Panel; // Panels to display player hands and desk cards
    JButton player1ChangeButton, player2ChangeButton; // Buttons for players to change cards

    public Game() {
        initializeGame(); // Initialize the game state

        setTitle("Poker Game"); // Set the window title
        setSize(800, 800); // Set the window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close the application on window close
        setLayout(new BorderLayout()); // Use BorderLayout for the main layout

        JButton restartButton = new JButton("Restart"); // Restart button to reset the game
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRestart(); // Handle restart button click
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout()); // Main panel to hold all components
        mainPanel.add(restartButton, BorderLayout.WEST); // Add restart button to the main panel

        JPanel topPanel = new JPanel(new BorderLayout()); // Top panel for Player 1's hand and score
        player1Panel = new JPanel(new GridLayout(2, 5, 10, 5)); // Panel to display Player 1's cards
        player1PointsLabel = new JLabel("Player 1 Hand's Point: 0", SwingConstants.CENTER); // Label for Player 1's score
        player1PointsLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Set font for the score label
        topPanel.add(player1PointsLabel, BorderLayout.NORTH); // Add score label to the top panel
        topPanel.add(player1Panel, BorderLayout.CENTER); // Add Player 1's card panel to the top panel

        deskPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15)); // Panel to display desk cards
        for (int i = 0; i < 3; i++) {
            deskPanel.add(new JLabel(new ImageIcon(getClass().getResource("/images/back.png")))); // Add face-down cards to the desk
        }

        JPanel bottomPanel = new JPanel(new BorderLayout()); // Bottom panel for Player 2's hand and score
        player2Panel = new JPanel(new GridLayout(2, 5, 10, 5)); // Panel to display Player 2's cards
        player2PointsLabel = new JLabel("Player 2 Hand's Point: Hidden", SwingConstants.CENTER); // Label for Player 2's score
        player2PointsLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Set font for the score label
        bottomPanel.add(player2PointsLabel, BorderLayout.NORTH); // Add score label to the bottom panel
        bottomPanel.add(player2Panel, BorderLayout.CENTER); // Add Player 2's card panel to the bottom panel

        player1ChangeButton = new JButton("Player 1: Change Cards"); // Button for Player 1 to change cards
        player1ChangeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleChangeButtonClick(player1, true); // Handle Player 1's card change
            }
        });

        player2ChangeButton = new JButton("Player 2: Change Cards"); // Button for Player 2 to change cards
        player2ChangeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleChangeButtonClick(player2, false); // Handle Player 2's card change
            }
        });

        topPanel.add(player1ChangeButton, BorderLayout.SOUTH); // Add Player 1's change button to the top panel
        bottomPanel.add(player2ChangeButton, BorderLayout.SOUTH); // Add Player 2's change button to the bottom panel

        mainPanel.add(topPanel, BorderLayout.NORTH); // Add top panel to the main panel
        mainPanel.add(deskPanel, BorderLayout.CENTER); // Add desk panel to the main panel
        mainPanel.add(bottomPanel, BorderLayout.SOUTH); // Add bottom panel to the main panel

        add(mainPanel); // Add the main panel to the frame
        refreshUI(); // Refresh the UI to display initial state
    }

    public static void main(String[] args) {
        new Game().setVisible(true); // Start the game and make the window visible
    }

    private void initializeGame() {
        player1.resetHand(); // Reset Player 1's hand
        player2.resetHand(); // Reset Player 2's hand

        deskCards.clear(); // Clear the desk cards

        deck.shuffle(); // Shuffle the deck

        for (int i = 0; i < 5; i++) {
            player1.addCard(deck.drawCard()); // Deal 5 cards to Player 1
            player2.addCard(deck.drawCard()); // Deal 5 cards to Player 2
        }

        for (int i = 0; i < 3; i++) {
            deskCards.add(deck.drawCard()); // Deal 3 cards to the desk
        }
    }

    private void refreshUI() {
        player1Panel.removeAll(); // Clear Player 1's card panel
        deskPanel.removeAll(); // Clear the desk panel
        player2Panel.removeAll(); // Clear Player 2's card panel

        if (isPlayer1Turn) {
            displayCards(player1Panel, player1.getHand()); // Display Player 1's cards
            displayCards(player2Panel, player2.getHand(), true); // Display Player 2's cards face-down
        } else {
            displayCards(player2Panel, player2.getHand()); // Display Player 2's cards
            displayCards(player1Panel, player1.getHand(), true); // Display Player 1's cards face-down
        }

        if (player1Changed && player2Changed) {
            for (Card card : deskCards) {
                JLabel cardLabel = new JLabel(new ImageIcon(getClass().getResource(card.getImagePath()))); // Display desk cards face-up
                deskPanel.add(cardLabel);
            }
        } else {
            for (int i = 0; i < 3; i++) {
                JLabel cardLabel = new JLabel(new ImageIcon(getClass().getResource("/images/back.png"))); // Display desk cards face-down
                deskPanel.add(cardLabel);
            }
        }

        int player1Score = calculateScoreWithDeskCards(player1.getHand(), deskCards); // Calculate Player 1's score
        int player2Score = calculateScoreWithDeskCards(player2.getHand(), deskCards); // Calculate Player 2's score

        if (isPlayer1Turn) {
            player1PointsLabel.setText("Player 1 Hand's Point: " + player1Score); // Display Player 1's score
            player2PointsLabel.setText("Player 2 Hand's Point: Hidden"); // Hide Player 2's score
        } else {
            player1PointsLabel.setText("Player 1 Hand's Point: Hidden"); // Hide Player 1's score
            player2PointsLabel.setText("Player 2 Hand's Point: " + player2Score); // Display Player 2's score
        }

        if (player1Changed && player2Changed) {
            player1PointsLabel.setText("Player 1 Hand's Point: " + player1Score); // Display both players' scores
            player2PointsLabel.setText("Player 2 Hand's Point: " + player2Score);
            determineWinner(player1Score, player2Score); // Determine the winner
        }

        if (isPlayer1Turn) {
            player1ChangeButton.setEnabled(!player1Changed); // Enable/disable Player 1's change button
            player2ChangeButton.setEnabled(player1Changed && !player2Changed); // Enable/disable Player 2's change button
        } else {
            player2ChangeButton.setEnabled(!player2Changed); // Enable/disable Player 2's change button
            player1ChangeButton.setEnabled(player2Changed && !player1Changed); // Enable/disable Player 1's change button
        }

        revalidate(); // Refresh the UI layout
        repaint(); // Repaint the UI
    }

    private void displayCards(JPanel panel, List<Card> hand) {
        displayCards(panel, hand, false); // Display cards face-up by default
    }

    private void displayCards(JPanel panel, List<Card> hand, boolean faceDown) {
        for (Card card : hand) {
            JPanel cardPanel = new JPanel(new BorderLayout()); // Create a panel for each card
            String imagePath;
            if (faceDown) {
                imagePath = "/images/back.png"; // Use face-down image if specified
            } else {
                imagePath = card.getImagePath(); // Use card's image if face-up
            }

            JLabel cardLabel = new JLabel(new ImageIcon(getClass().getResource(imagePath))); // Create a label for the card image
            JCheckBox checkBox = new JCheckBox(); // Add a checkbox for card selection
            checkBox.setHorizontalAlignment(SwingConstants.CENTER); // Center the checkbox

            cardPanel.add(cardLabel, BorderLayout.CENTER); // Add the card image to the panel
            cardPanel.add(checkBox, BorderLayout.SOUTH); // Add the checkbox to the panel

            panel.add(cardPanel); // Add the card panel to the main panel
        }
    }

    private void handleChangeButtonClick(Player player, boolean isPlayer1) {
        if ((isPlayer1 && player1Changed) || (!isPlayer1 && player2Changed)) {
            JOptionPane.showMessageDialog(this, "You can only change cards once per turn!"); // Show error if player tries to change cards more than once
            return;
        }

        List<Card> hand = player.getHand(); // Get the player's hand
        for (int i = 0; i < hand.size(); i++) {
            JCheckBox checkBox;
            if (isPlayer1) {
                checkBox = (JCheckBox) ((JPanel) player1Panel.getComponent(i)).getComponent(1); // Get the checkbox for Player 1's card
            } else {
                checkBox = (JCheckBox) ((JPanel) player2Panel.getComponent(i)).getComponent(1); // Get the checkbox for Player 2's card
            }

            if (checkBox.isSelected()) {
                hand.set(i, deck.drawCard()); // Replace the selected card with a new one from the deck
            }
        }

        if (isPlayer1) {
            player1Changed = true; // Mark Player 1's cards as changed
        } else {
            player2Changed = true; // Mark Player 2's cards as changed
        }

        isPlayer1Turn = !isPlayer1Turn; // Switch turns

        refreshUI(); // Refresh the UI
    }

    private void handleRestart() {
        isPlayer1Turn = !isPlayer1Turn; // Switch starting player for the next round

        player1Changed = false; // Reset Player 1's change status
        player2Changed = false; // Reset Player 2's change status

        initializeGame(); // Reinitialize the game

        refreshUI(); // Refresh the UI
    }

    private int calculateScore(List<Card> hand) {
        int score = 0;
        ArrayList<Integer> values = new ArrayList<>(); // List to store card values
        ArrayList<String> suits = new ArrayList<>(); // List to store card suits

        for (Card card : hand) {
            values.add(card.getValue()); // Add card value to the list
            suits.add(card.getSuit()); // Add card suit to the list
        }

        score += calculatePairsAndSets(values); // Calculate score for pairs and sets
        score += calculateStraight(values); // Calculate score for straights
        score += calculateFlush(suits, values); // Calculate score for flushes
        score += calculateAce(values); // Calculate score for Aces and face cards

        return score; // Return the total score
    }

    private int calculatePairsAndSets(ArrayList<Integer> values) {
        int score = 0;
        Collections.sort(values); // Sort the values for easier calculation

        for (int i = 0; i < values.size() - 1; i++) {
            int count = Collections.frequency(values, values.get(i)); // Count occurrences of each value
            if (count == 2) {
                score += values.get(i) * 2; // Add score for pairs
            }
            if (count == 3) {
                score += values.get(i) * 3; // Add score for three of a kind
            }
            if (count == 4) {
                score += values.get(i) * 4; // Add score for four of a kind
            }
        }
        return score; // Return the score for pairs and sets
    }

    private int calculateStraight(ArrayList<Integer> values) {
        boolean isStraight = true;
        Collections.sort(values); // Sort the values for easier calculation

        for (int i = 0; i < values.size() - 1; i++) {
            if (values.get(i) + 1 != values.get(i + 1)) {
                isStraight = false; // Check if the values form a straight
                break;
            }
        }

        if (values.contains(1) && values.contains(14) && isStraight) {
            return values.stream().mapToInt(Integer::intValue).sum() * 5; // Special case for Ace-low straight
        }

        if (isStraight) {
            return values.stream().mapToInt(Integer::intValue).sum() * 5; // Return score for a straight
        }

        return 0; // Return 0 if no straight is found
    }

    private int calculateFlush(ArrayList<String> suits, ArrayList<Integer> values) {
        int score = 0;
        for (String suit : suits) {
            int count = Collections.frequency(suits, suit); // Count occurrences of each suit
            if (count >= 5) {
                score += values.stream().mapToInt(Integer::intValue).sum() * 6; // Add score for a flush
            }
        }
        return score; // Return the score for flushes
    }

    private int calculateAce(ArrayList<Integer> values) {
        int score = 0;
        for (Integer value : values) {
            if (value == 1) {
                score += 11; // Add score for Ace
            } else if (value >= 11 && value <= 13) {
                score += 10; // Add score for face cards (Jack, Queen, King)
            } else {
                score += value; // Add score for numeric cards
            }
        }
        return score; // Return the score for Aces and face cards
    }

    private int calculateScoreWithDeskCards(List<Card> hand, List<Card> deskCards) {
        List<Card> combinedCards = new ArrayList<>(hand); // Combine player's hand and desk cards
        combinedCards.addAll(deskCards);
        return calculateScore(combinedCards); // Calculate the total score
    }

    private void determineWinner(int player1Score, int player2Score) {
        if (player1Score > player2Score) {
            JOptionPane.showMessageDialog(this, "Player 1 wins this round!"); // Player 1 wins
        } else if (player2Score > player1Score) {
            JOptionPane.showMessageDialog(this, "Player 2 wins this round!"); // Player 2 wins
        } else {
            JOptionPane.showMessageDialog(this, "It's a tie!"); // It's a tie
        }

        handleRestart(); // Restart the game after determining the winner
    }
}