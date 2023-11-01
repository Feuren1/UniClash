import {Entity, model, property} from '@loopback/repository';

@model()
export class Type extends Entity {
  @property({
    type: 'string',
    id: true,
    generated: false,
  })
  name?: string;

  constructor(data?: Partial<Type>) {
    super(data);
  }
}

export interface TypeRelations {
  // describe navigational properties here
}

export type TypeWithRelations = Type & TypeRelations;
