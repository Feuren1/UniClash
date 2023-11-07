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
  Item,
  ItemCopy,
} from '../models';
import {ItemRepository} from '../repositories';
import {authenticate} from '@loopback/authentication';
//@authenticate('jwt')
export class ItemItemCopyController {
  constructor(
    @repository(ItemRepository) protected itemRepository: ItemRepository,
  ) { }

  @get('/items/{id}/item-copies', {
    responses: {
      '200': {
        description: 'Array of Item has many ItemCopy',
        content: {
          'application/json': {
            schema: {type: 'array', items: getModelSchemaRef(ItemCopy)},
          },
        },
      },
    },
  })
  async find(
    @param.path.number('id') id: number,
    @param.query.object('filter') filter?: Filter<ItemCopy>,
  ): Promise<ItemCopy[]> {
    return this.itemRepository.itemCopies(id).find(filter);
  }

  @post('/items/{id}/item-copies', {
    responses: {
      '200': {
        description: 'Item model instance',
        content: {'application/json': {schema: getModelSchemaRef(ItemCopy)}},
      },
    },
  })
  async create(
    @param.path.number('id') id: typeof Item.prototype.id,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(ItemCopy, {
            title: 'NewItemCopyInItem',
            exclude: ['id'],
            optional: ['itemId']
          }),
        },
      },
    }) itemCopy: Omit<ItemCopy, 'id'>,
  ): Promise<ItemCopy> {
    return this.itemRepository.itemCopies(id).create(itemCopy);
  }

  @patch('/items/{id}/item-copies', {
    responses: {
      '200': {
        description: 'Item.ItemCopy PATCH success count',
        content: {'application/json': {schema: CountSchema}},
      },
    },
  })
  async patch(
    @param.path.number('id') id: number,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(ItemCopy, {partial: true}),
        },
      },
    })
    itemCopy: Partial<ItemCopy>,
    @param.query.object('where', getWhereSchemaFor(ItemCopy)) where?: Where<ItemCopy>,
  ): Promise<Count> {
    return this.itemRepository.itemCopies(id).patch(itemCopy, where);
  }

  @del('/items/{id}/item-copies', {
    responses: {
      '200': {
        description: 'Item.ItemCopy DELETE success count',
        content: {'application/json': {schema: CountSchema}},
      },
    },
  })
  async delete(
    @param.path.number('id') id: number,
    @param.query.object('where', getWhereSchemaFor(ItemCopy)) where?: Where<ItemCopy>,
  ): Promise<Count> {
    return this.itemRepository.itemCopies(id).delete(where);
  }
}
