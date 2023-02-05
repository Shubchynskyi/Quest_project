<%@ page contentType="text/html;charset=UTF-8" %>
<%@include file="parts/header.jsp" %>
<div class="container">
    <p>список квестов</p>

    <c:forEach var="quest" items="${requestScope.quests}">

        <div class="row">
            <div class="col-lg-4">
                <svg class="bd-placeholder-img rounded-circle" width="140" height="140"
                     xmlns="http://www.w3.org/2000/svg" role="img" aria-label="Placeholder: 140x140"
                     preserveAspectRatio="xMidYMid slice" focusable="false"><title>Placeholder</title>
                    <rect width="100%" height="100%" fill="#777"></rect>
                    <text x="50%" y="50%" fill="#777" dy=".3em">140x140</text>
                </svg>
                <h2 class="fw-normal">${quest.name}</h2>
                <p>${quest.description}</p>
                <p><a class="btn btn-secondary" href="quest?id=${quest.id}">PLAY &raquo;</a></p>
            </div>
        </div>

    </c:forEach>

</div>
<c:import url="parts/footer.jsp"/>
