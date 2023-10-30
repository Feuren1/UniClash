import {
  repository,
} from '@loopback/repository';
import {
  param,
  get,
  getModelSchemaRef,
} from '@loopback/rest';
import {
  Adress,
  Friend,
} from '../models';
import {AdressRepository} from '../repositories';

export class AdressFriendController {
  constructor(
    @repository(AdressRepository)
    public adressRepository: AdressRepository,
  ) { }

  @get('/adresses/{id}/friend', {
    responses: {
      '200': {
        description: 'Friend belonging to Adress',
        content: {
          'application/json': {
            schema: getModelSchemaRef(Friend),
          },
        },
      },
    },
  })
  async getFriend(
    @param.path.number('id') id: typeof Adress.prototype.id,
  ): Promise<Friend> {
    return this.adressRepository.friend(id);
  }
}
