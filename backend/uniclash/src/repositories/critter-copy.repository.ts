import {inject, Getter} from '@loopback/core';
import {DefaultCrudRepository, repository, BelongsToAccessor} from '@loopback/repository';
import {DbDataSource} from '../datasources';
import {CritterCopy, CritterCopyRelations, Critter} from '../models';
import {CritterRepository} from './critter.repository';

export class CritterCopyRepository extends DefaultCrudRepository<
  CritterCopy,
  typeof CritterCopy.prototype.id,
  CritterCopyRelations
> {

  public readonly critter: BelongsToAccessor<Critter, typeof CritterCopy.prototype.id>;

  constructor(
    @inject('datasources.db') dataSource: DbDataSource, @repository.getter('CritterRepository') protected critterRepositoryGetter: Getter<CritterRepository>,
  ) {
    super(CritterCopy, dataSource);
    this.critter = this.createBelongsToAccessorFor('critter', critterRepositoryGetter,);
    this.registerInclusionResolver('critter', this.critter.inclusionResolver);
  }
}
