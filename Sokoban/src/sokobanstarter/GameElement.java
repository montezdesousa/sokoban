package sokobanstarter;

import pt.iscte.poo.gui.ImageTile;
import pt.iscte.poo.utils.Point2D;

public abstract class GameElement implements ImageTile {

  public abstract Name getElementName();

  @Override
  public abstract String getName();

  @Override
  public abstract Point2D getPosition();

  @Override
  public abstract int getLayer();

  public abstract void setLayer(int layer);

  public abstract void setPosition(int x, int y);

  public String getCategory() {
    return this.getClass().getSimpleName();
  }
}
