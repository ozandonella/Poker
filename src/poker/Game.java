package poker;
import java.util.Locale;
import java.util.Scanner;
import static poker.Poker.*;
import static poker.Screen.*;
public class Game {
    private static final Scanner input = new Scanner(System.in);
    private Player player;
    private final Screen screen;
    public Game(){
        screen=new Screen();
    }
    public void start(){
        adjustScreen();
        startScreen();
        mainMenuScreen();
    }
    public void playMatch(Bot bot) {
        screen.clearScreen();
        Match match = new Match(player, bot);
        match.start();
        if (player.getMoney() > 0 && bot.getMoney() > 0){
            screen.clearScreen();
            screen.writeLines(bot.getHeader(),0);
            screen.writeLine("REMATCH[1]", Y/2-1);
            screen.writeLine("MAIN MENU[2]", Y/2);
            screen.writeLines(player.getFooter(),51);
            screen.printScreen();
            if(getInput(new String[]{"1","2"}).equals("1")) playMatch(bot);
            else mainMenuScreen();
        }
        else{
            screen.fillY(fillX("/", X),0, Y/2-5);
            screen.fillY(fillX("/", X),Y/2-3, Y/2);
            screen.fillY(fillX("/", X),Y/2+2, Y-1);
            if(player.getMoney()==0){
                screen.writeLine(centerX("You lose!!","/", X), Y/2-4);
                screen.writeLine(fillX("/", X), Y/2+1);
                screen.printScreen();
            }
            else{
                screen.writeLine(centerX("You win!!","/", X), Y/2-4);
                screen.writeLine(centerX("[CONTINUE]","/", X), Y/2+1);
                screen.printScreen();
                input.next();
                mainMenuScreen();
            }
        }
    }
    public void adjustScreen() {
        screen.writeLine("/"+fillX("-",X-2)+"\\",0);
        screen.fillY("|"+getXBlank(X-2)+"|",1,Y/2-2);
        screen.writeLine("|"+getXBlank(83)+"Adjust console to these constraints"+getXBlank(83)+"|",Y/2-1);
        screen.writeLine("|"+getXBlank(76)+"(input any character and press enter to continue)"+getXBlank(76)+"|", Y/2);
        screen.writeLine("|"+getXBlank(96)+"[CONTINUE]"+getXBlank(95)+"|", Y/2+1);
        screen.fillY("|"+getXBlank(X-2)+"|",Y/2+2,Y-2);
        screen.writeLine("\\"+fillX("-",X-2)+"/",Y-1);
        screen.printScreen();
        screen.clearScreen();
        input.next();
    }
    public void startScreen() {
        screen.writeLines(Card.back.getGraphic(),8);
        screen.writeLine(centerX("Welcome to Poker on the Console!"),19);
        screen.writeLine(centerX("[ENTER NAME]"),25);
        screen.printScreen();
        screen.clearScreen();
        player=new Player(input.next());
        player.setMoney(7500);
    }
    public void mainMenuScreen() {
        screen.clearScreen();
        screen.writeLine(fillX("/",X),0);
        screen.writeLines(Card.back.getGraphic(),18);
        screen.writeLine("Poker on the Console",28);
        screen.writeLine("PLAY[1]",30);
        screen.writeLine("TUTORIAL[2]",31);
        screen.writeLines(player.getFooter(),51);
        screen.printScreen();
        if(getInput(new String[] {"1","2"}).equals("1")) playMatch(new Bot(player.getMoney()));
        else tutorial();
    }
    public void tutorial(){
        screen.clearScreen();
        screen.writeLine(fillX("/",X),0);
        screen.writeLine("You will be playing 5-card Texas Holdem against our poker bots who have been training since January 12 2004",18);
        screen.writeLine("Your goal is to make one million dollars",19);
        screen.writeLine("[Enter]",22);
        screen.writeLines(player.getFooter(),51);
        screen.printScreen();
        screen.clearScreen();
        input.next();
        int suit=0;
        while(true){
            screen.writeLine(fillX("/",X),0);
            screen.writeLine("Here are some of the cards you will be playing with ("+Card.suits[suit]+")",4);
            screen.writeLines(PokerHand.getRoyalFlushes().get(suit).getGraphics(),10);
            screen.writeLines(Poker.getSpreadCardGraphics(Deck.getCardsOfSuit(suit++),X/2),21);
            if(suit==4) suit=0;
            screen.writeLine("CONTINUE[1]",30);
            screen.writeLine("SHOW "+Card.suits[suit].toUpperCase(Locale.ROOT)+"[2]",31);
            screen.writeLines(player.getFooter(),51);
            screen.printScreen();
            screen.clearScreen();
            if(getInput(new String[] {"1","2"}).equals("1"))break;
        }
        screen.clearScreen();
        screen.writeLine(fillX("/",X),0);
        screen.writeLine("We have given you $75.00",19);
        screen.writeLine("If you run out of money, you lose!",20);
        screen.writeLine("[Enter]",23);
        screen.writeLines(player.getFooter(),51);
        screen.printScreen();
        screen.clearScreen();
        input.next();
        mainMenuScreen();
    }

    public static String getInput(String[] possibleInputs){
        while(true){
            String ans = input.next();
            for(String s : possibleInputs) if(s.equals(ans)) return ans;
        }
    }
}
