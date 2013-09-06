<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>

<div class="container-fluid">
    <div class="row-fluid">
        <div id="slidepanel" class="span2 transparent">

            <div class="infoWindowInner">
                <div id="slideAdminChecked" class="ribbon-outer" style="display: none">
                    <div class="ribbon-inner">
                        <a href="/help#moderation">ПРОВЕРЕНО!</a>
                    </div>
                </div>

                <ul class="media-list">
                    <button id="closeSlidePanel" type="button" class="close" data-dismiss="modal">&times;</button>

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
                                    <button id="slideFromHere" class="btn btn-small">Маршрут сюда</button>
                                    <button id="slideToHere" class="btn btn-small">Маршрут отсюда</button>
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
                            <h4 class="media-heading">Комментики</h4>
                        </div>
                    </li>

                    <li class="media">
                        <p><a href="#">Оставьте отзыв<i class="icon-chevron-right spacer3"></i></a></p>
                        <div class="media-body well well-small">
                            <small class="muted">Осталось 1000 символов</small>
                            <textarea class="span12" rows="3"></textarea>
                            <button class="btn btn-mini btn-primary pull-right">Отправить</button>
                        </div>
                    </li>

                    <ul class="media-list">
                        <li class="media">
                            <div class="media-body">
                                <p>
                                    <b>
                                        <i class="icon-user"></i>
                                        <a href="#" class="spacer3 text-error transparent">Petr Popov</a>
                                        <small class="spacer3">27.08.2013 14.59</small>
                                    </b>

                                    <span class="pull-right">
                                        <a class="badge badge-success transparent"><i class="icon-thumbs-up"></i>
                                            <span class="spacer3">23</span>
                                        </a>
                                        <a class="badge badge-important transparent"><i class="icon-thumbs-down"></i>
                                            <span class="spacer3">23</span>
                                        </a>
                                    </span>
                                </p>

                                <p>
                                    Да нормальная у нее жопа
                                    <span class="pull-right">
                                        <button class="btn btn-mini"><i class="icon-trash"></i> Удалить</button>
                                    </span>
                                </p>
                            </div>
                            <hr/>
                        </li>

                        <li class="media">
                            <div class="media-body">
                                <p>
                                    <b>
                                        <i class="icon-user"></i>
                                        <a href="#" class="spacer3 text-inso transparent">Vasya Popov</a>
                                        <small class="spacer3">27.08.2013 14.59</small>
                                    </b>

                                <span class="pull-right">
                                    <a class="badge badge-success transparent"><i class="icon-thumbs-up"></i> 23</a>
                                    <a class="badge badge-important transparent"><i class="icon-thumbs-down"></i> 23</a>
                                </span>
                                </p>
                                <p>БОЛЬШАЯ ЖОПА!</p>
                            </div>
                            <hr/>
                        </li>

                    </ul>


                </ul>
            </div>
        </div>

        <div id="map" class="span12"></div>
    </div>
</div>

