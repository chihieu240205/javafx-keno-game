import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.Map;

public class SceneRouter {
    private final Stage stage;
    private final Map<String, Scene> scenes = new HashMap<>();

    public SceneRouter(Stage stage) {
        this.stage = stage;
    }

    public void add(String name, Scene scene) {
        scenes.put(name, scene);
    }

    public void switchTo(String name) {
        Scene scene = scenes.get(name);
        if (scene != null) {
            stage.setScene(scene);
            stage.show();
        } else {
            System.err.println(" Scene not found: " + name);
        }
    }

    public void reloadGame() {
        Scene gameScene = scenes.get("game");
        if (gameScene != null && stage != null) {
            GameView newGameView = new GameView(this);
            scenes.put("game", newGameView.getScene());
        }
    }
}
