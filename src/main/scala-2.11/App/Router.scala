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
        (pathEndOrSingleSlash & get & parameter('movie_id,'user_id ,'rate)){ (movieId, user_id, rate) =>
          complete{
              //updated
            val stmt = SqlConnection.connection.createStatement()
            var sql = "INSERT INTO movies_rating (movie_id, user_id, movie_rating) VALUES (" + movieId + "," + "'" + user_id +"', " + rate +");"
            println(" ****** " + sql)
            val resultSet = stmt.executeUpdate(sql);
            OK
          }
        }
      } ~ pathPrefix("top"){
        (pathEndOrSingleSlash & get & parameter('genre)){ genre =>
          complete{
            //updated
            val stmt = SqlConnection.connection.createStatement()
            var sql = "Select DISTINCT t1.movie_id,t1.movie_rating, t1.movie_genre from(SELECT DISTINCT movies_rating.movie_id as movie_id, movies_rating.movie_rating as movie_rating, movie_genre FROM movies_rating LEFT JOIN movies_genre ON movies_rating.movie_id=movies_genre.movie_id WHERE movie_genre='"+ genre+"' ORDER BY movie_rating DESC)as t1 GROUP BY movie_id ORDER BY movie_rating DESC limit 5"
            println(" ****** " + sql)
            val resultSet = stmt.executeQuery(sql);
            while ( resultSet.next() ) {
              val movieID = resultSet.getString("movie_id")
              val rating = resultSet.getString("movie_rating")
              val genre = resultSet.getString("movie_genre")
              println("MovieID : " + movieID + " Rating : " + rating + " Genre : " + genre)
            }
            OK
          }
        }
      } ~ pathPrefix("sort"){
        (pathEndOrSingleSlash & get){
          complete{
            // DESC // ASC
            val stmt = SqlConnection.connection.createStatement()
            var sql = "SELECT movie_genre, avg(movies_rating.movie_rating) as movie_rating FROM movies_genre INNER JOIN movies_rating ON movies_genre.movie_id=movies_rating.movie_id GROUP BY movie_genre ORDER BY movie_rating DESC"
            println(" ****** " + sql)
            val resultSet = stmt.executeQuery(sql);

            while ( resultSet.next() ) {

              val movie_rating = resultSet.getString("movie_rating")
              val movie_genre = resultSet.getString("movie_genre")

              println(" Rating : " + movie_rating + " movie_genre : " + movie_genre)
            }
            OK
          }
        }
      } ~pathPrefix("avg"){
        (pathEndOrSingleSlash & get & parameter('movie_id)){ movie_id =>
          complete{
            //updated
            val stmt = SqlConnection.connection.createStatement()
            var sql = "SELECT avg(movie_rating), movie_id FROM movies_rating WHERE movie_id = " + movie_id + " GROUP BY movie_id;"
            println(" ****** " + sql)
            val resultSet = stmt.executeQuery(sql);
            if(resultSet.next())
              println("AVERAGE Value of " + movie_id + " : " + resultSet.getFloat(1))
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