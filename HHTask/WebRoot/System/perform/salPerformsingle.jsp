<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@page import="com.task.util.Util"%>
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
.table2 {
	font-size: 16px;
	padding: 0px;
	margin: 0px;
	border-collapse: collapse;
	border-width: 0px 0 0 0px;
	width: 100%;
}

.table2 th,.table2 td {
	border-width: 0 0px 0px 0;
}
</style>
	</head>
	<body style="background: url('');">
		<center>
			<%@include file="/util/sonTop.jsp"%>
			<form action="performsingleAction_addPerformsingle.action"
				method="post" theme="simple">
				<input type="hidden" name="performsingle.purchase_category"
					value="${performsingle.purchase_category}">
				<input type="hidden" name="performsingle.purchase_number"
					value="${performsingle.purchase_number}">
				<input type="hidden" name="performsingle.purchase_name"
					value="${performsingle.purchase_name}">

				<table style="size: 18px; line-height: 22px;" border="1px"
					class="table" id="complexselectedlist">
					<s:if test='performsingle.purchase_category=="OA"'>
						<tr>
							<td align="center">
								物品名称
							</td>
							<td align="center">
								类型
							</td>
							<td align="center">
								规格
							</td>
							<td align="center">
								数量
							</td>
							<td align="center">
								单位
							</td>
							<td align="center">
								预算单价(元)
							</td>
							<td align="center">
								金额(RMB/元)
							</td>
							<td align="center">
								操作
							</td>
						</tr>
					</s:if>
					<s:if test='performsingle.purchase_category=="SB"'>
						<tr>
							<td align="center">
								货物名称
							</td>
							<td align="center">
								规格
							</td>
							<td align="center">
								单位
							</td>
							<td align="center">
								数量
							</td>
							<td align="center">
								单价
							</td>
							<td align="center">
								备注
							</td>
							<td align="center">
								操作
							</td>
						</tr>
					</s:if>
					<s:if test='performsingle.purchase_category=="KVP"'>
						<tr>
							<td align="center">
								项目执行编号
							</td>
							<td align="center">
								改进员工
							</td>
							<td align="center">
								责任员工
							</td>
							<td align="center">
								成本结余
							</td>
							<td align="center">
								操作
							</td>
						</tr>
					</s:if>
					<s:if test='performsingle.purchase_category=="紧急采购"'>
						<tr>
							<td align="center">
								名称
							</td>
							<td align="center">
								单位
							</td>
							<td align="center">
								数量
							</td>
							<td align="center">
								单价
							</td>
							<td align="center">
								金额
							</td>
						</tr>
					</s:if>
					<s:if test='performsingle.purchase_category=="零部件及工序外委采购"'>
						<tr>
							<td align="center">
								件号
							</td>
							<td align="center">
								名称
							</td>
							<td align="center">
								每件单价(元)
								<%--												/--%>
								<%--												<select name="performsingle.istax">--%>
								<%--												<option value="含税">含税</option>--%>
								<%--												<option value="不含税">不含税</option>--%>
								<%--												</select>)--%>
							</td>
							<td align="center">
								型别
							</td>
							<td align="center">
								生产类型
							</td>
							<td align="center">
								产品类型
							</td>
							<td align="center">
								操作
							</td>
						</tr>
					</s:if>
					<s:if
						test='performsingle.purchase_category=="原材料采购"||performsingle.purchase_category=="包装物"'>
						<tr>
							<td align="center">
								牌号
							</td>
							<td align="center">
								规格
							</td>
							<td align="center">
								单位
							</td>
							<td align="center">
								含税单价(元/KG)
							</td>
							<td align="center">
								操作
							</td>
						</tr>
					</s:if>
					<s:if test='performsingle.purchase_category=="设备"'>
						<tr>
							<td align="center">
								设备编号
							</td>
							<td align="center">
								设备名称
							</td>
							<td align="center">
								设备类型
							</td>
							<td align="center">
								部门
							</td>
							<td align="center">
								购买金额
							</td>
							<td align="center">
								操作
							</td>
						</tr>
					</s:if>
					<s:if test='performsingle.purchase_category=="设备"'>
						<s:iterator value="list" id="hh" status="pageStatus">
							<tr>
								<td align="center">
									<input
										name="performsingleDetails[${pageStatus.index}].machine_no"
										value="${hh.machine_no}">
								</td>
								<td align="center">
									<input
										name="performsingleDetails[${pageStatus.index}].machine_name"
										value="${hh.machine_name}">
								</td>
								<td align="center">
									<input
										name="performsingleDetails[${pageStatus.index}].machine_type"
										value="${hh.machine_type}">
								</td>
								<td align="center">
									<input
										name="performsingleDetails[${pageStatus.index}].machine_classGroup"
										value="${hh.machine_classGroup}">
								</td>
								<td align="center">
									<input
										name="performsingleDetails[${pageStatus.index}].machine_buyamount"
										value="${hh.machine_buyamount}">
								</td>
								<td align="center">
									<s:if test="#hh.machine_no!=null">
										<a
											href="ProdEquipmentAction!findAll.action?machine.no=${hh.machine_no}">查看设备</a>
									</s:if>
								</td>
							</tr>
						</s:iterator>
					</s:if>
					<s:if
						test='performsingle.purchase_category=="原材料采购"||performsingle.purchase_category=="包装物"'>
						<s:iterator value="list" id="hh" status="pageStatus">
							<tr>
								<td align="center">
									<input
										name="performsingleDetails[${pageStatus.index}].materials_name"
										value="${hh.materials_name}">
								</td>
								<td align="center">
									<input
										name="performsingleDetails[${pageStatus.index}].materials_format"
										value="${hh.materials_format}">
								</td>
								<td align="center">
									<input
										name="performsingleDetails[${pageStatus.index}].materials_unit"
										value="${hh.materials_unit}">
								</td>
								<td align="center">
									<input
										name="performsingleDetails[${pageStatus.index}].materials_price"
										value="${hh.materials_price}">
								</td>
								<td align="center">
									<s:if test="#hh.zhaobiao_id!=null">
										<a
											href="zhaobiaoAction!chankantoubiaoXi.action?zhaobiao.id=${hh.zhaobiao_id}">招标</a>
									</s:if>
								</td>
							</tr>
						</s:iterator>
					</s:if>
					<s:if test='performsingle.purchase_category=="OA"'>
						<s:iterator value="list" id="hh" status="pageStatus">
							<tr>
								<td align="center">
									${hh.detailAppName}
									<%--															<input readonly="readonly" name="performsingleDetails[${pageStatus.index}].detailAppName" value="${hh.detailAppName}" >--%>
									<%--															<input type="hidden"  name="performsingleDetails[${pageStatus.index}].detailItemId"  value="${hh.detailItemId}" >--%>
								</td>
								<td align="center">
									${hh.detailChildClass}
									<%--															<input readonly="readonly" name="performsingleDetails[${pageStatus.index}].detailChildClass" value="${hh.detailChildClass}" >--%>
								</td>
								<td align="center">
									${hh.detailFormat}
									<%--														<input readonly="readonly" name="performsingleDetails[${pageStatus.index}].detailFormat" value="${hh.detailFormat}" >--%>
								</td>
								<td align="center">
									${hh.detailCount}
									<%--														<input readonly="readonly" name="performsingleDetails[${pageStatus.index}].detailCount" value="${hh.detailCount}" >--%>
								</td>
								<td align="center">
									${hh.detailUnit}
									<%--														<input readonly="readonly" name="performsingleDetails[${pageStatus.index}].detailUnit" value="${hh.detailUnit}" >--%>
								</td>
								<td align="center">
									${hh.detailBudgetMoney}
									<%--														<input readonly="readonly" name="performsingleDetails[${pageStatus.index}].detailBudgetMoney" value="${hh.detailBudgetMoney}" >--%>
								</td>
								<td align="center">
									${hh.detailCount*hh.detailBudgetMoney}
									<%--															<input type="text" name="performsingleDetails[${pageStatus.index}].zongMoney" value="${hh.detailCount*hh.detailBudgetMoney}" readonly="readonly">--%>
								</td>
								<td align="center">
									<s:if test="#hh.quotedPrice_id!=null">
										<a
											href="QuotedPrice_findQuotedPrice.action?id=${hh.quotedPrice_id}&pageStatus=bom">项目核算/</a>
									</s:if>
									<s:if test="#hh.bargain_id!=null">
										<a
											href="bargainAction_salBargain.action?bargain.id=${hh.bargain_id}&test=1">询比议价</a>
									</s:if>
								</td>
							</tr>
						</s:iterator>
					</s:if>
					<s:if test='performsingle.purchase_category=="KVP"'>
						<s:iterator value="list" id="hh" status="pageStatus">
							<tr>
								<td align="center">
									${hh.executeNumber}
									<%--															<input  name="performsingleDetails[${pageStatus.index}].executeNumber" value="${hh.executeNumber}" >--%>
								</td>
								<td align="center">
									${hh.improve_username}
									<%--														<input  name="performsingleDetails[${pageStatus.index}].improve_username" value="${hh.improve_username}" >--%>
								</td>
								<td align="center">
									${hh.res_username}
									<%--														<input  name="performsingleDetails[${pageStatus.index}].res_username" value="${hh.res_username}" >--%>
								</td>
								<td align="center">
									${hh.costsavings}
									<%--														<input  name="performsingleDetails[${pageStatus.index}].costsavings" value="${hh.costsavings}" >--%>
								</td>
								<td align="center">
									<s:if test="#hh.kvp_id!=null">
										<a
											href="kvpAssessAction_findExecuteKVPById.action?id=${hh.kvp_id}&tag=1">KVP/</a>
									</s:if>
								</td>
							</tr>
						</s:iterator>
					</s:if>
					<s:if test='performsingle.purchase_category=="SB"'>
						<s:iterator value="list" id="hh" status="pageStatus">
							<tr>
								<td align="center">
									${hh.macrepair_name}
									<%--															<input  name="performsingleDetails[${pageStatus.index}].barcode" value="${hh.barcode}" >--%>
								</td>
								<td align="center">
									${hh.macrepair_format}
									<%--														<input  name="performsingleDetails[${pageStatus.index}].name" value="${hh.name}" >--%>
								</td>
								<td align="center">
									${hh.macrepair_unit}
									<%--														<input  name="performsingleDetails[${pageStatus.index}].type" value="${hh.type}" >--%>
								</td>
								<td align="center">
									${hh.macrepair_amount}
									<%--														<input  name="performsingleDetails[${pageStatus.index}].macrepair_classGroup" value="${hh.macrepair_classGroup}" >--%>
								</td>
								<td align="center">
									${hh.macrepair_money}
									<%--														<input  name="performsingleDetails[${pageStatus.index}].macrepair_alermMan" value="${hh.macrepair_alermMan}" >--%>
								</td>
								<td align="center">
									${hh.macrepair_remark}
								</td>
								<td align="center">
									<s:if test="#hh.quotedPrice_id!=null">
										<a
											href="QuotedPrice_findQuotedPrice.action?id=${hh.quotedPrice_id}&pageStatus=bom">项目核算/</a>
									</s:if>
									<s:if test="#hh.bargain_id!=null">
										<a
											href="bargainAction_salBargain.action?bargain.id=${hh.bargain_id}&test=1">询比议价</a>
									</s:if>
								</td>
							</tr>
						</s:iterator>
					</s:if>
					<s:if test='performsingle.purchase_category=="紧急采购"'>
						<s:iterator value="list" id="hh" status="pageStatus">
							<tr>
								<td align="center">
									<input name="performsingleDetails[${pageStatus.index}].qtName"
										value="${hh.qtName}">
								</td>
								<td align="center">
									<input name="performsingleDetails[${pageStatus.index}].qtUnit"
										value="${hh.qtUnit}">
								</td>
								<td align="center">
									<input name="performsingleDetails[${pageStatus.index}].qtNum"
										value="${hh.qtNum}">
								</td>
								<td align="center">
									<input name="performsingleDetails[${pageStatus.index}].qtMoney"
										value="${hh.qtMoney}">
								</td>
								<td align="center" colspan="3">
									<input
										name="performsingleDetails[${pageStatus.index}].zongMoney"
										value="${hh.zongMoney}">
								</td>
							</tr>
						</s:iterator>
						<tr id="uploadtr">
							<td align="left">
								<input type="button" id="inforButton_1"
									onclick="saveHKInfor(this,1)" value="添加物品" />
							</td>
							<td align="left" colspan="5">
								<input id="deleteItem" style="display: none;" type="button"
									id="inforButton_2" onclick="delInfor()" value="删除物品" />
							</td>
						</tr>
					</s:if>
					<s:if test='performsingle.purchase_category=="零部件及工序外委采购"'>
						<s:iterator value="list" id="hh" status="pageStatus">
							<tr>
								<td align="center">
									${hh.gx_number}
									<%--														<select id="gx_number1" name="performsingleDetails[${pageStatus.index}].gx_number" onchange="getType(1)" onmouseover="getNumber(1)">--%>
									<%--															<option value="${hh.gx_number}">${hh.gx_number}</option>--%>
									<%--															</select>--%>
									<%--															<input type="hidden" id="gx_quotedNumber1" name="performsingleDetails[${pageStatus.index}].gx_quotedNumber" value="${hh.gx_quotedNumber}">--%>
									<%--															<input type="hidden" id="gx_projectnum1" name="performsingleDetails[${pageStatus.index}].gx_projectnum" value="${hh.gx_projectnum}">--%>
								</td>
								<td align="center">
									${hh.gx_name}
									<%--															<select id="gx_name1" name="performsingleDetails[${pageStatus.index}].gx_name">--%>
									<%--															<option value="${hh.gx_name}">${hh.gx_name}</option>--%>
									<%--															</select>--%>
								</td>
								<td align="center">
									${hh.gx_price}
									<%--														<input  name="performsingleDetails[${pageStatus.index}].gx_price" value="${hh.gx_price}" >--%>
								</td>
								<td align="center">
									${hh.gx_type}
									<%--														<select id="gx_type1" name="performsingleDetails[${pageStatus.index}].gx_type" >--%>
									<%--															<option value="${hh.gx_type}">${hh.gx_type}</option>--%>
									<%--															</select>--%>
								</td>
								<td align="center">
									${hh.gx_producetype}
									<%--															<select id="gx_producetype1" name="performsingleDetails[${pageStatus.index}].gx_producetype" >--%>
									<%--															<option value="${hh.gx_producetype}">${hh.gx_producetype}</option>--%>
									<%--															</select>--%>
									<%--															<input type="hidden"   id="gx_status1" name="performsingleDetails[${pageStatus.index}].gx_status" value="${hh.gx_status}" >--%>
								</td>
								<td align="center">
									${hh.gx_goodstype}
									<%--															<input  name="performsingleDetails[${pageStatus.index}].gx_goodstype" value="${hh.gx_goodstype}" >--%>
								</td>
								<td align="center">
									<a
										href="osaAction!findOSAppList.action?tag=manager&osa.markID=${hh.gx_number}">外购外委</a>
								</td>
							</tr>
						</s:iterator>
					</s:if>
				</table>
				<%--											<input type="submit" value="保存" class="input" />--%>

			</form>
			<%@include file="/util/foot.jsp"%>
		</center>
		<SCRIPT type="text/javascript">
		//件号
		 function getNumber(obj){
  			  $.ajax( {
				url : "bargainAction_findbargainNumber.action",
				type : 'post',
				dataType : 'json',
				cache : false,//防止数据缓存
				data : {
				},
				success : function(data) {
					$("#gx_number"+obj+"").empty();//清空
					$.each(data,function(i,n){
						if(i==0){
					   $("#gx_number"+obj+"").append("<option value=''>--请选择外委评审单号--</option>");
						}
							$("#gx_number"+obj+"").append("<option value='"+n+"'>"+n+"</option>");
					})
				},
				error : function() {
					alert("服务器异常!");
				}
			});
  	 }
	
	
	//根据件号下拉名称
	function getName(obj){
		 var gx_number = $("#gx_number"+obj+"").val();
		 $.ajax( {
				url : "bargainAction_findbargainNumber5.action",
				type : 'post',
				dataType : 'json',
				cache : false,//防止数据缓存
				data : {
				 gx_number:gx_number
				},
				success : function(data) {
					$("#gx_name"+obj+"").empty();//清空
					$("#gx_name"+obj+"").append("<option value='"+data.partName+"'>"+data.partName+"</option>");
				},
				error : function() {
					alert("服务器异常!");
				}
			});
	}
	//查询询价单号gx_quotedNumber1
	function getquotedNumber(obj){
			  var gx_number = $("#gx_number"+obj+"").val();
		  $.ajax( {
				url : "bargainAction_findbargainNumber3.action",
				type : 'post',
				dataType : 'json',
				cache : false,//防止数据缓存
				data : {
			  	gx_number:gx_number
				},
				success : function(data) {
					$("#gx_quotedNumber"+obj+"").empty();//清空
					$("#gx_quotedNumber"+obj+"").attr("value",data.quotedNumber);
						//根据核价编号查询项目编号
					getprojectnum(obj);
				},
				error : function() {
					alert("服务器异常!");
				}
			});
	}

	//根据核价编号查询项目编号
	function getprojectnum(obj){
		  var quotedNumber = $("#gx_quotedNumber"+obj+"").val();
		  $.ajax( {
				url : "bargainAction_findbargainNumber4.action",
				type : 'post',
				dataType : 'json',
				cache : false,//防止数据缓存
				data : {
			  	quotedNumber:quotedNumber
				},
				success : function(data) {
					$("#gx_projectnum"+obj+"").empty();//清空
					$("#gx_projectnum"+obj+"").attr("value",data.projectNum);
				},
				error : function() {
					alert("服务器异常!");
				}
			});
		
	}
	
	
	 //型别
		  function getType(obj){
			 getquotedNumber(obj);
			 getName(obj);
			  var gx_number = $("#gx_number"+obj+"").val();
  			  $.ajax( {
				url : "bargainAction_findbargainNumber1.action",
				type : 'post',
				dataType : 'json',
				cache : false,//防止数据缓存
				data : {
  				  gx_number:gx_number
				},
				success : function(data) {
					$("#gx_type"+obj+"").empty();//清空
					$.each(data,function(i,n){
							$("#gx_type"+obj+"").append("<option value='"+n+"'>"+n+"</option>");
					})
					getProducetype(obj);
				},
				error : function() {
					alert("服务器异常!");
				}
			});
  	 }
		 //生产类型
		  function getProducetype(obj){
			 var gx_number = $("#gx_number"+obj+"").val();
  			  $.ajax( {
				url : "bargainAction_findbargainNumber2.action",
				type : 'post',
				dataType : 'json',
				cache : false,//防止数据缓存
				data : {
  				  gx_number:gx_number
				},
				success : function(data) {
					$("#gx_producetype"+obj+"").empty();//清空
					$.each(data,function(i,n){
							$("#gx_producetype"+obj+"").append("<option value='"+n+"'>"+n+"</option>");
							if(n=="外购"){
								$("#gx_status"+obj+"").attr("value",n);
								$("#gx_producetype"+obj+"").attr("value",null);
							}else{
								$("#gx_status"+obj+"").attr("value","外委");
							}
							
					})
					
				},
				error : function() {
					alert("服务器异常!");
				}
			});
  	 }
		
		
		
		 	//添加物品
	var begAddLineNum = "<s:property value='list.size()'/>";//物品
	var lineCount = "<s:property value='list.size()'/>";
