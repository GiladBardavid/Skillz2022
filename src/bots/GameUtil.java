package bots;

import penguin_game.*;
import java.util.*;

public class GameUtil {

    /**
     * Returns how many penguins will an enemy iceberg have in turn x.
     * @param destination iceberg to check
     * @param turnsTillArrival turn to check
     * @return how many penguins will the enemy iceberg have in turn x
     */
    public static int howManyPenguinsWillDestinationHave(Game game, Iceberg destination, int turnsTillArrival) {
        //TODO simulate turns and also calculate bonus icebergs

        // Current penguin amount in the iceberg
        int currentAmount = destination.penguinAmount;

        // The amount of penguins that the iceberg will generate by the time the attack arrives
        int amountThatWillBeGenerated = (turnsTillArrival + 1) * destination.penguinsPerTurn;

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
        int amountOFEnemyPenguinsThatWillArriveByTurnX = 0;
        for(PenguinGroup enemyPenguinGroup : game.getEnemyPenguinGroups()) {
            // Check if the penguin group is going to the destination and will arrive before the attack
            if(enemyPenguinGroup.destination == destination && enemyPenguinGroup.turnsTillArrival <= turnsTillArrival) {

                //Add the penguins that will arrive by turn x
                amountOFEnemyPenguinsThatWillArriveByTurnX += enemyPenguinGroup.penguinAmount;

            }
        }

        // We want to return the amount of penguins that the enemy already has in the iceberg plus the amount of penguins that it will generate plus
        // the amount of enemy penguins that will help the destination iceberg minus the amount of penguins that I already sent to the destination
        // (that will arrive in time).
        int result = currentAmount + amountThatWillBeGenerated + amountOFEnemyPenguinsThatWillArriveByTurnX - amountOfMyPenguinsThatWillArriveByTurnX;
        Log.log("\nB_0: " + destination + " will have " + result + " penguins in " + turnsTillArrival + " turns.\n");
        return result;
    }
}
