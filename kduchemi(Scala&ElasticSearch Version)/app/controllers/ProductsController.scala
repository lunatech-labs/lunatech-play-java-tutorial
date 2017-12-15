package controllers

import javax.inject.Inject

import com.sksamuel.elastic4s.bulk.RichBulkResponse
import kantan.csv._
import kantan.csv.ops._
import play.api.mvc._
import services.{Product, Products}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ProductsController @Inject()(cc: ControllerComponents, products: Products) extends AbstractController(cc) with play.api.i18n.I18nSupport {

  val csvPath: String = utils.MyUtilsConfiguration.csvPath

  def releaseDB(): Action[AnyContent] = Action.async {
    products.releaseData()
    products.createTable().map { _ =>
      Ok(views.html.index("DB released"))
    }
  }

  def loadSamplesFromCSV(): Unit = {
    implicit val productDecoder: RowDecoder[Product] = products.productDecoder
    val sourceCSV = scala.io.Source.fromFile(csvPath)
    val readerCSV = sourceCSV.reader().asCsvReader[Product](rfc.withHeader)
    products.addProducts(readerCSV.map(elt => Product((
      elt.get.ean.hashCode + elt.get.name.hashCode
      - elt.get.description.hashCode
      * elt.get.price.hashCode()
      ).hashCode().toString, elt.get.name, elt.get.description, elt.get.price)).toSeq)
  }

  def loadSamples: Action[AnyContent] = Action {
    loadSamplesFromCSV()
    Redirect(routes.ProductsController.listing())

  }

  def listing: Action[AnyContent] = Action.async {
    products.listingOfProducts.map { result =>
      Ok(views.html.list.render(result))
    }
  }

  def listingBy(sort: String): Action[AnyContent] = Action.async {
    products.listingBy(sort).map { result =>
      Ok(views.html.list.render(result))
    }
  }

  def searchListingBy(sort: String, term: String): Action[AnyContent] = Action.async {
    products.searchListingBy(sort, term).map { result =>
      Ok(views.html.searchlist.render(result, term))
    }
  }

  def searchAll(term: String): Action[AnyContent] = Action.async {
    products.findByAll(term).map { result =>
      Ok(views.html.searchlist.render(result, term))
    }
  }

  def newProduct = Action { implicit request =>
    Ok(views.html.newproduct(products.productForm))
  }

  def saveProduct = Action { implicit request =>

    products.productForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.newproduct(formWithErrors))
      },
      productData => {
        products.addNewOrUpdateOldProduct(productData)
        Redirect(routes.ProductsController.listing())
      }
    )
  }

  def remove(ean: String): Action[AnyContent] = Action.async { implicit request =>
    products.findByEan(ean).map { product =>
      if (product.isDefined) {
        products.removeProduct(product.get)
        Redirect(routes.ProductsController.listing())
      }
      else
        NotFound("Product not exist or already deleted")
    }
  }

  def details(ean: String): Action[AnyContent] = Action.async { implicit request =>
    products.findByEan(ean).map { product =>
      Ok(views.html.edit(products.productForm.fill(product.get)))
    }
  }

  def averagePrice: Action[AnyContent] = Action.async {
    products.averagePrice.map { avg =>
      Ok("Moyenne du prix des produits est de " + avg + "Euro")
    }
  }

  def topAndLessTenDescriptionOcc: Action[AnyContent] = Action.async {
    products.topAndLessTenDescriptionOccurrence.map { topAndLess =>
      Ok(views.html.topList(topAndLess.toList))
    }
  }

}
