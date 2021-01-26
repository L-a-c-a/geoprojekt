package geoprojekt.db

import geoprojekt.utilities.*

import org.jooq.*
import org.jooq.impl.*
import org.jooq.impl.DSL.*
import org.postgresql.geometric.*

class CRUD
{
  /* */ fun q0() = { "izé "}()

  fun getPgPointXXX(id:Int):GeoJsonPoint =
  {
    val qry:ResultQuery<Record>? =
    try
    {
      DB.kontxt.resultQuery("""select 'point' "type", geom::point coordinates from  stations s where gid=? """, id)
      /* */ .also{println(it.getSQL()); println(it.getBindValues())}
    }catch(e:Throwable)  { e.printStackTrace(); null }
    //* */GeoJsonPoint("point", doubleArrayOf(2.0, 3.14))
    try { qry?.fetchAnyInto(GeoJsonPoint::class.java) }catch(e:Throwable) { e.printStackTrace(); null } ?: GeoJsonPoint("error", PGpoint(0.0, 0.0))
  }()

  fun getPgPoint(id:Int):GeoJsonPoint =
  {
    var pont:PGpoint?
    val qry:ResultQuery<Record>? =
      try
      {
        DB.kontxt.resultQuery("""select geom::point from  stations s where gid=? """, id)
        /* */ .also{println(it.getSQL()); println(it.getBindValues())}
      }catch(e:Throwable)  { e.printStackTrace(); null }
    try { pont = qry?.fetchAnyInto(PGpoint::class.java) }catch(e:Throwable) { e.printStackTrace(); pont = PGpoint(0.0, 0.0) }

    GeoJsonPoint(pont?.type, pont)

  }()



}


data class GeoJsonPoint (val type:String?, val coordinates:PGpoint?)