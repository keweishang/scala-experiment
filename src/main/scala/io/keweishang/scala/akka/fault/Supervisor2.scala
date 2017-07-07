package io.keweishang.scala.akka.fault

import akka.actor.{Actor, Props}

class Supervisor2 extends Actor {
  import akka.actor.OneForOneStrategy
  import akka.actor.SupervisorStrategy._
  import scala.concurrent.duration._

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case _: ArithmeticException      => Resume
      case _: NullPointerException     => Restart
      case _: IllegalArgumentException => Stop
      case _: Exception                => Escalate
    }

  def receive = {
    case p: Props => sender() ! context.actorOf(p)
  }
  // override the default, which kills all children during restart. Now we don't kill them.
  override def preRestart(cause: Throwable, msg: Option[Any]) {}
}
