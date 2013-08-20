<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div id="hideModal" class="modal hide fade alert alert-error alert-block">
    <div id="hideAlert" class="alert" hidden="true" style="display: none;">
        <button type="button" class="close" data-dismiss="alert">&times;</button>
        <strong id="hideAlertBeginText">Ошибка!</strong><span class="spacer3" id="hideAlertText">Произошла ошибка во время скрытия локации!</span>
    </div>

    <button type="button" class="close" data-dismiss="modal">&times;</button>
    <p>
        <strong>Скрыть локацию?</strong>
    </p>
    <p>
        <button id="hideMarkerButtonModal" data-loading-text="Скрываем..." class="btn btn-danger" href="#">Да</button>
        <button class="btn" data-dismiss="modal">Отмена</button>
    </p>
</div>