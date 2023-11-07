import {inject, Getter} from '@loopback/core';
import {DefaultCrudRepository, repository, BelongsToAccessor, HasManyRepositoryFactory} from '@loopback/repository';
import {DbDataSource} from '../datasources';
import {CritterCopy, CritterCopyRelations, Critter, Attack} from '../models';
import {CritterRepository} from './critter.repository';
import {AttackRepository} from './attack.repository';

export class CritterCopyRepository extends DefaultCrudRepository<
  CritterCopy,
  typeof CritterCopy.prototype.id,
  CritterCopyRelations
> {

  public readonly critter: BelongsToAccessor<Critter, typeof CritterCopy.prototype.id>;

  public readonly attacks: HasManyRepositoryFactory<Attack, typeof CritterCopy.prototype.id>;

  constructor(
    @inject('datasources.db') dataSource: DbDataSource, @repository.getter('CritterRepository') protected critterRepositoryGetter: Getter<CritterRepository>, @repository.getter('AttackRepository') protected attackRepositoryGetter: Getter<AttackRepository>,
  ) {
    super(CritterCopy, dataSource);
    this.attacks = this.createHasManyRepositoryFactoryFor('attacks', attackRepositoryGetter,);
    this.registerInclusionResolver('attacks', this.attacks.inclusionResolver);
    this.critter = this.createBelongsToAccessorFor('critter', critterRepositoryGetter,);
    this.registerInclusionResolver('critter', this.critter.inclusionResolver);
  }
}
