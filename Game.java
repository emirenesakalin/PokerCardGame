import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

//Represents the main game window and logic for a Poker game.
public class Game extends JFrame {
    Deck deck = new Deck();
    List<Card> deskCards = new ArrayList<>();
    Player player1 = new Player();
    Player player2 = new Player();
    boolean player1Changed = false;
    boolean player2Changed = false;
    boolean isPlayer1Turn = true;

    JLabel player1PointsLabel, player2PointsLabel;
    JPanel player1Panel, deskPanel, player2Panel;
    JButton player1ChangeButton, player2ChangeButton;

    public Game() {
        initializeGame();

        setTitle("Poker Game");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JButton restartButton = new JButton("Restart");
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRestart();
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(restartButton, BorderLayout.WEST);

        JPanel topPanel = new JPanel(new BorderLayout());
        player1Panel = new JPanel(new GridLayout(2, 5, 10, 5));
        player1PointsLabel = new JLabel("Player 1 Hand's Point: 0", SwingConstants.CENTER);
        player1PointsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(player1PointsLabel, BorderLayout.NORTH);
        topPanel.add(player1Panel, BorderLayout.CENTER);

        deskPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        for (int i = 0; i < 3; i++) {
            deskPanel.add(new JLabel(new ImageIcon(getClass().getResource("/CardImages/back.png"))));
        }

        JPanel bottomPanel = new JPanel(new BorderLayout());
        player2Panel = new JPanel(new GridLayout(2, 5, 10, 5));
        player2PointsLabel = new JLabel("Player 2 Hand's Point: Hidden", SwingConstants.CENTER);
        player2PointsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        bottomPanel.add(player2PointsLabel, BorderLayout.NORTH);
        bottomPanel.add(player2Panel, BorderLayout.CENTER);

        player1ChangeButton = new JButton("Player 1: Change Cards");
        player1ChangeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleChangeButtonClick(player1, true);
            }
        });

        player2ChangeButton = new JButton("Player 2: Change Cards");
        player2ChangeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleChangeButtonClick(player2, false);
            }
        });

        topPanel.add(player1ChangeButton, BorderLayout.SOUTH);
        bottomPanel.add(player2ChangeButton, BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(deskPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        refreshUI();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Game().setVisible(true));
    }

    private void initializeGame() {
        player1.resetHand();
        player2.resetHand();
        deskCards.clear();
        deck.shuffle();

        for (int i = 0; i < 5; i++) {
            player1.addCard(deck.drawCard());
            player2.addCard(deck.drawCard());
        }

        for (int i = 0; i < 3; i++) {
            deskCards.add(deck.drawCard());
        }
    }

    private void refreshUI() {
        player1Panel.removeAll();
        deskPanel.removeAll();
        player2Panel.removeAll();

        if (isPlayer1Turn) {
            displayCards(player1Panel, player1.getHand());
            displayCards(player2Panel, player2.getHand(), true);
        } else {
            displayCards(player2Panel, player2.getHand());
            displayCards(player1Panel, player1.getHand(), true);
        }

        if (player1Changed && player2Changed) {
            for (Card card : deskCards) {
                JLabel cardLabel = new JLabel(new ImageIcon(getClass().getResource(card.getImagePath())));
                deskPanel.add(cardLabel);
            }
        } else {
            for (int i = 0; i < 3; i++) {
                JLabel cardLabel = new JLabel(new ImageIcon(getClass().getResource("/CardImages/back.png")));
                deskPanel.add(cardLabel);
            }
        }

        int player1Score = calculateScoreWithDeskCards(player1.getHand(), deskCards);
        int player2Score = calculateScoreWithDeskCards(player2.getHand(), deskCards);

        if (isPlayer1Turn) {
            player1PointsLabel.setText("Player 1 Hand's Point: " + player1Score);
            player2PointsLabel.setText("Player 2 Hand's Point: Hidden");
        } else {
            player1PointsLabel.setText("Player 1 Hand's Point: Hidden");
            player2PointsLabel.setText("Player 2 Hand's Point: " + player2Score);
        }

        if (player1Changed && player2Changed) {
            player1PointsLabel.setText("Player 1 Hand's Point: " + player1Score);
            player2PointsLabel.setText("Player 2 Hand's Point: " + player2Score);
            determineWinner(player1Score, player2Score);
        }

        if (isPlayer1Turn) {
            player1ChangeButton.setEnabled(!player1Changed);
            player2ChangeButton.setEnabled(player1Changed && !player2Changed);
        } else {
            player2ChangeButton.setEnabled(!player2Changed);
            player1ChangeButton.setEnabled(player2Changed && !player1Changed);
        }

        revalidate();
        repaint();
    }

    private void displayCards(JPanel panel, List<Card> hand) {
        displayCards(panel, hand, false);
    }

    private void displayCards(JPanel panel, List<Card> hand, boolean faceDown) {
        for (Card card : hand) {
            JPanel cardPanel = new JPanel(new BorderLayout());
            String imagePath;
            if (faceDown) {
                imagePath = "/CardImages/back.png";
            } else {
                imagePath = card.getImagePath();
            }

            JLabel cardLabel = new JLabel(new ImageIcon(getClass().getResource(imagePath)));
            JCheckBox checkBox = new JCheckBox();
            checkBox.setHorizontalAlignment(SwingConstants.CENTER);

            cardPanel.add(cardLabel, BorderLayout.CENTER);
            cardPanel.add(checkBox, BorderLayout.SOUTH);

            panel.add(cardPanel);
        }
    }

    private void handleChangeButtonClick(Player player, boolean isPlayer1) {
        if ((isPlayer1 && player1Changed) || (!isPlayer1 && player2Changed)) {
            JOptionPane.showMessageDialog(this, "You can only change cards once per turn!");
            return;
        }

        List<Card> hand = player.getHand();
        JPanel targetPanel = isPlayer1 ? player1Panel : player2Panel;

        for (int i = 0; i < hand.size(); i++) {
            try {
                Component comp = targetPanel.getComponent(i);
                if (comp instanceof JPanel) {
                    JPanel cardPanel = (JPanel) comp;
                    Component checkBoxComp = cardPanel.getComponent(1);
                    if (checkBoxComp instanceof JCheckBox) {
                        JCheckBox checkBox = (JCheckBox) checkBoxComp;
                        if (checkBox.isSelected()) {
                            hand.set(i, deck.drawCard());
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error accessing card component: " + e.getMessage());
            }
        }

        if (isPlayer1) {
            player1Changed = true;
        } else {
            player2Changed = true;
        }

        isPlayer1Turn = !isPlayer1Turn;
        refreshUI();
    }

    private void handleRestart() {
        isPlayer1Turn = !isPlayer1Turn;
        player1Changed = false;
        player2Changed = false;
        initializeGame();
        refreshUI();
    }

    /**
     * Calculate score for a poker hand using standard poker rankings
     * Scoring system (from highest to lowest):
     * - Royal Flush: 1000 points
     * - Straight Flush: 800 + high card
     * - Four of a Kind: 600 + 4x card value
     * - Full House: 500 + 3x triplet value + 2x pair value
     * - Flush: 400 + sum of 5 highest cards
     * - Straight: 300 + high card value
     * - Three of a Kind: 200 + 3x card value
     * - Two Pair: 100 + 2x higher pair + 2x lower pair
     * - One Pair: 50 + 2x pair value
     * - High Card: sum of card values
     */
    private int calculateScore(List<Card> hand) {
        ArrayList<Integer> values = new ArrayList<>();
        ArrayList<String> suits = new ArrayList<>();

        for (Card card : hand) {
            values.add(card.getValue());
            suits.add(card.getSuit());
        }

        Collections.sort(values, Collections.reverseOrder());

        // Check for flush
        boolean hasFlush = false;
        String flushSuit = null;
        Map<String, Integer> suitCounts = new HashMap<>();
        for (String suit : suits) {
            suitCounts.put(suit, suitCounts.getOrDefault(suit, 0) + 1);
            if (suitCounts.get(suit) >= 5) {
                hasFlush = true;
                flushSuit = suit;
                break;
            }
        }

        // Get flush cards if exists
        List<Integer> flushValues = new ArrayList<>();
        if (hasFlush) {
            for (int i = 0; i < hand.size(); i++) {
                if (hand.get(i).getSuit().equals(flushSuit)) {
                    flushValues.add(hand.get(i).getValue());
                }
            }
            Collections.sort(flushValues, Collections.reverseOrder());
        }

        // Check for straight
        int straightHigh = checkStraight(values);
        int flushStraightHigh = hasFlush ? checkStraight(flushValues) : 0;

        // Royal Flush (A-K-Q-J-10 of same suit)
        if (flushStraightHigh == 14 && flushValues.contains(13) && flushValues.contains(12)
                && flushValues.contains(11) && flushValues.contains(10)) {
            return 1000;
        }

        // Straight Flush
        if (flushStraightHigh > 0) {
            return 800 + flushStraightHigh;
        }

        // Count value frequencies
        Map<Integer, Integer> valueCounts = new HashMap<>();
        for (Integer value : values) {
            valueCounts.put(value, valueCounts.getOrDefault(value, 0) + 1);
        }

        // Find pairs, three of a kind, four of a kind
        List<Integer> fours = new ArrayList<>();
        List<Integer> threes = new ArrayList<>();
        List<Integer> pairs = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : valueCounts.entrySet()) {
            if (entry.getValue() == 4) {
                fours.add(entry.getKey());
            } else if (entry.getValue() == 3) {
                threes.add(entry.getKey());
            } else if (entry.getValue() == 2) {
                pairs.add(entry.getKey());
            }
        }

        Collections.sort(threes, Collections.reverseOrder());
        Collections.sort(pairs, Collections.reverseOrder());

        // Four of a Kind
        if (!fours.isEmpty()) {
            return 600 + fours.get(0) * 4;
        }

        // Full House (three of a kind + pair)
        if (!threes.isEmpty() && (!pairs.isEmpty() || threes.size() >= 2)) {
            int threeValue = threes.get(0);
            int pairValue = pairs.isEmpty() ? threes.get(1) : pairs.get(0);
            return 500 + threeValue * 3 + pairValue * 2;
        }

        // Flush
        if (hasFlush) {
            int sum = 0;
            for (int i = 0; i < Math.min(5, flushValues.size()); i++) {
                sum += flushValues.get(i);
            }
            return 400 + sum;
        }

        // Straight
        if (straightHigh > 0) {
            return 300 + straightHigh;
        }

        // Three of a Kind
        if (!threes.isEmpty()) {
            return 200 + threes.get(0) * 3;
        }

        // Two Pair
        if (pairs.size() >= 2) {
            return 100 + pairs.get(0) * 2 + pairs.get(1) * 2;
        }

        // One Pair
        if (pairs.size() == 1) {
            return 50 + pairs.get(0) * 2;
        }

        // High Card - sum of top 5 cards
        int sum = 0;
        for (int i = 0; i < Math.min(5, values.size()); i++) {
            sum += values.get(i);
        }
        return sum;
    }

    /**
     * Check if values contain a straight (5 consecutive cards)
     * Returns the high card value of the straight, or 0 if no straight
     */
    private int checkStraight(List<Integer> values) {
        Set<Integer> uniqueValues = new HashSet<>(values);
        List<Integer> sorted = new ArrayList<>(uniqueValues);
        Collections.sort(sorted, Collections.reverseOrder());

        // Check for regular straight
        for (int i = 0; i <= sorted.size() - 5; i++) {
            boolean isStraight = true;
            for (int j = 0; j < 4; j++) {
                if (sorted.get(i + j) - 1 != sorted.get(i + j + 1)) {
                    isStraight = false;
                    break;
                }
            }
            if (isStraight) {
                return sorted.get(i); // Return high card
            }
        }

        // Check for A-2-3-4-5 straight (wheel)
        if (uniqueValues.contains(14) && uniqueValues.contains(2) &&
                uniqueValues.contains(3) && uniqueValues.contains(4) &&
                uniqueValues.contains(5)) {
            return 5; // In A-2-3-4-5, the high card is 5
        }

        return 0;
    }

    private int calculateScoreWithDeskCards(List<Card> hand, List<Card> deskCards) {
        List<Card> combinedCards = new ArrayList<>(hand);
        combinedCards.addAll(deskCards);
        return calculateScore(combinedCards);
    }

    private void determineWinner(int player1Score, int player2Score) {
        if (player1Score > player2Score) {
            JOptionPane.showMessageDialog(this,
                    "Player 1 wins this round!\nPlayer 1: " + player1Score + " points\nPlayer 2: " + player2Score + " points");
        } else if (player2Score > player1Score) {
            JOptionPane.showMessageDialog(this,
                    "Player 2 wins this round!\nPlayer 1: " + player1Score + " points\nPlayer 2: " + player2Score + " points");
        } else {
            JOptionPane.showMessageDialog(this,
                    "It's a tie!\nBoth players: " + player1Score + " points");
        }

        handleRestart();
    }
}