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
import {Adress} from '../models';
import {AdressRepository} from '../repositories';

export class AddressController {
  constructor(
    @repository(AdressRepository)
    public adressRepository : AdressRepository,
  ) {}

  @post('/adresses')
  @response(200, {
    description: 'Adress model instance',
    content: {'application/json': {schema: getModelSchemaRef(Adress)}},
  })
  async create(
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Adress, {
            title: 'NewAdress',
            exclude: ['id'],
          }),
        },
      },
    })
    adress: Omit<Adress, 'id'>,
  ): Promise<Adress> {
    return this.adressRepository.create(adress);
  }

  @get('/adresses/count')
  @response(200, {
    description: 'Adress model count',
    content: {'application/json': {schema: CountSchema}},
  })
  async count(
    @param.where(Adress) where?: Where<Adress>,
  ): Promise<Count> {
    return this.adressRepository.count(where);
  }

  @get('/adresses')
  @response(200, {
    description: 'Array of Adress model instances',
    content: {
      'application/json': {
        schema: {
          type: 'array',
          items: getModelSchemaRef(Adress, {includeRelations: true}),
        },
      },
    },
  })
  async find(
    @param.filter(Adress) filter?: Filter<Adress>,
  ): Promise<Adress[]> {
    return this.adressRepository.find(filter);
  }

  @patch('/adresses')
  @response(200, {
    description: 'Adress PATCH success count',
    content: {'application/json': {schema: CountSchema}},
  })
  async updateAll(
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Adress, {partial: true}),
        },
      },
    })
    adress: Adress,
    @param.where(Adress) where?: Where<Adress>,
  ): Promise<Count> {
    return this.adressRepository.updateAll(adress, where);
  }

  @get('/adresses/{id}')
  @response(200, {
    description: 'Adress model instance',
    content: {
      'application/json': {
        schema: getModelSchemaRef(Adress, {includeRelations: true}),
      },
    },
  })
  async findById(
    @param.path.number('id') id: number,
    @param.filter(Adress, {exclude: 'where'}) filter?: FilterExcludingWhere<Adress>
  ): Promise<Adress> {
    return this.adressRepository.findById(id, filter);
  }

  @patch('/adresses/{id}')
  @response(204, {
    description: 'Adress PATCH success',
  })
  async updateById(
    @param.path.number('id') id: number,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Adress, {partial: true}),
        },
      },
    })
    adress: Adress,
  ): Promise<void> {
    await this.adressRepository.updateById(id, adress);
  }

  @put('/adresses/{id}')
  @response(204, {
    description: 'Adress PUT success',
  })
  async replaceById(
    @param.path.number('id') id: number,
    @requestBody() adress: Adress,
  ): Promise<void> {
    await this.adressRepository.replaceById(id, adress);
  }

  @del('/adresses/{id}')
  @response(204, {
    description: 'Adress DELETE success',
  })
  async deleteById(@param.path.number('id') id: number): Promise<void> {
    await this.adressRepository.deleteById(id);
  }
}
