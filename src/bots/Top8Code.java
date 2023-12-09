package bots;
import penguin_game.*;
import java.util.*;


/**
 * This is an example for a bot.
 */
public class MyBot implements SkillzBot {
    /**
     * Makes the bot run a single turn.
     *
     * @param game - the current game state.
     */
    @Override
    public void doTurn(Game game) {
        for(Iceberg myIceberg : game.getMyIcebergs()) {

            boolean upgraded = false;

            Iceberg destination = null;
            destination = closestNeutral(game, myIceberg);

            if(destination == null || isEnemyClosest(game, destination)) {
                destination = closestEnemy(game, myIceberg);
            }


            if(!areThereEnemyPenguinTowardMe(game, myIceberg)) {
                int turnsTillArrival = myIceberg.getTurnsTillArrival(destination);

                int onImpact = onImpact(game, destination, turnsTillArrival);

                if(myIceberg.penguinAmount > onImpact && onImpact >= 0) {
                    myIceberg.sendPenguins(destination, onImpact + 1);
                }

                else {
                    if(myIceberg.penguinAmount >= myIceberg.upgradeCost) {
                        if(!isEnemyClosest(game, myIceberg)) {
                            upgraded = true;
                            myIceberg.upgrade();
                        }
                    }
                }



                if(upgraded == false) {
                    for(Iceberg dangered : game.getMyIcebergs()) {
                        if(isInDanger(game, dangered) && myIceberg.getTurnsTillArrival(dangered) <= minDistanceEnemyPenguinGroup(game, dangered)) {
                            myIceberg.sendPenguins(dangered, 2);
                        }
                    }
                }
            }






        }
    }

    public int minDistanceEnemyPenguinGroup(Game game, Iceberg i) {
        int minDis = 10000;
        for(PenguinGroup p : game.getEnemyPenguinGroups()) {
            if(p.destination == i) {
                minDis = p.turnsTillArrival;
            }
        }
        return minDis;
    }

    public boolean isInDanger(Game game, Iceberg my) {
        boolean danger = areThereEnemyPenguinTowardMe(game, my);
        boolean gotHelp = gotHelp(game, my);

        return danger && !gotHelp;
    }

    public boolean gotHelp(Game game, Iceberg i) {
        for(PenguinGroup p : game.getMyPenguinGroups()) {
            if(p.destination == i) {
                return true;
            }
        }
        return false;
    }


    public int onImpact(Game game, Iceberg iceberg, int turnsTillArrival) {
        if(iceberg.owner == game.getNeutral()) {
            return iceberg.penguinAmount;
        }

        int amount = iceberg.penguinAmount + (iceberg.penguinsPerTurn * turnsTillArrival);
        for(int i = 0; i <= turnsTillArrival; i++) {
            amount += getEnemyPenguinsArrivingInX(game, iceberg, i);
            amount -= getMyPenguinsArrivingInX(game, iceberg, i);
        }

        return amount;
    }


    public boolean isEnemyClosest(Game game, Iceberg i) {
        Iceberg minDis = game.getMyIcebergs()[0];
        if(game.getMyIcebergs()[0] == i) {
            minDis = game.getEnemyIcebergs()[0];
        }

        for(Iceberg a : game.getMyIcebergs()) {
            if(a.getTurnsTillArrival(i) <= minDis.getTurnsTillArrival(i) && a != i) {
                minDis = a;
            }
        }
        for(Iceberg a : game.getEnemyIcebergs()) {
            if(a.getTurnsTillArrival(i) <= minDis.getTurnsTillArrival(i) && a != i) {
                minDis = a;
            }
        }
        return (minDis.owner == game.getEnemy());
    }

    public Iceberg closestNeutral(Game game, Iceberg check) {
        if(game.getNeutralIcebergs().length == 0) return null;
        Iceberg minDis = game.getNeutralIcebergs()[0];

        for(Iceberg neutral : game.getNeutralIcebergs()) {
            if(neutral.getTurnsTillArrival(check) < minDis.getTurnsTillArrival(check)) {
                minDis = neutral;
            }
        }

        return minDis;
    }

    public Iceberg closestEnemy(Game game, Iceberg check) {
        Iceberg minDis = game.getEnemyIcebergs()[0];

        for(Iceberg enemy : game.getEnemyIcebergs()) {
            if(enemy.getTurnsTillArrival(check) < minDis.getTurnsTillArrival(check)) {
                minDis = enemy;
            }
        }

        return minDis;
    }


    public int getEnemyPenguinsArrivingInX(Game game, Iceberg toCheck, int x) {
        int count = 0;
        for(PenguinGroup p : game.getEnemyPenguinGroups()) {
            if(p.destination == toCheck && p.turnsTillArrival == x) {
                count += p.penguinAmount;
            }
        }
        return count;
    }

    public int getMyPenguinsArrivingInX(Game game, Iceberg toCheck, int x) {
        int count = 0;
        for(PenguinGroup p : game.getMyPenguinGroups()) {
            if(p.destination == toCheck && p.turnsTillArrival == x) {
                count += p.penguinAmount;
            }
        }
        return count;
    }


    public boolean areThereEnemyPenguinTowardMe(Game game, Iceberg my) {
        for(PenguinGroup p : game.getEnemyPenguinGroups()) {
            if(p.destination == my) {
                return true;
            }
        }
        return false;
    }
}
