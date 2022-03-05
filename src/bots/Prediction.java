package bots;

import penguin_game.*;
import java.util.*;

import static bots.IceBuildingState.Owner.*;

public class Prediction {

    public Map<IceBuilding, List<IceBuildingState>> iceBuildingStateAtWhatTurn;

    public Map<IceBuilding, int[]> howManyOfMyPenguinsWillArriveAtWhatTurn;
    public Map<IceBuilding, int[]> howManyEnemyPenguinsWillArriveAtWhatTurn;

    public Map<IceBuilding, int[]> howManyOfMyPenguinsCanArriveAtWhatTurn;
    public Map<IceBuilding, int[]> howManyEnemyPenguinsCanArriveAtWhatTurn;

    public Map<Iceberg, int[]> howManyPenguinsWillSendAtWhatTurn;

    public boolean isValid = true;

    private final static int MAX_CAN_SEND_LOOKAHEAD = 12;


    public Prediction(Game game, List<Action> executedActions) {

        BridgeHelper bridgeHelper = new BridgeHelper(game);

        int maxTurnsLookAhead = GameUtil.getMaxLengthBetweenIceBuildings(game) * 2 + 1;

        howManyOfMyPenguinsWillArriveAtWhatTurn = new HashMap<>();
        howManyEnemyPenguinsWillArriveAtWhatTurn = new HashMap<>();
        howManyPenguinsWillSendAtWhatTurn = new HashMap<>();

        howManyOfMyPenguinsCanArriveAtWhatTurn = new HashMap<>();
        howManyEnemyPenguinsCanArriveAtWhatTurn = new HashMap<>();

        Set<Iceberg> upgradedIcebergs = new HashSet<>();

        List<BridgeAction> bridgeActions = new ArrayList<>();


        for(Action action : executedActions) {
            if(action instanceof AttackAction) {
                AttackAction attackAction = (AttackAction) action;

                AttackPlan attackPlan = attackAction.plan;
                IceBuilding target = attackPlan.target;

                for(AttackPlan.AttackPlanAction planAction : attackPlan.actions) {

                    int turnsToSend = planAction.turnsToSend;
                    int turnsToArrive = bridgeHelper.getArrivalTurn(planAction.sender, target, turnsToSend, bridgeActions);

                    int[] penguinAmountArrivingAtWhatTurn = howManyOfMyPenguinsWillArriveAtWhatTurn.get(target);

                    if(penguinAmountArrivingAtWhatTurn == null){
                        penguinAmountArrivingAtWhatTurn = new int[maxTurnsLookAhead];
                        howManyOfMyPenguinsWillArriveAtWhatTurn.put(target, penguinAmountArrivingAtWhatTurn);
                    }

                    penguinAmountArrivingAtWhatTurn[turnsToArrive] += planAction.penguinAmount;



                    int[] penguinAmountToSendAtWhatTurn = howManyPenguinsWillSendAtWhatTurn.get(planAction.sender);

                    if(penguinAmountToSendAtWhatTurn == null){
                        penguinAmountToSendAtWhatTurn = new int[maxTurnsLookAhead];
                        howManyPenguinsWillSendAtWhatTurn.put(planAction.sender, penguinAmountToSendAtWhatTurn);
                    }

                    penguinAmountToSendAtWhatTurn[turnsToSend] += planAction.penguinAmount;
                    /*Log.log("Prediction for " + planAction.sender + ": " + Arrays.toString(penguinAmountToSendAtWhatTurn));*/
                }
            }
            else if(action instanceof UpgradeAction) {
                upgradedIcebergs.add(((UpgradeAction) action).upgradingIceberg);
            }

            else if (action instanceof DefendAction) {
                DefendAction defendAction = (DefendAction) action;
                int[] penguinAmountToSendAtWhatTurn = howManyPenguinsWillSendAtWhatTurn.get(defendAction.from);

                if(penguinAmountToSendAtWhatTurn == null){
                    penguinAmountToSendAtWhatTurn = new int[maxTurnsLookAhead];
                    howManyPenguinsWillSendAtWhatTurn.put(defendAction.from, penguinAmountToSendAtWhatTurn);
                }

                penguinAmountToSendAtWhatTurn[0] += defendAction.penguinAmount;


                int[] penguinAmountArrivingAtWhatTurn = howManyOfMyPenguinsWillArriveAtWhatTurn.get(defendAction.to);

                if(penguinAmountArrivingAtWhatTurn == null){
                    penguinAmountArrivingAtWhatTurn = new int[maxTurnsLookAhead];
                    howManyOfMyPenguinsWillArriveAtWhatTurn.put(defendAction.to, penguinAmountArrivingAtWhatTurn);
                }

                int turnsToArrive = bridgeHelper.getArrivalTurn(defendAction.from, defendAction.to, 0, bridgeActions);
                penguinAmountArrivingAtWhatTurn[turnsToArrive] += defendAction.penguinAmount;
            }

            else if (action instanceof BridgeAction) {
                BridgeAction bridgeAction = (BridgeAction) action;
                bridgeActions.add(bridgeAction);

                int[] penguinAmountToSendAtWhatTurn = howManyPenguinsWillSendAtWhatTurn.get(bridgeAction.from);

                if(penguinAmountToSendAtWhatTurn == null){
                    penguinAmountToSendAtWhatTurn = new int[maxTurnsLookAhead];
                    howManyPenguinsWillSendAtWhatTurn.put(bridgeAction.from, penguinAmountToSendAtWhatTurn);
                }

                penguinAmountToSendAtWhatTurn[0] += bridgeAction.cost;
            }
        }



        for(PenguinGroup myPenguinGroup : game.getMyPenguinGroups()){
            IceBuilding destination  = myPenguinGroup.destination;
            int[] penguinAmountArrivingAtWhatTurn = howManyOfMyPenguinsWillArriveAtWhatTurn.get(destination);

            if(penguinAmountArrivingAtWhatTurn == null){
                penguinAmountArrivingAtWhatTurn = new int[maxTurnsLookAhead];
                howManyOfMyPenguinsWillArriveAtWhatTurn.put(destination, penguinAmountArrivingAtWhatTurn);
            }

            int turnsTillArrival = bridgeHelper.getActualTurnsTillArrival(myPenguinGroup, bridgeActions);
            int penguinAmount = myPenguinGroup.penguinAmount;

            penguinAmountArrivingAtWhatTurn[turnsTillArrival] += penguinAmount;
        }


        for(PenguinGroup enemyPenguinGroup : game.getEnemyPenguinGroups()){
            /*Log.log("D_0_6: found enemy group: " + enemyPenguinGroup.toString() + " with destination: " + enemyPenguinGroup.destination.toString() + " and turnsTillArrival: " + enemyPenguinGroup.turnsTillArrival);*/
            IceBuilding destination  = enemyPenguinGroup.destination;
            int[] penguinAmountArrivingAtWhatTurn = howManyEnemyPenguinsWillArriveAtWhatTurn.get(destination);

            if(penguinAmountArrivingAtWhatTurn == null){
                penguinAmountArrivingAtWhatTurn = new int[maxTurnsLookAhead];
                howManyEnemyPenguinsWillArriveAtWhatTurn.put(destination, penguinAmountArrivingAtWhatTurn);
            }

            int turnsTillArrival = bridgeHelper.getActualTurnsTillArrival(enemyPenguinGroup, bridgeActions);
            int penguinAmount = enemyPenguinGroup.penguinAmount;

            penguinAmountArrivingAtWhatTurn[turnsTillArrival] += penguinAmount;
        }




        iceBuildingStateAtWhatTurn = new HashMap<>();

        for (IceBuilding iceBuilding : GameUtil.getAllIceBuildings(game)) {
            List<IceBuildingState> howManyPenguinsWillBeAtWhatTurn = new ArrayList<>();
            iceBuildingStateAtWhatTurn.put(iceBuilding, howManyPenguinsWillBeAtWhatTurn);

            IceBuildingState state = new IceBuildingState(game, iceBuilding);
            if(upgradedIcebergs.contains(iceBuilding)){
                state.penguinAmount -= ((Iceberg)iceBuilding).upgradeCost;
            }
            if(howManyPenguinsWillSendAtWhatTurn.get(iceBuilding) != null){
                state.penguinAmount -= howManyPenguinsWillSendAtWhatTurn.get(iceBuilding)[0];
            }
            if(state.penguinAmount < 0) {
                /*Log.log("IceBuilding is: " + iceBuilding + " penguins amount: " + iceBuilding.penguinAmount);
                Log.log("how many my arriving: " + howManyOfMyPenguinsWillArriveAtWhatTurn.get(iceBuilding)[0]);
                Log.log("how many enemy arriving: " + howManyOfMyPenguinsWillArriveAtWhatTurn.get(iceBuilding)[0]);
                Log.log("how many penguins will send: " + howManyPenguinsWillSendAtWhatTurn.get(iceBuilding)[0]);*/
                isValid = false;
            }
            howManyPenguinsWillBeAtWhatTurn.add(state);

        }

        BonusIceberg bonusIceberg = game.getBonusIceberg();
        int turnsLeftToBonus = bonusIceberg.turnsLeftToBonus;

        for (int i = 1; i < maxTurnsLookAhead; i++) {

            IceBuildingState bonusIcebergState = iceBuildingStateAtWhatTurn.get(bonusIceberg).get(i - 1);


            // Handle bonus iceberg bonuses
            int thisTurnBonus = 0;
            IceBuildingState.Owner thisTurnBonusOwner = null;


            if(turnsLeftToBonus == 0){
                thisTurnBonus = bonusIceberg.penguinBonus;
                thisTurnBonusOwner = bonusIcebergState.owner;

                turnsLeftToBonus = bonusIceberg.maxTurnsToBonus;
            }


            for (IceBuilding iceBuilding : GameUtil.getAllIceBuildings(game)) {

                int penguinsPerTurn = (iceBuilding instanceof Iceberg) ? ((Iceberg) iceBuilding).penguinsPerTurn : 0;
                if(upgradedIcebergs.contains(iceBuilding)){
                    penguinsPerTurn += ((Iceberg)iceBuilding).upgradeValue;
                }

                int[] arrivingMineByTurn = howManyOfMyPenguinsWillArriveAtWhatTurn.get(iceBuilding);
                int[] arrivingEnemyByTurn = howManyEnemyPenguinsWillArriveAtWhatTurn.get(iceBuilding);
                int[] sendingMineByTurn = howManyPenguinsWillSendAtWhatTurn.get(iceBuilding);

                List<IceBuildingState> statesByTurn = iceBuildingStateAtWhatTurn.get(iceBuilding);
                IceBuildingState state = statesByTurn.get(i - 1);

                int newPenguinAmount = state.penguinAmount;
                IceBuildingState.Owner newOwner = state.owner;

                if (state.owner != NEUTRAL) {
                    newPenguinAmount += penguinsPerTurn;
                }




                int arrivingMine = arrivingMineByTurn == null ? 0 : arrivingMineByTurn[i];
                int arrivingEnemy = arrivingEnemyByTurn == null ? 0 : arrivingEnemyByTurn[i];

                int sendingMine = sendingMineByTurn == null ? 0 : sendingMineByTurn[i];

                switch (state.owner) {
                    case ME:
                        newPenguinAmount += arrivingMine;
                        newPenguinAmount -= arrivingEnemy;
                        break;
                    case ENEMY:
                        newPenguinAmount += arrivingEnemy;
                        newPenguinAmount -= arrivingMine;
                        break;
                    case NEUTRAL:
                        int totalArriving = arrivingMine + arrivingEnemy;
                        if (totalArriving <= newPenguinAmount) {
                            newPenguinAmount -= totalArriving;
                        } else {
                            // Neutral iceberg logic
                            int diff = Math.abs(arrivingMine - arrivingEnemy);
                            int sum = arrivingMine + arrivingEnemy;
                            newPenguinAmount = Math.min(diff, sum - newPenguinAmount);
                            if (newPenguinAmount == 0) {
                                newOwner = NEUTRAL;
                            } else if (arrivingMine > arrivingEnemy) {
                                newOwner = ME;
                            } else {
                                newOwner = ENEMY;
                            }

                            // If the bonus iceberg was captured, reset the bonus timer
                            if(iceBuilding.equals(bonusIceberg)) {

                                turnsLeftToBonus = bonusIceberg.maxTurnsToBonus;

                                if(newOwner != NEUTRAL) {
                                    thisTurnBonus++;
                                }
                            }
                        }
                }

                if (newPenguinAmount == 0) {
                    newOwner = NEUTRAL;

                    // If the bonus iceberg was captured, reset the bonus timer
                    if(iceBuilding.equals(bonusIceberg)) {
                        turnsLeftToBonus = bonusIceberg.maxTurnsToBonus; // Not +1 as it is now neutral
                    }
                }
                if (newPenguinAmount < 0) {
                    newOwner = state.getOppositeOwner();

                    // Make positive
                    newPenguinAmount *= -1;

                    // If the bonus iceberg was captured, reset the bonus timer
                    if(iceBuilding.equals(bonusIceberg)) {
                        turnsLeftToBonus = bonusIceberg.maxTurnsToBonus + 1;
                    }

                }


                if(newOwner == thisTurnBonusOwner && !iceBuilding.equals(bonusIceberg)) {
                    newPenguinAmount += thisTurnBonus;
                }

                // Send penguins to other icebergs
                if(newOwner == ME) {
                    newPenguinAmount -= sendingMine;
                    if(newPenguinAmount < 0) {
                        isValid = false;
                    }
                }
                else {
                    if(sendingMine > 0) {
                        // The prediction is not valid in this case, we are sending from an iceberg that won't be able to send
                        isValid = false;
                    }
                }


                state = new IceBuildingState(newPenguinAmount, newOwner);
                statesByTurn.add(state);
            }

            // Decrement the bonus timer
            if(bonusIcebergState.owner != NEUTRAL){
                turnsLeftToBonus--;
            }

        }


        // Update possible penguin arrivals
        for(IceBuilding iceBuilding : GameUtil.getAllIceBuildings(game)) {
            int[] myPossiblePenguinArrivals = new int[MAX_CAN_SEND_LOOKAHEAD];
            int[] enemyPossiblePenguinArrivals = new int[MAX_CAN_SEND_LOOKAHEAD];

            howManyOfMyPenguinsCanArriveAtWhatTurn.put(iceBuilding, myPossiblePenguinArrivals);
            howManyEnemyPenguinsCanArriveAtWhatTurn.put(iceBuilding, enemyPossiblePenguinArrivals);

            for(IceBuilding otherIceBuilding : GameUtil.getAllIceBuildings(game)) {

                if(otherIceBuilding.equals(iceBuilding)) {
                    continue;
                }

                int distance = iceBuilding.getTurnsTillArrival(otherIceBuilding); // TODO factoring bridges
                /*if(iceBuilding instanceof Iceberg && otherIceBuilding instanceof Iceberg) {
                    distance /= ((Iceberg)(iceBuilding)).bridgeSpeedMultiplier;
                }*/

                for(int sendingTurn = 0; sendingTurn < MAX_CAN_SEND_LOOKAHEAD - distance; sendingTurn++) {

                    IceBuildingState otherIcebergState = iceBuildingStateAtWhatTurn.get(otherIceBuilding).get(sendingTurn);

                    if(otherIcebergState.owner == ME) {
                        myPossiblePenguinArrivals[sendingTurn + distance] += otherIcebergState.penguinAmount;
                    }
                    else if(otherIcebergState.owner == ENEMY) {
                        enemyPossiblePenguinArrivals[sendingTurn + distance] += otherIcebergState.penguinAmount;
                    }
                }
            }
        }
        /*Log.log("Possible penguin arrivals updated:");
        for(IceBuilding iceBuilding : GameUtil.getAllIceBuildings(game)) {
            Log.log("Iceberg " + IcebergUtil.toString(iceBuilding) + ": Mine:\n " + Arrays.toString(howManyOfMyPenguinsCanArriveAtWhatTurn.get(iceBuilding)));
            Log.log("Enemy:\n" + Arrays.toString(howManyEnemyPenguinsCanArriveAtWhatTurn.get(iceBuilding)));
        }*/

    }

