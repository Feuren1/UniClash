import {inject, Getter} from '@loopback/core';
import {DefaultCrudRepository, repository, HasManyRepositoryFactory} from '@loopback/repository';
import {DbDataSource} from '../datasources';
import {Critter, CritterRelations, CritterCopy} from '../models';
import {CritterCopyRepository} from './critter-copy.repository';

export class CritterRepository extends DefaultCrudRepository<
  Critter,
  typeof Critter.prototype.id,
  CritterRelations
> {

  public readonly critterCopies: HasManyRepositoryFactory<CritterCopy, typeof Critter.prototype.id>;

  constructor(
    @inject('datasources.db') dataSource: DbDataSource, @repository.getter('CritterCopyRepository') protected critterCopyRepositoryGetter: Getter<CritterCopyRepository>,
  ) {
    super(Critter, dataSource);
    this.critterCopies = this.createHasManyRepositoryFactoryFor('critterCopies', critterCopyRepositoryGetter,);
    this.registerInclusionResolver('critterCopies', this.critterCopies.inclusionResolver);
  }
}
