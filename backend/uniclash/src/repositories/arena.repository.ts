import {inject} from '@loopback/core';
import {DefaultCrudRepository} from '@loopback/repository';
import {DbDataSource} from '../datasources';
import {Arena, ArenaRelations} from '../models';

export class ArenaRepository extends DefaultCrudRepository<
  Arena,
  typeof Arena.prototype.id,
  ArenaRelations
> {
  constructor(
    @inject('datasources.db') dataSource: DbDataSource,
  ) {
    super(Arena, dataSource);
  }
}
