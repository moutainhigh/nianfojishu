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
			<link rel="stylesheet"
			href="${pageContext.request.contextPath}/javascript/layer/theme/default/layer.css">
		<script type="text/javascript"
			src="${pageContext.request.contextPath}/javascript/layer/layer.js">
			</script>
		<style type="text/css">
.ztree li a {
	color: #fff;
}
</style>
	</head>
	<body>
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
							<span id="title">您正在设置标价天数</span>
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
		<div align="center">
		</div>

		<div align="center">
			<h3>

				<br />
			</h3>
			<form action="yuanclAndWaigjAction_showWuXiaoList.action"
				method="post" id="form">
				<input name="status" value="${status}" type="hidden">
				<table class="table" align="center">
					<tr>
						<td align="center" colspan="4">
							<div class="zTreeDemoBackground left">
								<ul class="list">
									<li class="title">
										物料类别:
										<input id="wgType" type="text" readonly="readonly" value=""
											style="width: 120px;" name="yuanclAndWaigj.wgType" />
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
					</tr>
					<tr>
						<td align="center">
							<input type="hidden" name="pageStatus"
								value="<s:property value='pageStatus'/>" />
							件号：
							<input type="text" name="yuanclAndWaigj.markId"
								value="<s:property value="yuanclAndWaigj.markId"/>" />
						</td>
						<td align="center">
							名称：
							<input type="text" name="yuanclAndWaigj.name"
								value="<s:property value="yuanclAndWaigj.name"/>" />
						</td>
						<td align="center">
							规格：
							<input type="text" name="yuanclAndWaigj.specification"
								value="<s:property value="yuanclAndWaigj.specification"/>" />
						</td>
						<td align="center">
							材质：
							<input type="text" name="yuanclAndWaigj.caizhi"
								value="<s:property value="yuanclAndWaigj.caizhi"/>" />
						</td>
					</tr>
					<tr>
						<td align="center">
							版本号：
							<input type="text" name="yuanclAndWaigj.banbenhao"
								value="<s:property value="yuanclAndWaigj.banbenhao"/>" />
						</td>
						<td align="center">
							状态：
							<select id="status" name="yuanclAndWaigj.status"
								style="width: 155px;">
								<option></option>
								<option>
									使用
								</option>
								<option>
									已确认
								</option>
								<option>
									禁用
								</option>
							</select>
						</td>
						<td align="center">
							图号：
							<input id="caizhi" type="text" name="yuanclAndWaigj.tuhao"
								value="<s:property value="yuanclAndWaigj.tuhao"/>" />
						</td>
						<td align="center">
							总成号：
							<input id="zcMarkId" type="text" name="yuanclAndWaigj.zcMarkId"
								value="<s:property value="yuanclAndWaigj.zcMarkId"/>" />
						</td>
					</tr>
					<tr>
						<td align="center">
							BOM单位：
							<select id="unit" name="yuanclAndWaigj.unit"
								style="width: 155px;">
								<option></option>
							</select>
						</td>
						<td align="center">
							仓库单位：
							<select id="ckUnit" name="yuanclAndWaigj.ckUnit"
								style="width: 155px;">
								<option></option>
							</select>
						</td>
						
							<td align="center">
							供料属性:
								<select name="kgliao" style="width: 155px;">
									<option value="${kgliao}">
										${kgliao}
									</option>
									<option>
									</option>
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
					</tr>
					<tr>
						<td align="center" colspan="4">
							<input type="submit" style="width: 100px; height: 40px;"
								value="查询(select)" />
							<s:if test="status=='newly'">
								<input type="button" value="导出(export)" onclick="clickWei();"
									style="width: 100px; height: 40px;" />
							</s:if>
						</td>
					</tr>
				</table>
			</form>
			<table class="table">
				<tr bgcolor="#c0dcf2" height="50px">
					<td align="center">
							<input type="checkbox" onclick="chageAllCheck(this)"/>
					</td>
					<td align="center">
						序号
					</td>
					<td align="center">
						物料类别
					</td>
					<td align="center">
						件号
					</td>
					<td align="center">
						名称
					</td>
					<td align="center">
						规格
					</td>
					<td align="center">
						版本号
					</td>
					<td align="center">
						供料属性
					</td>
					<td align="center">
						单位
					</td>
					<td align="center">
						材质
					</td>
					<td align="center">
						图号
					</td>
					<td align="center">
						单张重量
					</td>
					<td align="center">
						仓库单位
					</td>
					<td align="center">
						总成号
					</td>
					<%--
					<td align="center">
						材料类型
					</td>
					--%>
					<td align="center">
						质检周期
					</td>
					<td align="center">
						可用状态
					</td>
					<td align="center">
						物料状态
					</td>
					<td align="center" colspan="2">
						操作
					</td>
				</tr>
				<s:iterator value="YuanclAndWaigjList" id="yuanclAndWaigjPage"
					status="pageStatus">
					<s:if test="#pageStatus.index%2==1">
						<tr align="center" bgcolor="#e6f3fb"
							onmouseover="chageBgcolor(this)"
							onmouseout="outBgcolor(this,'#e6f3fb')">
					</s:if>
					<s:else>
						<tr align="center" onmouseover="chageBgcolor(this)"
							onmouseout="outBgcolor(this,'')">
					</s:else>
					<td align="center">
							<input id="offerId" name="offerIds" type="checkbox" value="${yuanclAndWaigjPage.id}" onclick="chageNum(this)"/>
						</td>
					<td>
						<s:property value="#pageStatus.index+1" />
					</td>
					<td>
						${yuanclAndWaigjPage.wgType}
					</td>
					<td align="left">
						${yuanclAndWaigjPage.markId}
					</td>
					<td align="left">
						${yuanclAndWaigjPage.name}
					</td>
					<td>
						${yuanclAndWaigjPage.specification}
					</td>
					<td>
						${yuanclAndWaigjPage.banbenhao}
					</td>
					<td>
						${yuanclAndWaigjPage.kgliao}
					</td>
					<td>
						${yuanclAndWaigjPage.unit}
					</td>
					<td>
						${yuanclAndWaigjPage.caizhi}
					</td>
					<td>
						${yuanclAndWaigjPage.tuhao}
					</td>
					<td>
						${yuanclAndWaigjPage.bili}
					</td>
					<td>
						${yuanclAndWaigjPage.ckUnit}
					</td>
					<td>
						${yuanclAndWaigjPage.zcMarkId}
					</td>
					<td>
						${yuanclAndWaigjPage.round}
					</td>
					<td>
						<s:if test="#yuanclAndWaigjPage.status=='禁用'">禁用</s:if>
						<s:elseif test="#yuanclAndWaigjPage.status=='已确认'">已确认</s:elseif>
						<s:else>使用</s:else>
					</td>
					<td>
						${yuanclAndWaigjPage.pricestatus}
					</td>
					<td colspan="2">
						<a onclick="downloadFile(${yuanclAndWaigjPage.id},'${cpage}')">下载图纸</a>/
						<s:if test="#yuanclAndWaigjPage.pricestatus=='报价中'">
							<a onclick="findAllZhOffer(${yuanclAndWaigjPage.id});">所有报价</a>/<a
								onclick="checkZhuser(${yuanclAndWaigjPage.id});">选择供应商</a>
						</s:if>
						<s:elseif test="#yuanclAndWaigjPage.pricestatus=='新增'">
							<s:if test="#yuanclAndWaigjPage.bjStartDate==null">
								<a href="javascript:;"
									onclick="showProcessForSb(${yuanclAndWaigjPage.id});">填写报价周期</a>
							</s:if>
						</s:elseif>
						<s:elseif test="#yuanclAndWaigjPage.pricestatus=='打样中'">
							<a onclick="findAllZhOffer(${yuanclAndWaigjPage.id},'yangpin');">确认样品</a>
						</s:elseif>
						<s:elseif test="#yuanclAndWaigjPage.pricestatus=='未审批'">
							<a onclick="findAllZhOffer(${yuanclAndWaigjPage.id},'yangpin');">所有报价</a>
						</s:elseif>
						<s:elseif test="#yuanclAndWaigjPage.pricestatus=='同意'">
							<a onclick="lvjiage(${yuanclAndWaigjPage.id});">录入价格</a>
						</s:elseif>
					</td>
				</s:iterator>
				<tr>
					<s:if test="errorMessage==null">
						<td colspan="19" align="right">
							第
							<font color="red"><s:property value="cpage" /> </font> /
							<s:property value="total" />
							页
							<fenye:pages cpage="%{cpage}" total="%{total}" url="%{url}"
								styleClass="page" theme="number" />
					</s:if>
					<s:else>
						<td colspan="19" align="center" style="color: red">
							${errorMessage}
						</td>
					</s:else>

				</tr>
				
				<s:if test="successMessage!=null">
					<tr>
						<td colspan="18" align="center" style="color: red">
							${successMessage}

						</td>
					</tr>
					
				</s:if>
				<tr> 
					<td colspan="18" align="center" style="color: red">
							<input type="button" value="选择报价周期" onclick="showProcess()" height="120px"/>
					</td>
				</tr>
			</table>
		</div>
		<br>
		</div>
		<%@include file="/util/foot.jsp"%>

		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
		<script type="text/javascript">

