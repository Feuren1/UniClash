import {inject} from '@loopback/core';
import {DefaultCrudRepository} from '@loopback/repository';
import {DbDataSource} from '../datasources';
import {Attack, AttackRelations} from '../models';

export class AttackRepository extends DefaultCrudRepository<
  Attack,
  typeof Attack.prototype.id,
  AttackRelations
> {
  constructor(
    @inject('datasources.db') dataSource: DbDataSource,
  ) {
    super(Attack, dataSource);
  }
}
