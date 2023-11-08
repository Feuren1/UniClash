import {Model, model, property} from '@loopback/repository';
import {Attack} from '.';

@model()
export class CritterUsable extends Model {
  @property({
    type: 'number',
    default: 1,
  })
  level: number;

  @property({
    type: 'string',
  })
  name: string;

  @property({
    type: 'number',
    default: 1,
  })
  hp: number;

  @property({
    type: 'number',
    default: 1,
  })
  atk: number;

  @property({
    type: 'number',
    default: 1,
  })
  def: number;

  @property({
    type: 'number',
    default: 1,
  })
  spd: number;

  @property.array(Attack)
  attacks: Attack[]; // Store Attack model instances directly

  constructor(data?: Partial<CritterUsable>) {
    super(data);
  }
}

export interface CritterUsableRelations {
  // describe navigational properties here
}

export type CritterUsableWithRelations = CritterUsable & CritterUsableRelations;
