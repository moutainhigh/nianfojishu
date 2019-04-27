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
		<div id="gongneng"
			style="width: 100%; border: thin solid 1px #0170b8; margin-top: 10px;">
			<div id="xitong"
				style="width: 100%; font-weight: bold; padding-left: 35px; padding-top: 5px; padding-bottom: 5px; "
				align="left">
				<div style="float: left; width: 50%" align="left">
					
				</div>
				<div style="float: left; width: 48%" align="right">
					<a href="javascript:location.reload();" style="color: #ffffff">刷新</a>
				</div>
			</div>
			
			<div align="center">
			<form action="business_batchPrintProof.action" method="post" onsubmit="return vali()">
				<h3>申请付款</h3>
				<table  class="table">
					<tr>
						<th align="center" colspan="11">业务</th>
					</tr>
					<tr bgcolor="#c0dcf2" height="50px">
						<td></td>
						<td align="center">序号</td>
						<td align="center">业务类型</td>
						<td align="center" width="260px;">业务内容</td>
						<td align="center">费用金额</td>
						<td align="center">收款单位</td>
						<td align="center">经办人</td>
						<td align="center">办理时间</td>
						<td></td>
						<td></td>
						<td></td>
						<Td></Td>
					</tr>
					<s:iterator value="list" id="pageList" status="pageStatus">
						<s:if test="#pageStatus.index%2==1">
							<tr align="center" bgcolor="#e6f3fb"
								onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'#e6f3fb')">
						</s:if>
						<s:else>
							<tr align="center" onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'')">
						</s:else>
						<td><input type="checkbox" name="selected" value="${pageList.id }" /></td>
						<td>
							<s:if test="#pageStatus.index%2==1">
								<font>
							</s:if>
							<s:else>
								<font color="#c0dcf2">
							</s:else>
							<s:property value="#pageStatus.index+1" />
							</font>
						</td>
									<td>${pageList.type }</td>
									<td>${pageList.content }</td>
									<s:if test="#pageList.money == 0.0">
										<td>0.00</td>
									</s:if>
									<s:else>
									<td>${pageList.money }${pageList.currencyType }</td>
									</s:else>
									<td>${pageList.collectionUnit }</td>
									<td>${pageList.transactor }</td>
									<td>${pageList.time }</td>
									<td><input type="button" value="明细" style="width: 60px; height: 30px;" onclick="detail(${pageList.id})"/></td>
									<td><input type="button" value="发票" style="width: 60px; height: 30px;" onclick="invoice(${pageList.id})"/></td>
									<td><input type="button" value="提交审核" style="width: 80px; height: 30px;" onclick="print(${pageList.id })"/></td>
						</tr>
						</s:iterator>
						<tr><td colspan="11" align="center"><input type="submit" value="批量提交审核" style="width: 100px; height: 50px;"/></td></tr>
							<tr>
						<s:if test="errorMessage==null">
							<td colspan="6" align="right">
								第
								<font color="red"><s:property value="cpage" /> </font> /
								<s:property value="total" />
								页
								<fenye:pages cpage="%{cpage}" total="%{total}" url="%{url}"
									styleClass="page" theme="number" />
						</s:if>
						<s:else>
							<td colspan="11" align="center" style="color: red">
								${errorMessage}
						</s:else>
						</td>
					</tr>
				</table>
			</div>
			<br>
		</div>
		</form>
		<%@include file="/util/foot.jsp"%>
		</center>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
		<script type="text/javascript">
			function invoice(id){
				window.location = "business_queryInvoiceByBusinessId.action?id="+id+"&flow=A";
			}
			function detail(id){
				window.location = "business_queryDetailByBusinessId.action?id="+id+"&flow=A";
			}
			function print(id){
				window.location = "business_batchPrintProof.action?selected="+id;
			}
			function vali(){
			 var selectList = document.getElementsByName("selected");
			  for(var i = 0;i<selectList.length;i++){
			    if(selectList[i].checked){
			      return true;
			    }
			  }
			  alert("请选择需要审核的业务！谢谢");
			  return false;
			}
		</script>
	</body>
</html>
