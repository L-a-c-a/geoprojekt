# Geo Project

*CRUD API for saving, retrieving, updating, and deleting geometrical shapes to/from a PostgreSQL database. Additionally querying whether a polygon contains a point.*

The shape can be a point or a polygon.

_Configuration:_ There is a config file at app/src/main/resources/konf.json (not included in the repo).
It's format is:

`{ "dbkoord": { "url":"jdbc:postgresql://pici:5433/geo", "usr":"geo", "pwd":"***" } }`
or wherever is your database.

(TODO: the server's port could also be configurable. Now it's 8000 wired in.)

_Running:_ by

`./gradlew app:run`

then in the browser:

`http://localhost:8000/`function`?type=`type`&id=`id

where function is one of `read`, `create`, `update`, `delete`, `contains`,  
type is one of `point`, `polygon`,  
id is a positive integer that identifies the shape.

The tables in the database are:
```
CREATE TABLE geo_points (
	id serial NOT NULL,
	coord point NULL
);
```
```
CREATE TABLE geo_polygons (
	id serial NOT NULL,
	coord polygon NULL
);
```
