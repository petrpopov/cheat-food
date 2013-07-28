$(document).ready(function(){

    "use strict";

    var params = {
        realPath: null,
        types: []
    };
    var authorized = false;
    var COOKIE_NAME = "CHEATFOOD";
    var EFFECTS_TIME = 250;


    var map;
    var renderers = [];
    var linesArray = [];
    var moscowCenter = {lat: 55.764283, lng: 37.606614};
    var infoBox;
    var myPosition;

    var errors = {
        access_denied: "access_denied",
        unknown_location: "unknown_location",
        already_voted: "already_voted",
        already_rated: "already_rated",
        other: "other"
    };

    var markersCount = 0;
    var markers = new HashMap();
    var markersIds = new HashMap();

    var curBounds;
    var prevBounds;

    var newMarker = false;

    var GRID_SIZE = 50;
    var MAX_ZOOM = 15;
    var MAX_ZOOM_FOR_MARKER = 18;
    var MAX_POSSIBLE_ZOOM = 30;
    var ENTER_KEY = 13;
    var ZOOM_LEVEL = 10;
    var TYPE_IMAGE_WIDTH = 128;
    var DATE_FORMAT = 'yy-mm-dd';
    var DATE_FORMAT_DISPLAY = 'dd.mm.yy';
    var DATE_LANGUAGE = 'ru';


    loadParams();

    function loadParams() {

        params.realPath = $('#realPath').text().trim();

        var login = $('#loginLabel').text();

        var location = $('#locationLabel').text();
        if( location ) {
            location = JSON.parse(location);
        }

        var index = document.URL.indexOf(params.realPath);
        if( index >= 0 ) {
            var tempURL = document.URL.substring(params.realPath.length+1);
            var locationIndex = tempURL.indexOf("location");
            if( locationIndex === 0 ) {
                //location/id - in URL
                //if location == null, wrong location, wrong id etc
                //we change URL in address bar to main page URL
                if( !location ) {
                    window.history.pushState("", "", params.realPath );
                }
            }

            var loginIndex = tempURL.indexOf("login");
            if( loginIndex === 0 ) {
                if(login) {
                    window.history.pushState("", "", params.realPath );
                }
            }
        }


        //strange bug in URL after facebook authorization
        index = document.URL.indexOf('#_=_');
        if( index >= 0 ) {
            var url = document.URL.substring(0, index);
            window.history.pushState("", "", url);
        }

        $.get(params.realPath+'/api/types', function(data) {
            params.types = data;
            init(location);
        });
    }


    function init(location) {
        checkCookies(location);
    }

    function checkCookies(location) {
        var cookie = $.cookie(COOKIE_NAME);

        if( !cookie ) {
            //non authorized
            sessionStorage.setItem('showHello', false);
            modifyInterface(false, location);
        }
        else {
            $.ajax({
                type: "POST",
                url: params.realPath+"/api/checkcookie",
                data: JSON.stringify( {cookie:cookie} ),
                contentType: 'application/json',
                mimeType: 'application/json',
                dataType: 'json',
                success: function() {

                },
                complete: function(result) {

                    var res = result.responseJSON;
                    if( res ) {
                        if( res.error === false ) {
                            //authorized

                            if( needToShowHello() === true ) {
                                showNote("Вы вошли в систему. Привет!");
                            }

                            sessionStorage.setItem('showHello', true);
                            modifyInterface(true, location);
                        }
                        else {
                            //non authorized
                            sessionStorage.setItem('showHello', false);
                            modifyInterface(false, location);
                        }
                    }
                    else {
                        //non authorized
                        sessionStorage.setItem('showHello', false);
                        modifyInterface(false, location);
                    }
                }
            });
        }
    }

    function needToShowHello() {
        var show = sessionStorage.getItem('showHello');

        if( !show ) {
            return true;
        }

        show = JSON.parse(show);

        if( show == true ) {
            return false;
        }

        return true;
    }

    function needToShowNoLocations() {
        var show = sessionStorage.getItem('showNoLocations');

        if( !show ) {
            return true;
        }

        show = JSON.parse(show);

        if( show == true ) {
            return false;
        }

        return true;
    }

    function modifyInterface(auth, location) {

        if( auth === true ) {
            authorized = true;

            $('#editMenu').show();
            $('#addMarkerButton').show();
        }
        else {
            authorized = false;

            $('#editMenu').hide();
            $('#addMarkerButton').hide();
        }

        buildLoginMenu(authorized);
        buildInterface(auth, location);
    }

    function buildLoginMenu(auth) {

        if( auth === false ) {
            showLoginMenuInfo(false, null);
            return;
        }

        $.get(params.realPath+"/api/users/current", function(user) {

            if( !user ) {
                showLoginMenuInfo(auth, null);
            }
            else {
                var email = user.email;
                var firstName = user.firstName;
                var lastName = user.lastName;

                var name = "";
                if( firstName ) {
                    name = firstName;
                }
                if( lastName ) {
                    if( stringIsNotEmpty(name) ) {
                        name += " " + lastName;
                    }
                    else {
                        name = lastName;
                    }
                }

                if( !stringIsNotEmpty(name) ) {
                    name = email;
                }

                showLoginMenuInfo(auth, name);
            }
        });
    }

    function showLoginMenuInfo(auth, name) {
        if( auth === true ) {
            $('#loginMenuLink').text(name);

            $('#loginLink').hide();
            $('#registrationLink').hide();
            $('#profileLink').show();
            $('#logoutLink').show();
        }
        else if( auth === false ) {
            $('#loginMenuLink').text('Вход');

            $('#loginLink').show();
            $('#registrationLink').hide();
            $('#profileLink').hide();
            $('#logoutLink').hide();
        }
    }

    function buildInterface(auth, location) {
        createRegistrationAuthActions();

        if( $.fn.createMap !== undefined ) {
            $().createMap(auth, location);
        }
    }

    function createRegistrationAuthActions() {

        $('#logoutLink').click(function() {
            logout();
        });

        $('#loginLink').click(function() {
            $.noty.closeAll();
            $('#loginModal').modal('show');
        });

        $('#registrationLink').click(function() {
            clearCreateUserForm();
            $('#registrationModal').modal('show');
        });

        $('#registrationButton').click(function() {
            clearCreateUserForm();
            $('#loginModal').modal('hide');
            $('#registrationModal').modal('show');
        });

        initCreateUserFormValidation();

        $('#registrationAlert').hide();

        $('#createUserForm').ready(function() {
            $('#emailCreate').focus();
        });

        $('#createUserForm').off('submit');
        $('#createUserForm').submit(function() {
            checkCreateUserFormValidOrNot();
            return false;
        });
    }

    function checkCreateUserFormValidOrNot() {

        $('#createUserSubmit').button('loading');
        $('#registrationAlert').hide();

        if( $("#createUserForm").valid() ) {
            submitCreateUserForm();
        }
        else {
            $('#createUserSubmit').button('reset');
        }
    }

    function submitCreateUserForm() {

        var formParams = {
            email: $('#emailCreate').val().trim(),
            password: $('#passwordCreate').val().trim()
        };

        $.ajax({
            type: "POST",
            url: params.realPath + '/api/users/add',
            data: JSON.stringify(formParams),
            contentType: 'application/json',
            mimeType: 'application/json',
            dataType: 'json',
            success: function(data) {
            },
            complete: function(data) {
                var result = JSON.parse(data.responseText);

                if( result.error === false ) {
                    $('#registrationModal').modal('hide');
                }
                else {
                    $('#registrationAlert').show(EFFECTS_TIME);
                }

                checkCookies();
                $('#createUserSubmit').button('reset');
            },
            statusCode: {
                400: function(data) {

                }
            }
        });
    }

    function initCreateUserFormValidation() {
        $("#createUserForm").validate({
            rules : {
                email: {
                    required: true,
                    email: true,
                    minlength: 3,
                    maxlength: 50
                },
                password: {
                    required: true,
                    minlength: 5,
                    maxlength: 50
                }
            },
            success: function() {
            },
            highlight: function (element, errorClass, validClass) {
                $(element).closest('.control-group').addClass('error');
            },
            unhighlight: function (element, errorClass, validClass) {
                $(element).closest('.control-group').removeClass('error');
            }
        });
    }

    function logout() {

        $.ajax({
            type: "DELETE",
            url: params.realPath + "/connect/logout",
            success: function(data) {

            },
            complete: function(data) {
                $.noty.closeAll();
                showNote("Вы успешно вышли из системы");

                sessionStorage.setItem('showHello', false);
                modifyInterface(false);

                if( $.fn.disableContextMenu !== undefined ) {
                    $().disableContextMenu();
                }
            }
        });
    }

    function clearCreateUserForm() {
        $('#emailCreate').val(null);
        $('#passwordCreate').val(null);
        $('#registrationAlert').hide();
    }

    function showNote(text) {
        var n = noty({
            text: text,
            layout: 'topRight',
            theme: 'defaultTheme',
            type: 'success',
            closeWith: ['click','button'],
            timeout: 2000
        });
    }

    function showNoteTopCenter(text, type, close) {

        if( close ) {
            if( close === true ) {
                $.noty.closeAll();
            }
        }

        var n = noty({
            text: text,
            layout: 'topCenter',
            theme: 'defaultTheme',
            type: type,
            closeWith: ['click','button'],
            timeout: 3000
        });
    }

    function showNoLocationsNoteTopCenter() {

        var text = "Кажется, в этом районе никто еще не отметил ни одной локации. Вы можете быть первым!";

        if(authorized === false ) {
            text += " Только" + '<a id="noteLoginLink" href="#"> войдите</a>' + ' в систему для этого';
        }

        var n = noty({
            text: text,
            layout: 'topCenter',
            theme: 'defaultTheme',
            type: 'information',
            closeWith: ['click','button'],
            timeout: 4000,
            callback: {
                onShow: function() {
                    $('#noteLoginLink').off('click');
                    $('#noteLoginLink').click(function() {
                        $.noty.closeAll();
                        $('#loginModal').modal('show');
                    });
                }
            }
        });
    }


    $.fn.createMap = function createMap(auth, location) {
        var mcOptions = {gridSize: GRID_SIZE, maxZoom: MAX_ZOOM};
        var styles = [
            {
                featureType: "road",
                elementType: "geometry",
                stylers: [
                    { lightness: 100 },
                    { visibility: "simplified" }
                ]
            },{
                featureType: "road",
                elementType: "labels",
                stylers: [
                    { visibility: "on" }
                ]
            }
        ];

        map = new GMaps({
            div: '#map',
            lat: moscowCenter.lat,
            lng: moscowCenter.lng,
            zoom: ZOOM_LEVEL,
            maxZoom: MAX_POSSIBLE_ZOOM,
            streetViewControl: true,
            panControl: true,
            mapTypeControl: true,
            mapTypeControlOptions: {
                mapTypeIds: [google.maps.MapTypeId.HYBRID,
                    google.maps.MapTypeId.ROADMAP,
                    google.maps.MapTypeId.SATELLITE,
                    google.maps.MapTypeId.TERRAIN],
                position: google.maps.ControlPosition.BOTTOM_LEFT,
                style: google.maps.MapTypeControlStyle.DROPDOWN_MENU
            },
            zoomControl: true,
            zoomControlOptions: {
                position: google.maps.ControlPosition.TOP_LEFT
            },
            styles: styles,
            markerClusterer: function(map) {
                return new MarkerClusterer(map, [], mcOptions);
            }
        });

        var transitLayer = new google.maps.TransitLayer();
        transitLayer.setMap(map.map);

        setDefaultMapClickBehavior();

        createMapControls(auth);
        loadDataAfterMapIsLoaded(location);

        createMapBoundsChangedBehavior();
    };

    function createMapBoundsChangedBehavior() {

        google.maps.event.addListener(map.map, 'dragend', function() {
            loadAndCreateMarkersForLocationsInBounds();
        } );

        google.maps.event.addListener(map.map, 'zoom_changed', function() {
            loadAndCreateMarkersForLocationsInBounds();
        });
    }

    function loadDataAfterMapIsLoaded(location) {

        google.maps.event.addListenerOnce(map.map, 'idle', function() {
            getLocationsCountInCurrentBounds(location);

            centerMapToLocation(location);
        });

    }

    function getLocationsCountInCurrentBounds(location) {
        var bounds = map.map.getBounds();

        var ne = bounds.getNorthEast();
        var sw = bounds.getSouthWest();

        $.ajax({
            type: "GET",
            url: params.realPath+"/api/locations/count",
            data: {
                ne_latitude: ne.lat(),
                ne_longitude: ne.lng(),
                sw_latitude: sw.lat(),
                sw_longitude: sw.lng()
            },
            success: function(data) {

                if( data ) {
                    if( data.result === 0 ) {

                        if( needToShowNoLocations() === true ) {
                            showNoLocationsNoteTopCenter();
                        }
                        sessionStorage.setItem('showNoLocations', true);
                    }
                    else if( data.result > 0 ) {
                        loadAndCreateMarkersForLocationsInBounds(null, location);
                    }
                }
            }
        });
    }

    function createMapControls(auth) {
        createInfoBox(auth);
        createContextMenuForMap(auth);
        createContextMenuForMarker(auth);
        createMarkerEditFormOnMap();
        createLocateMeButton();
        createAddMarkerButton(auth);
        createRouteForm();
    }

    function createInfoBox() {
        infoBox = createInfoBoxForMarkers();
    }

    function createContextMenuForMap(auth) {

        if( auth === true ) {
            var title = $('<span/>')
                .append(
                    $('<img/>').attr('src', getImagePath('pin.png')).attr('width', 20)
                )
                .append(
                    $('<span/>').addClass("spacer3").text("Создать точку здесь")
                ).html();

            map.setContextMenu({
                control: 'map',
                options: [
                    {
                        title: title,
                        name: 'add_location',
                        action: function(e) {
                            createMarkerForContextMenu(e.latLng);
                        }
                    }
                ]
            });
        }
    }

    function createContextMenuForMarker(auth) {

        if( auth == true ) {
            var show = $('<span/>')
                .append(
                    $('<i/>').addClass("icon-list-alt")
                )
                .append(
                    $('<span/>').addClass("spacer3").text("Подробнее")
                ).html();

            var edit = $('<span/>')
                .append(
                    $('<i/>').addClass("icon-edit")
                )
                .append(
                    $('<span/>').addClass("spacer3").text("Редактировать")
                ).html();

            var delete_ = $('<span/>')
                .append(
                    $('<i/>').addClass("icon-trash")
                )
                .append(
                    $('<span/>').addClass("spacer3").text("Удалить")
                ).html();

            map.setContextMenu({
                control: 'marker',
                options: [
                    {
                        title: show,
                        name: 'show_marker',
                        action: function(e) {
                            var infoBoxObject = markers.get(e.marker);
                            initialShowInfoBoxForMarker(e.marker, infoBoxObject);
                        }
                    },
                    {
                        title: edit,
                        name: 'edit_marker',
                        action: function(e) {
                            var infoBoxObject = markers.get(e.marker);
                            infoBox.setPosition(e.latLng);
                            initAndShowEditForm(infoBoxObject);
                        }
                    },
                    {
                        title: delete_,
                        name: 'delete_marker',
                        action: function(e) {
                            var infoBoxObject = markers.get(e.marker);
                            deleteMarkerAction(infoBoxObject);
                        }
                    }
                ]
            });
        }

    }

    function createMarkerEditFormOnMap() {
        var div = $('<div/>').attr('id','editMarkerFormDiv').attr('hidden', 'true')
            .addClass('span6 transparent infoWindow').append(getMarkerEditContent());

        var mapControls = map.map.controls[google.maps.ControlPosition.TOP_RIGHT];
        map.map.controls[google.maps.ControlPosition.TOP_RIGHT].push(div.get(0));
    }

    function createLocateMeButton() {
        var div = $('<div/>').attr('id','locateMeDiv').addClass("spacer28 infoWindow").append(
            $('<a/>').attr('id', 'locateMe').append(
                $('<img/>').attr('src', getImagePath('location.png')).attr('width', '32')
            )
        );

        map.map.controls[google.maps.ControlPosition.LEFT_TOP].push( div.get(0) );

        google.maps.event.addListener(map.map, 'idle', function(event) {
            enableLocateMeButton();
        });
    }

    function createAddMarkerButton(auth) {

        if(auth === true) {
            var div = $('<div/>').addClass('addMarker')
                .append(
                    $('<a/>').attr('id','addMarkerButton').addClass("btn btn-small")
                        .append(
                            $('<img/>').attr('src', getImagePath('pin.png')).attr('width', '20')
                        )
                        .append(
                            $('<span/>').addClass("spacer3").text('Добавить точку')
                        )
                );

            map.map.controls[google.maps.ControlPosition.TOP_LEFT].push( div.get(0) );

            google.maps.event.addListener(map.map, 'idle', function(event) {
                $('#addMarkerButton').click(function(){
                    addMarkerOnMapByLeftClick();
                });
            });
        }

    }

    function createRouteForm() {
        var div = $('<div/>').attr("id", "routeFormDiv").addClass("span7 infoWindow").attr("hidden", "true")
            .append(
                $('<button/>').attr("type", "button").addClass("close").text("x").attr("id", "closeRouteForm")
            ).append(
                $('<form/>').attr("id", "routeForm").addClass("form-horizontal span7")
                    .append(
                        $('<div/>').addClass("control-group")
                            .append(
                                $('<input/>').attr("type", "text").addClass("input-xxlarge").attr("name", "fromHere")
                                    .attr("id", "fromHere").attr("placeholder", "откуда")
                            )
                    )
                    .append(
                        $('<div/>').addClass("control-group")
                            .append(
                                $('<input/>').attr("type", "text").addClass("input-xxlarge").attr("name", "toHere")
                                    .attr("id", "toHere").attr("placeholder", "куда")
                            )
                    )
                    .append(
                        $('<div/>').addClass("control-group").attr("hidden", "true")
                            .append(
                                $('<input>').attr("id", "fromHereLatLng")
                            )
                    )
                    .append(
                        $('<div/>').addClass("control-group").attr("hidden", "true")
                            .append(
                                $('<input>').attr("id", "toHereLatLng")
                            )
                    )
                    .append(
                        $('<div/>').addClass("control-group")
                            .append(
                                $('<button/>').attr("type", "submit").addClass("btn btn-primary").text("Маршрут")
                                    .attr("id", "submitRouteForm").attr('data-loading-text', 'Строим маршрут...')
                            )
                            .append(
                                $('<button/>').attr("type", "button").addClass("btn").text("Отмена").attr("id", "cancelRouteForm")
                            )
                    )
            );

        map.map.controls[google.maps.ControlPosition.TOP_LEFT].push( div.get(0) );

        google.maps.event.addListener(map.map, 'idle', function(event) {

            initRouteFormValidation();
            initRouteFormButtonsBehavior();
            initSubmitRouteFormBehavior();
        });

    }

    function initRouteFormValidation() {
        $("#routeForm").validate({
            rules : {
                fromHere: {
                    required: true
                },
                toHere: {
                    required: true
                }
            },
            success: function() {
            },
            highlight: function (element, errorClass, validClass) {
                $(element).closest('.control-group').addClass('error');
            },
            unhighlight: function (element, errorClass, validClass) {
                $(element).closest('.control-group').removeClass('error');
            }
        });
    }

    function initRouteFormButtonsBehavior() {
        $('#fromHere').off('change');
        $('#fromHere').change(function() {
            $('#fromHereLatLng').val(null);
        });

        $('#toHere').off('change');
        $('#toHere').change(function() {
            $('#toHereLatLng').val(null);
        });

        $('#closeRouteForm').off('click');
        $('#closeRouteForm').click(function() {
            $('#routeFormDiv').hide(EFFECTS_TIME);
            clearRouteForm();
            clearMapRoutes();
        });

        $('#cancelRouteForm').off('click');
        $('#cancelRouteForm').click(function() {
            $('#routeFormDiv').hide(EFFECTS_TIME);
            clearRouteForm();
            clearMapRoutes();
        });
    }

    function initSubmitRouteFormBehavior() {
        $('#routeForm').off('submit');
        $('#routeForm').submit(function(){
            submitRouteForm();
            return false;
        });
    }

    function submitRouteForm() {
        checkRouteFormValidOrNot();
    }

    function checkRouteFormValidOrNot() {
        $('#submitRouteForm').button('loading');

        if( $("#routeForm").valid() ) {
            submitRouteFormCreateRotues();
        }
        else {
            $('#submitRouteForm').button('reset');
        }
    }

    function submitRouteFormCreateRotues() {

        clearMapRoutes();

        var renderer = getRouteRenderer();
        var directionsService = new google.maps.DirectionsService();

        var from = $('#fromHereLatLng').val();
        var fromLatLng = sessionStorage.getItem('fromHerePos');
        if( fromLatLng ) {
            fromLatLng = JSON.parse(fromLatLng);
            fromLatLng = new google.maps.LatLng(fromLatLng.lat, fromLatLng.lng);
        }

        var to = $('#toHereLatLng').val();
        var toLatLng = sessionStorage.getItem('toHerePos', null);
        if( toLatLng ) {
            toLatLng = JSON.parse(toLatLng);
            toLatLng = new google.maps.LatLng(toLatLng.lat, toLatLng.lng);
        }

        if( !stringIsNotEmpty(from) ) {
            from = $('#fromHere').val();
            fromLatLng = undefined;
        }

        if( !stringIsNotEmpty(to) ) {
            to = $('#toHere').val();
            toLatLng = undefined;
        }

        var request = {
            origin:from,
            destination:to,
            travelMode: google.maps.TravelMode.DRIVING
        };

        directionsService.route(request, function(result, status) {
            if (status == "OK") {
                if( infoBox ) {
                    infoBox.hide();
                }
                renderer.setDirections(result);
                renderLinesForStartEndOfRoute(result, fromLatLng, toLatLng);
            }
            else if( status == "NOT_FOUND") {
                showNoteTopCenter("Извините, ничего не найдено по вашему запросу", "warning", true);
            }
            else if(status == "ZERO_RESULTS") {
                showNoteTopCenter("Извините, маршрут не удалось построить", "warning", true);
            }
            else {
                showNoteTopCenter("Извините, произошла ошибка", "warning", true);
            }

            $('#submitRouteForm').button('reset');
        });
    }

    function renderLinesForStartEndOfRoute(result, from, to) {

        var start_location = result.routes[0].legs[0].start_location;
        var end_location = result.routes[0].legs[0].end_location;

        if( from ) {
            if( from !== start_location ) {
                renderLine(from, start_location);
            }
        }

        if( to ) {
            if( to !== end_location ) {
                renderLine(to, end_location);
            }
        }
    }

    function getRouteLineOptions() {
        return {
            strokeColor: '#1918FA',
            strokeOpacity: 0.8,
            strokeWeight: 4
        };
    }

    function getRouteRenderer() {

        var rendererOptions = {
            suppressMarkers:true,
            polylineOptions: getRouteLineOptions(),
            draggable: false,
            suppressInfoWindows: true
        };

        var directionsDisplay = new google.maps.DirectionsRenderer(rendererOptions);
        directionsDisplay.setMap(map.map);
        renderers.push(directionsDisplay);

        return directionsDisplay;
    }

    function renderLine(start, end) {

        var line = new google.maps.Polyline({
            map: map.map,
            editable: false,
            draggable: false,
            path: [start, end],
            options: getRouteLineOptions()
        });
        linesArray.push(line);
    }

    function clearMapRoutes() {
        $.each(renderers, function(n, rend) {
            rend.setMap(null);
        });

        $.each(linesArray, function(n, line) {
            line.setMap(null);
        });
    }

    function setDefaultMapClickBehavior() {
        google.maps.event.addListener(map.map, 'click', function(event) {
            map.hideContextMenu();

            if( !$('#infoBox').is(":visible") ) {
                disableEditMarkerMenu();
                disableDeleteMarkerMenu();
            }
        });
    }

    function centerMapToLocation(location) {
        disableLocateMeButton();

        GMaps.geolocate({
            success: function(position) {
                myPosition = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);

                if( !location ) {
                    map.setCenter(myPosition.lat(), myPosition.lng());
                }

                enableLocateMeButton();
            },
            error: function(error) {

                if( !location ) {
                    map.setCenter( moscowCenter.lat, moscowCenter.lng );
                }

                $('#locateMeDiv').hide();
            },
            not_supported: function() {
                if( !location ) {
                    map.setCenter( moscowCenter.lat, moscowCenter.lng );
                }

                $('#locateMeDiv').hide();
            },
            always: function() {}
        });

    }

    function createMarkerForContextMenu(latLng) {

        if( newMarker === true ) {
            return;
        }

        newMarker = true;
        var location = createEmptyLocation();
        location.geoLocation = getGeoLocationFromLatLng(latLng);

        var infoBoxObject = {
            infoBox: infoBox,
            location: location
        };
        infoBox.setPosition(latLng);

        $('#currentActionForm').data("infoBoxObject", infoBoxObject);

        createMarkerWithInfoBoxForLocation(infoBoxObject);
        initAndShowEditForm(infoBoxObject);
    }

    function createMainMenuBehavior() {
        createCategoryMenu();
        enableAddMarkerMenu();
        disableEditMarkerMenu();
        disableDeleteMarkerMenu();
    }

    function createCategoryMenu() {

        $.each(params.types, function(n, type) {
            $('#categoryMenu').append(
                $('<li/>').append(
                    $('<a/>').attr("id", "type_"+type.id).attr("href", "#").attr("type_id", type.id)
                        .text( getTypeValueByLanguage(type, DATE_LANGUAGE) )
                        .click(function(e) {
                            categoryTypeMenuBehavior("type_"+type.id)
                        })
                )
            )
        });
    }

    function categoryTypeMenuBehavior(type_id) {

        var id = $('#'+type_id).attr("type_id");

        removeAllMarkers();
        loadAndCreateMarkersByCategory(id);
    }

    function loadAndCreateMarkersByCategory(type_id) {

        var type = getTypeById(type_id);
        var text = "Показывается категория: " + getTypeValueByLanguage(type, DATE_LANGUAGE);

        showCurrentActionForm(text, cancelLoadAndCreateMarkersByCategory);
        loadAndCreateMarkersForLocationsInBounds(type_id);
    }

    function cancelLoadAndCreateMarkersByCategory() {
        removeAllMarkers();
        loadAndCreateMarkersForLocationsInBounds();
    }

    function enableCategoryMenu() {
        $.each(params.types, function(n, type) {
            $('#type_'+type.id).closest('li').removeClass('disabled');
            $('#type_'+type.id).off('click');
            $('#type_'+type.id).click( function() {
                categoryTypeMenuBehavior(type.id);
            });
        });
    }

    function disableCategoryMenu() {
        $.each(params.types, function(n, type) {
            $('#type_'+type.id).closest('li').addClass('disabled');
            $('#type_'+type.id).off('click');
        });
    }

    function enableAddMarkerMenu() {
        $('#addMarkerMenu').closest('li').removeClass('disabled');
        $('#addMarkerMenu').off('click');
        $('#addMarkerMenu').click(function() {
            addMarkerOnMapByLeftClick();
        });

        $('#addMarkerButton').removeClass('disabled');
        $('#addMarkerButton').off('click');
        $('#addMarkerButton').click(function() {
            addMarkerOnMapByLeftClick();
        });
    }

    function disableAddMarkerMenu() {
        $('#addMarkerMenu').closest('li').addClass('disabled');
        $('#addMarkerMenu').off('click');

        $('#addMarkerButton').addClass('disabled');
        $('#addMarkerButton').off('click');
    }

    function enableEditMarkerMenu(infoBoxObject) {
        $('#editMarkerMenu').closest('li').removeClass('disabled');
        $('#editMarkerMenu').off('click');
        $('#editMarkerMenu').click(function() {
            initAndShowEditForm(infoBoxObject);
        });
    }

    function disableEditMarkerMenu() {
        $('#editMarkerMenu').closest('li').addClass('disabled');
        $('#editMarkerMenu').off('click');
    }

    function enableDeleteMarkerMenu(infoBoxObject) {
        $('#deleteMarkerMenu').closest('li').removeClass('disabled');
        $('#deleteMarkerMenu').off('click');
        $('#deleteMarkerMenu').click(function() {
            deleteMarkerAction(infoBoxObject);
        });
    }

    function disableDeleteMarkerMenu() {
        $('#deleteMarkerMenu').closest('li').addClass('disabled');
        $('#deleteMarkerMenu').off('click');
    }

    function addMarkerOnMapByLeftClick() {

        showCurrentActionForm('Добавление новой точки...', cancelNewMarkerAddition, "infoBoxObject");

        var cursorPath = getImagePath('pin.png');

        //change cursor
        map.setOptions( {
            draggableCursor : "url("+cursorPath+"), auto",
            draggingCursor : "url("+cursorPath+"), auto"
        });

        disableContextMenu();

        //create marker and edit-form on left click
        google.maps.event.addListener(map.map, 'click', function(event) {
            createMarkerForContextMenu(event.latLng);
        });

        disableCategoryMenu();
        disableAddMarkerMenu();
    }

    $.fn.disableContextMenu = function disableContextMenu() {
        //disable context menu
        google.maps.event.clearListeners(map.map, 'rightclick');
    };

    function setDefaultMouseBehavior() {

        //hide bottom form
        $('#currentActionForm').hide(EFFECTS_TIME);

        //set default cursor
        map.setOptions({
            draggableCursor: 'default',
            draggingCursor: 'pointer'
        });

        //disable create marker on left click event function
        google.maps.event.clearListeners(map.map, 'click');

        //enable context menu
        //code from gmaps.js
        google.maps.event.addListener(map.map, 'rightclick', function(e) {
            if(window.context_menu[map.el.id]['map'] != undefined) {
                map.buildContextMenu('map', e);
            }
        });
        //end of gmaps.js

        //hide context on left click (default behavior)
        setDefaultMapClickBehavior();

        enableAddMarkerMenu();
    }

    function showCurrentActionForm(text, callback, paramName) {

        $('#currentActionForm').off('submit');
        $('#currentActionForm').submit(function() {
            return false;
        });

        $('#currentActionText').text(text);
        $('#currentActionForm').show(EFFECTS_TIME);

        $('#cancelCurrentAction').off('click');
        $('#cancelCurrentAction').click(function() {
            $('#currentActionForm').hide(EFFECTS_TIME);

            if( paramName ) {
                var param = $('#currentActionForm').data(paramName);
                if( param ) {
                    callback(param);
                }
                else {
                    callback();
                }
            }
            else {
                callback();
            }
        });
    }

    function renderMarkersForLocations(data, infoBox, location) {
        $.each(data, function(n, loadedLocation) {
            renderSingleMarkerForLocation(infoBox, loadedLocation, location);
        });
    }

    function renderSingleMarkerForLocation(infoBox, locationToRender, singleLocationToZoomIn) {

        var infoBoxObject = {
            infoBox: infoBox,
            location: locationToRender
        };

        var zoomIn = false;
        if( singleLocationToZoomIn ) {
            if(locationToRender.id === singleLocationToZoomIn.id) {
                zoomIn = true;
            }
        }

        createMarkerWithInfoBoxForLocation(infoBoxObject, zoomIn);
    }

    function loadAndCreateMarkersForLocationsInBounds(type_id, location) {

        var bounds = map.map.getBounds();
        prevBounds = curBounds;
        curBounds = bounds;

        var data = {};
        if( curBounds ) {
            data.ne_latitude_cur = curBounds.getNorthEast().lat();
            data.ne_longitude_cur = curBounds.getNorthEast().lng();
            data.sw_latitude_cur = curBounds.getSouthWest().lat();
            data.sw_longitude_cur = curBounds.getSouthWest().lng();
        }
        if( prevBounds ) {
            data.ne_latitude_prev = prevBounds.getNorthEast().lat();
            data.ne_longitude_prev = prevBounds.getNorthEast().lng();
            data.sw_latitude_prev = prevBounds.getSouthWest().lat();
            data.sw_longitude_prev = prevBounds.getSouthWest().lng();
        }

        if( type_id ) {
            if( type_id !== null ) {
                data.typeId = type_id;
            }
        }

        $.ajax({
            type: "GET",
            url: params.realPath+"/api/locations/inbounds",
            data: data,
            success: function(result) {
                renderMarkersForLocations(result, infoBox, location);
                createMainMenuBehavior();
            }
        });
    }

    function enableLocateMeButton() {
        $('#locateMe').click(function(){
            centerMapToLocation();
        });
    }

    function disableLocateMeButton() {
        $('#locateMe').off('click');
        $('#locateMe').addClass('disabled');
    }

    function createInfoBoxForMarkers(position) {

        var boxText = document.createElement("div");
        boxText.id = 'infoBox';
        boxText.className = "arrow_box infoWindowInner";
        boxText.innerHTML = getMarkerContentFromLocation();

        var infoOptions = {
            content: boxText,
            boxClass: "span6 transparent infoWindow",
            disableAutoPan: false,
            isHidden: true,
            pane: "floatPane",
            infoBoxClearance: new google.maps.Size(50, 50),
            maxWidth: 800,
            pixelOffset: new google.maps.Size(10, -190),
            enableEventPropagation: false
        };
        infoBox = new InfoBox(infoOptions);

        if(position) {
            infoBox.setPosition(position);
        }

        return infoBox;
    }

    function createMarkerWithInfoBoxForLocation(infoBoxObject, zoomIn) {

        if( isMarkerAlreadyRendered(infoBoxObject.location) === true ) {
            return;
        }

        var pos = getLatLngFromGeoLocation(infoBoxObject.location.geoLocation);

        var marker = map.addMarker({
            lat: pos.lat(),
            lng: pos.lng(),
            title: location.title,
            icon: getImagePath('bread.png'),
            click: function() {
                markerClickBehavior(marker, infoBoxObject);
            }
        });

        infoBoxObject.marker = marker;
        markers.put(marker, infoBoxObject);
        markersIds.put(infoBoxObject.location.id, infoBoxObject.location);
        markersCount++;


        if( zoomIn ) {
            if( zoomIn === true ) {
                map.map.setCenter(marker.getPosition());

                markerClickBehavior(marker, infoBoxObject);

                map.setZoom(MAX_ZOOM_FOR_MARKER);
            }
        }

        return marker;
    }

    function markerClickBehavior(marker, infoBoxObject) {
        initialShowInfoBoxForMarker(marker, infoBoxObject);
        enableEditMarkerMenu(infoBoxObject);
        enableDeleteMarkerMenu(infoBoxObject);
    }

    function initialShowInfoBoxForMarker(marker, infoBoxObject) {

        if( !isEditMarkerFormActive() ) {
            var m = infoBoxObject.marker;
            if( !m ) {
                m = marker;
            }
            infoBoxObject.infoBox.open(map.map, m);
            infoBoxObject.infoBox.show();

            google.maps.event.addListener(infoBoxObject.infoBox, 'domready', function() {
                setInfoBoxContentFromLocation(infoBoxObject);
                initInfoBoxButtonsBehavior(infoBoxObject);
            });
        }
    }

    function showInfoBoxForMarker(infoBoxObject) {
        initInfoBoxButtonsBehavior(infoBoxObject);
        infoBoxObject.infoBox.show();
        setInfoBoxContentFromLocation(infoBoxObject);
    }

    function setInfoBoxContentFromLocation(infoBoxObject) {

        var location = infoBoxObject.location;
        var ratyProperties = {
            path: getImageDirPath(),
            size: 24,
            score: location.averageRate,
            click: function(score, e) {
                rateForLocation(infoBoxObject, score);
            }
        };

        if( authorized === false ) {
            $('#approveLocation').hide();
            $('#editDeleteMarkerGroup').hide();
            $('#rateButtons').hide();
        }
        else {
            if( location.alreadyVoted === false ) {
                $('#approveLocation').show();
            }
            else {
                $('#approveLocation').hide();
            }

            if( location.alreadyRated === true ) {
                ratyProperties.readOnly = true;
            }

            $('#rateButtons').raty(ratyProperties);

            $('#editDeleteMarkerGroup').show();
        }


        $('#info_title').text(location.title);
        $('#info_type').text( getTypeValueByLanguage(location.type, DATE_LANGUAGE) );
        $('#info_description').text(location.description);

        if(location.addressDescription) {
            $('#info_addressDescription').text(location.addressDescription);
            $('#info_addressd_body').show();
        }
        else {
            $('#info_addressd_body').hide();
        }

        if( location.footype === true ) {
            $('#info_footype').addClass('icon-warning-sign').removeClass('icon-ok-sign');
            $('#info_footype_text').text('Это тошняк или палатка');
        }
        else if( location.footype === false ) {
            $('#info_footype').removeClass('icon-warning-sign').addClass('icon-ok-sign');
            $('#info_footype_text').text('Это нормальное кафе');
        }

        if( location.address ) {
            var address = "";//addressToString(location.address);

            if( location.address.addressLine ) {
                address = location.address.addressLine;
            }
            else {
                address = addressToString(location.address);
            }

            if( stringIsNotEmpty(address) === true   ) {
                $('#info_address').text(address);
            }
            else {
                $('#info_address_body').hide();
            }
        }

        if(location.actualDate) {
            var parseDate = $.datepicker.parseDate( DATE_FORMAT, location.actualDate );
            var displayDate = $.datepicker.formatDate( DATE_FORMAT_DISPLAY, parseDate );
            $('#info_actualDate').text(displayDate);
        }

        if( window.pluso ) {
            if (typeof window.pluso.start === "function") {
                var url = getLocationFullURL(location);
                $('#shareLocationInSocial').attr("data-url", url);

                window.pluso.start();
            }
        }
    }

    function initInfoBoxButtonsBehavior(infoBoxObject) {
        initScrollInfoBoxBehavior();
        initToggleEditAndViewBehavior(infoBoxObject);
        initVoteButtonsBehavior(infoBoxObject);
        initRouteButtonsBehavior(infoBoxObject);
    }

    function initRouteButtonsBehavior(infoBoxObject) {
        $('#routeToHere').off('click');
        $('#routeToHere').click(function() {
            getRouteAddresses(infoBoxObject, true);
        });

        $('#routeFromHere').off('click');
        $('#routeFromHere').click(function() {
            getRouteAddresses(infoBoxObject, false);
        });
    }

    function getRouteAddresses(infoBoxObject, to) {

        var routeAddress = {};
        routeAddress.locationPosition = getLatLngFromGeoLocation(infoBoxObject.location.geoLocation);
        routeAddress.locationAddress = infoBoxObject.location.address.addressLine;

        if( myPosition ) {
            routeAddress.myPosition = myPosition;
            geoCodeLatLngToAddress(myPosition.lat(), myPosition.lng(), function(address) {
                routeAddress.myAddress = address.addressLine;
                showRouteFormAndBuildRoute(routeAddress, to);
            });
        }
        else {
            showRouteFormAndBuildRoute(routeAddress, to);
        }
    }

    function clearRouteForm() {
        $('#fromHere').val(null);
        $('#toHere').val(null);
        $('#fromHereLatLng').val(null);
        $('#toHereLatLng').val(null);

        sessionStorage.removeItem('fromHerePos');
        sessionStorage.removeItem('toHerePos');
    }

    function showRouteFormAndBuildRoute(routeAddress, to) {

        clearRouteForm();
        $('#routeFormDiv').show(EFFECTS_TIME);

        var myInput;
        var myHideInput;
        var markerInput;
        var markerHideInput;

        if( to === true ) {
            myInput = $('#fromHere');
            myHideInput = $('#fromHereLatLng');
            markerInput = $('#toHere');
            markerHideInput = $('#toHereLatLng');

            sessionStorage.setItem('toHerePos', JSON.stringify( {
                lat:  routeAddress.locationPosition.lat(),
                lng:  routeAddress.locationPosition.lng()
            }));
            if( routeAddress.myPosition ) {
                sessionStorage.setItem('fromHerePos', JSON.stringify( {
                    lat:  routeAddress.myPosition.lat(),
                    lng:  routeAddress.myPosition.lng()
                }));
            }
        }
        else if( to === false ) {
            myInput = $('#toHere');
            myHideInput = $('#toHereLatLng');
            markerInput = $('#fromHere');
            markerHideInput = $('#fromHereLatLng');

            sessionStorage.setItem('fromHerePos', JSON.stringify( {
                lat:  routeAddress.locationPosition.lat(),
                lng:  routeAddress.locationPosition.lng()
            }));
            if( routeAddress.myPosition ) {
                sessionStorage.setItem('toHerePos', JSON.stringify( {
                    lat:  routeAddress.myPosition.lat(),
                    lng:  routeAddress.myPosition.lng()
                }));
            }
        }

        markerInput.val(routeAddress.locationAddress);
        markerHideInput.val(routeAddress.locationPosition);

        if( routeAddress.myPosition ) {
            myInput.val(routeAddress.myAddress);
            myHideInput.val(routeAddress.myPosition);
        }
        else {
            myInput.val(null);
            myHideInput.val(null);
            myInput.focus();
        }

        $('#submitRouteForm').click();
    }

    function initVoteButtonsBehavior(infoBoxObject) {
        $('#approveButton').off('click');
        $('#approveButton').click(function() {
            voteForLocation(infoBoxObject, true, $('#approveButton') );
        });

        $('#notApproveButton').off('click');
        $('#notApproveButton').click(function() {
            voteForLocation(infoBoxObject, false, $('#notApproveButton') );
        });
    }

    function voteForLocation(infoBoxObject, value, button) {

        button.button('loading');

        var id = infoBoxObject.location.id;

        $.ajax({
            type: "GET",
            url: params.realPath + "/api/votes/add",
            data: {
                locationId: id,
                value: value
            },
            success: function(data) {

                if( data.error == false ) {
                    infoBoxObject.location.alreadyVoted = true;
                    showNoteTopCenter("Спасибо!", "success", true);
                    $('#approveLocation').hide();
                }
                else {
                    if( data.errorType === errors.access_denied) {
                        showNoteTopCenter("Извините, но у вас нет прав на это действие...", "warning", true);
                        $('#approveLocation').hide();
                    }
                    else if( data.errorType === errors.already_voted ) {
                        infoBoxObject.location.alreadyVoted = true;
                        showNoteTopCenter("А вы уже голосовали за эту локацию. Давайте без вбросов,ок? =)", "warning", true);
                        $('#approveLocation').hide();
                    }
                    else if( data.errorType === errors.unknown_location ) {
                        infoBoxObject.location.alreadyVoted = true;
                        showNoteTopCenter("Такой локации больше нет в базе. Обновите страничку!", "warning", true);
                        $('#approveLocation').hide();
                    }
                    else {
                        showNoteTopCenter("Какая-то странная ошибка на сервере..извините", "warning", true);
                    }
                }
            },
            complete: function() {
                button.button('reset');
            },
            statusCode: {
                400: function(data) {
                    showNoteTopCenter("Какая-то ошибка на сервере, скорее всего у вас нет прав на это действие...", "warning", true);
                    button.button('reset');
                }
            }
        });
    }

    function rateForLocation(infoBoxObject, score) {

        var id = infoBoxObject.location.id;

        $.ajax({
            type: "GET",
            url: params.realPath + "/api/rates/add",
            data: {
                locationId: id,
                value: score
            },
            success: function(data) {
                if( data.error == false ) {
                    var loc = data.result;

                    infoBoxObject.location.alreadyRated = true;
                    infoBoxObject.location.averageRate = loc.averageRate;
                    $('#rateButtons').raty('readOnly', true);
                    showNoteTopCenter("Спасибо!", "success", true);
                }
                else {
                    if( data.errorType === errors.access_denied) {
                        showNoteTopCenter("Извините, но у вас нет прав на это действие...", "warning", true);
                    }
                    else if( data.errorType === errors.already_rated ) {
                        infoBoxObject.location.alreadyRated = true;
                        $('#rateButtons').raty('readOnly', true);
                        showNoteTopCenter("А вы уже голосовали за эту локацию. Давайте без вбросов,ок? =)", "warning", true);
                    }
                    else if( data.errorType === errors.unknown_location ) {
                        infoBoxObject.location.alreadyVoted = true;
                        $('#rateButtons').raty('readOnly', true);
                        showNoteTopCenter("Такой локации больше нет в базе. Обновите страничку!", "warning", true);
                    }
                    else {
                        showNoteTopCenter("Какая-то странная ошибка на сервере..извините", "warning", true);
                    }
                }
            },
            statusCode: {
                400: function(data) {
                    showNoteTopCenter("Какая-то ошибка на сервере, скорее всего у вас нет прав на это действие...", "warning", true);
                }
            }
        });
    }

    function initScrollInfoBoxBehavior() {
        //https://code.google.com/p/google-maps-utility-library-v3/issues/detail?id=19#c8
        //this is for scrolling into the InfoBox
        $('#infoBox').on('mouseenter', function() {
            map.setOptions({draggable:false, scrollwheel:false});
        });
        $('#infoBox').on('mouseleave', function() {
            map.setOptions({draggable:true, scrollwheel:true});
        });
        //end of scrolling
    }

    function isEditMarkerFormActive() {
        if( $('#editMarkerFormDiv').is(':visible') ) {
              return true;
        }
        else {
            return false;
        }
    }

    function initToggleEditAndViewBehavior(infoBoxObject) {

        $('.dropdown-toggle').dropdown();

        $('#editMarkerButton').off('click');
        $('#editMarkerButton').click(function() {
            initAndShowEditForm(infoBoxObject);
        });

        $('#deleteMarkerButton').off('click');
        $('#deleteMarkerButton').click(function() {
            $('#deleteAlert').hide();
            $('#deleteModal').modal('show');
        });

        $('#deleteMarkerButtonModal').off('click');
        $('#deleteMarkerButtonModal').click( function() {
            deleteMarkerRequest(infoBoxObject);
        });

        $('#closeInfoBox').off('click');
        $('#closeInfoBox').click(function() {
            infoBoxObject.infoBox.hide();
            disableEditMarkerMenu();
            disableDeleteMarkerMenu();
        });
    }

    function deleteMarkerAction(infoBoxObject) {
        $('#deleteMarkerButtonModal').off('click');
        $('#deleteMarkerButtonModal').click( function() {
            deleteMarkerRequest(infoBoxObject);
        });

        $('#deleteAlert').hide();
        $('#deleteModal').modal('show');
    }

    function deleteMarkerRequest(infoBoxObject) {
        $('#deleteMarkerButtonModal').button('loading');


        $.ajax({
            type: "DELETE",
            url: params.realPath+'/api/locations/'+infoBoxObject.location.id+'/delete',
            complete: function(data) {

                if( data.responseJSON ) {
                    var res = data.responseJSON;

                    if( res.error == false ) {
                        $('#deleteModal').modal('hide');
                        removeMarkerAndInfoBox(infoBoxObject);
                    }
                    else {
                        if( res.errorType === errors.access_denied ) {
                            $('#deleteAlert').show(EFFECTS_TIME);
                            $('#deleteAlertBeginText').text("Сожалеем, но..");
                            $('#deleteAlertText').text("У вас нет прав на удаление этой локации");
                        }
                        else if( res.errorType === errors.unknown_location ) {
                            $('#deleteAlert').show(EFFECTS_TIME);
                            $('#deleteAlertText').text("Такой локации не существует!");
                        }
                        else {
                            $('#deleteAlert').show(EFFECTS_TIME);
                            $('#deleteAlertBeginText').text("Упс..");
                            $('#deleteAlertText').text("Извините, произошла какая-то ошибка..");
                        }
                    }

                    $('#deleteMarkerButtonModal').button('reset');
                }
            },
            statusCode: {
                400: function(data) {
                    $('#deleteAlert').show(EFFECTS_TIME);
                    $('#deleteAlertBeginText').text("Не получилось.")
                    $('#deleteAlertText').text("Извините, произошла какая-то ошибка. Скорее всего у вас нет прав на это действие");
                    $('#deleteMarkerButtonModal').button('reset');
                }
            }
        });
    }

    function removeAllMarkers() {
        $.each(markers._dict, function(n, marker) {
            removeMarkerAndInfoBox( {infoBox: marker.infoBox, location: marker.location, marker: marker.marker} );
        });
    }

    function removeMarkerAndInfoBox(infoBoxObject) {
        infoBoxObject.infoBox.hide();
        map.removeMarker(infoBoxObject.marker);
        markers.remove(infoBoxObject.marker);
        markersIds.remove(infoBoxObject.location.id);
        markersCount--;
    }

    function initAndShowEditForm(infoBoxObject) {
        map.map.setCenter( infoBoxObject.infoBox.getPosition() );
        infoBoxObject.infoBox.hide();

        $('#editMarkerFormDiv').fadeIn(EFFECTS_TIME, function() {
            $('#editFormAlert').hide();
            initEditFormAddressData(infoBoxObject);
            initEditFormValidation();
            initEditFormWithData(infoBoxObject);
            initEditFormFocus();
            initDatePicker();
            initSwitch();
        });


        $('#currentActionForm').data("infoBoxObject", infoBoxObject);
        showCurrentActionForm("Редактирование точки...", cancelNewMarkerAddition, "infoBoxObject");

        $('#cancelEdit').off('click');
        $('#cancelEdit').click(function() {
            cancelNewMarkerAddition(infoBoxObject);
        });

        $('#editMarkerForm').off('submit');
        $('#editMarkerForm').submit(function(){
            submitEditForm(infoBoxObject);
            return false;
        });

        $("#editMarkerForm :input").keypress(function(e) {
            if(e.which === ENTER_KEY) {
                $('#submitEdit').click();
            }
        });
    }

    function cancelNewMarkerAddition(infoBoxObject) {

        $('#editMarkerFormDiv').fadeOut(EFFECTS_TIME);

        if( infoBoxObject ) {
            infoBoxObject.infoBox.show();

            enableEditMarkerMenu(infoBoxObject);
            enableDeleteMarkerMenu(infoBoxObject);

            if( newMarker === true ) {
                removeMarkerAndInfoBox(infoBoxObject);
            }
        }
        else {
            disableEditMarkerMenu();
            disableDeleteMarkerMenu();
        }
        enableCategoryMenu();
        setDefaultMouseBehavior();

        newMarker = false;

    }

    function submitEditForm(infoBoxObject) {
        checkEditFormValidOrNot(infoBoxObject);
    }

    function checkEditFormValidOrNot(infoBoxObject) {
        $('#submitEdit').button('loading');

        if( $("#editMarkerForm").valid() ) {
            submitEditFormPost(infoBoxObject);
        }
        else {
            $('#submitEdit').button('reset');
        }
    }

    function submitEditFormPost(infoBoxObject) {

        var id = $('#location-id').val();
        var title = $('#title').val();
        var description = $('#description').val();

        var typeId = $('#type').val();
        var type = getTypeById(typeId);

        var footype = $("#editMarkerForm input[type='radio']:checked").val();
        var addressDescription = $('#addressDescription').val();
        var actualDate = $('#actualDate').datepicker('getDate');
        var latitude = $('#latitude').val();
        var longitude = $('#longitude').val();
        var country = $('#country').val();
        var region = $('#region').val();
        var city = $('#city').val();
        var street = $('#street').val();
        var house = $('#house').val();
        var zipcode = $('#zipcode').val();
        var addressLine = $('#addressLine').val();


        var param = {
            id: id,
            title: title,
            description: description,
            type: type,
            footype: footype,
            addressDescription: addressDescription,
            actualDate: actualDate,
            geoLocation: {
                lng: longitude,
                lat: latitude
            },
            address: {
                country: country,
                region: region,
                city: city,
                street: street,
                house: house,
                zipcode: zipcode,
                addressLine: addressLine
            }
        };

        param = JSON.stringify(param);

        $.ajax({
            type: "POST",
            url: params.realPath+'/api/locations/add',
            data: param = param,
            contentType: 'application/json',
            mimeType: 'application/json',
            dataType: 'json',
            success: function(data) {
            },
            complete: function(data) {

                if( data.responseJSON ) {
                    var res = data.responseJSON;

                    if( res.error === false ) {
                        var newLocation = res.result;
                        infoBoxObject.location = newLocation;

                        newMarker = false;

                        $('#editMarkerFormDiv').fadeOut(EFFECTS_TIME);
                        showInfoBoxForMarker(infoBoxObject);
                        enableEditMarkerMenu(infoBoxObject);
                        enableDeleteMarkerMenu(infoBoxObject);
                    }
                    else {
                        if( res.errorType === errors.access_denied) {
                            $('#editFormAlertBeginText').text("Ошибка!");
                            $('#editFormAlertText').text("Необходимо авторизоваться!");
                            $('#editFormAlert').show(EFFECTS_TIME);

                            $.noty.closeAll();
                            $('#loginModal').modal('show');
                        }
                        else {
                            //show error message
                            $('#editFormAlert').show(EFFECTS_TIME);
                        }
                    }
                }

                $('#submitEdit').button('reset');
                setDefaultMouseBehavior();
            },
            statusCode: {
                400: function(data) {
                    $('#editFormAlert').show(EFFECTS_TIME);
                    $('#editFormAlertBeginText').text("Не получилось.");
                    $('#editFormAlertText').text("Извините, произошла какая-то ошибка. Скорее всего у вас нет прав на это действие");
                    $('#submitEdit').button('reset');
                }
            }
        });
    }

    function initEditFormAddressData(infoBoxObject) {

        var latLng = getLatLngFromGeoLocation(infoBoxObject.location.geoLocation);

        geoCodeLatLngToAddress(latLng.lat(), latLng.lng(), fillEditFormWithAddress);
    }

    function geoCodeLatLngToAddress(latitude, longitude, callback) {
        var myGeocoder = ymaps.geocode(latitude + ", " + longitude);
        myGeocoder.then(
            function (res) {
                var geoObject = res.geoObjects.get(0);
                if (!geoObject) {
                    return;
                }

                var metaData = geoObject.properties.get("metaDataProperty").GeocoderMetaData;
                if( !metaData ) {
                    return;
                }

                var details = metaData.AddressDetails;

                var address = {};

                if( !details.hasOwnProperty("Country") ){
                    return;
                }

                if( details.Country.hasOwnProperty("AddressLine") ) {
                    address.addressLine = details.Country.AddressLine;
                }

                var country = details.Country.CountryName;
                address.country = country;

                if( details.Country.hasOwnProperty("AdministrativeArea") ) {
                    address.region = details.Country.AdministrativeArea.AdministrativeAreaName;

                    if( details.Country.AdministrativeArea.hasOwnProperty("Locality") ) {
                        address.city = details.Country.AdministrativeArea.Locality.LocalityName;

                        if( details.Country.AdministrativeArea.Locality.hasOwnProperty("Thoroughfare")) {
                            address.street = details.Country.AdministrativeArea.Locality.Thoroughfare.ThoroughfareName;

                            if( details.Country.AdministrativeArea.Locality.Thoroughfare.hasOwnProperty("Premise")) {
                                address.house = details.Country.AdministrativeArea.Locality.Thoroughfare.Premise.PremiseNumber;
                            }
                        }
                    }
                }
                else {
                    if( details.Country.hasOwnProperty("Locality") ) {
                        address.region = details.Country.Locality.LocalityName;
                        address.city = details.Country.Locality.LocalityName;

                        if( details.Country.Locality.hasOwnProperty("Thoroughfare") ) {
                            address.street = details.Country.Locality.Thoroughfare.ThoroughfareName;

                            if( details.Country.Locality.Thoroughfare.hasOwnProperty("Premise")) {
                                address.house = details.Country.Locality.Thoroughfare.Premise.PremiseNumber;
                            }
                        }
                    }
                }

                callback(address);
            },
            function (err) {
                // обработка ошибки
            }
        );
    }

    function fillEditFormWithAddress(address) {
        if( !address ) {
            return;
        }

        if( address.hasOwnProperty("country")) {
            $('#country').val(address.country);
        }

        if( address.hasOwnProperty("region")) {
            $('#region').val(address.region);
        }

        if( address.hasOwnProperty("city")) {
            $('#city').val(address.city);
        }

        if( address.hasOwnProperty("street")) {
            $('#street').val(address.street);
        }

        if( address.hasOwnProperty("house")) {
            $('#house').val(address.house);
        }

        if( address.hasOwnProperty("zipcode")) {
            $('#zipcode').val(address.zipcode);
        }

        if( address.hasOwnProperty("addressLine")) {
            $('#addressLine').val(address.addressLine);
        }
    }

    function initEditFormValidation() {
        $("#editMarkerForm").validate({
            rules : {
                title: {
                    required: true,
                    minlength: 2,
                    maxlength: 50
                },
                description: {
                    required: true,
                    minlength: 4,
                    maxlength: 250
                },
                type: {
                    required: true
                },
                addressDescription: {
                    minlength: 3,
                    maxlength: 250
                },
                actualDate: {
                    required: true
                },
                latitude: {
                    required: true
                },
                longitude: {
                    required: true
                }
            },
            success: function() {
            },
            highlight: function (element, errorClass, validClass) {
                $(element).closest('.control-group').addClass('error');
            },
            unhighlight: function (element, errorClass, validClass) {
                $(element).closest('.control-group').removeClass('error');
            }
        });
    }

    function initEditFormWithData(infoBoxObject) {

        clearEditForm();

        var location = infoBoxObject.location;

        //main fields
        $('#location-id').val( location.id );
        $('#title').val( location.title );
        $('#description').val( location.description );
        $('#addressDescription').val( location.addressDescription );

        initDatePicker(location.actualDate);

        //types
        $("#type").val(location.type.id);
        if( location.footype === true ) {
            $('input:radio[name="footypeRadio"]').filter('[value="true"]').attr('checked', true);
        }
        else {
            $('input:radio[name="footypeRadio"]').filter('[value="false"]').attr('checked', true);
        }

        //hidden fields
        $('#latitude').val( location.geoLocation.lat );
        $('#longitude').val( location.geoLocation.lng );

        //unnecessary fields
        if( location.address ) {
            $('#country').val( location.address.country );
            $('#region').val( location.address.region );
            $('#city').val( location.address.city );
            $('#street').val( location.address.street );
            $('#house').val( location.address.house );
            $('#zipcode').val( location.address.zipcode );
            $('#addressLine').val( location.address.addressLine );
        }

        $('#title').focus();
    }

    function clearEditForm() {
        $('#location-id').val(null);
        $('#title').val( null );
        $('#description').val( null );
        $('#addressDescription').val(null);
        $('#actualDate').val(null);
        $('#latitude').val( null );
        $('#longitude').val( null );
        $('#country').val( null );
        $('#region').val( null );
        $('#city').val( null );
        $('#street').val( null );
        $('#house').val( null );
        $('#zipcode').val( null );
        $('#addressLine').val( null );
    }

    function initEditFormFocus() {
        $('#title').focus();
    }

    function initDatePicker(date) {

        $.datepicker.setDefaults(
            $.datepicker.regional[DATE_LANGUAGE]
        );
        $('#actualDate').datepicker({ dateFormat: DATE_FORMAT_DISPLAY });

        if( date ) {
            var parseDate = $.datepicker.parseDate( DATE_FORMAT, date );
            $('#actualDate').datepicker('setDate', parseDate );
        }
    }

    function initSwitch() {

        $('#createAddressSwitch').off('click');
        $('#createAddressSwitch').click(function(){
            if($(this).parent().find('input').is(':checked')) {
                $('#realAddress').show(EFFECTS_TIME);
            }
            else {
                $('#realAddress').hide(EFFECTS_TIME);
            }
        });
    }

    function getMarkerContentFromLocation() {

        var res = getMarkerContentElementFromLocation();

        return res.html();
    }

    function getMarkerContentElementFromLocation() {

        var pluso = $('<div/>').addClass("pluso").attr("id", "shareLocationInSocial")
            .attr("data-options", "small,square,line,horizontal,counter,theme=03")
            .attr("data-services", "facebook,twitter,vkontakte,google,odnoklassniki,moimir,print")
            .attr("data-background", "transparent")
            .attr("data-url", "")
            .attr("data-image", getMainlogoURL() )
            .attr("data-title", "Классное дешевое местечко!")
            .attr("data-description", "Классное дешевое местечко!")
            .attr("data-user", "1262715342");

        var raty = $('<span/>').attr("id", "rateButtons");

        var exRes = $('<div/>');

        var res = $('<ul/>').attr('id', 'infoContent').addClass('media-list')
            .append(
                $('<button/>').attr('id','closeInfoBox').attr('type', 'button').addClass('close').text("x")
            )
            .append(
                $('<li/>').addClass('media')
                    .append(
                        $('<a/>').addClass('pull-left img-with-text').attr('href', '#')
                            .append(
                                $('<img/>').addClass('media-object').attr('src', getImagePath('basic.png'))
                                    .attr('width', TYPE_IMAGE_WIDTH)
                            )
                            .append(
                                $('<p/>').attr('align', 'center').append(
                                    $('<span/>').attr('id', 'info_type').addClass('label label-info')
                                )
                            )
                    )
                    .append(
                        $('<div/>').addClass('media-body')
                            .append(
                                $('<h4/>').attr('id', 'info_title').addClass('media-heading')
                            )
                            .append(
                                $('<p/>').attr('id', 'info_description')
                            )
                            .append(
                                $('<div/>').addClass('media')
                                    .append(
                                        $('<div/>').addClass('media-body')
                                            .append(
                                                $('<i/>').attr('id','info_footype')
                                            )
                                            .append(
                                                $('<span/>').attr('id','info_footype_text')
                                                    .addClass('spacer5')
                                            )
                                    )
                                    .append(
                                        $('<div/>').addClass('media-body').attr('id','info_addressd_body')
                                            .append(
                                                $('<span/>').addClass('label label-info').text('Адрес')
                                            )
                                            .append(
                                                $('<span/>').attr('id','info_addressDescription')
                                                    .addClass('spacer5')
                                            )
                                    )
                                    .append(
                                        $('<div/>').addClass('media-body').attr("id", "info_address_body")
                                            .append(
                                                $('<span/>').addClass('label').text('Реальный адрес')
                                            )
                                            .append(
                                                $('<span/>').attr('id', 'info_address').addClass('spacer5')
                                            )
                                    )
                                    .append(
                                        $('<div/>').addClass('media-body')
                                            .append(
                                                $('<span/>').addClass('label').text('Дата проверки')
                                            )
                                            .append(
                                                $('<span/>').attr('id', 'info_actualDate').addClass('spacer5')
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
                                                $('<button/>').addClass('btn btn-small').text('Маршрут сюда')
                                                    .attr("id", "routeToHere")
                                            )
                                            .append(
                                                $('<button/>').addClass('btn btn-small').text('Маршрут отсюда')
                                                    .attr("id", "routeFromHere")
                                            )

                                    )
                                    .append(
                                        $('<div/>').addClass('btn-group pull-right').attr('id', "editDeleteMarkerGroup")
                                            .append(
                                                $('<button/>').attr('id', 'editMarkerButton')
                                                    .addClass('btn btn-small')
                                                    .append(
                                                        $('<i/>').addClass('icon-edit')
                                                    )
                                                    .append(
                                                        $('<span/>').addClass("spacer3").text('Редактировать')
                                                    )
                                            )
                                            .append(
                                                $('<button/>').attr('id', 'deleteMarkerButton')
                                                    .addClass('btn btn-small')
                                                    .append(
                                                        $('<i/>').addClass('icon-trash')
                                                    )
                                                    .append(
                                                        $('<span/>').addClass("spacer3").text('Удалить')
                                                    )
                                            )
                                    )
                            )
                    )
            )
            .append(
                $('<div/>').attr("id", "voteRateDiv")
                    .append(
                        $('<hr/>')
                    )
                    .append (
                        $('<div/>')
                            .append(
                                $('<label/>').text("Оцените это место")
                            )
                            .append(
                                raty.addClass("pull-left")
                            )
                            .append(
                                pluso
                            )
                    )
            )
            .append(
                $('<div/>').attr('id', 'approveLocation')
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
                                                        $('<button/>').attr("id", "approveButton")
                                                            .addClass('btn btn-small btn-success')
                                                            .attr('data-loading-text', 'Голосуем...')
                                                            .append(
                                                                $('<i/>').addClass('icon-ok icon-white')
                                                            )
                                                            .append(
                                                                $('<span/>').addClass("spacer3").text('Подтверждаю точку')
                                                            )
                                                    )

                                            )
                                            .append(
                                                $('<div/>').addClass('btn-group pull-right')
                                                    .append(
                                                        $('<button/>').attr("id", "notApproveButton")
                                                            .addClass('btn btn-small btn-warning')
                                                            .attr('data-loading-text', 'Голосуем...')
                                                            .append(
                                                                $('<i/>').addClass('icon-remove icon-white')
                                                            )
                                                            .append(
                                                                $('<span/>').addClass("spacer3").text('Точки здесь больше нет')
                                                            )
                                                    )
                                            )
                                    )
                            )
                    )
            );

        exRes.append(res);
        return exRes;
    }

    function getMarkerEditContent() {

        var res = $('<form/>').attr('id', 'editMarkerForm').addClass('infoWindowInner form-horizontal')
            .attr('autocomplete', 'off')
            .append(
                $('<div/>').attr("id", "editFormAlert").addClass("alert").attr("hidden", "true").attr("style", "display: none;")
                    .append(
                        $('<button/>').attr("type", "button").addClass("close").attr("data-dismiss", "alert").text('x')
                    )
                    .append(
                        $('<strong/>').text("Упс..").attr("id", "editFormAlertBeginText")
                    )
                    .append(
                        $('<span/>').attr("id", "editFormAlertText").addClass("spacer3")
                            .text("Извините, произошла какая-то ошибка..")
                    )
            )
            .append(
                $('<legend/>').text('Создание точки')
            )
            .append(
                $('<div/>').addClass('control-group').attr('hidden', 'true')
                    .append(
                        $('<label/>').addClass('control-label').attr('for','location-id').text('id')
                    )
                    .append(
                        $('<div/>').addClass('controls')
                            .append(
                                $('<input/>').addClass('input-block-level span4').attr('id', 'location-id')
                            )
                    )
            )
            .append(
                $('<div/>').addClass('control-group')
                    .append(
                        $('<label/>').addClass('control-label').attr('for','title').text('Название')
                    )
                    .append(
                        $('<div/>').addClass('controls')
                            .append(
                                $('<input/>').addClass('input-block-level span4')
                                    .attr('id', 'title').attr('name', 'title')
                                    .attr('placeholder', 'Чебуречная Ашота').attr('required', 'true')
                            )
                    )
            )
            .append(
                $('<div/>').addClass('control-group')
                    .append(
                        $('<label/>').addClass('control-label').attr('for','description').text('Описание')
                    )
                    .append(
                        $('<div/>').addClass('controls')
                            .append(
                                $('<input/>').addClass('input-block-level span4')
                                    .attr('id', 'description').attr('name', 'description')
                                    .attr('placeholder', 'Самые лучшие чебуреки и шаурма в городе!')
                                    .attr('required', 'true')
                            )
                    )
            )
            .append(
                $('<div/>').addClass('control-group')
                    .append(
                        $('<label/>').addClass('control-label').attr('for','type').text('Тип')
                    )
                    .append(
                        $('<div/>').addClass('controls')
                            .append(
                                $('<select/>').attr('id', 'type').attr('name', 'type')
                                    .append(
                                        getOptionsElementsForType()
                                    )
                            )
                    )
            )
            .append(
                $('<div/>').addClass('control-group')
                    .append(
                        $('<div/>').addClass('controls')
                            .append(
                                $('<label/>').addClass('radio')
                                    .append(
                                        $('<input/>').attr('id', 'footype1').attr('name', 'footypeRadio')
                                            .attr('type', 'radio')
                                            .attr('value', 'true').attr('checked', 'true')
                                    )
                                    .append(
                                        $('<span/>').text('Это тошняк или палатка')
                                    )
                            )
                            .append(
                                $('<label/>').addClass('radio')
                                    .append(
                                        $('<input/>').attr('id', 'footype2').attr('name', 'footypeRadio')
                                            .attr('type', 'radio')
                                            .attr('value', 'false')
                                    )
                                    .append(
                                        $('<span/>').text('Это нормальное кафе')
                                    )
                            )
                    )
            )
            .append(
                $('<div/>').addClass('control-group')
                    .append(
                        $('<label/>').addClass('control-label').attr('for', 'actualDate').text('Дата проверки')
                    )
                    .append(
                        $('<div/>').addClass('controls')
                            .append(
                                $('<input/>').attr('id', 'actualDate').addClass('input-block-level span4')
                                    .attr('type', 'text').attr('name', 'actualDate')
                            )
                    )
            )
            .append(
                $('<div/>').addClass('control-group')
                    .append(
                        $('<label/>').addClass('control-label').attr('for','addressDescription').text('Описание адреса')
                    )
                    .append(
                        $('<div/>').addClass('controls')
                            .append(
                                $('<input/>').addClass('input-block-level span4')
                                    .attr('id', 'addressDescription').attr('name', 'addressDescription')
                                    .attr('placeholder', 'Выходите с электрички и спускаетесь под мост')
                            )
                    )
            )
            .append(
                $('<div/>').addClass('control-group').attr('hidden', 'true')
                    .append(
                        $('<label/>').addClass('control-label').attr('for','latitude').text('Latitude')
                    )
                    .append(
                        $('<div/>').addClass('controls')
                            .append(
                                $('<input/>').addClass('input-block-level span4')
                                    .attr('id', 'latitude').attr('name', 'latitude')
                                    .attr('hidden', 'true').attr('required', 'true')
                            )
                    )
            )
            .append(
                $('<div/>').addClass('control-group').attr('hidden', 'true')
                    .append(
                        $('<label/>').addClass('control-label').attr('for','longitude').text('Longitude')
                    )
                    .append(
                        $('<div/>').addClass('controls')
                            .append(
                                $('<input/>').addClass('input-block-level span4')
                                    .attr('id', 'longitude').attr('name', 'longitude')
                                    .attr('required', 'true')
                            )
                    )
                )
                .append(
                    $('<hr/>')
                )
                .append(
                    $('<div/>').addClass('control-group').attr('hidden', 'true')
                        .append(
                            $('<div/>').addClass('controls')
                                .append(
                                    $('<label/>').addClass("checkbox inline")
                                        .append(
                                            $('<input/>').attr('type', 'checkbox').attr('id','createAddressSwitch')
                                        )
                                        .append(
                                            $('<span/>').text('Знаете реальный адрес?')
                                        )
                                )
                        )
                )
                .append(
                    $('<div/>').attr('id', 'realAddress').attr('hidden', 'true')
                        .append(
                            $('<div/>').addClass('control-group')
                                .append(
                                    $('<label/>').addClass('control-label').attr('for','country').text('Страна')
                                )
                                .append(
                                    $('<div/>').addClass('controls')
                                        .append(
                                            $('<input/>').addClass('input-block-level span4')
                                                .attr('id', 'country').attr('name', 'country')

                                        )
                                )
                        )
                        .append(
                            $('<div/>').addClass('control-group')
                                .append(
                                    $('<label/>').addClass('control-label').attr('for','region').text('Регион')
                                )
                                .append(
                                    $('<div/>').addClass('controls')
                                        .append(
                                            $('<input/>').addClass('input-block-level span4')
                                                .attr('id', 'region').attr('name', 'region')

                                        )
                                )
                        )
                        .append(
                            $('<div/>').addClass('control-group')
                                .append(
                                    $('<label/>').addClass('control-label').attr('for','city').text('Город')
                                )
                                .append(
                                    $('<div/>').addClass('controls')
                                        .append(
                                            $('<input/>').addClass('input-block-level span4')
                                                .attr('id', 'city').attr('name', 'city')

                                        )
                                )
                        )
                        .append(
                            $('<div/>').addClass('control-group')
                                .append(
                                    $('<label/>').addClass('control-label').attr('for','street').text('Улица')
                                )
                                .append(
                                    $('<div/>').addClass('controls')
                                        .append(
                                            $('<input/>').addClass('input-block-level span4')
                                                .attr('id', 'street').attr('name', 'street')

                                        )
                                )
                        )
                        .append(
                            $('<div/>').addClass('control-group')
                                .append(
                                    $('<label/>').addClass('control-label').attr('for','house').text('Дом, корпус итд.')
                                )
                                .append(
                                    $('<div/>').addClass('controls')
                                        .append(
                                            $('<input/>').addClass('input-block-level span4')
                                                .attr('id', 'house').attr('name', 'house')

                                        )
                                )
                        )
                        .append(
                            $('<div/>').addClass('control-group').attr('hidden', 'true')
                                .append(
                                    $('<label/>').addClass('control-label').attr('for','addressLine').text('addressLine')
                                )
                                .append(
                                    $('<div/>').addClass('controls')
                                        .append(
                                            $('<input/>').addClass('input-block-level span4')
                                                .attr('id', 'addressLine').attr('name', 'addressLine')

                                        )
                                )
                        )
                        .append(
                            $('<div/>').addClass('control-group').attr('hidden', 'true')
                                .append(
                                    $('<label/>').addClass('control-label').attr('for','zipcode').text('Индекс')
                                )
                                .append(
                                    $('<div/>').addClass('controls')
                                        .append(
                                            $('<input/>').addClass('input-block-level span4')
                                                .attr('id', 'zipcode').attr('name', 'zipcode')

                                        )
                                )
                        )
                )
            .append(
                $('<div/>').addClass('form-actions')
                    .append(
                        $('<button/>').attr('id','submitEdit').attr('type', 'submit').addClass('btn btn-primary')
                            .attr('data-loading-text', 'Сохраняем...')
                            .text('Сохранить точку')
                    )
                    .append(
                        $('<button/>').attr('id','cancelEdit').attr('type', 'button').addClass('btn').text('Отмена')
                    )
            );

        return res;
    }

    function getOptionsElementsForType() {

        var res = [];
        for(var i = 0; i < params.types.length; i++) {
            var type = params.types[i];

            var id = type.id;
            var val = null;
            for(var j = 0; j < type.names.length; j++) {
                if( type.names[j].language === DATE_LANGUAGE ) {
                    val = type.names[j].value;
                }
            }
            res.push($('<option/>').val(id).text(val));
        }
        return res;
    }

    ///  help functions
    function getLatLngFromGeoLocation(geoLocation) {
        return new google.maps.LatLng( geoLocation.lat, geoLocation.lng );
    }

    function getGeoLocationFromLatLng(latLng) {
        return {
            lng: latLng.lng(),
            lat: latLng.lat()
        };
    }

    function createEmptyLocation() {
        return {
            title: "",
            description: "",
            type: {
                id: "",
                names: [
                    {
                        language: DATE_LANGUAGE,
                        value: ""
                    }
                ]
            },
            footype: true,
            addressDescription: "",
            actualDate: "",
            geoLocation: {
                lng: 0.0,
                lat: 0.0
            },
            address: {
                country: "",
                region: "",
                city: "",
                street: "",
                house: "",
                zipcode: "",
                addressLine: ""
            },
            alreadyVoted: false,
            alreadyRated: false,
            averageRate: 0.0
        };
    }

    function isMarkerAlreadyRendered(location) {

        var loaded = markersIds.get(location.id);
        if( !loaded ) {
            return false;
        }

        return true;
    }

    function getTypeById(id) {
        for(var i = 0; i < params.types.length; i++) {
            var type = params.types[i];
            if( type.id !== id ) {
                continue;
            }

            return type;
        }
        return null;
    }

    function getTypeValueByLanguage(type, lang) {
        for(var i = 0; i < type.names.length; i++) {
            if(type.names[i].language === lang) {
                return type.names[i].value;
            }
        }
    }

    function getLocationFullURL(location) {
        if( params.realPath.indexOf("localhost") >= 0 ) {
            return "www.cheatfood.com/location/" + location.id;
        }
        else {
            return params.realPath + "/location/" + location.id;
        }
    }

    function addressToString(address) {

        var res = "";
        if( stringIsNotEmpty(address.street) ) {
            res += address.street;
        }

        if( stringIsNotEmpty(address.city) ) {
            if( stringIsNotEmpty(res) ) {
                res += ", ";
            }
            res += address.city;
        }

        if( stringIsNotEmpty(address.region) ) {
            if( stringIsNotEmpty(res) ) {
                res += ", ";
            }
            res += address.region;
        }

        return res;
    }

    function stringIsNotEmpty(str) {

        if( str === "" ) {
            return false;
        }

        if( str.length === 0 ) {
            return false;
        }

        return true;
    }

    function getImageDirPath() {
        return params.realPath + "/resources/img/";
    }

    function getImagePath(imageName) {
        return getImageDirPath() + imageName;
    }

    function getMainlogoURL() {
        return getImagePath("mainlogo");
    }

    function HashMap(){
        this._dict = {};
    }
    HashMap.prototype._shared = {id: 1};
    HashMap.prototype.put = function put(key, value){
        if(typeof key == "object"){
            if(!key.hasOwnProperty._id){
                key.hasOwnProperty = function(key){
                    return Object.prototype.hasOwnProperty.call(this, key);
                }
                key.hasOwnProperty._id = this._shared.id++;
            }
            this._dict[key.hasOwnProperty._id] = value;
        }else{
            this._dict[key] = value;
        }
        return this; // for chaining
    };
    HashMap.prototype.get = function get(key){
        if(typeof key == "object"){
            return this._dict[key.hasOwnProperty._id];
        }
        return this._dict[key];
    };
    HashMap.prototype.remove = function remove(key) {
        if(typeof key == "object"){
            delete this._dict[key.hasOwnProperty._id];
        }
        delete this._dict[key];
    };
});

