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
		<style type="text/css">
a {
	cursor: pointer;
}

.table {
	border: 0px solid #999;
	width: 756px;
	border-collapse: collapse;
}

.table th,.table td {
	border-width: 1px;
	padding: 0px;
}

.subTable {
	text-align: center;
	border-collapse: collapse;
	width: 100%;
	border-width: 1px;
	border-style: hidden;
}
</style>
	</head>
	<body style="text-align: center;">
		<!-- 弹出层开始 -->
		<div id="bodyDiv" align="center" class="transDiv" style="left: 0px;"
			onclick="chageDiv('none')">
		</div>
		<div id="contentDiv"
			style="position: absolute; z-index: 999; width: 1024px; display: none;"
			align="center">
			<div id="closeDiv">
				<table style="width: 100%">
					<tr>
						<td>
							<span id="title"></span>
						</td>
						<td align="right">
							<img alt="" src="<%=basePath%>/images/closeImage.png" width="30"
								height="32" onclick="chageDiv('none')">
						</td>
					</tr>
				</table>
				<div id="operatingDiv"
					style="background-color: #ffffff; width: 100%; height: 400px;">
					<iframe id="uploadIframe" src="" marginwidth="0" marginheight="0"
						hspace="0" vspace="0" frameborder="0" scrolling="yes"
						style="width: 98%; height: 4000px; margin: 0px; padding: 0px;"></iframe>
				</div>
			</div>
		</div>
		<!-- 弹出层结束 -->
		<div align="center">
			<!-- A4页面开始 -->
			<div id="printDiv" style="width: 794px; border: 1px solid #000000;">
				<!-- 页边距内开始 -->
				<!-- 打印页边距设定为 5mm 时，网页内最大元素的分辨率：756×1086   5mm/18.89-->
				<div
					style="width: 756px; border: 1px solid #000000; position: relative; top: 0px; bottom: 100px;">
					<b style="font-size: 20px;">上传缺陷图纸</b>	
					<table id="processDataListTable" width="width:750px;" class="table"
						cellpadding="0" cellspacing="0">
						<!-- 1 -->
						<tr align="center">
							<td align="center" colspan="9">
								<table class="subTable">
									<tr>
										<td rowspan="2">
											产品信息
										</td>
										<td colspan="2">
											版本号
										</td>
										<td >
											件号
										</td>
										<td colspan="2">
											件名
										</td>
									</tr>
									<tr>
										<td colspan="2">
											${procardTemplate.banBenNumber}
										</td>
										<td>
											${procardTemplate.markId}
										</td>
										<td colspan="2">
											${procardTemplate.proName}
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<!-- 2 -->
						<tr align="center">
							<th >
								工序号
							</th>
							<th >
								工序名称
							</th>
							<th >
								状态
							</th>
							<th >
								操作
							</th>
						</tr>
						<!-- 34 -->
						<s:iterator id="p" value="processTemplateList" status="st">
							<tr align="center" style="line-height: 30px;">
								<td>
									${p.processNO}
								</td>
								<td>
									${p.processName}
								</td>
								<td><input type="button" onclick="showProcessTz(${p.id})" value="上传图纸" class="input">
								</td>
							</tr>
						</s:iterator>
						<!-- 35 -->
					</table>
				</div>
				<!-- 页边距内结束-->
			</div>
			<!-- A4页面结束  -->
		</div>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
		<SCRIPT type="text/javascript">
		function showProcessTz(id){
			$("#uploadIframe").attr("src",
			"procardTemplateGyAction_findProcessGongyiGuifan.action?id=" + id+"&tag=quexian");
	        chageDiv('block');
		}
		</SCRIPT>
	</body>
</html>
