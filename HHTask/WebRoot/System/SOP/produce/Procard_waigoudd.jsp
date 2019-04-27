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
	<STYLE type="text/css">
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
</STYLE>
	</head>
	<body>
		<%@include file="/util/sonTop.jsp"%>
		<div id="bodyDiv" align="center" class="transDiv"
			onclick="chageDiv('none')">
		</div>
		<div id="contentDiv"
			style="position: absolute; z-index: 255; width: 900px; display: none;"
			align="center">
			<div id="closeDiv"
				style="position: relative; top: 165px; left: 0px; right: 200px; z-index: 255; background: url(<%=basePath%>/images/bq_bg2.gif); width: 900px;">
				<table style="width: 100%">
					<tr>
						<td>
							<span id="title">您进行不合格品确认操作</span>
						</td>
						<td align="right">
							<img alt="" src="<%=basePath%>/images/closeImage.png" width="30"
								id="closeTcDiv" height="32" onclick="chageDiv('none')">
						</td>
					</tr>
				</table>
				<div id="operatingDiv"
					style="background-color: #ffffff; width: 100%;">
					<iframe id="xiugaiIframe" src="" marginwidth="0" marginheight="0"
						hspace="0" vspace="0" frameborder="0" scrolling="yes"
						style="width: 98%; height: 500px; margin: 0px; padding: 0px;"></iframe>

				</div>
			</div>
		</div>
		<div id="gongneng" style="width: 100%;">
			<div align="center">
				<s:if test='pageStatus != "bhgdl"'>
					<a
						href="WaigouwaiweiPlanAction!findDeliveryNote.action?pageStatus=${pageStatus}">送货单模式
					</a>&nbsp;&nbsp;送货单明细模式
			</s:if>
				<form
					action="WaigouwaiweiPlanAction!findAllWaigouDeliveryDetail.action"
					method="post">
					<table class="table">
						<tr>
							<th align="right">
								送货单号:
							</th>
							<td>
								<input type="text" name="waigoudd.waigouDelivery.planNumber"
									value="${waigoudd.waigouDelivery.planNumber}" />
							</td>
							<th align="right">
								件号:
							</th>
							<td>
								<input type="text" name="waigoudd.markId"
									value="${waigoudd.markId}" />
							</td>
							<th align="right">
								采购订单号:
							</th>
							<td>
								<input type="text" name="waigoudd.cgOrderNum"
									value="${waigoudd.cgOrderNum}" />
							</td>
						</tr>
						<tr>
							<th align="right">
								物料类别:
							</th>
							<td>
								<div class="zTreeDemoBackground left">
									<ul class="list">
										<li class="title">
											<input id="wgType" type="text" readonly="readonly"
												value="${waigoudd.wgType}" style="width: 120px;"
												name="waigoudd.wgType" />
											<a id="menuBtn" href="#" onclick="showMenu(); return false;">选择</a>(按住Ctrl建不松点击,可清空)
										</li>
									</ul>
								</div>
								<div id="menuContent" class="menuContent"
									style="display: none; position: absolute;">
									<ul id="treeDemo" class="ztree"
										style="margin-top: 0; width: 160px;"></ul>
								</div>
							</td>
							<th align="right">
								版本:
							</th>
							<td>
								<input type="text" name="waigoudd.banben"
									value="${waigoudd.banben}" />
							</td>
							<th align="right">
								名称:
							</th>
							<td>
								<input type="text" name="waigoudd.proName" />
							</td>
						</tr>
						<tr>
							<th align="right">
								状态:
							</th>
							<td>
								<SELECT name="waigoudd.status" id="status">
									<option value="待核对">
										待核对
									</option>
									<option value="待通知">
										待通知
									</option>
									<option value="待确认">
										待确认
									</option>
									<option value="协商确认">
										协商确认
									</option>
									<option value="生产中">
										生产中
									</option>
									<option value="送货中">
										送货中
									</option>
									<option value="待检验">
										待检验
									</option>
									<option value="检验中">
										检验中
									</option>
									<option value="待入库">
										待入库
									</option>
									<option value="入库">
										入库
									</option>
								</SELECT>
							</td>
							<th align="right">
								供货属性:
							</th>
							<td>
								<select name="waigoudd.kgliao" id="kgliao">
									<option value="TK">TK</option>
									<option value="TK AVL">TK AVL</option>
									<option value="CS">CS</option>
									<option value="TK Price">TK Price</option>
								</select>
							</td>
							<th align="right">
								采购类型:
							</th>
							<td>
								<select name="waigoudd.type">
									<option>${waigoudd.type}</option>
									<option>外购</option>
									<option>外委</option>
									<option>模具</option>
								</select>
							</td>
						</tr>
						<tr>
							<th align="right">
								入库时间从:
							</th>
							<td>
								<input type="text" name="firsttime" class="Wdate" value="${firsttime}"
									onClick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'whyGreen'})" />
							</td>
							<th align="right">
								止:
							</th>
							<td>
								<input type="text" name="endtime" class="Wdate" value="${endtime}"
									onClick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'whyGreen'})" />
							</td>
							<th align="right">
								检验批次:
							</th>
							<td>
								<input type="text" name="waigoudd.examineLot" value="${waigoudd.examineLot}" />
							</td>
						</tr>
						<tr>
							<th align="right">
								供应商名称:
							</th>
							<td>
								<input type="text" name="waigoudd.gysName" value="${waigoudd.gysName}" />
							</td>
							<th align="right">
								仓区:
							</th>
							<td>
							<select name="waigoudd.changqu">
								<option>${waigoudd.changqu}</option>
								<s:iterator  value="cqList" id="cq">
								<option>${cq}</option>
								</s:iterator>
							</select>
							</td>
						</tr>
					</table>
					<input type="hidden" value="${pageStatus}" name="pageStatus" />
					<input type="submit" value="查询" class="input" />
					<input type="button" value="导出" class="input"
						onclick="exportExcel(this.form);todisabledone(this)" data="downData" />
				</form>
				<table class="table">
					<tr bgcolor="#c0dcf2" height="50px">
						<th align="center">
							序号
						</th>
						<th align="center">
							送货单号
						</th>
						<th align="center">
							采购订单号
						</th>
						<th align="center">
							物料类别
						</th>
						<th align="center">
							供应商
						</th>
						<th align="center">
							件号
						</th>
						<th align="center">
							零件名称
						</th>
						<th align="center">
							规格
						</th>
						<th align="center">
							检验批次
						</th>
						<th align="center">
							图号
						</th>
						<th align="center">
							版本
						</th>
						<th align="center">
							供货属性
						</th>
						<th align="center">
							采购类型
						</th>
						<th align="center">
							单位
						</th>
						<th align="center" id="th_danwei">
							箱(包)数
						</th>
						<th align="center">
							送货数量
						</th>
						<s:if test="pageStatus != 'huizong'">
						<th align="center">
							送货时间
						</th>
						</s:if>
						<th align="center">
							签收数量
						</th>
						<s:if test="pageStatus !='huizong'">
						<th align="center">
							确认时间
						</th>
						</s:if>
						<th align="center">
							合格数量
						</th>
						<s:if test="pageStatus == 'huizong'">
						<th align="center">
							含税价
						</th>
						<th align="center">
							不含税价
						</th>
						<th align="center">
							总额
						</th>
						</s:if>
						<s:if test="pageStatus != 'huizong'">
						<th align="center">
							检验时间
						</th>
						</s:if>
						<th align="center">
							状态
						</th>
						<th align="center">
								入库时间
						</th>
						<th align="center">
								仓区
						</th>
						<s:if test="pageStatus != 'huizong'">
						<th align="center">
							签收人
						</th>
						<th align="center">
							备注
						</th>
						<th align="center">
							其他
						</th>
						<th align="center">
							消息提醒
						</th>
						</s:if>
					</tr>
					<s:iterator value="list_wdd" id="pageWgww2" status="pageStatus2">
						<s:if test="#pageStatus2.index%2==1">
							<tr align="center" bgcolor="#e6f3fb" style="height: 50px;"
								onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'#e6f3fb')">
						</s:if>
						<s:else>
							<tr align="center" onmouseover="chageBgcolor(this)"
								style="height: 50px;" onmouseout="outBgcolor(this,'')">
						</s:else>
						<td>
							<s:property value="#pageStatus2.index+1" />
						</td>
						<td style="max-width: 50px; overflow: hidden; white-space: nowrap; text-overflow: ellipsis;">
							<font size="1">
							<a href="WaigouwaiweiPlanAction!findDeliveryNoteDetail.action?id=${pageWgww2.waigouDelivery.id}&pageStatus=all">${pageWgww2.waigouDelivery.planNumber}</a></font>
									<ul class="qs_ul">
										<li>
											${pageWgww2.waigouDelivery.planNumber}
										</li>
							</ul>
						</td>
						<td style="max-width: 50px; overflow: hidden; white-space: nowrap; text-overflow: ellipsis;">
							<font size="1">${pageWgww2.cgOrderNum}</font>
									<ul class="qs_ul">
										<li>
											${pageWgww2.cgOrderNum}
										</li>
							</ul>
						</td>
						<td>
							${pageWgww2.wgType}
						</td>
						<td style="max-width: 50px; overflow: hidden; white-space: nowrap; text-overflow: ellipsis;">
							<font size="1">${pageWgww2.gysName}</font>
									<ul class="qs_ul">
										<li>
											${pageWgww2.gysName}
										</li>
							</ul>
						</td>
						<td>
							${pageWgww2.markId}
						</td>
						<td style="max-width: 50px; overflow: hidden; white-space: nowrap; text-overflow: ellipsis;">
							<font size="1">${pageWgww2.proName}</font>
									<ul class="qs_ul">
										<li>
											${pageWgww2.proName}
										</li>
							</ul>
						</td>
						<td style="max-width: 50px; overflow: hidden; white-space: nowrap; text-overflow: ellipsis;">
							<font size="1">${pageWgww2.specification}</font>
									<ul class="qs_ul">
										<li>
											${pageWgww2.specification}
										</li>
							</ul>
						</td>
						<td>
								${pageWgww2.examineLot}
						<td>
							${pageWgww2.tuhao}
						</td>
						<td>
							${pageWgww2.banben}
						</td>
						<td>
							${pageWgww2.kgliao}
						</td>
						<td>
							${pageWgww2.type}
						</td>
						<td>
							${pageWgww2.unit}
						</td>
						<td>
							${pageWgww2.ctn}
						</td>
						<th>
							${pageWgww2.shNumber}
						</th>
						<s:if test="pageStatus != 'huizong'">
						<td>
							${pageWgww2.printTime}
						</td>
						</s:if>
						<td>
							<s:if test="#pageWgww2.qrNumber<#pageWgww2.shNumber">
								<font color="red">${pageWgww2.qrNumber}</font>
							</s:if>
							<s:else>${pageWgww2.qrNumber}</s:else>
						</td>
					<s:if test="pageStatus != 'huizong'">
						<td>
							${pageWgww2.querenTime}
						</td>
					</s:if>
						<td>
							<s:if test="#pageWgww2.hgNumber<#pageWgww2.qrNumber">
								<font color="red">${pageWgww2.hgNumber}</font>
							</s:if>
							<s:else>${pageWgww2.hgNumber}</s:else>
						</td>
						<s:if test="pageStatus == 'huizong'">
							<td><fmt:formatNumber value="${pageWgww2.hsPrice}" pattern="#.####"></fmt:formatNumber></td>
							<td><fmt:formatNumber value="${pageWgww2.price}" pattern="#.####"></fmt:formatNumber></td>
							<td>
								<s:if test="#pageWgww2.hgNumber!=null ">
								<fmt:formatNumber value="${pageWgww2.hsPrice * pageWgww2.hgNumber}" pattern="#.####"></fmt:formatNumber>
								</s:if>
							</td>
						</s:if>
					<s:if test="pageStatus != 'huizong'">
						<td>
							${pageWgww2.checkTime}
						</td>
					</s:if>
						<td>
							${pageWgww2.status}
						</td>
						<td>
						${pageWgww2.rukuTime}
						</td>
						<td>
						${pageWgww2.changqu}
						</td>
					<s:if test="pageStatus != 'huizong'">
						<td>
							${pageWgww2.qrUsersName}
						</td>
						<td>
							${pageWgww2.remarks}
						</td>
						<td>
							<s:if test='#pageWgww2.status=="退货待核对"'>
								<a href="javascript:;" onclick="tanchu('${pageWgww2.id}')">不合格品确认</a>
							</s:if>
							<s:if
								test='#pageWgww2.status!="待检验" && #pageWgww2.status!="退货待核对"'>
								<a
									href="OsRecord_showScope.action?record.wwddId=${pageWgww2.id}">检验记录</a>
								<%--<a href="WaigouwaiweiPlanAction!sqrk.action?id=${pageWgww2.id}">申请入库</a>
																		<input type="button" value="申请入库" style="height: 35px;" onclick="javascript:location.href='WaigouwaiweiPlanAction!sqrk.action?id=${pageWgww2.id}';">--%>
							</s:if>
						</td>
						<td>
							<s:if test='#pageWgww2.status== "待检验"'>
								<a href="WaigouwaiweiPlanAction!Sendmsg.action?id=${pageWgww2.id}&tag=jy">提醒检验</a>
							</s:if>
							<s:elseif test='#pageWgww2.status== "待入库" && #pageWgww2.hgNumber>0'>
								<a href="WaigouwaiweiPlanAction!Sendmsg.action?id=${pageWgww2.id}&tag=rk">提醒入库</a>
							</s:elseif>
						</td>
					</s:if>
					
					</s:iterator>
					<tr>
						<th colspan="1" id="th_0"></th>
						<th>${sumNum}</th>
						<th></th>
						<th>${sumbdhNUmber}</th>
						<th></th>
						<th>${sumwshNum} </th>
						<th colspan="20"></th>
					</tr>
					<tr>
						<td colspan="28" align="right">
							第
							<font color="red"><s:property value="cpage" /> </font> /
							<s:property value="total" />
							页
							<fenye:pages cpage="%{cpage}" total="%{total}" url="%{url}"
								styleClass="page" theme="number" />
						</td>
					</tr>
				</table>
			</div>
		</div>
		<%@include file="/util/foot.jsp"%>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
		<SCRIPT type="text/javascript">
$(function(){
	duoxuaSelect("status",'${waigoudd.status}');//下拉多选框
	duoxuaSelect("kgliao",'${waigoudd.kgliao}');//下拉多选框
	var th_danwei = document.getElementById("th_danwei");
	$("#th_0").attr("colspan",th_danwei.cellIndex+1);
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
function exportExcel(obj){//zhaobiaoAction!listAll.action
		obj.action = "WaigouwaiweiPlanAction!exportExcelWaigouDeliveryDetail.action";
	 	obj.submit();
	  	obj.action = "WaigouwaiweiPlanAction!findAllWaigouDeliveryDetail.action";
	 	}


function tanchu(num) {
		document.getElementById("xiugaiIframe").src = "WaigouwaiweiPlanAction!approvalNoPass.action?waigoudd.id="+num+"&pageStatus=bhg"
	chageDiv('block');
}

</SCRIPT>
	</body>
</html>
