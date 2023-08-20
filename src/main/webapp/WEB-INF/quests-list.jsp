<%@ page contentType="text/html;charset=UTF-8" %>
<%@include file="parts/header.jsp" %>

<div class="container">
    <p class="mb-4 fs-4">список квестов</p>

    <div class="row">
        <c:forEach var="quest" items="${quests}">
            <div class="col-lg-4 mb-4">
                <div class="card">
                    <div style="height: 200px; overflow: hidden; display: flex; align-items: center; justify-content: center;">
                        <img src="images/${quest.image}" class="card-img-top" alt="..." style="max-width: 100%; max-height: 100%; object-fit: contain;">
                    </div>
                    <div class="card-body">
                        <h5 class="card-title">${quest.name}</h5>
                        <p class="card-text" style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">${quest.description}</p>
                        <a href="quest?id=${quest.id}" class="btn btn-primary">PLAY &raquo;</a>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
</div>

<%@include file="parts/footer.jsp" %>