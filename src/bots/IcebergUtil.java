package bots;

import penguin_game.*;
import java.util.*;
import java.util.stream.Collectors;

public class IcebergUtil {

    /**
     * A function that returns a list containing all the enemy penguin groups that are headed towards a given iceberg.
     * @param game The current game state.
     * @param target The target iceberg, the one who's getting penguins sent at.
     * @return A list of enemy penguin groups that are headed towards the target iceberg.
     */
    public static List<PenguinGroup> getEnemyPenguinGroupsHeadedTowardIceBuilding(Game game, IceBuilding target) {

        // Convert the array of enemy penguin groups to a list.
        List<PenguinGroup> allEnemyPenguinGroups = Arrays.asList(game.getEnemyPenguinGroups());

        // Filter from the list of enemy penguin groups all the enemy penguin groups that are headed towards the target iceberg.
        List<PenguinGroup> groupsHeadedTowardIceBuilding = allEnemyPenguinGroups
                .stream()
                .filter(enemyPenguinGroup -> enemyPenguinGroup.destination.equals(target))
                .collect(Collectors.toList());

        // Return the list of enemy penguin groups headed towards the target iceberg.
        return groupsHeadedTowardIceBuilding;
    }


    /**
     * A function that returns a list containing all my penguin groups that are headed towards a given iceberg.
     * @param game The current game state.
     * @param target The target iceberg, the one who's getting penguins sent at.
     * @return A list of my penguin groups that are headed towards the target iceberg.
     */
    public static List<PenguinGroup> getMyPenguinGroupsHeadedTowardIceBuilding(Game game, IceBuilding target) {

        // Convert the array of my penguin groups to a list.
        List<PenguinGroup> allMyPenguinGroups = Arrays.asList(game.getMyPenguinGroups());

        // Filter from the list of my penguin groups all my penguin groups that are headed towards the target iceberg.
        List<PenguinGroup> groupsHeadedTowardIceBuilding = allMyPenguinGroups
                .stream()
                .filter(myPenguinGroup -> myPenguinGroup.destination.equals(target))
                .collect(Collectors.toList());

        // Return the list of my penguin groups headed towards the target iceberg.
        return groupsHeadedTowardIceBuilding;
    }


    /**
     * A function that returns a map that contains how many enemy penguins will arrive at what turn to a target iceberg.
     * @param game The current game state.
     * @param destination The destination iceberg, the one who's getting penguins sent at.
     * @return A map that contains how many enemy penguins will arrive at what turn to the target iceberg.
     */
    public static Map<Integer, Integer> howManyEnemyPenguinsWillArriveToADestinationInXTurns(Game game, IceBuilding destination) {

        // Initialize the map that will contain the number of enemy penguins that will arrive to a destination in a certain number of turns.
        Map<Integer, Integer> howManyEnemyPenguinsWillArriveInXTurns = new TreeMap<>();

        // Loop over all the enemy penguin groups that will arrive to the destination in a certain number of turns.
        // For each enemy penguin group that will arrive to the destination in a certain number of turns,
        // add the number of penguins that will arrive to the destination in that number of turns to the map.
        for(PenguinGroup enemyPenguinGroup : getEnemyPenguinGroupsHeadedTowardIceBuilding(game, destination)) {
            // Store the number of turns that the enemy penguin group will take to reach the destination.
            int numberOfTurns = enemyPenguinGroup.turnsTillArrival;

            // Store the number of penguins that will arrive to the destination from the specific enemy penguin group.
            int numberOfPenguinsInTheGroup = enemyPenguinGroup.penguinAmount;

            // Find the number of penguins that will arrive to the destination in the number of turns.
            // If there are no other penguin groups arriving in the same turn, there is 0 penguins, so we default to 0.
            int previousNumberOfPenguins = howManyEnemyPenguinsWillArriveInXTurns.getOrDefault(numberOfTurns, 0);

            // Add the number of penguins that will arrive from the specific enemy penguin group
            // to the other penguin groups who will arrive in the same turn
            int newArrivingPenguinAmount = previousNumberOfPenguins + numberOfPenguinsInTheGroup;

            // Add the number of penguins that will arrive to the destination in the number of turns to the map.
            howManyEnemyPenguinsWillArriveInXTurns.put(numberOfTurns, newArrivingPenguinAmount);
        }

        // Return the how-many-penguins-will-arrive-at-what-turn map.
        return howManyEnemyPenguinsWillArriveInXTurns;
    }


