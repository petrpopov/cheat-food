<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div id="loginModal" class="modal hide fade">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h3>Вход</h3>
    </div>
    <div class="modal-body">

        <div id="loginAlert" class="alert" style="display: none" hidden="true" >
            <button type="button" class="close" data-dismiss="alert">&times;</button>
            <strong>Ошибка! </strong><span id="loginError">Ошибка на сервере</span>
        </div>

        <p>Тут можно пришвартовать свой швербот с помощью разных соцсетей:</p>
        <div class="form-inline clearfix">
            <form id="facebookForm" class="pull-left" method="POST" action="<c:url value="/connect/facebook" />">
                <input type="hidden" name="scope" value="email,publish_stream,offline_access" />
                <button class="facebookImage" type="submit"></button>
            </form>
            <form id="foursquareForm" class="pull-left spacer3" method="POST" action="<c:url value="/connect/foursquare" />">
                <button class="foursquareImage" type="submit"></button>
            </form>
            <form id="twitterForm" class="pull-left spacer3" method="POST" action="<c:url value="/connect/twitter" />">
                <button class="twitterImage" type="submit"></button>
            </form>
        </div>

        <hr/>

        <div class="form-horizontal">

            <form class="form-horizontal" method="post" action='' name="loginForm" id="loginForm">
                <div class="control-group">
                    <div class="controls">
                        Или войти с логином и паролем:
                    </div>

                </div>
                <div class="control-group">
                    <div class="controls">
                        <div class="input-prepend">
                            <span class="add-on"><i class="icon-envelope"></i></span>
                            <input type="text" class="span3" name="emailLogin" id="emailLogin" placeholder="электропочта">
                        </div>
                    </div>
                </div>

                <div class="control-group">
                    <div class="controls">
                        <div class="input-prepend">
                            <span class="add-on"><i class="icon-key"></i></span>
                            <input type="password" class="span3" name="passwordLogin" id="passwordLogin" placeholder="пароль">
                        </div>
                    </div>
                </div>

                <div class="control-group">
                    <div class="controls">
                        <button type="submit" id="loginUserSubmit" data-loading-text="Подождите..." class="btn btn-primary">
                            <i class="icon-rocket"></i> YARR!</button>
                        <a id="forgetPasswordLink" href="#">Забыли пароль?</a>
                    </div>
                </div>

                <p style="display: none" hidden="true"><label class="checkbox"><input type="checkbox" value="">
                    Запомнить меня</label></p>
            </form>
        </div>

    </div>
    <div class="modal-footer">
        <span>Нет учетки в соцсетях? Ну, тогда можно и по-старинке:</span>
        <a id="registrationButton" data-toggle="modal" >Регистрация</a>
        <a href="#" class="btn" data-dismiss="modal">Закрыть</a>
    </div>
</div>