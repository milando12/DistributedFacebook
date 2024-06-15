package cli.command;

import app.AppConfig;
import app.ServentInfo;
import app.suzuki_kasami.Token;
import cli.CLIParser;
import servent.SimpleServentListener;
import servent.message.myMessages.ShutDownMessage;
import servent.message.util.MessageUtil;

public class ShutDownCommand implements CLICommand {

    @Override
    public String commandName() {
        return "shutdown";
    }

    private CLIParser parser;
    private SimpleServentListener listener;

    public ShutDownCommand(CLIParser parser, SimpleServentListener listener) {
        this.parser = parser;
        this.listener = listener;
    }

    @Override
    public void execute(String args) {

        if (AppConfig.chordState.canShutdown()){
            parser.stop();
            listener.stop();
            AppConfig.getKeepAlive().stop();
            AppConfig.timestampedStandardPrint("Shutting down...");
            return;
        }

        AppConfig.setParserAndListener(parser, listener);

        // first we need distributed lock
        AppConfig.chordState.getSuzukiKasamiUtils().lock(AppConfig.chordState.getAllNodeInfo().stream().map(ServentInfo::getListenerPort).toList(), false);

        // now we get rid of token because we will propagate it through the message
        Token token = AppConfig.chordState.getSuzukiKasamiUtils().getToken();
        AppConfig.chordState.getSuzukiKasamiUtils().setToken(null);
        AppConfig.chordState.getSuzukiKasamiUtils().getHasSuzukiToken().set(false);
        AppConfig.chordState.getSuzukiKasamiUtils().getUsingToken().set(false);

        // send shutdown message to my successor
        ShutDownMessage sdm = new ShutDownMessage(AppConfig.myServentInfo.getListenerPort(), AppConfig.chordState.getNextNodePort(), AppConfig.myServentInfo, AppConfig.chordState.getPredecessor(), AppConfig.chordState.getSuccessorTable()[0], token);
        AppConfig.timestampedStandardPrint("Sending shutdown message to my successor...");
        MessageUtil.sendMessage(sdm);


    }
}
