package app;

import servent.message.myMessages.ComradeAskMessage;
import servent.message.myMessages.PingMessage;
import servent.message.myMessages.RecoveryMessage;
import servent.message.util.MessageUtil;

public class KeepAlive implements Runnable, Cancellable {

    private volatile boolean working = true;
    private NodeKeepAlive nodeKeepAlive;

    public KeepAlive(NodeKeepAlive nodeKeepAlive) {
        this.nodeKeepAlive = nodeKeepAlive;
    }

    @Override
    public void run() {
        while(working) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(!working) {
                break;
            }

            ServentInfo predecessor = AppConfig.chordState.getPredecessor();


            // if we don't have predecessor, reset the timestamp and set status to dead and continue
            if (predecessor == null) {
                nodeKeepAlive.resetTimestamp();
                nodeKeepAlive.setStatus(2);
                nodeKeepAlive.setDoneBroadcast(false);
                continue;
            }

            // send PING message to predecessor
            PingMessage pingMessage = new PingMessage(AppConfig.myServentInfo.getListenerPort(), predecessor.getListenerPort());
            MessageUtil.sendMessage(pingMessage);

            // if the timestamp is older than the weak limit, we set it to suspicious and do the broadcast,
            // also set doneBroadcast to true
            if (System.currentTimeMillis() - nodeKeepAlive.getTimestamp() > AppConfig.WEAK_LIMIT && !nodeKeepAlive.isDoneBroadcast()) {
                nodeKeepAlive.setStatus(1);
                nodeKeepAlive.setDoneBroadcast(true);
                askForHelp(predecessor.getListenerPort());
            }

            // if the timestamp is older than the strong limit, we set it to dead and trigger reorganization of the network
            if (System.currentTimeMillis() - nodeKeepAlive.getTimestamp() > AppConfig.STRONG_LIMIT && nodeKeepAlive.getStatus() == 1) {
//              distributed lock
                AppConfig.chordState.getSuzukiKasamiUtils().lock(AppConfig.chordState.getAllNodeInfo().stream().map(ServentInfo::getListenerPort).toList(), true);

                nodeKeepAlive.setStatus(2);
                AppConfig.timestampedStandardPrint("Node: " + predecessor.getListenerPort() + " is dead.");

                // remove node from my network and also add it to the remove message
                AppConfig.chordState.removeNode(predecessor);

                // also remove it from token queue so we don't lose the token
                AppConfig.chordState.getSuzukiKasamiUtils().getToken().removeNodeFromTokenQueue(predecessor.getListenerPort());

                if(AppConfig.chordState.getSuccessorTable().length == 0){
                    AppConfig.timestampedStandardPrint("I am the only node left in the network");
                    continue;
                }

                // send broadcast message to the neighbours to update their table
                broadcastMessage(predecessor);

                nodeKeepAlive.setStatus(0);
                nodeKeepAlive.setDoneBroadcast(false);

//              distributed unlock
                AppConfig.chordState.getSuzukiKasamiUtils().unlock();

                AppConfig.timestampedStandardPrint("Sending update messages for recovery");

            }
        }
    }

    private void broadcastMessage(ServentInfo predecessor) {
        int myPort = AppConfig.myServentInfo.getListenerPort();
        for (ServentInfo serventInfo : AppConfig.chordState.getAllNodeInfo()) {
            if (serventInfo.getListenerPort() != myPort) {
                RecoveryMessage rm = new RecoveryMessage(myPort, serventInfo.getListenerPort(), predecessor);
                MessageUtil.sendMessage(rm);
            }
        }
    }

    private void askForHelp(int warningNode) {
        AppConfig.timestampedStandardPrint("Asking other nodes to verify the status of the predecessor.");

        int myPort = AppConfig.myServentInfo.getListenerPort();
        for (ServentInfo serventInfo : AppConfig.chordState.getAllNodeInfo()) {
            if (serventInfo.getListenerPort() != myPort) {
                ComradeAskMessage cam = new ComradeAskMessage(myPort, serventInfo.getListenerPort(), warningNode, myPort);
                MessageUtil.sendMessage(cam);
            }
        }
    }

    @Override
    public void stop() {
        working = false;
    }
}
