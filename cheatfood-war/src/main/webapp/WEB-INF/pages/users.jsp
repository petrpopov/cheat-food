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
    <title>Cheat Food - Users</title>
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
                <li><a href="<s:url value="/manifest"/>">Манифест</a></li>
                <li class="active"><a href="<s:url value="/users"/>">Помощь</a></li>

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

<div class="container-fluid textArticle">
    <div class="row-fluid">
        <div class="span6 offset3 ">

            <h3>Что такое Cheat Food?</h3>
            <p class="textBlock">
                Cheat Food — это база знаний о дешевых заведениях общественного питания, где можно поесть без риска для здоровья. По-настоящему дешевых. И интересных. И вкусных. О местах, про которые мало кто знает, хотя в любом крупном городе их очень много. Правда-правда!
            </p>

            <h3>Какие заведения стоит добавлять на карту?</h3>
            <p class="textBlock">
                Заведения, где вкусно и дешево. Например, вы знаете клевую шашлычную, в которой шампур стоит 100 рублей. Или узбекское кафе, где можно поесть вдвоем на 300 рублей. Или странное кафе с названием «Снежана» и неприметным интерьером, где подают самый вкусный борщ на свете. Разумеется, недорогой.
            </p>

            <h3>Шаурма и прочий фастфуд</h3>
            <p class="textBlock">
                Палатка с шаурмой? Тоже подойдет. Но не стоит добавлять на карту все подряд. Шаурма может быть просто обалденно вкусной, если ее правильно приготовить, а может стать испытанием для желудка, если она сделана из мяса непонятного происхождения. Ваш желудок переносит любую шаурму? Поздравляем. Но подумайте и о других людях. И только при 100-процентной уверенность, что вы знаете классное местечко с классной шаурмой, добавляйте его на карту. Но не забывайте: там должно быть недорого!
            </p>

            <h3>Какие цены подходят?</h3>
            <p class="textBlock">
                Верхняя ценовая граница (в расчете на человека) — 250 рублей за обед. От 250 до 350 — ну только если это очень душевное место. Выше 350 — не надо. Цены указаны по меркам Москвы и Санкт-Петербурга. Если вы живете в другом городе, присылайте свои варианты ограничений, и мы включим их в проект.
            </p>

            <h3>Какой тип заведения указывать?</h3>
            <p class="textBlock">
                Часто бывает, что в каком-то заведении цены так себе, но есть несколько действительно дешевых и вкусных блюд. Например, в меню кафе — много-много всего, но особенно хороши пироги и кофе.  В таком случае вы просто указываете основной тип заведения — «кафе», а в описании упоминаете те самые прекрасные пироги. А можете сразу назвать тип — «пироги», но все равно пренебрегать описанием не стоит. Пара фраз — и людям сразу станет понятно, чего ожидать от этого места.
            </p>

            <h3>Какие точки не стоит добавлять?</h3>
            <p class="textBlock">
                Модные кофейни и кондитерские, которые у всех на слуху. Дорогие рестораны, магазины, продающие «экологические продукты». Это все прекрасные места, но поймите: далеко не каждый житель нашей страны может каждый день оставлять там деньги и полноценно питаться.
            </p>

            <h3>Голоса и рейтинг</h3>
            <p class="textBlock">
                Каждый пользователь проекта может голосовать и оценивать точки. Для этого есть кнопки «подтверждаю точку» и «не подтверждаю». Если вы просто знаете о существовании указанного заведения — первая кнопка для вас. Проверили лично? Понравилось или не очень? Нажимайте «подтверждаю» и оценивайте заведение по 5-бальной шкале. Если же вы точно знаете, что указанное кафе уже закрыто (увы, это реальность, дешевые заведения часто не выдерживают конкуренции), — для вас вторая кнопка. Также вторая кнопка пригодится, если вы считаете, что в кафе по указанному адресу ну совсем неподходящие для проекта цены. Положительные и отрицательные голоса будут учитываться при выводе заведений на карту.
            </p>

            <h3>Модерация</h3>
            <p class="texBottom">
                Никакой паранойи, но мы будем аккуратно следить за новыми заведениями. Какие-то будем проверять лично, тогда на точке появится значок «проверено администрацией». В конце концов, нам же самим интересны новые места! И если какое-либо из них ну совсем никак не будет подходить для проекта, мы удалим его с карты.
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