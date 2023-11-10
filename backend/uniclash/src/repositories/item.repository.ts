import {inject, Getter} from '@loopback/core';
import {DefaultCrudRepository, repository, BelongsToAccessor} from '@loopback/repository';
import {DbDataSource} from '../datasources';
import {Item, ItemRelations, ItemTemplate} from '../models';
import {ItemTemplateRepository} from './item-template.repository';

export class ItemRepository extends DefaultCrudRepository<
  Item,
  typeof Item.prototype.id,
  ItemRelations
> {

  public readonly itemTemplate: BelongsToAccessor<ItemTemplate, typeof Item.prototype.id>;

  constructor(
    @inject('datasources.db') dataSource: DbDataSource, @repository.getter('ItemTemplateRepository') protected itemTemplateRepositoryGetter: Getter<ItemTemplateRepository>,
  ) {
    super(Item, dataSource);
    this.itemTemplate = this.createBelongsToAccessorFor('itemTemplate', itemTemplateRepositoryGetter,);
    this.registerInclusionResolver('itemTemplate', this.itemTemplate.inclusionResolver);
  }
}
