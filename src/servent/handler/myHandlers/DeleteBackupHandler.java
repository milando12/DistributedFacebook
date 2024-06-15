package servent.handler.myHandlers;

import app.AppConfig;
import servent.handler.MessageHandler;
import servent.message.myMessages.DeleteBackupMessage;
import servent.message.Message;
import servent.message.MessageType;

public class DeleteBackupHandler implements MessageHandler {
    private Message clientMessage;

    public DeleteBackupHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        try {
            if (clientMessage.getMessageType() == MessageType.DELETE_BACKUP) {
                DeleteBackupMessage deleteBackupMessage = (DeleteBackupMessage) clientMessage;

                String path = deleteBackupMessage.getPath();
                AppConfig.chordState.deleteBackup(path);

            } else {
                AppConfig.timestampedErrorPrint("DeleteBackup handler got a message that is not DELETE_BACKUP");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
