import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import java.util.Objects;
import java.util.Set;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

public class GameView implements IGameView {
    private final GamePresenter presenter;
    private final SceneRouter router;
    private final Scene scene;
    private final StackPane root;
    private final ImageView bgView;
    private Button nextBtn;

    private final GridPane grid;

    private final Image bgClassic;
    private final Image bgNewLook;

    public GameView(SceneRouter router) {
        this.router = router;
        this.root = new StackPane();
        this.presenter = new GamePresenter(this);

        bgClassic = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/scene 2.1.jpg")));
        bgNewLook = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/scene 2.2.jpg")));


        bgView = new ImageView(bgClassic);
        bgView.setFitWidth(1365);
        bgView.setFitHeight(768);
        bgView.setPreserveRatio(false);
        bgView.setSmooth(true);
        bgView.setMouseTransparent(true);

        BorderPane overlay = new BorderPane();
        overlay.setPadding(new Insets(20));

        Button menuButton = new Button("Menu");
        menuButton.setStyle(
                "-fx-font-size: 18;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: gold;" +
                        "-fx-background-color: black;" +
                        "-fx-border-color: gold;" +
                        "-fx-border-width: 2;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-radius: 8;"
        );
        menuButton.setPrefSize(390, 40);
        menuButton.setCursor(javafx.scene.Cursor.HAND);

        HBox menuBox = new HBox(menuButton);
        menuBox.setAlignment(Pos.TOP_RIGHT);
        menuBox.setPadding(new Insets(-20, -20, 0, 0));
        overlay.setTop(menuBox);

        ContextMenu menuPopup = new ContextMenu();
        menuPopup.setStyle("-fx-background-color: transparent; -fx-border-color: gold; -fx-border-width: 2; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;");

        String[] labels = {"Rules", "Odds", "New Look", "Back to Home Screen", "Exit"};

        for (String label : labels) {
            Label lbl = new Label(label);
            lbl.setPrefWidth(365);
            lbl.setStyle(
                    "-fx-text-fill: gold;" +
                            "-fx-font-size: 16;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-color: rgba(50,50,50,0.4);" +
                            "-fx-padding: 10 15 10 15;" +
                            "-fx-focus-color: transparent;" +
                            "-fx-faint-focus-color: transparent;"
            );

            CustomMenuItem item = new CustomMenuItem(lbl, true);
            item.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-control-inner-background: transparent;" +
                            "-fx-background-insets: 0;" +
                            "-fx-padding: 0;" +
                            "-fx-focus-color: transparent;" +
                            "-fx-faint-focus-color: transparent;"
            );
            menuPopup.getItems().add(item);

            lbl.setOnMouseEntered(e -> lbl.setStyle(
                    "-fx-background-color: gold;" +
                            "-fx-text-fill: black;" +
                            "-fx-font-size: 16;" +
                            "-fx-font-weight: bold;" +
                            "-fx-padding: 10 15 10 15;"
            ));
            lbl.setOnMouseExited(e -> lbl.setStyle(
                    "-fx-background-color: rgba(50,50,50,0.4);" +
                            "-fx-text-fill: gold;" +
                            "-fx-font-size: 16;" +
                            "-fx-font-weight: bold;" +
                            "-fx-padding: 10 15 10 15;"
            ));

            lbl.setOnMouseClicked(e -> {
                switch (label) {
                    case "Rules":
                        String rulesMsg =
                                "HOW TO PLAY KENO \n\n" +
                                        " Choose how many spots to play: 1, 4, 8, or 10.\n" +
                                        "️ Select that many numbers from 1–80 on the grid.\n" +
                                        " Choose how many drawings to play (1–4 rounds).\n" +
                                        "Start the game — 20 random numbers will be drawn each round.\n" +
                                        "The more of your numbers match the drawn numbers, the more you win!\n\n" +
                                        "Once drawings begin, you can no longer change spots or selections.";
                        showPopup("Rules", rulesMsg);
                        break;
                    case "Odds":
                        String oddsMsg =
                                "KENO ODDS (North Carolina Lottery)\n\n" +
                                        "Spot 1: 1 in 4 chance to win\n" +
                                        "Spot 4: 1 in 326 chance to hit all 4\n" +
                                        "Spot 8: 1 in 230,000 chance to hit all 8\n" +
                                        "Spot 10: 1 in 8,911,711 chance to hit all 10\n\n" +
                                        "See: nclottery.com/KenoHow for full table.";
                        showPopup("Odds", oddsMsg);
                        break;

                    case "New Look":
                        toggleBackground();
                        break;

                    case "Back to Home Screen":
                        if (router != null) {
                            FadeTransition fade = new FadeTransition(Duration.millis(700), root);
                            fade.setFromValue(1.0);
                            fade.setToValue(0.0);
                            fade.setOnFinished(ev -> {
                                router.reloadGame();
                                router.switchTo("welcome");
                            });
                            fade.play();
                        } else {
                            showPopup("Unavailable", "Cannot go back — router not initialized.");
                        }
                        break;

                    case "Exit":
                        System.exit(0);
                        break;
                }

                menuPopup.hide();
            });
        }

