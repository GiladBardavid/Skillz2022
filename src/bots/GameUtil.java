package bots;

import penguin_game.*;

import java.util.*;
import java.util.stream.Collectors;

import static bots.IceBuildingState.Owner.ENEMY;
import static bots.IceBuildingState.Owner.ME;

public class GameUtil {



    public static int maxLengthBetweenTwoIcebergs = -1;

    public static Prediction prediction;

    /**
     * Returns how many penguins will an enemy or a neutral iceberg have in turn x.
     * Positive count - enemy or neutral.
     * Negative count - mine.
     * @param game current game state
     * @param destination iceberg to check
     * @param inHowManyTurns the amount of turns ahead we want to check
     * @return how many penguins will the enemy iceberg have in turn x
     */
    public static int getPenguinAmountInTurnXForEnemyOrNeutralIceBuilding(Game game, IceBuilding destination, int inHowManyTurns) {

        // Initiate result that we will update for every turn
        int currentPenguinAmount = destination.penguinAmount;

        // Store 3 maps, one for the incoming my penguin groups, one for the incoming enemy penguin groups and one for the incoming bonus penguins.
        Map<Integer, Integer> incomingMyPenguinGroups = IcebergUtil.howManyMyPenguinsWillArriveToADestinationInXTurns(game, destination);
        Map<Integer, Integer> incomingEnemyPenguinGroups = IcebergUtil.howManyEnemyPenguinsWillArriveToADestinationInXTurns(game, destination);
        Map<Integer, Integer> incomingBonusPenguins = IcebergUtil.howManyPenguinsWillAnIcebergGetAtEachTurnFromBonusIceberg(game, destination);

        //TODO set it so bonus icebergs don't get the bonus iceberg's bonus

        // A variable to keep track of how many turns we have gone through
        // Initialized to 1 because we already have the current turn's information
        int turnCounter = 1;

        while(turnCounter <= inHowManyTurns) {

            // Add the destination's penguins-per-turn
            // Initialized at 0 to avoid an error saying it isn't initialized.
            int penguinsPerTurn = 0;

            // If the destination is an iceberg, we need to change the penguin amount according to the penguins-per-turn of the destination.
            if(destination instanceof Iceberg) {

                // If the destination is neutral, it doesn't create penguins.
                // If the destination is enemy, we need to add the penguins-per-turn.
                // If the destination is mine, we need to subtract the penguins-per-turn, because we are looking from the POV of the enemy in this function.
                String destinationOwner = playerToString(game, destination.owner);
                switch (destinationOwner) {
                    case "Neutral":
                        // If the destination is neutral, it doesn't create penguins.
                        penguinsPerTurn = 0;
                        break;
                    case "Enemy":
                        // If the destination is enemy, we need to add the penguins-per-turn.
                        if(currentPenguinAmount > 0) {
                            penguinsPerTurn = ((Iceberg) destination).penguinsPerTurn;
                        }

                        // If the destination is mine (the enemy penguin will have a negative penguin amount),
                        // we need to subtract the penguins-per-turn, because we are looking from the POV of the enemy in this function.
                        else if(currentPenguinAmount < 0){
                            penguinsPerTurn = -((Iceberg) destination).penguinsPerTurn;
                        }

                        // If the iceberg is neutral, it doesn't create penguins.
                        else {
                            penguinsPerTurn = 0;
                        }
                        break;
                }
            }
            // If the destination is not an iceberg (it is a bonus iceberg), it doesn't spawn penguins on itself, so it's penguins-per-turn is 0.
            else {
                penguinsPerTurn = 0;
            }


            // Add the penguins-per-turn to the current penguin amount
            currentPenguinAmount += penguinsPerTurn;

            // Update the current penguin amount according to the penguin groups that will be arriving.
            // Incoming my penguin groups
            if(incomingMyPenguinGroups.containsKey(turnCounter)) {
                currentPenguinAmount -= incomingMyPenguinGroups.get(turnCounter);
            }

            // Incoming enemy penguin groups
            if(incomingEnemyPenguinGroups.containsKey(turnCounter)) {
                currentPenguinAmount += incomingEnemyPenguinGroups.get(turnCounter);
            }

            // Incoming bonus penguins
            if(incomingBonusPenguins.containsKey(turnCounter)) {
                currentPenguinAmount += incomingBonusPenguins.get(turnCounter);
            }


            // Increment the turn counter
            turnCounter++;

            Log.log("B_0: After " + turnCounter + " turns, the penguin amount is " + currentPenguinAmount);
        }

        // Return the penguin amount after the specified number of turns.
        Log.log("B_1 Returning: " + currentPenguinAmount);
        return currentPenguinAmount;
    }






