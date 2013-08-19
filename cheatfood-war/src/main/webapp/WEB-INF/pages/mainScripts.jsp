<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>

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
<!-- end of neccessary for every page scripts -->