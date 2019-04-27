<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib uri="/WEB-INF/fenye.tld" prefix="fenye"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
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
td:hover .qs_ul {
	display: block;
}

.qs_ul {
	display: none;
	border: 1px solid #999;
	list-style: none;
	margin: 0;
	padding: 0;
	position: absolute;
	width: auto;
	background: #CCC;
	color: green;
}
.ztree li a {
	color: #fff;
}

/* 带复选框的下拉框 */
ul li {
	list-style: none;
	padding: 0px;
	margin: 0px;
}

.select_checkBox {
	border: 0px solid red;
	position: relative;
	display: inline-block;
}

.chartQuota {
	height: 23px;
	float: left;
	display: inline-block;
	border: 0px solid black;
	position: relative;
}

.chartOptionsFlowTrend {
	z-index: 300;
	background-color: white;
	border: 1px solid gray;
	display: none;
	position: absolute;
	left: 0px;
	top: 23px;
	width: 150px;
}

.chartOptionsFlowTrend ul {
	float: left;
	padding: 0px;
	margin: 5px;
}

.chartOptionsFlowTrend li { /* float:left; */
	display: block;
	position: relative;
	left: 0px;
	margin: 0px;
	clear: both;
}

.chartOptionsFlowTrend li * {
	float: left;
}

a:-webkit-any-link {
	color: -webkit-link;
	text-decoration: underline;
	cursor: auto;
}

.chartQuota p a {
	float: left;
	height: 21px;
	outline: 0 none;
	border: 1px solid #ccc;
	line-height: 22px;
	padding: 0 5px;
	overflow: hidden;
	background: #eaeaea;
	color: #313131;
	text-decoration: none;
}

.chartQuota p {
	margin: 0px;
	folat: left;
	overflow: hidden;
	height: 23px;
	line-height: 24px;
	display: inline-block;
}

.chartOptionsFlowTrend p {
	height: 23px;
	line-height: 23px;
	overflow: hidden;
	position: relative;
	z-index: 2;
	background: #fefbf7;
	padding-top: 0px;
	display: inline-block;
}

.chartOptionsFlowTrend p a {
	border: 1px solid #fff;
	margin-left: 15px;
	color: #2e91da;
}
</style>
	</head>
	<body>
		<%@include file="/util/sonTop.jsp"%>
		<div id="bodyDiv" align="center" class="transDiv"
			onclick="chageDiv('none')">
		</div>		
		<div align="center">
			<h2>
				库存领用管理
			</h2>
			<form action="LendNectAction!findAllIsBent.action?tag=nect" method="post">
			<table class="table" style="width: 95%;">
					<tr>
						<th>
							件号:${goods.goodsBarcode}
						</th>
						<td>
							<input type="text" name="goods.goodsMarkId"
								value="${goods.goodsMarkId }" />
						</td>
						<th>
							批次:
						</th>
						<td>
							<input type="text" name="goods.goodsLotId"
								value="${goods.goodsLotId }" />
						</td>
						<th>
							名称:
						</th>
						<td>
							<input type="text" name="goods.goodsFullName"
								value="${goods.goodsFullName }" />
						</td>
