package Services


import com.sksamuel.elastic4s.analyzers.SimpleAnalyzer
import com.sksamuel.elastic4s.{ElasticsearchClientUri, Hit, HitReader, Indexable}
import com.sksamuel.elastic4s.http.HttpClient
import com.sksamuel.elastic4s.http.search.SearchResponse
import com.sksamuel.elastic4s.searches.aggs.{AvgAggregationDefinition, TermsAggregationDefinition}
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import models.Product
import org.elasticsearch.search.sort.{SortMode, SortOrder}






object ElasticTest{
  // you must import the DSL to use the syntax helpers
  import com.sksamuel.elastic4s.http.ElasticDsl._

  val client = HttpClient(ElasticsearchClientUri("localhost", 9200))



  implicit object CharacterHitReader extends HitReader[Product] {
    override def read(hit: Hit): Either[Throwable, Product] = {
      Right(Product(hit.sourceAsMap("ean").toString, hit.sourceAsMap("name").toString,hit.sourceAsMap("description").toString,BigDecimal(hit.sourceAsMap("price").toString)))
    }
  }

  implicit object CharacterIndexable extends Indexable[Product] {
    override def json(t: Product): String = s""" { "ean" : "${t.ean}", "name" : "${t.name}", "description" : "${t.description}", "price" : "${t.price}" } """
  }


  def sendInfo() {
    println("info sent")
    client.execute {
      bulk(
        indexInto("myindex" / "mytype").fields("country" -> "Mongolia", "capital" -> "Ulaanbaatar"),
        indexInto("myindex" / "mytype").fields("country" -> "Namibia", "capital" -> "Windhoek")
      ).refresh(RefreshPolicy.WAIT_UNTIL)
    }.await
  }

  def resultOfSearch():String = {
    val result: SearchResponse = client.execute {
      search("myindex").matchQuery("capital", "ulaanbaatar")
    }.await

    println("info read")
    // prints out the original json
    println(result.hits.hits.head.sourceAsString)
    result.hits.hits.head.sourceAsString
  }
  //client.close()


  def indexProductList(prod : List[Product]): Unit = {
    client.execute {
      createIndex("productindex") mappings (
        mapping("producttype") as (
          keywordField("ean"),
          textField("name") fielddata true,
          textField("description") fielddata true analyzer SimpleAnalyzer,
          floatField("price")
        )
        )
    }.await
    client.execute {
      bulk(prod.map(x=>indexInto("productindex" / "producttype").doc(x))
      ).refresh(RefreshPolicy.WAIT_UNTIL)
    }.await
  }

  def searchProducts : (List[Product],Int) = {
    val srchResult = client.execute {
      search("productindex" / "producttype") limit 500 sortBy {
        fieldSort("name")
      }
    }.await


    (srchResult.to[Product].toList,srchResult.hits.total)
  }

  def searchProductsNext (offsetForResults : Int) : (List[Product],Int) = {
    val srchResult = client.execute {
      search("productindex" / "producttype") start offsetForResults limit 500 sortBy {
        fieldSort("name")
      }
    }.await


    (srchResult.to[Product].toList,srchResult.hits.total)
  }


  def deleteProdindex = {
    client.execute { deleteIndex("productindex") }.await
  }


  def deleteProd (ean : String) = {
    client.execute {
      delete(ean).from("productindex" / "producttype")
    }.await
  }

  def getProd (ean:String): (Product,String) = {
    val result = client.execute {
      search("productindex" / "producttype") matchQuery ("ean",ean) limit 1
    }.await

    (result.to[Product].head,result.hits.hits.head.id)
  }


  def updateProd(ean: String, prod : Product) = {
    val targetId = getProd(ean)._2
    client.execute {
      update(targetId).in("productindex" / "producttype").doc(prod)
    }
  }

  def searchProduct(word : String) : (List[Product],Double) = {
    val averageAgg = AvgAggregationDefinition("average price")
      .field("price")
    val srchResult = client.execute {
      search("productindex" / "producttype") query word limit 500 aggregations(averageAgg) sortBy {
        fieldSort("name")
      }
    }.await


    (srchResult.to[Product].toList,srchResult.aggregations.get("average price").get.asInstanceOf[Map[String,AnyRef]].get("value").get.asInstanceOf[Double])
  }



  def agregateSearch : Seq[Map[String, AnyRef]] = {
    val buckets = TermsAggregationDefinition("buckets")
        .field("description")
    val result = client.execute {
      search("productindex" / "producttype") aggregations(buckets)
    }.await

    val test: Seq[Map[String, AnyRef]] = result.aggregations.get("buckets").get.asInstanceOf[Map[String, AnyRef]].get("buckets").get.asInstanceOf[List[Map[String, AnyRef]]]

    test.take(10)
  }

}


