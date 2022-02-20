package bots;

import penguin_game.*;

public class IceBuildingState {

    public int penguinAmount;

    public Owner owner;

    public enum Owner {
        ME, ENEMY, NEUTRAL
    }


    public IceBuildingState(int penguinAmount, Owner owner) {
        this.penguinAmount = penguinAmount;
        this.owner = owner;
    }

    public IceBuildingState(Game game, IceBuilding iceBuilding) {
        this.penguinAmount = iceBuilding.penguinAmount;

        switch (GameUtil.playerToString(game, iceBuilding.owner)) {
            case "Me":
                this.owner = Owner.ME;
                break;
            case "Enemy":
                this.owner = Owner.ENEMY;
                break;
            case "Neutral":
                this.owner = Owner.NEUTRAL;
                break;
        }
    }


    public Owner getOppositeOwner() {
        switch (this.owner) {
            case ME:
                return Owner.ENEMY;
            case ENEMY:
                return Owner.ME;
            case NEUTRAL:
                return Owner.NEUTRAL;
        }

        // Unreachable
        throw new IllegalStateException(owner.toString());
    }

    @Override
    public String toString() {
        return "{" + penguinAmount +
                "/" + owner +
                '}';
    }
}
