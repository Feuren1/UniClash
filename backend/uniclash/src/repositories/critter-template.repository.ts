import {inject, Getter} from '@loopback/core';
import {DefaultCrudRepository, repository, HasManyRepositoryFactory} from '@loopback/repository';
import {DbDataSource} from '../datasources';
import {CritterTemplate, CritterTemplateRelations, Critter} from '../models';
import {CritterRepository} from './critter.repository';

export class CritterTemplateRepository extends DefaultCrudRepository<
  CritterTemplate,
  typeof CritterTemplate.prototype.id,
  CritterTemplateRelations
> {

  public readonly critters: HasManyRepositoryFactory<Critter, typeof CritterTemplate.prototype.id>;

  constructor(
    @inject('datasources.db') dataSource: DbDataSource, @repository.getter('CritterRepository') protected critterRepositoryGetter: Getter<CritterRepository>,
  ) {
    super(CritterTemplate, dataSource);
    this.critters = this.createHasManyRepositoryFactoryFor('critters', critterRepositoryGetter,);
    this.registerInclusionResolver('critters', this.critters.inclusionResolver);
  }
}
