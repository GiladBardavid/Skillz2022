package bots;

/*

TODOs:

    We only want to use the worse prediction if the destination iceberg is not neutral.
    In order to do that, in the prediction calculation, check for each enemy penguin group whether we should add it's worst case or not.


    - Remove date hacks

    - Generalize GameUtil planAttack see TODO in there

    - Look at history only until end of game

    - Strategy for last move

    - Ducks R Better turn 79 should attack the bonus iceberg

    - Change tactics near end of game

    - Optimize MAX_LOOKAHEAD in prediction

    - Add bridge info when planning attacks

    - Fix attacking neutral icebergs that enemy can defend

    - In defend action, consider splitting to multiple destinations

    - Improve score of defend action

    - Improve target score in attack action (defense , distance)

    - Better compute upgrade score

    - Improve bridge score

 */