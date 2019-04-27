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
					<a
						href="ModuleFunctionAction!findMfByIdForJump.action?id=${moduleFunction.id}"
						style="color: #ffffff">刷新</a>
				</div>
			</div>
			<div align="center" style="border: 1px solid #00000;">
				<form id="bangCardForm${pageStatus.index+1}"
					action="javascript:submitForm('bangCardForm','${pageStatus.index+1}','${pageProcard.cardNum}')"
					method="post">
					<h1>
						交卡管理(Post card management)
					</h1>
					强制交卡:
					<input name="pageStatus" type="radio" value="yes">
					是
					<input name="pageStatus" type="radio" value="no" checked="checked">
					否
					<br />
					请刷生产周转卡(Please brush production turnover cards):
					<input id="cardNumber${pageStatus.index+1}" name="cardNumber"
						value="${pageProcard.cardNum}" />
				</form>
			</div>
			<br>
		</div>
		<%@include file="/util/foot.jsp"%>
		</center>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
		<script type="text/javascript">
//添加组件/原材料流水卡片
function submitForm(formId, index, oldCard) {
	if ($("#cardNumber" + index).val() == "") {
		alert("请刷生产周转卡!");
		$("#cardNumber" + index).select();
		return false;
	} else {
		$.ajax( {
			type : "POST",
			url : "ProcardAction!postCard.action",
			dataType : "json",
			data : $("#" + formId + index).serialize(),
			success : function(msg) {
				if (msg.success) {
					alert(msg.message);
					$("#cardNumber" + index).select();
					if (msg.message == "交卡成功!") {
						$("#sendCard" + index).html(
								$("#cardNumber" + index).val());
					}
				} else {
					alert(msg.message);
				}
			}
		});
	}
}
</script>
	</body>
</html>