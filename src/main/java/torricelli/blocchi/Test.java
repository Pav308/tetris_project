package torricelli.blocchi;

import java.util.Random;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Test extends Blocco {

	public Test(GridPane griglia) {

		super.lunghezza = 1;
		super.griglia = griglia;
		super.rand = new Random();
		super.pane = new Pane[lunghezza];
		for(int i = 0; i < super.pane.length; i++) {
			
			super.pane[0] = new Pane();
			super.pane[0].setBackground(new Background(new BackgroundFill(Color.BLUE, null, null)));
		}
		
		super.rotazione = 0;

		// Coordinate della x
		super.posX = new int[lunghezza][lunghezza];

		super.posX[0][0] = 5;
		
		// Coordinate della y
		super.posY = new int[lunghezza][lunghezza];

		super.posY[0][0] = 1;

		super.isFalling = true;
	}
}
