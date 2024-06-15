package servent.message.myMessages;

import app.MetaFile;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class BackupMessage extends BasicMessage {

    private static final long serialVersionUID = 42L;
    private MetaFile metaFile;

    public BackupMessage(int senderPort, int receiverPort, MetaFile metaFile) {
        super(MessageType.BACKUP, senderPort, receiverPort, metaFile.getPath());
        this.metaFile = metaFile;
    }

    public MetaFile getMetaFile() {
        return metaFile;
    }
}
