package poker;
import java.util.ArrayList;
import java.util.Scanner;
import static poker.Poker.*;

public class Match {
    private static final Scanner input = new Scanner(System.in);
    private final Player human;
    private final Bot bot;
    private final ArrayList<Card> community;
    private int pot;
    private final Screen screen;
    private final int globalMin;
    private final String[] roundNames = {"Deal","Flop","Turn","River"};
    private int round;
    private final Deck deck;
    private boolean currentTurn;
    private boolean firstTurn;
    private int winner;
    public Match(Player human, Bot bot){
        this.human=human;
        this.bot=bot;
        pot=0;
        round=0;
        winner=0;
        globalMin=Math.max(1,(bot.getMoney()+human.getMoney())/20);
        screen=new Screen();
        community=new ArrayList<>();
        deck=new Deck();
        currentTurn=Math.random()>.5;
        firstTurn=currentTurn;
    }
    public void start(){
        deck.shuffle();
        playRound();
        if(!bot.didFold()&&!human.didFold())playRound();
        if(!bot.didFold()&&!human.didFold())playRound();
        if(!bot.didFold()&&!human.didFold())playRound();
        determineWinner();
        distributeEarnings();
        human.reset();
        bot.reset();
    }
    public void playRound(){
        if(round==0){
            while(bot.getKickers().size()<2) bot.getKickers().add(deck.deal());
            while(human.getKickers().size()<2) human.getKickers().add(deck.deal());
        }
        else if(round==1) while (community.size()<3) community.add(deck.deal());
        else community.add(deck.deal());
        clearBotText();
        updateGraphics();
        if(bot.getMoney()==0) writeBotText(bot+" is all in");
        if(human.getMoney()==0) writePlayerText("("+roundNames[round++]+")","you are all in","[CONTINUE]");
        else writePlayerText("The "+roundNames[round++], "[CONTINUE]");
        screen.printScreen();
        clearPlayerText();
        input.next();
        bot.calculateOdds(community);
        bot.calcBluff();
        bot.allocateBet();
        firstTurn=currentTurn;
        if(currentTurn)humanTurn();
        else botTurn();
    }

