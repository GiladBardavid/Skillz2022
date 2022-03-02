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

    public static Game game = null;

    List<Action> ongoingActions = new ArrayList<>();

    public static boolean DO_NOTHING = false;
    public static boolean DONT_CREATE_NEW_ATTACKS = false;
    public static int ONLY_PRINT_FROM_TURN = 143;


    /**
     * Does the turn. This function is called by the system.
     * @param game current game state
     */
    public void doTurn(Game game) {

        this.game = game;

        // TODO remove, this is only to prevent a bad move in the circle map
        if(game.turn == 2) {
            if (game.getMyIcebergs()[0].getTurnsTillArrival(game.getNeutralIcebergs()[0]) == 9) {
                return;
            }
        }

        /*if(game.turn == 1) return;

        if(game.turn == 2) {
            game.getMyIcebergs()[0].upgrade();
            return;
        }*/

        /*if(game.turn == 21) {
            game.getMyIcebergs()[2].sendPenguins(game.getEnemyIcebergs()[0], 1);
            game.getMyIcebergs()[2].sendPenguins(game.getMyIcebergs()[1], 1);
        }*/

        /*for(PenguinGroup penguinGroup : game.getMyPenguinGroups()) {
            log("penguin group " + penguinGroup + " will arrive in: " + penguinGroup.turnsTillArrival + " turns");
        }*/

        if(DO_NOTHING) {
            return;
        }

        if(game.turn == 1) {
            Log.IS_DEBUG = false;
        }
        if(game.turn == ONLY_PRINT_FROM_TURN) {
            Log.IS_DEBUG = true;
        }

        /*if(game.turn == 19) {
            DONT_CREATE_NEW_ATTACKS = true;
        }*/

        /*log("maxTurnsToBonus: " + game.getBonusIceberg().maxTurnsToBonus);
        log("turnsLeftToBonus: " + game.getBonusIceberg().turnsLeftToBonus);
        log("penguinBonus: " + game.getBonusIceberg().penguinBonus);*/


        // Update static states
        GameUtil.updateTurnState(game);

        // age ongoing actions
        List<Action> newOngoingActions = new ArrayList<>();
        for(Action action : ongoingActions) {
            Action newAction = action.ageByTurn();
            if(newAction != null) {
                newOngoingActions.add(newAction);
            }
        }
        ongoingActions = newOngoingActions;

        log("ongoing actions: " + ongoingActions.size());
        for(Action action : ongoingActions) {
            log(action.toString());
        }
        log("------------------");

        Prediction prediction = GameUtil.prediction;
        log("Start prediction: " + prediction);

        Set<Iceberg> cannotSendNow = new HashSet<>();
        Set<Iceberg> cannotUpgradeNow = new HashSet<>();

        List<Action> executedActions = new ArrayList<>();


        while(true) {


            List<Action> candidateActions = createAllActions(game, prediction, cannotSendNow, cannotUpgradeNow, executedActions);
            log("candidateActions size: " + candidateActions.size());


            // Sort actions
            for (Action action : candidateActions) {
                action.computeScore(game);
            }


            // Filter out all elements from candidateActions whos score is 0 using a stream.
            candidateActions = candidateActions.stream()
                    .filter(action -> action.score > 0 && !executedActions.contains(action))
                    .filter(action -> (action.predictionAfterAction.computeScore() > action.predictionBeforeAction.computeScore()) || !action.mustImprovePrediction())
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

            log("printing candidate actions");
            for (Action action : candidateActions) {
                log(action.toString());
                log("Score: " + action.score);
            }


            if (candidateActions.size() > 0) {
                Action bestAction = candidateActions.get(0);

                log("\nBest action: " + bestAction.toString() + "\n" + "  Score: " + bestAction.score + "\n");

                /*bestAction.executeIfPossible(game);*/

                executedActions.add(bestAction);

                cannotSendNow.addAll(bestAction.getIcebergsThatUpgradedNow());
                /*log("Adding icebergs that sent now: " + bestAction.getIcebergsThatSentNow());*/
                cannotUpgradeNow.addAll(bestAction.getIcebergsThatSentNow());
                cannotUpgradeNow.addAll(bestAction.getIcebergsThatUpgradedNow());
                /*log("Adding icebergs that upgraded now: " + bestAction.getIcebergsThatUpgradedNow());*/
            }


            // Recalc prediction
            prediction = new Prediction(game, executedActions);
            /*log("Best action prediction after: " + prediction);*/
            /*log("New Prediction: " + prediction);*/
        }

        for(Action action : executedActions) {
            action.executeIfPossible(game);

            /*if(!ongoingActions.contains(action)) {
                ongoingActions.add(action);
            }
            log("Ongoing actions new: " + ongoingActions);*/
        }
        ongoingActions = executedActions;
        log("Ongoing actions new: " + ongoingActions);
    }



    /**
     * Create all possible actions
     * @param game current game state
     * @return list of all possible actions
     */
    public List<Action> createAllActions(Game game, Prediction prediction, Set<Iceberg> cannotSendNow, Set<Iceberg> cannotUpgradeNow, List<Action> executedActions) {
        if(!prediction.isValid) {
            log(prediction);
            if(Log.IS_DEBUG) {
                throw new IllegalStateException("Prediction is not valid");
            }
        }

        List<Action> actions = new ArrayList<>();


        // Add all ongoing actions.
        for(Action ongoingAction : ongoingActions) {
            if(executedActions.contains(ongoingAction)) {
                continue;
            }

            List<Action> actionsToTest = new ArrayList<>(executedActions);
            actionsToTest.add(ongoingAction);

            Prediction predictionAfterAction = new Prediction(game, actionsToTest);

            log("Ongoing action: " + ongoingAction.toString());
            /*log("Prediction after Ongoing action: " + predictionAfterAction);*/

            if(predictionAfterAction.isValid) {
                ongoingAction.predictionAfterAction = predictionAfterAction;
                ongoingAction.predictionBeforeAction = prediction;
                actions.add(ongoingAction);
            }
            else {
                log("Ongoing action is invalid: " + ongoingAction);
            }
        }

        /*actions.addAll(ongoingActions);*/

        if(DONT_CREATE_NEW_ATTACKS) return actions;

        // Create attack actions
        for(IceBuilding iceBuilding : GameUtil.getAllIceBuildings(game)) {
            boolean hasAttackOnThisIceberg = false;
            for(Action executedAction : executedActions) {
                if(executedAction instanceof AttackAction && ((AttackAction) executedAction).plan.target == iceBuilding) {
                    hasAttackOnThisIceberg = true;
                    break;
                }
            }
            if(hasAttackOnThisIceberg) continue;


            AttackPlan plan = GameUtil.planAttack(game, prediction, iceBuilding, cannotSendNow);
            if(plan != null) {
                /*log("Prediction = " + prediction.iceBuildingStateAtWhatTurn.get(iceBuilding));*/
                log(plan.toString());

                AttackAction action = new AttackAction(plan);
                List<Action> actionsToTest = new ArrayList<>(executedActions);
                actionsToTest.add(action);

                /*log("Prediction before action: " + prediction);*/
                /*log("Actions to test: " + actionsToTest);*/
                Prediction predictionAfterAction = new Prediction(game, actionsToTest);
                log("Action: " + action.toString());
                /*log("Prediction after action: " + predictionAfterAction);*/

                if(predictionAfterAction.isValid) {
                    action.predictionAfterAction = predictionAfterAction;
                    action.predictionBeforeAction = prediction;
                    actions.add(action);
                }
            }
        }

        // Create upgrade actions
        for(Iceberg myIceberg : game.getMyIcebergs()) {
            if(myIceberg.canUpgrade() && !cannotUpgradeNow.contains(myIceberg)) {
                UpgradeAction action = new UpgradeAction(myIceberg);

                List<Action> actionsToTest = new ArrayList<>(executedActions);
                actionsToTest.add(action);

                Prediction predictionAfterAction = new Prediction(game, actionsToTest);

                if(predictionAfterAction.isValid) {
                    action.predictionAfterAction = predictionAfterAction;
                    action.predictionBeforeAction = prediction;
                    actions.add(action);
                }

            }
        }


        //Create defend actions
        for(Iceberg myIceberg : game.getMyIcebergs()) {
            if(myIceberg.level != myIceberg.upgradeLevelLimit) continue;

            int maxThatCanSend = prediction.getMaxThatCanSend(myIceberg);
            if(maxThatCanSend == 0) continue;

            Iceberg closestIcebergThatIsNotMaxLevel = GameUtil.getClosestIcebergThatIsNotMaxLevel(game, myIceberg);
            if(closestIcebergThatIsNotMaxLevel == null) continue;

            DefendAction action = new DefendAction(myIceberg, closestIcebergThatIsNotMaxLevel, maxThatCanSend);

            log("Checking defend action: " + action.toString());

            List<Action> actionsToTest = new ArrayList<>(executedActions);
            actionsToTest.add(action);

            Prediction predictionAfterAction = new Prediction(game, actionsToTest);

            if(predictionAfterAction.isValid) {
                action.predictionAfterAction = predictionAfterAction;
                action.predictionBeforeAction = prediction;
                actions.add(action);

                log("Added defend action: " + action.toString());
            }
        }

        // TODO add bridge action.

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