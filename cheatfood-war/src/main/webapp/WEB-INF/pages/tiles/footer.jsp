<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div id="footer" class="navbar navbar-fixed-bottom navbar-inverse">
    <div class="navbar-inner">
        <ul class="nav">
            <li><a href="mailto:info@cheatfood.com" class="navbarLink">Связь и поддержка</a></li>
        </ul>

        <ul class="nav">
            <form id="currentActionForm" class="navbar-form form-inline centerForm" style="display: none;">
                <label class="blackLabel">
                    <h4 id="currentActionText">Добавление новой точки</h4>
                </label>
                <button id="cancelCurrentAction" class="btn">Отмена</button>
            </form>
        </ul>


        <ul class="nav pull-right">
            <li>
                <a href="https://www.facebook.com/cheatfood" target="_blank">
                    <img width="16" src="<s:url value="/resources"/>/img/facebook_small_32.png">
                    <span class="spacer3">Мы на Facebook</span>
                </a>
            </li>
        </ul>
    </div>
</div>