import java.util.*;

public class HuffmanComparator implements Comparator<HTree> {
    @Override
    public int compare(HTree t1, HTree t2) {
        return t1.getFrequency() - t2.getFrequency() > 0 ? 1 : t1.getFrequency() == t2.getFrequency() ? 0 : -1;
    }
}