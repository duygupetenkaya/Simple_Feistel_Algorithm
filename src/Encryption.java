import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Encryption {


    // Step 1- Your code will read a plaintext from a file block-by-block with block size of 8 characters
    public static ArrayList<String> readTextFileBlockByBlock(String filename) {
        ArrayList<String> textBlocks = new ArrayList<>();
        String inputFileString;
        try {
            FileInputStream file = new FileInputStream(filename);
            Scanner sc = new Scanner(file);

            // to know end of the text
            sc.useDelimiter("\\Z");

            while (sc.hasNextLine()) {
                inputFileString = sc.next();

                // to change whitespaces with underscore
                inputFileString = inputFileString.replaceAll("\\s+", "_");

                //if length of the string is less than 8 bits, add zeros until the length is 8 bits.
                if (inputFileString.length() % 8 != 0) {
                    while (inputFileString.length() % 8 != 0) {
                        inputFileString = inputFileString + "0";
                    }
                }

                int index = 0;

                // to add each 8 characters as a block to ArrayList of blocks
                while (index < inputFileString.length()) {
                    textBlocks.add(inputFileString.substring(index, Math.min(index + 8, inputFileString.length())));
                    index += 8;
                }

            }
            sc.close();

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return
                textBlocks;
    }

    // Step 3- Use either BINARY values of characters from Table 1 to encode characters so that each
    //character will be 8 bits of length.
    public static String encodeToBinary(String blockToEncode) {
        StringBuilder encodingBuilder = new StringBuilder();
        char[] chars = blockToEncode.toCharArray();

        for (char aChar : chars) {
            if (SimpleFiestelSystem.encodingTable.get(aChar) != null)
                encodingBuilder.append(String.format("%8s", Integer.toBinaryString(SimpleFiestelSystem.encodingTable.get(aChar))).replaceAll(" ", "0"));
        }

        return encodingBuilder.toString();
    }

    public static void writeCipheredTextToFile(String cipheredText) {
        try {
            FileWriter fw = new FileWriter("src//cipheredText.txt");
            fw.write(cipheredText);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void encryptionFunction(String filePathOfPlainText) {


        ArrayList<String> initiallyPermutatedBlocks = new ArrayList<>();
        ArrayList<String> encodedBlocks = new ArrayList<>();
        ArrayList<String> shiftedBlocks = new ArrayList<>();
        ArrayList<String> leftNibbles = new ArrayList<>();
        ArrayList<String> rightNibbles = new ArrayList<>();
        ArrayList<String> mergedNibbleBlocks = new ArrayList<>();
        ArrayList<String> reversePermutatedBlocks = new ArrayList<>();

        ArrayList<String> textBlocks = readTextFileBlockByBlock(filePathOfPlainText);

        System.out.println("Original Blocks:");
        System.out.println(textBlocks);

        for (String block : textBlocks) {
            initiallyPermutatedBlocks.add(SimpleFiestelSystem.permutateStringBlocks(block, SimpleFiestelSystem.IP));
        }
        System.out.println("Initially Permutated Blocks: " + initiallyPermutatedBlocks);

        for (String block : initiallyPermutatedBlocks) {
            encodedBlocks.add(encodeToBinary(block));
        }

        System.out.println("Encoded Blocks:" + encodedBlocks);

        for (String block : encodedBlocks) {
            shiftedBlocks.add(SimpleFiestelSystem.shiftRightRotate(block, 4));
        }
        System.out.println("Shifted Blocks:" + shiftedBlocks);


        for (String block : shiftedBlocks) {
            String[] blocksBeforeXor = SimpleFiestelSystem.createNibbles(block);

            leftNibbles.add(blocksBeforeXor[0]);
            rightNibbles.add(blocksBeforeXor[1]);
        }

        System.out.println("Splitted Blocks Before XOR");
        System.out.println("Left Nibbles: " + leftNibbles);
        System.out.println("Right Nibbles: " + rightNibbles);

        String xordLeft = "";
        String xordRight = "";

        // Step 9
        for (int i = 0, j = 0; i < leftNibbles.size() && j < rightNibbles.size(); i++, j++) {
            String left = "";
            String right = "";


            for (int indexLeft = 0; indexLeft < leftNibbles.get(i).length(); indexLeft = indexLeft + 8) {
                String leftNibble = leftNibbles.get(i).substring(indexLeft, indexLeft + 8);
                String key3 = SimpleFiestelSystem.subKeys[1];
                xordLeft = SimpleFiestelSystem.xorFunc(leftNibble, key3);
                left = left.concat(xordLeft);
            }

            for (int indexRight = 0; indexRight < rightNibbles.get(i).length(); indexRight = indexRight + 8) {
                String rightNibble = rightNibbles.get(j).substring(indexRight, indexRight + 8);
                String key2 = SimpleFiestelSystem.subKeys[0];
                xordRight = SimpleFiestelSystem.xorFunc(rightNibble, key2);
                right = right.concat(xordRight);
            }

            leftNibbles.set(i, right);
            rightNibbles.set(j, left);
        }

        System.out.println("Round 1");
        System.out.println("Left Nibbles after XOR: " + leftNibbles.toString());
        System.out.println("Right Nibbles after XOR: " + rightNibbles.toString());

//Step 10
        for (int i = 0, j = 0; i < leftNibbles.size() && j < rightNibbles.size(); i++, j++) {
            String left = "";


            for (int indexLeft = 0; indexLeft < leftNibbles.get(i).length(); indexLeft += 8) {
                String leftNibble = leftNibbles.get(i).substring(indexLeft, indexLeft + 8);
                String key5 = SimpleFiestelSystem.subKeys[3];
                xordLeft = SimpleFiestelSystem.xorFunc(leftNibble, key5);
                left = left.concat(xordLeft);
            }

            String right = "";
            for (int indexRight = 0; indexRight < rightNibbles.get(i).length(); indexRight += 8) {
                String rightNibble = rightNibbles.get(j).substring(indexRight, indexRight + 8);
                String key4 = SimpleFiestelSystem.subKeys[2];
                xordRight = SimpleFiestelSystem.xorFunc(rightNibble, key4);
                right = right.concat(xordRight);
            }

            leftNibbles.set(i, left);
            rightNibbles.set(j, right);
        }
        System.out.println("Round 2");

        System.out.println("Left Nibbles after XOR: " + leftNibbles.toString());
        System.out.println("Right Nibbles after XOR: " + rightNibbles.toString());
        // Step 11
        mergedNibbleBlocks = SimpleFiestelSystem.mergeNibbles(leftNibbles, rightNibbles);

        System.out.println("Merged Nibbles: " + mergedNibbleBlocks);


        for (String block : mergedNibbleBlocks) {
            String permutationBlocks = "";

            for (int i = 0; i < block.length(); i += 8) {
                String blockToBeReverselyPermuted = block.substring(i, i + 8);
                String permuted = SimpleFiestelSystem.permutateStringBlocks(blockToBeReverselyPermuted, SimpleFiestelSystem.reverseIP);
                permutationBlocks += permuted;
            }
            reversePermutatedBlocks.add(permutationBlocks);
        }

        System.out.println("Reverse Permutated Blocks as Ciphered Blocks: " + reversePermutatedBlocks);

        String cipheredText = SimpleFiestelSystem.mergeBlocks(reversePermutatedBlocks);
        System.out.println("Ciphered Text: " + cipheredText);

        writeCipheredTextToFile(cipheredText);

    }

}
