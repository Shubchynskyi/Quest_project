<%@ page contentType="text/html;charset=UTF-8" %>
<%@include file="parts/header.jsp" %>
<div class="container">
    <form class="form-horizontal" action="login" method="post">
        <fieldset>

            <!-- Form Name -->
            <legend>Login form</legend>

            <!-- Text input-->
            <div class="form-group">
                <label class="col-md-4 control-label" for="userLogin">Login</label>
                <div class="col-md-4">
                    <input id="userLogin" name="login" type="text" placeholder="set login" class="form-control input-md"
                           required=""
                           value="admin"> <!-- remove when finish dev -->
                </div>
            </div>

            <!-- Password input-->
            <div class="form-group">
                <label class="col-md-4 control-label" for="userPassword">Password</label>
                <div class="col-md-4">
                    <input id="userPassword" name="password" type="password" placeholder="pass req"
                           class="form-control input-md" required=""
                           value="admin"> <!-- remove when finish dev -->
                </div>
            </div>

            <!-- Button -->
            <div class="form-group">
                <label class="col-md-4 control-label" for="submit"></label>
                <div class="col-md-4">
                    <button id="submit" name="loginButton" class="btn btn-success">
                        Войти
                    </button>
                </div>
            </div>

        </fieldset>
    </form>
</div>
<c:import url="parts/footer.jsp"/>
