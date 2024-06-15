package servent.handler.myHandlers;

import app.AppConfig;
import app.ServentInfo;
import app.suzuki_kasami.Token;
import servent.handler.MessageHandler;
import servent.message.*;
import servent.message.myMessages.GoodbyeMessage;
import servent.message.myMessages.ShutDownMessage;
import servent.message.util.MessageUtil;

public class ShutDownHandler implements MessageHandler {

    private Message clientMessage;

    public ShutDownHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }


    @Override
    public void run() {
        try {
            if (clientMessage.getMessageType() == MessageType.SHUT_DOWN) {
                ShutDownMessage sdm = (ShutDownMessage) clientMessage;
                ServentInfo shuttingDown = sdm.getShuttingDownServentInfo();
                ServentInfo predecessor = sdm.getPredecessorServentInfo();
                ServentInfo successor = sdm.getSuccessorServentInfo();
                Token token = sdm.getToken();

                AppConfig.chordState.removeNode(shuttingDown);

                // if I am shutdown's predecessor, then release distributed lock
                if(AppConfig.myServentInfo.getListenerPort() == predecessor.getListenerPort()){

                    // we don't need token at all
                    AppConfig.chordState.getSuzukiKasamiUtils().setToken(token);
                    AppConfig.chordState.getSuzukiKasamiUtils().getHasSuzukiToken().set(true);
                    if (!AppConfig.chordState.getSuzukiKasamiUtils().getUsingToken().get())
                        AppConfig.chordState.getSuzukiKasamiUtils().unlock();

                    GoodbyeMessage gm = new GoodbyeMessage(AppConfig.myServentInfo.getListenerPort(), shuttingDown.getListenerPort(), ".");
                    MessageUtil.sendMessage(gm);

                } else { // just node in the middle
                    // propagate message through the network
                    ShutDownMessage sdm1 = new ShutDownMessage(AppConfig.myServentInfo.getListenerPort(), AppConfig.chordState.getNextNodePort(), shuttingDown, predecessor, successor, token);
                    MessageUtil.sendMessage(sdm1);
                }

            } else {
                AppConfig.timestampedErrorPrint("Shutdown handler got a message that is not SHUTDOWN");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
