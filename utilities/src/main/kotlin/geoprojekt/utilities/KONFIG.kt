package geoprojekt.utilities

import kotlin.io.*
import java.io.*
import com.google.gson.*

data class DB (val url: String, val usr: String, val pwd: String)
data class Konf(val dbkoord:DB)

object KONFIG
{
  val konftxt:String? = File("src/main/resources/konf.json").readText()

  val konf:Konf? =
  {
    /* */println("most olvasom be a konfigot")
    konftxt?.let{Gson().fromJson(it, Konf::class.java)}
  }()

}

