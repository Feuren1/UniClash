# Game API<br>

## Critters<br>
| id | name                | baseHealth | baseAttack | baseDefence | baseSpeed |Evolution            |Evolves at|type      |
|----|---------------------|------------|------------|-------------|-----------|---------------------|----------|----------|
| 1  | PRC2Duck            | 41         | 64         | 45          | 50        |KnifeDuck            |20        |Java      |
| 2  | KnifeDuck           | 61         | 84         | 65          | 70        |MutantDuck           |40        |water     |
| 3  | MutantDuck          | 91         | 134        | 95          | 80        |                     |          |AI        |
| 4  | KnifeTurtle         | 200        | 45         | 10          | 40        |                     |          |water     |
| 5  | CoolDuck            | 20         | 10         | 55          | 80        |CrocodileDuck        |50        |water     |
| 6  | CrocodileDuck       | 95         | 125        | 79          | 81        |                     |          |AI        |
| 7  | DemoMusk            | 40         | 50         | 40          | 90        |Musk                 |25        |human     |
| 8  | Musk                | 90         | 95         | 95          | 70        |                     |          |human     |
| 9  | Mockito             | 48         | 48         | 48          | 48        |                     |          |Java      |
| 10 | QuizizzDragon       | 80         | 105        | 65          | 130       |                     |          |dragon    |
| 10 | LinuxPingiun        | 50         | 95         | 180         | 70        |                     |          |Linux     |
| 11 | Borzoi              | 90         | 65         | 65          | 15        |                     |          |normal    |
| 12 | Pikachu             | 35         | 55         | 40          | 90        |                     |          |elektro   |
| 13 | Matryoshka          | 35         | 100        | 50          | 120       |                     |          |normal    |
| 14 | Nutcracker          | 70         | 84         | 70          | 51        |                     |          |Java      |
| 15 | EggGivingWoolMilkPig| 90         | 65         | 65          | 15        |                     |          |normal    |
| 16 | StudentAssistence   | 100        | 100        | 100         | 100       |                     |          |human     |
| 17 | Charmander          | 39         | 52         | 43          | 65        |Charmeleon           |16        |fire      |
| 18 | Charmeleon          | 58         | 64         | 58          | 80        |Charizard            |36        |fire      |
| 19 | Charizard           | 78         | 84         | 78          | 100       |                     |          |fire      |
| 20 | Snorlax             | 160        | 110        | 65          | 30        |                     |          |normal    |
| 21 | Fontys              | 80         | 130        | 100         | 110       |                     |          |dragon    |

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
|id  |name                |type       |attack value  |attack Type  |
|----|--------------------|-----------|--------------|-------------|
| 1  | Growl              |normal     | 20           |DAMAGE_DEALER|
| 2  | Ember              |fire       | 50           |DAMAGE_DEALER|
| 0  | Inferno Blaze      |fire       | 105          |DAMAGE_DEALER|
| 0  | Heat Wave          |fire       | 85           |DAMAGE_DEALER|
| 3  | QuickAttack        |normal     | 45           |DAMAGE_DEALER|
| 4  | Tackle             | Normal    | 45           |DAMAGE_DEALER|
| 5  | Zap Connon         | Electric  | 120          |DAMAGE_DEALER|
| 6  | Volt Tackle        | Electric  | 100          |DAMAGE_DEALER|
| 7  | Double Shock       | Electric  | 110          |DAMAGE_DEALER|
| 8  | Blizzard           | Ice       | 110          |DAMAGE_DEALER|
| 9  | RunTime Exp        | Java      | 120          |DAMAGE_DEALER|
| 10 | EndlesLoop         | Java      | 90           |DAMAGE_DEALER|
| 11 | OpenSource Strike  | Linux     | 90           |DAMAGE_DEALER|
| 12 | Tech Knowledge     | human     | 100          |DAMAGE_DEALER|
| 13 | Adaptabillity Slash| human     | 95           |DAMAGE_DEALER|
| 14 | Lightning Bolt     | electro   | 105          |DAMAGE_DEALER|
| 15 | Thunder Strike     | electro   | 120          |DAMAGE_DEALER|
| 16 | dragon Breath      | dragon    | 105          |DAMAGE_DEALER|
| 17 | scale Shield       | dragon    | 90           |DAMAGE_DEALER|
| 18 | normal kick        | normal    | 80           |DAMAGE_DEALER|
| 19 | versatile slam     | normal    | 95           |DAMAGE_DEALER|
| 20 | rain               | water     | 80           |DAMAGE_DEALER|
| 21 | tsunami            | water     | 110          |DAMAGE_DEALER|


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
| id | name           | Price |
|----|----------------|-------|
| 1  | RedBull        | 5     |
| 2  | CHOCOLATEWAFFLE| 3     |
| 2  | FART SPRAY     | 1     |

## Critter Values<br>
Max Level: 100<br>
Ep for one Redbull: 100ep<br>

## Student / User Rules<br>
Every user gets 100 coins by registering <br>
Ep for reaching a User Level: 500 <br>
Max User Level: max.Integer<br>
Needed Level to create new Buildings: 5 , 10 ,15 aso.<br>
And how many buildings: 2<br>

## Map<br>
Refresh every 5min<br>
Frist spawn after 30min<br>
Critter Radius 2km -> 800 wild encounter<br>

## Other Game Rules<br>
Coins per catch: 1 <br>
Ep per catch: 25<br>
Coins per fight Win: 5 <br>
Ep per fight Win: 200<br>
Coins per captured Arena: 7<br>
Ep per captured Arena: 300<br>
Legendary critters can not be set in arenas.<br>
