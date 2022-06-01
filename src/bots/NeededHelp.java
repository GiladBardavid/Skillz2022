package bots;

import penguin_game.*;

public class NeededHelp {


    /*


    ----------------------------------------------------------------------------------------------
    ATTENTION

    we are not using this class at all
    don't learn it
    ----------------------------------------------------------------------------------------------



















     */



    public int howManyPenguins;
    public int inHowManyTurns;

    public NeededHelp(int howManyPenguins, int inHowManyTurns) {
        this.howManyPenguins = howManyPenguins;
        this.inHowManyTurns = inHowManyTurns;
    }



    public static NeededHelp getNeededHelp(Game game, IceBuilding myIceBuilding) {

        int currentAmount = myIceBuilding.penguinAmount;

        // + 1 to length because we want to check the last turn as well
        int[] penguinAmountAfterTurn = new int[GameUtil.getFarthestPenguinGroupHeadedTowardIceBuilding(game, myIceBuilding) + 1];

        int penguinsPerTurn = (myIceBuilding instanceof Iceberg) ? ((Iceberg)myIceBuilding).penguinsPerTurn : 0;

        for(int i = 0; i < penguinAmountAfterTurn.length; i++) {
            penguinAmountAfterTurn[i] = currentAmount + (i * penguinsPerTurn);
        }

        for(PenguinGroup incomingEnemyPenguinGroup : GameUtil.getEnemyPenguinGroupsHeadedTowardIceBuilding(game, myIceBuilding)) {
            Log.log("C_0_2: subtracting " + incomingEnemyPenguinGroup.penguinAmount + " penguins");
            int attackingPenguinGroupTurnsTillArrival = incomingEnemyPenguinGroup.turnsTillArrival;

            for(int i = attackingPenguinGroupTurnsTillArrival; i < penguinAmountAfterTurn.length; i++) {
                int penguinAmountToRemove = incomingEnemyPenguinGroup.penguinAmount;
                penguinAmountAfterTurn[i] -= penguinAmountToRemove;
            }
        }

        for(PenguinGroup incomingMyPenguinGroup : GameUtil.getMyPenguinGroupsHeadedTowardIceBuilding(game, myIceBuilding)) {
            Log.log("C_0_3: adding " + incomingMyPenguinGroup.penguinAmount + " penguins");
            int helpingPenguinGroupTurnsTillArrival = incomingMyPenguinGroup.turnsTillArrival;

            for(int i = helpingPenguinGroupTurnsTillArrival; i < penguinAmountAfterTurn.length; i++) {
                int penguinAmountToAdd = incomingMyPenguinGroup.penguinAmount;
                penguinAmountAfterTurn[i] += penguinAmountToAdd;
            }
        }

        // TODO add bonus iceberg bonus-penguins

        for(int i = 0; i < penguinAmountAfterTurn.length; i++) {
            Log.log("C_0_1: penguinAmountAfterTurn[" + i + "] = " + penguinAmountAfterTurn[i]);
            if(penguinAmountAfterTurn[i] <= 0) {
                Log.log("C_0_0: IceBuilding " + myIceBuilding + " needs help! " + (-penguinAmountAfterTurn[i] + 1) + " penguins in " + i + " turns.");
                // + 1 because we want to make the iceberg ours, not just neutral
                return new NeededHelp(-penguinAmountAfterTurn[i] + 1, i);
            }
        }

        // Doesn't need help
        return null;
    }






