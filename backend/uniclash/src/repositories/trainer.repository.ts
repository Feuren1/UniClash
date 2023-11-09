import {inject, Getter} from '@loopback/core';
import {DefaultCrudRepository, repository, HasManyRepositoryFactory} from '@loopback/repository';
import {DbDataSource} from '../datasources';
import {Trainer, TrainerRelations, CritterCopy} from '../models';
import {CritterCopyRepository} from './critter-copy.repository';

export class TrainerRepository extends DefaultCrudRepository<
  Trainer,
  typeof Trainer.prototype.id,
  TrainerRelations
> {

  public readonly critterCopies: HasManyRepositoryFactory<CritterCopy, typeof Trainer.prototype.id>;

  constructor(
    @inject('datasources.db') dataSource: DbDataSource, @repository.getter('CritterCopyRepository') protected critterCopyRepositoryGetter: Getter<CritterCopyRepository>,
  ) {
    super(Trainer, dataSource);
    this.critterCopies = this.createHasManyRepositoryFactoryFor('critterCopies', critterCopyRepositoryGetter,);
    this.registerInclusionResolver('critterCopies', this.critterCopies.inclusionResolver);
  }
}