<%--						<th>--%>
<%--							规格：--%>
<%--						</th>--%>
<%--						<td>--%>
<%--							<input type="text" name="goods.goodsFormat"--%>
<%--								value="${goods.goodsFormat }" />--%>
<%--						</td>--%>
					</tr>
					<tr>

						<th>
							仓区:
						</th>
						<td>
							<select id="goodHouseName" name="goods.goodHouseName"
								style="width: 155px;">
								<option>
									${goods.goodHouseName}
								</option>
								<option></option>
							</select>
						</td>
						<th>
							库位:
						</th>
						<td>
							<input type="text" name="goods.goodsPosition"
								value="${goods.goodsPosition}"/>
						</td>
						<th>
							入库日期从
						</th>
						<td>
							<input class="Wdate" type="text" name="startDate"
								value="${startDate}" size="15"
								onClick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'whyGreen'})" />
								到
							<input class="Wdate" type="text" name="endDate"
								value="${ endDate}" size="15"
								onClick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'whyGreen'})" />
						</td>
					</tr>
					<tr>
						<th>
							需求部门:
						</th>
						<td>
							<input type="text" name="goods.demanddept"
								value="${goods.demanddept}"/>
						</td>
						<th></th>
						<td></td>
						<th></th>
						<td></td>
					</tr>
					<tr>
						<th colspan="8">
							<input type="submit" value="查询" class="input" />
							<input type="hidden" name="cpage" value="1"/> 
							<input type="hidden" name="goods.goodsId" value=""/>
						</th>
						
					</tr>
			</table>
			</form>
		<form action="LendNectAction!pladdNect.action?tag=nect" method="POST"  onsubmit="return check()">
			<table class="table" style="width: 95%;">
				<tr bgcolor="#c0dcf2" height="30px"
					style="border-collapse: separate;">
					<th align="center">
						<input type="checkbox" onclick="chageAllCheck(this);addNums()">
					</th>
					<th align="center">
						序号
					</th>
					<th align="center" style="width: 57px;">
						库别
					</th>
					<th align="center">
						仓区
					</th>
					<th align="center">
						库位
					</th>
					<th align="center">
						件号
					</th>
					<th align="center">
						批次
					</th>
					<th align="center">
						版本
					</th>
					<th align="center">
						供料属性
					</th>
					<th align="center">
						物料类别
					</th>
					<th align="center">
						品名
					</th>
					<th align="center">
						规格
					</th>
					<th align="center">
						可领用数量
					</th>
					<th align="center">
						单位
					</th>
					<th align="center">
						转换数量
					</th>
					<th align="center">
						转换单位
					</th>
					<th align="center">
						客户
					</th>
					<th align="center">
						供应商
					</th>
					<th align="center">
						入库类型
					</th>
					<th align="center">
						入库日期
					</th>
					<th align="center">
						状态
					</th>
					<th align="center">
						封存审批
					</th>
					<th align="center">
						需求部门
					</th>
					<th align="center" width="30">
						操作</h1>
					</th>
					
				</tr>
				
				<s:if test="{list.size()>0}">
					<s:iterator value="list" status="see" id="gs">
						<s:if test="#see.index%2==1">
							<tr align="center" bgcolor="#e6f3fb"
								onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'#e6f3fb')">
						</s:if>
						<s:else>
							<tr align="center" onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'')">
						</s:else>
						<td>
							<input type="checkbox" name="ids"  value="${gs.goodsId}" onclick="addNum(${gs.goodsId},this)" />
						</td>
						<td>
							<s:property value="#see.index+1" />
						</td>
						<td align="left" style="color: gray;">
							${gs.goodsClass}
						</td>
						
						<td align="left" style="color: gray;width:100px;">
							${gs.goodHouseName}
						</td>
						<td align="left" style="color: gray;">
							${gs.goodsPosition}
						</td>
						<td align="left">
							${gs.goodsMarkId}
