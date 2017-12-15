package actors;


import actors.SearchActorProtocol.BingSearch;
import actors.SearchActorProtocol.DownloadPics;
import actors.SearchActorProtocol.MatchingPics;
import akka.actor.Props;
import akka.actor.UntypedActor;
import models.Product;
import models.URLEntity;
import models.WebSearch;
import play.Logger;
import services.DownloaderImage;
import services.MatcherImage;
import services.WebSearchService;

import java.util.stream.Collectors;

public class SearchActor extends UntypedActor {

    public static Props props = Props.create(SearchActor.class);
    private WebSearchService web = new WebSearchService();

    public void onReceive(Object msg) throws Exception {
        if (msg instanceof BingSearch) {
            int limit = 0;
            for (String descriptionSearched : Product.allProductsDescriptionsSortedByOccurenceDesc()
                    .stream()
                    .filter(description -> !WebSearchService.visitedTarget.contains(description))
                   //.map(product -> product.description)
                    .collect(Collectors.toList())) {

                if (limit++ > 5)
                    return;
                if (descriptionSearched == null)
                    continue;
                web.searchTerm(descriptionSearched);
            }
            printDataBaseState();
        }

        if (msg instanceof DownloadPics) {
            DownloaderImage.downloadImageFromURLInDB();
            printDataBaseState();
        }

        if (msg instanceof MatchingPics) {
            MatcherImage.matching();
            printDataBaseState();
        }
    }

    private static void printDataBaseState() {
        Logger.debug(
                "DB - ws:" + WebSearch.findAll().size()
                        + " products:" + Product.findAll().size()
                        + " urls:" + URLEntity.findAll().size()
        );
    }

}
