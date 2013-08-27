$(function() {

    "use strict";

    loadParams(createRegistrationAuthActions);
});

var params = {
    realPath: null,
    types: []
};

var EFFECTS_TIME = 250;

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
    user_already_exists: "user_already_exists",
    email_is_empty: "email_is_empty",
    no_user_with_such_email: "no_user_with_such_email",
    overpriced: "overpriced",
    other: "other"
};

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

        if( callback ) {
            callback();
        }
    });
}

function createRegistrationAuthActions() {

    console.log(params.realPath);

    $('#logoutLink').off('click');
    $('#logoutLink').click(function() {
        sessionStorage.removeItem('showHello');
        sessionStorage.removeItem('showFirstTime');
    });

    $('#loginLink').off('click');
    $('#loginLink').click(function() {
        clearLoginUserForm();
        $.noty.closeAll();
        $('#loginModal').modal('show');
    });

    $('#registrationLink').off('click');
    $('#registrationLink').click(function() {
        clearCreateUserForm();
        $.noty.closeAll();
        $('#registrationModal').modal('show');
    });

    $('#registrationButton').click(function() {
        clearCreateUserForm();
        $('#loginModal').modal('hide');
        $('#registrationModal').modal('show');
    });

    $('#forgetPasswordLink').off('click');
    $('#forgetPasswordLink').click(function() {
        clearForgetPasswordForm();
        $.noty.closeAll();
        $('#loginModal').modal('hide');
        $('#registrationModal').modal('hide');
        $('#forgetPasswordModal').modal('show');
    });

    $('#restorePasswordForm').off('submit');
    $('#restorePasswordForm').submit(function() {
        return false;
    });
    $('#restorePasswordForm').ready(function() {
        $('#passwordRestore').focus();
    });
    $('#restorePasswordSubmit').off('click');
    $('#restorePasswordSubmit').click(function() {
        checkRestorePasswordFormValidOrNot();
    });

    initRestorePasswordFormValidation();
    initForgetPasswordFormValidation();
    initLoginUserFormValidation();
    initCreateUserFormValidation();

    $('#forgetAlert').hide();
    $('#registrationAlert').hide();
    $('#loginAlert').hide();

    $('#createUserForm').ready(function() {
        $('#emailCreate').focus();
    });

    $('#createUserForm').off('submit');
    $('#createUserForm').submit(function() {
        return false;
    });

    $('#createUserSubmit').off('click');
    $('#createUserSubmit').click( function(e) {
        if( $('#createUserForm').data('submitted') === true ) {
            console.log('prevented submit');
            e.preventDefault();
            return;
        }
        else {
            $('#createUserForm').data('submitted', true);
        }

        checkCreateUserFormValidOrNot();
    });


    //login
    $('#loginForm').ready( function() {
        $('#emailLogin').focus();
    });
    $('#loginForm').off('submit');
    $('#loginForm').submit(function() {
        return false;
    });

    $('#loginUserSubmit').off('click');
    $('#loginUserSubmit').click(function(e) {
        if( $('#loginForm').data('submitted') === true ) {
            console.log('prevented submit');
            e.preventDefault();
            return;
        }
        else {
            $('#loginForm').data('submitted', true);
        }

        checkLoginFormValidOrNot();
    });

    //forget password
    $('#forgetPasswordForm').ready( function() {
        $('#emailForget').focus();
    });
    $('#forgetPasswordForm').off('submit');
    $('#forgetPasswordForm').submit(function() {
        return false;
    });

    $('#forgetPasswordSubmit').off('click');
    $('#forgetPasswordSubmit').click(function(e) {
        if( $('#forgetPasswordForm').data('submitted') === true ) {
            console.log('prevented submit');
            e.preventDefault();
            return;
        }
        else {
            $('#forgetPasswordForm').data('submitted', true);
        }

        checkForgetPasswordFormValidOrNot();
    });
}

function checkForgetPasswordFormValidOrNot() {
    $('#forgetPasswordSubmit').button('loading');
    $('#forgetAlert').hide();

    if( $("#forgetPasswordForm").valid() ) {
        submitForgetPasswordUserForm();
    }
    else {
        resetForgetPasswordButtonSubmitBehavior();
    }
}

function checkLoginFormValidOrNot() {
    $('#loginUserSubmit').button('loading');
    $('#loginAlert').hide();

    if( $("#loginForm").valid() ) {
        submitLoginUserForm();
    }
    else {
        resetLoginUserButtonSubmitBehavior();
    }
}

