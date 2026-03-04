package torricelli.tetris_project;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import torricelli.blocchi.Blocco;
import torricelli.blocchi.Punta;
import torricelli.blocchi.Test;

public class FXMLController implements Initializable {

	private Blocco blocco;
	private boolean[][] occupied;
	
	@FXML
	private GridPane mainGrid;

	@FXML
	private GridPane nextBlockGrid;

	@FXML
	void onKeyPressed(KeyEvent event) {
		KeyCode code =  event.getCode();
		if(code == KeyCode.UP) {
			//Ruota il blocco
			blocco.rotate();
		}
		if(code == KeyCode.DOWN){
			//Conferma il blocco
			blocco.confirm();
		}
		if(code == KeyCode.LEFT){
			//Sposta a sinistra il blocco
			blocco.moveLeft();
		}
		if(code == KeyCode.RIGHT){
			//Sposta a destra il blocco
			blocco.moveRight();
		}
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {

		occupied = new boolean[mainGrid.getRowCount()][mainGrid.getColumnCount()];
		for(int i = 0; i < occupied.length; i++)
			for(int j = 0; j < occupied[j].length; j++)
				occupied[i][j] = false;
		
		game();
	}

	public void game() {

		spawnBlock();
		Timeline timeline = new Timeline(

				new KeyFrame(Duration.seconds(1), e -> {

					if(blocco.getIsFalling())
						blocco.fall();
					
					else
						spawnBlock();
					
				}));

		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();
	}
	
	public void spawnBlock() {
		
		blocco = new Punta(mainGrid, occupied);
		blocco.draw();
	}
}