    /**
     * Returns how many penguins will one of my icebergs have in turn x.
     * Positive count - mine.
     * Negative count - enemy or neutral.
     * @param game current game state
     * @param myIceBuilding IceBuilding to check
     * @param inHowManyTurns the amount of turns ahead we want to check
     * @return how many penguins will my iceberg have in turn x
     */
    public static int getPenguinAmountInTurnXForMyIceBuilding(Game game, IceBuilding myIceBuilding, int inHowManyTurns) {
        // Initiate result that we will update for every turn
        int currentPenguinAmount = myIceBuilding.penguinAmount;

        // Store 3 maps, one for the incoming my penguin groups, one for the incoming enemy penguin groups and one for the incoming bonus penguins.
        Map<Integer, Integer> incomingMyPenguinGroups = IcebergUtil.howManyMyPenguinsWillArriveToADestinationInXTurns(game, myIceBuilding);
        Map<Integer, Integer> incomingEnemyPenguinGroups = IcebergUtil.howManyEnemyPenguinsWillArriveToADestinationInXTurns(game, myIceBuilding);
        Map<Integer, Integer> incomingBonusPenguins = IcebergUtil.howManyPenguinsWillAnIcebergGetAtEachTurnFromBonusIceberg(game, myIceBuilding);

        //TODO set it so bonus icebergs don't get the bonus iceberg's bonus

        // A variable to keep track of how many turns we have gone through
        // Initialized to 1 because we already have the current turn's information
        int turnCounter = 1;

        while(turnCounter <= inHowManyTurns) {

            // Add the destination's penguins-per-turn
            // Initialized at 0 to avoid an error saying it isn't initialized.
            int penguinsPerTurn = 0;

            // If the destination is an iceberg, we need to change the penguin amount according to the penguins-per-turn of the destination.
            if(myIceBuilding instanceof Iceberg) {

                // If the destination is neutral, it doesn't create penguins.
                // If the destination is enemy, we need to add the penguins-per-turn.
                // If the destination is mine, we need to subtract the penguins-per-turn, because we are looking from the POV of the enemy in this function.
                String destinationOwner = playerToString(game, myIceBuilding.owner);
                switch (destinationOwner) {
                    case "Neutral":
                        // If the destination is neutral, it doesn't create penguins.
                        penguinsPerTurn = 0;
                        break;
                    case "Enemy":
                        // If the destination is enemy, we need to add the penguins-per-turn.
                        if(currentPenguinAmount > 0) {
                            penguinsPerTurn = ((Iceberg) myIceBuilding).penguinsPerTurn;
                        }

                        // If the destination is mine (the enemy penguin will have a negative penguin amount),
                        // we need to subtract the penguins-per-turn, because we are looking from the POV of the enemy in this function.
                        else if(currentPenguinAmount < 0){
                            penguinsPerTurn = -((Iceberg) myIceBuilding).penguinsPerTurn;
                        }

                        // If the iceberg is neutral, it doesn't create penguins.
                        else {
                            penguinsPerTurn = 0;
                        }
                        break;
                }
            }
            // If the destination is not an iceberg (it is a bonus iceberg), it doesn't spawn penguins on itself, so it's penguins-per-turn is 0.
            else {
                penguinsPerTurn = 0;
            }


            // Add the penguins-per-turn to the current penguin amount
            currentPenguinAmount += penguinsPerTurn;

            // Update the current penguin amount according to the penguin groups that will be arriving.
            // Incoming my penguin groups
            if(incomingMyPenguinGroups.containsKey(turnCounter)) {
                currentPenguinAmount -= incomingMyPenguinGroups.get(turnCounter);
            }

            // Incoming enemy penguin groups
            if(incomingEnemyPenguinGroups.containsKey(turnCounter)) {
                currentPenguinAmount += incomingEnemyPenguinGroups.get(turnCounter);
            }

            // Incoming bonus penguins
            if(incomingBonusPenguins.containsKey(turnCounter)) {
                currentPenguinAmount += incomingBonusPenguins.get(turnCounter);
            }

            // Increment the turn counter
            turnCounter++;

            Log.log("\nB_4_3: After " + turnCounter + " turns, the penguin amount is " + currentPenguinAmount + "\n");
        }

        // Return the penguin amount after the specified number of turns.
        Log.log("B_4_4 Returning: " + currentPenguinAmount);
        return currentPenguinAmount;
    }






    /**
     * A function to get a list containing all the icebergs that are either the enemy's or neutral.
     * @param game current game state
     * @return A list containing all the icebergs that are either the enemy's or neutral.
     */
    public static List<Iceberg> getEnemyOrNeutralIcebergs(Game game) {
        // Initiate result list, and use a stream to filter only the icebergs that are either the enemy's or neutral.
        List<Iceberg> result = Arrays.stream(game.getAllIcebergs())
                .filter(iceberg -> playerToString(game, iceberg.owner).equals("Enemy") || playerToString(game, iceberg.owner).equals("Neutral"))
                .collect(Collectors.toList());

        // Return the result list.
        return result;
    }


    /**
     * A function that converts a player to a string representation of it.
     * @param game current game state
     * @param player the player to convert
     * @return A string representation of the player.
     */
    public static String playerToString(Game game, Player player) {
        // If the player is me, return "Me"
        if(player.equals(game.getMyself())) {
            return "Me";
        }

        // If the player is the enemy, return "Enemy"
        if(player.equals(game.getEnemy())){
            return "Enemy";
        }

        // If the player is neutral, return "Neutral"
        return "Neutral";
    }


    /**
     * A function that calculates the value of capturing an ice-building.
     * @param game current game state
     * @param destination the ice-building that we are checking its value of capturing
     * @return The value of capturing the destination ice-building.
     */
    public static double getValueOfCapturing(Game game, IceBuilding destination) {

        // If the destination is a normal iceberg, call a separate function that calculates its value of capturing.
        if(destination instanceof Iceberg) {

            // Pass the iceberg representation of the ice-building to the function that calculates its value of capturing.
            return getValueOfCapturingIceberg(game, (Iceberg) destination);
        }

        // If the destination is a normal iceberg, call a separate function that calculates its value of capturing.
        else if (destination instanceof BonusIceberg) {

            // Pass the bonus-iceberg representation of the ice-building to the function that calculates its value of capturing.
            return getValueOfCapturingBonusIceberg(game, (BonusIceberg) destination);
        }

        // Unreachable, this only avoids a missing-return error
        return 0;
    }


