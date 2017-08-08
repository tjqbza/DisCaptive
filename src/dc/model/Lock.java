/**
 * Created by Timo on 20.06.2016.
 */
package dc.model;

/**
 * A currently blocked Passage. Can be opened by a player with a key.
 */
class Lock extends Tile {
    Lock(int x, int y) {
        super(x, y);
    }
}
