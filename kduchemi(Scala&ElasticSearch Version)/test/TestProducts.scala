import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.{ElasticDsl, Hit, HitReader, TcpClient}
import com.sksamuel.elastic4s.embedded.LocalNode
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.FunSuite
import org.scalatest.tools.Durations
import services.{Product, Products}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration



class TestProducts extends FunSuite {



  test("test") {
    val localTestNode = LocalNode("node", "LocalNode/FirstNodeTest")
    val clientTCP: TcpClient = localTestNode.elastic4sclient()
    clientTCP.execute {
      bulk(
        indexInto("myindex" / "mytype").fields("country" -> "Mongolia", "capital" -> "Ulaanbaatar"),
        indexInto("myindex" / "mytype").fields("country" -> "Namibia", "capital" -> "Windhoek")
      ).refresh(RefreshPolicy.WAIT_UNTIL)
    }.await

    def resultOfSearch(): Future[String] = {
      clientTCP.execute {
        search("myindex").matchQuery("capital", "ulaanbaatar")
      }.map { result =>
        println("info read")
        // prints out the original json
        println(result.hits.head.sourceAsString)
        result.hits.head.sourceAsString
      }
    }

    assert(resultOfSearch().await === "{\"country\":\"Mongolia\",\"capital\":\"Ulaanbaatar\"}")

    clientTCP.close()
    localTestNode.stop(true)
  }


  val p0 = Product("0000", "0000", "0000", 0)
  val prod01: Product = Product("0001", "prod01", "test of product 0", 10)
  val prod02: Product = Product("0002", "prod02", "test of product 0", 20)
  val prod03: Product = Product("3333", "prod03", "test of product 0", 30)
  val prod04: Product = Product("4321", "prod04", "test of product 04", 40)
  val prod05: Product = Product("-555", "prod05", "test of product 05", 50)
  val prod06: Product = Product("0606", "prod06", "test of product 06", 60)
  val prod07: Product = Product("Jackpot777", "prod07", "test of product", 77.77)
  val prod08: Product = Product("A08B", "prod08", "test of product", 88)
  val prod09: Product = Product("D37-[09]", "prod09", "test of product", 99.9)
  val prod10: Product = Product("110", "prod10", "test of product 0", 100000)
  val prod11SameEanLike10: Product = Product("110", "prod11", "test of product 11", 110000)

  val prod21: Product = Product("2-0001", "prod01", "test of product 20", 10)
  val prod22: Product = Product("2-0002", "prod02", "test of product 21", 20)
  val prod23: Product = Product("2-3333", "prod03", "test of product 22", 30)
  val prod24: Product = Product("2-4321", "prod04", "test of product 204", 40)
  val prod25: Product = Product("2--555", "prod05", "test of product 205", 50)
  val prod26: Product = Product("2-0606", "prod06", "test of product 206", 60)
  val prod27: Product = Product("2-Jackpot777", "prod07", "test of product 27", 77.77)
  val prod28: Product = Product("2-A08B", "prod08", "test of product 28", 88)
  val prod29: Product = Product("2-D37-[09]", "prod09", "test of product23", 99.9)
  val prod20: Product = Product("2-110", "prod10", "test of product 0", 100000)

  val prod31: Product = Product("3-0001", "prod01", "test of product 201", 10)
  val prod32: Product = Product("3-0002", "prod02", "test of product 202", 20)
  val prod33: Product = Product("3-3333", "prod03", "test of product 203", 30)
  val prod34: Product = Product("3-4321", "prod04", "test of product 2034", 40)
  val prod35: Product = Product("3--555", "prod05", "test of product 2053", 50)
  val prod36: Product = Product("3-0606", "prod06", "test of product 2063", 60)
  val prod37: Product = Product("3-Jackpot777", "prod07", "test of product 273", 77.77)
  val prod38: Product = Product("3-A08B", "prod08", "test of product 283", 88)
  val prod39: Product = Product("3-D37-[09]", "prod09", "test of product3", 99.9)
  val prod30: Product = Product("3-110", "prod10", "test of product 03", 100000)

  test("Add One Product : p0") {
    val localNode = LocalNode("nodeProduct", "LocalNode/ProductNodeTest")
    val clientTCPProduct: TcpClient = localNode.elastic4sclient()
    val products: Products = new Products(clientTCPProduct)

    products.releaseData()
    products.createTable()

    products.addProducts(Seq(p0)).await

    products.refresh().await

    assert(products.listingOfProducts.await(Duration("60s")) === List(p0))

    clientTCPProduct.close()
    localNode.stop(true)
  }

