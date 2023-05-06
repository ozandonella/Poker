package poker;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Poker class where I store a bunch of helpful methods. Most of them manipulate strings
 * for centering and filling graphics on the screen. The others are just accessing the txt
 * files where the card graphics are stored.
 */
public class Poker {
    public static final int CARD_SIZE=7;
    public static final int NAME_SIZE=17;

    /**
     * the first two dimensions of the returned String array are for a card's given suit and value.
     * then the third dimension is for the card's graphic array. (ex: at cards[2][0] we find the Two of Diamonds graphic)
     *    twoOfDiamondsGraphic[]
     *       _____ 0
     *      |2    |1
     *      |  ^  |2
     *      |     |3
     *      |  ^  |4
     *      |____Z|5
     * @return structured card graphics array
     */
    public static String[][][] getCardGraphics(){
        Scanner read = getScanner("allCards.txt");
        String[][][] cards= new String[4][13][6];
        int val=0;
        int suit=0;
        while(read.useDelimiter(",").hasNext()){
            if(val==13) suit++;
            val%=13;
            cards[suit][val]=read.next().split(";");
            for(int x=1; x<6; x++)
                cards[suit][val][x]=cards[suit][val][x].strip();
            val++;
        }
        return cards;
    }

    public static String[] getBackGraphic() {
        Scanner read = getScanner("backOfCard.txt");
        String[] temp=new String[6];
        for(int x=0; x<6; x++){
            temp[x]=read.useDelimiter(";").next();
            if(x>0) temp[x]=temp[x].strip();
        }
        return temp;
    }

    /**
     * This method takes in a list of cards and a given distance and concatenates them
     * together, so they can be printed alongside each other. the method starts by taking the
     * first bar in the first card's graphic array and finding the correct amount of space to add between
     * the next card's first bar, and so on. however, this method also attaches the names of these cards
     * underneath so the very first thing it checks for is if it can fit all the full names under the cards,
     * if it cant it uses the two character long short names.
     * @param arr: the list of cards to concatenate
     * @param dist: the desired distance for them to be spread across
     * @return a string array filled with the concatenated graphics.
     */
    public static String[] getSpreadCardGraphics(ArrayList<Card> arr, int dist){
        int unit = dist-(arr.size()*NAME_SIZE)> arr.size() ? NAME_SIZE : CARD_SIZE;
        int freeSpace=dist-(arr.size()*unit);
        String[] s = new String[7];
        for(int x=0; x<7; x++) {
            StringBuilder tempB = new StringBuilder();
            int tempfree = freeSpace;
            for (int i=0; i < arr.size(); i++){
                String temp= x!=6 ? arr.get(i).getGraphic()[x] : (unit==NAME_SIZE ? arr.get(i).getName() : arr.get(i).getNameShort());
                temp=centerX(temp," ",unit);
                tempB.append(temp);
                if(i<arr.size()-1){
                    int space=tempfree/((arr.size()-1)-i);
                    tempB.append(getXBlank(space));
                    tempfree-=space;
                }
            }
            s[x]=tempB.toString();
        }
        return s;
    }
    public static Scanner getScanner(String URI){
        try{
            return new Scanner(Paths.get(URI));
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public static String getAsMoney(int money){
        return String.format("$%,.2f",(double) money/100);
    }
    public static ArrayList<Card> fillWithBack(ArrayList<Card> list, int size){
        while(list.size()<size)list.add(Card.back);
        return list;
    }
    public static String centerAt(String s, int center) {
        StringBuilder sb = new StringBuilder();
        while(sb.length()+s.length()/2<center) sb.append(" ");
        return sb.append(s).toString();
    }
    public static void printArr(String[] arr, int center){
        for(String s : arr){
            if(s==null) continue;
            System.out.println(centerAt(s,center));
        }
    }
    public static String getYBlank(int size) {
        StringBuilder blank=new StringBuilder();
        while(blank.length()<size) blank.append('\n');
        return blank.toString();
    }
    public static String getXBlank(int size) {
        StringBuilder blank=new StringBuilder();
        while(blank.length()<size) blank.append(" ");
        return blank.toString();
    }
    public static String fillX(String c, int size) {
        StringBuilder sb=new StringBuilder();
        while(sb.length()<size) sb.append(c);
        return sb.toString();
    }
    public static String centerX(String s) {
        return fillX(" ",(Screen.X-s.length())/2)+s+fillX(" ",(Screen.X-s.length())/2);
    }
    public static String centerX(String s, String fill) {
        return fillX(fill,(Screen.X-s.length())/2)+s+fillX(fill,(Screen.X-s.length())/2);
    }
    public static String centerX(String s, String fill, int dist){
        s=fillX(fill,(dist-s.length())/2)+s;
        return s+fillX(fill,(dist-s.length()));
    }
}
