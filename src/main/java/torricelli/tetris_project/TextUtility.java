package torricelli.tetris_project;

import java.io.*;

public class TextUtility {
    public static class HighScoreManager {
        private static final String FILE_NAME = "highscore.txt";

        // Salva il punteggio sul file
        public static void saveHighScore(long score) {

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {

                writer.write(String.valueOf(score));

            } catch (IOException e) {

                System.err.println("Errore nel salvataggio dell'high score: " + e.getMessage());
            }
        }

        // Legge il punteggio dal file
        public static int loadHighScore() {

            File file = new File(FILE_NAME);

            if (!file.exists()) return 0; // Se il file non esiste, l'high score è 0

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

                String line = reader.readLine();

                if (line != null) {

                    return Integer.parseInt(line.trim());
                }

            } catch (IOException | NumberFormatException e) {

                System.err.println("Errore nella lettura dell'high score: " + e.getMessage());
            }

            return 0;
        }
    }
}
