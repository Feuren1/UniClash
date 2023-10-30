import {inject, Getter} from '@loopback/core';
import {DefaultCrudRepository, repository, HasManyRepositoryFactory} from '@loopback/repository';
import {DbDataSource} from '../datasources';
import {Friend, FriendRelations, Adress} from '../models';
import {AdressRepository} from './adress.repository';

export class FriendRepository extends DefaultCrudRepository<
  Friend,
  typeof Friend.prototype.id,
  FriendRelations
> {

  public readonly adresses: HasManyRepositoryFactory<Adress, typeof Friend.prototype.id>;

  constructor(
    @inject('datasources.db') dataSource: DbDataSource, @repository.getter('AdressRepository') protected adressRepositoryGetter: Getter<AdressRepository>,
  ) {
    super(Friend, dataSource);
    this.adresses = this.createHasManyRepositoryFactoryFor('adresses', adressRepositoryGetter,);
    this.registerInclusionResolver('adresses', this.adresses.inclusionResolver);
  }
}
