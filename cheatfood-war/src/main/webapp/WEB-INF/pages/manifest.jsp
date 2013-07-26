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
                    <li><a href="#">Пользователям</a></li>

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

    <div class="container-fluid infoWindow container100">
        <div class="row-fluid">
            <div class="span6 offset3 textArticle">
                <h1>Манифест</h1>

                <p>
                    Мы глубоко убеждены в том, что чашка кофе за 250 рублей или салатик за 450 - это не те расходы, которые может позволить себе на каждый день большинство людей нашей страны. И проблема даже не в том, сколько вы зарабатываете, можете ли потратить на глоток эспрессо 150 рублей и на каком виде транспорта передвигаетесь по городу, на велосипеде или на майбахе. И мы прекрасно понимаем, что есть инфляция, дорогая арендная плата, курсы валют, проблемы с кредитованием малого бизнеса, налоги.. Настоящая же проблема в общем информационном вакууме и нашем менталитете.
                </p>
                <p>
                    Сегодня в крупных городах, особенно в Москве и Санкт-Петербурге, сложился слой кафе, баров и других общественных заведений с весьма определенным ценовым курсом, вроде приведенного выше. В последнее время к ним активно стали присоединяться различные команды молодых людей, активно продвигающие новую, вкусную, полезную еду. Это прекрасно! Мы, также как и вы, обожаем самодельные бургеры или самодельный свежий лимонад. Но.. Популярность этих заведений активно подогревается на соответствующих интернет-порталах и в прессе и при всей доступности общего информационного поля каждому человеку возникает обратный эффект - те места, которые не освящаются в прессе или в интернете, постепенно забываются и вытесняются из нашей памяти, что неудивительно при жизни в ритме современного города. А для подобных цен придуман эффектный ход - называть их демократичными, не вкладывая особого смысла в это слово. А что же с другими заведениями? С принципиально “другими”?
                </p>
                <p>
                    В каждом городе, даже в дорогой Москве, существует полно мест, где можно вкусно и дешево поесть. Столовые с открытым доступом, среднеазиатское заведение, где подают божественный лагман, шашлычные, безымянные кафе... Даже шаурма “из ларька” бывает вкусной. Да, это не самые подходящие места для романтических свиданий с девушкой, там нет Wi-Fi или модного интерьера, но зато там можно поесть. Просто поесть. То, что нам необходимо делать каждый день на протяжении всей нашей жизни. То, на что у многих уходит солидная часть дохода. И здесь та изначальная цель любого кафе - возможность для приема - пищи выполняется “на ура!” и это принципиальная особенность подобных мест. Каждый из нас знает хотя бы парочку таких “кафе” в своем районе или городе, но постепенно мы забываем про подобные заведения, начинаем проезжать их мимо на машине, морщить нос при виде вывесок... Общая проблема подпитывается нашим менталитетом: мы по-прежнему, в большинстве своем, считаем, что питаться надо дома, а кафе и рестораны - это лишняя трата денег. Даже учитывая общее положение ресторанного бизнеса в страна, подобное мышление - не более, чем иллюзия нашего разума. А информационный ваккуум дешевых мест только лишь подпитывает эту иллюзию.
                </p>
                <p>
                    Мы призываем делиться подобной информацией с людьми и рассказывать про интересные точки в вашем городе, где можно действительно дешево и без вреда для здоровья питаться. Такой точкой может быть не только ларек с фастфудом, от которого потом не болит живот, но и кафе в центре города, где вдруг низкие цены. Действительно низкие. Мы считаем, что верхний ценовой рубеж - 300 рублей на человека. Выше трехсот рублей - это уже не низкая цена, а демократичная. Каждый пользователь может внести свою лепту в проект, и тем самым поможет немного и другим людям.
                </p>
            </div>
        </div>
    </div>

    </body>
</html>