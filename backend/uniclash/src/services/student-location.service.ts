import {inject, injectable} from '@loopback/core';
import {repository} from '@loopback/repository';
import {Critter, CritterUsable, Student, User} from '../models';
import {AttackRepository, CritterAttackRepository, CritterRepository, CritterTemplateRepository, StudentRepository, UserRepository} from '../repositories';
import {CritterStatsService} from './critter-stats.service';
import {CritterListable} from "../models/critter-listable.model";
import {StudentLocation} from "../models/studentLocation.model";

@injectable()
export class StudentLocationService {
  constructor(
    @repository(StudentRepository) protected studentRepository: StudentRepository,
    @repository(UserRepository) protected userRepository: UserRepository,
  ) { }
  async setStudentLocation(studentId :number, lat : string, lon : string): Promise<StudentLocation[]>{
    const currentTime: Date = new Date();
    const hours: number = currentTime.getHours()*60;
    const minutes: number = currentTime.getMinutes()+hours;
    const student: Student = await this.studentRepository.findById(studentId);
    if(student != null){
      student.lon = lon;
      student.lat = lat;
      student.time = minutes;
      console.log("time" + minutes + "lat " + lat );
    }
    await this.studentRepository.update(student);
    return this.resetLocationsAndFindActiveUsers(studentId);
  }

  async resetLocationsAndFindActiveUsers(thisStudent : number): Promise<StudentLocation[]> {
    const currentTime: Date = new Date();
    const activeStudents : Student[] = [];

    const students: Student[] = await this.studentRepository.find()
    const hours: number = currentTime.getHours()*60;
    const minutes: number = currentTime.getMinutes()+hours;

    for(const student of students){
      if(student.time != minutes && student.time != minutes-1 && student.time != minutes-2 && student.time != minutes-3){
        student.lon = "0.0";
        student.lat = "0.0";
        student.time = 0;
        await this.studentRepository.update(student);
      } else if(student.lon != "0.0" && student.id != thisStudent){
        activeStudents.push(student)
      }
    }
    return this.getListOfActiveStudents(activeStudents);
  }

  async getListOfActiveStudents(students : Student[]): Promise<StudentLocation[]>{
    const activeStudents : StudentLocation[] = [];
    const users : User[] = await this.userRepository.find()
    for(const student of students){
      let studentName = "Noname"
      for(const user of users){
        if(user.id.toString() == student.userId.toString()){
            // @ts-ignore
          studentName = user.username.toString()
        }
      }

      const activeStudent = new StudentLocation({
        name: studentName,
        lat: student.lat,
        lon: student.lon,
        id: student.id,
        level: student.level,
      });
      activeStudents.push(activeStudent);
    }

    return activeStudents;
  }
}
