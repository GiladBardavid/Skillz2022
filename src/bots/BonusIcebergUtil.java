package bots;

import penguin_game.*;
import java.util.*;

public class BonusIcebergUtil {

    /**
     * A function that finds how many penguins will the bonus iceberg generate in the next x turns for a single iceberg that is owned.
     * @param game current game state
     * @param inHowManyTurns how many turns in the future to calculate
     * @return how many penguins will the bonus iceberg generate in the next x turns for a single iceberg that is owned.
     */
    public static int getAmountOfPenguinsThatBonusIcebergWillGenerateInTheNextXTurnsInEachIceberg(Game game, int inHowManyTurns) {
        // Fetch bonus iceberg
        BonusIceberg bonusIceberg = game.getBonusIceberg();

        // If the bonus iceberg will not generate any penguins in the next x turns, return 0.
        if(inHowManyTurns < bonusIceberg.turnsLeftToBonus) {
            return 0;
        }

        // Find how many times will the bonus iceberg generate in the next x turns.
        // After bonusIceberg.turnsLeftToBonus turns, the bonus iceberg will generate the first wave of penguins.
        int amountOfTurnsThatTheBonusIcebergWillAddPenguins = 1;
        inHowManyTurns -= bonusIceberg.turnsLeftToBonus;

        // After the first wave, we add the amount of turns that the bonus iceberg will generate in the next x turns,
        // which is calculated by taking the amount of turns / the amount of turns it takes the bonus iceberg to reload.
        amountOfTurnsThatTheBonusIcebergWillAddPenguins += inHowManyTurns / bonusIceberg.maxTurnsToBonus;

        // After we calculated the amount of times that the bonus iceberg will generate penguins,
        // in order to find the amount of penguins it will spawn we can multiply the amount of times it will spawn by the amount of penguins it
        // spawns each time.
        int amountOfPenguinsThatBonusIcebergWillGenerate = amountOfTurnsThatTheBonusIcebergWillAddPenguins * bonusIceberg.penguinBonus;

        // Return the amount of penguins that it will spawn in the next x turns per iceberg.
        return amountOfPenguinsThatBonusIcebergWillGenerate;
    }

}
