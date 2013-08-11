<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form"%>

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

<label id="realPath" hidden="true" style="display: none;">
    <%= request.getScheme()+"://"
            + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() %>
</label>
<label id="loginLabel" hidden="true" style="display: none;">${login}</label>

<label id="locationLabel" hidden="true" style="display: none;">${location}</label>

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
                <li><a href="<s:url value="/users"/>">Помощь</a></li>

                <li class="dropdown">
                    <a id="editMenu" href="#" class="dropdown-toggle" data-toggle="dropdown">
                        Редактировать<b class="caret"></b>
                    </a>
                    <ul class="dropdown-menu">
                        <li>
                                <a id="addMarkerMenu">
                                    <img src="<s:url value="/resources"/>/img/pin.png" width="20"/>
                                    <span class="spacer3">Добавить точку</span>
                                </a>
                        </li>
                        <li>
                            <a id="editMarkerMenu">
                                <i class="icon-edit"></i>
                                <span class="spacer3">Редактировать точку</span>
                            </a>
                        </li>
                        <li>
                            <a id="deleteMarkerMenu">
                                <i class="icon-remove"></i>
                                <span class="spacer3">Удалить точку</span>
                            </a>
                        </li>
                    </ul>
                </li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                        Категории<b class="caret"></b>
                    </a>
                    <ul id="categoryMenu" class="dropdown-menu">
                    </ul>
                </li>
                <li class="dropdown">
                    <a href="#"  class="dropdown-toggle" data-toggle="dropdown">
                        <span id="loginMenuLink">Вход</span>
                        <b class="caret"></b>
                    </a>
                    <ul class="dropdown-menu">
                        <li>
                            <a id="loginLink" href="#">Вход</a>
                        </li>
                        <li>
                            <a id="registrationLink" href="#" hidden="true" style="display: none;">Регистрация</a>
                        </li>
                        <li>
                            <a id="profileLink" href="#">Профиль</a>
                        </li>
                        <li>
                            <a id="logoutLink" href="#">Выход</a>
                        </li>
                    </ul>
                </li>
            </ul>

            <!-- Everything you want hidden at 940px or less, place within here -->
            <div class="nav-collapse collapse pull-right">
                <script type="text/javascript">(function() {
                    if (window.pluso)if (typeof window.pluso.start == "function") return;
                    var d = document, s = d.createElement('script'), g = 'getElementsByTagName';
                    s.type = 'text/javascript'; s.charset='UTF-8'; s.async = true;
                    s.src = ('https:' == window.location.protocol ? 'https' : 'http')  + '://share.pluso.ru/pluso-like.js';
                    var h=d[g]('head')[0] || d[g]('body')[0];
                    h.appendChild(s);
                })();</script>
                <div class="pluso" data-options="medium,square,line,horizontal,counter,theme=03"
                     data-services="facebook,twitter,vkontakte,google,odnoklassniki,moimir,email,print"
                     data-background="transparent" data-url="http://www.cheatfood.com"
                     data-title="Cheat Food" data-description="Клевый сервис для поиска мест, где можно ну очень дешево поесть!"
                     data-user="1262715342"
                     data-image="<s:url value="/resources"/>/img/mainlogo.png"></div>
            </div>

        </div>
    </div>
</div>

<div id="map"></div>

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

