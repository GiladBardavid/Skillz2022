package bots;

import penguin_game.*;
import java.util.*;

public class AttackPlan {

    List<AttackPlanAction> actions = new ArrayList<>();

    IceBuilding target;

    public AttackPlan(IceBuilding target) {
        this.target = target;
    }

    public void addAction(Iceberg sender, int penguinAmount, int turnsUntilSend) {
        actions.add(new AttackPlanAction(sender, penguinAmount, turnsUntilSend));
    }

    public int getTurnsToCapture() {
        int maxTurns = 0;
        for (AttackPlanAction action : actions) {
            maxTurns = Math.max(maxTurns, action.turnsToSend + action.sender.getTurnsTillArrival(target));
        }
        return maxTurns;
    }

    public int getTotalPenguinsSent() {
        int result = 0;
        for(AttackPlanAction action : actions) {
            result += action.penguinAmount;
        }
        return result;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (AttackPlanAction action : actions) {
            sb.append(action.toString()).append("\n");
        }
        return "Plan: target: " + target + " " + sb.toString();
    }

    static class AttackPlanAction {
        public Iceberg sender;
        public int penguinAmount;
        public int turnsToSend;

        public AttackPlanAction(Iceberg sender, int penguinAmount, int turnsToSend) {
            this.sender = sender;
            this.penguinAmount = penguinAmount;
            this.turnsToSend = turnsToSend;
        }

        public String toString() {
            return sender.toString() + ": " + penguinAmount + " penguins, " + turnsToSend + " turns to send";
        }
    }

}
