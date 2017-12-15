package services

import Services.ElasticTest
import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.ElasticDsl.{bulk, fieldSort, indexInto, search}
import com.sksamuel.elastic4s.http.HttpClient
import models.Product
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.FunSuite
import org.scalatest.mockito.MockitoSugar.mock
import org.scalatest.BeforeAndAfter
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import com.sksamuel.elastic4s.analyzers.SimpleAnalyzer
import com.sksamuel.elastic4s.embedded.LocalNode
import com.sksamuel.elastic4s.{ElasticsearchClientUri, Hit, HitReader, Indexable}
import com.sksamuel.elastic4s.http.HttpClient
import com.sksamuel.elastic4s.http.search.SearchResponse
import com.sksamuel.elastic4s.searches.aggs.{AvgAggregationDefinition, TermsAggregationDefinition}
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import models.Product
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.search.sort.{SortMode, SortOrder}
import org.mockito.stubbing._
import com.sksamuel.elastic4s.ElasticClient
import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

// you must import the DSL to use the syntax helpers
import com.sksamuel.elastic4s.ElasticDsl._


class TestingElasticTest extends FunSuite{





  val node = LocalNode("localhost", "elastic/testnode")
  val client = node.elastic4sclient()
  val elasticTest = new ElasticTest(client)






  test("first test"){
    client.execute {
      bulk(
        indexInto("myindex" / "mytype").fields("country" -> "Mongolia", "capital" -> "Ulaanbaatar"),
        indexInto("myindex" / "mytype").fields("country" -> "Namibia", "capital" -> "Windhoek")
      ).refresh(RefreshPolicy.WAIT_UNTIL)
    }.await


    assert(elasticTest.resultOfSearch().await==="{\"country\":\"Mongolia\",\"capital\":\"Ulaanbaatar\"}")
  }

  test("delete existing index, index two products and search for one"){
    elasticTest.deleteProdindex
    elasticTest.indexProductList(List(Product("1","un","un objet a test",BigDecimal("10")),Product("2","two","an object to test",BigDecimal("40"))))
    assert(elasticTest.getProd("1").await._1.name === "un")
  }

  test("search word and return product list and average price"){
    assert(elasticTest.searchProduct("test").await._1.filter(p => p.name=="un")===
      List(Product("1","un","un objet a test",BigDecimal("10"))))
    assert(elasticTest.searchProduct("test").await._2 === 25.00)

  }

  test("search next page test"){
    assert(elasticTest.searchProductsNext(1,5,"name.raw").await._1.size === 1)

  }


  test("delete existing index, index two products, delete one and check it no longer exists"){
    elasticTest.deleteProdindex
    elasticTest.indexProductList(List(Product("1","un","un objet a test",BigDecimal("10")),Product("2","two","an object to test",BigDecimal("40"))))
    elasticTest.deleteProd("1").map{del =>
      assert(elasticTest.getProd("1").await._1 === Product("","","",0))
    }
  }

  test("agregate search"){
    assert(elasticTest.agregateSearch.await.head.head._1 === "an object to test")
  }


  test("update a product and check new version"){
    elasticTest.updateProd("2",Product("2","two","an object to test",BigDecimal("60"))).map{res =>
      assert(elasticTest.getProd("2").await._1.price === 60)
    }
  }




}
