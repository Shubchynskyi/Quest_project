<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<c:import url="parts/header.jsp"/>
<div class="container">
    <form class="form-horizontal"
          action="quest-edit"
          method="post"
          enctype="multipart/form-data">
        <fieldset>

            <p align="center">Редактирование квеста ${requestScope.quest.name}.
                Выберите изображение для каждого вопроса квеста</p> <br><br><br>

            <!-- File Button -->
            <div class="form-group">

                <label class="col-md-4 control-label" for="question">
                    <div class="form-group">
                        <img id="previewId" src="images/${question.image}" width="150" alt="${question.image}">
                    </div>
                    Нажмите чтобы изменить
                </label>
                <div class="col-md-4">
                    <input onchange="PreviewImage('question','previewId');"
                           id="question"
                           name="question"
                           style="visibility:hidden;"
                           class="input-file" type="file">
                </div>
            </div>

            <script type="text/javascript">
                function PreviewImage(inputFileId,imageId) {
                    const oFReader = new FileReader();
                    oFReader.readAsDataURL(document.getElementById(inputFileId).files[0]);
                    oFReader.onload = function (oFREvent) {
                        document.getElementById(imageId).src = oFREvent.target.result;
                    };
                }
            </script>

<%--            <c:forEach var="question" items="${requestScope.questions}">--%>

<%--                <!-- скрытое поле с id пользователя, для doPost метода -->--%>
<%--                <input type="hidden" name="id-${question.id}" value="${question.id}">--%>

<%--                    <p>${question.id} - ${question.text}</p>--%>

<%--                    <!-- File Button - выбор картинки -->--%>
<%--                    <div class="form-group">--%>
<%--                        <label class="col-md-4 control-label" for="image-${question.id}">--%>
<%--                            <div class="form-group">--%>
<%--                                <img id="previewId-${question.id}" src="images/${question.image}" width="200"--%>
<%--                                     alt="${question.image}">--%>
<%--                            </div>--%>
<%--                            Нажмите чтобы выбрать изображение--%>
<%--                        </label>--%>
<%--                        <div class="col-md-4">--%>
<%--                            <input onchange="PreviewImage('image-${question.id}','previewId-${question.id}');"--%>
<%--                                   id="image-${question.id}"--%>
<%--                                   name="image-${question.id}"--%>
<%--                                   style="visibility:hidden;"--%>
<%--                                   class="input-file" type="file">--%>
<%--                        </div>--%>
<%--                    </div>--%>
<%--                    <script type="text/javascript">--%>
<%--                        function PreviewImage(inputFileId, imageId) {--%>
<%--                            const oFReader = new FileReader();--%>
<%--                            oFReader.readAsDataURL(document.getElementById(inputFileId).files[0]);--%>
<%--                            oFReader.onload = function (oFREvent) {--%>
<%--                                document.getElementById(imageId).src = oFREvent.target.result;--%>
<%--                            };--%>
<%--                        }--%>
<%--                    </script>--%>
<%--            </c:forEach>--%>

            <button id="save"
                    name="save"
                    class="btn btn-success">save
            </button>

            <!-- Button -->
            <div class="form-group">
                <label class="col-md-4 control-label" for="singlebutton">Single Button</label>
                <div class="col-md-4">
                    <button id="singlebutton" name="singlebutton" class="btn btn-primary">Button</button>
                </div>
            </div>

        </fieldset>
    </form>
    <c:import url="parts/footer.jsp"/>
