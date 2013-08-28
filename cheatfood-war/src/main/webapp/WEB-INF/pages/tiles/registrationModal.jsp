<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div id="registrationModal" class="modal hide fade">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h3>Регистрация</h3>
    </div>

    <div class="modal-body">
        <div>
            <div id="registrationAlert" class="alert" class="alert" style="display: none" hidden="true" >
                <button type="button" class="close" data-dismiss="alert">&times;</button>
                <strong>Ошибка! </strong><span id="registrationError">Пользователь с таким email уже существует.</span>
            </div>

            <p>Регистрация нового пользователя</p>
            <form id="createUserForm" class="form-horizontal">
                <div class="control-group">
                    <div class="controls">
                        <input type="email" name="email" class="span3" id="emailCreate" placeholder="электропочта" required />
                    </div>
                </div>
                <div class="control-group">
                    <div class="controls">
                        <input type="password" name="password" class="span3" id="passwordCreate" placeholder="пароль" required />
                    </div>
                </div>
                <div class="control-group">
                    <div class="controls">
                        <input type="password" name="passwordRepeat" class="span3" id="passwordRepeat" placeholder="повторите пароль" required />
                    </div>
                </div>
                <div class="control-group">
                    <div class="controls">
                        <button id="createUserSubmit" data-loading-text="Подождите..." type="submit" class="btn btn-primary">YARR!</button>
                        <button class="btn" data-dismiss="modal">Отмена</button>
                    </div>
                </div>
            </form>
        </div>

        <hr/>

        <p>Или вы все-таки можете припарковаться тут с помощью соцсетей:</p>
        <div class="form-inline clearfix">
            <form id="facebookFormReg" class="pull-left" method="POST" action="<c:url value="/connect/facebook" />">
                <input type="hidden" name="scope" value="email,publish_stream,offline_access" />
                <button class="facebookImage" type="submit"></button>
            </form>
            <form id="foursquareFormReg" class="pull-left spacer3" method="POST" action="<c:url value="/connect/foursquare" />">
                <button class="foursquareImage" type="submit"></button>
            </form>
            <form id="twitterFormReg" class="pull-left spacer3" method="POST" action="<c:url value="/connect/twitter" />">
                <button class="twitterImage" type="submit"></button>
            </form>
        </div>
    </div>

    <div class="modal-footer">
        <a href="#" class="btn" data-dismiss="modal">Закрыть</a>
    </div>
</div>