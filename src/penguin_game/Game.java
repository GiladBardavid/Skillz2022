package penguin_game;

public class Game extends BaseObject {

    public final int bonusIcebergMaxTurnsToBonus;
    public final int bonusIcebergPenguinBonus;
    public final int maxPoints;
    public final int maxTurns;
    public final int turn;
    private final Player[] players;

    public final int bonusIcebergBridgeCost = 0;
    public final float bonusIcebergBridgeSpeedMultiplier = 0;
    public final int bonusIcebergMaxBridgeDuration = 0;

    public final float decoyCostFactor = 0;
    public final int icebergBridgeCost = 0;
    public final float icebergBridgeSpeedMultiplier = 0;
    public final int icebergMaxBridgeDuration = 0;

    Game() {
        turn = 0;
        maxTurns = 300;
        maxPoints = 1000;
        players = new Player[3];
        bonusIcebergMaxTurnsToBonus = 0;
        bonusIcebergPenguinBonus = 0;
    }

    public void debug(Object arg){
        System.out.println(arg);
    }

    public Player getMyself() {return players[0];}

    public Player getNeutral() {return players[1];}

    public Player getEnemy() {return players[2];}

    public Iceberg[] getMyIcebergs(){return getMyself().icebergs;}

    public Iceberg[] getEnemyIcebergs(){return getEnemy().icebergs;}

    public Iceberg[] getNeutralIcebergs(){return getNeutral().icebergs;}

    public Iceberg[] getAllIcebergs(){return new Iceberg[10];}

    public PenguinGroup[] getMyPenguinGroups(){return getMyself().penguinGroups;}

    public PenguinGroup[] getEnemyPenguinGroups(){return getEnemy().penguinGroups;}

    public PenguinGroup[] getNaturalPenguinGroups(){return getNeutral().penguinGroups;}

    public PenguinGroup[] getAllPenguinGroups(){return new PenguinGroup[10];}

    public int getMaxTurnTime(){
        return 500;
    }

    public int getTimeRemaining(){return 100;}

    public BonusIceberg getMyBonusIceberg() {
        return new BonusIceberg();
    }

    public BonusIceberg getEnemyBonusIceberg() {
        return new BonusIceberg();
    }

    public BonusIceberg getNeutralBonusIceberg() {
        return new BonusIceberg();
    }

    public BonusIceberg getBonusIceberg() {
        return new BonusIceberg();
    }

    public PenguinGroup[] getMyDecoyPenguinGroups() {
        return null;
    }


}
