package sokobanstarter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pt.iscte.poo.utils.Point2D;

public class GamePlant {

  private List<List<GameElement>> plant;
  private Map<Integer, Tunnel> tunnelMap;

  private GamePlant(List<List<GameElement>> plant) {
    this.plant = plant;
    this.tunnelMap = new HashMap<>();
  }

  public static GamePlant createPlantWithDimensions(int width, int height) {
    List<List<GameElement>> initializedPlant = new ArrayList<>();

    for (int y = 0; y < height; y++) {
      List<GameElement> row = new ArrayList<>();
      for (int x = 0; x < width; x++) {
        row.add(null);
      }
      initializedPlant.add(row);
    }

    return new GamePlant(initializedPlant);
  }

  public void reset() {
    for (List<GameElement> row : plant) {
      for (int x = 0; x < row.size(); x++) {
        row.set(x, null);
      }
    }
    this.tunnelMap = new HashMap<>();
  }

  public GameElement getElement(int x, int y) {
    return this.plant.get(y).get(x);
  }

  public GameElement getElement(Point2D position) {
    return this.plant.get(position.getY()).get(position.getX());
  }

  public void setElement(int x, int y, GameElement gameElement) {
    this.plant.get(y).set(x, gameElement);
  }

  public void setElement(Point2D position, GameElement gameElement) {
    int x = position.getX();
    int y = position.getY();
    this.plant.get(y).set(x, gameElement);
  }

  public int getWidth() {
    return plant.get(0).size();
  }

  public int getHeight() {
    return plant.size();
  }

  public Map<Integer, Tunnel> getTunnelMap() {
    return this.tunnelMap;
  }

  public boolean isValid(Point2D position) {
    int x = position.getX();
    int y = position.getY();
    return x >= 0 && x < this.getWidth() && y >= 0 && y < this.getHeight();
  }
}
