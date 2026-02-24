import java.util.HashMap;
import java.util.Map;

public class PayoutTable {
    private final Map<Integer, Map<Integer, Integer>> table = new HashMap<>();

    public PayoutTable() {
        addRow(1, new int[][] {{1, 3}});
        addRow(4, new int[][] {{2, 1}, {3, 5}, {4, 75}});
        addRow(8, new int[][] {{4, 2}, {5, 12}, {6, 50}, {7, 750}, {8, 10000}});
        addRow(10, new int[][] {{5, 5}, {6, 15}, {7, 40}, {8, 450}, {9, 4250}, {10, 100000}});
    }

    private void addRow(int spot, int[][] pairs) {
        Map<Integer, Integer> m = new HashMap<>();
        for (int[] p : pairs) {
            m.put(p[0], p[1]);
        }
        table.put(spot, m);
    }

    public int getPayout(int spot, int match) {
        if (table.containsKey(spot) && table.get(spot).containsKey(match)) {
            return table.get(spot).get(match);
        }
        return 0;
    }
}
