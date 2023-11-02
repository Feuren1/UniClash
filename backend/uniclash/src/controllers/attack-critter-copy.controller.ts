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
  CritterCopy,
} from '../models';
import {AttackRepository} from '../repositories';

export class AttackCritterCopyController {
  constructor(
    @repository(AttackRepository) protected attackRepository: AttackRepository,
  ) { }

  @get('/attacks/{id}/critter-copies', {
    responses: {
      '200': {
        description: 'Array of Attack has many CritterCopy',
        content: {
          'application/json': {
            schema: {type: 'array', items: getModelSchemaRef(CritterCopy)},
          },
        },
      },
    },
  })
  async find(
    @param.path.number('id') id: number,
    @param.query.object('filter') filter?: Filter<CritterCopy>,
  ): Promise<CritterCopy[]> {
    return this.attackRepository.critterCopies(id).find(filter);
  }

  @post('/attacks/{id}/critter-copies', {
    responses: {
      '200': {
        description: 'Attack model instance',
        content: {'application/json': {schema: getModelSchemaRef(CritterCopy)}},
      },
    },
  })
  async create(
    @param.path.number('id') id: typeof Attack.prototype.id,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(CritterCopy, {
            title: 'NewCritterCopyInAttack',
            exclude: ['id'],
            optional: ['attackId']
          }),
        },
      },
    }) critterCopy: Omit<CritterCopy, 'id'>,
  ): Promise<CritterCopy> {
    return this.attackRepository.critterCopies(id).create(critterCopy);
  }

  @patch('/attacks/{id}/critter-copies', {
    responses: {
      '200': {
        description: 'Attack.CritterCopy PATCH success count',
        content: {'application/json': {schema: CountSchema}},
      },
    },
  })
  async patch(
    @param.path.number('id') id: number,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(CritterCopy, {partial: true}),
        },
      },
    })
    critterCopy: Partial<CritterCopy>,
    @param.query.object('where', getWhereSchemaFor(CritterCopy)) where?: Where<CritterCopy>,
  ): Promise<Count> {
    return this.attackRepository.critterCopies(id).patch(critterCopy, where);
  }

  @del('/attacks/{id}/critter-copies', {
    responses: {
      '200': {
        description: 'Attack.CritterCopy DELETE success count',
        content: {'application/json': {schema: CountSchema}},
      },
    },
  })
  async delete(
    @param.path.number('id') id: number,
    @param.query.object('where', getWhereSchemaFor(CritterCopy)) where?: Where<CritterCopy>,
  ): Promise<Count> {
    return this.attackRepository.critterCopies(id).delete(where);
  }

}
