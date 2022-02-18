package bots;

import penguin_game.*;

public class AttackAction extends Action {

    IceBuilding target;

    public AttackAction(IceBuilding target) {
        this.target = target;
    }

    @Override
    public double computeScoreImpl(Game game) {
        /**
         * Score by penguins-per-turn gain
         * Score by min-time-to-capture
         * Score by enemy ability to defend
         */

        int penguinsPerTurnDelta = (target instanceof Iceberg) ? ((Iceberg) target).penguinsPerTurn : 0;

        if(GameUtil.isEnemy(game, target)){
            penguinsPerTurnDelta *= 2;
        }


        int minTimeToCapture = GameUtil.getMinTimeToCapture(game, target);

        // If I can't capture, return 0
        if(minTimeToCapture == -1) {
            return 0;
        }

        // temporary
        double averageDistanceToMyIcebergs = GameUtil.getAverageDistanceToMyIcebergs(game, target);
        double averageDistanceToEnemyIcebergs = GameUtil.getAverageDistanceToEnemyIcebergs(game, target);

        double enemyDefendScore = averageDistanceToMyIcebergs - averageDistanceToEnemyIcebergs;

        double scorePenguins = GameUtil.normalizeScore(penguinsPerTurnDelta, 0, 20);
        double scoreTime = GameUtil.normalizeScore(100 - minTimeToCapture, 0, 100);
        double scoreDefend = GameUtil.normalizeScore(enemyDefendScore, -100, 100);

        double penguinsPerTurnFactor = 0.5;
        double minTimeToCaptureFactor = 0.3;
        double enemyDefendFactor = 0.2;

        double newScore = GameUtil.computeFactoredScore(scorePenguins, penguinsPerTurnFactor, scoreTime, minTimeToCaptureFactor, scoreDefend, enemyDefendFactor);
        return newScore;
    }

    @Override
    public void executeIfPossible(Game game) {

    }

    @Override
    public String toString() {
        return "AttackAction [target=" + target + "]  score = " + getScore();
    }
}
