package torricelli.tetris_project;

import java.io.*;

public class TextUtility {
    public static class HighScoreManager {
        private static final String FILE_NAME = "highscore.txt";

        // Salva i punteggi: accetta un array di long per comodità
        public static void saveHighScores(long... scores) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
                for (int i = 0; i < Math.min(scores.length, 5); i++) {
                    writer.write(String.valueOf(scores[i]));
                    writer.newLine();
                }
            } catch (IOException e) {
                System.err.println("Errore nel salvataggio: " + e.getMessage());
            }
        }

        // Legge tutti i punteggi e li restituisce come un'unica stringa separata da virgole
        public static String loadHighScoresRaw() {
            File file = new File(FILE_NAME);
            if (!file.exists()) return "0,0,0,0,0";

            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                int count = 0;
                while ((line = reader.readLine()) != null && count < 5) {
                    sb.append(line.trim()).append(",");
                    count++;
                }
            } catch (IOException e) {
                System.err.println("Errore nella lettura: " + e.getMessage());
            }

            // Rimuove l'ultima virgola se presente
            String result = sb.toString();
            return result.endsWith(",") ? result.substring(0, result.length() - 1) : result;
        }
    }
}