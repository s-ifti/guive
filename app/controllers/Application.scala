package controllers

import play.api._
//import play.api.mvc._
import play.libs.Akka._

import play.api.mvc.{Controller, Action, Request, AnyContent, AnyContentAsText, AnyContentAsJson}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{JsValue, Json}

import org.guive.replyable.reply._
import org.guive.neo._



import akka.actor._
import akka.actor.Status._
import akka.util.Timeout
import scala.concurrent.Future



import scala.concurrent.ExecutionContext



object Application extends Controller {
  import ExecutionContext.Implicits.global

  /* method to recieve query as GET parameter */
  def index(q:String) = Action { implicit req:Request[AnyContent]   =>
		implicit val timeout = Timeout(5000)
  		val query:String  = q
  		println("rx: " + query)
		  Async {
    		(NeoSearch.neo ? NeoSearchRequest(query) ). mapTo[String].map  { 
    				result => Ok( result ).as("application/json" )
    				}
    	} 
    				  

  	
  }  

  /* a general wrapper for neo4j queries, this can be used in place of Neo4J default host:7474 URL, though usually you want to avoid giving full access to Neo4J via Cypher */
  def neoSearch() = Action (parse.json) { implicit req   =>
		implicit val timeout = Timeout(5000)
  		val query:String  = (req.body \ "query").asOpt[String].get.toString()

		Async {
    		(NeoSearch.neo ? NeoSearchRequest(query) ). mapTo[String].map  { 
    				result =>  Ok( result ).as("application/json") 
    				}
    	} 
    				  

  	
  }  
}
