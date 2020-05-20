package gothello;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import java.util.concurrent.Callable;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;

@Command(
	name = "Gothello Server",
	mixinStandardHelpOptions = true,
	description = "The Gothello game server.\nCreated by Anton Lee, Max Bracken, Alex Hill, and Blake Akapita",
	version = "0.0.0")
public class App implements Callable<Integer> {
	// ---	CLI Arguments ---
	@Option(names = { "-p", "--port" }, description = "What port should the server serve to")
	private int port = 8000;

	// ---	Properties ---
	private Server server;

	public static void main(String[] args) {
		// Start the program after reading command line arguments
		int exitCode = new CommandLine(new App()).execute(args);
		System.exit(exitCode);
	}

	// Program Entry Point. The program starts here
	@Override
	public Integer call() {
		System.out.println("\n >> Gothello Server Starting");
		System.out.println("    Starting on port " + port);
		System.out.println("    http://localhost:"+port+"/api/status");
		System.out.println();

		// 
		// Setup Server
		// 
		server = new Server();
		ServerConnector conn = new ServerConnector(server);
		conn.setPort(port);
		server.setConnectors(new ServerConnector[] {conn});
		
		// 
		// Setup Servlet/Routes/Endpoints
		// 
		ServletHandler handler = new ServletHandler();
		
		handler.addServletWithMapping( Status.class, "/api/status");
		server.setHandler(handler);

		try {
			server.start();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}
