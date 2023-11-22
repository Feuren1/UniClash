import {Getter, inject} from '@loopback/core';
import {BelongsToAccessor, DefaultCrudRepository, HasManyRepositoryFactory, repository} from '@loopback/repository';
import {DbDataSource} from '../datasources';
import {Arena, Critter, Item, Student, StudentRelations, User} from '../models';
import {ArenaRepository} from './arena.repository';
import {CritterRepository} from './critter.repository';
import {ItemRepository} from './item.repository';
import {UserRepository} from './user.repository';

export class StudentRepository extends DefaultCrudRepository<
  Student,
  typeof Student.prototype.id,
  StudentRelations
> {

  public readonly critters: HasManyRepositoryFactory<Critter, typeof Student.prototype.id>;

  public readonly arenas: HasManyRepositoryFactory<Arena, typeof Student.prototype.id>;

  public readonly items: HasManyRepositoryFactory<Item, typeof Student.prototype.id>;

  public readonly user: BelongsToAccessor<User, typeof Student.prototype.id>;
  constructor(
    @inject('datasources.db') dataSource: DbDataSource, @repository.getter('CritterRepository') protected critterRepositoryGetter: Getter<CritterRepository>, @repository.getter('ArenaRepository') protected arenaRepositoryGetter: Getter<ArenaRepository>, @repository.getter('ItemRepository') protected itemRepositoryGetter: Getter<ItemRepository>, @repository.getter('UserRepository') protected userRepositoryGetter: Getter<UserRepository>,
  ) {
    super(Student, dataSource);
    this.items = this.createHasManyRepositoryFactoryFor('items', itemRepositoryGetter,);
    this.registerInclusionResolver('items', this.items.inclusionResolver);
    this.arenas = this.createHasManyRepositoryFactoryFor('arenas', arenaRepositoryGetter,);
    this.registerInclusionResolver('arenas', this.arenas.inclusionResolver);
    this.critters = this.createHasManyRepositoryFactoryFor('critters', critterRepositoryGetter,);
    this.registerInclusionResolver('critters', this.critters.inclusionResolver);
    this.user = this.createBelongsToAccessorFor('user', userRepositoryGetter,);
    this.registerInclusionResolver('user', this.user.inclusionResolver);
  }
}
