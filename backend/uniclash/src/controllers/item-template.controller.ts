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
import {ItemTemplate} from '../models';
import {ItemTemplateRepository} from '../repositories';

export class ItemTemplateController {
  constructor(
    @repository(ItemTemplateRepository)
    public itemTemplateRepository : ItemTemplateRepository,
  ) {}

  @post('/item-templates')
  @response(200, {
    description: 'ItemTemplate model instance',
    content: {'application/json': {schema: getModelSchemaRef(ItemTemplate)}},
  })
  async create(
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(ItemTemplate, {
            title: 'NewItemTemplate',
            exclude: ['id'],
          }),
        },
      },
    })
    itemTemplate: Omit<ItemTemplate, 'id'>,
  ): Promise<ItemTemplate> {
    return this.itemTemplateRepository.create(itemTemplate);
  }

  @get('/item-templates/count')
  @response(200, {
    description: 'ItemTemplate model count',
    content: {'application/json': {schema: CountSchema}},
  })
  async count(
    @param.where(ItemTemplate) where?: Where<ItemTemplate>,
  ): Promise<Count> {
    return this.itemTemplateRepository.count(where);
  }

  @get('/item-templates')
  @response(200, {
    description: 'Array of ItemTemplate model instances',
    content: {
      'application/json': {
        schema: {
          type: 'array',
          items: getModelSchemaRef(ItemTemplate, {includeRelations: true}),
        },
      },
    },
  })
  async find(
    @param.filter(ItemTemplate) filter?: Filter<ItemTemplate>,
  ): Promise<ItemTemplate[]> {
    return this.itemTemplateRepository.find(filter);
  }

  @patch('/item-templates')
  @response(200, {
    description: 'ItemTemplate PATCH success count',
    content: {'application/json': {schema: CountSchema}},
  })
  async updateAll(
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(ItemTemplate, {partial: true}),
        },
      },
    })
    itemTemplate: ItemTemplate,
    @param.where(ItemTemplate) where?: Where<ItemTemplate>,
  ): Promise<Count> {
    return this.itemTemplateRepository.updateAll(itemTemplate, where);
  }

  @get('/item-templates/{id}')
  @response(200, {
    description: 'ItemTemplate model instance',
    content: {
      'application/json': {
        schema: getModelSchemaRef(ItemTemplate, {includeRelations: true}),
      },
    },
  })
  async findById(
    @param.path.number('id') id: number,
    @param.filter(ItemTemplate, {exclude: 'where'}) filter?: FilterExcludingWhere<ItemTemplate>
  ): Promise<ItemTemplate> {
    return this.itemTemplateRepository.findById(id, filter);
  }

  @patch('/item-templates/{id}')
  @response(204, {
    description: 'ItemTemplate PATCH success',
  })
  async updateById(
    @param.path.number('id') id: number,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(ItemTemplate, {partial: true}),
        },
      },
    })
    itemTemplate: ItemTemplate,
  ): Promise<void> {
    await this.itemTemplateRepository.updateById(id, itemTemplate);
  }

  @put('/item-templates/{id}')
  @response(204, {
    description: 'ItemTemplate PUT success',
  })
  async replaceById(
    @param.path.number('id') id: number,
    @requestBody() itemTemplate: ItemTemplate,
  ): Promise<void> {
    await this.itemTemplateRepository.replaceById(id, itemTemplate);
  }

  @del('/item-templates/{id}')
  @response(204, {
    description: 'ItemTemplate DELETE success',
  })
  async deleteById(@param.path.number('id') id: number): Promise<void> {
    await this.itemTemplateRepository.deleteById(id);
  }
}
