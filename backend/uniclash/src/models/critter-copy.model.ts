import {Entity, model, property, belongsTo, hasMany} from '@loopback/repository';
import {Critter} from './critter.model';
import {Attack} from './attack.model';

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

  @property({
    type: 'string',
    default: 0,
  })
  nature?: string;

  @property({
    type: 'boolean',
    default: 0,
  })
  isWild?: boolean;

  @belongsTo(() => Critter)
  critterId: number;

  @hasMany(() => Attack)
  attacks: Attack[];

  constructor(data?: Partial<CritterCopy>) {
    super(data);
  }
}

export interface CritterCopyRelations {
  // describe navigational properties here
}

export type CritterCopyWithRelations = CritterCopy & CritterCopyRelations;
