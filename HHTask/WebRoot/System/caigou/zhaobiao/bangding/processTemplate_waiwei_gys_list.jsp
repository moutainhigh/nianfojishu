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
	<div id="bodyDiv" align="center" class="transDiv"
			onclick="chageDiv('none')">
		</div>
		<div id="contentDiv"
			style="position: absolute; z-index: 255; width: 900px; display: none;"
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
					<iframe id="showProcess" src="" marginwidth="0" marginheight="0"
						hspace="0" vspace="0" frameborder="0" scrolling="yes"
						style="width: 98%; height: 400px; margin: 0px; padding: 0px;"></iframe>
				</div>
			</div>
		</div>
		
		
	
			<div align="center">
			<form action="markIdAction!listtianxiejiepai.action" method="post"
					theme="simple">
				<table class="table" style="width: 100%;border: 1ex;" >
					<tr  bgcolor="#c0dcf2">
						<th align="cleft" >
							ID
						</th>
							<th align="cleft" >
							名称
						</th>
						<th align="cleft" >
							件号
						</th>
						<th align="cleft" >
							供货厂家
						</th>
						<th align="cleft" >
							类型
						</th>
						<th align="cleft" >
							状态
						</th>
					
					</tr>
					<s:iterator value="list" id="zhUser1" status="pageIndex">
						<tr  align="center" bgcolor="#e6f3fb"
								onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'#e6f3fb')">
							<td>
								${pageIndex.index+1}
							</td>
								<td>
							${zhUser1.proName}
							</td>
							<td>
								${zhUser1.markId}
							</td>
							<td>
								${zhUser1.gys}
							</td>
							<td>
								${zhUser1.procardStyle}
							</td>
						
							<td>
							<a onclick="toUpdatezhaobiao(${zhUser1.id})">填写工序</a>
		                 	</td>
		                 	
						</tr>
							</s:iterator>
						</table>
						
						</form>
						
				
		</div>
		
		</center>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
		<SCRIPT type="text/javascript">
			
	function toUpdatezhaobiao(id){
			var url=encodeURI(encodeURI("markIdAction!towaiwei.action?pIdZijian.id="+id));
		$("#showProcess").attr("src", url);	
		chageDiv('block');
	}
		function chakan(id){
			var url=encodeURI(encodeURI("zhaobiaoAction!chakan.action?gysjiepai.id="+id));
		$("#showProcess").attr("src", url);	
		chageDiv('block');
	}
	</SCRIPT>
	</body>
</html>