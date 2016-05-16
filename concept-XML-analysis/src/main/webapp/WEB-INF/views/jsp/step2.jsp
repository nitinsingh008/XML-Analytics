
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

function checkConnectivity()
{
		var tns = $("#tnsEntry").val();
		var username = $("#username").val();
		var password = $("#password").val();
		if(tns== null || tns == ''){
			alert("Please enter TNS entry");
			return;
		}
		$.ajax({
					    url: 'checkConnectivity',
					    data: {
					  		DatabaseType : $("#databaseType").val(),
					    	tns : tns,
					    	username : username,
					    	password : password
					    },
					  
					    type: 'POST',
						
					    success: function(data){
					   		alert(data);
					    }
					  });
	}


	function hideUserIcon() {
		document.getElementById("username").style.backgroundImage = 'none';
	}
	function hidePsswdIcon() {
		document.getElementById("password").style.backgroundImage = 'none';
	}
</script>




<div class="container" id="container-jsp2">
	<div class="xsd_container">
		<div class="row">
			<div class="xsdview_blck col-xs-6">
			
				<div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">
				  <div class="panel panel-default">
				    <div class="panel-heading" role="tab" id="headingOne">
				      <h4 class="panel-title">
				        <a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseOne" aria-expanded="true" aria-controls="collapseOne">
				         Input File View 
				        </a>
				      </h4>
				    </div>
				    <div id="collapseOne" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="headingOne">
				      <div class="panel-body">
				       	<textarea class="textareafull" readonly="readonly">${parsedInString}</textarea>
				      </div>
				    </div>
				  </div>
				  <div class="panel panel-default">
				    <div class="panel-heading" role="tab" id="headingTwo">
				      <h4 class="panel-title">
				        <a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseTwo" aria-expanded="false" aria-controls="collapseTwo">
				          Console Output
				        </a>
				      </h4>
				    </div>
				    <div id="collapseTwo" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingTwo">
				      <div class="panel-body">
				      	<textarea class="textareafull" id="consoleOutput" readonly="readonly"></textarea>
				      </div>
				    </div>
				  </div>
				</div>
			</div>
			<div class="col-xs-1"></div>
			<div class="setting_blck col-xs-5">
				<form:form commandName="xsdParseRequest" id="captureParseSettings"
					name="captureSettings">
						<h4>
							<strong>Select Setting</strong> <br>
						</h4>
						<form:checkbox path="doAll" id="doAll"></form:checkbox>
						<span class="settng_span">Select All</span> <br>

						<form:checkbox path="createScript" id="createScript" />
						<span class="settng_span">Create Database Script</span> <br>

						<form:checkbox path="createFramework" id="createFramework" />
						<span class="settng_span">Generate Parsing framework</span> <br>
						<form:checkbox path="createTable" id="createTable" />
						<span class="settng_span">Create Table in Database</span> <br>

						<span class="settng_span">Choose Database Type</span>
						<form:select path="databaseType" items="${databaseType}"></form:select>
						<br>
						<div id="databaseSetting">
							<span class="settng_span">Table Name PostFix</span>
							<form:input path="databaseTablePostFix" id="databaseTablePostFix" />
							<br> <span class="settng_span">Database Connection
								String</span> <br>
							<form:textarea path="tnsEntry" rows="2" cols="45" id="tnsEntry" />

							<br> <span class="settng_span">UserName&nbsp;</span>
							<form:input path="userName" id="username"
								oninput="hideUserIcon()" />
							<br> <span class="settng_span">Password&nbsp;&nbsp;&nbsp;</span>
							<form:input path="password" type="password" id="password"
								oninput="hidePsswdIcon()" />
							<br> <input type="button" value="Test Connectivity"
								onclick="checkConnectivity()" id="testconn" />


						</div>

						<input type="hidden" path="parsedXSDPath" id="parsedXSDPath"
							name="parsedXSDPath" value="${xsdParseRequest.parsedXSDPath}" />
						<input type="hidden" path="inputType" id="inputType"
							name="inputType" value="${xsdParseRequest.inputType}" /> <input
							type="hidden" path="delimiter" id="delimiter" name="delimiter"
							value="${xsdParseRequest.delimiter}" /> <input type="hidden"
							path="haveHeaderData" id="haveHeaderData" name="haveHeaderData"
							value="${xsdParseRequest.haveHeaderData}" />
							<br>
						<button type="button" id="GenerateButton" class="upload"
								value="Generate" onclick="clickGenerate()">
								<span>Generate</span>
							</button>
							<!-- <button type="button" id="Back" class="upload" value="Back"
								onclick="clickBack()">
								<span>Back</span>
							</button> -->
					
				</form:form>
			</div>
		</div>
		<div class="row">
			<div class="col-xs-5" id="step3jsp">
			
			</div>
		</div>
	</div>


</div>
<%-- <div class="footer_jsp2">			
			<h6 align="center">The Concept Crew &copy; markit.com 2016</h6>
			<h6 align="center">Gaurav Agarwal | Nitin Singh | Parag Garg</h6>
			 <img src="<c:url value='/resources/images/logo-1.png'/>" id="concpt_crew_logo" />  --%>


