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
	//Abbassa di uno la posizione Y di ogni blocco per ogni rotazione possibile
	public void editPosY(){
		for(int i = 0 ; i<posY.length;i++){
			for(int j = 0 ; j<posY[i].length;j++){
				posY[i][j] += 1;
			}
		}
	}

	// Metodo per far cadere il blocco
	public void fall() {

		// Controllo che il blocco non cada sotto la griglia
		for (int i = 0; i < posY[rotazione].length; i++)
			if ((posY[rotazione][i] + 1) >= griglia.getRowCount()) {
				isFalling = false;
				return;
			}

		// Se il blocco sta cadendo
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
		boolean occupation = false; //controlla se vuoto
		for(int i = 0;i<posY[rotazione].length;i++){
			if(occupied[posY[toRotate][i]][posX[toRotate][i]]){
				occupation = true;
				System.out.println("[!] Rotate: Tentato, blocco non libero.");
			}
		}
		if(!occupation){
			// TODO togli sysout
			System.out.println("tolto blocco con rotazione "+rotazione+" aggiunto con rotazione "+toRotate);
			isFalling = false;
			dispose();
			rotazione = toRotate;
			draw();
			isFalling = true;
		}
	}
	// TODO: finire i tre metodi quando fatti togli sysout
	public void confirm() {
		System.out.println("premuto freccia basso");
	}
	
	public void moveLeft() {
		System.out.println("premuto freccia sx");
	}
	
	public void moveRight() {
		System.out.println("premuto freccia dx");
	}
	
	public boolean getIsFalling() {

		return this.isFalling;
	}
}