<%--							<s:if test="#gs.processNo!=null">(<font color="red">${gs.processNo}</font> )</s:if>--%>
<%--							<s:if test='#gs.ywmarkId!=null'>--%>
<%--								(<font color="green">${gs.ywmarkId}</font> )--%>
<%--							</s:if>--%>
						</td>
						<td align="right">
							${gs.goodsLotId}
						</td>
						<td>
							${gs.banBenNumber}
						</td>
						<td align="left">
							<s:if test="#gs.kgliao=='TK'">
										自购(TK)
										</s:if>
							<s:elseif test="#gs.kgliao=='TK AVL'">
										指定供应商(TK AVL)
										</s:elseif>
							<s:elseif test="#gs.kgliao=='CS'">
										客供(CS)
										</s:elseif>
							<s:elseif test="#gs.kgliao=='TK Price'">
										完全指定(TK Price)
										</s:elseif>
						</td>
						<td align="left">
							${gs.wgType}
						</td>
						<td align="left" style="max-width: 50px; overflow: hidden; white-space: nowrap; text-overflow: ellipsis;">
							<font size="1">${gs.goodsFullName}</font>
									<ul class="qs_ul">
										<li>
											${gs.goodsFullName}
										</li>
							</ul>
						</td>
						<td align="left" style="max-width: 50px; overflow: hidden; white-space: nowrap; text-overflow: ellipsis;">
							<font size="1">${gs.goodsFormat}</font>
									<ul class="qs_ul">
										<li>
											${gs.goodsFormat}
										</li>
							</ul>
						</td>
						<td align="right" >
							<input type="text" value="${gs.goodsCurQuantity}" id="input_${gs.goodsId}"
							name="" style="width: 75px;" 
							onchange="changvalue(${gs.goodsCurQuantity},this);numyanzheng(this)"/>
						</td>
						<td>
							${gs.goodsUnit}
						</td>
						<td align="right">
							${gs.goodsZhishu}
						</td>
						<td>
							${gs.goodsStoreZHUnit}
						</td>
						<td>
							${gs.goodsCustomer}
						</td>
						<td>
							${gs.goodsArtsCard}
						</td>
						<td>
							${gs.goodsStyle}
						</td>
						<td>
							${gs.goodsChangeTime}
						</td>
						
						<td>
							<s:if test="#gs.fcStatus=='封存'">
								<font color="red">封存</font>
							</s:if>
							<s:else>可用</s:else>
						</td>
						<td>
							<s:if test="#gs.fcApplyStatus!=null">
							${gs.fcApplyStatus}
						   </s:if>
						</td>
						<td>
							${gs.demanddept}
						</td>
						<td>
							
							<a href="LendNectAction!createOneNect.action?goodsId=${gs.goodsId}">领用</a>
						</td>
						
						</tr>
					</s:iterator>
					<tr>
						<th colspan="11" align="right">

						</th>
						<th>
							<fmt:formatNumber value="${sumcount}" pattern="#,#00.0#"></fmt:formatNumber>
						</th>
						<th colspan="20"></th>
					</tr>
					<tr>
						<td colspan="25" align="right">
							共
							<s:property value="total" />
							页 第
							<s:property value="cpage" />
							页
							<fenye:pages cpage="%{cpage}" total="%{total}" url="%{url}"
								styleClass="page" theme="number" />

						</td>
					</tr>
				</s:if>
				
				
				<s:else>
					<tr>
						<td colspan="21" style="font-size: 15px; color: red;">
							对不起，没有查到相关的库存信息
						</td>
					</tr>
				</s:else>
				<tr>
					<td colspan="25" align="center">
						<b>刷卡:</b><input type="text" value="" name="cards" id="cards"/><br/>
						<input type="submit" value="领用" class="input" id="sub" />
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
		<script type="text/javascript">

//获取仓区
$(function(){
	$.ajax( {
		type : "POST",
		url : "WarehouseAreaAction_findwaListByNO.action",
		data : {
			wareHouseName:'综合库'    
		},
		dataType : "json",
		async:false,
		success : function(data) {
			if (data != null) {
				$("#goodHouseName").empty();
				$(data).each(function(){
						$("#goodHouseName").append('<option value='+this.goodHouseName+' >'+this.goodHouseName+'</option>');
				});
			}

		}
	});
	duoxuaSelect('goodHouseName');
	$("#textselectgoodHouseName").val('${goods.goodHouseName}');
			
});
function addNums(){
	var ids =	document.getElementsByName('ids');
	if(ids!=null && ids.length>0){
		for(var i=0;i<ids.length;i++){
			var id=ids[i].value;
			addNum(id,ids[i]);
		}
	}
}
function changvalue(num,obj){
	if(obj.value!='' && obj.value>num){
		alert('领用数量('+obj.value+')不能超过现有库存量('+num+')');
		obj.value=num;
		$(obj).focus();
	}
}
function addNum(id,obj){
	if(obj.checked){
		$("#input_"+id).attr('name','nums');
	}else{
		$("#input_"+id).removeAttr('name');
	}
}
function check(){
	var cards=$("#cards").val();
	if(cards==''){
		alert('请刷卡!~');
		$("#cards").focus();
		return false;
	}
	var ids =	document.getElementsByName('ids');
	var bool=false;
	for(var i=0;i<ids.length;i++){
		if(ids[i].checked){
			bool = true;
			break;
		}
	}
	if(!bool){
		alert('请至少选择一项物料领用!~');
		return false;
	}
	$("#sub").attr('disabled','disabled');
}
</script>
	</body>
</html>