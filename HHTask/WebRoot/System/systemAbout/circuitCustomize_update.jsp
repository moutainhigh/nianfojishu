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
		<SCRIPT type="text/javascript">
			$(function(){
				$("#ccForm").bind("submit",function(){
					if($("#name").val()==""){
						alert("请填写流程名称!");
						$("#name").focus();
						return false;
					}
					return true;
				})
			});
		</SCRIPT>
	</head>
	<body>
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
				<div style="float: left; width: 45%; padding-top: 5px;"
					align="right">
					<a
						href="ModuleFunctionAction!findMfByIdForJump.action?id=${moduleFunction.id}"
						style="color: #ffffff">查询所有</a>
					<a
						href="ModuleFunctionAction!findMfByIdForJump.action?id=${moduleFunction.id}"
						style="color: #ffffff">刷新</a>
				</div>
			</div>
			
			<div align="center">
				<form id="ccForm" action="CircuitCustomize_updateCirCus.action"
					method="post">
					<input type="hidden" " name="id" value="${circuitCustomize.id}" />
					<table class="table" style="width: 60%" id="mytable">
						<tr>
							<th colspan="2">
								修改流程
							</th>
						</tr>
						<tr>
							<th align="right">
								流程名称:
							</th>
							<td>
								<input id="name" name="circuitCustomize.name"
									value="${circuitCustomize.name}" />
							</td>
						</tr>
						<tr>
							<th align="right">
								流程类型:
							</th>
							<td>
								<s:if test="circuitCustomize.auditType=='oneBack'">
									<input name="circuitCustomize.auditType" type="radio"
										value="oneBack" checked="checked" />
								一次打回
								<input name="circuitCustomize.auditType" type="radio"
										value="lastBack" />
								最终审核
								<input name="circuitCustomize.auditType" type="radio"
										value="oneAudit" />
								任一审核
								</s:if>
								<s:elseif test="circuitCustomize.auditType=='lastBack'">
									<input name="circuitCustomize.auditType" type="radio"
										value="oneBack" />
								一次打回
								<input name="circuitCustomize.auditType" type="radio"
										value="lastBack" checked="checked" />
								最终审核
								<input name="circuitCustomize.auditType" type="radio"
										value="oneAudit" />
								任一审核
								</s:elseif>
								<s:else>
									<input name="circuitCustomize.auditType" type="radio"
										value="oneBack" />
								一次打回
								<input name="circuitCustomize.auditType" type="radio"
										value="lastBack" />
								最终审核
								<input name="circuitCustomize.auditType" type="radio"
										value="oneAudit" checked="checked" />
								任一审核
								</s:else>
							</td>
						</tr>
						<tr>
							<th align="right">
								验证码:
							</th>
							<td>
							<s:if test='circuitCustomize.isVerification=="是"'>
							<input type="radio" id="isVerification1"  
										name="circuitCustomize.isVerification" value="是"   checked="checked"/>	
										<label for="isVerification1">是</label>
										<input type="radio" id="isVerification2"  
										name="circuitCustomize.isVerification" value="否" />	
										<label for="isVerification2">否</label>
							</s:if>
							<s:if test='circuitCustomize.isVerification=="否"'>
							<input type="radio" id="isVerification1"  
										name="circuitCustomize.isVerification" value="是"  />	
										<label for="isVerification1">是</label>
										<input type="radio" id="isVerification2"  
										name="circuitCustomize.isVerification" value="否"  checked="checked"/>	
										<label for="isVerification2">否</label>
							</s:if>
							<s:if test='circuitCustomize.isVerification==null||circuitCustomize.isVerification==""'>
								<input type="radio" id="isVerification1"  
										name="circuitCustomize.isVerification" value="是"  />	
										<label for="isVerification1">是</label>
										<input type="radio" id="isVerification2"  
										name="circuitCustomize.isVerification" value="否"  checked="checked"/>	
										<label for="isVerification2">否</label>
							</s:if>
									
							</td>
						</tr>
						<tr>
							<th align="right">
								是否必填审批意见:
							</th>
							<td>
								<s:if test='circuitCustomize.isopinion == "是"'>
									<input type="radio" id="isopinion1"  
										name="circuitCustomize.isopinion" value="是" checked="checked"/>	
										<label for="isVerification1">是</label>
										<input type="radio" id="isVerification2"  
										name="circuitCustomize.isopinion" value="否"   />	
										<label for="isopinion2">否</label>
								</s:if>
								<s:else>
									<input type="radio" id="isopinion1"  
										name="circuitCustomize.isopinion" value="是" />	
										<label for="isVerification1">是</label>
										<input type="radio" id="isVerification2"  
										name="circuitCustomize.isopinion" value="否"   checked="checked"/>	
										<label for="isopinion2">否</label>
								</s:else>	
							</td>
						</tr>
						<tr>
							<th align="right">
								是否增加审批选项: 
							</th>
							<td>
								<s:if test='circuitCustomize.isspoption == "是"'>
									<input type="radio" id="isspoption1"  
										name="circuitCustomize.isspoption" value="是" onclick="showlast_tr()" checked="checked"/>	
										<label for="isspoption1_1">是</label>
										<input type="radio" id="isspoption2"  
										name="circuitCustomize.isspoption" value="否"  onclick="hidelast_tr()"  />	
										<label for="isspoption2_1">否</label>
								</s:if>
								<s:else>
									<input type="radio" id="isspoption1"  
										name="circuitCustomize.isspoption" value="是" onclick="showlast_tr()" />	
										<label for="isspoption1_1">是</label>
										<input type="radio" id="isspoption2"  
										name="circuitCustomize.isspoption" value="否"  onclick="hidelast_tr()"  checked="checked"/>	
										<label for="isspoption2_1">否</label>
								</s:else>
									
							</td>
						</tr>
						<tr style="display: none;" id="last_tr" >
							<th align="right">
								单选或者多选:
							</th>
							<td>
								<s:if test='circuitCustomize.danxuanorduoxuan == "单选"'>
									<input type="radio" id="danxuanorduoxuan1"  
										name="circuitCustomize.danxuanorduoxuan" value="单选"  checked="checked"/>	
										<label for="danxuanorduoxuan11_1">单选</label>
										<input type="radio" id="danxuanorduoxuan2"  
										name="circuitCustomize.danxuanorduoxuan" value="多选"   />	
										<label for="danxuanorduoxuan2_1">多选</label>
								</s:if>
								<s:else>
									<input type="radio" id="danxuanorduoxuan1"  
										name="circuitCustomize.danxuanorduoxuan" value="单选"  />	
										<label for="danxuanorduoxuan11_1">单选</label>
										<input type="radio" id="danxuanorduoxuan2"  
										name="circuitCustomize.danxuanorduoxuan" value="多选"  checked="checked" />	
										<label for="danxuanorduoxuan2_1">多选</label>
								</s:else>
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									<input type="button" value="添加" onclick="add()"/>
									<input type="button" value="删除" onclick="del()"/>
									
									<input type="hidden" value="<s:property value="listoption.size()" />" id="listsize" >
							</td>
						</tr>
						<s:iterator value="listoption" id="option" status="pageStatus">
							<tr>
								<th align="right">
									审批选项:
								</th>
								<td>
									<input type="text" value="${option.name}" name="circuitCustomize.listoption[${pageStatus.index}].name "/>
								</td>
							</tr>
						</s:iterator>
						<tr id="befroetr">
							<th align="right">
								说明:
							</th>
							<td>
								<textarea rows="8" cols="40" name="circuitCustomize.more">${circuitCustomize.more}</textarea>
							</td>
						</tr>
						<tr>
							<td align="center" colspan="2">
								<input type="submit" value="修改" class="input" />
								<input type="button" value="返回" class="input" onclick="goBack()" />
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
<SCRIPT type="text/javascript">
$(function(){
	if('${circuitCustomize.isspoption}' == '是'){
		$("#last_tr").show();
	}
	
})
function showlast_tr(){
	$("#last_tr").show();
}
function hidelast_tr(){
	$("#last_tr").hide();
}
var index= $("#listsize").val();
var count = 0;
function add(){
	index = parseInt(index);
	$("#befroetr").before('<tr><th align="right">审批选项:</th><td><input type="text" name="circuitCustomize.listoption['+index+'].name" ></td></tr>');
	index++;
}
function del(){
	if(index==0){
		if(count>0){
			alert('都告诉你不要再删了。你也删不掉啊。不听话!')
		}else{
			alert('已经没有了，不要再删了!')	
		}
		count++;
	}else{
		var n = $('#mytable tr').length;
		$($('#mytable tr')[n - 3]).remove();
		index--;
	}
}

</SCRIPT>
	</body>
</html>
