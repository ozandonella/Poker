package poker;
import java.util.ArrayList;
import static poker.Poker.*;
public class Player {

	private final String name;
	private int bet;
	private int money;
	private PokerHand hand;
	private ArrayList<Card> kickers;
	private boolean folded;
	
	public Player(String name) {
		bet=0;
		money=0;
		this.name=name;
		hand=new PokerHand();
		kickers=new ArrayList<>();
		folded=false;
	}
	public boolean didFold(){
		return folded;
	}
	public void setFold(boolean folded){
		this.folded=folded;
	}
	public int getMoney() {
		return money;
	}
	public void setMoney(int money) {
		this.money = money;
	}
	public PokerHand getHand() {
		return hand;
	}
	public int getBet() {
		return bet;
	}
	public ArrayList<Card> getKickers() {
		return kickers;
	}
	public void setHand(ArrayList<Card> comm){
		comm.addAll(kickers);
		hand=Bot.getBestHandFrom(comm);
	}
	public void placeBet(double bet){
		this.bet+=bet;
		money-=bet;
	}
	public void reset(){
		bet=0;
		hand=null;
		kickers=new ArrayList<>();
		folded=false;
	}
	public String[] getFooter(){
		return (String.format("Money: $%,-174.2f   Goal: $%,.2f\n",(double)money/100,1000000f)+centerX(name,"/")).split("\n");
	}
	
}
