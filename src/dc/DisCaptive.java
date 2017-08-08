package dc;

import dc.controller.LevelController;
import dc.model.*;
import dc.view.LevelView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Starts DisCaptive
 * Combines MVC.
 */
/*Timo Kubitza Matrikelnr. 2794825
Wahlpflichtteil: Kraftfelder und Schlösser.
Zusatzlevel: 31-36
Level können neu gestartet werden.
Es können mehrere Level hintereinander gespielt werden, jedoch nur für eine View gelöst.
 */
public class DisCaptive extends Application {
    //private static String[] arguments;

    public void start(Stage stage){
       // int i = Integer.parseInt(arguments[0]);
        Level model = new Level(1);
        LevelController controller = new LevelController(model);
        LevelView view = new LevelView(model, controller);
        Scene scene = new Scene(view);

        stage.setTitle("DisCaptive: You ain't going nowhere!");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
        view.requestFocus();
    }
    public static void main (String[] args) {
       // arguments = args;
        launch(args);
    }
}


