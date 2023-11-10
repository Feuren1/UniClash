import {inject, Getter} from '@loopback/core';
import {DefaultCrudRepository, repository, HasManyRepositoryFactory} from '@loopback/repository';
import {DbDataSource} from '../datasources';
import {Student, StudentRelations, Critter} from '../models';
import {CritterRepository} from './critter.repository';

export class StudentRepository extends DefaultCrudRepository<
  Student,
  typeof Student.prototype.id,
  StudentRelations
> {

  public readonly critters: HasManyRepositoryFactory<Critter, typeof Student.prototype.id>;

  constructor(
    @inject('datasources.db') dataSource: DbDataSource, @repository.getter('CritterRepository') protected critterRepositoryGetter: Getter<CritterRepository>,
  ) {
    super(Student, dataSource);
    this.critters = this.createHasManyRepositoryFactoryFor('critters', critterRepositoryGetter,);
    this.registerInclusionResolver('critters', this.critters.inclusionResolver);
  }
}