        menuButton.setOnAction(e ->
                menuPopup.show(menuButton, javafx.geometry.Side.BOTTOM, 0, 0)
        );


        grid = new GridPane();
        grid.setDisable(true);
        grid.getStyleClass().add("grid-pane");

        grid.setHgap(5);
        grid.setVgap(4);
        grid.setAlignment(Pos.CENTER);
        for (int i = 1; i <= 80; i++) {
            Button b = new Button(String.valueOf(i));
            b.setPrefSize(79, 55);
            int num = i;

            b.setStyle(
                    "-fx-background-color: rgba(139,0,0,1.0);" +
                            "-fx-text-fill: #FFFFFF;" +
                            "-fx-font-weight: bold;" +
                            "-fx-font-size: 19;" +
                            "-fx-effect: dropshadow(one-pass-box, black, 3, 1.0, 0, 0);" +
                            "-fx-border-color: gold; -fx-border-width: 2;" +
                            "-fx-background-radius: 8; -fx-border-radius: 8;"
            );

            b.setOnAction(e -> {
                if (b.isDisabled()) return;

                presenter.pickNumber(num);

                boolean isPicked = presenter.isPicked(num);
                boolean isClassic = (bgView.getImage() == bgClassic);

                if (isPicked) {
                    if (isClassic) {
                        b.setStyle(
                                "-fx-background-color: gold;" +
                                        "-fx-text-fill: black;" +
                                        "-fx-font-weight: bold;" +
                                        "-fx-font-size: 22;" +
                                        "-fx-border-color: gold; -fx-border-width: 2;" +
                                        "-fx-background-radius: 8; -fx-border-radius: 8;"
                        );
                    } else {
                        b.setStyle(
                                "-fx-background-color: rgba(139,0,0,1.0);" +
                                        "-fx-text-fill: white;" +
                                        "-fx-font-weight: bold;" +
                                        "-fx-font-size: 22;" +
                                        "-fx-border-color: gold; -fx-border-width: 2;" +
                                        "-fx-background-radius: 8; -fx-border-radius: 8;"
                        );
                    }
                } else {
                    if (isClassic) {
                        b.setStyle(
                                "-fx-background-color: rgba(139,0,0,1.0);" +
                                        "-fx-text-fill: #FFFFFF;" +
                                        "-fx-font-weight: bold;" +
                                        "-fx-font-size: 19;" +
                                        "-fx-border-color: gold; -fx-border-width: 2;" +
                                        "-fx-background-radius: 8; -fx-border-radius: 8;"
                        );
                    } else {
                        b.setStyle(
                                "-fx-background-color: #F5F5DC;" +
                                        "-fx-text-fill: darkred;" +
                                        "-fx-font-weight: bold;" +
                                        "-fx-font-size: 19;" +
                                        "-fx-border-color: black; -fx-border-width: 2;" +
                                        "-fx-background-radius: 8; -fx-border-radius: 8;"
                        );
                    }
                }
            });

            grid.add(b, (i - 1) % 10, (i - 1) / 10);
        }