    public static NeededHelp getNeededHelpForNeutralIceberg(Game game, IceBuilding neutralIceBuildingToHelp) {
        int currentAmount = neutralIceBuildingToHelp.penguinAmount;

        // + 1 to length because we want to check the last turn as well
        int[] penguinAmountAfterTurn = new int[GameUtil.getFarthestPenguinGroupHeadedTowardIceBuilding(game, neutralIceBuildingToHelp) + 1];

        int penguinsPerTurn = (neutralIceBuildingToHelp instanceof Iceberg) ? ((Iceberg)neutralIceBuildingToHelp).penguinsPerTurn : 0;

        for(int i = 0; i < penguinAmountAfterTurn.length; i++) {
            penguinAmountAfterTurn[i] = currentAmount;
        }

        for(PenguinGroup incomingEnemyPenguinGroup : GameUtil.getEnemyPenguinGroupsHeadedTowardIceBuilding(game, neutralIceBuildingToHelp)) {
            Log.log("C_0_4: subtracting " + incomingEnemyPenguinGroup.penguinAmount + " penguins");
            int attackingPenguinGroupTurnsTillArrival = incomingEnemyPenguinGroup.turnsTillArrival;

            for(int i = attackingPenguinGroupTurnsTillArrival; i < penguinAmountAfterTurn.length; i++) {
                int penguinAmountToRemove = incomingEnemyPenguinGroup.penguinAmount;
                penguinAmountAfterTurn[i] -= penguinAmountToRemove;
            }
        }

        for(PenguinGroup incomingMyPenguinGroup : GameUtil.getMyPenguinGroupsHeadedTowardIceBuilding(game, neutralIceBuildingToHelp)) {
            Log.log("C_0_5: adding " + incomingMyPenguinGroup.penguinAmount + " penguins");
            int helpingPenguinGroupTurnsTillArrival = incomingMyPenguinGroup.turnsTillArrival;

            for(int i = helpingPenguinGroupTurnsTillArrival; i < penguinAmountAfterTurn.length; i++) {
                int penguinAmountToAdd = incomingMyPenguinGroup.penguinAmount;
                penguinAmountAfterTurn[i] += penguinAmountToAdd;
            }
        }

        // TODO add bonus iceberg bonus-penguins

        for(int i = 0; i < penguinAmountAfterTurn.length; i++) {
            Log.log("C_0_6: penguinAmountAfterTurn[" + i + "] = " + penguinAmountAfterTurn[i]);
            if(penguinAmountAfterTurn[i] <= 0) {
                Log.log("C_0_7: IceBuilding " + neutralIceBuildingToHelp + " needs help! " + (-penguinAmountAfterTurn[i] + 1 + penguinsPerTurn) + " penguins in " + (i + 1) + " turns.");
                // + 1 because we want to make the iceberg ours, not just neutral
                //  + penguinsPerTurn because we always want to send the penguins 1 turn later
                // +1 to the i because we always attack 1 turn later
                return new NeededHelp(-penguinAmountAfterTurn[i] + 1 + penguinsPerTurn, i + 1);
            }
        }

        // Doesn't need help
        return null;
    }










    public static NeededHelp getNeededHelpEnemyPOV(Game game, IceBuilding enemyIceBuilding) {

        int currentAmount = enemyIceBuilding.penguinAmount;

        // + 1 to length because we want to check the last turn as well
        int[] penguinAmountAfterTurn = new int[GameUtil.getFarthestPenguinGroupHeadedTowardIceBuilding(game, enemyIceBuilding) + 1];

        int penguinsPerTurn = (enemyIceBuilding instanceof Iceberg) ? ((Iceberg)enemyIceBuilding).penguinsPerTurn : 0;

        for(int i = 0; i < penguinAmountAfterTurn.length; i++) {
            penguinAmountAfterTurn[i] = currentAmount + (i * penguinsPerTurn);
        }

        for(PenguinGroup incomingEnemyPenguinGroup : GameUtil.getEnemyPenguinGroupsHeadedTowardIceBuilding(game, enemyIceBuilding)) {
            Log.log("C_1_0: adding " + incomingEnemyPenguinGroup.penguinAmount + " penguins");
            int attackingPenguinGroupTurnsTillArrival = incomingEnemyPenguinGroup.turnsTillArrival;

            for(int i = attackingPenguinGroupTurnsTillArrival; i < penguinAmountAfterTurn.length; i++) {
                int penguinAmountToAdd = incomingEnemyPenguinGroup.penguinAmount;
                penguinAmountAfterTurn[i] += penguinAmountToAdd;
            }
        }

        for(PenguinGroup incomingMyPenguinGroup : GameUtil.getMyPenguinGroupsHeadedTowardIceBuilding(game, enemyIceBuilding)) {
            Log.log("C_1_1: subtracting " + incomingMyPenguinGroup.penguinAmount + " penguins");
            int helpingPenguinGroupTurnsTillArrival = incomingMyPenguinGroup.turnsTillArrival;

            for(int i = helpingPenguinGroupTurnsTillArrival; i < penguinAmountAfterTurn.length; i++) {
                int penguinAmountToSubtract = incomingMyPenguinGroup.penguinAmount;
                penguinAmountAfterTurn[i] -= penguinAmountToSubtract;
            }
        }

        // TODO add bonus iceberg bonus-penguins

        for(int i = 0; i < penguinAmountAfterTurn.length; i++) {
            Log.log("C_1_2: penguinAmountAfterTurn[" + i + "] = " + penguinAmountAfterTurn[i]);
            if(penguinAmountAfterTurn[i] <= 0) {
                Log.log("C_1_3: IceBuilding " + enemyIceBuilding + " needs help! " + (-penguinAmountAfterTurn[i] + 1) + " penguins in " + i + " turns.");
                // + 1 because we want to make the iceberg ours, not just neutral
                return new NeededHelp(-penguinAmountAfterTurn[i] + 1, i);
            }
        }

        // Doesn't need help
        return null;
    }





