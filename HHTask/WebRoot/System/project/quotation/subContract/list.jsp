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
		<script type="text/javascript" src="javascript/jquery-easyui-1.3.1/jquery-1.8.0.min.js">
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
				<table class="table" width="100%" border="0" style="border-collapse: collapse;">
					<tr bgcolor="#c0dcf2" height="50px">
						<th align="center">
							名称
						</th>
						<th align="center">
							零件代号
						</th>
						<th align="center">
							次级供应商名称
						</th>
						<th align="center">
							客户指定
						</th>
						<th align="center">
							定额, pc或kg
						</th>
						<th align="center">
							单价 RMB/Pc或Kg
						</th>
						<th align="center">
							零部件成本 RMB
						</th>
						<th>
							其他操作
						</th>
					</tr>
					<s:iterator value="subContracts" id="r" status="st">
						<s:if test="#st.index%2==1">
							<tr align="center" bgcolor="#e6f3fb" onmouseover="chageBgcolor(this)" onmouseout="outBgcolor(this,'#e6f3fb')">
						</s:if>
						<s:else>
							<tr align="center" onmouseover="chageBgcolor(this)" onmouseout="outBgcolor(this,'')">
						</s:else>
							<td>${r.name}</td>
							<td>${r.partNumber}</td>
							<td>${r.subSupplier}</td>
							<td>${r.directed}</td>
							<td>${r.quantity}</td>
							<td>${r.unitPrice}</td>
							<td>${r.cost}</td>
							<td>
								<a href="ProjectQuotationSubContract_updateInput.action?subContract.id=${r.id}">修改</a>
								<a id="deleteBut" onclick="deleteById(${r.id});"  href="javascript:void(0);">删除</a>
							</td>
						</tr>
						
					</s:iterator>
					<tr>
						<s:if test="errorMessage==null">
							<td colspan="8" align="right">
								第
								<font color="red"><s:property value="cpage" /> </font> /
								<s:property value="total" />
								页
								<fenye:pages cpage="%{cpage}" total="%{total}" url="%{url}"
									styleClass="page" theme="number" />
							</td>
						</s:if>
						<s:else>
							<td colspan="8" align="center" style="color: red">
								${errorMessage}
							</td>
						</s:else>
					</tr>
				</table>
			</div>
			<br>
		</div>
		<%@include file="/util/foot.jsp"%>
		</center>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
		
		<script type="text/javascript">
			function deleteById(id){
				if(confirm('是否删除?')){
					$.ajax({
						type: "POST",
						url: "ProjectQuotationSubContract_delete.action",
						data: "subContract.id=" + id,
						success: function(msg){
							alert( "删除成功!");
							window.location.reload();
						}
					});
				}
			}
		</script>
	</body>
</html>
