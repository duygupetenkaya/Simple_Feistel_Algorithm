import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class SimpleFiestelSystem {
    static final int blockSize = 8;
    //FIXED VARIABLES FOR FEISTEL CIPHER
    static final int[] IP = {6, 4, 2, 8, 7, 5, 3, 1};
    static final int[] reverseIP = {1,3,5,7,8,2,4,6};
    static HashMap<Character, Integer> encodingTable;
    static String key = "BD";
    static ArrayList<String> blocks = new ArrayList<>();
    static ArrayList<String> initiallyPermutatedBlocks = new ArrayList<>();
    static ArrayList<String> encodedBlocks = new ArrayList<>();
    static ArrayList<String> shiftedBlocks = new ArrayList<>();
    static ArrayList<String> leftNibbles = new ArrayList<>();
    static ArrayList<String> rightNibbles = new ArrayList<>();
    static ArrayList<String> mergedNibbleBlocks = new ArrayList<>();
    static ArrayList<String> reversePermutatedBlocks = new ArrayList<>();


    public static void createAlphabet() {
        encodingTable = new HashMap<>();
        encodingTable.put('A', 1);
        encodingTable.put('B', 2);
        encodingTable.put('C', 3);
        encodingTable.put('Ç', 4);
        encodingTable.put('D', 5);
        encodingTable.put('E', 6);
        encodingTable.put('F', 7);
        encodingTable.put('G', 8);
        encodingTable.put('Ğ', 9);
        encodingTable.put('H', 10);
        encodingTable.put('I', 11);
        encodingTable.put('İ', 12);
        encodingTable.put('J', 13);
        encodingTable.put('K', 14);
        encodingTable.put('L', 15);
        encodingTable.put('M', 16);
        encodingTable.put('N', 17);
        encodingTable.put('O', 18);
        encodingTable.put('Ö', 19);
        encodingTable.put('P', 20);
        encodingTable.put('R', 21);
        encodingTable.put('S', 22);
        encodingTable.put('Ş', 23);
        encodingTable.put('T', 24);
        encodingTable.put('U', 25);
        encodingTable.put('Ü', 26);
        encodingTable.put('V', 27);
        encodingTable.put('Y', 28);
        encodingTable.put('Z', 29);
        encodingTable.put('.', 30);
        encodingTable.put(',', 31);
        encodingTable.put('(', 32);
        encodingTable.put(')', 33);
        encodingTable.put('!', 34);
        encodingTable.put(';', 35);
        encodingTable.put(':', 36);
        encodingTable.put('\'', 37);
        encodingTable.put('\"', 38);
        encodingTable.put('-', 39);
        encodingTable.put('?', 40);
        encodingTable.put('$', 41);
        encodingTable.put('@', 42);
        encodingTable.put('%', 43);
        encodingTable.put('a', 44);
        encodingTable.put('b', 45);
        encodingTable.put('c', 46);
        encodingTable.put('ç', 47);
        encodingTable.put('d', 48);
        encodingTable.put('e', 49);
        encodingTable.put('f', 50);
        encodingTable.put('g', 51);
        encodingTable.put('ğ', 52);
        encodingTable.put('h', 53);
        encodingTable.put('ı', 54);
        encodingTable.put('i', 55);
        encodingTable.put('j', 56);
        encodingTable.put('k', 57);
        encodingTable.put('l', 58);
        encodingTable.put('m', 59);
        encodingTable.put('n', 60);
        encodingTable.put('o', 61);
        encodingTable.put('ö', 62);
        encodingTable.put('p', 63);
        encodingTable.put('r', 64);
        encodingTable.put('s', 65);
        encodingTable.put('ş', 66);
        encodingTable.put('t', 67);
        encodingTable.put('u', 68);
        encodingTable.put('ü', 69);
        encodingTable.put('v', 70);
        encodingTable.put('y', 71);
        encodingTable.put('z', 72);
        encodingTable.put('Q', 73);
        encodingTable.put('W', 74);
        encodingTable.put('q', 73);
        encodingTable.put('w', 76);
        encodingTable.put('_', 77);
        encodingTable.put('0', 78);
        encodingTable.put('1', 79);
        encodingTable.put('2', 80);
        encodingTable.put('3', 81);
        encodingTable.put('4', 82);
        encodingTable.put('5', 83);
        encodingTable.put('6', 84);
        encodingTable.put('7', 85);
        encodingTable.put('8', 86);
        encodingTable.put('9', 87);
        encodingTable.put('+', 88);


    }


    // Step 1- Your code will read a plaintext from a file block-by-block with block size of 8 characters
    public static void readTextFileBlockByBlock(String filename) {
        String inputFileString;
        try {
            FileInputStream file = new FileInputStream(filename);
            Scanner sc = new Scanner(file);

            // to know end of the text
            sc.useDelimiter("\\Z");

            while (sc.hasNextLine()) {
                inputFileString = sc.next();

                // to change whitespaces with underscore
                inputFileString = inputFileString.toLowerCase().replaceAll("\\s+", "_");

                //if length of the string is less than 8 bits, add zeros until the length is 8 bits.
                if (inputFileString.length() % blockSize != 0) {
                    while (inputFileString.length() % blockSize != 0) {
                        inputFileString = inputFileString + "0";
                    }
                }

                int index = 0;

                // to add each 8 characters as a block to ArrayList of blocks
                while (index < inputFileString.length()) {
                    blocks.add(inputFileString.substring(index, Math.min(index + 8, inputFileString.length())));
                    index += 8;
                }

            }
            sc.close();

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }


    }

    // Step 2- A permutation code of length 8 is chosen to be IP . The 8-character text
    //blocks will be permutated by using the initial permutation (IP) code

    public static String permutateStringBlocks(String block, int[] IP) {
        String permutatedString = "";
        for (int i = 0; i < IP.length; i++) {
            permutatedString += block.charAt(IP[i] - 1);
        }
        return permutatedString;
    }



    // Step 3- Use either BINARY values of characters from Table 1 to encode characters so that each
    //character will be 8 bits of length.
    public static String encodeToBinary(String blockToEncode) {
        StringBuilder encodingBuilder = new StringBuilder();
        char[] chars = blockToEncode.toCharArray();

        for (char aChar : chars) {
            if (encodingTable.get(aChar) != null)
                encodingBuilder.append(String.format("%8s", Integer.toBinaryString(encodingTable.get(aChar))).replaceAll(" ", "0"));
        }

        return encodingBuilder.toString();
    }

    // Step 4- Perform a Shift-right-rotate operation that shift and rotate 4 positions to right  on the encoded characters.
    public static String shiftRightRotate(String blockToBeShifted, int shiftAmount) {
        char[] charsAfterShiftRotate = new char[blockToBeShifted.length()];
        char[] chars = blockToBeShifted.toCharArray();
        StringBuilder shiftingBuilder = new StringBuilder();
        int i = 0;
        while (i < chars.length) {
            int k = (i + shiftAmount) % chars.length;
            charsAfterShiftRotate[k] = chars[i];
            i++;
        }
        for (char aChar : charsAfterShiftRotate) {
            shiftingBuilder.append(aChar);
        }
        return shiftingBuilder.toString();
    }

    // Step 5-  Get 2 characters at a time from the encoded block: Characters
    // at odd positions are placed into the left nibble and characters with even
    // positions are placed into the right nibble.
    public static String[] createNibbles(String blockToNibblized) {
        StringBuilder left = new StringBuilder();
        StringBuilder right = new StringBuilder();

        for (int i = 0, j = 8; i < blockToNibblized.length() && j < blockToNibblized.length(); i += 16, j += 16) {
            left.append(blockToNibblized, i, i + 8);
            right.append(blockToNibblized, j, j + 8);

        }

        return new String[]{String.valueOf(right), String.valueOf(left)};
    }

    // Step 6- Choose a 16-bit (2 characters) key and convert them to 16 bits,
    // and put the bits into a 4x4 matrix(table)
    public static int[][] keyToBitMatrix() {
        String keyFirst = String.format("%8s", Integer.toBinaryString(encodingTable.get(key.charAt(0)))).replaceAll(" ", "0");
        String keySecond = String.format("%8s", Integer.toBinaryString(encodingTable.get(key.charAt(1)))).replaceAll(" ", "0");

        String keyAsBits = keyFirst + keySecond;

        int[] keyAsBitsArray = new int[keyAsBits.length()];

        for (int i = 0; i < keyAsBits.length(); i++) {
            keyAsBitsArray[i] = Character.getNumericValue(keyAsBits.charAt(i));
        }
        int[][] keyMatrix = new int[4][4];
        int index = 0;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (index == keyAsBitsArray.length) break;
                keyMatrix[i][j] = keyAsBitsArray[index];
                index++;
            }
        }
        return keyMatrix;
    }

    //  Perform a Shift-left-rotate operation that shift 3 positions to left and rotates it on the encoded characters.
    public static String shiftLeftRotate(String blockToBeShifted, int shiftAmount) {
        char[] charsAfterShiftRotate = new char[blockToBeShifted.length()];
        char[] chars = blockToBeShifted.toCharArray();
        StringBuilder shiftingBuilder = new StringBuilder();
        int i = 0;
        while (i < chars.length) {
            int k = i - (shiftAmount % chars.length);
            if (k < 0) k = k + chars.length;
            charsAfterShiftRotate[k] = chars[i];
            i++;
        }
        for (char aChar : charsAfterShiftRotate) {
            shiftingBuilder.append(aChar);
        }
        return shiftingBuilder.toString();
    }

    // XOR function to operate XOR operation on two strings of equal length with 8 bits each
    public static String xorFunc(String firstString, String secondString) {
        StringBuilder xorBuilder = new StringBuilder();
        for (int i = 0; i < firstString.length(); i++) {
            if (firstString.charAt(i) == secondString.charAt(i)) {
                xorBuilder.append("0");
            } else {
                xorBuilder.append("1");
            }
        }
        return xorBuilder.toString();
    }

    // Step 7- Now use the columns 3,1,0, 2 to generate K2 and use columns 0,1,3,2 to generate K3.
    // a.Concatenate columns 3 and 1, that is x = (3||1), and concatenate columns 0 and 2 that is y= (0||2).
    // Here, x and y will now be 8 bits each. Now perform XOR on x and y to generate K2, i.e., K2= (x XOR y)
    // b.Concatenate columns 0 and 1, that is w= (0||1), and concatenate columns 2 and 3, that is z= (2||3), and.
    // Now perform XOR on x and y to generate, i.e., K3= (w XOR z).
    // c.Use K2 in function SLR(3, K2) to generate K4.
    // Likewise, Use K3 in function SRR(3, K3) to generate K5.
    public static String[] generateSubKeys(int[][] keyMatrix) {
        String[] subKeys;

        String k2;
        String k3;
        String k4;
        String k5;


        StringBuilder x = new StringBuilder();
        StringBuilder y = new StringBuilder();
        StringBuilder w = new StringBuilder();
        StringBuilder z = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            x.append(keyMatrix[i][3]);
            x.append(keyMatrix[i][1]);

            y.append(keyMatrix[i][0]);
            y.append(keyMatrix[i][2]);

            w.append(keyMatrix[i][0]);
            w.append(keyMatrix[i][1]);

            z.append(keyMatrix[i][2]);
            z.append(keyMatrix[i][3]);
        }

        k2 = xorFunc(String.valueOf(x), String.valueOf(y));
        k3 = xorFunc(String.valueOf(w), String.valueOf(z));
        k4 = shiftLeftRotate(k2, 3);
        k5 = shiftRightRotate(k3, 3);

        subKeys = new String[]{k2, k3, k4, k5};

        return subKeys;
    }

    // Step 11-Helper: Merge xor'd left and right nibbles of the input string to generate the output string.
    public static ArrayList<String> mergeNibbles(ArrayList<String> left, ArrayList<String> right) {

        for (int i = 0; i < left.size(); i++) {
            String mergedNibbles = "";
            mergedNibbles = left.get(i) + right.get(i);
            mergedNibbleBlocks.add(mergedNibbles);
        }
        return mergedNibbleBlocks;
    }

    public static String mergeBlocks(ArrayList<String> blocks) {
        StringBuilder mergedBlocksBuilder = new StringBuilder();
        for (String block : blocks) {
            mergedBlocksBuilder.append(block);
        }
        return mergedBlocksBuilder.toString();
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

    public static void encryptionFunction() {
        System.out.println("Original Blocks:");
        System.out.println(blocks);

        for (String block : blocks) {
            initiallyPermutatedBlocks.add(permutateStringBlocks(block, IP));
        }
        System.out.println("Initially Permutated Blocks: ");
        System.out.println(initiallyPermutatedBlocks);

        for (String block : initiallyPermutatedBlocks) {
            encodedBlocks.add(encodeToBinary(block));
        }

        System.out.println("Encoded Blocks:");
        System.out.println(encodedBlocks.toString());

        for (String block : encodedBlocks) {
            shiftedBlocks.add(shiftRightRotate(block, 4));
        }
        System.out.println("Shifted Blocks:");
        System.out.println(shiftedBlocks);

        int[][] keyMatrix = keyToBitMatrix();
        System.out.println("Key Matrix: " + Arrays.deepToString(keyMatrix));

        String[] subKeys = generateSubKeys(keyMatrix);
        // k2 = subkeys[0] , k3 = subkeys[1] , k4 = subkeys[2] , k5 = subkeys[3]
        System.out.println("Subkeys: " + Arrays.deepToString(subKeys));


        for (String block : shiftedBlocks) {
            String[] blocks = createNibbles(block);

            leftNibbles.add(blocks[1]);
            rightNibbles.add(blocks[0]);
        }


        System.out.println("Left Nibbles: " + "size: " + leftNibbles.size());
        System.out.println(leftNibbles);
        System.out.println("Right Nibbles: " + "size: " + rightNibbles.size());
        System.out.println(rightNibbles);


        String xordLeft = "";
        String xordRight = "";

        // Step 9
        for (int i = 0, j = 0; i < leftNibbles.size() && j < rightNibbles.size(); i++, j++) {
            String left = "";
            for (int indexLeft = 0; indexLeft < leftNibbles.get(i).length(); indexLeft += 8) {
                String leftNibble = leftNibbles.get(i).substring(indexLeft, indexLeft + 8);
                String key3 = subKeys[1];
                xordLeft = xorFunc(leftNibble, key3);
                left += xordLeft;
            }
            String right = "";
            for (int indexRight = 0; indexRight < rightNibbles.get(i).length(); indexRight += 8) {
                String rightNibble = rightNibbles.get(j).substring(indexRight, indexRight + 8);
                String key2 = subKeys[0];
                xordRight = xorFunc(rightNibble, key2);
                right += xordRight;
            }

            String temp = left;
            left = right;
            right = temp;

            leftNibbles.set(i, left);
            rightNibbles.set(j, right);
        }
        System.out.println("Round 1");
        System.out.println("Left Nibbles after XOR: " + leftNibbles);
        System.out.println("Right Nibbles after XOR: " + rightNibbles);

//Step 10
        for (int i = 0, j = 0; i < leftNibbles.size() && j < rightNibbles.size(); i++, j++) {
            String left = "";
            for (int indexLeft = 0; indexLeft < leftNibbles.get(i).length(); indexLeft += 8) {
                String leftNibble = leftNibbles.get(i).substring(indexLeft, indexLeft + 8);
                String key3 = subKeys[1];
                xordLeft = xorFunc(leftNibble, key3);
                left += xordLeft;
            }

            String right = "";
            for (int indexRight = 0; indexRight < rightNibbles.get(i).length(); indexRight += 8) {
                String rightNibble = rightNibbles.get(j).substring(indexRight, indexRight + 8);
                String key2 = subKeys[0];
                xordRight = xorFunc(rightNibble, key2);
                right += xordRight;
            }
            String temp = left;
            left = right;
            right = temp;

            leftNibbles.set(i, left);
            rightNibbles.set(j, right);
        }
        System.out.println("Round 2");

        System.out.println("Left Nibbles after XOR: " + leftNibbles);
        System.out.println("Right Nibbles after XOR: " + rightNibbles);
        // Step 11
        mergedNibbleBlocks = mergeNibbles(leftNibbles, rightNibbles);

        System.out.println("Merged Nibbles: ");
        System.out.println(mergedNibbleBlocks);


        for (String block : mergedNibbleBlocks) {
            String permutationBlocks = "";

            for (int i = 0; i < block.length(); i += 8) {
                String blockToBeReverselyPermuted = block.substring(i, i + 8);
                String permuted = permutateStringBlocks(blockToBeReverselyPermuted,reverseIP);
                permutationBlocks += permuted;
            }
            reversePermutatedBlocks.add(permutationBlocks);
        }

        System.out.println("Reverse Permutated Blocks as Ciphered Blocks: ");
        System.out.println(reversePermutatedBlocks);

        String cipheredText = mergeBlocks(reversePermutatedBlocks);
        System.out.println("Ciphered Text: " + cipheredText);

        writeCipheredTextToFile(cipheredText);

    }


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

    public static ArrayList<String> splitBlocks(String text) {
        ArrayList<String> blocks = new ArrayList<>();
        for (int i = 0; i < text.length(); i += 64) {
            String block = text.substring(i, i + 64);
            blocks.add(block);
        }
        return blocks;
    }


    public static void main(String[] args) {
        createAlphabet();
        readTextFileBlockByBlock("src/plainText.txt");

        encryptionFunction();

        String cipheredText=readCipheredTextFromFile("src/cipheredText.txt");
        System.out.println("\n");

        ArrayList<String> cipheredBlocks=splitBlocks(cipheredText);
        System.out.println("Ciphered Blocks (should be equal to reverse permutated blocks): " + cipheredBlocks);

        ArrayList<String> mergedNibblesDecipher= new ArrayList<String>();

        reversePermutatedBlocks.clear();
        for (String block : cipheredBlocks) {
            String permutationBlocks = "";

            for (int i = 0; i < block.length(); i += 8) {
                String blockToBeReverselyPermuted = block.substring(i, i + 8);
                String permuted = permutateStringBlocks(blockToBeReverselyPermuted,reverseIP );
                permutationBlocks += permuted;
            }
            reversePermutatedBlocks.add(permutationBlocks);
            mergedNibblesDecipher.add(permutationBlocks);
        }

        System.out.println("Reverse Permutated Blocks(should be equal to merged blocks): ");
        System.out.println(mergedNibblesDecipher);

    }

}

