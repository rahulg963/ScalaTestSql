package MongoDb

import akka.event.Logging
import com.typesafe.config.ConfigFactory
import reactivemongo.api.commands.WriteResult
import reactivemongo.api.commands.bson.BSONCountCommandImplicits._
import reactivemongo.bson.{BSONDocument, BSONObjectID}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by rahul on 6/9/16.
  * */

class MongoModels extends{
  val collectionObj = MongoConnection.connect("testdb", "movies")

  def updateRatingDB(movieId : String, rateId : String) : Future[WriteResult] = {

    val selector = BSONDocument(
      "movieId" -> movieId
    )
    val updatorDoc = BSONDocument(
      "$set" -> BSONDocument(
        "rating" -> rateId
      )
    )

    println(" \n "+BSONDocument.pretty(selector))
    collectionObj.update(selector,updatorDoc,upsert = false)
  }

//  def topRatedDB(genre : String) : Future[WriteResult] = {
//
//    val selector = BSONDocument(
//      "genre" -> genre
//    )
//
//    println(" \n "+BSONDocument.pretty(selector))
//    collectionObj.find(selector)
//  }

}
