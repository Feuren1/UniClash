import {
  repository,
} from '@loopback/repository';
import {
  get,
  getModelSchemaRef,
  param,
} from '@loopback/rest';
import {
  Critter,
  CritterCopy,
  CritterUsable,
} from '../models';
import {AttackRepository, CritterCopyAttackRepository, CritterCopyRepository, CritterRepository} from '../repositories';
import {CritterStatsService} from '../services/critter-stats.service';
//@authenticate('jwt')
export class CritterCopyCritterController {
  critterStatsService: CritterStatsService = new CritterStatsService(this.critterRepository, this.critterCopyRepository, this.critterCopyAttackRepository, this.attackRepository);
  constructor(
    @repository(CritterCopyRepository)
    public critterCopyRepository: CritterCopyRepository,
    @repository(CritterRepository)
    public critterRepository: CritterRepository,
    @repository(CritterCopyAttackRepository)
    public critterCopyAttackRepository: CritterCopyAttackRepository,
    @repository(AttackRepository)
    public attackRepository: AttackRepository
  ) { }

  @get('/critter-copies/{id}/critter', {
    responses: {
      '200': {
        description: 'Critter belonging to CritterCopy',
        content: {
          'application/json': {
            schema: getModelSchemaRef(Critter),
          },
        },
      },
    },
  })
  async getCritter(
    @param.path.number('id') id: typeof CritterCopy.prototype.id,
  ): Promise<Critter> {
    return this.critterCopyRepository.critter(id);
  }
  @get('/critters/{id}/actual-stats', {
    responses: {
      '200': {
        description: 'Calculate and return actual stats for a critter',
        content: {
          'application/json': {
            schema: {type: 'array', items: {type: 'number'}},
          },
        },
      },
    },
  })
  async calculateActualStats(
    @param.path.number('id') id: number,
  ): Promise<number[]> {
    // Call the service to calculate the actual stats
    return this.critterStatsService.calculateActualStats(id);
  }

  @get('/critters/{id}/usable', {
    responses: {
      '200': {
        description: 'Calculate and return CritterUsable for a critter',
        content: {
          'application/json': {
            schema: getModelSchemaRef(CritterUsable), // Use CritterUsable schema
          },
        },
      },
    },
  })
  async calculateCritterUsable(
    @param.path.number('id') id: number,
  ): Promise<CritterUsable> {
    return this.critterStatsService.createCritterUsable(id);
  }
}
