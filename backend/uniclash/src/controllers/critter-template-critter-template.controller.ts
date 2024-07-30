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
} from '../models';
import {CritterTemplateRepository} from '../repositories';
import {authenticate} from "@loopback/authentication";

export class CritterTemplateCritterTemplateController {
  constructor(
    @repository(CritterTemplateRepository) protected critterTemplateRepository: CritterTemplateRepository,
  ) { }

  @authenticate('jwt')
  @get('/critter-templates/{id}/critter-template', {
    responses: {
      '200': {
        description: 'CritterTemplate has one CritterTemplate',
        content: {
          'application/json': {
            schema: getModelSchemaRef(CritterTemplate),
          },
        },
      },
    },
  })
  async get(
    @param.path.number('id') id: number,
    @param.query.object('filter') filter?: Filter<CritterTemplate>,
  ): Promise<CritterTemplate> {
    return this.critterTemplateRepository.evolvesInto(id).get(filter);
  }

  @authenticate('jwt')
  @post('/critter-templates/{id}/critter-template', {
    responses: {
      '200': {
        description: 'CritterTemplate model instance',
        content: {'application/json': {schema: getModelSchemaRef(CritterTemplate)}},
      },
    },
  })
  async create(
    @param.path.number('id') id: typeof CritterTemplate.prototype.id,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(CritterTemplate, {
            title: 'NewCritterTemplateInCritterTemplate',
            exclude: ['id'],
            optional: ['evolvesIntoTemplateId']
          }),
        },
      },
    }) critterTemplate: Omit<CritterTemplate, 'id'>,
  ): Promise<CritterTemplate> {
    return this.critterTemplateRepository.evolvesInto(id).create(critterTemplate);
  }

  @authenticate('jwt')
  @patch('/critter-templates/{id}/critter-template', {
    responses: {
      '200': {
        description: 'CritterTemplate.CritterTemplate PATCH success count',
        content: {'application/json': {schema: CountSchema}},
      },
    },
  })
  async patch(
    @param.path.number('id') id: number,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(CritterTemplate, {partial: true}),
        },
      },
    })
    critterTemplate: Partial<CritterTemplate>,
    @param.query.object('where', getWhereSchemaFor(CritterTemplate)) where?: Where<CritterTemplate>,
  ): Promise<Count> {
    return this.critterTemplateRepository.evolvesInto(id).patch(critterTemplate, where);
  }

  @authenticate('jwt')
  @del('/critter-templates/{id}/critter-template', {
    responses: {
      '200': {
        description: 'CritterTemplate.CritterTemplate DELETE success count',
        content: {'application/json': {schema: CountSchema}},
      },
    },
  })
  async delete(
    @param.path.number('id') id: number,
    @param.query.object('where', getWhereSchemaFor(CritterTemplate)) where?: Where<CritterTemplate>,
  ): Promise<Count> {
    return this.critterTemplateRepository.evolvesInto(id).delete(where);
  }
}
