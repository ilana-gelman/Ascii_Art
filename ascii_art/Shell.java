package ascii_art;
import ascii_art.img_to_char.BrightnessImgCharMatcher;
import ascii_output.AsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;

import java.util.*;
import java.util.stream.Stream;

/**
 * this class  manages and asks from the user inputs that helps us to convert image to ascii art
 */
public class Shell {
    private static final String CMD_EXIT = "exit";
    private static final int INITIAL_CHARS_IN_ROW = 64;
    private static final int MIN_PIXELS_PER_CHAR = 2;
    private static final String FONT_NAME = "Courier New";
    private static final String OUTPUT_FILENAME = "out.html";
    private static final String INITIAL_CHARS_RANGE = "0-9";
    private static final String INVALID_INPUT = "illegal input,try again";
    private static final String INVALID_WIDTH = "Width out of range";
    private static final String CURRENT_RESOLUTION = "Width set to %d\n";

    private final int minCharsInRow;
    private final int maxCharsInRow;
    private int charsInRow;
    private BrightnessImgCharMatcher charMatcher;
    private AsciiOutput output;
    private Set<Character> charSet = new HashSet<>();

    /**
     * constructor
     * @param img the image to convert
     */
    public Shell(Image img) {
        minCharsInRow = Math.max(1, img.getWidth()/img.getHeight());
        maxCharsInRow = img.getWidth() / MIN_PIXELS_PER_CHAR;
        charsInRow = Math.max(Math.min(INITIAL_CHARS_IN_ROW, maxCharsInRow), minCharsInRow);
        charMatcher = new BrightnessImgCharMatcher(img, FONT_NAME);
        output = new HtmlAsciiOutput(OUTPUT_FILENAME, FONT_NAME);
        addChars(INITIAL_CHARS_RANGE);

    }

    /**
     * this method manages the users input and does all the legal actions he enters
     */
    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.print(">>> ");
        String cmd = scanner.nextLine().trim();
        String[] words = cmd.split("\\s+");
        while ( !( words.length ==1 && words[0].equals(CMD_EXIT))){
            if (!words[0].equals("")) {
                String param = "";
                if (words.length > 1) {
                    param = words[1];
                }
            }
            doOperation(words);
            System.out.print(">>> ");
            cmd = scanner.nextLine().trim();
            words = cmd.split("\\s+");
        }
    }

    /*
     * this method does the command that the user entered(chars,add,remove,res,render,console)
     * words- words array that contains the users input, split by " "
     */
    private void doOperation(String[] words) {
            switch (words[0]) {
                case "chars":
                    if(checkIllegalLength(words,1)){
                        break;
                    }
                    showChars();
                    break;
                case "add":
                    if(checkIllegalLength(words,2)){
                        break;
                    }
                    addChars(words[1]);
                    break;
                case "remove":
                    if(checkIllegalLength(words,2)){
                        break;
                    }
                    removeChars(words[1]);
                    break;
                case "res":
                    if(checkIllegalLength(words,2)){
                        break;
                    }
                    handleRes(words);
                    break;
                case "render":
                    if(checkIllegalLength(words,1)){
                        break;
                    }
                    render();
                    break;
                case  "console":
                    if(checkIllegalLength(words,1)){
                        break;
                    }
                    console();
                    break;
                case "":
                    break;

                default:
                    System.out.println(INVALID_INPUT);
                    break;


            }
    }

    /*
     * this method checks if the given input is in legal length
     * words- the users input as array
     * length- the desired length  the command should be
     * true if not legal , false otherwise
     */
    private  boolean checkIllegalLength(String[] words, int length){
        if(words.length !=length){
            System.out.println(INVALID_INPUT);
            return true;
        }
        return false;

    }

    /*
     * this method handles with the res command,
     * in case of res up it increases the resolution by multiply the chars in row
     * in case of res down it decreases the resolution by divide the chars in row
     * prints informative massage in case of invalid input or invalid chars in row
     *  splitedCmd- the users input
     */
    private void handleRes(String[] splitedCmd) {
        if ((!Objects.equals(splitedCmd[1], "up") && !Objects.equals(splitedCmd[1], "down")) ) {
            System.out.println(INVALID_INPUT);
        } else {
            resChange(splitedCmd[1]);
            if (charsInRow > maxCharsInRow) {
                charsInRow = maxCharsInRow;
                System.out.println(INVALID_WIDTH);
            } else if (charsInRow < minCharsInRow) {
                charsInRow = minCharsInRow;
                System.out.println(INVALID_WIDTH);
            } else {
                System.out.printf(CURRENT_RESOLUTION, charsInRow);

            }

        }
    }

    /*
     * this method increases or decreases the chars in row, according to the users input
     * command- the action to do
     */
    private void resChange(String command){
        if(command.equals("up")){
            charsInRow =charsInRow *2;
        }
        if( command.equals("down")){
            charsInRow =charsInRow /2;
        }

    }

    /*
     * this method prints the current chars in the set
     */
    private void showChars(){
        charSet.stream().sorted().forEach(c-> System.out.print(c + " "));
        System.out.println();
    }

    /*
     * this method parsers the "add" command, and adds to the chat the needed range of chars
     * param- what to add
     * return -array of the first chars to add and the last char to add
     */
    private static char[] parseCharRange(String param){
        if(param.length() ==1){
            char p =param.charAt(0);
            return  new char[]{p, p};

        }
        if (param.equals("all")){
            return new char[]{' ', '~'};

        }
        if(param.equals("space")){
            return  new char[]{' ', ' '};
        }
        String[] seperatedParam = param.split("-",2);
        if((seperatedParam.length ==2) && (seperatedParam[0].length()==1) && (seperatedParam[1].length()==1)){
            return CharsRange(seperatedParam);
        }
        System.out.println(INVALID_INPUT);
        return null;


    }

    /*
     * this method handles the case that the users asked to add more than one char in the given range
     * seperatedParam- the range to add
     * return -the first char and the last char
     */
    private static char[] CharsRange(String[] seperatedParam) {
        char char1 = seperatedParam[0].charAt(0);
        char char2 = seperatedParam[1].charAt(0);
        if(char1 > char2){
            return  new char[]{char2, char1};
        }
        else {
            return new  char[]{char1,char2};
        }
    }

    /*
     * this method add chars to the set
     * toAdd - the chars to add
     */
    private void addChars(String toAdd) {
        char[] range = parseCharRange(toAdd);
        if(range != null){
            Stream.iterate(range[0], c -> c <= range[1], c -> (char)((int)c+1)).forEach(charSet::add);
        }
    }

    /*
     * this method removes chars from the set
     * toRemove- the chars to remove
     */
    private void removeChars(String toRemove){
        char[] range = parseCharRange(toRemove);
        if(range != null){
            Stream.iterate(range[0], c -> c <= range[1], c -> (char)((int)c+1)).forEach(charSet::remove);
        }

    }

    /*
     * this method handles the render command
     * makes a html file with the converted image
     */
    private void render(){
        if( charSet.size() !=0 ){
        Character[] charactersArray= new Character[charSet.size()];
        charactersArray = charSet.toArray(charactersArray);
        output.output(charMatcher.chooseChars(charsInRow,charactersArray));}

    }

    /*
     * this method handles the console command
     * prints to the console the converted image
     */
    private  void  console(){
        this.output = new ConsoleAsciiOutput();
    }



}
