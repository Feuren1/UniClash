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
  Student,
  Item, CritterUsable,
} from '../models';
import {StudentRepository} from '../repositories';
import {ItemUsable} from "../models/item-usable.model";
import {service} from "@loopback/core";
import {StudentItemService} from "../services/student-Item.service";
import {authenticate} from "@loopback/authentication";

export class StudentItemController {
  constructor(
      @repository(StudentRepository) protected studentRepository: StudentRepository,
      @service(StudentItemService) protected studentItemService: StudentItemService,
  ) {
  }

  @authenticate('jwt')
  @get('/students/{id}/items', {
    responses: {
      '200': {
        description: 'Array of Student has many Item',
        content: {
          'application/json': {
            schema: {type: 'array', items: getModelSchemaRef(Item)},
          },
        },
      },
    },
  })
  async find(
      @param.path.number('id') id: number,
      @param.query.object('filter') filter?: Filter<Item>,
  ): Promise<Item[]> {
    return this.studentRepository.items(id).find(filter);
  }

  @authenticate('jwt')
  @post('/students/{id}/items', {
    responses: {
      '200': {
        description: 'Student model instance',
        content: {'application/json': {schema: getModelSchemaRef(Item)}},
      },
    },
  })
  async create(
      @param.path.number('id') id: typeof Student.prototype.id,
      @requestBody({
        content: {
          'application/json': {
            schema: getModelSchemaRef(Item, {
              title: 'NewItemInStudent',
              exclude: ['id'],
              optional: ['studentId']
            }),
          },
        },
      }) item: Omit<Item, 'id'>,
  ): Promise<Item> {
    return this.studentRepository.items(id).create(item);
  }

  @authenticate('jwt')
  @patch('/students/{id}/items', {
    responses: {
      '200': {
        description: 'Student.Item PATCH success count',
        content: {'application/json': {schema: CountSchema}},
      },
    },
  })
  async patch(
      @param.path.number('id') id: number,
      @requestBody({
        content: {
          'application/json': {
            schema: getModelSchemaRef(Item, {partial: true}),
          },
        },
      })
          item: Partial<Item>,
      @param.query.object('where', getWhereSchemaFor(Item)) where?: Where<Item>,
  ): Promise<Count> {
    return this.studentRepository.items(id).patch(item, where);
  }

  @authenticate('jwt')
  @del('/students/{id}/items', {
    responses: {
      '200': {
        description: 'Student.Item DELETE success count',
        content: {'application/json': {schema: CountSchema}},
      },
    },
  })
  async delete(
      @param.path.number('id') id: number,
      @param.query.object('where', getWhereSchemaFor(Item)) where?: Where<Item>,
  ): Promise<Count> {
    return this.studentRepository.items(id).delete(where);
  }

  @authenticate('jwt')
  @get('/students/{id}/Itemusables', {
    responses: {
      '200': {
        description: 'Calculate and return ItemUsable for all items of a student',
        content: {
          'application/json': {
            schema: getModelSchemaRef(ItemUsable),
          },
        },
      },
    },
  })
  async calculateCritterUsable(
      @param.path.number('id') id: number,
  ): Promise<ItemUsable[]> {
    return this.studentItemService.createItemUsableListOnStudentId(id);
  }

  @authenticate('jwt')
  @patch('/students/{studentId}/itemTemplate/{itemTemplateId}/buy', {
    responses: {
      '200': {
        description: 'let a student to buy an item',
        content: {
          'application/json': {
            schema: getModelSchemaRef(ItemUsable),
          },
        },
      },
    },
  })
  async buyItem(
      @param.path.number('studentId') studentId: number,
      @param.path.number('itemTemplateId') itemTemplateId: number,
  ): Promise<Boolean> {
    return this.studentItemService.buyItem(studentId, itemTemplateId);
  }
  @authenticate('jwt')
@patch('/students/{studentId}/itemTemplate/{itemTemplateId}/use', {
  responses: {
    '200': {
      description: 'use an Item from a student',
      content: {
        'application/json': {
          schema: getModelSchemaRef(ItemUsable),
        },
      },
    },
  },
})
async useItem(
    @param.path.number('studentId') studentId: number,
@param.path.number('itemTemplateId') itemTemplateId: number,
): Promise<Boolean> {
  return this.studentItemService.useItem(studentId,itemTemplateId);
}

  @authenticate('jwt')
@patch('/critter/{critterId}/useRedBull', {
  responses: {
    '200': {
      description: 'use an Redbull from a student',
      content: {
        'application/json': {
          schema: getModelSchemaRef(ItemUsable),
        },
      },
    },
  },
})
async useRedBull(
    @param.path.number('critterId') critterId: number,
): Promise<Boolean> {
  return this.studentItemService.useRedBull(critterId);
}
}
