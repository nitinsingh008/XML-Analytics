
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<link href="<c:url value="/resources/core/css/welcome.css" />"
	rel="stylesheet">
<script src="<c:url value="/resources/core/js/welcome.js"/>"></script>
<link rel="stylesheet"
	href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>
<script
	src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
<script type="text/javascript">

$(document).ready(function() {
	$("#username").attr("disabled","disabled");
	$("#password").attr("disabled","disabled");
	$("#tnsEntry").attr("disabled","disabled");
	
	$("#createTable").on('click',function(){
		if(document.getElementById('createTable').checked) {
			$("#username").attr("disabled","disabled");
			$("#password").attr("disabled","disabled");
			$("#tnsEntry").attr("disabled","disabled");
		} else {
		   $("#username").removeAttr("disabled");
			$("#password").removeAttr("disabled");
			$("#tnsEntry").removeAttr("disabled");
		}
	});
	
	$("#doAll").on('click',function(){
		if(document.getElementById('doAll').checked) {
		    $("#createScript").attr("checked",true);
		    $("#createFramework").attr("checked",true);
		    $("#createTable").attr("checked",true);
			$("#username").removeAttr("disabled");
			$("#password").removeAttr("disabled");
			$("#tnsEntry").removeAttr("disabled");
		} else {
		    $("#createScript").attr("checked",false);
		    $("#createFramework").attr("checked",false);
		    $("#createTable").attr("checked",false);	
		    $("#username").attr("disabled","disabled");
			$("#password").attr("disabled","disabled");
			$("#tnsEntry").attr("disabled","disabled");
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
	
		<div class="row col-xs-12">
			<table width="100%" style="table-layout: fixed" >
				<tr>
					<td rowspan="2" width="45%">
						<div class="xsdview_blck">
							<div class="panel-group" id="accordion" role="tablist"
								aria-multiselectable="true">
								<div class="panel panel-default">
									<div class="panel-heading" role="tab" id="headingOne">
										<h4 class="panel-title">
											<a role="button" data-toggle="collapse"
												data-parent="#accordion" href="#collapseOne"
												aria-expanded="true" aria-controls="collapseOne"> <span class="settng_span"><b>Input
												File View </b></span></a>
										</h4>
									</div>
									<div id="collapseOne" class="panel-collapse collapse in"
										role="tabpanel" aria-labelledby="headingOne">
										<div class="panel-body">
											<textarea class="textareafull" readonly="readonly">${parsedInString}</textarea>
										</div>
									</div>
								</div>
								<div class="panel panel-default">
									<div class="panel-heading" role="tab" id="headingTwo">
										<h4 class="panel-title">
											<a role="button" id="consoleOutPutLink" data-toggle="collapse"
												data-parent="#accordion" href="#collapseTwo"
												aria-expanded="false" aria-controls="collapseTwo">
												<span class="settng_span"><b>Output Console View</b></span> </a>
										</h4>
									</div>
									<div id="collapseTwo" class="panel-collapse collapse"
										role="tabpanel" aria-labelledby="headingTwo">
										<div class="panel-body">
											<textarea class="textareafull" id="consoleOutput"
												readonly="readonly"></textarea>
										</div>
									</div>
								</div>
							</div>
						</div>

					</td>
					<td width="5%"></td>
					<td width="45%">
						<div>
							<form:form commandName="xsdParseRequest"
								id="captureParseSettings" name="captureSettings">
								<table class="setting_blck" width="100%">
									<thead>
										<tr>
											<td colspan="2" style="padding:5px;">
												<h4>
													<strong>Select Setting</strong> <br>
												</h4>
											</td>
										</tr>
									</thead>
									<tbody>
										<tr>
										</tr>
										<tr>
										
											<td width="25%" style="padding-left:8px;"><form:checkbox path="doAll" id="doAll"></form:checkbox>
												<span class="settng_span">Select All</span></td>
											
												<td width="25%" ><form:checkbox path="createFramework"
													id="createFramework" /> <span class="settng_span">Generate
													Parsing framework</span></td>
										</tr>
										
										<tr>
											<td style="padding-left:8px;"><form:checkbox path="createScript" id="createScript" />
												<span class="settng_span">Create Database Script</span></td>
											<td><form:checkbox path="createTable" id="createTable" />
												<span class="settng_span">Create Table in Database</span></td>
										</tr>
										<tr>
											<td style="padding-left:8px;"><span class="settng_span">Choose Database
													Type</span></td>
											<td><form:select path="databaseType" class="dropdown btn-default dropdown-toggle"
													items="${databaseType}" style="margin-bottom:5px;"></form:select></td>
										</tr>
										<tr>
											<td style="padding-left:8px;"><span class="settng_span">Table Name PostFix</span></td>
											<td><form:input path="databaseTablePostFix"
													id="databaseTablePostFix" value="RAW" style="margin-bottom:5px;"/></td>
										</tr>
										<tr>
											<td style="padding-left:8px;"><span class="settng_span">UserName</span></td>
											<td><form:input path="userName" id="username"
													oninput="hideUserIcon()" value="CORE_REF_DATA" style="margin-bottom:5px;"/></td>
										</tr>
										<tr>
											<td style="padding-left:8px;"><span class="settng_span">Password</span></td>
											<td><form:input path="password" type="password"
													id="password" oninput="hidePsswdIcon()"
													value="CORE_REF_DATA" style="margin-bottom:5px;"/></td>
										</tr>
										<tr>
											<td style="padding-left:8px;"><span class="settng_span">Database Connection
													String</span></td>
											<td><input type="button" value="Test Connectivity"
												onclick="checkConnectivity()" id="testconn" style="margin-bottom:5px;"/></td>
										</tr>
										<tr>
											<td colspan="2" style="padding-left:8px;"><form:textarea path="tnsEntry" rows="4" cols="55"
													id="tnsEntry" /></td>
										</tr>
										<tr>
											<td align="center">
												<button type="button" id="GenerateButton" class="upload"
													value="Generate" onclick="clickGenerate()">
													<span>Generate</span>
												</button>
											</td>
											<td align="center">
												<button type="reset" class="upload">Reset</button>
											</td>
										</tr>
									</tbody>
								</table>

								<input type="hidden" path="parsedXSDPath" id="parsedXSDPath"
									name="parsedXSDPath" value="${xsdParseRequest.parsedXSDPath}" />
								<input type="hidden" path="inputType" id="inputType"
									name="inputType" value="${xsdParseRequest.inputType}" />
								<input type="hidden" path="delimiter" id="delimiter"
									name="delimiter" value="${xsdParseRequest.delimiter}" />
								<input type="hidden" id="haveHeaderData" name="haveHeaderData"
									value="${xsdParseRequest.haveHeaderData}" />

							</form:form>
						</div>
					</td>
				</tr>
				<tr>
					<td></td>
					<td align="center">
						<div id="step3jsp" class="fileupload">
							<h4 align="left" style="padding-left:8px;">
								<strong>Input File Section</strong> <br>
							</h4>
							<input id="inputXML" name="inputXML" type="file"
								class="file-loading form-control" value="Please Select XSD"
								multiple="multiple"></input>
							<button type="button" id="uploadXMLButton"
								title="Upload selected files" class="upload" value="Upload"
								onclick="uploadXMLButton()" disabled="disabled">
								<span>Parse</span>
							</button>
						</div>
					</td>
				</tr>
			</table>

			<!-- 	<div class="col-xs-1"></div> -->

		</div>
		<!-- <div class="row">
			<div class="col-xs-6"></div>
			
		</div> -->
	</div>


</div>


