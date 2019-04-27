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
		<script type="text/javascript" src="javascript/query-easyui-1.3.1/jquery-1.8.0.min.js">
</script>
		<script type="text/javascript">
			function showDownload(){  
				$('#reload').show();
			}
			
		</script>
	</head>
	<body>
		<%@include file="/util/sonTop.jsp"%>
		<div id="gongneng"
			style="width: 100%; border: thin solid 1px #0170b8; margin-top: 10px;">
			<div id="xitong"
				style="width: 100%; font-weight: bold; padding-left: 35px; padding-top: 5px; padding-bottom: 5px; "
				align="left">
				<div style="float: left; width: 50%" align="left">
					
				</div>
				<div style="float: left; width: 48%" align="right">
					<a
						href="ModuleFunctionAction!findMfByIdForJump.action?id=${moduleFunction.id}"
						style="color: #ffffff">刷新</a>
				</div>
			</div>
			
			<div align="center">
				<form action="ProjectStartCountersigned_update.action"  method="post" enctype="multipart/form-data">
					<input type="hidden" name="p.id" value="${p.id}" />
					<table class="table" style="width: 50%" >
						<tr>
							<th colspan="2">修改会签纪要</th>
						</tr>
						<tr>
							<td>会签纪要(<a onclick="javascript:;showDownload();return false;" href="#">重新上传</a>)</td>
							<td>
<%--								<a  href="DownAction.action?fileName=${p.minutes}&directory=/upload/file/projectStart/">点击下载</a>--%>
								<a  href="FileViewAction.action?FilePath=/upload/file/projectStart/${p.minutes}">点击下载</a>
								<span id="reload" style="display :none;" ><input type="file" name="attachment"></span>
							</td>
						</tr>
						<tr>
							<td colspan="2" align="center" >
								<input type="submit" value="提交"/>
								<input type="reset" value="重置" />
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