    public double computeScore() {
        int countMyIcebergs = 0;
        int countEnemyIcebergs = 0;
        int countNeutralIcebergs = 0;

        int myPenguinsSum = 0;
        int enemyPenguinsSum = 0;

        for(IceBuilding iceBuilding : iceBuildingStateAtWhatTurn.keySet()) {
            List<IceBuildingState> states = iceBuildingStateAtWhatTurn.get(iceBuilding);
            IceBuildingState lastState = states.get(states.size() - 1);

            if(lastState.owner == ME) {
                countMyIcebergs++;
                myPenguinsSum += lastState.penguinAmount;
            }
            else if(lastState.owner == ENEMY) {
                countEnemyIcebergs++;
                enemyPenguinsSum += lastState.penguinAmount;
            }
            else {
                countNeutralIcebergs++;
            }
        }

        if(countEnemyIcebergs == 0) return 1;
        if(countMyIcebergs == 0) return 0;

        double scoreByPenguins = (double)myPenguinsSum / (double)(myPenguinsSum + enemyPenguinsSum);
        double scoreByIcebergs = (double)countMyIcebergs / (double)(countMyIcebergs + countEnemyIcebergs);

        double score = GameUtil.computeFactoredScore(
                scoreByPenguins, 1,
                scoreByIcebergs, 0
        );

        return score;
    }


