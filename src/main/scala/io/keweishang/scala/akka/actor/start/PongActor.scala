package io.keweishang.scala.akka.actor.start

import akka.actor.{Actor, ActorRef, Props}

/**
  * Created by kshang on 31/07/2017.
  */
class PongActor() extends Actor {

  def receive = {
    case "Ping" =>
      sender ! "Pong"
  }
}

object PongActor {
  def props(): Props = Props[PongActor]
}