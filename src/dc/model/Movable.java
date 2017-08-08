package dc.model;

import dc.controller.Movement;
import dc.util.Type;

/**
 * All movable objects for DisCaptive.
 * Has reference to the tile-object that it visually stands on.
 */
abstract class Movable extends Tile {
    Level thisLevel;
    Tile standsOn;
    private boolean movedThisTurn;

    /**
     * Creates a movable object.
     * @param x x-coordinate
     * @param y y-coordinate
     */
    Movable(int x, int y, Level lvl) {
        super(x, y);
        thisLevel = lvl;
        standsOn = new EmptyPassage(x, y);
        movedThisTurn = false;
    }

    /**
     * Moves a movable object into a direction.
     * Receives a Movement parameter and translates it into a Type variable.
     * @param dir The direction of a guard
     */
    void move(Level level, Movement dir) {
        switch (dir) {
            case DOWN:
                type = Type.SOUTH;
                move(level, this, thisLevel.map[getPosX() + 1][getPosY()], Type.SOUTH);
                break;
            case UP:
                type = Type.NORTH;
                move(level, this, thisLevel.map[getPosX() - 1][getPosY()], Type.NORTH);
                break;
            case LEFT:
                type = Type.WEST;
                move(level, this, thisLevel.map[getPosX()][getPosY() - 1], Type.WEST);
                break;
            case RIGHT:
                type = Type.EAST;
                move(level, this, thisLevel.map[getPosX()][getPosY() + 1], Type.EAST);
                break;
            default:
        }
    }

    /**
     * Tries to move a movable object.
     * get a direction and calculates what tile it wants to move to.
     * @param d direction
     */
    void move(Level level, Type d) {
        switch (d) {
            case SOUTH:
                move(level, this, thisLevel.map[getPosX() + 1][getPosY()], Type.SOUTH);
                break;
            case NORTH:
                move(level, this, thisLevel.map[getPosX() - 1][getPosY()], Type.NORTH);
                break;
            case WEST:
                move(level, this, thisLevel.map[getPosX()][getPosY() - 1], Type.WEST);
                break;
            case EAST:
                move(level, this, thisLevel.map[getPosX()][getPosY() + 1], Type.EAST);
                break;
            default:
        }
    }

