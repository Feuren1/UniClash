import {Entity, model, property} from '@loopback/repository';

@model()
export class Trainer extends Entity {
  @property({
    type: 'number',
    id: true,
    generated: true,
  })
  id?: number;

  @property({
    type: 'number',
    default: 0,
  })
  credits?: number;


  constructor(data?: Partial<Trainer>) {
    super(data);
  }
}

export interface TrainerRelations {
  // describe navigational properties here
}

export type TrainerWithRelations = Trainer & TrainerRelations;