<div id="loginModal" class="modal hide fade">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h3>Вход</h3>
    </div>
    <div class="modal-body">
        <p>Тут можно пришвартовать свой швербот с помощью разных соцсетей:</p>
        <div class="form-inline clearfix">
            <form id="foursquareForm" class="pull-left" method="POST" action="<c:url value="/connect/foursquare" />">
                <button class="foursquareImage" type="submit"></button>
            </form>
            <form id="facebookForm" class="pull-left spacer3" method="POST" action="<c:url value="/connect/facebook" />">
                <input type="hidden" name="scope" value="email,publish_stream,offline_access" />
                <button class="facebookImage" type="submit"></button>
            </form>
            <form id="twitterForm" class="pull-left spacer3" method="POST" action="<c:url value="/connect/twitter" />">
                <button class="twitterImage" type="submit"></button>
            </form>
        </div>

        <hr hidden="true" style="display: none;"/>

        <div class="form-horizontal" hidden="true" style="display: none;">
            <div>Или войти с логином и паролем:</div>
            <form class="form-horizontal" method="post" action='' name="login_form">
                <p><input type="text" class="span3" name="eid" id="email" placeholder="электропочта"></p>
                <p><input type="password" class="span3" name="passwd" placeholder="пароль"></p>
                <p><label class="checkbox"><input type="checkbox" value="">
                    Запомнить меня</label></p>
                <p>
                    <button type="submit" class="btn btn-primary">YARR!</button>
                    <a href="#">Забыли пароль?</a>
                </p>
            </form>
        </div>

    </div>
    <div class="modal-footer">
        <span hidden="true" style="display: none;">Нет учетки в соцсетях? Ну, тогда можно и по-старинке:</span>
        <a id="registrationButton" href="#" class="btn btn-primary" hidden="true" style="display: none;">Регистрация</a>
        <a href="#" class="btn" data-dismiss="modal">Отмена</a>
    </div>
</div>

<div id="registrationModal" class="modal hide fade">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h3>Регистрация</h3>
    </div>

    <div class="modal-body">
        <div>
            <div id="registrationAlert" class="alert" hidden="true" style="display: none;">
                <button type="button" class="close" data-dismiss="alert">&times;</button>
                <strong>Ошибка!</strong> Пользователь с таким email уже существует.
            </div>

            <p>Регистрация нового пользователя</p>
            <form id="createUserForm" class="form-horizontal">
                <div class="control-group">
                    <div class="controls">
                        <input type="email" name="email" class="span3" id="emailCreate" placeholder="электропочта" required />
                    </div>
                </div>
                <div class="control-group">
                    <div class="controls">
                        <input type="password" name="password" class="span3" id="passwordCreate" placeholder="пароль" required />
                    </div>
                </div>
                <div class="control-group">
                    <div class="controls">
                        <button id="createUserSubmit" data-loading-text="Подождите..." type="submit" class="btn btn-primary">YARR!</button>
                        <button class="btn" data-dismiss="modal">Отмена</button>
                    </div>
                </div>
            </form>
        </div>

        <hr/>

        <p>Или вы все-таки можете припарковаться тут с помощью соцсетей:</p>
        <div class="form-inline clearfix">
            <form id="facebookFormReg" class="pull-left" method="POST" action="<c:url value="/connect/facebook" />">
                <input type="hidden" name="scope" value="email,publish_stream,offline_access" />
                <button class="facebookImage" type="submit"></button>
            </form>
            <form id="foursquareFormReg" class="pull-left spacer3" method="POST" action="<c:url value="/connect/foursquare" />">
                <button class="foursquareImage" type="submit"></button>
            </form>
            <form id="twitterFormReg" class="pull-left spacer3" method="POST" action="<c:url value="/connect/twitter" />">
                <button class="twitterImage" type="submit"></button>
            </form>
        </div>
    </div>

    <div class="modal-footer">
        <a href="#" class="btn" data-dismiss="modal">Отмена</a>
    </div>
</div>

<div class="navbar navbar-fixed-bottom navbar-inverse">
    <div class="navbar-inner">
        <ul class="nav">
            <li><a href="mailto:popov.petr@gmail.com">Разработка и поддержка - Петр Попов, &copy; 2013</a></li>
        </ul>

        <form id="currentActionForm" class="navbar-form form-inline centerForm" style="display: none;">
            <label class="blackLabel">
                <h4 id="currentActionText">Добавление новой точки</h4>
            </label>
            <button id="cancelCurrentAction" class="btn">Отмена</button>
        </form>
    </div>
</div>

<script>
    (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
        (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
            m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
    })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

    ga('create', 'UA-42863228-1', 'cheatfood.com');
    ga('send', 'pageview');

</script>

</body>
</html>