<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<c:import url="parts/header.jsp"/>
<div class="container">
    <p>список квестов</p>

    <c:forEach var="quest" items="${requestScope.quests}">

        <!-- Three columns of text below the carousel -->
        <div class="row">
            <div class="col-lg-4">
                <svg class="bd-placeholder-img rounded-circle" width="140" height="140"
                     xmlns="http://www.w3.org/2000/svg" role="img" aria-label="Placeholder: 140x140"
                     preserveAspectRatio="xMidYMid slice" focusable="false"><title>Placeholder</title>
                    <rect width="100%" height="100%" fill="#777"/>
                    <text x="50%" y="50%" fill="#777" dy=".3em">140x140</text>
                </svg>
                <h2 class="fw-normal">${quest.name}</h2>
                <p>${quest.description}</p>
                <p><a class="btn btn-secondary" href="quest?id=${quest.id}">PLAY &raquo;</a></p>
            </div><!-- /.col-lg4 -->
        </div>
        <!-- /.row -->
        <br>
        <c:forEach var="question" items="${quest.questions}">
            <p>${question.text}</p>
            <div class="form-group">
                <img id="previewId" src="images/${question.image}" width="150" alt="${question.image}">
            </div>
            <br>
        </c:forEach>

    </c:forEach>


</div>
<c:import url="parts/footer.jsp"/>
