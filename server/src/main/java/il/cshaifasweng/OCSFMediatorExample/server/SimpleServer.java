package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class SimpleServer extends AbstractServer {
	private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();

	public SimpleServer(int port) {
		super(port);
		
	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		Message message = (Message) msg;
		String request = message.getMessage();
		try {
			//we got an empty message, so we will send back an error message with the error details.
			if (request.isBlank()){
				message.setMessage("Error! we got an empty message");
				client.sendToClient(message);
			}
			//we got a request to change submitters IDs with the updated IDs at the end of the string, so we save
			// the IDs at data field in Message entity and send back to all subscribed clients a request to update
			//their IDs text fields. An example of use of observer design pattern.
			//message format: "change submitters IDs: 123456789, 987654321"
			else if(request.startsWith("change submitters IDs:")){
				message.setData(request.substring(23));
				message.setMessage("update submitters IDs");
				sendToAllClients(message);
			}
			//we got a request to add a new client as a subscriber.
			else if (request.equals("add client")){
				SubscribedClient connection = new SubscribedClient(client);
				SubscribersList.add(connection);
				message.setMessage("client added successfully");
				client.sendToClient(message);
			}
			//we got a message from client requesting to echo Hello, so we will send back to client Hello world!
			else if(request.startsWith("echo Hello")){
				message.setMessage("Hello World!");
				client.sendToClient(message);
			}
			// sending here submitters IDs to client
			else if(request.startsWith("send Submitters IDs")){
				message.setMessage("318307923, 205815962");
				client.sendToClient(message);

			}
			// sending here names IDs to client
			else if (request.startsWith("send Submitters")){
				message.setMessage("Lior, Alon");
				client.sendToClient(message);
				//add code here to send submitters names to client
			}
			else if (request.equals("what’s the time?")) {
				DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
				LocalTime currentTime = LocalTime.now();
				message.setMessage(currentTime.format(timeFormat));
				client.sendToClient(message);
			}
			// send result of multiplication of two integers in the format of "multiply n*m"
			else if (request.startsWith("multiply")){
				// clean the unnecessary suffix and whitespaces
				String MsgStr = request.toString().substring(8).replaceAll("\\s+","");
				// calculation
				int nulIndex = MsgStr.indexOf("*");
				int result = Integer.parseInt(MsgStr.substring(0, nulIndex)) * Integer.parseInt(MsgStr.substring(nulIndex+1));
				// msg send to client
				message.setMessage(Integer.toString(result));
				client.sendToClient(message);
			}
			// get and send the same message
			else{
				message.setMessage(request.toString());
				client.sendToClient(message);
				//add code here to send received message to all clients.
				//The string we received in the message is the message we will send back to all clients subscribed.
				//Example:
					// message received: "Good morning"
					// message sent: "Good morning"
				//see code for changing submitters IDs for help
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void sendToAllClients(Message message) {
		try {
			for (SubscribedClient SubscribedClient : SubscribersList) {
				SubscribedClient.getClient().sendToClient(message);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
