package services;

import models.URLEntity;
import models.WebSearch;
import play.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DownloaderImage {


    private static final String DEFAULT_IMAGE_PATH = "/public/images/";
    private static String[] typeImages = {".bpm", ".gif", ".tiff", ".png", ".jpg"};

    public static String downloadImageFromURLInDB() {

        int counterNewImagesDL = 0;
        for (WebSearch webSearch : WebSearch.findAll()) {

            if (null == webSearch.urlList || webSearch.urlList.isEmpty())
                continue;

            int countNewImagesDLForOneProduct = 0;
            String path = DEFAULT_IMAGE_PATH + webSearch.name;
            List<URLEntity> urls = webSearch.urlList;

            if (urls.size() > 10)
                urls = urls.subList(0, 10);

            for (URLEntity urlEntity : urls) {

                String urlName = urlEntity.url;
                Optional<String> extension = Arrays.stream(typeImages).filter(urlName.toLowerCase()::contains).findFirst();

                if (!extension.isPresent())
                    continue;
                try {
                    if (!WriteDataFile.writeImage(path, Long.toUnsignedString(urlName.hashCode()), extension.get(), new URL(urlName)))
                        continue;
                    countNewImagesDLForOneProduct++;
                    Logger.debug("picture " + Long.toUnsignedString(urlName.hashCode()) + extension.get() + " - for " + webSearch.name);
                } catch (MalformedURLException mue) {
                    Logger.error("DownloaderImage.downloadImageFromURLInDB() - bad data :", mue);
                } catch (IOException io) {
                    Logger.error("DownloaderImage.downloadImageFromURLInDB() - IOE data :" + io);
                }
            }
            if (countNewImagesDLForOneProduct != 0) {
                webSearch.delete();
                counterNewImagesDL += countNewImagesDLForOneProduct;
            }
        }
        return counterNewImagesDL + " image's url added";
    }
}