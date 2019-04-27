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
		<div id="contentDiv"
			style="position: absolute; z-index: 255; width: 900px; display: none;"
			align="center">
			<div id="closeDiv"
				style="position: relative; top: 165px; left: 0px; right: 200px; z-index: 255; background: url(<%=basePath%>/images/bq_bg2.gif); width: 900px;">
				<table style="width: 100%">
					<tr>
						<td>
							<span id="title">您正在进行不合格品退货操作</span>
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
		<div align="center">
			<h2>
				<s:if test="pagestatus == 'bhg'">不合格品</s:if>
				库存管理
			</h2>
			<form action="goodsAction!findGoods.action" method="post">
				<table class="table" style="width: 95%;">
					<tr>
						<th>
							件号:
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
							规格：
						</th>
						<td>
							<input type="text" name="goods.goodsFormat"
								value="${goods.goodsFormat }" />
						</td>
					</tr>
					<tr>
						<th>
							物料类别:
						</th>
						<td>
							<div class="zTreeDemoBackground left">
								<ul class="list">
									<li class="title">
										<input id="wgType" type="text" value="${goods.wgType}"
											style="width: 120px;" name="goods.wgType" />
										<a id="menuBtn" href="#" onclick="showMenu(); return false;">选择</a>((按住Ctrl建不松点击,可清空)
									</li>
								</ul>
							</div>
							<div id="menuContent" class="menuContent"
								style="display: none; position: absolute;">
								<ul id="treeDemo" class="ztree"
									style="margin-top: 0; width: 160px;"></ul>
							</div>
						</td>
						<th>
							供料属性:
						</th>
						<td>
							<select name="goods.kgliao" id="kgliao2">
								<option>
									${goods.kgliao}
								</option>
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
						<th>
							版本:
						</th>
						<td>
							<input type="text" name="goods.banBenNumber"
								value="${goods.banBenNumber}" />
						</td>

					</tr>
					<tr>
						<th>
							库别：
						</th>
						<td>
							<select name="goods.goodsClass" id="whView" style="width: 155px;"
								onchange="changvalue(this)">
								<option value="">
									选择库别
								</option>
								<option value="${goods.goodsClass }" selected="selected">
									${goods.goodsClass }
								</option>
							</select>
						</td>
						<th>
							仓区:
						</th>
						<td>
							<%--							<input type="text" value="${goods.goodHouseName}"--%>
							<%--								name="goods.goodHouseName" id="goodHouseName"--%>
							<%--								readonly="readonly" />--%>
							<%--							<br>--%>
							<%--							<div class="select_checkBox">--%>
							<%--								<div class="chartQuota">--%>
							<%--									<p>--%>
							<%--										<a href="javascript:;" hidefocus="true" title="请选择仓区"><span>请选择仓区</span>--%>
							<%--											<b></b> </a>--%>
							<%--									</p>--%>
							<%--								</div>--%>
							<%--								<br>--%>
							<%--								<div class="chartOptionsFlowTrend"--%>
							<%--									style="overflow: scroll; height: 220px;">--%>
							<%--									<input type="text" onkeyup="kuanshu(this)"--%>
							<%--										onblur="kuanshu(this)" />--%>
							<%--									<ul>--%>
							<%--										<s:iterator value="goodHouseList" id="pageLists"--%>
							<%--											status="pageStatus">--%>
							<%--											<li class="">--%>
							<%--												<input type="checkbox" value="${pageLists.goodHouseName}"--%>
							<%--													name="chexkHouseName">--%>
							<%----%>
							<%--												<span>${pageLists.goodHouseName}</span>--%>
							<%--											</li>--%>
							<%--										</s:iterator>--%>
							<%--									</ul>--%>
							<%--									<p>--%>
							<%--										<a href="javascript:;" title="确定" hidefocus="true" class="a_0"--%>
							<%--											onclick="quding()">确定</a><a href="javascript:;" title="取消"--%>
							<%--											hidefocus="true" class="a_1" onclick="quxiao()">取消</a>--%>
							<%--									</p>--%>
							<%--								</div>--%>
							<%--							</div>--%>
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
								value="${goods.goodsPosition}" />
						</td>
					</tr>
					<tr>
						<th>
							日期从
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

						<th>
							业务件号
						</th>
						<td>
							<input type="text" name="goods.ywmarkId"
								value="${goods.ywmarkId }" />
						</td>
						<th>
							品名:
						</th>
						<td>
							<input type="text" name="goods.goodsFullName"
								value="${goods.goodsFullName }" />
						</td>
					</tr>
					<tr>

						<th>
							客户:
						</th>
						<td>
							<input type="text" name="goods.goodsCustomer"
								value="${goods.goodsCustomer }" />
						</td>
						<th>
							供应商:
						</th>
						<td>
							<input type="text" name="goods.goodsSupplier"
								value="${goods.goodsSupplier }" />
						</td>
						<s:if test="pagestatus != 'bhg'">
							<th>
								入库类型
							</th>
							<td>
								<select name="goods.goodsStyle">
									<option></option>
									<option>
										${goods.goodsStyle}
									</option>
									<option value="采购入库">采购入库</option>
									<option value="调仓入库">调仓入库</option>
									<option value="设变退库">设变退库</option>
									<option value="生产退料入库">生产退料入库</option>
									<option value="在制品入库">在制品入库</option>
									<option value="在制品入库(委外接收)">在制品入库(委外接收)</option>
									<option value="外协退料入库">外协退料入库</option>
									<option value="刷卡入库">刷卡入库</option>
									<option value="冲销转库">冲销转库</option>
									<option value="正常（成品）">正常（成品）</option>
								</select>
							</td>
					</tr>
					</s:if>
					<s:else>
						<td></td>
						<td></td>
					</s:else>
					</tr>
					<tr>
						<th>
							锁定单号
						</th>
						<td>
							<input type="text" name="goods.suodingdanhao"
								value="${goods.suodingdanhao}" size="15" />
						</td>
						<th>
							内部单号
						</th>
						<td>
							<input type="text" name="goods.neiorderId"
								value="${goods.neiorderId}" size="15" />
						</td>

						<th>
							库龄:
						</th>
						<td>
							<input type="text" name="goodsAge"
								onkeyup="mustBeNumber('goodsAge')" id="goodsAge"
								value="${goodsAge}" />
							(天)
						</td>
					</tr>
					<tr>
						<th colspan="8">
							<input type="hidden" value="${pageStatus }" name="pageStatus"/>
							<input type="submit" value="查询" class="input" />
							<s:if test="isall">
								<input type="button" value="查询所有" class="input"
									onclick="showAll(this.form)" />
							</s:if>

							<input type="button" value="导出" onclick="todisabledone(this);exportExcel(this.form)" data="downData"
								class="input" />
						</th>
					</tr>
				</table>
			</form>
			<table class="table" style="width: 95%;">
				<tr bgcolor="#c0dcf2" height="30px"
					style="border-collapse: separate;">
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
						内部单号
					</th>
					<th align="center">
						外部单号
					</th>
					<th align="center">
						锁定单号
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
						数量
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
					<s:if test="pageStatus!=null && pageStatus=='price'">
						<th align="center">
							含税单价
						</th>
						<th align="center">
							不含税单价
						</th>
						<th align="center">
							税率
						</th>
					</s:if>
					<s:else>
						<th align="center">
							状态
						</th>
						<th align="center">
							封存审批
						</th>
						<th align="center">
							操作
						</th>
						<th align="center">
							在库不良检验申请
						</th>
						
					</s:else>
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
							<s:property value="#see.index+1" />
						</td>
						<td align="left" style="color: gray;">
							${gs.goodsClass}
						</td>

						<td align="left" style="color: gray;">
							${gs.goodHouseName}
						</td>
						<td align="left" style="color: gray;">
							${gs.goodsPosition}
						</td>
						<td align="left">
							${gs.goodsMarkId}
							<s:if test="#gs.processNo!=null">(<font color="red">${gs.processNo}</font> )</s:if>
							<s:if test='#gs.ywmarkId!=null'>
								(<font color="green">${gs.ywmarkId}</font> )
							</s:if>
						</td>
						<td align="left">
							${gs.neiorderId}
						</td>
						<td align="left">
							${gs.waiorderId}
						</td>
						<td align="left">
							${gs.suodingdanhao}
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
						<td align="left"
							style="max-width: 50px; overflow: hidden; white-space: nowrap; text-overflow: ellipsis;">
							<font size="1">${gs.goodsFullName}</font>
							<ul class="qs_ul">
								<li>
									${gs.goodsFullName}
								</li>
							</ul>
						</td>
						<td align="left"
							style="max-width: 50px; overflow: hidden; white-space: nowrap; text-overflow: ellipsis;">
							<font size="1">${gs.goodsFormat}</font>
							<ul class="qs_ul">
								<li>
									${gs.goodsFormat}
								</li>
							</ul>
						</td>
						<td align="right">
							<fmt:formatNumber value="${gs.goodsCurQuantity}" pattern="#.0000" />
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
							${gs.goodsSupplier}
						</td>
						<td>
							${gs.goodsStyle}
						</td>
						<td>
							${gs.goodsChangeTime}
						</td>
						<s:if test="pageStatus!=null && pageStatus=='price'">
							<td>${gs.goodsBuyPrice}</td>
							<td>${gs.goodsBuyBhsPrice}</td>
							<td>${gs.taxprice}</td>
						</s:if>
						<s:else>
							<td>
							<s:if test="#gs.fcStatus=='封存'">
								<font color="red">封存</font>
							</s:if>
							<s:elseif test="#gs.goodsClass！='不合格品库'">
								可用
							</s:elseif>
						</td>
						<td>
							<s:if test="#gs.fcApplyStatus!=null&&true==#gs.bedit">
							${gs.fcApplyStatus}
						   </s:if>
						</td>
						<td>
						
							<s:if test="pagestatus == 'bhg'">
								<a href="javascript:;" onclick="tanchu('${gs.goodsId}')">不合格品确认</a>
							</s:if>
							<s:else>
								<s:if test="true==#gs.bout&&#gs.goodsClass!='外协库'">
									<a href="goodsAction!getOneGoods.action?id=${goodsId}&tag=out">出库</a>
									<a
										href="goodsAction!getOneGoods.action?id=${goodsId}&tag=goods_exChangeGoods">调库</a>
								</s:if>
								<s:if test="true==#gs.bedit">
									<a
										href="goodsAction!getOneGoods.action?id=${goodsId}&tag=update">修改</a>
									<s:if test="#gs.goodsClass!='成品库'">
										<a
											href="goodsAction!deletegs.action?goods.goodsId=${gs.goodsId}"
											onclick="return confirm('本次操作将会删除相关所有记录,确认执行删除操作?');">删除</a>
										<s:if test="#gs.fcStatus=='封存'">
											<s:if test="#gs.fcApplyStatus!='审批中'">
												<a
													href="javaScript:;if(window.confirm('您将申请解封此库存?')){window.location.href ='goodsAction!goodsApplyFcStatus.action?id=${goodsId}'};">解封</a>
													
											</s:if>
										</s:if>
										<s:else>
											<s:if test="#gs.fcApplyStatus!='审批中'">
												<a
													href="javaScript:;if(window.confirm('您将申请封存此库存?')){window.location.href ='goodsAction!goodsApplyFcStatus.action?id=${goodsId}'};">封存</a>
											</s:if>
										</s:else>
									</s:if>


									<s:if test="#gs.fcApplyStatus!=null&&#gs.fcApplyStatus!=''">
										<a href="CircuitRunAction_findAduitPage.action?id=${gs.epId}">审批动态</a>
									</s:if>
								</s:if>
								<s:if test="#gs.iddel == true">
									<s:if test="#gs.goodsClass!='成品库'">
										<a
											href="goodsAction!deletegs.action?goods.goodsId=${gs.goodsId}"
											onclick="return confirm('本次操作将会删除相关所有记录,确认执行删除操作?');">删除</a>
									</s:if>
								</s:if>
							</s:else>
							<s:if test='#gs.goodsClass == "外购件库"&&true==#gs.bedit'>
								<s:if test='#gs.islock == "NO" || #gs.islock == null'>
									<a href="goodsAction!sqsuocang.action?id=${gs.goodsId}"
										onclick="return confirm('是否要申请锁仓?')">锁仓</a>
								</s:if>
								<s:elseif test='#gs.islock == "YES"'>
									<a href="goodsAction!sqsqjiesuo.action?id=${gs.goodsId}"
										onclick="return confirm('是否要申请解锁?')">解锁</a>
								</s:elseif>
							</s:if>


							<%--			成品库--》外购件（调仓）记录表			--%>
							<s:if
								test='(#gs.goodsClass== "成品库"||#gs.goodsClass== "备货库")&&true==#gs.bedit '>
								<a href="goodsAction!getdtcChengGoods.action?id=${gs.goodsId}"
									onclick="return confirm('是否要调仓到外购件库')">调仓</a>
							</s:if>
							
						</td>
						<td>
							<s:if
								test='(#gs.goodsClass == "外购件库"||#gs.goodsClass == "外协库")&&true==#gs.bedit'>
								<a href="javaScript:;"
									onclick="sqbljy(${goodsId},'${gs.goodsMarkId}','${gs.goodsLotId}','${gs.goodsSupplier}')">在库不良检验申请</a>
							</s:if>
						</td>
						</s:else>
					
						
						</tr>
					</s:iterator>
					<tr>
						<th colspan="14" align="right">

						</th>
						<th>
							<fmt:formatNumber value="${sumcount}" pattern="#,#00.0#"></fmt:formatNumber>
						</th>
						<th colspan="12"></th>
					</tr>
					<tr>
						<td colspan="26" align="right">
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
						<td colspan="24" style="font-size: 15px; color: red;">
							对不起，没有查到相关的库存信息
						</td>
					</tr>
				</s:else>

			</table>
		</div>
		<br>
		</div>
		<%@include file="/util/foot.jsp"%>
		</center>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
		<script type="text/javascript">
function exportExcel(objForm) {
	objForm.action = "goodsAction!exportEXCEL.action?tag=goodsDetail";
	objForm.submit();
	objForm.action = "goodsAction!findGoods.action";
	window.confirm(arg0)
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
	addSelect();

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
function tanchu(num) {
	document.getElementById("xiugaiIframe").src = "goodsAction!getOneGoods.action?id="
			+ num + "&pagestatus=bhgth"
	chageDiv('block');
}
function showAll(objForm) {
	objForm.action = "goodsAction!findGoods.action?pagestatus=all";
	objForm.submit();
	objForm.action = "goodsAction!findGoods.action";
}
function sqbljy(id, markId, lotId, goodsSupplier) {
	if (confirm('你确定要申请检验该库存?此操作会将供应商:' + goodsSupplier + '的件号:' + markId
			+ '批次:' + lotId + '，下所有库存暂时转移到不合格品库。')) {
		window.location.href = "goodsAction!bljysq.action?id=" + id;
	}
}

function changvalue(obj) {
	if (obj != null && obj.value != "") {
		$.ajax( {
			type : "POST",
			url : "WarehouseAreaAction_findwaListByNO.action",
			data : {
				wareHouseName : obj.value
			},
			dataType : "json",
			success : function(data) {
				if (data != null && data.length > 0) {
					$("#goodHouseName").empty();
					$(data).each(
							function() {
								$("#goodHouseName").append(
										'<option value=' + this.goodHouseName
												+ '>' + this.goodHouseName
												+ '</option>');
							});
					var duoxuanselect = $(".duoxuaselect_div");
					if (duoxuanselect[1] != null) {
						$(duoxuanselect[1]).remove();
					}
					duoxuaSelect("goodHouseName");
				}
			}
		});
	}
}

function addSelect() {
	$
			.ajax( {
				type : "POST",
				url : "GoodsStoreAction!getViewAuth.action",
				data : {},
				dataType : "json",
				success : function(msg) {
					if (msg.success) {
						for (k in msg.data) {
							$('#whView').append(
									"<option>" + msg.data[k] + "</option>");
						}
					} else {
						alert(msg.message);
					}
					duoxuaSelect("whView", '${goods.goodsClass}');
				}
			});

}
</script>
	</body>
</html>