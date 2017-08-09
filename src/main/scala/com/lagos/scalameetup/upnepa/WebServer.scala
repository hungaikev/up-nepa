/**
  * Created by Babatunde Abdulquddus on 02/07/17.
  */

package com.lagos.scalameetup.upnepa

import akka.actor.{ActorSystem, Props}
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.lagos.scalameetup.upnepa.config.ApplicationConfiguration
import com.lagos.scalameetup.upnepa.routes.BaseRoutes

import com.lagos.scalameetup.upnepa.services.{GPSLocationRoute, GPSLocationService}
import akka.http.scaladsl.server._
import com.lagos.scalameetup.upnepa.utility.AppRequestHandler


object WebServer extends App with BaseRoutes with ApplicationConfiguration{
  /**
    * Set up needed to provide resources (actor system, actor materializer, and execution context for futures)
    * used internally by akka http
    */
  implicit val system = ActorSystem("upnepa-chatbot-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  /**
    * Used for logging messages to the console
    */
  val logger =  Logging(system.eventStream, "up-nepa")

  val gpsLocation = new GPSLocationService()

  val gpsRoute = new GPSLocationRoute(gpsLocation)



  val routes: Route = route ~ gpsRoute.route
  /**
    * Starts http server and binds it to a host and port
    */
  val bindingFuture = Http().bindAndHandle(routes, host, port)

  logger.info(s"Server started at http://localhost:$port... \n")

/*  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done*/
}
