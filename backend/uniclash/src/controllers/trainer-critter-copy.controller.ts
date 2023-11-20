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
  Trainer,
  CritterCopy,
} from '../models';
import {TrainerRepository} from '../repositories';

export class TrainerCritterCopyController {
  constructor(
    @repository(TrainerRepository) protected trainerRepository: TrainerRepository,
  ) { }

  @get('/trainers/{id}/critter-copies', {
    responses: {
      '200': {
        description: 'Array of Trainer has many CritterCopy',
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
    return this.trainerRepository.critterCopies(id).find(filter);
  }

  @post('/trainers/{id}/critter-copies', {
    responses: {
      '200': {
        description: 'Trainer model instance',
        content: {'application/json': {schema: getModelSchemaRef(CritterCopy)}},
      },
    },
  })
  async create(
    @param.path.number('id') id: typeof Trainer.prototype.id,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(CritterCopy, {
            title: 'NewCritterCopyInTrainer',
            exclude: ['id'],
            optional: ['trainerId']
          }),
        },
      },
    }) critterCopy: Omit<CritterCopy, 'id'>,
  ): Promise<CritterCopy> {
    return this.trainerRepository.critterCopies(id).create(critterCopy);
  }

  @patch('/trainers/{id}/critter-copies', {
    responses: {
      '200': {
        description: 'Trainer.CritterCopy PATCH success count',
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
    return this.trainerRepository.critterCopies(id).patch(critterCopy, where);
  }

  @del('/trainers/{id}/critter-copies', {
    responses: {
      '200': {
        description: 'Trainer.CritterCopy DELETE success count',
        content: {'application/json': {schema: CountSchema}},
      },
    },
  })
  async delete(
    @param.path.number('id') id: number,
    @param.query.object('where', getWhereSchemaFor(CritterCopy)) where?: Where<CritterCopy>,
  ): Promise<Count> {
    return this.trainerRepository.critterCopies(id).delete(where);
  }
}
