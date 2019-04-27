<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
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
		<script src="http://pv.sohu.com/cityjson?ie=utf-8">
</script>
		<script type="text/javascript">
if ('${strList1}'.indexOf('外部权限') < 0) {
	if ('${companyInfo.outIp}'.indexOf(returnCitySN["cip"])<0) {
		location.href = "<%=basePath%>/System/caiwu/findAllPrice_noWai.jsp";
	}
}
</script>
		<style type="text/css">
table {
	font-size: 14px;
	padding: 0px;
	margin: 0px;
	border-collapse: collapse;
	/* 关键属性：合并表格内外边框(其实表格边框有2px，外面1px，里面还有1px哦) */
	border: solid #999; /* 设置边框属性；样式(solid=实线)、颜色(#999=灰) */
	border-width: 1px 0 0 1px;
}

table th,table td {
	border: solid #999;
	border-width: 1 1px 1px 1;
	padding: 2px;
}

.ztree li a {
	color: #fff;
}
</style>

	</head>
	<body>
		<%@include file="/util/sonTop.jsp"%>
		<div id="gongneng"
			style="width: 100%; border: solid 1px #0170b8; margin-top: 10px;">
			<div id="xitong"
				style="width: 100%; font-weight: bold; padding-left: 35px; padding-top: 5px; padding-bottom: 5px;"
				align="left">

			</div>

			<div align="center">
				<div id="test" style="position: absolute; left: 475px; top: 0px;">
					<canvas id="myCanvas" width="130" height="50"></canvas>
				</div>
				<form
					action="PriceAction!findPriceByCondition.action?statue=find&tags=${tags}"
					method="post" id="myform">
					<table width="99%">
						<tr>
							<td align="right">
								件&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;号:
								<br />
								(Item Number)
							</td>
							<td>
								<input type="text" style="width: 150px;" name="price.partNumber"
									value="${price.partNumber}">
							</td>
							<td align="right">
								名&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;称:
								<br />
								(Name)
							</td>
							<td>
								<input type="text" style="width: 150px;" name="price.name">
							</td>
							<td align="right">
								产品类别:
								<br />
								(Product Type)
							</td>
							<td>
								<select style="width: 150px;" name="price.productCategory">
									<option></option>
									<s:iterator value="strList1" id="str">
										<s:if test="#str == '总成价格'">
											<option>
												总成
											</option>
										</s:if>
										<s:elseif test="#str == '外购价格'">
											<option>
												外购
											</option>
										</s:elseif>
										<s:elseif test="#str == '外委价格'">
											<option>
												外委
											</option>
										</s:elseif>
										<s:elseif test="#str == '辅料价格'">
											<option>
												辅料
											</option>
										</s:elseif>
										<s:elseif test="#str == '其他价格'">
											<option>
												其他
											</option>
										</s:elseif>
									</s:iterator>
								</select>
							</td>
						</tr>
						<tr>
							<td align="right">
								价格(含税):
								<br>
								(Price (including tax))
							</td>
							<td>
								<input type="text" style="width: 150px;" name="price.hsPrice">
							</td>
							<td align="right">
								价格(不含税):
								<br>
								(Price (excluding tax))
							</td>
							<td>
								<input type="text" style="width: 150px;" name="price.bhsPrice">
							</td>
							<td align="right">
								生产类型:
								<br>
								(Production Type)
							</td>
							<td>
								<select style="width: 150px;" name="price.produceType">
									<option></option>
									<s:iterator value="strList" id="str">
										<s:if test="#str == '外购'">
											<option>
												外购
											</option>
										</s:if>
										<s:if test="#str == '外委'">
											<option>
												外委
											</option>
										</s:if>
									</s:iterator>
									<option>
										外购
									</option>
									<option>
										外委
									</option>
								</select>
							</td>
						</tr>
						<tr>
							<td align="right">
								合同编号
								<br>
								(Contract Number)
							</td>
							<td>
								<input type="text" style="width: 150px;"
									name="price.contractNumber" value="${price.contractNumber}">
							</td>
							<td align="right">
								签订方
								<br>
								(The Signing Party)
							</td>
							<td>
								<input type="text" style="width: 150px;" name="price.qidingfang"
									value="${price.qidingfang}">
							</td>
							<td align="right">
								档案号
								<br>
								(file Number)
							</td>
							<td>
								<input type="text" style="width: 150px;" name="price.fileNumber"
									value="${price.fileNumber}">
							</td>
						</tr>
						<tr>
							<td align="right">
								物料类别
							</td>
							<td>
								<div class="zTreeDemoBackground left">
									<ul class="list">
										<li class="title">
											<input id="wgType" type="text" readonly="readonly" value=""
												style="width: 120px;" name="price.wlType" />
											<a id="menuBtn" href="#" onclick="showMenu(); return false;">选择</a>
										</li>
									</ul>
								</div>
								<div id="menuContent" class="menuContent"
									style="display: none; position: absolute;">
									<ul id="treeDemo" class="ztree"
										style="margin-top: 0; width: 160px;"></ul>
								</div>
							</td>
							<td align="right">
								供货属性
							</td>
							<td>
								<select name="price.kgliao" id="kgliao2">
									<option></option>
									<option value="TK">
										自购(TK)
									</option>
									<option value="TK AVL">
										指定供应商(TK AVL)
									</option>
									<option value="CS">
										客供(CS)
									</option>
									<option value="TK Price">
										完全指定(TK Price)
									</option>
								</select>
							</td>
							<td colspan="4" rowspan="4" align="center">
								<input type="submit" value="查询select"
									style="width: 100px; height: 40px">
								<input type="reset" value="重置 submit"
									style="width: 100px; height: 40px"><br/>
								<input type="button" value="查询所有(包含失效期)"
									style="width: 150px; height: 40px" onclick="selectall()" /><br/>
								<input type="button" value="导出"
									style="width: 75px; height: 40px"
									onclick="exportExcel(this.form,'find');todisabledone(this)" data="downData" />
									<input type="button" value="导出(包含失效期)"
									style="width: 150px; height: 40px"
									onclick="exportExcel(this.form,'all');todisabledone(this)" data="downData" />
							</td>
						</tr>
						<tr>
							<td align="right">
								规格：
								<br>
								(Specification)
							</td>
							<td >
								<div>
									<input type="text" style="width: 150px;" name="price.specification"
									value="${price.specification}">
								</div>
							</td>
							<td align="right">
								型别
								<br>
								(type)
							</td>
							<td>
								<input type="text" style="width: 150px;" name="price.type"
									value="${price.type}">
							</td>
