package services

import javax.inject.Inject

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.bulk.RichBulkResponse
import com.sksamuel.elastic4s.index.RichIndexResponse
import com.sksamuel.elastic4s.searches.SearchDefinition
import com.sksamuel.elastic4s.searches.aggs.TermsOrder
import com.sksamuel.elastic4s.update.RichUpdateResponse
import com.sksamuel.elastic4s.{delete => _, search => _, _}
import kantan.csv.RowDecoder
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import play.api.data.Form
import play.api.data.Forms._

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


case class Product(ean: String, name: String, description: String, price: BigDecimal){

}

class Products @Inject()(client: TcpClient) {

  implicit val productHit: HitReader[Product] = ProductHitReader
  val productForm = Form(
    play.api.data.Forms.mapping("ean" -> nonEmptyText, "name" -> text, "description" -> text, "price" -> bigDecimal(Int.MaxValue, 2))
    (Product.apply)(Product.unapply)
  )

  val bufferingSize = 2500

  implicit object ProductHitReader extends HitReader[Product] {
    override def read(hit: Hit): Either[Throwable, Product] = Right(Product(
      hit.sourceAsMap("ean").toString,
      hit.sourceAsMap("name").toString,
      hit.sourceAsMap("description").toString,
      BigDecimal(hit.sourceAsMap("price").toString)))
  }

  implicit object ProductIndexable extends Indexable[Product] {
    override def json(t: Product): String = s""" { "ean" : "${t.ean}", "name" : "${t.name}", "description" : "${t.description}", "price" : "${t.price}"} """
  }

  def createTable(): Future[CreateIndexResponse] = {
    client.execute {
      createIndex("items") mappings (
        ElasticDsl.mapping("product") as(
          keywordField("ean"),
          textField("name").fields(keywordField("raw")),
          textField("description").fields(keywordField("raw")),
          doubleField("price")
        )
        )
    }
  }
/*
  def addProduct(product: Product): Future[RichBulkResponse] = {
    client.execute {
      ElasticDsl.bulk {
        indexInto("items" / "product") fields("ean" -> product.ean, "name" -> product.name, "description" -> product.description, "price" -> product.price)
      }.refresh(RefreshPolicy.WAIT_UNTIL)
    }
  }*/

  def addProducts(products: Seq[Product]): Future[RichBulkResponse] = {
    //products.foreach(addNewOrUpdateOldProduct)
    client.execute {
      ElasticDsl.bulk {
        products.map(product => {
          indexInto("items" / "product") fields("ean" -> product.ean, "name" -> product.name, "description" -> product.description, "price" -> product.price)
        })
      }.refresh(RefreshPolicy.WAIT_UNTIL)
    }
  }

  def updateProduct(id: String, ean: String, name: String, description: String, price: BigDecimal): Future[RichUpdateResponse] = {
    client.execute(
      ElasticDsl.update(id).in("items" / "product").doc("ean" -> ean, "name" -> name, "description" -> description, "price" -> price)
    )
  }

  def addNewOrUpdateOldProduct(product: Product): Future[Any] = {
    findId(product).map { id =>
      if (id.isDefined)
        updateProduct(id.get, product.ean, product.name, product.description, product.price)
      else
        addProducts(Seq(product))
    }
  }

  def removeProduct(product: Product): Future[Any] = {
    findId(product)
      .map(id =>
        if (id.isDefined) {
          client.execute {
            delete(id.get).from("items" / "product")
          }
          refresh()
        }
      )
  }

  def releaseData(): Future[DeleteIndexResponse] = client.execute(deleteIndex("items"))

  def refresh(): Future[RefreshResponse] = client.execute(refreshIndex("items"))

  def productDecoder: RowDecoder[Product] = RowDecoder.decoder(0, 0, 1, 2)(Product.apply)

  def findByAll(term: String): Future[List[Product]] = {
    client.execute {
      search("items" / "product").query(term).limit(bufferingSize)
    }.map(_.to[Product].toList)
  }

  def findId(product: Product): Future[Option[String]] = {
    client.execute {
      search("items" / "product").matchQuery("ean", product.ean) start 0 limit 1
    }.map(elt => {
      val res = elt.hits.headOption
      if (res.isEmpty)
        Option.empty
      else
        Some(res.get.id)
    })
  }

  def listingOfProducts: Future[List[Product]] = {
    client.execute {
      search("items" / "product").matchAllQuery().limit(bufferingSize)
    }.map(elt => elt.to[Product].toList)
  }

  def listingBy(productPart: String): Future[List[Product]] = {
    sortQueryByField(productPart, search("items" / "product"))
  }

  def searchListingBy(productPart: String, term: String): Future[List[Product]] = {
    sortQueryByField(productPart, search("items" / "product").query(term))
  }

  def sortQueryByField(productPart: String, query: SearchDefinition): Future[List[Product]] ={
    client.execute {
      productPart match {
        case "ean-asc" => query.sortByFieldAsc("ean").limit(bufferingSize)
        case "ean-desc" => query.sortByFieldDesc("ean").limit(bufferingSize)
        case "name-asc" => query.sortByFieldAsc("name.raw").limit(bufferingSize)
        case "name-desc" => query.sortByFieldDesc("name.raw").limit(bufferingSize)
        case "description-asc" => query.sortByFieldAsc("description.raw").limit(bufferingSize)
        case "description-desc" => query.sortByFieldDesc("description.raw").limit(bufferingSize)
        case "price-asc" => query.sortByFieldAsc("price").limit(bufferingSize)
        case "price-desc" => query.sortByFieldDesc("price").limit(bufferingSize)
      }
    }.map(_.to[Product].toList)
  }

  def findByEan(ean: String): Future[Option[Product]] = client.execute {
    search("items" / "product").matchQuery("ean", ean)
  }.map(_.to[Product].headOption)

  def findByName(name: String): Future[List[Product]] = client.execute {
    search("items" / "product").matchQuery("name", name)
  }.map(_.to[Product].toList)

  def findByDescription(description: String): Future[List[Product]] = client.execute {
    search("items" / "product").matchQuery("description", description)
  }.map(_.to[Product].toList)

  def averagePrice: Future[Double] = {
    client.execute {
      search("items" / "product").matchAllQuery().aggs {
        avgAgg("average", "price")
      }
    }.map(_.aggregations.avgResult("average").value)
  }

  def topAndLessTenDescriptionOccurrence: Future[Seq[(String, String)]] = {
    val topTen = topTenDescriptionOccurrence
    val lessTen = lessTopTenDescriptionOccurrence
    val titleTop = ("Top Ten of Occurence's description", "Occurence : ")
    val titleLess = ("Less Top Ten of Occurence's description", "Occurence : ")

    for {
      top <- topTen
      less <- lessTen
    } yield top ++ less
  }

  def topTenDescriptionOccurrence: Future[Seq[(String, String)]] = {
    tenthFirstOccurrences(false)
  }

  def lessTopTenDescriptionOccurrence: Future[Seq[(String, String)]] = {
    tenthFirstOccurrences(true)
  }

  def tenthFirstOccurrences(order: Boolean): Future[Seq[(String, String)]] = {
    client.execute {
      search("items" / "product")
        .matchAllQuery()
        .aggs {
          termsAgg("allDescription", "description.raw") order TermsOrder("_count", asc = order)
        }
    }.map {
      _
        .aggregations
        .termsResult("allDescription")
        .getBuckets
        .asScala
        .map(elt => (elt.getKeyAsString, elt.getDocCount.toString))
    }
  }

}