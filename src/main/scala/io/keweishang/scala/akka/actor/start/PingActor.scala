package io.keweishang.scala.akka.actor.start

import akka.actor.{Actor, ActorRef, Props}

/**
  * Created by kshang on 31/07/2017.
  */
class PingActor(pongActor: ActorRef) extends Actor {
  var ponged = false;

  override def preStart(): Unit = {
    // send pongActor a message when an Actor is started
    pongActor ! "Ping"
  }

  def receive = {
    case "Pong" =>
      ponged = true
      println("Ponged")
  }
}

object PingActor {
  def props(pongActor: ActorRef): Props = Props(new PingActor(pongActor))
}