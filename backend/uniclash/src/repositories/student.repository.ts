import {inject, Getter} from '@loopback/core';
import {DefaultCrudRepository, repository, HasManyRepositoryFactory} from '@loopback/repository';
import {DbDataSource} from '../datasources';
import {Student, StudentRelations, Critter, Arena, Item} from '../models';
import {CritterRepository} from './critter.repository';
import {ArenaRepository} from './arena.repository';
import {ItemRepository} from './item.repository';

export class StudentRepository extends DefaultCrudRepository<
  Student,
  typeof Student.prototype.id,
  StudentRelations
> {

  public readonly critters: HasManyRepositoryFactory<Critter, typeof Student.prototype.id>;

  public readonly arenas: HasManyRepositoryFactory<Arena, typeof Student.prototype.id>;

  public readonly items: HasManyRepositoryFactory<Item, typeof Student.prototype.id>;

  constructor(
    @inject('datasources.db') dataSource: DbDataSource, @repository.getter('CritterRepository') protected critterRepositoryGetter: Getter<CritterRepository>, @repository.getter('ArenaRepository') protected arenaRepositoryGetter: Getter<ArenaRepository>, @repository.getter('ItemRepository') protected itemRepositoryGetter: Getter<ItemRepository>,
  ) {
    super(Student, dataSource);
    this.items = this.createHasManyRepositoryFactoryFor('items', itemRepositoryGetter,);
    this.registerInclusionResolver('items', this.items.inclusionResolver);
    this.arenas = this.createHasManyRepositoryFactoryFor('arenas', arenaRepositoryGetter,);
    this.registerInclusionResolver('arenas', this.arenas.inclusionResolver);
    this.critters = this.createHasManyRepositoryFactoryFor('critters', critterRepositoryGetter,);
    this.registerInclusionResolver('critters', this.critters.inclusionResolver);
  }
}
