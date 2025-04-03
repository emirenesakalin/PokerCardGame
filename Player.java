import java.util.ArrayList;
import java.util.List;

//Represents a player in the game with a hand of cards and sets won.
public class Player {
    private List<Card> hand = new ArrayList<>(); // The player's current hand of cards
    private int setsWon = 0; // The number of sets the player has won

    public List<Card> getHand() {
        return hand; // Returns the player's current hand
    }

    public void addCard(Card card) {
        hand.add(card); // Adds a card to the player's hand
    }

    public void resetHand() {
        hand.clear(); // Clears the player's hand (used at the start of a new round)
    }

    public int getSetsWon() {
        return setsWon; // Returns the number of sets the player has won
    }

    public void incrementSetsWon() {
        setsWon++; // Increments the number of sets won by the player
    }
}