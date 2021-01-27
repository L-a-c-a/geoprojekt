package geoprojekt.utilities


data class GeoJsonPoint (val type:String?, val coordinates:Array<Double?>?, val error:Array<String>?)
data class GeoJsonPolygon (val type:String?, val coordinates:Array<Array<Double>>?, val error:Array<String>?)

data class GeoJsonAny  (val type:String?, val coordinates:Any)

//az App.kt-ba is kell