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
		<script type="text/javascript">
//if (/Android|webOS|iPhone|iPod|BlackBerry/i.test(navigator.userAgent)) {
//	window.location.href = "/System/renshi/qingjiamobile/insertLeve.jsp";
//}
</script>
		<%@include file="/util/sonHead.jsp"%>
	</head>
	<body>
		<%@include file="/util/sonTop.jsp"%>
		<div id="gongneng">
			<!-- 显示弹出层 -->
			<div id="bodyDiv" align="center" class="transDiv"
				onclick="chageDiv('none')">
			</div>
			<div id="contentDiv"
				style="position: absolute; z-index: 255; width: 900px;height:800px; display: none;"
				align="center">
				<div id="closeDiv"
					style="background: url(<%=basePath%>/images/bq_bg2.gif); width: 100%;">
					<center>
					<table style="width: 100%">
						<tr>
							<td>
								
							</td>
							<td align="right">
								<img alt="" src="<%=basePath%>/images/closeImage.png" width="30"
									height="32" onclick="chageDiv('none')">
							</td>
						</tr>
					</table>
					</center>
					<div id="operatingDiv"
						style="background-color: #ffffff; width: 100%;">
						<iframe id="showDialogs" src="" marginwidth="0" marginheight="0"
							hspace="0" vspace="0" frameborder="0" scrolling="yes"
							style="width: 98%; height: 550px; margin: 0px; padding: 0px;"></iframe>
					</div>
				</div>
			</div>
			<!-- 显示弹出层结束 -->
			<div align="center">
				<h1>
					<font color="#46d0f5">申请请假</font>
					<s:if test="errorMessage!=null">
						<br />
						<font color="red"><s:property value="errorMessage" /> </font>
					</s:if>
				</h1>
				<form id="saveForm" action="AskForLeaveAction!saveOrUpdate.action?tag=${tag}"
					method="post" onsubmit="return checkType();">
					<input type="hidden" name="askForLeave.appayTag"
						value="${askForLeave.appayTag}" />
					<table class="table">
						<tr>
							<th align="right" style="width: 35%">
								请假类型:
							</th>
							<td align="left">
								<input id="" type="radio" name="askForLeave.leaveType"
									value="个人请假" checked="checked" onclick="changeType('1')" />
								个人请假
								<s:if test="pageStatus!='one'">
									<input type="radio" name="askForLeave.leaveType" value="代理请假"
										onclick="changeType('2')" />
									代理请假
								</s:if>
							</td>
						</tr>
						<tr>
							<th align="right">
								请假人所在部门:
							</th>
							<td align="left">
								<select id="dept" style="width: 155px;"
									name="askForLeave.leavePersonDept">
									<option value="${Users.dept}">
										${Users.dept}
									</option>
								</select>
							</td>
						</tr>
						<tr>
							<th align="right">
								请&nbsp;假&nbsp;人:&nbsp;
							</th>
							<td align="left">
								<input id="userName" type="text" name="askForLeave.leavePerson"
									readonly="readonly" value="${Users.name}">

								<select id="users" style="display: none; width: 155px;">
								</select>
							</td>
						</tr>
						<tr>
							<th align="right">
								请假人工号:
							</th>
							<td align="left">
								<input type="text" id="leavePersonCode"
									name="askForLeave.leavePersonCode" value="${Users.code}"
									readonly="readonly" />
							</td>
						</tr>
						<tr>
							<th align="right">
								请假时间:
							</th>
							<td align="left">
								<input class="Wdate" type="text" id="startDate"
									name="askForLeave.leaveStartDate" onblur="complateDuration()"
									onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:00',skin:'whyGreen'})" />
								至
								<input class="Wdate" type="text" id="endDate"
									name="askForLeave.leaveEndDate" onblur="complateDuration()"
									onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:00',skin:'whyGreen'})" />
								<font id="showDuration" style="color:red;"></font>
							</td>
						</tr>
						<tr>
							<th align="right">
								假事类型:
							</th>
							<td align="left">
								<s:radio id="radioType"
									list="#{'事假':'事假','病假':'病假','丧假':'丧假','婚假':'婚假','换休':'换休','年休':'年休','公出':'公出','产假':'产假'}"
									listKey="key" label="假事类型" required="true"
									onclick="gongchu(this);" requiredposition="center"
									listValue="value" value="事假" name="askForLeave.leaveTypeOf"
									cssClass="fileType" />
								<div id="feigongchu">
									是否借车：

									<input type="radio" name="che" onclick="onshi('否')"
										checked="checked" />
									否
									<input type="radio" name="che" onclick="onshi('是')" />
									是
									<SPAN id="carhao" style="display: none;">车牌号： <input
											type="text" name="askForLeave.carPaiNum" id="carPaiNum" /> </SPAN>
								</div>

								<div id="gongchuStyle" style="display: none;">
									<hr />
									选择公出目的地：
									<select name="askForLeave.gongchuPlace">
										<option value="省内">
											省内
										</option>
										<option value="省外">
											省外
										</option>
										<option value="国外">
											国外
										</option>
									</select>
									&nbsp;&nbsp;用车：
									<input type="radio" value="是" class="needcar"
										name="askForLeave.needCar" onclick="needcar('是')">
									是
									<input type="radio" value="否" class="needcar" checked="checked"
										name="askForLeave.needCar" onclick="needcar('否')">
									否
									<div id="tpdiv">
										<hr />
										请选择项目类型:
										<input id="LeaveType1" class="Wdate" type="radio"
											name="askForLeave.leaveObjectType" value="生产"
											checked="checked" onchange="objectType(this.value);" />
										生产
										<input id="LeaveType2" class="Wdate" type="radio"
											name="askForLeave.leaveObjectType" value="项目"
											onchange="objectType(this.value);" />
										项目
										<input id="LeaveType3" class="Wdate" type="radio"
											name="askForLeave.leaveObjectType" value="KVP"
											onchange="objectType(this.value);" />
										KVP
										<input id="LeaveType3" class="Wdate" type="radio"
											name="askForLeave.leaveObjectType" value="其他"
											onchange="objectType(this.value);" />
										其他
									</div>
									<div id="d1">
										加工件号:
										<select name="askForLeave.leaveObjectNeirong" id="markId"
											style="width: 250px;">
										</select>
									</div>
									<div id="d2" style="display: none;">
										项目编号:
										<select name="askForLeave.leaveObjectNeirong" id="xiangmu"
											style="width: 250px;">
										</select>
									</div>
									<div id="d3" style="display: none;">
										KVP编号:
										<select name="askForLeave.leaveObjectNeirong" id="kvp"
											style="width: 250px;">
										</select>
									</div>
									<div id="d4" style="display: none;">
										<table>
											<tr>
												<td align="left" style="border: 0px;">
													其他原因:
												</td>
												<td style="border: 0px;">
													<textarea name="askForLeave.leaveObjectNeirong" id="qiTa"
														rows="3" cols="40"></textarea>
												</td>
											</tr>
										</table>
									</div>
									<hr />
									<!-- 选择地图位置 -->
									<div id="selectMap" >
										公出位置：<input type="button" value="选择公出位置" onclick="showMapDialog()">
										<input type="hidden" name="askForLeave.lng" id="lng">
										<input type="hidden" name="askForLeave.lat" id="lat">
										<font id="showMapMessage" style="color: green;"></font>
									</div>
								</div>
								<div id="huanxiuxieyi" style="display: none;">
								</div>
								<div id="freeDeptDiv" style="display: none;">
								<hr />
								<font color="red">费用承担部门与审批人:</font><input type="button" value="增加" onclick="addFreeDept()" style="width: 60px;height: 30px">
								<ul id="freeDeptUl0">
									<li id="freeDeptli0">
									 <SELECT id="zrdept0" name="approvalId" onchange="changefreeDept(0)"></SELECT>
									 <SELECT id="zrpeople0" name="ids"></SELECT>
									 <input type="button" value="删除" onclick="deleteFreeDept(0)" style="width: 60px;height: 30px">
									</li>
								</ul>
								</div>
							</td>
						</tr>
						<tr>
							<th align="right">
								请假原因:
							</th>
							<td align="left">
								<s:textarea label="请假原因" name="askForLeave.leaveReason"
									id="reason" cols="50" rows="10"
									value="%{#attr.askForLeave.leaveReason}" />
							</td>
						</tr>
						<!-- singleCarAction_addSingleCar.action -->
						<tr id='carTr' style="display: none;">
							<table id='carTab' style="width: 98%; display: none;"
								class="table">
								<tr>
									<th colspan="4">
										申请用车单
									</th>
								</tr>
								<tr>
									<th>
										到达地点
									</th>
									<th colspan="1" align="left">
										<input type="text" id="car_place"
											name="askForLeave.singleCar.car_place" size="70%"><font color="red">*</font>
									</th>
										<th style="width: 15%;">
										早出晚归
									</th>
									<th align="left">
									<SELECT name="askForLeave.singleCar.zcwg">
										<option>否</option>
										<option>是</option>
									</SELECT><font color="red">*</font>
									</th>
								</tr>
								<tr>
									<th style="width: 15%;">
										出车前里程表
									</th>
									<th align="left">
										<input type="text" name="askForLeave.singleCar.beforeodometer"
											onkeyup="if(isNaN(value))execCommand('undo')">
									</th>
										<th style="width: 15%;">
										人数及吨位
									</th>
									<th align="left">
										<input type="text" name="askForLeave.singleCar.car_amount"
											value="" onkeyup="this.value=this.value.replace(/[^\d]/g,'')">
									</th>
								</tr>
								<tr>
									<th style="width: 15%;">
										预估公里数
									</th>
									<th align="left">
										<input type="text" name="askForLeave.singleCar.kilometers"
											onkeyup="if(isNaN(value))execCommand('undo')"><font color="red">*</font>
									</th>
									<th style="width: 15%;">
										驾驶员姓名
									</th>
									<th align="left">
										<input type="text" name="askForLeave.singleCar.pilotname"><font color="red">*</font>
									</th>
								</tr>
								<tr>
									<th style="width: 15%;">
										车牌号
									</th>
									<th align="left">
										<select id="car_number" name="askForLeave.singleCar.car_number">
										</select><font color="red">*</font>
									</th>
									<th style="width: 15%;">
										用车单位领导
									</th>
									<th align="left">
										<input type="text" name="askForLeave.singleCar.unit_leading">
									</th>
								</tr>
								<tr>
									<th>
										用车类型
									</th>
									<th align="left">
										<SELECT name="askForLeave.singleCar.car_useType">
											<option>
												处理质量事故
											</option>
											<option>
												市场开发
											</option>
											<option>
												产品试装
											</option>
											<option>
												财务相关
											</option>
											<option>
												PEBS系统维护
											</option>
										</SELECT><font color="red">*</font>
									</th>
									<th>
										备注
									</th>
									<th align="left">
										<input type="text" name="askForLeave.singleCar.remark">
									</th>
								</tr>
							</table>
						</tr>
						<tr>
							<td align="center" colspan="2">
								<input type="button" class="input" value="请假" id="tijiao"
									onclick="nianxiu()" />
								<input type="reset" class="input" value="重置" />
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
		<script type="text/javascript">
