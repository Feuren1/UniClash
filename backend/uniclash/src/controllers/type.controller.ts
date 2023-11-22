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
import {Type} from '../models';
import {TypeRepository} from '../repositories';

export class TypeController {
  constructor(
    @repository(TypeRepository)
    public typeRepository : TypeRepository,
  ) {}

  @post('/types')
  @response(200, {
    description: 'Type model instance',
    content: {'application/json': {schema: getModelSchemaRef(Type)}},
  })
  async create(
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Type, {
            title: 'NewType',
            
          }),
        },
      },
    })
    type: Type,
  ): Promise<Type> {
    return this.typeRepository.create(type);
  }

  @get('/types/count')
  @response(200, {
    description: 'Type model count',
    content: {'application/json': {schema: CountSchema}},
  })
  async count(
    @param.where(Type) where?: Where<Type>,
  ): Promise<Count> {
    return this.typeRepository.count(where);
  }

  @get('/types')
  @response(200, {
    description: 'Array of Type model instances',
    content: {
      'application/json': {
        schema: {
          type: 'array',
          items: getModelSchemaRef(Type, {includeRelations: true}),
        },
      },
    },
  })
  async find(
    @param.filter(Type) filter?: Filter<Type>,
  ): Promise<Type[]> {
    return this.typeRepository.find(filter);
  }

  @patch('/types')
  @response(200, {
    description: 'Type PATCH success count',
    content: {'application/json': {schema: CountSchema}},
  })
  async updateAll(
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Type, {partial: true}),
        },
      },
    })
    type: Type,
    @param.where(Type) where?: Where<Type>,
  ): Promise<Count> {
    return this.typeRepository.updateAll(type, where);
  }

  @get('/types/{id}')
  @response(200, {
    description: 'Type model instance',
    content: {
      'application/json': {
        schema: getModelSchemaRef(Type, {includeRelations: true}),
      },
    },
  })
  async findById(
    @param.path.string('id') id: string,
    @param.filter(Type, {exclude: 'where'}) filter?: FilterExcludingWhere<Type>
  ): Promise<Type> {
    return this.typeRepository.findById(id, filter);
  }

  @patch('/types/{id}')
  @response(204, {
    description: 'Type PATCH success',
  })
  async updateById(
    @param.path.string('id') id: string,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Type, {partial: true}),
        },
      },
    })
    type: Type,
  ): Promise<void> {
    await this.typeRepository.updateById(id, type);
  }

  @put('/types/{id}')
  @response(204, {
    description: 'Type PUT success',
  })
  async replaceById(
    @param.path.string('id') id: string,
    @requestBody() type: Type,
  ): Promise<void> {
    await this.typeRepository.replaceById(id, type);
  }

  @del('/types/{id}')
  @response(204, {
    description: 'Type DELETE success',
  })
  async deleteById(@param.path.string('id') id: string): Promise<void> {
    await this.typeRepository.deleteById(id);
  }
}