function saveHKInfor(obj, few) {
	var _tbody = document.getElementById("complexselectedlist").tBodies[0];//获得第一个tbody
	var uploadtr = document.getElementById("uploadtr");//将要在该Tr之前添加元素
	var _tr = document.createElement("tr");
	var _td = document.createElement("td");
	_tr.setAttribute('align', 'center');
	_tbody.insertBefore(_tr, uploadtr);
	begAddLineNum++;
	var status = "${performsingle.purchase_category}";//获得来源单号
	if(status=="紧急采购"){
		var x = _tr.insertCell(0);
		x.innerHTML = "<input  name=\"performsingleDetails["+lineCount+"].qtName\" value='' >";
	var x1 = _tr.insertCell(1);
	x1.innerHTML = "<input  name=\"performsingleDetails["+lineCount+"].qtUnit\" value=''>";
	var x2 = _tr.insertCell(2);
	x2.innerHTML = "<input  name=\"performsingleDetails["+lineCount+"].qtNum\" value='' >";
	var x3 = _tr.insertCell(3);
	x3.innerHTML = "<input  name=\"performsingleDetails["+lineCount+"].qtMoney\" value='' >";
	var x4 = _tr.insertCell(4);
	x4.innerHTML = "<input  name=\"performsingleDetails["+lineCount+"].zongMoney\" value='' >";
	}
	if(status=="零部件及工序外委采购"){
		var lineCount1 = lineCount*1+1;
	var x = _tr.insertCell(0);
	x.innerHTML = "<select id=\"gx_number"+lineCount1+"\" onchange=\"getType("+lineCount1+")\" " +
		"onmouseover=\"getNumber("+lineCount1+")\" name=\"performsingleDetails["+lineCount+"].gx_number\" >" +
		"<option value=''>--请选择零件号--</option></select>" +
		"<input type=\"hidden\" id=\"gx_quotedNumber"+lineCount1+"\" name=\"performsingleDetails["+lineCount+"].gx_quotedNumber\" value=''>" +
		"<input type=\"hidden\" id=\"gx_projectnum"+lineCount1+"\" name=\"performsingleDetails["+lineCount+"].gx_projectnum\" value=''>"
	var x1= _tr.insertCell(1);
	x1.innerHTML = "<select id=\"gx_name"+lineCount1+"\" name=\"performsingleDetails["+lineCount+"].gx_name\"><option value=''>--请选择名称--</option></select>";
	
	var x2 = _tr.insertCell(2);
	x2.innerHTML = "<input  name=\"performsingleDetails["+lineCount+"].gx_price\" value='' >";
	var x3 = _tr.insertCell(3);
	x3.innerHTML = "<select id=\"gx_type"+lineCount1+"\"  name=\"performsingleDetails["+lineCount+"].gx_type\" ><option value=''>--请选择型别--</option></select>"
	
	var x4 = _tr.insertCell(4);
	 x4.innerHTML = "<select id=\"gx_producetype"+lineCount1+"\"  name=\"performsingleDetails["+lineCount+"].gx_producetype\" ><option value=''>--请选择生产类型--</option></select><input type=\"hidden\"   id=\"gx_status"+lineCount1+"\" name=\"barContractDetailsList["+lineCount+"].gx_status\" value='' >"

	 var x5 = _tr.insertCell(5);
	x5.innerHTML = "<input  name=\"performsingleDetails["+lineCount+"].gx_goodstype\" value='' >";
	}
	if(status=="原材料采购"){
	var x = _tr.insertCell(0);
	x.innerHTML = "<input  name=\"performsingleDetails["+lineCount+"].materials_name\" value='' >";
	var x1 = _tr.insertCell(1);
	x1.innerHTML = "<input  name=\"performsingleDetails["+lineCount+"].materials_format\" value=''>";
	var x2 = _tr.insertCell(2);
	x2.innerHTML = "<input  name=\"performsingleDetails["+lineCount+"].materials_unit\" value='' >";
	var x3 = _tr.insertCell(3);
	//x3.setAttribute('colspan', '2');
	x3.innerHTML = "<input  name=\"performsingleDetails["+lineCount+"].materials_price\" value='' >";
	}
	if(status=="工装采购"){
	var x = _tr.insertCell(0);
	x.innerHTML = "<input type='text' name=\"performsingleDetails["+lineCount+"].frock_applynum\" value=''>";
	var x1 = _tr.insertCell(1);
	x1.innerHTML = "<input type='text' name=\"performsingleDetails["+lineCount+"].frock_biddingnum\" value=''>";
	var x2 = _tr.insertCell(2);
	x2.innerHTML = "<input type='text' name=\"performsingleDetails["+lineCount+"].frock_name\" value=''>";
	var x3 = _tr.insertCell(3);
	x3.innerHTML = "<input type='text' name=\"performsingleDetails["+lineCount+"].frock_partnum\" value=''>";
	var x4 = _tr.insertCell(4);
	x4.innerHTML = "<input type='text' name=\"performsingleDetails["+lineCount+"].frock_num\" value=''>";
	var x5 = _tr.insertCell(5);
	x5.innerHTML = "<input  type='text' name=\"performsingleDetails["+lineCount+"].frock_amount\" value=''>";
	var x6 = _tr.insertCell(6);
	x6.innerHTML = "<input type='text' name=\"performsingleDetails["+lineCount+"].frock_money\" value=''>";
	}
		lineCount++;
	document.getElementById("deleteItem").style.display = "block";
}

//删除物品
function delInfor() {
	complexselectedlist.deleteRow(begAddLineNum);
	begAddLineNum--;
	lineCount--;
	if (begAddLineNum < 2) {
		document.getElementById("deleteItem").style.display = "none";
	}
}
		</SCRIPT>
	</body>
</html>
