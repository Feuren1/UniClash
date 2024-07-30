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
  Attack,
  CritterAttack,
} from '../models';
import {AttackRepository} from '../repositories';
import {authenticate} from "@loopback/authentication";

export class AttackCritterAttackController {
  constructor(
    @repository(AttackRepository) protected attackRepository: AttackRepository,
  ) { }

  @authenticate('jwt')
  @get('/attacks/{id}/critter-attacks', {
    responses: {
      '200': {
        description: 'Array of Attack has many CritterAttack',
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
    return this.attackRepository.critterAttacks(id).find(filter);
  }

  @authenticate('jwt')
  @post('/attacks/{id}/critter-attacks', {
    responses: {
      '200': {
        description: 'Attack model instance',
        content: {'application/json': {schema: getModelSchemaRef(CritterAttack)}},
      },
    },
  })
  async create(
    @param.path.number('id') id: typeof Attack.prototype.id,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(CritterAttack, {
            title: 'NewCritterAttackInAttack',
            exclude: ['id'],
            optional: ['attackId']
          }),
        },
      },
    }) critterAttack: Omit<CritterAttack, 'id'>,
  ): Promise<CritterAttack> {
    return this.attackRepository.critterAttacks(id).create(critterAttack);
  }

  @authenticate('jwt')
  @patch('/attacks/{id}/critter-attacks', {
    responses: {
      '200': {
        description: 'Attack.CritterAttack PATCH success count',
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
    return this.attackRepository.critterAttacks(id).patch(critterAttack, where);
  }

  @authenticate('jwt')
  @del('/attacks/{id}/critter-attacks', {
    responses: {
      '200': {
        description: 'Attack.CritterAttack DELETE success count',
        content: {'application/json': {schema: CountSchema}},
      },
    },
  })
  async delete(
    @param.path.number('id') id: number,
    @param.query.object('where', getWhereSchemaFor(CritterAttack)) where?: Where<CritterAttack>,
  ): Promise<Count> {
    return this.attackRepository.critterAttacks(id).delete(where);
  }
}
