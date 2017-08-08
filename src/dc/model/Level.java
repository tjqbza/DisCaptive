/**
 * Created by Timo on 10.06.2016.
 */
package dc.model;

import dc.controller.Movement;
import dc.util.In;
import dc.util.Out;
import dc.util.Type;
import dc.view.TileBoardView;

/**
 * Handles most of the model of DisCaptive. Has all the references to important tile-objects.
 */
public class Level implements TileBoardModel<Tile> {
    private String name;
    private int height, width, numberOfBoxes, numberOfGuards;
    private List<Guard> guards = new List<Guard>();
    private List<Box> boxes = new List<Box>();
    List<ForceField> blueForceFields = new List<ForceField>();
    List<ForceField> redForceFields = new List<ForceField>();
    Tile[][] map;
    private Player player;
    private List<TileBoardView> views;

    /**
     * Creates a new Level, runs all necessary methods to load the level
     * @param i The number of the level
     */
    public Level(int i) {
        views = new List<>();
        createName(i);
        calculateHeight();
        calculateWidth();
        readMap();
    }

    /**
     * Handles mouse click
     * Checks the position of the click and tells the player to move into that direction.
     * @param row row of click
     * @param col collum of click
     */
    public void handleClick(int row, int col){
        if(row == player. getPosX()) {
            if (col > player.getPosY())
                move(Movement.RIGHT);
            else
            if(!(col == player.getPosY()))
                move(Movement.LEFT);
        }else{
            if(col == player.getPosY())
                if(row > player.getPosX())
                    move(Movement.DOWN);
                else
                    move(Movement.UP);
        }

    }

    /**
     * Moves the player and all guards
     * Moves the player in the direction of the parameter, all guards move, resets all movedThisTurn booleans.
     * @param direction direction
     */
    public void move(Movement direction) {
        for (TileBoardView view : views)
            view.updateStatusLine("");
        player.move(this, direction);
        for (int i = 0; i < getNumberOfGuards(); i++)
            guards.move(this);
        for (TileBoardView view : views)
            view.updateTile(1, 1);

        resetMovedThisTurn();
    }

    /**
     * Reloads the level
     * Clears all lists of guards, boxes and forcefields and reloads the entire map from scratch. Then tells all views to update themselves.
     */
    public void resetLevel(){
        guards.clear();
        boxes.clear();
        blueForceFields.clear();
        redForceFields.clear();
        readMap();
        for (TileBoardView view : views)
            view.updateView();
    }

    /**
     * Starts a new level.
     * Clears all lists of guards, boxes und forcefields, checks if the level number is available and then calculates heigth and width to finally read the map.
     * @param no the number of the level
     */
    public void startNewLevel(int no){
        guards.clear();
        boxes.clear();
        blueForceFields.clear();
        redForceFields.clear();
        createName(no);
        calculateHeight();
        calculateWidth();
        readMap();
    }




    /**
     * Returns the tile on bottom
     * @param rowNr row
     * @param colNr collum
     * @return bottom tile
     */
    public Tile getBottomTileAt(int rowNr, int colNr) {
        if (map[rowNr][colNr] instanceof Movable)
            return ((Movable) map[rowNr][colNr]).standsOn;
        else
            return map[rowNr][colNr];
    }

    /**
     * Returns the tile on top (movable)
     * @param rowNr
     * @param colNr
     * @return movable
     */
    public Tile getTopTileAt(int rowNr, int colNr) {
        if (map[rowNr][colNr] instanceof Movable)
            return map[rowNr][colNr];
        else
            return null;
    }

    /**
     * Appends the view to the list views, so multiple views can be updated
     * @param view
     */

    public void registerView(TileBoardView view) {
        views.prepend(view);
    }

    /**
     * Deletes view from the list, so the view won't be updated anymore
     * @param view
     */

    public void unregisterView(TileBoardView view) {
        views.delete(view);
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }


    /**
     * Updates the statusline in all views
     * @param text text of the statusline
     */
    protected void updateStatusLine(String text) {
        for (TileBoardView view : views)
            view.updateStatusLine(text);
    }

    /**
     * Tells all views to update the tile rowNr, colNr
     * @param rowNr row
     * @param colNr collum
     */
    protected void fireTileUpdate(int rowNr, int colNr) {
        for (TileBoardView view : views)
            view.updateTile(rowNr, colNr);
    }

    /**
     * tells all views that to handle that the level is won
     */
    protected void handelLevelComplete() {
        for (TileBoardView view : views)
            view.announceLevelComplete();
    }

    /**
     * tells all views to handle that the level is lost
     */
    protected void handleLevelLost() {
        for (TileBoardView view : views)
            view.announceLevelLost();
    }


