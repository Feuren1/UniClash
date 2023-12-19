import {inject, injectable} from '@loopback/core';
import {repository} from '@loopback/repository';
import {Critter, CritterUsable, Item, ItemTemplate, Student} from '../models';
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
    @repository(ItemTemplateRepository) protected itemTemplateRepository: ItemTemplateRepository,
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

  async buyItem(studentId : number, itemTemplateId : number) : Promise<String> {
    const student: Student = await this.studentRepository.findById(studentId)
    const studentItem: Student = await this.studentRepository.findById(studentId, {
      include: ['items'],
    })

    const itemTemplate: ItemTemplate = await this.itemTemplateRepository.findById(itemTemplateId);
    console.log(`Student ${student.credits}`);
    console.log(`itemTemplate ${itemTemplate.name}`);
    if(itemTemplate.cost != null && student.credits != null) {
      console.log("Step 1");
      if(student.credits - itemTemplate.cost > -1) {
        console.log("Step 2");
        console.log("Student quantity: " + student.credits);
        student.credits -= itemTemplate.cost
        console.log("Student quantity: " + student.credits);

        const items: Item[] = studentItem.items;
        try {
          for (const item of items) {
            console.log("Step 3");
            if (item.itemTemplateId == itemTemplateId) {
              if (item.quantity != null) {
                item.quantity++
                await this.itemRepository.update(item);
                await this.studentRepository.update(student);
                return "new quantity" + item.quantity
              }
            }
          }
        }catch (e) {
          console.log("Inventory is empty");
        }
        console.log("Wenn genug Geld aber kein Item");
        const itemData: Partial<Item> = {
          //id: 1, // Hier kann die ID deines Items sein, wenn du sie bereits kennst
          quantity: 1, // Die Anzahl der Items
          itemTemplateId: itemTemplateId, // ID des Item-Templates, zu dem dieses Item gehört
          studentId: studentId, // ID des Students, dem dieses Item gehört
        };
        const newItem: Item = new Item(itemData);
        this.itemRepository.create(newItem)
      }
    }
    return "student" + studentId + " and itemID" + itemTemplateId
  }
}
