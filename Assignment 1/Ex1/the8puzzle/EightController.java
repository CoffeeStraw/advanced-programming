/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package the8puzzle;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The controller for the EightPuzzle game.
 *
 * @author Antonio
 */
public class EightController implements VetoableChangeListener {

    // Game logic
    private int holePosition = -1;

    // Listeners
    private final PropertyChangeSupport tilesSupport;
    private final PropertyChangeSupport statusSupport;

    // Events
    private static final String VETO_MESSAGE = "Move not allowed.";

    public static final String EVENT_NEWGAME = "startNewGame";
    public static final String EVENT_MOVEVETOED = "moveVetoed";
    public static final String EVENT_MOVEALLOWED = "moveAllowed";
    public static final String EVENT_SETLABEL = "setLabel";

    public EightController() {
        this.tilesSupport = new PropertyChangeSupport(this);
        this.statusSupport = new PropertyChangeSupport(this);
    }

    public void addTileListener(PropertyChangeListener listener) {
        this.tilesSupport.addPropertyChangeListener(listener);
    }

    public void removeTileListener(PropertyChangeListener listener) {
        this.tilesSupport.removePropertyChangeListener(listener);
    }

    public void addStatusListener(PropertyChangeListener listener) {
        this.statusSupport.addPropertyChangeListener(listener);
    }

    public void removeStatusListener(PropertyChangeListener listener) {
        this.statusSupport.removePropertyChangeListener(listener);
    }

    /**
     * @return a random permutation of 0...8 to decide initial state of the
     *         game.
     */
    private int[] generatePermutation() {
        List<Integer> intList = IntStream.rangeClosed(0, 8).boxed().collect(Collectors.toList());
        Collections.shuffle(intList);
        return intList.stream().mapToInt(i -> i).toArray();
    }

    /**
     * Shuffle the tiles labels and fire the "startNewGame" event.
     */
    public void startNewGame() {
        int[] permutationLabels = generatePermutation();

        // Save the hole position
        for (int i = 0; i < permutationLabels.length; i++) {
            if (permutationLabels[i] == 0) {
                this.holePosition = i + 1;
                break;
            }
        }

        this.tilesSupport.firePropertyChange(EVENT_NEWGAME, null, permutationLabels);
        this.statusSupport.firePropertyChange(EVENT_NEWGAME, null, null);
    }

    /**
     * @param tilePosition1 The position of the first tile.
     * @param tilePosition2 The position of the second tile.
     * @return True if the positions are close (distant 1 either horizontally or
     *         vertically, but not both at the same time), false otherwise.
     */
    private boolean positionsAreClose(int tilePosition1, int tilePosition2) {
        tilePosition1 -= 1;
        tilePosition2 -= 1;

        int horizontal1 = tilePosition1 % 3;
        int horizontal2 = tilePosition2 % 3;

        int vertical1 = Math.round(tilePosition1 / 3);
        int vertical2 = Math.round(tilePosition2 / 3);

        int horizontalDistance = Math.abs(horizontal1 - horizontal2);
        int verticalDistance = Math.abs(vertical1 - vertical2);

        return (horizontalDistance ^ verticalDistance) == 1;
    }

    /**
     * Allow/deny the move of a tile, and notifies the hole to change its label.
     * It does also notify the status of the game.
     * 
     * @param evt The event fired by the tile.
     */
    @Override
    public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
        if (evt.getPropertyName().equals(EightTile.EVENT_TILECLICKED)) {
            int[] tileProperties = (int[]) evt.getOldValue();
            int position = tileProperties[0];
            int tileLabel = tileProperties[1];

            if (!positionsAreClose(position, this.holePosition)) {
                this.statusSupport.firePropertyChange(EVENT_MOVEVETOED, position, this.holePosition);
                throw new PropertyVetoException(VETO_MESSAGE, evt);
            }

            this.tilesSupport.firePropertyChange(EVENT_SETLABEL, null, new int[] { this.holePosition, tileLabel });
            this.statusSupport.firePropertyChange(EVENT_MOVEALLOWED, position, this.holePosition);
            this.holePosition = position;
        }
    }

    /**
     * Flip the labels of the first two tiles (if allowed).
     * 
     * @param labelTile1 the label of the first tile.
     * @param labelTile2 the label of the second tile.
     */
    public void flip(int labelTile1, int labelTile2) {
        if (this.holePosition == 9) {
            this.tilesSupport.firePropertyChange(EVENT_SETLABEL, null, new int[] { 1, labelTile2 });
            this.tilesSupport.firePropertyChange(EVENT_SETLABEL, null, new int[] { 2, labelTile1 });
        }
    }
}
