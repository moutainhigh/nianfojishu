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
				<form action="ProjectProposalFlow_update.action" method="post">
					<input type="hidden" name="flow.id" value="${flow.id}">
					<table class="table" style="width: 50%">
						<tr>
							<th>
								同意审核
							</th>
							<td>
								<input type="radio" name="flow.agree" value="true" checked="checked">
								是
								<input type="radio" name="flow.agree" value="false">
								否
							</td>
						</tr>
						<tr>
							<th>
								审核意见
							</th>
							<td>
								<textarea name="flow.about" rows="5" cols="45"></textarea>
							</td>
						</tr>
						<tr>
							<td colspan="2" align="center">
								<input type="submit" value="提交">
								<input type="reset" value="重填">
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
