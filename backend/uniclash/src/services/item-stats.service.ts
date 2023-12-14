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

    const itemUsable = new ItemUsable({
      itemTemplateId : itemTemplate.id,
      id : item.id,
      cost : itemTemplate.cost,
      quantity : item.quantity,
      name : itemTemplate.name,
      studentId : item.studentId,
    });

    return itemUsable;
  }
}
