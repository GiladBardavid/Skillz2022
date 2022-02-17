package bots;
import penguin_game.*;
import java.util.*;

/**
 * "The" Skillz 2022 Code
 *
 * @author Gilad "carry" B.
 * @author Ilay "cyber" B.
 * @author Sagi "8200" G.
 */
public class MyBot implements SkillzBot {

    /**
     * Does the turn. This function is called by the system.
     * @param game current game state
     */
    public void doTurn(Game game) {


        log("maxTurnsToBonus: " + game.getBonusIceberg().maxTurnsToBonus);
        log("turnsLeftToBonus: " + game.getBonusIceberg().turnsLeftToBonus);
        log("penguinBonus: " + game.getBonusIceberg().penguinBonus);

        log("my bonus iceberg: " + game.getMyBonusIceberg());
        log("enemy bonus iceberg: " + game.getEnemyBonusIceberg());
        log("neutral bonus iceberg: " + game.getNeutralBonusIceberg());

        log("me: " + GameUtil.playerToString(game, game.getMyself()) + " enemy: " + GameUtil.playerToString(game, game.getEnemy()) + " neutral: " + GameUtil.playerToString(game, game.getNeutral()));
        log("");

        // Find and store the best actions to perform
        PriorityQueue<IceBuilding> bestActionsToPerform = GameUtil.getPriorityQueueOfIceBuildings(game);
        log("bestActionsToPerform: " + bestActionsToPerform);



        // --------------------------------------------------------------


        for(Iceberg myIceberg : game.getMyIcebergs()) {

            boolean foundAGoalICouldntDo = false;
            while (!foundAGoalICouldntDo) {
                IceBuilding destination = bestActionsToPerform.peek();

                int distanceBetweenMyIcebergAndDestination = myIceberg.getTurnsTillArrival(destination);
                log("distanceBetweenMyIcebergAndDestination: " + distanceBetweenMyIcebergAndDestination);

                int howManyPenguinsToSend = GameUtil.getPenguinAmountInTurnXForEnemyOrNeutralIceBuilding(game, destination, distanceBetweenMyIcebergAndDestination) + 1;
                log("howManyPenguinsToSend: " + howManyPenguinsToSend);

                log("my penguin amount: " + myIceberg.penguinAmount);
                if (howManyPenguinsToSend > 0 && myIceberg.penguinAmount >= howManyPenguinsToSend) {
                    log(myIceberg + " sending " + howManyPenguinsToSend + " penguins to " + destination);
                    myIceberg.sendPenguins(destination, howManyPenguinsToSend);
                    bestActionsToPerform.poll();
                }
                else {
                    foundAGoalICouldntDo = true;
                }
            }
        }
    }




    /**
     * A function to print strings if we are in debug mode
     * @param toPrint The string to print
     */
    public void log(Object toPrint) {
        Log.log(toPrint);
    }
}