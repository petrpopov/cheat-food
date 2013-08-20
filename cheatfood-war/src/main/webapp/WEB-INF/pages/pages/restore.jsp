<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<label id="tokenLabel" hidden="true" style="display: none;">${tokenid}</label>

<div class="container">
    <div class="row">
        <div class="span6 offset3">

            <div id="restoreAlert" class="alert" style="display: none" hidden="true" >
                <button type="button" class="close" data-dismiss="alert">&times;</button>
                <strong>Ошибка! </strong><span id="restoreError">Ошибка</span>
            </div>

            <form id="restorePasswordForm" class="form-horizontal infoWindow span6">
                <legend>Восстановление пароля</legend>

                <div class="control-group">
                    <div class="controls">
                        <input type="password" name="passwordRestore" class="input-large" id="passwordRestore" placeholder="пароль" required />
                    </div>
                </div>

                <div class="control-group">
                    <div class="controls">
                        <input type="password" name="passwordRestoreCopy" class="input-large" id="passwordRestoreCopy"
                               placeholder="повторите пароль" required />
                    </div>
                </div>

                <div class="control-group">
                    <div class="controls">
                        <button id="restorePasswordSubmit" data-loading-text="Подождите..."
                                type="submit" class="btn btn-primary">Восстановить</button>
                    </div>
                </div>
            </form>

        </div>
    </div>
</div>