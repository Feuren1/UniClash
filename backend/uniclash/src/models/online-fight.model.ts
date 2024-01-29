import {Entity, model, property} from '@loopback/repository';

@model()
export class OnlineFight extends Entity {
  @property({
    type: 'number',
    id: true,
    generated: true,
  })
  fightId?: number;

  @property({
    type: 'number',
    required: true,
  })
  studentId: number;

  @property({
    type: 'number',
  })
  critterId?: number;

  @property({
    type: 'string',
    required: true,
  })
  state: string;

  @property({
    type: 'number',
    required: true,
  })
  time: number;


  constructor(data?: Partial<OnlineFight>) {
    super(data);
  }
}

export interface OnlineFightRelations {
  // describe navigational properties here
}

export type OnlineFightWithRelations = OnlineFight & OnlineFightRelations;
