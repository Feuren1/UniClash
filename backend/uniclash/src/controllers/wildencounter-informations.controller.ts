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
import {WildencounterInformation} from '../models';
import {WildencounterInformationRepository} from '../repositories';

export class WildencounterInformationsController {
  constructor(
    @repository(WildencounterInformationRepository)
    public wildencounterInformationRepository : WildencounterInformationRepository,
  ) {}

  @post('/wildencounter-informations')
  @response(200, {
    description: 'WildencounterInformation model instance',
    content: {'application/json': {schema: getModelSchemaRef(WildencounterInformation)}},
  })
  async create(
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(WildencounterInformation, {
            title: 'NewWildencounterInformation',
            
          }),
        },
      },
    })
    wildencounterInformation: WildencounterInformation,
  ): Promise<WildencounterInformation> {
    return this.wildencounterInformationRepository.create(wildencounterInformation);
  }

  @get('/wildencounter-informations/count')
  @response(200, {
    description: 'WildencounterInformation model count',
    content: {'application/json': {schema: CountSchema}},
  })
  async count(
    @param.where(WildencounterInformation) where?: Where<WildencounterInformation>,
  ): Promise<Count> {
    return this.wildencounterInformationRepository.count(where);
  }

  @get('/wildencounter-informations')
  @response(200, {
    description: 'Array of WildencounterInformation model instances',
    content: {
      'application/json': {
        schema: {
          type: 'array',
          items: getModelSchemaRef(WildencounterInformation, {includeRelations: true}),
        },
      },
    },
  })
  async find(
    @param.filter(WildencounterInformation) filter?: Filter<WildencounterInformation>,
  ): Promise<WildencounterInformation[]> {
    return this.wildencounterInformationRepository.find(filter);
  }

  @patch('/wildencounter-informations')
  @response(200, {
    description: 'WildencounterInformation PATCH success count',
    content: {'application/json': {schema: CountSchema}},
  })
  async updateAll(
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(WildencounterInformation, {partial: true}),
        },
      },
    })
    wildencounterInformation: WildencounterInformation,
    @param.where(WildencounterInformation) where?: Where<WildencounterInformation>,
  ): Promise<Count> {
    return this.wildencounterInformationRepository.updateAll(wildencounterInformation, where);
  }

  @get('/wildencounter-informations/{id}')
  @response(200, {
    description: 'WildencounterInformation model instance',
    content: {
      'application/json': {
        schema: getModelSchemaRef(WildencounterInformation, {includeRelations: true}),
      },
    },
  })
  async findById(
    @param.path.string('id') id: string,
    @param.filter(WildencounterInformation, {exclude: 'where'}) filter?: FilterExcludingWhere<WildencounterInformation>
  ): Promise<WildencounterInformation> {
    return this.wildencounterInformationRepository.findById(id, filter);
  }

  @patch('/wildencounter-informations/{id}')
  @response(204, {
    description: 'WildencounterInformation PATCH success',
  })
  async updateById(
    @param.path.string('id') id: string,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(WildencounterInformation, {partial: true}),
        },
      },
    })
    wildencounterInformation: WildencounterInformation,
  ): Promise<void> {
    await this.wildencounterInformationRepository.updateById(id, wildencounterInformation);
  }

  @put('/wildencounter-informations/{id}')
  @response(204, {
    description: 'WildencounterInformation PUT success',
  })
  async replaceById(
    @param.path.string('id') id: string,
    @requestBody() wildencounterInformation: WildencounterInformation,
  ): Promise<void> {
    await this.wildencounterInformationRepository.replaceById(id, wildencounterInformation);
  }

  @del('/wildencounter-informations/{id}')
  @response(204, {
    description: 'WildencounterInformation DELETE success',
  })
  async deleteById(@param.path.string('id') id: string): Promise<void> {
    await this.wildencounterInformationRepository.deleteById(id);
  }
}
