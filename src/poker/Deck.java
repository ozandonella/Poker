package poker;

import java.util.ArrayList;
public class Deck {

    private final ArrayList<Card> deck;
    private int nextToDeal;

	/**
	 * Constructor that sets the deck array. nextToDeal is a O(1) alternative to dealing cards.
	 * instead of the deck actually removing cards when dealing, it instead uses a pivot where all
	 * the cards to the left of the index are already delt and the cards to the right have not been used yet
	 *
	 */
	public Deck(){
		deck=getAllCards();
	    nextToDeal = 0;
	}
	public ArrayList<Card> getDeck(){
	    return deck;
	}
	public Card deal(){
		return deck.get(nextToDeal++);
	}

	/**
	 * When shuffling and using this dealing technique its important that we are only shuffling
	 * cards that are to the right of the next to deal index, because those are the cards that are
	 * still left in the deck.
	 */
	public void shuffle() {
		for(int pos = nextToDeal; pos<52; pos++){
			int difference = 51-nextToDeal;
			int num = (int)(Math.random()*difference+nextToDeal);
			Card tempCard = new Card(deck.get(pos).getValue(), deck.get(pos).getSuitVal(), deck.get(pos).getGraphic());
			deck.set(pos, deck.get(num));
			deck.set(num, tempCard);
		}
	}
	public static ArrayList<Card> getCardsOfSuit(int suit){
		ArrayList<Card> cards = new ArrayList<>();
		for(int i=2; i<15; i++) cards.add(new Card(i,suit,Card.cards[suit][i-2]));
		return cards;
	}
	public PokerHand getHand(){
		PokerHand hand = new PokerHand();
		while(hand.getHand().size()<5) hand.addCard(deal());
		return hand;
	}
	public String toString() {
	    StringBuilder toReturn = new StringBuilder();
	    for(int i=0; i<52; i++) toReturn.append(deck.get(i).toString()).append("\n");
		return toReturn.toString();
	}
	public static ArrayList<Card> getAllCards(){
		ArrayList<Card> deck = new ArrayList<>();
		for(int rank = 2; rank<=14; rank++){
			for(int suit = 0; suit<=3; suit++){
				Card c = new Card(rank, suit, Card.cards[suit][rank-2]);
				deck.add(c);
			}
		}
		return deck;
	}
}