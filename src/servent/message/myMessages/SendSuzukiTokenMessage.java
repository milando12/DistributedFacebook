package servent.message.myMessages;

import app.suzuki_kasami.Token;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class SendSuzukiTokenMessage extends BasicMessage {

    private static final long serialVersionUID = 3444837287772127636L;
    private Token token;

    public SendSuzukiTokenMessage(int senderPort, int receiverPort, Token token) {
        super(MessageType.SEND_SUZUKI_TOKEN, senderPort, receiverPort);
        this.token = token;
    }

    public Token getToken() {
        return token;
    }
}