    /**
     * A function to calculate the value of capturing an iceberg.
     * @param game current game state
     * @param destination the iceberg to capture
     * @return The value of capturing the iceberg.
     */
    public static double getValueOfCapturingIceberg(Game game, Iceberg destination) {
        // If we are capturing an iceberg, we are gaining its penguins-per-turn.
        double valueOfAttacking = destination.penguinsPerTurn;

        // If the iceberg is an enemy iceberg, not only are we gaining penguins-per-turn, we are also making the enemy lose penguins-per-turn.
        if(isEnemy(game, destination)) {
            // Take into account the enemy losing penguins-per-turn.
            valueOfAttacking += destination.penguinsPerTurn;
        }

        // TEMPORARY: we want to capture the closest iceberg if 2 have the same priority.
        // TODO make this better
        double averageDistanceToMyIcebergs = getAverageDistanceToMyIcebergs(game, destination);
        valueOfAttacking += 1 / averageDistanceToMyIcebergs;

        // Return the value of capturing the iceberg.
        //Log.log("B_6_0: value of capturing normal iceberg " + destination + "(" + playerToString(game, destination.owner) + ")" + " is " + valueOfAttacking);
        return valueOfAttacking;
    }


    /**
     * A function that calculated the value of capturing a bonus iceberg.
     * @param game current game state
     * @param destination the bonus-iceberg to capture
     * @return the value of capturing the bonus iceberg
     */
    public static double getValueOfCapturingBonusIceberg(Game game, BonusIceberg destination) {
        // The value that the bonus iceberg provides to each iceberg on each turn per average.
        double valuePerIceberg = BonusIcebergUtil.getAveragePenguinsPerTurnPerIceberg(game);

        // Store how many icebergs do I have, how many does the enemy have, and how many are neutral.
        int myIcebergAmount = game.getMyIcebergs().length;
        int enemyIcebergAmount = game.getEnemyIcebergs().length;
        int neutralIcebergAmount = game.getNeutralIcebergs().length;

        // Neutral icebergs are worth less than captured icebergs, this is because they will only impact in the future.
        // The value of this factor is not proven to be correct, but it is a good approximation. We might want to change this in the future.
        double neutralIcebergFactor = /*0.5*/0;

        // The value of attacking the bonus iceberg is the value per iceberg times the number of icebergs plus a factor for neutral icebergs.
        double valueOfAttacking = valuePerIceberg * (myIcebergAmount + enemyIcebergAmount + neutralIcebergAmount * neutralIcebergFactor);

        // Return the calculated value.
        //Log.log("B_6_1: value of capturing normal iceberg " + destination + "(" + playerToString(game, destination.owner) + ")" + " is " + valueOfAttacking);
        return valueOfAttacking;
    }


    /**
     * Get a priority queue of ice-buildings that are the best to capture.
     * @param game current game state
     * @return a priority queue of ice-buildings that are the best to capture
     */
    public static PriorityQueue<IceBuilding> getPriorityQueueOfIceBuildings(Game game) {

        // Create a priority queue of ice-buildings that are the best to capture, using a custom lambda comparator.
        PriorityQueue<IceBuilding> priorityQueue = new PriorityQueue<>((o1, o2) -> Double.compare(getValueOfCapturing(game, o2), getValueOfCapturing(game, o1)));

        // Add all the ice-buildings that are not mine to the priority queue.
        Set<IceBuilding> allEnemyOrNeutralIceBuildings = getEnemyOrNeutralIceBuildings(game);

        Log.log("All enemy or neutral ice-buildings: " + allEnemyOrNeutralIceBuildings);

        // Add all the members of the allEnemyOrNeutralIceBuildings to the priority queue.
        priorityQueue.addAll(allEnemyOrNeutralIceBuildings);

        // Return the calculated priority queue.
        return priorityQueue;
    }


    /**
     * A function to get all the ice-buildings that are not mine.
     * @param game current game state
     * @return a set of all the ice-buildings that are not mine
     */
    public static Set<IceBuilding> getEnemyOrNeutralIceBuildings(Game game) {

        // Initialize a set of ice-buildings that are not mine.
        Set<IceBuilding> allEnemyOrNeutralIceBuildings = new HashSet<IceBuilding>();

        // Use a stream to add to the set all the enemy or neutral ice-buildings.
        Arrays.stream(game.getAllIcebergs())
                // We only want to add the ice-buildings that are they enemy's or neutral.
                .filter(iceBuilding -> isEnemy(game, iceBuilding) || isNeutral(game, iceBuilding))
                // For each iceberg, add it's downcast to IceBuilding to the set.
                .forEach(iceBuilding -> allEnemyOrNeutralIceBuildings.add((IceBuilding) iceBuilding));

        // We want to also add the bonus iceberg if it is not mine. So firstly we check who's its owner
        String bonusIcebergOwner = playerToString(game, game.getBonusIceberg().owner);

        // If the bonus iceberg's owner is either enemy or neutral, add it to the set.
        if(bonusIcebergOwner.toLowerCase().equals("enemy") || bonusIcebergOwner.toLowerCase().equals("neutral")) {
            allEnemyOrNeutralIceBuildings.add(game.getBonusIceberg());
        }

        // Return the calculated set.
        Log.log("B_6_2: all enemy or neutral ice-buildings are " + allEnemyOrNeutralIceBuildings);
        return allEnemyOrNeutralIceBuildings;
    }


    /**
     * A function that checks whether an IceBuilding is mine.
     * @param game current game state
     * @param iceBuilding ice-building to check
     * @return true if the ice-building is mine, false otherwise
     */
    public static boolean isMine(Game game, IceBuilding iceBuilding) {
        return playerToString(game, iceBuilding.owner).toLowerCase().equals("me");
    }


