import {Entity, model, property, hasMany} from '@loopback/repository';
import {Item} from './item.model';

@model()
export class ItemTemplate extends Entity {
  @property({
    type: 'number',
    id: true,
    generated: true,
  })
  id?: number;

  @property({
    type: 'string',
    default: "Item",
  })
  name?: string;

  @property({
    type: 'number',
    default: 10,
  })
  cost?: number;

  @hasMany(() => Item)
  items: Item[];

  constructor(data?: Partial<ItemTemplate>) {
    super(data);
  }
}

export interface ItemTemplateRelations {
  // describe navigational properties here
}

export type ItemTemplateWithRelations = ItemTemplate & ItemTemplateRelations;
