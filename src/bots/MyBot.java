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

    // For runs that don't matter. Pick a random action
    public static boolean MAKE_CODE_WORSE = false;

    // If we only want to print debug messages from turn x, we set this variable to x.
    // This is only useful for if we don't need debugs from previous turns, but we have a lot of debug messages.
    // In other words, this is to sometimes avoid getting the "too many log messages" message in the logger.
    public static int ONLY_PRINT_FROM_TURN = 1;

    public static final Calendar TODAY = Calendar.getInstance();
    public static final Calendar CUTOFF_DATE = new GregorianCalendar(2022, Calendar.MAY, 3);
    public static final Calendar JUST_IN_CASE_DATE = new GregorianCalendar(2022, Calendar.MAY, 14);

    public static Game game;

    public boolean printTimeWarning = false;

    public long startTime = 0;


    /**
     * Does the turn. This function is called by the system.
     * @param game current game state
     */
    public void doTurn(Game game) {

        /*for(Bridge bridge : GameUtil.getAllBridges(game)) {
            log("Bridge: " + bridge + " duration: " + bridge.duration);
        }*/

        startTime = System.currentTimeMillis();

        if(printTimeWarning) {
            log("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            log("WARNING: LAST TURN WAS VERY CLOSE TO TIME OUT");
            log("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }

        /*if(game.turn == 1) {
            game.getMyIcebergs()[0].upgrade();
            Log.IS_DEBUG = true;
            return;
        }*/


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


        log("My power: " + GameUtil.getTotalMyPenguinsOnMap(game) + " (+" + GameUtil.getMyPenguinCreationRate(game) + ")");
        log("Enemy power: " + GameUtil.getTotalEnemyPenguinsOnMap(game) + " (+" + GameUtil.getEnemyPenguinCreationRate(game) + ")");
        log("------------------------------");

        /*log("\nTime1: " + (System.currentTimeMillis() - startTime) + "\n");*/

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
        log("------------------------------");


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

        log( "\nTime2: " + (System.currentTimeMillis() - startTime) + "\n");

        // While we have good candidate actions, we will pick the best one that we can perform and execute it.
        // Once we have no more good actions to add, we will break in the loop, so while(true) is fine.
        while(true) {

            log("\nTime3.0: " + (System.currentTimeMillis() - startTime) + "\n");

            // Find and store all the candidate actions.
            List<Action> candidateActions = createAllActions(game, prediction, cannotSendNow, cannotUpgradeNow, cannotBuildBridgeNow, executedActions);
            log("candidateActions size: " + candidateActions.size());

            log("\nTime3.1: " + (System.currentTimeMillis() - startTime) + "\n");

            // Calculate the score for each action in our candidate actions list.
            // The score is a variable in the action class. Each score is in the range 0-1.
            // Note: Scores can be higher than 1 or lower than 0, and the code will still work fine, but that is not ideal. Try to keep it in [0,1]
            for (Action action : candidateActions) {
                log("  Time3.1.0: " + (System.currentTimeMillis() - startTime));
                action.computeScore(game);
                log("Action: " + action + ", score: " + action.score);
            }

            log("\nTime3.2: " + (System.currentTimeMillis() - startTime) + "\n");


            // Filter out all elements from candidateActions who's score is 0 using a stream.
            // We also make sure we aren't checking an action that has already been executed.
            // Note: Some actions have to increase our prediction score, but some don't. Right now only defend actions don't require a prediction score increase.
            candidateActions = candidateActions.stream()
                    .filter(action -> action.score > 0 && !executedActions.contains(action))
                    .filter(action -> (action.predictionAfterAction.computeScore() >= action.predictionBeforeAction.computeScore())/* || !action.mustImprovePrediction()*/)
                    .collect(Collectors.toList());

            log("\nTime3.3: " + (System.currentTimeMillis() - startTime) + "\n");

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

            log("\nTime3.4: " + (System.currentTimeMillis() - startTime) + "\n");

            if (candidateActions.size() > 0) { // This check is useless as we already checked this case, but we can keep it just in case.

                // Pick the best action from the candidate actions list.
                // Because we sorted the list by scores from highest to lowest, the first element in the list is the best action.
                Action bestAction = candidateActions.get(0);


                if(MAKE_CODE_WORSE) {
                    Random random = new Random();
                    int randomIndex = random.nextInt(candidateActions.size());
                    bestAction = candidateActions.get(randomIndex);
                }

                log("\nTime3.5: " + (System.currentTimeMillis() - startTime) + "\n");

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

                log("\nTime3.6: " + (System.currentTimeMillis() - startTime) + "\n");
            }


            // Update our prediction variable, with executing the new action that we added.
            prediction = new Prediction(game, executedActions);

            log("\nTime3.7: " + (System.currentTimeMillis() - startTime) + "\n");

            /*log("Best action prediction after: " + prediction);*/
            /*log("New Prediction: " + prediction);*/
        }

        log("\nTime4: " + (System.currentTimeMillis() - startTime) + "\n");

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


        log("------------------------------");

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        if(totalTime > game.getMaxTurnTime() / 2) {
            printTimeWarning = true;
        }
        else {
            printTimeWarning = false;
        }
        log("\nTime taken: " + totalTime + " / " + game.getMaxTurnTime() + " ms");
    }



    /**
     * Create all possible actions
     * @param game current game state
     * @return list of all possible actions
     */
    public List<Action> createAllActions(Game game, Prediction prediction, Set<Iceberg> cannotSendNow, Set<Iceberg> cannotUpgradeNow, Set<Iceberg> cannotBuildBridgeNow, List<Action> executedActions) {

        log("\nTime3.0.0: " + (System.currentTimeMillis() - startTime) + "\n");

        if(!prediction.isValid) {
            log(prediction);
            if(Log.IS_DEBUG) {
                throw new IllegalStateException("Prediction is not valid");
            }
        }

        log("\nTime3.0.1: " + (System.currentTimeMillis() - startTime) + "\n");

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

        log("\nTime3.0.2: " + (System.currentTimeMillis() - startTime) + "\n");

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
            /*else {
                log("Attack plan for ice-building: " + IcebergUtil.toString(iceBuilding) + " is null");
            }*/
        }

        log("\nTime3.0.2: " + (System.currentTimeMillis() - startTime) + "\n");

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

        log("\nTime3.0.3: " + (System.currentTimeMillis() - startTime) + "\n");


        //Create defend actions
        for(Iceberg myIceberg : game.getMyIcebergs()) {

            if(cannotSendNow.contains(myIceberg)) continue;

            if(myIceberg.level != myIceberg.upgradeLevelLimit) continue;

            log("  Time3.0.3.0: " + (System.currentTimeMillis() - startTime));

            int maxThatCanSend = prediction.getMaxThatCanSpend(myIceberg, 0);

            log("  Time3.0.3.1: " + (System.currentTimeMillis() - startTime));

            // If I can't send any penguins right now, don't create a defend action.
            if(maxThatCanSend == 0) continue;

            Iceberg target = null;
            /*Iceberg closestIcebergThatIsNotMaxLevelAndIsMoreVulnerable = GameUtil.getClosestIcebergThatIsNotMaxLevelAndIsMoreVulnerable(game, myIceberg);*/
            /*Iceberg closestIcebergThatIsNotMaxLevel = GameUtil.getClosestIcebergThatIsNotMaxLevel(game, myIceberg);*/
            Iceberg closestIcebergThatCouldUseHelp = GameUtil.getClosestIcebergThatCouldUseHelp(game, myIceberg, prediction);

            log("  Time3.0.3.2: " + (System.currentTimeMillis() - startTime));

            Iceberg myMostVulnerableIceberg = GameUtil.closestIcebergToEnemy(game);

            log("  Time3.0.3.3: " + (System.currentTimeMillis() - startTime));
            /*if(closestIcebergThatIsNotMaxLevelAndIsMoreVulnerable != null) {
                target = closestIcebergThatIsNotMaxLevelAndIsMoreVulnerable;
            }*/
            /*if(closestIcebergThatIsNotMaxLevel != null) {
                target = closestIcebergThatIsNotMaxLevel;
            }*/
            if(closestIcebergThatCouldUseHelp != null) {
                target = closestIcebergThatCouldUseHelp;
            }
            else {
                target = myMostVulnerableIceberg;
            }

            // Target should never be null, just in case
            if(target == null || target == myIceberg) continue;


            DefendAction action = new DefendAction(myIceberg, target, maxThatCanSend);

            log("  Time3.0.3.4: " + (System.currentTimeMillis() - startTime));

            /*log("Checking defend action: " + action.toString());*/

            List<Action> actionsToTest = new ArrayList<>(executedActions);
            actionsToTest.add(action);

            Prediction predictionAfterAction = new Prediction(game, actionsToTest);

            log("  Time3.0.3.5: " + (System.currentTimeMillis() - startTime));

            if(predictionAfterAction.isValid) {
                action.predictionAfterAction = predictionAfterAction;
                action.predictionBeforeAction = prediction;
                actions.add(action);

                /*log("Added defend action: " + action.toString());*/
            }

            log("  Time3.0.3.6: " + (System.currentTimeMillis() - startTime));
        }


        log("\nTime3.0.4: " + (System.currentTimeMillis() - startTime) + "\n");


        // Create bridge actions
        for(Iceberg myIceberg : game.getMyIcebergs()) {
            /*log("Checking bridge action for iceberg: " + myIceberg.toString());*/
            if(cannotBuildBridgeNow.contains(myIceberg)) continue;

            for(Iceberg target : GameUtil.getIcebergsThatAreGettingSentPenguinsAtFromMyIceberg(game, myIceberg)) {
                if(target == myIceberg) continue;

                /*log("Checking can create bridge");*/
                if(!(myIceberg.penguinAmount >= myIceberg.bridgeCost)) continue; // maybe their code for canCreateBridge is wrong
                /*log("Can create bridge");*/

                boolean gameAlreadyHasThisBridge = false;
                for(Bridge bridge : myIceberg.bridges) {
                    if(bridge.getEdges()[0] == target || bridge.getEdges()[1] == target) {
                        if(bridge.duration > 1) {
                            gameAlreadyHasThisBridge = true;
                            break;
                        }
                    }
                }
                /*log("Already has bridge: " + gameAlreadyHasThisBridge);*/
                if(gameAlreadyHasThisBridge) continue;

                BridgeAction action = new BridgeAction(myIceberg, target);

                List<Action> actionsToTest = new ArrayList<>(executedActions);
                actionsToTest.add(action);


                log("\nTime3.0.5.1: " + (System.currentTimeMillis() - startTime) + "\n");
                Prediction predictionAfterAction = new Prediction(game, actionsToTest);
                log("\nTime3.0.5.2: " + (System.currentTimeMillis() - startTime) + "\n");

                if(predictionAfterAction.isValid) {
                    action.predictionAfterAction = predictionAfterAction;
                    action.predictionBeforeAction = prediction;
                    actions.add(action);

                    /*log("Added bridge action: " + action.toString());*/
                }
            }
        }

        log("\nTime3.0.5: " + (System.currentTimeMillis() - startTime) + "\n");

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