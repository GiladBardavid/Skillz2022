package bots;

/*

Ideas:

     - Perhaps we can use a priority queue for certain goals such as upgrading an Iceberg or capturing different IceBuildings.
          - Evaluate the priority of each goal by how many penguins are we spending on it / how many pps are we getting.

     - I think it is better to evaluate how good capturing an iceberg is by calculating the difference between the penguins per second (pps)
       it gives me compared to the enemy. (For example: capturing an iceberg with 1 pps equals priority 2.


TODOs:

     - Create a GitHub project. The saving / submitting system on the website is weird.

     - Think of a way to compare how good it is to capture a normal iceberg compared to a bonus iceberg.

     - When adding to an Iceberg to an attack object,
       we also need to take into account the new amount of penguins that will be in each of the other, closer Icebergs.

     - Add help


Notes:

     - We can store previously calculated info in class variables. Our MyBot class is only initiated At the beginning of each duel.

     - Maybe we can implement a crash-after-date system to avoid others dueling against us after each round.

     - Perhaps in some cases it is worth keeping more than 1 penguin in the bonus iceberg.
       This is because we want to prevent the enemy from capturing it.

 */