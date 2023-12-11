import {injectable} from '@loopback/core';
import {repository} from '@loopback/repository';
import {Attack, Critter, CritterAttack, CritterTemplate, CritterUsable, Item, ItemTemplate} from '../models';
import {
  AttackRepository,
  CritterAttackRepository,
  CritterRepository,
  CritterTemplateRepository, ItemRepository,
  ItemTemplateRepository
} from '../repositories';
import {ItemUsable} from "../models/item-usable.model";

@injectable()
export class ItemStatsService {
  constructor(
    @repository(ItemTemplateRepository) protected itemTemplateRepository: ItemTemplateRepository,
    @repository(ItemRepository) protected itemRepository: ItemRepository,
  ) { }

  async createItemUsable(itemId: number): Promise<ItemUsable> {
    const item: Item = await this.itemRepository.findById(itemId);
    const itemTemplate: ItemTemplate = await this.itemTemplateRepository.findById(item.itemTemplateId);
    //const actualStats: number[] = this.calculateStats(critterTemplate, critter);

    // Check if the attacks array is empty
    /*if (attacks.length === 0) {
      // Handle the case when there are no attacks (provide a default or handle accordingly)
      // For example, set a default name and empty attacks array:
      const critterUsable = new CritterUsable({
        level: critter.level,
        name: 'No Attacks Available', // Set a default name
        hp: actualStats[0], // Set the actual HP
        atk: actualStats[1], // Set the actual attack
        def: actualStats[2], // Set the actual defense
        spd: actualStats[3], // Set the actual speed
        attacks: [], // Set an empty array of attacks
      });
      return critterUsable;
    }*/

    // Create an instance of CritterUsable with valid attacks
    const itemUsable = new ItemUsable({
      itemTemplateId : itemTemplate.id,
      id : item.id,
      cost : itemTemplate.cost,
      quantity : item.quantity,
      studentId : item.studentId,
    });

    return itemUsable;
  }


  /*async getAttacks(attacks: CritterAttack[]): Promise<Attack[]> {


    const attackArray: Attack[] = [];
    console.log('Received attacks: in get attacks method c', attacks);
    for (const element of attacks) {
      const attack = await this.attackRepository.findById(element.attackId);
      attackArray.push(attack);
    }
    return attackArray;
  }*/

  /*async calculateActualStats(itemId: number): Promise<number[]> {
    // Retrieve the critter and its copies from the repository
    const item: Item = await this.itemRepository.findById(itemId);
    const itemTemplate: ItemTemplate = await this.itemTemplateRepository.findById(item.itemTemplateId);

    // Calculate the actual stats based on the critter and its copies
    const actualStats: number[] = this.calculateStats(critterTemplate, critter);

    return actualStats;
  }*/

  // Implement the actual stats calculation logic here
  /*private calculateStats(critterTemplate: CritterTemplate, critter: Critter): number[] {
    // Your logic for calculating stats goes here
    // You can use the critter, its copies, and attacks to compute the actual stats
    // and return them as an array of integers.
    return [
      critterTemplate.baseHealth + critter.level,
      critterTemplate.baseAttack + critter.level,
      critterTemplate.baseDefence + critter.level,
      critterTemplate.baseSpeed + critter.level,
      // Additional calculations using 'attacks' if needed
    ];
  }*/
}
