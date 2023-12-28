import {
  Count,
  CountSchema,
  Filter,
  repository,
  Where,
} from '@loopback/repository';
import {
  del,
  get,
  getModelSchemaRef,
  getWhereSchemaFor,
  param,
  patch,
  post,
  requestBody,
} from '@loopback/rest';
import {
  Critter,
  CritterAttack,
} from '../models';
import {CritterRepository} from '../repositories';
import {authenticate} from "@loopback/authentication";

export class CritterCritterAttackController {
  constructor(
    @repository(CritterRepository) protected critterRepository: CritterRepository,
  ) { }

  @authenticate('jwt')
  @get('/critters/{id}/critter-attacks', {
    responses: {
      '200': {
        description: 'Array of Critter has many CritterAttack',
        content: {
          'application/json': {
            schema: {type: 'array', items: getModelSchemaRef(CritterAttack)},
          },
        },
      },
    },
  })
  async find(
    @param.path.number('id') id: number,
    @param.query.object('filter') filter?: Filter<CritterAttack>,
  ): Promise<CritterAttack[]> {
    return this.critterRepository.critterAttacks(id).find(filter);
  }

  @authenticate('jwt')
  @post('/critters/{id}/critter-attacks', {
    responses: {
      '200': {
        description: 'Critter model instance',
        content: {'application/json': {schema: getModelSchemaRef(CritterAttack)}},
      },
    },
  })
  async create(
    @param.path.number('id') id: typeof Critter.prototype.id,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(CritterAttack, {
            title: 'NewCritterAttackInCritter',
            exclude: ['id'],
            optional: ['critterId']
          }),
        },
      },
    }) critterAttack: Omit<CritterAttack, 'id'>,
  ): Promise<CritterAttack> {
    return this.critterRepository.critterAttacks(id).create(critterAttack);
  }

  @authenticate('jwt')
  @patch('/critters/{id}/critter-attacks', {
    responses: {
      '200': {
        description: 'Critter.CritterAttack PATCH success count',
        content: {'application/json': {schema: CountSchema}},
      },
    },
  })
  async patch(
    @param.path.number('id') id: number,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(CritterAttack, {partial: true}),
        },
      },
    })
    critterAttack: Partial<CritterAttack>,
    @param.query.object('where', getWhereSchemaFor(CritterAttack)) where?: Where<CritterAttack>,
  ): Promise<Count> {
    return this.critterRepository.critterAttacks(id).patch(critterAttack, where);
  }

  @authenticate('jwt')
  @del('/critters/{id}/critter-attacks', {
    responses: {
      '200': {
        description: 'Critter.CritterAttack DELETE success count',
        content: {'application/json': {schema: CountSchema}},
      },
    },
  })
  async delete(
    @param.path.number('id') id: number,
    @param.query.object('where', getWhereSchemaFor(CritterAttack)) where?: Where<CritterAttack>,
  ): Promise<Count> {
    return this.critterRepository.critterAttacks(id).delete(where);
  }
}
