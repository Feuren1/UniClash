import {Entity, hasMany, model, property} from '@loopback/repository';
import {Critter} from './critter.model';

@model()
export class CritterTemplate extends Entity {
  @property({
    type: 'number',
    id: true,
    generated: true,
  })
  id?: number;

  @property({
    type: 'string',
    default: 10,
  })
  name?: string;

  @property({
    type: 'number',
    default: 20,
  })
  baseHealth: number;

  @property({
    type: 'number',
    default: 15,
  })
  baseAttack: number;

  @property({
    type: 'number',
    default: 15,
  })
  baseDefence: number;

  @property({
    type: 'number',
    default: 10,
  })
  baseSpeed: number;

  @hasMany(() => Critter)
  critters: Critter[];

  constructor(data?: Partial<CritterTemplate>) {
    super(data);
  }
}

export interface CritterTemplateRelations {
  // describe navigational properties here
}

export type CritterTemplateWithRelations = CritterTemplate & CritterTemplateRelations;
