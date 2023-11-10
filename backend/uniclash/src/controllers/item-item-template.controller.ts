import {
  repository,
} from '@loopback/repository';
import {
  param,
  get,
  getModelSchemaRef,
} from '@loopback/rest';
import {
  Item,
  ItemTemplate,
} from '../models';
import {ItemRepository} from '../repositories';

export class ItemItemTemplateController {
  constructor(
    @repository(ItemRepository)
    public itemRepository: ItemRepository,
  ) { }

  @get('/items/{id}/item-template', {
    responses: {
      '200': {
        description: 'ItemTemplate belonging to Item',
        content: {
          'application/json': {
            schema: getModelSchemaRef(ItemTemplate),
          },
        },
      },
    },
  })
  async getItemTemplate(
    @param.path.number('id') id: typeof Item.prototype.id,
  ): Promise<ItemTemplate> {
    return this.itemRepository.itemTemplate(id);
  }
}
