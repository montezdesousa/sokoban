package sokobanstarter;

public class Breakable extends Basic {

  public Breakable(Name name, int x, int y, int layer) {
    super(name, x, y, layer);
  }

  public void hit(GamePlant gamePlant) {
    gamePlant.setElement(position, null);
    setLayer(-1);
  }
}
