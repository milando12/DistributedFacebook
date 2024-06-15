package servent.message.myMessages;

import app.ServentInfo;
import app.suzuki_kasami.Token;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class ShutDownMessage extends BasicMessage {
    private static final long serialVersionUID = 789L;
    private ServentInfo shuttingDownServentInfo;
    private ServentInfo predecessorServentInfo;
    private ServentInfo successorServentInfo;
    private Token token;

    public ShutDownMessage(int senderPort, int receiverPort, ServentInfo shuttingDownServentInfo, ServentInfo predecessorServentInfo, ServentInfo successorServentInfo, Token token) {
        super(MessageType.SHUT_DOWN, senderPort, receiverPort, "");
        this.shuttingDownServentInfo = shuttingDownServentInfo;
        this.predecessorServentInfo = predecessorServentInfo;
        this.successorServentInfo = successorServentInfo;
        this.token = token;
    }

    public ServentInfo getShuttingDownServentInfo() {
        return shuttingDownServentInfo;
    }

    public ServentInfo getPredecessorServentInfo() {
        return predecessorServentInfo;
    }

    public ServentInfo getSuccessorServentInfo() {
        return successorServentInfo;
    }
    public Token getToken() {
        return token;
    }
}