function disable() {
	document.getElementById("tijiao").disabled = true
}
function enable() {
	document.getElementById("tijiao").disabled = false
}
function onshi(obj) {
	if (obj == '是') {
		$("#carhao").show();
		$("#carhao").removeAttr("disabled");
	} else {
		$("#carhao").hide();
		$("#carhao").attr("disabled", "disabled");
	}
}

function nianxiu() {
	var s = checkType();
	if (s) {
		var temp = document.getElementsByName("askForLeave.leaveTypeOf");
		var nianxiuvalue;
		for (i = 0; i < temp.length; i++) {
			if (temp[i].checked) {
				nianxiuvalue = temp[i].value;
				break;
			}
		}
		var startDate = document.getElementById("startDate").value;
		var endDate = document.getElementById("endDate").value;
		var leavePersonCode = document.getElementById("leavePersonCode").value;
		//var tijiao=document.getElementById("tijiao");
		//------------------------------------------------------------------------
		if ("年休" == nianxiuvalue) {
			$
					.ajax( {
						type : "POST",
						url : "AskForLeaveAction!panduannianxiu.action?askForLeave.leaveStartDate="
								+ startDate
								+ "&askForLeave.leaveEndDate="
								+ endDate
								+ "&askForLeave.leavePersonCode="
								+ leavePersonCode,
						dataType : "json",
						success : function(object) {
							alert(object);
							if (object == "您可以申请年休!") {
								document.getElementById("saveForm").submit();
								return;
							}
						}
					});
		}
		//--------------------------------------------
		else if ("换休" == nianxiuvalue) {
			$
					.ajax( {
						type : "POST",
						url : "AskForLeaveAction!panduanhuanxiu.action?askForLeave.leaveStartDate="
								+ startDate
								+ "&askForLeave.leaveEndDate="
								+ endDate+"&askForLeave.leavePersonCode="+leavePersonCode,
						dataType : "json",
						success : function(object) {
							if(object!=null){
								var str = object[0]+"\n请假时长："+object[1]+"(天),"+object[2]+"(小时)\n可用换休时长："+object[3]+"(天)";
								if (object[0] == "您可以申请换休!") {
									str+="\n确认请假吗？";
									if(confirm(str)){
										document.getElementById("saveForm").submit();
										return;
									}
								}else{
									alert(str);
								}
							}else{
								alert("返回数据失败");
							}
						}
					});
		}else {
			document.getElementById("saveForm").submit();
			return;
		}/*贾辉辉要求加上换休控制2017-01-11*/
		//------------------------换休------------------------------------------------------
		/*	if ("换休" == nianxiuvalue) {
				$
						.ajax( {
							type : "POST",
							url : "AskForLeaveAction!panduanhuanxiu.action?askForLeave.leaveStartDate="
									+ startDate
									+ "&askForLeave.leaveEndDate="
									+ endDate+"&askForLeave.leavePersonCode="+leavePersonCode,
							dataType : "json",
							success : function(object) {
								alert(object);
								if (object == "您可以申请换休!") {
									document.getElementById("saveForm").submit();
									return;
								}
							}
						});
			} else {
				document.getElementById("saveForm").submit();
				return;
			}
		 */
		//--------------------------------------------------------------------------------------
	}
}
//判断年休
/*
 function nianxiu(nianxiu){
 var startDate=document.getElementById("startDate").value;
 var endDate=document.getElementById("endDate").value;
 alert(nianxiu);
 if("年休"==nianxiu){
 alert("1111  ..   "+startDate+"  ..  "+endDate);
 window.location.href="AskForLeaveAction!panduannianxiu.action?askForLeave.leaveStartDate="+startDate+"&askForLeave.leaveEndDate="+endDate;
 }
 }
 */
