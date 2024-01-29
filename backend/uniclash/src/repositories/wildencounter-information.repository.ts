import {inject} from '@loopback/core';
import {DefaultCrudRepository} from '@loopback/repository';
import {DbDataSource} from '../datasources';
import {WildencounterInformation, WildencounterInformationRelations} from '../models';

export class WildencounterInformationRepository extends DefaultCrudRepository<
  WildencounterInformation,
  typeof WildencounterInformation.prototype.date,
  WildencounterInformationRelations
> {
  constructor(
    @inject('datasources.db') dataSource: DbDataSource,
  ) {
    super(WildencounterInformation, dataSource);
  }
}