    public static NeededHelp getNeededHelpForNeutralIcebergEnemyPOV(Game game, IceBuilding neutralIceBuildingToHelp) {
        int currentAmount = neutralIceBuildingToHelp.penguinAmount;

        // + 1 to length because we want to check the last turn as well
        int[] penguinAmountAfterTurn = new int[GameUtil.getFarthestPenguinGroupHeadedTowardIceBuilding(game, neutralIceBuildingToHelp) + 1];

        int penguinsPerTurn = (neutralIceBuildingToHelp instanceof Iceberg) ? ((Iceberg)neutralIceBuildingToHelp).penguinsPerTurn : 0;

        for(int i = 0; i < penguinAmountAfterTurn.length; i++) {
            penguinAmountAfterTurn[i] = currentAmount;
        }

        for(PenguinGroup incomingEnemyPenguinGroup : GameUtil.getEnemyPenguinGroupsHeadedTowardIceBuilding(game, neutralIceBuildingToHelp)) {
            Log.log("C_1_4: subtracting " + incomingEnemyPenguinGroup.penguinAmount + " penguins");
            int attackingPenguinGroupTurnsTillArrival = incomingEnemyPenguinGroup.turnsTillArrival;

            for(int i = attackingPenguinGroupTurnsTillArrival; i < penguinAmountAfterTurn.length; i++) {
                int penguinAmountToAdd = incomingEnemyPenguinGroup.penguinAmount;
                penguinAmountAfterTurn[i] += penguinAmountToAdd;
            }
        }

        for(PenguinGroup incomingMyPenguinGroup : GameUtil.getMyPenguinGroupsHeadedTowardIceBuilding(game, neutralIceBuildingToHelp)) {
            Log.log("C_1_5: adding " + incomingMyPenguinGroup.penguinAmount + " penguins");
            int helpingPenguinGroupTurnsTillArrival = incomingMyPenguinGroup.turnsTillArrival;

            for(int i = helpingPenguinGroupTurnsTillArrival; i < penguinAmountAfterTurn.length; i++) {
                int penguinAmountToSubtract = incomingMyPenguinGroup.penguinAmount;
                penguinAmountAfterTurn[i] -= penguinAmountToSubtract;
            }
        }

        // TODO add bonus iceberg bonus-penguins

        for(int i = 0; i < penguinAmountAfterTurn.length; i++) {
            Log.log("C_1_6: penguinAmountAfterTurn[" + i + "] = " + penguinAmountAfterTurn[i]);
            if(penguinAmountAfterTurn[i] <= 0) {
                Log.log("C_1_7: IceBuilding " + neutralIceBuildingToHelp + " needs help! " + (-penguinAmountAfterTurn[i] + 1 + penguinsPerTurn) + " penguins in " + (i + 1) + " turns.");
                // + 1 because we want to make the iceberg ours, not just neutral
                //  + penguinsPerTurn because we always want to send the penguins 1 turn later
                // +1 to the i because we always attack 1 turn later
                return new NeededHelp(-penguinAmountAfterTurn[i] + 1 + penguinsPerTurn, i + 1);
            }
        }

        // Doesn't need help
        return null;
    }






    public static boolean willBeMine(Game game, IceBuilding iceBuildingToCheck) {

        if(GameUtil.isEnemy(game, iceBuildingToCheck)) {
            return getNeededHelpEnemyPOV(game, iceBuildingToCheck) != null;
        }

        else if(GameUtil.isNeutral(game, iceBuildingToCheck)) {
            return getNeededHelpForNeutralIcebergEnemyPOV(game, iceBuildingToCheck) != null;
        }

        else if(GameUtil.isMine(game, iceBuildingToCheck)) {
            return getNeededHelp(game, iceBuildingToCheck) == null;
        }

        // Unreachable
        return false;
    }


    public static boolean willSurvive(Game game, IceBuilding myIceberg) {
        return getNeededHelp(game, myIceberg) == null;
    }



    public static int getMaxThatCanSend(Game game, Iceberg sendingIceberg) {
        return 0;
    }
}
