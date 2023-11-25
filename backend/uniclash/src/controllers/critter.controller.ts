import {service} from '@loopback/core';
import {
  Count,
  CountSchema,
  Filter,
  FilterExcludingWhere,
  repository,
  Where,
} from '@loopback/repository';
import {
  del,
  get,
  getModelSchemaRef,
  param,
  patch,
  post,
  put,
  requestBody,
  response,
} from '@loopback/rest';
import {Critter, CritterUsable} from '../models';
import {CritterRepository} from '../repositories';
import {EvolveCritterService} from '../services';
import {StudentCritterService} from '../services/student-critter.service';

export class CritterController {
  constructor(
    @service(EvolveCritterService) protected evolveCritterService: EvolveCritterService,
    @repository(CritterRepository)
    public critterRepository: CritterRepository,
    @service(StudentCritterService) protected studentCritterService: StudentCritterService,
  ) { }

  @post('/critters')
  @response(200, {
    description: 'Critter model instance',
    content: {'application/json': {schema: getModelSchemaRef(Critter)}},
  })
  async create(
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Critter, {
            title: 'NewCritter',
            exclude: ['id'],
          }),
        },
      },
    })
    critter: Omit<Critter, 'id'>,
  ): Promise<Critter> {
    return this.critterRepository.create(critter);
  }

  @get('/critters/count')
  @response(200, {
    description: 'Critter model count',
    content: {'application/json': {schema: CountSchema}},
  })
  async count(
    @param.where(Critter) where?: Where<Critter>,
  ): Promise<Count> {
    return this.critterRepository.count(where);
  }

  @get('/critters')
  @response(200, {
    description: 'Array of Critter model instances',
    content: {
      'application/json': {
        schema: {
          type: 'array',
          items: getModelSchemaRef(Critter, {includeRelations: true}),
        },
      },
    },
  })
  async find(
    @param.filter(Critter) filter?: Filter<Critter>,
  ): Promise<Critter[]> {
    return this.critterRepository.find(filter);
  }

  @patch('/critters')
  @response(200, {
    description: 'Critter PATCH success count',
    content: {'application/json': {schema: CountSchema}},
  })
  async updateAll(
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Critter, {partial: true}),
        },
      },
    })
    critter: Critter,
    @param.where(Critter) where?: Where<Critter>,
  ): Promise<Count> {
    return this.critterRepository.updateAll(critter, where);
  }

  @get('/critters/{id}')
  @response(200, {
    description: 'Critter model instance',
    content: {
      'application/json': {
        schema: getModelSchemaRef(Critter, {includeRelations: true}),
      },
    },
  })
  async findById(
    @param.path.number('id') id: number,
    @param.filter(Critter, {exclude: 'where'}) filter?: FilterExcludingWhere<Critter>
  ): Promise<Critter> {
    return this.critterRepository.findById(id, filter);
  }

  @patch('/critters/{id}')
  @response(204, {
    description: 'Critter PATCH success',
  })
  async updateById(
    @param.path.number('id') id: number,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Critter, {partial: true}),
        },
      },
    })
    critter: Critter,
  ): Promise<void> {
    await this.critterRepository.updateById(id, critter);
  }

  @put('/critters/{id}')
  @response(204, {
    description: 'Critter PUT success',
  })
  async replaceById(
    @param.path.number('id') id: number,
    @requestBody() critter: Critter,
  ): Promise<void> {
    await this.critterRepository.replaceById(id, critter);
  }

  @del('/critters/{id}')
  @response(204, {
    description: 'Critter DELETE success',
  })
  async deleteById(@param.path.number('id') id: number): Promise<void> {
    await this.critterRepository.deleteById(id);
  }

  @get('/critters/{id}/evolve', {
    responses: {
      '200': {
        description: 'Evolve Critter and return Critter',
        content: {
          'application/json': {
            schema: getModelSchemaRef(Critter),
          },
        },
      },
    },
  })
  async evolveCritter(
    @param.path.number('id') id: number,
  ): Promise<Critter> {
    return this.evolveCritterService.evolveCritter(id);
  }

  @get('/critters/{id}/evolveUsable', {
    responses: {
      '200': {
        description: 'Evolve Critter and return CritterUsable',
        content: {
          'application/json': {
            schema: getModelSchemaRef(Critter),
          },
        },
      },
    },
  })
  async evolveCritterUsable(
    @param.path.number('id') id: number,
  ): Promise<CritterUsable> {
    return this.evolveCritterService.evolveCritterUsable(id);
  }

  @get('/usables', {
    responses: {
      '200': {
        description: 'Calculate and return CritterUsable of all critters',
        content: {
          'application/json': {
            schema: getModelSchemaRef(CritterUsable), // Use CritterUsable schema
          },
        },
      },
    },
  })
  async calculateAllCritterUsable(
    @param.path.number('id') id: number,
  ): Promise<CritterUsable[]> {
    return this.studentCritterService.createCritterUsableListOfAll();
  }

}
