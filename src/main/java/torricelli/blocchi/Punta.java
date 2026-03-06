package torricelli.blocchi;

import java.util.Random;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Punta extends Blocco {

	public Punta(GridPane griglia, boolean[][] occupied) {

		super.lunghezza = 4;
		super.nrotazione = 4;
		super.griglia = griglia;
		super.rand = new Random();
		super.pane = new Pane[lunghezza];
		for (int i = 0; i < super.pane.length; i++) {

			super.pane[i] = new Pane();
			super.pane[i].setBackground(new Background(new BackgroundFill(Color.VIOLET, null, null)));
		}
		
		super.occupied = occupied;
		
		super.rotazione = rand.nextInt(nrotazione);

		// Coordinate della x
		super.posX = new int[nrotazione][lunghezza];

		super.posX[0][0] = 5;
		super.posX[0][1] = 4;
		super.posX[0][2] = 5;
		super.posX[0][3] = 6;

		super.posX[1][0] = 5;
		super.posX[1][1] = 4;
		super.posX[1][2] = 5;
		super.posX[1][3] = 5;
		
		super.posX[2][0] = 4;
		super.posX[2][1] = 5;
		super.posX[2][2] = 6;
		super.posX[2][3] = 5;
		
		super.posX[3][0] = 5;
		super.posX[3][1] = 5;
		super.posX[3][2] = 6;
		super.posX[3][3] = 5;

		// Coordinate della y
		super.posY = new int[nrotazione][lunghezza];

		super.posY[0][0] = 1;
		super.posY[0][1] = 2;
		super.posY[0][2] = 2;
		super.posY[0][3] = 2;
		
		super.posY[1][0] = 1;
		super.posY[1][1] = 2;
		super.posY[1][2] = 2;
		super.posY[1][3] = 3;
		
		super.posY[2][0] = 2;
		super.posY[2][1] = 2;
		super.posY[2][2] = 2;
		super.posY[2][3] = 3;
		
		super.posY[3][0] = 1;
		super.posY[3][1] = 2;
		super.posY[3][2] = 2;
		super.posY[3][3] = 3;

		super.isFalling = true;
	}
}
