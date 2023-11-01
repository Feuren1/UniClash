import {inject, Getter} from '@loopback/core';
import {DefaultCrudRepository, repository, HasManyRepositoryFactory} from '@loopback/repository';
import {DbDataSource} from '../datasources';
import {Item, ItemRelations, ItemCopy} from '../models';
import {ItemCopyRepository} from './item-copy.repository';

export class ItemRepository extends DefaultCrudRepository<
  Item,
  typeof Item.prototype.id,
  ItemRelations
> {

  public readonly itemCopies: HasManyRepositoryFactory<ItemCopy, typeof Item.prototype.id>;

  constructor(
    @inject('datasources.db') dataSource: DbDataSource, @repository.getter('ItemCopyRepository') protected itemCopyRepositoryGetter: Getter<ItemCopyRepository>,
  ) {
    super(Item, dataSource);
    this.itemCopies = this.createHasManyRepositoryFactoryFor('itemCopies', itemCopyRepositoryGetter,);
    this.registerInclusionResolver('itemCopies', this.itemCopies.inclusionResolver);
  }
}
