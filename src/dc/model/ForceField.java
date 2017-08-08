package dc.model;

import dc.util.Type;

/**
 * A Forcefield that can be open or closed and can have different colors.
 */
class ForceField extends Tile {

    boolean active = true;

    ForceField(int x, int y, Type color, boolean bol) {
        super(x, y);
        type = color;
    }


    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
