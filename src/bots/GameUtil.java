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
        return 0; // temp
        /*// Initiate result that we will update for every turn
        int currentPenguinAmount = destination.penguinAmount;

        // A variable to keep track of how many turns we have gone through
        int turnCounter = 0;

        while(turnCounter < inHowManyTurns) {

            // Add the destination's penguins-per-turn
            int penguinsPerTurn;
            if(destination instanceof Iceberg) {
                Player destinationOwner = destination.owner;
                switch (destinationOwner) {
                    case game.getNeutral():
                        penguinsPerTurn = 0;
                        break;
                    case game.getEnemy():

                }
            }
        }*/
    }






    // ------------------------------ OLD FUNCTION - NEW ON ABOVE THIS COMMENT ------------------------------------------------

    /*public static int howManyPenguinsWillDestinationHave(Game game, IceBuilding destination, int turnsTillArrival) {

        // Current penguin amount in the iceberg
        int currentAmount = destination.penguinAmount;

        // If the IceBuilding is an iceberg, it's penguinsPerTurn is just simply equals to the iceberg's penguinPerTurn attribute.
        // Else, it generated 0 penguins per turn.
        int penguinsGeneratedPerTurn = destination instanceof Iceberg ? ((Iceberg)destination).penguinsPerTurn : 0;

        // The amount of penguins that the iceberg will generate by the time the attack arrives
        int amountThatWillBeGenerated = turnsTillArrival * penguinsGeneratedPerTurn;

        // The amount of penguins that I have already sent to the destination that will also arrive before the attack
        int amountOfMyPenguinsThatWillArriveByTurnX = 0;
        for(PenguinGroup myPenguinGroup : game.getMyPenguinGroups()) {
            // Check if the penguin group is going to the destination and will arrive before the attack
            if(myPenguinGroup.destination == destination && myPenguinGroup.turnsTillArrival <= turnsTillArrival) {

                //Add the penguins that will arrive by turn x
                amountOfMyPenguinsThatWillArriveByTurnX += myPenguinGroup.penguinAmount;

            }
        }

        // The amount of penguins that the enemy has already sent to the destination that will also arrive before the attack.
        // This is most likely the enemy's help-penguins
        int amountOfEnemyPenguinsThatWillArriveByTurnX = 0;
        for(PenguinGroup enemyPenguinGroup : game.getEnemyPenguinGroups()) {
            // Check if the penguin group is going to the destination and will arrive before the attack
            if(enemyPenguinGroup.destination == destination && enemyPenguinGroup.turnsTillArrival <= turnsTillArrival) {

                //Add the penguins that will arrive by turn x
                amountOfEnemyPenguinsThatWillArriveByTurnX += enemyPenguinGroup.penguinAmount;

            }
        }

        // We want to return the amount of penguins that the enemy already has in the iceberg plus the amount of penguins that it will generate plus
        // the amount of enemy penguins that will help the destination iceberg minus the amount of penguins that I already sent to the destination
        // (that will arrive in time).
        int result = currentAmount + amountThatWillBeGenerated + amountOfEnemyPenguinsThatWillArriveByTurnX - amountOfMyPenguinsThatWillArriveByTurnX;
        Log.log("\nB_0: " + destination + " will have " + result + " penguins in " + turnsTillArrival + " turns.\n");
        Log.log("B_1: this is because currentAmount = " + currentAmount + ",\n amountThatWillBeGenerated = " + amountThatWillBeGenerated + ",\n amountOfEnemyPenguinsThatWillArriveByTurnX = " + amountOfEnemyPenguinsThatWillArriveByTurnX + ",\n amountOfMyPenguinsThatWillArriveByTurnX = " + amountOfMyPenguinsThatWillArriveByTurnX + "\n");
        return result;
    }*/


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
