import {inject, Getter} from '@loopback/core';
import {DefaultCrudRepository, repository, HasManyRepositoryFactory} from '@loopback/repository';
import {DbDataSource} from '../datasources';
import {ItemTemplate, ItemTemplateRelations, Item} from '../models';
import {ItemRepository} from './item.repository';

export class ItemTemplateRepository extends DefaultCrudRepository<
  ItemTemplate,
  typeof ItemTemplate.prototype.id,
  ItemTemplateRelations
> {

  public readonly items: HasManyRepositoryFactory<Item, typeof ItemTemplate.prototype.id>;

  constructor(
    @inject('datasources.db') dataSource: DbDataSource, @repository.getter('ItemRepository') protected itemRepositoryGetter: Getter<ItemRepository>,
  ) {
    super(ItemTemplate, dataSource);
    this.items = this.createHasManyRepositoryFactoryFor('items', itemRepositoryGetter,);
    this.registerInclusionResolver('items', this.items.inclusionResolver);
  }
}
