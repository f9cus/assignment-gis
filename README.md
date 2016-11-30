# General course assignment

Build a map-based application, which lets the user see geo-based data on a map and filter/search through it in a meaningfull way. Specify the details and build it in your language of choice. The application should have 3 components:

1. Custom-styled background map, ideally built with [mapbox](http://mapbox.com). Hard-core mode: you can also serve the map tiles yourself using [mapnik](http://mapnik.org/) or similar tool.
2. Local server with [PostGIS](http://postgis.net/) and an API layer that exposes data in a [geojson format](http://geojson.org/).
3. The user-facing application (web, android, ios, your choice..) which calls the API and lets the user see and navigate in the map and shows the geodata. You can (and should) use existing components, such as the Mapbox SDK, or [Leaflet](http://leafletjs.com/).


# My project

##Application description 
Nature of Slovakia is a single-page application that displays geo-based data on a map, including protected areas, protected trees and species of Slovakia.

##Data sources
- [DanubeHack hackathon](http://2015.danubehack.eu/)
- [enviroportal](http://stromy.enviroportal.sk/)

##Technologies used
- PostgreSQL 9.5
- PostGIS 2.2
- ogr2ogr
- Java 8
- JavaScript & jQuery 3.1.1
- Mapbox 3.0.1