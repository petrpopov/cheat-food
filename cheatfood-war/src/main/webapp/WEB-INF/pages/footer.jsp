<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="navbar navbar-fixed-bottom navbar-inverse">
    <div class="navbar-inner">
        <ul class="nav">
            <li><a href="mailto:info@cheatfood.com">Разработка и поддержка - Петр Попов, &copy; 2013</a></li>
        </ul>

        <form id="currentActionForm" class="navbar-form form-inline centerForm" style="display: none;">
            <label class="blackLabel">
                <h4 id="currentActionText">Добавление новой точки</h4>
            </label>
            <button id="cancelCurrentAction" class="btn">Отмена</button>
        </form>
    </div>
</div>