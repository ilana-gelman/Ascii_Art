package ascii_art;

import image.Image;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * this class contains some functions that implements algorithms
 */
public class Algorithms {


    /**
     * this function gets an array of numbers in range of n ,while the array length is n+1
     * one number appears in this array more than once, and the function finds this number
     * @param numList array of numbers
     * @return the number that appears more than one
     */

    public static int findDuplicate(int[] numList) {
        if(numList.length<=1){
            return -1;
        }
        int slowerRunner = numList[0];
        int fasterRunner = numList[0];
        slowerRunner = numList[slowerRunner];
        fasterRunner = numList[numList[fasterRunner]];
        while (slowerRunner != fasterRunner){
            slowerRunner = numList[slowerRunner];
            fasterRunner = numList[numList[fasterRunner]];
        }
        slowerRunner = numList[0];
        while (slowerRunner != fasterRunner) {
            slowerRunner = numList[slowerRunner];
            fasterRunner = numList[fasterRunner];
        }
        return fasterRunner;
    }


    /**
     * this functions gets a list of words and builds their morse translation,
     * and returns how many unique morse representations there are
     * @param words list of word to check from the unique morse representations
     * @return number of the unique morse representations
     */
    public static int uniqueMorseRepresentations(String[] words){
        String[] morse = {".-","-...","-.-.","-..",".","..-.","--.",
                "....","..",".---","-.- ",".-..","--","-.","---",".--.","--.-",".-.","...",
                "-","..-","...-",".--","-..-","-.--","--.."};

        Set<String>uniqueMorses = new HashSet<>();

        for (int i = 0; i < words.length; i++) {
            String morseWord = "";
            for (int j = 0; j < words[i].length() ; j++) {
                char oneChar= words[i].charAt(j);
                morseWord += morse[oneChar- 97];

            }
            uniqueMorses.add(morseWord);

        }
        return uniqueMorses.size();

    }





}
