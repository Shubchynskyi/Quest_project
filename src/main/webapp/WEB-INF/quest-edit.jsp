<%@ page contentType="text/html;charset=UTF-8" %>
<%@include file="parts/header.jsp" %>
<div class="container">
    <h4 style="margin: auto; padding-bottom: 50px; color: #565756">
        Редактирование квеста <b>${requestScope.quest.name}</b>
    </h4>
    <form class="row form-horizontal"
          action="quest-edit"
          method="post"
          enctype="multipart/form-data">

        <!-- Quest Name -->
        <div class="mb-3">
            <label for="questName">Название квеста:</label>
            <input id="questName" name="questName" class="w-100" value="${requestScope.quest.name}">
        </div>

        <!-- Quest Description -->
        <div class="mb-3">
            <label for="questDescription">Описание квеста:</label>
            <input id="questDescription" name="questDescription" class="w-100"
                   value="${requestScope.quest.description}">
        </div>

        <!-- Button -->
        <div class="mb-3">
            <button type="submit" class="btn btn-success"
                    style="width: 200px; margin-bottom: 50px">
                Сохранить
            </button>
            <input name="id" type="hidden" value="${requestScope.quest.id}">
        </div>
    </form>
</div>

<hr class="hr hr-blurry"/>
<div class="container">

    <c:forEach var="question" items="${requestScope.quest.questions}">
        <form class="row form-horizontal"
              action="quest-edit"
              method="post"
              enctype="multipart/form-data">

            <!-- Question Text -->
            <div class="mb-3">
                <label for="questionText">Вопрос:</label>
                <input id="questionText" name="questionText" class="w-100"
                       value="${question.text}">
            </div>

            <!-- Answers Text -->
            <div class="mb-3" style="padding-bottom: 10px">
                <c:forEach var="answer" items="${question.answers}">
                    <label for="answerText">Ответ:</label>
                    <input id="answerText" class="w-100" name="answer${answer.id}" value="${answer.text}">
                </c:forEach>
            </div>

            <!-- Image -->
            <div class="mb-3">
                <label class="col-md-4 control-label" for="image-${question.id}">
                    <img id="preview-${question.id}" src="images/${question.image}" width="75%"
                         alt="${question.image}">
                </label>

                <input onchange="PreviewImage('image-${question.id}','preview-${question.id}');"
                       id="image-${question.id}"
                       name="image"
                       style="visibility:hidden;"
                       class="input-file" type="file">

                <script type="text/javascript">
                    function PreviewImage(inputFileId, imageId) {
                        const oFReader = new FileReader();
                        oFReader.readAsDataURL(document.getElementById(inputFileId).files[0]);
                        oFReader.onload = function (oFREvent) {
                            document.getElementById(imageId).src = oFREvent.target.result;
                        };
                    }
                </script>
            </div>

            <div class="mb-3">
                <button type="submit" class="btn btn-success" style="width: 200px;">
                    Сохранить
                </button>
                <input name="questionId" type="hidden" value="${question.id}">
                <input name="id" type="hidden" value="${requestScope.quest.id}">
            </div>
            <a id="label-${question.id}"></a>
        </form>
        <hr class="hr hr-blurry"/>
    </c:forEach>
</div>
<div class="container">
    <a href="quests-list">
        <button type="button" class="btn btn-info"
                style="width: 500px;">
            К списку квестов
        </button>
    </a>
    <input name="id" type="hidden" value="${requestScope.quest.id}">
</div>
<c:import url="parts/footer.jsp"/>
