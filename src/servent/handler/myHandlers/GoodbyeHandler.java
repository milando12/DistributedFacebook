package servent.handler.myHandlers;

import app.AppConfig;
import servent.handler.MessageHandler;
import servent.message.myMessages.GoodbyeMessage;
import servent.message.Message;
import servent.message.MessageType;

public class GoodbyeHandler implements MessageHandler {
    private Message clientMessage;

    public GoodbyeHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        try {
            if (clientMessage.getMessageType() == MessageType.GOODBYE) {
                GoodbyeMessage gm = (GoodbyeMessage) clientMessage;
                AppConfig.timestampedStandardPrint("Final goodbye: " + gm.getGoodbyeMessage());

                AppConfig.getListener().stop();
                AppConfig.getParser().stop();
                AppConfig.getKeepAlive().stop();

            } else {
                AppConfig.timestampedErrorPrint("Goodbye handler got a message that is not GOODBYE");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
