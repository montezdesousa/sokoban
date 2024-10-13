package sokobanstarter;

import java.io.IOException;
import java.nio.file.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ScoreWriter {

  private static final int TOP_N_SCORES = 3;
  private String filePath;

  public ScoreWriter(String filePath) {
    this.filePath = filePath;
  }

  List<String> write(String player, int moves, int currentLevel) {
    try {
      String score = currentLevel + "," + player + "," + moves;

      if (!Files.exists(Path.of(filePath))) {
        Files.createFile(Path.of(filePath));
      }

      List<String> existingScores = Files
        .lines(Path.of(filePath))
        .skip(1) // Skip the header line
        .collect(Collectors.toList());

      // Filter scores for the current level
      List<String> levelScores = existingScores
        .stream()
        .filter(line -> Integer.parseInt(line.split(",")[0]) == currentLevel)
        .collect(Collectors.toList());

      // Add the new score to the list
      levelScores.add(score);

      // Sort the scores based on moves in ascending order
      Collections.sort(
        levelScores,
        Comparator.comparingInt(line -> Integer.parseInt(line.split(",")[2]))
      );

      // Keep only the top N scores (TOP_N_SCORES)
      List<String> topScores = levelScores.subList(
        0,
        Math.min(levelScores.size(), TOP_N_SCORES)
      );

      // Update the existing scores with the top scores
      existingScores.removeAll(levelScores);
      existingScores.addAll(topScores);

      Collections.sort(
        existingScores,
        Comparator.comparingInt(line -> Integer.parseInt(line.split(",")[0]))
      );

      // Write the updated scores back to the file
      Files.write(
        Path.of(filePath),
        Collections.singleton("level,player,moves")
      );
      Files.write(Path.of(filePath), existingScores, StandardOpenOption.APPEND);
      return topScores;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
