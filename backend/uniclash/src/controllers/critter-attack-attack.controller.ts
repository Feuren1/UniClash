import {
  repository,
} from '@loopback/repository';
import {
  param,
  get,
  getModelSchemaRef,
} from '@loopback/rest';
import {
  CritterAttack,
  Attack,
} from '../models';
import {CritterAttackRepository} from '../repositories';

export class CritterAttackAttackController {
  constructor(
    @repository(CritterAttackRepository)
    public critterAttackRepository: CritterAttackRepository,
  ) { }

  @get('/critter-attacks/{id}/attack', {
    responses: {
      '200': {
        description: 'Attack belonging to CritterAttack',
        content: {
          'application/json': {
            schema: getModelSchemaRef(Attack),
          },
        },
      },
    },
  })
  async getAttack(
    @param.path.number('id') id: typeof CritterAttack.prototype.id,
  ): Promise<Attack> {
    return this.critterAttackRepository.attack(id);
  }
}