<!--							<td align="right">-->
<!--								型号：-->
<!--								<br>-->
<!--								(No. of model.)-->
<!--							</td>-->
<!--							<td>-->
<!--								<div>-->
<!--									<input type="text" style="width: 150px;" name="price.modelNo"-->
<!--											value="${price.modelNo}">-->
<!--								</div>-->
<!--							</td>-->
						</tr>
						<tr>
							<td align="center">
								供应商
							</td>
							<td>
								<input type="text" value="" name="gys" />
								(输入供应商编号或名称查询)
							</td>
							<td align="right">
								图号：
								<br>
								(No. of PIC.)
							</td>
							<td>
								<div>
									<input type="text" style="width: 150px;" name="price.picNo"
											value="${price.picNo}">
								</div>
							</td>
						</tr>
						<tr>
							<td align="right">
								添加时间从
								<br>
								(Price valid start time)
							</td>
							<td>
								<div>
									<input class="Wdate" type="text" name="price.pricePeriodStart"
										onClick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'whyGreen'})" />
								</div>
							</td>
							<td align="right">
								止
								<br>
								(Price valid end time)
							</td>
							<td>
								<div>
									<input class="Wdate" type="text" name="price.pricePeriodEnd"
										onClick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'whyGreen'})" />
								</div>
							</td>
						</tr>
						
					</table>
				</form>
				<br>
				<center>
					<table width="99%">
						<tr align="center" bgcolor="#c0dcf2" height="50px">
							<td>
								序号
							</td>
							<td>
								产品类别
							</td>
							<td>
								生产类型
							</td>
							<td width="80px">
								供料属性
							</td>
							<td>
								件号
							</td>
							<td>
								版本
							</td>
							<td>
								单位
							</td>
							<td>
								规格
							</td>
							<td>
								图号
							</td>
							<td>
								物料类别
							</td>
							<td width="80px">
								名称
							</td>
							<td>
								工序号
							</td>
							<td width="60px">
								工序名称
							</td>
							<td>
								外委类型
							</td>
							<td width="60px">
								签订方
							</td>
							<td width="40px">
								型别
							</td>
							<td>
								价格(含税)
							</td>
							<td>
								价 格 (不含税)
							</td>
							<td>
								税率
							</td>
							<td>
								合同编号
							</td>
							<td>
								档案编号
							</td>
							<td>
								档案柜号
							</td>
							<td>
								价格有效期
							</td>
							<td>
								采购比例
							</td>
							<td>
								操作
							</td>
						</tr>
						<tr align="center" bgcolor="red" height="50px">
							<td colspan="25">
								未归档
							</td>
						</tr>
						<s:iterator id="unpricetest" value="unpriceList"
							status="statussdf">
							<s:if test="#statussdf.index%2==1">
								<tr align="center" bgcolor="#e6f3fb"
									onmouseover="chageBgcolor(this)"
									onmouseout="outBgcolor(this,'#e6f3fb')">
							</s:if>
							<s:else>
								<tr align="center" onmouseover="chageBgcolor(this)"
									onmouseout="outBgcolor(this,'')">
							</s:else>
							<td>
								<s:property value="#statussdf.index+1" />
							</td>
							<td>
								${unpricetest.productCategory}
							</td>
							<td>
								${unpricetest.produceType}
							</td>
							<td>
								${unpricetest.kgliao}
							</td>
							<td>
								${unpricetest.partNumber}
							</td>
							<td>
								${unpricetest.banbenhao}
							</td>
							<td>
								${unpricetest.danwei}
							</td>
							<td>
								${unpricetest.specification}
							</td>
							<td>
								${unpricetest.picNo}
							</td>
							<td>
								${unpricetest.wlType}
							</td>
							<td align="left" width="60px" style="font-size: 5px;">
								${unpricetest.name}
							</td>
							<td>
								${unpricetest.gongxunum}
							</td>
							<td width="60px">
								${unpricetest.processNames}
							</td>
							<td>
								${unpricetest.wwType}
							</td>
							<td align="left" width="60px">
								${unpricetest.qidingfang}
							</td>
							<td align="left" width="40px">
								${unpricetest.type}
							</td>
							<td>
								<s:iterator value="strList1" id="str">
									<s:if test="#str == (#unpricetest.productCategory+'价格')">
										<fmt:formatNumber value="${unpricetest.hsPrice}"
											pattern="#.####"></fmt:formatNumber>

									</s:if>
									<s:elseif test="#str == (#unpricetest.produceType+'价格')">
										<fmt:formatNumber value="${unpricetest.hsPrice}"
											pattern="#.####"></fmt:formatNumber>
									</s:elseif>
								</s:iterator>
								<%--								${pricetest.hsPrice}--%>
							</td>
							<td>
								<s:iterator value="strList1" id="str">
									<s:if test="#str == (#unpricetest.productCategory+'价格')">
										<fmt:formatNumber value="${unpricetest.bhsPrice}"
											pattern="#.####"></fmt:formatNumber>
									</s:if>
									<s:elseif test="#str == (#unpricetest.produceType+'价格')">
										<fmt:formatNumber value="${unpricetest.bhsPrice}"
											pattern="#.####"></fmt:formatNumber>
									</s:elseif>
								</s:iterator>
							</td>
							<td>
								${unpricetest.taxprice}
							</td>
							<td>
								${unpricetest.contractNumber}
							</td>
							<td>
								${unpricetest.fileNumber}
							</td>
							<td>
								${unpricetest.danganWeizhi}
							</td>
							<td>
								${unpricetest.pricePeriodEnd}
							</td>
							<td>
								${unpricetest.cgbl}
							</td>
							<td>
								<a
									href="PriceAction!findPriceById.action?id=${unpricetest.id}&statue=${statue}">明细</a>
								<a
									href="PriceAction!deletePrice.action?id=${unpricetest.id}&statue=${statue}">删除</a>
								<br />
								<s:if
									test="#pricetest.attachmentName!=null&&#pricetest.attachmentName!=''">
									<a onclick="return window.confirm('此操作需领导审批同意，确认申请?')"
										title="领导审批同意后，在《合同附件申请查看(个人)》功能下载合同"
										href="PriceAction!shenqing.action?id=${unpricetest.id}&statue=${statue}">申请查看附件</a>
									<s:if test="tags=='admin'">
										<input type="button" value="查看合同"
											onclick="xiazai('${unpricetest.attachmentName}')">
									</s:if>
								</s:if>
							</td>

						</s:iterator>
						<tr align="center" bgcolor="green" height="50px">
							<td colspan="25">
								所有
							</td>
						</tr>
						<s:iterator id="pricetest" value="priceList" status="statussdf">
							<s:if test="#statussdf.index%2==1">
								<tr align="center" bgcolor="#e6f3fb"
									onmouseover="chageBgcolor(this)"
									onmouseout="outBgcolor(this,'#e6f3fb')">
							</s:if>
							<s:else>
								<tr align="center" onmouseover="chageBgcolor(this)"
									onmouseout="outBgcolor(this,'')">
							</s:else>
							<td>
								<s:property value="#statussdf.index+1" />
							</td>
							<td>
								${pricetest.productCategory}
							</td>
							<td>
								${pricetest.produceType}
							</td>
							<td>
								${pricetest.kgliao}
							</td>
							<td align="left">
								${pricetest.partNumber}
							</td>
							<td>
								${pricetest.banbenhao}
							</td>
							<td>
								${pricetest.danwei}
							</td>
							<td>
								${pricetest.specification}
							</td>
							<td>
								${pricetest.picNo}
							</td>
							<td>
								${pricetest.wlType}
							</td>
							<td align="left" width="150px"  style="font-size: 5px;">
								${pricetest.name}
							</td>
							<td>
								${pricetest.gongxunum}
							</td>
							<td width="60px">
								${pricetest.processNames}
							</td>
							<td>
								${pricetest.wwType} ${pricetestt.gys}
							</td>
							<td align="left" width="60px">
								${pricetest.qidingfang}
							</td>
							<td align="left" width="40px">
								${pricetest.type}
							</td>
							<td>
								<s:iterator value="strList1" id="str">
									<s:if test="#str == (#pricetest.productCategory+'价格')">
										<fmt:formatNumber value="${pricetest.hsPrice}"
											pattern="#.####"></fmt:formatNumber>
									</s:if>
									<s:elseif test="#str == (#pricetest.produceType+'价格')">
										<fmt:formatNumber value="${pricetest.hsPrice}"
											pattern="#.####"></fmt:formatNumber>
									</s:elseif>
								</s:iterator>
								<%--								${pricetest.hsPrice}--%>
							</td>
							<td>
								<s:iterator value="strList1" id="str">
									<s:if test="#str == (#pricetest.productCategory+'价格')">
										<fmt:formatNumber value="${pricetest.bhsPrice}"
											pattern="#.####"></fmt:formatNumber>
									</s:if>
									<s:elseif test="#str == (#pricetest.produceType+'价格')">
										<fmt:formatNumber value="${pricetest.bhsPrice}"
											pattern="#.####"></fmt:formatNumber>
									</s:elseif>
								</s:iterator>
							</td>
							<td>
								${pricetest.taxprice}
							</td>
							<td>
								${pricetest.contractNumber}
							</td>
							<td>
								${pricetest.fileNumber}
							</td>
							<td>
								${pricetest.danganWeizhi}
							</td>
							<td>
								${pricetest.pricePeriodEnd}
							</td>
							<td>
								${pricetest.cgbl}
							</td>
							<td>
								<a
									href="PriceAction!findPriceById.action?id=${pricetest.id}&statue=${statue}"
									target="_blank">明细</a>/
								<a onclick="return window.confirm('确定要删除本条价格数据?')"
									href="PriceAction!deletePrice.action?id=${pricetest.id}&statue=${statue}&cpage=${cpage}">删除</a>/
								<br />
								<s:if
									test="#pricetest.attachmentName!=null&&#pricetest.attachmentName!=''">
									<s:if test="tags=='admin'">
										<input type="button" value="查看合同"
											onclick="xiazai('${pricetest.attachmentName}')">
									</s:if>
									<s:else>
										<a
											onclick="return window.confirm('此操作需领导审批同意后，在《合同附件申请查看(个人)》功能下载合同. 确认申请?')"
											title="领导审批同意后，在《合同附件申请查看(个人)》功能下载合同"
											href="PriceAction!shenqing.action?id=${pricetest.id}&statue=${statue}">查看附件</a>
									</s:else>
								</s:if>
							</td>

						</s:iterator>
						<tr>
							<td colspan="25" align="right">
								第
								<font color="red"><s:property value="cpage" /> </font> /
								<s:property value="total" />
								页
								<fenye:pages cpage="%{cpage}" total="%{total}" url="%{url}"
									styleClass="page" theme="number" />

							</td>
						</tr>
					</table>
				</center>
			</div>
			<br>
		</div>
		<%@include file="/util/foot.jsp"%>
		<SCRIPT type="text/javascript">