function checkRestorePasswordFormValidOrNot() {
    $('#restorePasswordSubmit').button('loading');

    if( $("#restorePasswordForm").valid() ) {
        submitRestorePasswordForm();
    }
    else {
        $('#restorePasswordSubmit').button('reset');
    }
}

function checkCreateUserFormValidOrNot() {

    $('#createUserSubmit').button('loading');
    $('#registrationAlert').hide();

    if( $("#createUserForm").valid() ) {
        submitCreateUserForm();
    }
    else {
        resetCreateUserSubmitButtonBehavior();
    }
}

function submitRestorePasswordForm() {

    var formParams = {
        password: $('#passwordRestore').val().trim(),
        passwordCopy: $('#passwordRestoreCopy').val().trim(),
        token: params.token
    };

    $.ajax({
        type: "POST",
        url: params.realPath + '/api/users/restore',
        data: JSON.stringify(formParams),
        contentType: 'application/json',
        mimeType: 'application/json',
        dataType: 'json',
        success: function(data) {
        },
        complete: function(data) {

            if( data.responseJSON ) {
                var result = JSON.parse(data.responseText);

                if( result.error === false ) {
                    window.location.replace(params.realPath);
                }
                else {
                    if( result.errorType === errors.password_mismatch ) {
                        showRestoreError("Введенные пароли не совпадают!");
                    }
                    else if( result.errorType === errors.wrong_token ) {
                        showRestoreError("Вы пытаетесь восстановить чужой пароль?");
                    }
                    else if( result.errorType === errors.login_error ) {
                        showRestoreError("Извините, произошла ошибка.");
                    }
                }
            }
            else {
                showRestoreError("Извините, произошла ошибка.");
            }
        },
        statusCode: {
            400: function(data) {
                showRestoreError("Извините, произошла ошибка.");
            }
        }
    });
}

function submitForgetPasswordUserForm() {

    var email = $('#emailForget').val().trim();

    $.ajax({
        type: 'POST',
        url: params.realPath + '/api/users/forget',
        data: email,
        contentType: 'application/json',
        mimeType: 'application/json',
        dataType: 'json',
        success: function(data) {
        },
        complete: function(data) {
            if( data.responseJSON ) {
                var result = JSON.parse(data.responseText);

                if( result.error === false ) {
                    //show message about email
                    $('#forgetPasswordForm').fadeOut(EFFECTS_TIME, function() {
                        $('#forgetEmailLinkInfo').fadeIn(EFFECTS_TIME, function() {
                            $('#forgetPasswordCancel').text("Закрыть");
                            resetForgetPasswordButtonSubmitBehavior();
                        });
                    });
                }
                else {
                    if( result.errorType === errors.email_is_empty ) {
                        showForgetPasswordMessage("Введите что-нибудь в поле email.")
                    }
                    else if( result.errorType === errors.no_user_with_such_email ) {
                        showForgetPasswordMessage("Нет пользователя в системе с таким email!")
                    }
                }
            }
            else {
                showForgetPasswordMessage("Извините, у нас на сервере какая-то ошибка :(");
            }
        },
        statusCode: {
            400: function(data) {
                showForgetPasswordMessage("Извините, у нас на сервере какая-то ошибка :(");
            }
        }
    });
}

