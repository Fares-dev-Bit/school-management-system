package com.app.demo;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.app.demo.configuration.AppProperties;

import java.awt.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class DemoApplication {

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        SpringApplication.run(DemoApplication.class, args);
     // 	  openBrowser("http://localhost:8080");
    }

	@PostConstruct
	public void init() {
		try {
			String userHome = System.getProperty("user.home");
			String dataDir = System.getProperty("app.data.dir", "Documents/school-management");
			Path dir = Paths.get(userHome, dataDir, "candidates");

			Files.createDirectories(dir);
		} catch (Exception e) {
			System.err.println("Could not create data directory: " + e.getMessage());
		}
	}
	private static void openBrowser(String url) {
		try {
			String os = System.getProperty("os.name").toLowerCase();
			if (os.contains("win")) {
				new ProcessBuilder("cmd", "/c", "start", url).start();
			} else if (os.contains("mac")) {
				new ProcessBuilder("open", url).start();
			} else if (os.contains("nix") || os.contains("nux")) {
				new ProcessBuilder("xdg-open", url).start();
			} else {
				if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
					Desktop.getDesktop().browse(new URI(url));
				}
			}
		} catch (Exception e) {
			System.err.println("Failed to open browser automatically: " + e.getMessage());
		}
	}
}
