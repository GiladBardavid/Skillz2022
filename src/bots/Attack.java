package bots;
import penguin_game.*;
import java.util.*;

/**
 * A class to represent an attack on an enemy / neutral ice building.
 * An attack is meant to coordinate attacks between my icebergs.
 * We have an IceBuilding destination, and a map of every single one of our icebergs that we wish would help in the attack and how much to contribute.
 */
public class Attack {
    /*
    Attributes :

    IceBuilding destination - The attack destination (the IceBuilding that we are attacking)

    int turnsTillArrival - turnsTillArrival - The turns till the attack arrives. Since all the attack's penguin groups are synced,
     this will be a constant that we will decrement every turn.

    Map<Integer, Map<Iceberg, Integer>> turnsTillArrivalToIcebergsThatNeedToAttack - This map represents which icebergs need to send what amount at every turn
    (at every distance from the destination).

    int penguinsNeeded - The amount of penguins that we need to successfully capture the destination.

    int currentPenguinAmount - The current total amount of penguins that we have that will reach the destination.
    */

    public IceBuilding destination;

    public int turnsTillArrival;

    public Map<Integer, Map<Iceberg, Integer>> turnsTillArrivalToIcebergsThatNeedToAttack;

    public int penguinsNeeded;

    public int currentPenguinAmount;




    /**
     * An attack constructor that only takes a destination IceBuilding.
     * @param destination The new attack destination
     */
    public Attack(IceBuilding destination) {
        this.destination = destination;
        turnsTillArrival = 0;
        turnsTillArrivalToIcebergsThatNeedToAttack = new HashMap<>();

        penguinsNeeded = destination.penguinAmount;
        currentPenguinAmount = 0;
    }



    /**
     * A function to add an iceberg to the attack.
     * @param icebergThatWillAttack The iceberg that will attack the destination
     * @param amountCanSend The amount of penguins that the iceberg can send
     */
    public void addIcebergThatCanAttack(Iceberg icebergThatWillAttack, int amountCanSend) {
        // Calculate the distance from the destination
        int distanceFromDestination = icebergThatWillAttack.getTurnsTillArrival(destination);

        // Update turnsTillArrival if needed
        turnsTillArrival = Math.max(turnsTillArrival, distanceFromDestination);

        // If there is already another iceberg that can attack the destination at the same distance, we only need to add the iceberg to the map.
        // Else, we need to create a new entry in the map.
        if (turnsTillArrivalToIcebergsThatNeedToAttack.containsKey(distanceFromDestination)) {
            // Add the iceberg to the map
            turnsTillArrivalToIcebergsThatNeedToAttack.get(distanceFromDestination).put(icebergThatWillAttack, amountCanSend);
        }
        else {
            // Create a new entry in the map and add the iceberg to it
            Map<Iceberg, Integer> icebergsThatCanAttack = new HashMap<>();
            icebergsThatCanAttack.put(icebergThatWillAttack, amountCanSend);
            turnsTillArrivalToIcebergsThatNeedToAttack.put(distanceFromDestination, icebergsThatCanAttack);
        }
    }



    /**
     * A function to get a list of all icebergs contributing to the attack.
     * @return A list of all icebergs contributing to the attack
     */
    public List<Iceberg> getIcebergsParticipatingInTheAttack() {
        List<Iceberg> icebergsParticipatingInTheAttack = new ArrayList<>();
        for(Map<Iceberg, Integer> icebergsThatCanAttack : turnsTillArrivalToIcebergsThatNeedToAttack.values()) {
            for(Iceberg participatingIceberg : icebergsThatCanAttack.keySet()) {
                icebergsParticipatingInTheAttack.add(participatingIceberg);
            }
        }
        return icebergsParticipatingInTheAttack;
    }


    /**
     * A function that performs all the actions of the attack for the given turn.
     */
    public void execute() {
        // Get the map of icebergs that need to attack the destination and the amount that each of them needs to send
        Map<Iceberg, Integer> currentAttacksThatNeedToBeExecuted = turnsTillArrivalToIcebergsThatNeedToAttack.get(turnsTillArrival);

        // If there are no icebergs that need to attack the destination, we can stop here
        if (currentAttacksThatNeedToBeExecuted == null) {
            return;
        }

        // Loop through all icebergs that need to attack the destination, and attack
        for(Iceberg icebergThatNeedsToAttack : currentAttacksThatNeedToBeExecuted.keySet()) {
            icebergThatNeedsToAttack.sendPenguins(destination, currentAttacksThatNeedToBeExecuted.get(icebergThatNeedsToAttack));
        }
    }


    /**
     * A simple function that will be performed from doTurn at the start of every turn.
     */
    public void decrementTurnsTillArrival() {
        turnsTillArrival--;
    }







    public static boolean canAttack(Game game, IceBuilding destination) {

        return false;
    }






    /**
     * A toString function for an attack.
     * @return A string representation of the attack
     */
    public String toString() {
        return "Attack:\n" + "Destination: " + destination.toString() + "\nArriving in: " + turnsTillArrival + "\nIcebergs that will participate\n" + turnsTillArrivalToIcebergsThatNeedToAttack.toString();
    }
}
