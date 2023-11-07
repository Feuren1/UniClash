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
import {Critter} from '../models';
import {CritterRepository} from '../repositories';
import {authenticate} from '@loopback/authentication';
//@authenticate('jwt')
export class CritterController {
  constructor(
    @repository(CritterRepository)
    public critterRepository : CritterRepository,
  ) {}

  @post('/critters')
  @response(200, {
    description: 'Critter model instance',
    content: {'application/json': {schema: getModelSchemaRef(Critter)}},
  })
  async create(
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Critter, {
            title: 'NewCritter',
            exclude: ['id'],
          }),
        },
      },
    })
    critter: Omit<Critter, 'id'>,
  ): Promise<Critter> {
    return this.critterRepository.create(critter);
  }

  @get('/critters/count')
  @response(200, {
    description: 'Critter model count',
    content: {'application/json': {schema: CountSchema}},
  })
  async count(
    @param.where(Critter) where?: Where<Critter>,
  ): Promise<Count> {
    return this.critterRepository.count(where);
  }

  @get('/critters')
  @response(200, {
    description: 'Array of Critter model instances',
    content: {
      'application/json': {
        schema: {
          type: 'array',
          items: getModelSchemaRef(Critter, {includeRelations: true}),
        },
      },
    },
  })
  async find(
    @param.filter(Critter) filter?: Filter<Critter>,
  ): Promise<Critter[]> {
    return this.critterRepository.find(filter);
  }

  @patch('/critters')
  @response(200, {
    description: 'Critter PATCH success count',
    content: {'application/json': {schema: CountSchema}},
  })
  async updateAll(
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Critter, {partial: true}),
        },
      },
    })
    critter: Critter,
    @param.where(Critter) where?: Where<Critter>,
  ): Promise<Count> {
    return this.critterRepository.updateAll(critter, where);
  }

  @get('/critters/{id}')
  @response(200, {
    description: 'Critter model instance',
    content: {
      'application/json': {
        schema: getModelSchemaRef(Critter, {includeRelations: true}),
      },
    },
  })
  async findById(
    @param.path.number('id') id: number,
    @param.filter(Critter, {exclude: 'where'}) filter?: FilterExcludingWhere<Critter>
  ): Promise<Critter> {
    return this.critterRepository.findById(id, filter);
  }

  @patch('/critters/{id}')
  @response(204, {
    description: 'Critter PATCH success',
  })
  async updateById(
    @param.path.number('id') id: number,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Critter, {partial: true}),
        },
      },
    })
    critter: Critter,
  ): Promise<void> {
    await this.critterRepository.updateById(id, critter);
  }

  @put('/critters/{id}')
  @response(204, {
    description: 'Critter PUT success',
  })
  async replaceById(
    @param.path.number('id') id: number,
    @requestBody() critter: Critter,
  ): Promise<void> {
    await this.critterRepository.replaceById(id, critter);
  }

  @del('/critters/{id}')
  @response(204, {
    description: 'Critter DELETE success',
  })
  async deleteById(@param.path.number('id') id: number): Promise<void> {
    await this.critterRepository.deleteById(id);
  }
}
