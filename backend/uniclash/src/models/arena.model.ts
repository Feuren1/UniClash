import {Entity, model, property} from '@loopback/repository';

@model()
export class Arena extends Entity {
  @property({
    type: 'number',
    id: true,
    generated: true,
  })
  id?: number;

  @property({
    type: 'string',
    default: 'Arena',
  })
  name?: string;

  @property({
    type: 'string',
    default: 'Arena description',
  })
  description?: string;

  @property({
    type: 'geopoint',
  })
  arenaGeoLocation?: string;

  constructor(data?: Partial<Arena>) {
    super(data);
  }
}

export interface ArenaRelations {
  // describe navigational properties here
}

export type ArenaWithRelations = Arena & ArenaRelations;
