package io.keweishang.scala.akka.fault

import akka.actor.{ActorRef, ActorSystem, Props, Terminated}
import akka.testkit.{ImplicitSender, TestKit}
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

class FaultHandlingDocSpec(_system: ActorSystem) extends TestKit(_system)
  with ImplicitSender with FlatSpecLike with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem(
    "FaultHandlingDocSpec",
    ConfigFactory.parseString(
      """
      akka {
        loggers = ["akka.testkit.TestEventListener"]
        loglevel = "WARNING"
      }
      """)))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "A supervisor" must "apply the chosen strategy for its child" in {
    val supervisor = system.actorOf(Props[Supervisor], "supervisor")

    supervisor ! Props[Child]
    val child = expectMsgType[ActorRef] // retrieve answer from TestKit’s testActor

    child ! 42 // set state to 42
    child ! "get"
    expectMsg(42)

    child ! new ArithmeticException // crash it; the value 42 survives the fault handling directive; 'Resume' doesn't log the exception
    child ! "get"
    expectMsg(42)

    child ! new NullPointerException // crash it harder; 'Restart' logs the exception
    child ! "get"
    expectMsg(0)

    watch(child) // have testActor watch “child”
    child ! new IllegalArgumentException // break it; 'Stop' logs the exception
    expectMsgPF() { case Terminated(`child`) => () } // "child" sends a Terminated message to watcher


    // The supervisor escalates the failure. The supervisor itself is supervised by the top-level actor provided by
    // the ActorSystem, which has the default policy to restart in case of all Exception cases (with the notable
    // exceptions of ActorInitializationException and ActorKilledException). Since the default directive in case of
    // a restart is to kill all children, we expected our poor child not to survive this failure.
    supervisor ! Props[Child] // create new child
    val child2 = expectMsgType[ActorRef]
    watch(child2)
    child2 ! "get" // verify it is alive
    expectMsg(0)

    // In case this is not desired (which depends on the use case), we need to use a different supervisor which
    // overrides this behavior. With this parent, the child survives the escalated restart
    child2 ! new Exception("CRASH") // escalate failure
    expectMsgPF() {
      case t@Terminated(`child2`) if t.existenceConfirmed => ()
    }

    val supervisor2 = system.actorOf(Props[Supervisor2], "supervisor2")

    supervisor2 ! Props[Child]
    val child3 = expectMsgType[ActorRef]

    child3 ! 23
    child3 ! "get"
    expectMsg(23)

    child3 ! new Exception("CRASH")
    child3 ! "get"
    expectMsg(0)
  }

}
