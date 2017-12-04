package bucket.application;

import bucket.listener.EventListener;
import bucket.network.Server;

public abstract class Application implements EventListener {

	protected Server server;
	
	public Application(Server server) {
		this.server = server;
	}

	

}
