package bots;
import penguin_game.*;
import java.util.*;

/**
 * "The" Skillz 2022 Code
 *
 *
 */
public class MyBot implements SkillzBot {

    /**
     * Does the turn. This function is called by the system.
     * @param game current game state
     */
    public void doTurn(Game game) {


        // Update static states
        GameUtil.updateTurnState(game);

        for(IceBuilding iceBuilding : GameUtil.getAllIceBuildings(game)) {
            log(Arrays.toString(GameUtil.howManyEnemyPenguinsWillArriveAtWhatTurn.get(iceBuilding)));
        }

        for(IceBuilding iceBuilding : GameUtil.getAllIceBuildings(game)) {
            log("iceBuildingStateAtWhatTurn: iceberg = " + iceBuilding + " value = " + GameUtil.iceBuildingStateAtWhatTurn.get(iceBuilding));
        }

        List<Action> candidateActions = createAllActions(game);

        // Sort actions
        for(Action action : candidateActions) {
            action.computeScore(game);
        }

        Collections.sort(candidateActions, new Comparator<Action>() {
            @Override
            public int compare(Action o1, Action o2) {
                return Double.compare(o2.score, o1.score);
            }
        });


        for (Action action : candidateActions) {
            log(action.toString());
        }


        // Execute actions
        for(Action action : candidateActions) {
            action.executeIfPossible(game);
        }
    }



    /**
     * Create all possible actions
     * @param game current game state
     * @return list of all possible actions
     */
    public List<Action> createAllActions(Game game) {
        List<Action> actions = new ArrayList<>();

        // Add attack actions
        for(IceBuilding iceBuildingToAdd : GameUtil.getEnemyOrNeutralIceBuildings(game)) {
            actions.add(new AttackAction(iceBuildingToAdd));
        }

        return actions;
    }




    /**
     * A function to print strings if we are in debug mode
     * @param toPrint The string to print
     */
    public void log(Object toPrint) {
        Log.log(toPrint);
    }
}