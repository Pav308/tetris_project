package torricelli.tetris_project;

import java.net.URL;
import java.util.Arrays;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import torricelli.blocchi.*;

public class FXMLController implements Initializable {

	private final Random rand = new Random();
	private Pane[][] gridNodes; // Per gestire i riferimenti ai quadratini
	private Blocco blocco;
	private Timeline timeline;

	private long punteggio = 0;

	private int level = 0;
	private int linee = 0;
	private int lineeTemp = 0;
	private int speed = 1000;
	private int changeSpeed = 200;
	private int highscore;

	// --- VARIABILI AGGIUNTE ---
	private int prossimoTipo = -1; // Memorizza l'indice del blocco che arriverà dopo
	// ---------------------------

	private boolean gameOver = false;
	private boolean isAnimating = false; // Per bloccare il gioco durante i flash
	private boolean[][] occupied;

	private final String GREEN = "\u001B[32m";
	private final String YELLOW = "\u001B[33m";
	private final String RED = "\u001B[31m";
	private final String RESET = "\u001B[0m";

	@FXML
	private GridPane mainGrid;
	@FXML
	private GridPane nextBlockGrid;

	@FXML
	private Label punteggioLines;

	@FXML
	private Label punteggioScore;

	@FXML
	private Label punteggioSpeed;

