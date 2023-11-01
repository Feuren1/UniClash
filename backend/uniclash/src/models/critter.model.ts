import {Entity, model, property, hasMany} from '@loopback/repository';
import {CritterCopy} from './critter-copy.model';

@model()
export class Critter extends Entity {
  @property({
    type: 'number',
    id: true,
    generated: true,
  })
  id?: number;

  @property({
    type: 'string',
  })
  name: string;

  @property({
    type: 'number',
  })
  baseHealth: number;

  @property({
    type: 'number',
  })
  baseSpeed: number;

  @property({
    type: 'number',
  })
  baseAttack: number;

  @property({
    type: 'number',
  })
  baseDefend: number;

  @hasMany(() => CritterCopy)
  critterCopies: CritterCopy[];

  constructor(data?: Partial<Critter>) {
    super(data);
  }
}

export interface CritterRelations {
  // describe navigational properties here
}

export type CritterWithRelations = Critter & CritterRelations;
