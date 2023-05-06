package poker;
public class Card {
	private static final String[] values = {"Two", "Three", "Four", "Five", "Six", "Seven", "Eight",
			"Nine", "Ten", "Jack", "Queen", "King", "Ace"};
    private static final String[] valuesShort = {"2", "3", "4", "5", "6", "7", "8",
            "9", "T", "J", "Q", "K", "A"};

    public static final String[] suits = {"Spades","Clubs","Hearts" ,"Diamonds"};
    public static final String[] suitsShort = {"S","C","H","D"};
    public static final String[][][] cards = Poker.getCardGraphics();
    public static final String[] backGraphic = Poker.getBackGraphic();
    public static final Card back = new Card(backGraphic);

    private final int value;
    private final int suitVal;
    private final String name;
    private final String nameShort;
    private final String[] graphic;

    /**
     * Constructor for cards that are only graphical. I use this for when playing cards are "face down"
     * in hindsight I want to change this so all cards have actual values, and add a boolean "isFaceUp"
     * to either print out its front or back.
     * @param graphic: the lines of strings that make up the card's appearance.
     */
    public Card (String[] graphic){
        this.graphic=graphic;
        name="???";
        nameShort="???";
        value=0;
        suitVal=0;
    }

    /**
     * Constructor for playing cards. I have a short and long name because I can print the cards spread out
     * between a given distance, and since the full name of some cards (up to 17 characters) are much wider than the cards
     * themselves (only 7 characters) the program will print out the truncated name if there is not enough room for full names.
     * @param value: int from 2-14 used in value arrays.
     * @param suitVal: int from 0-4 used in suit arrays.
     * @param graphic: the card's graphic.
     */
    public Card (int value, int suitVal, String[] graphic){
        this.graphic=graphic;
        this.value = value;
        this.suitVal = suitVal;
        name=values[value-2]+" of "+suits[this.suitVal];
        nameShort=valuesShort[value-2]+suitsShort[this.suitVal];
    }
    public String[] getGraphic() {
        return graphic;
    }

    public int getValue(){
        return value;
    }
    public String getName() {
		return name;
	}
    public String getNameShort() {
        return nameShort;
    }
    public int getSuitVal(){
        return suitVal;
    }
    public String toString(){
        return name;
    }
    public boolean equals(Card other){
        return name.equals(other.name);
    }
}
