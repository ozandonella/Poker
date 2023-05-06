package poker;

/**
 * This is the Screen class. I made this to be crude graphic saver. It stores strings inside an
 * array, so I can manipulate and print things to the console more efficiently. Also by default
 * when I use printScreen, any empty indices in the lines array will print new line characters
 * and I won't need to use a million println().
 */
public class Screen {
    private final StringBuilder[] lines;
    public static final int X=203;
    public static final int Y=53;

    /**
     * Constructor for Screen. Y is the vertical height in spaces every frame of my game takes up,
     * and X is the horizontal.
     */
    public Screen(){
        lines=new StringBuilder[Y];
        for(int x=0; x<lines.length; x++) lines[x]=new StringBuilder();
    }
    public void fillY(String fill, int start, int end){
        for(int x=start; x<=end; x++) lines[x].append(fill);
    }
    public void writeLines(String[] lines, int start, int center){
        for(int x=0; x<lines.length; x++) this.lines[start+x].append(Poker.centerAt(lines[x],center));
    }
    public void writeLines(String[] lines, int start){
        for(int x=0; x<lines.length; x++) writeLine(lines[x],start+x);
    }
    public void writeLine(String line, int ind, int center){
        lines[ind].append(Poker.centerAt(line,center));
    }
    public void writeLine(String line, int ind){
        if(line==null) return;
        lines[ind].append(Poker.centerAt(line,X/2));
    }
    public void clearLine(int ind){
        lines[ind].replace(0,lines[ind].length(),"");
    }
    public void clearLines(int start, int end){
        for(int x=start; x<=end; x++) clearLine(x);
    }
    public void clearScreen(){
        clearLines(0,Y-1);
    }
    public void printScreen() {
        for(StringBuilder s : lines) System.out.println(s);
    }
}
