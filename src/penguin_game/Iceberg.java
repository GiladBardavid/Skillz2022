package penguin_game;

public class Iceberg extends IceBuilding {

    public final int costFactor;
    public final int level;
    public final int penguinsPerTurn;
    public final int upgradeCost;
    public final int upgradeLevelLimit;
    public final int upgradeValue;

    Iceberg(int costFactor, int level, int penguinsPerTurn, int upgradeCost, int upgradeLevelLimit, int upgradeValue) {
        this.costFactor = costFactor;
        this.level = level;
        this.penguinsPerTurn = penguinsPerTurn;
        this.upgradeCost = upgradeCost;
        this.upgradeLevelLimit = upgradeLevelLimit;
        this.upgradeValue = upgradeValue;
    }

    public boolean CanUpgrade() {return true;}

    public void upgrade() {}

}
