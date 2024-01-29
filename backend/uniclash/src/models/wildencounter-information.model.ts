import {Entity, model, property} from '@loopback/repository';

@model()
export class WildencounterInformation extends Entity {
  @property({
    type: 'string',
    id: true,
    generated: false,
    required: true,
  })
  date: string;


  constructor(data?: Partial<WildencounterInformation>) {
    super(data);
  }
}

export interface WildencounterInformationRelations {
  // describe navigational properties here
}

export type WildencounterInformationWithRelations = WildencounterInformation & WildencounterInformationRelations;
