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

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (AttackPlanAction action : actions) {
            sb.append(action.toString()).append("\n");
        }
        return sb.toString();
    }
}
