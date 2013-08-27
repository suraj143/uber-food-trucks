uber-food-trucks
================
This web app implements the "Food trucks" Uber coding challenge described here:
https://github.com/uber/coding-challenge-tools

The application makes Food Truck data available on a visual map interface.  The user is free to move the map and initiate a request for data in a different area.  The map is initially place where the user’s browser geolocates him, or in San Francisco, if this information is unavailable.  Using browser geolocation was new to me.

The application contains two components: a front-end html file, and a back-end Java servlet.  Both run on a single Tomcat server.  The front-end interface utilizes the Google Maps API to display data visually.  It makes requests to the back-end servlet, which proxies them to obtain data from the SFData.org Food Trucks API.

When the front-end queries this data, it narrows it down to the visible space of the map at the time of the request.  The back-end servlet validates this data, and if it is valid, applies it in the query it uses to obtain data.  If validation of these parameters fails, it simply queries the full data set.  All queries to the API limit the data to space of APPROVED food trucks.  Note that if the map is moved to an area outside of the food truck data, then no data will be returned.

Upon receiving a json-formatted list of Food Truck data, the front-end makes it visually available on the map.  First, it clears any map markers that were present from a previous call.  It proceeds to add a marker for each food truck element to the map.  Each map marker has an infowindow associated, containing the name and address of the truck, as well as a link to its schedule.  Clicking the marker opens its infowindow and closes any previously open infowindow.
