import {Entity, belongsTo, model, property} from '@loopback/repository';
import {Attack, CritterCopy} from '.';

@model()
export class CritterCopyAttack extends Entity {
  @property({
    type: 'number',
    id: true,
    generated: true,
  })
  id?: number;

  @belongsTo(() => CritterCopy)
  critterCopyId: number;

  @belongsTo(() => Attack)
  attackId: number;

  constructor(data?: Partial<CritterCopyAttack>) {
    super(data);
  }
}

export interface CritterCopyAttackRelations {
  // describe navigational properties here
}

export type CritterCopyAttackWithRelations = CritterCopyAttack & CritterCopyAttackRelations;
