package torricelli.blocchi;

import java.util.Random;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class Blocco {

	protected GridPane griglia;
	protected Random rand;
	protected Pane[] pane;
	protected boolean[][] occupied;

	protected int lunghezza;
	protected int nrotazione;
	protected int rotazione;
	protected int[][] posX;
	protected int[][] posY;
	protected boolean isFalling;
	protected final String RED = "\u001B[31m";
	protected final String GREEN = "\u001B[32m";
	protected final String YELLOW = "\u001B[33m";
	protected final String RESET = "\u001B[0m";

	// Metodo per "disegnare" il blocco
	public void draw() {
		for (int i = 0; i < pane.length; i++)
			griglia.add(pane[i], posX[rotazione][i], posY[rotazione][i]);
	}

	// Metodo per "cancellare" il blocco
	public void dispose() {
		griglia.getChildren().removeAll(pane);
	}

	// Abbassa di uno la posizione Y di ogni blocco per ogni rotazione possibile
	public void editPosY() {
		for (int i = 0; i < posY.length; i++) {
			for (int j = 0; j < posY[i].length; j++) {
				posY[i][j] += 1;
			}
		}
	}

	public void editPosX(boolean direction) {
		// 1. DEFINIAMO L'OFFSET (Spostamento)
		// Invece del ternario, usiamo un if per decidere se sommare 1 o -1
		int offset;
		if (direction) {
			offset = -1; // Sinistra
		} else {
			offset = 1;  // Destra
		}

		// 2. FASE DI CONTROLLO (POSSO MUOVERMI?)
		// Cicliamo su ogni quadratino della rotazione attuale
		for (int j = 0; j < posX[rotazione].length; j++) {
			int nextX = posX[rotazione][j] + offset;
			int currentY = posY[rotazione][j];

			// Controllo se esce dai bordi della griglia
			if (nextX < 0 || nextX >= griglia.getColumnCount()) {
				System.out.println(RED+"MOVE: Non permesso, fuori griglia.\nMOVE: "+getBlockCoords());
				return; // Interrompe il metodo: il movimento è vietato
			}

			// Controllo se la cella di destinazione è già occupata
			if (occupied[currentY][nextX]) {
				System.out.println(RED+"MOVE: Non permesso, occupato.\nMOVE: "+getBlockCoords());
				return; // Interrompe il metodo: c'è un ostacolo
			}
		}

		// 3. FASE DI AGGIORNAMENTO (ESEGUIAMO IL MOVIMENTO)
		// Se siamo arrivati qui, significa che il controllo sopra non ha mai colpito il "return"

		dispose(); // Cancelliamo il blocco dalla vecchia posizione grafica

		// Aggiorniamo le coordinate X per TUTTE le rotazioni possibili del pezzo
		for (int i = 0; i < posX.length; i++) {
			for (int j = 0; j < posX[i].length; j++) {
				posX[i][j] = posX[i][j] + offset;
			}
		}
		draw(); // Ridisegniamo il blocco nella nuova posizione
	}

	// Metodo per far cadere il blocco
	public void fall() {

		// Controllo che il blocco non cada sotto la griglia e che le celle non sono
		// occupate
		for (int i = 0; i < posY[rotazione].length; i++)

			if ((posY[rotazione][i] + 1) >= griglia.getRowCount()
					|| occupied[posY[rotazione][i] + 1][posX[rotazione][i]]) {
				System.out.println(RED+"[!] DOWN: Non permesso, fuori griglia.\n"+getBlockCoords());
				isFalling = false;

				// Imposto che le celle sono occupate
				for (int j = 0; j < posY[rotazione].length; j++)
					occupied[posY[rotazione][j]][posX[rotazione][j]] = true;
				System.out.println(RED+"[!] DOWN: Non permesso, celle occupate.\n"+getBlockCoords());
				return;
			}

		// Se il blocco sta cadendo cadendo
		if (isFalling) {

			// Cancello il blocco vecchio
			dispose();

			// Aumento la posizione
			editPosY();

			// Disegno il nuovo blocco
			draw();
		}
	}

	public void rotate() {

		int toRotate = (rotazione + 1) % posX.length;
		boolean occupation = false; // controlla se vuoto
		for (int i = 0; i < posY[rotazione].length; i++) {
			if(posX[toRotate][i]<0 || posX[toRotate][i]>= griglia.getColumnCount()){
				System.out.println(RED+"[!] ROTATE: "+YELLOW+rotazione+" -> "+rotazione+" (fuori griglia)");
			}
			if (occupied[posY[toRotate][i]][posX[toRotate][i]]) {
				occupation = true;
				System.out.println(RED+"[!] ROTATE: "+YELLOW+rotazione+" -> "+rotazione+" (occupato)");
			}
		}

		if (!occupation) {

			// TODO togli sysout
			System.out.println(YELLOW+"ROTATE: " +GREEN+ rotazione + " -> " + toRotate+RESET);
			isFalling = false;
			dispose();
			rotazione = toRotate;
			draw();
			isFalling = true;
		}
	}

	// TODO: finire i tre metodi quando fatti togli sysout
	public void moveDown() {
		System.out.println(GREEN+"DOWN: "+getBlockCoords());
		fall();
	}

	public void confirm() {
		for(int i = posY[rotazione][3]; i <= griglia.getRowCount(); i++)
			fall();
		System.out.println(GREEN+"CONFIRM: "+getBlockCoords());
	}

	public void moveLeft() {
		System.out.println(GREEN+"LEFT: "+getBlockCoords());
		editPosX(true);
	}

	public void moveRight() {
		System.out.println(GREEN+"RIGHT: "+getBlockCoords());
		editPosX(false);
	}

	public boolean getIsFalling() {
		return this.isFalling;
	}
	public String getBlockCoords(){
		return YELLOW+"[POSIZIONE BLOCCO]"+GREEN+" X:"+posX[rotazione][0]+" Y:"+posX[rotazione][0]+RESET;
	}
}
