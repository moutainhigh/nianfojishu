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
		
<script type="text/javascript"
	src="<%=basePath%>/javascript/DatePicker/WdatePicker.js">
</script>
<meta http-equiv="X-UA-Compatible" content="IE=Edge">
<title>${moduleFunction.functionName},生产力生态平衡系统</title>
<link rel="shortcut icon" href="/upload/file/sysImages/favicon.ico" />
<link type="text/css" rel="stylesheet"
	href="${pageContext.request.contextPath}/css/index.css" />

<script type="text/javascript"
	src="${pageContext.request.contextPath}/javascript/DatePicker/WdatePicker.js">
</script>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/javascript/zTree/css/zTreeStyle/zTreeStyle.css"
	type="text/css">
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/css/css.css" />
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/javascript/kindeditor-master/themes/default/default.css" />
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/javascript/kindeditor-master/plugins/code/prettify.css" />
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/tinyselect.css" />
<script type="text/javascript"
	src="${pageContext.request.contextPath}/javascript/jquery-1.8.3.js">
</script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/javascript/javascript.js">
</script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/javascript/zTree/js/jquery.ztree.core-3.5.js">
</script>
<script charset="utf-8"
	src="${pageContext.request.contextPath}/javascript/kindeditor-master/kindeditor.js">
</script>
<script charset="utf-8"
	src="${pageContext.request.contextPath}/javascript/kindeditor-master/lang/zh_CN.js">
</script>
<script charset="utf-8"
	src="${pageContext.request.contextPath}/javascript/kindeditor-master/plugins/code/prettify.js">
</script>
<script language="javascript"
	src="${pageContext.request.contextPath}/javascript/jquery.jqprint.js">
</script>
<script language="javascript"
	src="${pageContext.request.contextPath}/javascript/json2.js">
</script>
<script language="javascript"
	src="${pageContext.request.contextPath}/js/awardRotate.js">
</script>
<script language="javascript"
	src="${pageContext.request.contextPath}/javascript/jquery.qrcode.min.js">
</script>
<script language="javascript"
	src="${pageContext.request.contextPath}/javascript/piliangshangchuan/plupload.full.min.js">
</script>
<script language="javascript"
	src="${pageContext.request.contextPath}/js/jqmeter.min.js">
