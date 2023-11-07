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
import {Arena} from '../models';
import {ArenaRepository} from '../repositories';

export class ArenaController {
  constructor(
    @repository(ArenaRepository)
    public arenaRepository : ArenaRepository,
  ) {}

  @post('/arenas')
  @response(200, {
    description: 'Arena model instance',
    content: {'application/json': {schema: getModelSchemaRef(Arena)}},
  })
  async create(
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Arena, {
            title: 'NewArena',
            exclude: ['id'],
          }),
        },
      },
    })
    arena: Omit<Arena, 'id'>,
  ): Promise<Arena> {
    return this.arenaRepository.create(arena);
  }

  @get('/arenas/count')
  @response(200, {
    description: 'Arena model count',
    content: {'application/json': {schema: CountSchema}},
  })
  async count(
    @param.where(Arena) where?: Where<Arena>,
  ): Promise<Count> {
    return this.arenaRepository.count(where);
  }

  @get('/arenas')
  @response(200, {
    description: 'Array of Arena model instances',
    content: {
      'application/json': {
        schema: {
          type: 'array',
          items: getModelSchemaRef(Arena, {includeRelations: true}),
        },
      },
    },
  })
  async find(
    @param.filter(Arena) filter?: Filter<Arena>,
  ): Promise<Arena[]> {
    return this.arenaRepository.find(filter);
  }

  @patch('/arenas')
  @response(200, {
    description: 'Arena PATCH success count',
    content: {'application/json': {schema: CountSchema}},
  })
  async updateAll(
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Arena, {partial: true}),
        },
      },
    })
    arena: Arena,
    @param.where(Arena) where?: Where<Arena>,
  ): Promise<Count> {
    return this.arenaRepository.updateAll(arena, where);
  }

  @get('/arenas/{id}')
  @response(200, {
    description: 'Arena model instance',
    content: {
      'application/json': {
        schema: getModelSchemaRef(Arena, {includeRelations: true}),
      },
    },
  })
  async findById(
    @param.path.number('id') id: number,
    @param.filter(Arena, {exclude: 'where'}) filter?: FilterExcludingWhere<Arena>
  ): Promise<Arena> {
    return this.arenaRepository.findById(id, filter);
  }

  @patch('/arenas/{id}')
  @response(204, {
    description: 'Arena PATCH success',
  })
  async updateById(
    @param.path.number('id') id: number,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Arena, {partial: true}),
        },
      },
    })
    arena: Arena,
  ): Promise<void> {
    await this.arenaRepository.updateById(id, arena);
  }

  @put('/arenas/{id}')
  @response(204, {
    description: 'Arena PUT success',
  })
  async replaceById(
    @param.path.number('id') id: number,
    @requestBody() arena: Arena,
  ): Promise<void> {
    await this.arenaRepository.replaceById(id, arena);
  }

  @del('/arenas/{id}')
  @response(204, {
    description: 'Arena DELETE success',
  })
  async deleteById(@param.path.number('id') id: number): Promise<void> {
    await this.arenaRepository.deleteById(id);
  }
}