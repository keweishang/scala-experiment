package io.keweishang.scala.akka.cluster

import akka.actor.{Actor, Props, Status}

/**
  * Created by kshang on 30/06/2017.
  */

//greetor-companion
//greetor-messages
object Greetor {
  def props: Props = Props[Greetor]

  // messages
  final case class Greeting(greeting: String)
}

class Greetor extends Actor {

  import Greetor._
  override def receive = {
    case Greeting(greeting) => sender() ! s"Hello back! $greeting"
    case stranger => Status.Failure(new IllegalStateException(s"I don't know you: $stranger"))
  }
}
