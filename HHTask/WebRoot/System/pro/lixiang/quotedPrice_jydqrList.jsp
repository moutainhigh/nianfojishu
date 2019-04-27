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
		<div id="bodyDiv" align="center" class="transDiv">
		</div>
		<div id="contentDiv"
			style="position: absolute; z-index: 255; width: 900px; display: none;"
			align="center">
			<div id="closeDiv"
				style="position: relative; top: 165px; left: 0px; right: 200px; z-index: 255; background: url(<%=basePath%>/images/bq_bg2.gif); width: 980px;">
				<table style="width: 100%">
					<tr>
						<td>
							<span id="title">纪要审批</span>
						</td>
						<td align="right">
							<img alt="" src="<%=basePath%>/images/closeImage.png" width="30"
								height="32" onclick="chageDiv('none')">
						</td>
					</tr>
				</table>
				<div id="operatingDiv"
					style="background-color: #ffffff; width: 100%;">
					<table style="width: 100%">
					<tr>
						<td align="right" width="40%">
							审批意见:
						</td>
						<td align="left" width="60%">
							<input type="hidden" id="jydId" value="0"/>
							<input type="hidden" id="hidIndex" value="0"/>
							<textarea id="spMsg" rows="4" cols="80"></textarea>
						</td>
					</tr>
					<tr>
					<td colspan="2" align="center">
						<input type="button" class="sp_<s:property value="#jydclIndex"/>" value="同意" onclick="spjyd('agree')">
						<input type="button" class="sp_<s:property value="#jydclIndex"/>" value="打回" onclick="spjyd('back')">
					</td>
					</tr>
				</table>
				</div>
			</div>
		</div>
		
		<div id="gongneng" style="width: 100%;">
			<div align="center">
				<h3>进度纪要</h3>
			</div>
			<div align="center">
			</div>
			<div align="center">
				<table class="table">
				<tr>
					<td align="center">
					序号
					</td>
					<td align="center">
						编号
					</td>
					<td align="center">
						标题
					</td>
					<td align="center">
						录入人
					</td>
					<td align="center">
						纪要时间
					</td>
					<td align="center">
						内容
					</td>
					<td align="center">
						指派时间
					</td>
					<td align="center">
						负责人
					</td>
					<td align="center">
						预完成时间
					</td>
					<td align="center">
						状态
					</td>
					<td align="center">
						操作
					</td>
				</tr>
				<tr>
					<td align="center" colspan="11" bgcolor="red">待处理</td>
				</tr>
				<s:set name="jydclIndex" value="0"></s:set>
				<s:iterator value="qpjydclList" id="pageqpjy" status="jyStatus">
				<s:if test="#pageqpjy.qpjyDetailList==null||#pageqpjy.qpjyDetailList.size()==0">
					<s:if test="#jydclIndex%2==1">
							<tr align="center" bgcolor="#e6f3fb"
								onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'#e6f3fb')">
						</s:if>
						<s:else>
							<tr align="center" onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'')">
						</s:else>
						<td align="center">
						<s:property value="#jyStatus.index+1"/>
					</td>
					<td align="center">
						<s:property value="#pageqpjy.jyNumber"/>
					</td>
					<td align="left">
						<s:property value="#pageqpjy.title"/>
					</td>
					<td align="center">
					<s:property value="#pageqpjy.addUserName"/>
					</td>
					<td align="center">
						<s:property value="#pageqpjy.jyTime"/>
					</td>
					<td align="center">
					</td>
					<td align="center">
					</td>
					<td align="center">
					</td>
					<td align="center">
					</td>
					<td align="center">
					</td>
					<td align="center">
					</td>
						</tr>
				</s:if>
				<s:else>
					<s:if test="#jydclIndex%2==1">
							<tr align="center" bgcolor="#e6f3fb" 
								onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'#e6f3fb')">
						</s:if>
						<s:else>
							<tr align="center" onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'')">
						</s:else>
						<td align="center" rowspan="<s:property value="#pageqpjy.qpjyDetailList.size()"/>">
						<s:property value="#jyStatus.index+1"/>
					</td>
					<td align="center" rowspan="<s:property value="#pageqpjy.qpjyDetailList.size()"/>">
						<s:property value="#pageqpjy.jyNumber"/>
					</td>
					<td align="left" rowspan="<s:property value="#pageqpjy.qpjyDetailList.size()"/>">
						<s:property value="#pageqpjy.title"/>
					</td>
					<td align="center" rowspan="<s:property value="#pageqpjy.qpjyDetailList.size()"/>">
					<s:property value="#pageqpjy.addUserName"/>
					</td>
					<td align="center" rowspan="<s:property value="#pageqpjy.qpjyDetailList.size()"/>">
						<s:property value="#pageqpjy.jyTime"/>
					</td>
					<s:iterator value="#pageqpjy.qpjyDetailList" id="pageqpjyDetail" status="jydStatus">
					<s:if test="#jydStatus.index>0">
					<s:if test="#jydclIndex%2==1">
							<tr align="center" bgcolor="#e6f3fb" 
								onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'#e6f3fb')">
						</s:if>
						<s:else>
							<tr align="center" onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'')">
						</s:else>
					</s:if>
					<td align="left">
						<s:property value="#pageqpjyDetail.jyContext"/>
					</td>
					<td align="center">
						<s:property value="#pageqpjyDetail.zpTime"/>
					</td>
					<td align="center">
						<s:property value="#pageqpjyDetail.zxUsers"/>
					</td>
					<td align="center">
						<s:property value="#pageqpjyDetail.yqTime"/>
					</td>
					<td align="center">
					<s:if test="#pageqpjyDetail.status=='待指派'||#pageqpjyDetail.status=='待执行'||#pageqpjyDetail.status=='重新执行'">
						<font color="red"><s:property value="#pageqpjyDetail.status"/></font>
						</s:if>
						<s:elseif test="#pageqpjyDetail.status=='待确认'">
						<font color="green"><s:property value="#pageqpjyDetail.status"/></font>
						</s:elseif>
						<s:elseif test="#pageqpjyDetail.status=='确认关闭'">
						<font color="gary"><s:property value="#pageqpjyDetail.status"/></font>
						</s:elseif>
					</td>
					<td align="center">
						<input type="button" class="sp_<s:property value="#jydclIndex"/>" value="审批" onclick="showspjyd(<s:property value="#jydclIndex"/>,<s:property value="#pageqpjyDetail.id"/>)">
						<s:if test="#pageqpjyDetail.status=='待确认'||#pageqpjyDetail.status=='确认关闭'||#pageqpjyDetail.status=='重新执行'">
						<input type="button" value="文件与方案" onclick="toshowjyfile(<s:property value="#pageqpjyDetail.id"/>)">
						</s:if>
					</td>
					</tr>
					</s:iterator>
				</s:else>
				<s:set name="jydclIndex" value="#jydclIndex+1"></s:set>
				</s:iterator>
				
				
				
				<tr>
					<td align="center" colspan="11" bgcolor="green">历史</td>
				</tr>
				<s:set name="jyIndex" value="0"></s:set>
				<s:iterator value="qpjyList" id="pageqpjy" status="jyStatus">
				<s:if test="#pageqpjy.qpjyDetailList==null||#pageqpjy.qpjyDetailList.size()==0">
					<s:if test="#jyIndex%2==1">
							<tr align="center" bgcolor="#e6f3fb"
								onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'#e6f3fb')">
						</s:if>
						<s:else>
							<tr align="center" onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'')">
						</s:else>
						<td align="center">
						<s:property value="#jyStatus.index+1"/>
					</td>
					<td align="center">
						<s:property value="#pageqpjy.jyNumber"/>
					</td>
					<td align="left">
						<s:property value="#pageqpjy.title"/>
					</td>
					<td align="center">
					<s:property value="#pageqpjy.addUserName"/>
					</td>
					<td align="center">
						<s:property value="#pageqpjy.jyTime"/>
					</td>
					<td align="center">
					</td>
					<td align="center">
					</td>
					<td align="center">
					</td>
					<td align="center">
					</td>
					<td align="center">
					</td>
					<td align="center">
					</td>
						</tr>
				</s:if>
				<s:else>
					<s:if test="#jyIndex%2==1">
							<tr align="center" bgcolor="#e6f3fb" 
								onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'#e6f3fb')">
						</s:if>
						<s:else>
							<tr align="center" onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'')">
						</s:else>
						<td align="center" rowspan="<s:property value="#pageqpjy.qpjyDetailList.size()"/>">
						<s:property value="#jyStatus.index+1"/>
					</td>
					<td align="center" rowspan="<s:property value="#pageqpjy.qpjyDetailList.size()"/>">
						<s:property value="#pageqpjy.jyNumber"/>
					</td>
					<td align="left" rowspan="<s:property value="#pageqpjy.qpjyDetailList.size()"/>">
						<s:property value="#pageqpjy.title"/>
					</td>
					<td align="center" rowspan="<s:property value="#pageqpjy.qpjyDetailList.size()"/>">
					<s:property value="#pageqpjy.addUserName"/>
					</td>
					<td align="center" rowspan="<s:property value="#pageqpjy.qpjyDetailList.size()"/>">
						<s:property value="#pageqpjy.jyTime"/>
					</td>
					<s:iterator value="#pageqpjy.qpjyDetailList" id="pageqpjyDetail" status="jydStatus">
					<s:if test="#jydStatus.index>0">
					<s:if test="#jyIndex%2==1">
							<tr align="center" bgcolor="#e6f3fb" 
								onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'#e6f3fb')">
						</s:if>
						<s:else>
							<tr align="center" onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'')">
						</s:else>
					</s:if>
					<td align="left">
						<s:property value="#pageqpjyDetail.jyContext"/>
					</td>
					<td align="center">
						<s:property value="#pageqpjyDetail.zpTime"/>
					</td>
					<td align="center">
						<s:property value="#pageqpjyDetail.zxUsers"/>
					</td>
					<td align="center">
						<s:property value="#pageqpjyDetail.yqTime"/>
					</td>
					<td align="center">
						<s:if test="#pageqpjyDetail.status=='待指派'||#pageqpjyDetail.status=='待执行'||#pageqpjyDetail.status=='重新执行'">
						<font color="red"><s:property value="#pageqpjyDetail.status"/></font>
						</s:if>
						<s:elseif test="#pageqpjyDetail.status=='待确认'">
						<font color="green"><s:property value="#pageqpjyDetail.status"/></font>
						</s:elseif>
						<s:elseif test="#pageqpjyDetail.status=='确认关闭'">
						<font color="gary"><s:property value="#pageqpjyDetail.status"/></font>
						</s:elseif>
					</td>
					<td align="center">
					<s:if test="#pageqpjyDetail.status=='待确认'||#pageqpjyDetail.status=='确认关闭'||#pageqpjyDetail.status=='重新执行'">
						<input type="button" value="文件与方案" onclick="toshowjyfile(<s:property value="#pageqpjyDetail.id"/>)">
						</s:if>
					</td>
					</tr>
					</s:iterator>
				</s:else>
				<s:set name="jyIndex" value="#jyIndex+1"></s:set>
				</s:iterator>
				</table>
			</div>
		</div>
		<%@include file="/util/foot.jsp"%>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
<SCRIPT type="text/javascript">
function showspjyd(index ,id){
	$("#hidIndex").val(index);
	$("#jydId").val(id);
	$("#spMsg").val("");
	chageDiv("block");
}

function spjyd(op){
	$.ajax( {
		url : 'QuotedPrice_spjyd.action',
		dataType : 'json',
		type : "post",
		data : {
					'qpjyd.id' : $("#jydId").val(),
					'qpjyd.spMsg' : $("#spMsg").val(),
					op : op
					},
		cache : false,//防止数据缓存
		success : function(data) {
					if(data=="成功"){
						alert("操作成功!");
						$(".sp_"+$("#hidIndex").val()).hide();
					}else{
						alert(data);
					}
					chageDiv("none");
		}

	});
}
function toshowjyfile(id){
	window.location.href ="QuotedPrice_toshowjyfile.action?id="+id;
}
</SCRIPT>
	</body>
</html>
