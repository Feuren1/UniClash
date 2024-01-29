import {inject} from '@loopback/core';
import {DefaultCrudRepository} from '@loopback/repository';
import {DbDataSource} from '../datasources';
import {CritterInFight, CritterInFightRelations} from '../models';

export class CritterInFightRepository extends DefaultCrudRepository<
  CritterInFight,
  typeof CritterInFight.prototype.critterId,
  CritterInFightRelations
> {
  constructor(
    @inject('datasources.db') dataSource: DbDataSource,
  ) {
    super(CritterInFight, dataSource);
  }
}
