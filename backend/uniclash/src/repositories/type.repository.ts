import {inject} from '@loopback/core';
import {DefaultCrudRepository} from '@loopback/repository';
import {DbDataSource} from '../datasources';
import {Type, TypeRelations} from '../models';

export class TypeRepository extends DefaultCrudRepository<
  Type,
  typeof Type.prototype.name,
  TypeRelations
> {
  constructor(
    @inject('datasources.db') dataSource: DbDataSource,
  ) {
    super(Type, dataSource);
  }
}
