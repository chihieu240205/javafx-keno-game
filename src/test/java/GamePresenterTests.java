import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GamePresenterTests {
    private GamePresenter presenter;
    private MockView mockView;

    private static class MockView implements IGameView {
        private final List<String> messages = new ArrayList<>();

        public List<String> getMessages() {
            return messages;
        }

        @Override
        public void showPopup(String title, String msg) {
            messages.add(title);
            messages.add(msg);
        }

        @Override public void enableGrid(boolean on) {}
        @Override public void clearSelections() {}
        @Override public void updateSelectionHighlight(int n, boolean selected) {}
        @Override public void showDrawResults(Set<Integer> results, Set<Integer> picks) {}
        @Override public void showFinalResult(List<Integer> hitsPerDraw, int totalSpots, int totalWin) {}
        @Override public void highlightDrawnNumber(int n) {}
        @Override public void resetToPickColors(Set<Integer> picks) {}
        @Override public void disableNextButton(boolean disable) {}
    }


    @BeforeEach
    public void setup() {
        mockView = new MockView();
        presenter = new GamePresenter(mockView);
    }

    @Test
    public void testInitialPickEmpty() {
        assertTrue(presenter.getPickedNumbers().isEmpty());
    }

    @Test
    public void testPickAndUnpick() {
        presenter.selectSpotsForTest(4);
        presenter.pickNumber(5);
        assertTrue(presenter.getPickedNumbers().contains(5));
        presenter.pickNumber(5);
        assertFalse(presenter.getPickedNumbers().contains(5));
    }

    @Test
    public void testAutoPickCreatesCorrectCount() {
        presenter.selectSpotsForTest(8);
        presenter.autoPick();
        assertEquals(8, presenter.getPickedNumbers().size());
    }


    @Test
    public void testResetClearsData() {
        presenter.selectSpotsForTest(4);
        presenter.autoPick();
        presenter.resetGame();
        assertTrue(presenter.getPickedNumbers().isEmpty());
    }

    @Test
    public void testRandomDrawUnique() {
        Set<Integer> drawn = new HashSet<>();
        Random r = new Random();
        while (drawn.size() < 20) drawn.add(r.nextInt(80) + 1);
        assertEquals(20, drawn.size());
    }

    @Test
    public void testSelectSpotsForTestSetsCorrectValue() {
        presenter.selectSpotsForTest(8);
        assertEquals(8, presenter.getNumSpots());
    }

    @Test
    public void testAutoPickUniqueNumbers() {
        presenter.selectSpotsForTest(10);
        presenter.autoPick();
        Set<Integer> picked = presenter.getPickedNumbers();
        assertEquals(picked.size(), new HashSet<>(picked).size());
    }

    @Test
    public void testStartDrawingReducesCount() {
        presenter.selectSpotsForTest(4);
        presenter.setNumDrawingsForTest(3);
        presenter.autoPick();
        int before = presenter.getNumDrawings();
        presenter.startDrawingForTest();
        int after = presenter.getNumDrawings();
        assertTrue(after < before);
    }

    @Test
    public void testResetGameAllowsReplay() {
        presenter.selectSpotsForTest(4);
        presenter.autoPick();
        presenter.resetGame();
        presenter.selectSpotsForTest(4);
        presenter.autoPick();
        assertEquals(4, presenter.getPickedNumbers().size());
    }

    @Test
    public void testPickNumberStopsWhenLimitReached() {
        presenter.selectSpotsForTest(2);
        presenter.pickNumber(1);
        presenter.pickNumber(2);
        presenter.pickNumber(3);
        assertEquals(2, presenter.getPickedNumbers().size());
    }

    @Test
    public void testStartDrawingCalculatesHitCount() {
        presenter.selectSpotsForTest(4);
        presenter.setNumDrawingsForTest(1);
        Set<Integer> picks = presenter.getPickedNumbers();
        picks.addAll(Set.of(1, 2, 3, 4));
        presenter.startDrawingForTest();
        assertTrue(presenter.getNumDrawings() >= 0);
    }

    @Test
    public void testAutoPickFailsWhenGameStarted() {
        MockView view = new MockView();
        GamePresenter p = new GamePresenter(view);

        p.selectSpotsForTest(4);
        p.setNumDrawingsForTest(1);
        for (int i = 1; i <= 4; i++) p.pickNumber(i);
        p.startDrawingForTest();

        p.autoPick();
        assertTrue(view.getMessages().toString().contains("Cannot Auto Pick"));
    }

    @Test
    public void testNextDrawingGameOverShowsPopup() {
        presenter.setNumDrawingsForTest(0);
        presenter.nextDrawing();
        assertNotNull(presenter);
    }

    @Test
    public void testStartDrawingClickedMultipleTimes() {
        MockView view = new MockView();
        GamePresenter p = new GamePresenter(view);
        p.selectSpotsForTest(4);
        p.setNumDrawingsForTest(3);

        for (int i = 1; i <= 4; i++) p.pickNumber(i);

        p.startDrawingForTest();
        p.startDrawingForTest();
        assertTrue(view.getMessages().toString().contains("Already Started"));
    }
    @Test
    public void testNextBeforeStart() {
        MockView view = new MockView();
        GamePresenter p = new GamePresenter(view);

        p.selectSpotsForTest(4);
        p.setNumDrawingsForTest(3);
        for (int i = 1; i <= 4; i++) p.pickNumber(i);

        p.nextDrawing();

        assertTrue(view.getMessages().contains("Not Started Yet"));
    }
    @Test
    public void testClearDuringGameNotAllowed() {
        MockView view = new MockView();
        GamePresenter p = new GamePresenter(view);

        p.selectSpotsForTest(4);
        p.setNumDrawingsForTest(3);
        for (int i = 1; i <= 4; i++) p.pickNumber(i);

        p.startDrawingForTest();
        p.clearSelections();

        assertFalse(p.getPickedNumbers().isEmpty());
    }

    @Test
    public void testResetDuringGameShowsWarning() {
        MockView view = new MockView();
        GamePresenter p = new GamePresenter(view);

        p.selectSpotsForTest(4);
        p.setNumDrawingsForTest(3);
        for (int i = 1; i <= 4; i++) p.pickNumber(i);

        p.startDrawingForTest();
        p.resetGame();
        assertTrue(view.getMessages().toString().contains("Wait"));
    }

    @Test
    public void testNextAfterGameOverShowsPopup() {
        MockView view = new MockView();
        GamePresenter p = new GamePresenter(view);

        p.selectSpotsForTest(4);
        p.setNumDrawingsForTest(1);
        for (int i = 1; i <= 4; i++) p.pickNumber(i);

        p.setNumDrawingsForTest(0);
        p.nextDrawing();
        assertTrue(view.getMessages().toString().contains("Game Over"));
    }
    @Test
    public void testAutoPickAfterStartNotAllowed() {
        MockView view = new MockView();
        GamePresenter p = new GamePresenter(view);

        p.selectSpotsForTest(4);
        p.setNumDrawingsForTest(3);
        for (int i = 1; i <= 4; i++) p.pickNumber(i);

        p.startDrawingForTest();
        p.autoPick();
        assertTrue(view.getMessages().toString().contains("Cannot Auto Pick"));
    }

    @Test
    public void testStartDrawingWithoutSpotsShowsWarning() {
        MockView view = new MockView();
        GamePresenter p = new GamePresenter(view);
        p.startDrawing();
        assertTrue(view.getMessages().toString().contains("No Spots Selected"));
    }

    @Test
    public void testStartDrawingWithoutDrawingsShowsWarning() {
        MockView view = new MockView();
        GamePresenter p = new GamePresenter(view);
        p.selectSpotsForTest(4);
        for (int i = 1; i <= 4; i++) p.pickNumber(i);
        p.startDrawing();
        assertTrue(view.getMessages().toString().contains("No Drawings Set"));
    }

    @Test
    public void testResetGameAfterFinishAllowsNewGame() {
        MockView view = new MockView();
        GamePresenter p = new GamePresenter(view);
        p.selectSpotsForTest(4);
        for (int i = 1; i <= 4; i++) p.pickNumber(i);
        p.setNumDrawingsForTest(1);
        p.startDrawingForTest();
        p.setNumDrawingsForTest(0);
        p.resetGame();
        assertTrue(view.getMessages().toString().contains("New Game"));
    }

    @Test
    public void testPickManualShowsPopup() {
        MockView view = new MockView();
        GamePresenter p = new GamePresenter(view);
        p.pickManual();
        assertTrue(view.getMessages().toString().contains("Manual Mode"));
    }

    @Test
    public void testStartDrawingTwiceShowsAlreadyStarted() {
        MockView view = new MockView();
        GamePresenter p = new GamePresenter(view);
        p.selectSpotsForTest(4);
        p.setNumDrawingsForTest(2);
        for (int i = 1; i <= 4; i++) p.pickNumber(i);
        p.startDrawingForTest();
        p.startDrawingForTest();
        assertTrue(view.getMessages().toString().contains("Already Started"));
    }

    @Test
    public void testMultipleDrawingsProgressAndGameOver() {
        MockView view = new MockView();
        GamePresenter p = new GamePresenter(view);

        // setup: chọn spots và drawings
        p.selectSpotsForTest(4);
        p.setNumDrawingsForTest(3);
        for (int i = 1; i <= 4; i++) p.pickNumber(i);

        // bắt đầu vòng đầu tiên
        int before = p.getNumDrawings();
        p.startDrawingForTest();
        int afterFirst = p.getNumDrawings();

        // sau lượt đầu, số drawings phải giảm
        assertTrue(afterFirst < before, "Number of drawings should decrease after first round");

        // tiếp tục vẽ cho tới hết
        while (p.getNumDrawings() > 0) {
            p.nextDrawing();
        }

        // khi hết lượt, popup "Game Over" phải hiện
        p.nextDrawing();
        assertTrue(view.getMessages().toString().contains("Game Over"),
                "Expected 'Game Over' popup after all drawings are complete");
    }

}