var parentdocument =  window.parent.document;
var offset=0;
$(function(){
	var oDiv=document.getElementById("test"); 
	var  obj =window.parent.document.getElementById("showAll");
	if(obj!=null){
		offset = obj.offsetTop; 
	}
})
parentdocument.onscroll = function(){
	var oDiv=document.getElementById("test"); 
	var parenttop = parentdocument.body.scrollTop;
		parenttop = parenttop-offset;
		if(parenttop<0){
			parenttop = 10;
		}
	oDiv.style.top=parenttop;
}
window.onscroll=function(){ 
	var oDiv=document.getElementById("test"); 
	oDiv.style.top=document.body.scrollTop + 10;  //控制上下位置
} 
$(function(){
	var c=document.getElementById("myCanvas");
	var ctx=c.getContext("2d");
 	ctx.strokeStyle = 'red';
	ctx.font="5px Arial";
	ctx.strokeText("${Users.code} ${Users.name}",10,10);
})


function selectall(){
	var myform = document.getElementById("myform");
	myform.action = "PriceAction!findPriceByCondition.action?statue=ALL&tags=${tags}";
	myform.submit();
	myform.action = "PriceAction!findPriceByCondition.action?statue=find&tags=${tags}";
}
$(function(){
	if (/Android|webOS|iPhone|iPod|BlackBerry/i.test(navigator.userAgent)) {
		$.ajax( {
		type : "POST",
		url : "PriceAction!findPhoneqx.action",
		data : {},
		dataType : "json",
		success : function(data) {
		if(data ==null || data.length == 0){
			window.location.href="<%=basePath%>/System/caiwu/price_noShow.jsp";
		}

		}
	})
	}
})
var mfzTree;
var addzTree;
var delzTree;
var updatezTree;

