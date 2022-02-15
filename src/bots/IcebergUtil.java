package bots;

import penguin_game.*;
import java.util.*;

public class IcebergUtil {

    /**
     * A function that returns a list containing all the enemy penguin groups that are headed towards a given iceberg.
     * @param game The current game state.
     * @param target The target iceberg, the one who's getting penguins sent at.
     * @return A list of enemy penguin groups that are headed towards the target iceberg.
     */
    public static List<PenguinGroup> getEnemyPenguinGroupsHeadedTowardIceBuilding(Game game, IceBuilding target) {
        // Initialize the list of enemy penguin groups headed towards the target iceberg.
        List<PenguinGroup> groupsHeadedTowardIceBuilding = new ArrayList<PenguinGroup>();

        // Loop through all the enemy penguin groups and check if their destination is the given IceBuilding.
        for (PenguinGroup enemyPenguinGroup : game.getEnemyPenguinGroups()) {

            // If the enemy penguin group's destination is the given IceBuilding, add it to the list.
            if (enemyPenguinGroup.destination.equals(target)) {
                groupsHeadedTowardIceBuilding.add(enemyPenguinGroup);
            }
        }

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
        // Initialize the list of my penguin groups headed towards the target iceberg.
        List<PenguinGroup> groupsHeadedTowardIceBuilding = new ArrayList<PenguinGroup>();

        // Loop through all my penguin groups and check if their destination is the given IceBuilding.
        for (PenguinGroup myPenguinGroup : game.getMyPenguinGroups()) {

            // If my penguin group's destination is the given IceBuilding, add it to the list.
            if (myPenguinGroup.destination.equals(target)) {
                groupsHeadedTowardIceBuilding.add(myPenguinGroup);
            }
        }

        // Return the list of my penguin groups headed towards the target iceberg.
        return groupsHeadedTowardIceBuilding;
    }




}
