<%@ page contentType="text/html;charset=UTF-8" %>
<%@include file="parts/header.jsp" %>

<div class="container">
    <%-- new user registration form --%>
    <form class="form-horizontal"
          action="signup"
          method="post"
          enctype="multipart/form-data">
        <fieldset>

            <!-- Form Name -->
            <legend>User Form</legend>

            <!-- File Button -->
            <div class="form-group">
                <div class="form-group">
                    <label class="col-md-4 control-label" for="image">
                        <img id="previewId" src="images/${user.image}" width="150" alt="${user.image}">
                        Нажмите чтобы изменить
                    </label>
                </div>
                <div class="col-md-4">
                    <input onchange="PreviewImage('image','previewId');" id="image" name="image"
                           style="visibility:hidden;"
                           class="input-file" type="file">
                </div>
            </div>
            <script type="text/javascript">
                function PreviewImage(inputFileId, imageId) {
                    const oFReader = new FileReader();
                    oFReader.readAsDataURL(document.getElementById(inputFileId).files[0]);
                    oFReader.onload = function (oFREvent) {
                        document.getElementById(imageId).src = oFREvent.target.result;
                    };
                }
            </script>

            <!-- hidden field with id=0 - for new user registration -->
            <input type="hidden" name="id" value="0">

            <!-- Text input-->
            <div class="form-group">
                <label class="col-md-4 control-label" for="login">Login</label>
                <div class="col-md-4">
                    <input id="login" name="login" type="text" placeholder="please enter your login"
                           class="form-control input-md" required="">
                </div>
            </div>

            <!-- Password input-->
            <div class="form-group">
                <label class="col-md-4 control-label" for="password">Password</label>
                <div class="col-md-4">
                    <input id="password" name="password" type="password" placeholder="please enter your password"
                           class="form-control input-md" required="">
                </div>
            </div>

            <!-- Select Basic -->
            <div class="form-group">
                <label class="col-md-4 control-label" for="role">Role</label>
                <div class="col-md-4">
                    <select id="role" name="role" class="form-control">
                        <c:forEach items="${applicationScope.roles}" var="role">
                            <option value="${role}" ${"GUEST"==role?"selected":""}>${role}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>

            <!-- Button (Double) -->
            <div class="form-group">
                <label class="col-md-4 control-label" for="updateOrCreate"></label>
                <div class="col-md-8">
                    <button id="updateOrCreate" name="create" class="btn btn-success">
                        Sing-up
                    </button>
                </div>
            </div>

        </fieldset>
    </form>
</div>
<%@include file="parts/footer.jsp" %>
