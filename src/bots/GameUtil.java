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
    public static int getPenguinAmountInTurnXForEnemyOrNeutralIceBuilding(Game game, IceBuilding destination, int inHowManyTurns) {

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
                        else if(currentPenguinAmount < 0){
                            penguinsPerTurn = -((Iceberg) destination).penguinsPerTurn;
                        }

                        // If the iceberg is neutral, it doesn't create penguins.
                        else {
                            penguinsPerTurn = 0;
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
     * Returns how many penguins will one of my icebergs have in turn x.
     * Positive count - mine.
     * Negative count - enemy or neutral.
     * @param game current game state
     * @param myIceBuilding IceBuilding to check
     * @param inHowManyTurns the amount of turns ahead we want to check
     * @return how many penguins will my iceberg have in turn x
     */
    public static int getPenguinAmountInTurnXForMyIceBuilding(Game game, IceBuilding myIceBuilding, int inHowManyTurns) {
        // Initiate result that we will update for every turn
        int currentPenguinAmount = myIceBuilding.penguinAmount;

        // Store 3 maps, one for the incoming my penguin groups, one for the incoming enemy penguin groups and one for the incoming bonus penguins.
        Map<Integer, Integer> incomingMyPenguinGroups = IcebergUtil.howManyMyPenguinsWillArriveToADestinationInXTurns(game, myIceBuilding);
        Map<Integer, Integer> incomingEnemyPenguinGroups = IcebergUtil.howManyEnemyPenguinsWillArriveToADestinationInXTurns(game, myIceBuilding);
        Map<Integer, Integer> incomingBonusPenguins = IcebergUtil.howManyPenguinsWillAnIcebergGetAtEachTurnFromBonusIceberg(game, myIceBuilding);

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
            if(myIceBuilding instanceof Iceberg) {

                // If the destination is neutral, it doesn't create penguins.
                // If the destination is enemy, we need to add the penguins-per-turn.
                // If the destination is mine, we need to subtract the penguins-per-turn, because we are looking from the POV of the enemy in this function.
                String destinationOwner = playerToString(game, myIceBuilding.owner);
                switch (destinationOwner) {
                    case "Neutral":
                        // If the destination is neutral, it doesn't create penguins.
                        penguinsPerTurn = 0;
                        break;
                    case "Enemy":
                        // If the destination is enemy, we need to add the penguins-per-turn.
                        if(currentPenguinAmount > 0) {
                            penguinsPerTurn = ((Iceberg) myIceBuilding).penguinsPerTurn;
                        }

                        // If the destination is mine (the enemy penguin will have a negative penguin amount),
                        // we need to subtract the penguins-per-turn, because we are looking from the POV of the enemy in this function.
                        else if(currentPenguinAmount < 0){
                            penguinsPerTurn = -((Iceberg) myIceBuilding).penguinsPerTurn;
                        }

                        // If the iceberg is neutral, it doesn't create penguins.
                        else {
                            penguinsPerTurn = 0;
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
            Log.log("B_4_1: added penguins per turn and current penguin amount = " + currentPenguinAmount);

            // Update the current penguin amount according to the penguin groups that will be arriving.
            // Incoming my penguin groups
            if(incomingMyPenguinGroups.containsKey(turnCounter)) {
                Log.log("B_4: found a penguin group that I own that will arrive after " + turnCounter + " turns with size " + incomingMyPenguinGroups.get(turnCounter));
                currentPenguinAmount -= incomingMyPenguinGroups.get(turnCounter);
                Log.log("B_4_2: new penguin amount is " + currentPenguinAmount);
            }
            // Incoming enemy penguin groups
            if(incomingEnemyPenguinGroups.containsKey(turnCounter)) {
                Log.log("B_5_0: found a penguin group that the enemy owns that will arrive after " + turnCounter + " turns with size " + incomingMyPenguinGroups.get(turnCounter));
                currentPenguinAmount += incomingEnemyPenguinGroups.get(turnCounter);
                Log.log("B_5_1: new penguin amount is " + currentPenguinAmount);
            }
            // Incoming bonus penguins
            if(incomingBonusPenguins.containsKey(turnCounter)) {
                currentPenguinAmount += incomingBonusPenguins.get(turnCounter);
            }

            // Increment the turn counter
            turnCounter++;

            Log.log("\nB_4_3: After " + turnCounter + " turns, the penguin amount is " + currentPenguinAmount + "\n");
        }

        // Return the penguin amount after the specified number of turns.
        Log.log("B_4_4 Returning: " + currentPenguinAmount);
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


    /**
     * A function that calculates the value of capturing an ice-building.
     * @param game current game state
     * @param destination the ice-building that we are checking its value of capturing
     * @return The value of capturing the destination ice-building.
     */
    public static int getValueOfCapturing(Game game, IceBuilding destination) {

        // If the destination is a normal iceberg, call a separate function that calculates its value of capturing.
        if(destination instanceof Iceberg) {

            // Pass the iceberg representation of the ice-building to the function that calculates its value of capturing.
            return getValueOfCapturingIceberg(game, (Iceberg) destination);
        }

        // If the destination is a normal iceberg, call a separate function that calculates its value of capturing.
        else if (destination instanceof BonusIceberg) {

            // Pass the bonus-iceberg representation of the ice-building to the function that calculates its value of capturing.
            return getValueOfCapturingBonusIceberg(game, (BonusIceberg) destination);
        }

        // Unreachable, this only avoids a missing-return error
        return 0;
    }


    /**
     * A function to calculate the value of capturing an iceberg.
     * @param game current game state
     * @param destination the iceberg to capture
     * @return The value of capturing the iceberg.
     */
    public static int getValueOfCapturingIceberg(Game game, Iceberg destination) {
        // If we are capturing an iceberg, we are gaining its penguins-per-turn.
        int valueOfAttacking = destination.penguinsPerTurn;

        // If the iceberg is an enemy iceberg, not only are we gaining penguins-per-turn, we are also making the enemy lose penguins-per-turn.
        if(playerToString(game, destination.owner).equals("Enemy")) {
            // Take into account the enemy losing penguins-per-turn.
            valueOfAttacking += destination.penguinsPerTurn;
        }

        // Return the value of capturing the iceberg.
        Log.log("B_6_0: value of capturing normal iceberg " + destination + " is " + valueOfAttacking);
        return valueOfAttacking;
    }


    /**
     * A function that calculated the value of capturing a bonus iceberg.
     * @param game current game state
     * @param destination the bonus-iceberg to capture
     * @return the value of capturing the bonus iceberg
     */
    public static int getValueOfCapturingBonusIceberg(Game game, BonusIceberg destination) {
        // The value that the bonus iceberg provides to each iceberg on each turn per average.
        double valuePerIceberg = BonusIcebergUtil.getAveragePenguinsPerTurnPerIceberg(game);

        // Store how many icebergs do I have, how many does the enemy have, and how many are neutral.
        int myIcebergAmount = game.getMyIcebergs().length;
        int enemyIcebergAmount = game.getEnemyIcebergs().length;
        int neutralIcebergAmount = game.getNeutralIcebergs().length;

        // Neutral icebergs are worth less than captured icebergs, this is because they will only impact in the future.
        // The value of this factor is not proven to be correct, but it is a good approximation. We might want to change this in the future.
        double neutralIcebergFactor = 0.5;

        // The value of attacking the bonus iceberg is the value per iceberg times the number of icebergs plus a factor for neutral icebergs.
        int valueOfAttacking = (int) (valuePerIceberg * (myIcebergAmount + enemyIcebergAmount + neutralIcebergAmount * neutralIcebergFactor));

        // Return the calculated value.
        Log.log("B_6_1: value of capturing bonus iceberg " + destination + " is " + valueOfAttacking);
        return valueOfAttacking;
    }
}
