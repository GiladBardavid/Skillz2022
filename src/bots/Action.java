package bots;

import penguin_game.*;
import java.util.*;

public abstract class Action {

    double score;

    public double getScore() {
        return score;
    }

    abstract double computeScoreImpl(Game game);

    public void computeScore(Game game) {
        setScore(computeScoreImpl(game));
    }

    public abstract boolean executeIfPossible(Game game);

    public abstract Set<Iceberg> getModifiedIcebergs();

    public void setScore(double score) {
        this.score = score;
    }
}
