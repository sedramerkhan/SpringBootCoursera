<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%-- Spring form tag library — provides the two-way-binding form tags. The URI
     is springframework.org/tags/form (NOT a jakarta uri); it ships with
     spring-webmvc, so no extra dependency is needed. --%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Add Todo</title>
    <link rel="stylesheet" href="/css/app.css">
</head>
<body>
    <div class="card">
        <h1 class="card__title">Add a todo</h1>
        <p class="card__subtitle">${name}, what do you want to get done?</p>

        <%-- modelAttribute="todo" ties this form to the Todo bean the controller
             put in the model. Each form:input path maps to a Todo property. --%>
        <form:form class="form" action="/todos/add" method="post" modelAttribute="todo">
            <div class="form-group">
                <label for="description">Description</label>
                <form:input path="description" cssClass="field"
                            placeholder="e.g. Learn Spring validation"/>
                <%-- Shows the validation message for this field when it fails. --%>
                <form:errors path="description" cssClass="field-error"/>
            </div>

            <div class="form-group">
                <label for="targetDate">Target date</label>
                <form:input path="targetDate" type="date" cssClass="field"/>
                <form:errors path="targetDate" cssClass="field-error"/>
            </div>

            <%-- Hidden fields so id and done round-trip with the bean (they are
                 part of the object but not edited on this screen). --%>
            <form:hidden path="id"/>
            <form:hidden path="done"/>

            <button class="btn" type="submit">Add todo</button>
        </form:form>

        <a class="link" href="/todos">&larr; Back to list</a>
    </div>
</body>
</html>