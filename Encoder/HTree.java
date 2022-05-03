public class HTree {
    private final int frequency;
    private final char character;
    private HTree left;
    private HTree right;

    public HTree(int frequency, char character) {
        this.frequency = frequency;
        this.character = character;
    }

    public int getFrequency() {
        return frequency;
    }
    
    public char getCharacter() {
        return character;
    }
    
    public HTree getLeft() {
        return left;
    }

    public HTree getRight() {
        return right;
    }
    
    public void setLeft(HTree left) {
        this.left = left;
    }
    
    public void setRight(HTree right) {
        this.right = right;
    }
    
    public String toString() {
        return toStringHelper("");
    }
    
    public String toStringHelper(String indent) {
        String res = indent + "(" + character + "," + frequency + ")" + "\n";
        if (left != null) res += left.toStringHelper(indent + "  ");
        if (right != null) res += right.toStringHelper(indent + "  ");
        return res;
    }
}