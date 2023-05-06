package poker;

import java.util.ArrayList;
import java.util.Comparator;

public class PokerHand {
	public final static String[] hands = {"Royal Flush", "Straight Flush", "Four of a Kind", "Full House", "Flush	",
			"Straight","Three of a Kind", "Two Pair", "One Pair", "High Card"};
    private final ArrayList<Card> hand;
	private String name;
    private int handVal;

    public PokerHand(){
        handVal = 10;
        name="";
        hand = new ArrayList<>();
    }
    public PokerHand(ArrayList<Card> cardList){
        handVal = 9;
        name="";
        hand = new ArrayList<>(cardList);
    }

	public ArrayList<Card> getHand() {
		return hand;
	}

	public int getHandVal() {
		return handVal;
	}
	/*
    adds card object to a Poker Hand object, if the Poker Hand object doesn't have 5 Card objects
    :param card: Card object
    */
    public void addCard(Card card) {
        if (hand.size() < 5) hand.add(card);
    }
    public Card getCard(int index) {
        if (index < 0 || index >= hand.size()) return null;
        return hand.get(index);
    }
    private int[] valList(){
    	int[] valList = new int[13];
        for(int i=0; i<5; i++) valList[hand.get(i).getValue()-2] += 1;
        return valList;
    }
    public void evalHand() {
    	hand.sort(Comparator.comparingInt(Card::getValue));
		int[] valFreq = new int[14];
		int numOfPair=0;
		int highPair=0;
		boolean isStraight=true;
		boolean isFlush=true;
		for(int x=0; x<5; x++) {
			if(x<4) {
				if(getCard(x).getSuitVal()!=(getCard(x+1).getSuitVal())) isFlush=false;
				if(getCard(x).getValue()+1!=getCard(x+1).getValue()) isStraight=false;
			}
			int val = hand.get(x).getValue()-2;
			valFreq[val]++;
			if(valFreq[val]==2)numOfPair++;
			highPair=Math.max(valFreq[val],highPair);
		}
		if(hand.get(4).getValue()==14&&hand.get(3).getValue()==5&&numOfPair==0) isStraight=true;
		if(isFlush) {
			if(isStraight&&hand.get(3).getValue()==13) handVal=0;
			else if(isStraight) handVal=1;
			else handVal=4;
		}
		else {
			if(highPair==4) handVal=2;
			else if(highPair==3&&numOfPair==2) handVal=3;
			else if(isStraight) handVal=5;
			else if(highPair==3) handVal=6;
			else if(numOfPair==2) handVal=7;
			else if(highPair==2) handVal=8;
			else handVal=9;
		}
		name=hands[handVal];
	}
	public static double compareKickers(ArrayList<Card> kicker, ArrayList<Card> oppKicker){
		int diff;
		kicker.sort(Comparator.comparingInt(Card::getValue));
		oppKicker.sort(Comparator.comparingInt(Card::getValue));
		diff=kicker.get(1).getValue()-oppKicker.get(1).getValue();
		if(diff==0) diff=kicker.get(1).getValue()-oppKicker.get(1).getValue();
		if(kicker.get(1).getValue()==kicker.get(0).getValue())diff+=kicker.get(0).getValue();
		if(oppKicker.get(1).getValue()==oppKicker.get(0).getValue())diff-=oppKicker.get(0).getValue();
		if(oppKicker.get(1).getSuitVal()==(oppKicker.get(0).getSuitVal())) diff-=Math.ceil((double)oppKicker.get(1).getValue()/2);
		if(kicker.get(1).getSuitVal()==(kicker.get(0).getSuitVal())) diff+=Math.ceil((double)kicker.get(1).getValue()/2);
		if(kicker.get(1).getValue()-kicker.get(0).getValue()<5||kicker.get(1).getValue()==14&&kicker.get(0).getValue()<6) diff+=Math.ceil((double)kicker.get(1).getValue()/3);
		if(oppKicker.get(1).getValue()-oppKicker.get(0).getValue()<5||oppKicker.get(1).getValue()==14&&oppKicker.get(0).getValue()<6) diff-=Math.ceil((double)oppKicker.get(1).getValue()/3);
		return diff;
	}
	public static ArrayList<PokerHand> getRoyalFlushes(){
		ArrayList<PokerHand> royalFlushes = new ArrayList<>();
		for(int x=0; x<4; x++){
			PokerHand insert = new PokerHand();
			for(int i=10; i<15; i++) insert.addCard(new Card(i,x,Card.cards[x][i-2]));
			insert.evalHand();
			royalFlushes.add(insert);
		}
		return royalFlushes;
	}

	public String toString() {
		StringBuilder toReturn = new StringBuilder(Poker.centerAt(name,Screen.X/2)+Poker.getYBlank(2));
		String[] lines = Poker.getSpreadCardGraphics(hand,91);
		for(String s: lines) toReturn.append(Poker.centerAt(s,Screen.X/2)).append("\n");
		return toReturn.toString();
	}
	public String[] getGraphics() {
		String[] ret = new String[10];
		ret[0] = name;
		String[] lines = Poker.getSpreadCardGraphics(hand,91);
		System.arraycopy(lines, 0, ret, 3, 7);
		return ret;
	}
    public int compareTo(PokerHand other) {
		evalHand();
		other.evalHand();
        if (handVal < other.getHandVal()) return 1;
        else if (handVal > other.getHandVal()) return -1;
		else return breakTie(other);
    }
	public int breakTie(PokerHand other){
		int[] thisVals=valList();
		int[] otherVals=other.valList();
		int thisBest=-1;
		int otherBest=-1;
		for(int x=12; x>=0; x--) {
			if(thisVals[x]==otherVals[x]) continue;
			if(thisVals[x]!=0&&(thisBest==-1||thisVals[x]>thisVals[thisBest])) thisBest=x;
			if(otherVals[x]!=0&&(otherBest==-1||otherVals[x]>otherVals[otherBest])) otherBest=x;
		}
		return thisBest-otherBest;
	}
}
