package dc.model;

import dc.util.Type;

/**
 * Abstract class for all objects that are part of the DisCaptive map
 */
public abstract class Tile {
    int posX, posY;
    Level thisLevel;
    Type type;

    Tile(int x, int y) {
        setPosY(y);
        setPosX(x);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }


    public String getTileType() {
        String classType = this.getClass().toGenericString();
        int i = classType.lastIndexOf(".");
        classType = classType.substring(i + 1);
        return classType;
    }
}
