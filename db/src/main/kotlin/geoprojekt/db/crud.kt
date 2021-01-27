package geoprojekt.db

import geoprojekt.utilities.*

import org.jooq.*
import org.jooq.impl.*
import org.jooq.impl.DSL.*
import org.postgresql.geometric.*

class CRUD
{
  val errorMessages:MutableList<String> = mutableListOf()

  fun getPGpoint(id:Int):GeoJsonPoint =
  {
    var pont:PGpoint?
    val qry:ResultQuery<Record>? =
      try
      {
        DB.kontxt.resultQuery("""select coord from geo_points where id=? """, id)
        /* */ .also{println(it.getSQL()); println(it.getBindValues())}
      }catch(e:Throwable)  { e.printStackTrace(); null }
    try { pont = qry?.fetchAnyInto(PGpoint::class.java) }catch(e:Throwable) { e.printStackTrace(); pont = PGpoint(0.0, 0.0) }

    GeoJsonPoint(pont?.type, arrayOf(pont?.x, pont?.y), errorMessages.takeUnless{it.isEmpty()}?.let{it.toTypedArray()})  // to conform GeoJSON

  }()

  fun getPGpolygon(id:Int):GeoJsonPolygon =
  {
    val qry:ResultQuery<Record>? =
      try
      {
        DB.kontxt.resultQuery("""select coord from geo_polygons where id=? """, id)
        /* */ .also{println(it.getSQL()); println(it.getBindValues())}
      }catch(e:Throwable)  { e.printStackTrace(); e.message?.let{errorMessages.add(it)}; null }
    val poly =
      try 
      { qry?.fetchAnyInto(PGpolygon::class.java) 
      }catch(e:Throwable) { e.printStackTrace(); e.message?.let{errorMessages.add(it)}; PGpolygon() }

    GeoJsonPolygon("polygon", poly?.points?.map{arrayOf(it.x, it.y)}?.toTypedArray(), errorMessages.takeUnless{it.isEmpty()}?.let{it.toTypedArray()})  // to conform GeoJSON
  }()

  /*
  fun putPG(obj:GeoJsonAny):Array<Int> =
  {
    when (obj)
    {
      is GeoJsonPoint -> arrayOf(1, 2)
      is GeoJsonPolygon -> arrayOf(1, 3)  //nem műx, Incompatible types:
      else -> arrayOf(0, 0)
    }
    
  }()
  */

  fun putPGpoint(obj:GeoJsonPoint):Array<Int> =
  {
    println(obj)
    arrayOf(1, 2)
  }()

  fun putPGpolygon(obj:GeoJsonPolygon):Array<Int> =
  {
    arrayOf(1, 3)
  }()

}

