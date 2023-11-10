import {Entity, belongsTo, hasMany, model, property} from '@loopback/repository';
import {CritterAttack} from './critter-attack.model';
import {CritterTemplate} from './critter-template.model';
import {Student} from './student.model';

@model()
export class Critter extends Entity {
  @property({
    type: 'number',
    id: true,
    generated: true,
  })
  id?: number;

  @property({
    type: 'number',
    default: 1,
  })
  level: number;

  @property({
    type: 'number',
    default: 0,
  })
  expToNextLevel?: number;

  @property({
    type: 'string',
  })
  nature?: string;

  @hasMany(() => CritterAttack)
  critterAttacks: CritterAttack[];

  @belongsTo(() => CritterTemplate)
  critterTemplateId: number;

  @belongsTo(() => Student)
  studentId: number;

  constructor(data?: Partial<Critter>) {
    super(data);
  }
}

export interface CritterRelations {
  // describe navigational properties here
}

export type CritterWithRelations = Critter & CritterRelations;
