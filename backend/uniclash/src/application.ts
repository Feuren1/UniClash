
import {BootMixin} from '@loopback/boot';
import {ApplicationConfig} from '@loopback/core';
import {RepositoryMixin} from '@loopback/repository';
import {RestApplication} from '@loopback/rest';
import {
  RestExplorerBindings,
  RestExplorerComponent,
} from '@loopback/rest-explorer';
import {ServiceMixin} from '@loopback/service-proxy';
import path from 'path';
import {DbDataSource} from './datasources';
import {UserCredentialsRepository, UserRepository} from './repositories';
import {MySequence} from './sequence';
import {CritterStatsService} from './services/critter-stats.service';

import {AuthenticationComponent} from './authentication.component';
import {JWTAuthenticationComponent} from './jwt-authentication-component';
import {UserServiceBindings} from './keys';
import {MyUserService} from './services/user-credential.service';
export {ApplicationConfig};

export class UniclashApplication extends BootMixin(
  ServiceMixin(RepositoryMixin(RestApplication)),
) {
  constructor(options: ApplicationConfig = {}) {
    super(options);
    this.service(CritterStatsService);
    // Set up the custom sequence
    this.sequence(MySequence);

    // Set up default home page
    this.static('/', path.join(__dirname, '../public'));

    // Customize @loopback/rest-explorer configuration here
    this.configure(RestExplorerBindings.COMPONENT).to({
      path: '/explorer',
    });
    this.component(RestExplorerComponent);

    this.projectRoot = __dirname;
    // Customize @loopback/boot Booter Conventions here
    this.bootOptions = {
      controllers: {
        // Customize ControllerBooter Conventions here
        dirs: ['controllers'],
        extensions: ['.controller.js'],
        nested: true,
      },
    };
    this.component(AuthenticationComponent);
    // Mount jwt component
    this.component(JWTAuthenticationComponent);
    // Bind datasource
    this.dataSource(DbDataSource, UserServiceBindings.DATASOURCE_NAME);

    this.bind(UserServiceBindings.USER_SERVICE).toClass(
      MyUserService
    ),
      this.bind(UserServiceBindings.USER_REPOSITORY).toClass(
        UserRepository
      ),
      this.bind(UserServiceBindings.USER_CREDENTIALS_REPOSITORY).toClass(
        UserCredentialsRepository
      )
  }
}
