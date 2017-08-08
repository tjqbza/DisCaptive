package dc.model;

import dc.util.Type;

/**
 * Passages are tiles Movables can step on.
 */
abstract class Passage extends Tile {

    Passage(int x, int y) {
        super(x, y);
    }

}

/**
 * A lock that has been opened so movables can walk through.
 */
class OpenLock extends Passage{
    OpenLock (int x, int y){
        super(x, y);
    }
}

/**
 * An empty passage
 */
class EmptyPassage extends Passage {

    EmptyPassage(int x, int y) {
        super(x, y);
    }
}

/**
 * A gap that has been filled by a box, so Movables can move on it.
 */
class BoxInGap extends Passage {
    BoxInGap(int x, int y) {
        super(x, y);
    }
}

/**
 * The goal for the player.
 */
class PlayerGoal extends Passage {
    PlayerGoal(int x, int y) {
        super(x, y);
    }
}

/**
 * A 90°-turn signal for guards.
 */
class SwitchDirection extends Passage {


    SwitchDirection(int x, int y, Type direction) {
        super(x, y);
        type = direction;
    }
}

/**
 * A key that can be picked up buy the player.
 */
class Key extends Passage {
    Key(int x, int y) {
        super(x, y);
    }
}

/**
 * A forcefield opener, that can open a specific color of forcefields.
 */
class ForceFieldOpener extends Passage {

    ForceFieldOpener(int x, int y, Type color) {
        super(x, y);
        type = color;
    }
}


