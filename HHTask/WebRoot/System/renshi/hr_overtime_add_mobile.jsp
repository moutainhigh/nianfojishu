<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib uri="/WEB-INF/fenye.tld" prefix="fenye"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<meta name="viewport" content="width=device-width, initial-scale=1" />
		<%@include file="/util/sonHead.jsp"%>
	</head>
	<body>
		<%@include file="/util/sonTop.jsp"%>
		<div id="gongneng"
			style="width: 100%; border: solid 1px #0170b8; margin-top: 10px;">
			<div id="xitong"
				style="width: 100%; font-weight: bold; height: 50px;" align="left">
				<div
					style="float: left; width: 50%; padding-left: 30px; padding-top: 5px;"
					align="left">

				</div>
				<div style="float: left; width: 45%; padding-top: 5px;"
					align="right">

				</div>
			</div>
			<s:if test="successMessage!=null">
				<div align="center">
					<h5>
						<font color="red">${successMessage}</font>
					</h5>
				</div>
			</s:if>
			<s:if test="errorMessage!=null">
				<div align="center">
					<h5>
						<font color="red">${errorMessage}</font>
					</h5>
				</div>
			</s:if>
			<div align="center">
				<form action="overtimeAction!addOvertime.action" method="post"
					style="" onsubmit="return checkType();">
					<br>
					<table class="table">
						<tr>
							<td align="right">
								加班人部门:
							</td>
							<td>
								<input id="applyId" type="hidden" name="overtime.applyId"
									value="${sessionScope.Users.id}" />
								<input id="applyCode" type="hidden" name="overtime.applyCode"
									value="${sessionScope.Users.code}" />
								<input id="applyName" type="hidden" name="overtime.applyName"
									value="${sessionScope.Users.name}" />
								<input id="applyDept" type="hidden" name="overtime.applyDept"
									value="${sessionScope.Users.dept}" />
								<input id="overtimeId" type="hidden" name="overtime.overtimeId"
									value="${sessionScope.Users.id}" />
								<input id="overtimeCardId" type="hidden"
									name="overtime.overtimeCardId"
									value="${sessionScope.Users.cardId}" />
								<input id="overtimeCode" type="hidden"
									name="overtime.overtimeCode" value="${sessionScope.Users.code}" />
								<input id="overtimeName" type="hidden"
									name="overtime.overtimeName" value="${sessionScope.Users.name}" />


								<input id="overtimeDept" type="text"
									name="overtime.overtimeDept" value="${sessionScope.Users.dept}"
									readonly="readonly" />
								<!-- 
								<select id="overtimeDept" name="overtime.overtimeDept" style="color:#000000;">
									<option value="">请选择</option>
								</select>
								 -->
							</td>
						</tr>
						<tr>
							<td align="right">
								加班人:
							</td>
							<td>
								<input id="overtimeUser" type="text"
									name="overtime.overtimeUser" value="${sessionScope.Users.name}"
									readonly="readonly" />
								<!-- 
								<select id="overtimeUser" name="overtime.overtimeUser" >
									<option value="">请选择</option>
								</select>
								 -->
							</td>

						</tr>
						<tr>
							<td align="right">
								加班开始时间:
							</td>
							<td>
								<input id="joined1" class="Wdate" type="text"
									name="overtime.startDate"
									onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',skin:'whyGreen'})" />
							</td>
						</tr>
						<tr>
							<td align="right">
								加班结束时间:
							</td>
							<td>
								<input id="joined2" class="Wdate" type="text"
									name="overtime.endDate"
									onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',skin:'whyGreen'})" />
							</td>
						</tr>

						<tr>
							<td align="right">
								加班类型:
							</td>
							<td>
								<input id="overtimeType1" class="Wdate" type="radio"
									name="overtime.overtimeType" value="生产" checked="checked"
									onchange="gongchu(this.value);" />
								生产

								<input id="overtimeType2" class="Wdate" type="radio"
									name="overtime.overtimeType" value="项目"
									onchange="gongchu(this.value);" />
								项目

								<input id="overtimeType3" class="Wdate" type="radio"
									name="overtime.overtimeType" value="KVP"
									onchange="gongchu(this.value);" />
								KVP

							</td>
						</tr>
						<!--  ********************************************************************************************** -->
						<tr id="t1">
							<td align="right">
								加工件号:
							</td>
							<td>
								<!-- 
								<select name="overtime.markId" id="markId" multiple="multiple"
									size="2">

								</select>
								 -->
								<select name="overtime.markId" id="markId" style="width: 250px;">
								</select>
							</td>
						</tr>
						<tr>
							<td align="right" id="t12">
								数量:
							</td>
							<td>
								<input type="text" name="overtime.amount" value="" />
							</td>
						</tr>
						<!--  ********************************************************************************************** -->
						<tr id="t2" style="display: none;">
							<td align="right">
								项目编号:
							</td>
							<td>
								<select name="overtime.markId" id="xiangmu"
									style="width: 250px;">
								</select>
							</td>

						</tr>
						<!--  ********************************************************************************************** 	-->
						<tr id="t3" style="display: none;">
							<td align="right">
								KVP编号:
							</td>
							<td>
								<select name="overtime.markId" id="kvp" style="width: 250px;">
								</select>
							</td>
						</tr>

						<!--  ********************************************************************************************** -->
						<tr>
							<td align="right">
								是否换休:
							</td>
							<td>
