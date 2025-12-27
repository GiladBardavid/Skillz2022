<a id="readme-top"></a>

<!-- TOP SECTION -->
<div align="center">
   <h3 align="center">🥈Israeli Programming Championship 2022 - 2nd Place Code🥈</h3>
   
   <img src="images/team_image.JPG" alt="Team photo" width="300">

   <p align="center">
      Our code that won us the 2nd place at the official Israeli Programming Championship organized by the Ministry of Education.
   </p>
</div>

<!-- FILE STRUCTURE -->
## :file_folder: File Structure

```
.
├── api_autocomplete
├── images
├── src
│    ├── MyBot.java
│    ├── ...  .java
├── GrandFinalsBlitz.java
└── README.md
```

| No | File Name | Details 
|----|------------|-------|
| 1  | api_autocomplete | The championship had a built-in web based IDE. We preferred to use our own local IDEs so we implemented a mock of the game's API so that we could get autocorrection in our local IDEs as well as static typechecking.
| 2  | images | Selected images from the finals.
| 3  | src | Our bot's source code.
| 4  | src/MyBot.java | Entry point.
| 5  | GrandFinalsBlitz.java | My (Gilad) code that I wrote in the 40-minute grand finals stage. See [Grand Finals](#grand-finals) for more info.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- ABOUT THE CONTEST -->
## :beginner: About The Contest
Skillz Coding, the official Israeli High School Programming Championship, is an annual 4-month-long programming tournament for groups of 3-5 students.
Each year the tournament consists of a "game" that involves the code of two teams facing each other to win the game as the game's rules and API get more complex the later the stage.  
In 2022, I competed with 2 of my highschool friends.  
The contest is split between 4 stages:

* **Regional Contest (~8,000 groups)**  
Each school across the country runs a self-hosted one-day tournament based on simplified rules of the full championship objective and the top 3 teams from each school advance to the national online stage.

* **National Contest (~1,000 groups)**  
The national stage lasts 3 months and comprises of multiple iterations of the contest organizers releasing a new mechanic or feature to the game, giving the teams limited time to adapt their code and then running an all-vs-all tournament where a win awards 2 points, a tie 1 and a loss 0.
A team's score is the sum of their scores against all other competing teams, and the total overall score is a weighted sum of a team's tournaments scores where a larger weight is given to tournaments later in the contest.

* **Finals (100 groups)**  
The top 100 teams from the online stage are invited to compete in the finals, a one-day physical event.
In the finals all scores are reset and another game changing feature is announced. Each team then has 3 hours to apply changes to their code from the national stage to adapt to the new feature. A single all-vs-all tournament is run and the top 8 teams out of the 100 participating teams move on to the Grand Finals.

* <a id="grand-finals"></a>**Grand Finals (8 groups)** 
Up until this stage, teams compete together as a unit and at this final stage, a final twist to the game is introduced and all team members split up to compete as individuals.
Each team member then as only 40 minutes to update the code to adapt to the twist. To determine the final ranking, an individual all-vs-all tournament is run with the same points distribution mechanism from the previous stages and a team's score is the average of each of it's members scores.
This year, the twist was that game reset to the vanilla version from the start of the national contest, removing all of the special features added throughout the national stage, as well as all of the team's code was erased and each team member started the 40 minute coding period with nothing but a simple blank template file.

Our team made it through all stages and won 2nd place in the grand finals, awarding us with a trophy that is to-this-day displayed at our school's computer lab, as well as a medal and an Xbox One each.

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- KEY STRATEGIES -->
## :brain: Key Strategies & Algorithms
* **Deep Lookahead Prediction:** The core of the bot is the Prediction class. Before making any move, the bot simulates the game forward by a variable horizon of turns (determined by the map's diameter) to predict the future state of every iceberg.  
    * **Action Generation:** In every turn, the bot instantiates hundreds of possible moves (AttackAction, UpgradeAction, BridgeAction, DefendAction).  
    * **Action Scoring:** Each action is assigned a score based on how much it improves the future game state compared to the baseline prediction.  
    * **Action Selection:** The bot sorts actions by score and executes them in order from largest to smallest.  

* **Multi-Source Attack Plans:** The main distinction between other bots and ours is our AttackPlan class. Using our updating prediction, we generate multi-source attack plans and keep track of our ongoing attacks in a global structure that is perserved between turns.  
Therefore, we allow for strategies spanning and executed across multiple turns instead of relying on a per-turn strategy.

* **Dynamic Runtime Dependant Exiting:** As the bot had a maximum runtime per turn, we dynamically stop executing new plans based on the elapsed time from the start of the call to the doTurn function to make sure we finish executing before running out of time.

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- TEAM MEMBERS -->
## :fire: Team Members

Team captain - Gilad B. (Me) - [GitHub](https://github.com/GiladBardavid)

Team member  - Matan K.

Team member  - Asaf A.


Project Link: [https://github.com/GiladBardavid/Skillz2022](https://github.com/GiladBardavid/Skillz2022)

<p align="right">(<a href="#readme-top">back to top</a>)</p>
