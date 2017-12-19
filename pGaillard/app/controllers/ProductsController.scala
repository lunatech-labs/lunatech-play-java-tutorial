package controllers

import javax.inject.{Inject, Singleton}

import Services.ElasticTest
import kantan.csv._
import kantan.csv.ops._
import kantan.csv.generic._
import models.{Product, WordSearch}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import play.api.data.format.Formats._

import scala.util.Random

@Singleton
class ProductsController @Inject()(mcc: MessagesControllerComponents) extends MessagesAbstractController(mcc) {

case class ProductReader(name :String,description: String)

  val rng = new scala.util.Random(100)


  def list() = Action {implicit request: Request[AnyContent] =>
    val firstResults =  ElasticTest.searchProducts

    Ok(views.html.list(firstResults._1,firstResults._2,1))
  }

  def nextResults(currentPage : Int) = Action {
    val newResults = ElasticTest.searchProductsNext(currentPage*500)

    Ok(views.html.list(newResults._1,newResults._2,currentPage+1))
  }


  def loadSamples = Action {
  //  Product.flushAll

    ElasticTest.deleteProdindex

    val namelist = scala.io.Source.fromFile("public/ikea-names.csv","UTF-8").mkString.readCsv[List,ProductReader](rfc.withHeader)

    val modifiedProdList = namelist.map(x => x.get).map(x => Product(x.hashCode().toString,x.name,x.description,
      BigDecimal(rng.nextDouble() * 200).setScale(2, BigDecimal.RoundingMode.HALF_UP)))

    ElasticTest.indexProductList(modifiedProdList)

    Redirect(routes.ProductsController.list())
  }


  def searchPageBlank =
    Action{implicit request: MessagesRequest[AnyContent] =>
      Ok(views.html.search(FormController.searchForm,List(),0,""))
    }

//  def searchPage(myform: Form[models.WordSearch],results : List[Product],wordSearched : String) =
//    Action{implicit request: MessagesRequest[AnyContent] =>
//      Ok(views.html.search(myform,results,wordSearched))
//  }

  def searchByName = Action { implicit request: MessagesRequest[AnyContent] =>
    FormController.searchForm.bindFromRequest.fold(
      formWithErrors => {
        // binding failure, you retrieve the form containing errors:
        BadRequest(views.html.search(FormController.searchForm,List(),0,""))
      },
      userProvidedString => {
        /* binding success, you get the actual value. */
        /* flashing uses a short lived cookie */


        val searchTarget = userProvidedString.aWord

        val searchresult: (List[Product], Double) = ElasticTest.searchProduct(searchTarget)
        val resultList: List[Product] = searchresult._1
        val averagePrice = searchresult._2.toFloat


        Ok(views.html.search(FormController.searchForm,resultList,averagePrice,searchTarget))
      }

    )
  }

  def searchTop10 =
    Action{implicit request: MessagesRequest[AnyContent] =>
      Ok(views.html.top10(ElasticTest.agregateSearch,"top 10 keywords"))
    }



  def deleteProd(ean: String) = Action {
    ElasticTest.deleteProd(ean)
    Redirect(routes.ProductsController.list())
  }

  def editProd(ean: String) = Action {implicit request: MessagesRequest[AnyContent] =>
    val productToEdit = ElasticTest.getProd(ean)._1

    val preFilledForm = FormController.prodEditForm.fill(productToEdit)
    Ok(views.html.details(preFilledForm))
  }

  def saveEdit = Action {
    implicit request: MessagesRequest[AnyContent] =>
      FormController.prodEditForm.bindFromRequest.fold(
        formWithErrors => {
          // binding failure, you retrieve the form containing errors:
          BadRequest(views.html.search(FormController.searchForm,List(),0,""))
        },
        userProvidedProduct => {
          /* binding success, you get the actual value. */
          /* flashing uses a short lived cookie */


          ElasticTest.updateProd(userProvidedProduct.ean,userProvidedProduct)

          Redirect(routes.ProductsController.list())
        }
      )
  }

  def doubleEdit = Action {
    val ean = "1610196384"
    val product = Product(ean,"AINA","pair of curtains",69.56)
    val productv2 = Product(ean,"AINA","pair of curtains",100)
    val productv3 = Product(ean,"AINA","pair of curtains",20)
    ElasticTest.updateProd(ean,productv2)
    ElasticTest.updateProd(ean,productv3)
    Redirect(routes.ProductsController.list())
  }


}

object FormController {
  val searchForm = Form(
    mapping(
      "aWord" -> nonEmptyText
    )(WordSearch.apply)(WordSearch.unapply)
  )


  var prodEditForm = Form(mapping(
    "ean" -> nonEmptyText,
    "name" -> text,
    "description" -> text,
    "price" -> bigDecimal
  )(Product.apply)(Product.unapply))

}