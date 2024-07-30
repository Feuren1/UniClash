import {Entity, model, property} from '@loopback/repository';

@model()
export class CritterInFightInformation extends Entity {
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

  @property({
    type: 'String',
    required: true,
  })
  name: String;


  constructor(data?: Partial<CritterInFightInformation>) {
    super(data);
  }
}

export interface CritterInFightInformationRelations {
  // describe navigational properties here
}

export type CritterInFightWithRelations = CritterInFightInformation & CritterInFightInformationRelations;
