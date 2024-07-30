import {inject, injectable, service} from '@loopback/core';
import {repository} from '@loopback/repository';
import {Critter, CritterUsable, Item, ItemTemplate, Student} from '../models';
import {
  CritterRepository,
  ItemRepository, ItemTemplateRepository,
  StudentRepository
} from '../repositories';;
import {ItemUsable} from "../models/item-usable.model";
import {ItemStatsService} from "./item-stats.service";
import {LevelCalcCritterService} from "./levelCalc-critter.service";

@injectable()
export class StudentItemService {
  constructor(
    @repository(ItemTemplateRepository) protected itemTemplateRepository: ItemTemplateRepository,
    @repository(ItemRepository) protected itemRepository: ItemRepository,
    @repository(CritterRepository) protected critterRepository: CritterRepository,
    @repository(StudentRepository) protected studentRepository: StudentRepository,
    @service(LevelCalcCritterService) protected levelCalcCritterService : LevelCalcCritterService,
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
      const itemUsable = await this.itemStatsService.createItemUsable(item.id);
      itemUsables.push(itemUsable);

    }

    return itemUsables;
  }

  async createItemUsableListOfAll(): Promise<ItemUsable[]> {

    const items: Item[] = await this.itemRepository.find();
    const itemUsables: ItemUsable[] = [];

    for (const item of items) {
      const critterUsable = await this.itemStatsService.createItemUsable(item.id);
      itemUsables.push(critterUsable);

    }

    return itemUsables;
  }

  async buyItem(studentId : number, itemTemplateId : number) : Promise<Boolean> {
    const student: Student = await this.studentRepository.findById(studentId)
    const studentItem: Student = await this.studentRepository.findById(studentId, {
      include: ['items'],
    })

    const itemTemplate: ItemTemplate = await this.itemTemplateRepository.findById(itemTemplateId);
    if(itemTemplate.cost != null && student.credits != null) {
      if(student.credits - itemTemplate.cost > -1) {
        student.credits -= itemTemplate.cost

        const items: Item[] = studentItem.items;
        try {
          for (const item of items) {
            if (item.itemTemplateId == itemTemplateId) {
              if (item.quantity != null) {
                item.quantity++
                await this.itemRepository.update(item);
                await this.studentRepository.update(student);
                
                return true
              }
            }
          }
        }catch (e) {
          console.log("Inventory is empty");
        }
        const itemData: Partial<Item> = {
          quantity: 1,
          itemTemplateId: itemTemplateId,
          studentId: studentId,
        };
        const newItem: Item = new Item(itemData);
        this.itemRepository.create(newItem)
      }
    }
    return false
  }

  async useItem(studentId : number, itemTemplateId : number) : Promise<Boolean>{
    const studentItem: Student = await this.studentRepository.findById(studentId, {
      include: ['items'],
    })
    const items: Item[] = studentItem.items;

    try {
      for (const item of items) {
          if(item.itemTemplateId == itemTemplateId){
            if(item.quantity != null && item.quantity>0){
              item.quantity--
              await this.itemRepository.update(item);
              return true
            }
          }
      }
    }catch (e) {
      console.log("Inventory is empty");
    }
    return false
  }

  async useRedBull(critterId : number):Promise<Boolean>{
    const critter: Critter = await this.critterRepository.findById(critterId)
    const itemUsed : Boolean = await this.useItem(critter.studentId, 1)

    if(itemUsed){
      this.levelCalcCritterService.increaseCritterExp(critterId,100)
      return true
    }
    return false
  }
}
