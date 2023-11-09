import {Entity, belongsTo, hasMany, model, property} from '@loopback/repository';
import {CritterCopyAttack} from '.';
import {Critter} from './critter.model';
import {Trainer} from './trainer.model';

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
    required: true,
  })
  level: number;

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

  @hasMany(() => CritterCopyAttack)
  critterCopyAttacks: CritterCopyAttack[];

  @belongsTo(() => Trainer)
  trainerId: number;

  constructor(data?: Partial<CritterCopy>) {
    super(data);
  }
}

export interface CritterCopyRelations {
  // describe navigational properties here
}

export type CritterCopyWithRelations = CritterCopy & CritterCopyRelations;
