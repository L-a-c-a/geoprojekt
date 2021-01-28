/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package geoprojekt.app

import geoprojekt.utilities.*
import geoprojekt.db.*

import io.ktor.server.netty.*
import io.ktor.routing.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.server.engine.*
import io.ktor.features.*
import io.ktor.gson.*

fun main() {
	embeddedServer(Netty, port = 8000) {
    install(ContentNegotiation) { gson {} }
		routing {
      get("konf") { call.respond(KONFIG.konf!!)}  //vigyázz, kiírja a jelszót!

      get("/point/{id}")
      {
        val id = call.parameters["id"]?.toIntOrNull()?:0
        call.respond(CRUD().getPGpoint(id))
      }

      get("/read")
      {
        val id = call.request.queryParameters["id"]?.toIntOrNull()?:0
        val typ = call.request.queryParameters["type"]
        if (id<1) call.respond("Invalid id, may be only positive integer")
        else
          when (typ)
          {
            "polygon" -> call.respond(CRUD().getPGpolygon(id))
            "point" -> call.respond(CRUD().getPGpoint(id))
            else -> call.respond("Invalid type: $typ")
          }
      }

      put("/create")
      {
        val typ = call.request.queryParameters["type"]
        when (typ)
        { "point" -> 
          {
            val pointObj = call.receive<GeoJsonPoint>()
            call.respond(CRUD().putPGpoint(pointObj) ?. let{"${it[0]} object(s) saved, new id = ${it[1]}"} ?: "Invalid input or internal error")
          }
          "polygon" -> 
          {
            val polygonObj = call.receive<GeoJsonPolygon>()
            call.respond(CRUD().putPGpolygon(polygonObj) ?. let{"${it[0]} object(s) saved, new id = ${it[1]}"} ?: "Invalid input or internal error")
          }
          else -> call.respond("Invalid input")
        }
      }
      // TODO: pass data to CRUD in text format (with receiveText()) and CRUD will deserialize it as needed, so the "type" parameter is not needed

    }
	}.start(wait = true)
}
