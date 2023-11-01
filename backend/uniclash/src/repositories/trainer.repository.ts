import {inject} from '@loopback/core';
import {DefaultCrudRepository} from '@loopback/repository';
import {DbDataSource} from '../datasources';
import {Trainer, TrainerRelations} from '../models';

export class TrainerRepository extends DefaultCrudRepository<
  Trainer,
  typeof Trainer.prototype.id,
  TrainerRelations
> {
  constructor(
    @inject('datasources.db') dataSource: DbDataSource,
  ) {
    super(Trainer, dataSource);
  }
}
