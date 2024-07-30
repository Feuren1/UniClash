import {inject} from '@loopback/core';
import {DefaultCrudRepository} from '@loopback/repository';
import {DbDataSource} from '../datasources';
import {OnlineFight, OnlineFightRelations} from '../models';

export class OnlineFightRepository extends DefaultCrudRepository<
  OnlineFight,
  typeof OnlineFight.prototype.fightId,
  OnlineFightRelations
> {
  constructor(
    @inject('datasources.db') dataSource: DbDataSource,
  ) {
    super(OnlineFight, dataSource);
  }
}
