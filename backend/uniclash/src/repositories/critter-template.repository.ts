import {Getter, inject} from '@loopback/core';
import {DefaultCrudRepository, HasManyRepositoryFactory, HasOneRepositoryFactory, repository} from '@loopback/repository';
import {DbDataSource} from '../datasources';
import {Critter, CritterTemplate, CritterTemplateRelations} from '../models';
import {CritterRepository} from './critter.repository';

export class CritterTemplateRepository extends DefaultCrudRepository<
  CritterTemplate,
  typeof CritterTemplate.prototype.id,
  CritterTemplateRelations
> {

  public readonly critters: HasManyRepositoryFactory<Critter, typeof CritterTemplate.prototype.id>;

  public readonly evolvesInto: HasOneRepositoryFactory<CritterTemplate, typeof CritterTemplate.prototype.id>;

  constructor(
    @inject('datasources.db') dataSource: DbDataSource, @repository.getter('CritterRepository') protected critterRepositoryGetter: Getter<CritterRepository>, @repository.getter('CritterTemplateRepository') protected critterTemplateRepositoryGetter: Getter<CritterTemplateRepository>,
  ) {
    super(CritterTemplate, dataSource);
    this.evolvesInto = this.createHasOneRepositoryFactoryFor('evolvesInto', critterTemplateRepositoryGetter);
    this.registerInclusionResolver('evolvesInto', this.evolvesInto.inclusionResolver);
    this.critters = this.createHasManyRepositoryFactoryFor('critters', critterRepositoryGetter,);
    this.registerInclusionResolver('critters', this.critters.inclusionResolver);
  }
}
