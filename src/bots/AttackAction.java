package bots;

import penguin_game.*;

import java.util.*;

import static bots.IceBuildingState.Owner.*;

public class AttackAction extends Action {

    AttackPlan plan;

    public AttackAction(AttackPlan plan) {
        this.plan = plan;
    }

    @Override
    public boolean mustImprovePrediction() {
        return true;
    }

    @Override
    public double computeScoreImpl(Game game) {

        // If the enemy is the closest to a neutral iceberg and it's penguin amount is positive, we should never attack it as the enemy could recapture it the next turn.
        boolean isEnemyClosest = false;

        Collection<Iceberg> sortedByDistance = GameUtil.getIcebergsSortedByDistance(game, plan.target);
        Iceberg myClosestIceberg = null;
        Iceberg enemyClosestIceberg = null;

        int planTurnsToCapture = plan.getTurnsToCapture();
        for(Iceberg iceberg : sortedByDistance) {
            IceBuildingState.Owner owner = predictionBeforeAction.iceBuildingStateAtWhatTurn.get(iceberg).get(planTurnsToCapture).owner;
            if(owner == ME && myClosestIceberg == null) {
                myClosestIceberg = iceberg;
            }
            else if(owner == ENEMY && enemyClosestIceberg == null) {
                enemyClosestIceberg = iceberg;
            }
        }


        // IMPORTRANT - the -3 is making it so we don't attack neutral icebergs that the enemy is not to far away from
        if(enemyClosestIceberg != null && myClosestIceberg != null && enemyClosestIceberg.getTurnsTillArrival(plan.target) - 3 <= myClosestIceberg.getTurnsTillArrival(plan.target)) {
            isEnemyClosest = true;
        }

        boolean willTargetBeNeutral = predictionBeforeAction.iceBuildingStateAtWhatTurn.get(plan.target).get(planTurnsToCapture).owner == NEUTRAL;

        Log.log("target: " + IcebergUtil.toString(plan.target) + " isEnemyClosest: " + isEnemyClosest + " willTargetBeNeutral: " + willTargetBeNeutral + " penguinAmount: " + plan.target.penguinAmount);
        Log.log("because enemyClosestIceberg turns till arrival is: " + (enemyClosestIceberg == null ? "null" : enemyClosestIceberg.getTurnsTillArrival(plan.target)) + " and myClosestIceberg turns till arrival is: " + (myClosestIceberg == null ? "null" : myClosestIceberg.getTurnsTillArrival(plan.target)));
        if(willTargetBeNeutral && isEnemyClosest && plan.target.penguinAmount > 0) {
            return 0;
        }
        if(willTargetBeNeutral && plan.target instanceof BonusIceberg && plan.target.penguinAmount > 0) {
            return 0;
        }




        double predictionScore = predictionAfterAction.computeScore();

        /*boolean canEnemyDefend = false;
        AttackPlan.AttackPlanAction lastAction = plan.actions.get(0);

        int firstTurnEnemyDetectsAttack = lastAction.turnsToSend + 1;
        for(int enemySendDefenceTurn = firstTurnEnemyDetectsAttack; enemySendDefenceTurn < planTurnsToCapture && !canEnemyDefend; enemySendDefenceTurn++) {
            for(Iceberg potentialDefendingIceberg : sortedByDistance) {
                if(enemySendDefenceTurn + potentialDefendingIceberg.getTurnsTillArrival(plan.target) > planTurnsToCapture) {
                    break;
                }

                IceBuildingState defenderState = predictionBeforeAction.iceBuildingStateAtWhatTurn.get(potentialDefendingIceberg).get(enemySendDefenceTurn);
                if(defenderState.owner == ENEMY && defenderState.penguinAmount >= 1) {
                    canEnemyDefend = true;
                    break;
                }
            }
        }
        *//*if(canEnemyDefend) {
            Log.log("Cancelled attack on target: " + IcebergUtil.toString(game, plan.target) + " because enemy can defend\n");
            return 0;
        }*//*



        double enemyDefendScore = canEnemyDefend ? 0 : 1;
        Log.log("for target + " + plan.target + ", enemyDefendScore: " + enemyDefendScore + " and predictionScore: " + predictionScore);*/


        /*List<Boolean> canEnemyCaptureSender = new ArrayList<>();
        for(AttackPlan.AttackPlanAction action : plan.actions) {
            Iceberg sender = action.sender;

        }*/


        double score = predictionScore;
        Log.log("for target + " + plan.target + ", score: " + score);

        return score;

    }

    @Override
    public boolean executeIfPossible(Game game) {
        boolean wasExecuted = false;
        for(AttackPlan.AttackPlanAction planAction : plan.actions) {
            if(planAction.turnsToSend == 0) {
                if(planAction.sender.penguinAmount >= planAction.penguinAmount) {
                    planAction.sender.sendPenguins(plan.target, planAction.penguinAmount);
                    Log.log("Performed action: target = " + IcebergUtil.toString(plan.target) +  ", action = " + planAction.toString());
                    wasExecuted = true;
                }
                else {
                    Log.log("Could not perform action " + planAction.toString() + " because sender doesn't have enough penguins.");
                }
            }
        }
        return wasExecuted;
    }

    @Override
    public String toString() {
        return plan.toString();
    }

    @Override
    public Set<Iceberg> getIcebergsThatSentNow() {
        Set<Iceberg> icebergs = new HashSet<>();
        for(AttackPlan.AttackPlanAction planAction : plan.actions) {
            if(planAction.turnsToSend == 0) {
                icebergs.add(planAction.sender);
            }
        }
        return icebergs;
    }


    @Override
    public Action ageByTurn() {
        AttackPlan newPlan = plan.ageByTurn();
        if(newPlan == null) {
            return null;
        }
        return new AttackAction(newPlan);
    }
}
