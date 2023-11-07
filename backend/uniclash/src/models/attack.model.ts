import {Entity, model, property, hasMany} from '@loopback/repository';
import {CritterCopy} from './critter-copy.model';

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
    type: 'number',
  })
  critterCopyId?: number;

  @hasMany(() => CritterCopy)
  critterCopies: CritterCopy[];

  constructor(data?: Partial<Attack>) {
    super(data);
  }
}

export interface AttackRelations {
  // describe navigational properties here
}

export type AttackWithRelations = Attack & AttackRelations;