    /**
     * Tries to move a movable object.
     * Checks if it already moved this turn. Saves the starting coordinates. Sets the direction in case it is a player.
     * Always checks what kind of tile is in front. then checks what kind of object itself is. Depending on the object in front
     * it either moves there, doesn't move there, or tells the object in front to move first.
     * @param start where the movable object starts out
     * @param field where it wants to move to
     * @param d direction
     */
    void move(Level level, Movable start, Tile field, Type d) {

        if (movedThisTurn) {
            return;
        }
        int startX = start.getPosX();
        int startY = start.getPosY();

        if (this instanceof Player) {
            this.type = d;
        }


        if (field instanceof Passage) {                                     // Passage ahead
            thisLevel.map[getPosX()][getPosY()] = standsOn;
            standsOn = field;
            thisLevel.map[field.getPosX()][field.getPosY()] = this;
            setPosX(field.getPosX());
            setPosY(field.getPosY());
            setMovedThisTurn(true);
            level.fireTileUpdate(startX, startY);
            level.fireTileUpdate(field.getPosX(), field.getPosY());


            if (this instanceof Guard) {                                    // Guard moves onto a passage
                ((Guard) this).screen();
                return;
            }
            if (this instanceof Player) {                                   // Check if on goal

                if (standsOn instanceof PlayerGoal) {
                    level.handelLevelComplete();
                    return;
                }
                if (this.standsOn instanceof ForceFieldOpener) {              // Check if on forcefield opener
                    if (standsOn.getType() == Type.BLUE) {
                        thisLevel.blueForceFields.openForceField(level);
                    } else {
                        thisLevel.redForceFields.openForceField(level);
                    }
                    standsOn = new EmptyPassage(this.getPosX(), this.getPosY());
                    return;
                }
                if (this.standsOn instanceof Key) {                           // Check if stands on key
                    ((Player) this).setKeys(((Player) this).getKeys() + 1);
                    standsOn = new EmptyPassage(this.getPosX(), this.getPosY());
                }
            }
            return;
        }

        if (field instanceof Lock) {
            if (this instanceof Player) {                                     //Player meets lock
                if (((Player) this).getKeys() > 0) {
                    thisLevel.map[field.getPosX()][field.getPosY()] = new OpenLock(field.getPosX(), field.getPosY());
                    ((Player) this).setKeys(((Player) this).getKeys() - 1);
                    this.move(level, type);
                } else {
                    level.updateStatusLine("You don't have enough keys");
                }
            }

            if (this instanceof Guard) {                                      //Guard meets lock
                ((Guard) this).turn180(level);
                move(level, this.type);
            }

            if (this instanceof Box) {                                        //Box meets lock
                this.setMovedThisTurn(true);
            }
        }

        //Forcefield ahead
        if (field instanceof ForceField) {                                    //Forcefield ist still on
            if (((ForceField) field).isActive()) {
                if (this instanceof Player) {                                 //Player runs into closed forcefield
                    level.updateStatusLine("You cant move through yet.");
                }
                if (this instanceof Box) {                                    //Box runs into closed forcefield
                    this.setMovedThisTurn(true);
                }
                if (this instanceof Guard) {                                  //Guard runs into closed forcefield
                    ((Guard) this).turn180(level);
                    move(level, this.type);
                }


            } else {
                thisLevel.map[getPosX()][getPosY()] = standsOn;             //Forcefield is open
                standsOn = field;
                thisLevel.map[field.getPosX()][field.getPosY()] = this;
                setPosX(field.getPosX());
                setPosY(field.getPosY());
                setMovedThisTurn(true);
                level.fireTileUpdate(startX, startY);
                level.fireTileUpdate(field.getPosX(), field.getPosY());

            }
        }

        //Wall ahead
        if (field instanceof Wall) {
            if (this instanceof Player) {                                   //Player runs into wall
                level.updateStatusLine("You hit a Wall!");
                level.fireTileUpdate(startX, startY);
            }
            if (this instanceof Guard) {                                    //Guard runs into wall
                ((Guard) this).turn180(level);
                this.move(level, this.type);
            }
            if (this instanceof Box) {                                        //Box runs into wall
                this.setMovedThisTurn(true);
            }
            return;
        }
        //Box ahead
        if (field instanceof Box) {

            if (start instanceof Guard) {                                   //Guard pushes a box
                if (((Box) field).isMovedThisTurn()) {
                    ((Guard) this).turn180(level);
                } else {
                    ((Box) field).move(level, type);
                    ((Guard) this).move(level, this.type);
                    if (!this.movedThisTurn) {
                        ((Guard) this).turn180(level);
                    }
                }
                return;
            }

            if (start instanceof Player) {                                    //Player pushes a box
                if (((Box) field).isMovedThisTurn()) {
                    level.updateStatusLine("You can't move this box");
                } else {
                    ((Box) field).move(level, type);
                    ((Player) start).move(level, type);
                }
            }
            //Box pushes a box
            if (start instanceof Box) {
                level.updateStatusLine("You cant move through yet.");
                ((Box) this).setMovedThisTurn(true);
            }
        }

        //Guard ahead
        if (field instanceof Guard) {

            if (this instanceof Guard) {                                    //Guard hits guard
                if (this instanceof Guard) {
                    if (((Guard) field).isTurned()) {
                        ((Guard) this).turn180(level);
                        ((Guard) this).setTurned(true);
                        //move(level, ((Guard) field).getType());
                    } else {
                        ((Guard) this).setTurned(true);
                        if (!((Guard) field).isMovedThisTurn())
                            //((Guard)this).turn180(level);
                            ((Guard) field).move(level, ((Guard) field).getType());
                    }
                    return;
                }

                }
                if (this instanceof Player) {                                     //Player hits guard
                    // The player won't move, but unless the
                    // guard faces the player he won't get caught.
                    level.updateStatusLine("You got lucky he didn't see you");
                }
                if (this instanceof Box) {                                        //Box hits guard
                    this.setMovedThisTurn(true);
                    //The box won't be able to move
                }
                return;

        }

        //Gap ahead
        if (field instanceof Gap) {
            if (this instanceof Player) {                                   //Player runs into gap
                standsOn = field;
                thisLevel.map[field.getPosX()][field.getPosY()] = this;
                setPosX(field.getPosX());
                setPosY(field.getPosY());
                level.handleLevelLost();
            }
            if (this instanceof Guard) {                                    //Guard runs into gap
                ((Guard) this).turn180(level);
                return;
            }
            if (this instanceof Box) {                              //Box runs into gap
                thisLevel.map[field.getPosX()][field.getPosY()] = new BoxInGap(field.getPosX(), field.getPosY());
                thisLevel.map[start.getPosX()][start.getPosY()] = standsOn;
                level.fireTileUpdate(field.getPosX(), field.getPosY());
                standsOn = field;
                field = this;
                setMovedThisTurn(true);

            }
            return;
        }
        //player ahead
        if (this instanceof Guard) {

            if (field instanceof Player) {                                  //Guard runs into player
                level.handleLevelLost();
            }
        }

    }

    protected Level getThisLevel() {
        return thisLevel;
    }

    protected void setThisLevel(Level thisLevel) {
        this.thisLevel = thisLevel;
    }

    protected Tile getStandsOn() {
        return standsOn;
    }

