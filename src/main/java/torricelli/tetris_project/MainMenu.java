package torricelli.tetris_project;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MainMenu {

	@FXML
	private Button tetrisButton;
	
	@FXML
	void startTetris() {
		
		EntryPoint.sm.loadTetris();
	}
}
