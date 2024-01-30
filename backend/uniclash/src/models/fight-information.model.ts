import {Entity, model, property} from '@loopback/repository';

@model()
export class FightInformation extends Entity {
  @property({
    type: 'number',
  })
  fightConnectionId: number;

  @property({
    type: 'number',
    required: true,
  })
  studentId: number;

  @property({
    type: 'string',
    required: true,
  })
  userName: string;

  constructor(data?: Partial<FightInformation>) {
    super(data);
  }
}

export interface FightInformationRelations {
  // describe navigational properties here
}

export type OnlineFightWithRelations = FightInformation & FightInformationRelations;
