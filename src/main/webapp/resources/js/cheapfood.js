$(document).ready(function(){

    "use strict";

    var map;
    var moscowCenter = {lat: 55.764283, lng: 37.606614};
    var ZOOM_LEVEL = 10;

    var TYPE_IMAGE_WIDTH = 128;


    init();

    function init() {
        createMap();
        //centerMapToLocation();
        createMarkersForLocations();
    }

    function createMap() {
        map = new GMaps({
            div: '#map',
            lat: moscowCenter.lat,
            lng: moscowCenter.lng,
            zoom: ZOOM_LEVEL,
            streetViewControl: false,
            panControl: false,
            markerClusterer: function(map) {
                return new MarkerClusterer(map);
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

    function createMarkersForLocations() {

        $.get("api/locations", function(data) {

            $.each(data, function(n, location) {

                var pos = getLatLngFromText(location.geoLocation);

                var boxText = document.createElement("div");
                boxText.innerHTML = getMarkerContentFromLocation(location);

                var infoOptions = {
                    content: boxText,
                    boxClass: "span5 transparent infoWindow",
                    isHidden: true,
                    pane: "floatPane",
                    infoBoxClearance: new google.maps.Size(50, 50),
                    maxWidth: 300,
                    pixelOffset: new google.maps.Size(0, -150)
                };
                var infoBox = new InfoBox(infoOptions);

                var marker = map.addMarker({
                    lat: pos.lat(),
                    lng: pos.lng(),
                    title: location.title,
                    click: function() {
                        infoBox.open(map.map, marker);
                        infoBox.show();
                    }
                });

                infoBox.open(map.map, marker);
            });
        });
    }

    function getMarkerContentFromLocation(location) {

        var res = getMarkerContentElementFromLocation(location);
        return res.html();
    }

    function getMarkerContentElementFromLocation(location) {

        var res = $('<ul/>').addClass('media-list')
            .append(
                $('<li/>').addClass('media')
                    .append(
                        $('<a/>').addClass('pull-left img-with-text').attr('href', '#')
                            .append(
                                $('<img/>').addClass('media-object').attr('src', 'resources/img/basic.png')
                                    .attr('width', TYPE_IMAGE_WIDTH)
                            )
                            .append(
                                $('<p/>').attr('align', 'center').append(
                                    $('<span/>').addClass('label label-info').text(location.type)
                                )
                            )
                    )
                    .append(
                        $('<div/>').addClass('media-body')
                            .append(
                                $('<h4/>').addClass('media-heading').text(location.title)
                            )
                            .append(
                                $('<p/>').text(location.description)
                            )
                            .append(
                                $('<div/>').addClass('media')
                                    .append(
                                        $('<div/>').addClass('media-body')
                                            .append(
                                                $('<span/>').addClass('label label-info').text('Адрес:')
                                            )
                                            .append(
                                                $('<span/>').addClass('spacer5').text(location.addressDescription)
                                            )
                                    )
                                    .append(
                                        $('<div/>').addClass('media-body')
                                            .append(
                                                $('<span/>').addClass('label').text('Реальный адрес:')
                                            )
                                            .append(
                                                $('<span/>').addClass('spacer5').text(location.address)
                                            )
                                    )
                                    .append(
                                        $('<div/>').addClass('media-body')
                                            .append(
                                                $('<span/>').addClass('label').text('Дата проверки:')
                                            )
                                            .append(
                                                $('<span/>').addClass('spacer5').text(location.actualDate)
                                            )
                                    )
                            )
                    )
            )
            .append(
                $('<div/>').addClass('media-body')
                    .append(
                        $('<div/>').addClass('media')
                            .append(
                                $('<div/>').addClass('btn-toolbar')
                                    .append(
                                        $('<div/>').addClass('btn-group')
                                            .append(
                                                $('<button/>').addClass('btn btn-small').text('Подробнее')
                                            )
                                    )
                                    .append(
                                        $('<div/>').addClass('btn-group')
                                            .append(
                                                $('<button/>').addClass('btn btn-small').text('Маршрут')
                                            )
                                    )
                                    .append(
                                        $('<div/>').addClass('btn-group pull-right')
                                            .append(
                                                $('<button/>').addClass('btn btn-primary btn-small').text('Редактировать')
                                            )
                                    )
                            )
                    )
            )
            .append(
                $('<hr/>')
            )
            .append(
                $('<div/>').addClass('media-body')
                    .append(
                        $('<div/>').addClass('media')
                            .append(
                                $('<div/>').addClass('btn-toolbar')
                                    .append(
                                        $('<div/>').addClass('btn-group pull-left')
                                            .append(
                                                $('<button/>').addClass('btn btn-success btn-small').text('Подтверждаю точку')
                                            )
                                    )
                                    .append(
                                        $('<div/>').addClass('btn-group pull-right')
                                            .append(
                                                $('<button/>').addClass('btn btn-warning btn-small').text('Точки здесь больше нет')
                                            )
                                    )
                            )
                    )
            );
        return res;
    }

    ///  help functions
    function getLatLngFromText(geoLocation) {
        return new google.maps.LatLng( geoLocation.latitude, geoLocation.longitude );
    }
});

