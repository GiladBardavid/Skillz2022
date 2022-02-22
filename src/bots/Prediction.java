package bots;

import penguin_game.*;
import java.util.*;

import static bots.IceBuildingState.Owner.ME;
import static bots.IceBuildingState.Owner.NEUTRAL;

public class Prediction {

    public static Map<IceBuilding, List<IceBuildingState>> iceBuildingStateAtWhatTurn;

    public static Map<IceBuilding, int[]> howManyOfMyPenguinsWillArriveAtWhatTurn;
    public static Map<IceBuilding, int[]> howManyEnemyPenguinsWillArriveAtWhatTurn;

    public static Map<Iceberg, int[]> howManyPenguinsWillSendAtWhatTurn;


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
                }
            }
            else if(action instanceof UpgradeAction) {
                upgradedIcebergs.add(((UpgradeAction) action).target);
            }

            // TODO: implement bridge and defend actions
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
            howManyPenguinsWillBeAtWhatTurn.add(state);

            // State now is always updated by the game, including upgrades and sends
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
                                newOwner = IceBuildingState.Owner.ENEMY;
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
                }
                else {
                    if(sendingMine > 0) {
                        throw new IllegalStateException("ERROR trying to send: amount = " + sendingMine + " predicted owner = " + newOwner);
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
