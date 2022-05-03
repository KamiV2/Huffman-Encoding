import java.nio.file.Files;
import java.util.*;
import java.io.*;

public class HuffmanEncoder {
    private PriorityQueue<HTree> queue;
    private HTree root;

    public HuffmanEncoder() {
        queue = new PriorityQueue<HTree>(new HuffmanComparator());
        root = null;
    }

    public void add(HTree tree) {
        queue.add(tree);
    }

    public void make_tree() {
        if (queue.size() == 1) {
            HTree a = queue.poll();
            root = new HTree(a.getFrequency(), '*');
            root.setLeft(a);
        } else {
            while (queue.size() > 1) {
                HTree left = queue.poll();
                HTree right = queue.poll();
                int sumfreq = left.getFrequency() + right.getFrequency();
                HTree ntree = new HTree(sumfreq, '*');
                ntree.setLeft(left);
                ntree.setRight(right);
                queue.add(ntree);
            }
            root = queue.poll();
        }
    }

    public void build_tree(HashMap<Character, Integer> char_freq_map) {
        for (Character c : char_freq_map.keySet()) {
            HTree tree = new HTree(char_freq_map.get(c), c);
            add(tree);
        }
        make_tree();
    }

    public ArrayList<Character> getCharList(String pathName) throws Exception {
        ArrayList<Character> chars = new ArrayList<Character>();
        BufferedReader input = new BufferedReader(new FileReader(pathName));
        try {
            int cInt = input.read();
            while (cInt != -1) {
                char c = (char) cInt;
                chars.add(c);
                cInt = input.read();
            }
        } catch (Exception e) {
            System.err.println("ReadFileError: " + e.getMessage());
        } finally {
            input.close();
        }
        return chars;
    }

    public HashMap<Character, Integer> getCharMap(ArrayList<Character> chars) throws Exception {
        HashMap<Character, Integer> char_freq_map = new HashMap<>();
        for (Character c : chars) {
            if (char_freq_map.containsKey(c)) {
                char_freq_map.put(c, char_freq_map.get(c) + 1);
            } else {
                char_freq_map.put(c, 1);
            }
        }
        return char_freq_map;
    }
    
    public HashMap<Character, String> getCodes(HTree root) throws Exception {
        HashMap<Character, String> codes = new HashMap<>();
        if (root != null) {
            if (root.getLeft() == null && root.getRight() == null) {
                codes.put(root.getCharacter(), "");
            } else {
                getCodeHelper(codes, root, "");
            }
        }
        return codes;
    }
    
    private String getCodeHelper(HashMap<Character, String> codes, HTree root, String existingcode) throws Exception {
        try {
            if (root.getLeft() == null && root.getRight() == null) {
                codes.put(root.getCharacter(), existingcode);
            } else {
                if (root.getLeft() != null) {
                    getCodeHelper(codes, root.getLeft(), existingcode + "0");
                }
                if (root.getRight() != null) {
                    getCodeHelper(codes, root.getRight(), existingcode + "1");
                }
            }
        } catch (Exception e) {
            System.err.println("EmptyTree: " + e.getMessage());
        }
        return existingcode;
    }

    public void encode(String infn, boolean debug) throws Exception {
        ArrayList<Character> chars = getCharList(infn);
        HashMap<Character, Integer> char_freq_map = getCharMap(chars);
        build_tree(char_freq_map);
        HashMap<Character, String> codes = getCodes(root);
        if (debug) { // print the frequency map, the tree, and the codes if debug is true
            System.out.println("Char Frequency Map: " + char_freq_map);
            System.out.println("Huffman Tree: \n" + root);
            System.out.println("Codes: " + codes);
        }
        String outfn = infn.substring(0, infn.length() - 4) + "_encoded.txt";
        BufferedBitWriter writer = new BufferedBitWriter(outfn);
        try {
            for (Character c : chars) {
                String code = codes.get(c);
                for (int i = 0; i < code.length(); i++) {
                    writer.writeBit(code.charAt(i) == '1');
                }
            }
        } catch (Exception e) {
            System.err.println("WriteFileError: " + e.getMessage());
        } finally {
            writer.close();
        }
    }
    
    public void decode(String infn) throws Exception {
        BufferedBitReader reader = new BufferedBitReader(infn);
        String outfn = infn.substring(0, infn.length() - 12) + "_decoded.txt";
        BufferedWriter writer = new BufferedWriter(new FileWriter(outfn));
        try {
            HTree current = root;
            while (reader.hasNext()) {
                boolean bit = reader.readBit();
                if (bit) {
                    current = current.getRight();
                } else {
                    current = current.getLeft();
                }
                if (current.getLeft() == null && current.getRight() == null) {
                    writer.write(current.getCharacter());
                    current = root;
                }
            }
        } catch (Exception e) {
            System.err.println("WriteFileError: " + e.getMessage());
        } finally {
            writer.close();
        }
    }
    
    public static void main(String[] args) throws Exception {
        boolean test = false; //runs test cases
        boolean debug = false; //prints some intermediate stage outputs and tests final decode for accuracy
        String fn = "Inputs/WarAndPeace.txt"; // input file here
        HuffmanEncoder huffman = new HuffmanEncoder();
        if (test) {
            String[] test_files = {"Inputs/test0.txt", "Inputs/test1.txt", "Inputs/test2.txt", "Inputs/test3.txt"}; //
            for (int i = 0; i < test_files.length; i++) {
                String test_file = test_files[i];
                System.out.println("Test " + i + " started");
                huffman.encode(test_file, debug);
                huffman.decode(test_file.substring(0, test_file.length() - 4) + "_encoded.txt");
                File file_original = new File(test_file);
                File file_decoded = new File(test_file.substring(0, test_file.length() - 4) + "_decoded.txt");
                byte[] test_bytes = Files.readAllBytes(file_original.toPath());
                byte[] decoded_bytes = Files.readAllBytes(file_decoded.toPath());
                if (Arrays.equals(test_bytes, decoded_bytes)) {
                    System.out.println("Test " + i + " passed" + "\n" + "----------------------------------");
                } else {
                    System.err.println("Test " + i + " failed");
                }
            }
        }
        huffman.encode(fn, debug); //encode
        huffman.decode(fn.substring(0, fn.length() - 4) + "_encoded.txt"); //decode

        if (debug) {
            File file_original = new File(fn);
            File file_decoded = new File(fn.substring(0, fn.length() - 4) + "_decoded.txt");
            byte[] test_bytes = Files.readAllBytes(file_original.toPath());
            byte[] decoded_bytes = Files.readAllBytes(file_decoded.toPath());
            if (Arrays.equals(test_bytes, decoded_bytes)) {
                System.out.println("Encoded " + fn + " successfully" + "\n" + "----------------------------------");
            }
        }
    }
}
