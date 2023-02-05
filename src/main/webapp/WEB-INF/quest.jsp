<%@ page contentType="text/html;charset=UTF-8" %>
<%@include file="parts/header.jsp" %>
<div class="container">

    <c:if test="${requestScope.question==null}">
        <form class="form-horizontal"
              action="quest?id=${requestScope.id}&questionId=${requestScope.startQuestionId}"
              method="post">
            <fieldset>
                <input type="hidden" name="questName" value="${requestScope.questName}">
                <p>${requestScope.questName}</p>
                <p>${requestScope.questDescription}</p>
                <button id="start" name="start"
                        class="btn btn-success">Начать
                </button>
            </fieldset>
        </form>
    </c:if>

    <c:if test="${requestScope.question.id>0}">

        <p>${requestScope.questName}</p>
        <img src="images/${question.image}" alt="images/${question.image}" width="500px">
        <p>${requestScope.question.text}</p>
        <form class="form-horizontal" action="quest" method="post">
            <fieldset>
                <input type="hidden" name="id" value="${requestScope.id}">
                <input type="hidden" name="questName" value="${requestScope.questName}">
                <input type="hidden" name="gameState" value="${requestScope.question.gameState}">

                <c:if test="${requestScope.question.answers!=null}">
                    <c:forEach var="answer" items="${requestScope.question.answers}">
                        <!-- Multiple Radios -->
                        <div class="form-group">
                            <div class="col-md-4">
                                <div class="radio">
                                    <label for="radios-${answer.nextQuestionId}">
                                        <input type="radio" name="questionId" id="radios-${answer.nextQuestionId}"
                                               value="${answer.nextQuestionId}" checked="checked">
                                            ${answer.text}
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
                        <button id="singlebutton" name="singlebutton" class="btn btn-primary">
                                ${requestScope.question.gameState=="PLAY"?"Далее":"К списку квестов"}
                        </button>
                    </div>
                </div>

            </fieldset>
        </form>
    </c:if>

</div>
<c:import url="parts/footer.jsp"/>
