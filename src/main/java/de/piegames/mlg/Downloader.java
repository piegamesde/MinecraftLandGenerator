package de.piegames.mlg;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import io.github.soc.directories.ProjectDirectories;

public class Downloader {

	private static Log			log				= LogFactory.getLog(Downloader.class);

	private static final String	MANIFEST_URL	= "https://launchermeta.mojang.com/mc/game/version_manifest.json";

	/**
	 * Get a {@code server.jar} file for a given Minecraft version, automatically downloading it if needed, and caching the result for
	 * future use. The cache folder will be used according to {@link ProjectDirectories#cacheDir}. No integrity checks are made. No sanity
	 * checks are made on the cache folder (don't touch it!).
	 * 
	 * @param versionName
	 *            The desired version id. The latest release will be fetched on a {@code null} value.
	 */
	public static Path getMinecraft(String versionName) throws IOException {
		Path cache = Paths.get(ProjectDirectories.from("de", "piegames", "MinecraftLandGenerator").cacheDir);
		Files.createDirectories(cache);

		/* URL to the JSON file describing a single version */
		String serverJsonUrl = null;
		VersionManifest manifest = null;

		/* Fetch latest version */
		if (versionName == null) {
			log.debug("Searching for latest release");
			manifest = downloadManifest(cache);
			VersionManifest.Version version = manifest.get(manifest.latest.release).get();
			versionName = version.id;
			serverJsonUrl = version.url;
			log.debug("Found latest release " + versionName);
		}

		Path jarFile = cache.resolve("server-" + versionName + ".jar");
		if (!Files.exists(jarFile)) {
			log.debug("No cached server.jar found, downloading…");
			/* Download and parse manifest */
			if (serverJsonUrl == null) {
				if (manifest == null)
					manifest = downloadManifest(cache);
				String v = versionName;
				serverJsonUrl = manifest
						.get(versionName)
						.orElseThrow(() -> new IllegalArgumentException("Could not find version " + v)).url;
			}
			/* Download server jar */
			downloadFile(
					new JsonParser().parse(new InputStreamReader(new URL(serverJsonUrl).openStream()))
							.getAsJsonObject()
							.getAsJsonObject("downloads")
							.getAsJsonObject("server")
							.get("url")
							.getAsString(),
					jarFile);
		}
		return jarFile;
	}

	private static VersionManifest downloadManifest(Path cache) throws IOException {
		Path manifestFile = cache.resolve("version_manifest.json");
		try {
			downloadFile(MANIFEST_URL, manifestFile);
		} catch (UnknownHostException e) {
			if (Files.exists(manifestFile))
				log.warn("You seem to be offline — using a cached version. This may not actually give you the truly latest release. " + e);
			else
				throw e;
		}
		return new Gson().fromJson(Files.newBufferedReader(manifestFile), VersionManifest.class);
	}

	private static void downloadFile(String url, Path path) throws IOException {
		log.debug("Downloading " + url + " to " + path);

		try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream())) {
			Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
		}
	}

	/** Java representation of the file located at {@link Downloader#DOWNLOAD_URL} (but only the bits we need). */
	private static class VersionManifest {
		Latest		latest;
		Version[]	versions;

		Optional<Version> get(String id) {
			return Arrays.stream(versions).filter(v -> v.id.equals(id)).findAny();
		}

		static class Latest {
			String release;
		}

		static class Version {
			String id, url;
		}
	}

}
