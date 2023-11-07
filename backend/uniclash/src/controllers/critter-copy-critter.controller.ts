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
  Critter,
} from '../models';
import {CritterCopyRepository} from '../repositories';
import {authenticate} from '@loopback/authentication';
//@authenticate('jwt')
export class CritterCopyCritterController {
  constructor(
    @repository(CritterCopyRepository)
    public critterCopyRepository: CritterCopyRepository,
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
}
