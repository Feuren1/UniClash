import {Entity, model, property, belongsTo} from '@loopback/repository';
import {Attack} from './attack.model';
import {Critter} from './critter.model';

@model()
export class CritterAttack extends Entity {
  @property({
    type: 'number',
    id: true,
    generated: true,
  })
  id?: number;

  @belongsTo(() => Attack)
  attackId: number;

  @belongsTo(() => Critter)
  critterId: number;

  constructor(data?: Partial<CritterAttack>) {
    super(data);
  }
}

export interface CritterAttackRelations {
  // describe navigational properties here
}

export type CritterAttackWithRelations = CritterAttack & CritterAttackRelations;