        VBox gridBox = new VBox(grid);
        gridBox.setAlignment(Pos.CENTER);
        gridBox.setPadding(new Insets(8, 0, 0, 0));
        overlay.setCenter(gridBox);


        VBox leftButtons = new VBox(26);
        leftButtons.setAlignment(Pos.CENTER_LEFT);
        leftButtons.setPadding(new Insets(50, 0, 0, 23));

        Button spotsBtn = new Button("Spots: 0");
        Button drawingsBtn = new Button("Drawings: 0");
        nextBtn = new Button("Next");

        VBox rightButtons = new VBox(26);
        rightButtons.setAlignment(Pos.CENTER_RIGHT);
        rightButtons.setPadding(new Insets(50, 24, 0, 0));

        Button pickManualBtn = new Button("Pick manually");
        Button autoPickBtn = new Button("Auto pick");
        Button clearBtn = new Button("Clear");
        clearBtn.setOnAction(e -> clearSelections());

        for (Button btn : new Button[]{
                spotsBtn, drawingsBtn, nextBtn,
                pickManualBtn, autoPickBtn, clearBtn
        }) {
            btn.setPrefSize(175, 50);
            btn.setStyle(
                    "-fx-font-size: 18;" +
                            "-fx-font-weight: bold;" +
                            "-fx-text-fill: transparent;" +
                            "-fx-background-color: transparent;" +
                            "-fx-border-color: -fx-border-width: 2;" +
                            "-fx-border-width: 2;" +
                            "-fx-background-radius: 15;" +
                            "-fx-border-radius: 15;"
            );

            btn.setOnMouseEntered(e -> btn.setStyle(
                    "-fx-font-size: 18; -fx-font-weight: bold;" +
                            "-fx-text-fill: transparent;" +
                            "-fx-background-color: rgba(0,0,0,0.3);" +
                            "-fx-border-color: gold; -fx-border-width: 2;" +
                            "-fx-background-radius: 15; -fx-border-radius: 15;"
            ));
            btn.setOnMouseExited(e -> btn.setStyle(
                    "-fx-font-size: 18; -fx-font-weight: bold;" +
                            "-fx-text-fill: transparent;" +
                            "-fx-background-color: transparent;" +
                            "-fx-border-color: -fx-border-width: 2;" +
                            "-fx-background-radius: 15; -fx-border-radius: 15;"
            ));
        }

        spotsBtn.setOnAction(e -> presenter.selectSpots());
        drawingsBtn.setOnAction(e -> presenter.selectDrawings());
        nextBtn.setOnAction(e -> presenter.nextDrawing());


        pickManualBtn.setOnAction(e -> presenter.pickManual());
        autoPickBtn.setOnAction(e -> presenter.autoPick());
        clearBtn.setOnAction(e -> presenter.clearSelections());

        leftButtons.getChildren().addAll(spotsBtn, drawingsBtn, nextBtn);
        rightButtons.getChildren().addAll(pickManualBtn, autoPickBtn, clearBtn);
        overlay.setLeft(leftButtons);
        overlay.setRight(rightButtons);


        HBox bottom = new HBox(165);
        bottom.setAlignment(Pos.CENTER);

        Button startDrawing = new Button("Start drawing");
        Button newGame = new Button("New game");
        startDrawing.setOnAction(e -> presenter.startDrawing());
        newGame.setOnAction(e -> presenter.resetGame());

