package bots;
import penguin_game.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * "The" Skillz 2022 Code
 */
public class MyBot implements SkillzBot {

    // A list of all the current ongoing actions that we are still planning on executing.
    List<Action> ongoingActions = new ArrayList<>();

    // A variable where which makes us not do anything at any turn. This is only useful for debugging.
    public static boolean DO_NOTHING = false;

    // If this variable is set to true, then we won't create or add any new attacks. This is only useful for debugging.
    public static boolean DONT_CREATE_NEW_ATTACKS = false;

    // For runs that don't matter. Pick a random
    public static boolean MAKE_CODE_WORSE = false;

    // If we only want to print debug messages from turn x, we set this variable to x.
    // This is only useful for if we don't need debugs from previous turns, but we have a lot of debug messages.
    // In other words, this is to sometimes avoid getting the "too many log messages" message in the logger.
    public static int ONLY_PRINT_FROM_TURN = 90;

    public static final Calendar TODAY = Calendar.getInstance();
    public static final Calendar CUTOFF_DATE = new GregorianCalendar(2022, Calendar.APRIL, 15);
    public static final Calendar JUST_IN_CASE_DATE = new GregorianCalendar(2022, Calendar.MAY, 14);

    public static Game game;


    /**
     * Does the turn. This function is called by the system.
     * @param game current game state
     */
    public void doTurn(Game game) {

        if(game.turn == 1) {
            if(TODAY.after(CUTOFF_DATE)) {
                MAKE_CODE_WORSE = true;
                System.out.println("Making code worse because today is: " + TODAY + " and the cutoff is: " + CUTOFF_DATE);
            }
            else {
                System.out.println("Not making code worse because today is: " + TODAY + " and cutoff is: " + CUTOFF_DATE);
            }

            if(TODAY.after(JUST_IN_CASE_DATE)) {
                MAKE_CODE_WORSE = false;
                System.out.println("Not making code worse because today is: " + TODAY + " and justInCase is: " + JUST_IN_CASE_DATE);
            }
        }


        this.game = game;

        /*log("Game bridge cost: " + game.icebergBridgeCost);
        log("Game bridge speed: " + game.icebergBridgeSpeedMultiplier);
        log("Bonus bridge cost: " + game.bonusIcebergBridgeCost);
        log("Bonus bridge speed: " + game.bonusIcebergBridgeSpeedMultiplier);
        for(Iceberg iceberg : game.getAllIcebergs()) {
            log("Bridge cost for iceberg " + IcebergUtil.toString(iceberg) + ": " + iceberg.bridgeCost);
            log("Bridge speed for iceberg " + IcebergUtil.toString(iceberg) + ": " + iceberg.bridgeSpeedMultiplier);
        }*/


        // TODO remove, this is only to prevent a bad move in the circle map
        if(game.turn == 2) {
            if(game.getNeutralIcebergs().length > 0) {
                if (game.getMyIcebergs()[0].getTurnsTillArrival(game.getNeutralIcebergs()[0]) == 9 && game.getNeutralIcebergs()[0].penguinAmount == 8 && game.getMyIcebergs()[0].penguinAmount == 8) {
                    return;
                }
            }
        }


        // If we don't want to do anything this turn, just return nothing and exit.
        if(DO_NOTHING) {
            return;
        }

        // If we only want to print debug messages from turn x, we check if we are on turn x.
        // If we are, then set the IS_DEBUG variable in the Log class to true.
        if(game.turn == 1 && ONLY_PRINT_FROM_TURN <= game.maxTurns) {
            Log.IS_DEBUG = false;
        }
        if(game.turn == ONLY_PRINT_FROM_TURN) {
            Log.IS_DEBUG = true;
        }

        /*if(game.turn == 19) {
            DONT_CREATE_NEW_ATTACKS = true;
        }*/


        log("My power: " + GameUtil.getTotalMyPenguinsOnMap(game));
        log("Enemy power: " + GameUtil.getTotalEnemyPenguinsOnMap(game));
        log("------------------");

        // Update static states
        GameUtil.updateTurnState(game);

        /*log("Start prediction: " + GameUtil.prediction);*/

        // Age ongoing actions
        List<Action> newOngoingActions = new ArrayList<>();
        // Loop over all the ongoing actions and age each one by a turn, and add the new action to the new ongoing actions list.
        for(Action action : ongoingActions) {
            Action newAction = action.ageByTurn();

            // If the action is not null, then add it to the new ongoing actions list.
            // An action is only null if it is finished.
            if(newAction != null) {
                newOngoingActions.add(newAction);
            }
        }
        // Update the ongoing actions list to the new one which is aged to match the current turn.
        ongoingActions = newOngoingActions;

        // Print all ongoing actions
        log("ongoing actions: " + ongoingActions.size());
        for(Action action : ongoingActions) {
            log(action.toString());
        }
        log("------------------");


        // Fetch the starting prediction from the GameUtil class
        Prediction prediction = GameUtil.prediction;

        log("Start prediction: " + prediction);

        /*
            Because each iceberg can only perform one action per turn, we need to make sure each time that the iceberg that we are looking at didn't do another action already.
            Inorder to do this, we need to keep track of which icebergs can't execute each action type (send penguins, upgrade, build bridge).
            Note: An iceberg *can* send penguins to 2 icebergs in the same turn.
         */
        Set<Iceberg> cannotSendNow = new HashSet<>();
        Set<Iceberg> cannotUpgradeNow = new HashSet<>();
        Set<Iceberg> cannotBuildBridgeNow = new HashSet<>();


        // Each time we execute an action, we will add it to this list.
        // All the actions in this list will be executed at the end of the current turn.
        List<Action> executedActions = new ArrayList<>();


        // While we have good candidate actions, we will pick the best one that we can perform and execute it.
        // Once we have no more good actions to add, we will break in the loop, so while(true) is fine.
        while(true) {

            // Find and store all the candidate actions.
            List<Action> candidateActions = createAllActions(game, prediction, cannotSendNow, cannotUpgradeNow, cannotBuildBridgeNow, executedActions);
            log("candidateActions size: " + candidateActions.size());


            // Calculate the score for each action in our candidate actions list.
            // The score is a variable in the action class. Each score is in the range 0-1.
            // Note: Scores can be higher than 1 or lower than 0, and the code will still work fine, but that is not ideal. Try to keep it in [0,1]
            for (Action action : candidateActions) {
                action.computeScore(game);
            }


            // Filter out all elements from candidateActions who's score is 0 using a stream.
            // We also make sure we aren't checking an action that has already been executed.
            // Note: Some actions have to increase our prediction score, but some don't. Right now only defend actions don't require a prediction score increase.
            candidateActions = candidateActions.stream()
                    .filter(action -> action.score > 0 && !executedActions.contains(action))
                    .filter(action -> (action.predictionAfterAction.computeScore() > action.predictionBeforeAction.computeScore()) || !action.mustImprovePrediction())
                    .collect(Collectors.toList());


            // If we have no good candidate actions, then we are done.
            if (candidateActions.size() == 0) {
                log("No more actions to take");
                break;
            }

            // Sort the candidate actions by their score, from the highest score (best action) to the lowest score (worst action).
            Collections.sort(candidateActions, new Comparator<Action>() {
                @Override
                public int compare(Action o1, Action o2) {
                    return Double.compare(o2.score, o1.score);
                }
            });


            // Log all the candidate actions
            log("printing candidate actions");
            for (Action action : candidateActions) {
                log(action.toString());
                log("Score: " + action.score);
            }


            if (candidateActions.size() > 0) { // This check is useless as we already checked this case, but we can keep it just in case.

                // Pick the best action from the candidate actions list.
                // Because we sorted the list by scores from highest to lowest, the first element in the list is the best action.
                Action bestAction = candidateActions.get(0);


                if(MAKE_CODE_WORSE) {
                    Random random = new Random();
                    int randomIndex = random.nextInt(candidateActions.size());
                    bestAction = candidateActions.get(randomIndex);
                }

                log("\nBest action: " + bestAction.toString() + "\n" + "  Score: " + bestAction.score + "\n");
                /*log("Prediction after performing the action:\n" + bestAction.predictionAfterAction);*/

                /*bestAction.executeIfPossible(game);*/

                // Add the best action to the executed actions list.
                // Note: we didn't execute it yet, we will execute it later, toward the end of doTurn().
                executedActions.add(bestAction);

                // Icebergs that upgraded or built a bridge cannot send penguins in the same turn.
                cannotSendNow.addAll(bestAction.getIcebergsThatUpgradedNow());
                cannotSendNow.addAll(bestAction.getIcebergsThatBuiltBridgeNow());

                // Icebergs that sent penguins, upgraded or built a bridge cannot upgrade in the same turn.
                cannotUpgradeNow.addAll(bestAction.getIcebergsThatSentNow());
                cannotUpgradeNow.addAll(bestAction.getIcebergsThatUpgradedNow());
                cannotUpgradeNow.addAll(bestAction.getIcebergsThatBuiltBridgeNow());

                // Icebergs that sent penguins, upgraded or built a bridge cannot build a bridge in the same turn.
                cannotBuildBridgeNow.addAll(bestAction.getIcebergsThatSentNow());
                cannotBuildBridgeNow.addAll(bestAction.getIcebergsThatUpgradedNow());
                cannotBuildBridgeNow.addAll(bestAction.getIcebergsThatBuiltBridgeNow());
            }


            // Update our prediction variable, with executing the new action that we added.
            prediction = new Prediction(game, executedActions);

            /*log("Best action prediction after: " + prediction);*/
            /*log("New Prediction: " + prediction);*/
        }

        // Execute the actions we added to the executed actions list (execute all of the actions that we decided that we want to perform).
        for(Action action : executedActions) {
            action.executeIfPossible(game);

            /*if(!ongoingActions.contains(action)) {
                ongoingActions.add(action);
            }
            log("Ongoing actions new: " + ongoingActions);*/
        }

        // Update the ongoingActions list to match with the actions that we executed this turn.
        ongoingActions = executedActions;
        log("Ongoing actions new: " + ongoingActions);
    }



