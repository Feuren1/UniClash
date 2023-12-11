import {inject, injectable} from '@loopback/core';
import {repository} from '@loopback/repository';
import {Critter, CritterUsable, Item, Student} from '../models';
import {
  AttackRepository,
  CritterAttackRepository,
  CritterRepository,
  CritterTemplateRepository,
  ItemRepository, ItemTemplateRepository,
  StudentRepository
} from '../repositories';
import {CritterStatsService} from './critter-stats.service';
import {ItemUsable} from "../models/item-usable.model";
import {ItemStatsService} from "./item-stats.service";

@injectable()
export class StudentItemService {
  constructor(
    @repository(ItemRepository) protected itemTemplateRepository: ItemTemplateRepository,
    @repository(ItemRepository) protected itemRepository: ItemRepository,
    @repository(StudentRepository) protected studentRepository: StudentRepository,
    @inject('services.ItemStatsService') // Inject the CritterStatsService
    protected itemStatsService: ItemStatsService,
  ) { }

  async createItemUsableListOnStudentId(studentId: number): Promise<ItemUsable[]> {
    const student: Student = await this.studentRepository.findById(studentId, {
      include: ['items'],
    })

    const items: Item[] = student.items;
    const itemUsables: ItemUsable[] = [];

    for (const item of items) {

      //if (critter.id !== undefined) {//other option is to remove the ? in the model at id? was used
      //}
      const itemUsable = await this.itemStatsService.createItemUsable(item.id);
      itemUsables.push(itemUsable);

    }

    return itemUsables;
  }

  async createItemUsableListOfAll(): Promise<ItemUsable[]> {

    const items: Item[] = await this.itemRepository.find();
    const itemUsables: ItemUsable[] = [];

    for (const item of items) {

      //if (critter.id !== undefined) {//other option, that was used, is to remove the ? in the model at id?
      //}
      const critterUsable = await this.itemStatsService.createItemUsable(item.id);
      itemUsables.push(critterUsable);

    }

    return itemUsables;
  }
}
