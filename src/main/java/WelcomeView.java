import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.geometry.Insets;

public class WelcomeView {
    private final SceneRouter router;
    private final Scene scene;

    public WelcomeView(SceneRouter router) {
        this.router = router;

        StackPane root = new StackPane();

        Image bgImage = new Image(getClass().getResourceAsStream("/background_welcome.jpg"));
        ImageView bgView = new ImageView(bgImage);
        bgView.setPreserveRatio(true);
        bgView.setFitWidth(1365);
        bgView.setFitHeight(768);
        bgView.setSmooth(true);
        bgView.setMouseTransparent(true);

        BorderPane overlay = new BorderPane();
        overlay.setPadding(new Insets(20));

        Button menuOverlay = new Button("Menu");
        menuOverlay.setStyle(
                "-fx-font-size: 18;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: gold;" +
                        "-fx-background-color: black;" +
                        "-fx-border-color: gold;" +
                        "-fx-border-width: 2;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-radius: 8;"
        );
        menuOverlay.setPrefWidth(355);
        menuOverlay.setPrefHeight(10);
        menuOverlay.setCursor(javafx.scene.Cursor.HAND);

        StackPane.setAlignment(menuOverlay, Pos.TOP_RIGHT);
        StackPane.setMargin(menuOverlay, new Insets(0, 2, 0, 0));

        ContextMenu menuPopup = new ContextMenu();
        menuPopup.setStyle(
                "-fx-background-color: rgba(0,0,0,0.9);" +
                        "-fx-border-color: gold;" +
                        "-fx-border-width: 2;" +
                        "-fx-padding: 4;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;"
        );


        String[] labels = {"Rules", "Odds", "Exit"};
        for (String label : labels) {
            Label lbl = new Label(label);
            lbl.setPrefWidth(335);
            lbl.setStyle(
                    "-fx-text-fill: gold;" +
                            "-fx-font-size: 18;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-color: rgba(0,0,0,0.9);" +
                            "-fx-padding: 10 15 10 15;" +
                            "-fx-alignment: center;"
            );

            CustomMenuItem item = new CustomMenuItem(lbl, true);
            menuPopup.getItems().add(item);

            lbl.setOnMouseEntered(e -> lbl.setStyle(
                    "-fx-text-fill: black;" +
                            "-fx-font-size: 18;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-color: gold;" +
                            "-fx-padding: 10 15 10 15;" +
                            "-fx-alignment: center;"
            ));
            lbl.setOnMouseExited(e -> lbl.setStyle(
                    "-fx-text-fill: gold;" +
                            "-fx-font-size: 18;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-color: rgba(0,0,0,0.9);" +
                            "-fx-padding: 10 15 10 15;" +
                            "-fx-alignment: center;"
            ));

            lbl.setOnMouseClicked(e -> {
                switch (label) {
                    case "Rules":
                        String rulesMsg =
                                " HOW TO PLAY KENO \n\n" +
                                        "Choose how many spots to play: 1, 4, 8, or 10.\n" +
                                        "Select that many numbers from 1–80 on the grid.\n" +
                                        "Choose how many drawings to play (1–4 rounds).\n" +
                                        "Start the game — 20 random numbers will be drawn each round.\n" +
                                        "The more of your numbers match the drawn numbers, the more you win!\n\n" +
                                        "Once drawings begin, you can no longer change spots or selections.";
                        showPopup("Rules", rulesMsg);
                        break;

                    case "Odds":
                        String oddsMsg =
                                "🎲 KENO ODDS (North Carolina Lottery)\n\n" +
                                        "Spot 1 → 1 in 4 chance to win\n" +
                                        "Spot 4 → 1 in 326 chance to hit all 4\n" +
                                        "Spot 8 → 1 in 230,000 chance to hit all 8\n" +
                                        "Spot 10 → 1 in 8,911,711 chance to hit all 10\n\n" +
                                        "Full payout & odds table available at:\n" +
                                        "https://nclottery.com/KenoHow";
                        showPopup("Odds", oddsMsg);
                        break;

                    case "Exit":
                        System.exit(0);
                        break;
                }

                menuPopup.hide();
            });
        }


        menuOverlay.setOnAction(e ->
                menuPopup.show(menuOverlay, javafx.geometry.Side.BOTTOM, 0, 0)
        );

        VBox centerBox = new VBox();
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(525, 0, 0, 0));

        Button startBtn = new Button("Start game");
        startBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: transparent;" +
                        "-fx-text-fill: transparent;"
        );
        startBtn.setPrefWidth(330);
        startBtn.setPrefHeight(150);
        startBtn.setOnAction(e -> router.switchTo("game"));

        centerBox.getChildren().add(startBtn);
        overlay.setCenter(centerBox);


        root.getChildren().addAll(bgView, overlay, menuOverlay);

        this.scene = new Scene(root, 1365, 768);
    }

    private void showPopup(String title, String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    public Scene getScene() {
        return scene;
    }
}
