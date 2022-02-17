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


        // Find and store the best actions to perform
        PriorityQueue<IceBuilding> bestActionsToPerform = GameUtil.getPriorityQueueOfIceBuildings(game);

        // Temporarily change our data structure from PriorityQueue to ArrayList
        List<IceBuilding> bestTargetsToAttack = new ArrayList<>();
        while (!bestActionsToPerform.isEmpty()) {
            bestTargetsToAttack.add(bestActionsToPerform.poll());
        }

        // Remove from the targets list the ones that will already be mine
        for(int i = 0; i < bestTargetsToAttack.size(); i++) {
            IceBuilding iceBuildingToCheck = bestTargetsToAttack.get(i);
            
            if(NeededHelp.willBeMine(game, iceBuildingToCheck)) {
                bestTargetsToAttack.remove(i);
                i--;
            }
        }

        log("bestActionsToPerform: " + bestActionsToPerform);

        log("\nbestTargetsToAttack: " + bestTargetsToAttack + "\n");
        for(int i = 0; i < bestTargetsToAttack.size(); i++) {
            log("bestTargetsToAttack[" + i + "] value is: " + GameUtil.getValueOfCapturing(game, bestTargetsToAttack.get(i)));
        }
        log("\n");


        Set<Iceberg> icebergsThatHaveSentPenguins = new HashSet<>();
        Set<Iceberg> icebergsThatHaveUpgraded = new HashSet<>();

        Set<IceBuilding> iceBuildingsThatGotFullHelp = new HashSet<>();



        for(IceBuilding myIceBuilding : GameUtil.getMyIceBuildings(game)) {
            log("TIME1: time left: " + game.getTimeRemaining());
            log("checking help for ice-building: " + myIceBuilding);
            NeededHelp helpForCurrentIceberg = NeededHelp.getNeededHelp(game, myIceBuilding);
            if(helpForCurrentIceberg != null) {
                log("Needed help for: " + myIceBuilding);

                int neededAmount = helpForCurrentIceberg.howManyPenguins;
                for(Iceberg myIceberg : game.getMyIcebergs()) {

                    boolean isCloseEnoughToSendHelp = myIceberg.getTurnsTillArrival(myIceBuilding) <= helpForCurrentIceberg.inHowManyTurns;
                    if(myIceberg.penguinAmount >= neededAmount && isCloseEnoughToSendHelp) {
                        log("Sending help to: " + myIceberg);
                        log(myIceberg + " is sending " + neededAmount + " penguins to " + myIceBuilding);
                        myIceberg.sendPenguins(myIceBuilding, neededAmount);
                        icebergsThatHaveSentPenguins.add(myIceberg);

                        iceBuildingsThatGotFullHelp.add(myIceBuilding);
                        // Break because the iceberg got full help, so we don't need another one to help it aswell
                        break;
                    }
                }
            }
        }

        log("\nFINISHED HELPING MINE\n");


        for(IceBuilding neutralIceBuilding : GameUtil.getNeutralIceBuildings(game)) {
            log("Checking neutral help for ice-building: " + neutralIceBuilding);
            NeededHelp helpForNeutralIceberg = NeededHelp.getNeededHelpForNeutralIceberg(game, neutralIceBuilding);
            if(helpForNeutralIceberg != null) {
                log("Needed help for: " + neutralIceBuilding);

                int neededAmount = helpForNeutralIceberg.howManyPenguins;
                for(Iceberg myIceberg : game.getMyIcebergs()) {

                    boolean isInRangeToSendHelp = myIceberg.getTurnsTillArrival(neutralIceBuilding) == helpForNeutralIceberg.inHowManyTurns;
                    if(myIceberg.penguinAmount >= neededAmount && isInRangeToSendHelp) {
                        log("Sending help to: " + myIceberg);
                        log(myIceberg + " is sending " + neededAmount + " penguins to " + neutralIceBuilding);
                        myIceberg.sendPenguins(neutralIceBuilding, neededAmount);
                        icebergsThatHaveSentPenguins.add(myIceberg);

                        iceBuildingsThatGotFullHelp.add(neutralIceBuilding);
                        // Break because the iceberg got full help, so we don't need another one to help it aswell
                        break;
                    }
                }
            }
        }

        log("\nFINISHED HELPING NEUTRAL\n");



        for(Iceberg myIceberg : game.getMyIcebergs()) {
            // We don't want icebergs that are under attack to send penguins --- not optimal
            if(GameUtil.isGettingHelp(game, myIceberg)) {
                continue;
            }


            for(int i = 0; i < bestTargetsToAttack.size(); i++) {
                // Temp change priority queue to list
                IceBuilding destination = /*bestActionsToPerform.peek()*/bestTargetsToAttack.get(i);

                int distanceBetweenMyIcebergAndDestination = myIceberg.getTurnsTillArrival(destination);
                log("distanceBetweenMyIcebergAndDestination: " + distanceBetweenMyIcebergAndDestination);

                int howManyPenguinsToSend = GameUtil.getPenguinAmountInTurnXForEnemyOrNeutralIceBuilding(game, destination, distanceBetweenMyIcebergAndDestination) + 1;
                log("howManyPenguinsToSend: " + howManyPenguinsToSend);

                log("my penguin amount: " + myIceberg.penguinAmount);
                if (!icebergsThatHaveUpgraded.contains(myIceberg) && howManyPenguinsToSend > 0 && myIceberg.penguinAmount >= howManyPenguinsToSend) {
                    log(myIceberg + " sending " + howManyPenguinsToSend + " penguins to " + destination);
                    myIceberg.sendPenguins(destination, howManyPenguinsToSend);
                    icebergsThatHaveSentPenguins.add(myIceberg);
                    /*bestActionsToPerform.poll();*/
                    // temp
                    bestTargetsToAttack.remove(i);
                    i--;
                }
                /*else {
                    foundAGoalICouldntDo = true;
                }*/
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