    /**
     * resets the movedThisTurn boolen of all movable objects
     */

    protected void resetMovedThisTurn() {
        player.setMovedThisTurn(false);
        guards.reset();
        boxes.reset();
    }

    /**
     * Reads the map from a textfile
     * Creates a 2 dimensional array and fills it with objects
     * Also adds all movable objects to their lists
     */
    private void readMap() {
        if (getHeight() >= 4 && getWidth() >= 4) {
            map = new Tile[getHeight()][getWidth()];
            char[] line;

            In.open(name);
            for (int p = 0; p < getHeight(); p++) {
                line = In.readLine().toCharArray();
                int i = 0;
                for (; i < line.length; i++) {
                    switch (line[i]) {
                        case ' ':
                            map[p][i] = new EmptyPassage(p, i);
                            break;
                        case '#':
                            map[p][i] = new Wall(p, i);
                            break;
                        case '$':
                            map[p][i] = new Box(p, i, this);
                            boxes.append((Box) map[p][i]);
                            setNumberOfBoxes(getNumberOfBoxes() + 1);
                            break;
                        case '@':
                            player = new Player(p, i, this);
                            map[p][i] = player;
                            break;
                        case '.':
                            map[p][i] = new PlayerGoal(p, i);
                            break;
                        case '*':
                            map[p][i] = new BoxOverGoal(p, i, this);
                            boxes.append((Box) map[p][i]);
                            setNumberOfBoxes(getNumberOfBoxes() + 1);
                            break;
                        case '!':
                            map[p][i] = new Gap(p, i);
                            break;
                        case 'N':
                        case 'O':
                        case 'S':
                        case 'W':
                            map[p][i] = new Guard(p, i, line[i], this);
                            setNumberOfGuards(getNumberOfGuards() + 1);
                            guards.append((Guard) map[p][i]);
                            break;
                        case 'R':
                            map[p][i] = new SwitchDirection(p, i, Type.RIGHT);
                            break;
                        case 'L':
                            map[p][i] = new SwitchDirection(p, i, Type.LEFT);
                            break;
                        case 'X':
                            map[p][i] = new ForceField(p, i, Type.BLUE, true);
                            blueForceFields.append(((ForceField) map[p][i]));
                            break;
                        case 'Y':
                            map[p][i] = new ForceField(p, i, Type.RED, true);
                            redForceFields.append(((ForceField) map[p][i]));
                            break;
                        case 'x':
                            map[p][i] = new ForceFieldOpener(p, i, Type.BLUE);
                            break;
                        case 'y':
                            map[p][i] = new ForceFieldOpener(p, i, Type.RED);
                            break;
                        case 'z':
                            map[p][i] = new Key(p, i);
                            break;
                        case 'Z':
                            map[p][i] = new Lock(p, i);
                            break;
                        default:

                    }
                }
                if (i < getWidth()) {
                    for (; i < getWidth(); i++) {
                        map[p][i] = new EmptyPassage(p, i);
                    }
                }
            }
            In.close();

        } else {
            Out.println("Map '" + name + "' is not a valid map.");
        }
    }

    /**
     * Opens the textfile and sets the width for the level
     */
    private void calculateWidth() {
        In.open(name);
        if (In.done()) {
            int max = 0;
            String zeile = "";
            do {
                zeile = In.readLine();
                if (zeile.length() > max) {
                    max = zeile.length();
                }
            } while (In.done());
            In.close();
            setWidth(max);
        } else {
            Out.println("Map '" + name + "' does not exist.");
        }
    }

    /**
     * Opens the textfile and sets the height for the level
     */
    private void calculateHeight() {
        In.open(name);
        if (In.done()) {
            String zeile = "";
            int counter = 0;
            do {
                zeile = In.readLine();
                counter++;
            } while (In.done());
            counter--;
            In.close();
            setHeight(counter);
        } else {
            Out.println("Map '" + name + "' does not exist.");
        }
    }

    /**
     * Takes i and creates the levelname, so other functions can know the name of the textfile
     * @param i level
     */
    private void createName(int i) {
        setName("Level" + i + ".txt");
    }



    private void setWidth(int width) {
        this.width = width;
    }

    private void setHeight(int height) {
        this.height = height;
    }

    private String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    protected int getNumberOfGuards() {
        return numberOfGuards;
    }

    private void setNumberOfGuards(int numberOfGuards) {
        this.numberOfGuards = numberOfGuards;
    }

    protected int getNumberOfBoxes() {
        return numberOfBoxes;
    }

    private void setNumberOfBoxes(int numberOfBoxes) {
        this.numberOfBoxes = numberOfBoxes;
    }
}