getxieyi();
function getxieyi() {
	$
			.ajax( {
				type : "POST",
				url : " AskForLeaveAction!listhuanxiuxieyi.action",
				dataType : "json",
				success : function(object) {
					var reshtml = "<table class='table' id='t2'><tr><td rowspan='5' align='center'>换休说明</td></tr>";
					var th = "";

					$.each(object, function(i, obj) {
						th += "<tr ><td align='left'>" + (i + 1) + "."
								+ obj.content + "</td></tr>"
					});
					reshtml = reshtml
							+ th
							+ "<tr><td><input type='checkBox' id='tongyi' onclick='if (this.checked) {enable()} else {disable()}' >我同意此协议</td></tr></table>";
					$(reshtml).appendTo("#huanxiuxieyi");

					//--------------
					/*
					$(object).each(
							function() {
								th += "<tr ><th>" + this.content + "</th></tr>"
							});
					
					reshtml = reshtml + th + "</table>";
					$(reshtml).appendTo("#huanxiuxieyi");
					
					 {
					$("#gongchuStyle").hide();
					}
					 */
				}

			});

}

//项目编号
function objectType(val) {
	document.getElementById("xiangmu").value = "";
	document.getElementById("kvp").value = "";
	document.getElementById("markId").value = "";
	document.getElementById("qiTa").value = "";
	//项目
	if (val == "项目") {
		$("#d1").css("display", "none");
		$("#d3").css("display", "none");
		$("#d4").css("display", "none");
		$("#markId").attr("disabled", "disabled");
		$("#qiTa").attr("disabled", "disabled");
		$("#kvp").attr("disabled", "disabled");
		$("#xiangmu").removeAttr("disabled");
		$("#d2").show();
		//tr_modifing.style.display = "table−row";
		//显示项目信息
		//-----------------------------------------------------------
		//-------------------------------------KVP------------------------------------
	} else if (val == "KVP") {
		$("#d1").css("display", "none");
		$("#markId").attr("disabled", "disabled");
		$("#qiTa").attr("disabled", "disabled");
		$("#xiangmu").attr("disabled", "disabled");
		$("#kvp").removeAttr("disabled");
		$("#d2").css("display", "none");
		$("#d4").css("display", "none");
		$("#d3").show();

	} else if (val == "其他") {
		$("#d1").css("display", "none");
		$("#d2").css("display", "none");
		$("#d3").css("display", "none");
		$("#markId").attr("disabled", "disabled");
		$("#xiangmu").attr("disabled", "disabled");
		$("#kvp").attr("disabled", "disabled");
		$("#qiTa").removeAttr("disabled");
		$("#d4").show();

	} else {
		$("#d3").css("display", "none");
		$("#d2").css("display", "none");
		$("#d4").css("display", "none");
		$("#xiangmu").attr("disabled", "disabled");
		$("#qiTa").attr("disabled", "disabled");
		$("#kvp").attr("disabled", "disabled");
		$("#markId").removeAttr("disabled");
		$("#d1").show();
		//------------------------------------
		//---------------------------------------
	}
}

