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
	<title></title>
		<%@include file="/util/sonHead.jsp"%>
	</head>
	<body>
		<%@include file="/util/sonTop.jsp"%>
		<div id="gongneng" style="width: 100%;">
			<div align="center">
				<div class="container">
					<h1 class="text-center">屏幕列表</h1>
					<div class="row">
						<table class="table table-responsive">
							<tr >
								<th class="text-right">
									屏幕名称：
								</th>
								<td>
									<input type="text" name="screen.name" value="${screen.name }">
								</td>
								<th class="text-right">
									屏幕位置：
								</th>
								<td>
									<input type="text" name="screen.position" value="${screen.position}">
								</td>
							</tr>
							<tr>
								<td colspan="8" class="text-center" align="center">
									<input type="button" value="查询" class="input btn ">
									<input type="button" value="前往添加" class="input btn" 
										onclick="location.href='${pageContext.request.contextPath}/inDoorScreenAction!toAddScreen.action'">
								</td>
							</tr>
						</table>
					</div>
					
					<div class="row">
						<table class="table table-responsive table-striped table-bordered">
							<thead>
								<tr >
									<th class="text-center">序号</th>
									<th class="text-center">屏幕名称</th>
									<th class="text-center">屏幕位置</th>
									<th class="text-center">添加时间</th>
									<th class="text-center">修改时间</th>
									<th class="text-center">操作</th>
								</tr>
							</thead>
							<tbody class="text-center">
								<s:iterator value="screenList" id="sl" status="ps">
									<tr>
										<td>${ps.index+1 }</td>
										<td>${sl.name }</td>
										<td>${sl.position }</td>
										<td>${sl.addTime }</td>
										<td>${sl.updateTime }</td>
										<td>
											<input type="button" class="btn btn-default" value="修改" 
												onclick="location.href='${pageContext.request.contextPath}/inDoorScreenAction!toAddScreen.action?id=${sl.id}'"/>
											<input type="button" class="btn btn-default" value="绑定人员" 
												onclick="location.href='${pageContext.request.contextPath}/inDoorScreenAction!toBindUsers.action?id=${sl.id}'"/>
											<input type="button" class="btn btn-default" value="查看屏幕" 
												onclick="location.href='${pageContext.request.contextPath}/inDoorScreenAction!gotoIndoorScreenBySrc.action?id=${sl.id}'"/>
										</td>
									</tr>
									
								</s:iterator>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
		<%@include file="/util/foot.jsp"%>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
	</body>
</html>
