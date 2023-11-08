import {
  Count,
  CountSchema,
  Filter,
  FilterExcludingWhere,
  repository,
  Where,
} from '@loopback/repository';
import {
  post,
  param,
  get,
  getModelSchemaRef,
  patch,
  put,
  del,
  requestBody,
  response,
} from '@loopback/rest';
import {CritterCopyAttack} from '../models';
import {CritterCopyAttackRepository} from '../repositories';

export class CritterCopyAttackController {
  constructor(
    @repository(CritterCopyAttackRepository)
    public critterCopyAttackRepository : CritterCopyAttackRepository,
  ) {}

  @post('/critter-copy-attacks')
  @response(200, {
    description: 'CritterCopyAttack model instance',
    content: {'application/json': {schema: getModelSchemaRef(CritterCopyAttack)}},
  })
  async create(
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(CritterCopyAttack, {
            title: 'NewCritterCopyAttack',
            exclude: ['id'],
          }),
        },
      },
    })
    critterCopyAttack: Omit<CritterCopyAttack, 'id'>,
  ): Promise<CritterCopyAttack> {
    return this.critterCopyAttackRepository.create(critterCopyAttack);
  }

  @get('/critter-copy-attacks/count')
  @response(200, {
    description: 'CritterCopyAttack model count',
    content: {'application/json': {schema: CountSchema}},
  })
  async count(
    @param.where(CritterCopyAttack) where?: Where<CritterCopyAttack>,
  ): Promise<Count> {
    return this.critterCopyAttackRepository.count(where);
  }

  @get('/critter-copy-attacks')
  @response(200, {
    description: 'Array of CritterCopyAttack model instances',
    content: {
      'application/json': {
        schema: {
          type: 'array',
          items: getModelSchemaRef(CritterCopyAttack, {includeRelations: true}),
        },
      },
    },
  })
  async find(
    @param.filter(CritterCopyAttack) filter?: Filter<CritterCopyAttack>,
  ): Promise<CritterCopyAttack[]> {
    return this.critterCopyAttackRepository.find(filter);
  }

  @patch('/critter-copy-attacks')
  @response(200, {
    description: 'CritterCopyAttack PATCH success count',
    content: {'application/json': {schema: CountSchema}},
  })
  async updateAll(
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(CritterCopyAttack, {partial: true}),
        },
      },
    })
    critterCopyAttack: CritterCopyAttack,
    @param.where(CritterCopyAttack) where?: Where<CritterCopyAttack>,
  ): Promise<Count> {
    return this.critterCopyAttackRepository.updateAll(critterCopyAttack, where);
  }

  @get('/critter-copy-attacks/{id}')
  @response(200, {
    description: 'CritterCopyAttack model instance',
    content: {
      'application/json': {
        schema: getModelSchemaRef(CritterCopyAttack, {includeRelations: true}),
      },
    },
  })
  async findById(
    @param.path.number('id') id: number,
    @param.filter(CritterCopyAttack, {exclude: 'where'}) filter?: FilterExcludingWhere<CritterCopyAttack>
  ): Promise<CritterCopyAttack> {
    return this.critterCopyAttackRepository.findById(id, filter);
  }

  @patch('/critter-copy-attacks/{id}')
  @response(204, {
    description: 'CritterCopyAttack PATCH success',
  })
  async updateById(
    @param.path.number('id') id: number,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(CritterCopyAttack, {partial: true}),
        },
      },
    })
    critterCopyAttack: CritterCopyAttack,
  ): Promise<void> {
    await this.critterCopyAttackRepository.updateById(id, critterCopyAttack);
  }

  @put('/critter-copy-attacks/{id}')
  @response(204, {
    description: 'CritterCopyAttack PUT success',
  })
  async replaceById(
    @param.path.number('id') id: number,
    @requestBody() critterCopyAttack: CritterCopyAttack,
  ): Promise<void> {
    await this.critterCopyAttackRepository.replaceById(id, critterCopyAttack);
  }

  @del('/critter-copy-attacks/{id}')
  @response(204, {
    description: 'CritterCopyAttack DELETE success',
  })
  async deleteById(@param.path.number('id') id: number): Promise<void> {
    await this.critterCopyAttackRepository.deleteById(id);
  }
}
