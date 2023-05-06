package poker;

import java.util.ArrayList;

public class Test {
	public static void main(String[] args) {
		Game g = new Game();
		g.start();
	}
	public static void runBetTests(int commSize){
		System.out.println(Poker.fillX("-",Screen.X));
		Bot bot = new Bot(100);
		Deck d = new Deck();
		d.shuffle();
		ArrayList<Card> comm = new ArrayList<>();
		while(comm.size()<commSize) comm.add(d.deal());
		Poker.printArr(Poker.getSpreadCardGraphics(comm,100),Screen.X/2);
		while(bot.getKickers().size()<2) bot.getKickers().add(d.deal());
		Poker.printArr(Poker.getSpreadCardGraphics(bot.getKickers(),50),Screen.X/2);
		bot.calculateOdds(comm);
		bot.calcBluff();
		bot.allocateBet();
		System.out.println();
		System.out.println(Poker.centerX(Poker.getAsMoney(bot.getCurrentBet(0, bot.getMoney()))+" / "+Poker.getAsMoney(100)));
		System.out.println(Poker.centerX("bot odds: "+"%"+ Math.floor(bot.getOdds()*10000)/100));
		System.out.println(Poker.centerX("bot was bluffing: "+bot.getBluff()));
		System.out.println();
		if(commSize>2){
			Poker.printArr(bot.getHand().getGraphics(),Screen.X/2);
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println();
		}
		System.out.println(Poker.fillX("-",Screen.X));
	}
	public static void runOddsTests(){
		System.out.println("Expected results");
		System.out.println("0.1341350601295097");
		System.out.println("0.1294685990338164");
		System.out.println("0.13535353535353536");
		System.out.println();
		System.out.println();
		Bot bot = new Bot(100);
		Deck d = new Deck();
		ArrayList<Card> arr=d.getDeck();
		ArrayList<Card> comm=new ArrayList<>();
		ArrayList<Card> hand=new ArrayList<>();

		bot.getKickers().add(arr.get(29));
		arr.remove(29);
		bot.getKickers().add(arr.get(9));
		arr.remove(9);

		comm.add(arr.get(34));
		arr.remove(34);
		comm.add(arr.get(15));
		arr.remove(15);

		while(comm.size()<5){
			comm.add(arr.get(0));
			arr.remove(0);
			System.out.println(bot.getKickers());
			System.out.println(comm);
			bot.calculateOdds(comm);
			System.out.println(bot.getOdds());
			System.out.println(bot.getHand());
			System.out.println();
			System.out.println();
		}
	}
}
