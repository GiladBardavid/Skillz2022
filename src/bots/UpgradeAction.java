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

        double score = predictionAfterAction.computeScore();

        return score;

        // TODO enemy defend score

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

    @Override
    public Action ageByTurn() {
        return null;
    }
}
