import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.control.ChoiceDialog;

public class GamePresenter {
    private final IGameView view;

    private final Set<Integer> picks = new HashSet<>();
    private final Set<Integer> results = new HashSet<>();
    private final java.util.List<Integer> hitsPerDraw = new java.util.ArrayList<>();

    private int numSpots = 0;
    private int numDrawings = 0;
    private final Random random = new Random();
    private boolean gameStarted = false;
    private boolean firstDrawStarted = false;
    private boolean drawingInProgress = false;


    public GamePresenter(IGameView view) {
        this.view = view;
    }

    public void selectSpots() {
        if (gameStarted) {
            view.showPopup("Locked", "You cannot change spots after drawings have started.");
            return;
        }

        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(4, 1, 4, 8, 10);
        dialog.setTitle("Select Spots");
        dialog.setHeaderText("Choose how many spots you want to play:");
        dialog.setContentText("Spots:");

        dialog.showAndWait().ifPresent(choice -> {
            numSpots = choice;
            picks.clear();
            results.clear();
            view.clearSelections();
            gameStarted = false;
            view.enableGrid(true);

            view.showPopup("Spots Selected",
                    "You will play with " + numSpots + " spots.");
        });
    }

    public void selectDrawings() {
        if (gameStarted) {
            view.showPopup("Locked", "You cannot change drawings after the game has started.");
            return;
        }

        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(1, 1, 2, 3, 4);
        dialog.setTitle("Select Drawings");
        dialog.setHeaderText("Choose how many drawings you want to play:");
        dialog.setContentText("Drawings:");

        dialog.showAndWait().ifPresent(choice -> {
            numDrawings = choice;
            view.showPopup("Drawings Set", "Total drawings: " + numDrawings);
        });
    }

    public void startDrawing() {
        if (numSpots <= 0) {
            view.showPopup("No Spots Selected", "Please select the number of spots first.");
            return;
        }

        if (firstDrawStarted) {
            view.showPopup("Already Started", "Use the 'Next' button for subsequent drawings.");
            return;
        }

        if (gameStarted) {
            view.showPopup("Already Started", "Drawing already in progress!");
            return;
        }

        if (picks.isEmpty()) {
            view.showPopup("No Numbers Selected", "Please pick your numbers before starting.");
            return;
        }

        if (numDrawings <= 0) {
            view.showPopup("No Drawings Set", "Please select number of drawings first.");
            return;
        }

        if (picks.size() < numSpots) {
            view.showPopup("Incomplete Selection",
                    "You selected " + picks.size() + " number(s), but chose " + numSpots + " spots.\n" +
                            "Please pick " + (numSpots - picks.size()) + " more before starting.");
            return;
        }

        firstDrawStarted = true;
        gameStarted = true;

        performDrawingRound();
    }

    private void performDrawingRound() {
        if (drawingInProgress) return;
        drawingInProgress = true;

        view.resetToPickColors(picks);
        view.disableNextButton(true);

        results.clear();
        while (results.size() < 20) {
            results.add(random.nextInt(80) + 1);
        }

        Timeline timeline = new Timeline();
        int index = 0;
        for (Integer num : results) {
            KeyFrame kf = new KeyFrame(Duration.seconds(index * 0.25), e -> {
                view.highlightDrawnNumber(num);
            });
            timeline.getKeyFrames().add(kf);
            index++;
        }

        timeline.setOnFinished(e -> {
            javafx.application.Platform.runLater(() -> {

                view.showDrawResults(results, picks);

                int hits = 0;
                for (int n : picks)
                    if (results.contains(n)) hits++;
                hitsPerDraw.add(hits);

                numDrawings--;

                PayoutTable payout = new PayoutTable();
                int reward = payout.getPayout(numSpots, hits);

                if (numDrawings > 0) {
                    view.showPopup(
                            "Drawing Result",
                            "You hit " + hits + " out of " + numSpots +
                                    "!\nYou win $" + reward +
                                    "\n" + numDrawings + " drawing(s) remaining."
                    );
                    view.enableGrid(false);
                    view.disableNextButton(false);
                } else {
                    int totalWin = 0;
                    for (int h : hitsPerDraw) {
                        totalWin += payout.getPayout(numSpots, h);
                    }

                    view.showFinalResult(hitsPerDraw, picks.size(), totalWin);
                    view.disableNextButton(true);
                    firstDrawStarted = false;
                    view.enableGrid(false);
                }
            });
            drawingInProgress = false;
        });

        timeline.play();
    }

