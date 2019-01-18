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


public class PixaBayImageClientTest {

    static String data = "{\n" +
            "    \"totalHits\": 500,\n" +
            "    \"hits\": [\n" +
            "        {\n" +
            "            \"largeImageURL\": \"https://pixabay.com/get/e835b90e2ff4093ed1584d05fb1d4797e077e2d11fb10c4090f4c67fa3eab5bcdd_1280.jpg\",\n" +
            "            \"webformatHeight\": 426,\n" +
            "            \"webformatWidth\": 640,\n" +
            "            \"likes\": 251,\n" +
            "            \"imageWidth\": 3500,\n" +
            "            \"id\": 1081708,\n" +
            "            \"user_id\": 242387,\n" +
            "            \"views\": 80538,\n" +
            "            \"comments\": 22,\n" +
            "            \"pageURL\": \"https://pixabay.com/en/desk-table-simple-mockup-1081708/\",\n" +
            "            \"imageHeight\": 2333,\n" +
            "            \"webformatURL\": \"https://pixabay.com/get/e835b90e2ff4093ed1584d05fb1d4797e077e2d11fb10c4090f4c67fa3eab5bcdd_640.jpg\",\n" +
            "            \"type\": \"photo\",\n" +
            "            \"previewHeight\": 99,\n" +
            "            \"tags\": \"desk, table, simple\",\n" +
            "            \"downloads\": 34876,\n" +
            "            \"user\": \"Free-Photos\",\n" +
            "            \"favorites\": 481,\n" +
            "            \"imageSize\": 536110,\n" +
            "            \"previewWidth\": 150,\n" +
            "            \"userImageURL\": \"https://cdn.pixabay.com/user/2014/05/07/00-10-34-2_250x250.jpg\",\n" +
            "            \"previewURL\": \"https://cdn.pixabay.com/photo/2015/12/08/00/26/desk-1081708_150.jpg\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"largeImageURL\": \"https://pixabay.com/get/ee36b00e2ff51c22d2524518b74d4795e377e6d319ac104491f7c77da0edb1bd_1280.jpg\",\n" +
            "            \"webformatHeight\": 640,\n" +
            "            \"webformatWidth\": 426,\n" +
            "            \"likes\": 190,\n" +
            "            \"imageWidth\": 4200,\n" +
            "            \"id\": 731171,\n" +
            "            \"user_id\": 242387,\n" +
            "            \"views\": 44022,\n" +
            "            \"comments\": 17,\n" +
            "            \"pageURL\": \"https://pixabay.com/en/chair-balloon-celebration-party-731171/\",\n" +
            "            \"imageHeight\": 6300,\n" +
            "            \"webformatURL\": \"https://pixabay.com/get/ee36b00e2ff51c22d2524518b74d4795e377e6d319ac104491f7c77da0edb1bd_640.jpg\",\n" +
            "            \"type\": \"photo\",\n" +
            "            \"previewHeight\": 150,\n" +
            "            \"tags\": \"chair, balloon, celebration\",\n" +
            "            \"downloads\": 26960,\n" +
            "            \"user\": \"Free-Photos\",\n" +
            "            \"favorites\": 350,\n" +
            "            \"imageSize\": 3604091,\n" +
            "            \"previewWidth\": 100,\n" +
            "            \"userImageURL\": \"https://cdn.pixabay.com/user/2014/05/07/00-10-34-2_250x250.jpg\",\n" +
            "            \"previewURL\": \"https://cdn.pixabay.com/photo/2015/04/20/13/13/chair-731171_150.jpg\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"largeImageURL\": \"https://pixabay.com/get/ea30b20820f4003ed1584d05fb1d4797e077e2d11fb10c4090f4c67fa3eab5bcdd_1280.jpg\",\n" +
            "            \"webformatHeight\": 423,\n" +
            "            \"webformatWidth\": 640,\n" +
            "            \"likes\": 913,\n" +
            "            \"imageWidth\": 3776,\n" +
            "            \"id\": 3537801,\n" +
            "            \"user_id\": 8164369,\n" +
            "            \"views\": 343963,\n" +
            "            \"comments\": 141,\n" +
            "            \"pageURL\": \"https://pixabay.com/en/cafe-architecture-building-greece-3537801/\",\n" +
            "            \"imageHeight\": 2500,\n" +
            "            \"webformatURL\": \"https://pixabay.com/get/ea30b20820f4003ed1584d05fb1d4797e077e2d11fb10c4090f4c67fa3eab5bcdd_640.jpg\",\n" +
            "            \"type\": \"photo\",\n" +
            "            \"previewHeight\": 99,\n" +
            "            \"tags\": \"cafe, architecture, building\",\n" +
            "            \"downloads\": 280249,\n" +
            "            \"user\": \"analogicus\",\n" +
            "            \"favorites\": 733,\n" +
            "            \"imageSize\": 5025321,\n" +
            "            \"previewWidth\": 150,\n" +
            "            \"userImageURL\": \"https://cdn.pixabay.com/user/2019/01/13/01-46-16-171_250x250.jpg\",\n" +
            "            \"previewURL\": \"https://cdn.pixabay.com/photo/2018/07/14/15/27/cafe-3537801_150.jpg\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"largeImageURL\": \"https://pixabay.com/get/e83db7082ff1003ed1584d05fb1d4797e077e2d11fb10c4090f4c67fa3eab5bcdd_1280.jpg\",\n" +
            "            \"webformatHeight\": 426,\n" +
            "            \"webformatWidth\": 640,\n" +
            "            \"likes\": 184,\n" +
            "            \"imageWidth\": 4000,\n" +
            "            \"id\": 1867751,\n" +
            "            \"user_id\": 2286921,\n" +
            "            \"views\": 46430,\n" +
            "            \"comments\": 11,\n" +
            "            \"pageURL\": \"https://pixabay.com/en/kindle-ereader-tablet-e-reader-1867751/\",\n" +
            "            \"imageHeight\": 2667,\n" +
            "            \"webformatURL\": \"https://pixabay.com/get/e83db7082ff1003ed1584d05fb1d4797e077e2d11fb10c4090f4c67fa3eab5bcdd_640.jpg\",\n" +
            "            \"type\": \"photo\",\n" +
            "            \"previewHeight\": 99,\n" +
            "            \"tags\": \"kindle, ereader, tablet\",\n" +
            "            \"downloads\": 23566,\n" +
            "            \"user\": \"Pexels\",\n" +
            "            \"favorites\": 377,\n" +
            "            \"imageSize\": 2272753,\n" +
            "            \"previewWidth\": 150,\n" +
            "            \"userImageURL\": \"https://cdn.pixabay.com/user/2016/03/26/22-06-36-459_250x250.jpg\",\n" +
            "            \"previewURL\": \"https://cdn.pixabay.com/photo/2016/11/29/06/16/kindle-1867751_150.jpg\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"largeImageURL\": \"https://pixabay.com/get/e83db20b2bf6093ed1584d05fb1d4797e077e2d11fb10c4090f4c67fa3eab5bcdd_1280.jpg\",\n" +
            "            \"webformatHeight\": 426,\n" +
            "            \"webformatWidth\": 640,\n" +
            "            \"likes\": 115,\n" +
            "            \"imageWidth\": 5184,\n" +
            "            \"id\": 1834328,\n" +
            "            \"user_id\": 2286921,\n" +
            "            \"views\": 29423,\n" +
            "            \"comments\": 9,\n" +
            "            \"pageURL\": \"https://pixabay.com/en/apple-chair-computer-desk-table-1834328/\",\n" +
            "            \"imageHeight\": 3456,\n" +
            "            \"webformatURL\": \"https://pixabay.com/get/e83db20b2bf6093ed1584d05fb1d4797e077e2d11fb10c4090f4c67fa3eab5bcdd_640.jpg\",\n" +
            "            \"type\": \"photo\",\n" +
            "            \"previewHeight\": 99,\n" +
            "            \"tags\": \"apple, chair, computer\",\n" +
            "            \"downloads\": 14963,\n" +
            "            \"user\": \"Pexels\",\n" +
            "            \"favorites\": 264,\n" +
            "            \"imageSize\": 1409104,\n" +
            "            \"previewWidth\": 150,\n" +
            "            \"userImageURL\": \"https://cdn.pixabay.com/user/2016/03/26/22-06-36-459_250x250.jpg\",\n" +
            "            \"previewURL\": \"https://cdn.pixabay.com/photo/2016/11/18/13/03/apple-1834328_150.jpg\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"largeImageURL\": \"https://pixabay.com/get/e835b20e2cfd053ed1584d05fb1d4797e077e2d11fb10c4090f4c67fa3eab5bcdd_1280.jpg\",\n" +
            "            \"webformatHeight\": 426,\n" +
            "            \"webformatWidth\": 640,\n" +
            "            \"likes\": 124,\n" +
            "            \"imageWidth\": 4121,\n" +
            "            \"id\": 1031494,\n" +
            "            \"user_id\": 242387,\n" +
            "            \"views\": 18866,\n" +
            "            \"comments\": 7,\n" +
            "            \"pageURL\": \"https://pixabay.com/en/conservatory-coffee-plants-table-1031494/\",\n" +
            "            \"imageHeight\": 2747,\n" +
            "            \"webformatURL\": \"https://pixabay.com/get/e835b20e2cfd053ed1584d05fb1d4797e077e2d11fb10c4090f4c67fa3eab5bcdd_640.jpg\",\n" +
            "            \"type\": \"photo\",\n" +
            "            \"previewHeight\": 99,\n" +
            "            \"tags\": \"conservatory, coffee, plants\",\n" +
            "            \"downloads\": 7297,\n" +
            "            \"user\": \"Free-Photos\",\n" +
            "            \"favorites\": 186,\n" +
            "            \"imageSize\": 2522888,\n" +
            "            \"previewWidth\": 150,\n" +
            "            \"userImageURL\": \"https://cdn.pixabay.com/user/2014/05/07/00-10-34-2_250x250.jpg\",\n" +
            "            \"previewURL\": \"https://cdn.pixabay.com/photo/2015/11/07/11/48/conservatory-1031494_150.jpg\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"largeImageURL\": \"https://pixabay.com/get/e83db60d20fc093ed1584d05fb1d4797e077e2d11fb10c4090f4c67fa3eab5bcdd_1280.jpg\",\n" +
            "            \"webformatHeight\": 640,\n" +
            "            \"webformatWidth\": 426,\n" +
            "            \"likes\": 163,\n" +
            "            \"imageWidth\": 3744,\n" +
            "            \"id\": 1872888,\n" +
            "            \"user_id\": 3888952,\n" +
            "            \"views\": 60070,\n" +
            "            \"comments\": 10,\n" +
            "            \"pageURL\": \"https://pixabay.com/en/cafe-restaurant-chair-outdoors-1872888/\",\n" +
            "            \"imageHeight\": 5616,\n" +
            "            \"webformatURL\": \"https://pixabay.com/get/e83db60d20fc093ed1584d05fb1d4797e077e2d11fb10c4090f4c67fa3eab5bcdd_640.jpg\",\n" +
            "            \"type\": \"photo\",\n" +
            "            \"previewHeight\": 150,\n" +
            "            \"tags\": \"cafe, restaurant, chair\",\n" +
            "            \"downloads\": 48572,\n" +
            "            \"user\": \"3888952\",\n" +
            "            \"favorites\": 222,\n" +
            "            \"imageSize\": 5822621,\n" +
            "            \"previewWidth\": 100,\n" +
            "            \"userImageURL\": \"\",\n" +
            "            \"previewURL\": \"https://cdn.pixabay.com/photo/2016/11/30/14/08/cafe-1872888_150.jpg\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"largeImageURL\": \"https://pixabay.com/get/eb34b40a2bf3073ed1584d05fb1d4797e077e2d11fb10c4090f4c67fa3eab5bcdd_1280.jpg\",\n" +
            "            \"webformatHeight\": 411,\n" +
            "            \"webformatWidth\": 640,\n" +
            "            \"likes\": 176,\n" +
            "            \"imageWidth\": 5808,\n" +
            "            \"id\": 2155376,\n" +
            "            \"user_id\": 427626,\n" +
            "            \"views\": 60566,\n" +
            "            \"comments\": 19,\n" +
            "            \"pageURL\": \"https://pixabay.com/en/living-room-chair-sofa-couch-home-2155376/\",\n" +
            "            \"imageHeight\": 3732,\n" +
            "            \"webformatURL\": \"https://pixabay.com/get/eb34b40a2bf3073ed1584d05fb1d4797e077e2d11fb10c4090f4c67fa3eab5bcdd_640.jpg\",\n" +
            "            \"type\": \"photo\",\n" +
            "            \"previewHeight\": 96,\n" +
            "            \"tags\": \"living room, chair, sofa\",\n" +
            "            \"downloads\": 31372,\n" +
            "            \"user\": \"ErikaWittlieb\",\n" +
            "            \"favorites\": 307,\n" +
            "            \"imageSize\": 3756792,\n" +
            "            \"previewWidth\": 150,\n" +
            "            \"userImageURL\": \"https://cdn.pixabay.com/user/2017/03/15/03-09-43-465_250x250.jpg\",\n" +
            "            \"previewURL\": \"https://cdn.pixabay.com/photo/2017/03/19/01/43/living-room-2155376_150.jpg\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"largeImageURL\": \"https://pixabay.com/get/e83db50f2df6073ed1584d05fb1d4797e077e2d11fb10c4090f4c67fa3eab5bcdd_1280.jpg\",\n" +
            "            \"webformatHeight\": 400,\n" +
            "            \"webformatWidth\": 640,\n" +
            "            \"likes\": 136,\n" +
            "            \"imageWidth\": 3872,\n" +
            "            \"id\": 1840526,\n" +
            "            \"user_id\": 2286921,\n" +
            "            \"views\": 19657,\n" +
            "            \"comments\": 4,\n" +
            "            \"pageURL\": \"https://pixabay.com/en/chair-cottage-country-style-1840526/\",\n" +
            "            \"imageHeight\": 2420,\n" +
            "            \"webformatURL\": \"https://pixabay.com/get/e83db50f2df6073ed1584d05fb1d4797e077e2d11fb10c4090f4c67fa3eab5bcdd_640.jpg\",\n" +
            "            \"type\": \"photo\",\n" +
            "            \"previewHeight\": 93,\n" +
            "            \"tags\": \"chair, cottage, country style\",\n" +
            "            \"downloads\": 9650,\n" +
            "            \"user\": \"Pexels\",\n" +
            "            \"favorites\": 209,\n" +
            "            \"imageSize\": 3573270,\n" +
            "            \"previewWidth\": 150,\n" +
            "            \"userImageURL\": \"https://cdn.pixabay.com/user/2016/03/26/22-06-36-459_250x250.jpg\",\n" +
            "            \"previewURL\": \"https://cdn.pixabay.com/photo/2016/11/19/17/39/chair-1840526_150.jpg\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"largeImageURL\": \"https://pixabay.com/get/e836b2092cfd063ed1584d05fb1d4797e077e2d11fb10c4090f4c67fa3eab5bcdd_1280.jpg\",\n" +
            "            \"webformatHeight\": 360,\n" +
            "            \"webformatWidth\": 640,\n" +
            "            \"likes\": 128,\n" +
            "            \"imageWidth\": 3840,\n" +
            "            \"id\": 1336497,\n" +
            "            \"user_id\": 2240009,\n" +
            "            \"views\": 30794,\n" +
            "            \"comments\": 13,\n" +
            "            \"pageURL\": \"https://pixabay.com/en/room-interior-showpiece-design-1336497/\",\n" +
            "            \"imageHeight\": 2160,\n" +
            "            \"webformatURL\": \"https://pixabay.com/get/e836b2092cfd063ed1584d05fb1d4797e077e2d11fb10c4090f4c67fa3eab5bcdd_640.jpg\",\n" +
            "            \"type\": \"photo\",\n" +
            "            \"previewHeight\": 84,\n" +
            "            \"tags\": \"room, interior, showpiece\",\n" +
            "            \"downloads\": 16477,\n" +
            "            \"user\": \"Monoar\",\n" +
            "            \"favorites\": 170,\n" +
            "            \"imageSize\": 1148413,\n" +
            "            \"previewWidth\": 150,\n" +
            "            \"userImageURL\": \"https://cdn.pixabay.com/user/2016/03/18/05-51-03-682_250x250.jpg\",\n" +
            "            \"previewURL\": \"https://cdn.pixabay.com/photo/2016/04/18/13/53/room-1336497_150.jpg\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"largeImageURL\": \"https://pixabay.com/get/e83cb50f29f3053ed1584d05fb1d4797e077e2d11fb10c4090f4c67fa3eab5bcdd_1280.jpg\",\n" +
            "            \"webformatHeight\": 426,\n" +
            "            \"webformatWidth\": 640,\n" +
            "            \"likes\": 204,\n" +
            "            \"imageWidth\": 3000,\n" +
            "            \"id\": 1940174,\n" +
            "            \"user_id\": 3107153,\n" +
            "            \"views\": 58021,\n" +
            "            \"comments\": 14,\n" +
            "            \"pageURL\": \"https://pixabay.com/en/kitchen-interior-design-room-home-1940174/\",\n" +
            "            \"imageHeight\": 2000,\n" +
            "            \"webformatURL\": \"https://pixabay.com/get/e83cb50f29f3053ed1584d05fb1d4797e077e2d11fb10c4090f4c67fa3eab5bcdd_640.jpg\",\n" +
            "            \"type\": \"photo\",\n" +
            "            \"previewHeight\": 99,\n" +
            "            \"tags\": \"kitchen, interior design, room\",\n" +
            "            \"downloads\": 28531,\n" +
            "            \"user\": \"shadowfirearts\",\n" +
            "            \"favorites\": 248,\n" +
            "            \"imageSize\": 1175807,\n" +
            "            \"previewWidth\": 150,\n" +
            "            \"userImageURL\": \"https://cdn.pixabay.com/user/2016/08/16/03-47-59-486_250x250.jpg\",\n" +
            "            \"previewURL\": \"https://cdn.pixabay.com/photo/2016/12/30/07/59/kitchen-1940174_150.jpg\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"largeImageURL\": \"https://pixabay.com/get/e83db70728f1003ed1584d05fb1d4797e077e2d11fb10c4090f4c67fa3eab5bcdd_1280.jpg\",\n" +
            "            \"webformatHeight\": 426,\n" +
            "            \"webformatWidth\": 640,\n" +
            "            \"likes\": 108,\n" +
            "            \"imageWidth\": 5760,\n" +
            "            \"id\": 1868051,\n" +
            "            \"user_id\": 2286921,\n" +
            "            \"views\": 16546,\n" +
            "            \"comments\": 4,\n" +
            "            \"pageURL\": \"https://pixabay.com/en/balancing-chair-fashion-man-model-1868051/\",\n" +
            "            \"imageHeight\": 3840,\n" +
            "            \"webformatURL\": \"https://pixabay.com/get/e83db70728f1003ed1584d05fb1d4797e077e2d11fb10c4090f4c67fa3eab5bcdd_640.jpg\",\n" +
            "            \"type\": \"photo\",\n" +
            "            \"previewHeight\": 99,\n" +
            "            \"tags\": \"balancing, chair, fashion\",\n" +
            "            \"downloads\": 8460,\n" +
            "            \"user\": \"Pexels\",\n" +
            "            \"favorites\": 164,\n" +
            "            \"imageSize\": 1086796,\n" +
            "            \"previewWidth\": 150,\n" +
            "            \"userImageURL\": \"https://cdn.pixabay.com/user/2016/03/26/22-06-36-459_250x250.jpg\",\n" +
            "            \"previewURL\": \"https://cdn.pixabay.com/photo/2016/11/29/07/16/balancing-1868051_150.jpg\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"largeImageURL\": \"https://pixabay.com/get/e835b60721f6023ed1584d05fb1d4797e077e2d11fb10c4090f4c67fa3eab5bcdd_1280.jpg\",\n" +
            "            \"webformatHeight\": 425,\n" +
            "            \"webformatWidth\": 640,\n" +
            "            \"likes\": 111,\n" +
            "            \"imageWidth\": 6016,\n" +
            "            \"id\": 1078923,\n" +
            "            \"user_id\": 427626,\n" +
            "            \"views\": 30612,\n" +
            "            \"comments\": 7,\n" +
            "            \"pageURL\": \"https://pixabay.com/en/nursery-crib-chair-bedroom-room-1078923/\",\n" +
            "            \"imageHeight\": 4000,\n" +
            "            \"webformatURL\": \"https://pixabay.com/get/e835b60721f6023ed1584d05fb1d4797e077e2d11fb10c4090f4c67fa3eab5bcdd_640.jpg\",\n" +
            "            \"type\": \"photo\",\n" +
            "            \"previewHeight\": 99,\n" +
            "            \"tags\": \"nursery, crib, chair\",\n" +
            "            \"downloads\": 14615,\n" +
            "            \"user\": \"ErikaWittlieb\",\n" +
            "            \"favorites\": 132,\n" +
            "            \"imageSize\": 3921299,\n" +
            "            \"previewWidth\": 150,\n" +
            "            \"userImageURL\": \"https://cdn.pixabay.com/user/2017/03/15/03-09-43-465_250x250.jpg\",\n" +
            "            \"previewURL\": \"https://cdn.pixabay.com/photo/2015/12/05/23/38/nursery-1078923_150.jpg\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"largeImageURL\": \"https://pixabay.com/get/e83db50a2af3013ed1584d05fb1d4797e077e2d11fb10c4090f4c67fa3eab5bcdd_1280.jpg\",\n" +
            "            \"webformatHeight\": 426,\n" +
            "            \"webformatWidth\": 640,\n" +
            "            \"likes\": 102,\n" +
            "            \"imageWidth\": 4272,\n" +
            "            \"id\": 1845270,\n" +
            "            \"user_id\": 10087552,\n" +
            "            \"views\": 25385,\n" +
            "            \"comments\": 4,\n" +
            "            \"pageURL\": \"https://pixabay.com/en/chair-furniture-indoors-1845270/\",\n" +
            "            \"imageHeight\": 2848,\n" +
            "            \"webformatURL\": \"https://pixabay.com/get/e83db50a2af3013ed1584d05fb1d4797e077e2d11fb10c4090f4c67fa3eab5bcdd_640.jpg\",\n" +
            "            \"type\": \"photo\",\n" +
            "            \"previewHeight\": 99,\n" +
            "            \"tags\": \"chair, furniture, indoors\",\n" +
            "            \"downloads\": 14157,\n" +
            "            \"user\": \"karishea\",\n" +
            "            \"favorites\": 171,\n" +
            "            \"imageSize\": 3487934,\n" +
            "            \"previewWidth\": 150,\n" +
            "            \"userImageURL\": \"https://cdn.pixabay.com/user/2018/10/08/20-08-42-478_250x250.jpg\",\n" +
            "            \"previewURL\": \"https://cdn.pixabay.com/photo/2016/11/21/12/59/chair-1845270_150.jpg\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"largeImageURL\": \"https://pixabay.com/get/ec33b80c2ef51c22d2524518b74d4795e377e6d319ac104491f7c77da0edb1bd_1280.jpg\",\n" +
            "            \"webformatHeight\": 480,\n" +
            "            \"webformatWidth\": 640,\n" +
            "            \"likes\": 61,\n" +
            "            \"imageWidth\": 4000,\n" +
            "            \"id\": 569361,\n" +
            "            \"user_id\": 242387,\n" +
            "            \"views\": 26661,\n" +
            "            \"comments\": 5,\n" +
            "            \"pageURL\": \"https://pixabay.com/en/architect-desk-office-workplace-569361/\",\n" +
            "            \"imageHeight\": 3000,\n" +
            "            \"webformatURL\": \"https://pixabay.com/get/ec33b80c2ef51c22d2524518b74d4795e377e6d319ac104491f7c77da0edb1bd_640.jpg\",\n" +
            "            \"type\": \"photo\",\n" +
            "            \"previewHeight\": 112,\n" +
            "            \"tags\": \"architect, desk, office\",\n" +
            "            \"downloads\": 13405,\n" +
            "            \"user\": \"Free-Photos\",\n" +
            "            \"favorites\": 160,\n" +
            "            \"imageSize\": 4438086,\n" +
            "            \"previewWidth\": 150,\n" +
            "            \"userImageURL\": \"https://cdn.pixabay.com/user/2014/05/07/00-10-34-2_250x250.jpg\",\n" +
            "            \"previewURL\": \"https://cdn.pixabay.com/photo/2014/12/15/17/19/architect-569361_150.jpg\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"largeImageURL\": \"https://pixabay.com/get/e830b50628fd073ed1584d05fb1d4797e077e2d11fb10c4090f4c67fa3eab5bcdd_1280.jpg\",\n" +
            "            \"webformatHeight\": 426,\n" +
            "            \"webformatWidth\": 640,\n" +
            "            \"likes\": 207,\n" +
            "            \"imageWidth\": 4470,\n" +
            "            \"id\": 1549096,\n" +
            "            \"user_id\": 652234,\n" +
            "            \"views\": 35907,\n" +
            "            \"comments\": 29,\n" +
            "            \"pageURL\": \"https://pixabay.com/en/lost-places-old-decay-ruin-factory-1549096/\",\n" +
            "            \"imageHeight\": 2980,\n" +
            "            \"webformatURL\": \"https://pixabay.com/get/e830b50628fd073ed1584d05fb1d4797e077e2d11fb10c4090f4c67fa3eab5bcdd_640.jpg\",\n" +
            "            \"type\": \"photo\",\n" +
            "            \"previewHeight\": 99,\n" +
            "            \"tags\": \"lost places, old, decay\",\n" +
            "            \"downloads\": 20719,\n" +
            "            \"user\": \"MichaelGaida\",\n" +
            "            \"favorites\": 267,\n" +
            "            \"imageSize\": 1641067,\n" +
            "            \"previewWidth\": 150,\n" +
            "            \"userImageURL\": \"https://cdn.pixabay.com/user/2018/08/23/19-08-53-332_250x250.jpg\",\n" +
            "            \"previewURL\": \"https://cdn.pixabay.com/photo/2016/07/28/19/38/lost-places-1549096_150.jpg\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"largeImageURL\": \"https://pixabay.com/get/eb34b90e21f2013ed1584d05fb1d4797e077e2d11fb10c4090f4c67fa3eab5bcdd_1280.jpg\",\n" +
            "            \"webformatHeight\": 426,\n" +
            "            \"webformatWidth\": 640,\n" +
            "            \"likes\": 154,\n" +
            "            \"imageWidth\": 5616,\n" +
            "            \"id\": 2181960,\n" +
            "            \"user_id\": 2286921,\n" +
            "            \"views\": 43926,\n" +
            "            \"comments\": 13,\n" +
            "            \"pageURL\": \"https://pixabay.com/en/chairs-contemporary-furniture-2181960/\",\n" +
            "            \"imageHeight\": 3744,\n" +
            "            \"webformatURL\": \"https://pixabay.com/get/eb34b90e21f2013ed1584d05fb1d4797e077e2d11fb10c4090f4c67fa3eab5bcdd_640.jpg\",\n" +
            "            \"type\": \"photo\",\n" +
            "            \"previewHeight\": 99,\n" +
            "            \"tags\": \"chairs, contemporary, furniture\",\n" +
            "            \"downloads\": 23068,\n" +
            "            \"user\": \"Pexels\",\n" +
            "            \"favorites\": 299,\n" +
            "            \"imageSize\": 2771191,\n" +
            "            \"previewWidth\": 150,\n" +
            "            \"userImageURL\": \"https://cdn.pixabay.com/user/2016/03/26/22-06-36-459_250x250.jpg\",\n" +
            "            \"previewURL\": \"https://cdn.pixabay.com/photo/2017/03/28/12/11/chairs-2181960_150.jpg\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"largeImageURL\": \"https://pixabay.com/get/e83db50f28f5003ed1584d05fb1d4797e077e2d11fb10c4090f4c67fa3eab5bcdd_1280.jpg\",\n" +
            "            \"webformatHeight\": 360,\n" +
            "            \"webformatWidth\": 640,\n" +
            "            \"likes\": 66,\n" +
            "            \"imageWidth\": 4213,\n" +
            "            \"id\": 1840011,\n" +
            "            \"user_id\": 2286921,\n" +
            "            \"views\": 15993,\n" +
            "            \"comments\": 5,\n" +
            "            \"pageURL\": \"https://pixabay.com/en/chair-couch-furniture-road-sofa-1840011/\",\n" +
            "            \"imageHeight\": 2370,\n" +
            "            \"webformatURL\": \"https://pixabay.com/get/e83db50f28f5003ed1584d05fb1d4797e077e2d11fb10c4090f4c67fa3eab5bcdd_640.jpg\",\n" +
            "            \"type\": \"photo\",\n" +
            "            \"previewHeight\": 84,\n" +
            "            \"tags\": \"chair, couch, furniture\",\n" +
            "            \"downloads\": 8191,\n" +
            "            \"user\": \"Pexels\",\n" +
            "            \"favorites\": 104,\n" +
            "            \"imageSize\": 3067166,\n" +
            "            \"previewWidth\": 150,\n" +
            "            \"userImageURL\": \"https://cdn.pixabay.com/user/2016/03/26/22-06-36-459_250x250.jpg\",\n" +
            "            \"previewURL\": \"https://cdn.pixabay.com/photo/2016/11/19/15/50/chair-1840011_150.jpg\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"largeImageURL\": \"https://pixabay.com/get/e83db20b2ffc053ed1584d05fb1d4797e077e2d11fb10c4090f4c67fa3eab5bcdd_1280.jpg\",\n" +
            "            \"webformatHeight\": 426,\n" +
            "            \"webformatWidth\": 640,\n" +
            "            \"likes\": 135,\n" +
            "            \"imageWidth\": 4096,\n" +
            "            \"id\": 1834784,\n" +
            "            \"user_id\": 2286921,\n" +
            "            \"views\": 41294,\n" +
            "            \"comments\": 18,\n" +
            "            \"pageURL\": \"https://pixabay.com/en/brick-wall-chairs-furniture-1834784/\",\n" +
            "            \"imageHeight\": 2730,\n" +
            "            \"webformatURL\": \"https://pixabay.com/get/e83db20b2ffc053ed1584d05fb1d4797e077e2d11fb10c4090f4c67fa3eab5bcdd_640.jpg\",\n" +
            "            \"type\": \"photo\",\n" +
            "            \"previewHeight\": 99,\n" +
            "            \"tags\": \"brick wall, chairs, furniture\",\n" +
            "            \"downloads\": 22348,\n" +
            "            \"user\": \"Pexels\",\n" +
            "            \"favorites\": 207,\n" +
            "            \"imageSize\": 3208397,\n" +
            "            \"previewWidth\": 150,\n" +
            "            \"userImageURL\": \"https://cdn.pixabay.com/user/2016/03/26/22-06-36-459_250x250.jpg\",\n" +
            "            \"previewURL\": \"https://cdn.pixabay.com/photo/2016/11/18/14/05/brick-wall-1834784_150.jpg\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"largeImageURL\": \"https://pixabay.com/get/eb30b70a29f4043ed1584d05fb1d4797e077e2d11fb10c4090f4c67fa3eab5bcdd_1280.jpg\",\n" +
            "            \"webformatHeight\": 426,\n" +
            "            \"webformatWidth\": 640,\n" +
            "            \"likes\": 144,\n" +
            "            \"imageWidth\": 4896,\n" +
            "            \"id\": 2565105,\n" +
            "            \"user_id\": 894430,\n" +
            "            \"views\": 39499,\n" +
            "            \"comments\": 7,\n" +
            "            \"pageURL\": \"https://pixabay.com/en/kitchen-interior-design-room-dining-2565105/\",\n" +
            "            \"imageHeight\": 3264,\n" +
            "            \"webformatURL\": \"https://pixabay.com/get/eb30b70a29f4043ed1584d05fb1d4797e077e2d11fb10c4090f4c67fa3eab5bcdd_640.jpg\",\n" +
            "            \"type\": \"photo\",\n" +
            "            \"previewHeight\": 99,\n" +
            "            \"tags\": \"kitchen, interior design, room\",\n" +
            "            \"downloads\": 16943,\n" +
            "            \"user\": \"StockSnap\",\n" +
            "            \"favorites\": 201,\n" +
            "            \"imageSize\": 3099368,\n" +
            "            \"previewWidth\": 150,\n" +
            "            \"userImageURL\": \"https://cdn.pixabay.com/user/2015/03/30/12-22-31-508_250x250.jpg\",\n" +
            "            \"previewURL\": \"https://cdn.pixabay.com/photo/2017/08/01/12/43/kitchen-2565105_150.jpg\"\n" +
            "        }\n" +
            "    ],\n" +
            "    \"total\": 2667\n" +
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
//        PixaBayImageClient tested = new PixaBayImageClient(client);
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
        PixaBayImageClient tested = new PixaBayImageClient(client);
        JsonNode json = Json.parse(data);

        // WHEN
        List<SearchImageData> results = tested.extractURLs(json);

        // THEN
        assertEquals(20, results.size());
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
