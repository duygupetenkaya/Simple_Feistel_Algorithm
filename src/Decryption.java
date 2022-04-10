import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class Decryption {

    public static String readCipheredTextFromFile(String cipheredText) {
        String cipheredTextString = "";
        try {
            FileInputStream file = new FileInputStream(cipheredText);
            Scanner sc = new Scanner(file);
            sc.useDelimiter("\\Z");
            cipheredTextString = sc.next();
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cipheredTextString;

    }

    // Step 11-Helper:
    public static ArrayList<String> mergeNibblesAsReverseStep5(ArrayList<String> left, ArrayList<String> right) {
        ArrayList<String> mergedNibblesDecipher = new ArrayList<String>();
        for (int i = 0, j = 0; i < left.size() && j < right.size(); i++, j++) {
            String merged = "";

            for (int k = 0, l = 0; k < left.get(i).length() && l < right.get(j).length(); k = k + 8, l = l + 8) {
                merged = merged.concat(left.get(i).substring(k, k + 8));
                merged = merged.concat(right.get(j).substring(l, l + 8));
            }

            mergedNibblesDecipher.add(merged);
        }
        return mergedNibblesDecipher;
    }

    public static String decodeBlocks(String block) {
        StringBuilder decodedBlock = new StringBuilder();
        for (int i = 0; i < block.length(); i += 8) {
            String decoded = block.substring(i, i + 8);
            int decimalToCharValue = Integer.parseInt(decoded, 2);
            for (Map.Entry<Character, Integer> entry : SimpleFiestelSystem.encodingTable.entrySet()) {
                if (entry.getValue().equals(decimalToCharValue)) {
                    decodedBlock.append(entry.getKey());
                }
            }
        }
        return decodedBlock.toString();
    }

    public static ArrayList<String> readBlocksToWriteText(ArrayList<String> textBlocks) {
        ArrayList<String> textBlocksToWrite = new ArrayList<>();
        String removePadding = textBlocks.get(textBlocks.size() - 1).replaceAll("0", "");
        textBlocks.set(textBlocks.size() - 1, removePadding);
        for (String block : textBlocks) {
            block = block.toLowerCase().replaceAll("_", " ");
            textBlocksToWrite.add(block);
        }
        return textBlocksToWrite;
    }

    public static void writeproducedPlainTextToFile(String producedPlainText) {
        try {
            FileWriter fw = new FileWriter("src//producedPlainText.txt");
            fw.write(producedPlainText);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void decryptionFunction(String filePathOfCipheredText) {

        String cipheredText = readCipheredTextFromFile(filePathOfCipheredText);


        ArrayList<String> cipheredBlocks = SimpleFiestelSystem.splitBlocks(cipheredText);
        System.out.println("Reverse permutated blocks: " + cipheredBlocks);

        ArrayList<String> mergedBlocksDecipher = new ArrayList<String>();


        int[] backReverseIP = new int[]{1, 6, 2, 7, 3, 8, 4, 5};
        for (String block : cipheredBlocks) {
            String permutationBlocks = "";

            for (int i = 0; i < block.length(); i += 8) {

                String blockToBeReverselyPermuted = block.substring(i, i + 8);
                String permuted = SimpleFiestelSystem.permutateStringBlocks(blockToBeReverselyPermuted, backReverseIP);
                permutationBlocks += permuted;
            }
            mergedBlocksDecipher.add(permutationBlocks);
        }

        System.out.println("Merged Blocks(Back permutate): " + mergedBlocksDecipher);

        ArrayList<String> leftNibblesDecipher = new ArrayList<String>();
        ArrayList<String> rightNibblesDecipher = new ArrayList<String>();

        for (String blockToNibble : mergedBlocksDecipher) {
            leftNibblesDecipher.add(blockToNibble.substring(0, 32));
            rightNibblesDecipher.add(blockToNibble.substring(32, 64));
        }
        System.out.println("Round 2 ");
        System.out.println("Left Nibbles: " + leftNibblesDecipher);
        System.out.println("Right Nibbles: " + rightNibblesDecipher);

        String xordLeft = "";
        String xordRight = "";


        for (int i = 0, j = 0; i < leftNibblesDecipher.size() && j < rightNibblesDecipher.size(); i++, j++) {

            String left = "";
            for (int indexLeft = 0; indexLeft < leftNibblesDecipher.get(i).length(); indexLeft += 8) {
                String leftNibble = leftNibblesDecipher.get(i).substring(indexLeft, indexLeft + 8);
                String key5 = SimpleFiestelSystem.subKeys[3];
                xordLeft = SimpleFiestelSystem.xorFunc(leftNibble, key5);
                left = left.concat(xordLeft);
            }

            String right = "";
            for (int indexRight = 0; indexRight < rightNibblesDecipher.get(i).length(); indexRight += 8) {
                String rightNibble = rightNibblesDecipher.get(j).substring(indexRight, indexRight + 8);
                String key4 = SimpleFiestelSystem.subKeys[2];
                xordRight = SimpleFiestelSystem.xorFunc(rightNibble, key4);
                right = right.concat(xordRight);
            }


            leftNibblesDecipher.set(i, left);
            rightNibblesDecipher.set(j, right);


        }
        System.out.println("Round 1 ");
        System.out.println("Left Nibbles after XOR: " + leftNibblesDecipher);
        System.out.println("Right Nibbles after XOR: " + rightNibblesDecipher);

        for (int i = 0, j = 0; i < leftNibblesDecipher.size() && j < rightNibblesDecipher.size(); i++, j++) {


            String left = "";
            for (int indexLeft = 0; indexLeft < leftNibblesDecipher.get(i).length(); indexLeft += 8) {
                String leftNibble = leftNibblesDecipher.get(i).substring(indexLeft, indexLeft + 8);
                String key4 = SimpleFiestelSystem.subKeys[0];
                xordLeft = SimpleFiestelSystem.xorFunc(leftNibble, key4);
                left = left.concat(xordLeft);
            }

            String right = "";
            for (int indexRight = 0; indexRight < rightNibblesDecipher.get(i).length(); indexRight += 8) {
                String rightNibble = rightNibblesDecipher.get(j).substring(indexRight, indexRight + 8);
                String key5 = SimpleFiestelSystem.subKeys[1];
                xordRight = SimpleFiestelSystem.xorFunc(rightNibble, key5);
                right = right.concat(xordRight);
            }


            leftNibblesDecipher.set(i, right);
            rightNibblesDecipher.set(j, left);
        }

        System.out.println("Splitted Blocks Before XOR");
        System.out.println("Left Nibbles : " + leftNibblesDecipher);
        System.out.println("Right Nibbles : " + rightNibblesDecipher);

        ArrayList<String> mergedNibblesDecipher = mergeNibblesAsReverseStep5(leftNibblesDecipher, rightNibblesDecipher);

        System.out.println("(Reverse) Shifted :" + mergedNibblesDecipher);

        ArrayList<String> reversePermutatedBlocksDecipher = new ArrayList<String>();
        for (String blockToBeReverselyPermuted : mergedNibblesDecipher) {
            reversePermutatedBlocksDecipher.add(SimpleFiestelSystem.shiftLeftRotate(blockToBeReverselyPermuted, 4));

        }

        System.out.println("(Reverse Permutated)Encoded Blocks: " + reversePermutatedBlocksDecipher);

        ArrayList<String> finalDecodedBlocks = new ArrayList<String>();
        for (String blockToDecode : reversePermutatedBlocksDecipher) {
            finalDecodedBlocks.add(decodeBlocks(blockToDecode));
        }

        System.out.println("Decoded Blocks (Initially Permutated):" + finalDecodedBlocks);

        ArrayList<String> decodedToOriginalBlocks = new ArrayList<String>();
        int[] finalPermutation = new int[]{8, 3, 7, 2, 6, 1, 5, 4};
        for (String blockToOriginal : finalDecodedBlocks) {
            decodedToOriginalBlocks.add(SimpleFiestelSystem.permutateStringBlocks(blockToOriginal, finalPermutation));
        }

        System.out.println("Decoded Blocks (Permutated to Original):" + decodedToOriginalBlocks);

        ArrayList<String> finalOutputBlocks = readBlocksToWriteText(decodedToOriginalBlocks);
        System.out.println("Decoded Text:" + finalOutputBlocks);


        String finalOutput = SimpleFiestelSystem.mergeBlocks(finalOutputBlocks);
        System.out.println("\nText that will be written to file: ");
        System.out.println(finalOutput);

        writeproducedPlainTextToFile(finalOutput);
    }

}