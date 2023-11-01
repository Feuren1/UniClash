import {Entity, model, property} from '@loopback/repository';

@model()
export class StudentHub extends Entity {
  @property({
    type: 'number',
    id: true,
    generated: true,
  })
  id?: number;

  @property({
    type: 'string',
    default: 'StudentHub',
  })
  name?: string;

  @property({
    type: 'geopoint',
  })
  studentHubGeoLocation?: string;

  @property({
    type: 'string',
    default: 'StudentHub description',
  })
  description?: string;


  constructor(data?: Partial<StudentHub>) {
    super(data);
  }
}

export interface StudentHubRelations {
  // describe navigational properties here
}

export type StudentHubWithRelations = StudentHub & StudentHubRelations;
