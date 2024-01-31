import {injectable, service} from '@loopback/core';
import {repository} from "@loopback/repository";
import {CritterInFight, OnlineFight, User} from "../models";
import {
  CritterInFightRepository,
  CritterRepository,
  CritterTemplateRepository,
  OnlineFightRepository,
  StudentRepository,
  UserRepository
} from "../repositories";
import {NotificationService} from "./NotificationService";
import {LevelCalcStudentService} from "./levelCalc-student.service";
import {CritterStatsService} from "./critter-stats.service";
import {FightInformation} from "../models/fight-information.model";
import {CritterInFightInformation} from "../models/critter-in-fight-information.model";

@injectable()
export class OnlineFightService {
  constructor(
    @repository(CritterTemplateRepository) protected critterTemplateRepository: CritterTemplateRepository,
    @repository(CritterRepository) protected critterRepository: CritterRepository,
    @repository(OnlineFightRepository) protected onlineFightRepository : OnlineFightRepository,
    @repository(CritterInFightRepository) protected critterInFightRepository : CritterInFightRepository,
    @repository(StudentRepository) protected studentRepository: StudentRepository,
    @service(LevelCalcStudentService) protected levelCalcStudentService : LevelCalcStudentService,
    @service(CritterStatsService) protected critterStatsService : CritterStatsService,
    @repository(UserRepository) protected userRepository: UserRepository,
  ) { }

  async createFight(studentId:number, enemyStudentId:number): Promise<void>{
    await this.deleteOldFights()

    const currentTime: Date = new Date();
    const generator = Math.random();
    const fightConnectionId = Math.floor(generator * (9999 - 1 + 1)) + 1;
    const newFight = new OnlineFight({
      fightConnectionId: fightConnectionId,
      studentId: studentId,
      state: OnlineFightState.Waiting,
      startTime: currentTime.getHours()*60+currentTime.getMinutes(),
      timer: currentTime.getSeconds()+currentTime.getMinutes()*60
        })

    const newFight2 = new OnlineFight({
      fightConnectionId: fightConnectionId,
      studentId: enemyStudentId,
      state: OnlineFightState.Waiting,
      startTime: currentTime.getHours()*60+currentTime.getMinutes(),
      timer: currentTime.getSeconds()+currentTime.getMinutes()*60
    })

    await this.onlineFightRepository.create(newFight)
    await this.onlineFightRepository.create(newFight2)

    //push Notification
    const user = await this.studentRepository.user(studentId)
    const enemyUser = await this.studentRepository.user(enemyStudentId)
    const sendPushNotificationService = new NotificationService();
    await sendPushNotificationService.sendPushNotification(enemyUser.fcmtoken, "Fight Invitation", user.username + " wants a fight against you!!!")
  }

  async deleteOldFights():Promise<void>{
  /*  const fights: OnlineFight[] = await this.onlineFightRepository.find()
    const currentTime: Date = new Date();
    const time = currentTime.getMinutes()+currentTime.getHours()*60

    for(const fight of fights){

      if(fight.startTime != time && fight.startTime+1 != time && fight.startTime+2 != time && fight.startTime+3 != time && fight.startTime+4 != time){
        await this.onlineFightRepository.deleteById(fight.fightId)
        if(fight.critterId != null )await this.critterInFightRepository.deleteById(fight.critterId)
      } else if(fight.state == OnlineFightState.Loser || fight.state == OnlineFightState.Winner){
        await this.onlineFightRepository.deleteById(fight.fightId)
        if(fight.critterId != null )await this.critterInFightRepository.deleteById(fight.critterId)
      } else if(fight.startTime+2 == time || fight.startTime+3 == time || fight.startTime+4 == time){
        if(fight.state == OnlineFightState.Waiting){
          await this.onlineFightRepository.deleteById(fight.fightId)
          if(fight.critterId != null )await this.critterInFightRepository.deleteById(fight.critterId)
        }
      }
    }*/
  }

