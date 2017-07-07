//#full-example
package io.keweishang.scala.akka.router.balancingpool

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.routing.{BalancingPool, RoundRobinPool}
import com.typesafe.config.ConfigFactory

import scala.io.StdIn

//#greeter-companion
//#greeter-messages
object Greeter {
  //#greeter-messages
  def props(message: String, balancingPool: ActorRef): Props = Props(new Greeter(message, balancingPool))

  //#greeter-messages
  final case class WhoToGreet(who: String)

  case object Greet

}

//#greeter-messages
//#greeter-companion

//#greeter-actor
class Greeter(message: String, balancingPool: ActorRef) extends Actor {

  import Greeter._
  import Printer._

  var greeting = ""

  def receive = {
    case WhoToGreet(who) =>
      greeting = s"$message, $who"
    case Greet =>
      //#greeter-send-message
      balancingPool ! Greeting(greeting)
    //#greeter-send-message
  }
}

//#greeter-actor

//#printer-companion
//#printer-messages
object Printer {
  //#printer-messages
  def props: Props = Props[Printer]

  //#printer-messages
  final case class Greeting(greeting: String)

}

//#printer-messages
//#printer-companion

//#printer-actor
class Printer extends Actor with ActorLogging {

  import Printer._

  def receive = {
    case Greeting(greeting) =>
      log.info(s"Greeting received (from ${sender()}): $greeting")
  }
}

//#printer-actor

//#main-class
object Basic extends App {

  import Greeter._

  // Create the 'helloAkka' actor system
  val system: ActorSystem = ActorSystem("helloAkka")

  try {
    //#create-actors
    // Create router actor, this actor creates 8 child actors on demand
    val balancingPool = system.actorOf(BalancingPool(8).props(Props(classOf[Printer])), "my-balancing-pool-router")

    // Create the 'greeter' actor
    val howdyGreeter: ActorRef = system.actorOf(Greeter.props("Howdy", balancingPool), "howdyGreeter")
    //#create-actors

    //#main-send-messages
    1 to 10 map {
      i => {
        howdyGreeter ! WhoToGreet(s"cow$i")
        howdyGreeter ! Greet
      }
    }
    //#main-send-messages

    println(">>> Press ENTER to exit <<<")
    StdIn.readLine()
  } finally {
    system.terminate()
  }
}

//#main-class
//#full-example
