package torricelli.tetris_project;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import torricelli.blocchi.Blocco;
import torricelli.blocchi.Punta;
import torricelli.blocchi.Test;

public class FXMLController implements Initializable {

	@FXML
	private GridPane mainGrid;

	@FXML
	private GridPane nextBlockGrid;

	@FXML
	void onKeyPressed(KeyEvent event) {

	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {

		game();
	}

	public void game() {

		Blocco blocco = new Punta(mainGrid);
		blocco.draw();
		Timeline timeline = new Timeline(

				new KeyFrame(Duration.seconds(1), e -> {

					blocco.fall();
				}));
		
		timeline.setCycleCount(20);
		timeline.play();
	}
}
