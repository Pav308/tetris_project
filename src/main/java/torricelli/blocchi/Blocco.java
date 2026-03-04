package torricelli.blocchi;

import java.util.Random;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class Blocco {

	protected GridPane griglia;
	protected Random rand;
	protected Pane[] pane;

	protected int lunghezza;
	protected int rotazione;
	protected int[][] posX;
	protected int[][] posY;
	protected boolean isFalling;

	// Metodo per "disegnare" il blocco
	public void draw() {

		for (int i = 0; i < pane.length; i++)
			griglia.add(pane[i], posX[rotazione][i], posY[rotazione][i]);
	}

	// Metodo per "cancellare" il blocco
	public void dispose() {

		griglia.getChildren().removeAll(pane);
	}

	// Metodo per far cadere il blocco
	public void fall() {

		for (int i = 0; i < lunghezza; i++)
			if (posY[rotazione][i]++ > griglia.getRowCount())
				dispose();

		if (isFalling) {

			for (int i = 0; i < posY[rotazione].length; i++)
				posY[rotazione][i]++;
		}
	}
}
