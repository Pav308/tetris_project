package torricelli.tetris_project;

import java.net.URL;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.ResourceBundle;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import torricelli.blocchi.*;

public class FXMLController implements Initializable {

	private static final Logger log = LoggerFactory.getLogger(FXMLController.class);
	private final Random rand = new Random();
	private Pane[][] gridNodes; // Gestire i riferimenti ai quadratini
	private Blocco blocco;
	private Timeline timeline;
	private Clip musica;

	private int level = 0;
	private int linee = 0;
	private int lineeTemp = 0;
	private int speed = 1000;
	private int prossimoTipo = -1;

	private long punteggio = 0;
	private long[] highScores = new long[5];

	private boolean gameOver = false;
	private boolean isAnimating = false; // Per bloccare il gioco durante i flash
	private boolean[][] occupied;

	private final String GREEN = "\u001B[32m";
	private final String YELLOW = "\u001B[33m";
	private final String RESET = "\u001B[0m";

	@FXML
	private Label highScore1;

	@FXML
	private Label highScore2;

	@FXML
	private Label highScore3;

	@FXML
	private Label highScore4;

	@FXML
	private Label highScore5;

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
	private Label titolo;


	@FXML
	void onKeyPressed(KeyEvent event) {

		KeyCode code = event.getCode();

		if (code == KeyCode.UP && !gameOver) {

			blocco.rotate();
		}

		if (code == KeyCode.DOWN && !gameOver) {
			punteggio += blocco.moveDown();
			updateHighScores(punteggio); // <--- Aggiungi questo
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
			updateHighScores(punteggio); // <--- Aggiungi questo
			updateLabels();
		}

		if (code == KeyCode.SPACE && gameOver) {

			gameStart();
		}

		if (code == KeyCode.Q) {

			EntryPoint.sm.loadMenu();
			gameOver = true;
		}
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {

		mainGrid.setFocusTraversable(true);
		mainGrid.setOnKeyPressed(this::onKeyPressed);
		mainGrid.requestFocus();

		try {

			// Prendo la musica
			AudioInputStream audio = AudioSystem.getAudioInputStream(
					Objects.requireNonNull(getClass().getResource("/audio/Tetris-Theme-Piano.wav")));

			musica = AudioSystem.getClip();
			musica.open(audio);

		} catch (Exception e) {
			log.error("ERRORE CARICAMENTO MUSICA: ", (e));
		}

		gameStart();
	}

	public void gameStart() {

		mainGrid.getChildren().clear();

		level = 0;
		linee = 0;
		lineeTemp = 0;
		speed = 1000;
		prossimoTipo = -1;

		punteggio = 0;

		gameOver = false;
		isAnimating = false; // Per bloccare il gioco durante i flash
		titolo.setText("TETRISFX");
		titolo.setStyle("-fx-font-size: 48px");

		punteggio = 0;
		linee = 0;
		level = 0;
		speed = 1000;
		String rawScores = TextUtility.HighScoreManager.loadHighScoresRaw();
		String[] splitScores = rawScores.split(",");
		for (int i = 0; i < 5; i++) {
			if (i < splitScores.length) {
				highScores[i] = Long.parseLong(splitScores[i].trim());
			} else {
				highScores[i] = 0;
			}
		}

		gridNodes = new Pane[mainGrid.getRowCount()][mainGrid.getColumnCount()];
		occupied = new boolean[mainGrid.getRowCount()][mainGrid.getColumnCount()];

		for (boolean[] booleans : occupied)
			Arrays.fill(booleans, false);

		prossimoTipo = rand.nextInt(7); // Determina il primo "prossimo blocco"
		updateNextBlockGrid(); // Lo disegna nella griglia piccola

		updateLabels();
		spawnBlock();

		timeline = new Timeline(new KeyFrame(Duration.millis(speed), e -> gameTick()));
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();

		startMusic();
	}

	private void gameTick() {

		if (gameOver) {

			stopMusic();

			// Controlla se il punteggio entra in classifica
			updateHighScores(punteggio);

			// Salva i 5 punteggi usando il metodo della TextUtility
			TextUtility.HighScoreManager.saveHighScores(
					highScores[0], highScores[1], highScores[2], highScores[3], highScores[4]
			);

			System.out.println(GREEN + "Classifica Aggiornata!" + RESET);
			timeline.stop();
			titolo.setText("HAI PERSO!");

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

		// Registro il blocco all'interno della griglia
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

		// Controllo se le linee sono piene e in caso le rimuovo
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
				lineeTemp++; // Aumento un valore temporaneo
				return;
			}
		}

		// Aumento il punteggio
		switch (lineeTemp) {

		case 1:

			punteggio += (long) 40 * (level + 1);
			break;

		case 2:

			punteggio += (long) 100 * (level + 1);
			break;

		case 3:

			punteggio += (long) 300 * (level + 1);
			break;

		case 4:

			punteggio += (long) 1200 * (level + 1);
			break;
		}

		// Aumento il numero di linee effettive
		linee += lineeTemp;
		lineeTemp = 0;

		// Cambio il livello se ho tolto 10 linee
		if (linee / 10 > level)
			changeLevel();

		// Cambio high score se è maggiore
		updateHighScores(punteggio);
		updateLabels();
		spawnBlock();
	}

