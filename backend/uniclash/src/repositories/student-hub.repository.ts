import {inject} from '@loopback/core';
import {DefaultCrudRepository} from '@loopback/repository';
import {DbDataSource} from '../datasources';
import {StudentHub, StudentHubRelations} from '../models';

export class StudentHubRepository extends DefaultCrudRepository<
  StudentHub,
  typeof StudentHub.prototype.id,
  StudentHubRelations
> {
  constructor(
    @inject('datasources.db') dataSource: DbDataSource,
  ) {
    super(StudentHub, dataSource);
  }
}
