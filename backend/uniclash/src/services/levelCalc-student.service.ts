import {injectable, service} from '@loopback/core';
import {repository} from "@loopback/repository";
import {Student} from "../models";
import {StudentRepository} from "../repositories";

@injectable()
export class LevelCalcStudentService {
  constructor(
    @repository(StudentRepository) protected studentRepository: StudentRepository
  ) { }

  async checkForLevelUp(studentId: number): Promise<void> {
    const student: Student = await this.studentRepository.findById(studentId);
    if (!student) {
      throw new Error(`Student with ID ${studentId} not found. CheckForLevel Up Methode`);
    };

    if(student.level == null){
      student.level = 0;
    }

    if(student.expToNextLevel != null){
      if(student.expToNextLevel > 499){
        student.expToNextLevel = student.expToNextLevel - 500;
        student.level++;
      }
    }

    await this.studentRepository.update(student);
  }

  async increaseStudentCredits(studentId: number, creditsToAdd: number, epToAdd : number): Promise<void> {
    const student: Student = await this.studentRepository.findById(studentId);
    if (!student) {
      throw new Error(`Student with ID ${studentId} not found. increase StudentCredits`);
    };
    student.credits = (student.credits || 0) + creditsToAdd;
    student.expToNextLevel = (student.expToNextLevel || 0) + epToAdd;

    await this.studentRepository.update(student);
    await this.checkForLevelUp(studentId);
  }

  async increasePlacedBuildingOfStudent(studentId: number, placedBuildings: number): Promise<void> {
    const student: Student = await this.studentRepository.findById(studentId);
    if (!student) {
      throw new Error(`Student with ID ${studentId} not found. increase StudentCredits`);
    };
    student.placedBuildings = (student.placedBuildings || 0) + placedBuildings;

    await this.studentRepository.update(student);
  }
}

