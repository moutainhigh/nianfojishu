<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<title>添加摄像头</title>
		<%@include file="/util/sonHead.jsp"%>
	</head>
	<body>
		<%@include file="/util/sonTop.jsp"%>
		<div id="gongneng" style="width: 100%;" align="center">
			<h1 style="font-size: 20px;" align="center">添加摄像头</h1>
			<s:if test="faceCamera!=null&&faceCamera.id!=null">
				<form action="${pageContext.request.contextPath}/faceAction!updateFaceCamera.action" method="post" id="submitForm">
				<input type="hidden" value="${faceCamera.id }" name="faceCamera.id">
			</s:if>
			<s:else>
				<form action="${pageContext.request.contextPath}/faceAction!addFaceCamera.action" method="post" id="submitForm">
			</s:else>
				<table class="table" >
					<tr>
						<td width="50%" align="right">摄像头名称</td>
						<td>
							<input type="text" name="faceCamera.name" value="${faceCamera.name }" id="name">
						</td>
					</tr>
					<tr>
						<td align="right">摄像头位置</td>
						<td>
							<input type="text" name="faceCamera.position" value="${faceCamera.position }" id="position">
						</td>
					</tr>
					<tr>
						<td align="right">
							摄像头IP
						</td>
						<td>
							<input type="text" name="faceCamera.ip" value="${faceCamera.ip }" id="ip">
						</td>
					</tr>
					<tr>
						<td align="right">端口号</td>
						<td>
							<input type="text" name="faceCamera.port" value="${faceCamera.port}" id="port">
						</td>
					</tr>
					<tr>
						<td align="right">比对相似度</td>
						<td>
							<input type="text" name="faceCamera.similarity" value="${faceCamera.similarity}" id="similarity" >
						</td>
					</tr>
					<tr>
						<td align="right">用户名</td>
						<td>
							<input type="text" name="faceCamera.userName" value="${faceCamera.userName }" id="userName">
						</td>
					</tr>
					<tr>
						<td align="right">密码</td>
						<td>
							<input type="password" name="faceCamera.password" value="${faceCamera.password }" id="password">
						</td>
					</tr>
					<tr>
						<td align="right">用途</td>
						<td>
<%-- 							<input type="text" name="faceCamera.use" value="${faceCamera.use}" id="" --%>
							<select name="faceCamera.cameraUse">
								<s:iterator value="{'进门','出门','监控' }" id="u">
									<s:if test="faceCamera.cameraUse==#u">
										<option value="${u}" selected="selected">${u}</option>
									</s:if>
									<s:else>
										<option value="${u}">${u}</option>
									</s:else>
								</s:iterator>
							</select>
						</td>
					</tr>
					<tr>
						<td colspan="2"  align="center">
							<input type="button" value="提交" onclick="btnSubmit()" class="input">
						</td>
					</tr>
					
				</table>
			</form>
			
		</div>
		<%@include file="/util/foot.jsp"%>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
		<script type="text/javascript">
			function btnSubmit(){
				var name = $("#name").val();
				if(name==null || name==""){
					alert("摄像头名称不能为空");
					return false;
				}
				var ip = $("#ip").val();
				if(ip==null || ip==""){
					alert("IP不能为空");
					return false;
				}
				var port = $("#port").val();
				if(port==null || port==""){
					alert("端口号不能为空");
					return false;
				}
				var userName = $("#userName").val();
				if(userName==null || userName==""){
					alert("用户名不能为空");
					return false;
				}
				var password = $("#password").val();
				if(password!=null && password==""){
					alert("密码不能为空");
					return false;
				}
				var similarity = $("#similarity").val();
				if(similarity!=null && similarity==""){
					alert("比对相似度不能为空");
					return false;
				}
				$("#submitForm").submit();
			}
		</script>
	</body>
</html>
