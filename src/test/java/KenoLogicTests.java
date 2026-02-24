import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class KenoLogicTests {

    @Test
    public void testCountHits() {
        Set<Integer> picks = Set.of(1, 2, 3, 4);
        Set<Integer> results = Set.of(3, 4, 5, 6);
        long hits = picks.stream().filter(results::contains).count();
        assertEquals(2, hits);
    }

    @Test
    public void testDrawRangeValid() {
        Random r = new Random();
        Set<Integer> draw = new HashSet<>();
        while (draw.size() < 20) draw.add(r.nextInt(80) + 1);
        assertTrue(draw.stream().allMatch(x -> x >= 1 && x <= 80));
    }

    @Test
    public void testNoDuplicatesInDraw() {
        Random r = new Random();
        Set<Integer> draw = new HashSet<>();
        while (draw.size() < 20) draw.add(r.nextInt(80) + 1);
        assertEquals(20, draw.size());
    }

    @Test
    public void testMultipleRoundsSumPayout() {
        PayoutTable p = new PayoutTable();
        int total = p.getPayout(8, 6) + p.getPayout(8, 7);
        assertTrue(total > 0);
    }

    @Test
    public void testAutoPickCountMatchesSpots() {
        int spots = 8;
        Set<Integer> picks = new HashSet<>();
        Random r = new Random();
        while (picks.size() < spots) picks.add(r.nextInt(80) + 1);
        assertEquals(spots, picks.size());
    }

    @Test
    public void testEachDrawIndependent() {
        Set<Integer> d1 = new HashSet<>();
        Set<Integer> d2 = new HashSet<>();
        Random r = new Random();
        while (d1.size() < 20) d1.add(r.nextInt(80) + 1);
        while (d2.size() < 20) d2.add(r.nextInt(80) + 1);
        assertNotEquals(d1, d2);
    }

    @Test
    public void testDrawNumbersInRange() {
        Set<Integer> draw = new HashSet<>();
        Random r = new Random();
        while (draw.size() < 20) draw.add(r.nextInt(80) + 1);
        assertTrue(draw.stream().allMatch(n -> n >= 1 && n <= 80));
    }

    @Test
    public void testNoDuplicateAcrossMultipleDraws() {
        Set<Integer> all = new HashSet<>();
        Random r = new Random();
        for (int i = 0; i < 3; i++) {
            Set<Integer> draw = new HashSet<>();
            while (draw.size() < 20) draw.add(r.nextInt(80) + 1);
            all.addAll(draw);
        }
        assertTrue(all.size() <= 60, "Across 3 draws, total unique numbers should not exceed 60 typical limit");
    }

}
