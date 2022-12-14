package kalah;

/**
 * Abstract class that holds most functionality for both the House
 * and Store objects. This allows both House and Store to be extended
 * and ArrayLists of both objects to be held together in the creation
 * of the board.
 * @author mpie374
 */
public abstract class Pit {
    protected int numSeeds;

    /**
     * Adds seeds to the pit
     * @param seeds - number of seeds to be added
     */
    public void addSeeds(int seeds) {
        numSeeds += seeds;
    }

    /**
     * Retrieves the number of seeds in the pit
     * @return - the number of seeds
     */
    public int getNumSeeds() {
        return numSeeds;
    }

    /**
     * Empties the Pit at the start of each turn and when seeds are captured
     */
    public void emptyPit() {
        numSeeds = 0;
    }

    /**
     * Clones the pit for the dummy board
     * @param clonedSeeds
     */
    public void clonePit(int clonedSeeds) {
        numSeeds = clonedSeeds;
    }

    /**
     * Captures the oppositions seeds when a player lands in a previously
     * empty pit of their own (if corresponding opposite pit is not empty)
     * @return - the number of seeds to be captured
     */
    public int captureSeeds() {
        int seeds = numSeeds;
        emptyPit();
        return seeds;
    }

    /**
     * Used to display the seeds on the board. Extra space required if single
     * digit number.
     * @return - number of seeds in the pit as a String
     */
    public String toString() {
        if(numSeeds < 10)
            return " " + numSeeds;
        else return String.valueOf(numSeeds);
    }
}