    /**
     * A function that checks whether an IceBuilding is owned by the enemy.
     * @param game current game state
     * @param iceBuilding ice-building to check
     * @return true if the ice-building is owned by the enemy, false otherwise
     */
    public static boolean isEnemy(Game game, IceBuilding iceBuilding) {
        return playerToString(game, iceBuilding.owner).toLowerCase().equals("enemy");
    }


    /**
     * A function that checks whether an IceBuilding is neutral.
     * @param game current game state
     * @param iceBuilding ice-building to check
     * @return
     */
    public static boolean isNeutral(Game game, IceBuilding iceBuilding) {
        return playerToString(game, iceBuilding.owner).toLowerCase().equals("neutral");
    }





    public static double getAverageDistanceToMyIcebergs(Game game, IceBuilding destination) {
        Iceberg[] myIcebergs = game.getMyIcebergs();
        long sum = 0;
        for(int i = 0; i < myIcebergs.length; i++) {
            int distance = destination.getTurnsTillArrival(myIcebergs[i]);
            sum += distance;
        }
        return (double)sum / myIcebergs.length;
    }

    public static double getAverageDistanceToEnemyIcebergs(Game game, IceBuilding destination) {
        Iceberg[] enemyIcebergs = game.getEnemyIcebergs();
        long sum = 0;
        for(int i = 0; i < enemyIcebergs.length; i++) {
            int distance = destination.getTurnsTillArrival(enemyIcebergs[i]);
            sum += distance;
        }
        return (double)sum / enemyIcebergs.length;
    }


    public static List<IceBuilding> getMyIceBuildings(Game game) {
        List<IceBuilding> myIceBuildings = new ArrayList<>();
        for(int i = 0; i < game.getMyIcebergs().length; i++) {
            myIceBuildings.add(game.getMyIcebergs()[i]);
        }
        if(isMine(game, game.getBonusIceberg())) {
            myIceBuildings.add(game.getBonusIceberg());
        }
        return myIceBuildings;
    }


    public static List<PenguinGroup> getEnemyPenguinGroupsHeadedTowardIceBuilding(Game game, IceBuilding destination) {
        List<PenguinGroup> enemyPenguinsGroupsHeadedTowardIceBuilding = new ArrayList<>();

        for(PenguinGroup penguinGroup : game.getEnemyPenguinGroups()) {
            if (penguinGroup.destination.equals(destination)) {
                enemyPenguinsGroupsHeadedTowardIceBuilding.add(penguinGroup);
            }
        }

        return enemyPenguinsGroupsHeadedTowardIceBuilding;
    }


    public static List<PenguinGroup> getMyPenguinGroupsHeadedTowardIceBuilding(Game game, IceBuilding destination) {
        List<PenguinGroup> myPenguinGroupsHeadedTowardIceBuilding = new ArrayList<>();

        for(PenguinGroup penguinGroup : game.getMyPenguinGroups()) {
            if (penguinGroup.destination.equals(destination)) {
                myPenguinGroupsHeadedTowardIceBuilding.add(penguinGroup);
            }
        }

        return myPenguinGroupsHeadedTowardIceBuilding;
    }


    public static int getFarthestPenguinGroupHeadedTowardIceBuilding(Game game, IceBuilding destination) {
        int maxDistance = 0;

        for(PenguinGroup penguinGroup : game.getAllPenguinGroups()) {
            if (penguinGroup.destination.equals(destination)) {
                int distance = penguinGroup.turnsTillArrival;
                maxDistance = Math.max(maxDistance, distance);
            }
        }

        return maxDistance;
    }

    public static List<IceBuilding> getNeutralIceBuildings(Game game) {
        List<IceBuilding> neutralIceBuildings = new ArrayList<>();

        for(Iceberg neutralIceberg : game.getNeutralIcebergs()) {
            neutralIceBuildings.add(neutralIceberg);
        }

        if(isNeutral(game, game.getBonusIceberg())) {
            neutralIceBuildings.add(game.getBonusIceberg());
        }

        return neutralIceBuildings;
    }




    //TODO this method is not optimal
    public static boolean isGettingHelp(Game game, IceBuilding myIceBuilding) {
        if(getMyPenguinGroupsHeadedTowardIceBuilding(game, myIceBuilding).size() > 0) {
            return true;
        }
        return false;
    }



    public static List<Iceberg> getClosestIcebergs(Game game, IceBuilding destination) {
        int minDistance = Integer.MAX_VALUE;

        for(Iceberg icebergToCheck : game.getAllIcebergs()) {
            if(icebergToCheck.equals(destination)) {
                continue;
            }
            if(icebergToCheck.getTurnsTillArrival(destination) < minDistance) {
                minDistance = icebergToCheck.getTurnsTillArrival(destination);
            }
        }

        List<Iceberg> closestIcebergs = new ArrayList<>();
        for(Iceberg icebergToCheck : game.getAllIcebergs()) {
            if(icebergToCheck.getTurnsTillArrival(destination) == minDistance) {
                closestIcebergs.add(icebergToCheck);
            }
        }

        return closestIcebergs;
    }


    public static boolean closestIcebergsContainsEnemy(Game game, IceBuilding destination) {
        for(Iceberg icebergToCheck : getClosestIcebergs(game, destination)) {
            if(playerToString(game, icebergToCheck.owner).equals("Enemy")) {
                Log.log("\n\nclosestIcebergsContainsEnemy for iceBuilding " + destination + " is true\n\n");
                return true;
            }
        }
        Log.log("\n\nclosestIcebergsContainsEnemy for iceBuilding " + destination + " is false\n\n");
        return false;
    }




