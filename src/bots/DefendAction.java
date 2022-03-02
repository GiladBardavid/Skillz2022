package bots;

import penguin_game.*;
import java.util.*;

public class DefendAction extends Action {

    Iceberg from;

    Iceberg to;

    int penguinAmount;

    int distance;

    public DefendAction(Iceberg from, Iceberg to, int penguinAmount) {
        this.from = from;
        this.to = to;
        this.penguinAmount = penguinAmount;
        this.distance = from.getTurnsTillArrival(to);
    }

    @Override
    public boolean mustImprovePrediction() {
        return false;
    }

    @Override
    double computeScoreImpl(Game game) {
        return 0.1;
    }

    @Override
    public boolean executeIfPossible(Game game) {
        if(from.penguinAmount >= penguinAmount) {
            from.sendPenguins(to, penguinAmount);
            Log.log("Performed defend action: target = " + IcebergUtil.toString(to) +  ", from = " + IcebergUtil.toString(from) + ", amount = " + penguinAmount);
            return true;
        }

        Log.log("Could not perform defend action because sender doesn't have enough penguins.");
        return false;
    }

    @Override
    public String toString() {
        return "DefendAction(" + IcebergUtil.toString(from) + ", " + IcebergUtil.toString(to) + ", " + penguinAmount + ")";
    }

    @Override
    public Action ageByTurn() {
        return null;
    }
}
