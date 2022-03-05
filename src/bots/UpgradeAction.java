package bots;

import penguin_game.*;
import java.util.*;

public class UpgradeAction extends Action {

    Iceberg upgradingIceberg;

    public UpgradeAction(Iceberg target) {
        this.upgradingIceberg = target;
    }

    public String toString() {
        return "Upgrade action: " + upgradingIceberg;
    }



    @Override
    public boolean mustImprovePrediction() {
        return true;
    }

    @Override
    double computeScoreImpl(Game game) {

        if(predictionAfterAction.canBeAtRisk(upgradingIceberg)) {
            return 0;
        }

        double score = predictionAfterAction.computeScore();

        return score;

        // TODO enemy defend score

    }

    @Override
    public boolean executeIfPossible(Game game) {
        Log.log("Trying upgrade action: target = " + upgradingIceberg + " canUpgrade = " + upgradingIceberg.canUpgrade());
        if(upgradingIceberg.canUpgrade()){
            upgradingIceberg.upgrade();
            Log.log("Upgrade action executed: target = " + upgradingIceberg);
            return true;
        }
        return false;
    }


    @Override
    public Set<Iceberg> getIcebergsThatUpgradedNow() {
        Set<Iceberg> icebergs = new HashSet<>();
        icebergs.add(upgradingIceberg);
        return icebergs;
    }

    @Override
    public Action ageByTurn() {
        return null;
    }
}
