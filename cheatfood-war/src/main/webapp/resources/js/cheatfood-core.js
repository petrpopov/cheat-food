$(document).ready(function(){

    "use strict";

    var params = {
        realPath: null,
        types: []
    };
    var authorized = false;
    var COOKIE_NAME = "CHEATFOOD";
    var EFFECTS_TIME = 250;

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
            layout: 'topRight',
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

    function stringIsNotEmpty(str) {

        if( str === "" ) {
            return false;
        }

        if( str.length === 0 ) {
            return false;
        }

        return true;
    }
});