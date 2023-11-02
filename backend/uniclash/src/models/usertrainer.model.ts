import { User, } from "@loopback/authentication-jwt";

export class UserTrainer extends User{


    constructor(data?: Partial<UserTrainer>) {
        super(data);
      }
}