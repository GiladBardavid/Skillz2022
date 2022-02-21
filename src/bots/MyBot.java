package bots;
import penguin_game.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * "The" Skillz 2022 Code
 *
 *
 */
public class MyBot implements SkillzBot {



    /**
     * Does the turn. This function is called by the system.
     * @param game current game state
     */
    public void doTurn(Game game) {


        /*log("maxTurnsToBonus: " + game.getBonusIceberg().maxTurnsToBonus);
        log("turnsLeftToBonus: " + game.getBonusIceberg().turnsLeftToBonus);
        log("penguinBonus: " + game.getBonusIceberg().penguinBonus);*/


        // Update static states
        GameUtil.updateTurnState(game);

        Prediction prediction = GameUtil.prediction;
        /*log("Start prediction: " + prediction);*/

        Set<Iceberg> cannotSendNow = new HashSet<>();
        Set<Iceberg> cannotUpgradeNow = new HashSet<>();

        List<Action> executedActions = new ArrayList<>();

        while(true) {


            List<Action> candidateActions = createAllActions(game, prediction, cannotSendNow, cannotUpgradeNow);
            log("candidateActions size: " + candidateActions.size());


            // Sort actions
            for (Action action : candidateActions) {
                action.computeScore(game);
            }


            // Filter out all elements from candidateActions whos score is 0 using a stream.
            candidateActions = candidateActions.stream()
                    .filter(action -> action.score > 0)
                    .collect(Collectors.toList());


            if (candidateActions.size() == 0) {
                log("No more actions to take");
                break;
            }

            Collections.sort(candidateActions, new Comparator<Action>() {
                @Override
                public int compare(Action o1, Action o2) {
                    return Double.compare(o2.score, o1.score);
                }
            });


            for (Action action : candidateActions) {
                log(action.toString());
                log("Score: " + action.score);
            }


            if (candidateActions.size() > 0) {
                Action bestAction = candidateActions.get(0);

                log("\nBest action: " + bestAction.toString() + "\n");

                bestAction.executeIfPossible(game);

                executedActions.add(bestAction);

                cannotSendNow.addAll(bestAction.getIcebergsThatUpgradedNow());
                /*log("Adding icebergs that sent now: " + bestAction.getIcebergsThatSentNow());*/
                cannotUpgradeNow.addAll(bestAction.getIcebergsThatSentNow());
                cannotUpgradeNow.addAll(bestAction.getIcebergsThatUpgradedNow());
                /*log("Adding icebergs that upgraded now: " + bestAction.getIcebergsThatUpgradedNow());*/
            }


            // Recalc prediction
            prediction = new Prediction(game, executedActions);
            /*log("New Prediction: " + prediction);*/
        }
    }



    /**
     * Create all possible actions
     * @param game current game state
     * @return list of all possible actions
     */
    public List<Action> createAllActions(Game game, Prediction prediction, Set<Iceberg> cannotSendNow, Set<Iceberg> cannotUpgradeNow) {
        List<Action> actions = new ArrayList<>();

        // Create attack actions
        for(IceBuilding iceBuilding : GameUtil.getAllIceBuildings(game)) {
            AttackPlan plan = GameUtil.planAttack(game, prediction, iceBuilding, cannotSendNow);
            if(plan != null) {
                log("Prediction = " + prediction.iceBuildingStateAtWhatTurn.get(iceBuilding));
                log(plan.toString());

                actions.add(new AttackAction(plan));
            }
        }

        for(Iceberg myIceberg : game.getMyIcebergs()) {
            if(myIceberg.canUpgrade() && !cannotUpgradeNow.contains(myIceberg)) {
                actions.add(new UpgradeAction(myIceberg));
            }
        }

        // TODO add defend action, bridge action.

        return actions;
    }




    /**
     * A function to print strings if we are in debug mode
     * @param toPrint The string to print
     */
    public void log(Object toPrint) {
        Log.log(toPrint);
    }
}