    public void nextDrawing() {
        view.enableGrid(true);
        if (numDrawings == 0 && !firstDrawStarted) {
            view.showPopup("Game Over", "No more drawings left!");
            return;
        }

        if (numDrawings == 0 && firstDrawStarted) {
            view.showPopup("Game Over", "No more drawings left!");
            firstDrawStarted = false;
            gameStarted = false;
            return;
        }

        if (!firstDrawStarted) {
            view.showPopup("Not Started Yet", "Press 'Start Drawing' to begin the game.");
            return;
        }

        if (numDrawings > 0) {
            performDrawingRound();
        } else {
            view.showPopup("Game Over", "No more drawings left!");
            firstDrawStarted = false;
            gameStarted = false;
        }
    }

    public void pickManual() {
        if (gameStarted) {
            view.showPopup("Cannot Auto Pick", "You cannot change numbers once the drawings have started!");
            return;
        }

        view.showPopup("Manual Mode", "Click on the grid to select numbers manually.");
    }

    public void autoPick() {
        if (gameStarted) {
            view.showPopup("Cannot Auto Pick", "You cannot change numbers once the drawings have started!");
            return;
        }

        if (numSpots <= 0) {
            view.showPopup("No Spots Selected", "Please select the number of spots first.");
            return;
        }

        if (numDrawings == 0 && !picks.isEmpty()) {
            view.showPopup("Game Over", "Start a new game or clear all spots to auto pick again.");
            return;
        }

        picks.clear();
        while (picks.size() < numSpots) {
            picks.add(random.nextInt(80) + 1);
        }

        view.clearSelections();
        for (int n : picks) {
            view.updateSelectionHighlight(n, true);
        }

        view.showPopup("Auto Pick", "Auto-picked numbers:\n" + picks);
    }


    public void clearSelections() {
        if (gameStarted || firstDrawStarted) {
            view.showPopup("Action Locked",
                    "You cannot clear selections after the game has started.");
            return;
        }

        picks.clear();
        view.clearSelections();
        view.showPopup("Cleared", "All selections have been cleared.");
    }


    public void pickNumber(int num) {
        if (gameStarted) {
            return;
        }

        if (picks.contains(num)) {
            picks.remove(num);
            view.updateSelectionHighlight(num, false);
        } else {
            if (picks.size() < numSpots || numSpots == 0) {
                picks.add(num);
                view.updateSelectionHighlight(num, true);
            } else {
                view.showPopup("Limit Reached", "You have reached the max spots: " + numSpots);
            }
        }
    }

    public void resetGame() {
        if (drawingInProgress || (numDrawings > 0 && gameStarted)) {
            view.showPopup("Wait", "You cannot start a new game while a drawing is in progress.");
            return;
        }

        picks.clear();
        results.clear();
        numSpots = 0;
        numDrawings = 0;
        hitsPerDraw.clear();

        firstDrawStarted = false;
        gameStarted = false;

        view.clearSelections();
        view.enableGrid(false);
        view.disableNextButton(true);

        view.showPopup("New Game", "Game has been reset. Please select Spots and Drawings again.");

    }


    public boolean isPicked(int num) {
        return picks.contains(num);
    }

    public int getNumDrawings() { return numDrawings; }
    public Set<Integer> getPickedNumbers() { return picks; }
    public void selectSpotsForTest(int n) { numSpots = n; }
    public int getNumSpots() { return numSpots; }

    public void setNumDrawingsForTest(int n) { numDrawings = n; }

    public void startDrawingForTest() {
        if (gameStarted && firstDrawStarted) {
            view.showPopup("Already Started", "Drawing already in progress!");
            return;
        }

        gameStarted = true;
        firstDrawStarted = true;
        results.clear();

        if (picks.isEmpty()) {
            picks.add(1);
        }

        Random r = new Random();
        while (results.size() < 20) results.add(r.nextInt(80) + 1);

        int hits = 0;
        for (int n : picks) if (results.contains(n)) hits++;
        hitsPerDraw.add(hits);

        numDrawings = Math.max(0, numDrawings - 1);

    }

}
