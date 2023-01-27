<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<c:import url="parts/header.jsp"/>
<div class="container">
    <jsp:useBean id="user" scope="session"
                 type="com.example.quest_project.entity.User"/>

    <p>редактирую квест, сюда наверное надо передать квест id</p>
    <a href="quests-list">После редактирования квеста можно перейти к списку квестов</a>
</div>
<c:import url="parts/footer.jsp"/>