    /**
     * A function that returns a map that contains how many my penguins will arrive at what turn to a target iceberg.
     * @param game The current game state.
     * @param destination The destination iceberg, the one who's getting penguins sent at.
     * @return A map that contains how many my penguins will arrive at what turn to the target iceberg.
     */
    public static Map<Integer, Integer> howManyMyPenguinsWillArriveToADestinationInXTurns(Game game, IceBuilding destination) {

        // Initialize the map that will contain the number of my penguins that will arrive to a destination in a certain number of turns.
        Map<Integer, Integer> howManyMyPenguinsWillArriveInXTurns = new TreeMap<>();

        // Loop over all my penguin groups that will arrive to the destination in a certain number of turns.
        // For each my penguin group that will arrive to the destination in a certain number of turns,
        // add the number of penguins that will arrive to the destination in that number of turns to the map.
        for(PenguinGroup myPenguinGroup : getMyPenguinGroupsHeadedTowardIceBuilding(game, destination)) {
            // Store the number of turns that my penguin group will take to reach the destination.
            int numberOfTurns = myPenguinGroup.turnsTillArrival;

            // Store the number of penguins that will arrive to the destination from the specific my penguin group.
            int numberOfPenguinsInTheGroup = myPenguinGroup.penguinAmount;

            // Find the number of penguins that will arrive to the destination in the number of turns.
            // If there are no other penguin groups arriving in the same turn, there is 0 penguins, so we default to 0.
            int previousNumberOfPenguins = howManyMyPenguinsWillArriveInXTurns.getOrDefault(numberOfTurns, 0);

            // Add the number of penguins that will arrive from the specific my penguin group
            // to the other penguin groups who will arrive in the same turn
            int newArrivingPenguinAmount = previousNumberOfPenguins + numberOfPenguinsInTheGroup;

            // Add the number of penguins that will arrive to the destination in the number of turns to the map.
            howManyMyPenguinsWillArriveInXTurns.put(numberOfTurns, newArrivingPenguinAmount);
        }

        // Return the how-many-penguins-will-arrive-at-what-turn map.
        return howManyMyPenguinsWillArriveInXTurns;
    }


    /**
     * A function that returns a map that contains how many enemy penguins will be added at what turn to a target iceberg from the bonus iceberg.
     * @param game The current game state.
     * @param target The target iceberg, the one that we are checking.
     * @return A map that contains how many bonus penguins will be added at what turn to the target iceberg from the bonus iceberg.
     */
    public static Map<Integer, Integer> howManyPenguinsWillAnIcebergGetAtEachTurnFromBonusIceberg(Game game, IceBuilding target) {

        // Initialize the map that will contain the number of penguins that will be added to a target iceberg in a certain number of turns.
        Map<Integer, Integer> howManyPenguinsWillBeAddedInXTurns = new TreeMap<>();

        // Store the bonusIceberg, and the amount of penguins that it gives each time it produces penguins.
        BonusIceberg bonusIceberg = game.getBonusIceberg();
        int penguinAmountThatWillGetAddedEachTime = bonusIceberg.penguinBonus;

        // If the target iceberg is neutral, or it doesn't have the same owner as the bonus iceberg, it will never revieve any penguins.
        // So we return an empty map.
        if(target.owner.equals(game.getNeutral()) || !target.owner.equals(bonusIceberg.owner)) {
            return howManyPenguinsWillBeAddedInXTurns;
        }

        // Loop over all turns that the bonus iceberg will produce penguins, and add them to the map.
        for(int turn = bonusIceberg.turnsLeftToBonus; turn <= game.maxTurns; turn += bonusIceberg.maxTurnsToBonus) {

            // Add the penguins that will be added to the target iceberg in the current turn to the map.
            howManyPenguinsWillBeAddedInXTurns.put(turn, penguinAmountThatWillGetAddedEachTime);
        }

        // Return the how-many-penguins-will-be-added-at-what-turn map.
        return howManyPenguinsWillBeAddedInXTurns;
    }



    public static String toString(IceBuilding iceBuilding) {
        String result = "[[" + iceBuilding.id + "/";
        switch (iceBuilding.owner.id) {
            case 0:
                result += "ME";
                break;
            case 1:
                result += "ENEMY";
                break;
            case -1:
                result += "NEUTRAL";
                break;
        }
        return result + "]]";
    }
}
