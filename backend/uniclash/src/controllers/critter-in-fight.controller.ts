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
import {CritterInFight} from '../models';
import {CritterInFightRepository} from '../repositories';

export class CritterInFightController {
  constructor(
    @repository(CritterInFightRepository)
    public critterInFightRepository : CritterInFightRepository,
  ) {}

  @post('/critter-in-fights')
  @response(200, {
    description: 'CritterInFight model instance',
    content: {'application/json': {schema: getModelSchemaRef(CritterInFight)}},
  })
  async create(
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(CritterInFight, {
            title: 'NewCritterInFight',
            exclude: ['critterId'],
          }),
        },
      },
    })
    critterInFight: Omit<CritterInFight, 'id'>,
  ): Promise<CritterInFight> {
    return this.critterInFightRepository.create(critterInFight);
  }

  @get('/critter-in-fights/count')
  @response(200, {
    description: 'CritterInFight model count',
    content: {'application/json': {schema: CountSchema}},
  })
  async count(
    @param.where(CritterInFight) where?: Where<CritterInFight>,
  ): Promise<Count> {
    return this.critterInFightRepository.count(where);
  }

  @get('/critter-in-fights')
  @response(200, {
    description: 'Array of CritterInFight model instances',
    content: {
      'application/json': {
        schema: {
          type: 'array',
          items: getModelSchemaRef(CritterInFight, {includeRelations: true}),
        },
      },
    },
  })
  async find(
    @param.filter(CritterInFight) filter?: Filter<CritterInFight>,
  ): Promise<CritterInFight[]> {
    return this.critterInFightRepository.find(filter);
  }

  @patch('/critter-in-fights')
  @response(200, {
    description: 'CritterInFight PATCH success count',
    content: {'application/json': {schema: CountSchema}},
  })
  async updateAll(
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(CritterInFight, {partial: true}),
        },
      },
    })
    critterInFight: CritterInFight,
    @param.where(CritterInFight) where?: Where<CritterInFight>,
  ): Promise<Count> {
    return this.critterInFightRepository.updateAll(critterInFight, where);
  }

  @get('/critter-in-fights/{id}')
  @response(200, {
    description: 'CritterInFight model instance',
    content: {
      'application/json': {
        schema: getModelSchemaRef(CritterInFight, {includeRelations: true}),
      },
    },
  })
  async findById(
    @param.path.number('id') id: number,
    @param.filter(CritterInFight, {exclude: 'where'}) filter?: FilterExcludingWhere<CritterInFight>
  ): Promise<CritterInFight> {
    return this.critterInFightRepository.findById(id, filter);
  }

  @patch('/critter-in-fights/{id}')
  @response(204, {
    description: 'CritterInFight PATCH success',
  })
  async updateById(
    @param.path.number('id') id: number,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(CritterInFight, {partial: true}),
        },
      },
    })
    critterInFight: CritterInFight,
  ): Promise<void> {
    await this.critterInFightRepository.updateById(id, critterInFight);
  }

  @put('/critter-in-fights/{id}')
  @response(204, {
    description: 'CritterInFight PUT success',
  })
  async replaceById(
    @param.path.number('id') id: number,
    @requestBody() critterInFight: CritterInFight,
  ): Promise<void> {
    await this.critterInFightRepository.replaceById(id, critterInFight);
  }

  @del('/critter-in-fights/{id}')
  @response(204, {
    description: 'CritterInFight DELETE success',
  })
  async deleteById(@param.path.number('id') id: number): Promise<void> {
    await this.critterInFightRepository.deleteById(id);
  }
}