	private boolean checkhighscore(long punt1, long punt2) {
		return (punt1 > punt2);
	}
	private void updateHighScores(long currentScore) {
		int currentIdx = -1;

		// 1. Controlla se il giocatore è già presente in classifica (magari dal tick precedente)
		for (int i = 0; i < highScores.length; i++) {
			// Usiamo un trucco: se il punteggio è uguale, sappiamo che è "lui" che sta salendo
			if (currentScore >= highScores[i]) {
				currentIdx = i;
				break;
			}
		}

		if (currentIdx != -1) {
			// Se il punteggio è già il primo e sta solo aumentando, aggiorna solo il valore
			if (currentIdx == 0) {
				highScores[0] = currentScore;
			} else {
				// Se sta superando qualcuno sopra di lui, effettua lo shift
				// Shiftiamo solo se il punteggio è effettivamente maggiore del superiore
				if (currentScore > highScores[currentIdx - 1]) {
					long temp = highScores[currentIdx - 1];
					highScores[currentIdx - 1] = currentScore;
					highScores[currentIdx] = temp;
				} else {
					highScores[currentIdx] = currentScore;
				}
			}
		}
	}

	private void animateAndRemoveLine(int row) {

		isAnimating = true;
		final int r = row;

		Timeline flash = new Timeline(new KeyFrame(Duration.ZERO, e -> setRowColor(r, "white")),
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

			// Rimuovo i pane alla linea corrente
			mainGrid.getChildren().remove(gridNodes[row][c]);
			gridNodes[row][c] = null;
			occupied[row][c] = false;
		}
	}

