import {service} from '@loopback/core';
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
  CritterTemplate,
  CritterUsable,
} from '../models';
import {CritterRepository} from '../repositories';
import {CritterStatsService} from '../services/critter-stats.service';

export class CritterCritterTemplateController {
  constructor(
    @service(CritterStatsService) protected critterStatsService: CritterStatsService,
    @repository(CritterRepository)
    public critterRepository: CritterRepository,
  ) { }

  @get('/critters/{id}/critter-template', {
    responses: {
      '200': {
        description: 'CritterTemplate belonging to Critter',
        content: {
          'application/json': {
            schema: getModelSchemaRef(CritterTemplate),
          },
        },
      },
    },
  })
  async getCritterTemplate(
    @param.path.number('id') id: typeof Critter.prototype.id,
  ): Promise<CritterTemplate> {
    return this.critterRepository.critterTemplate(id);
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

