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
  ItemTemplate,
  Item,
} from '../models';
import {ItemTemplateRepository} from '../repositories';
import {authenticate} from "@loopback/authentication";

export class ItemTemplateItemController {
  constructor(
    @repository(ItemTemplateRepository) protected itemTemplateRepository: ItemTemplateRepository,
  ) { }

  @authenticate('jwt')
  @get('/item-templates/{id}/items', {
    responses: {
      '200': {
        description: 'Array of ItemTemplate has many Item',
        content: {
          'application/json': {
            schema: {type: 'array', items: getModelSchemaRef(Item)},
          },
        },
      },
    },
  })
  async find(
    @param.path.number('id') id: number,
    @param.query.object('filter') filter?: Filter<Item>,
  ): Promise<Item[]> {
    return this.itemTemplateRepository.items(id).find(filter);
  }

  @authenticate('jwt')
  @post('/item-templates/{id}/items', {
    responses: {
      '200': {
        description: 'ItemTemplate model instance',
        content: {'application/json': {schema: getModelSchemaRef(Item)}},
      },
    },
  })
  async create(
    @param.path.number('id') id: typeof ItemTemplate.prototype.id,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Item, {
            title: 'NewItemInItemTemplate',
            exclude: ['id'],
            optional: ['itemTemplateId']
          }),
        },
      },
    }) item: Omit<Item, 'id'>,
  ): Promise<Item> {
    return this.itemTemplateRepository.items(id).create(item);
  }

  @authenticate('jwt')
  @patch('/item-templates/{id}/items', {
    responses: {
      '200': {
        description: 'ItemTemplate.Item PATCH success count',
        content: {'application/json': {schema: CountSchema}},
      },
    },
  })
  async patch(
    @param.path.number('id') id: number,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Item, {partial: true}),
        },
      },
    })
    item: Partial<Item>,
    @param.query.object('where', getWhereSchemaFor(Item)) where?: Where<Item>,
  ): Promise<Count> {
    return this.itemTemplateRepository.items(id).patch(item, where);
  }

  @authenticate('jwt')
  @del('/item-templates/{id}/items', {
    responses: {
      '200': {
        description: 'ItemTemplate.Item DELETE success count',
        content: {'application/json': {schema: CountSchema}},
      },
    },
  })
  async delete(
    @param.path.number('id') id: number,
    @param.query.object('where', getWhereSchemaFor(Item)) where?: Where<Item>,
  ): Promise<Count> {
    return this.itemTemplateRepository.items(id).delete(where);
  }
}
