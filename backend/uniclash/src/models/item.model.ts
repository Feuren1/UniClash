import {Entity, model, property, belongsTo} from '@loopback/repository';
import {ItemTemplate} from './item-template.model';

@model()
export class Item extends Entity {
  @property({
    type: 'number',
    id: true,
    generated: true,
  })
  id?: number;

  @property({
    type: 'number',
    default: 1,
  })
  quantity?: number;

  @belongsTo(() => ItemTemplate)
  itemTemplateId: number;

  constructor(data?: Partial<Item>) {
    super(data);
  }
}

export interface ItemRelations {
  // describe navigational properties here
}

export type ItemWithRelations = Item & ItemRelations;
