package servent.message.myMessages;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class SKRequestMessage extends BasicMessage {

    private static final long serialVersionUID = 3899837287772127636L;
    private int senderRN;

    public SKRequestMessage(int senderPort, int receiverPort, int senderRN) {
        super(MessageType.REQUEST_SUZUKI_TOKEN, senderPort, receiverPort);
        this.senderRN = senderRN;
    }

    public int getSenderRN() {
        return senderRN;
    }
}
