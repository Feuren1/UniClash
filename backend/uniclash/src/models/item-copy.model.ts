import {Entity, model, property, belongsTo} from '@loopback/repository';
import {Item} from './item.model';

@model()
export class ItemCopy extends Entity {
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

  @belongsTo(() => Item)
  itemId: number;

  constructor(data?: Partial<ItemCopy>) {
    super(data);
  }
}

export interface ItemCopyRelations {
  // describe navigational properties here
}

export type ItemCopyWithRelations = ItemCopy & ItemCopyRelations;
