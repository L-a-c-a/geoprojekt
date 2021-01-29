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
        //* */ .also{println(it.getSQL()); println(it.getBindValues())}
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
        //* */ .also{println(it.getSQL()); println(it.getBindValues())}
      }catch(e:Throwable)  { e.printStackTrace(); e.message?.let{errorMessages.add(it)}; null }
    val poly =
      try 
      { qry?.fetchAnyInto(PGpolygon::class.java) 
      }catch(e:Throwable) { e.printStackTrace(); e.message?.let{errorMessages.add(it)}; PGpolygon() }

    GeoJsonPolygon("polygon", poly?.points?.map{arrayOf(it.x, it.y)}?.toTypedArray(), errorMessages.takeUnless{it.isEmpty()}?.let{it.toTypedArray()})  // to conform GeoJSON
  }()

  fun putPGpoint(obj:GeoJsonPoint):Array<Int>?
  {
    if (obj.coordinates==null) return null

    val qryStr = "insert into geo_points (coord) values(point(${obj.coordinates!![0]}, ${obj.coordinates!![1]}))"
    val qry:Query = DB.kontxt.query(qryStr)
    val cnt = try { qry.execute() }catch(e:Throwable)  { e.printStackTrace(); return null }

    val idQry:ResultQuery<Record>? = try { DB.kontxt.resultQuery("select currval('geo_points_id_seq')") }catch(e:Throwable) { e.printStackTrace(); return null }
    val newID = try { idQry?.fetchAnyInto(Int::class.java) }catch(e:Throwable) { e.printStackTrace(); return null }
    return arrayOf(cnt, newID?:-1)
  }

  fun putPGpolygon(obj:GeoJsonPolygon):Array<Int>?
  {
    if (obj.coordinates==null) return null

    val coordPGInputFormat:String = obj.coordinates!!.flatten().joinToString(prefix="'", postfix="'")

    val qryStr = "insert into geo_polygons (coord) values(polygon($coordPGInputFormat))"
    //* */ println(qryStr)
    val qry:Query = DB.kontxt.query(qryStr)
    val cnt = try { qry.execute() }catch(e:Throwable)  { e.printStackTrace(); return null }

    val idQry:ResultQuery<Record>? = try { DB.kontxt.resultQuery("select currval('geo_polygons_id_seq')") }catch(e:Throwable) { e.printStackTrace(); return null }
    val newID = try { idQry?.fetchAnyInto(Int::class.java) }catch(e:Throwable) { e.printStackTrace(); return null }
    return arrayOf(cnt, newID?:-1)
  }

  fun updPGpoint(id:Int, obj:GeoJsonPoint):Array<Int>?
  {
    if (obj.coordinates==null) return null

    val qryStr = "update geo_points set coord=point(${obj.coordinates!![0]}, ${obj.coordinates!![1]}) where id=?"
    //* */ println(qryStr)
    val qry:Query = DB.kontxt.query(qryStr, id)
    val cnt = try { qry.execute() }catch(e:Throwable)  { e.printStackTrace(); return null }

    return arrayOf(cnt, id)
  }

  fun updPGpolygon(id:Int, obj:GeoJsonPolygon):Array<Int>?
  {
    if (obj.coordinates==null) return null

    val coordPGInputFormat:String = obj.coordinates!!.flatten().joinToString(prefix="'", postfix="'")

    val qryStr = "update geo_polygons set coord=polygon($coordPGInputFormat) where id=?"
    //* */ println(qryStr)
    val qry:Query = DB.kontxt.query(qryStr, id)
    val cnt = try { qry.execute() }catch(e:Throwable)  { e.printStackTrace(); return null }

    return arrayOf(cnt, id)
  }

  fun delPGpoint(id:Int):Array<Int>?
  {
    val qryStr = "delete from geo_points where id=?"
    //* */ println(qryStr)
    val qry:Query = DB.kontxt.query(qryStr, id)
    val cnt = try { qry.execute() }catch(e:Throwable)  { e.printStackTrace(); return null }
    return arrayOf(cnt, id)
  }

  fun delPGpolygon(id:Int):Array<Int>?
  {
    val qryStr = "delete from geo_polygons where id=?"
    //* */ println(qryStr)
    val qry:Query = DB.kontxt.query(qryStr, id)
    val cnt = try { qry.execute() }catch(e:Throwable)  { e.printStackTrace(); return null }
    return arrayOf(cnt, id)
  }

  fun contains(id:Int, obj:GeoJsonPoint):Boolean?
  {
    if (obj.coordinates==null) return null

    val qry:ResultQuery<Record>? =
      try
      {
        DB.kontxt.resultQuery("select point(${obj.coordinates!![0]}, ${obj.coordinates!![1]}) <@ coord from geo_polygons where id=? ", id)
        //* */ .also{println(it.getSQL()); println(it.getBindValues())}
      }catch(e:Throwable)  { e.printStackTrace(); return null }
    val doesItContainIt:Boolean? =
      try 
      { qry?.fetchAnyInto(Boolean::class.java) 
      }catch(e:Throwable) { e.printStackTrace(); return null }
    return doesItContainIt
  }

}