var id;
var pId;
var name;
var setting = { 
view: { 
dblClickExpand: false 
}, 
data: { 
simpleData: { 
enable: true 
} 
}, 
callback: { 
beforeClick: beforeClick, 
onClick: onClick 
} 
}; 
//读取树形数据
$(document).ready(function() {
	parent.mfzTree();
});
var zNodes = [];
parent.mfzTree = function() {
	$.ajax( {
		url : 'CategoryAction_findcyListByrootId.action',
		type : 'post',
		data :{status:'物料类别'},
		dataType : 'json',
		cache : true,
		success : function(doc) {
			$(doc).each(
					function() {
						zNodes.push( {
							id : $(this).attr('id'),
							pId : $(this).attr('fatherId'),
							name : $(this).attr('name'),
							target : "main",
							click : false
						});

					});
			$.fn.zTree.init($("#treeDemo"), setting, zNodes);
			var zTree = $.fn.zTree.getZTreeObj("treeDemo");
			zTree.expandAll(true);
		},
		error : function() {
			alert("服务器异常!");
		}
	});
};
function beforeClick(treeId, treeNode) { 
var check = (treeNode && !treeNode.isParent); 

return true; 
} 

function onClick(e, treeId, treeNode) { 
var zTree = $.fn.zTree.getZTreeObj("treeDemo"), 
nodes = zTree.getSelectedNodes(), 
v = ""; 
nodes.sort(function compare(a,b){return a.id-b.id;}); 
for (var i=0, l=nodes.length; i<l; i++) { 
v = nodes[i].name ; 
} 
//if (v.length > 0 ) v = v.substring(0, v.length-1); 
 cityObj = $("#wgType").val(v); 

} 

