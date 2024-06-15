package servent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.AppConfig;
import app.Cancellable;
import servent.handler.*;
import servent.handler.myHandlers.*;
import servent.message.Message;
import servent.message.util.MessageUtil;

public class SimpleServentListener implements Runnable, Cancellable {

	private volatile boolean working = true;
	
	public SimpleServentListener() {
		
	}

	/*
	 * Thread pool for executing the handlers. Each client will get it's own handler thread.
	 */
	private final ExecutorService threadPool = Executors.newWorkStealingPool();
	
	@Override
	public void run() {
		ServerSocket listenerSocket = null;
		try {
			listenerSocket = new ServerSocket(AppConfig.myServentInfo.getListenerPort(), 100);
			/*
			 * If there is no connection after 1s, wake up and see if we should terminate.
			 */
			listenerSocket.setSoTimeout(1000);
		} catch (IOException e) {
			AppConfig.timestampedErrorPrint("Couldn't open listener socket on: " + AppConfig.myServentInfo.getListenerPort());
			System.exit(0);
		}
		
		
		while (working) {
			try {
				Message clientMessage;
				
				Socket clientSocket = listenerSocket.accept();
				
				//GOT A MESSAGE! <3
				clientMessage = MessageUtil.readMessage(clientSocket);
				
				MessageHandler messageHandler = new NullHandler(clientMessage);
				
				/*
				 * Each message type has it's own handler.
				 * If we can get away with stateless handlers, we will,
				 * because that way is much simpler and less error prone.
				 */
				switch (clientMessage.getMessageType()) {
				case NEW_NODE:
					messageHandler = new NewNodeHandler(clientMessage);
					break;
				case WELCOME:
					messageHandler = new WelcomeHandler(clientMessage);
					break;
				case SORRY:
					messageHandler = new SorryHandler(clientMessage);
					break;
				case UPDATE:
					messageHandler = new UpdateHandler(clientMessage);
					break;
				case PUT:
					messageHandler = new PutHandler(clientMessage);
					break;
				case ASK_GET:
					messageHandler = new AskGetHandler(clientMessage);
					break;
				case TELL_GET:
					messageHandler = new TellGetHandler(clientMessage);
					break;
				case POISON:
					break;
				case SEND_SUZUKI_TOKEN:
					messageHandler = new GotTokenHandler(clientMessage);
					break;
				case REQUEST_SUZUKI_TOKEN:
					messageHandler = new SKRequestHandler(clientMessage);
					break;
				case PUT_UNLOCK:
					messageHandler = new PutUnlockHandler(clientMessage);
					break;
				case DELETE:
					messageHandler = new DeleteHandler(clientMessage);
					break;
				case DELETE_UNLOCK:
					messageHandler = new DeleteUnlockHandler(clientMessage);
					break;
				case BACKUP:
					messageHandler = new BackupHandler(clientMessage);
					break;
				case DELETE_BACKUP:
					messageHandler = new DeleteBackupHandler(clientMessage);
					break;
				case SHUT_DOWN:
					messageHandler = new ShutDownHandler(clientMessage);
					break;
				case GOODBYE:
					messageHandler = new GoodbyeHandler(clientMessage);
					break;
				case PING:
					messageHandler = new PingHandler(clientMessage);
					break;
				case PONG:
					messageHandler = new PongHandler(clientMessage);
					break;
				case RECOVERY:
					messageHandler = new RecoveryHandler(clientMessage);
					break;
				case COMRADE_ASK:
						messageHandler = new ComradeAskHandler(clientMessage);
						break;
				case COMRADE_ANSWER:
						messageHandler = new ComradeAnswerHandler(clientMessage);
						break;
				case COMRADE_CONTACT:
						messageHandler = new ComradeContactHandler(clientMessage);
						break;
				case COMRADE_ALIVE:
						messageHandler = new ComradeAliveHandler(clientMessage);
						break;
				}
				
				threadPool.submit(messageHandler);
			} catch (SocketTimeoutException timeoutEx) {
				//Uncomment the next line to see that we are waking up every second.
//				AppConfig.timedStandardPrint("Waiting...");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void stop() {
		this.working = false;
	}

}
