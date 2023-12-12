# Game API<br>

## Critters<br>
| id | name                | baseHealth | baseAttack | baseDefence | baseSpeed |Evolution            |Evolves at|type      |
|----|---------------------|------------|------------|-------------|-----------|---------------------|----------|----------|
| 0  | PRC2Duck            | 41         | 64         | 45          | 50        |KnifeDuck            |20        |Java      |
| 0  | KnifeDuck           | 61         | 84         | 65          | 70        |MutantDuck           |40        |water     |
| 0  | MutantDuck          | 91         | 134        | 95          | 80        |                     |          |AI        |
| 0  | KnifeTurtle         | 200        | 45         | 10          | 40        |                     |          |water     |
| 0  | CoolDuck            | 20         | 10         | 55          | 80        |CrocodileDuck        |50        |water     |
| 0  | CrocodileDuck       | 95         | 125        | 79          | 81        |                     |          |AI        |
| 0  | DemoMusk            | 40         | 50         | 40          | 90        |Musk                 |25        |human     |
| 0  | Musk                | 90         | 95         | 95          | 70        |                     |          |human     |
| 0  | Mockito             | 48         | 48         | 48          | 48        |                     |          |Java      |
| 0  | QuizizzDragon       | 80         | 105        | 65          | 130       |                     |          |dragon    |
| 0  | LinuxPingiun        | 50         | 95         | 180         | 70        |                     |          |Linux     |
| 0  | Borzoi              | 90         | 65         | 65          | 15        |                     |          |normal    |
| 0  | Pikachu             | 35         | 55         | 40          | 90        |                     |          |elektro   |
| 0  | Matryoshka          | 35         | 100        | 50          | 120       |                     |          |normal    |
| 0  | Nutcracker          | 70         | 84         | 70          | 51        |                     |          |Java      |
| 0  | EggGivingWoolMilkPig| 90         | 65         | 65          | 15        |                     |          |normal    |
| 0  | StudentAssistence   | 100        | 100        | 100         | 100       |                     |          |human     |
| 1  | Charmander          | 39         | 52         | 43          | 65        |Charmeleon           |16        |fire      |
| 2  | Charmeleon          | 58         | 64         | 58          | 80        |Charizard            |36        |fire      |
| 23 | Charizard           | 78         | 84         | 78          | 100       |                     |          |fire      |
| 24 | Snorlax             | 160        | 110        | 65          | 30        |                     |          |normal    |
| 0  | Fontys              | 80         | 130        | 100         | 110       |                     |          |dragon    |

## Types<br>
|name     |
|---------|
|Java     |
|Linux    |
|AI       |
|human    |
|dragon   |
|water    |
|fire     |
|normal   |
|electro  |

## Attacks<br>
|id  |name                |type       |attack value|
|----|--------------------|-----------|--------------|
| 1  | Growl              |           | 20           |
| 2  | Ember              |fire       | 50           |
| 0  | Inferno Blaze      |fire       | 105          |
| 0  | Heat Wave          |fire       | 85           |
| 3  | QuickAttack        |           | 45           |
| 4  | Tackle             | Normal    | 45           |
| 5  | Zap Connon         | Electric  | 120          |
| 6  | Volt Tackle        | Electric  | 100          |
| 7  | Double Shock       | Electric  | 110          |
| 8  | Blizzard           | Ice       | 110          |
| 0  | RunTime Exp        | Java      | 120          |
| 0  | EndlesLoop         | Java      | 90           |
| 0  | OpenSource Strike  | Linux     | 90           |
| 0  | Tech Knowledge     | human     | 100          |
| 0  | Adaptabillity Slash| human     | 95           |
| 0  | Lightning Bolt     | electro   | 105          |
| 0  | Thunder Strike     | electro   | 120          |
| 0  | dragon Breath      | dragon    | 105          |
| 0  | scale Shield       | dragon    | 90           |
| 0  | normal kick        | normal    | 80           |
| 0  | versatile slam     | normal    | 95           |
| 0  | rain               | water     | 80           |
| 0  | tsunami            | water     | 110          |


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
Max Level: 100<br>

Ep for reaching a User Level: 500 <br>

# Other Game Rules<br>
Max User Level: 30? or max.Integer<br>
Coins per catch: 1 <br>
Ep per catch: 25<br>
Coins per fight Win: 3 <br>
Ep per fight Win: 100<br>
Coins per captured Arena: 3<br>
Ep per captured Arena: 150<br>
Needed Level to create new Buildings: 5<br>
And how many building: 2<br>
Needed Level to create new Buildings: 10<br>
And how many building: +2<br>
Legendary critter can not set in arenas.<br>

jeder user bekommt 100 credits zum start
