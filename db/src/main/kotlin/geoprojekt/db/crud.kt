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
        DB.kontxt.resultQuery("""select geom::point from  stations s where gid=? """, id)  //provisory query
        /* */ .also{println(it.getSQL()); println(it.getBindValues())}
      }catch(e:Throwable)  { e.printStackTrace(); null }
    try { pont = qry?.fetchAnyInto(PGpoint::class.java) }catch(e:Throwable) { e.printStackTrace(); pont = PGpoint(0.0, 0.0) }

    GeoJsonPoint(pont?.type, arrayOf(pont?.x, pont?.y), errorMessages.takeUnless{it.isEmpty()}?.let{it.toTypedArray()})  // to conform GeoJSON

  }()
/*
  fun read(id:String?, typ:String?):GeoJsonAnyType =
  {
    when (typ)
    {
      "polygon" -> GeoJsonAnyType(null, getPGpolygon(id?.toIntOrNull()?:0))
      else -> GeoJsonAnyType(getPGpoint(id?.toIntOrNull()?:0), null)
    }
  }()
 */

  fun getPGpolygon(id:Int):GeoJsonPolygon =
  {
    //val poly = PGpolygon(arrayOf(PGpoint(1.1,2.2), PGpoint(3.3,4.4)))
    //* */ println (poly)
    //var errorMessage:String? = null

    val qry:ResultQuery<Record>? =
      try
      {
        DB.kontxt.resultQuery("""select st_geometryn(wkb_geometry, 1)::polygon from q where id=? """, id)  //provisory query
        /* */ .also{println(it.getSQL()); println(it.getBindValues())}
      }catch(e:Throwable)  { e.printStackTrace(); e.message?.let{errorMessages.add(it)}; null }
    val poly =
      try { qry?.fetchAnyInto(PGpolygon::class.java) }catch(e:Throwable) { e.printStackTrace(); e.message?.let{errorMessages.add(it)}; PGpolygon() }

    GeoJsonPolygon("polygon", poly?.points?.map{arrayOf(it.x, it.y)}?.toTypedArray(), errorMessages.takeUnless{it.isEmpty()}?.let{it.toTypedArray()})  // to conform GeoJSON
  }()


}


data class GeoJsonPoint (val type:String?, val coordinates:Array<Double?>?, val error:Array<String>?)
data class GeoJsonPolygon (val type:String?, val coordinates:Array<Array<Double>>?, val error:Array<String>?)

//data class GeoJsonAnyType(val point:GeoJsonPoint?, val polygon:GeoJsonPolygon?/*, ... */)
