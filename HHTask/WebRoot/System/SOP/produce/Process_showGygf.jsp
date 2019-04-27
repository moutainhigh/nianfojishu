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
		<center>
			<div style="width: 100%; height: 100%">
				<table class="table">
					<tr id="spTr">
						<td colspan="3" align="left">
							<h3>
								视频
							</h3>
						</td>
					</tr>
					<s:iterator value="list" id="spFile">
						<s:if test="#spFile.type=='视频文件'">
							<tr>
								<td align="left">
									<video
										src="<%=path%>/upload/file/processTz/${spFile.month}/<s:property value="#spFile.fileName"/>"
										controls="controls">
									您的浏览器不支持 video 标签。
									</video>
								</td>
								<td align="left">
									<s:property value="#spFile.oldfileName" />
								</td>
								<td>
									<a
										href="DownAction.action?fileName2=${spFile.oldfileName}&fileName=${spFile.fileName}&directory=/upload/file/processTz/${spFile.month}/">下载</a>&nbsp;&nbsp;
								</td>
							</tr>
						</s:if>
					</s:iterator>
					<tr id="3DTr">
						<td colspan="3" align="left">
							<h3>
								3D文件
							</h3>
						</td>
					</tr>
					<s:iterator value="list" id="file3D">
						<s:if test="#file3D.type=='3D文件'">
							<tr>
								<td align="left">
									<a
										href="<%=path%>/show3Dfile.jsp?filePath=upload/file/processTz/${file3D.month}/<s:property value="#file3D.fileName"/>">
										3D文件 </a>
								</td>
								<td align="left">
									<s:property value="#file3D.oldfileName" />
								</td>
								<td>
									<a
										href="<%=path%>/DownAction.action?fileName2=${file3D.oldfileName}&fileName=${file3D.fileName}&directory=/upload/file/processTz/${file3D.month}/">下载</a>&nbsp;&nbsp;
								</td>
							</tr>
						</s:if>
					</s:iterator>
					<tr id="mxTr">
						<td colspan="5" align="left">
							<h3>
								3D模型
							</h3>
						</td>
					</tr>
					<s:iterator value="list" id="modelFile">
						<s:if test="#modelFile.type=='3D模型'">
							<tr>
								<td align="left">
									<a
										href="<%=path%>/upload/file/processTz/${modelFile.month}/<s:property value="#modelFile.fileName"/>">
										<img
											src="<%=path%>/upload/file/processTz/${modelFile.month}/<s:property value="#modelFile.fileName"/>"
											style="width: 80px; height: 80px;" />
									</a>
								</td>
								<td align="left">
									<s:property value="#modelFile.oldfileName" />
								</td>
								<td>
									<%--				   <a href="DownAction.action?fileName=${modelFile.fileName}&directory=/upload/file/processTz/${modelFile.month}/">下载</a>&nbsp;&nbsp;--%>
									<%--				   <input type="button" onclick="deletetz(${modelFile.id},${param.id})" value="删除"/>--%>
									<%--				   <a href="ProcardTemplateAction!deleteProcessTz.action?processTemplateFile.id=${modelFile.id}&pageStatus=card&id=${param.id}" onclick='return window.confirm(\"确认要删除该条记录?\");'>删除</a>--%>
								</td>
							</tr>
						</s:if>
					</s:iterator>
					<tr id="qxTr">
						<td colspan="3" align="left">
							<h3>
								缺陷图纸
							</h3>
						</td>
					</tr>
					<s:iterator value="list" id="qxFile">
						<s:if test="#qxFile.type=='缺陷图纸'">
							<tr>
								<td align="left">
									<a
										<%--									href="<%=path%>/upload/file/processTz/${qxFile.month}/<s:property value="#qxFile.fileName"/>">--%>
									href="<%=path%>/FileViewAction.action?wmark=${procard.orderNumber}&FilePath=/upload/file/processTz/${qxFile.month}/<s:property value="#qxFile.fileName"/>">
										${qxFile.fileName} <%--									<img--%> <%--										src="<%=path%>/upload/file/processTz/${qxFile.month}/<s:property value="#qxFile.fileName"/>"--%>
										<%--										style="width: 80px; height: 80px;" />--%> </a>
								</td>
								<td align="left">
									${oldfileName}
								</td>
								<td>
									<a
										<%--									href="DownAction.action?fileName=${qxFile.fileName}&directory=/upload/file/processTz/${qxFile.month}/">下载</a>&nbsp;&nbsp;--%>
									href="<%=path%>/FileViewAction.action?wmark=${procard.orderNumber}&FilePath=/upload/file/processTz/${qxFile.month}/${qxFile.fileName}">下载</a>&nbsp;&nbsp;
								</td>
							</tr>
						</s:if>
					</s:iterator>
					<tr id="gfTr">
						<td colspan="3" align="left">
							<h3>
								工艺规范
							</h3>
						</td>
					</tr>
					<s:iterator value="list" id="tzFile">
						<s:if test="#tzFile.type=='工艺规范'">
							<tr>
								<td align="left">
									<a target="_showP"
										<%--										href="<%=path%>/upload/file/processTz/${tzFile.month}/<s:property value="#tzFile.fileName"/>">--%>
										href="<%=path%>/FileViewAction.action?wmark=${procard.orderNumber}&FilePath=/upload/file/processTz/${tzFile.month}/<s:property value="#tzFile.fileName"/>">
										<%--										<img--%> <%--											src="<%=path%>/upload/file/processTz/${tzFile.month}/<s:property value="#tzFile.fileName"/>"--%>
										<%--											style="width: 80px; height: 80px;" /> --%>
										${tzFile.fileName}</a>
								</td>
								<td align="left">
									<s:property value="#tzFile.oldfileName" />
								</td>
								<td>
									<%--<a
										href="DownAction.action?fileName=${tzFile.fileName}&directory=/upload/file/processTz/${tzFile.month}/">下载</a>&nbsp;&nbsp;
								--%>
								</td>
							</tr>
						</s:if>
					</s:iterator>
					<tr id="sopTr">
						<td colspan="3" align="left">
							<h3>
								SOP文件
							</h3>
						</td>
					</tr>
					<s:iterator value="list" id="sopFile">
						<s:if test="#sopFile.type=='SOP文件'">
							<tr>
								<td align="left">
									<a target="_showP"
										href="<%=path%>/upload/file/processTz/${sopFile.month}/<s:property value="#sopFile.fileName"/>">
										<%--										<img--%> <%--											src="<%=path%>/upload/file/processTz/${sopFile.month}/<s:property value="#sopFile.fileName"/>"--%>
										<%--											style="width: 80px; height: 80px;" /> --%>
										${sopFile.fileName} </a>
								</td>
								<td align="left">
									<s:property value="#sopFile.oldfileName" />
								</td>
								<td>
									<%--<a
										href="DownAction.action?fileName=${sopFile.fileName}&directory=/upload/file/processTz/${sopFile.month}/">下载</a>&nbsp;&nbsp;
								--%>
								</td>
							</tr>
						</s:if>
					</s:iterator>
					<tr id="sapTr">
						<td colspan="3" align="left">
							<h3>
								SIP文件
							</h3>
						</td>
					</tr>
					<s:iterator value="list" id="sapFile">
						<s:if test="#sapFile.type=='SIP文件'">
							<tr>
								<td align="left">
									<a target="_showP"
										href="<%=path%>/upload/file/processTz/${sapFile.month}/<s:property value="#sapFile.fileName"/>">
										<%--										<img--%> <%--											src="<%=path%>/upload/file/processTz/${sapFile.month}/<s:property value="#sapFile.fileName"/>"--%>
										<%--											style="width: 80px; height: 80px;" /> --%>
										${sapFile.fileName} </a>
								</td>
								<td align="left">
									<s:property value="#sapFile.oldfileName" />
								</td>
								<td>
									<%--<a
										href="DownAction.action?fileName=${sapFile.fileName}&directory=/upload/file/processTz/${sapFile.month}/">下载</a>&nbsp;&nbsp;
								--%>
								</td>
							</tr>
						</s:if>
					</s:iterator>
					<tr id="cxTr">
						<td colspan="3" align="left">
							<h3>
								成型图
							</h3>
						</td>
					</tr>
					<s:iterator value="list" id="cxFile">
						<s:if test="#cxFile.type=='成型图'">
							<tr>
								<td align="left">
									<a target="_showP"
										<%--										href="<%=path%>/upload/file/processTz/${cxFile.month}/<s:property value="#cxFile.fileName"/>">--%>
										href="<%=path%>/FileViewAction.action?wmark=${procard.orderNumber}&FilePath=/upload/file/processTz/${cxFile.month}/<s:property value="#cxFile.fileName"/>">
										<%--										<img--%> <%--											src="<%=path%>/upload/file/processTz/${cxFile.month}/<s:property value="#cxFile.fileName"/>"--%>
										<%--											style="width: 80px; height: 80px;" /> --%>
										${cxFile.fileName} </a>
								</td>
								<td align="left">
									<s:property value="#cxFile.oldfileName" />
								</td>
								<td>
									<a
										href="<%=path%>/DownAction.action?fileName=${cxFile.fileName}&fileName2=${cxFile.oldfileName}&directory=/upload/file/processTz/${cxFile.month}/">下载</a>&nbsp;&nbsp;
									<%--										href="<%=path%>FileViewAction.action?FilePath=/upload/file/processTz/${cxFile.month}/${cxFile.fileName}">下载</a>&nbsp;&nbsp;--%>
								</td>
							</tr>
						</s:if>
					</s:iterator>
					<tr id="bqwjTr">
						<td colspan="3" align="left">
							<h3>
								标签文件
							</h3>
						</td>
					</tr>
					<s:iterator value="list" id="bqwjFile">
						<s:if test="#bqwjFile.type=='标签文件'">
							<tr>
								<td align="left">
									<a target="_showP"
										<%--										href="<%=path%>/upload/file/processTz/${bqwjFile.month}/<s:property value="#bqwjFile.fileName"/>">--%>
										href="<%=path%>/FileViewAction.action?wmark=${procard.orderNumber}&FilePath=/upload/file/processTz/${bqwjFile.month}/<s:property value="#bqwjFile.fileName"/>">
										<%--										<img--%> <%--											src="<%=path%>/upload/file/processTz/${bqwjFile.month}/<s:property value="#bqwjFile.fileName"/>"--%>
										<%--											style="width: 80px; height: 80px;" /> --%>
										${bqwjFile.fileName} </a>
								</td>
								<td align="left">
									<s:property value="#bqwjFile.oldfileName" />
								</td>
								<td>
									<a
										href="<%=path%>/DownAction.action?fileName=${bqwjFile.fileName}&fileName2=${bqwjFile.oldfileName}&directory=/upload/file/processTz/${bqwjFile.month}/">下载</a>&nbsp;&nbsp;
									<%--										href="<%=path%>FileViewAction.action?FilePath=/upload/file/processTz/${bqwjFile.month}/${bqwjFile.fileName}">下载</a>&nbsp;&nbsp;--%>
								</td>
							</tr>
						</s:if>
					</s:iterator>
				</table>
			</div>
		</center>
		<SCRIPT type="text/javascript">
		function deletetz(fileId,id){
	if(window.confirm("确认要删除该条记录?")){
		window.location.href="ProcardTemplateAction!deleteProcessTz.action?processTemplateFile.id="+fileId+"&pageStatus=card&id="+id
	}
	
}
		</SCRIPT>
	</body>
</html>