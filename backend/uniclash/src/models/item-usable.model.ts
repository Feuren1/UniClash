import {Model, model, property} from '@loopback/repository';
import {Attack} from '.';

@model()
export class ItemUsable extends Model {
  @property({
    type: 'number',
  })
  id: number;

  @property({
    type: 'number',
    default: 1,
  })
  quantity: number;

  @property({
    type: 'number',
  })
  itemTemplateId: number;

  @property({
    type: 'string',
  })
  name: string;

  @property({
    type: 'number',
  })
  cost: number;

  @property({
    type: 'number',
  })
  studentId: number;

  constructor(data?: Partial<ItemUsable>) {
    super(data);
  }
}

export interface ItemUsableRelations {
  // describe navigational properties here
}

export type ItemUsableWithRelations = ItemUsable & ItemUsableRelations;
