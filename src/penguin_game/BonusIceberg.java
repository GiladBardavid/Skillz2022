package penguin_game;

public class BonusIceberg extends IceBuilding{

    public int bridgeCost;
    public float bridgeSpeedMultiplier;
    public Bridge[] bridges;
    public int maxBridgeDuration;

    public int maxTurnsToBonus;

    public int penguinBonus;

    public int turnsLeftToBonus;

    public boolean canCretaeBridge(BonusIceberg destination) {return true;}

    public void createBridge(BonusIceberg destination) {}
}