    public void botTurn(){
        updateGraphics();
        currentTurn=!currentTurn;
        if(bot.getMoney()==0||bot.getCurrentTotalBet()>human.getBet()) return;
        if(bot.getCurrentTotalBet()==human.getBet()&&human.getMoney()==0) return;
        if(bot.getCurrentTotalBet()==0&&human.getBet()==0) botStartBet();
        else if(bot.decide(human.getBet()-bot.getCurrentTotalBet())) botReturnBet();
        else bot.setFold(true);
    }
    private void botStartBet(){
         int botBet=bot.getCurrentBet(globalMin, human.getMoney());
         placeBotBet(botBet);
         if(bot.getMoney()==0) writeBotText(bot + " is all in (bet "+getAsMoney(botBet)+")","(buy in was: "+getAsMoney(globalMin)+")");
         else writeBotText(bot+" bets "+getAsMoney(botBet),"(buy in was: "+getAsMoney(globalMin)+")");
         updateGraphics();
         humanTurn();
    }
    private void botReturnBet(){
        int botBet, min=human.getBet()-bot.getCurrentTotalBet();
        botBet=bot.getCurrentBet(min,min+human.getMoney());
        if(bot.getMoney()==botBet){
            writeBotText(bot+ " is all in");
            placeBotBet(botBet);
            updateGraphics();
            return;
        }
        if(botBet==min) {
            if (min == 0) {
                writeBotText(bot + " checks");
                if (!firstTurn) humanTurn();
                else{
                    writePlayerText("[CONTINUE]");
                    screen.printScreen();
                    input.next();
                }
            }
            else {
                writeBotText(bot + " calls");
                writePlayerText("[CONTINUE]");
                placeBotBet(botBet);
                updateGraphics();
                screen.printScreen();
                input.next();
            }
            return;
        }
        writeBotText(bot.getCurrentTotalBet()==human.getBet() ? bot+ " bets "+getAsMoney(botBet) : bot+ " raises "+getAsMoney(botBet-min));
        placeBotBet(botBet);
        updateGraphics();
        if(botBet>min||human.getBet()==0) humanTurn();
    }
    public void humanTurn(){
        updateGraphics();
        currentTurn=!currentTurn;
        if(human.getMoney()==0||human.getBet()>bot.getCurrentTotalBet()) return;
        if(bot.getCurrentTotalBet()==human.getBet()&&bot.getMoney()==0) return;
        if(bot.getCurrentTotalBet()==0&&human.getBet()==0) playerStartBet();
        else if(bot.getCurrentTotalBet()==human.getBet()) playerFreeBet();
        else playerBoundBet();
        clearPlayerText();
    }
    private void playerStartBet(){
        clearBotText();
        if(human.getMoney()<=globalMin){
            writePlayerText("forced to go all in","[CONTINUE]");
            placePlayerBet(human.getMoney());
            updateGraphics();
            screen.printScreen();
            input.next();
            botTurn();
            return;
        }
        writePlayerText("RAISE[1]  CALL[2]","Minimum buy in ("+getAsMoney(globalMin)+")");
        screen.printScreen();
        if(Game.getInput(new String[]{"1","2"}).equals("1")){
            writePlayerText("Enter amount ","[ENTER]");
            screen.printScreen();
            placePlayerBet(globalMin);
            placePlayerBet(getBetInput());
        }
        else placePlayerBet(globalMin);
        updateGraphics();
        botTurn();
    }
    private void playerBoundBet(){
        String ans;
        int min = bot.getCurrentTotalBet()-human.getBet();
        if(min>=human.getMoney()) {
            writePlayerText("ALL IN[1]  FOLD[2]");
            screen.printScreen();
            clearBotText();
            if(Game.getInput(new String[]{"1","2"}).equals("1")) placePlayerBet(human.getMoney());
            else human.setFold(true);
            return;
        }
        if(bot.getMoney()==0){
            writePlayerText("CALL[1]  FOLD[2]");
            screen.printScreen();
            clearBotText();
            if(Game.getInput(new String[]{"1","2"}).equals("1")) placePlayerBet(min);
            else human.setFold(true);
            return;
        }
        writePlayerText("RAISE[1]  CALL[2]  FOLD[3]");
        screen.printScreen();
        clearBotText();
        ans=Game.getInput(new String[]{"1","2","3"});
        if(ans.equals("1")){
            writePlayerText("Enter amount ","[ENTER]");
            screen.printScreen();
            placePlayerBet(min);
            int bet = getBetInput();
            placePlayerBet(bet);
            if(bet>0) botTurn();
        }
        else if(ans.equals("2")) placePlayerBet(min);
        else human.setFold(true);
    }
    private void playerFreeBet(){
        String ans;
        writePlayerText("BET[1]  CHECK[2]  FOLD[3]");
        screen.printScreen();
        clearBotText();
        ans=Game.getInput(new String[]{"1","2","3"});
        if(ans.equals("1")){
            writePlayerText("Enter amount","[ENTER]");
            screen.printScreen();
            int bet=getBetInput();
            placePlayerBet(bet);
            if(bet>0||bet==0&&firstTurn) botTurn();
        }
        else if (ans.equals("3")) human.setFold(true);
        else if(firstTurn) botTurn();
    }
    public void clearBotText(){
        screen.clearLines(12,20);
    }
    public void writeBotText(String...text){
        clearBotText();
        screen.writeLines(text,12+(4-text.length/2));
    }
    public void clearPlayerText(){
        screen.clearLines(32,40);
    }
    public void writePlayerText(String...text){
        clearPlayerText();
        screen.writeLines(text,32+(4-text.length/2));
    }
    private String[] getCommunityGraphics(){
        String[] s = new String[7];
        String[] comm = getSpreadCardGraphics(fillWithBack(new ArrayList<>(community),5),95);
        for(int x=0; x<7; x++) s[x]=(x==6 ? centerX("Deck"," ",17) : centerX(Card.back.getGraphic()[x]," ",17)) + fillX(" ",25)+comm[x];
        return s;
    }
    public void updateGraphics(){
        clearGraphics();
        screen.writeLines(bot.getHeader(),0);
        screen.writeLines(Poker.getSpreadCardGraphics(Bot.getHiddenKickers(),37),3);
        screen.writeLine("Community",21);
        screen.writeLines(getCommunityGraphics(),22,81);
        screen.writeLine("Pot: "+getAsMoney(pot),31,22);
        screen.writeLines(Poker.getSpreadCardGraphics(human.getKickers(),37),42);
        screen.writeLines(human.getFooter(),51);
    }
    public void updateGraphicsUnhidden(){
        clearGraphics();
        screen.writeLines(bot.getHeader(),0);
        screen.writeLines(Poker.getSpreadCardGraphics(bot.getKickers(),37),3);
        screen.writeLine("Community",21);
        screen.writeLines(getCommunityGraphics(),22,81);
        screen.writeLine("Pot: "+getAsMoney(pot),31,22);
        screen.writeLines(Poker.getSpreadCardGraphics(human.getKickers(),37),42);
        screen.writeLines(human.getFooter(),51);
    }
    private void clearGraphics(){
        screen.clearLines(0,10);
        screen.clearLines(21,31);
        screen.clearLines(42,52);
    }
    public void placeBotBet(int bet){
        bot.placeBet(bet);
        pot+=bet;
    }
    public void placePlayerBet(int bet){
        human.placeBet(bet);
        pot+=bet;
    }
    public int getBetInput(){
        while(true){
            try{
                int i =(int)(input.nextDouble()*100);
                if(i>human.getMoney()) continue;
                return i;
            } catch(Exception ignored){

            }
        }
    }
    public void determineWinner(){
        clearBotText();
        clearPlayerText();
        updateGraphicsUnhidden();
        if(human.didFold()){
            writePlayerText("[CONTINUE]");
            winner=-1;
        }
        else if(bot.didFold()){
            writeBotText(bot+" folded");
            writePlayerText("[CONTINUE]");
            winner=1;
        }
        else{
            human.setHand(new ArrayList<>(community));
            bot.setHand(new ArrayList<>(community));
            int n=human.getHand().compareTo(bot.getHand());
            if(n>0){writePlayerText("you won","show hands","[CONTINUE]");winner=1;}
            else if(n<0){writePlayerText("you lost","show hands","[CONTINUE]");winner=-1;}
            else writePlayerText("you tied","show hands","[CONTINUE]");
            screen.printScreen();
            screen.clearScreen();
            input.next();
        }
    }
    public void distributeEarnings(){
        int botEarn=0, humanEarn=0;
        screen.clearLines(21,31);
        if(winner==1) humanEarn=pot;
        else if(winner==-1) botEarn=pot;
        else{humanEarn=pot/2;botEarn=pot-humanEarn;}
        screen.writeLine("you "+(botEarn>humanEarn ? "lost " : "gained ")+Poker.getAsMoney(Math.abs(humanEarn-human.getBet())),25);
        screen.writeLine("(total pot was "+Poker.getAsMoney(pot)+")",26);
        if(!human.didFold()&&!bot.didFold()){
            screen.writeLines(bot.getHand().getGraphics(),4);
            screen.writeLines(human.getHand().getGraphics(),35);
        }
        human.setMoney(human.getMoney()+humanEarn);
        bot.setMoney(bot.getMoney()+botEarn);
        pot=0;
        screen.writeLines(bot.getHeader(),0);
        screen.writeLines(human.getFooter(),51);
        screen.printScreen();
        input.next();
    }
}
