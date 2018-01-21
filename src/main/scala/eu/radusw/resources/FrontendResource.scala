package eu.radusw.resources

import java.nio.file.{Files, Path}

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

object FrontendResource {

  def route(resDir: Path): Route = {
    val extPattern = """(.*)[.](.*)""".r
    pathEndOrSingleSlash {
      val page = resDir.resolve("index.html")
      val byteArray = Files.readAllBytes(page)
      complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`text/html(UTF-8)`, byteArray)))
    } ~
      path(Segment) { resource =>
        val res = resDir.resolve(resource)
        if (res.getParent == resDir && Files.exists(res) && !Files.isDirectory(res)) {
          val ext = res.getFileName.toString match {
            case extPattern(_, extGroup) => extGroup
            case _                       => ""
          }
          val byteArray = Files.readAllBytes(res)
          val entity = HttpEntity(ContentType(MediaTypes.forExtension(ext), () => HttpCharsets.`UTF-8`), byteArray)
          complete(HttpResponse(StatusCodes.OK, entity = entity))
        } else {
          complete(HttpResponse(StatusCodes.NotFound, entity = "w00t"))
        }
      }
  }
}