    public static int getMaxLengthBetweenIceBuildings(Game game) {
        if(maxLengthBetweenTwoIcebergs != -1) {
            return maxLengthBetweenTwoIcebergs;
        }

        int maxLength = 0;

        List<IceBuilding> allIceBuildings = getAllIceBuildings(game);

        for(int i = 0; i < allIceBuildings.size(); i++) {
            for(int j = i + 1; j < allIceBuildings.size(); j++) {
                int length = allIceBuildings.get(i).getTurnsTillArrival(allIceBuildings.get(j));
                maxLength = Math.max(maxLength, length);
            }
        }

        maxLengthBetweenTwoIcebergs = maxLength;
        return maxLength;
    }

    public static List<IceBuilding> getAllIceBuildings(Game game) {
        List<IceBuilding> allIceBuildings = new ArrayList<>();

        for(IceBuilding iceBuilding : game.getAllIcebergs()) {
            allIceBuildings.add(iceBuilding);
        }

        allIceBuildings.add(game.getBonusIceberg());

        return allIceBuildings;
    }


    public static int getMinTimeToCapture(Game game, IceBuilding target) {
        List<Iceberg> closestToFarthestOfMyIcebergs = Arrays.asList(game.getMyIcebergs());

        Collections.sort(closestToFarthestOfMyIcebergs, new Comparator<Iceberg>() {
            @Override
            public int compare(Iceberg o1, Iceberg o2) {
                return o1.getTurnsTillArrival(target) - o2.getTurnsTillArrival(target);
            }
        });

        Log.log("D_0_5: closestToFarthestOfMyIcebergs: " + closestToFarthestOfMyIcebergs);

        for(int i = 0; i < game.getMyIcebergs().length; i++) {
            if(canCaptureIfISendAllFromTheClosestXIcebergs(game, target, i)) {
                return closestToFarthestOfMyIcebergs.get(i).getTurnsTillArrival(target);
            }
        }

        return -1;
    }



    public static boolean canCaptureIfISendAllFromTheClosestXIcebergs(Game game, IceBuilding target, int amountOfIceBuildingsToSend) {
        List<Iceberg> closestToFarthestOfMyIcebergs = Arrays.asList(game.getMyIcebergs());

        Collections.sort(closestToFarthestOfMyIcebergs, new Comparator<Iceberg>() {
            @Override
            public int compare(Iceberg o1, Iceberg o2) {
                return o1.getTurnsTillArrival(target) - o2.getTurnsTillArrival(target);
            }
        });


        // The max of the turns till arrival of the X-th closest iceberg
        int totalTurnsTillArrival = closestToFarthestOfMyIcebergs.get(amountOfIceBuildingsToSend).getTurnsTillArrival(target);

        // How many penguins will be in the ice-building after every turn
        int[] penguinAmountsInTargetAfterTurns = new int[getMaxLengthBetweenIceBuildings(game) + 1];

        // Current penguin amount in the iceberg
        int originalAmount = target.penguinAmount;

        // Calculate the penguins per turn. If the iceberg is the enemies then it has a positive penguins-per-turn, otherwise it's 0.
        int penguinsPerTurn = 0;
        if(target instanceof Iceberg && isEnemy(game, target)) {
            penguinsPerTurn = ((Iceberg) target).penguinsPerTurn;
        }

        // For each turn in the array, calculate according to the penguins-per-second and the original penguin amount
        for(int i = 0; i < penguinAmountsInTargetAfterTurns.length; i++) {
            penguinAmountsInTargetAfterTurns[i] = originalAmount + i * penguinsPerTurn;
        }

        // Add enemy penguin groups that will arrive to help the target
        for(PenguinGroup enemyPenguinGroup : getEnemyPenguinGroupsHeadedTowardIceBuilding(game, target)) {
            int turnsTillArrival = enemyPenguinGroup.turnsTillArrival;

            for(int i = turnsTillArrival; i < penguinAmountsInTargetAfterTurns.length; i++) {
                penguinAmountsInTargetAfterTurns[i] += enemyPenguinGroup.penguinAmount;
            }
        }

        // Subtract the amount of my penguins that are already on the way
        for(PenguinGroup myPenguinGroup : getMyPenguinGroupsHeadedTowardIceBuilding(game, target)) {
            int turnsTillArrival = myPenguinGroup.turnsTillArrival;

            for(int i = turnsTillArrival; i < penguinAmountsInTargetAfterTurns.length; i++) {
                penguinAmountsInTargetAfterTurns[i] -= myPenguinGroup.penguinAmount;
            }
        }


        // Simulate what would happen if we were to send all penguins from the closest x icebergs
        int totalPenguinsThatWillArriveAfterSendingFromFirstX = 0;
        for(int i = 0; i < amountOfIceBuildingsToSend; i++) {
            Iceberg currentIceberg = closestToFarthestOfMyIcebergs.get(i);

            int currentTurnsTillArrival = currentIceberg.getTurnsTillArrival(target);

            // For every iceberg, it also generates additional penguins while waiting for the other penguin groups in the attack to arrive
            int turnsTillArrivalDelta = totalTurnsTillArrival - currentTurnsTillArrival;

            int currentPenguinAmount = currentIceberg.penguinAmount;
            int amountOfGeneratedPenguins = turnsTillArrivalDelta * currentPenguinAmount;

            // We increase the total amount of penguins that will arrive by the original amount that was in the iceberg + the amount that it will generate before sending those penguins
            totalPenguinsThatWillArriveAfterSendingFromFirstX += amountOfGeneratedPenguins + currentPenguinAmount;
        }

        // Loop over the array from the impact turn and forward, subtracting all of my penguins that will arrive in that turn.
        for(int i = totalTurnsTillArrival; i < penguinAmountsInTargetAfterTurns.length; i++) {
            penguinAmountsInTargetAfterTurns[i] -= totalPenguinsThatWillArriveAfterSendingFromFirstX;
        }

        for(int i = 0; i < penguinAmountsInTargetAfterTurns.length; i++) {
            Log.log("Turn " + i + ": " + penguinAmountsInTargetAfterTurns[i]);
        }


        for(int i = 0; i < penguinAmountsInTargetAfterTurns.length; i++) {
            if(penguinAmountsInTargetAfterTurns[i] < 0) {
                return true;
            }
        }
        return false;
    }



