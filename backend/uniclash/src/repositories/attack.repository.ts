import {Getter, inject} from '@loopback/core';
import {DefaultCrudRepository, HasManyRepositoryFactory, repository} from '@loopback/repository';
import {DbDataSource} from '../datasources';
import {Attack, AttackRelations, CritterCopy} from '../models';
import {CritterCopyRepository} from './critter-copy.repository';

export class AttackRepository extends DefaultCrudRepository<
  Attack,
  typeof Attack.prototype.id,
  AttackRelations
> {

  public readonly critterCopies: HasManyRepositoryFactory<CritterCopy, typeof Attack.prototype.id>;

  constructor(
    @inject('datasources.db') dataSource: DbDataSource, @repository.getter('CritterCopyRepository') protected critterCopyRepositoryGetter: Getter<CritterCopyRepository>,
  ) {
    super(Attack, dataSource);
    this.critterCopies = this.createHasManyRepositoryFactoryFor('critterCopies', critterCopyRepositoryGetter,);
    this.registerInclusionResolver('critterCopies', this.critterCopies.inclusionResolver);
  }
}
