$(document).ready(function(){

    "use strict";

    var map;
    var moscowCenter = {lat: 55.764283, lng: 37.606614};
    var infoBox;

    var params = {
        realPath: null,
        types: []
    };
    var markers = new HashMap();

    var newMarker = false;

    var GRID_SIZE = 50;
    var MAX_ZOOM = 15;
    var MAX_ZOOM_FOR_MARKER = 18;
    var MAX_POSSIBLE_ZOOM = 30;
    var ENTER_KEY = 13;
    var ZOOM_LEVEL = 10;
    var TYPE_IMAGE_WIDTH = 128;
    var EFFECTS_TIME = 250;
    var DATE_FORMAT = 'yy-mm-dd';
    var DATE_FORMAT_DISPLAY = 'dd.mm.yy';
    var DATE_LANGUAGE = 'ru';


    loadParams();

    function loadParams() {

        params.realPath = $('#realPath').text().trim();

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

        buildLoginMenu();
        createRegistrationAuthActions();

        createMap();
        createContextMenuForMap();
        createContextMenuForMarker();
        createMarkerEditFormOnMap();
        createLocateMeButton();
        createAddMarkerButton();
        createMainMenuBehavior();
        centerMapToLocation(location);
        createMarkersForLocations(location);

    }

    function buildLoginMenu() {
        $.get(params.realPath+"/api/users/current", function(user) {

            if( !user ) {
                $('#loginMenuLink').text('Вход');

                $('#loginLink').show();
                $('#registrationLink').hide();
                $('#profileLink').hide();
                $('#logoutLink').hide();
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

                $('#loginMenuLink').text(name);

                $('#loginLink').hide();
                $('#registrationLink').hide();
                $('#profileLink').show();
                $('#logoutLink').show();
            }
        });
    }

    function createRegistrationAuthActions() {

        $('#logoutLink').click(function() {
            logout();
        });

        $('#loginLink').click(function() {
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
            $('#emailCreate').focus()
        });

        $('#createUserForm').off('submit');
        $('#createUserForm').submit(function() {
            checkCreateUserFormValidOrNot();
            return false;
        });
    }

    function logout() {

        $.ajax({
            type: "DELETE",
            url: params.realPath + "/connect/logout",
            success: function(data) {

            },
            complete: function(data) {
                buildLoginMenu();
            }
        })
    }

    function clearCreateUserForm() {
        $('#emailCreate').val(null);
        $('#passwordCreate').val(null);
        $('#registrationAlert').hide();
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
            url: params.realPath + '/api/users/create',
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

                buildLoginMenu();
                $('#createUserSubmit').button('reset');
            },
            statusCode: {
                400: function(data) {

                }
            }
        });
    }

    function createMap() {
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
                    { visibility: "off" }
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

    function centerMapToLocation() {
        disableLocateMeButton();

        GMaps.geolocate({
            success: function(position) {

                if( !location ) {
                    map.setCenter(position.coords.latitude, position.coords.longitude);
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

    function createContextMenuForMap() {

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

    function createContextMenuForMarker() {

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
        enableAddMarkerMenu();
        disableEditMarkerMenu();
        disableDeleteMarkerMenu();
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

        //disable context menu
        google.maps.event.clearListeners(map.map, 'rightclick');

        //create marker and edit-form on left click
        google.maps.event.addListener(map.map, 'click', function(event) {
            createMarkerForContextMenu(event.latLng);
        });

        disableAddMarkerMenu();
    }

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

    function createMarkersForLocations(location) {

        infoBox = createInfoBoxForMarkers();

        $.get(params.realPath+"/api/locations", function(data) {
            $.each(data, function(n, loadedLocation) {
                var infoBoxObject = {
                    infoBox: infoBox,
                    location: loadedLocation
                };

                var zoomIn = false;
                if( location ) {
                    if(loadedLocation.id === location.id) {
                        zoomIn = true;
                    }
                }

                createMarkerWithInfoBoxForLocation(infoBoxObject, zoomIn);
            });
        });
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

    function enableLocateMeButton() {
        $('#locateMe').click(function(){
            centerMapToLocation();
        });
    }

    function disableLocateMeButton() {
        $('#locateMe').off('click');
        $('#locateMe').addClass('disabled');
    }

    function createAddMarkerButton() {
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
            pixelOffset: new google.maps.Size(8, -190),
            enableEventPropagation: false
        };
        infoBox = new InfoBox(infoOptions);

        if(position) {
            infoBox.setPosition(position);
        }

        return infoBox;
    }

    function createMarkerWithInfoBoxForLocation(infoBoxObject, zoomIn) {

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
            var address = addressToString(location.address);
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
    }

    function initInfoBoxButtonsBehavior(infoBoxObject) {
        initScrollInfoBoxBehavior();
        initToggleEditAndViewBehavior(infoBoxObject);
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

        $('#deleteModal').modal('show');
    }

    function deleteMarkerRequest(infoBoxObject) {
        $('#deleteMarkerButtonModal').button('loading');

        $.ajax({
            type: "DELETE",
            url: params.realPath+'/api/location/'+infoBoxObject.location.id,
            success: function(data) {
                $('#deleteModal').modal('hide');
                $('#deleteMarkerButtonModal').button('reset');
                removeMarkerAndInfoBox(infoBoxObject);
            }
        });
    }

    function removeMarkerAndInfoBox(infoBoxObject) {
        infoBoxObject.infoBox.hide();
        map.removeMarker(infoBoxObject.marker);
        markers.remove(infoBoxObject.marker);
    }

    function initAndShowEditForm(infoBoxObject) {
        map.map.setCenter( infoBoxObject.infoBox.getPosition() );
        infoBoxObject.infoBox.hide();

        $('#editMarkerFormDiv').fadeIn(EFFECTS_TIME, function() {
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
        var zipcode = $('#zipcode').val();

        var param = {
            id: id,
            title: title,
            description: description,
            type: type,
            footype: footype,
            addressDescription: addressDescription,
            actualDate: actualDate,
            geoLocation: {
                latitude: latitude,
                longitude: longitude
            },
            address: {
                country: country,
                region: region,
                city: city,
                street: street,
                zipcode: zipcode
            }
        };

        param = JSON.stringify(param);

        $.ajax({
            type: "POST",
            url: params.realPath+'/api/location',
            data: param = param,
            contentType: 'application/json',
            mimeType: 'application/json',
            dataType: 'json',
            success: function(data) {
            },
            complete: function(data) {
                var newLocation = JSON.parse(data.responseText);
                infoBoxObject.location = newLocation;

                $('#submitEdit').button('reset');
                $('#editMarkerFormDiv').fadeOut(EFFECTS_TIME);

                showInfoBoxForMarker(infoBoxObject);
                newMarker = false;
                enableEditMarkerMenu(infoBoxObject);
                enableDeleteMarkerMenu(infoBoxObject);
                setDefaultMouseBehavior();
            },
            statusCode: {
                400: function(data) {

                }
            }
        });
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
        $('#latitude').val( location.geoLocation.latitude );
        $('#longitude').val( location.geoLocation.longitude );

        //unnecessary fields
        if( location.address ) {
            $('#country').val( location.address.country );
            $('#region').val( location.address.region );
            $('#city').val( location.address.city );
            $('#street').val( location.address.street );
            $('#zipcode').val( location.address.zipcode );
        }
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
        $('#zipcode').val( null );
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
                                            )
                                            .append(
                                                $('<button/>').addClass('btn btn-small').text('Маршрут отсюда')
                                            )

                                    )
                                    .append(
                                        $('<div/>').addClass('btn-group pull-right')
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
                                                $('<button/>').addClass('btn btn-small btn-success')
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
                                                $('<button/>').addClass('btn btn-small btn-warning')
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
            );

        exRes.append(res);
        return exRes;
    }

    function getMarkerEditContent() {

        var res = $('<form/>').attr('id', 'editMarkerForm').addClass('infoWindowInner form-horizontal')
            .attr('autocomplete', 'off')
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
                    $('<hr/>')
                )
                .append(
                    $('<div/>').addClass('control-group')
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
                                    $('<label/>').addClass('control-label').attr('for','street').text('Улица, дом итд')
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
        return new google.maps.LatLng( geoLocation.latitude, geoLocation.longitude );
    }

    function getGeoLocationFromLatLng(latLng) {
        return {
            latitude: latLng.lat(),
            longitude: latLng.lng()
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
                latitude: "",
                longitude: ""
            },
            address: {
                country: "",
                region: "",
                city: "",
                street: "",
                zipcode: ""
            }
        };
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

    function getImagePath(imageName) {
        return params.realPath + "/resources/img/" + imageName;
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
    }
    HashMap.prototype.get = function get(key){
        if(typeof key == "object"){
            return this._dict[key.hasOwnProperty._id];
        }
        return this._dict[key];
    }
    HashMap.prototype.remove = function remove(key) {
        if(typeof key == "object"){
            delete this._dict[key.hasOwnProperty._id];
        }
        delete this._dict[key];
    }

});

