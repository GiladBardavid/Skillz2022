package bots;

import penguin_game.*;
import java.util.*;

public class NeededHelp {

    public int howManyPenguins;
    public int inHowManyTurns;

    public NeededHelp(int howManyPenguins, int inHowManyTurns) {
        this.howManyPenguins = howManyPenguins;
        this.inHowManyTurns = inHowManyTurns;
    }



    public static NeededHelp getNeededHelp(Game game, IceBuilding myIceBuilding) {

        int currentAmount = myIceBuilding.penguinAmount;

        int[] penguinAmountAfterTurn = new int[GameUtil.getFarthestPenguinGroupHeadedTowardIceBuilding(game, myIceBuilding)];

        int penguinsPerTurn = (myIceBuilding instanceof Iceberg) ? ((Iceberg)myIceBuilding).penguinsPerTurn : 0;

        for(int i = 0; i < GameUtil.getFarthestPenguinGroupHeadedTowardIceBuilding(game, myIceBuilding); i++) {
            penguinAmountAfterTurn[i] = currentAmount + (i * penguinsPerTurn);
        }

        for(PenguinGroup incomingEnemyPenguinGroup : GameUtil.getEnemyPenguinsGroupsHeadedTowardIceBuilding(game, myIceBuilding)) {
            int attackingPenguinGroupTurnsTillArrival = incomingEnemyPenguinGroup.turnsTillArrival;

            for(int i = attackingPenguinGroupTurnsTillArrival; i < penguinAmountAfterTurn.length; i++) {
                int penguinAmountToRemove = incomingEnemyPenguinGroup.penguinAmount;
                penguinAmountAfterTurn[i] -= penguinAmountToRemove;
            }
        }

        for(PenguinGroup incomingMyPenguinGroup : GameUtil.getMyPenguinGroupsHeadedTowardIceBuilding(game, myIceBuilding)) {
            int helpingPenguinGroupTurnsTillArrival = incomingMyPenguinGroup.turnsTillArrival;

            for(int i = helpingPenguinGroupTurnsTillArrival; i < penguinAmountAfterTurn.length; i++) {
                int penguinAmountToAdd = incomingMyPenguinGroup.penguinAmount;
                penguinAmountAfterTurn[i] += penguinAmountToAdd;
            }
        }

        // TODO add bonus iceberg bonus-penguins

        for(int i = 0; i < penguinAmountAfterTurn.length; i++) {
            if(penguinAmountAfterTurn[i] <= 0) {
                return new NeededHelp(-penguinAmountAfterTurn[i], i);
            }
        }

        // Doesn't need help
        return null;
    }
}
