package bots;

import penguin_game.*;

public abstract class Action {

    double score;

    public double getScore() {
        return score;
    }

    abstract double computeScoreImpl(Game game);

    public void computeScore(Game game) {
        setScore(computeScoreImpl(game));
    }

    public abstract void executeIfPossible(Game game);

    public void setScore(double score) {
        this.score = score;
    }
}