    protected void setStandsOn(Tile standsOn) {
        if (standsOn instanceof Passage) {
            this.standsOn = standsOn;
        }
    }

    protected boolean isMovedThisTurn() {
        return movedThisTurn;
    }

    protected void setMovedThisTurn(boolean movedThisTurn) {
        this.movedThisTurn = movedThisTurn;
    }


}

/**
 * A player starts out with 0 keys and can collect more.
 */
class Player extends Movable {
    int keys = 0;

    Player(int x, int y, Level lvl) {
        super(x, y, lvl);
    }

    protected int getKeys() {
        return keys;
    }

    protected void setKeys(int keys) {
        this.keys = keys;
    }
}

/**
 * Box for DisCaptive
 */
class Box extends Movable {
    Box(int x, int y, Level lvl) {
        super(x, y, lvl);
    }
}

/**
 * Guard for DisCaptive
 * Guard has a direction and can turn in all directions. Always checks if a player is in his vision.
 */
class Guard extends Movable {
    boolean turned = false;

    /**
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @param d char that stands for its direction
     */
    Guard(int x, int y, char d, Level lvl) {
        super(x, y, lvl);
        switch (d) {
            case 'N':
                type = Type.NORTH;
                break;
            case 'O':
                type = Type.EAST;
                break;
            case 'S':
                type = Type.SOUTH;
                break;
            case 'W':
                type = Type.WEST;
                break;
        }
    }

    /**
     * Checks if the guard stands on a switchDirection-tile before calling for the move method in Movable.
     */
    void move(Level level, Type type) {                      // In case Guard stands on a turn-field
        if (standsOn instanceof SwitchDirection) {
            if (((SwitchDirection) standsOn).type == type.RIGHT) {
                turnRight();
                super.move(level, this.type);
            } else {
                turnLeft();
                super.move(level, this.type);
            }
        } else {
            super.move(level, this.type);
        }
    }

    /**
     * Turns the guard right.
     */
    void turnRight() {                       //Guard turns 90° right
        switch (type) {

            case NORTH:
                type = Type.EAST;
                break;
            case EAST:
                type = Type.SOUTH;
                break;
            case SOUTH:
                type = Type.WEST;
                break;
            case WEST:
                type = Type.NORTH;
                break;
        }

    }

    /**
     * Turns the guard left.
     */
    void turnLeft() {                        //Guard turns 90° left
        switch (type) {
            case NORTH:
                type = Type.WEST;
                break;
            case WEST:
                type = Type.SOUTH;
                break;
            case SOUTH:
                type = Type.EAST;
                break;
            case EAST:
                type = Type.NORTH;
                break;
        }
    }

    /**
     * turns the guard 180° and screens for the player.
     * @param level
     */
    void turn180(Level level) {                             // Guard turns 180°
        switch (type) {
            case NORTH:
                type = Type.SOUTH;
                break;
            case SOUTH:
                type = Type.NORTH;
                break;
            case WEST:
                type = Type.EAST;
                break;
            case EAST:
                type = Type.WEST;
                break;

        }
        this.setTurned(true);
        level.fireTileUpdate(getPosX(), getPosY());
        screen();
    }

    /**
     * starts screening
     */
    void screen() {                      // sets up the first screening
        screen(1);
    }

    /**
     * Handles which tile has to be screened.
     * @param i how many tiles in front the guard has to screen.
     */
    private void screen(int i) {                        // finds the field that is to be screened

        switch (type) {
            case SOUTH:
                screen(thisLevel.map[getPosX() + i][getPosY()], i);
                break;
            case NORTH:
                screen(thisLevel.map[getPosX() - i][getPosY()], i);
                break;
            case EAST:
                screen(thisLevel.map[getPosX()][getPosY() + i], i);
                break;
            case WEST:
                screen(thisLevel.map[getPosX()][getPosY() - i], i);
                break;
            default:
        }
    }

    /**
     * Screens a specific field and handles when it catches a player
     * @param Field the tile that is supposed to be screened
     * @param i how many tiles have been screened already
     */
    private void screen(Tile Field, int i) {    // screens a specific field
        if (Field instanceof Player) {
            thisLevel.handleLevelLost();
            return;
        }
        if (Field instanceof Passage || Field instanceof Gap) {
            screen(i + 1);
        }
        if (Field instanceof Wall || Field instanceof Box || Field instanceof Guard) {
            return;
        }

    }

    boolean isTurned() {
        return turned;
    }

    void setTurned(boolean turned) {
        this.turned = turned;
    }

}

/**
 * CLass used if a box starts out over a goal.
 */
class BoxOverGoal extends Box {
    BoxOverGoal(int x, int y, Level lvl) {
        super(x, y, lvl);
        PlayerGoal Goal = new PlayerGoal(x, y);
        standsOn = Goal;
    }
}

