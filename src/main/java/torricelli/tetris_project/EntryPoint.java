package torricelli.tetris_project;

import javafx.application.Application;
import javafx.stage.Stage;


public class EntryPoint extends Application {

	public static SceneManager sm;
    
	@Override
    public void start(Stage stage) throws Exception {
    	
    	sm = new SceneManager(stage);
    	sm.loadMenu();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
