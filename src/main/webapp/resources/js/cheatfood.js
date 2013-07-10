$(document).ready(function(){

    "use strict";

    var map;
    var moscowCenter = {lat: 55.764283, lng: 37.606614};
    var types = [];
    var infoBox;

    var markers = new HashMap();

    var GRID_SIZE = 50;
    var MAX_ZOOM = 15;
    var ENTER_KEY = 13;
    var ZOOM_LEVEL = 10;
    var TYPE_IMAGE_WIDTH = 128;
    var EFFECTS_TIME = 250;
    var DATE_FORMAT = 'yy-mm-dd';
    var DATE_FORMAT_DISPLAY = 'dd.mm.yy';
    var DATE_LANGUAGE = 'ru';


    loadParams();

    function loadParams() {
        $.get('api/types', function(data) {
            types = data;
            init();
        });
    }

    function init() {

        createMap();
        createContextMenuForMap();
        createContextMenuForMarker();
        centerMapToLocation();
        createMarkersForLocations();
        createMarkerEditFormOnMap();
    }

    function createMap() {
        var mcOptions = {gridSize: GRID_SIZE, maxZoom: MAX_ZOOM};

        map = new GMaps({
            div: '#map',
            lat: moscowCenter.lat,
            lng: moscowCenter.lng,
            zoom: ZOOM_LEVEL,
            streetViewControl: true,
            panControl: true,
            mapTypeControl: false,
            markerClusterer: function(map) {
                return new MarkerClusterer(map, [], mcOptions);
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

    function createContextMenuForMap() {
        map.setContextMenu({
            control: 'map',
            options: [
                {
                    title: '<i class="icon-ok"></i><span class="spacer3">Создать точку здесь</span>',
                    name: 'add_location',
                    action: function(e) {
                        createMarkerForContextMenu(e);
                    }
                }
            ]
        });
    }

    function createContextMenuForMarker() {
        map.setContextMenu({
            control: 'marker',
            options: [
                {
                    title: '<i class="icon-list-alt"></i><span class="spacer3">Подробнее</span>',
                    name: 'show_marker',
                    action: function(e) {
                        var infoBoxObject = markers.get(e.marker);
                        initialShowInfoBoxForMarker(e.marker, infoBoxObject);
                    }
                },
                {
                    title: '<i class="icon-edit"></i><span class="spacer3">Редактировать</span>',
                    name: 'edit_marker',
                    action: function(e) {
                        var infoBoxObject = markers.get(e.marker);
                        infoBox.setPosition(e.latLng);
                        initAndShowEditForm(infoBoxObject);
                    }
                },
                {
                    title: '<i class="icon-trash"></i><span class="spacer3">Удалить</span>',
                    name: 'delete_marker',
                    action: function(e) {
                        var infoBoxObject = markers.get(e.marker);
                        $('#deleteMarkerButtonModal').off('click');
                        $('#deleteMarkerButtonModal').click( function() {
                            deleteMarkerRequest(infoBoxObject);
                        });

                        $('#deleteModal').modal('show');
                    }
                }
            ]
        });
    }

    function createMarkerForContextMenu(e) {

        var location = createEmptyLocation();
        location.geoLocation = getGeoLocationFromLatLng(e.latLng);

        var infoBoxObject = {
            infoBox: infoBox,
            location: location
        };
        infoBox.setPosition(e.latLng);

        createMarkerWithInfoBoxForLocation(infoBoxObject);
        initAndShowEditForm(infoBoxObject);
    }

    function createMarkersForLocations() {

        infoBox = createInfoBoxForMarkers();

        $.get("api/locations", function(data) {

            $.each(data, function(n, location) {

                var infoBoxObject = {
                    infoBox: infoBox,
                    location: location
                };

                createMarkerWithInfoBoxForLocation(infoBoxObject);
            });
        });
    }

    function createMarkerEditFormOnMap() {
        var div = $('<div/>').attr('id','editMarkerFormDiv').attr('hidden', 'true')
            .addClass('span6 transparent infoWindow').append(getMarkerEditContent());

        var mapControls = map.map.controls[google.maps.ControlPosition.TOP_RIGHT];
        map.map.controls[google.maps.ControlPosition.TOP_RIGHT].push(div.get(0));
    }

    function createInfoBoxForMarkers(position) {

        var boxText = document.createElement("div");
        boxText.id = 'infoBox';
        boxText.className = "arrow_box infoWindowInner";
        boxText.innerHTML = getMarkerContentFromLocation();

        var infoOptions = {
            content: boxText,
            boxClass: "span5 transparent infoWindow",
            disableAutoPan: false,
            isHidden: true,
            pane: "floatPane",
            infoBoxClearance: new google.maps.Size(50, 50),
            maxWidth: 500,
            pixelOffset: new google.maps.Size(8, -190),
            enableEventPropagation: false
        };
        infoBox = new InfoBox(infoOptions);

        if(position) {
            infoBox.setPosition(position);
        }

        return infoBox;
    }

    function createMarkerWithInfoBoxForLocation(infoBoxObject) {

        var pos = getLatLngFromGeoLocation(infoBoxObject.location.geoLocation);

        var marker = map.addMarker({
            lat: pos.lat(),
            lng: pos.lng(),
            title: location.title,
            click: function() {
                initialShowInfoBoxForMarker(marker, infoBoxObject);
            }
        });

        infoBoxObject.marker = marker;
        markers.put(marker, infoBoxObject);

        return marker;
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
        $('#info_type').text(location.type);
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
        });
    }

    function deleteMarkerRequest(infoBoxObject) {
        $('#deleteMarkerButtonModal').button('loading');

        $.ajax({
            type: "DELETE",
            url: 'api/location/'+infoBoxObject.location.id,
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

        $('#cancelEdit').off('click');
        $('#cancelEdit').click(function() {
            $('#editMarkerFormDiv').fadeOut(EFFECTS_TIME);
            infoBoxObject.infoBox.show();
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
        var type = $('#type').val();
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
            url: 'api/location',
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
        $("#type").val(location.type);
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
                                $('<img/>').addClass('media-object').attr('src', 'resources/img/basic.png')
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
                                                $('<button/>').addClass('btn btn-small')
                                                    .append(
                                                        $('<i/>').addClass('icon-road')
                                                    )
                                                    .append(
                                                        $('<span/>').addClass("spacer3").text('Маршрут')
                                                    )
                                            )
                                            .append(
                                                $('<button/>').addClass('btn btn-small dropdown-toggle')
                                                    .attr('data-toggle','dropdown')
                                                    .append(
                                                        $('<span/>').addClass('caret')
                                                    )
                                            )
                                            .append(
                                                $('<ul/>').addClass('dropdown-menu')
                                                    .append(
                                                        $('<li/>').append(
                                                            $('<a/>').attr('href', '#').text('Сюда')
                                                        )
                                                    )
                                                    .append(
                                                        $('<li/>').append(
                                                            $('<a/>').attr('href', '#').text('Отсюда')
                                                        )
                                                    )
                                            )
                                    )
                                    .append(
                                        $('<div/>').addClass('btn-group pull-right')
                                            .append(
                                                $('<button/>').attr('id', 'editMarkerButton')
                                                    .addClass('btn btn-primary btn-small')
                                                    .append(
                                                        $('<i/>').addClass('icon-edit icon-white')
                                                    )
                                                    .append(
                                                        $('<span/>').addClass("spacer3").text('Редактировать')
                                                    )
                                            )
                                            .append(
                                                $('<button/>').attr('id', 'deleteMarkerButton')
                                                    .addClass('btn btn-danger btn-small')
                                                    .append(
                                                        $('<i/>').addClass('icon-trash icon-white')
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

        var res = $('<form/>').attr('id', 'editMarkerForm').addClass('infoWindowInner form-horizontal scrollable')
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
        for(var i = 0; i < types.length; i++) {
            res.push($('<option/>').text(types[i]));
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
            type: "",
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

