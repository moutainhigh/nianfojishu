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
							<span id="title">产品明细与维护</span>
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
						style="width: 98%; height: 500px; margin: 0px; padding: 0px;"></iframe>
				</div>
			</div>
		</div>
		<div>
			<div>
				&nbsp;&nbsp;&nbsp;&nbsp; [
				<a id="expandAllBtn" href="#" onclick="return false;">全部展开</a> ]
				&nbsp;&nbsp;&nbsp;&nbsp;[
				<a id="collapseAllBtn" href="#" onclick="return false;">全部折叠</a> ]

				另:页面查找请按Ctrl+F
			</div>
			<div align="left">
				<!-- 显示树形流水卡片模板 -->
				<div style="" align="left">
					<div style="height: 100%;">
						<ul id="treeDemo" class="ztree"></ul>
					</div>
				</div>
				<!-- 添加流水卡片模板操作 -->
				<div
					style="border: 1px solid #000000; position: absolute; background-color: #ffffff"
					id="showProDetail">
					<div align="center" style="display: none;" id="showdateil2">
						<table style="width: 100%">
							<tr>
								<th style="font-size: 10px;">
									产品明细与维护
								</th>
								<td align="right">
									<img alt="" src="<%=basePath%>/images/closeImage.png"
										width="30" height="25"
										onclick="javascript:$('#showProDetail').hide();chageDiv('none')">
								</td>
							</tr>
						</table>
					</div>
					<div id="selectDiv" style="display: none;" align="center">
						<input id="module0" type="button" value="配比明细"
							onclick="showSonCard()" style="width: 80px; height: 40px;" />
						<input id="module1" type="button" value="添加自制件"
							onclick="chageModule(this,'1')"
							style="width: 80px; height: 40px;" />
						<input id="module2" type="button" value="添加外购件"
							onclick="chageModule(this,'2')"
							style="width: 80px; height: 40px;" />
						<%--						<input id="module3" type="button" value="添加自制件"--%>
						<%--							onclick="chageModule(this,'3')"--%>
						<%--							style="width: 80px; height: 40px;" />--%>
						<input id="module2_1" type="button" value="添加半成品"
							onclick="chageModule(this,'2',true)"
							style="width: 80px; height: 40px;" />
						<input id="module4" type="button" value="工序管理"
							onclick="chageModule(this,'4');showProcess();"
							style="width: 80px; height: 40px;" />
						<input id="module5" type="button" value="复制模板"
							onclick="chageModule(this,'5');"
							style="width: 80px; height: 40px;" />
						<input id="module6" type="button" value="替换件号"
							onclick="chageModule(this,'6');"
							style="width: 80px; height: 40px;" />
						<input id="uId" type="hidden">
						<input id="module7" type="button" value="更新流水卡"
							onclick="updateProcard(this)" style="width: 80px; height: 40px;" />
						<input id="module8" type="button" value="删除零件"
							onclick="delProCard(this)" style="width: 80px; height: 40px;" />
						<%--						<input id="module9" type="button" value="转批产"--%>
						<%--							onclick="chageModule(this,'9');"--%>
						<%--							style="width: 80px; height: 40px;" />--%>
						<input id="module10" type="button" value="产品图纸"
							onclick="showCardTz()" style="width: 80px; height: 40px;" />
						<input id="module10_1" type="button" value="图纸下载"
							onclick="download()" style="width: 80px; height: 40px;" />
						<input id="module11" type="button" value="BOM预览"
							onclick="reviewBom();" style="width: 80px; height: 40px;" />
						<input id="module11_1" type="button" value="自检"
							onclick="checkSelf();" style="width: 80px; height: 40px;" />
						<input id="module12" type="button" value="一键导入"
							onclick="checkAndUpdateTz();" style="width: 80px; height: 40px;" />
						<input id="module13" type="button" value="下阶层"
							onclick="moveStatus()" style="width: 80px; height: 40px;" />
						<input id="module14" type="button" value="修改记录"
							onclick="changeLogshow();" style="width: 80px; height: 40px;" />
						<input id="module17" type="button" value="Bom结构审批指派"
							onclick="BomTreeSpzp()"
							style="width: 120px; height: 40px; display: none;" />
						<input id="module15" type="button" value="Bom结构申请审批"
							onclick="applyBomTree()" style="width: 100px; height: 40px;" />
						<input id="module16" type="button" value="Bom结构审批动态"
							onclick="BomTreeSpdt()"
							style="width: 120px; height: 40px; display: none;" />
						<input id="module18" type="button" value="未绑工序外购件"
							onclick="window.open('procardTemplateSbAction_findwbdProcessWgProcard.action?id=${param.id}')"
							style="width: 120px; height: 40px;" />
						<input id="module19" type="button" value="导入子件"
							onclick="daoruson()" style="width: 80px; height: 40px;" />
						<input id="module20" type="button" value="整体工序管理"
							onclick="window.open('procardTemplateSbAction_showBOMasExlAndProcedure.action?id=${param.id}')"
							style="width: 120px; height: 40px;" />
							<input id="module21" type="button" value="工序同步生产"
							onclick="gxtbsc(this)" style="width: 100px; height: 40px;display: none;" />
							<input id="module22" type="button" value="导出BOM"
							onclick="daochuBOM(this)" style="width: 100px; height: 40px;" />
						<input type="hidden" id="epId2">
						<br />
					</div>
					<div id="module1_1"
						style="display: none; background-color: #ffffff">
						<form id="lingForm" style="margin: 0px; padding: 0px;">
							<input type="hidden" id="wrootId" name="procardTemplatesb.rootId" />
							<input type="hidden" id="wfatherId"
								name="procardTemplatesb.fatherId" />
							<input type="hidden" id="wbelongLayer"
								name="procardTemplatesb.belongLayer" />
							<table class="table" style="width: 100%;">
								<tr>
									<th align="center" colspan="2">
										添加自制件
										<input type="submit" value="保存" />
										<input type="reset" value="重置" />
									</th>
								</tr>
								<tr>
									<th align="right" style="width: 25%;">
										件号:
									</th>
									<td>

										<div id="showAll1"
											style="background-color: #ffffff; position: absolute; visibility: hidden; z-index: 1;">
										</div>
										<input type="hidden" value="0" id="shortnameId1">
										<input name="procardTemplatesb.markId"
											onBlur="hidediv('showAll1')"
											onkeyup="noZhongwen(this,'zuhemarkId','showAll1','shortnameId1','组合')"
											onFocus="initfz('zuhemarkId','showAll1',130,230)"
											id="zuhemarkId">
										<font color="red">*</font>
										<div id="zuhemarkIddiv"></div>
									</td>
								</tr>
								<tr>
									<th align="right">
										名称:
									</th>
									<td>
										<input name="procardTemplatesb.proName" id="zuheproName">
										<font color="red">*</font>
									</td>
								</tr>
								<tr>
									<th align="right">
										版本号:
									</th>
									<td>
										<input name="procardTemplatesb.banBenNumber"
											id="zuhebanBenNumber">
									</td>
								</tr>
								<tr>
									<th align="right">
										权值:
									</th>
									<td>
										<input name="procardTemplatesb.corrCount" id="zuhecorrCount"
											value="1">
										<font color="red">*</font> (权值,对应上层所需数量)
									</td>
								</tr>
								<tr>
									<th align="right">
										单位:
									</th>
									<td>
										<select name="procardTemplatesb.unit" style="width: 155px;"
											id="zuheunit">
											<option>
												PCS
											</option>
											<option>
												件
											</option>
										</select>
										<font color="red">*</font>
									</td>
								</tr>
								<tr>
									<th align="right">
										是否领料:
									</th>
									<td>
										<select name="procardTemplatesb.lingliaostatus"
											id="zuhelingliaostatus" style="width: 155px;">
											<option value="是">
												是
											</option>
											<option value="否">
												否
											</option>
										</select>
										<font color="red">*</font>
									</td>
								</tr>
								<tr>
									<th align="right">
										提前激活:
									</th>
									<td>
										<select name="procardTemplatesb.zzjihuo" style="width: 155px;">
											<option value="正常">
												正常
											</option>
											<option value="提前">
												提前
											</option>
										</select>
									</td>
								</tr>
								<tr>
									<th align="right">
										领料方式:
									</th>
									<td>
										<select name="procardTemplatesb.lingliaoType"
											style="width: 155px;">
											<option value="all">
												全部到齐
											</option>
											<option value="part">
												部分到齐
											</option>
										</select>
									</td>
								</tr>
								<tr>
									<th align="right">
										初始总成:
									</th>
									<td>
										<input name="procardTemplatesb.loadMarkId" id="zuheloadMarkId">
									</td>
								</tr>
								<tr>
									<th align="right">
										型别:
									</th>
									<td>
										<input name="procardTemplatesb.carStyle" id="zuhecarStyle">
									</td>
								</tr>
								<tr>
									<th align="right">
										表处:
									</th>
									<td>
										<input name="procardTemplatesb.biaochu" id="zuhebiaochu">
									</td>
								</tr>
								<tr>
									<th align="right">
										材质:
									</th>
									<td>
										<input name="procardTemplatesb.clType" id="zuheclType">
									</td>
								</tr>
								<tr>
									<th align="right">
										长
									</th>
									<td>
										<input type="text" id="zuhethisLength"
											name="procardTemplatesb.thisLength">
									</td>
								</tr>
								<tr>
									<th align="right">
										宽
									</th>
									<td>
										<input type="text" id="zuhethisWidth"
											name="procardTemplatesb.thisWidth">
									</td>
								</tr>
								<tr>
									<th align="right">
										高
									</th>
									<td>
										<input type="text" id="zuhethisHight"
											name="procardTemplatesb.thisHight">

									</td>
								</tr>
								<%--<tr>
									<th align="right">
										安全库存:
									</th>
									<td>
										<input name="procardTemplatesb.safeCount" id="zuhesafeCount">
									</td>
								</tr>
								<tr>
									<th align="right">
										最低存量:
									</th>
									<td>
										<input name="procardTemplatesb.lastCount" id="zuhelastCount">
									</td>
								</tr>
								--%>
								<tr>
									<th align="right">
										卡片类型:
									</th>
									<td>
										<input name="procardTemplatesb.procardStyle" value="自制"
											readonly="readonly" id="zuheprocardStyle" />

									</td>
								</tr>
								<tr>
									<th align="right">
										产品类型:
									</th>
									<td>
										<select name="procardTemplatesb.productStyle"
											id="zuheproductStyle" style="width: 155px;"
											class="productStyle">
										</select>
									</td>
								</tr>
								<%--<tr>
									<th align="right">
										是否外购:
									</th>
									<td>
										<select name="procardTemplatesb.status" style="width: 155px;"
											id="zuhestatus">
											<option value="否">
												否
											</option>
											<option value="是">
												是
											</option>
										</select>
									</td>
								</tr>
								--%>
								<tr>
									<th align="right">
										工艺编号:
									</th>
									<td>
										<input name="procardTemplatesb.numb" id="zuhenumb">
									</td>
								</tr>
								<tr>
									<th align="right">
										发出日:
									</th>
									<td>
										<input class="Wdate" type="text"
											name="procardTemplatesb.fachuDate" id="zuhefachuDate"
											readonly="readonly"
											onClick="WdatePicker( { dateFmt : 'yyyy-MM-dd',skin : 'whyGreen'});" />
									</td>
								</tr>
								<tr>
									<th align="right">
										页数:
									</th>
									<td>
										<input name="procardTemplatesb.pageTotal" id="zuhepageTotal"
											onkeyup="mustBeNumber('pageTotal')">
									</td>
								</tr>
								<tr class="bjtr">
									<th align="right">
										编辑人 ：
									</th>
									<td>
										<select name="procardTemplatesb.bianzhiId" class="bianzhi"
											id="zuhebianzhiId"></select>
										<%--
										时间 ：
										<input class="Wdate" type="text"
											name="procardTemplatesb.bianzhiDate" id="bianzhiDate"
											readonly="readonly"
											onClick="WdatePicker( { dateFmt : 'yyyy-MM-dd',skin : 'whyGreen'});" />
									--%>
									</td>
								</tr>
								<tr class="jdtr">
									<th align="right">
										校对人 ：
									</th>
									<td>
										<select name="procardTemplatesb.jiaoduiId" class="jiaodui"
											id="zuhejiaoduiId"></select>
										<%--
										时间 ：
										<input class="Wdate" type="text"
											name="procardTemplatesb.jiaoduiDate" id="jiaoduiDate"
											readonly="readonly"
											onClick="WdatePicker( { dateFmt : 'yyyy-MM-dd',skin : 'whyGreen'});" />
									--%>
									</td>
								</tr>
								<tr class="shtr">
									<th align="right">
										审核人 ：
									</th>
									<td>
										<select name="procardTemplatesb.shenheId" class="shenhe"
											id="zuheshenheId"></select>
										<%--
										时间 ：
										<input class="Wdate" type="text"
											name="procardTemplatesb.shenheDate" id="shenheDate"
											readonly="readonly"
											onClick="WdatePicker( { dateFmt : 'yyyy-MM-dd',skin : 'whyGreen'});" />
									--%>
									</td>
								</tr>
								<tr class="pztr">
									<th align="right">
										批准人 ：
									</th>
									<td>
										<select name="procardTemplatesb.pizhunId" class="pizhun"
											id="zuhepizhunId"></select>
										<%--
										时间 ：
										<input class="Wdate" type="text"
											name="procardTemplatesb.pizhunDate" id="pizhunDate"
											readonly="readonly"
											onClick="WdatePicker( { dateFmt : 'yyyy-MM-dd',skin : 'whyGreen'});" />
									--%>
									</td>
								</tr>
								<tr>
									<th align="right">
										备注:
									</th>
									<td>
										<input name="procardTemplatesb.remark" id="zuheremark">
									</td>
								</tr>
								<tr>
									<td align="center" colspan="2">
										<input type="button" value="添加"
											onclick="submitForm(this,'lingForm')"
											style="width: 80px; height: 40px;" />
										<input type="reset" value="重置"
											style="width: 80px; height: 40px;" />
									</td>
								</tr>
							</table>
						</form>
					</div>
					<div id="module1_2"
						style="display: none; background-color: #ffffff">
						<form id="waiForm" style="margin: 0px; padding: 0px;">
							<input type="hidden" id="rootId" name="procardTemplatesb.rootId" />
							<input type="hidden" id="fatherId"
								name="procardTemplatesb.fatherId" />
							<input type="hidden" id="belongLayer"
								name="procardTemplatesb.belongLayer" />
							<table class="table" style="width: 100%;">
								<tr>
									<th align="center" colspan="2">
										添加外购件工艺卡片模版
										<input type="submit" value="保存" />
										<input type="reset" value="重置" />
									</th>
								</tr>
								<tr>
									<th align="right" style="width: 25%;">
										件号:
									</th>
									<td>
										<%--										<div id="showAllwgj"--%>
										<%--											style="background-color: #ffffff; position: absolute; visibility: hidden; z-index: 1;">--%>
										<%--										</div>--%>
										<%--										<input id="shortnamewgj" onkeyup="getAllNameswgj()"--%>
										<%--											style="height: 20px"--%>
										<%--											onFocus="init('shortnamewgj','showAllwgj')"--%>
										<%--											onBlur="hidediv('showAllwgj')">--%>
										<input type="text" id="wajmarkId"
											name="procardTemplatesb.markId" readonly="readonly" />
										<input type="hidden" value="" name="procardTemplatesb.isycl"
											id="wajisycl" />
										<font color="red">*</font>
										<input type="button" value="选择外购件"
											onclick="selectYclAndWgj('wgj')">
										<a href="<%=path%>/System/yclandwgj/yuanclAndWaigj_add.jsp"
											target="_showWai">找不到需要的件号?前往添加</a>
									</td>
								</tr>
								<tr>
									<th align="right">
										名称:
									</th>
									<td>
										<input type="text" id="wajproName"
											name="procardTemplatesb.proName" readonly="readonly" />
										<font color="red">*</font>
									</td>
								</tr>
								<tr>
									<th align="right">
										规格:
									</th>
									<td>
										<input type="text" id="wajspecification"
											name="procardTemplatesb.specification" readonly="readonly">
									</td>
								</tr>
								<tr>
									<th align="right">
										单位:
									</th>
									<td>
										<input type="text" id="danwei2" name="procardTemplatesb.unit"
											readonly="readonly" />
										<font color="red">*</font>
									</td>
								</tr>
								<tr>
									<th align="right">
										图号:
									</th>
									<td>
										<input type="text" id="wajtuhao" name="procardTemplatesb.tuhao"
											readonly="readonly" />
									</td>
								</tr>
								<tr>
									<th align="right">
										版本号:
									</th>
									<td>
										<input type="text" id="wajbanBenNumber"
											name="procardTemplatesb.banBenNumber" readonly="readonly">
									</td>
								</tr>
								<tr>
									<th align="right">
										重要性:
									</th>
									<td>
										<input type="text" id="wajimportance"
											name="procardTemplatesb.importance" readonly="readonly" />
									</td>
								</tr>
								<tr>
									<th align="right">
										初始总成:
									</th>
									<td>
										<input type="text" id="wajloadMarkId"
											name="procardTemplatesb.loadMarkId">
									</td>
								</tr>
								<tr>
									<th align="right">
										物料类别:
									</th>
									<td>
										<input type="text" id="wajwgType"
											name="procardTemplatesb.wgType" readonly="readonly">
									</td>
								</tr>
								<tr>
									<th align="right">
										单张重量:
									</th>
									<td>
										<input type="text" id="waibili" name="procardTemplatesb.bili"
											readonly="readonly">
									</td>
								</tr>
								<tr>
									<th align="right">
										长
									</th>
									<td>
										<input type="text" id="wajthisLength"
											name="procardTemplatesb.thisLength">
									</td>
								</tr>
								<tr>
									<th align="right">
										宽
									</th>
									<td>
										<input type="text" id="wajthisWidth"
											name="procardTemplatesb.thisWidth">
									</td>
								</tr>
								<tr>
									<th align="right">
										高
									</th>
									<td>
										<input type="text" id="wajthisHight"
											name="procardTemplatesb.thisHight">

									</td>
								</tr>
								<tr>
									<th align="right">
										车型:
									</th>
									<td>
										<input name="procardTemplatesb.carStyle">
									</td>
								</tr>
								<tr>
									<th align="right">
										卡片类型:
									</th>
									<td>
										<input name="procardTemplatesb.procardStyle" value="外购"
											readonly="readonly" />
									</td>
								</tr>
								<tr>
									<th align="right">
										供料属性:
									</th>
									<td>
										<select name="procardTemplatesb.kgliao" id="kgliao">
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
									<th align="right">
										权值:
									</th>
									<td>
										<input name="procardTemplatesb.quanzi1" style="width: 71px;" />
										:
										<input name="procardTemplatesb.quanzi2" style="width: 71px;" />
										(组合:外购件,格式如1:1)
										<font color="red">*</font>
									</td>
								</tr>
								<tr>
									<th align="right">
										损耗值:
									</th>
									<td>
										<input type="text" value="${procardTemplatesb.sunhao}"
											name="procardTemplatesb.sunhao" onchange="numyanzheng(this)">
										%
									</td>
								</tr>
								<tr>
									<th align="right">
										产品类型:
									</th>
									<td>
										<select name="procardTemplatesb.productStyle"
											style="width: 155px;" class="productStyle">
										</select>
									</td>
								</tr>
								<tr>
									<th align="right">
										余料加工:
									</th>
									<td>
										<select name="procardTemplatesb.jgyl" id="zuhejgyl">
											<option value="yes">
												是
											</option>
											<option value="no">
												否
											</option>
										</select>
									</td>
								</tr>
								<tr>
									<th align="right">
										炉号:
									</th>
									<td>
										<input name="procardTemplatesb.luhao" id="wailuhao">
									</td>
								</tr>
								<tr>
									<th align="right">
										编号:
									</th>
									<td>
										<input name="procardTemplatesb.number" id="wainumber">
									</td>
								</tr>
								<tr>
									<th align="right">
										实际定额:
									</th>
									<td>
										<input name="procardTemplatesb.actualFixed" id="waiactualFixed">
									</td>
								</tr>

								<tr>
									<th align="right">
										是否外购:
									</th>
									<td>
										<select name="procardTemplatesb.status" style="width: 155px;"
											id="waistatus">
											<option value="是">
												是
											</option>
											<option value="否">
												否
											</option>
										</select>
									</td>
								</tr>
								<tr>
									<th align="right">
										是否半成品:
									</th>
									<td>
										<select name="procardTemplatesb.needProcess" id="neddProcess"
											style="width: 155px;"
											onchange="changeneedProcess(this.value)">
											<option value="yes">
												是
											</option>
											<option value="no">
												否
											</option>
										</select>
									</td>
								</tr>
								<tr>
									<th align="right">
										是否领料:
									</th>
									<td>
										<select name="procardTemplatesb.lingliaostatus"
											id="zizlingliaostatus" style="width: 155px;">
											<option value="是">
												是
											</option>
											<option value="否">
												否
											</option>
										</select>
										<font color="red">*</font>
									</td>
								</tr>
								<tr id="safetr" style="display: none;">
									<th align="right">
										安全库存:
									</th>
									<td>
										<input name="procardTemplatesb.safeCount">
									</td>
								</tr>
								<tr id="lasttr" style="display: none;">
									<th align="right">
										最低存量:
									</th>
									<td>
										<input name="procardTemplatesb.lastCount">
									</td>
								</tr>
								<tr>
									<th align="right">
										工艺编号:
									</th>
									<td>
										<input name="procardTemplatesb.numb" id="wajnumb">
									</td>
								</tr>
								<tr>
									<th align="right">
										发出日:
									</th>
									<td>
										<input class="Wdate" type="text"
											name="procardTemplatesb.fachuDate" id="fachuDate"
											readonly="readonly"
											onClick="WdatePicker( { dateFmt : 'yyyy-MM-dd',skin : 'whyGreen'});" />
									</td>
								</tr>
								<tr>
									<th align="right">
										页数:
									</th>
									<td>
										<input name="procardTemplatesb.pageTotal" id="pageTotal"
											onkeyup="mustBeNumber('pageTotal')">
									</td>
								</tr>
								<tr class="bjtr">
									<th align="right">
										编辑人 ：
									</th>
									<td>
										<select name="procardTemplatesb.bianzhiId" class="bianzhi"></select>
										<%--
										时间 ：
										<input class="Wdate" type="text"
											name="procardTemplatesb.bianzhiDate" id="bianzhiDate"
											readonly="readonly"
											onClick="WdatePicker( { dateFmt : 'yyyy-MM-dd',skin : 'whyGreen'});" />
									--%>
									</td>
								</tr>
								<tr class="jdtr">
									<th align="right">
										校对人 ：
									</th>
									<td>
										<select name="procardTemplatesb.jiaoduiId" class="jiaodui"></select>
										<%--
										时间 ：
										<input class="Wdate" type="text"
											name="procardTemplatesb.jiaoduiDate" id="jiaoduiDate"
											readonly="readonly"
											onClick="WdatePicker( { dateFmt : 'yyyy-MM-dd',skin : 'whyGreen'});" />
									--%>
									</td>
								</tr>
								<tr class="shtr">
									<th align="right">
										审核人 ：
									</th>
									<td>
										<select name="procardTemplatesb.shenheId" class="shenhe"></select>
										<%--
										时间 ：
										<input class="Wdate" type="text"
											name="procardTemplatesb.shenheDate" id="shenheDate"
											readonly="readonly"
											onClick="WdatePicker( { dateFmt : 'yyyy-MM-dd',skin : 'whyGreen'});" />
									--%>
									</td>
								</tr>
								<tr class="pztr">
									<th align="right">
										批准人 ：
									</th>
									<td>
										<select name="procardTemplatesb.pizhunId" class="pizhun"></select>
										<%--
										时间 ：
										<input class="Wdate" type="text"
											name="procardTemplatesb.pizhunDate" id="pizhunDate"
											readonly="readonly"
											onClick="WdatePicker( { dateFmt : 'yyyy-MM-dd',skin : 'whyGreen'});" />
									--%>
									</td>
								</tr>
								<tr>
									<th align="right">
										备注:
									</th>
									<td>
										<input name="procardTemplatesb.remark">
									</td>
								</tr>
								<tr>
									<td align="center" colspan="2">
										<input type="button" value="添加"
											onclick="submitForm(this,'waiForm')"
											style="width: 80px; height: 40px;" />
										<input type="reset" value="重置"
											style="width: 80px; height: 40px;" />
									</td>
								</tr>
							</table>
						</form>
					</div>
					<div id="module1_4"
						style="display: none; background-color: #ffffff" align="center">
						<div>
							<table id="ProcessTab" class="table" style="width: 100%;">
							</table>
						</div>
						<br />
						<br />
						<form id="processForm"
							action="javascript:submitForm2('processForm');"
							style="margin: 0px; padding: 0px;" method="post">
							<input id="cardId" type="hidden" name="id" />
							<input id="parallelId" type="hidden"
								name="processTemplate.parallelId" />
							<input type="hidden" name="processTemplate.optechnologyRate"
								value="0" />
							<input type="hidden" name="processTemplate.opCouldReplaceRate"
								value="0" />
							<input type="hidden" name="processTemplate.opfuheRate" value="0" />
							<input type="hidden" name="processTemplate.opcaozuojiepai"
								value="0" />
							<input type="hidden" name="processTemplate.opshebeijiepai"
								value="0" />
							<input type="hidden" name="processTemplate.opnoReplaceRate"
								value="0" />
							<input type="hidden" name="processTemplate.opzonghezhishu"
								value="0" />
							<input type="hidden" name="processTemplate.opzongheqiangdu"
								value="0" />
							<input type="hidden" name="processTemplate.gztechnologyRate"
								value="0" />
							<input type="hidden" name="processTemplate.gzCouldReplaceRate"
								value="0" />
							<input type="hidden" name="processTemplate.gzfuheRate" value="0" />
							<input type="hidden" name="processTemplate.gzzhunbeijiepai"
								value="0" />
							<input type="hidden" name="processTemplate.gzzhunbeicishu"
								value="0" />
							<input type="hidden" name="processTemplate.gznoReplaceRate"
								value="0" />
							<input type="hidden" name="processTemplate.gzzonghezhishu"
								value="0" />
							<input type="hidden" name="processTemplate.gzzongheqiangdu"
								value="0" />
							<input type="hidden" name="processTemplate.processMomey"
								value="0" />
							<input type="hidden" name="processTemplate.opjiaofu" value="0" />
						</form>
					</div>
					<div id="module1_5"
						style="display: none; background-color: #ffffff" align="center">
						<br />
						<br />
						<br />
						<input id="mfatherId" type="hidden" />
						<div id="showAll"
							style="background-color: #ffffff; position: absolute; visibility: hidden; z-index: 1;">
						</div>
						<input type="text" id="shortname"
							onkeyup="getAllNames('shortname','showAll','shortnameId')"
							style="height: 20px;"
							onFocus="initfz('shortname','showAll',20,0)"
							onBlur="hidediv('showAll')" name="markId" />
						<input type="hidden" value="0" id="shortnameId">
						<input type="button" value="复制"
							onclick="copyProcard('${param.id}','shortname','shortnameId')">
						<br />
						<br />
						<br />
					</div>
					<div id="module1_6"
						style="display: none; background-color: #ffffff" align="center">
						<form action="" method="get">
							<table>
								<tr>
									<th>
										原件号
									</th>
									<td>
										<label id="oldMarkId"></label>
									</td>
								</tr>
								<tr>
									<th>
										新件号
									</th>
									<td>
										<input id="newMarkId" name="newMarkId">
									</td>
								</tr>
								<tr align="center">
									<td colspan="2">
										<input type="button" value="替换" onclick="updateMarkId()">
									</td>
								</tr>
							</table>
						</form>
						<br />
						<br />
					</div>
					<div id="module1_9"
						style="display: none; background-color: #ffffff" align="center">
						<br />
						<br />
						<br />
						<form action="" method="get">
							<table>
								<tr>
									<th>
										批产件号
									</th>
									<td>
										<input id="lpMarkId" name="lpMarkId">
									</td>
								</tr>
								<tr id='dd9' style="display: none;">
									<td align="center">
										<h3>
											<font color="red">正在转换请耐心等待.............</font>
										</h3>
									</td>
								</tr>
								<tr align="center">
									<td colspan="2">
										<input type="button" value="转换" onclick="changeTolp()">
									</td>
								</tr>
							</table>
						</form>
						<br />
						<br />
						<br />
					</div>
					<div id="module1_12"
						style="display: none; background-color: #000000; opacity: 0.5; border: 1px #000000 solid;"
						align="center">
						<br />
						<input type="button" id="tzdrbtn" onclick="tzdr()" value="图纸一键导入" />
						&nbsp;&nbsp;&nbsp;
						<input type="button" id="tzjdbtn" onclick="tzjd()" value="图纸一键加点" />
						&nbsp;&nbsp;&nbsp;
						<input type="button" id="gxjdbtn" onclick="tzgx()" value="工序一键导入" />
						&nbsp;&nbsp;&nbsp;
						<%--						<input type="button" id="wgjGxbtn" onclick="wgjgx()" value="工序关联外购件" />--%>
						<%--						&nbsp;&nbsp;&nbsp;--%>
						<input type="button" id="wgjgxgl" onclick="processAndWgProcard()"
							value="外购件工序一键关联" />
						<div id="module1_12_son"
							style="display: none; background-color: #ffffff" align="left">
							<br />
							<br />
							<br />
							<h3 id="drh3" style="display: none; background-color: #ffffff">
								<font color="red">正在导入图纸请耐心等待.............</font>
							</h3>
							<h3 id="jdh3" style="display: none; background-color: #ffffff">
								<font color="red">正在加点请耐心等待.............</font>
							</h3>
							<h3 id="jdh4" style="display: none; background-color: #ffffff">
								<font color="red">正在工序请耐心等待.............</font>
							</h3>
							<h3 id="jdh5" style="display: none; background-color: #ffffff">
								<font color="red">正在关联外购件工序.............</font>
							</h3>
							<h3 id="wgjgx5" style="display: none; background-color: #ffffff">
								<font color="red"></font>
							</h3>
							<br />
							<br />
							<br />
						</div>

						<br />
						<br />
					</div>
					<div id="showCardTemplate"
						style="display: none; height: 1000px; background-color: #ffffff">
						<iframe id="showCardIframe" src="" marginwidth="0"
							marginheight="0" hspace="0" vspace="0" frameborder="0"
							scrolling="no"
							style="width: 100%; height: 1500px; margin: 0px; padding: 0px;"></iframe>
					</div>
				</div>
				<div style="clear: both;"></div>

			</div>
			<br>
		</div>
		<%@include file="/util/foot.jsp"%>
		<div style="display: none;"></div>
		<input type="hidden" value="${param.showId}" id="showId" />
		</center>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
		<script type="text/javascript"
			src="<%=basePath%>/javascript/zTree/js/jquery.ztree.excheck-3.5.js">
