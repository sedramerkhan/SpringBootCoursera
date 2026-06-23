<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="../common/header.jspf" %>
<%@ include file="../common/navbar.jspf" %>
    <div class="card">
        <h1 class="card__title">Edit todo</h1>
        <p class="card__subtitle">${name}, update the details below.</p>

        <%-- Same command-bean binding as the add form, but posts to /update and
             is pre-filled from the existing todo. --%>
        <form:form class="form" action="/todos/${todo.id}/update" method="post" modelAttribute="todo">
            <div class="form-group">
                <label for="description">Description</label>
                <form:input path="description" cssClass="field"/>
                <form:errors path="description" cssClass="field-error"/>
            </div>

            <div class="form-group">
                <label for="targetDate">Target date</label>
                <form:input path="targetDate" type="date" cssClass="field"/>
                <form:errors path="targetDate" cssClass="field-error"/>
            </div>

            <div class="checkbox-row">
                <form:checkbox path="done" id="done"/>
                <label for="done">Mark as done</label>
            </div>

            <%-- id round-trips so the controller updates the right row. --%>
            <form:hidden path="id"/>

            <button class="btn" type="submit">Save changes</button>
        </form:form>

        <a class="link" href="/todos">&larr; Back to list</a>
    </div>
<%@ include file="../common/footer.jspf" %>