/**
 //项目编号
 function objectType(val) {
 document.getElementById("xiangmu").value = "";
 document.getElementById("kvp").value = "";
 document.getElementById("markId").value = "";
 document.getElementById("qiTa").value = "";
 //项目
 if (val == "项目") {
 $("#d1").css("display", "none");
 $("#d3").css("display", "none");
 $("#d4").css("display", "none");
 $("#d2").show();
 //tr_modifing.style.display = "table−row";
 //显示项目信息
 //-----------------------------------------------------------
 //-------------------------------------KVP------------------------------------
 } else if (val == "KVP") {
 $("#d1").css("display", "none");
 $("#d2").css("display", "none");
 $("#d4").css("display", "none");
 $("#d3").show();

 } else if (val == "其他") {
 $("#d1").css("display", "none");
 $("#d2").css("display", "none");
 $("#d3").css("display", "none");
 $("#d4").show();

 } else {
 $("#d3").css("display", "none");
 $("#d2").css("display", "none");
 $("#d4").css("display", "none");
 $("#d1").show();
 //------------------------------------
 //---------------------------------------

 }
 }**/
//获取数据
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
				bj += "<option value='" + obj.code + " / " + obj.name + "'>"
						+ obj.code + "   " + obj.name + "</option>";
			});
			$(bj).appendTo("#xiangmu")
		}
	});
	//显示KVp信息
	//-----------------------------------------------------------
	var userName = document.getElementById('userName').value;
	$.ajax( {
		type : "POST",
		url : "proAction!listKVP.action",
		data : {
			overName : userName
		},
		dataType : "json",
		cache : false,//防止数据缓存
		success : function(object) {
			var bj = "<option></option>";
			$.each(object, function(i, obj) {
				bj += "<option value='" + obj[0] + " / " + obj[1] + "'>"
						+ obj[0] + "   " + obj[1] + "</option>";
			});
			$(bj).appendTo("#kvp");
		},
		error : function() {
			alert("KVP加载失败！");
		}

	});
	//------------------------
}

