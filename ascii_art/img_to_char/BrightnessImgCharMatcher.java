package ascii_art.img_to_char;
import  image.Image;
import java.awt.*;
import java.util.HashMap;

/**
 * this class manages the logic that converts a color image to an ASCII image.
 */
public class BrightnessImgCharMatcher {

    private Image image;
    private String fontName;

    private static  final  int NUM_OF_PIXELS =16;
    private static  final  int MAX_RGB =255;
    private final HashMap<Image, Double> cache = new HashMap<>();


    /**
     * constructor
     * @param image the image to convert
     * @param fontName the character font
     */
    public BrightnessImgCharMatcher(Image image, String fontName){
        this.image = image;
        this.fontName = fontName;


    }


    /**
     * this function uses all the helper function below and makes the final array that representing the image
     * @param numCharsInRow number of characters for each line in the ASCII image
     * @param charSet array that contains the characters from which the ASCII image can be assembled
     * @return two-dimensional array of ASCII characters representing an image
     * (the same image obtained in the constructor).
     */
    public char[][] chooseChars(int numCharsInRow, Character[] charSet){
        if(charSet.length==0){
            return new char[0][];
        }
        double[] brightnessImage = brightnessImg(charSet);
        double[] normalizedBrightness= normalizedBrightness(brightnessImage);
        return convertImageToAscii(normalizedBrightness,numCharsInRow,charSet);



    }

    /*
     * this method calculates the brightness value of each character in the charSet
     * charSet-  array that contains the characters from which the ASCII image can be assembled
     * return- array of the brightness values of the given chars
     */
    private double[] brightnessImg(Character[] charSet){
        double[] brightnessArray= new double[charSet.length];
        double numOfTrue =0;
        for (int i = 0; i < charSet.length; i++) {
            boolean[][] boolArray = CharRenderer.getImg(charSet[i],NUM_OF_PIXELS,fontName);
            for (int j = 0; j < boolArray.length ; j++) {
                for (int k = 0; k < boolArray[j].length; k++) {
                    if(boolArray[j][k]){
                        numOfTrue++;
                    }
                }
            }
            double brightLevel = numOfTrue/(NUM_OF_PIXELS*NUM_OF_PIXELS);
            brightnessArray[i]= brightLevel;
            numOfTrue = 0;
        }
        return  brightnessArray;
    }

    /*
     * this method uses the maximal and the minimal brightness in the given array(by using helper method)
     * and goes throughout the given array and normalized each char
     * charsBrightness-  array of the brightness values of the chars
     * return  array of the brightness values of the chars after normalized them
     */
    private double[] normalizedBrightness(double[]charsBrightness){
        double[]  minMaxBrightness = findMinMaxBrightness(charsBrightness);
        if ( minMaxBrightness[1] == minMaxBrightness[0]){
           return new double[charsBrightness.length];

        }
       for (int j = 0; j < charsBrightness.length; j++) {
                charsBrightness[j] =
                        (charsBrightness[j]- minMaxBrightness[0])/(minMaxBrightness[1] - minMaxBrightness[0]);

            }
       return  charsBrightness;
    }

    /*
     * this method finds the minimal and the maximal value in the given charsBrightness array.
     * charsBrightness-  array of the brightness values of the chars
     * return- array with the  minimal and the maximal value
     */
    private double[] findMinMaxBrightness(double[]charsBrightness){
        double minBrightness = charsBrightness[0];
        double maxBrightness =charsBrightness[0];
        for (int i = 0; i < charsBrightness.length; i++) {
            if(charsBrightness[i]>maxBrightness){
                maxBrightness = charsBrightness[i];
            }
            if( charsBrightness[i] < minBrightness){
                minBrightness =charsBrightness[i];
            }

        }
        return new double[]{minBrightness,maxBrightness};


    }

    /*
     * this method calculates the average brightness of the whole image.
     * image- the image to calculate it's average
     * return- the  average brightness of the given image
     */
    private double averageBrightness(Image image){
        double sum =0;
        double numOfPixels =0;
        double theAverageBrightness = 0;
        for (Color pixel : image.pixels()){

            double greyColor = (pixel.getRed()* 0.2126) + (pixel.getGreen()* 0.7152) + (pixel.getBlue()* 0.0722);
            sum +=  greyColor;
            numOfPixels ++;

        }
        theAverageBrightness = sum/numOfPixels;
        return theAverageBrightness/MAX_RGB;

    }

    /*
     * this method calculates for each sub-image its brightness level, and match it with the character
     * with the closest brightness level. Keep this character in a matched position in the asciiArt array.
     * brightnessArray -array of the brightness values of the chars after normalized them
     * numCharsInRow -number of characters for each line in the ASCII image
     * charSet- array that contains the characters from which the ASCII image can be assembled
     * return- two-dimensional array of ASCII characters representing an image
     */
    private char[][] convertImageToAscii(double[] brightnessArray, int numCharsInRow,Character[] charSet){
        int pixels =image.getWidth()/numCharsInRow;
        int asciiArtRows = image.getHeight()/pixels;
        int asciiArtCols =  image.getWidth()/pixels;
        char[][] asciiArt= new  char[asciiArtRows][asciiArtCols];
        int row=0;
        int col=0;
        double averageBrightnessValue;
        for(Image subImage: image.squareSubImagesOfSize(pixels)){
            averageBrightnessValue = getBrightnessValue(subImage);
            int matchedIndex = findClosestChar(brightnessArray, averageBrightnessValue);
            asciiArt[row][col]= charSet[matchedIndex];
            if(col >= asciiArtCols -1)   {
                    col= 0;
                    row++;
            }
            else
                col++;
            }
    return asciiArt;

    }

    /*
     * this method adds to the hashmap pair of key-subImage , value-averageBrightness(by using helper function)
     * and returns the averageBrightnessValue witch is the value of the given key(subImage)
     * subImage- part of the big image that needs to be converted
     * return-the averageBrightnessValue of the given image
     */
    private double getBrightnessValue(Image subImage) {
        double averageBrightnessValue;
        if(cache.containsKey(subImage)){
            averageBrightnessValue = cache.get(subImage);
        }
        else{
            averageBrightnessValue = averageBrightness(subImage);
            cache.put(subImage,averageBrightnessValue);
        }
        return averageBrightnessValue;
    }

    /*
     * this method finds the closet brightness to the averageBrightnessValue of the subImage,
     * it calculates the gap of the averageBrightness and each element from the brightnessArray,
     * in that way it finds the matched index of the closest brightness in the given array
     * brightnessArray- array of the brightness values of the chars after normalized them
     * averageBrightnessValue-the average value of the subImage
     * return - matched index of the closest brightness in the given array
     */
    private int findClosestChar(double[] brightnessArray, double averageBrightnessValue) {
        double gap=Math.abs(averageBrightnessValue - brightnessArray[0]);
        int matchedIndex=0;
        for (int k = 0; k < brightnessArray.length; k++) {
            if (Math.abs(averageBrightnessValue - brightnessArray[k]) < gap) {
                gap = Math.abs(averageBrightnessValue - brightnessArray[k]);
                matchedIndex = k;
            }
        }
        return matchedIndex;
    }

}