	@FXML
	void onKeyPressed(KeyEvent event) {

		KeyCode code = event.getCode();

		if (code == KeyCode.UP && !gameOver) {
			blocco.rotate();
		}

		if (code == KeyCode.DOWN && !gameOver) {
			punteggio += blocco.moveDown();
			updateLabels();
		}

		if (code == KeyCode.LEFT && !gameOver) {
			blocco.moveLeft();
		}

		if (code == KeyCode.RIGHT && !gameOver) {
			blocco.moveRight();
		}

		if (code == KeyCode.SPACE && !gameOver) {
			punteggio += blocco.confirm();
			updateLabels();
		}
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {

		mainGrid.setFocusTraversable(true);
		mainGrid.setOnKeyPressed(this::onKeyPressed);
		mainGrid.requestFocus();

		gridNodes = new Pane[mainGrid.getRowCount()][mainGrid.getColumnCount()];
		occupied = new boolean[mainGrid.getRowCount()][mainGrid.getColumnCount()];

		for (boolean[] booleans : occupied)
			Arrays.fill(booleans, false);

		// --- LOGICA AGGIUNTA ---
		prossimoTipo = rand.nextInt(7); // Determina il primo "prossimo blocco"
		updateNextBlockGrid();          // Lo disegna nella griglia piccola
		// -----------------------

		game();
	}

	public void game() {

		punteggio = 0;
		linee = 0;
		level = 0;
		speed = 1000;

		updateLabels();
		spawnBlock();

		timeline = new Timeline(new KeyFrame(Duration.millis(speed), e -> gameTick()));
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();
	}

	private void gameTick() {

		if (gameOver) {
			System.out.println(RED + "[FINE PROGRAMMA]" + YELLOW + " Utente ha perso." + GREEN + " Score: "
					+ punteggio + " High score: " + highscore + RESET);
			timeline.stop();
			System.exit(0);
		} else {
			if (isAnimating)
				return;

			if (blocco.getIsFalling()) {
				blocco.fall();
			} else {
				registerBlockInGrid();
				checkAndClearLines();
			}
		}
	}

	private void registerBlockInGrid() {
		int rot = blocco.getRotazione();
		Pane[] panes = blocco.getPanes();
		int[][] px = blocco.getPosX();
		int[][] py = blocco.getPosY();

		for (int i = 0; i < panes.length; i++) {
			int r = py[rot][i];
			int c = px[rot][i];
			if (r >= 0 && r < mainGrid.getRowCount()) {
				gridNodes[r][c] = panes[i];
				occupied[r][c] = true;
			}
		}
	}

	private void checkAndClearLines() {
		for (int r = mainGrid.getRowCount() - 1; r >= 0; r--) {
			boolean full = true;
			for (int c = 0; c < mainGrid.getColumnCount(); c++) {
				if (!occupied[r][c]) {
					full = false;
					break;
				}
			}

			if (full) {
				animateAndRemoveLine(r);
				lineeTemp++;
				return;
			}
		}

		switch (lineeTemp) {
			case 1: punteggio += 40 * (level + 1); break;
			case 2: punteggio += 100 * (level + 1); break;
			case 3: punteggio += 300 * (level + 1); break;
			case 4: punteggio += 1200 * (level + 1); break;
		}

		linee += lineeTemp;
		lineeTemp = 0;

		if (linee / 10 > level) {
			changeLevel();
		}

		updateLabels();
		spawnBlock();
	}

	private void animateAndRemoveLine(int row) {
		isAnimating = true;
		final int r = row;

		Timeline flash = new Timeline(
				new KeyFrame(Duration.ZERO, e -> setRowColor(r, "white")),
				new KeyFrame(Duration.millis(150), e -> setRowColor(r, "darkviolet")),
				new KeyFrame(Duration.millis(300), e -> setRowColor(r, "white")),
				new KeyFrame(Duration.millis(450), e -> {
					removeLine(r);
					shiftDown(r);
					isAnimating = false;
					checkAndClearLines();
				}));

		flash.play();
	}

	private void setRowColor(int row, String color) {
		for (int c = 0; c < mainGrid.getColumnCount(); c++) {
			if (gridNodes[row][c] != null) {
				gridNodes[row][c].setStyle("-fx-background-color: " + color + "; -fx-border-color: black;");
			}
		}
	}

	private void removeLine(int row) {
		for (int c = 0; c < mainGrid.getColumnCount(); c++) {
			mainGrid.getChildren().remove(gridNodes[row][c]);
			gridNodes[row][c] = null;
			occupied[row][c] = false;
		}
	}

	private void shiftDown(int deletedRow) {
		for (int r = deletedRow - 1; r >= 0; r--) {
			for (int c = 0; c < mainGrid.getColumnCount(); c++) {
				if (gridNodes[r][c] != null) {
					gridNodes[r + 1][c] = gridNodes[r][c];
					occupied[r + 1][c] = true;
					GridPane.setRowIndex(gridNodes[r + 1][c], r + 1);
					gridNodes[r][c] = null;
					occupied[r][c] = false;
				}
			}
		}
	}

	public void spawnBlock() {
		// --- MODIFICATA PER USARE PROSSIMOTIPO ---
		int tipoAttuale = prossimoTipo;

		switch (tipoAttuale) {
			case 0: blocco = new Punta(mainGrid, occupied); break;
			case 1: blocco = new Lungo(mainGrid, occupied); break;
			case 2: blocco = new Quadrato(mainGrid, occupied); break;
			case 3: blocco = new Lsinistra(mainGrid, occupied); break;
			case 4: blocco = new Ldestra(mainGrid, occupied); break;
			case 5: blocco = new Zsinistra(mainGrid, occupied); break;
			case 6: blocco = new Zdestra(mainGrid, occupied); break;
		}

		// Genera il prossimo e aggiorna la preview
		prossimoTipo = rand.nextInt(7);
		updateNextBlockGrid();
		// ------------------------------------------

		System.out.println(YELLOW + "[SPAWN]" + GREEN + " Spawn blocco tipo: " + blocco.getClass().getSimpleName() + RESET);
		blocco.draw();
		gameOver = blocco.checkSpawn();
	}

	public void updateLabels() {
		punteggioScore.setText("Score: " + punteggio);
		punteggioLines.setText("Lines: " + linee);
		punteggioSpeed.setText("Speed: " + speed);
	}

	public void changeLevel() {
		level++;
		speed -= changeSpeed;
		if (speed < 100) speed = 100;

		timeline.stop();
		timeline = new Timeline(new KeyFrame(Duration.millis(speed), e -> gameTick()));
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();
		updateLabels();
	}

	// ==========================================================
	// METODI AGGIUNTI PER LA GESTIONE DELLA NEXT BLOCK GRID
	// ==========================================================

	private void updateNextBlockGrid() {
		nextBlockGrid.getChildren().clear();

		int[][] posXnext = {};
		int[][] posYnext = {};
		javafx.scene.paint.Color colorePezzo = javafx.scene.paint.Color.TRANSPARENT;

		// Definiamo le coordinate e il colore esatto per ogni pezzo
		switch (prossimoTipo) {
			case 0: // Punta
				posXnext = new int[][]{{1, 0, 1, 2}}; posYnext = new int[][]{{0, 1, 1, 1}};
				colorePezzo = javafx.scene.paint.Color.VIOLET;
				break;
			case 1: // Lungo
				posXnext = new int[][]{{1, 1, 1, 1}}; posYnext = new int[][]{{0, 1, 2, 3}};
				colorePezzo = javafx.scene.paint.Color.CYAN;
				break;
			case 2: // Quadrato
				posXnext = new int[][]{{1, 2, 1, 2}}; posYnext = new int[][]{{1, 1, 2, 2}};
				colorePezzo = javafx.scene.paint.Color.YELLOW;
				break;
			case 3: // L-Sinistra
				posXnext = new int[][]{{1, 2, 1, 1}}; posYnext = new int[][]{{0, 0, 1, 2}};
				colorePezzo = javafx.scene.paint.Color.ORANGE;
				break;
			case 4: // L-Destra
				posXnext = new int[][]{{1, 2, 2, 2}}; posYnext = new int[][]{{0, 0, 1, 2}};
				colorePezzo = javafx.scene.paint.Color.BLUE;
				break;
			case 5: // Z-Sinistra
				posXnext = new int[][]{{1, 1, 2, 2}}; posYnext = new int[][]{{0, 1, 1, 2}};
				colorePezzo = javafx.scene.paint.Color.RED;
				break;
			case 6: // Z-Destra
				posXnext = new int[][]{{2, 2, 1, 1}}; posYnext = new int[][]{{0, 1, 1, 2}};
				colorePezzo = javafx.scene.paint.Color.GREEN;
				break;
		}

		// Creiamo il background usando lo stesso metodo della classe Punta
		javafx.scene.layout.Background bg = new javafx.scene.layout.Background(
				new javafx.scene.layout.BackgroundFill(colorePezzo, null, null)
		);

		for (int i = 0; i < 4; i++) {
			Pane p = new Pane();
			p.setBackground(bg); // Applichiamo l'oggetto Background reale

			// Se vuoi togliere i bordi bianchi assicurati che hgap e vgap della grid siano 0
			nextBlockGrid.add(p, posXnext[0][i], posYnext[0][i]);
		}
	}
}