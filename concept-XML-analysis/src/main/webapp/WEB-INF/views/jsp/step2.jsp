<jsp:include page="library.jsp"></jsp:include>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<html>
<head>

<link href="<c:url value="/resources/core/css/step2.css" />"
	rel="stylesheet">
<script src="<c:url value="/resources/core/js/welcome.js" />"></script>
<link rel="stylesheet"
	href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>
<script
	src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
<%-- <link href="<c:url value="/resources/core/css/default.css" />"
	rel="stylesheet"> --%>


</head>
<body>

	<div class="container-fluid">
		<div class="xsd_container">

			<div class="xsdview_blck">
				<h4>
					<strong> XSD View </strong>
				</h4>
				<textarea rows="15" cols="50" readonly="readonly">${parsedInString}</textarea>
			</div>


			<form:form commandName="xsdParseRequest" id="captureParseSettings"
				name="captureSettings">

				<div class="setting_blck">
					<strong>Select Setting</strong> <br>

					<form:checkbox path="doAll"></form:checkbox>
					<span>Run All</span>
					<br>

					<form:checkbox path="createScript" />
					<span>Create Database Script</span> <br> <span>Choose
						Database Type</span> <br>
					<form:select path="databaseType" items="${databaseType}"></form:select>
<br>

					<form:checkbox path="createTable" />
					<span>Create Table in Database</span> <br> <span>Database
						TNS</span> <br>
					<form:textarea path="tnsEntry" rows="5" cols="20" />
					<br> <span>UserName</span>
					<form:input path="userName" />
					<br><br> <span>Password</span>
					<form:input path="password" />
					<br>

					<form:checkbox path="createFramework" />
					<span>Generate Parsing framework</span>

</div>

					 <div class="generatebtn">
						<button type="button" id="GenerateButton" class="upload" value="Generate"
							onclick="clickGenerate()" ><span>Generate</span></button>
							</div>
				
			</form:form>

		</div>
	</div>

</body>
</html>
