<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>

<!DOCTYPE html>
<html lang="en">

<head>
    <t:insertAttribute name="signinScripts" />
</head>

<body>

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
                <li><a href="<s:url value="/manifest"/>">Манифест</a></li>
            </ul>

        </div>
    </div>
</div>

<div class="container">
    <div class="row">
        <div class="content span6 offset3">
            <h1 class="logo">Cheat Food</h1>
            <div class="box">
                <section class="intro">
                    <h4>Клевые места, где можно дешево поесть</h4>
                    <p>Недовольны современными ценами в кафе? Мы тоже.</p>
                </section>
                <section class="form">
                    <form class="form-inline">
                        <fieldset>
                            <a href="#registrationModal" class="btn btn-primary" data-toggle="modal">Регистрация</a>
                            <a href="#loginModal" class="btn" data-toggle="modal">Вход</a>
                        </fieldset>
                        <small class="muted">Бесплатно.</small>
                    </form>
                </section>
            </div>
        </div>
    </div>
</div>

<t:insertAttribute name="loginModal" />

<t:insertAttribute name="registrationModal" />

<t:insertAttribute name="forgetPasswordModal" />

<t:insertAttribute name="footer" />

<t:insertAttribute name="googleAnalytics" />

</body>