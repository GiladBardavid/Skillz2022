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

        log("maxTurnsToBonus: " + game.getBonusIceberg().maxTurnsToBonus);
        log("turnsLeftToBonus: " + game.getBonusIceberg().turnsLeftToBonus);
        log("penguinBonus: " + game.getBonusIceberg().penguinBonus);



        // Update static states
        GameUtil.updateTurnState(game);





        List<Action> candidateActions = createAllActions(game);

        if(candidateActions.size() == 0) {
            return;
        }

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
            log("Score: " + action.score);
        }


        Action bestAction = candidateActions.get(0);

        bestAction.executeIfPossible(game);
        // TODO execute more than one action
    }



    /**
     * Create all possible actions
     * @param game current game state
     * @return list of all possible actions
     */
    public List<Action> createAllActions(Game game) {
        List<Action> actions = new ArrayList<>();

        // Create attack actions
        for(IceBuilding iceBuilding : GameUtil.getAllIceBuildings(game)) {
            AttackPlan plan = GameUtil.planAttack(game, GameUtil.prediction, iceBuilding);
            if(plan != null) {
                log("Prediction = " + GameUtil.prediction.iceBuildingStateAtWhatTurn.get(iceBuilding));
                log(plan.toString());

                actions.add(new AttackAction(plan));
            }
        }

        // TODO add defend action, upgrade actions, bridge action.

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