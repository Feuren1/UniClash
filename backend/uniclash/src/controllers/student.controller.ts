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
import {Student} from '../models';
import {StudentRepository} from '../repositories';
import {LevelCalcStudentService} from "../services";
import {authenticate} from "@loopback/authentication";
import {StudentLocation} from "../models/studentLocation.model";
import {StudentLocationService} from "../services/student-location.service";


export class StudentController {
  constructor(
    @repository(StudentRepository)
    public studentRepository : StudentRepository,
    @service(StudentLocationService) protected studentLocationService: StudentLocationService,
    @service(LevelCalcStudentService) protected levelCalcStudentService: LevelCalcStudentService,
  ) {}

  @authenticate('jwt')
  @post('/students')
  @response(200, {
    description: 'Student model instance',
    content: {'application/json': {schema: getModelSchemaRef(Student)}},
  })
  async create(
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Student, {
            title: 'NewStudent',
            exclude: ['id'],
          }),
        },
      },
    })
    student: Omit<Student, 'id'>,
  ): Promise<Student> {
    return this.studentRepository.create(student);
  }

  @authenticate('jwt')
  @get('/students/count')
  @response(200, {
    description: 'Student model count',
    content: {'application/json': {schema: CountSchema}},
  })
  async count(
    @param.where(Student) where?: Where<Student>,
  ): Promise<Count> {
    return this.studentRepository.count(where);
  }

  @authenticate('jwt')
  @get('/students')
  @response(200, {
    description: 'Array of Student model instances',
    content: {
      'application/json': {
        schema: {
          type: 'array',
          items: getModelSchemaRef(Student, {includeRelations: true}),
        },
      },
    },
  })
  async find(
    @param.filter(Student) filter?: Filter<Student>,
  ): Promise<Student[]> {
    return this.studentRepository.find(filter);
  }

  @authenticate('jwt')
  @patch('/students')
  @response(200, {
    description: 'Student PATCH success count',
    content: {'application/json': {schema: CountSchema}},
  })
  async updateAll(
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Student, {partial: true}),
        },
      },
    })
    student: Student,
    @param.where(Student) where?: Where<Student>,
  ): Promise<Count> {
    return this.studentRepository.updateAll(student, where);
  }

  @authenticate('jwt')
  @get('/students/{id}')
  @response(200, {
    description: 'Student model instance',
    content: {
      'application/json': {
        schema: getModelSchemaRef(Student, {includeRelations: true}),
      },
    },
  })
  async findById(
    @param.path.number('id') id: number,
    @param.filter(Student, {exclude: 'where'}) filter?: FilterExcludingWhere<Student>
  ): Promise<Student> {
    return this.studentRepository.findById(id, filter);
  }

  @authenticate('jwt')
  @patch('/students/{id}')
  @response(204, {
    description: 'Student PATCH success',
  })
  async updateById(
    @param.path.number('id') id: number,
    @requestBody({
      content: {
        'application/json': {
          schema: getModelSchemaRef(Student, {partial: true}),
        },
      },
    })
    student: Student,
  ): Promise<void> {
    await this.studentRepository.updateById(id, student);
  }

  @authenticate('jwt')
  @put('/students/{id}')
  @response(204, {
    description: 'Student PUT success',
  })
  async replaceById(
    @param.path.number('id') id: number,
    @requestBody() student: Student,
  ): Promise<void> {
    await this.studentRepository.replaceById(id, student);
  }

  @authenticate('jwt')
  @del('/students/{id}')
  @response(204, {
    description: 'Student DELETE success',
  })
  async deleteById(@param.path.number('id') id: number): Promise<void> {
    await this.studentRepository.deleteById(id);
  }

  @authenticate('jwt')
  @patch('/students/{id}/increaseBuilding')
  @response(204, {
    description: 'increases placed Buildings from Student',
  })
  async replaceByStudentId(
      @param.path.number('id') id: number,
      @requestBody() addedBuildings: number,
  ): Promise<void> {
    await this.levelCalcStudentService.increasePlacedBuildingOfStudent(id, addedBuildings);
  }

  //@authenticate('jwt')
  @patch('/students/{id}/{lat}/{lon}/getStudentLocations')
  @response(204, {
    description: 'return all location of every active Student',
  })
  async getStudentLocation(
      @param.path.number('id') id: number,
      @param.path.number('lat') lat: string,
      @param.path.number('lon') lon: string,
  ): Promise<StudentLocation[]> {
    return await this.studentLocationService.setStudentLocation(id,lat,lon);
  }
}
