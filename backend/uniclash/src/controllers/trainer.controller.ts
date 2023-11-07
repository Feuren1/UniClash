import {
  Count,
  CountSchema,
  Filter,
  FilterExcludingWhere,
  repository,
  Where,
} from '@loopback/repository';
import {
  del,
  get,
  getModelSchemaRef,
  param,
  patch,
  post,
  put,
  requestBody,
  response,
} from '@loopback/rest';
import {Trainer} from '../models';
import {TrainerRepository} from '../repositories';

export class TrainerController {
  constructor(
    @repository(TrainerRepository)
    public trainerRepository: TrainerRepository,
  ) { }

  @post('/trainers')
  @response(200, {
    description: 'Trainer model instance',
    content: {'application/json': {schema: getModelSchemaRef(Trainer)}},
  })
  async create(
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Trainer, {
            title: 'NewTrainer',
            exclude: ['id'],
          }),
        },
      },
    })
    trainer: Omit<Trainer, 'id'>,
  ): Promise<Trainer> {
    return this.trainerRepository.create(trainer);
  }

  @get('/trainers/count')
  @response(200, {
    description: 'Trainer model count',
    content: {'application/json': {schema: CountSchema}},
  })
  async count(
    @param.where(Trainer) where?: Where<Trainer>,
  ): Promise<Count> {
    return this.trainerRepository.count(where);
  }

  @get('/trainers')
  @response(200, {
    description: 'Array of Trainer model instances',
    content: {
      'application/json': {
        schema: {
          type: 'array',
          items: getModelSchemaRef(Trainer, {includeRelations: true}),
        },
      },
    },
  })
  async find(
    @param.filter(Trainer) filter?: Filter<Trainer>,
  ): Promise<Trainer[]> {
    return this.trainerRepository.find(filter);
  }

  @patch('/trainers')
  @response(200, {
    description: 'Trainer PATCH success count',
    content: {'application/json': {schema: CountSchema}},
  })
  async updateAll(
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Trainer, {partial: true}),
        },
      },
    })
    trainer: Trainer,
    @param.where(Trainer) where?: Where<Trainer>,
  ): Promise<Count> {
    return this.trainerRepository.updateAll(trainer, where);
  }

  @get('/trainers/{id}')
  @response(200, {
    description: 'Trainer model instance',
    content: {
      'application/json': {
        schema: getModelSchemaRef(Trainer, {includeRelations: true}),
      },
    },
  })
  async findById(
    @param.path.number('id') id: number,
    @param.filter(Trainer, {exclude: 'where'}) filter?: FilterExcludingWhere<Trainer>
  ): Promise<Trainer> {
    return this.trainerRepository.findById(id, filter);
  }

  @patch('/trainers/{id}')
  @response(204, {
    description: 'Trainer PATCH success',
  })
  async updateById(
    @param.path.number('id') id: number,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Trainer, {partial: true}),
        },
      },
    })
    trainer: Trainer,
  ): Promise<void> {
    await this.trainerRepository.updateById(id, trainer);
  }

  @put('/trainers/{id}')
  @response(204, {
    description: 'Trainer PUT success',
  })
  async replaceById(
    @param.path.number('id') id: number,
    @requestBody() trainer: Trainer,
  ): Promise<void> {
    await this.trainerRepository.replaceById(id, trainer);
  }

  @del('/trainers/{id}')
  @response(204, {
    description: 'Trainer DELETE success',
  })
  async deleteById(@param.path.number('id') id: number): Promise<void> {
    await this.trainerRepository.deleteById(id);
  }
}
