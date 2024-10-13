package sokobanstarter;

public class Tunnel extends Basic {

  private int energyDelta = 0;  // Change of energy after interacting with this element
  private int id; // Tunnel id where the symmetric value corresponds to the other exit.

  public Tunnel(Name name, int x, int y, int layer, int energyDelta, int id) {
    super(name, x, y, layer);
    this.energyDelta = energyDelta;
    this.id = id;
  }

  public boolean travel(GameElement gameElement, GamePlant gamePlant) {
    Tunnel exit = gamePlant.getTunnelMap().get(-this.id);
    int x = exit.getPosition().getX();
    int y = exit.getPosition().getY();
    if (gamePlant.getElement(x, y) instanceof Movable) return false; // End is tapped
    int currentX = gameElement.getPosition().getX();
    int currentY = gameElement.getPosition().getY();
    gamePlant.setElement(currentX, currentY, null);
    gameElement.setPosition(x, y);
    gamePlant.setElement(x, y, gameElement);
    return true;
  }
}
