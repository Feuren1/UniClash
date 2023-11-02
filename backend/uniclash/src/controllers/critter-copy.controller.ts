import {
  Count,
  CountSchema,
  Filter,
  FilterExcludingWhere,
  repository,
  Where,
} from '@loopback/repository';
import {
  post,
  param,
  get,
  getModelSchemaRef,
  patch,
  put,
  del,
  requestBody,
  response,
} from '@loopback/rest';
import {CritterCopy} from '../models';
import {CritterCopyRepository} from '../repositories';

export class CritterCopyController {
  constructor(
    @repository(CritterCopyRepository)
    public critterCopyRepository : CritterCopyRepository,
  ) {}

  @post('/critter-copies')
  @response(200, {
    description: 'CritterCopy model instance',
    content: {'application/json': {schema: getModelSchemaRef(CritterCopy)}},
  })
  async create(
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(CritterCopy, {
            title: 'NewCritterCopy',
            exclude: ['id'],
          }),
        },
      },
    })
    critterCopy: Omit<CritterCopy, 'id'>,
  ): Promise<CritterCopy> {
    return this.critterCopyRepository.create(critterCopy);
  }

  @get('/critter-copies/count')
  @response(200, {
    description: 'CritterCopy model count',
    content: {'application/json': {schema: CountSchema}},
  })
  async count(
    @param.where(CritterCopy) where?: Where<CritterCopy>,
  ): Promise<Count> {
    return this.critterCopyRepository.count(where);
  }

  @get('/critter-copies')
  @response(200, {
    description: 'Array of CritterCopy model instances',
    content: {
      'application/json': {
        schema: {
          type: 'array',
          items: getModelSchemaRef(CritterCopy, {includeRelations: true}),
        },
      },
    },
  })
  async find(
    @param.filter(CritterCopy) filter?: Filter<CritterCopy>,
  ): Promise<CritterCopy[]> {
    return this.critterCopyRepository.find(filter);
  }

  @patch('/critter-copies')
  @response(200, {
    description: 'CritterCopy PATCH success count',
    content: {'application/json': {schema: CountSchema}},
  })
  async updateAll(
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(CritterCopy, {partial: true}),
        },
      },
    })
    critterCopy: CritterCopy,
    @param.where(CritterCopy) where?: Where<CritterCopy>,
  ): Promise<Count> {
    return this.critterCopyRepository.updateAll(critterCopy, where);
  }

  @get('/critter-copies/{id}')
  @response(200, {
    description: 'CritterCopy model instance',
    content: {
      'application/json': {
        schema: getModelSchemaRef(CritterCopy, {includeRelations: true}),
      },
    },
  })
  async findById(
    @param.path.number('id') id: number,
    @param.filter(CritterCopy, {exclude: 'where'}) filter?: FilterExcludingWhere<CritterCopy>
  ): Promise<CritterCopy> {
    return this.critterCopyRepository.findById(id, filter);
  }

  @patch('/critter-copies/{id}')
  @response(204, {
    description: 'CritterCopy PATCH success',
  })
  async updateById(
    @param.path.number('id') id: number,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(CritterCopy, {partial: true}),
        },
      },
    })
    critterCopy: CritterCopy,
  ): Promise<void> {
    await this.critterCopyRepository.updateById(id, critterCopy);
  }

  @put('/critter-copies/{id}')
  @response(204, {
    description: 'CritterCopy PUT success',
  })
  async replaceById(
    @param.path.number('id') id: number,
    @requestBody() critterCopy: CritterCopy,
  ): Promise<void> {
    await this.critterCopyRepository.replaceById(id, critterCopy);
  }

  @del('/critter-copies/{id}')
  @response(204, {
    description: 'CritterCopy DELETE success',
  })
  async deleteById(@param.path.number('id') id: number): Promise<void> {
    await this.critterCopyRepository.deleteById(id);
  }
}
