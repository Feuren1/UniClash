import {Entity, belongsTo, hasMany, model, property} from '@loopback/repository';
import {Arena} from './arena.model';
import {Critter} from './critter.model';
import {Item} from './item.model';
import {User} from './user.model';

@model()
export class Student extends Entity {
  @property({
    type: 'number',
    id: true,
    generated: true,
  })
  id: number;

  @property({
    type: 'number',
  })
  level?: number;

  @property({
    type: 'string',
    default: "0.0",
  })
  lat?: string;

  @property({
    type: 'string',
    default: "0.0",
  })
  lon?: string;

  @property({
    type: 'number',
    default: 0,
  })
  time?: number;

  @property({
    type: 'number',
    default: 0,
  })
  credits?: number;

  @property({
    type: 'number',
    default: 0,
  })
  expToNextLevel?: number;

  @property({
    type: 'number',
    default: 0,
  })
  placedBuildings: number;

  @hasMany(() => Critter)
  critters: Critter[];

  @hasMany(() => Arena)
  arenas: Arena[];

  @hasMany(() => Item)
  items: Item[];
  @belongsTo(() => User)
  userId: string;

  constructor(data?: Partial<Student>) {
    super(data);
  }
}

export interface StudentRelations {
  // describe navigational properties here
}

export type StudentWithRelations = Student & StudentRelations;
