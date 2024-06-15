package servent.handler.myHandlers;

import app.AppConfig;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.myMessages.RecoveryMessage;

public class RecoveryHandler implements MessageHandler {
    private Message clientMessage;

    public RecoveryHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        try {
            if(clientMessage.getMessageType() == MessageType.RECOVERY) {
                RecoveryMessage rm = (RecoveryMessage) clientMessage;
                AppConfig.chordState.removeNode(rm.getToBeDeleted());

                AppConfig.timestampedStandardPrint("Node " + rm.getToBeDeleted().getChordId() + " with port " + rm.getToBeDeleted().getListenerPort() + " is removed from the system.");
            }else {
                System.out.println("Recovery handler got a message that is not RECOVERY");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

}
