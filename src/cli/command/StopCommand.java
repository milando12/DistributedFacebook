package cli.command;

import app.AppConfig;
import cli.CLIParser;
import servent.SimpleServentListener;

public class StopCommand implements CLICommand {

	private CLIParser parser;
	private SimpleServentListener listener;
	
	public StopCommand(CLIParser parser, SimpleServentListener listener) {
		this.parser = parser;
		this.listener = listener;
	}
	
	@Override
	public String commandName() {
		return "stop";
	}

	@Override
	public void execute(String args) {
		parser.stop();
		listener.stop();
		AppConfig.getKeepAlive().stop();

		AppConfig.timestampedStandardPrint("Stopping..." + " has token: " + AppConfig.chordState.getSuzukiKasamiUtils().getHasSuzukiToken().get());
	}

}