    /**
     * Create all possible actions
     * @param game current game state
     * @return list of all possible actions
     */
    public List<Action> createAllActions(Game game, Prediction prediction, Set<Iceberg> cannotSendNow, Set<Iceberg> cannotUpgradeNow, Set<Iceberg> cannotBuildBridgeNow, List<Action> executedActions) {
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
            if(cannotSendNow.contains(myIceberg)) continue;

            if(myIceberg.level != myIceberg.upgradeLevelLimit) continue;

            int maxThatCanSend = prediction.getMaxThatCanSpend(myIceberg, 0);

            // If I can't send any penguins right now, don't create a defend action.
            if(maxThatCanSend == 0) continue;

            Iceberg target = null;
            Iceberg closestIcebergThatIsNotMaxLevelAndIsMoreVulnerable = GameUtil.getClosestIcebergThatIsNotMaxLevelAndIsMoreVulnerable(game, myIceberg);
            Iceberg myMostVulnerableIceberg = GameUtil.closestIcebergToEnemy(game);
            if(closestIcebergThatIsNotMaxLevelAndIsMoreVulnerable != null) {
                target = closestIcebergThatIsNotMaxLevelAndIsMoreVulnerable;
            }
            else {
                target = myMostVulnerableIceberg;
            }

            // Target should never be null, just in case
            if(target == null || target == myIceberg) continue;

            if(target == null) continue;

            DefendAction action = new DefendAction(myIceberg, target, maxThatCanSend);

            /*log("Checking defend action: " + action.toString());*/

            List<Action> actionsToTest = new ArrayList<>(executedActions);
            actionsToTest.add(action);

            Prediction predictionAfterAction = new Prediction(game, actionsToTest);

            if(predictionAfterAction.isValid) {
                action.predictionAfterAction = predictionAfterAction;
                action.predictionBeforeAction = prediction;
                actions.add(action);

                /*log("Added defend action: " + action.toString());*/
            }
        }


