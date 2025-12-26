package penguin_game;

public class Iceberg extends IceBuilding {

    public final int costFactor;
    public final int level;
    public final int penguinsPerTurn;
    public final int upgradeCost;
    public final int upgradeLevelLimit;
    public final int upgradeValue;

    public final int bridgeCost;
    public final float bridgeSpeedMultiplier;
    public final Bridge[] bridges;

    public final float decoyCostFactor;
    public final int maxBridgeDuration;

    Iceberg(int costFactor, int level, int penguinsPerTurn, int upgradeCost, int upgradeLevelLimit, int upgradeValue, int bridgeCost, float bridgeSpeedMultiplier, Bridge[] bridges,
            float decoyCostFactor, int maxBridgeDuration) {
        this.costFactor = costFactor;
        this.level = level;
        this.penguinsPerTurn = penguinsPerTurn;
        this.upgradeCost = upgradeCost;
        this.upgradeLevelLimit = upgradeLevelLimit;
        this.upgradeValue = upgradeValue;

        this.bridgeCost = bridgeCost;
        this.bridgeSpeedMultiplier = bridgeSpeedMultiplier;
        this.bridges = bridges;
        this.decoyCostFactor = decoyCostFactor;
        this.maxBridgeDuration = maxBridgeDuration;
    }

    public boolean canUpgrade() {return true;}

    public void upgrade() {}

    public boolean canCreateBridge(Iceberg destination) {return true;}

    public boolean canSendDecoyPenguins(Iceberg destination, Iceberg fakeDestination, int penguinsAmount) {return true;}

    public void createBridge(Iceberg destination) {}

    public void sendDecoyPenguins(Iceberg destination, Iceberg fakeDestination, int penguinsAmount) {}

}
