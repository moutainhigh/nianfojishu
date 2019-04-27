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
				style="width: 100%; font-weight: bold; height: 50px; "
				align="left">
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
			
			<div align="center">
				<form action="Templatem_update.action" method="post">
					<input type="hidden" name="m.id" value="${m.id }">
					<table class="table" style="width: 50%">
						<tr>
							<th colspan="4">修改</th>
						</tr>
						<tr>
							<th>产品型别</th>
							<td> <input name="m.productType" value="${m.productType }"/> </td>
							<th>零件号</th>
							<td> <input name="m.partNumber" value="${m.processNumber}"/> </td>
						</tr>
						<tr>
							<th>工序号</th>
							<td> <input name="m.processNumber" value="${m.processNumber}"/> </td>
							<th>工位号</th>
							<td> <input name="m.gwNumber" value="${m.gwNumber}"/> </td>
						</tr>
						<tr>
							<th>检验编号</th>
							<td colspan="3"> <input name="m.jygcNumber" value="${m.jygcNumber}"/> </td>
						</tr>
						<tr>
							<th>检查内容</th>
							<td colspan="3">
								<textarea name="m.jcnr" rows="5" cols="40">${m.jcnr}</textarea>
							</td>
						</tr>
						<tr>
							<td align="center" colspan="4">
								<input type="submit">
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
