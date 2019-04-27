<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.DecimalFormat"%>
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
				<table style="width: 100%">
					<tr>
						<td>
							<span id="title">您正在对工序信息进行操作</span>
						</td>
						<td align="right">
							<img alt="" src="<%=basePath%>/images/closeImage.png" width="30"
								height="32" onclick="chageDiv('none')">
						</td>
					</tr>
				</table>
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
			style="width: 100%; font-weight: bold; height: 50px; "
			align="left">
			<div
				style="float: left; width: 50%; padding-left: 30px; padding-top: 5px;"
				align="left">
				
			</div>
			<div style="float: left; width: 45%; padding-top: 5px;" align="right">
				<a href="" style="color: #ffffff">刷新</a>
			</div>
		</div>
		
		<div align="center">
		<s:if test="status == 'muju'">
			<h3>模具管理</h3>
		</s:if>
		<s:else>
			<h3>工装管理</h3>
		</s:else>	
			<form action="GzstoreAction_importFile.action" method="post"
				enctype="multipart/form-data">
				选择导入文件:
				<input type="file" name="uploadFile" />
				<a href="<%=basePath%>upload/file/download/gzstore.xls">导入模版下载</a>
				<input type="submit" value="批量导入" id="sub" />
			</form>
			<form action="GzstoreAction_findAll.action" method="post" >
				<table class="table" >
					<tr>
						<th>名称</th>
						<td>
						<input type="text" name="gzstore.matetag" />
						</td>
						<th>位置</th>
						<td> <input type="text" name="gzstore.place" />
						</td>
					</tr>
					<tr>
					<th>工装号</th>
						<td> <input type="text" name="gzstore.number" />
						</td>
						<th>型别</th>
						<td> <input type="text" name="gzstore.xingbie" />
						</td>
					</tr>
					<tr>
						<td colspan="4" align="center"><input type="submit" style="width: 100px; height: 40px;" value="查询"  class="input" />
							<input type="hidden" value="${status}" name="status"/>
						<input type="button" style="width: 100px; height: 40px;" value="添加"  class="input" onclick="add()"  /> 
						</td>
					</tr>
				</table>
			</form>
			<table width="100%" border="0" style="border-collapse: collapse;" class="table" >
				<tr bgcolor="#c0dcf2" height="50px">
					<td align="center">序号</td>
					<td align="center">库存编号</td>
					<td align="center">编号</td>
					<td align="center">名称</td>
					<td align="center">仓库</td>
					<td align="center">分类</td>
					<td align="center">位置</td>
<%--					<td align="center"> 报检周期(月)</td>--%>
					<td align="center">报检周期(次)</td>
					<td align="center">使用次数</td>
					<td align="center">型别</td>
<%--					<td align="center">工序</td>--%>
					<td align="center">操作</td>
				</tr>
				<s:iterator value="gzList" id="pageList0" status="pageStatus0">
					<s:if test="#pageStatus0.first">
						<tr style="background: red;" height="25px;">
							<td colspan="20" style="color:#fff;">待校验</td>
						</tr>
					</s:if>
					<s:if test="#pageStatus.index%2==1">
						<tr align="center" bgcolor="#e6f3fb"
							onmouseover="chageBgcolor(this)"
							onmouseout="outBgcolor(this,'#e6f3fb')">
					</s:if>
					<s:else>
						<tr align="center" onmouseover="chageBgcolor(this)"
							onmouseout="outBgcolor(this,'')">
					</s:else>
					<td><s:if test="#pageStatus0.index%2==1">
							<font>
						</s:if> <s:else>
							<font color="#c0dcf2">
						</s:else> <s:property value="#pageStatus0.index+1" /> </font></td>
					<td>${pageList0.fk_stoid}</td>
					<td>${pageList0.number}</td>
					<td>${pageList0.matetag }</td>
					<td>${pageList0.storehouse }</td>
					<td>${pageList0.place }</td>
					<td>${pageList0.parClass}</td>
					<td>${pageList0.bjcs}</td>
					<td>${pageList0.bjcs-pageList0.sybjcs}</td>
					<td>${pageList0.xingbie }</td>
					<td>
