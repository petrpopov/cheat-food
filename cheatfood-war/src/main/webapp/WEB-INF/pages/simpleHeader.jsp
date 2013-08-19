<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="navbar navbar-inverse">
    <div class="navbar-inner">
        <div class="container">

            <!-- .btn-navbar is used as the toggle for collapsed navbar content -->
            <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </a>

            <a class="rightspacer3" href="<c:url value="/"/>">
                <img src="<s:url value="/resources"/>/img/mainlogo_white.png" width="50" class="pull-left"/>
            </a>

            <!-- Be sure to leave the brand out there if you want it shown -->
            <a class="brand spacer3" href="<c:url value="/"/>">
                cheat food
            </a>

            <ul class="nav">
                <li><a href="<s:url value="/"/>">На главную</a></li>
            </ul>

        </div>
    </div>
</div>