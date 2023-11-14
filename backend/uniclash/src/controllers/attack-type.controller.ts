import {
  repository,
} from '@loopback/repository';
import {
  param,
  get,
  getModelSchemaRef,
} from '@loopback/rest';
import {
  Attack,
  Type,
} from '../models';
import {AttackRepository} from '../repositories';

export class AttackTypeController {
  constructor(
    @repository(AttackRepository)
    public attackRepository: AttackRepository,
  ) { }

  @get('/attacks/{id}/type', {
    responses: {
      '200': {
        description: 'Type belonging to Attack',
        content: {
          'application/json': {
            schema: getModelSchemaRef(Type),
          },
        },
      },
    },
  })
  async getType(
    @param.path.number('id') id: typeof Attack.prototype.id,
  ): Promise<Type> {
    return this.attackRepository.type(id);
  }
}
