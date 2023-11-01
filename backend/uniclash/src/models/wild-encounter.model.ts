import {Entity, model, property} from '@loopback/repository';

@model()
export class WildEncounter extends Entity {
  @property({
    type: 'number',
    id: true,
    generated: true,
  })
  id?: number;
  
  @property({
    type: 'number',
    default: 10,
  })
  expOnSucess?: number;

  @property({
    type: 'geopoint',
  })
  arenaGeoLocation?: string;

  constructor(data?: Partial<WildEncounter>) {
    super(data);
  }
}

export interface WildEncounterRelations {
  // describe navigational properties here
}

export type WildEncounterWithRelations = WildEncounter & WildEncounterRelations;
