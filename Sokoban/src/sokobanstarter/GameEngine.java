package sokobanstarter;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pt.iscte.poo.gui.ImageMatrixGUI;
import pt.iscte.poo.gui.ImageTile;
import pt.iscte.poo.observer.Observed;
import pt.iscte.poo.observer.Observer;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public class GameEngine implements Observer {

  public static final int GRID_HEIGHT = 10;
  public static final int GRID_WIDTH = 10;
  public static final String LEVELS_FOLDER = "levels";
  public static final String SCORES_FILE = "scores.txt";
  public static final int LEVELS_MAX = 6;
  private static final int INITIAL_ENERGY = 100;

  private static GameEngine INSTANCE;
  private ImageMatrixGUI gui;
  private List<ImageTile> tileList;
  private Movable bobcat;
  private List<GameElement> targetList;
  private List<GameElement> gameOverList;
  private List<Movable> overlayList;

  private int currentLevel;
  private String player;
  private int moves;
  private int energy;

  ScoreWriter scoreWriter;

  private GamePlant gamePlant;

  private GameEngine() {
    tileList = new ArrayList<>();
    targetList = new ArrayList<>();
    gameOverList = new ArrayList<>();
    overlayList = new ArrayList<>();

    currentLevel = 6;
    player = "";
    moves = 0;
    energy = INITIAL_ENERGY;
    gamePlant = GamePlant.createPlantWithDimensions(GRID_WIDTH, GRID_HEIGHT);
    scoreWriter = new ScoreWriter(SCORES_FILE);
  }

  public static GameEngine getInstance() {
    if (INSTANCE == null) return INSTANCE = new GameEngine();
    return INSTANCE;
  }

  public void start() {
    gui = ImageMatrixGUI.getInstance();
    gui.setSize(GRID_HEIGHT, GRID_WIDTH);
    gui.registerObserver(this);
    gui.go();

    promptForName();

    loadLevel(currentLevel);
    gui.addImages(tileList);
    gui.setStatusMessage(getStatus());
    gui.update();
  }

  public void reset() {
    gui.clearImages();
    gamePlant.reset();
    tileList = new ArrayList<>();
    targetList = new ArrayList<>();
    gameOverList = new ArrayList<>();
    overlayList = new ArrayList<>();
    moves = 0;
    energy = INITIAL_ENERGY;
    loadLevel(currentLevel);
    gui.addImages(tileList);
    gui.setStatusMessage(getStatus());
    gui.update();
  }

  public int getEnergy() {
    return energy;
  }

  @Override
  public void update(Observed source) {
    int key = gui.keyPressed();

    switch (key) {
      case KeyEvent.VK_LEFT:
        updateStatus(bobcat.move(Direction.LEFT, gamePlant));
        break;
      case KeyEvent.VK_UP:
        updateStatus(bobcat.move(Direction.UP, gamePlant));
        break;
      case KeyEvent.VK_RIGHT:
        updateStatus(bobcat.move(Direction.RIGHT, gamePlant));
        break;
      case KeyEvent.VK_DOWN:
        updateStatus(bobcat.move(Direction.DOWN, gamePlant));
        break;
    }

    gui.setStatusMessage(getStatus());
    gui.update();
    if (checkLevelCompleted()) {
      if (currentLevel < LEVELS_MAX) {
        currentLevel++;
        reset();
      } else {
        gui.setMessage("Congratulations, all levels passed!");
        gui.dispose();
        System.exit(1);
      }
    }

    if (checkGameOver()) {
      gui.setMessage("Game Over!");
      reset();
    }
  }

  private boolean checkLevelCompleted() {
    for (GameElement t : targetList) {
      GameElement g = gamePlant.getElement(t.getPosition());
      if (g == null || g.getElementName() != Name.CRATER) return false;
    }

    String msg = "Moves: " + moves;
    msg += "\n";
    msg += "Top 3: ";
    List<String> scores = this.scoreWriter.write(player, moves, currentLevel);

    for (String s : scores) {
      msg += "\n" + s;
    }

    gui.setMessage(msg);
    gui.setMessage("Level completed!");
    return true;
  }

  private boolean checkGameOver() {
    if (energy <= 0) return true;

    // Check if bobcat fell inside game over element (e.g. holes)
    Point2D bobcatPosition = bobcat.getPosition();
    for (GameElement g : gameOverList) if (
      g.getLayer() > 0 && bobcatPosition.distanceTo(g.getPosition()) == 0
    ) return true;

    // Check if overlay elements displayed (e.g. crater) are less than targets
    int alive = 0;
    for (Movable o : overlayList) if (
      o.isPlayable(gamePlant, targetList)
    ) alive++;

    if (alive < targetList.size()) return true;

    return false;
  }

  private void loadLevel(int level) {
    File file = new File(LEVELS_FOLDER, "level" + level + ".txt");

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      String line;
      int y = 0;

      while ((line = reader.readLine()) != null && y < GRID_HEIGHT) {
        int x = 0;
        for (char c : line.toCharArray()) {
          switch (c) {
            case 'X':
              GameElement target = Factory.create(
                Category.BASIC,
                Name.TARGET,
                x,
                y,
                0,
                0
              );
              tileList.add(target);
              targetList.add(target);
              gamePlant.setElement(x, y, target);
              break;
            case 'B':
              GameElement battery = Factory.create(
                Category.COLLECTABLE,
                Name.BATTERY,
                x,
                y,
                1,
                50,
                false
              );
              tileList.add(battery);
              tileList.add(
                Factory.create(Category.BASIC, Name.FLOOR, x, y, 0, 0)
              );
              gamePlant.setElement(x, y, battery);
              break;
            case 'O':
              GameElement hole = Factory.create(
                Category.VORTEX,
                Name.HOLE,
                x,
                y,
                1,
                0
              );
              tileList.add(hole);
              gamePlant.setElement(x, y, hole);
              gameOverList.add(hole);
              break;
            case 'C':
              GameElement crater = Factory.create(
                Category.MOVABLE,
                Name.CRATER,
                x,
                y,
                2,
                -1
              );
              tileList.add(crater);
              tileList.add(
                Factory.create(Category.BASIC, Name.FLOOR, x, y, 0, 0)
              );
              gamePlant.setElement(x, y, crater);
              overlayList.add((Movable) crater);
              break;
            case '~':
              GameElement stone = Factory.create(
                Category.MOVABLE,
                Name.STONE,
                x,
                y,
                2,
                -1
              );
              tileList.add(stone);
              tileList.add(
                Factory.create(Category.BASIC, Name.FLOOR, x, y, 0, 0)
              );
              gamePlant.setElement(x, y, stone);
              overlayList.add((Movable) stone);
              break;
            case ' ':
              tileList.add(
                Factory.create(Category.BASIC, Name.FLOOR, x, y, 0, 0)
              );
              break;
            case 'E':
              bobcat =
                (Movable) Factory.create(
                  Category.MOVABLE,
                  Name.BOBCAT,
                  x,
                  y,
                  2,
                  -1,
                  true,
                  true,
                  false,
                  true
                );
              tileList.add(bobcat);
              tileList.add(
                Factory.create(Category.BASIC, Name.FLOOR, x, y, 0, 0)
              );
              break;
            case 'M':
              GameElement hammer = Factory.create(
                Category.COLLECTABLE,
                Name.HAMMER,
                x,
                y,
                1,
                0,
                true
              );
              tileList.add(hammer);
              tileList.add(
                Factory.create(Category.BASIC, Name.FLOOR, x, y, 0, 0)
              );
              gamePlant.setElement(x, y, hammer);
              break;
            case 'P':
              GameElement pallet = Factory.create(
                Category.MOVABLE,
                Name.PALLET,
                x,
                y,
                1,
                -1,
                false,
                false,
                true,
                false
              );
              tileList.add(pallet);
              tileList.add(
                Factory.create(Category.BASIC, Name.FLOOR, x, y, 0, 0)
              );
              gamePlant.setElement(x, y, pallet);
              break;
            case '#':
              GameElement wall = Factory.create(
                Category.BOUND,
                Name.WALL,
                x,
                y,
                0,
                0
              );
              tileList.add(wall);
              gamePlant.setElement(x, y, wall);
              break;
            case '%':
              GameElement crackedWall = Factory.create(
                Category.BREAKABLE,
                Name.CRACKED_WALL,
                x,
                y,
                1,
                0
              );
              tileList.add(crackedWall);
              tileList.add(
                Factory.create(Category.BASIC, Name.FLOOR, x, y, 0, 0)
              );
              gamePlant.setElement(x, y, crackedWall);
              break;
            case 'T':
              Map<Integer, Tunnel> tunnelMap = gamePlant.getTunnelMap();
              int id = 1;
              if (tunnelMap.size() > 0) id = -1;
              if (tunnelMap.size() > 1) throw new IllegalArgumentException(
                "Can only have 1 pair of teleport!"
              );

              GameElement teleport = Factory.create(
                Category.TUNNEL,
                Name.TELEPORT,
                x,
                y,
                1,
                -1,
                id
              );
              tileList.add(teleport);
              tunnelMap.put(id, (Tunnel) teleport);
              break;
            default:
              // includes '=' and all other characters
              tileList.add(
                Factory.create(Category.BASIC, Name.EMPTY, x, y, 0, 0)
              );
              break;
          }
          x++;
          if (x == GRID_WIDTH) {
            break;
          }
        }
        y++;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private String getStatus() {
    return (
      "Level " +
      this.currentLevel +
      " - Player " +
      this.player +
      " - Moves " +
      this.moves +
      " Energy: " +
      this.energy
    );
  }

  private void updateStatus(int energyDelta) {
    if (energyDelta != 0) {
      this.energy += energyDelta;
      this.moves++;
    }
  }

  private void promptForName() {
    do {
      this.player = gui.askUser("What is your name?");
      if (this.player == null) {
        gui.dispose();
        System.exit(1);
      }
    } while (this.player.trim().isEmpty());
  }
}