  async makingDamage(fightConnectionId : number, studentId : number, amountOfDamage : number):Promise<void>{
    const currentTime: Date = new Date();
    const fights: OnlineFight[] = await this.onlineFightRepository.find()
    const currentFights : OnlineFight[] = []

    for(const fight of fights){
      if(fight.fightConnectionId== fightConnectionId){
        currentFights.push(fight)
      }
    }

    let allowToMakeDamage = false
    let critterIdFromEnemy = 0

    for(const fight of currentFights){
     if(fight.state == OnlineFightState.YourTurn && fight.studentId == studentId){
       allowToMakeDamage = true

       fight.timer = currentTime.getSeconds()+currentTime.getMinutes()*60
       fight.state = OnlineFightState.EnemyTurn
       await  this.onlineFightRepository.update(fight)
     }
     if(fight.state == OnlineFightState.EnemyTurn && fight.studentId != studentId){
       if(fight.critterId != null) {
         critterIdFromEnemy = fight.critterId
         fight.timer = currentTime.getSeconds()+currentTime.getMinutes()*60
         fight.state = OnlineFightState.YourTurn
         await this.onlineFightRepository.update(fight)
       }
     }
    }

    //make damage
    if(allowToMakeDamage && critterIdFromEnemy != 0) {
      const enemyCritter: CritterInFight = await this.critterInFightRepository.findById(critterIdFromEnemy)
      enemyCritter.health -= amountOfDamage
      await this.critterInFightRepository.update(enemyCritter)
    }

    await this.checkIfWinnerIsDetected(currentFights)
  }

  async checkIfWinnerIsDetected(currentFights : OnlineFight[]) {
    let loserDetected = false
    for (const fight of currentFights) {
      if (fight.critterId != null) {
        const critter: CritterInFight = await this.critterInFightRepository.findById(fight.critterId)
        if (critter.health <= 0) {
          loserDetected = true
          fight.state = OnlineFightState.Loser
          await this.onlineFightRepository.update(fight)
        }
      }
    }
    if (loserDetected) {
      for (const fight of currentFights) {
        if (fight.critterId != null) {
          const critter: CritterInFight = await this.critterInFightRepository.findById(fight.critterId)
          if (critter.health > 0) {
            fight.state = OnlineFightState.Winner
            await this.onlineFightRepository.update(fight)
            await this.levelCalcStudentService.increaseStudentCredits(fight.studentId, 5, 200)
          }
        }
      }
    }
  }

  async checkIfFightCanStart(fightConnectionId:number){
    const fights: OnlineFight[] = await this.onlineFightRepository.find()
    const currentFights : OnlineFight[] = []

    for(const fight of fights){
      if(fight.fightConnectionId== fightConnectionId){
        currentFights.push(fight)
      }
    }

    if(currentFights[0].state == OnlineFightState.Waiting && currentFights[1].state == OnlineFightState.Waiting){
      if(currentFights[0].critterId != null && currentFights[0].critterId != 0 && currentFights[1].critterId != null && currentFights[1].critterId != 0){
        const critter1 = await this.critterStatsService.createCritterUsable(currentFights[0].critterId)
        const critter2 = await this.critterStatsService.createCritterUsable(currentFights[1].critterId)

        //Set turns
        if(critter1.spd>=critter2.spd){
          currentFights[0].state = OnlineFightState.YourTurn
          currentFights[1].state = OnlineFightState.EnemyTurn
        } else {
          currentFights[0].state = OnlineFightState.EnemyTurn
          currentFights[1].state = OnlineFightState.YourTurn
        }
        await this.onlineFightRepository.update(currentFights[0])
        await this.onlineFightRepository.update(currentFights[1])

        //insert Critter to CritterInFight table
        const critterInFight1 = new CritterInFight({
          critterId: critter1.critterId,
          health: critter1.hp,
          attack: critter1.atk,
          defence: critter1.def,
        })
        const critterInFight2 = new CritterInFight({
          critterId: critter2.critterId,
          health: critter2.hp,
          attack: critter2.atk,
          defence: critter2.def,
        })
        await this.critterInFightRepository.create(critterInFight1)
        await this.critterInFightRepository.create(critterInFight2)
      }
    }
  }

