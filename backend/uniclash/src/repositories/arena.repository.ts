import {inject, Getter} from '@loopback/core';
import {DefaultCrudRepository, repository, BelongsToAccessor} from '@loopback/repository';
import {DbDataSource} from '../datasources';
import {Arena, ArenaRelations, Student} from '../models';
import {StudentRepository} from './student.repository';

export class ArenaRepository extends DefaultCrudRepository<
  Arena,
  typeof Arena.prototype.id,
  ArenaRelations
> {

  public readonly student: BelongsToAccessor<Student, typeof Arena.prototype.id>;

  constructor(
    @inject('datasources.db') dataSource: DbDataSource, @repository.getter('StudentRepository') protected studentRepositoryGetter: Getter<StudentRepository>,
  ) {
    super(Arena, dataSource);
    this.student = this.createBelongsToAccessorFor('student', studentRepositoryGetter,);
    this.registerInclusionResolver('student', this.student.inclusionResolver);
  }
}
