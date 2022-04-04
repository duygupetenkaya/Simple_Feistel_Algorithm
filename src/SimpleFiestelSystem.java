import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class SimpleFiestelSystem {
    //FIXED VARIABLES FOR FEISTEL CIPHER
    static int[] IP = {6,4,2,8,7,5,3,1};
    static final int blockSize = 8;
    static ArrayList<String> blocks = new ArrayList<>();
    static ArrayList<String> initiallyPermutatedBlocks = new ArrayList<>();
    static ArrayList<String> encodedBlocks = new ArrayList<>();
    static ArrayList<String> shiftedBlocks = new ArrayList<>();
    static String leftNibble="";
    static String rightNibble="";

    // Step 1- Your code will read a plaintext from a file block-by-block with block size of 8 characters
    public static void readTextFileBlockByBlock(String filename) {
        String inputFileString = "";
        try {
            FileInputStream file=new FileInputStream(filename);
            Scanner sc = new Scanner(file);

            // to know end of the text
            sc.useDelimiter("\\Z");

            while(sc.hasNextLine())
            {
                inputFileString = sc.next();

                // to change whitespaces with underscore
                inputFileString=inputFileString.toLowerCase().replaceAll("\\s+","_");

                //if length of the string is less than 8 bits, add zeros until the length is 8 bits.
                if(inputFileString.length()%blockSize!=0) {
                    while(inputFileString.length()%blockSize!=0) {
                        inputFileString = inputFileString + "0";
                    }
                }

                int index=0;

                // to add each 8 characters as a block to ArrayList of blocks
                while (index < inputFileString.length()) {
                    blocks.add(inputFileString.substring(index, Math.min(index + 8,inputFileString.length())));
                    index += 8;
                }

            }
            sc.close();

        }
        catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }



    }

    // Step 2- A permutation code of length 8 is chosen to be IP = 6 4 2 8 7 5 3 1. The 8-character text
    //blocks will be permutated using the initial permutation (IP) code.
    public static String permutateStringBlocks(String blockToPermutate){
        StringBuilder permutationBuilder = new StringBuilder();

        for (int j : IP) {

            permutationBuilder.append(blockToPermutate.charAt(j-1));
        }
        return permutationBuilder.toString();
    }

    // Step 3- Use either BINARY values of characters from Table 1 to encode characters so that each
    //character will be 8 bits of length.
    // Since I could not understand the table format, I used binary values directly
    public static String encodeToBinary(String blockToEncode){
        StringBuilder encodingBuilder = new StringBuilder();
        char[] chars = blockToEncode.toCharArray();

        for (char aChar : chars) {
            encodingBuilder.append(
                    String.format("%8s", Integer.toBinaryString(aChar)).replaceAll(" ", "0")                         // zero pads
            );

        }

        return encodingBuilder.toString();
    }

    // Step 4- Perform a Shift-right-rotate operation with 4 positions on the encoded characters.
    public static String shiftRightRotate(String blockToBeShifted) {
            StringBuilder shiftingBuilder = new StringBuilder();
            for (int j = 0; j < blockToBeShifted.length(); j++) {
                shiftingBuilder.append(blockToBeShifted.charAt((j + 4) % blockToBeShifted.length()));
            }
        return shiftingBuilder.toString();
    }

    // Step 5-  Get 2 characters at a time from the encoded block: Characters
    // at odd positions are placed into the left nibble and characters with even
    // positions are placed into the right nibble.
    public static String[] createNibbles(String blockToNibble){

         }

    public static void main(String[] args) {

         readTextFileBlockByBlock("src/plainText.txt");

        System.out.println(blocks);

        for (String block :blocks) {
            initiallyPermutatedBlocks.add(permutateStringBlocks(block));
        }

        System.out.println(initiallyPermutatedBlocks);

        for(String block : initiallyPermutatedBlocks){
            encodedBlocks.add(encodeToBinary(block));
        }

        System.out.println(encodedBlocks);

        for(String block : encodedBlocks){
            shiftedBlocks.add(shiftRightRotate(block));
        }

        System.out.println(shiftedBlocks);



    }

}

