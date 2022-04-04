import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class feistel {


    public static String readFile(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();

        while (line != null) {
            sb.append(line);
            sb.append("\n");
            line = br.readLine();
        }
        return sb.toString();
    }

    static final int totalRounds = 1;
    static final int blockSize = 8;
    static final int keySize = 16;
    static String initializationVector = "";
    static ArrayList<String> blocks = new ArrayList<>();


    public static void readTextFileBlockByBlock(String filename) {
        String inputFileString = "";
        try {
            FileInputStream file = new FileInputStream(filename);
            Scanner sc = new Scanner(file);

            sc.useDelimiter("\\Z");

            while (sc.hasNextLine()) {
                inputFileString = sc.next();

                inputFileString = inputFileString.toLowerCase().replaceAll("\\s+", "_");

                if (inputFileString.length() % blockSize != 0) {
                    while (inputFileString.length() % blockSize != 0) {
                        inputFileString = inputFileString + "0";
                    }
                }

                int index = 0;
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


    public static ArrayList<String> perm(ArrayList<String> blocks) {
        ArrayList<String> permutedBlocks = new ArrayList<>();

        int permutation[] = {6, 4, 2, 8, 7, 5, 3, 1};

        for (int i = 0; i < blocks.size(); i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < permutation.length; j++) {
                sb.append(blocks.get(i).charAt(permutation[j] - 1));

            }
            permutedBlocks.add(sb.toString());

        }
        return permutedBlocks;

    }
    public static ArrayList<String> encoder(ArrayList<String> blocks) {
        ArrayList<String> encodedBlocks = new ArrayList<>();
        for (int i = 0; i < blocks.size(); i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < blocks.get(i).length(); j++) {
                sb.append(Integer.toBinaryString(blocks.get(i).charAt(j)));
                while (sb.length() < 8) {
                    sb.append("0");
                }
            }
            encodedBlocks.add(sb.toString());
        }
        return encodedBlocks;
    }


    public static ArrayList<String> shifter(ArrayList<String> blocks) {
        ArrayList<String> shiftedBlocks = new ArrayList<>();
        for (int i = 0; i < blocks.size(); i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < blocks.get(i).length(); j++) {
                sb.append(blocks.get(i).charAt((j + 4) % blocks.get(i).length()));
            }
            shiftedBlocks.add(sb.toString());
        }
        return shiftedBlocks;
    }



    public static void main(String[] args) throws IOException {

        // sanity check
        // System.out.println("test");

        // read from txt file
        // String text = readFile("src/plaintext.txt");
        readTextFileBlockByBlock("src/plaintext.txt");

        System.out.println("plaintext: " + blocks);

        // permute blocks

        String permutedBlocks = String.valueOf(perm(blocks));
        System.out.println("permutedBlocks: " + permutedBlocks);

        // encode blocks

        String encodedBlocks = String.valueOf(encoder(blocks));
        System.out.println("encodedBlocks: " + encodedBlocks);

        // shift blocks



    }
}


