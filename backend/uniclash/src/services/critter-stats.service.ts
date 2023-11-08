import {injectable} from '@loopback/core';
import {repository} from '@loopback/repository';
import {Attack, Critter, CritterCopy, CritterCopyAttack, CritterUsable} from '../models';
import {AttackRepository, CritterCopyAttackRepository, CritterCopyRepository, CritterRepository} from '../repositories';

@injectable()
export class CritterStatsService {
  constructor(
    @repository(CritterRepository) protected critterRepository: CritterRepository,
    @repository(CritterCopyRepository) protected critterCopyRepository: CritterCopyRepository,
    @repository(CritterCopyAttackRepository) protected critterCopyAttackRepository: CritterCopyAttackRepository,
    @repository(AttackRepository) protected attackRepository: AttackRepository
  ) { }

  async createCritterUsable(critterCopyId: number): Promise<CritterUsable> {
    const copy: CritterCopy = await this.critterCopyRepository.findById(critterCopyId);
    const critter: Critter = await this.critterRepository.findById(copy.critterId);
    console.log('Received attacks: createcritterusable method', copy.critterCopyAttacks);
    const attacks: Attack[] = await this.getAttacks(copy.critterCopyAttacks);
    const actualStats: number[] = this.calculateStats(critter, copy);

    // Check if the attacks array is empty
    if (attacks.length === 0) {
      // Handle the case when there are no attacks (provide a default or handle accordingly)
      // For example, set a default name and empty attacks array:
      const critterUsable = new CritterUsable({
        level: copy.level,
        name: 'No Attacks Available', // Set a default name
        hp: actualStats[0], // Set the actual HP
        atk: actualStats[1], // Set the actual attack
        def: actualStats[2], // Set the actual defense
        spd: actualStats[3], // Set the actual speed
        attacks: [], // Set an empty array of attacks
      });
      return critterUsable;
    }

    // Create an instance of CritterUsable with valid attacks
    const critterUsable = new CritterUsable({
      level: copy.level,
      name: critter.name, // Use the critter's name
      hp: actualStats[0], // Set the actual HP
      atk: actualStats[1], // Set the actual attack
      def: actualStats[2], // Set the actual defense
      spd: actualStats[3], // Set the actual speed
      attacks: attacks, // Set the array of Attack instances
    });

    return critterUsable;
  }


  async getAttacks(attacks: CritterCopyAttack[]): Promise<Attack[]> {
    const attackArray: Attack[] = [];
    console.log('Received attacks: in get attacks method c', attacks);
    for (const element of attacks) {
      const attack = await this.attackRepository.findById(element.attackId);
      attackArray.push(attack);
    }
    return attackArray;
  }

  async calculateActualStats(critterCopyId: number): Promise<number[]> {
    // Retrieve the critter and its copies from the repository
    const copy: CritterCopy = await this.critterCopyRepository.findById(critterCopyId);
    const critter: Critter = await this.critterRepository.findById(copy.critterId);

    // Calculate the actual stats based on the critter and its copies
    const actualStats: number[] = this.calculateStats(critter, copy);

    return actualStats;
  }

  // Implement the actual stats calculation logic here
  private calculateStats(critter: Critter, copy: CritterCopy): number[] {
    // Your logic for calculating stats goes here
    // You can use the critter, its copies, and attacks to compute the actual stats
    // and return them as an array of integers.
    return [
      critter.baseHealth + copy.level,
      critter.baseAttack + copy.level,
      critter.baseDefend + copy.level,
      critter.baseSpeed + copy.level,
      // Additional calculations using 'attacks' if needed
    ];
  }
}
