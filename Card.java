//Represents a playing card with a suit and value.
public class Card {
    private String suit;  // The suit of the card (e.g., hearts, diamonds)
    private int value;    // The value of the card (e.g., 2-10, Jack, Queen, King, Ace)

    //Creates a new Card with the given suit and value.
    public Card(String suit, int value) {
        this.suit = suit;
        this.value = value;
    }

    //Returns the card's value.
    public int getValue() {
        return value;
    }

    //Returns the card's suit.
    public String getSuit() {
        return suit;
    }

     //Returns the path to the card's image based on its value and suit.
    public String getImagePath() {
        String valueName;
        switch (value) {
            case 11:
                valueName = "jack";  // Jack
                break;
            case 12:
                valueName = "queen"; // Queen
                break;
            case 13:
                valueName = "king";  // King
                break;
            case 14:
                valueName = "ace";   // Ace
                break;
            default:
                valueName = String.valueOf(value); // Numeric cards (2-10)
                break;
        }
        // Constructs the image path using the card's value and suit
        return "/images/" + valueName + "_of_" + suit.toLowerCase() + ".jpg";
    }
}