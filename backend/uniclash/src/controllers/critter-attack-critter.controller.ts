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
  Critter,
} from '../models';
import {CritterAttackRepository} from '../repositories';
import {authenticate} from "@loopback/authentication";

export class CritterAttackCritterController {
  constructor(
    @repository(CritterAttackRepository)
    public critterAttackRepository: CritterAttackRepository,
  ) { }

  @authenticate('jwt')
  @get('/critter-attacks/{id}/critter', {
    responses: {
      '200': {
        description: 'Critter belonging to CritterAttack',
        content: {
          'application/json': {
            schema: getModelSchemaRef(Critter),
          },
        },
      },
    },
  })
  async getCritter(
    @param.path.number('id') id: typeof CritterAttack.prototype.id,
  ): Promise<Critter> {
    return this.critterAttackRepository.critter(id);
  }
}
