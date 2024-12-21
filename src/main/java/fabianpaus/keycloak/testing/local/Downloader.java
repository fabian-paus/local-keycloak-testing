package fabianpaus.keycloak.testing.local;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Downloader {
    private static final String BASE_URL = "https://github.com/keycloak/keycloak/releases/download/";

    public static Path download(String version, Path parentPath) {
        Path destinationPath = parentPath.resolve("keycloak-" + version);
        if (alreadyDownloaded(destinationPath)) {
            return destinationPath;
        }

        String urlString = BASE_URL + version + "/keycloak-" + version + ".zip";
        try {
            URL url = new URI(urlString).toURL();
            try (InputStream input = url.openStream()) {
                unzip(input, parentPath);
            }
        } catch (URISyntaxException | IOException ex) {
            throw new RuntimeException(ex);
        }

        return destinationPath;
    }

    private static boolean alreadyDownloaded(Path path) {
        String[] expectedFiles = new String[]{
                "bin/kc.bat",
                "bin/kc.sh",
                "lib/quarkus-run.jar",
                "lib/app/keycloak.jar"
        };
        for (String expectedFile : expectedFiles) {
            Path expectedPath = path.resolve(expectedFile);
            if (!Files.exists(expectedPath)) return false;
        }
        return true;
    }

    private static void unzip(InputStream input, Path destination) {
        byte[] buffer = new byte[1024];

        try (ZipInputStream zipFile = new ZipInputStream(input)) {
            ZipEntry entry = zipFile.getNextEntry();
            while (entry != null) {
                String fileName = entry.getName();
                File newFile = destination.resolve(fileName).toFile();

                if (entry.isDirectory()) {
                    boolean ignored = newFile.mkdirs();
                } else {
                    writeFile(newFile, zipFile, buffer);
                }

                zipFile.closeEntry();
                entry = zipFile.getNextEntry();
            }
            zipFile.closeEntry();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeFile(File newFile, ZipInputStream zipFile, byte[] buffer) throws IOException {
        FileOutputStream output = new FileOutputStream(newFile);
        int readBytes;
        while ((readBytes = zipFile.read(buffer)) > 0) {
            output.write(buffer, 0, readBytes);
        }
        output.close();
    }
}