<%--								<input type="radio" name="overtime.huanxiu" value="是"--%>
<%--									 />--%>
<%--								是--%>
								<input type="radio" name="overtime.huanxiu" value="否" checked="checked" />
								否
							</td>
						</tr>
						<tr>
							<th align="right">
								加班说明：
							</th>
							<td>
								<textarea rows="3" cols="25" id="shuoming" style="width: 100%"
									name="overtime.shuoming" /></textarea>
							</td>
						</tr>
						<tr>
							<td align="center" colspan="8">
								<input type="submit" value="提交"
									style="width: 100px; height: 50px;" />

								<input type="reset" value="重置"
									style="width: 100px; height: 50px;" />
							</td>
						</tr>
					</table>
				</form>
			</div>
			<br>
		</div>
		<%@include file="/util/foot.jsp"%>
		</center>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
	</body>
</html>
<script type="text/javascript">
f1();
function f1() {
	$.ajax( {
		type : "POST",
		url : "overtimeAction!finAllMarkIdForSetlectAll.action",
		dataType : "json",
		cache : false,//防止数据缓存
		success : function(object) {
			var bj = "<option></option>";
			$.each(object, function(i, obj) {
				bj += "<option value='" + obj[0]+ "/" + obj[1]
						+ "'>" + "件号" + obj[0] + "   批次" + obj[1]
						+ "</option>";
			});
			$(bj).appendTo("#markId")
		}
	});
}

