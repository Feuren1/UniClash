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
  CritterTemplate,
  Critter,
} from '../models';
import {CritterTemplateRepository} from '../repositories';

export class CritterTemplateCritterController {
  constructor(
    @repository(CritterTemplateRepository) protected critterTemplateRepository: CritterTemplateRepository,
  ) { }

  @get('/critter-templates/{id}/critters', {
    responses: {
      '200': {
        description: 'Array of CritterTemplate has many Critter',
        content: {
          'application/json': {
            schema: {type: 'array', items: getModelSchemaRef(Critter)},
          },
        },
      },
    },
  })
  async find(
    @param.path.number('id') id: number,
    @param.query.object('filter') filter?: Filter<Critter>,
  ): Promise<Critter[]> {
    return this.critterTemplateRepository.critters(id).find(filter);
  }

  @post('/critter-templates/{id}/critters', {
    responses: {
      '200': {
        description: 'CritterTemplate model instance',
        content: {'application/json': {schema: getModelSchemaRef(Critter)}},
      },
    },
  })
  async create(
    @param.path.number('id') id: typeof CritterTemplate.prototype.id,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Critter, {
            title: 'NewCritterInCritterTemplate',
            exclude: ['id'],
            optional: ['critterTemplateId']
          }),
        },
      },
    }) critter: Omit<Critter, 'id'>,
  ): Promise<Critter> {
    return this.critterTemplateRepository.critters(id).create(critter);
  }

  @patch('/critter-templates/{id}/critters', {
    responses: {
      '200': {
        description: 'CritterTemplate.Critter PATCH success count',
        content: {'application/json': {schema: CountSchema}},
      },
    },
  })
  async patch(
    @param.path.number('id') id: number,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Critter, {partial: true}),
        },
      },
    })
    critter: Partial<Critter>,
    @param.query.object('where', getWhereSchemaFor(Critter)) where?: Where<Critter>,
  ): Promise<Count> {
    return this.critterTemplateRepository.critters(id).patch(critter, where);
  }

  @del('/critter-templates/{id}/critters', {
    responses: {
      '200': {
        description: 'CritterTemplate.Critter DELETE success count',
        content: {'application/json': {schema: CountSchema}},
      },
    },
  })
  async delete(
    @param.path.number('id') id: number,
    @param.query.object('where', getWhereSchemaFor(Critter)) where?: Where<Critter>,
  ): Promise<Count> {
    return this.critterTemplateRepository.critters(id).delete(where);
  }
}
