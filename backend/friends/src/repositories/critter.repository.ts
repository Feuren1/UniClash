import {inject} from '@loopback/core';
import {DefaultCrudRepository} from '@loopback/repository';
import {DbDataSource} from '../datasources';
import {Critter, CritterRelations} from '../models';

export class CritterRepository extends DefaultCrudRepository<
  Critter,
  typeof Critter.prototype.id,
  CritterRelations
> {
  constructor(
    @inject('datasources.db') dataSource: DbDataSource,
  ) {
    super(Critter, dataSource);
  }
}
