<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<c:import url="parts/header.jsp"/>
<div class="container">

    <%--    если в запросе нет question --%>

    <%--        если WIN или LOST, <p>Вы проиграли/выиграли</p>--%>
<%--    нет вопроса, показываю имя квеста и описание, отправляю имя квеста, id квеста, id первого вопроса квеста--%>
    <c:if test="${requestScope.question==null}">
        <form class="form-horizontal"
              action="quest?id=${requestScope.id}&questionId=${requestScope.startQuestionId}"
              method="post">
            <fieldset>
<%--                <input type="hidden" name="id" value="${requestScope.quest.id}">--%>
                <input type="hidden" name="questName" value="${requestScope.questName}">
                <p>вопрос не найден. Отображаю описание квеста и кнопку старт, на которой ссылка типа
                    /quest?id=id&questionId=questionId</p>
                    <%--            <img src="images/${quest.image}" alt="images/${quest.image}" width="500px">--%>
                <p>${requestScope.questName}</p>
                <p>${requestScope.questDescription}</p>
                <button id="start" name="start"
                        class="btn btn-success">Start
                </button>

            </fieldset>
        </form>
    </c:if>

<%--    есть вопрос--%>
        сюда должно прилететь имя квеста, id квеста, id следующего вопроса
        проверить на WIN/LOST/PLAY
        1) если win/lost, то кнопка будет две кнопки "Завершить" "Начать сначала"
        2) если Play, то будет список ответов и кнопка далее
    <c:if test="${requestScope.question.id>0}">


        <p>
            id / questName / Object question
            отображаю вопрос и варианты ответов, в кнопку передаю
            /quest?id=id&questionNextId=questionNextId
            если WIN или LOST, то кнопка "К списку квестов", если PLAY, кнопка далее
        </p>
        <p>${requestScope.questName}</p>
        <img src="images/${question.image}" alt="images/${question.image}" width="500px">
        <p>${requestScope.question.text}</p>
        <form class="form-horizontal"
              action="quest"
              method="post">
            <fieldset>
                <input type="hidden" name="id" value="${requestScope.id}">
                <input type="hidden" name="questName" value="${requestScope.questName}">

                <!-- Form Name -->
        <c:if test="${requestScope.question.answers!=null}">
                <c:forEach var="answer" items="${requestScope.question.answers}">
                    <!-- Multiple Radios -->
                    <div class="form-group">
<%--                    <label class="col-md-4 control-label" for="radios">Multiple Radios</label>--%>
                        <div class="col-md-4">
                            <div class="radio">
                                <label for="radios-${answer.nextQuestionId}">
                                    <input type="radio" name="nextQuestionId" id="radios-${answer.nextQuestionId}" value="${answer.nextQuestionId}">  <%-- checked="checked"--%>
                                        ${answer.text} + ${answer.nextQuestionId}
                                </label>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:if>

                <!-- Button -->
                <div class="form-group">
                    <label class="col-md-4 control-label" for="singlebutton"></label>
                    <div class="col-md-4">
<%--                        два варианта кнопки, в зависимости от GameState--%>
                        <button id="singlebutton" name="singlebutton" class="btn btn-primary">Next</button>
                    </div>
                </div>

            </fieldset>
        </form>



    </c:if>


</div>
<c:import url="parts/footer.jsp"/>
