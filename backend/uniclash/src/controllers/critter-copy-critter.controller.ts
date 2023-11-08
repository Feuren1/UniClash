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
} from '../models';
import {CritterCopyRepository, CritterRepository} from '../repositories';
import {CritterStatsService} from '../services/critter-stats.service';
//@authenticate('jwt')
export class CritterCopyCritterController {
  critterStatsService: CritterStatsService = new CritterStatsService(this.critterRepository, this.critterCopyRepository);
  constructor(
    @repository(CritterCopyRepository)
    public critterCopyRepository: CritterCopyRepository,
    @repository(CritterRepository)
    public critterRepository: CritterRepository
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
}
