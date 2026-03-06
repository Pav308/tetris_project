package torricelli.blocchi;

import java.util.Random;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Lungo extends Blocco {

	public Lungo(GridPane griglia, boolean[][] occupied) {

		super.lunghezza = 4;
		super.nrotazione = 2;
		super.griglia = griglia;
		super.rand = new Random();
		super.pane = new Pane[lunghezza];
		for (int i = 0; i < super.pane.length; i++) {

			super.pane[i] = new Pane();
			super.pane[i].setBackground(new Background(new BackgroundFill(Color.AQUA, null, null)));
		}

		super.occupied = occupied;

		super.rotazione = rand.nextInt(nrotazione);

		// Coordinate della x
		super.posX = new int[nrotazione][lunghezza];

		super.posX[0][0] = 5;
		super.posX[0][1] = 5;
		super.posX[0][2] = 5;
		super.posX[0][3] = 5;

		super.posX[1][0] = 4;
		super.posX[1][1] = 5;
		super.posX[1][2] = 6;
		super.posX[1][3] = 7;

		// Coordinate della y
		super.posY = new int[nrotazione][lunghezza];

		super.posY[0][0] = 1;
		super.posY[0][1] = 2;
		super.posY[0][2] = 3;
		super.posY[0][3] = 4;

		super.posY[1][0] = 2;
		super.posY[1][1] = 2;
		super.posY[1][2] = 2;
		super.posY[1][3] = 2;

		super.isFalling = true;
	}
}
