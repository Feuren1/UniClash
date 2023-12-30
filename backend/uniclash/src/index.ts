import { ApplicationConfig, UniclashApplication } from './application';
import { NotificationService } from './services/NotificationService';

export * from './application';

export async function main(options: ApplicationConfig = {}) {
  const app = new UniclashApplication(options);
  const notificationService = new NotificationService();

  const startTasks = async () => {
    await notificationService.sendPushNotification('drk-4D87SHuWHAH0kH_pWm:APA91bHWiJes8l5AES9KegTxQADJBNhEIpLIw946tdAVo0sjHm5uwLqSUaFeMBBavPFPiaTZuuyAlpaLiAyQnVIQyPBc3cokyN3eJCX6SdHY4Xs___uYfoeJ75P6ebnGpYZ7DGGnQUTz', 'Test Title', 'Test Body');

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
