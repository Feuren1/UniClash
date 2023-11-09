import {Entity, hasMany, model, property} from '@loopback/repository';
import {CritterCopyAttack} from '.';

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

  @hasMany(() => CritterCopyAttack)
  critterCopyAttacks: CritterCopyAttack[];

  constructor(data?: Partial<Attack>) {
    super(data);
  }
}

export interface AttackRelations {
  // describe navigational properties here
}

export type AttackWithRelations = Attack & AttackRelations;
