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
		<div id="gongneng" style="width: 100%;">
			<div align="center">
				冲销转库申请
			<div>
				<form action="goodsAction!applycxzk.action" method="post">
				<input type="hidden" value="${id}" name="id" >
				<input type="hidden" value="${goods.goodsId}" name="goods.goodsId" >
				<table class="table">
					<tr>
						<th>件号</th><td>${goods.goodsMarkId}</td>
						<th>名称</th><td>${goods.goodsFullName}</td>
						<th>批次</th><td>${goods.goodsLotId}</td>
					</tr>
					<tr>
						<th>库存</th><td>${goods.goodsCurQuantity}</td>
						<th>可转</th><td>${goods.kzCount}</td>
						<th>转库数量</th><td><input id="kzCount" onkeyup="mustBeNumber('kzCount')" name="goods.kzCount" value="${goods.kzCount}"/></td>
					</tr>
					<tr>
						<td colspan="6">
							<input type="submit" value="提交">
						</td>
					</tr>
					</form>
				</table>
			</div>	
			</div>
		</div>
		<%@include file="/util/foot.jsp"%>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
	</body>
</html>