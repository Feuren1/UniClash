import * as admin from 'firebase-admin';
import * as json from "./uniclash.json"


export class NotificationService {
    private firebaseAdmin: admin.app.App;

    constructor() {
        
        const serviceAccount = require("./uniclash.json");

        this.firebaseAdmin = admin.initializeApp({  
            credential: admin.credential.cert(serviceAccount),
            // other configuration options if needed
        });
    }

    async sendPushNotification(deviceToken: string, title: string, body: string) {
        const message: admin.messaging.Message = {
            notification: {
                title: title,
                body: body,
            },
            token: deviceToken,
        };

        try {
            const response = await this.firebaseAdmin.messaging().send(message);
            console.log('Successfully sent message:', response);
        } catch (error) {
            console.error('Error sending message:', error);
        }
    }
}
