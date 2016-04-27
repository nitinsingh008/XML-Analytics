
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>


<script src="<c:url value="/resources/core/js/welcome.js"/>"></script>
<link rel="stylesheet"
	href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>
<script
	src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
<script type="text/javascript">

$(document).ready(function() {
	$("#databaseSetting").hide();
	
	$("#createTable").on('click',function(){
		if(document.getElementById('createTable').checked) {
		    $("#databaseSetting").show();
		} else {
		    $("#databaseSetting").hide();
		}
	});
	
	$("#doAll").on('click',function(){
		if(document.getElementById('doAll').checked) {
		    $("#createScript").attr("checked",true);
		    $("#createFramework").attr("checked",true);
		    $("#createTable").attr("checked",true);
		     $("#databaseSetting").show();
		} else {
		    $("#createScript").attr("checked",false);
		    $("#createFramework").attr("checked",false);
		    $("#createTable").attr("checked",false);
		     $("#databaseSetting").hide();
		}
	});
});
</script>




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

					<form:checkbox path="doAll" id="doAll"></form:checkbox>
					<span>Run All</span> <br>

					<form:checkbox path="createScript" id="createScript"/>
					<span>Create Database Script</span> <br> 

					<form:checkbox path="createFramework" id="createFramework" />
					<span>Generate Parsing framework</span> <br>
					<form:checkbox path="createTable" id="createTable"/>
					<span>Create Table in Database</span> <br>
					
					<span>Choose
						Database Type</span> 
					<form:select path="databaseType" items="${databaseType}"></form:select>
					<br>
					<div id="databaseSetting">
						 <span>Database Connection String</span> <br>
						<form:textarea path="tnsEntry" rows="5" cols="20" />
						<br> <span>UserName</span>
						<form:input path="userName" />
						<br>
						<br> <span>Password</span>
						<form:input path="password" type="password"/>
						<br>
					</div>

					 <input type="hidden" path="parsedXSDPath"
						id="parsedXSDPath" name="parsedXSDPath"
						value="${xsdParseRequest.parsedXSDPath}"/> 

				</div>


			</form:form>
			
			<div class="generatebtn" align ="center">
					<button type="button" id="GenerateButton" class="upload"
						value="Generate" onclick="clickGenerate()">
						<span>Generate</span>
					</button>
					<button type="button" id="Back" class="upload"
						value="Back" onclick="clickBack()">
						<span>Back</span>
					</button>
				</div>

		</div>
	</div>

