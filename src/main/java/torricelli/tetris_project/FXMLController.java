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
import javafx.scene.text.Font;
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
    private Label highScore;
    
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

			// Ruota il blocco
			blocco.rotate();
		}

		if (code == KeyCode.DOWN && !gameOver) {

			// Conferma il blocco
			punteggio += blocco.moveDown();
			updateLabels();
		}

		if (code == KeyCode.LEFT && !gameOver) {

			// Sposta a sinistra il blocco
			blocco.moveLeft();
		}

		if (code == KeyCode.RIGHT && !gameOver) {

			// Sposta a destra il blocco
			blocco.moveRight();
		}

		if (code == KeyCode.SPACE && !gameOver) {

			punteggio += blocco.confirm();
			updateLabels();
		}
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {

		Font.loadFont(getClass().getResource("/fonts/PressStart2P-Regular.ttf").toExternalForm(), 12);
		mainGrid.setFocusTraversable(true);

		// Registro l'handler UNA SOLA VOLTA
		mainGrid.setOnKeyPressed(this::onKeyPressed);

		// Forzo il focus sulla griglia
		mainGrid.requestFocus();

		// Inizializza le matrici
		gridNodes = new Pane[mainGrid.getRowCount()][mainGrid.getColumnCount()];
		occupied = new boolean[mainGrid.getRowCount()][mainGrid.getColumnCount()];

		// Aggiunge false alla matrice occupied a tutti gli elementi
		for (boolean[] booleans : occupied)
			Arrays.fill(booleans, false);

		game();
	}

	public void game() {

		punteggio = 0;
		linee = 0;
		level = 0;
		speed = 1000;

		updateLabels();
		spawnBlock();

		// Timer che richiama il gameTick
		timeline = new Timeline(new KeyFrame(Duration.millis(speed), e -> gameTick()));

		timeline.setCycleCount(Timeline.INDEFINITE);

		// Timer infinito che continua
		timeline.play();
	}

	private void gameTick() {

		// TODO: schermata game over
		if (gameOver) {

			System.out.println(RED + "[FINE PROGRAMMA]" + YELLOW + " Utente ha perso." + GREEN + " Score: "
					+ punteggio + " High score: " + highscore + RESET);

			timeline.stop();
			System.exit(0);

		} else {

			// Se stiamo animando o cancellando linee, il timer non fa nulla
			if (isAnimating)
				return;

			if (blocco.getIsFalling()) {

				// Se il blocco deve cadere, lo fa cadere
				blocco.fall();

			} else {

				// Quando il blocco si ferma, lo registriamo nella matrice gridNodes
				registerBlockInGrid();

				// Controlliamo se ci sono linee da eliminare
				checkAndClearLines();

			}
		}
	}

	private void registerBlockInGrid() {

		// Prende rotazione, posX, posY e panes del blocco
		int rot = blocco.getRotazione();
		Pane[] panes = blocco.getPanes();
		int[][] px = blocco.getPosX();
		int[][] py = blocco.getPosY();

		for (int i = 0; i < panes.length; i++) {

			// Registra in tutte le matrici necessarie i dati del blocco
			int r = py[rot][i];
			int c = px[rot][i];
			if (r >= 0 && r < mainGrid.getRowCount()) {

				gridNodes[r][c] = panes[i];
				occupied[r][c] = true;
			}
		}
	}

	private void checkAndClearLines() {

		// Controlla per ogni riga se è completa. In questo caso, fa il flash della riga
		// e le elimina
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
				return; // Usciamo per gestire una riga alla volta
			}
		}

		switch (lineeTemp) {

		case 0:

			punteggio += 0;
			break;

		case 1:

			punteggio += 40 * (level + 1);
			break;

		case 2:

			punteggio += 100 * (level + 1);
			break;

		case 3:

			punteggio += 300 * (level + 1);
			break;

		case 4:

			punteggio += 1200 * (level + 1);
			break;
		}

		linee += lineeTemp;
		lineeTemp = 0;

		// Cambia livello ogni 10 linee
		if (linee / 10 > level) {

			changeLevel();
		}

		updateLabels();

		// Se non ci sono righe piene, spawna il prossimo blocco
		spawnBlock();
	}

	private void animateAndRemoveLine(int row) {

		System.out.println(YELLOW + "DEL: " + RED + "Eliminazione riga " + row + RESET);

		isAnimating = true;
		final int r = row;

		// Sequenza di lampeggio: Bianco -> Violetto -> Rimozione
		// Durante l'animazione, animating è true quindi il blocco non spawna fino a
		// quando l'animazione è finita
		Timeline flash = new Timeline(

				new KeyFrame(Duration.ZERO, e -> setRowColor(r, "white")),
				new KeyFrame(Duration.millis(150), e -> setRowColor(r, "darkviolet")),
				new KeyFrame(Duration.millis(300), e -> setRowColor(r, "white")),
				new KeyFrame(Duration.millis(450), e -> {

					removeLine(r);
					shiftDown(r);
					isAnimating = false;
					checkAndClearLines(); // Ricontrolla se ci sono altre linee (una alla volta)
				}));

		flash.play();
	}

	private void setRowColor(int row, String color) {

		// Cambia il colore di tutta la riga, usato per fare il flash
		for (int c = 0; c < mainGrid.getColumnCount(); c++) {

			if (gridNodes[row][c] != null) {

				gridNodes[row][c].setStyle("-fx-background-color: " + color + "; -fx-border-color: black;");
			}
		}
	}

	private void removeLine(int row) {

		// Rimuove la linea solo dalle matrici (Non toglie il disegno)
		for (int c = 0; c < mainGrid.getColumnCount(); c++) {

			mainGrid.getChildren().remove(gridNodes[row][c]);
			gridNodes[row][c] = null;
			occupied[row][c] = false;
		}
	}

	private void shiftDown(int deletedRow) {

		// In questo caso invece usa la logica di blocco.editPosY ma per la riga
		// generalmente
		for (int r = deletedRow - 1; r >= 0; r--) {

			for (int c = 0; c < mainGrid.getColumnCount(); c++) {

				if (gridNodes[r][c] != null) {

					// Sposta il riferimento nella matrice
					gridNodes[r + 1][c] = gridNodes[r][c];
					occupied[r + 1][c] = true;

					// Aggiorna la posizione nel GridPane
					GridPane.setRowIndex(gridNodes[r + 1][c], r + 1);

					// Pulisci la vecchia cella
					gridNodes[r][c] = null;
					occupied[r][c] = false;
				}
			}
		}
	}

	public void spawnBlock() {

		switch (rand.nextInt(7)) {

		case 0:

			blocco = new Punta(mainGrid, occupied);
			break;

		case 1:

			blocco = new Lungo(mainGrid, occupied);
			break;

		case 2:

			blocco = new Quadrato(mainGrid, occupied);
			break;

		case 3:

			blocco = new Lsinistra(mainGrid, occupied);
			break;

		case 4:

			blocco = new Ldestra(mainGrid, occupied);
			break;

		case 5:

			blocco = new Zsinistra(mainGrid, occupied);
			break;

		case 6:

			blocco = new Zdestra(mainGrid, occupied);
			break;
		}

		System.out.println(
				YELLOW + "[SPAWN]" + GREEN + " Spawn di un blocco di tipo: " + blocco.getClass().getName() + RESET);
		blocco.draw();
		gameOver = blocco.checkSpawn();
	}

	public void updateLabels() {

		// Cambio tutti i label
		punteggioScore.setText(" Score: " + punteggio);
		punteggioLines.setText(" Lines: " + linee);
		punteggioSpeed.setText(" Speed: " + speed);
		highScore.setText(" High score: " + highscore);
	}

	public void changeLevel() {

		// Aumento il livello
		level++;

		// Aumento la velocità
		speed -= changeSpeed;

		// Non può diminuire sotto il cento
		if (speed < 100)
			speed = 100;

		// Ricrea la timeline con la nuova velocità
		timeline.stop();

		timeline = new Timeline(new KeyFrame(Duration.millis(speed), e -> gameTick()));
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();

		// Aggiorno i labels
		updateLabels();
	}
}