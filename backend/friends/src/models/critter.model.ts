import {Entity, model, property} from '@loopback/repository';

@model({settings: {strict: false}})
export class Critter extends Entity {
  @property({
    type: 'number',
    id: true,
    generated: true,
  })
  id?: number;

  @property({
    type: 'number',
    required: true,
  })
  hp: number;

  @property({
    type: 'number',
    required: true,
  })
  atk: number;

  @property({
    type: 'number',
    required: true,
  })
  def: number;

  @property({
    type: 'number',
    required: true,
  })
  spd: number;

  @property({
    type: 'string',
    required: true,
  })
  name: string;

  // Define well-known properties here

  // Indexer property to allow additional data
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  [prop: string]: any;

  constructor(data?: Partial<Critter>) {
    super(data);
  }
}

export interface CritterRelations {
  // describe navigational properties here
}

export type CritterWithRelations = Critter & CritterRelations;
