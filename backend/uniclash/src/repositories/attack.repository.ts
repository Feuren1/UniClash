import {Getter, inject} from '@loopback/core';
import {BelongsToAccessor, DefaultCrudRepository, HasManyRepositoryFactory, repository} from '@loopback/repository';
import {DbDataSource} from '../datasources';
import {Attack, AttackRelations, CritterAttack, Type} from '../models';
import {CritterAttackRepository} from './critter-attack.repository';
import {TypeRepository} from './type.repository';

export class AttackRepository extends DefaultCrudRepository<
  Attack,
  typeof Attack.prototype.id,
  AttackRelations
> {


  public readonly critterAttacks: HasManyRepositoryFactory<CritterAttack, typeof Attack.prototype.id>;

  public readonly type: BelongsToAccessor<Type, typeof Attack.prototype.id>;

  constructor(
    @inject('datasources.db') dataSource: DbDataSource, @repository.getter('CritterAttackRepository') protected critterAttackRepositoryGetter: Getter<CritterAttackRepository>, @repository.getter('TypeRepository') protected typeRepositoryGetter: Getter<TypeRepository>,
  ) {
    super(Attack, dataSource);
    this.type = this.createBelongsToAccessorFor('type', typeRepositoryGetter,);
    this.registerInclusionResolver('type', this.type.inclusionResolver);
    this.critterAttacks = this.createHasManyRepositoryFactoryFor('critterAttacks', critterAttackRepositoryGetter,);
    this.registerInclusionResolver('critterAttacks', this.critterAttacks.inclusionResolver);
  }
}
