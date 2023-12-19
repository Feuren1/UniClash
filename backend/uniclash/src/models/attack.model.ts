import {Entity, belongsTo, hasMany, model, property} from '@loopback/repository';
import {CritterAttack} from '.';
import {Type} from './type.model';

@model()
export class Attack extends Entity {
  @property({
    type: 'number',
    id: true,
    generated: true,
  })
  id?: number;

  @property({
    type: 'string',
  })
  name?: string;

  @property({
    type: 'number',
    default: 100,
  })
  strength?: number;

  @property({
    type: 'string',
  })
  attackType?: string;

  @hasMany(() => CritterAttack)
  critterAttacks: CritterAttack[];

  @belongsTo(() => Type)
  typeId: string;

  constructor(data?: Partial<Attack>) {
    super(data);
  }
}

export interface AttackRelations {
  // describe navigational properties here
}

export type AttackWithRelations = Attack & AttackRelations;
