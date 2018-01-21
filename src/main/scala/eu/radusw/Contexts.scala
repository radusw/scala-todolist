package eu.radusw

import akka.actor.ActorSystem

import scala.concurrent.ExecutionContext

object Contexts {
  implicit val akkaSystem = ActorSystem("tdas")

  implicit val blockingOpsDispatcher: ExecutionContext =
    akkaSystem.dispatchers.lookup("contexts.blocking-ops-dispatcher")

  implicit val routingInfrastructureDispatcher: ExecutionContext =
    akkaSystem.dispatcher
}
