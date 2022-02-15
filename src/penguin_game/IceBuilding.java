package penguin_game;

public class IceBuilding extends GameObject {
    public int penguinAmount;

    public boolean canSendPenguins(IceBuilding destination, int penguinAmount) { return true;}

    public int getTurnsTillArrival(IceBuilding destination) { return 0;}

    public void sendPenguins(IceBuilding destination, int penguinAmount) {}
}
