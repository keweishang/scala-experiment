package io.keweishang.scala.future

import scala.concurrent.{Await, Future}

object FutureCreationExamples extends App {

  /**
    * This way of creation of future (using apply method) needs asynchronous computation, thus needs an execution context
    */
  def creatingFutureNeedExecutionContext() = {
    import scala.concurrent.ExecutionContext.Implicits.global
    import scala.concurrent.duration._

    val fut = Future(25 + 25)
    Await.result(fut, 10.second)
    println(fut.value)
  }

  /**
    * The Future companion object also includes three factory methods for creating already-completed futures:
    *
    * `successful`, `failed`, and `fromTry`. These factory methods do not require an `ExecutionContext`.
    */
  def creatingFutureNoNeedExecutionContext() = {
    val futSuccessful = Future.successful(25 + 25)
    val futFailed = Future.failed(new IllegalArgumentException("bad params"))
    println(futSuccessful.value)
    println(futFailed.value)
  }

  creatingFutureNeedExecutionContext()
  creatingFutureNoNeedExecutionContext()
}
