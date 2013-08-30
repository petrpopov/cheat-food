<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<label id="tokenLabel" hidden="true" style="display: none;">${tokenid}</label>

<div class="container">
    <div class="row">
        <div class="span6 offset3">

            <div id="emailAlert" class="alert" style="display: none" hidden="true" >
                <button type="button" class="close" data-dismiss="alert">&times;</button>
                </strong><span id="emailError">Ошибка</span>
            </div>

            <form id="emailConnectForm" class="form-horizontal infoWindow span6" action="/connect/email/${userId}" method="GET">
                <legend>Адрес вашей электронной почты, пожалуйста</legend>

                <input id="oauth_verifier" name="oauth_verifier" value="${oauth_verifier}" style="display: none" hidden="true"/>
                <input id="code" name="code" value="${code}" style="display: none" hidden="true"/>

                <div class="control-group">
                    <div class="controls">
                        <input type="email" name="emailConnect" class="input-large" id="emailConnect" placeholder="электронная почта"/>
                    </div>
                </div>

                <div class="control-group">
                    <div class="controls">
                        <button id="emailConnectSubmit" data-loading-text="Подождите..." type="submit"
                                class="btn btn-primary" name="ok">Продолжить</button>
                        <button id="emailCancelSubmit" type="submit" class="btn" name="cancel">Отмена</button>
                    </div>
                </div>
            </form>

        </div>
    </div>
</div>