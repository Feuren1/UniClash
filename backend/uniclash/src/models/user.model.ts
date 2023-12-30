import {Entity, hasOne, model, property} from '@loopback/repository';
import {UserCredentials} from '.';
import {Student} from './student.model';

@model()
export class User extends Entity {
  @property({
    type: 'string',
    id: true,
    generated: false,
    defaultFn: 'uuidv4',
  })
  id: string;


  @property({
    type: 'string',
    required: true,
    index: {
      unique: true,
    },
  })
  email: string;


  @property({
    type: 'string',
    required: false,
    index: {
      unique: true,
    },
  })
  fcmtoken: string;


  @property({
    type: 'string',
  })
  username?: string;

  @hasOne(() => UserCredentials)
  userCredentials: UserCredentials;

  @hasOne(() => Student)
  student: Student;
  [prop: string]: any;

  constructor(data?: Partial<User>) {
    super(data);
  }
}

export interface UserRelations {
  // describe navigational properties here
}

export type UserWithRelations = User & UserRelations;

