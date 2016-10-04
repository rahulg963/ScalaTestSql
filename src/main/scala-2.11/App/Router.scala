package App
import java.sql.ResultSet

import MongoDb.MongoModels
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.model.headers.{Cookie, HttpCookie, `Set-Cookie`}
import akka.stream.{ActorMaterializer, Materializer}
import com.typesafe.config.{Config, ConfigFactory}
import akka.http.scaladsl.model.{HttpEntity, _}
import akka.http.scaladsl.model.StatusCodes._
import com.google.gson.Gson
import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.stream.{ActorMaterializer, Materializer}
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.server.{Directives, Route, RouteResult}
import akka.http.scaladsl.{ConnectionContext, Http, HttpsConnectionContext}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import scala.language.postfixOps
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

import scala.concurrent.Future
import akka.event.Logging
import org.slf4j.LoggerFactory
import SqlDB.SqlConnection

trait Service{
  implicit val system: ActorSystem
  implicit val materializer: ActorMaterializer
  val mongoModels = new MongoModels()
  val routes : Route = {
    pathPrefix("imdb"){
      pathPrefix("rate"){
        (pathEndOrSingleSlash & get & parameter('movie_id, 'rate)){ (movieId,rate) =>
          complete{
            val stmt = SqlConnection.connection.createStatement()
            var sql = "UPDATE movies SET movie_rating=" + "'" + rate + "'" + "WHERE movie_id=" + "'" + movieId + "'" + ";"
            println(" ****** " + sql)
            val resultSet = stmt.executeUpdate(sql);
            OK
          }
        }
      } ~ pathPrefix("top"){
        (pathEndOrSingleSlash & get & parameter('genre)){ genre =>
          complete{
            val stmt = SqlConnection.connection.createStatement()
            var sql = "select distinct movie_rating, movie_id from movies where movie_genre=" + "'" +genre.toString + "'" + "order by movie_rating desc limit 5;"
            println(" ****** " + sql)
            val resultSet = stmt.executeQuery(sql);
            while ( resultSet.next() ) {
              val movieID = resultSet.getString("movie_id")
              val rating = resultSet.getString("movie_rating")
              println("MovieID : " + movieID + " Rating : " + rating)
            }
            OK
          }
        }
      } ~ pathPrefix("sort"){
        (pathEndOrSingleSlash & get & parameter('order)){ order =>
          complete{
            // DESC // ASC
            val stmt = SqlConnection.connection.createStatement()
            var sql = "SELECT * from movies ORDER BY movie_genre " + order + ";"
            println(" ****** " + sql)
            val resultSet = stmt.executeQuery(sql);

            while ( resultSet.next() ) {
              val movie_id = resultSet.getString("movie_id")
              val movie_rating = resultSet.getString("movie_rating")
              val movie_genre = resultSet.getString("movie_genre")
              val movie_lang = resultSet.getString("movie_lang")
              println("Movie_id : " + movie_id + " Rating : " + movie_rating + " movie_genre : " + movie_genre + " movie_lang : " + movie_lang)
            }
            OK
          }
        }
      } ~pathPrefix("avg"){
        (pathEndOrSingleSlash & get & parameter('genre)){ genre =>
          complete{
            val stmt = SqlConnection.connection.createStatement()
            var sql = "SELECT avg(movie_rating) FROM movies WHERE movie_genre="+ "'" + genre +"'" + ";"
            println(" ****** " + sql)
            val resultSet = stmt.executeQuery(sql);
            if(resultSet.next())
              println("AVERAGE Value is : " + resultSet.getFloat(1))
            OK
          }
        }
      }
    }
  }
}

object Router extends App with Service{
  implicit val system = ActorSystem("DBActor")
  override implicit val materializer = ActorMaterializer()
  println(" ---- " + SqlConnection.connection)
  Http().bindAndHandle(routes, "0.0.0.0", 8001)
}