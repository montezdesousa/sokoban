package sokobanstarter;

import pt.iscte.poo.utils.Point2D;

public class Basic extends GameElement {

  protected Name name;
  protected String imageName;
  protected Point2D position;
  private int layer;

  public Basic(Name name, int x, int y, int layer) {
    this.name = name;
    this.imageName = name.getImageName();
    this.position = new Point2D(x, y);
    this.layer = layer;
  }

  @Override
  public Name getElementName() {
    return name;
  }

  @Override
  public String getName() {
    return imageName;
  }

  @Override
  public Point2D getPosition() {
    return position;
  }

  @Override
  public int getLayer() {
    return layer;
  }

  @Override
  public void setPosition(int x, int y) {
    this.position = new Point2D(x, y);
  }

  @Override
  public void setLayer(int layer) {
    this.layer = layer;
  }
}
