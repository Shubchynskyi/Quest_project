<%@ page contentType="text/html;charset=UTF-8" %>
<%@include file="parts/header.jsp" %>

<div class="container">
    <c:forEach var="user" items="${requestScope.users}">
        <img src="images/${user.image}" alt="images/${user.image}" width="100px">
        Edit user <a href="user?id=${user.id}">${user.login}<br></a
        User login = ${user.login} <br>
    </c:forEach>
</div>

<%@include file="parts/footer.jsp" %>
