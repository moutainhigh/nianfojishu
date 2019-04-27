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
		<div id="gongneng" style="width: 100%; margin-top: 10px;">
			<div id="xitong"
				style="width: 100%; font-weight: bold; height: 50px;" align="left">
				<div
					style="float: left; width: 50%; padding-left: 30px; padding-top: 5px;"
					align="left">

				</div>
				<div style="float: left; width: 45%; padding-top: 5px;"
					align="right">
					<a href=""
						style="color: rgb(79, 77, 77)"><br />
					</a>
				</div>
			</div>

			<div align="center">
				<h3>
					添加工具柜
					<br />
					<s:if test="successMessage!=null">
						<font color="red">${successMessage}</font>
					</s:if>
				</h3>
				<form action="ToolCabineAction_add.action"
					method="post" onsubmit="return validate()">
					<table class="table">
						<tr style="width: 100%">
							<th align="center">
								工具柜标识
							</th>
							<td align="center">
								<input type="text" name="toolCabine.cabOpenOrder"
									id="cabOpenOrder" />
							</td>
							<th align="center">
								工具柜IP
							</th>
							<td align="center">
								<input type="text" name="toolCabine.cabAceIp"
									id="cabAceIp" />
							</td>
						</tr>
						<tr>
							<th align="center">
								工具柜物品名称
							</th>
							<td align="center">
								<input type="text" name="toolCabine.articleName"
									id="articleName" />
							</td>
							<th align="center">
								工具柜物品型号
							</th>
							<td align="center">
								<input type="text" name="toolCabine.articleFormat"
									id="articleFormat" />
							</td>
						</tr>
						<tr>
							<th align="center">
								工具柜编号(G0001~G9999)
							</th>
							<td align="center">
								<input type="text" name="toolCabine.cabNumber"
									id="cabNumber" />
							</td>
							<td colspan="2"></td>
						</tr>
						<tr>
							<td colspan="4" align="center">
								<input type="submit" value="添加(Add)"
									style="width: 100px; height: 50px;" />
							</td>
						</tr>
					</table>
				</form>
			</div>
		</div>
		<%@include file="/util/foot.jsp"%>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
		<script type="text/javascript">
function validate() {
	if (!validateText("cabOpenOrder", "工具柜标识")) {
		return false;
	}
	var cabOpenOrder = $("#cabOpenOrder").val();
	if(cabOpenOrder>24){
		$("#cabOpenOrder").val(1);
		alert("柜子指令不能大于24");
		return false;
	}
	if (!validateText("cabAceIp", "工具柜IP")) {
		return false;
	}
	if (!validateText("cabNumber", "工具柜编号")) {
		return false;
	}
}

function validateText(id, textname) {
	var textValue = $.trim($("#" + id).val());
	if (textValue == null || textValue == "") {
		alert(textname + "不能为空");
		return false;
	}
	return true;
}
$("#cabOpenOrder").keyup(function() {
	var tmptxt = $(this).val();
	$(this).val(tmptxt.replace(/\D|^0/g, ''));
})
</script>
	</body>
</html>