</script>
<script type="text/javascript">
$(function() {
	//调整高度
	autoHeight();
	//为select统一添加查询搜索
	$("select.cxselect").each(function() {
		$(this).tinyselect();
	});
})
</script>
	</head>
	<body>
		<%@include file="/util/sonTop.jsp"%>
		<div id="gongneng"
			style="width: 100%; border: solid 1px #0170b8; margin-top: 10px;">
			<div align="center">
				<font color="red" size="5px"><b>${errorMessage}</b> </font>
				<div style="font-weight: bolder; font-size: 18px;">
					${circuitRun.name}
					<s:if test="circuitRun.detailUrl!=null">
					(
					<a href="${circuitRun.detailUrl}">明细</a>)
						</s:if>
				</div>
				<div style="margin-bottom: 10px;">
					审批总状态:
					<font color="red">${circuitRun.allStatus}</font>&nbsp;&nbsp; 审批动态:
					<font color="red">${circuitRun.auditStatus}</font>&nbsp;&nbsp;申请人:
					<font color="red">${circuitRun.addUserName}</font>&nbsp;&nbsp;添加日期:
					<font color="red">${circuitRun.addDateTime}</font>
				</div>
				<div>
					<h2>
						审批内容明细
					</h2>
					<table class="table" id="showDetails" style=""></table>
					<h2>
						审批节点动态
						<s:if test="circuitRun.xgepId!=null">
					(
					<a
								href="CircuitRunAction_findAduitPage.action?id=${circuitRun.xgepId}">相关审批</a>)
						</s:if>
					</h2>
					<table class="table" style="">
						<tr>
							<th>
								序号
							</th>
							<th style="width: 40px;">
								审批
								<br />
								顺序
							</th>
							<th>
								审核人姓名
							</th>
							<th>
								审核状态
							</th>
							<th>
								通知时间
							</th>
							<th>
								审批时间
							</th>
							<th>
								审批意见
							</th>
						</tr>
						<s:iterator value="list" id="pageCircuitRun" status="pageIndex">
							<tr>
								<td align="center">
									${pageIndex.index+1}
								</td>
								<td align="center">
									${pageCircuitRun.auditLevel}
								</td>
								<td align="center">
									${pageCircuitRun.auditUserName}
								</td>
								<td align="center">
									${pageCircuitRun.auditStatus}
								</td>
								<td align="center">
									${pageCircuitRun.startDateTime}
								</td>
								<td align="center">
									${pageCircuitRun.auditDateTime}
								</td>
								<td align="center">
									${pageCircuitRun.auditOpinion}
								</td>
							</tr>
						</s:iterator>
					</table>
					<br />
					<div id="showMess"
						style="display: none; font-size: 20px; font-weight: bolder; color: red;"></div>
					<br />
					<div id="showWWDetail"
						style="font-weight: bolder; display: none; font-size: 14px;">
						审批成功，物料激活中,请稍后...
						<font color="red">(预计用时10-30秒钟,请勿关闭页面)</font>
					</div>
					<s:if test="executionNode!=null">
						<form id="auditForm" action="" method="post"
							onsubmit="return check();">
							<input type="hidden" name="nextMessage" value="${nextMessage}" />
							<input type="hidden" name="sendSms" value="${sendSms}" />
							<input type="hidden" name="id" value="${circuitRun.id}" />
							<input type="hidden" name="executionNode.id"
								value="${executionNode.id}" />
							<table class="table" style="">
								<tr>
									<th colspan="2">
										审批
									</th>
								</tr>
								<tr>
									<th align="right">
										审批:
									</th>
									<td>
										<input id="auditTrue" type="radio" onchange="btnChange1()"
											name="auditStauts" value="true" checked="checked" />
										<label for="auditTrue">
											同意
										</label>
										<input id="auditFalse" type="radio" onchange="btnChange2()"
											name="auditStauts" value="false" />
										<label for="auditFalse">
											打回
										</label>
									</td>
								</tr>

								<tr>
									<th align="right">
										<s:if test="circuitRun.entityName=='Dimission_Handover'">
										交接内容:
										</s:if>
										<s:else>审批意见:</s:else>
									</th>
									<td align="center">
										<textarea name="executionNode.auditOpinion" rows="5" cols="25"
											style="width: 100%" id="auditOpinion"></textarea>
									</td>
								</tr>
								<s:if test='circuitRun.isspoption == "是"'>
									<s:if test='circuitRun.danxuanorduoxuan=="单选"'>
										<tr>
											<th align="right">
												意见选项:
											</th>
											<td>
												<ul>
													<s:iterator value="optionrunList" id="optionrun"
														status="pagestatus">
														<li style="float: left; width: 33%;">
															<input type="radio" value="${optionrun.name}"
																name="auditOption">
															${optionrun.name} &nbsp;&nbsp;&nbsp;&nbsp;
														</li>
													</s:iterator>
												</ul>
											</td>
										</tr>
									</s:if>
									<s:elseif test='circuitRun.danxuanorduoxuan=="多选"'>
										<tr>
											<th align="right">
												意见选项:
											</th>
											<td>
												<ul>
													<s:iterator value="optionrunList" id="optionrun"
														status="pagestatus">
														<li style="float: left; width: 33%;">
															<input type="checkbox" value="${optionrun.name}"
																name="auditOption">
															${optionrun.name} &nbsp;&nbsp;&nbsp;&nbsp;
														</li>
													</s:iterator>
												</ul>
											</td>
										</tr>
									</s:elseif>
								</s:if>
								<s:if test='circuitRun.isVerification=="是"'>
									<tr>
										<th align="right">
											验证码:
										</th>
										<td>
											<input id="number1" type="text" style="width: 80px;"
												onkeyup="getNumber()" align="middle" maxlength="6">
											<input id="send_id" type="button" onclick="onsend()"
												value="获取验证码" align="middle">
											<span id="isok"> </span>
											<span style="color: red"> 序号为：${number}</span>
										</td>
									</tr>
								</s:if>

								<tr>
									<td colspan="2" align="center">
										<s:if test='circuitRun.isVerification=="是"'>
											<input type="submit" value="审批" onclick="updatemsg()"
												class="input" id="ok" disabled="disabled" />
										</s:if>
										<s:else>
											<input type="submit" value="审批" class="input" id="ok"
												onclick="todisabled(this);updatemsg()" />
										</s:else>
									</td>
								</tr>
							</table>
						</form>
					</s:if>
				</div>
			</div>
			<br>
		</div>
		<%@include file="/util/foot.jsp"%>
		</center>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
		<script type="text/javascript">
