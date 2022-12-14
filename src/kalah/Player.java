package kalah;

import java.util.ArrayList;

/**
 * Player class that holds each individual players part of the playing board.
 * It handles the creation of their House's and Store on the board and handles
 * their score.
 * @author mpie374
 */
public class Player {
    private int NUMHOUSES; //number of houses belonging to each player
    private ArrayList<Pit> playerPits;
    private int score;

    /**
     * Default Player constructor
     */
    Player(int numHouses) {
        //initialise houses and player store
        NUMHOUSES = numHouses;
        initialisePlayerPits();
        score = 0;
    }

    /**
     * Initialises the players Pit's as an ArrayList<Pit> and adds their Store.
     */
    private void initialisePlayerPits() {
        playerPits = new ArrayList<>();
        for(int i = 0; i < NUMHOUSES; i++) {
            playerPits.add(new House());
        }
        playerPits.add(new Store());
    }

    /**
     * Retrieves the Pits of the Player
     * @return - the ArrayList of Pits
     */
    public ArrayList<Pit> getPlayerPits() {
        return playerPits;
    }

    /**
     * Calculates the Player's score by iterating through the
     * number of seeds stored in each Pit
     */
    public void calcScore() {
        for(int i = 0; i < playerPits.size(); i++) {
            score += playerPits.get(i).getNumSeeds();
        }
    }

    /**
     * Retrieves the player's final score
     * @return - the score as an int
     */
    public int getScore() {
        return score;
    }
}
