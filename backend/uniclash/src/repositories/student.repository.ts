import {inject, Getter} from '@loopback/core';
import {DefaultCrudRepository, repository, HasManyRepositoryFactory, BelongsToAccessor} from '@loopback/repository';
import {DbDataSource} from '../datasources';
import {Student, StudentRelations, Critter, User} from '../models';
import {CritterRepository} from './critter.repository';
import {UserRepository} from './user.repository';

export class StudentRepository extends DefaultCrudRepository<
  Student,
  typeof Student.prototype.id,
  StudentRelations
> {

  public readonly critters: HasManyRepositoryFactory<Critter, typeof Student.prototype.id>;

  public readonly user: BelongsToAccessor<User, typeof Student.prototype.id>;

  constructor(
    @inject('datasources.db') dataSource: DbDataSource, @repository.getter('CritterRepository') protected critterRepositoryGetter: Getter<CritterRepository>, @repository.getter('UserRepository') protected userRepositoryGetter: Getter<UserRepository>,
  ) {
    super(Student, dataSource);
    this.user = this.createBelongsToAccessorFor('user', userRepositoryGetter,);
    this.registerInclusionResolver('user', this.user.inclusionResolver);
    this.critters = this.createHasManyRepositoryFactoryFor('critters', critterRepositoryGetter,);
    this.registerInclusionResolver('critters', this.critters.inclusionResolver);
  }
}
