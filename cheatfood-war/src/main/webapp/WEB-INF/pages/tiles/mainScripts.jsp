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

<link href="/wro/main.css" rel="stylesheet" media="screen" type="text/css"/>

<!--link href="/resources/css/bootstrap.min.css" rel="stylesheet" media="screen" type="text/css"/>
<link href="/resources/css/bootstrap-responsive.min.css" rel="stylesheet" type="text/css"/>
<link href="/resources/css/font-awesome.min.css" rel="stylesheet" type="text/css"/>
<link href="/resources/css/jquery-ui-1.10.3.custom.min.css" rel="stylesheet" type="text/css"/>
<link href="/resources/css/jquery-ui-timepicker-addon-1.4.css" rel="stylesheet" type="text/css"/>
<link href="/resources/css/style.css" rel="stylesheet" type="text/css"/-->

<!--[if IE 7]>
<link rel="stylesheet" href="/resources/css/font-awesome-ie7.min.css">
<![endif]-->


<script src="/wro/main.js" type="text/javascript"></script>

<!--[if lt IE 9]>
<script src="http://css3-mediaqueries-js.googlecode.com/svn/trunk/css3-mediaqueries.js"></script>
<![endif]-->

<label id="realPath" hidden="true" style="display: none;">
    <%= request.getScheme()+"://"
            + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() %>
</label>
<label id="loginLabel" hidden="true" style="display: none;">${login}</label>

<label id="locationLabel" hidden="true" style="display: none;">${location}</label>