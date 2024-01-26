import {Entity, belongsTo, hasMany, model, property} from '@loopback/repository';
import {Arena} from './arena.model';
import {Critter} from './critter.model';
import {Item} from './item.model';
import {User} from './user.model';

@model()
export class StudentLocation extends Entity {
  @property({
    type: 'number',
    id: true,
    generated: true,
  })
  id: number;

  @property({
    type: 'string',
  })
  name: string;

  @property({
    type: 'number',
  })
  level?: number;

  @property({
    type: 'string',
    default: "0.0",
  })
  lat: string;

  @property({
    type: 'string',
    default: "0.0",
  })
  lon: string;

  constructor(data?: Partial<StudentLocation>) {
    super(data);
  }
}

export interface StudentRelations {
  // describe navigational properties here
}

