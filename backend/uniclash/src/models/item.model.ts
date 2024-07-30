import {Entity, model, property, belongsTo} from '@loopback/repository';
import {ItemTemplate} from './item-template.model';
import {Student} from './student.model';

@model()
export class Item extends Entity {
  @property({
    type: 'number',
    id: true,
    generated: true,
  })
  id: number;
  //removed the "?"

  @property({
    type: 'number',
    default: 1,
  })
  quantity?: number;

  @belongsTo(() => ItemTemplate)
  itemTemplateId: number;

  @belongsTo(() => Student)
  studentId: number;

  constructor(data?: Partial<Item>) {
    super(data);
  }
}

export interface ItemRelations {
  // describe navigational properties here
}

export type ItemWithRelations = Item & ItemRelations;
