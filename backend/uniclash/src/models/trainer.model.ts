import {Entity, model, property, hasMany} from '@loopback/repository';
import {CritterCopy} from './critter-copy.model';

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
  @property({
    type: 'number',
  })
  lat?: number;

  @property({
    type: 'number',
  })
  lon?: number;

  @hasMany(() => CritterCopy)
  critterCopies: CritterCopy[];

  constructor(data?: Partial<Trainer>) {
    super(data);
  }
}

export interface TrainerRelations {
  // describe navigational properties here
}

export type TrainerWithRelations = Trainer & TrainerRelations;
