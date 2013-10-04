<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>

<div class="container-fluid">
    <div class="row-fluid">
        <div id="slidepanel" class="span2 transparent infoWindow">

            <button id="closeSlidePanel" type="button" class="close">&times;</button>

            <div id="slidepanelEditContent">

            </div>

            <div id="slidepanelInfoContent">

                <div class="infoWindowInner">
                    <div id="slideAdminChecked" class="ribbon-outer" style="display: none">
                        <div class="ribbon-inner">
                            <a href="/help#moderation">ПРОВЕРЕНО!</a>
                        </div>
                    </div>

                    <ul class="media-list">

                        <li class="media">
                            <a class="pull-left" href="#">
                                <img id="slideImage" class="media-object" width="192">
                                <p align="center">
                                    <span id="slideType" class="label label-info"></span>
                                </p>
                            </a>

                            <div class="media-body">
                                <h4 class="media-heading">
                                    <i id="slideFootype" class="icon-warning-sign"></i>
                                    <span id="slideTitle" class="spacer3"></span>
                                </h4>
                                <p id="slideDescription"></p>

                                <div class="media">
                                    <!--div class="media-body">

                                        <span id="slideFootypeText" class="spacer5">Это тошняк или палатка</span>
                                    </div-->

                                    <div class="media-body">
                                        <span id="slideAveragePriceInfo" class="label">Средний чек</span>
                                        <span id="slideAveragePrice" class="spacer5">565 RUB</span>
                                    </div>

                                    <div class="media-body" id="slideAddressDescrBody">
                                        <span class="label label-info">Описание адреса</span>
                                        <span id="slideAddressDescription" class="spacer5">Под мостом, под электричкой</span>
                                    </div>

                                    <div class="media-body">
                                        <span class="label">Дата проверки</span>
                                        <span id="slideActualDate" class="spacer5">27/08/2013</span>
                                    </div>

                                    <div class="media-body" id="slideCreatorBody">
                                        <span class="label">Добавил</span>
                                        <span id="slideCreator" class="spacer5">565 RUB</span>
                                    </div>

                                    <br/>

                                    <div class="media-body" id="slideAddressBody">
                                        <address id="slideAddress">
                                            795 Folsom Ave, Suite 600<br>
                                            San Francisco, CA 94107<br>
                                        </address>
                                    </div>

                                    <div class="media-body" id="slideSiteUrlBody">
                                        <a id="slideSiteUrl" target="_blank">565 RUB</a>
                                    </div>

                                </div>


                            </div>
                        </li>

                        <div class="media-body">
                            <div class="media">
                                <div class="btn-toolbar">
                                    <div class="btn-group">
                                        <button id="slideFromHere" class="btn btn-small">
                                            <i class="icon-arrow-right"></i> Маршрут сюда</button>
                                        <button id="slideToHere" class="btn btn-small">
                                            <i class="icon-arrow-left"></i> Маршрут отсюда</button>
                                    </div>

                                    <div id="slideEditGroup" class="btn-group pull-right">
                                        <button id="slideEdit" class="btn btn-small"><i class="icon-edit"></i> Редактировать</button>
                                        <button id="slideDelete" class="btn btn-small"><i class="icon-trash"></i> Удалить</button>
                                        <button id="slideHide" class="btn btn-small"><i class="icon-trash"></i> Скрыть</button>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <hr/>

                        <div class="media-body">
                            <div class="media">
                                <div class="btn-toolbar">
                                    <div class="btn-group">
                                        <span>Средний рейтинг</span><span id="slideRatyView" class="spacer3"></span>
                                    </div>

                                    <div id="slideRatyGroup" class="btn-group pull-right">
                                        <span>Оцените локацию</span><span id="slideRatyAction" class="spacer3"></span>
                                    </div>
                                </div>

                                <div class="btn-toolbar">
                                    <div class="btn-group">
                                        <div id="slidePluso"></div>
                                    </div>

                                    <div class="btn-group pull-right">
                                        <a id="slideFav" class="favLink" href="#"><i id="slideFavIcon" class="icon-heart-empty icon-2x"></i></a>
                                    </div>
                                </div>
                            </div>
                        </div>



                        <div id="slideApproveLoc" class="media-body">
                            <hr/>
                            <div class="media">
                                <div class="btn-toolbar">
                                    <div class="btn-group">
                                        <button id="slideApprove" class="btn btn-small btn-success" data-loading-text="Голосуем...">
                                            <img src="/resources/img/up_white.png" width="20"/>
                                            <span> Подтверждаю точку</span>
                                        </button>
                                    </div>

                                    <div class="btn-group pull-right">
                                        <button id="slideNotApprove" class="btn btn-small btn-warning" data-loading-text="Голосуем...">
                                            <img src="/resources/img/down_white.png" width="20"/>
                                            <span> Точки здесь нет или она не подходит</span>
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <hr/>

                        <li class="media">
                            <div class="media-body">
                                <h4 class="media-heading">Комментарии</h4>
                            </div>
                        </li>

                        <div id="slideCommentAlert" class="alert" style="display: none;">
                            <span id="slideCommentError">Какая-то ошибка</span>
                        </div>

                        <li class="media">
                            <p>
                                <a id="slideComment" href="#"><span id="slideCommentTitle">Оставьте отзыв</span>
                                    <i id="slideCommentIcon" class="icon-chevron-right spacer3"></i>
                                </a>
                            </p>

                            <form id="slideCommentForm" class="media-body well well-small" style="display: none;">
                                <small class="muted">Осталось <span id="slideSymbNumber">1000</span> символов</small>
                                <input type="text" class="input-mini" id="slideQuestionComment" style="display: none" hidden="true"/>

                                <div class="control-group">
                                    <div class="controls">
                                        <textarea id="slideCommentText" name="slideCommentText" class="span12" rows="3"></textarea>
                                    </div>
                                </div>

                                <label id="slideQuestionLabel" class="label label-info transparent" style="display: none;">
                                    Это ответ пользователю: <span id="slideQuestionQuote"></span>
                                    <a id="slideQuestionClose" >(отмена)</a>
                                </label>

                                <button id="slideSubmitComment" class="btn btn-mini btn-primary pull-right" data-loading-text="Отправляем...">
                                    <i class="icon-comment"></i> Отправить</button>
                            </form>
                        </li>

                        <li id="slideNoCommentsWarning" class="media" style="display: none;">
                            <div class="well well-small">
                                <p class="text-center">Здесь никто ничего не написал еще</p>
                            </div>
                        </li>

                        <ul id="slideCommentsTree" class="media-list">
                        </ul>


                    </ul>
                </div>
            </div>
        </div>

        <div id="mainList" class="span3 infoWindow">
            <div class="media">
                <div class="media-body">
                    <h4 class="media-heading">Результаты поиска: Столовая</h4>

                    <div class="media">

                    </div>
                </div>

                <hr/>
            </div>

            <div class="media-list">

                <li class="media">

                    <a class="pull-left" href="#">
                        <img class="media-object" src="/resources/img/types/tandoor.png" width="64">
                        <label class="label transparent">Столовая</label>
                    </a>

                    <div class="media-body">
                        <h4 class="media-heading">Кафе "У Коли"</h4>
                        <p>Клевая забегаловка</p>

                        <address>
                            Москва, Новоданиловаская набережная, дом 12а<br/>
                        </address>

                        <div class="media">
                            <div class="media-body">
                                <span class="label label-success transparent">565 RUB</span>
                                <span class="badge badge-info spacer5 transparent">3.5</span>

                                <div class="btn-group pull-right">
                                    <a class="favLink" href="#">
                                        <i class="icon-heart-empty"></i>
                                    </a>
                                    <button class="btn btn-mini spacer5">
                                        <i class="icon-expand-alt"></i> Подробнее
                                    </button>
                                </div>

                            </div>
                        </div>
                    </div>

                    <hr/>
                </li>

                <li class="media">
                    <a class="pull-left" href="#">
                        <img class="media-object" src="/resources/img/types/tandoor.png" width="64">
                        <label class="label transparent">Тандыр</label>
                    </a>

                    <div class="media-body">
                        <h4 class="media-heading">Кафе "У Коли"</h4>
                        <p>Клевая забегаловка</p>

                        <address>
                            Москва, Новоданиловаская набережная, дом 12а<br/>
                        </address>

                        <div class="media">
                            <div class="media-body">
                                <span class="label label-success transparent">565 RUB</span>
                                <span class="badge badge-info spacer5 transparent">3.5</span>
                                <div class="btn-group pull-right">
                                    <a class="favLink" href="#">
                                        <i class="icon-heart-empty"></i>
                                    </a>
                                    <button class="btn btn-mini spacer5">
                                        <i class="icon-expand-alt"></i> Подробнее
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <hr/>
                </li>

                <li class="media">
                    <a class="pull-left" href="#">
                        <img class="media-object" src="/resources/img/types/tandoor.png" width="64">
                        <label class="label transparent">Столовая</label>
                    </a>

                    <div class="media-body">
                        <h4 class="media-heading">Кафе "У Коли"</h4>
                        <p>Клевая забегаловка</p>

                        <address>
                            Москва, Новоданиловаская набережная, дом 12а<br/>
                        </address>

                        <div class="media">
                            <div class="media-body">
                                <span class="label label-success transparent">565 RUB</span>
                                <span class="badge badge-info spacer5 transparent">3.5</span>

                                <div class="btn-group pull-right">
                                    <a class="favLink" href="#">
                                        <i class="icon-heart-empty"></i>
                                    </a>
                                    <button class="btn btn-mini spacer5">
                                        <i class="icon-expand-alt"></i> Подробнее
                                    </button>
                                </div>

                            </div>
                        </div>
                    </div>

                    <hr/>
                </li>

                <li class="media">
                    <a class="pull-left" href="#">
                        <img class="media-object" src="/resources/img/types/tandoor.png" width="64">
                        <label class="label transparent">Столовая</label>
                    </a>

                    <div class="media-body">
                        <h4 class="media-heading">Кафе "У Коли"</h4>
                        <p>Клевая забегаловка</p>

                        <address>
                            Москва, Новоданиловаская набережная, дом 12а<br/>
                        </address>

                        <div class="media">
                            <div class="media-body">
                                <span class="label label-success transparent">565 RUB</span>
                                <span class="badge badge-info spacer5 transparent">3.5</span>

                                <div class="btn-group pull-right">
                                    <a class="favLink" href="#">
                                        <i class="icon-heart-empty"></i>
                                    </a>
                                    <button class="btn btn-mini spacer5">
                                        <i class="icon-expand-alt"></i> Подробнее
                                    </button>
                                </div>

                            </div>
                        </div>
                    </div>

                    <hr/>
                </li>


            </div>

        </div>

        <div id="map" class="span9"></div>
    </div>
</div>

