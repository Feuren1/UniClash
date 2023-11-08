import {Getter, inject} from '@loopback/core';
import {BelongsToAccessor, DefaultCrudRepository, HasManyRepositoryFactory, repository} from '@loopback/repository';
import {CritterCopyAttackRepository} from '.';
import {DbDataSource} from '../datasources';
import {Critter, CritterCopy, CritterCopyAttack, CritterCopyRelations} from '../models';
import {CritterRepository} from './critter.repository';

export class CritterCopyRepository extends DefaultCrudRepository<
  CritterCopy,
  typeof CritterCopy.prototype.id,
  CritterCopyRelations
> {

  public readonly critter: BelongsToAccessor<Critter, typeof CritterCopy.prototype.id>;

  public readonly attacks: HasManyRepositoryFactory<CritterCopyAttack, typeof CritterCopy.prototype.id>;

  constructor(
    @inject('datasources.db') dataSource: DbDataSource, @repository.getter('CritterRepository') protected critterRepositoryGetter: Getter<CritterRepository>, @repository.getter('AttackRepository') protected critterCopyAttackRepositoryGetter: Getter<CritterCopyAttackRepository>,
  ) {
    super(CritterCopy, dataSource);
    this.attacks = this.createHasManyRepositoryFactoryFor('critterCopyAttacks', critterCopyAttackRepositoryGetter,);
    this.registerInclusionResolver('attacks', this.attacks.inclusionResolver);
    this.critter = this.createBelongsToAccessorFor('critter', critterRepositoryGetter,);
    this.registerInclusionResolver('critter', this.critter.inclusionResolver);
  }
}