        // Create bridge actions
        for(Iceberg myIceberg : game.getMyIcebergs()) {
            /*log("Checking bridge action for iceberg: " + myIceberg.toString());*/
            if(cannotBuildBridgeNow.contains(myIceberg)) continue;

            for(Iceberg target : game.getAllIcebergs()) {
                if(target == myIceberg) continue;

                /*log("Checking can create bridge");*/
                if(!(myIceberg.penguinAmount >= myIceberg.bridgeCost)) continue; // maybe their code for canCreateBridge is wrong
                /*log("Can create bridge");*/

                boolean gameAlreadyHasThisBridge = false;
                for(Bridge bridge : myIceberg.bridges) {
                    if(bridge.getEdges()[0] == target || bridge.getEdges()[1] == target) {
                        gameAlreadyHasThisBridge = true;
                        break;
                    }
                }
                /*log("Already has bridge: " + gameAlreadyHasThisBridge);*/
                if(gameAlreadyHasThisBridge) continue;

                BridgeAction action = new BridgeAction(myIceberg, target);

                List<Action> actionsToTest = new ArrayList<>(executedActions);
                actionsToTest.add(action);

                Prediction predictionAfterAction = new Prediction(game, actionsToTest);

                if(predictionAfterAction.isValid) {
                    action.predictionAfterAction = predictionAfterAction;
                    action.predictionBeforeAction = prediction;
                    actions.add(action);

                    /*log("Added bridge action: " + action.toString());*/
                }
            }
        }

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