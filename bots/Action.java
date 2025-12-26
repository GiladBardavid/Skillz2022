package bots;

import penguin_game.*;
import java.util.*;

public abstract class Action {

    double score;

    public abstract boolean mustImprovePrediction();

    public double getScore() {
        return score;
    }

    abstract double computeScoreImpl(Game game);

    public void computeScore(Game game) {
        setScore(computeScoreImpl(game));
    }

    public abstract boolean executeIfPossible(Game game);

    public void setScore(double score) {
        this.score = score;
    }

    public Set<Iceberg> getIcebergsThatSentNow(){
        return new HashSet<>();
    }

    public Set<Iceberg> getIcebergsThatUpgradedNow(){
        return new HashSet<>();
    }

    public Set<Iceberg> getIcebergsThatBuiltBridgeNow(){
        return new HashSet<>();
    }

    public abstract Action ageByTurn();

    public Prediction predictionAfterAction;

    public Prediction predictionBeforeAction;
}
