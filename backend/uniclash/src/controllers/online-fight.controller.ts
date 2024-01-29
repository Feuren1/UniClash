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
import {OnlineFight} from '../models';
import {OnlineFightRepository} from '../repositories';
import {service} from "@loopback/core";
import {OnlineFightService} from "../services/online-fight.service";

export class OnlineFightController {
  constructor(
    @repository(OnlineFightRepository)
    public onlineFightRepository : OnlineFightRepository,
  ) {}

  @post('/online-fights')
  @response(200, {
    description: 'OnlineFight model instance',
    content: {'application/json': {schema: getModelSchemaRef(OnlineFight)}},
  })
  async create(
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(OnlineFight, {
            title: 'NewOnlineFight',
            exclude: ['fightId'],
          }),
        },
      },
    })
    onlineFight: Omit<OnlineFight, 'id'>,
  ): Promise<OnlineFight> {
    return this.onlineFightRepository.create(onlineFight);
  }

  @get('/online-fights/count')
  @response(200, {
    description: 'OnlineFight model count',
    content: {'application/json': {schema: CountSchema}},
  })
  async count(
    @param.where(OnlineFight) where?: Where<OnlineFight>,
  ): Promise<Count> {
    return this.onlineFightRepository.count(where);
  }

  @get('/online-fights')
  @response(200, {
    description: 'Array of OnlineFight model instances',
    content: {
      'application/json': {
        schema: {
          type: 'array',
          items: getModelSchemaRef(OnlineFight, {includeRelations: true}),
        },
      },
    },
  })
  async find(
    @param.filter(OnlineFight) filter?: Filter<OnlineFight>,
  ): Promise<OnlineFight[]> {
    return this.onlineFightRepository.find(filter);
  }

  @patch('/online-fights')
  @response(200, {
    description: 'OnlineFight PATCH success count',
    content: {'application/json': {schema: CountSchema}},
  })
  async updateAll(
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(OnlineFight, {partial: true}),
        },
      },
    })
    onlineFight: OnlineFight,
    @param.where(OnlineFight) where?: Where<OnlineFight>,
  ): Promise<Count> {
    return this.onlineFightRepository.updateAll(onlineFight, where);
  }

  @get('/online-fights/{id}')
  @response(200, {
    description: 'OnlineFight model instance',
    content: {
      'application/json': {
        schema: getModelSchemaRef(OnlineFight, {includeRelations: true}),
      },
    },
  })
  async findById(
    @param.path.number('id') id: number,
    @param.filter(OnlineFight, {exclude: 'where'}) filter?: FilterExcludingWhere<OnlineFight>
  ): Promise<OnlineFight> {
    return this.onlineFightRepository.findById(id, filter);
  }

  @patch('/online-fights/{id}')
  @response(204, {
    description: 'OnlineFight PATCH success',
  })
  async updateById(
    @param.path.number('id') id: number,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(OnlineFight, {partial: true}),
        },
      },
    })
    onlineFight: OnlineFight,
  ): Promise<void> {
    await this.onlineFightRepository.updateById(id, onlineFight);
  }

  @put('/online-fights/{id}')
  @response(204, {
    description: 'OnlineFight PUT success',
  })
  async replaceById(
    @param.path.number('id') id: number,
    @requestBody() onlineFight: OnlineFight,
  ): Promise<void> {
    await this.onlineFightRepository.replaceById(id, onlineFight);
  }

  @del('/online-fights/{id}')
  @response(204, {
    description: 'OnlineFight DELETE success',
  })
  async deleteById(@param.path.number('id') id: number): Promise<void> {
    await this.onlineFightRepository.deleteById(id);
  }
}
