package bots;

import penguin_game.*;
import java.util.*;

import static bots.IceBuildingState.Owner.*;

public class Prediction {

    public Map<IceBuilding, List<IceBuildingState>> iceBuildingStateAtWhatTurn;

    public Map<IceBuilding, int[]> howManyOfMyPenguinsWillArriveAtWhatTurn;
    public Map<IceBuilding, int[]> howManyEnemyPenguinsWillArriveAtWhatTurn;

    public Map<Iceberg, int[]> howManyPenguinsWillSendAtWhatTurn;

    public boolean isValid = true;


    public Prediction(Game game, List<Action> executedActions) {

        int maxTurnsLookAhead = GameUtil.getMaxLengthBetweenIceBuildings(game) * 2 + 1;

        howManyOfMyPenguinsWillArriveAtWhatTurn = new HashMap<>();
        howManyEnemyPenguinsWillArriveAtWhatTurn = new HashMap<>();
        howManyPenguinsWillSendAtWhatTurn = new HashMap<>();

        Set<Iceberg> upgradedIcebergs = new HashSet<>();


        for(Action action : executedActions) {
            if(action instanceof AttackAction) {
                AttackAction attackAction = (AttackAction) action;

                AttackPlan attackPlan = attackAction.plan;
                IceBuilding target = attackPlan.target;

                for(AttackPlan.AttackPlanAction planAction : attackPlan.actions) {

                    int turnsToSend = planAction.turnsToSend;
                    int turnsToArrive = planAction.turnsToSend + planAction.sender.getTurnsTillArrival(target);

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
                upgradedIcebergs.add(((UpgradeAction) action).target);
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

                penguinAmountArrivingAtWhatTurn[defendAction.distance] += defendAction.penguinAmount;
            }

            // TODO: implement bridge actions
        }



        for(PenguinGroup myPenguinGroup : game.getMyPenguinGroups()){
            IceBuilding destination  = myPenguinGroup.destination;
            int[] penguinAmountArrivingAtWhatTurn = howManyOfMyPenguinsWillArriveAtWhatTurn.get(destination);

            if(penguinAmountArrivingAtWhatTurn == null){
                penguinAmountArrivingAtWhatTurn = new int[maxTurnsLookAhead];
                howManyOfMyPenguinsWillArriveAtWhatTurn.put(destination, penguinAmountArrivingAtWhatTurn);
            }

            int turnsTillArrival = myPenguinGroup.turnsTillArrival;
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

            int turnsTillArrival = enemyPenguinGroup.turnsTillArrival;
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
                scoreByPenguins, 0.7,
                scoreByIcebergs, 0.3
        );

        return score;
    }


    public int getMaxThatCanSend(Iceberg iceberg) {
        List<IceBuildingState> states = iceBuildingStateAtWhatTurn.get(iceberg);

        int minPenguinAmountInState = Integer.MAX_VALUE;
        for(IceBuildingState state : states) {
            if(state.owner != ME) return 0;

            if(state.penguinAmount < minPenguinAmountInState) {
                minPenguinAmountInState = state.penguinAmount;
            }
        }

        return minPenguinAmountInState;
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
