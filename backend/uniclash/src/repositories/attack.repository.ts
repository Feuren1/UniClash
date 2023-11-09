import {Getter, inject} from '@loopback/core';
import {DefaultCrudRepository, HasManyRepositoryFactory, repository} from '@loopback/repository';
import {CritterCopyAttackRepository} from '.';
import {DbDataSource} from '../datasources';
import {Attack, AttackRelations, CritterCopyAttack} from '../models';

export class AttackRepository extends DefaultCrudRepository<
  Attack,
  typeof Attack.prototype.id,
  AttackRelations
> {

  public readonly critterCopyAttacks: HasManyRepositoryFactory<CritterCopyAttack, typeof Attack.prototype.id>;

  constructor(
    @inject('datasources.db') dataSource: DbDataSource, @repository.getter('CritterCopyRepository') protected critterCopyAttackRepositoryGetter: Getter<CritterCopyAttackRepository>,
  ) {
    super(Attack, dataSource);
    this.critterCopyAttacks = this.createHasManyRepositoryFactoryFor('critterCopyAttacks', critterCopyAttackRepositoryGetter,);
    this.registerInclusionResolver('critterCopies', this.critterCopyAttacks.inclusionResolver);
  }
}
