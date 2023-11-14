import {Entity, hasMany, model, property} from '@loopback/repository';
import {Critter} from './critter.model';

@model()
export class Student extends Entity {
  @property({
    type: 'number',
    id: true,
    generated: true,
  })
  id?: number;

  @property({
    type: 'number',
  })
  level?: number;

  @property({
    type: 'number',
  })
  lat?: number;

  @property({
    type: 'number',
  })
  lon?: number;

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

  @hasMany(() => Critter)
  critters: Critter[];

  constructor(data?: Partial<Student>) {
    super(data);
  }
}

export interface StudentRelations {
  // describe navigational properties here
}

export type StudentWithRelations = Student & StudentRelations;
