import {
  repository,
} from '@loopback/repository';
import {
  param,
  get,
  getModelSchemaRef,
} from '@loopback/rest';
import {
  CritterCopy,
  Trainer,
} from '../models';
import {CritterCopyRepository} from '../repositories';

export class CritterCopyTrainerController {
  constructor(
    @repository(CritterCopyRepository)
    public critterCopyRepository: CritterCopyRepository,
  ) { }

  @get('/critter-copies/{id}/trainer', {
    responses: {
      '200': {
        description: 'Trainer belonging to CritterCopy',
        content: {
          'application/json': {
            schema: getModelSchemaRef(Trainer),
          },
        },
      },
    },
  })
  async getTrainer(
    @param.path.number('id') id: typeof CritterCopy.prototype.id,
  ): Promise<Trainer> {
    return this.critterCopyRepository.trainer(id);
  }
}
