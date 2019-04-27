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
		<script type="text/javascript">

</script>

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
	
	
		<%@include file="/util/sonTop.jsp"%>
		<div id="gongneng"
			style="width: 100%; border: solid 1px #0170b8; margin-top: 10px;">
			<div id="xitong"
				style="width: 100%; font-weight: bold; height: 50px;" align="left">
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
			<div align="center" style="border: 1px solid #00000;">
				<form action="GzstoreAction_findGzMsn.action"
					method="post">
					<table class="table">
						<tr>
							<th colspan="6">
							GR&R模板（MSN分析）
							</th>
						</tr>
						<tr>
							<th>
								编号：
							</th>
							<td>
								<input name="gzMsn.number"
									value="${gzMsn.number}" />
							</td>
							<th>
								名称：
							</th>
							<td>
									<input name="gzMsn.matetag"
									value="${gzMsn.matetag}" />
							</td>
						</tr>
						<tr>
							<th>
								是否在有效期内：
							</th>
							<td>
									<SELECT name="gzMsn.isyouxiao">
										<option value="${gzMsn.isyouxiao}">${gzMsn.isyouxiao}</option>
										<option value="是">是</option>
										<option value="否">否</option>
									</SELECT>
							</td>
							<th>
								结果：
							</th>
							<td>
									<SELECT name="gzMsn.result">
										<option value="${gzMsn.result}">${gzMsn.result}</option>
										<option value="OK">OK</option>
										<option value="可接受">可接受</option>
										<option value="需改进">需改进</option>
									</SELECT>
							</td>
						</tr>
						<tr>
							<td colspan="4">
								<center><input type="submit" value="查询" class="button"></center>
							</td>
						</tr>
					</table>
				</form>

				<div id="rootTemplateDiv">
					<div id="showMessage"
						style="color: red; font-size: 14px; font-weight: bolder;">
					</div>
					
					<table class="table">
						<tr bgcolor="#c0dcf2" height="50px">
							<th align="center">
								序号
							</th>
							<th align="center">
								编号
							</th>
							<th align="center">
								名称
							</th>
						
							<th align="center">
								测量精度
							</th>
							<th align="center">
								是否在有效期内
							</th>
							<th align="center">
								添加时间
							</th>
							<th align="center">
								测试人员
							</th>
							<th align="center">
								职称
							</th>
							
							<th align="center">
								结果<br>(GR&R值)
							</th>
							<th align="center">
								 附件
							</th>
							<th align="center">
								操作
							</th>
						</tr>
						<s:iterator value="gsMsnList" id="gsMsn"
							status="pageindex">
							<s:if test="#pageindex.index%2==1">
								<tr align="center" bgcolor="#e6f3fb"
									onmouseover="chageBgcolor(this)"
									onmouseout="outBgcolor(this,'#e6f3fb')">
							</s:if>
							<s:else>
								<tr align="center" onmouseover="chageBgcolor(this)"
									onmouseout="outBgcolor(this,'')">
							</s:else>
							<td>
								<s:property value="#pageindex.index+1" />
							</td>
							<td style="width: 180px;">
								${gsMsn.number}
							</td>
							<td>
								${gsMsn.matetag}
							</td>
							
							<td align="center">
								${gsMsn.cljd}
							</td>
							<td align="center">
								${gsMsn.isyouxiao}
							</td>
							<td align="center">
								${gsMsn.addDate}
							</td>
							<td align="center">
								${gsMsn.person}
							</td>
							<td align="center">
								${gsMsn.dept}
							</td>
							<td align="center">
								${gsMsn.result} <br> (${gsMsn.GRR})
							</td>
							<td align="center">
								<a href="<%=basePath%>/FileViewAction.action?FilePath=/upload/file/Msn/${gsMsn.pic}">查看附件
								</a>/
								<a href="${pageContext.request.contextPath}/upload/file/Msn/${gsMsn.pic}">
									 下载附件</a>
							</td>
							<td align="center">
<%--							<a onclick="toUpdatezhaobiao(${pageProcardTem.id})">修改</a>--%>
<%--							--%>
									<a href="GzstoreAction_delGzMsn.action?gzMsn.id=${gsMsn.id}">删除</a>
							</td>
							</tr>
						</s:iterator>
						<tr>
							<s:if test="errorMessage==null">
								<td colspan="11" align="right">
									第
									<font color="red"><s:property value="cpage" /> </font> /
									<s:property value="total" />
									页
									<fenye:pages cpage="%{cpage}" total="%{total}" url="%{url}"
										styleClass="page" theme="number" />
							</s:if>
							<s:else>
								<td colspan="11" align="center" style="color: red">
							</s:else>
							</td>
					</table>
				</div>
			</div>
			<br>
		</div>
		<%@include file="/util/foot.jsp"%>
		</center>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
		<script type="text/javascript">
			function toUpdatezhaobiao(id){
			var url=encodeURI(encodeURI("EquipmentAction!toUpdateresponsibilities.action?responsibilities.id="+id));
		$("#showProcess").attr("src", url);	
		chageDiv('block');
	}
		function add(){
			var url=encodeURI(encodeURI("${pageContext.request.contextPath}/System/Equipment/responsibilities_add.jsp"));
		$("#showProcess").attr("src", url);	
		chageDiv('block');
	}
		</script>
	</body>
</html>