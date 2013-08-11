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
        <title>Cheat Food - Manifest</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
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

        <!-- cheatfood part -->
        <script src="<s:url value="/resources"/>/js/cheatfood-core.js"></script>
        <!-- end of cheatfood part -->
    </head>

    <body>

    <label id="realPath" hidden="true" style="display: none;">
        <%= request.getScheme()+"://"
                + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() %>
    </label>
    <label id="loginLabel" hidden="true" style="display: none;">${login}</label>

    <div class="navbar navbar-inverse">
        <div class="navbar-inner">
            <div class="container">

                <!-- .btn-navbar is used as the toggle for collapsed navbar content -->
                <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </a>

                <img src="<s:url value="/resources"/>/img/logo.png" width="35" class="pull-left"/>

                <!-- Be sure to leave the brand out there if you want it shown -->
                <a class="brand" href="<c:url value="/"/>">
                    cheat food
                </a>

                <ul class="nav">
                    <li class="active"><a href="<s:url value="/manifest"/>">Манифест</a></li>
                    <li><a href="<s:url value="/users"/>">Помощь</a></li>

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
                    <div class="pluso" data-options="medium,square,line,horizontal,counter,theme=03" data-services="facebook,twitter,vkontakte,google,odnoklassniki,print" data-background="transparent" data-url="http://www.cheatfood.com" data-title="Cheat Food" data-description="Клевый сервис для поиска мест, где можно ну очень дешево поесть!"></div>
                </div>

            </div>
        </div>
    </div>

    <div class="container-fluid textArticle container100">
        <div class="row-fluid">
            <div class="span6 offset3">
                <h1>Манифест</h1>

                <p>
                    Мы глубоко убеждены, что чашка кофе не должна стоить250 рублей, а салат — 450 рублей. И пусть для подобных цен придуман эффектный ход — называть их не высокими, а демократичными — это не те расходы, которые каждый день может позволить себе большинство людей нашей страны.
                </p>
                <p>
                    Но проблема даже не в том, сколько мы зарабатываем, а в общем информационном вакууме. За несколько последних лет в крупных городах сформировался широкий слой заведений общественного питания с уровнем цен, вроде приведенного выше. Популярность этих заведений активно подогревается на соответствующих интернет-порталах и в прессе, а тех кафе, ресторанов, баров, о которых не пишут СМИ, как бы вовсе нет.
                </p>
                <p>
                    На самом деле в каждом городе, даже в дорогой Москве, существует много мест, где можно вкусно и дешево поесть. Столовые с открытым доступом, рестораны среднеазиатской кухни, шашлычные, безымянные кафе... Даже шаурма «из ларька» бывает вкусной. Да, это не самые подходящие места для романтических свиданий, там нет Wi-Fi и модного интерьера, но зато там можно поесть, просто поесть.
                </p>
                <p>
                    Еще одна проблема — менталитет: большинство из нас по-прежнему считает, что питаться надо дома, а кафе и рестораны — это лишняя трата денег. Однако подобное мнение — не более, чем иллюзия, подпитанная незнанием дешевых заведений.
                </p>
                <p>
                    Мы призываем делиться друг с другом информацией о кафе и ресторанах вашего города, где можно действительно дешево и без вреда для здоровья питаться. И почти наверняка это будут не только ларьки с фастфудом, от которого «лишь бы не заболел живот», но и кафе на центральных улицах, где вдруг окажутся низкие цены, действительно низкие. Мы считаем, что счет не должен превышать 250 рублей на человека. Выше 350 рублей — это уже не низкая, а та самая «демократичная» цена. Каждый пользователь может внести свою лепту в проект и тем самым помочь другим людям.
                </p>
            </div>
        </div>
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

    <div class="navbar navbar-fixed-bottom navbar-inverse">
        <div class="navbar-inner">
            <ul class="nav">
                <li><a href="mailto:popov.petr@gmail.com">Разработка и поддержка - Петр Попов, &copy; 2013</a></li>
            </ul>
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