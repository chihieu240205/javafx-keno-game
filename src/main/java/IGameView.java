import java.util.List;
import java.util.Set;

public interface IGameView {
    void showPopup(String title, String msg);
    void enableGrid(boolean on);
    void clearSelections();
    void updateSelectionHighlight(int n, boolean selected);
    void showDrawResults(Set<Integer> results, Set<Integer> picks);
    void showFinalResult(List<Integer> hitsPerDraw, int totalSpots, int totalWin);
    void highlightDrawnNumber(int n);
    void resetToPickColors(Set<Integer> picks);
    void disableNextButton(boolean disable);
}
