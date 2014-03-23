package org.guive.neo

import akka.actor._
import akka.event.Logging

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.iteratee.{Concurrent, Iteratee}

import play.api.libs.json._


import scala.concurrent.duration._

import org.joda.time.DateTime

import org.guive.replyable._


object NeoSearch {

  val system = ActorSystem("neo-search")

  val neo = system.actorOf(Props(new NeoSearchActor(system.eventStream)).withDispatcher("my-thread-pool-dispatcher"), "NeoSearchActor")

 
}

case class NeoSearchRequest(q:String)  extends Replyable[String]
/* A simple actor to wrap and execute Neo4J queries */
 
class NeoSearchActor(eventStream: akka.event.EventStream) extends Actor with ActorLogging {
  override val log = Logging(context.system, this)
  override def preStart() { println("NeoSearchActor starting") }
  override def preRestart(reason: Throwable, message: Option[Any]) {
    log.error(reason, "Restarting due to [{}] when processing [{}]", reason.getMessage, message.getOrElse(""))
  }
  def receive = {
    case m: NeoSearchRequest  => {
      var sendTo:ActorRef = sender;
      try {
              
              NeoClient.execSearch( m.q,  { (resp: String)  => 
                sendTo ! resp
            } )
       
        
      } catch {
        case e: Exception => sender ! "ERROR" //Failure(e)
      }
    }
  }
  
}



/* Helper static class wrapping calls to finagle based async access to Neo4J */

object NeoClient {


import com.twitter.finagle.{Http, Service}
import com.twitter.util.{Await, Future}
import java.net.InetSocketAddress
import org.jboss.netty.handler.codec.http._
import org.jboss.netty.buffer.{ChannelBuffer, ChannelBuffers, ChannelBufferIndexFinder}
import org.jboss.netty.util.CharsetUtil
import org.jboss.netty.handler.codec.http.HttpHeaders.Names._




  def execSearch( q:String, actionResult: (String) => _   ) { 
  
    val client: Service[HttpRequest, HttpResponse] =
      Http.newService("localhost:7474")
  
    val request =  new DefaultHttpRequest(
      HttpVersion.HTTP_1_1, HttpMethod.POST, "/db/data/cypher")
    
    val content: String = Json.stringify( 
                                Json.toJson( Map( "query" -> q ))
                          )
    //println("sending " + content)
    val contentBuffer:ChannelBuffer  = ChannelBuffers.copiedBuffer( 
                          content , CharsetUtil.UTF_8
                        )
    request.setHeader(CONTENT_TYPE, "application/json");
    request.setHeader(ACCEPT, "application/json");

    request.setHeader(USER_AGENT, "Give");
    request.setHeader(HOST, "localhost:7474");

    request.setHeader(CONNECTION, "close");
    request.setHeader(CONTENT_LENGTH, String.valueOf(contentBuffer.readableBytes()));


    request.setContent( contentBuffer )
    val response: Future[HttpResponse] = client(request)
      println("netty Thread: " + java.lang.Thread.currentThread().getId().toString() )

    response onSuccess { 
      (resp: HttpResponse) =>  {
        if(resp.getStatus() != HttpResponseStatus.OK) {
          println("ERROR returned for neo search : " + channelBufferToString( resp.getContent() ) )
        }
        else { 
          println("netty response Thread: " + java.lang.Thread.currentThread().getId().toString() )

          actionResult(channelBufferToString( resp.getContent() ))
        }
      }
    }
    //Await.ready(response)

  }


  //helper methods for channelbuffer to string
  def channelBufferToBytes(channelBuffer: ChannelBuffer): Array[Byte] = {
    val length = channelBuffer.readableBytes()
    val bytes = new Array[Byte](length)
    channelBuffer.getBytes(channelBuffer.readerIndex(), bytes, 0, length)
    bytes
  }

  def channelBufferToString(channelBuffer: ChannelBuffer): String =
    new String(channelBufferToBytes(channelBuffer))


}