	private void shiftDown(int deletedRow) {

		// r = riga
		// c = colonna
		for (int r = deletedRow - 1; r >= 0; r--) {

			for (int c = 0; c < mainGrid.getColumnCount(); c++) {

				if (gridNodes[r][c] != null) {

					// Sposto tutti i nodi in basso di uno cambiando i riferimenti e gli indici
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

		int tipoAttuale = prossimoTipo;

		switch (tipoAttuale) {

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

		// Genera il prossimo e aggiorna la preview
		prossimoTipo = rand.nextInt(7);
		updateNextBlockGrid();

		System.out.println(
				YELLOW + "[SPAWN]" + GREEN + " Spawn blocco tipo: " + blocco.getClass().getSimpleName() + RESET);
		blocco.draw();
		gameOver = blocco.checkSpawn();
	}

	public void updateLabels() {

		// Aggiorno tutti le scritte di cui ho bisogno
		punteggioScore.setText(" Score: " + punteggio);
		punteggioLines.setText(" Lines: " + linee);
		punteggioSpeed.setText(" Speed: " + speed + " ms");
		highScore1.setText(" 1st: " + highScores[0]);
		highScore2.setText(" 2nd: " + highScores[1]);
		highScore3.setText(" 3rd: " + highScores[2]);
		highScore4.setText(" 4th: " + highScores[3]);
		highScore5.setText(" 5th: " + highScores[4]);
	}

	public void changeLevel() {

		level++; // Aumento il livello

		// La velocità diminuisce del venti per cento
		if (speed >= 100)
			speed -= speed * 20 / 100;

		// Aggiorno la velocità a livello effettivo
		timeline.stop();
		timeline = new Timeline(new KeyFrame(Duration.millis(speed), e -> gameTick()));
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();
		updateLabels();
	}

	private void updateNextBlockGrid() {

		nextBlockGrid.getChildren().clear();

		int[][] posXnext = {};
		int[][] posYnext = {};
		javafx.scene.paint.Color colorePezzo = javafx.scene.paint.Color.TRANSPARENT;

		// Definiamo le coordinate e il colore esatto per ogni pezzo
		switch (prossimoTipo) {

		case 0: // Punta

			posXnext = new int[][] { { 1, 0, 1, 2 } };
			posYnext = new int[][] { { 0, 1, 1, 1 } };
			colorePezzo = javafx.scene.paint.Color.VIOLET;
			break;

		case 1: // Lungo

			posXnext = new int[][] { { 1, 1, 1, 1 } };
			posYnext = new int[][] { { 0, 1, 2, 3 } };
			colorePezzo = javafx.scene.paint.Color.CYAN;
			break;

		case 2: // Quadrato

			posXnext = new int[][] { { 1, 2, 1, 2 } };
			posYnext = new int[][] { { 1, 1, 2, 2 } };
			colorePezzo = javafx.scene.paint.Color.YELLOW;
			break;

		case 3: // L-Sinistra

			posXnext = new int[][] { { 1, 2, 2, 2 } };
			posYnext = new int[][] { { 0, 0, 1, 2 } };
			colorePezzo = javafx.scene.paint.Color.ORANGE;
			break;

		case 4: // L-Destra

			posXnext = new int[][] { { 1, 2, 1, 1 } };
			posYnext = new int[][] { { 0, 0, 1, 2 } };
			colorePezzo = javafx.scene.paint.Color.BLUE;
			break;

		case 5: // Z-Sinistra

			posXnext = new int[][] { { 1, 1, 2, 2 } };
			posYnext = new int[][] { { 0, 1, 1, 2 } };
			colorePezzo = javafx.scene.paint.Color.RED;
			break;

		case 6: // Z-Destra

			posXnext = new int[][] { { 2, 2, 1, 1 } };
			posYnext = new int[][] { { 0, 1, 1, 2 } };
			colorePezzo = javafx.scene.paint.Color.GREEN;
			break;
		}

		// Creiamo il background usando lo stesso metodo della classe Punta
		javafx.scene.layout.Background bg = new javafx.scene.layout.Background(
				new javafx.scene.layout.BackgroundFill(colorePezzo, null, null));

		for (int i = 0; i < 4; i++) {

			Pane p = new Pane();
			p.setBackground(bg); // Applichiamo l'oggetto Background reale
			nextBlockGrid.add(p, posXnext[0][i], posYnext[0][i]);
		}
	}

	private void startMusic() {

		// Faccio partire la musica se esiste
		if (musica != null) {

			musica.setFramePosition(0);
			musica.loop(Clip.LOOP_CONTINUOUSLY); // loop infinito
			musica.start();
		}
	}

	private void stopMusic() {

		// Fermo la musica
		if (musica != null && musica.isRunning())
			musica.stop();
	}
}