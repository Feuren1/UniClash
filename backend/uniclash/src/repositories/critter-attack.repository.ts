import {inject, Getter} from '@loopback/core';
import {DefaultCrudRepository, repository, BelongsToAccessor} from '@loopback/repository';
import {DbDataSource} from '../datasources';
import {CritterAttack, CritterAttackRelations, Attack, Critter} from '../models';
import {AttackRepository} from './attack.repository';
import {CritterRepository} from './critter.repository';

export class CritterAttackRepository extends DefaultCrudRepository<
  CritterAttack,
  typeof CritterAttack.prototype.id,
  CritterAttackRelations
> {

  public readonly attack: BelongsToAccessor<Attack, typeof CritterAttack.prototype.id>;

  public readonly critter: BelongsToAccessor<Critter, typeof CritterAttack.prototype.id>;

  constructor(
    @inject('datasources.db') dataSource: DbDataSource, @repository.getter('AttackRepository') protected attackRepositoryGetter: Getter<AttackRepository>, @repository.getter('CritterRepository') protected critterRepositoryGetter: Getter<CritterRepository>,
  ) {
    super(CritterAttack, dataSource);
    this.critter = this.createBelongsToAccessorFor('critter', critterRepositoryGetter,);
    this.registerInclusionResolver('critter', this.critter.inclusionResolver);
    this.attack = this.createBelongsToAccessorFor('attack', attackRepositoryGetter,);
    this.registerInclusionResolver('attack', this.attack.inclusionResolver);
  }
}
