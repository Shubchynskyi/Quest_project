<%@ page contentType="text/html;charset=UTF-8" %>
<%@include file="parts/header.jsp" %>
<div class="container">
    <p class="mb-4 fs-4">список квестов</p>

    <c:forEach var="quest" items="${quests}">

        <div class="row mb-4">
            <div class="col-lg-4">
                <div class="card">
                    <img src="${quest.image}" class="card-img-top" alt="...">
                    <div class="card-body">
                        <h5 class="card-title">${quest.name}</h5>
<%--                        <p class="card-text">${quest.description}</p>--%>
                        <a href="quest?id=${quest.id}" class="btn btn-primary">PLAY &raquo;</a>
                    </div>
                </div>
            </div>
        </div>

    </c:forEach>

</div>
<c:import url="parts/footer.jsp"/>
