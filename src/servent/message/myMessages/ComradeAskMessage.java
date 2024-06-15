package servent.message.myMessages;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class ComradeAskMessage extends BasicMessage {

    private static final long serialVersionUID = 1786L;
    private int tryPort;
    private int initPort;   // initiator port

    public ComradeAskMessage(int senderPort, int receiverPort, int tryPort, int initPort) {
        super(MessageType.COMRADE_ASK, senderPort, receiverPort);
        this.tryPort = tryPort;
        this.initPort = initPort;
    }

    public int getTryPort() {
        return tryPort;
    }

    public int getInitPort() {
        return initPort;
    }
}
