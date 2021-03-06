$(function() {

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
        no_such_user: "no_such_user",
        no_password_data: "no_password_data",
        wrong_password: "wrong_password",
        login_error: "login_error",
        password_mismatch: "password_mismatch",
        wrong_token: "wrong_token",
        merge_users: "merge_users",
        user_already_exists: "user_already_exists",
        email_is_empty: "email_is_empty",
        no_user_with_such_email: "no_user_with_such_email",
        overpriced: "overpriced",
        comment_is_empty: "comment_is_empty",
        too_early_comment: "too_early_comment",
        other: "other"
    };

    var warnings = {
        email_change: "email_change"
    };

    var currentPage = 0;
    var markersCount = 0;
    var markers = new HashMap();
    var markersIds = new HashMap();
    var tempMarkers = [];

    var curBounds;
    var prevBounds;
    var currentTypeId;

    var slidepanel = false;
    var newMarker = false;
    var searchMarker = false;


    var PAGE_SIZE = 10;
    var COMMENT_LENGTH = 1000;
    var RATY_SIZE = 24;
    var UNKNOWN_USER_ID = "-1";
    var NEW_MARKER_ID = "";
    var GRID_SIZE = 50;
    var MAX_ZOOM = 15;
    var MAX_ZOOM_FOR_MARKER = 18;
    var MAX_POSSIBLE_ZOOM = 30;
    var ENTER_KEY = 13;
    var ZOOM_LEVEL = 10;
    var TYPE_IMAGE_WIDTH = 192;

    var DATE_FORMAT = 'yy-mm-dd';
    var DATE_FORMAT_DISPLAY = 'dd.mm.yy';

    var TIME_FORMAT = 'HH:mm:ss';
    var TIME_FORMAT_DISPLAY = 'HH:mm:ss';

    var DATE_LANGUAGE = 'ru';
    var MIN_WIDTH_TOOLBAR = 1366;
    var MIN_WIDTH_INFOBAR = 1024;


    loadParams(init);

    function loadParams(callback) {

        params.realPath = $('#realPath').text().trim();

        var token = $('#tokenLabel').text();
        params.token = token;

        var login = $('#loginLabel').text();

        var location = $('#locationLabel').text();
        if( location ) {
            location = JSON.parse(location);
            params.location = location;
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

        $.get(params.realPath+'/api/params', function(data) {
            params.types = data.types;
            params.maxPrice = data.maxPrice;
            params.recommendedPrice = data.recommendedPrice;
            params.commentSecondsDelay = data.commentSecondsDelay;

            if( callback ) {
                callback();
            }
        });
    }

    function init() {

        $(window).resize(function() {
            setMapCorrectSize();
        });

        setMapCorrectSize();
        checkCookies();
    }

    function setMapCorrectSize() {
        var h = $(window).height();
        var offset = $('#firstNavbar').height() + $('#secondNavbar').height();
        if( $('#footer').is(':visible') === true ) {
            offset += $('#footer').height();
        }

        $('#mainList').css('height', (h-offset));
        $('#map').css('height', (h-offset));
        $('#slidepanel').css('height', (h-offset));
    }

    function checkCookies() {
        var cookie = $.cookie(COOKIE_NAME);

        if( !cookie ) {
            //non authorized
            sessionStorage.setItem('showHello', false);
            $().modifyInterface(false);
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
                                showNoteTopCenter("Привет, %username%!", "success", true);
                            }

                            if( needToShowFirstTime() === true ) {
                                var details = res.result;
                                if( details ) {
                                    var firstTime = details.firstTimeLogin;
                                    if( firstTime === true ) {
                                        var text = "Первый раз на сайте? Прочтите ";
                                        text += "<a href=\"" + params.realPath + "/help" + "\">правила</a>";
                                        text += " использования, чтобы все было проще.";

                                        showNoteTopCenter(text, "warning", true);
                                    }
                                }
                            }

                            sessionStorage.setItem('showFirstTime', true);
                            sessionStorage.setItem('showHello', true);
                            $().modifyInterface(true);
                        }
                        else {
                            //non authorized
                            sessionStorage.setItem('showHello', false);
                            $().modifyInterface(false);
                        }
                    }
                    else {
                        //non authorized
                        sessionStorage.setItem('showHello', false);
                        $().modifyInterface(false);
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

    function needToShowFirstTime() {
        var show = sessionStorage.getItem('showFirstTime');

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

    $.fn.modifyInterface = function modifyInterface(auth) {

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
        buildInterface(auth);
    };

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
                params.currentUser = {id: user.id, name: user.visibleName, email: user.email, admin: isUserAdmin(user)};

                showLoginMenuInfo(auth, user.visibleName);
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
            $('#registrationLink').show();
            $('#profileLink').hide();
            $('#logoutLink').hide();
        }
    }

    function buildInterface(auth) {
        createRegistrationAuthActions();

        if( $.fn.createMap !== undefined ) {
            $().createMap(auth);
        }

        if($.fn.loadLocations !== undefined ) {
            $().loadLocations();
        }
    }

    function showNoLocationsNoteTopCenter() {

        var text = "Кажется, в этом районе никто еще не отметил ни одной локации. Вы можете быть первым!";

        if(authorized === false ) {
            text += " Только" + '<a id="noteLoginLink" href="#"> войдите</a>' + ' в систему для этого';
        }

        var n = noty({
            text: text,
            layout: 'topRight',
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

    $.fn.loadLocations = function loadLocations() {
        getLocationsTotalCount(loadLocationsBody);
    };

    function loadLocationsBody(totalCount) {
        var url = params.realPath+"/api/locations/all?size="+PAGE_SIZE+"&page="+currentPage;

        $.ajax({
            type: "GET",
            url: url,
            success: function(data) {

                $('#mainLocations').children().remove();

                $.each(data, function(n, location) {
                    $('#mainLocations').append(createLocationInMainList(location));
                });
            }
        });

        var pageCount = Math.ceil(totalCount / PAGE_SIZE);

        $("#mainListPrev").off('click');
        if( currentPage === 0 ) {
            $("#mainListPrev").closest('li').addClass("disabled");
            $("#mainListPrev").off('click');
        }
        else {
            $("#mainListPrev").closest('li').removeClass("disabled");

            $("#mainListPrev").click(function() {
                currentPage--;
                if( currentPage < 0 ) {
                    currentPage = 0;
                }

                loadLocationsBody(totalCount);
            });
        }


        $("#mainListNext").off('click');
        if(currentPage === pageCount-1) {
            $("#mainListNext").closest('li').addClass("disabled");
            $("#mainListNext").off('click');
        }
        else {
            $("#mainListNext").closest('li').removeClass("disabled");

            $("#mainListNext").click(function() {
                currentPage++;
                if( currentPage >= pageCount ) {
                    currentPage = pageCount-1;
                }

                loadLocationsBody(totalCount);
            });
        }

    }

    function getLocationsTotalCount(callback) {
        $.get(params.realPath+"/api/locations/totalcount", function(res) {

            var totalCount = res.result;

            if( callback ) {
                callback(totalCount);
            }
        });
    }

    function createLocationInMainList(location) {

        var favClass = "icon-heart-empty";
        if( location.inFavourites ) {
            if( location.inFavourites === true ) {
                favClass = "icon-heart";
            }
            else {
                favClass = "icon-heart-empty";
            }
        }
        else {
            favClass = "icon-heart-empty";
        }

        var fav = $('<a/>').addClass("favLink").attr("href", "#")
            .append(
                $('<i/>').addClass(favClass).attr("id", "mainFavIcon"+location.id)
            )
            .click(function() {
                var infoBoxObject = {
                    infoBox: infoBox,
                    location: location
                };
                favForLocation(infoBoxObject);
            })
            .hide();

        var rate = location.averageRate;
        if( !rate ) {
            rate = 0;
        }

        var res = $('<li/>').addClass("media").append(
            $('<a/>').addClass("pull-left")
                .append(
                    $('<img/>').addClass("media-object").attr("width", "64").attr("src", getIconImagePath(location.type))
                )
                .append(
                    $('<label/>').addClass("label transparent").text(getTypeValueByLanguage(location.type, DATE_LANGUAGE))
                )
        ).append(
                $('<div/>').addClass("media-body").append(
                    $('<h5/>').addClass("media-heading").text(location.title)
                )
                   /* .append(
                        $('<p/>').append($('<small/>').text(location.description))
                    )*/
                    .append(
                        $('<address/>').append(
                            $('<small/>').text(location.address.addressLine)
                        )
                            .append($('<br/>'))
                    )
                    .append(
                        $('<div/>').addClass("media")
                            .append(
                                $('<div/>').addClass("media-body")
                                    .append(
                                        $('<span/>').addClass("label label-success transparent")
                                            .text(location.averagePrice+" RUB")
                                    )
                                    .append(
                                        $('<span/>').addClass("badge badge-info spacer5 transparent")
                                            .text(rate)
                                    )
                                    .append(
                                        $('<div/>').addClass("btn-group pull-right")
                                            .append(
                                                fav
                                            )
                                            .append(
                                                $('<button/>').addClass("btn btn-mini spacer5")
                                                    .append(
                                                        $('<i/>').addClass("icon-expand-alt")
                                                    )
                                                    .append($('<span/>').addClass("spacer3").text("Подробнее"))
                                                    .click(function() {
                                                        detailsButtonInListClicked(location);
                                                    })
                                            )
                                    )
                            )
                    )
            );

        return res;
    }

    function detailsButtonInListClicked(location) {
        var infoBoxObject = {
            infoBox: infoBox,
            location: location
        };

        showMarkerSlidePanel(infoBoxObject);
        loadLocationAndCenterMap(location.id);
    }

    $.fn.createMap = function createMap(auth) {
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
        loadDataAfterMapIsLoaded();

        createMapBoundsChangedBehavior();
    };

    function createMapBoundsChangedBehavior() {

        google.maps.event.addListener(map.map, 'dragend', function() {
            hideAutoCompleteResults();
            loadAndCreateMarkersForLocationsInBounds();
            loadLocationsInfo();
        } );

        google.maps.event.addListener(map.map, 'zoom_changed', function() {
            hideAutoCompleteResults();
            loadAndCreateMarkersForLocationsInBounds();
            loadLocationsInfo();
        });
    }

    function loadDataAfterMapIsLoaded() {

        google.maps.event.addListenerOnce(map.map, 'idle', function() {
            loadLocationsInfo();
            getLocationsCountInCurrentBounds();

            centerMapToLocation();
        });

    }

    function getLocationsCountInCurrentBounds() {
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
                        loadAndCreateMarkersForLocationsInBounds(null);
                    }
                }
            }
        });
    }

    function createMapControls(auth) {
        createInfoBox(auth);
        createContextMenuForMap(auth);
//        createContextMenuForMarker(auth);
        createMarkerEditFormOnMap();
        createLocateMeButton();
        createAddMarkerButton(auth);
        createRouteForm();
        initSearchBarBehavior();
        hideOrShowControlsDueToDocumentWidth();
    }

    function hideOrShowControlsDueToDocumentWidth() {

        var width = document.width;
        if( width <= MIN_WIDTH_TOOLBAR ) {
            $('#addMarkerButton').hide();
            $('#infoBar').hide();
        }
        else {
            $('#addMarkerButton').show();
            $('#infoBar').show();
        }
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
        var div = $('<div/>').attr('id','editMarkerFormDiv').attr("style", "display: none")
            .addClass('span12').append(getEditFormMarkup());

        $('#slidepanelInfoContent').hide();

        $('#slidepanelEditContent').children().remove();
        $('#slidepanelEditContent').append(div);
        $('#slidepanelEditContent').show();
    }

    function showEditMarkerFormDiv(infoBoxObject, doNotShowSlidePanel) {

        if( !doNotShowSlidePanel ) {
            showMarkerSlidePanel(infoBoxObject, true);
        }
        else {
            if( doNotShowSlidePanel === false ) {
                showMarkerSlidePanel(infoBoxObject, true);
            }
        }

        createMarkerEditFormOnMap();
        $('#editMarkerFormDiv').fadeIn(EFFECTS_TIME, function() {
            $('#closeSlidePanel').off('click');
            $('#closeSlidePanel').click(function() {
                cancelNewMarkerAddition(infoBoxObject);
            });
        });
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

                var width = document.width;
                if( width <= MIN_WIDTH_TOOLBAR ) {
                    $('#addMarkerButton').hide();
                }
                else {
                    $('#addMarkerButton').show();
                }

                $('#addMarkerButton').click(function(){
                    addMarkerOnMapByLeftClick();
                });
            });
        }

    }

    function loadLocationsInfo() {
        var bounds = map.map.getBounds();

        var ne = bounds.getNorthEast();
        var sw = bounds.getSouthWest();

        $.ajax({
            type: "GET",
            url: params.realPath+"/api/locations/locationsinfo",
            data: {
                ne_latitude: ne.lat(),
                ne_longitude: ne.lng(),
                sw_latitude: sw.lat(),
                sw_longitude: sw.lng()
            },
            success: function(data) {

                if( data ) {
                    if( data.result ) {
                        $('#locationsCountLabel').text(data.result.totalCount);
                        $('#locationsLocalCountLabel').text(data.result.regionCount);
                        $('#locationsNewCountLabel').text(data.result.newCount);
                    }
                }
            }
        });
    }

    function initSearchBarBehavior() {

        var address = document.getElementById("searchBar");
        var autocomplete = new google.maps.places.Autocomplete(address);

        initAutocompleteFirstResult(autocomplete);
        initSearchBarFormBehavior();
    }

    function initAutocompleteFirstResult(autocomplete) {
        google.maps.event.addListener(autocomplete, 'place_changed', function() {

            hideAutoCompleteResults();

            var place = autocomplete.getPlace();
            if (!place.geometry) {
                return;
            }

            initGeocodeResultMarkerBehavior(place.geometry.location);
        });
    }

    function initSearchBarFormBehavior() {

        $('#searchBarForm').off("submit");
        $('#searchBarForm').submit(function() {
            return false;
        });

        $('#searchButton').off('click');
        $('#searchButton').click(function() {
            $('#searchButton').button('loading');
            searchAddress();
        });

        $("#clearSearchButton").off('click');
        $("#clearSearchButton").click(function() {
            $('#searchBar').val(null);
            map.map.setZoom(ZOOM_LEVEL);
            removeAllTempMarkers();
        });
    }

    function checkOrClearAutocompleteResultsOnMap() {
        var search = $('#searchBar').val();
        if( !search ) {
            removeAllTempMarkers();
            $('#searchButton').button('reset');
            return false;
        }

        if( !stringIsNotEmpty(search) ) {
            removeAllTempMarkers();
            $('#searchButton').button('reset');
            return false;
        }

        return true;
    }

    function searchAddress() {

        if( checkOrClearAutocompleteResultsOnMap() === false ) {
            return;
        }

        var search = $('#searchBar').val();
        var geocoder = new google.maps.Geocoder();
        geocoder.geocode( { 'address': search}, function(results, status) {

            if (status == google.maps.GeocoderStatus.OK) {
                var position = results[0].geometry.location;
                $('#searchBar').val(results[0].formatted_address);
                hideAutoCompleteResults();
                initGeocodeResultMarkerBehavior(position);
            }
            else if( status == google.maps.GeocoderStatus.ZERO_RESULTS ) {
                showNoteTopCenter("Извините, ничего не найдено", "warning", true);
            }
            else {
                showNoteTopCenter("Извините, произошла ошибка", "warning", true);
            }

            $('#searchButton').button('reset');
            hideAutoCompleteResults();
        });
    }

    function initGeocodeResultMarkerBehavior(position) {

        map.map.setCenter(position);
        map.map.setZoom(MAX_ZOOM_FOR_MARKER);

       /* var marker = map.addMarker({
            lat: position.lat(),
            lng: position.lng(),
            draggable: true,
            icon: getDefaultMarkerImagePath(),
            click: function() {
                createMarkerForContextMenuByMarker(marker);
            }
        });*/

        removeAllTempMarkers();
//        tempMarkers.push(marker);

        searchMarker = true;
    }

    function hideAutoCompleteResults() {
        $('.pac-container').hide();
    }

    function removeAllTempMarkers() {
        hideAllTempMarkers();
        tempMarkers = [];
        searchMarker = false;
    }

    function hideAllTempMarkers() {
        $.each(tempMarkers, function(n, marker) {
            marker.setMap(null);
            map.removeMarker(marker);
        });
    }

    function showAllTempMarkers() {

        $.each(tempMarkers, function(n, marker) {
            marker.setMap(map.map);
        });
    }

    function createRouteForm() {
        var div = $('<div/>').attr("id", "routeFormDiv").addClass("span4 infoWindow")
            .attr("style", "display: none")
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

        map.map.controls[google.maps.ControlPosition.TOP_CENTER].push( div.get(0) );

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
                disableHideMarkerMenu();
            }
        });
    }

    function centerMapToLocation() {
        disableLocateMeButton();

        GMaps.geolocate({
            success: function(position) {
                myPosition = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);

                if( !params.location ) {
                    map.setCenter(myPosition.lat(), myPosition.lng());
                }

                enableLocateMeButton();
            },
            error: function(error) {

                if( !params.location ) {
                    map.setCenter( moscowCenter.lat, moscowCenter.lng );
                }

                $('#locateMeDiv').hide();
            },
            not_supported: function() {
                if( !params.location ) {
                    map.setCenter( moscowCenter.lat, moscowCenter.lng );
                }

                $('#locateMeDiv').hide();
            },
            always: function() {}
        });

    }

    function createMarkerForContextMenuByMarker(marker) {
        createMarkerForContextMenu(marker.position);
    }

    function createMarkerForContextMenu(latLng) {

        if( newMarker === true ) {
            return;
        }

        disableAddMarkerMenu();
        hideAllTempMarkers();

        if( $.fn.disableContextMenu !== undefined ) {
            $().disableContextMenu();
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
        disableHideMarkerMenu();
        enableProfileMenu();
    }

    function enableProfileMenu() {

        $('#profileLink').off('click');
        $('#profileLink').click(function() {
            if( infoBox ) {
                infoBox.hide();
            }
            $('#profileModal').modal('show');
        });

        $('#profileModal').on('show', function () {
            initProfileForm();
        });
    }

    function initProfileForm() {

        $('#profileAlert').hide();
        returnProfileFormSubmitDefault();
        initProfileFormValidation();
        initProfileFormBehavior();

        if( params.hasOwnProperty('currentUser')) {
            loadProfileData();
        }
    }

    function loadProfileData() {
        $.get(params.realPath+"/api/users/current", function(user) {
            if( user ) {
                if( user.hasOwnProperty('firstName')) {
                    $('#profileFirstName').val(user.firstName);
                }
                else {
                    $('#profileFirstName').val(null);
                }

                if( user.hasOwnProperty('lastName')) {
                    $('#profileLastName').val(user.lastName);
                }
                else {
                    $('#profileLastName').val(null);
                }

                if( user.hasOwnProperty('email')) {
                    $('#profileEmail').val(user.email);
                }
                else {
                    $('#profileEmail').val(null);
                }

                if( user.facebookId ) {
                    $('#profileFacebookLink').show();
                    $('#profileFacebookLink').attr("href", "http://www.facebook.com/" + user.facebookId);
                }
                else {
                    $('#profileFacebookLink').hide();
                }

                if( user.foursquareId ) {
                    $('#profileFoursquareLink').show();
                    $('#profileFoursquareLink').attr("href", "http://foursquare.com/user/" + user.foursquareId);
                }
                else {
                    $('#profileFoursquareLink').hide();
                }

                if( user.twitterUsername ) {
                    $('#profileTwitterLink').show();
                    $('#profileTwitterLink').attr("href", "http://twitter.com/" + user.twitterUsername);
                }
                else {
                    $('#profileTwitterLink').hide();
                }
            }
        });

        $.get(params.realPath+"/api/locations/userconnected", function(locations) {

            if( locations ) {
                if( locations.length === 0 ) {
                    $('#connectedLocationsLabel').show(EFFECTS_TIME);
                }
                else {
                    $('#connectedLocationsLabel').hide(EFFECTS_TIME);

                    var locEls = [];
                    for(var i = 0; i < locations.length; i++ ) {
                        locEls.push(getLocationTrInTable(locations[i], i));
                    }

                    $('#connectedLocsBody').children().remove();
                    $('#connectedLocsBody').append(locEls);
                }
            }
        });
    }

    function getLocationTrInTable(location, i) {

        var ratyViewProperties = {
            path: getImageDirPath(),
            size: RATY_SIZE,
            score: location.averageRate,
            readOnly: true
        };

        return $('<tr/>').attr("id", "connLocTr"+i)
            .append(
                $('<td/>').attr("style", "display: none").attr("hidden", "true").attr("id", "conLocId"+i)
                    .text(location.id)
            )
            .append(
                $('<td/>').append(
                    $('<a/>').attr("id", "connLocLink"+i).attr('href', '#').text(location.title)
                        .click(function() {
                            showLocationFromProfile($('#conLocId'+i).text());
                        })
                )
            ).append(
                $('<td/>').text(location.address.addressLine)
            )
            .append(
                $('<td/>').text(location.averagePrice + " RUB")
            )
            .append(
                $('<td/>').raty(ratyViewProperties)
            )
            .append(
                $('<td/>').text(location.creator.publicName)
            )
            .append(
                $('<td/>').append(
                    $('<a/>').attr("id", "connLocEdit"+i)
                        .append(
                            $('<i/>').addClass("icon-remove-sign")
                        ).click(function() {
                            removeLocationFromProfile(i,$('#conLocId'+i).text());
                        })
                )
            );
    }

    function showLocationFromProfile(id) {

        $('#profileModal').modal('hide');


    }

    function loadLocationAndCenterMap(id) {
        $.get(params.realPath+'/api/locations/'+id, function(location) {
            if( !location ) {
                return;
            }

            renderSingleMarkerForLocation(infoBox, location, true);

            $.each(markers._dict, function(n, marker) {
                var location = marker.location;

                if( location.id === id ) {

                    var pos = getLatLngFromGeoLocation(location.geoLocation);

                    map.map.setCenter(pos);
                    map.map.setZoom(MAX_ZOOM_FOR_MARKER);

                    var infoBoxObject = {
                        infoBox: marker.infoBox,
                        location: location,
                        marker: marker
                    };

                    markerClickBehavior(marker.marker, infoBoxObject);
                }
            });
        });
    }

    function removeLocationFromProfile(i, id) {

        $.ajax({
            type: "DELETE",
            url: params.realPath+'/api/locations/'+id+'/deleteprofile',
            complete: function(result) {

                if( !result ) {
                    return;
                }

                if( result.responseJSON ) {
                    var res = result.responseJSON;
                    if( res.error === false ) {

                        var loc = res.result;

                        $.each(markers._dict, function(n, marker) {
                            var location = marker.location;
                            if( location.id === loc.id ) {
                                var infoBoxObject = markers.get(marker.marker);
                                infoBoxObject.location = loc;
                            }
                        });


                        var trId = "connLocTr"+i;
                        $('#'+trId).replaceWith(
                            $('<tr/>').attr('id', trId).append(
                                $('<span/>').text("Локация удалена из избранного!")
                            ).append(
                                    $('<a/>').addClass("spacer5").attr("href", "#").text("Восстановить?")
                                        .attr("id", "restoreConnLoc"+i).click(function() {
                                            restoreLocationToProfile(i, id)
                                        })
                                )
                        );
                    }
                }

            }
        });
    }

    function restoreLocationToProfile(i, id) {

        $.get(params.realPath+'/api/locations/'+id+'/restoreprofile', function(result) {

            if( !result ) {
                return;
            }

            if( result.error === false ) {

                var loc = result.result;

                $.each(markers._dict, function(n, marker) {
                    var location = marker.location;
                    if( location.id === loc.id ) {
                        var infoBoxObject = markers.get(marker.marker);
                        infoBoxObject.location = loc;
                    }
                });

                var trId = "#connLocTr"+i;
                $(trId).replaceWith(
                    getLocationTrInTable(loc, i)
                );
            }
        });
    }

    function initProfileFormBehavior() {

        $('#profileForm').off('submit');

        $('#profileEmail').off('keyup change');
        $('#profileEmail').on('keyup change', function() {
            var email = $('#profileEmail').val().trim();
            if( params.currentUser.email ) {
                if( params.currentUser.email !== email ) {
                    $('#profileAlert').show(EFFECTS_TIME);
                    $('#profileError').text("Вы собираетесь поменять адрес email?.. ");
                }
                else {
                    $('#profileAlert').hide(EFFECTS_TIME);
                }
            }
        });

        $('#saveProfileForm').off('click');
        $('#saveProfileForm').click(function() {
            //don't need prevent?
            checkProfileFormValidOrNot();
        });
    }

    function returnProfileFormSubmitDefault() {
        $('#profileForm').data('submitted', false);
        $('#saveProfileForm').button('reset');
    }

    function initProfileFormValidation() {
        $("#profileForm").validate({
            rules: {
                profileEmail: {
                    required: true,
                    email: true
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

    function checkProfileFormValidOrNot() {
        $('#saveProfileForm').button('loading');

        if( $("#profileForm").valid() ) {
            submitProfileForm();
        }
        else {
            $('#saveProfileForm').button('reset');
        }
    }

    function submitProfileForm() {

        var userUpdate = {
            id: params.currentUser.id,
            firstName: $('#profileFirstName').val().trim(),
            lastName: $('#profileLastName').val().trim(),
            email: $('#profileEmail').val().trim()
        };

        $.ajax({
            type: "POST",
            url: params.realPath+"/api/users/update",
            data: JSON.stringify( userUpdate ),
            contentType: 'application/json',
            mimeType: 'application/json',
            dataType: 'json',
            success: function() {

            },
            complete: function(data) {

                $('#profileAlert').hide();

                if( data.responseJSON ) {
                    var res = data.responseJSON;

                    if( res.error === false ) {

                        if( res.warning === false ) {
                            $('#profileModal').modal('hide');
                            returnProfileFormSubmitDefault();
                            window.location.replace(params.realPath);
                        }
                        else {
                            if( res.warningType === warnings.email_change ) {
                                $('#profileModal').modal('hide');
                                showGlobalMessage("Мы отправили вам письмо с секретной ссылкой для смены адреса электропочты " +
                                    "на введеный вами. " +
                                    "Вам нужно пройти по ссылке из письма для привязки адреса email к вашей учетной записи." +
                                    " Выходить (разлогиниваться) из системы для этого не нужно! Обычно письма приходят в течении " +
                                    "нескольких секунд, но если письма не будет - напишите нам на info@cheatfood.com");
                            }
                        }
                    }
                    else {
                        if( res.errorType === errors.no_such_user ) {
                            $('#profileAlert').show(EFFECTS_TIME);
                            $('#profileError').text("Нет такого пользователя ;(");
                        }
                        else if( res.errorType === errors.access_denied) {
                            $('#profileAlert').show(EFFECTS_TIME);
                            $('#profileError').text("Извините, но у вас нет прав на это действие...")
                        }
                        else if( res.errorType === errors.merge_users ) {
                            $('#profileAlert').show(EFFECTS_TIME);
                            $('#profileError').text("Мы нашли в системе другого пользователя с адресом email, который вы указали." +
                                " Если это действительно вы, то напишите нам на info@cheatfood.com и мы сможем объединить эти учетные записи" +
                                " в одну.")

                            $('#profileEmail').val(null);
                        }
                    }
                }
                returnProfileFormSubmitDefault();
            },
            statusCode: {
                400: function(data) {
                    $('#profileAlert').show(EFFECTS_TIME);
                    $('#profileError').text("Извините, у нас какая-то ошибка на сервере ;(");
                    returnProfileFormSubmitDefault();
                }
            }
        });
    }

    function showGlobalMessage(text) {
        $('#messageModal').modal('show');
        $('#messageModalText').html(text);
    }

    function createCategoryMenu() {

        if( $('#categoryMenu').children().length > 0 ) {
            return;
        }

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

        if( infoBox ) {
            infoBox.hide();
        }

        showCurrentActionForm(text, cancelLoadAndCreateMarkersByCategory);
        loadAndCreateMarkersForLocationsInBounds(type_id);
    }

    function cancelLoadAndCreateMarkersByCategory() {
        currentTypeId = undefined;

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

        $('#deleteMarkerMenu').closest('li').hide();

        if( params.hasOwnProperty('currentUser') ) {
            if( params.currentUser.hasOwnProperty('id') ) {

                if( infoBoxObject.location.hasOwnProperty("creator") ) {
                    if( infoBoxObject.location.creator.id === params.currentUser.id ) {
                        $('#deleteMarkerMenu').closest('li').show();
                        $('#deleteMarkerMenu').closest('li').removeClass('disabled');
                        $('#deleteMarkerMenu').off('click');
                        $('#deleteMarkerMenu').click(function() {
                            deleteMarkerAction(infoBoxObject);
                        });
                    }
                    else {
                        if( params.currentUser.hasOwnProperty('admin') ) {
                            if( params.currentUser.admin === true) {
                                $('#deleteMarkerMenu').closest('li').show();
                                $('#deleteMarkerMenu').closest('li').removeClass('disabled');
                                $('#deleteMarkerMenu').off('click');
                                $('#deleteMarkerMenu').click(function() {
                                    deleteMarkerAction(infoBoxObject);
                                });
                            }
                        }
                    }
                }
            }
        }
    }

    function disableDeleteMarkerMenu() {

        $('#deleteMarkerMenu').closest('li').addClass('disabled');
        $('#deleteMarkerMenu').off('click');
        $('#deleteMarkerMenu').closest('li').hide();
    }

    function enableHideMarkerMenu(infoBoxObject) {

        $('#hideMarkerMenu').closest('li').hide();

        if( params.hasOwnProperty('currentUser') ) {
            if( params.currentUser.hasOwnProperty('admin')) {
                if( params.currentUser.admin === true ) {
                    $('#hideMarkerMenu').closest('li').show();
                    $('#hideMarkerMenu').closest('li').removeClass('disabled');
                    $('#hideMarkerMenu').off('click');
                    $('#hideMarkerMenu').click(function() {
                        hideMarkerAction(infoBoxObject);
                    });
                }
            }
        }
    }

    function disableHideMarkerMenu() {

        $('#hideMarkerMenu').closest('li').addClass('disabled');
        $('#hideMarkerMenu').off('click');

        if( params.hasOwnProperty('currentUser') ) {
            if( params.currentUser.hasOwnProperty('admin') ) {
                if( params.currentUser.admin !== true ) {
                    $('#hideMarkerMenu').closest('li').hide();
                }
                else {
                    $('#hideMarkerMenu').closest('li').show();
                }
            }
            else {
                $('#hideMarkerMenu').closest('li').show();
            }
        }
        else {
            $('#hideMarkerMenu').closest('li').show();
        }
    }




    $.fn.disableContextMenu = function disableContextMenu() {
        //disable context menu
        google.maps.event.clearListeners(map.map, 'rightclick');
    };

    function addMarkerOnMapByLeftClick() {

        hideAllTempMarkers();

        showCurrentActionForm('Добавление новой точки...', cancelNewMarkerAddition, "infoBoxObject");

        var cursorPath = getImagePath('pin.png');
        map.setOptions( {
            draggableCursor : "url("+cursorPath+"), auto",
            draggingCursor : "url("+cursorPath+"), auto"
        });

        //create marker and edit-form on left click
        google.maps.event.addListener(map.map, 'click', function(event) {
            createMarkerForContextMenu(event.latLng);
        });

        disableCategoryMenu();
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

    function renderMarkersForLocations(data, infoBox) {
        $.each(data, function(n, loadedLocation) {
            renderSingleMarkerForLocation(infoBox, loadedLocation);
        });
    }

    function renderSingleMarkerForLocation(infoBox, locationToRender, zoom) {

        var infoBoxObject = {
            infoBox: infoBox,
            location: locationToRender
        };

        var zoomIn = false;
        if( params.location ) {
            if(locationToRender.id === params.location.id) {
                zoomIn = true;
            }
        }

        if( zoom === true ) {
            zoomIn = true;
        }

        return createMarkerWithInfoBoxForLocation(infoBoxObject, zoomIn);
    }

    function loadAndCreateMarkersForLocationsInBounds(type_id) {

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
                currentTypeId = type_id;
                data.typeId = type_id;
            }
            else {
                currentTypeId = undefined;
            }
        }
        else {
            if(currentTypeId) {
                if( currentTypeId !== null ) {
                    data.typeId = currentTypeId;
                }
            }
        }

        $.ajax({
            type: "GET",
            url: params.realPath+"/api/locations/inbounds",
            data: data,
            success: function(result) {
                renderMarkersForLocations(result, infoBox);
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
        boxText.innerHTML = getInfoBoxWindowMarkup();

        var infoOptions = {
            content: boxText,
            boxClass: "span7 transparent infoWindow",
            boxStyle: {
                "min-width": "510px",
                width: "700px"
            },
            disableAutoPan: false,
            isHidden: true,
            pane: "floatPane",
            infoBoxClearance: new google.maps.Size(50, 50),
            maxWidth: 900,
            pixelOffset: new google.maps.Size(40, -250),
            enableEventPropagation: false
        };
        infoBox = new InfoBox(infoOptions);

        if(position) {
            infoBox.setPosition(position);
        }

        return infoBox;
    }

    function createMarkerWithInfoBoxForLocation(infoBoxObject, zoomIn) {

        var renderRes = isMarkerAlreadyRendered(infoBoxObject.location);
        if( renderRes === true ) {
            return;
        }

        var pos = getLatLngFromGeoLocation(infoBoxObject.location.geoLocation);

        var marker = map.addMarker({
            lat: pos.lat(),
            lng: pos.lng(),
            title: location.title,
            icon: getMarkerImagePath(infoBoxObject.location.type),
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
                map.map.setCenter(pos);

                markerClickBehavior(marker, infoBoxObject);

                map.setZoom(MAX_ZOOM_FOR_MARKER);
            }
        }

        return marker;
    }

    function showMarkerSlidePanel(infoBoxObject, edit) {

        if( slidepanel === false ) {

            $('#mainList').hide();

            $('#slidepanel').show();
            $('#slidepanel').animate({
                "width": "+=350px"
            }, EFFECTS_TIME);
            $('#map').removeClass("span9");

            setMapCorrectSize();
        }

        if( infoBox ) {
            infoBox.hide();
        }

        slidepanel = true;

        if( edit ) {
            if( edit === true ) {
                //initialization the form with data is in caller method
                return;
            }
        }

        $('#slidepanelInfoContent').show();
        $('#slidepanelEditContent').hide();
        initSlidePanelWithData(infoBoxObject);
        initSlidePanelButtonsBehavior(infoBoxObject);
    }

    function hideMarkerSlidePanel(infoBoxShow, callback) {


        $('#slidepanel').fadeOut(EFFECTS_TIME, function() {
            $('#mainList').show();
            $('#map').addClass("span9");

            $('#slidepanel').animate({
                "width": "-=350px"
            }, 0);

            setMapCorrectSize();
        });


        if( infoBoxShow ) {
            if( infoBoxShow === true ) {
                if( infoBox ) {
                    infoBox.show();
                }
            }
        }

        if( callback ) {
            callback();
        }

        slidepanel = false;
    }

    function initSlidePanelWithData(infoBoxObject) {

        $('#slideCommentsTree').children().remove();

        loadCommentsForLocation(infoBoxObject);

        $('#closeSlidePanel').off('click');
        $('#closeSlidePanel').click(function() {
            hideMarkerSlidePanel(true);
        });

        var loc = infoBoxObject.location;

        $('#slideImage').attr("src", getIconImagePath(loc.type) );
        $('#slideType').text( getTypeValueByLanguage(loc.type, DATE_LANGUAGE) );
        $('#slideTitle').text(loc.title);
        $('#slideDescription').text(loc.description);

        if( loc.footype === true ) {
            $('#slideFootype').addClass('icon-warning-sign').removeClass('icon-ok-sign');
            $('#slideFootypeText').text('Это тошняк или палатка');
        }
        else if( loc.footype === false ) {
            $('#slideFootype').removeClass('icon-warning-sign').addClass('icon-ok-sign');
            $('#slideFootypeText').text('Это нормальное кафе');
        }

        if(loc.averagePrice) {
            $('#slideAveragePrice').text(loc.averagePrice + " RUB");

            if( loc.averagePrice > params.recommendedPrice ) {
                $('#slideAveragePriceInfo').addClass("label-warning");
                $('#slideAveragePriceInfo').addClass("label-warning");
                $('#slideAveragePriceInfo').removeClass("label-success");
            }
            else {
                $('#slideAveragePriceInfo').removeClass("label-warning");
                $('#slideAveragePriceInfo').removeClass("label-warning");
                $('#slideAveragePriceInfo').addClass("label-success");
            }
        }

        $('#slideAddressDescrBody').hide();
        if(loc.addressDescription) {
            $('#slideAddressDescription').text(loc.addressDescription);
            $('#slideAddressDescrBody').show();
        }

        if(loc.actualDate) {
            var parseDate = $.datepicker.parseDate( DATE_FORMAT, loc.actualDate );
            var displayDate = $.datepicker.formatDate( DATE_FORMAT_DISPLAY, parseDate );
            $('#slideActualDate').text(displayDate);
        }

        $('#slideAddressBody').hide();
        if( loc.address ) {
            var address = "";

            if( loc.address.addressLine ) {
                address = loc.address.addressLine;
            }
            else {
                address = addressToString(loc.address);
            }

            if( stringIsNotEmpty(address) === true   ) {
                $('#slideAddress').text(address);
                $('#slideAddressBody').show();
            }
        }

        $('#slideSiteUrlBody').hide();
        if(loc.siteUrl) {
            $('#slideSiteUrl').text(loc.siteUrl);
            $('#slideSiteUrl').attr("href", loc.siteUrl);
            $('#slideSiteUrlBody').show();
        }

        $('#slideCreatorBody').hide();
        if( loc.creator ) {
            if( stringIsNotEmpty(loc.creator.publicName)) {
                if( loc.creator.id !== UNKNOWN_USER_ID ) {
                    $('#slideCreatorBody').show();
                    $('#slideCreator').text(loc.creator.publicName);
                }
            }
        }

        $('#slideApproveLoc').hide();
        $('#slideEditGroup').hide();
        $('#slideHide').hide();
        $('#slideEdit').hide();
        $('#slideDelete').hide();
        $('#slideRatyGroup').hide();

        if( authorized === true ) {
            $('#slideEditGroup').show();
            $('#slideEdit').show();

            if( params.hasOwnProperty('currentUser') ) {
                if(params.currentUser.hasOwnProperty('admin') ) {
                    if( params.currentUser.admin === true ) {
                        $('#slideHide').show();
                        $('#slideDelete').show();
                    }
                    else {
                        if( loc.creator ) {
                            if( loc.creator.id === params.currentUser.id ) {
                                $('#slideDelete').show();
                            }
                        }
                    }
                }
            }

            if( loc.alreadyVoted === false ) {
                $('#slideApproveLoc').show();
            }

            if( loc.alreadyRated === false ) {
                var ratyActionProperties = {
                    path: getImageDirPath(),
                    size: RATY_SIZE,
                    score: 0,
                    click: function(score, e) {
                        rateForLocation(infoBoxObject, score);
                    }
                };
                $('#slideRatyGroup').show();
                $('#slideRatyAction').raty(ratyActionProperties);
            }

            if( loc.inFavourites ) {
                if( loc.inFavourites === true ) {
                    $('#slideFavIcon').removeClass("icon-heart-empty");
                    $('#slideFavIcon').addClass("icon-heart");
                }
                else {
                    $('#slideFavIcon').removeClass("icon-heart");
                    $('#slideFavIcon').addClass("icon-heart-empty");
                }
            }
            else {
                $('#slideFavIcon').removeClass("icon-heart");
                $('#slideFavIcon').addClass("icon-heart-empty");
            }
        }

        var ratyViewProperties = {
            path: getImageDirPath(),
            size: RATY_SIZE,
            score: loc.averageRate,
            readOnly: true
        };
        $('#slideRatyView').raty(ratyViewProperties);

        if( window.pluso ) {
            if (typeof window.pluso.start === "function") {
                var url = getLocationFullURL(infoBoxObject.location);

                $('#slidePluso').children().remove();
                $('#slidePluso').append( getPluso(url));

                window.pluso.start();
            }
        }
    }

    function loadCommentsForLocation(infoBoxObject) {

        var id = infoBoxObject.location.id;

        $.get(params.realPath+'/api/locations/'+id+'/comments', function(result) {
            if( result === null ) {
                $('#slideNoCommentsWarning').show(EFFECTS_TIME);
                return;
            }

            var comments = result.result;
            if( comments === null ) {
                $('#slideNoCommentsWarning').show(EFFECTS_TIME);
                return;
            }

            if( comments.length === 0 ) {
                $('#slideNoCommentsWarning').show(EFFECTS_TIME);
                return;
            }

            $('#slideNoCommentsWarning').hide(EFFECTS_TIME);
            $('#slideCommentsTree').children().remove();

            $.each(comments, function(n, comment) {
                $('#slideCommentsTree').prepend(getCommentMarkup(infoBoxObject, comment, comments));
            })
        });
    }

    function deleteComment(infoBoxObject, comment) {

        $('#slideDelComment'+id).button('loading');

        var id = infoBoxObject.location.id;

        $.ajax({
            type: "DELETE",
            url: params.realPath + "/api/locations/"+id+"/comments/"+comment.id+"/delete",
            success: function(){},
            complete: function(data) {

                $('#slideDelComment'+id).button('reset');

                if( !data ) {
                    showNoteTopCenter("Какая-то странная ошибка на сервере..извините", "warning", true);
                    return;
                }

                if( !data.responseJSON ) {
                    showNoteTopCenter("Какая-то странная ошибка на сервере..извините", "warning", true);
                    return;
                }

                var res = data.responseJSON;
                if( res.error === false ) {
                    loadCommentsForLocation(infoBoxObject);
                }
            }
        });
    }

    function voteForComment(infoBoxObject, comment, value) {

        var id = infoBoxObject.location.id;

        $.ajax({
            type: "POST",
            url: params.realPath+"/api/locations/"+id+"/comments/"+comment.id+"/vote",
            data: JSON.stringify(value),
            contentType: 'application/json',
            mimeType: 'application/json',
            dataType: 'json',
            success: function(){},
            complete: function(data) {

                if( data.responseJSON ) {
                    var res = data.responseJSON;
                    if( res.error === false ) {
                        var comm = res.result;

                        showNoteTopCenter("Спасибо!", "success", true);

                        $('#slideVotesUpText'+comment.id).text(comm.votesUpCount);
                        $('#slideVotesDownText'+comment.id).text(comm.votesDownCount);
                    }
                }
                else {
                    showNoteTopCenter("Какая-то странная ошибка на сервере..извините", "warning", true);
                    return;
                }
            }
        });
    }

    function quoteCommentInForm(comments) {

        var quote = $('#slideQuestionComment').val();
        if( quote ) {
            $('#slideQuestionLabel').show(EFFECTS_TIME);

            $('#slideQuestionClose').off('click');
            $('#slideQuestionClose').click(function() {
                $('#slideQuestionLabel').hide(EFFECTS_TIME);
                $('#slideQuestionComment').val(null);
            });

            $('#slideQuestionQuote').text(getQuoteComment(comments, quote));
        }
        else {
            $('#slideQuestionLabel').hide(EFFECTS_TIME);
        }
    }

    function getQuoteComment(comments, quote) {

        if( !quote ) {
            return "";
        }

        var comment = null;
        for(var i = 0; i < comments.length; i++ ){
            if( comments[i].id === quote) {
                comment = comments[i];
                break;
            }
        }

        if( comment ) {
            return comment.authorPublicName;
        }
        else {
            return quote;
        }
    }

    function getCommentMarkup(infoBoxObject, comment, comments) {

        var id = comment.id;

        if( comment.date ) {
            var parseDate = $.datepicker.parseDate( DATE_FORMAT, comment.date );
            var parseTime = $.datepicker.parseTime( TIME_FORMAT, comment.time );

            var displayDate = $.datepicker.formatDate( DATE_FORMAT_DISPLAY, parseDate );
            var displayTime = $.datepicker.formatTime( TIME_FORMAT_DISPLAY, parseTime );

            var dateTime = displayDate + ' ' + displayTime;
        }

        var admin = isCurrentUserAdmin();

        var deleteButton = $('<button/>').addClass("btn btn-mini").attr("id", "slideDelComment"+id)
            .attr("data-loading-text", "Удаляем...")
            .attr("style", "display: none")
            .click(function(){
                deleteComment(infoBoxObject, comment);
            })
            .append(
                $('<i/>').addClass("icon-trash")
            )
            .append(
                $('<span/>').addClass("spacer3").text("Удалить")
            );

        var commentAuthor = $('<a/>').attr('id', 'slideCommentAuthor'+id).attr("href", "#")
            .addClass("spacer3 transparent")
            .text(comment.authorPublicName)
            .click(function() {
                $('#slideQuestionComment').val(id);
                showSlideAddCommentForm(function() {
                    quoteCommentInForm(comments);
                });
            });

        if( admin === true ) {
            deleteButton.show();
            commentAuthor.addClass("text-error");
        }
        else {
            commentAuthor.addClass("text-info");
        }

        var voteUpButton = $('<a/>').addClass("badge badge-success transparent")
            .attr("id", "slideVoteUpComment"+id)
            .append(
                $('<i/>').addClass("icon-thumbs-up")
            )
            .append(
                $('<span/>').attr("id", "slideVotesUpText"+id)
                    .addClass("spacer3").text(comment.votesUpCount)
            );

        var voteDownButton = $('<a/>').addClass("badge badge-important transparent")
            .attr("id", "slideVoteDownComment"+id)
            .append(
                $('<i/>').addClass("icon-thumbs-down")
            )
            .append(
                $('<span/>').attr("id", "slideVotesDownText"+id)
                    .addClass("spacer3").text(comment.votesDownCount)
            );

        voteUpButton.addClass("disabled");
        voteDownButton.addClass("disabled");

        if( authorized === true ) {
            if(params.currentUser) {
                if( comment.author.id !== params.currentUser.id ) {
                    voteUpButton.click(function() {
                        voteForComment(infoBoxObject, comment, true);
                    });
                    voteDownButton.click(function() {
                        voteForComment(infoBoxObject, comment, false);
                    });
                }
            }
        }


        var quoteLabel;
        if( comment.questionCommentId ) {
            var quoteText = getQuoteComment(comments, comment.questionCommentId);

            quoteLabel = $('<label/>').addClass("label transparent").attr("id", "slideQuoteInComment"+id)
                .append(
                    $('<span/>').text("это ответ пользователю: ")
                )
                .append(
                    $('<span/>').attr('id', "slideQuoteInCommentText"+id).text(quoteText)
                );
        }
        else {
            quoteLabel = $('<span/>');
        }

        var res = $('<li/>').addClass("media")
            .append(
                $('<div/>').addClass("media-body")
                    .append(
                        $('<p/>')
                            .append(
                                $('<b/>')
                                    .append(
                                        $('<i/>').addClass("icon-user")
                                    )
                                    .append(
                                        commentAuthor
                                    )
                                    .append(
                                        $('<small/>').attr("id", "slideCommentDate"+id).addClass("spacer3")
                                            .text(dateTime)
                                    )
                            )
                            .append(
                                $('<span/>').addClass("pull-right")
                                    .append(
                                        voteUpButton
                                    )
                                    .append(
                                        voteDownButton
                                    )
                            )
                    )
                    .append(
                        $('<p/>')
                            .append(
                                $('<span/>').attr('id', 'slideCommentId'+id).attr("style", "display: none;").text(id)
                            )
                            .append(
                                $('<span/>').attr("id", "slideCommentBody"+id).text(comment.text)
                            )

                    )
                    .append(
                        $('<p/>').append(
                            quoteLabel
                        )
                            .append(
                                $('<span/>').addClass("pull-right")
                                    .append(
                                        deleteButton
                                    )
                            )
                    )
            )
            .append(
                $('<hr/>')
            );

        return res;
    }

    function initSlidePanelButtonsBehavior(infoBoxObject) {

        $('#slideToHere').off('click');
        $('#slideToHere').click(function() {
            getRouteAddresses(infoBoxObject, true);
        });

        $('#slideFromHere').off('click');
        $('#slideFromHere').click(function() {
            getRouteAddresses(infoBoxObject, false);
        });

        $('#slideEdit').off('click');
        $('#slideEdit').click(function() {
            initAndShowEditForm(infoBoxObject, true);
        });

        $('#slideDelete').off('click');
        $('#slideDelete').click(function() {
            $('#deleteAlert').hide();
            $('#deleteModal').modal('show');
        });

        $('#slideHide').off('click');
        $('#slideHide').click(function() {
            $('#hideAlert').hide();
            $('#hideModal').modal('show');
        });

        $('#slideFav').off('click');
        $('#slideFav').click(function() {
            favForLocation(infoBoxObject);
        });

        $('#slideApprove').off('click');
        $('#slideApprove').click(function() {
            voteForLocation(infoBoxObject, true, $('#slideApprove') );
        });

        $('#slideNotApprove').off('click');
        $('#slideNotApprove').click(function() {
            voteForLocation(infoBoxObject, false, $('#slideNotApprove') );
        });

        //comments
        $('#slideComment').off('click');
        if( authorized === false ) {
            $('#slideComment').hide();
        }
        else {
            $('#slideComment').click(function() {
                var vis = $('#slideCommentForm').data('visible');

                if( vis !== true ) {
                    showSlideAddCommentForm(quoteCommentInForm());
                }
                else {
                    hideSlideAddCommentForm();
                }
            });
        }


        initSlideAddCommentFormValidation(infoBoxObject);
        initSlideCommentFormSubmitBehavior(infoBoxObject);
    }

    function showSlideAddCommentForm(callback) {
        $('#slideCommentForm').fadeIn(EFFECTS_TIME, function() {
            $('#slideCommentForm').data('visible', true);
            $('#slideCommentTitle').text("Отмена");
            $('#slideCommentIcon').removeClass("icon-chevron-right");
            $('#slideCommentIcon').addClass("icon-chevron-left");
            $('#slideCommentText').focus();

            if( callback ) {
                callback();
            }
        });
    }

    function hideSlideAddCommentForm() {
        $('#slideCommentForm').fadeOut(EFFECTS_TIME, function() {
            $('#slideCommentForm').data('visible', false);
            $('#slideCommentTitle').text("Оставьте отзыв");
            $('#slideCommentIcon').addClass("icon-chevron-right");
            $('#slideCommentIcon').removeClass("icon-chevron-left");

            clearSlideAddCommentForm();
        });
    }

    function clearSlideAddCommentForm() {
        $('#slideCommentText').val(null);
        $('#slideQuestionComment').val(null);
        $('#slideSymbNumber').text(COMMENT_LENGTH);
    }

    function initSlideAddCommentFormValidation(infoBoxObject) {
        $("#slideCommentForm").validate({
            rules: {
                slideCommentText: {
                    required: true,
                    minlength: 1,
                    maxlength: 1000
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

    function initSlideCommentFormSubmitBehavior(infoBoxObject) {

        $('#slideCommentText').off('keyup change');
        $('#slideCommentText').on('keyup change', function() {
            var text = $('#slideCommentText').val();
            $('#slideSymbNumber').text(COMMENT_LENGTH - text.length);
        });

        $('#slideCommentForm').off('submit');
        $('#slideCommentForm').submit(function() {
            return false;
        });
        $('#slideSubmitComment').off('click');
        $('#slideSubmitComment').click(function() {
            submitSlideAddCommentForm(infoBoxObject);
        });
    }

    function submitSlideAddCommentForm(infoBoxObject) {

        $('#slideCommentAlert').hide(EFFECTS_TIME);
        $('#slideSubmitComment').button('loading');

        if( $("#slideCommentForm").valid() ) {
            submitSlideAddCommentFormPOST(infoBoxObject);
        }
        else {
            $('#slideSubmitComment').button('reset');
        }
    }

    function submitSlideAddCommentFormPOST(infoBoxObject) {
        var comment = {
            text: $('#slideCommentText').val().trim(),
            questionCommentId: $('#slideQuestionComment').val().trim()
        };
        var id = infoBoxObject.location.id;

        $.ajax({
            type: "POST",
            url: params.realPath + "/api/locations/"+id+"/comments/add",
            data: JSON.stringify(comment),
            contentType: 'application/json',
            mimeType: 'application/json',
            dataType: 'json',
            success: function(){},
            complete: function(data) {

                if( data.responseJSON ) {
                    var res = data.responseJSON;

                    if( res.error === false ) {
                        //good job
                        loadCommentsForLocation(infoBoxObject);

                        $('#slideSubmitComment').button('reset');
                        hideSlideAddCommentForm();
                    }
                    else {
                        showSlideAddCommentErrorByType(res.errorType);
                    }
                }
                else {
                    showSlideAddCommentError("Какая-то странная ошибка на сервере..извините");
                }
            }
        });
    }

    function showSlideAddCommentErrorByType(errorType) {

        if( errorType === errors.unknown_location ) {
            showSlideAddCommentError("Такой локации больше нет в базе. Обновите страничку!");
        }
        else if( errorType === errors.no_such_user ) {
            showSlideAddCommentError("Сервер говорит, что вашего пользователя больше не существует. Сожалеем...");
        }
        else if( errorType === errors.comment_is_empty ) {
            showSlideAddCommentError("Введите что-нибудь, пустые комментарии не разрешены");
        }
        else if( errorType === errors.access_denied ) {
            showSlideAddCommentError("Извините, у вас нет прав на этой действие");
        }
        else if( errorType === errors.too_early_comment ) {
            var del = params.commentSecondsDelay;
            showSlideAddCommentError("Подождите " +del+ " секунд, потом снова сможете отправить комментарий");
        }

        $('#slideSubmitComment').button('reset');
    }

    function showSlideAddCommentError(text) {
        $('#slideCommentError').text(text);
        $('#slideCommentAlert').show(EFFECTS_TIME);
    }

    function markerClickBehavior(marker, infoBoxObject) {
        initialShowInfoBoxForMarker(marker, infoBoxObject);
        enableEditMarkerMenu(infoBoxObject);
        enableDeleteMarkerMenu(infoBoxObject);
        enableHideMarkerMenu(infoBoxObject);
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
        infoBoxObject.infoBox.setPosition(getLatLngFromGeoLocation(infoBoxObject.location.geoLocation));
        infoBoxObject.infoBox.show();
        setInfoBoxContentFromLocation(infoBoxObject);

    }

    function setInfoBoxContentFromLocation(infoBoxObject) {

        google.maps.event.clearListeners(infoBoxObject.infoBox, 'domready');

        var location = infoBoxObject.location;
        var ratyViewProperties = {
            path: getImageDirPath(),
            size: RATY_SIZE,
            score: location.averageRate,
            readOnly: true
        };
        $('#rateViewButtons').raty(ratyViewProperties);

        var ratyActionProperties = {
            path: getImageDirPath(),
            size: RATY_SIZE,
            score: 0,
            click: function(score, e) {
                rateForLocation(infoBoxObject, score);
            }
        };

        if( authorized === false ) {
            $('#approveLocation').hide();
            $('#editDeleteMarkerGroup').hide();
            $('#rateActionsDiv').hide();
        }
        else {
            if( location.alreadyVoted === false ) {
                $('#approveLocation').show();
            }
            else {
                $('#approveLocation').hide();
            }

            if( location.alreadyRated === true ) {
                ratyActionProperties.readOnly = true;
                $('#rateActionsDiv').hide();
            }
            else {
                $('#rateActionsDiv').show();
            }

            if( location.creator.id != params.currentUser.id ) {
                $('#deleteMarkerButton').hide();

                if( params.hasOwnProperty('currentUser') ) {
                    if(params.currentUser.hasOwnProperty('admin') ) {
                        if( params.currentUser.admin === true ) {
                            $('#deleteMarkerButton').show();
                        }
                    }
                }
            }
            else {
                $('#deleteMarkerButton').show();
            }

            $('#rateActionButtons').raty(ratyActionProperties);

            $('#editDeleteMarkerGroup').show();
        }

        $('#hideMarkerButton').hide();
        if( params.hasOwnProperty('currentUser') ) {
            if(params.currentUser.hasOwnProperty('admin') ) {
                if( params.currentUser.admin === true ) {
                    $('#hideMarkerButton').show();
                }
            }
        }

        $('#adminCheckedRibbon').hide();
        if( location.hasOwnProperty('adminChecked') ) {
            if( location.adminChecked === true ) {
                $('#adminCheckedRibbon').show();
            }
        }

        $('#voteUpLabel').text(location.votesUpCount);
        $('#voteDownLabel').text(location.votesDownCount);

        $('#info_title').text(location.title);
        $('#info_type').text( getTypeValueByLanguage(location.type, DATE_LANGUAGE) );
        $('#info_description').text(location.description);
        $('#info_type_icon').attr("src", getIconImagePath(location.type) );

        $('#info_creator_body').hide();
        if( location.creator ) {
            if( stringIsNotEmpty(location.creator.publicName)) {
                if( location.creator.id !== UNKNOWN_USER_ID ) {
                    $('#info_creator_body').show();
                    $('#info_creator').text(location.creator.publicName);
                }
            }
        }

        if( location.inFavourites ) {
            if( location.inFavourites === true ) {
                $('#addLocToFavIcon').removeClass("icon-heart-empty");
                $('#addLocToFavIcon').addClass("icon-heart");
            }
            else {
                $('#addLocToFavIcon').removeClass("icon-heart");
                $('#addLocToFavIcon').addClass("icon-heart-empty");
            }
        }
        else {
            $('#addLocToFavIcon').removeClass("icon-heart");
            $('#addLocToFavIcon').addClass("icon-heart-empty");
        }

        $('#info_type_link').off('click');
        $('#info_type_link').click(function() {
            removeAllMarkers();
            loadAndCreateMarkersByCategory(location.type.id);
        });

        if(location.siteUrl) {
            $('#info_siteUrl').text(location.siteUrl);
            $('#info_siteUrl').attr("href", location.siteUrl);
            $('#info_siteUrl_body').show();
        }
        else {
            $('#info_siteUrl_body').hide();
        }

        if(location.addressDescription) {
            $('#info_addressDescription').text(location.addressDescription);
            $('#info_addressd_body').show();
        }
        else {
            $('#info_addressd_body').hide();
        }

        if(location.averagePrice) {
            $('#info_averagePrice').text(location.averagePrice + " RUB");
            $('#info_averagePrice').show();

            if( location.averagePrice > params.recommendedPrice ) {
                $('#info_averagePrice_label').addClass("label-warning");
                $('#info_averagePrice').addClass("label-warning");
                $('#info_averagePrice_label').removeClass("label-success");
            }
            else {
                $('#info_averagePrice_label').removeClass("label-warning");
                $('#info_averagePrice').removeClass("label-warning");
                $('#info_averagePrice_label').addClass("label-success");
            }
        }
        else {
            $('#info_averagePrice').hide();
        }

        if( location.footype === true ) {
            $('#info_footype').addClass('icon-warning-sign').removeClass('icon-ok-sign');
            //$('#info_footype_text').text('Это тошняк или палатка');
        }
        else if( location.footype === false ) {
            $('#info_footype').removeClass('icon-warning-sign').addClass('icon-ok-sign');
            //$('#info_footype_text').text('Это нормальное кафе');
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
                var url = getLocationFullURL(infoBoxObject.location);
                //$('#shareLocationInSocial').attr("data-url", url);

                $('#plusoShareLoc').children().remove();
                $('#plusoShareLoc').append( getPluso(url));

                window.pluso.start();
            }
        }
    }

    function initInfoBoxButtonsBehavior(infoBoxObject) {
        initScrollInfoBoxBehavior();
        initToggleEditAndViewBehavior(infoBoxObject);
        initVoteButtonsBehavior(infoBoxObject);
        initRouteButtonsBehavior(infoBoxObject);
        initFavButtonBehavior(infoBoxObject);

        $('#infoDetails').off('click');
        $('#infoDetails').click(function() {
            showMarkerSlidePanel(infoBoxObject);
        });
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

    function initFavButtonBehavior(infoBoxObject) {

        $('#addLocToFav').off('click');
        $('#addLocToFav').click(function() {
            favForLocation(infoBoxObject);
        });
    }

    function favForLocation(infoBoxObject) {

        var fav = infoBoxObject.location.inFavourites;
        if( fav ) {
            if( fav === false ) {
                addLocationToFav(infoBoxObject);
            }
            else {
                removeLocationFromFav(infoBoxObject);
            }
        }
        else {
            addLocationToFav(infoBoxObject);
        }
    }

    function addLocationToFav(infoBoxObject) {
        var id = infoBoxObject.location.id;
        $.get(params.realPath+'/api/locations/'+id+'/addprofile', function(result) {

            if( !result ) {
                showNoteTopCenter("Какая-то странная ошибка на сервере..извините", "warning", true);
                return;
            }

            if( result.error === false ) {

                infoBoxObject.location = result.result;
                setInfoBoxContentFromLocation(infoBoxObject);
                initSlidePanelWithData(infoBoxObject);

                $("#mainFavIcon"+infoBoxObject.location.id).removeClass("icon-heart-empty").addClass("icon-heart");


                showNoteTopCenter("Добавлено в избранное!", "success", true);
            }
            else {
                showNoteTopCenter("Какая-то странная ошибка на сервере..извините", "warning", true);
            }
        });
    }

    function removeLocationFromFav(infoBoxObject) {

        var id = infoBoxObject.location.id;
        $.ajax({
            type: "DELETE",
            url: params.realPath+'/api/locations/'+id+'/deleteprofile',
            complete:function(result) {

                if( !result ) {
                    showNoteTopCenter("Какая-то странная ошибка на сервере..извините", "warning", true);
                    return;
                }

                var res = result.responseJSON;
                if( !res ) {
                    showNoteTopCenter("Какая-то странная ошибка на сервере..извините", "warning", true);
                    return;
                }

                if( res.error === false ) {

                    infoBoxObject.location = res.result;;
                    setInfoBoxContentFromLocation(infoBoxObject);
                    initSlidePanelWithData(infoBoxObject);

                    $("#mainFavIcon"+infoBoxObject.location.id).addClass("icon-heart-empty").removeClass("icon-heart");

                    showNoteTopCenter("Убрано из избранного!", "success", true);
                }
                else {
                    showNoteTopCenter("Какая-то странная ошибка на сервере..извините", "warning", true);
                }
            }
        });
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

                    infoBoxObject.location = data.result;
                    setInfoBoxContentFromLocation(infoBoxObject);
                    initSlidePanelWithData(infoBoxObject);

                    showNoteTopCenter("Спасибо!", "success", true);
                    $('#approveLocation').hide();
                    $('#slideApproveLoc').hide();
                }
                else {
                    if( data.errorType === errors.access_denied) {
                        showNoteTopCenter("Извините, но у вас нет прав на это действие...", "warning", true);
                        $('#approveLocation').hide();
                        $('#slideApproveLoc').hide();
                    }
                    else if( data.errorType === errors.already_voted ) {
                        infoBoxObject.location.alreadyVoted = true;
                        showNoteTopCenter("А вы уже голосовали за эту локацию. Давайте без вбросов,ок? =)", "warning", true);
                        $('#approveLocation').hide();
                        $('#slideApproveLoc').hide();
                    }
                    else if( data.errorType === errors.unknown_location ) {
                        infoBoxObject.location.alreadyVoted = true;
                        showNoteTopCenter("Такой локации больше нет в базе. Обновите страничку!", "warning", true);
                        $('#approveLocation').hide();
                        $('#slideApproveLoc').hide();
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

                    infoBoxObject.location = loc;
                    setInfoBoxContentFromLocation(infoBoxObject);
                    initSlidePanelWithData(infoBoxObject);

                    $('#rateActionsDiv').hide(EFFECTS_TIME);
                    $('#slideRatyGroup').hide(EFFECTS_TIME);

                    showNoteTopCenter("Спасибо!", "success", true);
                }
                else {
                    if( data.errorType === errors.access_denied) {
                        showNoteTopCenter("Извините, но у вас нет прав на это действие...", "warning", true);
                    }
                    else if( data.errorType === errors.already_rated ) {
                        infoBoxObject.location.alreadyRated = true;

                        $('#slideRatyAction').raty('readOnly', true);
                        $('#rateActionButtons').raty('readOnly', true);
                        $('#rateActionsDiv').hide(EFFECTS_TIME);
                        $('#slideRatyGroup').hide(EFFECTS_TIME);
                        showNoteTopCenter("А вы уже голосовали за эту локацию. Давайте без вбросов,ок? =)", "warning", true);
                    }
                    else if( data.errorType === errors.unknown_location ) {
                        infoBoxObject.location.alreadyVoted = true;

                        $('#slideRatyAction').raty('readOnly', true);
                        $('#rateActionButtons').raty('readOnly', true);
                        $('#rateActionsDiv').hide(EFFECTS_TIME);
                        $('#slideRatyGroup').hide(EFFECTS_TIME);
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

        $('#hideMarkerButton').off('click');
        $('#hideMarkerButton').click(function() {
            $('#hideAlert').hide();
            $('#hideModal').modal('show');
        });

        $('#deleteMarkerButtonModal').off('click');
        $('#deleteMarkerButtonModal').click( function() {
            deleteMarkerRequest(infoBoxObject);
        });

        $('#hideMarkerButtonModal').off('click');
        $('#hideMarkerButtonModal').click( function() {
            hideMarkerRequest(infoBoxObject);
        });

        $('#closeInfoBox').off('click');
        $('#closeInfoBox').click(function() {
            infoBoxObject.infoBox.hide();
            disableEditMarkerMenu();
            disableDeleteMarkerMenu();
            disableHideMarkerMenu();
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

    function hideMarkerAction(infoBoxObject) {
        $('#hideMarkerButtonModal').off('click');
        $('#hideMarkerButtonModal').click( function() {
            hideMarkerRequest(infoBoxObject);
        });

        $('#hideAlert').hide();
        $('#hideModal').modal('show');
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
                        hideMarkerSlidePanel();
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
                    $('#deleteAlertBeginText').text("Не получилось.");
                    $('#deleteAlertText').text("Извините, произошла какая-то ошибка. Скорее всего у вас нет прав на это действие");
                    $('#deleteMarkerButtonModal').button('reset');
                }
            }
        });
    }

    function hideMarkerRequest(infoBoxObject) {

        $('#hideMarkerButtonModal').button('loading');

        $.ajax({
            type: "DELETE",
            url: params.realPath+'/api/locations/'+infoBoxObject.location.id+'/hide',
            complete: function(data) {

                if( data.responseJSON ) {
                    var res = data.responseJSON;

                    if( res.error == false ) {
                        $('#hideModal').modal('hide');
                        removeMarkerAndInfoBox(infoBoxObject);
                        hideMarkerSlidePanel();
                    }
                    else {
                        if( res.errorType === errors.access_denied ) {
                            $('#hideAlert').show(EFFECTS_TIME);
                            $('#hideAlertBeginText').text("Сожалеем, но..");
                            $('#hideAlertText').text("У вас нет прав на удаление этой локации");
                        }
                        else if( res.errorType === errors.unknown_location ) {
                            $('#hideAlert').show(EFFECTS_TIME);
                            $('#hideAlertText').text("Такой локации не существует!");
                        }
                        else {
                            $('#hideAlert').show(EFFECTS_TIME);
                            $('#hideAlertBeginText').text("Упс..");
                            $('#hideAlertText').text("Извините, произошла какая-то ошибка..");
                        }
                    }

                    $('#hideMarkerButtonModal').button('reset');
                }
            },
            statusCode: {
                400: function(data) {
                    $('#hideAlert').show(EFFECTS_TIME);
                    $('#hideAlertBeginText').text("Не получилось.");
                    $('#hideAlertText').text("Извините, произошла какая-то ошибка. Скорее всего у вас нет прав на это действие");
                    $('#hideMarkerButtonModal').button('reset');
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

    function initAndShowEditForm(infoBoxObject, doNotShowSlidePanel) {

        map.map.setCenter( infoBoxObject.infoBox.getPosition() );
        infoBoxObject.infoBox.hide();

        showEditMarkerFormDiv(infoBoxObject, doNotShowSlidePanel);
        initEditForm(infoBoxObject);

        $('#currentActionForm').data("infoBoxObject", infoBoxObject);
        showCurrentActionForm("Редактирование точки...", cancelNewMarkerAddition, "infoBoxObject");
        initEditFormControlsBehavior(infoBoxObject);
        initMarkerEditBehavior(infoBoxObject);
    }

    function initEditForm(infoBoxObject) {
        clearEditForm();
        $('#editFormAlert').hide();
        initEditFormAddressData(infoBoxObject);
        initEditFormValidation();
        initEditFormWithData(infoBoxObject);
        initEditFormFocus();
        initSwitch();
    }

    function initMarkerEditBehavior(infoBoxObject) {

        var marker = infoBoxObject.marker;
        if( !marker ) {
            return;
        }

        marker.setDraggable(true);

        google.maps.event.addListener(marker, 'dragend', function() {
            var pos = getGeoLocationFromLatLng(marker.position);
            infoBoxObject.location.geoLocation = pos;
            initEditFormLatLngFields(infoBoxObject.location);
            markers.put(marker, infoBoxObject);
        });
    }

    function initMarkerDefaultNonEditBehavior(infoBoxObject) {

        var marker = infoBoxObject.marker;
        if( !marker ) {
            return;
        }

        marker.setDraggable(false);

        google.maps.event.clearListeners(marker, 'dragend');
    }

    function initEditFormControlsBehavior(infoBoxObject) {

        $('#type').off('change');
        $('#type').change(function() {
            setMarkerCurrentIcon(infoBoxObject);
        });

        $('#cancelEdit').off('click');
        $('#cancelEdit').click(function() {
            cancelNewMarkerAddition(infoBoxObject);
        });

        $('#editMarkerForm').off('submit');
        $('#editMarkerForm').submit(function(){
            //submitEditForm(infoBoxObject);
            return false;
        });

        $('#submitEdit').off('click');
        $('#submitEdit').click(function(e) {

            if( $('#submitEdit').data('submitted') === true ) {
                console.log('prevented submit');
                e.preventDefault();
                return;
            }
            else {
                $('#submitEdit').data('submitted', true);
            }

            submitEditForm(infoBoxObject);
        });

        $("#editMarkerForm :input").keypress(function(e) {
            if(e.which === ENTER_KEY) {
                $('#submitEdit').click();
            }
        });
    }

    function setMarkerCurrentIcon(infoBoxObject) {
        var path = getMarkerImagePathForCurrentType();
        infoBoxObject.marker.setIcon(path);
    }

    function cancelNewMarkerAddition(infoBoxObject) {

        hideMarkerSlidePanel(true);

        if( infoBoxObject ) {
            infoBoxObject.infoBox.show();

            enableEditMarkerMenu(infoBoxObject);
            enableDeleteMarkerMenu(infoBoxObject);
            enableHideMarkerMenu(infoBoxObject);

            if( newMarker === true ) {
                removeMarkerAndInfoBox(infoBoxObject);
            }
        }
        else {
            disableEditMarkerMenu();
            disableDeleteMarkerMenu();
            disableHideMarkerMenu();
        }

        if( searchMarker === true ) {
            disableEditMarkerMenu();
            disableDeleteMarkerMenu();
            disableHideMarkerMenu();
        }

        enableCategoryMenu();
        setDefaultMouseBehavior();

        newMarker = false;

        showAllTempMarkers();

        resetSubmitEditButtonBehavior();
    }

    function submitEditForm(infoBoxObject) {
        console.log('submit edit form');
        checkEditFormValidOrNot(infoBoxObject);
    }

    function checkEditFormValidOrNot(infoBoxObject) {
        $('#submitEdit').button('loading');

        var url = $('#siteUrl').val();
        if( url ) {
            var index1 = url.indexOf("http://");
            var index2 = url.indexOf("https://");

            if( index1 < 0 && index2 < 0 ) {
                url = "http://" + url;
                $('#siteUrl').val(url);
            }
        }

        if( $("#editMarkerForm").valid() ) {
            submitEditFormPost(infoBoxObject);
        }
        else {
            var tab1Errors = $('#editFormTab1 .error').filter(function() { return $(this).text() != ""; }).length;
            var tab2Errors = $('#editFormTab2 .error').filter(function() { return $(this).text() != ""; }).length;

            if( tab1Errors === 0 && tab2Errors !== 0 ) {
                $('#editFormTab a:last').tab('show');
            }
            else if( tab1Errors !== 0 && tab2Errors === 0 ) {
                $('#editFormTab a:first').tab('show');
            }

            resetSubmitEditButtonBehavior();
        }
    }

    function submitEditFormPost(infoBoxObject) {

        var id = $('#location-id').val();
        var title = $('#title').val();
        var description = $('#description').val();

        var type = getCurrentType();

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
        var averagePrice = $('#averagePrice').val();
        var siteUrl = $('#siteUrl').val();


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
            },
            averagePrice: averagePrice,
            siteUrl: siteUrl
        };

        param = JSON.stringify(param);

        $.ajax({
            type: "POST",
            url: params.realPath+'/api/locations/add',
            data: JSON.stringify(param),
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

                        markers.put(infoBoxObject.marker, infoBoxObject);
                        markersIds.remove(NEW_MARKER_ID);
                        markersIds.put(newLocation.id, newLocation);

                        initMarkerDefaultNonEditBehavior(infoBoxObject);

                        newMarker = false;
                        searchMarker = false;

                        hideMarkerSlidePanel(true, resetSubmitEditButtonBehavior);
                        removeAllTempMarkers();
                        showInfoBoxForMarker(infoBoxObject);
                        enableEditMarkerMenu(infoBoxObject);
                        enableDeleteMarkerMenu(infoBoxObject);
                        enableHideMarkerMenu(infoBoxObject);

                        if($.fn.loadLocations !== undefined ) {
                            $().loadLocations();
                        }
                    }
                    else {
                        if( res.errorType === errors.access_denied) {
                            $('#editFormAlertBeginText').text("Ошибка!");
                            $('#editFormAlertText').text("Необходимо авторизоваться!");
                            $('#editFormAlert').show(EFFECTS_TIME);

                            $.noty.closeAll();
                            $('#loginModal').modal('show');
                        }
                        else if(res.errorType === errors.overpriced) {
                            $('#editFormAlertBeginText').text("Ошибка!");
                            $('#editFormAlertText').text("Это слишком дорого, извините...");
                            $('#editFormAlert').show(EFFECTS_TIME);
                        }
                        else {
                            //show error message
                            $('#editFormAlert').show(EFFECTS_TIME);
                        }

                        resetSubmitEditButtonBehavior();
                    }
                }
                else {
                    resetSubmitEditButtonBehavior();
                }
            },
            statusCode: {
                400: function(data) {
                    $('#editFormAlert').show(EFFECTS_TIME);
                    $('#editFormAlertBeginText').text("Не получилось.");
                    $('#editFormAlertText').text("Извините, произошла какая-то ошибка. Скорее всего у вас нет прав на это действие");
                    resetSubmitEditButtonBehavior();
                }
            }
        });
    }

    function resetSubmitEditButtonBehavior() {
        $('#submitEdit').button('reset');
        $('#submitEdit').data('submitted', false);
        setDefaultMouseBehavior();
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
            ignore: "",
            rules: {
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
                },
                averagePrice: {
                    required: false,
                    max: params.maxPrice,
                    digits: true
                },
                siteUrl: {
                    required: false,
                    url: true
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

        var location = infoBoxObject.location;

        //main fields
        var id = location.id;
        $('#location-id').val(id);

        if( id === NEW_MARKER_ID ) {
            $('#editFormLegend').text("Создание точки");
        }
        else {
            $('#editFormLegend').text("Редактирование точки");
        }

        $('#title').val( location.title );
        $('#description').val( location.description );
        $('#description').tooltip(
            {
                placement: "top",
                title: "Короткое описание места - что здесь можно дешево поесть. " +
                    "Если это кафе с большим выбором в меню, то - основное дешевое блюдо"
            } );


        $('#addressDescription').val( location.addressDescription );
        $('#addressDescription').tooltip({
            placement: "top",
            title: "Оставьте короткий совет о том, как найти это место"
        });

        $('#averagePrice').numeric();
        $('#averagePrice').val( location.averagePrice );
        $('#averagePrice').typeahead({
            source: ["100", "150", "200", "250", "300", "350"],
            items: 8,
            minlength: 0
        });
        $('#averagePrice').off('keyup change');
        $('#averagePrice').on('keyup change', function() {
            var price = $('#averagePrice').val();
            if( price > params.recommendedPrice && price <= params.maxPrice ) {
                $('#editFormAlertBeginText').text("Ой!");
                $('#editFormAlertText').text("Это неплохая цена, но, все-таки, лучше чтобы кафе было дешевле "+params.recommendedPrice+" рублей");
                $('#editFormAlert').show(EFFECTS_TIME);
            }
            else if( price > params.maxPrice ) {
                $('#editFormAlertBeginText').text("Ой!");
                $('#editFormAlertText').text("Это очень дорого, максимальная цена должна быть меньше "+params.maxPrice+" рублей");
                $('#editFormAlert').show(EFFECTS_TIME);
            }
            else {
                $('#editFormAlert').hide(EFFECTS_TIME);
            }
        });

        $('#siteUrl').off('keyup change');
        if( location.siteUrl ) {
            $('#siteUrl').val(location.siteUrl);
        }
        $('#siteUrl').on('keyup change', function() {
            var url = $('#siteUrl').val();
            if( url ) {

                var index1 = url.indexOf("http://");
                var index2 = url.indexOf("https://");

                if( index1 < 0 && index2 < 0 ) {
                    url = "http://" + url;
                    $('#siteUrl').val(url);
                }
            }
        });

        initDatePicker(location.actualDate);

        //types
        if( location.type.id !== NEW_MARKER_ID ) {
            $("select#type").val(location.type.id);
        }
        else {
            $('select#type option:first-child');
            setMarkerCurrentIcon(infoBoxObject);
        }
        $("select#type").tooltip({
            placement: "top",
            title: "Если кафе предлагает разные блюда, то укажите тип основного дешевого блюда"
        });

        if( location.footype === true ) {
            $('input:radio[name="footypeRadio"]').filter('[value="true"]').attr('checked', true);
        }
        else {
            $('input:radio[name="footypeRadio"]').filter('[value="false"]').attr('checked', true);
        }

        initEditFormLatLngFields(location);

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

    function initEditFormLatLngFields(location) {
        //hidden fields
        $('#latitude').val( location.geoLocation.lat );
        $('#longitude').val( location.geoLocation.lng );
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
        $('#averagePrice').val(null);
        $('#siteUrl').val(null);

        $('#editFormTab a:first').tab('show');
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
        else {
            $('#actualDate').datepicker('setDate', "-0d");
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

    function getInfoBoxWindowMarkup() {

        var res = getInfoBoxHtml();

        return res.html();
    }

    function getPluso(url) {

        var urlString = "";
        if( url ) {
            urlString = url;
        }
        return $('<div/>').addClass("pluso pull-left").attr("id", "shareLocationInSocial")
            .attr("data-options", "small,square,line,horizontal,counter,theme=03")
            .attr("data-services", "facebook,twitter,vkontakte,google,odnoklassniki,moimir")
            .attr("data-background", "transparent")
            .attr("data-url", urlString)
            .attr("data-image", getMainlogoURL() )
            .attr("data-title", "Классное дешевое местечко!")
            .attr("data-description", "Классное дешевое местечко!")
            .attr("data-user", "1262715342");
    }

    function getInfoBoxHtml() {

        var pluso = $('<div/>').addClass("pull-left").attr("id", "plusoShareLoc");

        var rateViewButtons = $('<span/>').attr("id", "rateViewButtons").addClass("spacer3");
        var rateActionButtons = $('<span/>').attr("id", "rateActionButtons").addClass("spacer3");

        var fav = $('<div/>').addClass("pull-right")
            .append(
                $('<a/>').attr("id", "addLocToFav").addClass("favLink").append(
                    $('<i/>').attr("id","addLocToFavIcon").addClass("icon-heart-empty icon-2x")
                )
            );

        var exRes = $('<div/>');

        exRes.append(
            $('<div/>').attr("id", "adminCheckedRibbon").addClass("ribbon-outer").attr("style", "display: none")
                .append(
                    $('<div/>').addClass("ribbon-inner")
                        .append(
                            $('<a/>').attr("href", params.realPath + "/help#moderation").text("ПРОВЕРЕНО!")
                        )
                )
        );

        var res = $('<ul/>').attr('id', 'infoContent').addClass('media-list')
            .append(
                $('<button/>').attr('id','closeInfoBox').attr('type', 'button').addClass('close').text("x")
            )
            .append(
                $('<li/>').addClass('media')
                    .append(
                        $('<a/>').attr("id", "info_type_link").addClass('pull-left img-with-text').attr('href', '#')
                            .append(
                                $('<img/>').attr("id", "info_type_icon").addClass('media-object')
                                    .attr('src', getDefaultIconImagePath() )
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
                                $('<h4/>').addClass('media-heading')
                                    .append(
                                        $('<i/>').attr('id','info_footype')
                                    )
                                    .append(
                                        $('<span/>').attr('id', 'info_title').addClass("spacer3")
                                    )
                            )
                            .append(
                                $('<p/>').attr('id', 'info_description')
                            )
                            .append(
                                $('<div/>').addClass('media')
                                    .append(
                                        $('<div/>').addClass('media-body')
                                            .append(
                                                $('<span/>').addClass('label').attr("id", "info_averagePrice_label").text('Средний чек')
                                            )
                                            .append(
                                                $('<span/>').attr('id', 'info_averagePrice').addClass('spacer5')
                                            )
                                    )
                                    .append(
                                        $('<div/>').addClass('media-body').attr('id','info_addressd_body')
                                            .append(
                                                $('<span/>').addClass('label label-info').text('Описание адреса')
                                            )
                                            .append(
                                                $('<span/>').attr('id','info_addressDescription')
                                                    .addClass('spacer5')
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
                                    .append(
                                        $('<div/>').addClass('media-body').attr("id", "info_creator_body")
                                            .append(
                                                $('<span/>').addClass('label').text('Добавил')
                                            )
                                            .append(
                                                $('<span/>').attr('id', 'info_creator').addClass('spacer5')
                                            )
                                    )
                                    .append(
                                        $('<br/>')
                                    )
                                    .append(
                                        $('<div/>').addClass('media-body').attr("id", "info_address_body")
                                            .append(
                                                $('<address/>').attr('id', 'info_address')
                                            )
                                    )
                                    .append(
                                        $('<div/>').addClass('media-body').attr('id','info_siteUrl_body')
                                            .append(
                                                $('<a/>').attr('id','info_siteUrl').attr("target", "_blank")
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
                                                $('<button/>').addClass('btn btn-small')
                                                    .attr("id", "routeToHere")
                                                    .append(
                                                        $('<i/>').addClass("icon-arrow-right")
                                                    )
                                                    .append(
                                                        $('<span/>').addClass("spacer3").text('Маршрут сюда')
                                                    )
                                            )
                                            .append(
                                                $('<button/>').addClass('btn btn-small')
                                                    .attr("id", "routeFromHere")
                                                    .append(
                                                        $('<i/>').addClass("icon-arrow-left")
                                                    )
                                                    .append(
                                                        $('<span/>').addClass("spacer3").text('Маршрут отсюда')
                                                    )
                                            )
                                            .append(
                                                $('<button/>').addClass('btn btn-small')
                                                    .attr("id", "infoDetails")
                                                    .append(
                                                        $('<i/>').addClass("icon-expand-alt")
                                                    )
                                                    .append(
                                                        $('<span/>').addClass("spacer3").text('Подробнее')
                                                    )
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
                                            .append(
                                                $('<button/>').attr('id', 'hideMarkerButton')
                                                    .addClass('btn btn-small')
                                                    .append(
                                                        $('<i/>').addClass('icon-trash')
                                                    )
                                                    .append(
                                                        $('<span/>').addClass("spacer3").text('Скрыть')
                                                    )
                                            )
                                    )
                            )
                    )
            )
            .append(
                $('<div/>').attr("id", "voteRateDiv").addClass("clearfix")
                    .append(
                        $('<hr/>')
                    )
                    .append (
                        $('<div/>').addClass("form-inline")
                            .append(
                                $('<div/>').addClass("form-inline").addClass("pull-left")
                                    .append(
                                        $('<label/>').text("Средний рейтинг").addClass("pull-left")
                                    )
                                    .append(
                                        rateViewButtons
                                    )
                            )
                            .append(
                                $('<div/>').attr("id", "rateActionsDiv").addClass("form-inline spacer3").addClass("pull-right")
                                    .append(
                                        $('<label/>').text("Оцените локацию")
                                    )
                                    .append(
                                        rateActionButtons
                                    )
                            )
                    )
            )
            .append(
                $('<div/>').attr('id', 'shareDiv').addClass("clearfix form-inline")
                    .append(
                        $('<div/>').addClass("form-inline pull-left")
                            .append(pluso)
                            .append(
                                $('<div/>').addClass("spacer28 pull-left")
                                    .append(
                                        $('<label/>').text("53K").attr("width", "32").attr("id", "voteUpLabel")
                                    )
                                    .append(
                                        $('<img/>').attr('src', getImagePath('up.png')).attr('width', 24)
                                    )
                                    .append(
                                        $('<img/>').attr('src', getImagePath('down.png')).attr('width', 24)
                                            .addClass("spacer10")
                                    )
                                    .append(
                                        $('<label/>').text("53K").attr("width", "32").attr("id", "voteDownLabel")
                                    )
                            )
                    )
                    .append(
                        fav
                    )
            )
            .append(
                $('<div/>').attr('id', 'approveLocation').addClass("clearfix")
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
                                                $('<div/>').addClass('btn-group')
                                                    .append(
                                                        $('<button/>').attr("id", "approveButton")
                                                            .addClass('btn btn-small btn-success')
                                                            .attr('data-loading-text', 'Голосуем...')
                                                            .append(
                                                                $('<img/>').attr('src', getImagePath('up_white.png')).attr('width', 20)
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
                                                                $('<img/>').attr('src', getImagePath('down_white.png')).attr('width', 20)
                                                            )
                                                            .append(
                                                                $('<span/>').addClass("spacer3").text('Точки здесь нет или она не подходит')
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

    function getEditFormMarkup() {

        var form = $('<form/>').attr('id', 'editMarkerForm').addClass('infoWindowInner form-horizontal')
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
                $('<legend/>').attr("id","editFormLegend").text('Создание точки')
            )
            .append(
                getEditFormTabbableMarkup()
            )
            .append(
                $('<div/>').addClass('control-group')
                    .append(
                        $('<div/>').addClass('controls')
                            .append(
                                $('<button/>').attr('id','submitEdit').attr('type', 'button').addClass('btn btn-primary')
                                    .attr('data-loading-text', 'Сохраняем...')
                                    .text('Сохранить точку')
                            )
                            .append(
                                $('<button/>').attr('id','cancelEdit').attr('type', 'button').addClass('btn').text('Отмена')
                            )
                    )
            );

        return form;
    }

    function getEditFormTabbableMarkup() {

        var div = $('<div/>').addClass("tabbable")
            .append(
                $('<ul/>').addClass("nav nav-pills").attr("id", "editFormTab")
                    .append(
                        $('<li/>').addClass("active").append(
                            $('<a/>')
                                .attr("href", "#editFormTab1").attr("data-toggle", "tab").text("Основное")
                        )
                    )
                    .append(
                        $('<li/>').append(
                            $('<a/>').attr("href", "#editFormTab2").attr("data-toggle", "tab").text("Дополнительно")
                        )
                    )
            )
            .append(
                $('<div/>').addClass("tab-content")
                    .append(
                        $('<div/>').addClass("tab-pane active").attr("id", "editFormTab1")
                            .append(
                                getEditFormTab1Markup()
                            )
                    )
                    .append(
                        $('<div/>').addClass("tab-pane").attr("id", "editFormTab2")
                            .append(
                                getEditFormTab2Markup()
                            )
                    )
            );


        return div;
    }

    function getEditFormTab1Markup() {

        var div = $('<div/>')
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
                                $('<input/>').addClass('input-block-level span11')
                                    .attr('id', 'title').attr('name', 'title')
                                    .attr('placeholder', 'Чебуречная Ашота').attr('required', 'true')
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
                        $('<label/>').addClass('control-label').attr('for','description').text('Оставьте совет')
                    )
                    .append(
                        $('<div/>').addClass('controls')
                            .append(
                                $('<textarea/>')//.addClass('input-block-level span4')
                                    .attr('id', 'description').attr('name', 'description')
                                    .attr('placeholder', 'Самые лучшие чебуреки и шаурма в городе!')
                                    .attr('required', 'true')
                                    .attr("rows", 3).addClass("span11")
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
                                $('<input/>').addClass('input-block-level span11')
                                    .attr('id', 'addressDescription').attr('name', 'addressDescription')
                                    .attr('placeholder', 'Выходите из электрички и спускайтесь под мост')
                            )
                    )
            );

        return div;
    }

    function getEditFormTab2Markup() {

        var div = $('<div/>')
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
                                $('<input/>').attr('id', 'actualDate').addClass('input-block-level span11')
                                    .attr('type', 'text').attr('name', 'actualDate')
                            )
                    )
            )
            .append(
                $('<div/>').addClass('control-group')
                    .append(
                        $('<label/>').addClass('control-label').attr('for', 'averagePrice').text('Средний чек')
                    )
                    .append(
                        $('<div/>').addClass('controls')
                            .append(
                                $('<div/>').addClass("input-append")
                                    .append(
                                        $('<input/>').attr('id', 'averagePrice').addClass('span10')
                                            .attr('type', 'text').attr('name', 'averagePrice')
                                    )
                                    .append(
                                        $('<span/>').addClass("add-on").text("RUB")
                                    )
                            )

                    )
            )
            .append(
                $('<div/>').addClass('control-group')
                    .append(
                        $('<label/>').addClass('control-label').attr('for', 'siteUrl').text('Сайт')
                    )
                    .append(
                        $('<div/>').addClass('controls')
                            .append(
                                $('<input/>').attr('id', 'siteUrl').addClass('input-block-level span11')
                                    .attr('type', 'text').attr('name', 'siteUrl').attr("placeholder", "необязательно")
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
                $('<hr/>').attr('hidden', 'true')
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
            );

        return div;
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

            var el = $('<option/>').val(id).text(val);
            res.push(el);
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
            id: NEW_MARKER_ID,
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
            averageRate: 0.0,
            averagePrice: params.recommendedPrice
        };
    }

    function isMarkerAlreadyRendered(location) {

        var loaded = markersIds.get(location.id);
        if( !loaded ) {
            return false;
        }

        return true;
    }

    function getMarkerImagePathForCurrentType() {
        var type = getCurrentType();
        var path = getMarkerImagePath(type);

        return path;
    }

    function getCurrentType() {
        var typeId = $('#type').val();
        var type = getTypeById(typeId);

        return type;
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

    function getLocationFullURL(loc) {

        if( params.realPath.indexOf("localhost") >= 0 ) {
            return "www.cheatfood.com/location/" + loc.id;
        }
        else if( params.realPath.indexOf("127.0.0.1") >= 0 ){
            return "www.cheatfood.com/location/" + loc.id;
        }
        else {
            return params.realPath + "/location/" + loc.id;
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

    function isCurrentUserAdmin() {
        if( !params ) {
            return false;
        }

        if( !params.hasOwnProperty('currentUser')) {
            return false;
        }

        if( !params.currentUser.hasOwnProperty('admin') ) {
            return false;
        }

        var admin = params.currentUser.admin;
        return admin;
    }

    function isUserAdmin(user) {

        if( !user.roles ) {
            return false;
        }

        if( user.roles.length < 1 ) {
            return false;
        }

        for(var i = 0; i < user.roles.length; i++) {
            var role = user.roles[i];
            if( role.name === "ROLE_ADMIN" ) {
                return true;
            }
        }

        return false;
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

    function getDefaultMarkerImagePath() {
        return params.realPath + "/api/images/markers/basic.png";
    }

    function getMarkerImagePath(type) {
        return params.realPath + "/api/images/markers/" + type.code;
    }

    function getDefaultIconImagePath() {
        return params.realPath + "/api/images/types/basic.png";
    }

    function getIconImagePath(type) {
        return params.realPath + "/api/images/types/" + type.code;
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
                key.hasOwnProperty = function(key) {
                    return Object.prototype.hasOwnProperty.call(this, key);
                };
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
} );

