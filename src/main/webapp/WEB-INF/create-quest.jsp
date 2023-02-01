<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<c:import url="parts/header.jsp"/>
<div class="container">
  <form action="create-quest" method="post">

    <div class="mb-3">
      <label for="quest-name" class="form-label">Название квеста</label>
      <input name="name" type="text" class="form-control" id="quest-name" placeholder="Тут укажите имя квеста">
    </div>

    <div class="mb-3">
      <label for="quest-description" class="form-label">Описание квеста</label>
      <input name="description" type="text" class="form-control" id="quest-description"
             placeholder="Добавьте описание для Вашего квеста">
    </div>

    <div class="mb-3">
      <label for="exampleFormControlTextarea1" class="form-label">Содержимое квеста</label>
      <textarea name="text" class="form-control" id="exampleFormControlTextarea1" rows="10"
                placeholder="<%@include file="./parts/quest-demo.jsp" %>"></textarea>
    </div>

    <div class=" form-group">
      <label class="col-md-4 control-label" for="submit"></label>
      <div class="col-md-4">
        <button id="submit" name="create"
                class="btn btn-success">Создать квест</button>
      </div>
    </div>
  </form>
</div>
<c:import url="parts/footer.jsp"/>

