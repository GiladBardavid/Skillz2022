package bots;

import penguin_game.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AttackAction extends Action {

    AttackPlan plan;

    public AttackAction(AttackPlan plan) {
        this.plan = plan;
    }

    @Override
    public double computeScoreImpl(Game game) {
        /**
         * Factors:
         * - cost
         * - turns to capture
         * - type of target (normal / bonus)
         * - average distance to my icebergs
         * - iceberg parameters (upgrade cost, upgrade value)
         * - enemy defend score
         * - penguins per turn delta
         */

        IceBuilding target = plan.target;

        int penguinsSent = plan.getTotalPenguinsSent();
        double penguinsSentScore = GameUtil.normalizeScore(penguinsSent, 1000, 1);

        int turnsToCapture = plan.getTurnsToCapture();
        double turnsToCaptureScore = GameUtil.normalizeScore(turnsToCapture, 100, 1 );

        double targetTypeScore = 0;
        if(target == game.getBonusIceberg()) {
            int amountOfIcebergs = game.getMyIcebergs().length + game.getEnemyIcebergs().length + (int)(0.3 * game.getNeutralIcebergs().length);
            targetTypeScore = GameUtil.normalizeScore(BonusIcebergUtil.getAveragePenguinsPerTurnPerIceberg(game) * amountOfIcebergs, 0, 200);
        }

        double islandParametersScore = 0; // TODO add max level parameter

        if(target instanceof Iceberg) {
            Iceberg iceberg = (Iceberg) target;
            islandParametersScore = GameUtil.normalizeScore((double)iceberg.upgradeValue / iceberg.upgradeCost, 0, 1);
        }

        double averageDistanceToMyIcebergs = GameUtil.getAverageDistanceToMyIcebergs(game, target);
        double averageDistanceToMyIcebergsScore = GameUtil.normalizeScore(averageDistanceToMyIcebergs, 100, 1);

        int penguinsPerTurnDelta = (target instanceof Iceberg) ? ((Iceberg) target).penguinsPerTurn : 0;
        if(GameUtil.isEnemy(game, target)){
            penguinsPerTurnDelta *= 2;
        }
        double penguinsPerTurnDeltaScore = GameUtil.normalizeScore(penguinsPerTurnDelta, 0, 30);

        double averageDistanceToEnemyIcebergs = GameUtil.getAverageDistanceToEnemyIcebergs(game, target);
        double enemyDefendScore = GameUtil.normalizeScore(averageDistanceToMyIcebergs - averageDistanceToEnemyIcebergs, 30, -30);
        if(averageDistanceToEnemyIcebergs == 0) {
            enemyDefendScore = 1;
        }

        Log.log("Target: " + target + ", Average distance to enemy icebergs: " + averageDistanceToEnemyIcebergs +  ", average distance to mine: " + averageDistanceToMyIcebergs + " so score = " + enemyDefendScore);
        // TODO improve enemy defend score calculation by using weighted average


        double score = GameUtil.computeFactoredScore(
                penguinsSentScore, 0.2,
                turnsToCaptureScore, 0.1,
                targetTypeScore, 0.15,
                islandParametersScore, 0.05,
                averageDistanceToMyIcebergsScore, 0.1,
                penguinsPerTurnDeltaScore, 0.3,
                enemyDefendScore, 0.1
        );

        // If the enemy will be able to capture it back, there is no point in attacking it
        if(enemyDefendScore < 0.35){
            score *= 0;
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
                    Log.log("Performed action: target = " + plan.target +  ", action = " + planAction.toString());
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
