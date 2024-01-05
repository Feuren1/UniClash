import { ApplicationConfig, UniclashApplication } from './application';
import { NotificationService } from './services/NotificationService';

export * from './application';

export async function main(options: ApplicationConfig = {}) {
  const app = new UniclashApplication(options);
  const notificationService = new NotificationService();

  const startTasks = async () => {
    await notificationService.sendPushNotification('c5MLeBPpTT27a9Mp376WXb:APA91bGfq0llhpTjNolQwAwxQWSwtVj3vmr-gM6qqLIADU2UG53I5bWMHhDciHmU78LUZuk8otOpV7mYUIPekbixYA9ksyPttY93kPZ2vy1SykuC0TWaXvyBPZ9mcOW0dLA2xY_WNzGY', 'Test Title', 'Test Body');

  };

  
  await app.boot();
  await startTasks();
  await app.start();

  const url = app.restServer.url;
  console.log(`Server is running at ${url}`);
  console.log(`Try ${url}/ping`);

  return app;
}

if (require.main === module) {

  const config = {
    rest: {
      port: +(process.env.PORT ?? 3000),
      host: process.env.HOST,
      gracePeriodForClose: 5000, 
      openApiSpec: {
        setServersFromRequest: true,
      },
    },
  };

  main(config).catch(err => {
    console.error('Cannot start the application.', err);
    process.exit(1);
  });
}
