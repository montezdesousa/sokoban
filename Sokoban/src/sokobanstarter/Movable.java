package sokobanstarter;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public class Movable extends Basic {

  private int energyDelta = 0; // Change of energy after interacting with this element
  private boolean canCollect = false;
  private boolean canPush = false;
  private boolean canTap = false;
  protected boolean canBreak = false;
  protected boolean isActive = true;
  private boolean updateImage = false;

  public Movable(
    Name name,
    int x,
    int y,
    int layer,
    int energyDelta,
    boolean canCollect,
    boolean canPush,
    boolean canTap,
    boolean updateImage
  ) {
    super(name, x, y, layer);
    this.energyDelta = energyDelta;
    this.canCollect = canCollect;
    this.canPush = canPush;
    this.canTap = canTap;
    this.updateImage = updateImage;
  }

  public Movable(Name name, int x, int y, int layer, int energyDelta) {
    super(name, x, y, layer);
    this.energyDelta = energyDelta;
  }

  public int move(Direction direction, GamePlant gamePlant) {
    Point2D newPosition = position.plus(direction.asVector());

    if (updateImage) updateImage(direction);

    if (gamePlant.isValid(newPosition)) {
      GameElement next = gamePlant.getElement(newPosition);

      String category;
      int partial = 0; // Partial energy change from interactions

      if (next == null) category = ""; else category = next.getCategory();

      

      switch (category) {
        case "Bound":
          return 0;
        case "Breakable":
          if (canBreak) ((Breakable) next).hit(gamePlant); else return 0;
          update(gamePlant, newPosition);
          return partial + energyDelta;
        case "Collectable":
          if (canCollect) partial =
            ((Collectable) next).collect(this); else return 0;
          update(gamePlant, newPosition);
          return partial + energyDelta;
        case "Movable":

          int currentEnergy = GameEngine.getInstance().getEnergy();
          if (currentEnergy < 90) {
            if (gamePlant.getElement(newPosition).getElementName() == Name.STONE) {
              return 0;
            };
          }

          if (canPush) {
            partial = ((Movable) next).move(direction, gamePlant);
            if (partial == 0) return 0;
          } else return 0;

          if (checkTunnels(gamePlant, newPosition)) return energyDelta;
          update(gamePlant, newPosition);
          return partial + energyDelta;
        case "Vortex":
          if (canTap) {
            this.setLayer(1);
            next.setLayer(0);
            gamePlant.setElement(newPosition, null);
          } else {
            partial = ((Vortex) next).destroy(this, gamePlant);
          }
          gamePlant.setElement(position, null);
          position = newPosition;
          return partial + energyDelta;
        default:
          if (checkTunnels(gamePlant, newPosition)) return energyDelta;
          update(gamePlant, newPosition);
          return partial + energyDelta;
      }
    }
    return 0;
  }

  private void update(GamePlant gamePlant, Point2D newPosition) {
    gamePlant.setElement(newPosition, this);
    gamePlant.setElement(position, null);
    position = newPosition;
  }

  private boolean checkTunnels(GamePlant gamePlant, Point2D newPosition) {
    // We iterate over the tunnels and check if we can travel
    Set<Entry<Integer, Tunnel>> entrySet = gamePlant.getTunnelMap().entrySet();
    for (Map.Entry<Integer, Tunnel> tunnel : entrySet) {
      Tunnel t = tunnel.getValue();
      if (t.getPosition().distanceTo(newPosition) == 0) {
        return ((Tunnel) t).travel(this, gamePlant);
      }
    }
    return false;
  }

  private boolean isBound(GameElement e) {
    if (e instanceof Bound) return true;
    return false;
  }

  public boolean isPlayable(GamePlant gamePlant, List<GameElement> exclude) {
    for (GameElement e : exclude) {
      if (e.getPosition().distanceTo(position) == 0) return true;
    }

    if (this.isActive) {
      int x = position.getX();
      int y = position.getY();

      if (
        isBound(gamePlant.getElement(x, y - 1)) &&
        isBound(gamePlant.getElement(x - 1, y))
      ) return false;
      if (
        isBound(gamePlant.getElement(x, y - 1)) &&
        isBound(gamePlant.getElement(x + 1, y))
      ) return false;
      if (
        isBound(gamePlant.getElement(x + 1, y)) &&
        isBound(gamePlant.getElement(x, y + 1))
      ) return false;
      if (
        isBound(gamePlant.getElement(x - 1, y)) &&
        isBound(gamePlant.getElement(x, y + 1))
      ) return false;
      return true;
    }
    return false;
  }

  public void updateImage(Direction direction) {
    imageName =
      imageName.substring(0, imageName.length() - 1) +
      direction.toString().charAt(0);
  }
}
