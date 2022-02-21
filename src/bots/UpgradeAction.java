package bots;

import penguin_game.*;
import java.util.*;

public class UpgradeAction extends Action {

    Iceberg target;

    public UpgradeAction(Iceberg target) {
        this.target = target;
    }

    public String toString() {
        return "Upgrade action: " + target;
    }


    @Override
    double computeScoreImpl(Game game) {
        int penguinsPerTurnDelta = target.upgradeValue;
        int penguinCost = target.upgradeCost;
        double averageDistanceFromMyIcebergs = GameUtil.getAverageDistanceToMyIcebergs(game, target);
        double averageDistanceFromEnemyIcebergs = GameUtil.getAverageDistanceToEnemyIcebergs(game, target);

        double penguinsPerTurnDeltaScore = GameUtil.normalizeScore(penguinsPerTurnDelta, 0, 10);
        double penguinCostScore = GameUtil.normalizeScore(penguinCost, 100, 0);
        double averageDistanceFromMyIcebergsScore = GameUtil.normalizeScore(averageDistanceFromMyIcebergs, 100, 0);
        double enemyDefendScore = GameUtil.normalizeScore(averageDistanceFromMyIcebergs - averageDistanceFromEnemyIcebergs, 30, -30);

        double score = GameUtil.computeFactoredScore(
            penguinsPerTurnDeltaScore, 0.45,
            penguinCostScore, 0.2,
            averageDistanceFromMyIcebergsScore, 0.15,
            enemyDefendScore, 0.2
        );

        return score;
    }

    @Override
    public boolean executeIfPossible(Game game) {
        Log.log("Trying upgrade action: target = " + target + " canUpgrade = " + target.canUpgrade());
        if(target.canUpgrade()){
            target.upgrade();
            Log.log("Upgrade action executed: target = " + target);
            return true;
        }
        return false;
    }


    @Override
    public Set<Iceberg> getIcebergsThatUpgradedNow() {
        Set<Iceberg> icebergs = new HashSet<>();
        icebergs.add(target);
        return icebergs;
    }
}
