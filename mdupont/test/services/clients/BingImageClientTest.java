package services.clients;

import com.fasterxml.jackson.databind.JsonNode;
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
import play.libs.Json;
import play.libs.ws.WSClient;
import play.test.Helpers;
import services.clients.PixaBayImageClient;


import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;


public class BingImageClientTest {

    static String data = "{\n" +
            "  \"_type\": \"Images\",\n" +
            "  \"instrumentation\": {\n" +
            "    \"_type\": \"ResponseInstrumentation\"\n" +
            "  },\n" +
            "  \"readLink\": \"https://api.cognitive.microsoft.com/api/v7/images/search?q=chair\",\n" +
            "  \"webSearchUrl\": \"https://www.bing.com/images/search?q=chair&FORM=OIIARP\",\n" +
            "  \"queryContext\": {\n" +
            "    \"originalQuery\": \"chair\",\n" +
            "    \"alterationDisplayQuery\": \"chair\",\n" +
            "    \"alterationOverrideQuery\": \"+chair\",\n" +
            "    \"alterationMethod\": \"AM_JustChangeIt\",\n" +
            "    \"alterationType\": \"CombinedAlterationsChained\"\n" +
            "  },\n" +
            "  \"totalEstimatedMatches\": 920,\n" +
            "  \"nextOffset\": 35,\n" +
            "  \"value\": [\n" +
            "    {\n" +
            "      \"webSearchUrl\": \"https://www.bing.com/images/search?view=detailv2&FORM=OIIRPO&q=chair&id=5846B62A9F50D30BA2196C13B13AEE3EB4D2EC71&simid=607999954113528509\",\n" +
            "      \"name\": \"Ronan Tobacco Brown Dining Chair | Pier 1 Imports\",\n" +
            "      \"thumbnailUrl\": \"https://tse2.mm.bing.net/th?id=OIP.ehJ7iHb1JYSkRCzI1ec1mQHaHa&pid=Api\",\n" +
            "      \"datePublished\": \"2016-09-08T08:14:00.0000000Z\",\n" +
            "      \"isFamilyFriendly\": true,\n" +
            "      \"contentUrl\": \"https://images.pier1.com/dis/dw/image/v2/AAID_PRD/on/demandware.static/-/Sites-pier1_master/default/dwcd617f89/images/2500470/2500470_1.jpg?sw=1600&sh=1600\",\n" +
            "      \"hostPageUrl\": \"https://www.pier1.com/ronan-tobacco-brown-dining-chair/2500470.html\",\n" +
            "      \"contentSize\": \"138493 B\",\n" +
            "      \"encodingFormat\": \"jpeg\",\n" +
            "      \"hostPageDisplayUrl\": \"https://www.pier1.com/ronan-tobacco-brown-dining-chair/2500470.html\",\n" +
            "      \"width\": 1500,\n" +
            "      \"height\": 1500,\n" +
            "      \"thumbnail\": {\n" +
            "        \"width\": 474,\n" +
            "        \"height\": 474\n" +
            "      },\n" +
            "      \"imageInsightsToken\": \"ccid_ehJ7iHb1*mid_5846B62A9F50D30BA2196C13B13AEE3EB4D2EC71*simid_607999954113528509*thid_OIP.ehJ7iHb1JYSkRCzI1ec1mQHaHa\",\n" +
            "      \"insightsMetadata\": {\n" +
            "        \"pagesIncludingCount\": 15,\n" +
            "        \"availableSizesCount\": 10\n" +
            "      },\n" +
            "      \"imageId\": \"5846B62A9F50D30BA2196C13B13AEE3EB4D2EC71\",\n" +
            "      \"accentColor\": \"462C1F\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"webSearchUrl\": \"https://www.bing.com/images/search?view=detailv2&FORM=OIIRPO&q=chair&id=827F7E993478A3443F9C2E547BF4B180340CED25&simid=608024117620442067\",\n" +
            "      \"name\": \"Colette Flax Dining Chair | Pier 1 Imports\",\n" +
            "      \"thumbnailUrl\": \"https://tse3.mm.bing.net/th?id=OIP.dIZ6_uvK9e8VxOUv4Fcy1AHaHa&pid=Api\",\n" +
            "      \"datePublished\": \"2017-01-10T17:43:00.0000000Z\",\n" +
            "      \"isFamilyFriendly\": true,\n" +
            "      \"contentUrl\": \"http://images.pier1.com/dis/dw/image/v2/AAID_PRD/on/demandware.static/-/Sites-pier1_master/default/dw34a59e45/images/2923979/2923979_1.jpg?sw=1600&sh=1600\",\n" +
            "      \"hostPageUrl\": \"http://www.pier1.com/colette-flax-dining-chair/2923979.html\",\n" +
            "      \"contentSize\": \"261952 B\",\n" +
            "      \"encodingFormat\": \"jpeg\",\n" +
            "      \"hostPageDisplayUrl\": \"www.pier1.com/colette-flax-dining-chair/2923979.html\",\n" +
            "      \"width\": 1500,\n" +
            "      \"height\": 1500,\n" +
            "      \"thumbnail\": {\n" +
            "        \"width\": 474,\n" +
            "        \"height\": 474\n" +
            "      },\n" +
            "      \"imageInsightsToken\": \"ccid_dIZ6/uvK*mid_827F7E993478A3443F9C2E547BF4B180340CED25*simid_608024117620442067*thid_OIP.dIZ6!_uvK9e8VxOUv4Fcy1AHaHa\",\n" +
            "      \"insightsMetadata\": {\n" +
            "        \"pagesIncludingCount\": 18,\n" +
            "        \"availableSizesCount\": 11\n" +
            "      },\n" +
            "      \"imageId\": \"827F7E993478A3443F9C2E547BF4B180340CED25\",\n" +
            "      \"accentColor\": \"7E6B4D\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    @Inject
    Application application;

