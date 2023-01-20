<%--
  Created by IntelliJ IDEA.
  User: Shubchynskyi
  Date: 17.01.2023
  Time: 1:58
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>--%>
<%@include file="parts/header.jsp" %>
<!-- чтобы писать не request.scope.user, а просто user -->
<%--<jsp:useBean id="user" scope="request" class="com.example.quest_project.entity.User"/>--%>
    <%--<html>--%>
<%--<head>--%>
<%--    <!-- условие - до знака ? если true, после : если false -->--%>
<%--    <title>User ${user.login!=null?user.login:"none"}</title>--%>
<%--    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css"--%>
<%--          rel="stylesheet"--%>
<%--          integrity="sha384-GLhlTQ8iRABdZLl6O3oVMWSktQOp6b7In1Zl3/Jr59b6EGGoI1aFkw7cmDA6j6gD"--%>
<%--          crossorigin="anonymous">--%>
<%--</head>--%>
    <%--<body>--%>
<!--в форме настроить action(куда будет отсылаться форма) и method(тип запроса)-->
<!--теперь при нажатии любой кнопки заполненная форма отправится на сервлет UserServlet, doPost -->
<!-- enctype="multipart/form-data" - для того чтобы форма отправляла не только поля но и данные о файлах-->
<form class="form-horizontal"
      action="user?id=${user.id==null?0:user.id}" <%-- если id=null, то подставляем 0 - для нового пользователя --%>
      method="post"
      enctype="multipart/form-data">
    <fieldset>

        <!-- Form Name -->
        <legend>User Form</legend>

        <!-- File Button -->
        <div class="form-group">

            <label class="col-md-4 control-label" for="image">
                <div class="form-group">
                    <img id="previewId" src="images/${user.image}" width="150" alt="${user.image}">
                </div>
                Нажмите чтобы изменить
            </label>
            <div class="col-md-4">
                <input onchange="PreviewImage('image','previewId');" id="image" name="image" style="visibility:hidden;"
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

        <!-- скрытое поле с id пользователя, для doPost метода -->
        <input type="hidden" name="id" value="${requestScope.id}">

        <!-- Text input-->
        <div class="form-group">
            <label class="col-md-4 control-label" for="login">Login</label>
            <div class="col-md-4">
                <input id="login" name="login" type="text" placeholder="please enter your login"
                       class="form-control input-md" required="" value="${user.login}">
            </div>
        </div>

        <!-- Password input-->
        <div class="form-group">
            <label class="col-md-4 control-label" for="password">Password</label>
            <div class="col-md-4">
                <input id="password" name="password" type="password" placeholder="please enter your password"
                       class="form-control input-md" required="" value="${user.password}">
            </div>
        </div>

        <!-- Select Basic -->
        <div class="form-group">
            <label class="col-md-4 control-label" for="role">Role</label>
            <div class="col-md-4">
                <select id="role" name="role" class="form-control">
                    <!-- метод init в сервлете UserServlet позволил использовать роли Role на этой странице -->
                    <c:forEach items="${applicationScope.roles}" var="role">
                        <%--                        делаем выбор ролей циклом и "selected" согласно роли текущего пользователя--%>
                        <option value="${role}" ${user.role==role?"selected":""}>${role}</option>
                    </c:forEach>
                    <%--                    <c:forEach items="${applicationScope.roles}" var="role">--%>
                    <%--                    <option value="${role} ${user.role==role?"selected":""}>${role}</option>--%>
                    <%--                    </c:forEach>--%>

                    <%--                    убираем хардкод --%>
                    <%--                    <option value="GUEST">Guest</option>--%>
                    <%--                    <option value="USER">User</option>--%>
                    <%--                    <option value="MODERATOR">Moderator</option>--%>
                    <%--                    <option value="ADMIN">Admin</option>--%>
                </select>
            </div>
        </div>

        <!-- Button (Double) -->
        <div class=" form-group
                    ">
            <label class="col-md-4 control-label" for="updateOrCreate"></label>
            <div class="col-md-8">
                <!-- если Id>0, то кнопка будет Update, если <= 0, то Create -->
                <button id="updateOrCreate" name="${requestScope.id>0?"update":"create"}"
                        class="btn btn-success">${requestScope.id>0?"Update":"Create"}
                </button>
                <!-- если id>0, то будет еще кнопка Delete -->
                <c:if test="${requestScope.id>0}">
                    <button id="delete" name="delete"
                            class="btn btn-danger">Delete
                    </button>
                </c:if>
            </div>
        </div>

    </fieldset>
</form>

<%@include file="parts/footer.jsp"%>
