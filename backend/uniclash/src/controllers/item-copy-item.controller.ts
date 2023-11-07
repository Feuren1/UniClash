import {
  repository,
} from '@loopback/repository';
import {
  param,
  get,
  getModelSchemaRef,
} from '@loopback/rest';
import {
  ItemCopy,
  Item,
} from '../models';
import {ItemCopyRepository} from '../repositories';
import {authenticate} from '@loopback/authentication';
//@authenticate('jwt')
export class ItemCopyItemController {
  constructor(
    @repository(ItemCopyRepository)
    public itemCopyRepository: ItemCopyRepository,
  ) { }

  @get('/item-copies/{id}/item', {
    responses: {
      '200': {
        description: 'Item belonging to ItemCopy',
        content: {
          'application/json': {
            schema: getModelSchemaRef(Item),
          },
        },
      },
    },
  })
  async getItem(
    @param.path.number('id') id: typeof ItemCopy.prototype.id,
  ): Promise<Item> {
    return this.itemCopyRepository.item(id);
  }
}