function submitLoginUserForm() {
    var formParams = {
        email: $('#emailLogin').val().trim(),
        password: $('#passwordLogin').val().trim()
    };

    $.ajax({
        type: 'POST',
        url: params.realPath + '/api/users/login',
        data: JSON.stringify(formParams),
        contentType: 'application/json',
        mimeType: 'application/json',
        dataType: 'json',
        success: function(data) {
        },
        complete: function(data) {
            if( data.responseJSON ) {
                var result = JSON.parse(data.responseText);

                if( result.error === false ) {
                    resetLoginUserButtonSubmitBehavior();
                    window.location.replace(params.realPath);
                    /*$('#loginModal').modal('hide');
                     checkCookies();
                     resetLoginUserButtonSubmitBehavior();*/
                }
                else {
                    if( result.errorType === errors.no_such_user ) {
                        showLoginError("Пользователя с таким email не существует");
                    }
                    else if( result.errorType === errors.no_password_data ) {
                        var text = "Вы заходили сюда, используя соц.сети? Пройдите ";
                        text += "<a href=\"#\" id=\"registrationErrorButton\">регистрацию</a>";
                        text += ", чтобы установить пароль.";

                        showLoginError(text, function() {
                            $('#registrationErrorButton').click(function() {
                                clearCreateUserForm();
                                $('#loginModal').modal('hide');
                                $('#registrationModal').modal('show');
                            });
                        });
                    }
                    else if( result.errorType === errors.wrong_password ) {
                        showLoginError("Неправильный пароль!");
                    }
                    else {
                        showLoginError("Ошибка авторизации...");
                    }
                }
            }
            else {
                showLoginError("Извините, у нас на сервере какая-то ошибка :(");
            }
        },
        statusCode: {
            400: function(data) {
                showLoginError("Извините, у нас на сервере какая-то ошибка :(");
            }
        }
    });
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

            if( data.responseJSON ) {
                var result = JSON.parse(data.responseText);

                if( result.error === false ) {
                    /*$('#registrationModal').modal('hide');
                     checkCookies();
                     resetCreateUserSubmitButtonBehavior();*/
                    //resetCreateUserSubmitButtonBehavior();
                    window.location.replace(params.realPath);
                }
                else {
                    if( result.errorType === errors.user_already_exists ) {
                        showRegistrationError("Пользователь с таким email уже существует.");
                    }
                    else {
                        showRegistrationError("Извините, у нас на сервере какая-то ошибка :(");
                    }
                }
            }
            else {
                showRegistrationError("Извините, у нас на сервере какая-то ошибка :(");
            }

        },
        statusCode: {
            400: function(data) {
                showRegistrationError("Извините, у нас на сервере какая-то ошибка :(");
            }
        }
    });
}

function showForgetPasswordMessage(text) {
    $('#forgetAlert').fadeIn(EFFECTS_TIME, function() {
        $('#forgetError').text(text);
        resetForgetPasswordButtonSubmitBehavior();
    });
}

function showLoginError(text, callback) {
    $('#loginAlert').fadeIn(EFFECTS_TIME, function() {
        $('#loginError').html(text);
        if( callback ) {
            callback();
        }
        resetLoginUserButtonSubmitBehavior();
    });
}

function showRegistrationError(text) {
    $('#registrationAlert').fadeIn(EFFECTS_TIME, function() {
        $('#registrationError').text(text);
        resetCreateUserSubmitButtonBehavior();
    });
}

function showRestoreError(text) {
    $('#restoreAlert').fadeIn(EFFECTS_TIME, function() {
        $('#restoreError').text(text);
        resetRestorePasswordButtonBehavior();
    });
}

function resetRestorePasswordButtonBehavior() {
    $('#restorePasswordSubmit').button('reset');
}

function resetForgetPasswordButtonSubmitBehavior() {
    $('#forgetPasswordForm').data('submitted', false);
    $('#forgetPasswordSubmit').button('reset');
}

function resetLoginUserButtonSubmitBehavior() {
    $('#loginForm').data('submitted', false);
    $('#loginUserSubmit').button('reset');
}

function resetCreateUserSubmitButtonBehavior() {
    $('#createUserForm').data('submitted', false);
    $('#createUserSubmit').button('reset');
}

function initRestorePasswordFormValidation() {
    $("#restorePasswordForm").validate({
        rules : {
            passwordRestore: {
                required: true,
                minlength: 3,
                maxlength: 50
            },
            passwordRestoreCopy: {
                required: true,
                equalTo: "#passwordRestore",
                minlength: 3,
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

function initForgetPasswordFormValidation() {
    $("#forgetPasswordForm").validate({
        rules : {
            emailForget: {
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

function initLoginUserFormValidation() {
    $("#loginForm").validate({
        rules : {
            emailLogin: {
                required: true,
                email: true
            },
            passwordLogin: {
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

function clearForgetPasswordForm() {
    $('#emailForget').val(null);
    $('#forgetAlert').hide();
}

function clearLoginUserForm() {
    $('#emailLogin').val(null);
    $('#passwordLogin').val(null);
    $('#loginAlert').hide();
}

function clearCreateUserForm() {
    $('#emailCreate').val(null);
    $('#passwordCreate').val(null);
    $('#registrationAlert').hide();
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
        timeout: 3000,
        maxVisible: 1,
        dismissQueue: true
    });
}

