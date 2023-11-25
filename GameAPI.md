# Game API<br>

## Critters<br>
| id | name                | baseHealth | baseAttack | baseDefence | baseSpeed |Evolution            |Evolves at|type      |
|----|---------------------|------------|------------|-------------|-----------|---------------------|----------|----------|
| 0  | PRC2Duck            | 41         | 64         | 45          | 50        |KnifeDuck            |20        |          |
| 0  | KnifeDuck           | 61         | 84         | 65          | 70        |MutantDuck           |40        |          |
| 0  | MutantDuck          | 91         | 134        | 95          | 80        |                     |          |          |
| 0  | KnifeTurtle         | 200        | 45         | 10          | 40        |                     |          |          |
| 0  | CoolDuck            | 20         | 10         | 55          | 80        |CrocodileDuck        |50        |          |
| 0  | CrocodileDuck       | 95         | 125        | 79          | 81        |                     |          |          |
| 0  | DemoMusk            | 40         | 50         | 40          | 90        |Musk                 |25        |          |
| 0  | Musk                | 90         | 95         | 95          | 70        |                     |          |          |
| 0  | Mockito             | 48         | 48         | 48          | 48        |                     |          |          |
| 0  | QuizizzDragon       | 80         | 105        | 65          | 130       |                     |          |          |
| 0  | LinuxPingiun        | 50         | 95         | 180         | 70        |                     |          |          |
| 0  | Borzoi              | 90         | 65         | 65          | 15        |                     |          |          |
| 0  | Pikachu             | 35         | 55         | 40          | 90        |                     |          |          |
| 0  | Matryoshka          | 35         | 100        | 50          | 120       |                     |          |          |
| 0  | Nutcracker          | 70         | 84         | 70          | 51        |                     |          |          |
| 0  | EggGivingWoolMilkPig| 90         | 65         | 65          | 15        |                     |          |          |
| 0  | StudentAssistence   | 100        | 100        | 100         | 100       |                     |          |          |
| 1  | Charmander          | 39         | 52         | 43          | 65        |Charmeleon           |16        |          |
| 2  | Charmeleon          | 58         | 64         | 58          | 80        |Charizard            |36        |          |
| 23 | Charizard           | 78         | 84         | 78          | 100       |                     |          |          |
| 24 | Snorlax             | 160        | 110        | 65          | 30        |                     |          |          |
| 0  | Fontys              | 80         | 130        | 100         | 110       |                     |          |          |

## Types<br>
|name     |
|---------|
|Java     |
|Linux    |
|AI       |
|human    |
|dragon   |
|water    |
|normal   |
|electro  |

## Attacks<br>
|id|name|type|attack value|
|----|---------------|-----------|--------------|
| 1  | Growl         |           | 20           |
| 2  | Ember         |           | 50           |
| 3  | QuickAttack   |           | 45           |
| 4  | Tackle        | Normal    | 45           |
| 5  | Zap Connon    | Electric  | 120          |
| 6  | Volt Tackle   | Electric  | 100          |
| 7  | Double Shock  | Electric  | 110          |
| 8  | Blizzard      | Ice       | 110          |


## Effectivness<br>
//0.5 or 1 or 2
|Type1    |Type2     |effectivness value| 
|---------|----------|------------------|
|Java     |
|Linux    |
|AI       |
|human    |
|dragon   |
|water    |
|normal   |
|electro  |


## Items<br>
| id | name       | Price |
|----|------------|-------|
| 1  | RedBull    | 5     |
| 0  | string     | 0     |

## Critter Values<br>
Max Level: 50<br>

Ep for reaching a Level: 1000 <br>

# Other Game Rules<br>
Max User Level: 30? or max.Integer<br>
Coins per Win: 3 <br>
Ep per Win: 100<br>
Coins per captured Arena: 6<br>
Ep per captured Arena: 250<br>
Needed Level to create new Buildings: 5<br>
And how many building: 2<br>
Needed Level to create new Buildings: 10<br>
And how many building: +2<br>
Legendary critter can not set in arenas.<br>
