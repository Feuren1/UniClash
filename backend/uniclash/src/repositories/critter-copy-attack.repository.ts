import {inject} from '@loopback/core';
import {DefaultCrudRepository} from '@loopback/repository';
import {DbDataSource} from '../datasources';
import {CritterCopyAttack, CritterCopyAttackRelations} from '../models';

export class CritterCopyAttackRepository extends DefaultCrudRepository<
  CritterCopyAttack,
  typeof CritterCopyAttack.prototype.id,
  CritterCopyAttackRelations
> {
  constructor(
    @inject('datasources.db') dataSource: DbDataSource,
  ) {
    super(CritterCopyAttack, dataSource);
  }
}