$(function() {
	getUnit("ckUnit")
	getUnit("unit")
	getCaizhi("caizhi");
})


function baojia(id) {
	window.location.href = "yuanclAndWaigjAction_findZhUserByType.action?yuanclAndWaigj.id="
			+ id + "&cpage=${cpage}";
}
function findAllZhOffer(id, look) {
	window.location.href = "yuanclAndWaigjAction_findAllZhOffer.action?yuanclAndWaigj.id="
			+ id + "&cpage=${cpage}" + "&status=" + look;
}
function checkZhuser(id) {
	window.location.href = "yuanclAndWaigjAction_findAllZhusers.action?yuanclAndWaigj.id="
			+ id + "&cpage=${cpage}";
}
function lvjiage(id) {
	window.location.href = "ZhuserOfferAction_inputPrice.action?yuanclAndWaigj.id="
			+ id + "&cpage=${cpage}";
}
function showProcessForSb(id) {
	$("#showProcess").attr("src",
			"yuanclAndWaigjAction_findOne.action?yuanclAndWaigj.id=" + id);
	chageDiv('block');

}

function clickWei() {
	document.getElementById('form').action = "yuanclAndWaigjAction_explorDaiLuru.action";
	document.getElementById('form').submit();
	document.getElementById('form').action = "yuanclAndWaigjAction_showWuXiaoList.action";
}
var mfzTree;
var addzTree;
var delzTree;
var updatezTree;

