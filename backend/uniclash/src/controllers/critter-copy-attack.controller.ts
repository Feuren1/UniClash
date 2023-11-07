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
  CritterCopy,
  Attack,
} from '../models';
import {CritterCopyRepository} from '../repositories';

export class CritterCopyAttackController {
  constructor(
    @repository(CritterCopyRepository) protected critterCopyRepository: CritterCopyRepository,
  ) { }

  @get('/critter-copies/{id}/attacks', {
    responses: {
      '200': {
        description: 'Array of CritterCopy has many Attack',
        content: {
          'application/json': {
            schema: {type: 'array', items: getModelSchemaRef(Attack)},
          },
        },
      },
    },
  })
  async find(
    @param.path.number('id') id: number,
    @param.query.object('filter') filter?: Filter<Attack>,
  ): Promise<Attack[]> {
    return this.critterCopyRepository.attacks(id).find(filter);
  }

  @post('/critter-copies/{id}/attacks', {
    responses: {
      '200': {
        description: 'CritterCopy model instance',
        content: {'application/json': {schema: getModelSchemaRef(Attack)}},
      },
    },
  })
  async create(
    @param.path.number('id') id: typeof CritterCopy.prototype.id,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Attack, {
            title: 'NewAttackInCritterCopy',
            exclude: ['id'],
          }),
        },
      },
    }) attack: Omit<Attack, 'id'>,
  ): Promise<Attack> {
    return this.critterCopyRepository.attacks(id).create(attack);
  }

  @patch('/critter-copies/{id}/attacks', {
    responses: {
      '200': {
        description: 'CritterCopy.Attack PATCH success count',
        content: {'application/json': {schema: CountSchema}},
      },
    },
  })
  async patch(
    @param.path.number('id') id: number,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Attack, {partial: true}),
        },
      },
    })
    attack: Partial<Attack>,
    @param.query.object('where', getWhereSchemaFor(Attack)) where?: Where<Attack>,
  ): Promise<Count> {
    return this.critterCopyRepository.attacks(id).patch(attack, where);
  }

  @del('/critter-copies/{id}/attacks', {
    responses: {
      '200': {
        description: 'CritterCopy.Attack DELETE success count',
        content: {'application/json': {schema: CountSchema}},
      },
    },
  })
  async delete(
    @param.path.number('id') id: number,
    @param.query.object('where', getWhereSchemaFor(Attack)) where?: Where<Attack>,
  ): Promise<Count> {
    return this.critterCopyRepository.attacks(id).delete(where);
  }
}
