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
import {Item} from '../models';
import {ItemRepository} from '../repositories';
import {authenticate} from "@loopback/authentication";

export class ItemController {
  constructor(
    @repository(ItemRepository)
    public itemRepository : ItemRepository,
  ) {}

  @authenticate('jwt')
  @post('/items')
  @response(200, {
    description: 'Item model instance',
    content: {'application/json': {schema: getModelSchemaRef(Item)}},
  })
  async create(
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Item, {
            title: 'NewItem',
            exclude: ['id'],
          }),
        },
      },
    })
    item: Omit<Item, 'id'>,
  ): Promise<Item> {
    return this.itemRepository.create(item);
  }

  @authenticate('jwt')
  @get('/items/count')
  @response(200, {
    description: 'Item model count',
    content: {'application/json': {schema: CountSchema}},
  })
  async count(
    @param.where(Item) where?: Where<Item>,
  ): Promise<Count> {
    return this.itemRepository.count(where);
  }

  @authenticate('jwt')
  @get('/items')
  @response(200, {
    description: 'Array of Item model instances',
    content: {
      'application/json': {
        schema: {
          type: 'array',
          items: getModelSchemaRef(Item, {includeRelations: true}),
        },
      },
    },
  })
  async find(
    @param.filter(Item) filter?: Filter<Item>,
  ): Promise<Item[]> {
    return this.itemRepository.find(filter);
  }

  @authenticate('jwt')
  @patch('/items')
  @response(200, {
    description: 'Item PATCH success count',
    content: {'application/json': {schema: CountSchema}},
  })
  async updateAll(
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Item, {partial: true}),
        },
      },
    })
    item: Item,
    @param.where(Item) where?: Where<Item>,
  ): Promise<Count> {
    return this.itemRepository.updateAll(item, where);
  }

  @authenticate('jwt')
  @get('/items/{id}')
  @response(200, {
    description: 'Item model instance',
    content: {
      'application/json': {
        schema: getModelSchemaRef(Item, {includeRelations: true}),
      },
    },
  })
  async findById(
    @param.path.number('id') id: number,
    @param.filter(Item, {exclude: 'where'}) filter?: FilterExcludingWhere<Item>
  ): Promise<Item> {
    return this.itemRepository.findById(id, filter);
  }

  @authenticate('jwt')
  @patch('/items/{id}')
  @response(204, {
    description: 'Item PATCH success',
  })
  async updateById(
    @param.path.number('id') id: number,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Item, {partial: true}),
        },
      },
    })
    item: Item,
  ): Promise<void> {
    await this.itemRepository.updateById(id, item);
  }

  @authenticate('jwt')
  @put('/items/{id}')
  @response(204, {
    description: 'Item PUT success',
  })
  async replaceById(
    @param.path.number('id') id: number,
    @requestBody() item: Item,
  ): Promise<void> {
    await this.itemRepository.replaceById(id, item);
  }

  @authenticate('jwt')
  @del('/items/{id}')
  @response(204, {
    description: 'Item DELETE success',
  })
  async deleteById(@param.path.number('id') id: number): Promise<void> {
    await this.itemRepository.deleteById(id);
  }
}