f1();
getf1();
function f1() {
	$.ajax( {
		type : "POST",
		url : "overtimeAction!finAllMarkIdForSetlectAll.action",
		dataType : "json",
		cache : false,//防止数据缓存
		success : function(object) {
			var bj = "<option></option>";
			$.each(object, function(i, obj) {
				bj += "<option value='" + "件号 " + obj[0]+ "  /批次 "
						+ obj[1] + "'>" + "件号" + obj[0] + "   批次"
						+ obj[1] + "</option>";
			});
			$(bj).appendTo("#markId")
		}
	});
}

//处理公出
function gongchu(obj) {
	var qjStyle = obj.value;
	$("#freeDeptDiv").hide();
// 	$("#selectMap").show();
	if ("公出" == qjStyle) {
		var leaveObjectType=$("#tpdiv input[name='askForLeave.leaveObjectType']:checked ").val();
		objectType(leaveObjectType);
		$("#gongchuStyle").show();
		$("#huanxiuxieyi").hide();
		//$("#tijiao").attr("disabled","false");
		$("#tijiao").removeAttr("disabled");

		$("#feigongchu").hide();
		$("#carhao").attr("disabled", "disabled");
		$("#freeDeptDiv").show();
// 		$("#selectMap").show();
		if($("#zrdept0").val()==null){
			setDept(0);
		}
	} else if ("换休" == qjStyle) {
		//-----------------------------------------------------------------
		$("#huanxiuxieyi").show();
		//disabled="true"
		//$("#tijiao").disabled="true";tijiao
		$("#tijiao").attr("disabled", "true");
		$("#gongchuStyle").hide();

		$("#feigongchu").hide();
		$("#carhao").attr("disabled", "disabled");
		//------------------------------------------------------------------
	} else {
		$("#huanxiuxieyi").hide();
		$("#gongchuStyle").hide();
		$("#tijiao").removeAttr("disabled");

		$("#feigongchu").show();
		$("#carhao").removeAttr("disabled");
	}
}
//单选按钮的鼠标点击触发事件
function changeType(type) {
	if (type == "1") {
		$("#userName").val("${Users.name}");
		$("#userName").css("display", "block");
		$("#users").css("display", "none");
		$("#userName").attr("readonly", "readonly");
		$("#dept").empty();
		$("<option value='${Users.dept}'>${Users.dept}</option>").appendTo(
				"#dept");
	} else if (type == "2") {
		$("#userName").css("display", "none");
		$("#users").css("display", "block");
		$("#leavePersonCode").val("");
		//显示所有部门信息
		$.ajax( {
			url : 'DeptNumberAction!findAllDept.action',
			dataType : 'json',
			cache : false,//防止数据缓存
			success : function(allDdept) {
				$("#dept").empty();
				$("<option value=''>--请选择部门--</option>").appendTo("#dept");
				$(allDdept).each(
						function() {
							$(
									"<option value='" + this.dept + "'>"
											+ this.dept + "</option>")
									.appendTo("#dept");
						});
			}

		});
		//显示部门对应的员工信息
		$("#dept").bind(
				"change",
				function() {
					if ($("#dept").val() != "") {
						$.ajax( {
							url : "UsersAction!findUsersByDept.action",
							type : 'post',
							dataType : 'json',
							cache : false,//防止数据缓存
							data : {
								deptName : $("#dept").val()
							},
							success : function(useradsfa) {
								$("#users").empty();//清空
								$("<option></option>").appendTo("#users");
								$(useradsfa).each(
										function() {
											$(
													"<option value='"
															+ this.code + "|"
															+ this.name + "'>"
															+ this.name
															+ "</option>")
													.appendTo("#users")
										});
							},
							error : function() {
								alert("服务器异常!");
							}
						});
					}

				});

	}
	$("#users").bind("change", function() {
		var users = $("#users").val();
		var usersData = users.split("|");
		var code = usersData[0];
		$("#userName").val(usersData[1]);
		$("#leavePersonCode").val(code);
	});

}
function checkType() {
	var val = document.getElementById('startDate').value;
	var val2 = document.getElementById('endDate').value;
	var val3 = document.getElementById('reason').value;

	var userName = document.getElementById('userName').value;
	var leavePersonCode = document.getElementById('leavePersonCode').value;

	if (!val || val == "" || !val2 || val2 == "") {
		alert("时间不能为空，请选择请假开始和结束时间！！！");
		return false;
	} else if (!val3 || val3 == "") {
		alert("请假原因不能为空，请准确填写请假原因！！！");
		return false;
	}
	if (val > val2) {
		alert("请假开始时间不能大于结束时间！！！");
		return false;
	}
	if (userName = "") {
		alert("请假人不能为空！！！");
		return false;
	}
	if (leavePersonCode == "") {
		alert("请假工号不能为空！！！");
		return false;
	}
	/*
	alert(radioType);
	if (radioType ="换休") {
		//tongyi
		alert("test");
		var tongyi = document.getElementById('tongyi').value;
		if(tongyi==false){
			alert("请同意换休协议！！！");
		}
		return false;
	}*/
	var obj2 = document.getElementsByName('askForLeave.leaveTypeOf');
	var error = '';
	/* 非空验证 ******************************** */
	var i = 0, type = false;
	for (i = 0; i < obj2.length; i++) {
		if (obj2[i].checked)
			type = true;
	}
	if (type == false) {
		error = "请选择假事类型！\n";
		alert(error);
	} else {
		return true;
	}
	return false;
}
function needcar(obj) {
	if (obj == '是') {
		$("#carTab").show();
		$("#carTr").show();
		var car_number=$("#car_number").val();
		if(car_number==null||car_number==""){
			$.ajax( {
			type : "post",
			url : "AskForLeaveAction!getCarNumber.action",
			dataType : "json",
			success : function(data) {
			//填充部门信息
				$(data).each(
					function() {
						var html="<option value='" + this + "'>" + this+ "</option>";
						$(html).appendTo("#car_number");
					});
					$("#car_number").tinyselect();
			}
			});
		}
	} else {
		$("#carTab").hide();
		$("#carTr").hide();
	}
	;
}
var deptIndex=0;
function setDept(i){
	$.ajax( {
		type : "post",
		url : "GzstoreAction_getdept.action",
		dataType : "json",
		success : function(data) {
			//填充部门信息
			$(data).each(
					function() {
						var html="";
						if(this.dept=="${Users.dept}"){
							html="<option selected='selected' value='" + this.id + "'>" + this.dept+ "</option>";
						}else{
							html="<option value='" + this.id + "'>" + this.dept+ "</option>";
						}
						$(html).appendTo("#zrdept"+i);
					});
				changefreeDept(i);
				$("#zrdept"+i).tinyselect();
		}
	});
}
function changefreeDept(i){
	var deptId=$("#zrdept"+i).val();
	if(deptId>0){
	$.ajax( {
		type : "post",
		url : "AskForLeaveAction!getDeptUsers.action",
		dataType : "json",
		data : {
			id : deptId
		},
		success : function(data) {
			//填充部门信息
			var selectbox=$("#freeDeptUl"+i+" .tinyselect");
			if(selectbox.length>1){
				var len=selectbox.length-1;
				for(var n=len;n>=1;n--){
					$(selectbox[n]).remove();
				}
			}
			$("#zrpeople"+i).empty();
			$(data).each(
					function() {
						var html="<option value='" + this.userId + "'>" + this.userName+ "</option>";
						$(html).appendTo("#zrpeople"+i);
					});
			$("#zrpeople"+i).tinyselect();
			
		}
	});
	}
}
function addFreeDept(){
	deptIndex++;
	var html="<ul id='freeDeptUl"+deptIndex+"'>" +
	"<li id='freeDeptli"+deptIndex+"'>" +
	"<SELECT id='zrdept"+deptIndex+"' name='approvalId' onchange='changefreeDept("+deptIndex+")'></SELECT>" +
	"<SELECT id='zrpeople"+deptIndex+"' name='ids'></SELECT>" +
	"<input type='button' value='删除' onclick='deleteFreeDept("+deptIndex+")' style='width: 60px;height: 30px'>" +
	"</li></ul>"
	$(html).appendTo("#freeDeptDiv");
	setDept(deptIndex);
}
function deleteFreeDept (index){
	$("#freeDeptUl"+index).remove();
}

function showMapDialog(){
	$("#showDialogs").attr("src", "${pageContext.request.contextPath}/System/renshi/qingjia/showMap.jsp");	
	chageDiv('block');
}

function complateDuration(){
	var startDate = $("#startDate").val();
	var endDate = $("#endDate").val();
	if(startDate!=null && startDate!="" && endDate!=null && endDate!=""){
		$.ajax({
			type:"post",
			url:"${pageContext.request.contextPath}/AskForLeaveAction!getLeaveTime.action",
			data:{
				"askForLeave.leaveStartDate":startDate,
				"askForLeave.leaveEndDate":endDate,
				"id":"${Users.id}"
			},
			dataType:"json",
			success:function(data){
				if(data!=null){
					if(data.success){
						$("#showDuration").text("请假时长："+data.data1+"天，"+data.data2+"小时");
					}else{
						$("#showDuration").text("计算时长异常："+data.message);
					}
				}
			},error:function(){
				alert("系统计算请假时长异常。");
			}
		});
	}else{
		$("#showDuration").text("");
	}
	
}
</script>
	</body>
</html>

