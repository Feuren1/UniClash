import {
  Count,
  CountSchema,
  Filter,
  repository,
  Where,
} from '@loopback/repository';
import {
  del,
  get,
  getModelSchemaRef,
  getWhereSchemaFor,
  param,
  patch,
  post,
  requestBody,
} from '@loopback/rest';
import {
  Friend,
  Adress,
} from '../models';
import {FriendRepository} from '../repositories';

export class FriendAdressController {
  constructor(
    @repository(FriendRepository) protected friendRepository: FriendRepository,
  ) { }

  @get('/friends/{id}/adresses', {
    responses: {
      '200': {
        description: 'Array of Friend has many Adress',
        content: {
          'application/json': {
            schema: {type: 'array', items: getModelSchemaRef(Adress)},
          },
        },
      },
    },
  })
  async find(
    @param.path.number('id') id: number,
    @param.query.object('filter') filter?: Filter<Adress>,
  ): Promise<Adress[]> {
    return this.friendRepository.adresses(id).find(filter);
  }

  @post('/friends/{id}/adresses', {
    responses: {
      '200': {
        description: 'Friend model instance',
        content: {'application/json': {schema: getModelSchemaRef(Adress)}},
      },
    },
  })
  async create(
    @param.path.number('id') id: typeof Friend.prototype.id,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Adress, {
            title: 'NewAdressInFriend',
            exclude: ['id'],
            optional: ['friendId']
          }),
        },
      },
    }) adress: Omit<Adress, 'id'>,
  ): Promise<Adress> {
    return this.friendRepository.adresses(id).create(adress);
  }

  @patch('/friends/{id}/adresses', {
    responses: {
      '200': {
        description: 'Friend.Adress PATCH success count',
        content: {'application/json': {schema: CountSchema}},
      },
    },
  })
  async patch(
    @param.path.number('id') id: number,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Adress, {partial: true}),
        },
      },
    })
    adress: Partial<Adress>,
    @param.query.object('where', getWhereSchemaFor(Adress)) where?: Where<Adress>,
  ): Promise<Count> {
    return this.friendRepository.adresses(id).patch(adress, where);
  }

  @del('/friends/{id}/adresses', {
    responses: {
      '200': {
        description: 'Friend.Adress DELETE success count',
        content: {'application/json': {schema: CountSchema}},
      },
    },
  })
  async delete(
    @param.path.number('id') id: number,
    @param.query.object('where', getWhereSchemaFor(Adress)) where?: Where<Adress>,
  ): Promise<Count> {
    return this.friendRepository.adresses(id).delete(where);
  }
}
