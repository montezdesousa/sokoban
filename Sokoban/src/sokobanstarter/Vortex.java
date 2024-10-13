package sokobanstarter;

public class Vortex extends Basic {

  private int energyDelta = 0;  // Change of energy after interacting with this element

  public Vortex(Name name, int x, int y, int layer, int energyDelta) {
    super(name, x, y, layer);
    this.energyDelta = energyDelta;
  }

  public int destroy(Movable gameElement, GamePlant gamePlant) {
    gameElement.isActive = false;
    gameElement.setLayer(-1);
    return energyDelta;
  }
}
