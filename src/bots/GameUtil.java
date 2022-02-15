package bots;

import penguin_game.*;
import java.util.*;

public class GameUtil {

    /**
     * Returns how many penguins will an enemy or a neutral iceberg have in turn x.
     * Positive count - enemy or neutral.
     * Negative count - mine.
     * @param game current game state
     * @param destination iceberg to check
     * @param inHowManyTurns the amount of turns ahead we want to check
     * @return how many penguins will the enemy iceberg have in turn x
     */
    public static int howManyPenguinsWillEnemyOrNeutralIceBuildingHave(Game game, IceBuilding destination, int inHowManyTurns) {

        // Initiate result that we will update for every turn
        int currentPenguinAmount = destination.penguinAmount;

        // Store 3 maps, one for the incoming my penguin groups, one for the incoming enemy penguin groups and one for the incoming bonus penguins.
        Map<Integer, Integer> incomingMyPenguinGroups = IcebergUtil.howManyMyPenguinsWillArriveToADestinationInXTurns(game, destination);
        Map<Integer, Integer> incomingEnemyPenguinGroups = IcebergUtil.howManyEnemyPenguinsWillArriveToADestinationInXTurns(game, destination);
        Map<Integer, Integer> incomingBonusPenguins = IcebergUtil.howManyPenguinsWillAnIcebergGetAtEachTurnFromBonusIceberg(game, destination);

        //TODO set it so bonus icebergs don't get the bonus iceberg's bonus

        // A variable to keep track of how many turns we have gone through
        // Initialized to 1 because we already have the current turn's information
        int turnCounter = 1;

        while(turnCounter <= inHowManyTurns) {
            Log.log("B_0_0: entered loop and current penguin amount = " + currentPenguinAmount);

            // Add the destination's penguins-per-turn
            // Initialized at 0 to avoid an error saying it isn't initialized.
            int penguinsPerTurn = 0;

            // If the destination is an iceberg, we need to change the penguin amount according to the penguins-per-turn of the destination.
            if(destination instanceof Iceberg) {

                // If the destination is neutral, it doesn't create penguins.
                // If the destination is enemy, we need to add the penguins-per-turn.
                // If the destination is mine, we need to subtract the penguins-per-turn, because we are looking from the POV of the enemy in this function.
                String destinationOwner = playerToString(game, destination.owner);
                switch (destinationOwner) {
                    case "Neutral":
                        // If the destination is neutral, it doesn't create penguins.
                        penguinsPerTurn = 0;
                        break;
                    case "Enemy":
                        // If the destination is enemy, we need to add the penguins-per-turn.
                        if(currentPenguinAmount > 0) {
                            penguinsPerTurn = ((Iceberg) destination).penguinsPerTurn;
                        }

                        // If the destination is mine (the enemy penguin will have a negative penguin amount),
                        // we need to subtract the penguins-per-turn, because we are looking from the POV of the enemy in this function.
                        else {
                            penguinsPerTurn = -((Iceberg) destination).penguinsPerTurn;
                        }
                        break;
                }
            }
            // If the destination is not an iceberg (it is a bonus iceberg), it doesn't spawn penguins on itself, so it's penguins-per-turn is 0.
            else {
                penguinsPerTurn = 0;
            }


            // Add the penguins-per-turn to the current penguin amount
            currentPenguinAmount += penguinsPerTurn;
            Log.log("B_0_1: added penguins per turn and current penguin amount = " + currentPenguinAmount);

            // Update the current penguin amount according to the penguin groups that will be arriving.
            // Incoming my penguin groups
            if(incomingMyPenguinGroups.containsKey(turnCounter)) {
                Log.log("B_2: found a penguin group that I own that will arrive after " + turnCounter + " turns with size " + incomingMyPenguinGroups.get(turnCounter));
                currentPenguinAmount -= incomingMyPenguinGroups.get(turnCounter);
                Log.log("B_2_0: new penguin amount is " + currentPenguinAmount);
            }
            // Incoming enemy penguin groups
            if(incomingEnemyPenguinGroups.containsKey(turnCounter)) {
                Log.log("B_3: found a penguin group that the enemy owns that will arrive after " + turnCounter + " turns with size " + incomingMyPenguinGroups.get(turnCounter));
                currentPenguinAmount += incomingEnemyPenguinGroups.get(turnCounter);
                Log.log("B_3_0: new penguin amount is " + currentPenguinAmount);
            }
            // Incoming bonus penguins
            if(incomingBonusPenguins.containsKey(turnCounter)) {
                currentPenguinAmount += incomingBonusPenguins.get(turnCounter);
            }

            // Increment the turn counter
            turnCounter++;

            Log.log("\nB_0: After " + turnCounter + " turns, the penguin amount is " + currentPenguinAmount + "\n");
        }

        // Return the penguin amount after the specified number of turns.
        Log.log("B_1 Returning: " + currentPenguinAmount);
        return currentPenguinAmount;
    }





    /**
     * A function to get a list containing all the icebergs that are either the enemy's or neutral.
     * @param game current game state
     * @return A list containing all the icebergs that are either the enemy's or neutral.
     */
    public static List<Iceberg> getEnemyOrNeutralIcebergs(Game game) {
        // Initiate result list
        List<Iceberg> result = new ArrayList<Iceberg>();

        // Add all enemy icebergs to the result list
        for(Iceberg enemyIceberg : game.getEnemyIcebergs()) {
            result.add(enemyIceberg);
        }

        // Add all neutral icebergs to the result list
        for(Iceberg neutralIceberg : game.getNeutralIcebergs()) {
            result.add(neutralIceberg);
        }

        return result;
    }


    /**
     * A function that converts a player to a string representation of it.
     * @param game current game state
     * @param player the player to convert
     * @return A string representation of the player.
     */
    public static String playerToString(Game game, Player player) {
        // If the player is me, return "Me"
        if(player.equals(game.getMyself())) {
            return "Me";
        }

        // If the player is the enemy, return "Enemy"
        if(player.equals(game.getEnemy())){
            return "Enemy";
        }

        // If the player is neutral, return "Neutral"
        return "Neutral";
    }
}
