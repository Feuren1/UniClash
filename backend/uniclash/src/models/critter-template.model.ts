import {Entity, hasMany, hasOne, model, property} from '@loopback/repository';
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

  @property({
    type: 'number',
  })
  evolesAt: number;

  @property({
    type: 'string',
  })
  type: string;

  @hasMany(() => Critter)
  critters: Critter[];

  @hasOne(() => CritterTemplate, {keyTo: 'evolvesIntoTemplateId'})
  evolvesInto: CritterTemplate;

  @property({
    type: 'number',
  })
  evolvesIntoTemplateId?: number;

  constructor(data?: Partial<CritterTemplate>) {
    super(data);
  }
}

export interface CritterTemplateRelations {
  // describe navigational properties here
}

export type CritterTemplateWithRelations = CritterTemplate & CritterTemplateRelations;
