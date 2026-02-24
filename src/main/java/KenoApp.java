import javafx.application.Application;
import javafx.stage.Stage;

public class KenoApp extends Application {
    @Override
    public void start(Stage stage) {
        SceneRouter router = new SceneRouter(stage);

        WelcomeView welcomeView = new WelcomeView(router);
        router.add("welcome", welcomeView.getScene());

        GameView gameView = new GameView(router);
        router.add("game", gameView.getScene());

        router.switchTo("welcome");
    }

    public static void main(String[] args) {
        launch();
    }
}

// mvn clean compile exec:java to start game
