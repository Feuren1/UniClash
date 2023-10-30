import {Entity, model, property, belongsTo} from '@loopback/repository';
import {Friend} from './friend.model';

@model()
export class Adress extends Entity {
  @property({
    type: 'number',
    id: true,
    generated: true,
  })
  id?: number;

  @property({
    type: 'string',
    required: true,
  })
  city: string;

  @property({
    type: 'string',
    required: true,
  })
  postcode: string;

  @property({
    type: 'string',
    required: true,
  })
  street: string;

  @property({
    type: 'number',
    required: true,
  })
  number: number;

  @belongsTo(() => Friend)
  friendId: number;

  constructor(data?: Partial<Adress>) {
    super(data);
  }
}

export interface AdressRelations {
  // describe navigational properties here
}

export type AdressWithRelations = Adress & AdressRelations;
