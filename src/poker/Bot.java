package poker;

import java.util.ArrayList;
import java.util.Comparator;
import static poker.Poker.*;

public class Bot {
    private int allocatedBet;
    private int currentTotalBet;
    private boolean folded;
    private int money;
    private double odds;
    private PokerHand hand;
    private ArrayList<Card> kickers;
    private boolean bluffing;

    public Bot(int money){
        allocatedBet=0;
        setRandMoney(money);
        bluffing=false;
        hand=null;
        odds=0;
        kickers=new ArrayList<>();
        folded=false;
    }
    public boolean didFold(){
        return folded;
    }
    public void setFold(boolean folded){
        this.folded=folded;
    }
    public ArrayList<Card> getKickers(){
        return kickers;
    }
    public int getMoney() {
        return money;
    }
    public void setMoney(int money) {
        this.money = money;
    }
    public double getOdds(){
        return odds;
    }
    public boolean getBluff(){
        return bluffing;
    }
    public void reset(){
        currentTotalBet=0;
        allocatedBet=0;
        hand=null;
        bluffing=false;
        odds=0;
        kickers=new ArrayList<>();
        folded=false;
    }
    public void setRandMoney(int money) {
        int extra=(int)(Math.random()*(money/10));
        this.money=money;
        this.money+=Math.random()>.5d ? extra : -extra;
    }
    public PokerHand getHand() {
        return hand;
    }
    public static ArrayList<Card> getHiddenKickers(){
        ArrayList<Card> arr = new ArrayList<>();
        arr.add(Card.back);
        arr.add(Card.back);
        return arr;
    }
    public int getCurrentTotalBet(){
        return currentTotalBet;
    }
    public int getCurrentBet(int minBet, int maxBet) {
        maxBet=Math.max(minBet,maxBet);
        System.out.println("minbet: "+minBet+" maxbet: "+maxBet+" allocatedbet: "+allocatedBet);
        if(minBet>allocatedBet) return Math.min(minBet,money);
        double mult = bluffing ? .2*Math.random() : Math.random();
        int b = (allocatedBet-minBet)/2+minBet;
        System.out.println("rawbet b : "+b);
        if (b-minBet >= money/10) b-=((b-minBet)*mult);
        System.out.println("bet b : "+b);
        if (b-minBet <= money/10) return Math.random()>.8 ? Math.min(b,maxBet) : minBet;
        return Math.random()>.3 ? Math.min(b,maxBet) : minBet;
    }
    public boolean decide(int minBet){
        if(allocatedBet>=minBet&&!bluffing||minBet==0) return true;
        return Math.random()<odds;
    }
    public void allocateBet(){
        double mult = bluffing ? Math.max(odds+Math.random()*.5,.65) : odds;
        System.out.println("bet allocating");
        System.out.println("mult: "+mult+" money: "+money);
        int round = (int) ((money*mult)/2);
        allocatedBet = round==0 ? money : round;
    }
    public void calcBluff(){
        if(bluffing) bluffing = odds<.5;
        else bluffing = odds<.5&&Math.random()<.15;
    }
    public void placeBet(int bet){
        currentTotalBet+=bet;
        allocatedBet-=bet;
        money-=bet;
    }
    public void setHand(ArrayList<Card> comm){
        comm.addAll(kickers);
        hand=Bot.getBestHandFrom(comm);
    }
    public String[] getHeader(){
        return (centerX(toString(),"/")+"\nMoney: "+getAsMoney(getMoney())+fillX(" ",Screen.X)).split("\n");
    }
    private void setBestHand(ArrayList<Card> community){
        ArrayList<ArrayList<Card>> prefixes = getGroups(new ArrayList<>(kickers),5-community.size(), kickers.size());
        hand = getBestHandWithKicker(prefixes,new ArrayList<>(community));
    }
    private void calculateOddsNoComm(){
        ArrayList<Card> opponentCardPool = Deck.getAllCards();
        ArrayList<ArrayList<Card>> oppKickers = getGroups(new ArrayList<>(opponentCardPool),2, kickers.size());
        double wins=0;
        for(ArrayList<Card> k : oppKickers) if(PokerHand.compareKickers(kickers,k)>=0) wins++;
        odds=wins/oppKickers.size();
    }
    public void calculateOdds(ArrayList<Card> community){
        if(community.size()<3){
            calculateOddsNoComm();
            return;
        }
        setBestHand(community);
        ArrayList<Card> opponentCardPool = Deck.getAllCards();
        removeCards(kickers,opponentCardPool);
        removeCards(community,opponentCardPool);
        ArrayList<ArrayList<Card>> opposingKickers = getGroups(new ArrayList<>(opponentCardPool),2, 2);
        double total=0;
        double wins=0;
        for(ArrayList<Card> kicker : opposingKickers){
            ArrayList<ArrayList<Card>> opposingPermutations = getGroups(new ArrayList<>(kicker),5-community.size(), 2);
            PokerHand tempBest = getBestHandWithKicker(opposingPermutations,new ArrayList<>(community));
            if(hand.compareTo(tempBest)>0) wins++;
            total++;
        }
        odds=wins/total;
    }
    private static PokerHand findBestIn(ArrayList<PokerHand> hands){
        PokerHand best = null;
        for (PokerHand hand : hands){
            if (best==null) best = hand;
            if(best.compareTo(hand) < 0) best=hand;
        }
        return best;
    }
    public static PokerHand getBestHandWithKicker(ArrayList<ArrayList<Card>> prefixes, ArrayList<Card> possibleCards){
        PokerHand best=null;
        for(ArrayList<Card> pre : prefixes){
            PokerHand other = getBestHandFrom(pre,possibleCards);
            if(best==null) best=other;
            else if(best.compareTo(other)<0) best=other;
        }
        return best;
    }
    public static PokerHand getBestHandFrom(ArrayList<Card> currentCards, ArrayList<Card> otherCards) {
        ArrayList<PokerHand> hands = getHands(currentCards, otherCards);
        return findBestIn(hands);
    }
    public static PokerHand getBestHandFrom(ArrayList<Card> cards) {
        ArrayList<PokerHand> hands = getHands(new ArrayList<>(), cards);
        return findBestIn(hands);
    }
    private static ArrayList<PokerHand> getHands(ArrayList<Card> starting, ArrayList<Card> cards){
        ArrayList<PokerHand> hands = new ArrayList<>();
        generateHands(hands,cards,starting,0);
        return hands;
    }
    private static ArrayList<ArrayList<Card>> getGroups(ArrayList<Card> cards, int minSize, int maxSize){
        ArrayList<ArrayList<Card>> groups = new ArrayList<>();
        generateGroups(groups,cards,new ArrayList<>(),0, minSize, maxSize);
        return groups;
    }
    private static void generateHands(ArrayList<PokerHand> hands, ArrayList<Card> cards, ArrayList<Card> curr, int ind) {
        if(curr.size()==5) {
            hands.add(new PokerHand(new ArrayList<>(curr)));
            return;
        }
        for(int x=ind; x<cards.size(); x++) {
            curr.add(cards.get(x));
            generateHands(hands, cards, curr, x+1);
            curr.remove(curr.size()-1);
        }
    }
    private static void generateGroups(ArrayList<ArrayList<Card>> groups, ArrayList<Card> cards, ArrayList<Card> curr, int ind, int minSize, int maxSize) {
        if(ind>=cards.size()||curr.size()==maxSize) return;
        curr.add(cards.get(ind));
        generateGroups(groups, cards, curr, ind+1, minSize, maxSize);
        if(curr.size()>=minSize)groups.add(new ArrayList<>(curr));
        curr.remove(curr.size()-1);
        generateGroups(groups, cards, curr, ind+1, minSize, maxSize);
    }
    private static void removeCards(ArrayList<Card> cards, ArrayList<Card> allCards){
        allCards.sort(Comparator.comparingInt(Card::getValue));
        for (Card card : cards)
            for (int i = 0; i < allCards.size(); i++) if (card.equals(allCards.get(i))) allCards.remove(i--);
    }
}
