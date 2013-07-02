$(document).ready(function(){

    "use strict";

    var map;
    var moscowCenter = {lat: 55.764283, lng: 37.606614};
    var ZOOM_LEVEL = 10;

    createMap();
    centerMapToLocation();



    function createMap() {
        map = new GMaps({
            div: '#map',
            lat: moscowCenter.lat,
            lng: moscowCenter.lng,
            zoom: ZOOM_LEVEL,
            streetViewControl: false,
            panControl: false,

            click: function(e) {
                createMarkerFromEvent(e);
            }
        });
    }

    function centerMapToLocation() {
        GMaps.geolocate({
            success: function(position) {
                map.setCenter(position.coords.latitude, position.coords.longitude);
            },
            error: function(error) {
                map.setCenter( moscowCenter.lat, moscowCenter.lng );
            },
            not_supported: function() {
                map.setCenter( moscowCenter.lat, moscowCenter.lng );
            },
            always: function() {}
        });
    }

    function createMarkerFromEvent(e) {
        map.addMarker({
            lat: e.latLng.lat(),
            lng: e.latLng.lng(),
            title: 'Lima',
            click: function(e) {
                alert('You clicked in this marker');
            }
        });
    }
});

