import {inject, Getter} from '@loopback/core';
import {DefaultCrudRepository, repository, BelongsToAccessor} from '@loopback/repository';
import {DbDataSource} from '../datasources';
import {Item, ItemRelations, ItemTemplate, Student} from '../models';
import {ItemTemplateRepository} from './item-template.repository';
import {StudentRepository} from './student.repository';

export class ItemRepository extends DefaultCrudRepository<
  Item,
  typeof Item.prototype.id,
  ItemRelations
> {

  public readonly itemTemplate: BelongsToAccessor<ItemTemplate, typeof Item.prototype.id>;

  public readonly student: BelongsToAccessor<Student, typeof Item.prototype.id>;

  constructor(
    @inject('datasources.db') dataSource: DbDataSource, @repository.getter('ItemTemplateRepository') protected itemTemplateRepositoryGetter: Getter<ItemTemplateRepository>, @repository.getter('StudentRepository') protected studentRepositoryGetter: Getter<StudentRepository>,
  ) {
    super(Item, dataSource);
    this.student = this.createBelongsToAccessorFor('student', studentRepositoryGetter,);
    this.registerInclusionResolver('student', this.student.inclusionResolver);
    this.itemTemplate = this.createBelongsToAccessorFor('itemTemplate', itemTemplateRepositoryGetter,);
    this.registerInclusionResolver('itemTemplate', this.itemTemplate.inclusionResolver);
  }
}
