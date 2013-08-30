<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div id="profileModal" class="modal modalProfile hide fade">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h3>Профиль</h3>
    </div>

    <div class="modal-body">


        <div class="tabbable">
            <ul class="nav nav-pills">
                <li class="active"><a href="#tab1" data-toggle="tab">Локации</a></li>
                <li><a href="#tab2" data-toggle="tab">Общее</a></li>
            </ul>
            <div class="tab-content">
                <div class="tab-pane active" id="tab1">
                    <label id="connectedLocationsLabel" style="display: none" hidden="true">
                        Здесь будут показываться все локации, которые вы как-либо отметили - проголосовали
                    за них или подтвердили их существование</label>
                    <table class="table table-striped table-hover table-condensed">
                        <thead>
                            <tr>
                                <th>Название</th>
                                <th>Адрес</th>
                                <th>Средний чек</th>
                                <th>Рейтинг</th>
                                <th>Добавил</th>
                            </tr>
                        </thead>
                        <tbody id="connectedLocsBody">

                        </tbody>
                    </table>
                </div>
                <div class="tab-pane" id="tab2">
                    <form id="profileForm" class="form-horizontal">

                        <div id="profileAlert" class="alert" style="display: none" hidden="true" >
                            <button type="button" class="close" data-dismiss="alert">&times;</button>
                            </strong><span id="profileError">Ошибка на сервере</span>
                        </div>

                        <div class="control-group">
                            <label class="control-label" for="profileFirstName">Имя</label>
                            <div class="controls">
                                <input type="text" id="profileFirstName" name="profileFirstName">
                            </div>
                        </div>

                        <div class="control-group">
                            <label class="control-label" for="profileLastName">Фамилия</label>
                            <div class="controls">
                                <input type="text" id="profileLastName" name="profileLastName">
                            </div>
                        </div>

                        <div class="control-group">
                            <label class="control-label" for="profileEmail">Email</label>
                            <div class="controls">
                                <input type="text" id="profileEmail" name="profileEmail">
                            </div>
                        </div>

                        <div class="control-group">
                            <div class="controls">
                                <a id="saveProfileForm" href="#" class="btn btn-primary" data-loading-text="Сохраняем...">Сохранить</a>
                            </div>
                        </div>
                    </form>

                    <div>
                        <a id="profileFacebookLink" href="http://www.facebook.com" target="_blank" style="display: none" hidden="true">
                            <img width="16" src="<s:url value="/resources"/>/img/facebook_small_32.png"/>
                            Facebook
                        </a>
                        <a id="profileFoursquareLink" href="http://www.facebook.com" target="_blank" class="spacer3" style="display: none" hidden="true">
                            <img width="16" src="<s:url value="/resources"/>/img/foursquare_small_32.png"/>
                            Foursquare
                        </a>
                        <a id="profileTwitterLink" href="http://www.facebook.com" target="_blank" class="spacer3" style="display: none" hidden="true">
                            <img width="16" src="<s:url value="/resources"/>/img/twitter_small_32.png"/>
                            Twitter
                        </a>
                    </div>
                </div>
            </div>
        </div>


    </div>

    <div class="modal-footer">
        <a href="#" class="btn" data-dismiss="modal">Закрыть</a>
    </div>
</div>