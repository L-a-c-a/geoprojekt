package geoprojekt.db

import geoprojekt.utilities.*

import org.jooq.*
import org.jooq.impl.*
import org.jooq.impl.DSL.*
import org.postgresql.geometric.*

class CRUD
{
  fun getPGpoint(id:Int):GeoJsonPoint =
  {
    var pont:PGpoint?
    val qry:ResultQuery<Record>? =
      try
      {
        DB.kontxt.resultQuery("""select geom::point from  stations s where gid=? """, id)
        /* */ .also{println(it.getSQL()); println(it.getBindValues())}
      }catch(e:Throwable)  { e.printStackTrace(); null }
    try { pont = qry?.fetchAnyInto(PGpoint::class.java) }catch(e:Throwable) { e.printStackTrace(); pont = PGpoint(0.0, 0.0) }

    GeoJsonPoint(pont?.type, arrayOf(pont?.x, pont?.y))

  }()

  fun read(id:String?, typ:String?):GeoJsonPoint =
  {
    when (typ)
    {
      else -> getPGpoint(id?.toIntOrNull()?:0)
    }
  }()


}


data class GeoJsonPoint (val type:String?, val coordinates:Array<Double?>?)