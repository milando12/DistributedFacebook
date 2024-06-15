package servent.handler.myHandlers;

import app.AppConfig;
import app.ChordState;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.myMessages.SKRequestMessage;

public class SKRequestHandler implements MessageHandler {

    private Message clientMessage;

    public SKRequestHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }


    @Override
    public void run() {
        try {

            if (clientMessage.getMessageType() == MessageType.REQUEST_SUZUKI_TOKEN) {
                SKRequestMessage SKRequestMessage = (SKRequestMessage) clientMessage;
                Integer senderRN = SKRequestMessage.getSenderRN();
                Integer senderChordID = ChordState.chordHash(SKRequestMessage.getSenderPort());

                if (AppConfig.chordState.getSuzukiKasamiUtils().getRn().get(senderChordID) < senderRN) {
                    // update senders RN
                    AppConfig.chordState.getSuzukiKasamiUtils().getRn().set(senderChordID, senderRN);

                    // if we have token and not in critical section then send token
                    if (AppConfig.chordState.getSuzukiKasamiUtils().getHasSuzukiToken().get() &&
                            AppConfig.chordState.getSuzukiKasamiUtils().getToken().getLn().get(senderChordID) + 1 == senderRN) {

                        // add it to queue
                        AppConfig.chordState.getSuzukiKasamiUtils().getToken().getQueue().add(SKRequestMessage.getSenderPort());

                        // if we are not in critical section then send token
                        if (!AppConfig.chordState.getSuzukiKasamiUtils().getUsingToken().get())
                            AppConfig.chordState.getSuzukiKasamiUtils().sendTokenFromQueue();
                    }

                }
            } else {
                AppConfig.timestampedErrorPrint("Got to token request handler but message is not REQUEST_SUZUKI_TOKEN");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