  test("Add Ten Product : (p0,01,02,03,04,05,06,07,08,09)") {
    val localNode = LocalNode("nodeProduct", "LocalNode/ProductNodeTest")
    val clientTCPProduct: TcpClient = localNode.elastic4sclient()
    val products: Products = new Products(clientTCPProduct)

    products.releaseData()
    products.createTable()

    products.addProducts(Seq(p0)).await
    products.addProducts(Seq(prod01, prod02, prod03, prod04, prod05, prod06, prod07, prod08, prod09)).await

    products.refresh().await

    assert(products.listingOfProducts.await(Duration("60s")).sortWith((a,b) => a.ean < b.ean) === List(p0, prod01, prod02, prod03, prod04, prod05, prod06, prod07, prod08, prod09).sortWith((a,b) => a.ean < b.ean))

    clientTCPProduct.close()
    localNode.stop(true)
  }

  test("Average price of (prod01,02,03) is 20 ") {
    val localNode = LocalNode("nodeProduct", "LocalNode/ProductNodeTest")
    val clientTCPProduct: TcpClient = localNode.elastic4sclient()
    val products: Products = new Products(clientTCPProduct)

    products.releaseData()
    products.createTable()

    products.addProducts(Seq(prod01, prod02, prod03)).await

    products.refresh().await
    assert(products.averagePrice.await(Duration("60s")) === 20)

    clientTCPProduct.close()
    localNode.stop(true)
  }

  test("Verify top of description's occurences") {
    val localNode = LocalNode("nodeProduct", "LocalNode/ProductNodeTest")
    val clientTCPProduct: TcpClient = localNode.elastic4sclient()
    val products: Products = new Products(clientTCPProduct)

    products.releaseData()
    products.createTable()

    products.addProducts(
      Seq(prod01, prod02, prod03, prod04, prod07, prod08, prod09, prod10, prod21, prod22, prod23, prod24, prod27, prod28, prod29, prod20, prod31, prod32, prod33, prod34, prod37, prod38, prod39, prod30)
    ).await(Duration("120s"))

    products.refresh().await
    assert {
      lazy val res = products
        .topAndLessTenDescriptionOccurrence
        .await(Duration("120s"))
        .toList

      res.slice(0,2) === List(("test of product 0", "5"),("test of product", "3")) && res.size === 20
    }

    clientTCPProduct.close()
    localNode.stop(true)
  }
/*}
  }
  "Products Listing Sort" should {
    "verify top of description's occurences version 2" in {
      resetProductDataBase()
      queryAddTenthFirst()
      products.addProduct(prod11SameEanLike10)
      products.topAndLessTenDescriptionOccurrence.map {
        case x :: y :: z => {
          Set(x, y) === Set(("test of product 0", "3"), ("test of product", "3"))
          z.toSet === Set(("test of product 04", "1"), ("test of product 05", "1"), ("test of product 06", "1"), ("test of product 11", "1"))
        }
      }
    }
    "verify sort by ean ascendant" in {
      resetProductDataBase()
      queryAddTenthFirst()
      products.listingBy("ean-asc").map {
        _ === List(prod05, prod01, prod02, prod06, prod10, prod03, prod04, prod08, prod09, prod07)
      }
    }
    "verify sort by ean descendant" in {
      resetProductDataBase()
      queryAddTenthFirst()
      products.listingBy("ean-desc").map {
        _ === List(prod05, prod01, prod02, prod06, prod10, prod03, prod04, prod08, prod09, prod07).reverse
      }
    }
    "verify sort by name ascendant" in {
      resetProductDataBase()
      queryAddTenthFirst()
      products.listingBy("ean-asc").map {
        _ === List(prod01, prod02, prod03, prod04, prod05, prod06, prod07, prod08, prod09, prod10)
      }
    }
    "verify sort by name descendant" in {
      resetProductDataBase()
      queryAddTenthFirst()
      products.listingBy("ean-desc").map {
        _ === List(prod01, prod02, prod03, prod04, prod05, prod06, prod07, prod08, prod09, prod10).reverse
      }
    }
    "verify sort by price ascendant" in {
      resetProductDataBase()
      queryAddTenthFirst()
      products.listingBy("price-asc").map {
        _ === List(prod01, prod02, prod03, prod04, prod05, prod06, prod07, prod08, prod09, prod10)
      }
    }
    "verify sort by price descendant" in {
      resetProductDataBase()
      queryAddTenthFirst()
      products.listingBy("price-desc").map {
        _ === List(prod01, prod02, prod03, prod04, prod05, prod06, prod07, prod08, prod09, prod10).reverse
      }
    }*/
}
