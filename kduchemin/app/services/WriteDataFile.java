package services;

import play.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

class WriteDataFile {

    private static boolean createDirectoryIfNotExist(String path) {
        File dir;
        StringBuilder sb = new StringBuilder().append(path.split("/", 2)[0]);
        for (String dirName : path.split("/", 2)[1].split("/")) {
            sb.append("/").append(dirName);
            dir = new File(sb.toString());
            if (!dir.exists()) {
                if (!dir.mkdir()) {
                    System.err.println("Directory : " + dir + " - can't be created");
                    return false;
                }
            }
        }
        return true;
    }

    private static String validDirPathName(String path) {

        String finalPath = Objects.requireNonNull(path);
        if (!path.startsWith("./") && !path.startsWith("../") && !path.startsWith("~"))
            if (path.startsWith("/"))
                finalPath = "." + path;
            else
                finalPath = "./" + path;
        if (!path.endsWith("/"))
            finalPath = finalPath + "/";
        if (finalPath.endsWith("//"))
            throw new IllegalArgumentException("Directory Path : " + path + " - No Valid!");
        return finalPath;
    }

    static boolean writeImage(String pathDir, String fileName, String extension, URL url) throws IOException {
        pathDir = validDirPathName(pathDir);
        if (!createDirectoryIfNotExist(pathDir))
            return false;
        String path = pathDir + fileName + extension;
        int value;

        if (Files.exists(Paths.get(path)))
            return false;
        try (InputStream data = url.openStream(); FileOutputStream file = new FileOutputStream(path)) {
            byte[] buffer = new byte[1024];
            while ((value = data.read(buffer)) != -1)
                file.write(buffer, 0, value);
        } catch (IOException ioe) {
            Logger.error("IOException: " + ioe.getMessage());
            return false;
        }
        return true;
    }

}