    public static double normalizeScore(double value, double worstValue, double bestValue) {

        boolean reverse = false;
        if(worstValue > bestValue) {
            double temp = worstValue;
            worstValue = bestValue;
            bestValue = temp;

            reverse = true;
        }

        if (value <= worstValue) {
            return 0;
        }
        if (value >= bestValue) {
            return 1;
        }

        double score = (value - worstValue) / (bestValue - worstValue);
        if(reverse) {
            score = 1 - score;
        }

        return score;
    }


    public static double computeFactoredScore(double ... values) {
        double score = 0;
        for(int i = 0; i < values.length; i += 2) {
            score += values[i] * values[i + 1];
        }
        return score;
    }


    public static void updateTurnState(Game game) {
        prediction = new Prediction(game, new ArrayList<>());
    }




    public static AttackPlan planAttack(Game game, Prediction prediction, IceBuilding target, Set<Iceberg> cannotSendNow) {
        List<Iceberg> closestToFarthestIcebergs = Arrays.asList(game.getAllIcebergs());

        Collections.sort(closestToFarthestIcebergs, new Comparator<Iceberg>() {
            @Override
            public int compare(Iceberg o1, Iceberg o2) {
                return o1.getTurnsTillArrival(target) - o2.getTurnsTillArrival(target);
            }
        });


        boolean areWeClosest = areWeClosestToIceberg(game, target);

        for(int i = 0; i < closestToFarthestIcebergs.size(); i++) {
            /*log("farthest iceberg is: " + closestToFarthestIcebergs.get(i));*/
            Iceberg farthest = closestToFarthestIcebergs.get(i);

            // Because the farthest always needs to send now, we need to make sure that it isn't in the send-now-ban list.
            if(cannotSendNow.contains(farthest)) {
                continue;
            }

            // If the farthest iceberg (in the i-th index) , continue as it cannot send.
            if(!isMine(game, farthest)) {
                continue;
            }


            int turnsTillArrival = farthest.getTurnsTillArrival(target);
            /*log("Turns till arrival: " + turnsTillArrival);*/

            IceBuildingState targetStateAtArrival = prediction.iceBuildingStateAtWhatTurn.get(target).get(turnsTillArrival);

            // If the iceberg will be mine, we don't need to attack it
            if(targetStateAtArrival.owner == ME) {
                continue;
            }


            int penguinSum = 0;
            AttackPlan attackPlan = new AttackPlan(target);

            // Add the help penguins that the enemy can send - we want to only perform attacks that the enemy can't defend
            int amountOfPenguinsEnemyCanSendHelp = getAmountOfPenguinsEnemyCanSendHelpInXTurns(game, prediction, target, turnsTillArrival);
            int penguinAmountThatWillBeInTarget = targetStateAtArrival.penguinAmount + amountOfPenguinsEnemyCanSendHelp;

            /*if(target.equals(game.getBonusIceberg())) {*/ // TODO change this so instead of just bonus iceberg, we will do this for every iceberg that we are closest to
            if(areWeClosest) {
                penguinAmountThatWillBeInTarget -= amountOfPenguinsEnemyCanSendHelp;
            }

            boolean hasEnoughToCapture = false;
            // Add all penguins from the closest x of my icebergs
            for(int j = 0; j <= i && !hasEnoughToCapture; j++) {
                Iceberg currentIceberg = closestToFarthestIcebergs.get(j);

                List<IceBuildingState> stateByTurn = prediction.iceBuildingStateAtWhatTurn.get(currentIceberg);

                // Turns till arrival from the farthest of my icebergs
                int turnsTillArrivalDelta = turnsTillArrival - currentIceberg.getTurnsTillArrival(target);

                IceBuildingState state = stateByTurn.get(turnsTillArrivalDelta);

                // If the iceberg can send penguins, send all and check for excess
                if(state.owner == ME && state.penguinAmount > 0) {

                    // Set the amount to send variable to the max amount of penguins that the iceberg can send
                    /*int amountToSend = state.penguinAmount;*/
                    int amountToSend = prediction.getMaxThatCanSpend(currentIceberg, turnsTillArrivalDelta);

                    // If the current iceberg can't send anything, just continue to the next one
                    if(amountToSend <= 0) {
                        continue;
                    }

                    penguinSum += amountToSend;


                    if(penguinSum > penguinAmountThatWillBeInTarget) {
                        int prevAmountToSend = amountToSend;

                        int excess = penguinSum - penguinAmountThatWillBeInTarget;
                        amountToSend = amountToSend - excess + 1; // +1 because we want to capture and not only make neutral


                        penguinSum -= (prevAmountToSend - amountToSend);

                        hasEnoughToCapture = true;
                    }

                    attackPlan.addAction(currentIceberg, amountToSend, turnsTillArrivalDelta);

                }
            }


            if(penguinSum > penguinAmountThatWillBeInTarget) {

                // If the plan does not need to execute now, ignore it, it will be done in a later turn
                boolean hasActionToBePerformedNow = false;
                for(AttackPlan.AttackPlanAction action : attackPlan.actions) {
                    if(action.turnsToSend == 0) {
                        hasActionToBePerformedNow = true;
                        break;
                    }
                }
                if(!hasActionToBePerformedNow) {
                    return null;
                }

                Log.log("Attack " + IcebergUtil.toString(target) + " with " + penguinSum + " penguins");

                return attackPlan;
            }

        }

        // Cannot attack the target
        return null;
    }




