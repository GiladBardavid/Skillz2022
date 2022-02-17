package bots;

import penguin_game.*;
import java.util.*;

public class NeededHelp {

    public int howManyPenguins;
    public int inHowManyTurns;

    public NeededHelp(int howManyPenguins, int inHowManyTurns) {
        this.howManyPenguins = howManyPenguins;
        this.inHowManyTurns = inHowManyTurns;
    }



    public static NeededHelp getNeededHelp(Game game, IceBuilding myIceBuilding) {
        for(int i = 0; i < game.maxTurns; i++) {
            int penguinAmountInXTurns = GameUtil.getPenguinAmountInTurnXForEnemyOrNeutralIceBuilding(game, myIceBuilding, i);
            if(penguinAmountInXTurns <= 0) {
                return new NeededHelp(-penguinAmountInXTurns + 1, i);
            }
        }

        // Doesn't need help
        return null;
    }
}
