package sokobanstarter;

public class Collectable extends Basic {

  private int energyDelta = 0;  // Change of energy after interacting with this element
  private boolean givesBoost;  // Gives boost to break breakable elements

  public Collectable(
    Name name,
    int x,
    int y,
    int layer,
    int energyDelta,
    boolean givesBoost
  ) {
    super(name, x, y, layer);
    this.energyDelta = energyDelta;
    this.givesBoost = givesBoost;
  }

  public int collect(Movable gameElement) {
    setLayer(-1);
    if (givesBoost) gameElement.canBreak = true;
    return energyDelta;
  }
}
