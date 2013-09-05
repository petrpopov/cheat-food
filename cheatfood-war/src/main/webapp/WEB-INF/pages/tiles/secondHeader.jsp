<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div id="secondNavbar" class="navbar navbar-inverse">
    <div class="navbar-inner">
        <div class="container">
            <ul class="nav">
                <li>
                    <a href="#">Всего локаций: <span id="locationsCountLabel"></span></a>
                </li>
                <li>
                    <a href="#">В видимом регионе: <span id="locationsLocalCountLabel"></span></a>
                </li>
                <li>
                    <a href="#">Новых: <span id="locationsNewCountLabel"></span></a>
                </li>
            </ul>
        </div>
    </div>
</div>