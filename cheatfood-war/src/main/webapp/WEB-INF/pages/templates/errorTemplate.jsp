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
    <t:insertAttribute name="mainScripts" />
</head>
<body>

<t:insertAttribute name="simpleHeader" />

<div class="container-fluid errorBody">
    <div class="row-fluid">
        <div class="span6 offset4">
                <h1>Ошибка! Нет такой страницы :(</h1>
            <p>
                <img align="center" src="<s:url value="/resources"/>/img/unicorn.jpg"/>
            </p>
        </div>
    </div>
</div>

<t:insertAttribute name="footer" />

<t:insertAttribute name="googleAnalytics" />
</body>
</html>





