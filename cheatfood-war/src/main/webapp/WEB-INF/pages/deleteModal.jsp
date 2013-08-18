<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div id="deleteModal" class="modal hide fade alert alert-error alert-block">
    <div id="deleteAlert" class="alert" hidden="true" style="display: none;">
        <button type="button" class="close" data-dismiss="alert">&times;</button>
        <strong id="deleteAlertBeginText">Ошибка!</strong><span class="spacer3" id="deleteAlertText">Произошла ошибка во время удаления!</span>
    </div>

    <button type="button" class="close" data-dismiss="modal">&times;</button>
    <p>
        <strong>Удалить?</strong> Вот так просто взять и удалить весь нажитый опыт в области доступной еды и больше не
        делиться ни с кем этим сакральным знанием?
    </p>
    <p>
        <button id="deleteMarkerButtonModal" data-loading-text="Удаляем..." class="btn btn-danger" href="#">Ага</button>
        <button class="btn" data-dismiss="modal">Отмена</button>
    </p>
</div>