import akka.actor.PoisonPill

import play.api.GlobalSettings


import org.guive.neo._

object Global extends GlobalSettings {

  override def onStart(application: play.api.Application) { } 
   
  override def onStop(application: play.api.Application) {
    NeoSearch.neo ! PoisonPill
  }
  
}
