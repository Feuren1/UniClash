import {Entity, model, property} from '@loopback/repository';

@model()
export class CritterInFight extends Entity {
  @property({
    type: 'number',
    id: true,
    generated: false,
    required: true,
  })
  critterId: number;

  @property({
    type: 'number',
    required: true,
  })
  health: number;

  @property({
    type: 'number',
    required: true,
  })
  attack: number;

  @property({
    type: 'number',
    required: true,
  })
  defence: number;


  constructor(data?: Partial<CritterInFight>) {
    super(data);
  }
}

export interface CritterInFightRelations {
  // describe navigational properties here
}

export type CritterInFightWithRelations = CritterInFight & CritterInFightRelations;
