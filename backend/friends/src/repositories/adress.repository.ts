import {inject, Getter} from '@loopback/core';
import {DefaultCrudRepository, repository, BelongsToAccessor} from '@loopback/repository';
import {DbDataSource} from '../datasources';
import {Adress, AdressRelations, Friend} from '../models';
import {FriendRepository} from './friend.repository';

export class AdressRepository extends DefaultCrudRepository<
  Adress,
  typeof Adress.prototype.id,
  AdressRelations
> {

  public readonly friend: BelongsToAccessor<Friend, typeof Adress.prototype.id>;

  constructor(
    @inject('datasources.db') dataSource: DbDataSource, @repository.getter('FriendRepository') protected friendRepositoryGetter: Getter<FriendRepository>,
  ) {
    super(Adress, dataSource);
    this.friend = this.createBelongsToAccessorFor('friend', friendRepositoryGetter,);
    this.registerInclusionResolver('friend', this.friend.inclusionResolver);
  }
}
