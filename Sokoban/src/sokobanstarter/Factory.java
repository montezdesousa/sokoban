package sokobanstarter;

enum Category {
  BASIC,
  BOUND,
  BREAKABLE,
  COLLECTABLE,
  MOVABLE,
  TUNNEL,
  VORTEX,
}

enum Name {
  STONE("SmallStone"),
  BATTERY("Bateria"),
  BOBCAT("Empilhadora_U"),
  CRACKED_WALL("ParedeRachada"),
  CRATER("Caixote"),
  EMPTY("Vazio"),
  HOLE("Buraco"),
  FLOOR("Chao"),
  HAMMER("Martelo"),
  PALLET("Palete"),
  TARGET("Alvo"),
  TELEPORT("Teleporte"),
  WALL("Parede");

  private final String imageName; // Must match the image file name

  Name(String imageName) {
    this.imageName = imageName;
  }

  public String getImageName() {
    return imageName;
  }
}

class Factory {

  public static GameElement create(
    Category category,
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
    switch (category) {
      case MOVABLE:
        return new Movable(
          name,
          x,
          y,
          layer,
          energyDelta,
          canCollect,
          canPush,
          canTap,
          updateImage
        );
      default:
        throw new IllegalArgumentException("Invalid element type: " + category);
    }
  }

  public static GameElement create(
    Category category,
    Name name,
    int x,
    int y,
    int layer,
    int energyDelta,
    int id
  ) {
    switch (category) {
      case TUNNEL:
        return new Tunnel(name, x, y, layer, energyDelta, id);
      default:
        throw new IllegalArgumentException("Invalid element type: " + category);
    }
  }

  public static GameElement create(
    Category category,
    Name name,
    int x,
    int y,
    int layer,
    int energyDelta,
    boolean givesBoost
  ) {
    switch (category) {
      case COLLECTABLE:
        return new Collectable(name, x, y, layer, energyDelta, givesBoost);
      default:
        throw new IllegalArgumentException("Invalid element type: " + category);
    }
  }

  public static GameElement create(
    Category category,
    Name name,
    int x,
    int y,
    int layer,
    int energyDelta
  ) {
    switch (category) {
      case BASIC:
        return new Basic(name, x, y, layer);
      case BOUND:
        return new Bound(name, x, y, layer);
      case BREAKABLE:
        return new Breakable(name, x, y, layer);
      case MOVABLE:
        return new Movable(name, x, y, layer, energyDelta);
      case VORTEX:
        return new Vortex(name, x, y, layer, energyDelta);
      default:
        throw new IllegalArgumentException("Invalid element type: " + category);
    }
  }
}
