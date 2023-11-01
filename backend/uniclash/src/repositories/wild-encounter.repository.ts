import {inject} from '@loopback/core';
import {DefaultCrudRepository} from '@loopback/repository';
import {DbDataSource} from '../datasources';
import {WildEncounter, WildEncounterRelations} from '../models';

export class WildEncounterRepository extends DefaultCrudRepository<
  WildEncounter,
  typeof WildEncounter.prototype.id,
  WildEncounterRelations
> {
  constructor(
    @inject('datasources.db') dataSource: DbDataSource,
  ) {
    super(WildEncounter, dataSource);
  }
}