    public static Collection<Iceberg> getIcebergsSortedByDistance(Game game, IceBuilding target) {
        List<Iceberg> icebergs = new ArrayList<>(Arrays.asList(game.getAllIcebergs())).stream()
                .filter(iceberg -> iceberg != target)
                .sorted(Comparator.comparingInt(iceberg -> iceberg.getTurnsTillArrival(target)))
                .collect(Collectors.toList());

        return icebergs;
    }


    private static void log(Object toPrint) {
        Log.log(toPrint);
    }

    public static Iceberg getClosestIcebergThatIsNotMaxLevelAndIsMoreVulnerable(Game game, Iceberg myIceberg) {
        Iceberg closestIceberg = null;
        int minDistance = Integer.MAX_VALUE;

        for(Iceberg needsHelp : game.getMyIcebergs()) {
            if(needsHelp == myIceberg) continue;
            if(needsHelp.level == needsHelp.upgradeLevelLimit) continue;

            int distance = needsHelp.getTurnsTillArrival(myIceberg);

            boolean needsHelpIsMoreVulnerable = getAverageDistanceToEnemyIcebergs(game, needsHelp) <= getAverageDistanceToEnemyIcebergs(game, myIceberg);

            if(distance < minDistance && needsHelpIsMoreVulnerable) {
                minDistance = distance;
                closestIceberg = needsHelp;
            }
        }

        return closestIceberg;
    }


    public static Iceberg getClosestIcebergThatIsNotMaxLevel(Game game, Iceberg myIceberg) {
        Iceberg closestIceberg = null;
        int minDistance = Integer.MAX_VALUE;

        for(Iceberg needsHelp : game.getMyIcebergs()) {
            if(needsHelp == myIceberg) continue;
            if(needsHelp.level == needsHelp.upgradeLevelLimit) continue;

            int distance = needsHelp.getTurnsTillArrival(myIceberg);

            if(distance < minDistance) {
                minDistance = distance;
                closestIceberg = needsHelp;
            }
        }

        return closestIceberg;
    }


    public static Iceberg getClosestIcebergThatCouldUseHelp(Game game, Iceberg myIceberg, Prediction prediction) {

        List<Iceberg> closestToFarthestOfMyIcebergs = Arrays.asList(game.getMyIcebergs());

        Collections.sort(closestToFarthestOfMyIcebergs, new Comparator<Iceberg>() {
            @Override
            public int compare(Iceberg o1, Iceberg o2) {
                return o1.getTurnsTillArrival(myIceberg) - o2.getTurnsTillArrival(myIceberg);
            }
        });


        for(int i = 1; i < closestToFarthestOfMyIcebergs.size(); i++) { // Start at 1 to skip the iceberg itself
            Iceberg icebergToCheck = closestToFarthestOfMyIcebergs.get(i);

            /*log("F_0_2: iceberg " + IcebergUtil.toString(myIceberg) + " is now checking iceberg " + IcebergUtil.toString(icebergToCheck));*/

            if(icebergToCheck.level == icebergToCheck.upgradeLevelLimit) {
                continue;
            }

            boolean couldUseMyHelp = false;

            int amountNeededToUpgradeToMaxLevel = getCostNeededTillMaxLevel(icebergToCheck);

            List<IceBuildingState> states = prediction.iceBuildingStateAtWhatTurn.get(icebergToCheck);

            // If we upgraded the iceberg to max level exactly this turn, it's already max and doesn't need help.
            // This check will work over 90% of the time, but there is a small chance that it will fail.
            if(states.size() >= 2 && states.get(1).penguinAmount - states.get(0).penguinAmount == icebergToCheck.penguinsPerTurn + icebergToCheck.upgradeValue) {
                continue;
            }

            int turnsTillArrival = icebergToCheck.getTurnsTillArrival(myIceberg);
            for(int j = turnsTillArrival; j < states.size(); j++) {
                IceBuildingState state = states.get(j);
                if(state.owner != ME || state.penguinAmount <= amountNeededToUpgradeToMaxLevel) {
                    /*log("F_0_3: iceberg " + IcebergUtil.toString(icebergToCheck) + " does not need my help because states[" + j + "] = " + state);*/
                    couldUseMyHelp = true;
                    break;
                }
            }

            if(couldUseMyHelp) {
                return icebergToCheck;
            }
        }

        return null; // Found no good icebergs
    }