var id;
var pId;
var name;
var setting = {
	view : {
		dblClickExpand : false
	},
	data : {
		simpleData : {
			enable : true
		}
	},
	callback : {
		beforeClick : beforeClick,
		onClick : onClick
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
		data : {
			status : '物料类别'
		},
		dataType : 'json',
		cache : true,
		success : function(doc) {
			$(doc).each(function() {
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
	var zTree = $.fn.zTree.getZTreeObj("treeDemo"), nodes = zTree
			.getSelectedNodes(), v = "";
	nodes.sort(function compare(a, b) {
		return a.id - b.id;
	});
	for ( var i = 0, l = nodes.length; i < l; i++) {
		v = nodes[i].name;
	}
	//if (v.length > 0 ) v = v.substring(0, v.length-1); 
	cityObj = $("#wgType").val(v);

}

function showMenu() {
	var cityObj = $("#wgType");
	var cityOffset = $("#wgType").offset();
	$("#menuContent").css( {
		left : cityOffset.left + "px",
		top : cityOffset.top + cityObj.outerHeight() + "px"
	}).slideDown("fast");

	$("body").bind("mousedown", onBodyDown);
}
function hideMenu() {
	$("#menuContent").fadeOut("fast");
	$("body").unbind("mousedown", onBodyDown);
}
function onBodyDown(event) {
	if (!(event.target.id == "menuBtn" || event.target.id == "menuContent" || $(
			event.target).parents("#menuContent").length > 0)) {
		hideMenu();
	}
}
function downloadFile(id, cpage) {
	window.location.href = "ZhuserOfferAction_getwgOrderTz1.action?yuanclAndWaigj.id="
			+ id + "&cpage=" + cpage;
}
function showProcess() {
		 var ids = "";
		$("input:checkbox[name='offerIds']:checked").each(function(){
			if(ids==""){
				ids += $(this).val();
			}else{
				ids += ","+$(this).val() ;
			}
		});
		layer.open({
 			type: 2,
 			 title: '填写报价周期',
 			 area: ['1000px', '900px'],
 			 offset: '100px',
  				fixed: false, //不固定
				//  maxmin: true,
  				content: '<%=basePath%>/yuanclAndWaigjAction_adddeadline.action?offerIds='+ids
});
		}
</script>
	</body>
</html>
