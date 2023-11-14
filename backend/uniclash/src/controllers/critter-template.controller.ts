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
import {CritterTemplate} from '../models';
import {CritterTemplateRepository} from '../repositories';

export class CritterTemplateController {
  constructor(
    @repository(CritterTemplateRepository)
    public critterTemplateRepository : CritterTemplateRepository,
  ) {}

  @post('/critter-templates')
  @response(200, {
    description: 'CritterTemplate model instance',
    content: {'application/json': {schema: getModelSchemaRef(CritterTemplate)}},
  })
  async create(
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(CritterTemplate, {
            title: 'NewCritterTemplate',
            exclude: ['id'],
          }),
        },
      },
    })
    critterTemplate: Omit<CritterTemplate, 'id'>,
  ): Promise<CritterTemplate> {
    return this.critterTemplateRepository.create(critterTemplate);
  }

  @get('/critter-templates/count')
  @response(200, {
    description: 'CritterTemplate model count',
    content: {'application/json': {schema: CountSchema}},
  })
  async count(
    @param.where(CritterTemplate) where?: Where<CritterTemplate>,
  ): Promise<Count> {
    return this.critterTemplateRepository.count(where);
  }

  @get('/critter-templates')
  @response(200, {
    description: 'Array of CritterTemplate model instances',
    content: {
      'application/json': {
        schema: {
          type: 'array',
          items: getModelSchemaRef(CritterTemplate, {includeRelations: true}),
        },
      },
    },
  })
  async find(
    @param.filter(CritterTemplate) filter?: Filter<CritterTemplate>,
  ): Promise<CritterTemplate[]> {
    return this.critterTemplateRepository.find(filter);
  }

  @patch('/critter-templates')
  @response(200, {
    description: 'CritterTemplate PATCH success count',
    content: {'application/json': {schema: CountSchema}},
  })
  async updateAll(
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(CritterTemplate, {partial: true}),
        },
      },
    })
    critterTemplate: CritterTemplate,
    @param.where(CritterTemplate) where?: Where<CritterTemplate>,
  ): Promise<Count> {
    return this.critterTemplateRepository.updateAll(critterTemplate, where);
  }

  @get('/critter-templates/{id}')
  @response(200, {
    description: 'CritterTemplate model instance',
    content: {
      'application/json': {
        schema: getModelSchemaRef(CritterTemplate, {includeRelations: true}),
      },
    },
  })
  async findById(
    @param.path.number('id') id: number,
    @param.filter(CritterTemplate, {exclude: 'where'}) filter?: FilterExcludingWhere<CritterTemplate>
  ): Promise<CritterTemplate> {
    return this.critterTemplateRepository.findById(id, filter);
  }

  @patch('/critter-templates/{id}')
  @response(204, {
    description: 'CritterTemplate PATCH success',
  })
  async updateById(
    @param.path.number('id') id: number,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(CritterTemplate, {partial: true}),
        },
      },
    })
    critterTemplate: CritterTemplate,
  ): Promise<void> {
    await this.critterTemplateRepository.updateById(id, critterTemplate);
  }

  @put('/critter-templates/{id}')
  @response(204, {
    description: 'CritterTemplate PUT success',
  })
  async replaceById(
    @param.path.number('id') id: number,
    @requestBody() critterTemplate: CritterTemplate,
  ): Promise<void> {
    await this.critterTemplateRepository.replaceById(id, critterTemplate);
  }

  @del('/critter-templates/{id}')
  @response(204, {
    description: 'CritterTemplate DELETE success',
  })
  async deleteById(@param.path.number('id') id: number): Promise<void> {
    await this.critterTemplateRepository.deleteById(id);
  }
}