    // This is after doing math
    /*
    u = upgradeCost
    f = costFactor
    d = level distance from max level (maxLevel - level)

    total cost = u + (u+f) + (u+2f) + (u+3f) + ... + (u+(d-1)*f)
    = d*u + f + 2f + 3f + ... + (d-1)*f
    = d*u + f*(1+2+3+...+(d-1))
    = d*u + f*(d*(d-1)/2) =

    +----------------------+
    |  d * (u + f*(d-1)/2) |
    +----------------------+

     */
    public static int getCostNeededTillMaxLevel(Iceberg iceberg) {
        int u = iceberg.upgradeCost;
        int d = iceberg.upgradeLevelLimit - iceberg.level;
        int f = iceberg.costFactor;

        int result = (int) (d * (u + f * (d - 1) / 2.0));
        /*log("F_0_1: cost needed till max level for " + IcebergUtil.toString(iceberg) + " is " + result);*/
        return result;
    }


    public static Iceberg closestIcebergToEnemy(Game game) {
        Iceberg minAverageDistanceIceberg = null;
        int minAverageDistance = Integer.MAX_VALUE;

        for(Iceberg myIceberg : game.getMyIcebergs()) {
            double averageDistance = getAverageDistanceToEnemyIcebergs(game, myIceberg);

            if(averageDistance < minAverageDistance) {
                minAverageDistance = (int)averageDistance;
                minAverageDistanceIceberg = myIceberg;
            }
        }

        return minAverageDistanceIceberg;
    }

    public static double getAverageDistanceToEnemyIcebergs(Game game, Iceberg myIceberg) {
        int distancesSum = 0;

        for(Iceberg enemyIceberg : game.getEnemyIcebergs()) {
            if(myIceberg == null) log("--------------------------");
            distancesSum += myIceberg.getTurnsTillArrival(enemyIceberg);
        }

        int enemyIcebergAmount = game.getEnemyIcebergs().length;

        double result = (double)distancesSum / enemyIcebergAmount;
        return result;
    }


    public static int getAmountOfPenguinsEnemyCanSendHelpInXTurns(Game game, Prediction prediction, IceBuilding target, int turns) {
        if(prediction.iceBuildingStateAtWhatTurn.get(target).get(turns).owner != ENEMY) return 0;

        int amountOfPenguins = 0;
        for(Iceberg enemyIceberg : game.getEnemyIcebergs()) {
            if (enemyIceberg == target) continue;

            int distance = enemyIceberg.getTurnsTillArrival(target);
            if(distance <= turns) {
                amountOfPenguins += enemyIceberg.penguinAmount;
                amountOfPenguins += enemyIceberg.penguinsPerTurn * distance;
            }
        }
        return amountOfPenguins;
    }


    public static int getTotalEnemyPenguinsOnMap(Game game) {
        int total = 0;
        for(Iceberg enemyIceberg : game.getEnemyIcebergs()) {
            total += enemyIceberg.penguinAmount;
        }
        for(PenguinGroup enemyPenguinGroup : game.getEnemyPenguinGroups()) {
            total += enemyPenguinGroup.penguinAmount;
        }
        return total;
    }


    public static int getTotalMyPenguinsOnMap(Game game) {
        int total = 0;
        for(Iceberg myIceberg : game.getMyIcebergs()) {
            total += myIceberg.penguinAmount;
        }
        for(PenguinGroup myPenguinGroup : game.getMyPenguinGroups()) {
            total += myPenguinGroup.penguinAmount;
        }
        return total;
    }


    public static int getMyPenguinCreationRate(Game game) {
        int total = 0;
        for(Iceberg myIceberg : game.getMyIcebergs()) {
            total += myIceberg.penguinsPerTurn;
        }
        return total;
    }


    public static int getEnemyPenguinCreationRate(Game game) {
        int total = 0;
        for(Iceberg enemyIceberg : game.getEnemyIcebergs()) {
            total += enemyIceberg.penguinsPerTurn;
        }
        return total;
    }


    public static List<Iceberg> getIcebergsThatAreGettingSentPenguinsAtFromMyIceberg(Game game, Iceberg myIceberg) {
        List<Iceberg> icebergsThatAreGettingAttacked = new ArrayList<>();

        for(PenguinGroup myPenguinGroup : game.getMyPenguinGroups()) {
            if(myPenguinGroup.source == myIceberg) {
                IceBuilding destination = myPenguinGroup.destination;

                // If the destination is a bonus iceberg, we can't build a bridge to it because of the game API
                if(destination instanceof Iceberg) {
                    icebergsThatAreGettingAttacked.add((Iceberg) destination);
                }
            }
        }

        return icebergsThatAreGettingAttacked;
    }


    public static List<Bridge> getAllBridges(Game game) {
        Set<Bridge> bridges = new HashSet<>();
        for(Iceberg myIceberg : game.getMyIcebergs()) {
            for(Bridge bridge : myIceberg.bridges) {
                bridges.add(bridge);
            }
        }
        for(Iceberg enemyIceberg : game.getEnemyIcebergs()) {
            for(Bridge bridge : enemyIceberg.bridges) {
                bridges.add(bridge);
            }
        }

        return new ArrayList<>(bridges);
    }


    public static boolean areWeClosestToIceberg(Game game, IceBuilding iceBuilding) {
        int minDistance = Integer.MAX_VALUE;

        for(Iceberg iceberg : game.getMyIcebergs()) {
            int distance = iceberg.getTurnsTillArrival(iceBuilding);
            if(distance > 0 && distance < minDistance) {
                minDistance = distance;
            }
        }

        for(Iceberg iceberg : game.getEnemyIcebergs()) {
            int distance = iceberg.getTurnsTillArrival(iceBuilding);
            if(distance > 0 && distance < minDistance) {
                minDistance = distance;
            }
        }

        for(Iceberg iceberg : game.getMyIcebergs()) {
            int distance = iceberg.getTurnsTillArrival(iceBuilding);
            if (distance == minDistance) {
                return true;
            }
        }
        return false;
    }

}