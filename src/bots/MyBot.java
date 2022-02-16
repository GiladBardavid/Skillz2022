package bots;
import penguin_game.*;
import java.util.*;

/**
 * "The" Skillz 2022 Code
 *
 * @author Gilad "carry" B.
 * @author Ilay "cyber" B.
 * @author Sagi "8200" G.
 */
public class MyBot implements SkillzBot {

    // A set of all ongoing attacks.
    public Set<Attack> ongoingAttacks = new HashSet<>();

    // A map that for each iceberg, we will store how many penguins do we want it to have in turn x. This is so we will be able to correctly perform all attacks.
    public Map<Iceberg, Map<Integer, Integer>> howManyPenguinsShouldIcebergsHaveInTurn = new HashMap<>();

    /**
     * Does the turn. This function is called by the system.
     * @param game current game state
     */
    public void doTurn(Game game) {

        log("maxTurnsToBonus: " + game.getBonusIceberg().maxTurnsToBonus);
        log("turnsLeftToBonus: " + game.getBonusIceberg().turnsLeftToBonus);
        log("penguinBonus: " + game.getBonusIceberg().penguinBonus);

        log("my bonus iceberg: " + game.getMyBonusIceberg());
        log("enemy bonus iceberg: " + game.getEnemyBonusIceberg());
        log("neutral bonus iceberg: " + game.getNeutralBonusIceberg());

        log("me: " + GameUtil.playerToString(game, game.getMyself()) + " enemy: " + GameUtil.playerToString(game, game.getEnemy()) + " neutral: " + GameUtil.playerToString(game, game.getNeutral()));
        log("");


        // Update the turnsTillArrival of all ongoing attacks
        for(Attack ongoingAttack : ongoingAttacks) {
            ongoingAttack.decrementTurnsTillArrival();
        }



        // --------------------------------------------------------------



        for(Iceberg myIceberg : game.getMyIcebergs()) {
            /*IceBuilding destination;*/
            Iceberg destination = game.getEnemyIcebergs()[0];
            int distanceBetweenMyIcebergAndDestination = myIceberg.getTurnsTillArrival(destination);
            log("distanceBetweenMyIcebergAndDestination: " + distanceBetweenMyIcebergAndDestination);
            int howManyPenguinsToSend = GameUtil.getPenguinAmountInTurnXForEnemyOrNeutralIceBuilding(game, destination, distanceBetweenMyIcebergAndDestination) + 1;
            log("howManyPenguinsToSend: " + howManyPenguinsToSend);
            log("my penguin amount: " + myIceberg.penguinAmount);
            if(howManyPenguinsToSend > 0 && myIceberg.penguinAmount >= howManyPenguinsToSend) {
                log("Creating new attack");
                Attack a = new Attack(destination);
                a.addIcebergThatCanAttack(myIceberg, howManyPenguinsToSend);
                ongoingAttacks.add(a);
            }
        }

        // Execute all the attacks that need to be done
        for (Attack ongoingAttack : ongoingAttacks) {
            log("Parsing attack\n" + ongoingAttack);
            ongoingAttack.execute();
        }

        log("Ongoing attacks:\n" + ongoingAttacks + "\n");
    }




    /**
     * A function to print strings if we are in debug mode
     * @param toPrint The string to print
     */
    public void log(Object toPrint) {
        Log.log(toPrint);
    }
}