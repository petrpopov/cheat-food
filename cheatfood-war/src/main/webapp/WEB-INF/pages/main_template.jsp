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
    <meta charset="utf-8">
    <title>Cheat Food</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <meta property="og:title" content="Cheat Food"/>
    <meta property="og:url" content="http://www.cheatfood.com"/>
    <meta property="og:image" content="http://www.cheatfood.com/resources/img/mainlogo.png"/>
    <meta property="og:description" content="Секретные места, где можно дешево и вкусно поесть"/>

    <link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico" />

    <!-- Bootstrap -->
    <link href="<s:url value="/resources" />/css/bootstrap.min.css" rel="stylesheet" media="screen" type="text/css"/>
    <link href="<s:url value="/resources" />/css/bootstrap-responsive.min.css" rel="stylesheet" type="text/css"/>
    <link href="<s:url value="/resources" />/css/style.css" rel="stylesheet" type="text/css"/>
    <link href="<s:url value="/resources" />/css/jquery-ui-1.10.3.custom.min.css" rel="stylesheet" type="text/css"/>

    <!-- neccessary for every page scripts -->
    <script src="<s:url value="/resources"/>/js/jquery-1.10.1.min.js"></script>
    <script src="<s:url value="/resources"/>/js/jquery.cookie-1.3.1.js"></script>
    <script src="<s:url value="/resources"/>/js/bootstrap.min.js"></script>

    <script src="<s:url value="/resources"/>/js/jquery.validate-1.11.1.min.js"></script>
    <script src="<s:url value="/resources"/>/js/validation.messages_ru.js"></script>

    <script src="<s:url value="/resources"/>/js/jquery.noty-2.1.0.js"></script>
    <script src="<s:url value="/resources"/>/js/jquery.noty.top-2.1.0.js"></script>
    <script src="<s:url value="/resources"/>/js/jquery.noty.topCenter-2.1.0.js"></script>
    <script src="<s:url value="/resources"/>/js/jquery.noty.topRight-2.1.0.js"></script>
    <script src="<s:url value="/resources"/>/js/jquery.noty.default-theme-2.1.0.js"></script>
    <!-- end of neccessary for every page scripts -->

    <!-- custom scripts -->
    <script src="<s:url value="/resources"/>/js/jquery-ui-1.10.3.custom.min.js"></script>
    <script src="<s:url value="/resources"/>/js/jquery-ui.datepicker.ru.js"></script>
    <script src="<s:url value="/resources"/>/js/jquery.raty-2.5.2.min.js"></script>
    <script src="<s:url value="/resources"/>/js/jquery.numeric-1.3.1.js"></script>

    <script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false&key=AIzaSyBwBoJd0Pv2XqjkfAp25UJaehgWOzmajBc&libraries=places&language=RU"></script>
    <script src="http://api-maps.yandex.ru/2.0-stable/?load=package.standard&lang=ru-RU" type="text/javascript"></script>
    <!--script src="https://www.google.com/jsapi?sensor=true&key=AIzaSyBwBoJd0Pv2XqjkfAp25UJaehgWOzmajBc&libraries=places&language=RU"></script-->

    <script type="text/javascript" src="<s:url value="/resources"/>/js/markerclusterer-2.0.9.min.js"></script>
    <script type="text/javascript" src="<s:url value="/resources"/>/js/infobox-1.1.9.min.js"></script>

    <script src="<s:url value="/resources"/>/js/gmaps-0.4.4.js"></script>
    <!-- end of custom scripts -->

    <!-- cheatfood part -->
    <script src="<s:url value="/resources"/>/js/cheatfood-main.js"></script>
    <!-- end of cheatfood part -->
</head>
<body>

    <t:insertAttribute name="header" />


    <t:insertAttribute name="content" />


    <t:insertAttribute name="deleteModal" />

    <t:insertAttribute name="loginModal" />

    <t:insertAttribute name="registrationModal" />

    <t:insertAttribute name="forgetPasswordModal" />

    <t:insertAttribute name="footer" />

    <t:insertAttribute name="googleAnalytics" />
</body>
</html>





