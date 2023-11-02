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
import {Attack} from '../models';
import {AttackRepository} from '../repositories';

export class AttackController {
  constructor(
    @repository(AttackRepository)
    public attackRepository : AttackRepository,
  ) {}

  @post('/attacks')
  @response(200, {
    description: 'Attack model instance',
    content: {'application/json': {schema: getModelSchemaRef(Attack)}},
  })
  async create(
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Attack, {
            title: 'NewAttack',
            exclude: ['id'],
          }),
        },
      },
    })
    attack: Omit<Attack, 'id'>,
  ): Promise<Attack> {
    return this.attackRepository.create(attack);
  }

  @get('/attacks/count')
  @response(200, {
    description: 'Attack model count',
    content: {'application/json': {schema: CountSchema}},
  })
  async count(
    @param.where(Attack) where?: Where<Attack>,
  ): Promise<Count> {
    return this.attackRepository.count(where);
  }

  @get('/attacks')
  @response(200, {
    description: 'Array of Attack model instances',
    content: {
      'application/json': {
        schema: {
          type: 'array',
          items: getModelSchemaRef(Attack, {includeRelations: true}),
        },
      },
    },
  })
  async find(
    @param.filter(Attack) filter?: Filter<Attack>,
  ): Promise<Attack[]> {
    return this.attackRepository.find(filter);
  }

  @patch('/attacks')
  @response(200, {
    description: 'Attack PATCH success count',
    content: {'application/json': {schema: CountSchema}},
  })
  async updateAll(
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Attack, {partial: true}),
        },
      },
    })
    attack: Attack,
    @param.where(Attack) where?: Where<Attack>,
  ): Promise<Count> {
    return this.attackRepository.updateAll(attack, where);
  }

  @get('/attacks/{id}')
  @response(200, {
    description: 'Attack model instance',
    content: {
      'application/json': {
        schema: getModelSchemaRef(Attack, {includeRelations: true}),
      },
    },
  })
  async findById(
    @param.path.number('id') id: number,
    @param.filter(Attack, {exclude: 'where'}) filter?: FilterExcludingWhere<Attack>
  ): Promise<Attack> {
    return this.attackRepository.findById(id, filter);
  }

  @patch('/attacks/{id}')
  @response(204, {
    description: 'Attack PATCH success',
  })
  async updateById(
    @param.path.number('id') id: number,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Attack, {partial: true}),
        },
      },
    })
    attack: Attack,
  ): Promise<void> {
    await this.attackRepository.updateById(id, attack);
  }

  @put('/attacks/{id}')
  @response(204, {
    description: 'Attack PUT success',
  })
  async replaceById(
    @param.path.number('id') id: number,
    @requestBody() attack: Attack,
  ): Promise<void> {
    await this.attackRepository.replaceById(id, attack);
  }

  @del('/attacks/{id}')
  @response(204, {
    description: 'Attack DELETE success',
  })
  async deleteById(@param.path.number('id') id: number): Promise<void> {
    await this.attackRepository.deleteById(id);
  }
}
