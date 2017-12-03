package application;

import listener.EventListener;
import network.Server;

public abstract class Application implements EventListener {

	protected Server server;
	
	public Application(Server server) {
		this.server = server;
	}

	

}
