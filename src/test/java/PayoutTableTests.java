import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PayoutTableTests {

    @Test
    public void testKnownPayouts() {
        PayoutTable p = new PayoutTable();
        assertEquals(3, p.getPayout(1, 1));
        assertEquals(75, p.getPayout(4, 4));
        assertEquals(100000, p.getPayout(10, 10));
    }

    @Test
    public void testZeroWhenNoMatch() {
        PayoutTable p = new PayoutTable();
        assertEquals(0, p.getPayout(4, 0));
        assertEquals(0, p.getPayout(8, 1));
    }

    @Test
    public void testTableContainsSpotKeys() {
        PayoutTable p = new PayoutTable();
        for (int s : new int[]{1, 4, 8, 10}) {
            for (int m = 0; m <= 10; m++) {
                assertTrue(p.getPayout(s, m) >= 0);
            }
        }
    }

    @Test
    public void testHigherMatchGivesMoreReward() {
        PayoutTable p = new PayoutTable();
        assertTrue(p.getPayout(8, 8) > p.getPayout(8, 4));
    }

    @Test
    public void testUnknownSpotReturnsZero() {
        PayoutTable p = new PayoutTable();
        assertEquals(0, p.getPayout(5, 3));
    }

    @Test
    public void testZeroMatchReturnsZero() {
        PayoutTable table = new PayoutTable();
        assertEquals(0, table.getPayout(10, 0));
    }

    @Test
    public void testInvalidSpotReturnsZero() {
        PayoutTable table = new PayoutTable();
        assertEquals(0, table.getPayout(2, 2));
    }

    @Test
    public void testMaxRewardFor10Spot() {
        PayoutTable table = new PayoutTable();
        int payout = table.getPayout(10, 10);
        assertEquals(100000, payout);
    }

}
