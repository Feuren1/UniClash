// src/services/critter-stats.service.ts

import {injectable} from '@loopback/core';
import {repository} from '@loopback/repository';
import {Critter, CritterCopy} from '../models';
import {CritterCopyRepository, CritterRepository} from '../repositories';

@injectable()
export class CritterStatsService {
  constructor(
    @repository(CritterRepository) protected critterRepository: CritterRepository,
    @repository(CritterCopyRepository) protected critterCopyRepository: CritterCopyRepository,
  ) { }

  async calculateActualStats(critterCopyId: number): Promise<number[]> {
    // Retrieve the critter and its copies from the repository
    const copy: CritterCopy = await this.critterCopyRepository.findById(critterCopyId);
    const critter: Critter = await this.critterRepository.findById(copy.critterId);


    // Calculate the actual stats based on the critter and its copies
    const actualStats: number[] = this.calculateStats(critter, copy);

    return actualStats;
  }

  // Implement the actual stats calculation logic here
  private calculateStats(critter: Critter, copy: CritterCopy): number[] {
    // Your logic for calculating stats goes here
    // You can use the critter and its copies to compute the actual stats
    // and return them as an array of integers.
    return [critter.baseHealth + copy.level, critter.baseAttack + copy.level, critter.baseDefend + copy.level, critter.baseSpeed + copy.level]
  }
}
