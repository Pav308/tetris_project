package torricelli.tetris_project;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {

	private Stage stage;
	private Scene menu;

	public SceneManager(Stage stage) throws IOException {

		Parent root = FXMLLoader.load(getClass().getResource("/fxml/Menu.fxml"));
		this.menu = new Scene(root);

		this.stage = stage;
	}

	public void loadTetris() {

	    try {

	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Tetris.fxml"));
	        Parent root = loader.load();

	        Scene newScene = new Scene(root);

	        stage.setTitle("TetrisFX");
	        stage.setScene(newScene);
	        stage.show();

	    } catch (IOException e) {

	        e.printStackTrace();
	    }
	}

	public void loadMenu() {

		stage.setTitle("Menù");
		stage.setScene(menu);
		stage.show();
	}
}
