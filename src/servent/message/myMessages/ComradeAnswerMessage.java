package servent.message.myMessages;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class ComradeAnswerMessage extends BasicMessage {

        private static final long serialVersionUID = 17867773432L;
        private int tryPort;
        private int initPort;

        public ComradeAnswerMessage(int senderPort, int receiverPort, int tryPort, int initPort) {
            super(MessageType.COMRADE_ANSWER, senderPort, receiverPort);
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