function showMenu() { 
var cityObj = $("#wgType"); 
var cityOffset = $("#wgType").offset(); 
$("#menuContent").css({left:cityOffset.left + "px", top:cityOffset.top + cityObj.outerHeight() + "px"}).slideDown("fast"); 

$("body").bind("mousedown", onBodyDown); 
} 
function hideMenu() { 
$("#menuContent").fadeOut("fast"); 
$("body").unbind("mousedown", onBodyDown); 
} 
function onBodyDown(event) { 
if (!(event.target.id == "menuBtn" || event.target.id == "menuContent" || $(event.target).parents("#menuContent").length>0)) { 
hideMenu(); 
} 
} 

//下载合同
	function xiazai(url){
			//对中文进行加密
			//var fileName1 = encodeURI(encodeURI("${list.priceUrl}"));
			var fileName1 = url;
<%--			location.href="<%=request.getContextPath()%>/DownAction.action?fileName="+fileName1;--%>
			location.href="<%=request.getContextPath()%>/FileViewAction.action?FilePath="+"/upload/file/"+fileName1;
	}
 function exportExcel(obj,status){
		obj.action = "PriceAction!exportExcel.action?statue="+status;
	 	obj.submit();
	  	obj.action = "PriceAction!findPriceByCondition.action?statue=find";
	}

</SCRIPT>
		</center>
	</body>
</html>