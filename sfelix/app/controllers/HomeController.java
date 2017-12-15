package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Product;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.mvc.*;
import views.html.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.CompletionStage;

@Singleton
public class HomeController extends Controller {

    @Inject
    WSClient ws;

    public Result index() {
        return ok(index.render());
    }

    public Result allProducts() {
        List<Product> products = Product.findAll();

        JsonNode json = Json.toJson(products);

        ObjectNode jsonFinal = Json.newObject();
        jsonFinal.set("products", json);
        jsonFinal.set("size", Json.toJson(products.size()));

        return (ok(jsonFinal)).as("application/json");
    }

    public CompletionStage<Result> loadTestProducts() {

        CompletionStage<WSResponse> response = ws.url("http://localhost:9000/testjson").get();

        CompletionStage<Result> response2 = response.thenApply(WSResponse::asJson).thenApply(json -> extractSize(json)).thenApply(Results::ok);

        return response2;
    }

    private String extractSize(JsonNode json) {

        return json.findPath("size").numberValue().toString();
    }
}
