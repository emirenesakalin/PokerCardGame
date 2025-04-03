import java.util.*;

//Represents a deck of playing cards.
public class Deck {
    private List<Card> cards = new ArrayList<>(); // List to hold the cards in the deck

    public Deck() {
        initializeDeck(); // Initialize and shuffle the deck
    }

    private void initializeDeck() {
        String[] suits = {"hearts", "diamonds", "clubs", "spades"}; // All possible suits
        for (String suit : suits) {
            for (int value = 2; value <= 14; value++) { // Values from 2 to Ace (14)
                cards.add(new Card(suit, value)); // Create and add a new card to the deck
            }
        }
        shuffle(); // Shuffle the deck after initialization
    }

    public void shuffle() {
        Collections.shuffle(cards); // Shuffle the cards using Collections.shuffle
    }

    public Card drawCard() {
        if (cards.isEmpty()) {
            initializeDeck(); // If the deck is empty, reinitialize and shuffle it
        }
        return cards.remove(cards.size() - 1); // Draw and return the top card from the deck
    }
}