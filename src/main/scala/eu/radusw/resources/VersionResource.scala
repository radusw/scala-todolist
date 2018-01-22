package eu.radusw.resources

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

object VersionResource {

  def route(): Route = pathPrefix("version") {
    pathEndOrSingleSlash {
      get {
        complete(io.circe.parser.parse(api.BuildInfo.toJson))
      }
    }
  }
}
