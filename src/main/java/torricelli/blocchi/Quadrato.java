package torricelli.blocchi;

import java.util.Random;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Quadrato extends Blocco{

	public Quadrato(GridPane griglia, boolean[][] occupied) {

		super.lunghezza = 4;
		super.nrotazione = 1;
		super.griglia = griglia;
		super.rand = new Random();
		super.pane = new Pane[lunghezza];
		for (int i = 0; i < super.pane.length; i++) {

			super.pane[i] = new Pane();
			super.pane[i].setBackground(new Background(new BackgroundFill(Color.YELLOW, null, null)));
		}

		super.occupied = occupied;

		super.rotazione = rand.nextInt(3);

		// Coordinate della x
		super.posX = new int[nrotazione][lunghezza];

		super.posX[0][0] = 5;
		super.posX[0][1] = 6;
		super.posX[0][2] = 5;
		super.posX[0][3] = 6;

		// Coordinate della y
		super.posY = new int[nrotazione][lunghezza];

		super.posY[0][0] = 1;
		super.posY[0][1] = 1;
		super.posY[0][2] = 2;
		super.posY[0][3] = 2;

		super.isFalling = true;
	}
}