    public int getMaxThatCanSpend(Iceberg iceberg, int turnsTillSend) {
        List<IceBuildingState> states = iceBuildingStateAtWhatTurn.get(iceberg);

        int minPenguinAmountInState = Integer.MAX_VALUE;

        // If there is a penguin group in the same turn, the max that we can send is 1 less, as if we send all, the enemy will be neutral.
        int lastIndexOfMinPenguinAmount = 0;

        for(int i = turnsTillSend; i < states.size(); i++) {
            IceBuildingState currentState = states.get(i);

            if(currentState.owner != ME) return 0;

            int penguinAmountIfEveryoneSendsToMe = currentState.penguinAmount;

            if(i < /*turnsTillSend + */MAX_CAN_SEND_LOOKAHEAD) {
                penguinAmountIfEveryoneSendsToMe += howManyOfMyPenguinsCanArriveAtWhatTurn.get(iceberg)[i] - howManyEnemyPenguinsCanArriveAtWhatTurn.get(iceberg)[i];
            }

            if(penguinAmountIfEveryoneSendsToMe <= minPenguinAmountInState) { // penguinsAmountIFEveryoneSendsToMe was previously just currentState.penguinAmount
                minPenguinAmountInState = penguinAmountIfEveryoneSendsToMe;
                lastIndexOfMinPenguinAmount = i;
            }
        }

        // If the max amount that I can send will result in me turning neutral, send 1 less so I will have 1 penguin remaining and I will stay mine
        if(lastIndexOfMinPenguinAmount != turnsTillSend) {
            minPenguinAmountInState--;
        }

        // Because of the previous if, we don't want the max that I can send to be -1 (negative)
        int result = Math.max(0, minPenguinAmountInState);

        /*result = Math.min(result, states.get(turnsTillSend).penguinAmount);*/

        /*Log.log("Max that " + IcebergUtil.toString(iceberg) + " can send: " + result);*/
        return result;
    }



    public boolean canBeAtRisk(IceBuilding iceBuilding) {
        List<IceBuildingState> states = iceBuildingStateAtWhatTurn.get(iceBuilding);

        for(int i = 0; i < states.size(); i++) {
            IceBuildingState currentState = states.get(i);

            int penguinAmountIfEveryoneSendsToMe = currentState.penguinAmount;

            if(i < MAX_CAN_SEND_LOOKAHEAD) {
                penguinAmountIfEveryoneSendsToMe += howManyOfMyPenguinsCanArriveAtWhatTurn.get(iceBuilding)[i] - howManyEnemyPenguinsCanArriveAtWhatTurn.get(iceBuilding)[i];
            }

            if (penguinAmountIfEveryoneSendsToMe <= 0) {
                return true;
            }
        }

        return false;
    }



    @Override
    public String toString() {
        if(!Log.IS_DEBUG) { // Save time
            return "";
        }
        String s = "Prediction: \n";
        for(IceBuilding iceBuilding : iceBuildingStateAtWhatTurn.keySet()) {
            s += "  " + iceBuilding.toString() + ": " + iceBuildingStateAtWhatTurn.get(iceBuilding).toString() + "\n";
        }
        return s;
    }
}