var seconds = 300;
var speed = 1000;
var btn = document.getElementById("send_id");
//发送验证码			
function onsend() {
	$("#send_id").attr("disabled", "disabled");
	var nub = "${number}";
	$.ajax( {
		type : "POST",
		url : "paymentVoucherAction_send.action?nub=" + nub,
		data : {},
		dataType : "json",
		success : function(data) {
			//setTime();
		countDown();
		seconds = 300;
	}
	});
}

//一分钟倒计时
function countDown(seconds, speed) {
	var txt = ((seconds < 10) ? "0" + seconds : seconds) + "秒";
	btn.value = txt;
	var timeId = setTimeout("countDown(seconds--,speed)", speed);
	if (seconds == 0) {
		clearTimeout(timeId);
		$("#send_id").removeAttr("disabled");
		btn.value = "重新获取验证码";
	}
}

function btnChange2() {
	$("#number1").attr("disabled", "disabled");
	$("#send_id").attr("disabled", "disabled");
	$("#ok").removeAttr("disabled");
}
function btnChange1() {
	var isok = "${circuitRun.isVerification}";
	$("#number1").removeAttr("disabled");
	$("#send_id").removeAttr("disabled");
	$("#ok").attr("disabled", "disabled");
	if (isok != "是") {//是否需要验证码
		$("#ok").removeAttr("disabled");
	}

}
$(function() {
	var mesage=${successMessage};
	$("#showDetails").empty();
	$.each( mesage, function(i, n){
		$("#showDetails").append("<tr><th align='right' style='width:50%;'>"+i+":</th><td style='width:50%;'>"+n+"</td></tr>");
});
});
	function getNumber(){
			var number1 = $("#number1").val();
			var a = number1.length;
			var number2 = "";
			if(a==6){
					var strCookie = document.cookie;//获得所有cookie
					var arrCookie = strCookie.split(";");
					var code = "${sessionScope.Users.code}";//获得当前登录人工号
						for ( var i = 0; i < arrCookie.length; i++) {
						var arr = arrCookie[i].split("=");
						var arr0 = arr[0].replace(/\ /g, "");
						if (""+code+"_yzm" == arr0) {
							number2 = arr[1];
							if(number1==number2){
								$("#isok").html( "<img	src='${pageContext.request.contextPath}/images/success.jpg'>");
								$("#ok").removeAttr("disabled");
							}else{
									$("#isok").html( "<img	src='${pageContext.request.contextPath}/images/error.jpg'>");
							}
						} 
					}
			}
	}

	
function check(){
	var auditOpinion =	$("#auditOpinion").val();
	if('${circuitRun.isopinion}' == '是' && auditOpinion == ""){
		alert("请先填写审批意见!")
		$("#auditOpinion").focus();
		return false;
	}else{
		$.ajax({
			type : "POST",
			url : "CircuitRunAction_updateAudit.action",
			data :$("#auditForm").serialize(),
			dataType : "json",
			success : function(soure) {
				
				$("#auditForm").hide();
				$("#showMess").show();
				$("#showMess").html(soure.message);
				var circuitRunName = '${circuitRun.name}';
				var allStatus = soure.data;
			}
		});
		return false;
	}
}

</script>
	</body>
</html>
