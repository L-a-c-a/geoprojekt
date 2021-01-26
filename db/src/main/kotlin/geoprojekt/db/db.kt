package geoprojekt.db

import geoprojekt.utilities.*

import org.jooq.*
import org.jooq.impl.*
import org.jooq.impl.DSL.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource


object DB
{
  val hikariKonf:HikariConfig? =
    if (KONFIG.konf == null) null
    else
    {
      val konf:HikariConfig = HikariConfig()
      konf.jdbcUrl = KONFIG.konf!!.dbkoord.url  //kell a !! , mert másik modulban van a konf
      konf.username = KONFIG.konf!!.dbkoord.usr
      konf.password = KONFIG.konf!!.dbkoord.pwd
      konf.addDataSourceProperty("cachePrepStmts", "true") //https://github.com/vsch/kotlin-jdbc/blob/master/src/main/kotlin/com/vladsch/kotlin/jdbc/HikariCP.kt
      konf.addDataSourceProperty("prepStmtCacheSize", "250")
      konf.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
      konf
    }

  val hikariDS:HikariDataSource = HikariDataSource(hikariKonf)

  val kontxt:DSLContext = DSL.using(hikariDS, org.jooq.tools.jdbc.JDBCUtils.dialect(hikariDS.getConnection()))

}