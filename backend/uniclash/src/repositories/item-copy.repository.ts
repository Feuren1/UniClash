import {inject, Getter} from '@loopback/core';
import {DefaultCrudRepository, repository, BelongsToAccessor} from '@loopback/repository';
import {DbDataSource} from '../datasources';
import {ItemCopy, ItemCopyRelations, Item} from '../models';
import {ItemRepository} from './item.repository';

export class ItemCopyRepository extends DefaultCrudRepository<
  ItemCopy,
  typeof ItemCopy.prototype.id,
  ItemCopyRelations
> {

  public readonly item: BelongsToAccessor<Item, typeof ItemCopy.prototype.id>;

  constructor(
    @inject('datasources.db') dataSource: DbDataSource, @repository.getter('ItemRepository') protected itemRepositoryGetter: Getter<ItemRepository>,
  ) {
    super(ItemCopy, dataSource);
    this.item = this.createBelongsToAccessorFor('item', itemRepositoryGetter,);
    this.registerInclusionResolver('item', this.item.inclusionResolver);
  }
}
