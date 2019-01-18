package services;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Module;
import models.SearchImageData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.ApplicationLoader;
import play.Environment;
import play.inject.guice.GuiceApplicationBuilder;
import play.inject.guice.GuiceApplicationLoader;
import play.test.Helpers;
import services.clients.PixaBayImageClient;

import javax.inject.Inject;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SearchServiceTest {

    @Test
    public void checkThatServiceWritesToFile() {
        SearchService searchService = new SearchService();

        String searchTerm = "test";

        List<SearchImageData> urls = new ArrayList<>();
        urls.add(new SearchImageData("https://pixabay.com/get/e836b2092cfd063ed1584d05fb1d4797e077e2d11fb10c4090f4c67fa3eab5bcdd_1280.jpg"));
        urls.add(new SearchImageData("https://pixabay.com/get/e836b2092cfd063ed1584d05fb1d4797e077e2d11fb10c4090f4c67fa3eab5bcdd_1232.jpg"));
        urls.add(new SearchImageData("https://pixabay.com/get/e836b2092cfd063ed1584d05fb1d4797e077e2d11fb10c4090f4c67fa3eab5bcdd_1234.jpg"));

        searchService.saveFile(searchTerm, urls);

        File file = new File(System.getProperty("user.dir") + File.separator + "data" + File.separator + searchTerm + ".json");

        assertNotNull(file);
    }
}