$(function() {
	/*
	$('#applyCode').blur(function(){
		var applyCode=$('#applyCode').val();
		if(''!=applyCode){
			$.ajax({
				type: "get",
				dataType: "json",
				//url: "UsersAction!findByCode?user.code="+applyCode,
	            url: "overtimeAction!findUserByCode?code="+applyCode,
				async: false,
				success: function(data){
					var data=data.data;
					$('#applyCode').val(data.code);
					$('#applyName').val(data.name);
					$('#applyDept').val(data.dept);
				}
			});
		}
	});
	 */
	/*
	$.ajax( {
	type : "get",
	dataType : "text",
	url : "overtimeAction!finAllMarkIdForSetlect.action",
	async : false,
	cache : false,//防止数据缓存
	success : function(data) {
		var dept = data.split("|");
		for ( var i = 0; i < dept.length - 1; i++) {
			//var overtimeDeptItem=new Option(dept[i],dept[i]);
	$("<option value='" + dept[i] + "'>" + dept[i] + "</option>").appendTo(
			"#markId");
	}
	$("<option value=''>其它</option>").appendTo("#markId");
	}
	});
	 */
	$.ajax( {
		type : "get",
		dataType : "text",
		url : "DeptNumberAction!finAllDeptNumberForSetlect.action",
		async : false,
		success : function(data) {
			var dept = data.split("|");
			for ( var i = 0; i < dept.length - 1; i++) {
				//var overtimeDeptItem=new Option(dept[i],dept[i]);
		$("<option value='" + dept[i] + "'>" + dept[i] + "</option>").appendTo(
				"#overtimeDept");
		//$("#overtimeDept").append(overtimeDeptItem); 
	}
}
	});

	//级联查询出部门所对应的所有人员
	$("#overtimeDept").bind(
			"change",
			function() {
				if ($("#overtimeDept").val() != "") {
					$.ajax( {
						url : "UsersAction!findUsersByDept.action",
						type : 'post',
						dataType : 'json',
						cache : false,//防止数据缓存
						data : {
							deptName : $("#overtimeDept").val()
						},
						success : function(useradsfa) {
							$("#overtimeUser").empty();//清空
							$("<option value=''>请选择</option>").appendTo(
									"#overtimeUser");
							$(useradsfa).each(
									function() {
										$(
												"<option value='" + this.code
														+ "|" + this.name + "|"
														+ this.id + "|"
														+ this.cardId + "'>"
														+ this.name + this.code
														+ "</option>")
												.appendTo("#overtimeUser")
									});
						},
						error : function() {
							alert("服务器异常!");
						}
					});
				}
			});
	$("#overtimeUser").bind("change", function() {
		var overtimeUser = $("#overtimeUser").val();
		var overtimeValues = overtimeUser.split("|");
		var code = overtimeValues[0];
		var name = overtimeValues[1];
		var id = overtimeValues[2];
		var cardId = overtimeValues[3];
		$('#overtimeId').val(id);
		$('#overtimeCode').val(code);
		$('#overtimeName').val(name);
	});
});
//proAction!listPro.action",
function getf1() {
	//显示项目信息
	$.ajax( {
		type : "POST",
		url : "proAction!listPro.action",
		dataType : "json",
		cache : false,//防止数据缓存
		success : function(object) {
			var bj = "<option></option>";
			$.each(object, function(i, obj) {
				bj += "<option value='" + obj.code + "'>" + obj.code + "   "
						+ obj.name + "</option>";
			});
			$(bj).appendTo("#xiangmu")
		}
	});
	//显示KVp信息
	//-----------------------------------------------------------
	var overtimeName = document.getElementById('overtimeName').value;
	$.ajax( {
		type : "POST",
		url : "proAction!listKVP.action",
		data : {
			overName : overtimeName
		},
		dataType : "json",
		cache : false,//防止数据缓存
		success : function(object) {
			var bj = "<option></option>";
			$.each(object, function(i, obj) {
				bj += "<option value='" + obj[0] + "'>" + obj[0] + "   "
						+ obj[1] + "</option>";
			});
			$(bj).appendTo("#kvp");
		},
		error : function() {
			alert("KVP加载失败！");
		}

	});
	//------------------------
}
getf1();
function gongchu(val) {
	document.getElementById("xiangmu").value = "";
	document.getElementById("kvp").value = "";
	document.getElementById("markId").value = "";
	//项目
	if (val == "项目") {
		$("#t1").css("display", "none");
		$("#t3").css("display", "none");
		$("#t2").show();
		//tr_modifing.style.display = "table−row";
		//显示项目信息
		//-----------------------------------------------------------
		//-------------------------------------KVP------------------------------------
	} else if (val == "KVP") {
		$("#t1").css("display", "none");
		$("#t2").css("display", "none");
		$("#t3").show();

	} else {
		$("#t3").css("display", "none");
		$("#t2").css("display", "none");
		$("#t1").show();
		//------------------------------------
		//---------------------------------------

	}
}
function checkType() {
	var joined1 = document.getElementById('joined1').value;
	var joined2 = document.getElementById('joined2').value;
	if (joined1 == "") {
		alert("加班开始时间不能为空！");
		return false;
	}
	if (joined2 == "") {
		alert("加班结束时间不能为空！");
		return false;
	}
	if (joined1 > joined2) {
		alert("加班开始时间不能大于结束时间！！！");
		return false;
	}
	var shuoming = document.getElementById('shuoming').value;
	if (shuoming == "") {
		alert("加班说明不能为空");
		return false;
	}
	var xiangmu = document.getElementById('xiangmu').value;
	var kvp = document.getElementById('kvp').value;
	var markId = document.getElementById('markId').value;
	var value = "";
	var obj2 = document.getElementsByName('overtime.overtimeType');
	for ( var i = 0; i < obj2.length; i++) {
		if (obj2[i].checked == true) {
			value = obj2[i].value;
			break;
		}
	}

	if (value == "项目") {
		if (xiangmu == "") {
			alert("项目不能为空");
			return false;
		}
		var amount = document.getElementById('amount').value;
		if (amount == "") {
			alert("加工件号不能为空！");
			return false;
		}
	}

	if (value == "kvp") {
		if (kvp == "") {
			alert("kVP不能为空");
			return false;
		}
	}
	if (value == "生产") {
		if (markId == "") {
			alert("加工件号不能为空");
			return false;
		}
	}
	return true;
}
</script>
