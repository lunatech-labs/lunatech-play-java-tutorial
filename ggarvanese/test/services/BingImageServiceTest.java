package services;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.libs.Json;
import play.libs.ws.WSClient;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class BingImageServiceTest {

    @Inject
    Application application;

    @Before
    public void setup() {
//        Module testModule = new AbstractModule() {
//            @Override
//            public void configure() {
//                // Install custom test binding here
//            }
//        };
//
//        GuiceApplicationBuilder builder = new GuiceApplicationLoader()
//                .builder(new ApplicationLoader.Context(Environment.simple()))
//                .overrides(testModule);
//        Guice.createInjector(builder.applicationModule()).injectMembers(this);
//
//        Helpers.start(application);
    }

    @After
    public void teardown() throws IOException {
//        Helpers.stop(application);
        Files.deleteIfExists(Paths.get("public/images/mykeyword.txt"));
    }

    @Test
    public void checkThatItReturnsEmptyListFromEmptyJSON() {
        // GIVEN
        WSClient client = mock(WSClient.class);
        BingImageServiceImpl tested = new BingImageServiceImpl(client);
        JsonNode json = Json.parse("{}");

        // WHEN
        List<String> results = tested.extractURLs(json);

        // THEN
        assertEquals(new ArrayList<>(), results);
    }

    @Test
    public void checkThatFileisCreated() {
        /// GIVEN
        WSClient client = mock(WSClient.class);
        BingImageServiceImpl imageService = new BingImageServiceImpl(client);
        List itemlist = Arrays.asList("first item", "second item", "third item");
        String keyWord = "MyKeyWORd ";

        // WHEN
        Optional<String> filename = imageService.storeURLsAndKeyword(keyWord, itemlist);
        String name = filename.orElse("fail");

        // THEN
        assertEquals("public/images/mykeyword.txt", name);
    }

    @Test
    public void checkThatItReturnsValidURLsFromJson(){
        // GIVEN
        WSClient client = mock(WSClient.class);
        BingImageServiceImpl imageService = new BingImageServiceImpl(client);
        JsonNode json = Json.parse("{" +
                "\"type\": \"Images\"," +
                "\"value\": [" +
                "{\"datePublished\": \"2017-05-04T21:11:00.0000000Z\"," +
                "\"contentUrl\": \"item1\"}," +
                "{\"datePublished\": \"2018-05-04T21:11:00.0000000Z\"," +
                "\"contentUrl\": \"item2\"}" +
                "]" +
                "}");

        // WHEN
        List<String> stringList = imageService.extractURLs(json);

        // THEN
        assertEquals(2, stringList.size());
    }

}