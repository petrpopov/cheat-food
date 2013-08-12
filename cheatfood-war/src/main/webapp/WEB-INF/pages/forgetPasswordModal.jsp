<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


<div id="forgetPasswordModal" class="modal hide fade">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h3>Восстановление пароля</h3>
    </div>

    <div class="modal-body">
        <div>
            <div id="forgetAlert" class="alert" >
                <button type="button" class="close" data-dismiss="alert">&times;</button>
                <strong>Ошибка! </strong><span id="forgetError">Нет такого пользователя</span>
            </div>

            <p id="forgetEmailLinkInfo" style="display: none" hidden="true">
                На ваш электронный адрес была отправлена секретная ссылка для восстановления пароля. Проверьте ваш почтовый ящик,
                обычно письмо приходит в течении нескольких секунд.
            </p>

            <form id="forgetPasswordForm" class="form-horizontal">
                <div class="control-group">
                    <div class="controls">
                        <input type="email" name="email" class="span3" id="emailForget" placeholder="электропочта" required />
                    </div>
                </div>

                    <div class="control-group">
                        <div class="controls">
                            <button id="forgetPasswordSubmit" data-loading-text="Подождите..." type="submit" class="btn btn-primary">Напомнить</button>
                            <button class="btn" data-dismiss="modal">
                                <span id="forgetPasswordCancel">Отмена</span>
                            </button>
                        </div>
                    </div>
            </form>
        </div>
    </div>

    <div class="modal-footer">
        <a href="#" class="btn" data-dismiss="modal">Отмена</a>
    </div>
</div>