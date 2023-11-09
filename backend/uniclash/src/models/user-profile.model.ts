import {model, property} from '@loopback/repository';
import {UserProfile, securityId} from '@loopback/security';

@model()
export class MyUserProfile implements UserProfile {
  @property({
    type: 'string',
    required: true,
  })
  [securityId]: string;

  @property({
    type: 'string',
    required: true,
  })
  name?: string;
}
