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
  CritterCopy,
} from '../models';
import {CritterRepository} from '../repositories';
import {authenticate} from '@loopback/authentication';
@authenticate('jwt')
export class CritterCritterCopyController {
  constructor(
    @repository(CritterRepository) protected critterRepository: CritterRepository,
  ) { }

  @get('/critters/{id}/critter-copies', {
    responses: {
      '200': {
        description: 'Array of Critter has many CritterCopy',
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
    return this.critterRepository.critterCopies(id).find(filter);
  }

  @post('/critters/{id}/critter-copies', {
    responses: {
      '200': {
        description: 'Critter model instance',
        content: {'application/json': {schema: getModelSchemaRef(CritterCopy)}},
      },
    },
  })
  async create(
    @param.path.number('id') id: typeof Critter.prototype.id,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(CritterCopy, {
            title: 'NewCritterCopyInCritter',
            exclude: ['id'],
            optional: ['critterId']
          }),
        },
      },
    }) critterCopy: Omit<CritterCopy, 'id'>,
  ): Promise<CritterCopy> {
    return this.critterRepository.critterCopies(id).create(critterCopy);
  }

  @patch('/critters/{id}/critter-copies', {
    responses: {
      '200': {
        description: 'Critter.CritterCopy PATCH success count',
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
    return this.critterRepository.critterCopies(id).patch(critterCopy, where);
  }

  @del('/critters/{id}/critter-copies', {
    responses: {
      '200': {
        description: 'Critter.CritterCopy DELETE success count',
        content: {'application/json': {schema: CountSchema}},
      },
    },
  })
  async delete(
    @param.path.number('id') id: number,
    @param.query.object('where', getWhereSchemaFor(CritterCopy)) where?: Where<CritterCopy>,
  ): Promise<Count> {
    return this.critterRepository.critterCopies(id).delete(where);
  }
}
