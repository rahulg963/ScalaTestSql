package MongoDb

import com.typesafe.config.ConfigFactory
import reactivemongo.api.MongoDriver
import reactivemongo.api.collections.bson.BSONCollection
import scala.concurrent.ExecutionContext.Implicits.global

object MongoConnection extends{
  val host = "127.0.0.1"
  val port = "27017"
  val driver = new MongoDriver()
  val connection = driver.connection(List(host + ":" + port))
  def connect(databaseName: String, collectionName: String): BSONCollection = {
    connection(databaseName).collection(collectionName)
  }
}