    @Before
    public void setup() {
        Module testModule = new AbstractModule() {
            @Override
            public void configure() {
                // Install custom test binding here
            }
        };

        GuiceApplicationBuilder builder = new GuiceApplicationLoader()
                .builder(new ApplicationLoader.Context(Environment.simple()))
                .overrides(testModule);
        Guice.createInjector(builder.applicationModule()).injectMembers(this);

        Helpers.start(application);
    }

    @After
    public void teardown() {
        Helpers.stop(application);
    }

//    @Test
//    public void checkThatItReturnsEmptyListFromEmptyJSON() {
//        // GIVEN
//        WSClient client = mock(WSClient.class);
//        BingImageClient tested = new BingImageClient(client);
//        JsonNode json = Json.parse("{}");
//
//        // WHEN
//        List<String> results = tested.extractURLs(json);
//
//        // THEN
//        assertEquals(new ArrayList<>(), results);
//    }

    @Test
    public void checkThatItReturnsListFromJSON() {
        // GIVEN
        WSClient client = mock(WSClient.class);
        BingImageClient tested = new BingImageClient(client);
        JsonNode json = Json.parse(data);

        // WHEN
        List<SearchImageData> results = tested.extractURLs(json);

        // THEN
        assertEquals(2, results.size());
    }

//    @Test
//    public void checkThatItGetsImageDescriptionsFromASearchTerm() {
//        // GIVEN
//        WSClient client = mock(WSClient.class);
//        PixaBayImageClient tested = new PixaBayImageClient(client);
//
//        JsonNode json = Json.parse(data);
//
//        // WHEN
//        CompletionStage<List<String>> results = tested.getImagesFromDescription("chair");
//
//        List<String> response = null;
//        try {
//            response = results.toCompletableFuture().get();
//        } catch (InterruptedException e) {
//            Logger.error(e.getMessage(), e);
//        } catch (ExecutionException e) {
//            Logger.error(e.getMessage(), e);
//        }
//        assertEquals(20, response.size());
//
//    }
}
