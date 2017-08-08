package dc.model;

import dc.util.Type;
import java.util.Iterator;

/**
 * List that is used by the model
 * This class is a list that handles the movement of all guards, as well as handling the forcefields.
 * @param <T>
 */
public class List<T> implements Iterable<T> {
    Node head, tail;

    public List() {
        head = null;
        tail = null;
    }

    public Iterator<T> iterator() {
        return this.new ListIterator();
    }

    /**
     * returns the val of head and the removes it from the list.
     * @return returns the val of head as String
     */
    public String  getTop(){
        String save = head.val.toString();
        head.next=head;
        return save;

    }
    /**
     * Appends an object
     */
    void append(T val) {
        Node p = new Node(val);
        if (head == null)
            head = p;
        else
            tail.next = p;
        tail = p;
    }

    /**
     * prepends an object
     */
    void prepend(T val) {
        Node p = new Node(val);
        p.next = head;
        if (tail == null)
            tail = p;
        head = p;
    }

    /**
     * Deletes an item from the list
     * @param val gets deleted
     */
    void delete(T val) {
        Node p = head, prev = null;
        while (p != null && p.val != val) {
            prev = p;
            p = p.next;
        }
        // p == null || p.val == val
        if (p != null) { // p.val == val
            if (p == head)
                head = p.next;
            else
                prev.next = p.next;
            if (p == tail)
                tail = prev;
        }
    }

    /**
     * Opens all forcefields of the list and sets the status of these forcefield to open.
     * Tells view to update all forcefield-tiles
     */
    void openForceField(Level level) {
        Node p = head;
        if (p.val instanceof ForceField) {
            while (p != null) {
                ((ForceField) p.val).setActive(false);
                if (((ForceField) p.val).type == Type.BLUE)
                    ((ForceField) p.val).setType(Type.BLUEOPEN);
                else ((ForceField) p.val).setType(Type.REDOPNEN);
                level.fireTileUpdate(((ForceField) p.val).getPosX(), ((ForceField) p.val).getPosY());
                p = p.next;
            }
        }
    }

   public int getAmount(){
        Node p = head;
        int i = 0;
        while(p != null){
            i++;
            p = p.next;
        }
        return i+1;

    }

    /**
     * Clears the list.
     */
    void clear(){
        head = null;
        tail = null;
    }

    /**
     * Resets movedThisTurn and turned
     */
    void reset() {
        Node p = head;
        while (p != null) {
            ((Movable) p.val).setMovedThisTurn(false);
            if (p.val instanceof Guard) {
                ((Guard) p.val).setTurned(false);
            }
            p = p.next;
        }
    }

    /**
     * Moves all guards in the list.
     */
    void move(Level level) {
        Node p = head;
        while (p != null) {
            if (!((Guard) p.val).isMovedThisTurn())
                ((Guard) p.val).move(level, ((Guard) p.val).type);
            p = p.next;
        }
    }



    class Node {
        T val;
        Node next;

        Node(T v) {
            val = v;
        }
    }

    private class ListIterator implements Iterator<T> {
        private Node pos;

        private ListIterator() {
            pos = head;
        }

        public T next() {
            T res = pos.val;
            pos = pos.next;
            return res;
        }

        public boolean hasNext() {
            return pos != null;
        }
    }

}