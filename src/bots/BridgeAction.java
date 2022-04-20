package bots;

import penguin_game.*;
import java.util.*;

public class BridgeAction extends Action{

    Iceberg from;
    Iceberg to;

    int cost;
    int duration;
    double speedMultiplier;


    public BridgeAction(Iceberg from, Iceberg to){
        this.from = from;
        this.to = to;
        this.cost = from.bridgeCost;
        this.duration = from.maxBridgeDuration;
        this.speedMultiplier = from.bridgeSpeedMultiplier;
    }

    @Override
    public boolean mustImprovePrediction() {
        return true;
    }

    @Override
    double computeScoreImpl(Game game) {
        for(Bridge bridge : from.bridges) {
            if(bridge.getEdges()[0] == to || bridge.getEdges()[1] == to) {
                if(bridge.duration > 1) { // If the bridge will disappear next turn we can rebuild it
                    return 0;
                }
            }
        }

        if(predictionAfterAction.canBeAtRisk(from)) {
            return 0;
        }

        return 0.2 * predictionAfterAction.computeScore(); // score will be determined by prediction
    }

    @Override
    public boolean executeIfPossible(Game game) {
        if(from.penguinAmount >= from.bridgeCost) {
            from.createBridge(to);
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return "BridgeAction{" +
                "from=" + IcebergUtil.toString(from) +
                ", to=" + IcebergUtil.toString(to) +
                '}';
    }

    @Override
    public Action ageByTurn() {
        return null;
    }

    @Override
    public Set<Iceberg> getIcebergsThatBuiltBridgeNow() {
        Set<Iceberg> icebergs = new HashSet<>();
        icebergs.add(from);
        return icebergs;
    }
}
