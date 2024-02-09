import {Model, model, property} from '@loopback/repository';
import {Attack} from '.';

@model()
export class CritterListable extends Model {
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
  })
  critterId: number;

  @property({
    type: 'string',
  })
  type: string;

  constructor(data?: Partial<CritterListable>) {
    super(data);
  }
}

export interface CritterUsableRelations {
  // describe navigational properties here
}

