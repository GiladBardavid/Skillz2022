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
    public double computeScoreImpl(Game game) {

        double score = predictionAfterAction.computeScore();


        // If the enemy is the closest to a neutral iceberg and it's penguin amount is positive, we should never attack it as the enemy could recapture it the next turn.
        boolean isEnemyClosest = false;

        Collection<Iceberg> sortedByLength = GameUtil.getIcebergsSortedByDistance(game, plan.target);
        Iceberg myClosestIceberg = null;
        Iceberg enemyClosestIceberg = null;

        int planTurnsToCapture = plan.getTurnsToCapture();
        for(Iceberg iceberg : sortedByLength) {
            IceBuildingState.Owner owner = predictionBeforeAction.iceBuildingStateAtWhatTurn.get(iceberg).get(planTurnsToCapture).owner;
            if(owner == ME && myClosestIceberg == null) {
                myClosestIceberg = iceberg;
            }
            else if(owner == ENEMY && enemyClosestIceberg == null) {
                enemyClosestIceberg = iceberg;
            }
        }

        if(enemyClosestIceberg != null && myClosestIceberg != null && enemyClosestIceberg.getTurnsTillArrival(plan.target) <= myClosestIceberg.getTurnsTillArrival(plan.target)) {
            isEnemyClosest = true;
        }

        boolean willTargetBeNeutral = predictionBeforeAction.iceBuildingStateAtWhatTurn.get(plan.target).get(planTurnsToCapture).owner == NEUTRAL;

        if(willTargetBeNeutral && isEnemyClosest && plan.target.penguinAmount > 0) {
            score = 0;
        }

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