</script>
		<script type="text/javascript"
			src="<%=basePath%>/javascript/zTree/js/jquery.ztree.exedit-3.5.js">
</script>
		<style>
/*按钮*/
.icon_div {
	display: inline-block;
	height: 25px;
	width: 35px;
	background:
		url(http://c.csdnimg.cn/public/common/toolbar/images/f_icon.png)
		no-repeat 12px -127px;
}

.icon_div a {
	display: inline-block;
	width: 27px;
	height: 20px;
	cursor: pointer;
}

/*end--按钮*/ /*ztree表格*/
.ztree {
	padding: 0;
	border: 2px solid #CDD6D5;
}

.ztree li a {
	vertical-align: middle;
	height: 30px;
}

.ztree li a.curSelectedNode {
	height: 30px;
}

.ztree li>a {
	width: 100%;
}

.ztree li>a,.ztree li a.curSelectedNode {
	padding-top: 0px;
	background: none;
	border: none;
	cursor: default;
	opacity: 1;
}

.ztree li ul {
	padding-left: 0px
}

.ztree div.diy span {
	line-height: 30px;
	vertical-align: middle;
}

.ztree div.diy {
	height: 100%;
	width: 22%;
	line-height: 30px;
	border-top: 1px dotted #ccc;
	border-left: 1px solid #000000;
	text-align: center;
	display: inline-block;
	box-sizing: border-box;
	color: #000000;
	font-family: "SimSun";
	font-size: 14px;
	overflow: hidden;
	font-weight: bolder;
}

.ztree div.diy:first-child {
	text-align: left;
	text-indent: 10px;
	border-left: thin;
}

.ztree .head {
	background: #5787EB;
}

.ztree .head div.diy {
	border-top: none;
	border-right: 1px solid #CDD2D4;
	color: #fff;
	font-family: "Microsoft YaHei";
	font-size: 14px;
}
/*end--ztree表格*/
</style>
		<script type="text/javascript">
//========================================zTree显示
var module4 = $("#module4");
//自动组装树形结构
var setting= {
	edit : {
		enable : true,
		showRemoveBtn : false,
		showRenameBtn : false,
		showTitle : true
	},
	data : {
		simpleData : {
			enable : true
		},
		key : {
			title : "title"
		}
	},
	callback : {
		onClick : onClick,
		beforeDrag : beforeDrag,
		beforeDrop : beforeDrop,
		onDrag : onDrag,
	},
	view : {
		fontCss : getFont,
		nameIsHTML : true,
		showTitle : true,
		showLine : true,
		showIcon : true,
		addDiyDom : addDiyDom
	}
};
function expandNode(e) {
	var zTree = $.fn.zTree.getZTreeObj("treeDemo"), type = e.data.type, nodes = zTree
			.getSelectedNodes();
	if (type.indexOf("All") < 0 && nodes.length == 0) {
		alert("请先选择一个父节点");
	}

	if (type == "expandAll") {
		zTree.expandAll(true);
	} else if (type == "collapseAll") {
		zTree.expandAll(false);
	} else {
		var callbackFlag = $("#callbackTrigger").attr("checked");
		for ( var i = 0, l = nodes.length; i < l; i++) {
			zTree.setting.view.fontCss = {};
			if (type == "expand") {
				zTree.expandNode(nodes[i], true, null, null, callbackFlag);
			} else if (type == "collapse") {
				zTree.expandNode(nodes[i], false, null, null, callbackFlag);
			} else if (type == "toggle") {
				zTree.expandNode(nodes[i], null, null, null, callbackFlag);
			} else if (type == "expandSon") {
				zTree.expandNode(nodes[i], true, true, null, callbackFlag);
			} else if (type == "collapseSon") {
				zTree.expandNode(nodes[i], false, true, null, callbackFlag);
			}
		}
	}
}

//获取所有父节点，计算单机量
function getParentPath(treeNode){
	if(treeNode==null )
		return "";
	var yongliang = 0;
	if (treeNode.proStruts == '外购') {
		yongliang =  treeNode.quanzi2 / treeNode.quanzi1;
	} else {
		if((treeNode.corrCount==null||treeNode.corrCount==0 )&&treeNode.proStruts == '总成'){
			yongliang = 1 ;
		}else{
			yongliang = treeNode.corrCount ;
		}
	}
	
	var pNode = treeNode.getParentNode();
	if(pNode!=null){
		yongliang = yongliang*getParentPath(pNode);
	}
	return yongliang;
}

/**
 * 自定义DOM节点
 */
function addDiyDom(treeId, treeNode) {
	var spaceWidth = 15;
	var liObj = $("#" + treeNode.tId);
	var aObj = $("#" + treeNode.tId + "_a");
	var switchObj = $("#" + treeNode.tId + "_switch");
	var icoObj = $("#" + treeNode.tId + "_ico");
	var spanObj = $("#" + treeNode.tId + "_span");
	aObj.attr('title', '');
	aObj.append('<div class="diy swich"></div>');
	var div = $(liObj).find('div').eq(0);
	switchObj.remove();
	spanObj.remove();
	icoObj.remove();
	div.append(switchObj);
	div.append(spanObj);
	var spaceStr = "<span style='height:1px;display: inline-block;width:"
			+ (spaceWidth * treeNode.level) + "px'></span>";
	switchObj.before(spaceStr);
	var editStr = '';
	var markidAndTuhao = treeNode.markId;
	if (treeNode.tuhao != null && treeNode.tuhao != ""
			&& treeNode.tuhao != treeNode.markId) {
		markidAndTuhao += "(" + treeNode.tuhao + ")"
	}
	editStr += '<div class="diy" style="text-align: left;">' + (markidAndTuhao == null ? '&nbsp;'
			: markidAndTuhao) + '</div>';
	editStr += '<div class="diy" style="text-align: left;">' + (treeNode.specification == null ? '&nbsp;'
			: treeNode.specification) + '</div>';
	var corpCat = '<div title="' + treeNode.proName + '">' + treeNode.proName
			+ '</div>';
	editStr += '<div class="diy" style="text-align: left;width:60px;">' + (treeNode.proName == null ? '&nbsp;'
			: corpCat) + '</div>';
	if (treeNode.proStruts == '外购') {
		editStr += '<div class="diy" style="text-align: right;width:80px;">'
				+  Math.round((treeNode.quanzi2 / treeNode.quanzi1)*10000)/10000  + '</div>';
	} else {
		editStr += '<div class="diy" style="text-align: right;width:80px;">' + Math.round(treeNode.corrCount*10000)/10000  + '</div>';
	}
	//Math.round(getParentPath(treeNode)*10000)/10000 不补0的写法： 感觉不补0的话数字位数又不一致，先留着吧
	editStr += '<div class="diy" style="text-align: right;width:80px;">'
				+ Math.round(getParentPath(treeNode)*10000)/10000 + '</div>';
	var bzStatusStr = '';
	if (treeNode.bzStatus != '已批准') {
		bzStatusStr = '<font color="red">' + treeNode.bzStatus + '</font>';
	} else {
		bzStatusStr = '已批准';
	}
	editStr += '<div class="diy" style="text-align: left;width:60px;">' + bzStatusStr + '</div>';
	editStr += '<div class="diy" style="width:30px;">' + (treeNode.banBenNumber == null ? '&nbsp;'
			: treeNode.banBenNumber) + '</div>';
	editStr += '<div class="diy" style="width:30px;">' + (treeNode.banci == null ? '&nbsp;'
			: treeNode.banci) + '</div>';
	editStr += '<div class="diy" style="text-align: left;">'
			+ treeNode.bianzhiName + '、' + treeNode.jiaoduiName + '、'
			+ treeNode.shenheName + '、' + treeNode.pizhunName + '</div>';
	aObj.append(editStr);
}
/**
 * 根据权限展示功能按钮
 * @param treeNode
 * @returns {string}
 */
function formatHandle(treeNode) {
	var htmlStr = '';
	htmlStr += '<div class="icon_div"><a class="icon_view" title="查看"  href="javascript:view(\'' + treeNode.id + '\')"></a></div>';
	htmlStr += '<div class="icon_div"><a class="icon_edit" title="修改" href="javascript:edit(\'' + treeNode.id + '\')"></a></div>';
	htmlStr += '<div class="icon_div"><a class="icon_del" title="删除" href="javascript:del(\'' + treeNode.id + '\')"></a></div>';
	return htmlStr;
}
function setCheck() {
	var zTree = $.fn.zTree.getZTreeObj("treeDemo"), isCopy = true, isMove = true, prev = true, inner = true, next = true;
	zTree.setting.edit.drag.isCopy = isCopy;
	zTree.setting.edit.drag.isMove = isMove;

	zTree.setting.edit.drag.prev = prev;
	zTree.setting.edit.drag.inner = inner;
	zTree.setting.edit.drag.next = next;
}

//加载树形数据
$(document).ready(function() {
	loadTree();
	$("#expandAllBtn").bind("click", {
		type : "expandAll"
	}, expandNode);
	$("#collapseAllBtn").bind("click", {
		type : "collapseAll"
	}, expandNode);
	addOnClick();
});
var totalMaxCount = 0;
//生成
function loadTree() {
	$
			.ajax( {
				url : 'procardTemplateSbAction_findProcardTemByRootId.action',
				type : 'post',
				dataType : 'json',
				data : {
					id : '${rootId}'
				},
				cache : true,
				success : function(doc) {
					var zNodes = [];
					$(doc)
							.each(function() {
								//var b = true;
									if ($(this).attr('procardStyle') == "总成") {
										totalMaxCount = $(this)
												.attr('maxCount');
									}
									//供料属性
									var glsx = '';
									if ($(this).attr('procardStyle') == "外购"
											&& $(this).attr('kgliao') != null
											&& $(this).attr('kgliao') != ""
											&& $(this).attr('kgliao') != "TK") {
										glsx = " <span style='color:green;margin-right:0px;'>"
												+ $(this).attr('kgliao')
												+ "</span>";
									}
									var procardStyle = $(this).attr(
											'procardStyle');
									if (procardStyle == "待定") {
										procardStyle = "<span style='color:red;margin-right:0px;'>待定</span>";
									}
									//单交件状态
									var danjiaojian = $(this).attr(
											'danjiaojian');
									if (danjiaojian == null) {
										danjiaojian = "";
									}
									//半成品状态
									var needProcess = $(this).attr(
											'needProcess');
									if (needProcess == "yes") {
										needProcess = " (半成品)";
									} else {
										needProcess = "";
									}
									var bzStatus = $(this).attr('bzStatus');
									if (bzStatus == null || bzStatus == "") {
										bzStatus = "初始";
									}
									if (bzStatus != "已批准") {
										bzStatus = "<span style='color:red;margin-right:0px;'>"
												+ bzStatus + "</span>";
									} else {
										bzStatus = "";
									}
									var markid = $(this).attr('markId');
									if ($(this).attr('id') == "${param.showId}") {
										var  ywmarkId = $(this).attr('ywMarkId');
										if(ywmarkId == null || ywmarkId == ''){
											ywmarkId = '';
										}else{
											ywmarkId = '<font color="green">('+ywmarkId+')</font>'
										}
										markid = "<font color='red'>" + markid
												+ "</font>"+ywmarkId;
									}

									var hgstyle = "<span style='font-weight: bolder;font-size: 18px;'>--</span>";
									var belongLayer = "<font color = 'red'> "+$(this).attr('belongLayer')+"&nbsp;&nbsp;&nbsp;</font>"
									zNodes
											.push( {
												id : $(this).attr('id'),
												sbStatus : $(this).attr(
														'sbStatus'),
												bzStatus : $(this).attr(
														'bzStatus'),
												banBenNumber : $(this).attr(
														'banBenNumber'),
												banci : $(this).attr('banci'),
												bomApplyStatus : $(this).attr(
														'bomApplyStatus'),
												epId2 : $(this).attr('epId2'),
												pId : $(this).attr('fatherId'),
												proStruts : $(this).attr(
														'procardStyle'),
												rootId : $(this).attr('rootId'),
												markId : markid,
												tuhao : $(this).attr('tuhao'),
												danjiaojian : danjiaojian,
												productStyle : $(this).attr(
														'productStyle'),
												belongLayer : $(this).attr(
														'belongLayer'),
												name :  belongLayer+$(this).attr('proName'),
												proName : procardStyle
														+ needProcess + glsx,
												quanzi1 : $(this).attr(
														'quanzi1'),
												quanzi2 : $(this).attr(
														'quanzi2'),
												corrCount : $(this).attr(
														'corrCount'),
												bianzhiName : $(this).attr(
														'bianzhiName'),
												jiaoduiName : $(this).attr(
														'jiaoduiName'),
												shenheName : $(this).attr(
														'shenheName'),
												pizhunName : $(this).attr(
														'pizhunName'),
												specification : $(this).attr(
														'specification'),
												tq : $(this).attr(
														'tq'),
												title : $(this).attr(
														'procardStyle')
														+ '--'
														+ $(this)
																.attr('markId')
														+ '--'
														+ $(this).attr(
																'proName')
														+ danjiaojian
														+ needProcess,
												click : false,
												drop : true,
												open : true
											});

								});
					$.fn.zTree.init($("#treeDemo"), setting, zNodes);
					//添加表头
					var li_head = ' <li class="head"><a><div class="diy" >产品名称</div><div class="diy" align="center">件号/图号</div><div class="diy" align="center">规格</div><div class="diy" style="width:60px;">产品类型</div><div class="diy" style="width:80px;">单机用量</div><div class="diy" style="width:80px;">单台用量</div><div class="diy" style="width:60px;">编制状态</div><div class="diy"  style="width:30px;">版本</div><div class="diy"  style="width:30px;">版次</div><div class="diy">编制人员</div></a></li>';
					var rows = $("#treeDemo").find('li');
					if (rows.length > 0) {
						rows.eq(0).before(li_head)
					} else {
						$("#treeDemo").append(li_head);
						$("#treeDemo")
								.append(
										'<li ><div style="text-align: center;line-height: 30px;" >无符合条件数据</div></li>')
					}
				},
				error : function(doc) {
					console.log('doc.responseText:',doc.responseText)
					alert("BOM返回数据异常!");
				}
			});

}
function getFont(treeId, node) {
	return node.font ? node.font : {};
}
var moveId = 0;
function onDrag(event, treeId, treeNodes) {
	moveId = treeNodes[0].id;
}
function beforeDrag(treeId, treeNodes) {
	for ( var i = 0, l = treeNodes.length; i < l; i++) {
		if (treeNodes[i].drag === false) {
			return false;
		}
	}
	return true;
}function beforeDrop(treeId, treeNodes, targetNode, moveType) {
	if (!window.confirm("是否移动bom结构")) {
		return false;
	}
	if (targetNode.id == null) {
		return false;
	}
	var moveok = targetNode ? targetNode.drop !== false : true;
	if (!moveok) {
		return moveok;
	}
	if (moveok) {
		//alert(moveId);
		$.ajax( {
			type : "POST",
			url : "procardTemplateSbAction_moveProcardTemplate.action",
			dataType : "json",
			data : {
				moveId : moveId,
				targetId : targetNode.id
			},
			success : function(msg) {
				alert(msg.message);
				loadTree();
				return true;
			}
		});
	}
}
var oldObj;
var oldObj2;
//切换添加类型
function chageModule(obj, obj2, bcpStatus) {
	if (oldObj != null) {
		$("#module1_" + oldObj2).hide();
	}
	if (bcpStatus) {
		$("#neddProcess").val("yes");
		changeneedProcess("yes");
		obj2 = "2";
	} else {
		$("#neddProcess").val("no");
		changeneedProcess("no");
	}
	$("#module1_" + obj2).show('slow');
	oldObj = obj;
	oldObj2 = obj2;
}

//点击回调函数
function onClick(event, treeId, treeNode, clickFlag) {
	var proStruts = treeNode.proStruts;// 卡片状态(总成，零组件，原材料)
	var danjiaojian = treeNode.danjiaojian;// 卡片状态(总成，零组件，原材料)
	var productStyle = treeNode.productStyle;//生产类型
	var sbStatus = treeNode.sbStatus;//设变申请状态
	var bzStatus = treeNode.bzStatus;//编制状态
	var bomApplyStatus = treeNode.bomApplyStatus;//编制状态
	$(".productStyle").empty();
	$(".productStyle").append("<option value='"+productStyle+"'>"+productStyle+"</option>");
	var tq = treeNode.tq;
	$("#module21").hide();
	if (proStruts == "总成"
			&& (bomApplyStatus == null || (!bomApplyStatus == "未审批"
					&& !bomApplyStatus == "审批中" && !bomApplyStatus == "同意"))) {
		$("#module15").attr("disabled", false);
	} else {
		$("#module15").attr("disabled", true);
	}
	if (proStruts == "总成"&&tq=="yes"){
		$("#module21").show();
	}
	if (bomApplyStatus == "未审批" || bomApplyStatus == "审批中"
			|| bomApplyStatus == "同意") {
		$("#module16").show();
	} else {
		$("#module16").hide();
	}
	$("#module1_12").hide();
	if (productStyle == "试制") {
		$("#module7").show();
	} else {
		$("#module7").hide();
	}
<%--	if (bzStatus == "已批准") {--%>
<%--		if (sbStatus == "未审批" || sbStatus == "审批中") {--%>
<%--			$("#module14").attr("disabled", true);--%>
<%--		} else {--%>
<%--			$("#module14").attr("disabled", false);--%>
<%--		}--%>
<%--	} else {--%>
<%--		$("#module14").attr("disabled", true);--%>
<%--	}--%>

	//工序赋值
	$("#epId2").val(treeNode.epId2);
	$("#cardId").val(treeNode.id);
	$("#mfatherId").val(treeNode.id);
	$("#uId").val(treeNode.id);
	$("#newMarkId").val(treeNode.markId);
	$("#oldMarkId").empty();
	$("#oldMarkId").append(treeNode.markId);
	showDiv();//页面内容清空
	//显示添加选项的页面
	$("#selectDiv").show();
	var e = event || window.event;
	var scrollTop = getScrollTop();//获取滚动条离顶部距离
	//var mouseLeft = e.clientX + 30;
	var mouseLeft = 30;
	var mouseTop = e.clientY - 40 + scrollTop;
	if (mouseTop < 0) {
		mouseTop = 0;
	}
	//显示悬浮窗样式的项目
	$("#showProDetail").css( {
		"top" : mouseTop,
		"left" : mouseLeft
	});

	if (proStruts == "总成" && productStyle == "试制") {
		$("#module9").show();
	} else {
		$("#module9").hide();
	}
	if (proStruts == "总成") {
		$("#module17").show();
		$("#module12").show();
		$("#module18").show();
	} else {
		$("#module17").hide();
		$("#module12").hide();
		$("#module18").show();
	}
if (proStruts == "总成" || proStruts == "自制") {
		//零组件赋值
		$("#rootId").val(treeNode.rootId);
		$("#fatherId").val(treeNode.id);
		$("#belongLayer").val(treeNode.belongLayer + 1);
		//外购件赋值
		$("#wrootId").val(treeNode.rootId);
		$("#wfatherId").val(treeNode.id);
		$("#wbelongLayer").val(treeNode.belongLayer + 1);
		//原材料赋值
		$("#yrootId").val(treeNode.rootId);
		$("#yfatherId").val(treeNode.id);
		$("#ybelongLayer").val(treeNode.belongLayer + 1);
		if (danjiaojian == "单交件") {
			//全部不可用
			$("#module1").attr("disabled", true);
			$("#module2").attr("disabled", true);
			$("#module2_1").attr("disabled", true);
			$("#module3").attr("disabled", true);
			$("#module4").attr("disabled", false);
			$("#module5").attr("disabled", false);
			$("#module6").attr("disabled", false);
		} else {
			//全部可用
			$("#module1").attr("disabled", false);
			$("#module2").attr("disabled", false);
			$("#module2_1").attr("disabled", false);
			$("#module3").attr("disabled", false);
			$("#module4").attr("disabled", false);
			$("#module5").attr("disabled", false);
			$("#module6").attr("disabled", false);
			if (proStruts == "总成") {
				$("#module6").attr("disabled", false);
			}
		}
	} else if (proStruts == "外购") {
		//全部不可用
		$("#module1").attr("disabled", true);
		$("#module2").attr("disabled", true);
		$("#module2_1").attr("disabled", true);
		$("#module3").attr("disabled", true);
		$("#module4").attr("disabled", true);
		$("#module5").attr("disabled", true);
		$("#module5").attr("disabled", true);
		$("#module6").attr("disabled", false);
	} else if (proStruts == "自制") {
		//全部不可用
		$("#module1").attr("disabled", true);
		$("#module2").attr("disabled", true);
		$("#module2_1").attr("disabled", true);
		$("#module3").attr("disabled", true);
		$("#module4").attr("disabled", false);
		$("#module5").attr("disabled", false);
		$("#module6").attr("disabled", false);
	}

	//关闭已打开的功能Div
	if (oldObj != null) {
		$("#module1_" + oldObj2).hide();
	}

	//显示流水卡片明细
	$("#bodyDiv").show();
	$("#showProDetail").show();
	$("#showdateil2").show();
	$("#showCardTemplate").show();
	$("#showCardIframe")
			.attr(
					"src",
					"procardTemplateSbAction_findCardTemForShow.action?id="
							+ treeNode.id);
	$("#bodyDiv").bind("click", function() {
		$("#bodyDiv").hide();
		$("#showProDetail").hide();
		$("#showCardTemplate").hide();
	});
}

//进入页面自定义点击事件
function addOnClick(){
	var showId = $("#showId").val();
	
	if(null!=showId && ""!=showId&&"${param.showId}"!="${param.id}"){
		$.ajax({
			url : 'procardTemplateSbAction_findProcardTemById.action',
			type : 'post',
			dataType : 'json',
			data : {
				id : showId
			},
			cache : true,
			success : function(treeNode) {
				var proStruts = treeNode.procardStyle;// 卡片状态(总成，零组件，原材料)
				var danjiaojian = treeNode.danjiaojian;// 卡片状态(总成，零组件，原材料)
				var productStyle = treeNode.productStyle;//生产类型
				var sbStatus = treeNode.sbStatus;//设变申请状态
				var bzStatus = treeNode.bzStatus;//编制状态
				var bomApplyStatus = treeNode.bomApplyStatus;//编制状态
				var tq = treeNode.tq;
				$("#module21").hide();
				$(".productStyle").empty();
				$(".productStyle").append("<option value='"+productStyle+"'>"+productStyle+"</option>");
				if (proStruts == "总成"
						&& (bomApplyStatus == null || (!bomApplyStatus == "未审批"
								&& !bomApplyStatus == "审批中" && !bomApplyStatus == "同意"))) {
					$("#module15").attr("disabled", false);
				} else {
					$("#module15").attr("disabled", true);
				}
				if(proStruts == "总成"&&tq=="yes"){
					$("#module21").show();
				}
				if (bomApplyStatus == "未审批" || bomApplyStatus == "审批中"
						|| bomApplyStatus == "同意") {
					$("#module16").show();
				} else {
					$("#module16").hide();
				}
				$("#module1_12").hide();
				if(productStyle=="试制"){
					$("#module7").show();
				}else{
					$("#module7").hide();
				}
				//工序赋值
				$("#epId2").val(treeNode.epId2);
				$("#cardId").val(treeNode.id);
				$("#mfatherId").val(treeNode.id);
				$("#uId").val(treeNode.id);
				$("#newMarkId").val(treeNode.markId);
				$("#oldMarkId").empty();
				$("#oldMarkId").append(treeNode.markId);
				showDiv();//页面内容清空
				//显示添加选项的页面
				$("#selectDiv").show();
				//var e = event || window.event;
				//var scrollTop = getScrollTop();//获取滚动条离顶部距离
				//var mouseLeft = e.clientX + 30;
				var mouseLeft = 30;
				var mouseTop = 100;//e.clientY - 40 + scrollTop;
				//if (mouseTop < 0) {
				//	mouseTop = 0;
				//}
				//显示悬浮窗样式的项目
				$("#showProDetail").css( {
					"top" : mouseTop,
					"left" : mouseLeft
				});
			
				if (proStruts == "总成" && productStyle == "试制") {
					$("#module9").show();
				} else {
					$("#module9").hide();
				}
				if (proStruts == "总成") {
					$("#module17").show();
					$("#module12").show();
					$("#module18").show();
				} else {
					$("#module17").hide();
					$("#module12").hide();
					$("#module18").show();
				}
			if (proStruts == "总成" || proStruts == "自制") {
					//零组件赋值
					$("#rootId").val(treeNode.rootId);
					$("#fatherId").val(treeNode.id);
					$("#belongLayer").val(treeNode.belongLayer + 1);
					//外购件赋值
					$("#wrootId").val(treeNode.rootId);
					$("#wfatherId").val(treeNode.id);
					$("#wbelongLayer").val(treeNode.belongLayer + 1);
					//原材料赋值
					$("#yrootId").val(treeNode.rootId);
					$("#yfatherId").val(treeNode.id);
					$("#ybelongLayer").val(treeNode.belongLayer + 1);
					if (danjiaojian == "单交件") {
						//全部不可用
						$("#module1").attr("disabled", true);
						$("#module2").attr("disabled", true);
						$("#module2_1").attr("disabled", true);
						$("#module3").attr("disabled", true);
						$("#module4").attr("disabled", false);
						$("#module5").attr("disabled", false);
						$("#module6").attr("disabled", false);
					} else {
						//全部可用
						$("#module1").attr("disabled", false);
						$("#module2").attr("disabled", false);
						$("#module2_1").attr("disabled", false);
						$("#module3").attr("disabled", false);
						$("#module4").attr("disabled", false);
						$("#module5").attr("disabled", false);
						$("#module6").attr("disabled", false);
						if (proStruts == "总成") {
							$("#module6").attr("disabled", false);
						}
					}
				} else if (proStruts == "外购") {
					//全部不可用
					$("#module1").attr("disabled", true);
					$("#module2").attr("disabled", true);
					$("#module3").attr("disabled", true);
					$("#module4").attr("disabled", true);
					$("#module5").attr("disabled", true);
					$("#module5").attr("disabled", true);
					$("#module6").attr("disabled", false);
				} else if (proStruts == "自制") {
					//全部不可用
					$("#module1").attr("disabled", true);
					$("#module2").attr("disabled", true);
					$("#module2_1").attr("disabled", true);
					$("#module3").attr("disabled", true);
					$("#module4").attr("disabled", false);
					$("#module5").attr("disabled", false);
					$("#module6").attr("disabled", false);
				}
			
				//关闭已打开的功能Div
				if (oldObj != null) {
					$("#module1_" + oldObj2).hide();
				}
			
				//显示流水卡片明细
				$("#bodyDiv").show();
				$("#showProDetail").show();
				$("#showdateil2").show();
				$("#showCardTemplate").show();
				$("#showCardIframe")
						.attr(
								"src",
								"procardTemplateSbAction_findCardTemForShow.action?id="
										+ treeNode.id);
				$("#bodyDiv").bind("click", function() {
					$("#bodyDiv").hide();
					$("#showProDetail").hide();
					$("#showCardTemplate").hide();
				});
			}
			
		});
	}
	
}

//添加组件/原材料流水卡片
function submitForm(obj,formId) {
	if(formId=="lingForm"){//组合
		var makrId=$("#zuhemarkId").val();
		var proName=$("#zuheproName").val();
		var corrCount=$("#zuhecorrCount").val();
		var lingliaostatus=$("#zuhelingliaostatus").val();
		var trademark=$("#zuhetrademark").val();
		if(makrId==null||makrId==""){
			alert("请填写件号!");
			return false;
		}
		if(proName==null||proName==""){
			alert("请填写名称!");
			return false;
		}
		if(corrCount==null||corrCount==""){
			alert("请填写组合与上层的比例!");
			return false;
		}
		//if((lingliaostatus==null||lingliaostatus==""||lingliaostatus=="是")&&trademark.length>0){
		//	var specification=$("#zuhespecification").val();
		//	if(specification==null||specification==""){
		//		alert("该组合需领料且有原材料，请填写原材料规格!");
		//		return false;
		//	}
		//	var quanzi1=$("#zuhequanzi1").val();
		//	var quanzi2=$("#zuhequanzi2").val();
		//	if(quanzi1==null||quanzi1==""||quanzi2==null||quanzi2==""){
		//		alert("该组合需领料且有原材料，请填写原材料的权值比例!");
		//		return false;
		//	}
		//}
	}
	$(obj).attr("disabled","disabled");
	$.ajax( {
		type : "POST",
		url : "procardTemplateSbAction_addProcardTemplate.action",
		dataType : "json",
		data : $("#" + formId).serialize(),
		success : function(data) {
			if (data.success) {
				alert("添加成功!");
				$(obj).removeAttr("disabled");
				loadTree();
			} else {
				$(obj).removeAttr("disabled");
				alert(data.message);
			}
		}
	});
}

//显示卡片已有工序
function showProcess() {  
	$
			.ajax( {
				type : "POST",
				url : "procardTemplateSbAction_findProcessByFkId.action",
				dataType : "json",
				data : {
					id : $("#cardId").val()
				},
				success : function(msg) {
					$("#ProcessTab").empty();
					$("#ProcessTab")
							.append(
									"<tr><th colspan='10'>已有工序(<a onclick='toAddProcess()'>添加/插入工序</a>)(<a onclick='deleteAllProcess()'>清空工序</a>)</th></tr>"
											+ "<tr><th>工序号</th><th>名称</th><th>总节拍(s)</th><th>生产类型</th><th>特殊工序</th>"
											+ "<th>是否并行</th><th>是否首检</th><th>添加人</th><th>添加时间</th><th>操作</th></tr>");
					var maxProcessNO = 5;//最大工序号
					var i=0;
					$(msg)
							.each(
									function() {
										var isSpecial = $(this).attr('isSpecial');
										var applySpecial="";
										if(isSpecial!=null&&isSpecial=="审批中"){
											applySpecial='<a href="CircuitRunAction_findAduitPage.action?id='+ $(this).attr('epId')+'">审批动态</a>/';
										}else if(isSpecial==null||isSpecial.length==0||isSpecial=="普通"){
											isSpecial="普通";
											applySpecial='<a href="javascript:;" onclick=\'applySpecial('
															+ $(this).attr('id')
															+ ','+i+');\'>申请多工序并行</a>/';
										}
										isSpecial = "<label id='isSpecial"+i+"'>"+isSpecial+"</label>"
										var zjp = parseFloat($(
																		this)
																		.attr(
																				'opshebeijiepai')
																		+ $(
																				this)
																				.attr(
																						'opcaozuojiepai')
																		+ $(
																				this)
																				.attr(
																						'gzzhunbeijiepai')
																		* $(
																				this)
																				.attr(
																						'gzzhunbeicishu'));
										zjp =	Math.round(zjp*10000)/10000;
										var html='<tr align="center"  onmouseover=chageBgcolor(this)  onmouseout=outBgcolor(this,"#FFFFFF") ><td>'
																+ $(this)
																		.attr(
																				'processNO')
																+ '</td><td>'
																+ $(this)
																		.attr(
																				'processName')
																+ '</td><td>'
																+zjp
																+ '</td><td>'
																+ $(this)
																		.attr(
																				'productStyle')
																+ '</td><td>'
																+ isSpecial
																+ '</td><td>'
																+ $(this)
																		.attr(
																				'processStatus')
																+ '</td><td>'
																+ $(this)
																		.attr(
																				'zjStatus')
																+ '</td><td>'
																+ $(this)
																		.attr(
																				'addUser')
																+ '</td><td>'
																+ $(this)
																		.attr(
																				'addTime')
																+ '</td><td>' 
																+applySpecial 
																+'<a href="javascript:;" onclick="showProcessTz('
																+ $(this).attr(
																		'id')
																+ ');">图纸</a>/<a href="javascript:;" onclick="showProcessForSb('
																+ $(this).attr(
																		'id')//  onmouseout="outBgcolor(this,'#e6f3fb')" onmouseover="alert(000000000)"
																+ ');">修改</a>'
																//+ '/<a href="javascript:;" onclick= "showWgProcard('+$(this).attr('id')+');">关联零件</a>' 
																+ '/<a href="javascript:;" onclick="deleteProcess('
																+ $(this).attr(
																		'id')
																+ ');">删除</a></td></tr>';
										$("#ProcessTab")
												.append(html);
																																
																
										
										var processStatus = $(this).attr(
												'processStatus');
										if (processStatus == "no") {
											$("#parallelId").val("");
										} else {
											$("#parallelId").val(
													$(this).attr('id'));
										}
										maxProcessNO = parseFloat($(this).attr(
												'processNO')) + 5;
										i++;
									});
					$("#processNO").val(maxProcessNO);//计算下一个工序号是多少，方便填写
					$("#processName").select();
				}
			});
}
//添加工序
function submitForm2(formId) {
	if ($("#processName").val() == "") {
		alert("请填写工序名称!");
		$("#processName").select();
	} else {
		if ($("#processStatus").val() == "no") {
			$("#parallelId").val("");
		}
		$.ajax( {
			type : "POST",
			url : "procardTemplateSbAction_addProcessTemplate.action",
			dataType : "json",
			data : $("#" + formId).serialize(),
			success : function(msg) {
				if (msg) {
					alert("添加成功!");
					showProcess();
					$("#processNO").val(parseFloat($("#processNO").val()) + 5);
					$("#processName").val("");
					$("#processName").select();

				} else {
					alert("添加失败!");
				}
			}
		});
	}
}

//删除流水卡片
function delProCard(obj) {
	if (window.confirm('确定要删除本卡片吗?此操作将会删除该流水卡片下属的所有信息!')) {
		$(obj).attr("disabled","disabled");
		$(obj).val("正在删除中,请耐心等待....");
		
		$.ajax( {
			type : "POST",
			url : "procardTemplateSbAction_delProcard.action",
			dataType : "json",
			data : {
				id : $("#cardId").val()
			},
			success : function(msg) {
				if (msg=="删除成功!"||msg=="true") {
					alert("删除成功!");
					$(obj).val("删除本卡片");
					$(obj).removeAttr("disabled");
					showDiv();//页面内容清空
					$("#selectDiv").hide();
					loadTree();//重新加载树形
					} else {
					alert(msg);
			}
	}
		});
	}
}

//精益计算
function jingyiJisuan() {
	$.ajax( {
		type : "POST",
		url : "procardTemplateSbAction_jingyiJisuan.action",
		dataType : "json",
		data : {
			id : $("#cardId").val()
		},
		success : function(msg) {
			alert(msg);
		}
	});
}

//页面内容清空
function showDiv() {
	//清空工序table
	$("#ProcessTab").empty();
	//隐藏各个添加的详细页面
	$("#showCardTemplate").hide();
	$("#processDiv").hide();
	$("#lingDiv").hide();
	$("#yuanDiv").hide();
}

//显示工序详细
function showProcessForSb(id,ProcessNO ) {
	$("#showProcess").attr("src",
			"procardTemplateSbAction_showProcess.action?id=" + id);
	chageDiv('block');

}
//显示工序工序关联外购件
function showWgProcard(id){
	$("#showProcess").attr("src",
			"procardTemplateSbAction_findwgProcard.action?id=" + id);
	chageDiv('block');
}
//显示工序图纸
function showProcessTz(id) {
	$("#showProcess").attr("src",
			"procardTemplateSbAction_showProcessTz.action?id=" + id);
	chageDiv('block');

}
//申请特殊工序
function applySpecial(id,index) {
if (window.confirm('确定要将此工序申请为特殊工序吗?')) {
		$.ajax( {
			type : "POST",
			url : "procardTemplateSbAction_applySpecial.action",
			dataType : "json",
			data : {
				id : id
			},
			success : function(msg) {
				if (msg=="true") {
					$("#isSpecial"+index).empty();
					$("#isSpecial"+index).html("申请中");
				} else {
					alert(msg);
				}
			}
		});
	}
}
//显示工序质检；
function showProcessZJ(id,ProcessNO ){
	$("#showProcess").attr("src",
		"OsTemplate_addInput.action?id="+id+"&gongxuNum="+ProcessNO);


	chageDiv('block');
}
function deleteProcess(id) {
	if (window.confirm('确定要删除本工序吗?')) {
		$.ajax( {
			type : "POST",
			url : "procardTemplateSbAction_deleteProcess.action",
			dataType : "json",
			data : {
				id : id
			},
			success : function(msg) {
				if (msg.success) {
					alert(msg.message);
					showProcess();//显示该流水卡片已有工序
			$("#processNO").val(parseFloat($("#processNO").val()) + 5);
			$("#processName").val("");
			$("#processName").select();
		} else {
			alert(msg.message);
		}
	}
		});
	}
}

$(function() {
	getUnit("danwei2");
	getUnit("danwei3");
	getUnit("danwei4");
})
</script>

		<script language="javascript">
function initfz(obj1, obj2, num1, num2) {
	count_seach++;
	var shortname = document.getElementById(obj1);
	var showAll = document.getElementById(obj2);
	showAll.style.top = shortname.offsetTop + num1;
	showAll.style.left = shortname.offsetLeft + num2;
	showAll.style.visibility = "visible";
}
//ajax获取所有的类似的全称
function getAllNames(obj1, obj2, obj3, obj4) {
	$
			.ajax( {
				type : "POST",
				url : "procardTemplateSbAction_getAllNames.action",
				dataType : "json",
				data : {
					markId : $("#" + obj1).val(),
					procardStyle : obj4
				},
				success : function(data) {
					$("#" + obj2).empty();
					$("#" + obj3).val(0);
					$(data)
							.each(
									function() {
										var markid = $(this).attr('markId')
												.replace(
														$("#" + obj1).val(),
														"<font color='red'>"
																+ $("#" + obj1)
																		.val()
																+ "</font>");
										$("#" + obj2)
												.append(
														"<div onmouseover='ondiv(this)' onmouseout='outdiv(this)' onclick=selectdiv(this,'"
																+ obj1
																+ "','"
																+ obj2
																+ "','"
																+ obj3
																+ "') align='left' data='"
																+ $(this).attr(
																		'id')
																+ "'>"
																+ markid
																+ ","
																+ $(this)
																		.attr(
																				'procardStyle')
																+ "<span style='display: none;'>"
																+ $(this)
																		.attr(
																				'markId')
																+ "</span></div>");
									});
				}
			});
}

function copyProcard(id, obj1, obj2) {
	if ($("#" + obj1).val() == "") {
		alert("您选中的模板不存在，添加失败！");
		return;
	}
	$.ajax( {
		type : "POST",
		url : "procardTemplateSbAction_copyProcard.action",
		dataType : "json",
		data : {
			id : $("#mfatherId").val(),
			id2 : $("#" + obj2).val()
		},
		success : function(data) {
			if (data.success) {
				loadTree();
			}
			if (data.message != null) {
				alert(data.message);

			}

		}
	});
}
function updateProcard(obj) {
	if (!window.confirm("您将更新所有同件号的模板和状态为初始或者已发卡的流水卡,是否继续更新！")) {
		return false;
	}
	var id = $("#uId").val();
	if (id == "") {
		alert("您选中的模板不存在，添加失败！");
		return;
	}
	$(obj).attr("disabled", "disabled");
	$(obj).val("更新中....");
	$.ajax( {
		type : "POST",
		url : "procardTemplateSbAction_updateProcard.action",
		dataType : "json",
		data : {
			id : id
		},
		success : function(data) {
			if (data.message != null) {
				alert(data.message);
				$(obj).removeAttr("disabled");
				$(obj).val("更新流水卡");
			}
		}
	});
}
/**
 * 显示添加工序弹框
 */
function toAddProcess() {
	var id = $("#cardId").val();
	$("#showProcess").attr("src",
			"procardTemplateSbAction_toAddProcess.action?id=" + id);
	chageDiv('block');
}
function deleteAllProcess() {
	var id = $("#cardId").val();
	$
			.ajax( {
				type : "POST",
				url : "procardTemplateSbAction_deleteAllProcess.action",
				data : {
					id : id
				},
				dataType : "json",
				success : function(msg) {
					if (msg.success) {
						alert("已清空!");
						$("#ProcessTab").empty();
						$("#ProcessTab")
								.append(
										"<tr><th colspan='8'>已有工序(<a onclick='toAddProcess()'>添加工序</a>)(<a onclick='deleteAllProcess()'>清空工序</a>)</th></tr>"
												+ "<tr><th>工序号</th><th>名称</th><th>总节拍(s)</th><th>生产类型</th><th>特殊工序</th>"
												+ "<th>是否并行</th><th>是否首检</th><th>操作</th></tr>");
					} else {
						alert(msg.message);
					}
				}
			});
}
//查询所有工序
function getProcess() {
	$.ajax( {
		type : "POST",
		url : "processGzstoreAction_getProcessGzstoreListAllForSelect.action",
		dataType : "json",
		success : function(msg) {
			if (msg.success) {
				//$("#processName").empty();
				$("#processName").append("<option value=''></option>");
				$.each(msg.data, function(i, n) {
					$("#processName").append(
							"<option value='" + n.id + "'>" + n.processName
									+ "</option>");
				});
			} else {
				alert(msg.message);
			}
		}
	});
}
getProcess();
function updateMarkId() {
	var oldMarkId = $("#oldMarkId").html();
	var newMarkId = $("#newMarkId").val();
	if (newMarkId == null || newMarkId == "") {
		alert("请输入新件号");
		return false;
	}
	if (oldMarkId == newMarkId) {
		alert("原新件号请不要一致！");
		return false;
	}
	$.ajax( {
		type : "POST",
		url : "procardTemplateSbAction_updateMarkId.action",
		dataType : "json",
		data : {
			id : $("#cardId").val(),
			markId : newMarkId
		},
		success : function(data) {
			alert(data.message);
			if (data.success) {
				loadTree();
				$("#oldMarkId").empty();
				$("#oldMarkId").append(newMarkId);
				$("#showCardTemplate").show();
				$("#showCardIframe").attr(
						"src",
						"procardTemplateSbAction_findCardTemForShow.action?id="
								+ $("#cardId").val());
			}
		}
	})
}
function changeTolp() {
	var lpMarkId = $("#lpMarkId").val();
	$("#dd9").show();
	$.ajax( {
		type : "POST",
		url : "procardTemplateSbAction_changeTolp.action",
		dataType : "json",
		data : {
			id : $("#cardId").val(),
			markId : lpMarkId
		},
		success : function(data) {
			alert(data.message);
			$("#dd9").hide();
			loadTree();
			$("#oldMarkId").empty();
			$("#oldMarkId").append(newMarkId);
			$("#showCardTemplate").show();
			$("#showCardIframe").attr(
					"src",
					"procardTemplateSbAction_findCardTemForShow.action?id="
							+ $("#cardId").val());
		}
	})
}
function showSonCard() {
	var id = $("#cardId").val();
	$("#showProcess").attr("src",
			"procardTemplateSbAction_showSonCard.action?id=" + id);
	chageDiv('block');

}
</script>
		<!-- 外购件原材料模糊搜索js -->
		<SCRIPT type="text/javascript">
		function hidediv(allname) {
	count_seach--;
	if (count_seach == 0) {
		var showAll = document.getElementById(allname);
		showAll.style.visibility = "hidden";
	}

}

function init(shortName,allname) {
	count_seach++;
	var shortnamewgj = document.getElementById(shortName);
	var showAllwgj = document.getElementById(allname);
	showAllwgj.style.top = getTop(shortnamewgj) + 20;
	showAllwgj.style.left = getLeft(shortnamewgj);
	showAllwgj.style.visibility = "visible";
}
function selectdiv(obj,obj1,obj2,obj3){
	var html=$(obj).find("span").html();
	$("#"+obj1).val(html);
	var data = $(obj).attr('data');
	$("#"+obj3).val(data);
	var showAll=document.getElementById(obj2); 
	   showAll.style.visibility = "hidden";
	   if(obj3!="shortnameId"){
		   showProcardTemplate(data,obj1)
	   }
	   if(obj1 == "zuhemarkId"){
		   $("#zuhemarkIddiv").html('<input type="button" value="复制" onclick=copyProcard("${param.id}","'+obj1+'","'+obj3+'")>');
	   }else if(obj1 == "zizmarkId"){
		   $("#zizmarkIddiv").html('<input type="button" value="复制" onclick=copyProcard("${param.id}","'+obj1+'","'+obj3+'")>');
	   }
}
function showProcardTemplate(id,obj){
	if(obj == "zuhemarkId"){
		obj="zuhe";
	}else if(obj =="zizmarkId"){
		obj = "ziz";
	}
	$.ajax({
		type : "POST",
		url : "procardTemplateSbAction_findProcardTemById.action",
		dataType : "json",
		data : {id:id},
		success : function(pt) {
			if(pt!=null){
				$("#"+obj+"proName").val(pt.proName);
				$("#"+obj+"anBenNumber").val(pt.banBenNumber);
				$("#"+obj+"loadMarkId").val(pt.loadMarkId);
				$("#"+obj+"carStyle").val(pt.carStyle);
				$("#"+obj+"corrCount").val(pt.corrCount);
				$("#"+obj+"safeCount").val(pt.safeCount);
				$("#"+obj+"lastCount").val(pt.lastCount);
				$("#"+obj+"procardStyle").val(pt.procardStyle);
				$("#"+obj+"unit").val(pt.unit);
				$("#"+obj+"productStyle").val(pt.productStyle);
				$("#"+obj+"status").val(pt.status);
				$("#"+obj+"lingliaostatus").val(pt.lingliaostatus);
				$("#"+obj+"trademark").val(pt.trademark);
				$("#"+obj+"specification").val(pt.specification);
				$("#"+obj+"bili").val(pt.bili);
				$("#"+obj+"yuanUnit").val(pt.yuanUnit);
				$("#"+obj+"wgType").val(pt.wgType);
				$("#"+obj+"quanzi1").val(pt.quanzi1);
				$("#"+obj+"quanzi2").val(pt.quanzi2);
				$("#"+obj+"jgyl").val(pt.jgyl);
				$("#"+obj+"luhao").val(pt.luhao);
				$("#"+obj+"number").val(pt.number);
				$("#"+obj+"actualFixed").val(pt.actualFixed);
				$("#"+obj+"status").val(pt.status);
				$("#"+obj+"numb").val(pt.numb);
				$("#"+obj+"fachuDate").val(pt.fachuDate);
				$("#"+obj+"pageTotal").val(pt.pageTotal);
				$("#"+obj+"bianzhiId").val(pt.bianzhiId);
				$("#"+obj+"jiaoduiId").val(pt.jiaoduiId);
				$("#"+obj+"shenheId").val(pt.shenheId);
				$("#"+obj+"pizhunId").val(pt.pizhunId);
				$("#"+obj+"remark").val(pt.remark);
			}
		}
		
	});
}

//ajax获取所有的类似的全称
function getAllNameswgj() {
	if($("#shortnamewgj").val()==null||$("#shortnamewgj").val()==""){
		$("#showAllwgj").empty();
		return;
	}
	$
			.ajax( {
				type : "POST",
				url : "yuanclAndWaigjAction!getAllNames.action",
				dataType : "json",
				data : {
					'yuanclAndWaigj.markId' : $("#shortnamewgj").val(),
					'yuanclAndWaigj.clClass' : '外购件'
				},
				success : function(data) {
					$("#showAllwgj").empty();
					$(data).each(
									function() {
										var markId = $(this)
												.attr('markId')
												.replace(
														$("#shortnamewgj").val(),
														"<font color='red'>"
																+ $("#shortnamewgj").val()
																+ "</font>");
										$("#showAllwgj")
												.append(
														"<div onmouseover='ondiv(this)' onmouseout='outdiv(this)' onclick='selectdivwgj(this)' align='left'>"
																+ markId
																+ ":"
																+ $(this).attr('name')
																+":"
																+$(this).attr('unit')
																+"<span style='visibility: hidden;'>"
																+ $(this).attr('markId')
																+ ":"
																+ $(this).attr('name')
																+":"
																+$(this).attr('unit')
																+"</span>"
																+ "</div>");
									});
				}
			});
}
function selectdivwgj(obj){
	var html=$(obj).find("span").html();
	var showAllwgj=document.getElementById("showAllwgj"); 
	showAllwgj.style.visibility = "hidden";
	var htmls=html.split(":");
	$("#shortnamewgj").val(html);
	$("#wajmarkId").val(htmls[0]);
	$("#wajproName").val(htmls[1]);
	$("#danwei2").val(htmls[2]);
}
		
//ajax获取所有的类似的全称
function getAllNamesycl() {
	if($("#shortnameycl").val()==null||$("#shortnameycl").val()==""){
		$("#showAllycl").empty();
		return;
	}
	$
			.ajax( {
				type : "POST",
				url : "yuanclAndWaigjAction!getAllNames.action",
				dataType : "json",
				data : {
					'yuanclAndWaigj.trademark' : $("#shortnameycl").val(),
					'yuanclAndWaigj.clClass' : '原材料'
				},
				success : function(data) {
					$("#showAllycl").empty();
					$(data).each(
									function() {
										var trademark = $(this)
												.attr('trademark')
												.replace(
														$("#shortnameycl").val(),
														"<font color='red'>"
																+ $("#shortnameycl").val()
																+ "</font>");
										$("#showAllycl")
												.append(
														"<div onmouseover='ondiv(this)' onmouseout='outdiv(this)' onclick='selectdivycl(this)' align='left'>"
																+ trademark
																+ ":"
																+ $(this).attr('specification')
																+":"
																+$(this).attr('unit')
																+"<span style='visibility: hidden;'>"
																+ $(this).attr('trademark')
																+ ":"
																+ $(this).attr('specification')
																+":"
																+$(this).attr('unit')
																+"</span>"
																+ "</div>");
									});
				}
			});
}

function selectdivycl(obj){
	var html=$(obj).find("span").html();;
	var showAll=document.getElementById("showAllycl"); 
	showAll.style.visibility = "hidden";
	var htmls=html.split(":");
	$("#shortnameycl").val(html);
	$("#trademark").val(htmls[0]);
	$("#specification").val(htmls[1]);
	$("#unit4").val(htmls[2]);
}
//修改外购件是否需要工序
function changeneedProcess(obj){
	if(obj=="yes"){
		$("#safetr").show();
		$("#lasttr").show();
	}else{
		$("#safetr").hide();
		$("#lasttr").hide();
	}
	
}
function getGyPeople(type){
		$
			.ajax( {
				type : "POST",
				url : "procardTemplateSbAction_getGyPeople.action?pageStatus="+type,
				dataType : "json",
				success : function(data) {
					$(data).each(
									function() {
										if(type=="bz"){
										    $(".bianzhi")
												.append("<option value='" + $(this).attr('userId')+ "'>"+ $(this).attr('userName') +"</option>");
										}else if(type=="jd"){
											$(".jiaodui")
												.append("<option value='" + $(this).attr('userId')+ "'>"+ $(this).attr('userName') +"</option>");
										}else if(type=="sh"){
											$(".shenhe")
												.append("<option value='" + $(this).attr('userId')+ "'>"+ $(this).attr('userName') +"</option>");
										}else if(type=="pz"){
											$(".pizhun")
												.append("<option value='" + $(this).attr('userId')+ "'>"+ $(this).attr('userName') +"</option>");
											
										}
									});
				}
			});
}
$(document).ready(function(){
	getUnit("zuheunit");
	$(".bianzhi").append("<option value='${Users.id}'>${Users.name}</option>");
	$
			.ajax( {
				type : "POST",
				url : "procardTemplateSbAction_getSpjdCount.action",
				dataType : "json",
				success : function(data) {
					if(data=="2"){
						$(".jdtr").hide();
						$(".shtr").hide();
						getGyPeople("bz");
						getGyPeople("pz");
					}else if(data=="3"){
						$(".shtr").hide();
						getGyPeople("bz");
						getGyPeople("jd");
						getGyPeople("pz");
					}else{
						getGyPeople("bz");
						getGyPeople("jd");
						getGyPeople("sh");
						getGyPeople("pz");
					}
				}
			});
	
	
})
function reviewBom(){
	window.open("procardTemplateSbAction_reviewBom.action?id=${param.id}");  
}
function showCardTz(){
	var id=$("#cardId").val();
	$("#showProcess").attr("src",
			"procardTemplateSbAction_showCardTz.action?id=" + id+"&tag=${param.pageStatus}");
	chageDiv('block');
}
function selectYclAndWgj(type){
	$("#showProcess").attr("src",
			"procardTemplateGyAction_showYclAndWgj.action?type="+type);
	chageDiv('block');
}
function noZhongwen(obj,obj1,obj2,obj3,obj4){
	getAllNames(obj1,obj2,obj3,obj4);
	if (escape(obj.value).indexOf( "%u" )>=0){
	  obj.value="";
	  alert( "不能包含中文!" );
	}
}
function checkSelf(){
	$("#showProcess").attr("src",
			"procardTemplateSbAction_checkSelf.action?id=${param.id}");
	chageDiv('block');
}
function checkAndUpdateTz(){
		$("#module1_12").show();
}
function tzdr(){
	if(window.confirm("是否导入工艺图纸内容!")){
		$("#module1_12_son").show();
		$("#drh3").html("<font color='red'>正在导入图纸请耐心等待.............</font>");
		$("#drh3").show();
		$("#tzdrdbtn").attr("disabled","disabled");
		$
			.ajax( {
				type : "POST",
				url : "procardTemplateSbAction_checkAndUpdateTz.action",
				data : {
					id:${param.id}
				},
				dataType : "json",
				success : function(data) {
					$("#drh3").html("");
					$("#drh3").html(data);
					$("#jdh3").hide();
					$("#jdh4").hide();
					$("#jdh5").hide();
					$("#wgjgx5").hide();
					$("#tzdrdbtn").removeAttr("disabled");
				}
			});
	}
}
function tzjd(){
	if(window.confirm("是否工艺图纸名称加点!")){
		$("#module1_12_son").show();
		$("#jdh3").html("<font color='red'>正在一键加点,请耐心等待.............</font>");
		$("#jdh3").show();
		$("#tzjdbtn").attr("disabled","disabled");
		$
			.ajax( {
				type : "POST",
				url : "procardTemplateSbAction_updateUnuploadTzname.action",
				data : {
					id:${param.id}
				},
				dataType : "json",
				success : function(data) {
					$("#jdh3").html("");
					$("#jdh3").html(data);
					$("#drh3").hide();
					$("#jdh4").hide();
					$("#jdh5").hide();
					$("#wgjgx5").hide();
					$("#tzjdbtn").removeAttr("disabled");
				}
			});
	}
}
function tzgx(){
	if(window.confirm("是否一键导入工序?")){
		$("#module1_12_son").show();
		$("#jdh4").show();
		$("#jdh4").html("<font color='red'>正在一键导入工序,请耐心等待.............</font>");
		$("#gxjdbtn").attr("disabled","disabled");
		$
			.ajax( {
				type : "POST",
				url : "procardTemplateSbAction_daoRuProcessTempalte.action",
				data : {
					id:${param.id}
				},
				dataType : "json",
				success : function(data) {
					$("#jdh4").html("");
					$("#jdh4").html(data);
					$("#drh3").hide();
					$("#jdh3").hide();
					$("#jdh5").hide();
					$("#wgjgx5").hide();
					$("#gxjdbtn").removeAttr("disabled");
				}
			});
	}
}
function wgjgx(){
	if(window.confirm("是否关联外购件工序?")){
		$("#module1_12_son").show();
		$("#jdh5").show();
		$("#jdh5").html("<font color='red'>正在关联外购件工序,请耐心等待.............</font>");
		$("#wgjGxbtn").attr("disabled","disabled");
		$
			.ajax( {
				type : "POST",
				url : "procardTemplateSbAction_processAndWgProcard.action",
				data : {
					id:${param.id}
				},
				dataType : "json",
				success : function(data) {
					$("#jdh5").html("");
					$("#jdh5").html(data);
					$("#drh3").hide();
					$("#jdh3").hide();
					$("#jdh5").hide();
					$("#wgjgx5").hide();
					$("#wgjGxbtn").removeAttr("disabled");
				}
			});
	}
}
function processAndWgProcard(){
	if(window.confirm("是否一键关联工序外购件?")){
		$("#module1_12_son").show();
		$("#wgjgx5").show();
		$("#wgjgx5").html("<font color='red'>一键关联工序外购件,请耐心等待.............</font>");
		$("#gxjdbtn").attr("disabled","disabled");
		$
			.ajax( {
				type : "POST",
				url : "procardTemplateSbAction_processAndWgProcard.action",
				data : {
					id:${param.id}
				},
				dataType : "json",
				success : function(data) {
					$("#wgjgx5").html("");
					$("#wgjgx5").html(data);
					$("#drh3").hide();
					$("#jdh3").hide();
					$("#jdh4").hide();
					$("#wgjgx4").removeAttr("disabled");
				}
			});
	}
}
function moveStatus(){
	var id=$("#cardId").val();
	$("#showProcess").attr("src",
			"procardTemplateSbAction_moveStatus.action?id="+id);
	chageDiv('block');
}
function download(){
	var id=$("#cardId").val();
	window.location.href = "procardTemplateSbAction_findPicbyMarkId.action?id="
			+ id ;
}
function changeLogshow(){
	var id=$("#cardId").val();
	$("#showProcess").attr("src",
			"procardTemplateSbAction_changeLogshow.action?id="+id);
	chageDiv('block');
}
function BomTreeSpdt(){
	var id=$("#epId2").val();
	$("#showProcess").attr("src",
			"CircuitRunAction_findAduitPage.action?id="+id);
	chageDiv('block');
}
function BomTreeSpzp(){
	var id=$("#cardId").val();
	$("#showProcess").attr("src",
			"procardTemplateSbAction_tozhipaiBOmTree.action?id="+id);
	chageDiv('block');
}
function applyBomTree(){
	if(confirm("您将申请BOM结构审批是否继续?")){
	var id=$("#cardId").val();
	$("#module15").attr("disabled","disabled");
	$
			.ajax( {
				type : "POST",
				url : "procardTemplateSbAction_applyBomTree.action",
				data : {
					id:id
				},
				dataType : "json",
				success : function(data) {
					alert(data.message);
				}
			});
	}
}
function applySb(){
	if(confirm("您将申请设变是否继续?")){
	var id=$("#cardId").val();
	//$("#module14").attr("disabled","disabled");
	$
			.ajax( {
				type : "POST",
				url : "procardTemplateSbAction_applySb.action",
				data : {
					id:id
				},
				dataType : "json",
				success : function(data) {
					alert(data.message);
				}
			});
	}
}

<%--function yinshen(obj){--%>
<%--	alert($("#"+obj).attr("display"));--%>
<%--}--%>
function daoruson(){
	var id=$("#cardId").val();
	$("#showProcess").attr("src",
			"procardTemplateSbAction_todaoruson.action?id="+id);
	chageDiv('block');
}	

function gxtbsc(obj){
	if (window.confirm('确定要同步工序到生产吗?')) {
		$(obj).attr("disabled","disabled");
		$(obj).val("正在同步中,请耐心等待....");
		
		$.ajax( {
			type : "POST",
			url : "procardTemplateSbAction_gxtbsc.action",
			dataType : "json",
			data : {
				id : $("#cardId").val()
			},
			success : function(msg) {
				if (msg=="同步成功!"||msg=="true") {
					alert("同步成功!");
					$(obj).val("工序同步生产");
					$(obj).removeAttr("disabled");
					showDiv();//页面内容清空
					$("#selectDiv").hide();
					loadTree();//重新加载树形
					} else {
					alert(msg);
			}
	}
		});
	}
}
function daochuBOM(obj){
	var id=$("#cardId").val();
	window.location.href="procardTemplateSbAction_daoChuBom.action?id="+id;
}
		</SCRIPT>
	</body>
</html>