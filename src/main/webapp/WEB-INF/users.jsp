<%--
  Created by IntelliJ IDEA.
  User: Shubchynskyi
  Date: 16.01.2023
  Time: 15:09
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@include file="parts/header.jsp" %>

<div class="container">
    <%-- петачает пользователей циклом, var - переменная, items - коллекция --%>
    <c:forEach var="user" items="${requestScope.users}">
        <%-- печатем обьект user --%>
        <img src="images/${user.image}" alt="images/${user.image}" width="100px">
        Edit user <a href="user?id=${user.id}">${user.login}<br></a

        <%-- печатаем только логин юзера --%>
            User login = ${user.login} <br>
    </c:forEach>
</div>


<%@include file="parts/footer.jsp" %>