        for (Button btn : new Button[]{startDrawing, newGame}) {
            btn.setPrefSize(190, 90);
            btn.setStyle(
                    "-fx-font-size: 26; -fx-font-weight: bold;" +
                            "-fx-text-fill: transparent;" +
                            "-fx-background-color: transparent;" +
                            "-fx-border-color: transparent;" +
                            "-fx-background-radius: 12; -fx-border-radius: 12;"
            );

            btn.setOnMouseEntered(e -> btn.setStyle(
                    "-fx-font-size: 26; -fx-font-weight: bold;" +
                            "-fx-text-fill: transparent;" +
                            "-fx-background-color: rgba(0,0,0,0.3);" +
                            "-fx-border-color: gold; -fx-border-width: 3;" +
                            "-fx-background-radius: 12; -fx-border-radius: 12;"
            ));
            btn.setOnMouseExited(e -> btn.setStyle(
                    "-fx-font-size: 26; -fx-font-weight: bold;" +
                            "-fx-text-fill: transparent;" +
                            "-fx-background-color: transparent;" +
                            "-fx-border-color: transparent;" +
                            "-fx-background-radius: 12; -fx-border-radius: 12;"
            ));
        }

        bottom.getChildren().addAll(startDrawing, newGame);

        VBox bottomBox = new VBox(bottom);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(-55, 0, 0, 0));
        overlay.setBottom(bottomBox);
        BorderPane.setMargin(bottomBox, new Insets(0, 0, 40, 0));


        root.getChildren().addAll(bgView, overlay);

        this.scene = new Scene(root, 1365, 768);
    }

    public void toggleBackground() {
        boolean switchingToNew = (bgView.getImage() == bgClassic);
        bgView.setImage(switchingToNew ? bgNewLook : bgClassic);

        boolean isClassic = !switchingToNew;

        grid.getChildren().forEach(node -> {
            if (node instanceof Button) {
                Button b = (Button) node;
                if (b.getText().matches("\\d+")) {
                    int num = Integer.parseInt(b.getText());
                    boolean picked = presenter.isPicked(num);

                    if (picked) {
                        if (isClassic) {
                            b.setStyle(
                                    "-fx-background-color: gold;" +
                                            "-fx-text-fill: black;" +
                                            "-fx-font-weight: bold;" +
                                            "-fx-font-size: 19;" +
                                            "-fx-border-color: gold; -fx-border-width: 2;" +
                                            "-fx-background-radius: 8; -fx-border-radius: 8;"
                            );
                        } else {
                            b.setStyle(
                                    "-fx-background-color: #8B0000;" +
                                            "-fx-text-fill: white;" +
                                            "-fx-font-weight: bold;" +
                                            "-fx-font-size: 19;" +
                                            "-fx-border-color: gold; -fx-border-width: 2;" +
                                            "-fx-background-radius: 8; -fx-border-radius: 8;"
                            );
                        }
                    } else {
                        if (isClassic) {
                            b.setStyle(
                                    "-fx-background-color: #8B0000;" +
                                            "-fx-text-fill: #FFFFFF;" +
                                            "-fx-font-weight: bold;" +
                                            "-fx-font-size: 19;" +
                                            "-fx-border-color: gold; -fx-border-width: 2;" +
                                            "-fx-background-radius: 8; -fx-border-radius: 8;"
                            );
                        } else {
                            b.setStyle(
                                    "-fx-background-color: #F5F5DC;" +
                                            "-fx-text-fill: #8B0000;" +
                                            "-fx-font-weight: bold;" +
                                            "-fx-font-size: 19;" +
                                            "-fx-border-color: black; -fx-border-width: 2;" +
                                            "-fx-background-radius: 8; -fx-border-radius: 8;"
                            );
                        }
                    }
                }
            }
        });
    }


    public Scene getScene() {
        return scene;
    }

    public void showPopup(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void updateSelectionHighlight(int n, boolean selected) {
        boolean isClassic = (bgView.getImage() == bgClassic);

        root.lookupAll(".button").forEach(node -> {
            if (node instanceof Button) {
                Button b = (Button) node;
                if (b.getText().equals(String.valueOf(n))) {
                    if (selected) {
                        if (isClassic) {
                            b.setStyle(
                                    "-fx-background-color: gold;" +
                                            "-fx-text-fill: black;" +
                                            "-fx-font-weight: bold;" +
                                            "-fx-font-size: 19;" +
                                            "-fx-border-color: gold; -fx-border-width: 2;" +
                                            "-fx-background-radius: 8; -fx-border-radius: 8;"
                            );
                        } else {
                            b.setStyle(
                                    "-fx-background-color: #8B0000;" +
                                            "-fx-text-fill: white;" +
                                            "-fx-font-weight: bold;" +
                                            "-fx-font-size: 19;" +
                                            "-fx-border-color: gold; -fx-border-width: 2;" +
                                            "-fx-background-radius: 8; -fx-border-radius: 8;"
                            );
                        }
                    } else {
                        if (isClassic) {
                            b.setStyle(
                                    "-fx-background-color: #8B0000;" +
                                            "-fx-text-fill: #FFFFFF;" +
                                            "-fx-font-weight: bold;" +
                                            "-fx-font-size: 19;" +
                                            "-fx-border-color: gold; -fx-border-width: 2;" +
                                            "-fx-background-radius: 8; -fx-border-radius: 8;"
                            );
                        } else {
                            b.setStyle(
                                    "-fx-background-color: #F5F5DC;" +
                                            "-fx-text-fill: darkred;" +
                                            "-fx-font-weight: bold;" +
                                            "-fx-font-size: 19;" +
                                            "-fx-border-color: black; -fx-border-width: 2;" +
                                            "-fx-background-radius: 8; -fx-border-radius: 8;"
                            );
                        }
                    }
                }
            }
        });
    }


    public void clearSelections() {
        boolean isClassic = (bgView.getImage() == bgClassic);

        root.lookupAll(".button").forEach(node -> {
            if (node instanceof Button) {
                Button b = (Button) node;
                if (b.getText().matches("\\d+")) {
                    if (isClassic) {
                        b.setStyle(
                                "-fx-background-color: rgba(139,0,0,1.0);" +
                                        "-fx-text-fill: #FFFFFF;" +
                                        "-fx-font-weight: bold;" +
                                        "-fx-font-size: 19;" +
                                        "-fx-border-color: gold; -fx-border-width: 2;" +
                                        "-fx-background-radius: 8; -fx-border-radius: 8;"
                        );
                    } else {
                        b.setStyle(
                                "-fx-background-color: #F5F5DC;" +
                                        "-fx-text-fill: darkred;" +
                                        "-fx-font-weight: bold;" +
                                        "-fx-font-size: 19;" +
                                        "-fx-border-color: black; -fx-border-width: 2;" +
                                        "-fx-background-radius: 8; -fx-border-radius: 8;"
                        );
                    }
                }
            }
        });
    }


    public void enableGrid(boolean on) {
        grid.setDisable(!on);
        grid.setMouseTransparent(!on);
        grid.getChildren().forEach(node -> {
            if (node instanceof Button) {
                Button b = (Button) node;
                if (b.getText().matches("\\d+")) {
                    b.setDisable(!on);
                }
            }
        });

    }


    public void showDrawResults(Set<Integer> results, Set<Integer> picks) {
        root.lookupAll(".button").forEach(node -> {
            if (node instanceof Button) {
                Button b = (Button) node;
                if (b.getText().matches("\\d+")) {
                    int num = Integer.parseInt(b.getText());
                    if (results.contains(num) && picks.contains(num)) {
                        b.setStyle("-fx-background-color: limegreen; -fx-text-fill: white; -fx-font-weight: bold;");
                    } else if (results.contains(num)) {
                        b.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-weight: bold;");
                    } else if (picks.contains(num)) {
                        b.setStyle("-fx-background-color: gold; -fx-text-fill: black; -fx-font-weight: bold;");
                    } else {
                        b.setStyle("-fx-background-color: transparent; -fx-text-fill: transparent; -fx-border-color: gold; -fx-border-width: 2;");
                    }
                }
            }
        });
    }

    public void showFinalResult(java.util.List<Integer> hitsPerDraw, int totalSpots, int totalWin) {
        StringBuilder summary = new StringBuilder();

        for (int i = 0; i < hitsPerDraw.size(); i++) {
            int hits = hitsPerDraw.get(i);
            summary.append("Round ").append(i + 1)
                    .append(": ").append(hits)
                    .append(" hit(s)\n");
        }

        summary.append("\nTotal Winnings: $").append(totalWin);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Final Result");
        alert.setHeaderText("Game Over - Summary");
        alert.setContentText(summary.toString());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                resetAfterSummary();
            }
        });
    }


    public void highlightDrawnNumber(int num) {
        root.lookupAll(".button").forEach(node -> {
            if (node instanceof Button) {
                Button b = (Button) node;
                if (b.getText().equals(String.valueOf(num))) {
                    b.setStyle(
                            "-fx-background-color: deepskyblue;" +
                                    "-fx-text-fill: white;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-font-size: 20;" +
                                    "-fx-border-color: gold; -fx-border-width: 3;" +
                                    "-fx-background-radius: 8; -fx-border-radius: 8;"
                    );
                }
            }
        });
    }

    public void resetToPickColors(Set<Integer> picks) {
        boolean isClassic = (bgView.getImage() == bgClassic);

        grid.getChildren().forEach(node -> {
            if (node instanceof Button) {
                Button b = (Button) node;
                if (b.getText().matches("\\d+")) {
                    int num = Integer.parseInt(b.getText());
                    if (picks.contains(num)) {
                        if (isClassic) {
                            b.setStyle(
                                    "-fx-background-color: gold;" +
                                            "-fx-text-fill: black;" +
                                            "-fx-font-weight: bold;" +
                                            "-fx-font-size: 19;" +
                                            "-fx-border-color: gold; -fx-border-width: 2;" +
                                            "-fx-background-radius: 8; -fx-border-radius: 8;"
                            );
                        } else {
                            b.setStyle(
                                    "-fx-background-color: rgba(139,0,0,1.0);" +
                                            "-fx-text-fill: white;" +
                                            "-fx-font-weight: bold;" +
                                            "-fx-font-size: 19;" +
                                            "-fx-border-color: gold; -fx-border-width: 2;" +
                                            "-fx-background-radius: 8; -fx-border-radius: 8;"
                            );
                        }
                    } else {
                        if (isClassic) {
                            b.setStyle(
                                    "-fx-background-color: rgba(139,0,0,1.0);" +
                                            "-fx-text-fill: #FFFFFF;" +
                                            "-fx-font-weight: bold;" +
                                            "-fx-font-size: 19;" +
                                            "-fx-border-color: gold; -fx-border-width: 2;" +
                                            "-fx-background-radius: 8; -fx-border-radius: 8;"
                            );
                        } else {
                            b.setStyle(
                                    "-fx-background-color: #F5F5DC;" +
                                            "-fx-text-fill: darkred;" +
                                            "-fx-font-weight: bold;" +
                                            "-fx-font-size: 19;" +
                                            "-fx-border-color: black; -fx-border-width: 2;" +
                                            "-fx-background-radius: 8; -fx-border-radius: 8;"
                            );
                        }
                    }
                }
            }
        });
    }

    public void disableNextButton(boolean disable) {
        if (nextBtn != null) {
            nextBtn.setDisable(disable);
        }
    }

    private void resetAfterSummary() {
        presenter.resetGame();
    }

}