  async checkMyState(fightConnectionId : number, studentId : number): Promise<string>{
    await this.checkIfTimeout(fightConnectionId)
    const fights: OnlineFight[] = await this.onlineFightRepository.find()
    const currentFights : OnlineFight[] = []

    for(const fight of fights){
      if(fight.fightConnectionId== fightConnectionId && fight.studentId == studentId){
        currentFights.push(fight)
      }
    }
    if(currentFights[0] != null)return currentFights[0].state
    return "404"
  }

  private async checkIfTimeout(fightConnectionId : number){
    const fights: OnlineFight[] = await this.onlineFightRepository.find()
    const currentFights : OnlineFight[] = []

    for(const fight of fights){
      if(fight.fightConnectionId== fightConnectionId){
        currentFights.push(fight)
      }
    }

    if(currentFights[0] != null){
    const currentTime: Date = new Date();
    for(const fight of currentFights) {
      if (currentTime.getSeconds()+currentTime.getMinutes()*60 - fight.timer > 29 && fight.state == OnlineFightState.YourTurn) {
        fight.state = OnlineFightState.EnemyTurn
        fight.timer = currentTime.getSeconds()+currentTime.getMinutes()*60
        await this.onlineFightRepository.update(fight)
      } else if (currentTime.getSeconds()+currentTime.getMinutes()*60 - fight.timer > 29 && fight.state == OnlineFightState.EnemyTurn) {
        fight.state = OnlineFightState.YourTurn
        fight.timer = currentTime.getSeconds()+currentTime.getMinutes()*60
        await this.onlineFightRepository.update(fight)
      }
    }
    }
  }

  async insertCritter(critterId : number, studentId : number, fightConnectionId : number): Promise<void>{
    const fights: OnlineFight[] = await this.onlineFightRepository.find()

    for(const fight of fights){
      if(fight.fightConnectionId== fightConnectionId && fight.studentId == studentId && fight.state == OnlineFightState.Waiting){
        fight.critterId = critterId
        await this.onlineFightRepository.update(fight)
      }
    }
  }

  async fightInformationList(studentId : number) : Promise<FightInformation[]>{
    await this.deleteOldFights();

    const fights: OnlineFight[] = await this.onlineFightRepository.find();
    const lookedFights: number[] = []
    const listedFights: FightInformation[] = [];

    for (const fight of fights) {
      if(fight.studentId == studentId) lookedFights.push(fight.fightConnectionId)
    }

    for (const fight of fights){
      for(const fightConnectionId of lookedFights){
        if(fight.fightConnectionId==fightConnectionId&&fight.studentId!=studentId){
          let userName = "noName";
          const users: User[] = await this.userRepository.find();
          const student = await this.studentRepository.findById(fight.studentId);
          for (const user of users) {
            if (user.id.toString() == student.userId.toString()) {
              // @ts-ignore
              userName = user.username.toString();
            }
          }
          const fightOnList = new FightInformation({
            fightConnectionId: fight.fightConnectionId,
            studentId: fight.studentId,
            userName: userName,
          });
          listedFights.push(fightOnList);
        }
      }
    }
    return listedFights
  }

  async getCritterInformation(critterId : number):Promise<CritterInFightInformation>{
    const critterInFight = await this.critterInFightRepository.findById(critterId)
    const critter = await this.critterRepository.findById(critterId)
    const critterTemplate = await this.critterTemplateRepository.findById(critter.critterTemplateId)

    return new CritterInFightInformation({
      critterId: critterInFight.critterId,
      name: critterTemplate.name,
      attack: critterInFight.attack,
      defence: critterInFight.defence,
      health: critterInFight.health
    })
  }
}

enum OnlineFightState {
  YourTurn = "yourTurn",
  EnemyTurn = "enemyTurn",
  Waiting = "waiting",
  Winner = "winner",
  Loser = "loser",
}