<%--					<a href="GzstoreAction_findGgbj_Process.action?id=${pageList.id }">查看对应工序/</a>--%>
					<a href="GzstoreAction_findcdList.action?id=${pageList.id }">校验记录</a>/
					<a href="GzstoreAction_initUpdate.action?id=${pageList.id }&status=jy">校验</a>/
					<a href="GzstoreAction_initUpdate.action?id=${pageList0.id }">修改</a>/
					<a href="GzstoreAction_DelGzbj.action?del_id=${pageList0.id }" onclick="return confirm('确定要删除吗？')">删除</a>
					</td>
					</tr>
				</s:iterator>
				<s:iterator value="maps" id="pageList" status="pageStatus">
					<s:if test="#pageStatus.first">
						<tr bgcolor="blue">
							<th colspan="20" style="color: #fff;">正常</th>
						</tr>
					</s:if>
					<s:if test="#pageStatus.index%2==1">
						<tr align="center" bgcolor="#e6f3fb"
							onmouseover="chageBgcolor(this)"
							onmouseout="outBgcolor(this,'#e6f3fb')">
					</s:if>
					<s:else>
						<tr align="center" onmouseover="chageBgcolor(this)"
							onmouseout="outBgcolor(this,'')">
					</s:else>
					<td><s:if test="#pageStatus.index%2==1">
							<font>
						</s:if> <s:else>
							<font color="#c0dcf2">
						</s:else> <s:property value="#pageStatus.index+1" /> </font></td>
					<td>${pageList.fk_stoid}</td>
					<td>${pageList.number}</td>
					<td>${pageList.matetag }</td>
					<td>${pageList.storehouse }</td>
					<td>${pageList.parClass}</td>
					<td>${pageList.place }</td>
					<td>${pageList.bjcs}</td>
					<td><fmt:formatNumber value="${pageList.price }" pattern="#.00"/></td>
					<td>${pageList.xingbie }</td>
					<td>
<%--					<a href="GzstoreAction_findGgbj_Process.action?id=${pageList.id }">查看对应工序/</a>--%>
					<a href="javascript:;" onclick="showcode(${pageList.id},'${pageList.number}')">二维码</a>/
					<a href="GzstoreAction_findcdList.action?id=${pageList.id }">校验记录</a>/
					<a href="GzstoreAction_initUpdate.action?id=${pageList.id }&status=jy">校验</a>/
					<a href="GzstoreAction_initUpdate.action?id=${pageList.id }&status=${status}">修改</a>/
					<a href="GzstoreAction_DelGzbj.action?del_id=${pageList.id }&status=${status}" onclick="return confirm('确定要删除吗？')">删除</a>
					</td>
					</tr>
				</s:iterator>
				<tr>
					<s:if test="errorMessage==null">
						<td colspan="12" align="right">第 <font color="red"><s:property
									value="cpage" /> </font> / <s:property value="total" /> 页 <fenye:pages
								cpage="%{cpage}" total="%{total}" url="%{url}" styleClass="page"
								theme="number" />
					</s:if>
					<s:else>
						<td colspan="11" align="center" style="color: red">
							${errorMessage}
					</s:else>
				</tr>
			</table>
		</div>
		<br>
	</div>
	<%@include file="/util/foot.jsp"%>
	</center>
	<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
	<script type="text/javascript"  src="<%=request.getContextPath()%>/javascript/jquery-1.8.3.js"></script>
	<script type="text/javascript">
//	$(function(){
//		var x1 = document.getElementById("price").innerText;  
//		 x1 = parseInt(x1*10)/10;
//		alert("x1="+x1);  
//	})
	function add() {
<%--	$("#showProcess").attr("src", "/System/gzbj/addgzbj.jsp");--%>
<%--	chageDiv('block');--%>
		window.location.href = "<%=request.getContextPath()%>/System/gzbj/addgzbj.jsp?status=${status}";
	}
function showcode(id,no){
	$("#showProcess").attr("src", "<%=request.getContextPath()%>/System/gzbj/gz_QRCode.jsp?id="+id+"&no="+no);
	chageDiv('block');
}
</script>
</body>
</html>
