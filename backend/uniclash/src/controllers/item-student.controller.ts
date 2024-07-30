import {
  repository,
} from '@loopback/repository';
import {
  param,
  get,
  getModelSchemaRef,
} from '@loopback/rest';
import {
  Item,
  Student,
} from '../models';
import {ItemRepository} from '../repositories';
import {authenticate} from "@loopback/authentication";

export class ItemStudentController {
  constructor(
    @repository(ItemRepository)
    public itemRepository: ItemRepository,
  ) { }

  @authenticate('jwt')
  @get('/items/{id}/student', {
    responses: {
      '200': {
        description: 'Student belonging to Item',
        content: {
          'application/json': {
            schema: getModelSchemaRef(Student),
          },
        },
      },
    },
  })
  async getStudent(
    @param.path.number('id') id: typeof Item.prototype.id,
  ): Promise<Student> {
    return this.itemRepository.student(id);
  }
}
