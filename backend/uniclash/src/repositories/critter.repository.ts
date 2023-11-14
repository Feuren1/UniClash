import {inject, Getter} from '@loopback/core';
import {DefaultCrudRepository, repository, HasManyRepositoryFactory, BelongsToAccessor} from '@loopback/repository';
import {DbDataSource} from '../datasources';
import {Critter, CritterRelations, CritterAttack, CritterTemplate, Student} from '../models';
import {CritterAttackRepository} from './critter-attack.repository';
import {CritterTemplateRepository} from './critter-template.repository';
import {StudentRepository} from './student.repository';

export class CritterRepository extends DefaultCrudRepository<
  Critter,
  typeof Critter.prototype.id,
  CritterRelations
> {

  public readonly critterAttacks: HasManyRepositoryFactory<CritterAttack, typeof Critter.prototype.id>;

  public readonly critterTemplate: BelongsToAccessor<CritterTemplate, typeof Critter.prototype.id>;

  public readonly student: BelongsToAccessor<Student, typeof Critter.prototype.id>;

  constructor(
    @inject('datasources.db') dataSource: DbDataSource, @repository.getter('CritterAttackRepository') protected critterAttackRepositoryGetter: Getter<CritterAttackRepository>, @repository.getter('CritterTemplateRepository') protected critterTemplateRepositoryGetter: Getter<CritterTemplateRepository>, @repository.getter('StudentRepository') protected studentRepositoryGetter: Getter<StudentRepository>,
  ) {
    super(Critter, dataSource);
    this.student = this.createBelongsToAccessorFor('student', studentRepositoryGetter,);
    this.registerInclusionResolver('student', this.student.inclusionResolver);
    this.critterTemplate = this.createBelongsToAccessorFor('critterTemplate', critterTemplateRepositoryGetter,);
    this.registerInclusionResolver('critterTemplate', this.critterTemplate.inclusionResolver);
    this.critterAttacks = this.createHasManyRepositoryFactoryFor('critterAttacks', critterAttackRepositoryGetter,);
    this.registerInclusionResolver('critterAttacks', this.critterAttacks.inclusionResolver);
  }
}
