import {Entity, model, property, belongsTo} from '@loopback/repository';
import {Critter} from './critter.model';

@model()
export class CritterCopy extends Entity {
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
  level?: number;

  @property({
    type: 'number',
    default: 0,
  })
  expToNextLevel?: number;

  @belongsTo(() => Critter)
  critterId: number;

  constructor(data?: Partial<CritterCopy>) {
    super(data);
  }
}

export interface CritterCopyRelations {
  // describe navigational properties here
}

export type CritterCopyWithRelations = CritterCopy & CritterCopyRelations;
