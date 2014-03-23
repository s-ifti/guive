package org.guive.replyable

import akka.actor._
import akka.actor.Status._

import akka.util.Timeout
import scala.concurrent.Future
import scala.reflect.ClassTag

/* original pattern/classes from http://www.warski.org/blog/2013/05/typed-ask-for-akka/ */
/*
trait Replyable[T]

trait ReplySupport {
  implicit class ReplyActorRef(actorRef: ActorRef) {
    def ?[T](message: Replyable[T])
            (implicit timeout: Timeout, tag: ClassTag[T]): Future[T] = {
      akka.pattern.ask(actorRef, message).mapTo[T]
    }
  }
}
 
package object reply extends ReplySupport

trait ReplyingActor extends Actor {
  def receive = {
    case m: Replyable[_] if receiveReplyable.isDefinedAt(m) => {
      try {
        sender ! receiveReplyable(m)
      } catch {
        case e: Exception => sender ! Failure(e)
      }
    }
  }
 
  def receiveReplyable[T]: PartialFunction[Replyable[T], T]
}
*/
  

