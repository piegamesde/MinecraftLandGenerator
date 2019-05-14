package de.piegames.mlg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Start and manipulate a Minecraft server.
 *
 * @author morlok8k, piegames
 */
public class Server {

	private static Log				log	= LogFactory.getLog(Server.class);

	protected Path					serverDir;
	protected final ProcessBuilder	builder;

	/**
	 * @param serverFile
	 *            the path to the server.jar.
	 * @param javaOpts
	 *            the command line to start the server. To actually launch it, the path to the server file and "nogui" will be appended. The
	 *            default value is ["java", "-jar", "server.jar"]. Use this to specify JVM options (like more RAM etc.) or to enforce the
	 *            usage of a specific java version.
	 * @throws IOException
	 */
	public Server(Path serverFile, String[] javaOpts) throws IOException {
		if (!Files.exists(serverFile))
			throw new NoSuchFileException(serverFile.toString());

		serverDir = Files.createTempDirectory("MinecraftLandGenerator");
		log.info("Server directory: " + serverDir);
		log.debug("Symlinking server");
		Files.createSymbolicLink(serverDir.resolve("server.jar"), serverFile.toAbsolutePath());

		List<String> opts = new ArrayList<>(Arrays.asList(javaOpts != null ? javaOpts : new String[] { "java", "-jar", "server.jar" }));
		opts.add("nogui");
		builder = new ProcessBuilder(opts);
		builder.redirectErrorStream(true);
		builder.directory(serverDir.toFile());
	}

	/**
	 * Initialize the world by symlinking it to the server's folder.
	 *
	 * @param worldPath
	 *            the path to the world to generate. If it does not exist, a new world will be created at that position.
	 * @see #runMinecraft(boolean)
	 * @return A {@link World} object representing the world that will be loaded from the server by {@link #runMinecraft(boolean)}
	 * @author piegames
	 */
	public World initWorld(Path worldPath, boolean debugServer) throws IOException, InterruptedException {
		log.debug("Symlinking world");
		Files.createSymbolicLink(serverDir.resolve("world"), worldPath.toAbsolutePath());
		log.debug("Setting EULA");
		Files.write(serverDir.resolve("eula.txt"), "eula=true".getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
				StandardOpenOption.WRITE);
		if (!Files.exists(worldPath)) {
			log.warn("There is no world at " + worldPath + ". Will create a new one by running the server once.");
			Files.createDirectories(worldPath);
			runMinecraft(debugServer);
		}
		return new World(worldPath);
	}

	/**
	 * Start the Minecraft server using the current settings, wait until it finished loading the world and stop it. Communication with the
	 * server is done via standard IO streams and commands.
	 *
	 * @param debugServer
	 *            if set to true, all output from the server will be redirected to {@link System#out} for debugging purposes.
	 * @throws IOException
	 * @throws InterruptedException
	 *             if the thread gets interrupted while waiting for the server process to finish. This should never happen.
	 * @author Corrodias, Morlok8k, piegames
	 */
	public void runMinecraft(boolean debugServer) throws IOException, InterruptedException {
		log.info("Starting server");
		final Process process = builder.start();

		final BufferedReader pOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
		for (String line = pOut.readLine(); line != null; line = pOut.readLine()) {
			if (Thread.interrupted()) {
				log.warn("Got interrupted by other process, stopping");
				process.destroy();
				break;
			}
			line = line.trim();
			if (debugServer)
				System.out.println(line);

			if (line.contains("Done")) {
				PrintStream out = new PrintStream(process.getOutputStream());

				out.println("forceload query");
				log.info("Stopping server...");
				out.println("save-all");
				out.flush();
				out.println("stop");
				out.flush();
			}
		}

		/* The process should have stopped by now. */
		int exit = process.waitFor();
		if (exit != 0)
			log.warn("Process stopped with non-zero exit code (" + exit + ")");
		else
			log.info("Stopped server");
	}
}
