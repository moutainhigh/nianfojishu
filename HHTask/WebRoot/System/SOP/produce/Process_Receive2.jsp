<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib uri="/WEB-INF/fenye.tld" prefix="fenye"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
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
		<link rel="stylesheet" type="text/css"
			href="<%=basePath%>/css/button.css" />
		<script type="text/javascript" src="<%=basePath%>/js/JsBarcode.all.js">
</script>
		<script src="<%=basePath%>/javascript/radialIndicator.js">
</script>
		<script type="text/javascript"
			src="<%=basePath%>/javascript/popwin.js">
</script>
	</head>
	<style type="text/css">
td:hover .qs_ul {
	display: block;
}

.qs_ul {
	display: none;
	border: 1px solid #999;
	list-style: none;
	margin: 0;
	margin-left: -150;
	padding: 0;
	position: absolute;
	width: 0px;
	background: #CCC;
	color: red;
}

.ztree li a {
	color: #fff;
}
</style>
	<body>
		<div id="bodyDiv" align="center" class="transDiv"
			onclick="chageDiv('none');">
		</div>
		<div id="contentDiv"
			style="position: absolute; z-index: 255; width: 900px; display: none;"
			align="center">
			<div id="closeDiv"
				style="position: relative;background: url(<%=basePath%>/images/bq_bg2.gif); width:100%;">
				<div id="zjProcessDiv" style="display: none;">
					<iframe id="showZjProcess" src="" marginwidth="0" marginheight="0"
						hspace="0" vspace="0" frameborder="0" scrolling="yes"
						style="width: 100%; margin: 0px; padding: 0px; height: 500px;"></iframe>
				</div>

			</div>
			<div id="collorProcessDiv" style="display: none;">
				<table style="width: 100%; background-color: #ffffff">
					<tr>
						<td>
							您正在领取工序:
						</td>
						<td align="right">
							<img alt="" src="<%=basePath%>/images/closeImage.png" width="30"
								height="32" onclick="chageDiv('none')">
						</td>
					</tr>
				</table>
				<div id="operatingDiv"
					style="background-color: #ffffff; width: 100%;">
					<form id="collorProcess" style="margin: 0px; padding: 0px;">
						<table id="ProcessTab" class="table">
						</table>
					</form>
				</div>
			</div>
			<div id="submitProcessDiv" style="display: none;">
				<table style="width: 100%; background-color: #ffffff">
					<tr>
						<td>
							您正在提交工序:
						</td>
						<td align="right">
							<img alt="" src="<%=basePath%>/images/closeImage.png" width="30"
								height="32" onclick="chageDiv('none');location.reload(true);">
						</td>
					</tr>
				</table>
				<div id="operatingDiv"
					style="background-color: #ffffff; width: 100%;">
					<form id="submitProcess">
						<input id="submitProId" type="hidden" name="process.id" />
						<input type="hidden" name="id" value="${procard.id}" />
						<table class="table" style="width: 40%">
							<tr>
								<th colspan="2">
									提交工序
								</th>
							</tr>
							<tr>
								<th align="right">
									提交数量:
								</th>
								<th align="left">
									<input id="subNumber" name="process.submmitCount" maxsize="100"
										onblur="numyanzheng(this,'zhengshu')"
										onkeyup="numyanzheng(this,'zhengshu')"
										onchange="changNumber()" />
									最大可提交数量
								</th>
							</tr>
							<tr>
								<th align="right">
									不合格类型
								</th>
								<th align="left">
									<input type="radio" value="零件损坏" name="breaksubmit.type"
										id="zzbreakType_1" onclick="hidProcessAndWg('1')"
										checked="checked" />
									零件损坏
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									<input type="radio" value="外购件不合格" name="breaksubmit.type"
										id="wgbreakType_1" onclick="showProcessAndWg('1','不合格数量')" />
									<span id="wgbreakType_span">外购件不合格</span>
								</th>
							</tr>
							<tr id="wgtr_1" style="display: none;">

							</tr>
							<tr id="ljbreak_tr_1">
								<th align="right">
									不合格数量:
								</th>
								<th align="left">
									<input name="process.breakCount" value="0"
										onchange="changNumber();numyanzheng(this,'zhengshu')"
										id="breakCount_1" />
								</th>
							</tr>
							<tr>
								<td colspan="2" align="center">
									辅料
									<%--							<s:if test="process.isNeedFuliao=='yes'">--%>
									<%--								<input type="radio" name='process.isNeedFuliao'--%>
									<%--									value="yes" checked="checked" onchange="changeFuliao('是')">是--%>
									<%--					  <input type="radio" name='process.isNeedFuliao'--%>
									<%--									value="no" onchange="changeFuliao('否')"> 否--%>
									<%--					 </s:if>--%>
									<%--							<s:else>--%>
									<%--								<input type="radio" name='process.isNeedFuliao'--%>
									<%--									value="yes" onchange="changeFuliao('是')">是--%>
									<%--					  <input type="radio" name='process.isNeedFuliao'--%>
									<%--									value="no" checked="checked" onchange="changeFuliao('否')"> 否--%>
									<%--					 </s:else>--%>
								</td>
							</tr>
							<tr id="fuliaoTr">
								<td colspan="2" align="center">
									<table id="fuliaoTb">
										<tr>
											<td style="border-top: 0px; border-left: 0px" align="center">
												类别
											</td>
											<td style="border-top: 0px; border-left: 0px" align="center">
												名称
											</td>
											<td style="border-top: 0px; border-left: 0px" align="center">
												规格
											</td>
											<td style="border-top: 0px; border-left: 0px" align="center">
												用量
											</td>
											<td style="border-top: 0px; border-left: 0px" align="center">
												单位
											</td>
											<td
												style="border-top: 0px; border-left: 0px; border-right: 0px"
												align="center">
												<input type="button" value="增加" onclick="addFuliaoLine()">
											</td>
										</tr>
										<tr id='fuliaoTr0'>
											<td style="border-top: 0px; border-left: 0px" align="center">
												<SELECT id="type0" name="process.fuliaoList[0].type">
													<option></option>
													<option>
														备件
													</option>
													<option>
														金属五交材料
													</option>
													<option>
														工具
													</option>
													<option>
														办公用品
													</option>
													<option>
														杂品
													</option>
													<option>
														金属五交
													</option>
													<option>
														工装
													</option>
													<option>
														五金
													</option>
													<option>
														包装物
													</option>
												</SELECT>
											</td>
											<td style="border-top: 0px; border-left: 0px" align="center">
												<SELECT id="type0" name="process.fuliaoList[0].name">
													<option></option>
													<option>
														焊丝
													</option>
													<option>
														气体
													</option>
													<option>
														手套
													</option>
													<option>
														砂轮打磨片
													</option>
													<option>
														导电嘴
													</option>
													<option>
														润滑油
													</option>
													<option>
														油漆性记号笔
													</option>
													<option>
														防护玻璃片
													</option>
												</SELECT>
											</td>
											<td style="border-top: 0px; border-left: 0px" align="center">
												<input id="specification0"
													name="process.fuliaoList[0].specification">
											</td>

											<td style="border-top: 0px; border-left: 0px" align="center">
												<input id="outCount0" name="process.fuliaoList[0].outCount">
											</td>
											<td style="border-top: 0px; border-left: 0px" align="center">
												<select id="flUnit0" name="process.fuliaoList[0].unit">
													<option></option>
												</select>
											</td>

											<td
												style="border-top: 0px; border-left: 0px; border-right: 0px"
												align="center">
												<input type="button" value="删除"
													onclick="deleteFuliaoLine(<s:property value="#index.index"/>)">
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<th align="center" colspan="2">
									本工作日未完成半成品入库
								</th>
							</tr>
							<tr>
								<td colspan="2">
									<table class="table">
										<tr>
											<th style="display: none;">
												库别
											</th>
											<th>
												仓区
											</th>
											<th>
												库位
											</th>
										</tr>
										<tr>
											<td style="display: none;">
												<select name="processSaveLog.warehouse" id="warehouse"
													onchange="getcangqu()">
													<option></option>
													<option value="半成品库">
														半成品库
													</option>
												</select>
											</td>
											<td align="center" id="goodHouseName_td">
												<SELECT name="processSaveLog.goodHouseName"
													id="goodHouseName" onclick="selectcq()"
													onchange="getkuwei(this)">
													<option value=""></option>
												</SELECT>
											</td>
											<td align="center" id="goodsStorePosition_td">
												<select name="processSaveLog.goodsStorePosition"
													style="width: 200px;" id="goodsStorePosition">
													<option value=""></option>
												</select>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<th colspan="2">
									<input type="hidden" name="barcode" id="proLgbarcode" />
									<input id="submitProce" type="button" value="提交"
										onclick='submitForm()' style="width: 80px; height: 50px;" />
									<div id="showWait"></div>
								</th>
							</tr>
						</table>
					</form>
					<div id="printDiv" style="width: 100%; display: none;">
						<div style="font-size: 16px; color: red;">
							工序提交成功,请选择是否打印工序周转单
						</div>
						<input id="sjbsbut" type="button"
							style="height: 50; width: 100px; display: none;" value="打印首件标识"
							onclick="javascript:pagePrint('printDiv2', 'view');window.location = 'ProcardAction!findProcardByRunCard2.action?id=${id}&pageStatus=${pageStatus}';">
						<input type="button" style="height: 50; width: 100px;"
							value="打印工序周转单"
							onclick="javascript:pagePrint('printgxDiv', 'view');window.location = 'ProcardAction!findProcardByRunCard2.action?id=${id}&pageStatus=${pageStatus}';">
						<input type="button" style="height: 50; width: 80px;" value="暂不打印"
							onclick="javascript:window.location = 'ProcardAction!findProcardByRunCard2.action?id=${id}&pageStatus=${pageStatus}';">

						<div id="sjmore" style="display: none;">
							首件标识上面有
							<font color="red">首检二维码</font>请
							<font color="red">务必打印</font>！
							<br />
							(首件标识只有提交首件的时才能打印一次，不可补打。
							<br />
							不打印首件标识会导致首检无法扫码检验!)
						</div>
						<div id="printDiv2"
							style="width: 100%; padding: 0px; margin: 0px; display: none; float: left;">
							<table class="table"
								style="width: 260px; padding: 0px; margin: 0px; font-weight: bolder;">
								<tr>
									<th colspan="2">
										首件标识
									</th>
								</tr>
								<tr>
									<th align="right" style="width: 40px;">
										件号:
									</th>
									<td>
										<div id="printMarkId2"></div>
									</td>
								</tr>
								<tr>
									<th align="right">
										名称:
									</th>
									<td style="width: 180px; font-size: 6px;">
										<div id="printProName2"></div>
									</td>
								</tr>
								<tr>
									<th align="right">
										版本号:
									</th>
									<td>
										<div id="printbanben2"></div>
									</td>
								</tr>
								<tr>
									<th align="right">
										业务件号:
									</th>
									<td>
										<div id="printYwMarkId2"></div>
									</td>
								</tr>
								<tr>
									<th align="right">
										内部订单号:
									</th>
									<td>
										<div id="orderNum2"></div>
									</td>
								</tr>
								<tr>
									<th align="right">
										批次:
									</th>
									<td>
										<div id="printSelfCard2"></div>
									</td>
								</tr>
								<tr>
									<th align="right">
										工序号:
									</th>
									<td>
										<div id="printProcessNO2"></div>
									</td>
								</tr>
								<tr>
									<th align="right">
										工序名称:
									</th>
									<td>
										<div id="printProcessName2"></div>
									</td>
								</tr>
								<tr>
									<th align="right">
										提交数量:
									</th>
									<td>
										<div id="printSubmmitCount2"></div>
									</td>
								</tr>
								<tr>
									<th align="right">
										提交人:
									</th>
									<td>
										<div id="printUsernames2"></div>
									</td>
								</tr>
								<tr>
									<th align="right">
										提交时间:
									</th>
									<td>
										<div id="printSubmitDate2"></div>
									</td>
								</tr>
								<tr>
									<th align="right">
										成型图:
									</th>
									<td>
										<div id="cxtShow2"></div>
									</td>
								</tr>
								<%--								<tr>--%>
								<%--									<th align="right">--%>
								<%--										条形码:--%>
								<%--									</th>--%>
								<%--									<td>--%>
								<%--										<img id="bar" width="200px" height="50px" />--%>
								<%--									</td>--%>
								<%--								</tr>--%>
								<tr>
									<th align="right">
										首检码:
									</th>
									<td>
										<div id="erweima2"></div>
									</td>
								</tr>
							</table>
						</div>
						<div id="printgxDiv"
							style="width: 100%; padding: 0px; margin: 0px; float: left;">
							<table class="table"
								style="width: 260px; padding: 0px; margin: 0px; font-weight: bolder;">
								<tr>
									<th colspan="2">
										工序周转卡
									</th>
								</tr>
								<tr>
									<th align="right" style="width: 40px;">
										件号:
									</th>
									<td>
										<div id="printMarkId"></div>
									</td>
								</tr>
								<tr>
									<th align="right">
										名称:
									</th>
									<td style="width: 180px; font-size: 6px;">
										<div id="printProName"></div>
									</td>
								</tr>
								<tr>
									<th align="right">
										版本号:
									</th>
									<td>
										<div id="printbanben"></div>
									</td>
								</tr>
								<tr>
									<th align="right">
										业务件号:
									</th>
									<td>
										<div id="printYwMarkId"></div>
									</td>
								</tr>
								<tr>
									<th align="right">
										内部订单号:
									</th>
									<td>
										<div id="orderNum"></div>
									</td>
								</tr>
								<tr>
									<th align="right">
										批次:
									</th>
									<td>
										<div id="printSelfCard"></div>
									</td>
								</tr>
								<tr>
									<th align="right">
										工序号:
									</th>
									<td>
										<div id="printProcessNO"></div>
									</td>
								</tr>
								<tr>
									<th align="right">
										工序名称:
									</th>
									<td>
										<div id="printProcessName"></div>
									</td>
								</tr>
								<tr>
									<th align="right">
										提交数量:
									</th>
									<td>
										<div id="printSubmmitCount"></div>
									</td>
								</tr>
								<tr>
									<th align="right">
										提交人:
									</th>
									<td>
										<div id="printUsernames"></div>
									</td>
								</tr>
								<tr>
									<th align="right">
										提交时间:
									</th>
									<td>
										<div id="printSubmitDate"></div>
									</td>
								</tr>
								<tr>
									<th align="right">
										下转工序(3):
									</th>
									<td>
										<div id="nextProcess"></div>
									</td>
								</tr>
								<tr>
									<th align="right">
										成型图:
									</th>
									<td>
										<div id="cxtShow"></div>
									</td>
								</tr>
								<%--								<tr>--%>
								<%--									<th align="right">--%>
								<%--										条形码:--%>
								<%--									</th>--%>
								<%--									<td>--%>
								<%--										<img id="bar" width="200px" height="50px" />--%>
								<%--									</td>--%>
								<%--								</tr>--%>
								<tr>
									<th align="right">
										工序码:
									</th>
									<td>
										<div id="erweima"></div>
									</td>
								</tr>
							</table>
						</div>
						<div style="clear: both;"></div>
					</div>
				</div>
			</div>
			<div id="aginPrintDiv" style="display: none;">
				<table style="width: 100%; background-color: #ffffff">
					<tr>
						<td>
							您正在补打工序提交单:
						</td>
						<td align="right">
							<img alt="" src="<%=basePath%>/images/closeImage.png" width="30"
								height="32" onclick="chageDiv('none')">
						</td>
					</tr>
				</table>
				<div id="operatingDiv"
					style="background-color: #ffffff; width: 100%;">
					<form id="aginPrintForm">
						<input id="printProId" type="hidden" name="process.id" />
						<input type="hidden" name="id" value="${procard.id}" />
						<table class="table" style="width: 40%">
							<tr>
								<th colspan="2">
									打印工序提交单
								</th>
							</tr>
							<tr>
								<th>
									打印数量:
								</th>
								<th align="left">
									<input id="printNumber" name="process.submmitCount" />
								</th>
							</tr>
							<tr>
								<th colspan="2">
									<input type="button" value="提交" onclick='aginPrintForm()'
										style="width: 80px; height: 50px;" />
								</th>
							</tr>
						</table>
					</form>
				</div>
			</div>
			<div id="bhgDiv" style="display: none;">
				<table style="width: 100%; margin-top: ">
					<tr>
						<td>
							您正在提交不合格数量:
						</td>
						<td align="right">
							<img alt="" src="<%=basePath%>/images/closeImage.png" width="30"
								height="32" onclick="chageDiv('none')">
						</td>
					</tr>
				</table>
				<div id="operatingDiv"
					style="background-color: #ffffff; width: 100%;">
					<form id="bhgForm">
						<input id="bhgProcessId" type="hidden"
							name="breaksubmit.processId" />
						<input type="hidden" name="breaksubmit.procardId"
							value="${procard.id}" />
						<table class="table" style="width: 40%">
							<tr>
								<th colspan="2">
									提交不合格数量
								</th>
							</tr>
							<tr>
								<th colspan="2">
									<input type="radio" value="上工序不合格"
										name="breaksubmit.breakgroup" id="breakgroup1"
										onclick="hidwgbreakType('2')" />
									上工序不合格
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									<input type="radio" value="本工序不合格"
										name="breaksubmit.breakgroup" id="breakgroup1"
										checked="checked" onclick="showwgbreakType('2')" />
									本工序不合格
								</th>
							</tr>
							<tr>
								<th colspan="2">
									<input type="radio" value="零件损坏" name="breaksubmit.type"
										id="zzbreakType_2" onclick="hidProcessAndWg('2')"
										checked="checked" />
									零件损坏
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									<input type="radio" value="外购件不合格" name="breaksubmit.type"
										id="wgbreakType_2" onclick="showProcessAndWg('2','不合格数量')" />
									<span id="wgbreakType_span_2">外购件不合格</span>
								</th>
							</tr>
							<tr id="wgtr_2" style="display: none;">

							</tr>
							<tr id="ljbreak_tr_2">
								<th align="right">
									不合格数量:
								</th>
								<th align="left">
									<input id="bhgNumber" name="breaksubmit.tjbreakcount"
										onchange="numyanzheng(this,'zhengshu');numpanduan('idfuzhi');" />
								</th>
							</tr>
							<tr>
								<th colspan="2">
									<input type="hidden" value="" id="breaksubmit_id"
										name="breaksubmit.id" />
									<input type="hidden" value="" id="break_pageStatus"
										name="pageStatus" />
									<input type="button" value="提交" id="break_button"
										onclick='bhgForm();' style="width: 80px; height: 50px;" />
								</th>
							</tr>
						</table>
					</form>
				</div>
			</div>
			<div id="showCollorProcessDiv" style="display: none;">
				<table style="width: 100%; background-color: #ffffff;">
					<tr>
						<td>
							您正在查看领取工序记录:
						</td>
						<td align="right">
							<img alt="" src="<%=basePath%>/images/closeImage.png" width="30"
								height="32" onclick="chageDiv('none')">
						</td>
					</tr>
				</table>
				<div id="operatingDiv"
					style="background-color: #ffffff; width: 100%;">
					<iframe id="showCollorProcessIf" src="" marginwidth="0"
						marginheight="0" frameborder="0" scrolling="yes"
						style="width: 100%; margin: 0px; padding: 0px; height: 500px;"></iframe>
				</div>
			</div>
		</div>
		</div>

		<s:if test="tc=='no'">
			<div>
				<input class="input" onclick="window.history.back();" type="button"
					value="返回" style="height: 38px; width: 50%; float: left;">
				<input class="input" onclick="zhuye()" type="button" value="主页"
					style="height: 38px; width: 50%; float: right;">
			</div>
		</s:if>
		<s:if test='procard.productStyle=="批产"'>
			<div
				style="background-color: green; color: #ffffff; font-size: 14px;"
				align="center">
				批产 批产 批产 批产 批产 批产 批产 批产 批产 批产 批产 批产 批产 批产 批产 批产 批产 批产 批产 批产 批产 批产 批产
				批产 批产 批产 批产 批产
			</div>
		</s:if>
		<s:if test='procard.productStyle=="试制"'>
			<div
				style="background-color: yellow; color: #000000; font-size: 14px;"
				align="center">
				试制 试制 试制 试制 试制 试制 试制 试制 试制 试制 试制 试制 试制 试制 试制 试制 试制 试制 试制 试制 试制 试制 试制
				试制 试制 试制 试制 试制
			</div>
		</s:if>
		<%--		<s:if test="procard.sbNumber!=null">--%>
		<div id="div_shebian"
			style="background-color: #00FF00; color: #FF4500; font-weight: bolder; padding-left: 100px; font-size: 15px; font-family: 黑体; display: none;">
			批次设变提醒:[设变单号:
			<a
				href="procardTemplateGyAction_showSbApplyJd2.action?bbAply.id=${procard.sbId}">${procard.sbNumber}</a>][设变状态:${procard.sbStatus}]
		</div>
		<%--		</s:if>--%>
		<s:if test='procard.procardStyle=="总成"||procard.procardStyle=="自制"'>
			<div id="showZong" style="border: solid #000 1px;">
				<div align="center"
					style="border-bottom: solid #000 1px; font-weight: bolder;">
					工 艺 流 水 卡 片
					<s:if test="viewStatus=='zjl'||viewStatus=='KH'||viewStatus=='zjy'">
						<a target="abc"
							href="<%=basePath%>/ProcardAction!showProcardtzforsc.action?id=${procard.id}"><font
							style="font-size: 16px">零件图纸</font> </a>
					</s:if>
					<hr />
					<b>备注:<span style="color: red;">${procard.remark}</span> </b>
					<s:if test="pageStatus=='viewUpdate'">
					(<a href="javascript:void(0);"
							onclick="updateProcard(${procard.id});"><font color="red"
							size="4">更新</font> </a>)
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(<a href="javascript:void(0);"
							onclick="caigou(${procard.id});"><font color="red" size="4">重算采购</font>
						</a>) 
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(<a href="javascript:void(0);"
							id="jha" onclick="jihuo(${procard.id});"><font color="red"
							size="4">激活</font> </a>
						<label id="jhlabel" style="display: none;">
							<font color="gray" size="4">激活中</font>
						</label>) </s:if>
					<s:if test="procard.oldProcardId!=null">
						来自批次:<a
							href="ProcardAction!findProcardByRunCard2.action?id=${procard.oldProcardId}&pageStatus=${pageStatus}&viewStatus=">${procard.oldSelfCard}</a>的补料
					</s:if>
					<s:if
						test='viewStatus=="update"&&(procard.status=="初始"||procard.status=="已发卡")'>
					(<a target="_top"
							onclick="if(window.confirm('确定要删除本零件?')){window.location.href ='ProcardAction!deleteprocardtree.action?id=${procard.id}&procard.rootId=${procard.rootId}&pageStatus=&viewStatus='};"><font
							color="red" size="4">删除零件</font> </a>)
					</s:if>
				</div>
				<div style="font-weight: bolder;" align="center">
					件号:
					<font color="red"> ${procard.markId}</font>
					<s:if test="procard.ywMarkId!=null&&procard.ywMarkId!=''">[<font
							color="green" style="font-weight: bolder;">${procard.ywMarkId}</font>]</s:if>
					&nbsp;&nbsp; 批次:${procard.selfCard}
					&nbsp;&nbsp;订单号:${procard.orderNumber}
					&nbsp;&nbsp;图号:${procard.tuhao}
					<br />
					&nbsp;&nbsp;
					<font color="blue">版本: ${procard.banBenNumber}&nbsp;&nbsp;</font>
					<font color="blue">版次: ${procard.banci}&nbsp;&nbsp;</font> 产品类型:
					<font color="red">${procard.procardStyle}</font>&nbsp;&nbsp;状态:
					<font color="red">${procard.status}<s:if test="procard.status=='设变锁定'">(<s:property value="procard.sduser"/>)</s:if> </font>&nbsp;&nbsp;
					名称:${procard.proName} &nbsp;&nbsp;
					<br />
					生产类型:${procard.productStyle} 生成日期:${procard.procardTime}
					生成人:${procard.zhikaren}
				</div>
				<table class="table" style="width: 100%;">
					<tr>
						<th colspan="15">
							生产数量 : ${procard.filnalCount} ${procard.unit}
							&nbsp;&nbsp;&nbsp;&nbsp; 配套已领数：
							${procard.klNumber-procard.hascount} &nbsp;&nbsp;&nbsp;&nbsp;
							完成数量: ${procard.tjNumber} &nbsp;&nbsp;&nbsp;&nbsp; 入库数量:
							${procard.rukuCount}
						</th>
					</tr>
					<tr align="center">
						<th>
							零组件
						</th>
						<th>
							名称
						</th>
						<th>
							规格
						</th>
						<th>
							卡片
							<br />
							类型
						</th>
						<th>
							供料
							<br />
							属性
						</th>
						<th>
							需要
							<br />
							领料
						</th>
						<th>
							需求
							<br />
							数量
						</th>
						<th>
							领料
							<br />
							数量
						</th>
						<th>
							完成
							<br />
							数量
						</th>
						<th>
							单位
						</th>
						<th>
							库存
						</th>
						<th>
							半成品
							<br />
							入库/出库
						</th>
						<th>
							零件批次跟踪(批次:数量)
						</th>
					</tr>
					<s:iterator value="procardList" id="pageProcardTem" status="">
						<tr align="center">
							<td align="left">
								<a
									href="ProcardAction!findProcardByRunCard2.action?id=${pageProcardTem.id}&pageStatus=history&viewStatus=">
									${pageProcardTem.markId} </a>
							</td>
							<td align="left" style="max-width: 255px">
								${pageProcardTem.proName}
							</td>
							<td align="left" style="max-width: 155px">
								${pageProcardTem.specification}
							</td>
							<td>
								${pageProcardTem.procardStyle}
							</td>
							<td>
								${pageProcardTem.kgliao}
							</td>
							<td>
								<s:if test='#pageProcardTem.lingliaostatus=="否"'>
									否
								</s:if>
								<s:else>
									是
								</s:else>
							</td>
							<td align="right">
								${pageProcardTem.filnalCount}
							</td>
							<s:if test='#pageProcardTem.lingliaostatus=="否"'>
								<td style="background-color: green; color: #ffffff">
									0
							</s:if>
							<s:else>
								<s:if test="#pageProcardTem.hascount==0">
									<td style="background-color: green; color: #ffffff;">
										${pageProcardTem.filnalCount}
								</s:if>
								<s:elseif test="#pageProcardTem.ylNumber<0">
									<td style="background-color: red; color: #ffffff">
										0
								</s:elseif>
								<s:elseif
									test="#pageProcardTem.filnalCount-#pageProcardTem.ylNumber>0.005">
									<td style="background-color: red; color: #ffffff">
										${pageProcardTem.ylNumber}
								</s:elseif>
								<s:else>
									<td style="background-color: green; color: #ffffff">
										${pageProcardTem.ylNumber}
								</s:else>
							</s:else>
							</td>
							<s:if
								test='#pageProcardTem.procardStyle=="外购"&&#pageProcardTem.needProcess!="yes"'>
								<s:if test='#pageProcardTem.lingliaostatus=="否"'>
									<td style="background-color: green; color: #ffffff">
										0
								</s:if>
								<s:else>
									<s:if test="#pageProcardTem.hascount==0">
										<td style="background-color: green; color: #ffffff">
											${pageProcardTem.filnalCount}
									</s:if>
									<s:else>
										<td style="background-color: red; color: #ffffff">
											${pageProcardTem.ylNumber}
									</s:else>
								</s:else>
							</s:if>
							<s:else>
								<s:if test="#pageProcardTem.tjNumber==null">
									<td style="background-color: red; color: #ffffff">
										0
								</s:if>
								<s:elseif
									test="#pageProcardTem.filnalCount-#pageProcardTem.tjNumber>0.005">
									<td style="background-color: red; color: #ffffff">
										${pageProcardTem.tjNumber}
								</s:elseif>
								<s:else>
									<td style="background-color: green; color: #ffffff">
										${pageProcardTem.tjNumber}
										</font>
								</s:else>
							</s:else>
							</td>
							<td align="left">
								${pageProcardTem.unit}
							</td>
							<td align="right">
								<s:if test='#pageProcardTem.procardStyle=="外购"'>
									<a
										href="procardBlAction_findLackGoodsDetail.action?goods.goodsMarkId=${pageProcardTem.markId}">${pageProcardTem.kcNumber}</a>
								</s:if>
								<s:else>${pageProcardTem.kcNumber}</s:else>
							</td>
							<td align="right">
								<s:if test='#pageProcardTem.procardStyle!="外购"'>
									${pageProcardTem.zzNumber}/
									${pageProcardTem.ztNumber}
								</s:if>
							</td>
							<td align="left">
								<s:iterator id="paged"
									value="#pageProcardTem.lingliaoDetail.split(',')"
									status="pdindex">
									${paged}<b>；</b>
								</s:iterator>
							</td>
						</tr>
					</s:iterator>
				</table>
				<br />
				<table class="table" style="width: 100%">
					<tr>
						<th>
							采购类型
						</th>
						<th>
							订单号
						</th>
						<th>
							委外工序
						</th>
						<th>
							关联件号
						</th>
						<th>
							已交货/总数量
						</th>
						<th>
							物料状态
						</th>
						<th>
							批次数量
						</th>
						<th>
							外协出库
						</th>
						<th>
							物料位置
						</th>
					</tr>
					<s:if test="wwPlanList.size()>0">
						<s:iterator value="wwPlanList" id="pagewwPlan" status="pageIndex">
							<tr>
								<td align="center">
									${pagewwPlan.wwType}
								</td>
								<td>
									${pagewwPlan.waigouOrder.planNumber}
								</td>
								<td style="width: 150px;">
									${pagewwPlan.processNames}
								</td>
								<td>
									${pagewwPlan.remark}
								</td>
								<th align="right">
									<s:if test="#pagewwPlan.hasruku==null">
									0
									</s:if>
									<s:else>
										<span id="yijiao${pageIndex.index}"></span>
										<SCRIPT type="text/javascript">
											var num = formtNumber(${pagewwPlan.hasruku},4);
											var i = ${pageIndex.index};
											$("#yijiao"+i).html(num);
										</SCRIPT>
									</s:else>
									/
									<span id="zongshu${pageIndex.index}"></span>
									<SCRIPT type="text/javascript">
											var num = formtNumber(${pagewwPlan.number},4);
											var i = ${pageIndex.index};
											$("#zongshu"+i).html(num);
										</SCRIPT>
								</th>
								<td align="center">
									${pagewwPlan.status}
								</td>
								<td
									style="max-width: 100px; overflow: hidden; white-space: nowrap; text-overflow: ellipsis;">
									<font size="2">${pagewwPlan.pcDetail}</font>
									<ul class="qs_ul">
										<li>
											<b>${pagewwPlan.pcDetail}</b>
										</li>
									</ul>
								</td>
								<td align="center">
									${pagewwPlan.wxckCount}
								</td>
								<td style="width: 300px;">
									<s:if test="#pagewwPlan.wlWeizhiDt!=null">
										<input value="查看明细"
											onclick="showwldt('showzongwldt_${pageIndex.index}')"
											type="button">
										<input value="关闭明细"
											onclick="hidewldt('showzongwldt_${pageIndex.index}')"
											type="button">
									</s:if>
									<div id="showzongwldt_${pageIndex.index}"
										style="display: none;">
										${pagewwPlan.wlWeizhiDt}
									</div>
								</td>
							</tr>
						</s:iterator>
					</s:if>
					<s:else>
						<tr>
							<td colspan="7" align="center">
								无委外订单
							</td>
						</tr>
					</s:else>
				</table>
			</div>
		</s:if>
		<s:if test='procard.procardStyle=="外购"'>
			<div id="showWai" style="border: solid #000 1px;">
				<div align="center" style="border-bottom: solid #000 1px;">
					工 艺 流 水 卡 片
					<s:if test="viewStatus=='zjl'||viewStatus=='KH'||viewStatus=='zjy'">
						<a target="abc"
							href="<%=basePath%>/ProcardAction!showProcardtzforsc.action?id=${procard.id}"><font
							style="font-size: 16px">零件图纸</font> </a>
					</s:if>
					<s:if test="pageStatus=='viewUpdate'">
					(<a href="javascript:void(0);"
							onclick="updateProcard(${procard.id});"><font color="red"
							size="4">更新</font> </a>)
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(<a href="javascript:void(0);"
							id="jha" onclick="jihuo(${procard.id});"><font color="red"
							size="4">激活</font> </a>
						<label id="jhlabel" style="display: none;">
							<font color="gray" size="4">激活中</font>
						</label>) </s:if>
					<s:if
						test='viewStatus=="update"&&(procard.status=="初始"||procard.status=="已发卡")'>
					(<a target="_top"
							onclick="if(window.confirm('确定要删除本零件?')){window.location.href ='ProcardAction!deleteprocardtree.action?id=${procard.id}&procard.rootId=${procard.rootId}&pageStatus=&viewStatus='};"><font
							color="red" size="4">删除零件</font> </a>)
					</s:if>
				</div>
				<table class="table" style="width: 100%;">
					<tr>
						<th align="right">
							生产类型:
						</th>
						<td>
							<font color="red">${procard.procardStyle}</font>&nbsp;&nbsp;
						</td>
					</tr>
					<tr>
						<th align="right" style="width: 25%;">
							订单号:
						</th>
						<td>
							${procard.orderNumber}
						</td>
					</tr>
					<tr>
						<th align="right" style="width: 25%;">
							件号:
						</th>
						<td>
							<font color="red">${procard.markId}</font>
							<s:if test="procard.ywMarkId!=null">(<font color="green"
									style="font-weight: bolder;">${procard.ywMarkId}</font>)</s:if>
							&nbsp;&nbsp;
						</td>
					</tr>
					<tr>
						<th align="right" style="width: 25%;">
							图号:
						</th>
						<td>
							<font color="red">${procard.tuhao} </font>
						</td>
					</tr>
					<tr>
						<th align="right">
							名称:
						</th>
						<td>
							${procard.proName}
						</td>
					</tr>
					<tr>
						<th align="right">
							规格:
						</th>
						<td>
							${procard.specification}
						</td>
					</tr>
					<tr>
						<th align="right">
							<font color="blue">版本:</font>
						</th>
						<td>
							<font color="blue">${procard.banBenNumber}</font>
						</td>
					</tr>
					<tr>
						<th align="right">
							<font color="blue">版次:</font>
						</th>
						<td>
							<font color="blue">${procard.banci}</font>
						</td>
					</tr>
					<tr>
						<th align="right">
							物料类别:
						</th>
						<td>
							${procard.wgType}
						</td>
					</tr>
					<tr>
						<th align="right">
							供料属性:
						</th>
						<td>
							${procard.kgliao}
						</td>
					</tr>
					<tr>
						<th align="right">
							批次:
						</th>
						<td>
							${procard.selfCard}
						</td>
					</tr>
					<tr>
						<th align="right">
							需求数量:
						</th>
						<td>
							${procard.filnalCount}
						</td>
					</tr>
					<tr>
						<th align="right">
							已领数量:
						</th>
						<td>
							${procard.ylNumber}
						</td>
					</tr>
					<tr>
						<th align="right">
							库存量:
						</th>
						<td>
							${procard.kcNumber}
						</td>
					</tr>
					<tr>
						<th align="right">
							占用量:
						</th>
						<td>
							${procard.zzNumber}
						</td>
					</tr>
					<tr>
						<th align="right">
							在途量:
						</th>
						<td>
							${procard.ztNumber}
						</td>
					</tr>
					<tr>
						<th align="right">
							在途占用量:
						</th>
						<td>
							${procard.ztzyNumber}
						</td>
					</tr>
					<tr>
						<th align="right">
							外委包料量:
						</th>
						<td>
							${procard.wwblCount}
						</td>
					</tr>
					<tr>
						<th align="right">
							外委领料量:
						</th>
						<td>
							${procard.wwCount-procard.wwhascount} / ${procard.wwCount}
						</td>
					</tr>
					<tr>
						<th align="right">
							缺件量:
						</th>
						<td>
							${procard.qjCount}
						</td>
					</tr>
					<tr>
						<th align="right">
							材料跟踪(批次：数量)
						</th>
						<td align="left">
							<s:iterator id="paged" value="procard.lingliaoDetail.split(',')"
								status="pdindex">
									${paged}<b>；</b>
							</s:iterator>
						</td>
					</tr>
					<tr>
						<th align="right">
							单位:
						</th>
						<td>
							${procard.unit}
						</td>
					</tr>
					<tr>
						<th align="right">
							权值:
						</th>
						<td>
							${procard.quanzi1} : ${procard.quanzi2}
						</td>
					</tr>
					<tr>
						<th align="right">
							产品类型:
						</th>
						<td>
							${procard.productStyle}
						</td>
					</tr>
					<tr>
						<th align="right">
							生产状态:
						</th>
						<td>
							<font color="red">${procard.status}</font>
						</td>
					</tr>
					<form action="ProcardAction!updateProcardForLingliao.action"
						method="post" onsubmit="return upCaigou1()">
						<input name="procard.id" value="${procard.id}" type="hidden" />
						<tr>
							<tr>
								<th align="right">
									备注:
								</th>
								<td>
									<input type="text" value="${procard.remark }"
										name="procard.remark" />
								</td>
							</tr>
							<th align="right">
								是否领料:
							</th>
							<td>
								<font color="red">${procard.lingliaostatus}</font>
								<s:if
									test='viewStatus=="update"&&(procard.status=="初始"||procard.cgStatus=="否")'>
									<select name="procard.lingliaostatus" id="lingliaostatus1"
										onclick="isfou()">
										<option>
											${procard.lingliaostatus}
										</option>
										<option>
											是
										</option>
										<option>
											否
										</option>
									</select>
									<input type="hidden" value="${procard.filnalCount}"
										id="filnalCount1" />
									<input type="hidden"
										value=" <fmt:formatNumber type="number" value="${procard.klNumber-procard.hascount}" maxFractionDigits="4" />" id="hascount2" />
									<input type="hidden" value="${procard.klNumber}" id="klNumber2" />
									<input type="text" name="procard.klNumber"
										title="需领料数量=总数量-不用领料的数量" placeholder="需领料数量"
										value="${procard.klNumber}" id="klNumber1"
										style="width: 60px;" />
									<input type="submit" value="修改" />
								</s:if>
							</td>
						</tr>
						<tr>
							<th align="right">
								是否采购:
							</th>
							<td>
								<font color="red">${procard.cgStatus}</font>
								<s:if
									test='viewStatus=="update"&&(procard.status=="初始"||procard.status=="已发卡")'>
									<select name="procard.cgStatus" id="cgStatus1"">
										<option>
											${procard.cgStatus}
										</option>
										<option>
											是
										</option>
										<option>
											否
										</option>
									</select>
									<input type="submit" value="修改" />
								</s:if>
							</td>
						</tr>
						<tr>
							<th align="right">
								是否删除:
							</th>
							<td>
								<s:if test='viewStatus=="update"'>
									<select name="procard.sbStatus">
										<option>
											需要
										</option>
										<option>
											删除
										</option>
									</select>
									<input type="submit" value="修改" />
								</s:if>
							</td>
						</tr>
					</form>
					<tr>
						<th align="right">
							采购数量:
						</th>
						<td>
							<font color="red">${procard.cgNumber}(${procard.unit})</font>
						</td>
					</tr>
					<tr>
						<th align="right">
							损耗值:
						</th>
						<td>
							<font color="red">${procard.sunhao}%</font>
						</td>
					</tr>
					<tr>
						<th align="right">
							生成日期:
						</th>
						<td>
							${procard.procardTime}
						</td>
					</tr>
					<tr>
						<th align="right">
							生成人:
						</th>
						<td>
							${procard.zhikaren}
						</td>
					</tr>
					<%--<tr>
					<th align="right">
						物料状态:
					</th>
					<td>
						<font color="red">${procard.wlstatus}</font>
					</td>
				</tr>
				<tr>
					<th align="right">
						物料位置跟踪:
					</th>
					<td>
						<font color="red">${procard.wlWeizhiDt}</font>
					</td>
				</tr>
			--%>
				</table>
				<br />
				<table class="table" style="width: 100%">
					<tr>
						<th>
							采购类型
						</th>
						<th>
							订单号
						</th>
						<th>
							委外工序
						</th>
						<th>
							已交货/总数量
						</th>
						<th>
							物料状态
						</th>
						<th>
							批次数量
						</th>
						<th>
							物料位置
						</th>
					</tr>
					<s:if test="wwPlanList.size()>0">
						<s:iterator value="wwPlanList" id="pagewwPlan" status="pageIndex">
							<tr>
								<td align="center">
									${pagewwPlan.wwType}
								</td>
								<td>
									${pagewwPlan.waigouOrder.planNumber}
								</td>
								<td>
									${pagewwPlan.processNames}
								</td>
								<th align="right">
									<span id="yijiao_${pageIndex.index}"></span>
									<SCRIPT type="text/javascript">
										var num = formtNumber(${pagewwPlan.number-pagewwPlan.syNumber},4);
										var i = ${pageIndex.index};
										$("#yijiao_"+i).html(num);
									</SCRIPT>
									/
									<span id="zongshu_${pageIndex.index}"></span>
									<SCRIPT type="text/javascript">
										var num = formtNumber(${pagewwPlan.number},4);
										var i = ${pageIndex.index};
										$("#zongshu_"+i).html(num);
									</SCRIPT>
								</th>
								<td align="center">
									${pagewwPlan.status}
								</td>
								<td
									style="max-width: 100px; overflow: hidden; white-space: nowrap; text-overflow: ellipsis;">
									<font size="2">${pagewwPlan.pcDetail}</font>
									<ul class="qs_ul">
										<li>
											<b>${pagewwPlan.pcDetail}</b>
										</li>
									</ul>
								</td>
								<td style="width: 300px;">
									<s:if test="#pagewwPlan.wlWeizhiDt!=null">
										<input value="查看明细"
											onclick="showwldt('showwaiwldt_${pageIndex.index}')"
											type="button">
										<input value="关闭明细"
											onclick="hidewldt('showwaiwldt_${pageIndex.index}')"
											type="button">
									</s:if>
									<div id="showwaiwldt_${pageIndex.index}" style="display: none;">
										${pagewwPlan.wlWeizhiDt}
									</div>
								</td>
							</tr>
						</s:iterator>
					</s:if>
					<s:else>
						<tr>
							<td colspan="6" align="center">
								无外购订单
							</td>
						</tr>
					</s:else>
				</table>
			</div>
		</s:if>
		<div align="left">
			<h3>
				版次设变提示:
				<font color="red">${procard.sbmsg}</font>
			</h3>
		</div>
		<s:if
			test='procard.procardStyle=="总成"||procard.procardStyle=="自制"||procard.needProcess=="yes"'>
			<div id="process" style="">
				<table class="table" style="width: 100%;">
					<tr>
						<th colspan="30" height="40px;">
							领工序
						</th>
					</tr>
					<tr align="center">
						<th>
							工序号
						</th>
						<th>
							名称
						</th>
						<th>
							总数量
						</th>
						<th>
							可领数量
						</th>
						<th>
							已领数量
						</th>
						<th>
							提交数量
						</th>
						<th>
							不合格量
						</th>


						<s:if test="viewStatus=='zjl'||viewStatus=='KH'">
							<th>
								进度
							</th>
							<th>
								总节拍
							</th>
						</s:if>



						<th>
							领取时间
						</th>
						<th>
							提交时间
						</th>
						<th>
							并行
						</th>
						<th>
							工位
						</th>
						<th>
							设备编号
							<br>
							(设备名称)
						</th>
						<th>
							工装/模具
						</th>
						<th>
							量、检具
						</th>
						<th>
							生产类型
						</th>
						<th>
							状态
						</th>
						<th>
							控制类型
						</th>
						<th>
							领取人
						</th>
						<th>
							是否
							<br />
							转存
						</th>
						<th>
							关联件号
						</th>
						<th>
							操作
						</th>
					</tr>
					<s:set name="addIndex" value="0"></s:set>
					<s:iterator value="list" id="pageProcess" status="pageIndex">
						<tr align="center" onmouseover="chageBgcolor(this,'#F0F8FF')"
							onmouseout="outBgcolor(this,'')"
							onclick="clickBgcolor(this,'#D1EEEE');">
							<th height="30px">
								${pageProcess.processNO}
							</th>
							<th
								onclick="showDetail('ProdEquipmentAction!findMachineByNum.action?id=${pageProcess.id}',this)">
								${pageProcess.processName}
							</th>
							<th>
								<fmt:formatNumber value="${procard.filnalCount}" pattern="#" />
							</th>
							<th>
								<fmt:formatNumber value="${pageProcess.totalCount}" pattern="#" />
							</th>
							<th>
								<fmt:formatNumber value="${pageProcess.applyCount}" pattern="#" />
							</th>
							<th>
								<fmt:formatNumber value="${pageProcess.submmitCount}"
									pattern="#" />
							</th>
							<th>
								<fmt:formatNumber value="${pageProcess.breakCount}" pattern="#" />
							</th>

							<!-- 总节拍 -->
							<s:if test="viewStatus=='zjl'||viewStatus=='KH'">
								<th>
									<div class="processjindu" style="cursor: pointer;"
										onclick="showDetail('ProdEquipmentAction!findMachineByNum.action?id=${pageProcess.id}',this)"
										data="${pageProcess.submmitCount/procard.filnalCount*100}">
									</div>
								</th>

								<th>
									${pageProcess.nowAllJiepai}s
								</th>
							</s:if>


							<th>
								${pageProcess.firstApplyDate}
							</th>
							<th>
								${pageProcess.submitDate}
							</th>
							<th>
								${pageProcess.processStatus}
							</th>
							<th>
								<label id="gw<s:property value="#addIndex" />">
									${pageProcess.gongwei}
								</label>
							</th>
							<th align="left">
								<label id="sbno<s:property value="#addIndex" />">
									${pageProcess.shebeiNo}
									<br>
									(${pageProcess.shebeiName})
								</label>
							</th>
							<th>
								<label id="sbno<s:property value="#addIndex" />">
									${pageProcess.matetag}
									<br>
									(${pageProcess.number})
								</label>
							</th>
							<th>
								<label id="sbno<s:property value="#addIndex" />">
									${pageProcess.measuring_no}
									<br>
									(${pageProcess.measuringMatetag})
								</label>
							</th>
							<th>
								<s:if test='#pageProcess.productStyle=="外委"'>
									<a
										href="OSWorkAction!findAllOSW.action?osWork.markID=${procard.markId}&osWork.lotId=${procard.selfCard}">${pageProcess.productStyle}</a>
								</s:if>
								<s:else>
							${pageProcess.productStyle}
							</s:else>
							</th>
							<th>
								${pageProcess.status}
							</th>
							<!--						控制类型	-->

							<th>
								<s:if
									test='#pageProcess.pmiType==null || #pageProcess.pmiType=="" '>
									无
								</s:if>
								<s:else>
									${pageProcess.pmiType}
								</s:else>

							</th>

							<th>
								<s:if test="viewStatus=='zjl'">
									<a
										href="UsersAction!findUserByIdForDetails.action?id=${pageProcess.userId}">
										${pageProcess.usernames}</a>
								</s:if>
								<s:else>${pageProcess.usernames}</s:else>
							</th>
							<th>
								<s:if test='#pageProcess.needSave=="是"'>
									是
								</s:if>
								<s:else>
									否
								</s:else>
							</th>
							<th style="max-width: 100px;">
								${pageProcess.glMarkId}
							</th>
							<th id="th_${pageProcess.id}">
								<s:if test="viewStatus=='zjl'||viewStatus=='KH'">
									<div style="width: 100px;">
										<s:if
											test="#pageProcess.shebeiNo!=''&&#pageProcess.shebeiNo!=null">
											<a href="javascript:;"
												onclick="showDetail('ProdEquipmentAction!findMachineByNum.action?machine.no=${pageProcess.shebeiNo}&id=${pageProcess.id}',this)">
												<s:if
													test="#pageProcess.pmiType!=null&&#pageProcess.pmiType!=''">
													<img src="<%=basePath%>/images/PMIShebei.png" width="20px"
														height="20px" />
												</s:if> <s:else>机</s:else> </a>
										</s:if>
										<s:else>
											<font color="gray">机</font>
										</s:else>
										<s:if
											test="#pageProcess.gzstoreId!=''&&#pageProcess.gzstoreId!=null">
											<a
												href="GzstoreAction_findAll.action?gzstore.number=${pageProcess.matetag}">工</a>
										</s:if>
										<s:else>
											<font color="gray">工</font>
										</s:else>
										<s:if
											test="#pageProcess.measuringId!=''&&#pageProcess.measuringId!=null">
											<a
												href="MeasuringAction_saveMeasuring.action?measuring.measuring_no=${pageProcess.measuring_no}&flag=showhas">量</a>
										</s:if>
										<s:else>
											<font color="gray">量</font>
										</s:else>
										<s:if
											test='#pageProcess.zjStatus=="yes"&&#pageProcess.status!="初始"'>
											<a
												href="LogoStickerAction!findLogoSticker2.action?tag=manger&sticker.lotId=${procard.selfCard}&sticker.markId=${procard.markId}&sticker.processNO=${pageProcess.processNO}">检</a>
										</s:if>
										<s:else>
											<font color="gray">检</font>
										</s:else>
										<a
											href="ProcardAction!showProcessinforTz.action?id=${pageProcess.id}">文</a>
										<%--										<s:if test="procard.productStyle='试制'">--%>
										<%--											<a--%>
										<%--												href="ProcardAction!findGongyiGuifan.action?markid=${procard.markId}&processNO=${pageProcess.processNO}&id=0&banci=${procard.banci}">文</a>--%>
										<%--										</s:if>--%>
										<%--										<a--%>
										<%--											href="ProcardAction!findGongyiGuifan.action?markid=${procard.markId}&processNO=${pageProcess.processNO}&id=1&banci=${procard.banci}">文</a>--%>
										<%--										<s:else>--%>

										<%--										</s:else>--%>
									</div>
								</s:if>
								<s:elseif test="viewStatus=='zjy'">
									<div style="width: 100px;">
										<a
											href="ProcardAction!showProcessinforTz.action?id=${pageProcess.id}">工序图纸</a>
									</div>
								</s:elseif>
								<input type="hidden" value="${pageProcess.id}"
									name="pageProcessId">
								<input type="hidden" value="${pageProcess.submmitCount}"
									name="pageProcessSubmmitCount">
								<s:if test="pageStatus!='history'">
									<s:if test='procard.status=="领工序"||procard.status=="已发料"'>
										<s:if test='#pageProcess.productStyle!="外委"'>
											<s:if test="#pageProcess.status!='完成'">
												<a href="javascript:;"
													onclick="showwlqueren('${pageProcess.id}',this)">物料确认</a>/
											</s:if>
											<s:if
												test='#pageProcess.wlqrcount!=null && #pageProcess.wlqrcount>0 &&#pageProcess.status == "初始" '>
												<a href="javascript:;"
													onclick="showZjProcess('${pageProcess.id}','${procard.markId}',this)">自检</a>
												<br />
											</s:if>
											<s:elseif test='#pageProcess.pg.gxstatus == "已领"'>
												<!-- 如果提交量为0 则开始首检提交 -->
												<s:if
													test="#pageProcess.hadsj!='yes'&&#pageProcess.zjStatus=='yes'">
													<input type="button" value="提交"
														onclick="submitProcess('${pageProcess.id}','1','sj',this,'${pageProcess.pg.barcode}')"
														style="height: 30px;" />
													<input type="button" value="点检"
														onclick="isdj('${pageProcess.id}',this)"
														style="height: 30px;" />
												</s:if>
												<s:else>
													<s:if test='#pageProcess.pmiType=="强控"'>
														<input type="button" value="断电&提交" style="height: 30px;"
															onclick="pmiAutoSubPro('${pageProcess.id}')" />
														<input type="button" value="点检"
															onclick="isdj('${pageProcess.id}',this)"
															style="height: 30px;" />
													</s:if>
													<s:else>
														<input type="button" value="提交" style="height: 30px;"
															onclick="submitProcess('${pageProcess.id}','${pageProcess.pg.receiveNumber-pageProcess.pg.submitNumber-pageProcess.pg.breakCount}','',this,'${pageProcess.pg.barcode}')" />
														<input type="button" value="点检"
															onclick="isdj('${pageProcess.id}',this)"
															style="height: 30px;" />
													</s:else>
												</s:else>
												<s:if test="#pageProcess.showTz=='yes'">
													<a target="abc"
														href="<%=basePath%>/ProcardAction!showProcesstzforsc.action?id=${pageProcess.id}">图纸</a>
												</s:if>
											</s:elseif>
											<s:elseif
												test='#pageProcess.status =="自检" && #pageProcess.wlqrcount >0'>
												<input type="button" value="领取"
													onclick="getProcess('${pageProcess.id}',this)"
													style="height: 30px;" />
												<br />
												<input type="button" value="工位设备"
													onclick="changeMachine('${pageProcess.id}','<s:property value="#addIndex" />',this)"
													style="height: 30px;" />
												<input type="button" value="点检"
													onclick="isdj('${pageProcess.id}',this)"
													style="height: 30px;" />
											</s:elseif>
											<s:elseif
												test='#pageProcess.pg.gxstatus == "待领" && #pageProcess.wlqrcount >0'>
												<input type="button" value="工位设备"
													onclick="changeMachine('${pageProcess.id}','<s:property value="#addIndex" />',this)"
													style="height: 30px;" />
												<input type="button" value="领取"
													onclick="getProcess('${pageProcess.id}',this)"
													style="height: 30px;" />
											</s:elseif>


											<%--											<s:if test='#pageProcess.status =="初始"'>--%>
											<%--												<a href="javascript:;"--%>
											<%--													onclick="showZjProcess('${pageProcess.id}','${procard.markId}',this)">自检</a>--%>
											<%--											</s:if>--%>
											<%--																						<s:elseif test='#pageProcess.status =="自检"'>--%>
											<%--																							<input type="button" value="工位设备"--%>
											<%--																								onclick="changeMachine('${pageProcess.id}','${pageIndex.index}',this)"--%>
											<%--																								style="height: 30px;" />--%>
											<%--																							<input type="button" value="领取"--%>
											<%--																								onclick="getProcess('${pageProcess.id}',this)"--%>
											<%--																								style="height: 30px;" />--%>
											<%--																							<input type="button" value="点检"--%>
											<%--																								onclick="isdj('${pageProcess.id}',this)"--%>
											<%--																								style="height: 30px;" />--%>
											<%--																						</s:elseif>--%>
											<%--											<s:elseif test='#pageProcess.status=="已领"'>--%>
											<%--												<s:if test="#pageProcess.totalCount!=#pageProcess.applyCount">--%>
											<%--												<a href="javascript:;"--%>
											<%--													onclick="getProcess('${pageProcess.id}')">领取</a>/</s:if>--%>
											<%--											--%>
											<%--												<!-- 如果提交量为0 则开始首检提交 -->--%>
											<%--												<s:if--%>
											<%--													test="#pageProcess.hadsj!='yes'&&#pageProcess.zjStatus=='yes'">--%>
											<%--													<input type="button" value="提交"--%>
											<%--														onclick="submitProcess('${pageProcess.id}','1','sj',this)"--%>
											<%--														style="height: 30px;" />--%>
											<%--													<input type="button" value="点检"--%>
											<%--														onclick="isdj('${pageProcess.id}',this)"--%>
											<%--														style="height: 30px;" />--%>
											<%--												</s:if>--%>
											<%--												<s:else>--%>
											<%--													<s:if test='#pageProcess.pmiType=="强控"'>--%>
											<%--														<input type="button" value="断电&提交" style="height: 30px;"--%>
											<%--															onclick="pmiAutoSubPro('${pageProcess.id}')" />--%>
											<%--														<input type="button" value="点检"--%>
											<%--															onclick="isdj('${pageProcess.id}',this)"--%>
											<%--															style="height: 30px;" />--%>
											<%--													</s:if>--%>
											<%--													<s:else>--%>
											<%--														<input type="button" value="提交" style="height: 30px;"--%>
											<%--															onclick="submitProcess('${pageProcess.id}','${pageProcess.applyCount-pageProcess.submmitCount-pageProcess.breakCount}','',this)" />--%>
											<%--														<input type="button" value="点检"--%>
											<%--															onclick="isdj('${pageProcess.id}',this)"--%>
											<%--															style="height: 30px;" />--%>
											<%--													</s:else>--%>
											<%--												</s:else>--%>
											<%--											</s:elseif>--%>
										</s:if>
									</s:if>
									<s:if test='#pageProcess.submmitCount>0'>
										<br />
										<a href="javascript:;"
											onclick="aginPrint('${pageProcess.id}')">补打</a>
									</s:if>
								</s:if>
							</th>
						</tr>
						<s:if test="viewStatus!='zjl'">
							<tr>
								<th align="left" colspan="25"
									style="background-color: red; border: 0px; margin: 0px; padding: 0px;">
									<div align="left"
										style="background-color: #00ff01; width:${pageProcess.submmitCount/procard.filnalCount*100}%; text-overflow: ellipsis; white-space: nowrap; height: 100%;">
										&nbsp;&nbsp;&nbsp;&nbsp;${pageProcess.processNO}工序完成进度:
										${pageProcess.submmitCount/procard.filnalCount*100}%
									</div>
								</th>
							</tr>
						</s:if>
						<s:set name="addIndex" value="#addIndex+1"></s:set>
						<s:if test="#pageProcess.fxProcessList!=null">
							<%--返修工序--%>
							<s:iterator value="#pageProcess.fxProcessList" id="pagefxProcess">
								<tr align="center" onmouseover="chageBgcolor(this,'#F0F8FF')"
									onmouseout="outBgcolor(this,'')"
									onclick="clickBgcolor(this,'#D1EEEE');">
									<th height="30px">
										${pagefxProcess.processNO} 
									</th>
									<th
										onclick="showDetail('ProdEquipmentAction!findMachineByNum.action?id=${pagefxProcess.id}',this)">
										${pagefxProcess.processName}
									</th>
									<th>
										<fmt:formatNumber value="${pagefxProcess.totalCount}"
											pattern="#" />
									</th>
									<th>
										<fmt:formatNumber value="${pagefxProcess.totalCount}"
											pattern="#" />
									</th>
									<th>
										<fmt:formatNumber value="${pagefxProcess.applyCount}"
											pattern="#" />
									</th>
									<th>
										<fmt:formatNumber value="${pagefxProcess.submmitCount}"
											pattern="#" />
									</th>
									<th>
										<fmt:formatNumber value="${pagefxProcess.breakCount}"
											pattern="#" />
									</th>
									<s:if test="viewStatus=='zjl'||viewStatus=='KH'">
										<th>
											<div class="processjindu" style="cursor: pointer;"
												onclick="showDetail('ProdEquipmentAction!findMachineByNum.action?id=${pagefxProcess.id}',this)"
												data="${pagefxProcess.submmitCount/pagefxProcess.totalCount*100}">
											</div>
										</th>
										<th>
											${pagefxProcess.nowAllJiepai}
										</th>
									</s:if>
									<th>
										${pagefxProcess.firstApplyDate}
									</th>
									<th>
										${pagefxProcess.submitDate}
									</th>
									<th>
										${pagefxProcess.processStatus}
									</th>
									<th>
										<label id="gw<s:property value="#addIndex" />">
											${pagefxProcess.gongwei}
										</label>
									</th>
									<th>
										<label id="sbno<s:property value="#addIndex" />">
											${pagefxProcess.shebeiNo}
										</label>
									</th>
									<th>
										<label id="sbno<s:property value="#addIndex" />">
											${pagefxProcess.matetag}
											<br>
											(${pagefxProcess.number})
										</label>
									</th>
									<th>
										<label id="sbno<s:property value="#addIndex" />">
											${pagefxProcess.measuring_no}
											<br>
											(${pagefxProcess.measuringMatetag})
										</label>
									</th>
									<th>
										<s:if test='#pagefxProcess.productStyle=="外委"'>
											<a
												href="OSWorkAction!findAllOSW.action?osWork.markID=${procard.markId}&osWork.lotId=${procard.selfCard}">${pagefxProcess.productStyle}</a>
										</s:if>
										<s:else>
							${pagefxProcess.productStyle}
							</s:else>
									</th>
									<th>
										${pagefxProcess.status}
									</th>
									<th>
										${pagefxProcess.pmiType}
									</th>
									<th>
										<s:if test="viewStatus=='zjl'">
											<a
												href="pagefxProcession!findUserByIdForDetails.action?idpagefxProcessess.userId}">
												${pagefxProcess.usernames}</a>
										</s:if>
										<s:else>${pagefxProcess.usernames}</s:else>
									</th>
									<th>
										<s:if test='#pagefxProcess.needSave=="是"'>
									是
								</s:if>
										<s:else>
									否
								</s:else>
									</th>
									<th style="max-width: 100px;">
										${pagefxProcess.glMarkId}
									</th>
									<th id="th_${pagefxProcess.id}">
										<input type="hidden" value="${pagefxProcess.id}"
											name="pagefxProcessId">
										<input type="hidden" value="${pagefxProcess.submmitCount}"
											name="pageProcessSubmmitCount">
										<s:if test="pageStatus!='history'">
											<s:property value="" />
											<s:if test='procard.status=="领工序"||procard.status=="已发料"'>
												<s:if test='#pagefxProcess.productStyle!="外委"'>
													<s:if test="#pagefxProcess.status!='完成'">
														<a href="javascript:;"
															onclick="showwlqueren('${pagefxProcess.id}',this)">物料确认</a>
														<br />
													</s:if>
													<s:if test='#pagefxProcess.status == "初始"'>
														<a href="javascript:;"
															onclick="showZjProcess('${pagefxProcess.id}','${procard.markId}',this)">自检</a>
													</s:if>
													<s:elseif test='#pagefxProcess.status == "已领"'>
														<!-- 如果提交量为0 则开始首检提交 -->
														<s:if
															test="#pagefxProcess.hadsj!='yes'&&#pagefxProcess.zjStatus=='yes'">
															<input type="button" value="提交"
																onclick="submitProcess('${pagefxProcess.id}','1','sj',this,'${pagefxProcess.pg.barcode}')"
																style="height: 30px;" />
															<input type="button" value="点检"
																onclick="isdj('${pagefxProcess.id}',this)"
																style="height: 30px;" />
														</s:if>
														<s:else>
															<s:if test='#pagefxProcess.pmiType=="强控"'>
																<input type="button" value="断电&提交" style="height: 30px;"
																	onclick="pmiAutoSubPro('${pagefxProcess.id}')" />
																<input type="button" value="点检"
																	onclick="isdj('${pagefxProcess.id}',this)"
																	style="height: 30px;" />
															</s:if>
															<s:else>
																<input type="button" value="提交" style="height: 30px;"
																	onclick="submitProcess('${pagefxProcess.id}','${pagefxProcess.pg.receiveNumber-pagefxProcess.pg.submitNumber-pagefxProcess.pg.breakCount}','',this,'${pagefxProcess.pg.barcode}')" />
																<input type="button" value="点检"
																	onclick="isdj('${pagefxProcess.id}',this)"
																	style="height: 30px;" />
															</s:else>
														</s:else>
													</s:elseif>
													<s:elseif test='#pagefxProcess.status =="自检"'>
														<input type="button" value="工位设备"
															onclick="changeMachine('${pagefxProcess.id}','<s:property value="#addIndex" />',this)"
															style="height: 30px;" />
														<input type="button" value="领取"
															onclick="getProcess('${pagefxProcess.id}',this)"
															style="height: 30px;" />
														<input type="button" value="点检"
															onclick="isdj('${pagefxProcess.id}',this)"
															style="height: 30px;" />
													</s:elseif>
													<s:elseif test='#pagefxProcess.pg.gxstatus == "待领"'>
														<input type="button" value="工位设备"
															onclick="changeMachine('${pagefxProcess.id}','<s:property value="#addIndex" />',this)"
															style="height: 30px;" />
														<input type="button" value="领取"
															onclick="getProcess('${pagefxProcess.id}',this)"
															style="height: 30px;" />
													</s:elseif>


												</s:if>
											</s:if>
										</s:if>
										<s:if test="viewStatus=='zjl'||viewStatus=='KH'">
											<div style="width: 100px;">
												<s:if
													test="#pagefxProcess.shebeiNo!=''&&#pagefxProcess.shebeiNo!=null">
													<a href="javascript:;"
														onclick="showDetail('ProdEquipmentAction!findMachineByNum.action?machine.no=${pagefxProcess.shebeiNo}&id=${pagefxProcess.id}',this)">
														<s:if
															test="#pagefxProcess.pmiType!=null&&#pagefxProcess.pmiType!=''">
															<img src="<%=basePath%>/images/PMIShebei.png"
																width="20px" height="20px" />
														</s:if> <s:else>机</s:else> </a>
												</s:if>
												<s:else>
													<font color="gray">机</font>
												</s:else>
												<s:if
													test="#pagefxProcess.gzstoreId!=''&&#pagefxProcess.gzstoreId!=null">
													<a
														href="GzstoreAction_findAll.action?gzstore.number=${pagefxProcess.matetag}">工</a>
												</s:if>
												<s:else>
													<font color="gray">工</font>
												</s:else>
												<s:if
													test="#pagefxProcess.measuringId!=''&&#pagefxProcess.measuringId!=null">
													<a
														href="MeasuringAction_saveMeasuring.action?measuring.measuring_no=${pagefxProcess.measuring_no}&flag=showhas">量</a>
												</s:if>
												<s:else>
													<font color="gray">量</font>
												</s:else>
												<s:if
													test='#pagefxProcess.zjStatus=="yes"&&#pagefxProcess.status!="初始"'>
													<a
														href="LogoStickerAction!findLogoSticker2.action?tag=manger&sticker.lotId=${procard.selfCard}&sticker.markId=${procard.markId}&sticker.processNO=${pagefxProcess.processNO}">检</a>
												</s:if>
												<s:else>
													<font color="gray">检</font>
												</s:else>
												<s:if test="procard.productStyle='试制'">
													<a
														href="ProcardAction!findGongyiGuifan.action?markid=${procard.markId}&processNO=${pagefxProcess.processNO}&id=0&banci=${procard.banci}">文</a>
												</s:if>
												<a
													href="ProcardAction!findGongyiGuifan.action?markid=${procard.markId}&processNO=${pagefxProcess.processNO}&id=1&banci=${procard.banci}">文</a>
												<s:else>

												</s:else>
											</div>
										</s:if>
										<s:else>
											<s:if test='#pagefxProcess.submmitCount>0'>
												<br />
												<a href="javascript:;"
													onclick="aginPrint('${pagefxProcess.id}')">补打</a>
											</s:if>
										</s:else>
									</th>
								</tr>
								<s:if test="viewStatus!='zjl'">
									<tr>
										<th align="left" colspan="30"
											style="background-color: red; border: 0px; margin: 0px; padding: 0px;">
											<div align="left"
												style="background-color: #00ff01; width:${pagefxProcess.submmitCount/pagefxProcess.totalCount*100}%; text-overflow: ellipsis; white-space: nowrap; height: 100%;">
												&nbsp;&nbsp;&nbsp;&nbsp;${pagefxProcess.processNO}工序完成进度:
												${pagefxProcess.submmitCount/pagefxProcess.totalCount*100}%
											</div>
										</th>
									</tr>
								</s:if>
								<s:set name="addIndex" value="#addIndex+1"></s:set>
							</s:iterator>
							<%--返修工序--%>
						</s:if>
					</s:iterator>
					<s:if test="isbcpqx">
						<tr>
							<th colspan="25">
								<input type="button" value="现场半成品接收" class="button0 gray"
									onclick="window.location.href='GoodsStoreAction!tobcpquickreceiver.action?goodsStore.goodsStoreMarkId=${procard.markId}&goodsStore.goodsStoreLot=${procard.selfCard}'" />
							</th>
						</tr>

					</s:if>
				</table>
			</div>
		</s:if>
		</center>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
		<script type="text/javascript">
//现场自检表
function showZjProcess(processId, markId, obj) {
	$("#showZjProcess").attr(
			"src",
			"ProcardAction!showZj.action?procardTemplate.markId=" + markId
					+ "&id=" + processId);
	$("#zjProcessDiv").show();
	$("#submitProcessDiv").hide();
	chageDiv("block");
	//单独设置弹出层的高度
	var thisTopHeight = $(obj).offset().top - 50;
	if (thisTopHeight < 0) {
		thisTopHeight = 0;
	}
	$('#contentDiv').css( {
		'top' : thisTopHeight + 'px'
	});
}

//物料确认
function showwlqueren(processId, obj) {
	$("#showZjProcess").attr("src",
			"ProcardAction!getProcessById.action?id=" + processId);
	$("#zjProcessDiv").show();
	$("#submitProcessDiv").hide();
	$("#collorProcessDiv").hide();
	$("#aginPrintDiv").hide();
	$("#bhgDiv").hide();
	chageDiv("block");
	//单独设置弹出层的高度
	var thisTopHeight = $(obj).offset().top - 50;
	if (thisTopHeight < 0) {
		thisTopHeight = 0;
	}
	$('#contentDiv').css( {
		'top' : thisTopHeight + 'px'
	});
}

//显示卡片已有工序
function getProcess(pageId, obj) {
	$.ajax( {
				type : "POST",
				url : "ProcardAction!findProcess.action",
				dataType : "json",
				data : {
					id : pageId
				},
				success : function(msg) {
					if (msg.success) {
						$("#ProcessTab").empty();
						$("#ProcessTab").append(
								"<tr>" + "<th colspan='9'>领取工序</th>" + "</tr>"
										+ "<tr>" + "<th>工序号</th>"
										+ "<th>名称</th>" + "<th>可领取数量</th>"
										+ "<th>工位</th>" + "<th>设备编号</th>"
										+ "<th>是否并行</th>" + "<th>是否首检</th>"
										+ "<th>领取数量</th>"
										+ "<th>卡号(请刷员工卡)</th>" + "</tr>");
						var tableHtml = "";
						var forCount = 0;
						$
								.each(
										msg.data,
										function(i, process) {
											var keling = process.totalCount;
											var processNumbers = process.totalCount;
											var wlqrcount=process.wlqrcount ==null?0:process.wlqrcount;
											var applyCount = process.applyCount ==null?0:process.applyCount;
											var	keling0 = wlqrcount-applyCount;
											if(keling>keling0){
												keling =keling0;
											}
											processNumbers = keling;
											var readonly = "";
											if (process.zjStatus != null
													&& process.zjStatus == "yes"
													&& (process.hadsj == null || process.hadsj == "no")) {
												readonly = 'readonly="readonly"';
											}
											if(keling == 0){
												readonly = 'readonly="readonly"';
											}
											//如果未领取过，开始首检
<%--											if (process.applyCount == 0) {--%>
<%--												readonly = "readonly = 'readonly'";--%>
<%--												processNumbers = 1;--%>
<%--											} else if (process.submmitCount == 0) {--%>
<%--												return true;--%>
<%--											} else {--%>
<%--												processNumbers = process.totalCount--%>
<%--												-process.applyCount;--%>
<%--											}--%>
											var msg = process.msg;
											tableHtml += '<tr align="center"><td>'
													+ ''
													+ process.processNO
													+ '</td><td>'
													+ process.processName
													+ '</td><td>'
													+ keling
													+ '<input type="hidden" id="totalCount'
													+ forCount
													+ '" value="'
													+ keling
													+ '"/></td><td>'
													+ process.gongwei
													+ '</td><td>'
													+ process.shebeiNo
													+ '</td><td>'
													+ process.processStatus
													+ '</td><td>'
													+ process.zjStatus
													+ '</td>';
											var status = process.status;
											var userCardId = process.userCardId;
											if(msg!=null && msg.length>0){
												tableHtml+='<td colspan="2">'+msg+'</td>';
											}else{
											//判断是否领过
											tableHtml+=	'<td> <input type="hidden"  name="processIds" value="'
													+ process.id
													+ '"/>'
													+ '<input type="text" '
													+ ' id="processNumbers'
													+ forCount
													+ '" name="processNumbers" value="'
													+ processNumbers
													+ '"  onkeyup="numyanzheng(this,&apos;zhengshu&apos;)' +
													';maxlqnum(this,'+keling+')"' +
													' onblur="numyanzheng(this,&apos;zhengshu&apos;);maxlqnum(this,'+keling+')" /></td><td>'
<%--											if (status == '自检'--%>
<%--													|| userCardId == '') {--%>
												tableHtml += '<input type="text" id="processCards'
														+ forCount
														+ '" value="'
														+ "${Users.cardId}"
														+ '" name="list['
														+ forCount
														+ ']"  onchange="panding('+forCount+','+process.id+'),changeValue(this,'+process.id+','+forCount+')" />'
														+ '<input onclick="uploadFile(this,'
														+ forCount
														+ ','+process.id+')" type="button" value="多 人" id="duorenButton'
														+ forCount
														+ '" />'
														+ '<input type="radio" value="gerenCard'
														+ forCount
														+ '" name="card'
														+ forCount
														+ '" checked = "checked" onclick= "gerenCard('
														+ forCount
														+ ','+process.id+')"> 个人卡 '
														+ '<input type="radio" value="groupCard'
														+ forCount
														+ '" name="card'
														+ forCount
														+ '" onclick= "groupCard('
														+ forCount
														+ ','+process.id+')" >小组卡 '
														+ '<div id="fileDiv_'
														+ forCount
														+ '" style="display: none;"></div></td></tr>';
<%--											} else {--%>
<%--												var userCardId = process.userCardId;--%>
<%--												var cards = userCardId--%>
<%--														.split(",");--%>
<%--												for ( var i = 0; i < cards.length; i++) {--%>
<%--													tableHtml += '<input type="text" id="processCards'--%>
<%--															+ forCount--%>
<%--															+ '" name="list['--%>
<%--															+ forCount--%>
<%--															+ ']"  value="'--%>
<%--															+ cards[i]--%>
<%--															+ '"/><br/>';--%>
<%--												}--%>
<%--												tableHtml += '   </td></tr>';--%>
<%--											}--%>
											}
											forCount++;
										});

						tableHtml += "<tr>"
								+ "<th colspan='9'>"
								+ "<input id='receiveProce' type='button' value='领取' "
								+ "style='height:50px;width:80px;' onclick='collorForm()' /><div id='showreceiveWait' style='display:none;'></div></th>"
								+ "</tr>"
						$("#ProcessTab").append(tableHtml);
						$("#zjProcessDiv").hide();
						$("#submitProcessDiv").hide();
						$("#collorProcessDiv").show();
						chageDiv("block");
						$("#processCards0").focus();
						$("#processCards0").select();
						$("#contentDiv").removeAttr("css");
						$("#contentDiv").css("left", "0");
						$("#contentDiv").addClass("contentDiv100");//关闭统一样式

						//单独设置弹出层的高度
						var thisTopHeight = $(obj).offset().top - 160;
						$('#contentDiv').css( {
							'top' : thisTopHeight + 'px'
						});
					} else {
						alert(msg.message);
					}
				}
			});
}

var fileDivHTML = "";
var count = 0;
function uploadFile(obj, few,id) {
	var fileDiv = document.getElementById("fileDiv_" + few);
	fileDiv.style.display = "block";
	fileDivHTML = "<div id='file" + count
			+ "' style='padding-left:20px;' align='left'><input type='text' name='list["+ few + "]' onchange='changeValue(this,"+id+","+few+")' />" 
			+"<input type='radio' name='part" + count + "' id='hz" + count + "' checked='checked' onclick='hzorfz(\"合作\"," + count + ")'>合作 <input type='radio' name='part" + count + "' id='fz" + count + "' onclick='hzorfz(\"辅助\"," + count + ")'>辅助 " 
			+"<input id='fztextf" + count + "' style='display: none;' disabled='disabled'  name='list["+ few + "]' value=';'/>" 
			+"<input id='fztexts" + count + "' style='display: none;width: 30px;' disabled='disabled'  name='list["+ few + "]'/>"
			+"<lable id='fzbls" + count + "' style='display: none;'>%(奖金比例)</lable>"
			+"<a href='javascript:delFile(" + count + "," + few+")'>删除</a></div>";
	fileDiv.insertAdjacentHTML("beforeEnd", fileDivHTML);
	count++;
}

function delFile(obj, few) {
	document.getElementById("file" + obj).parentNode.removeChild(document
			.getElementById("file" + obj));
	count--;
}

//领取流水卡片wxf
function collorForm() {
	if ($("#processNumbers").val() == "") {
		alert("请填写领取数量!");
		$("#processNumbers").select();
	} else if ($("#processCards0").val() == "") {
		alert("请刷员工卡!");
		$("#processCards0").select();
	} else if (parseFloat($("#totalCount").val()) < parseFloat($(
			"#processNumbers").val())) {
		alert("领取数量不能大于" + $("#totalCount").val()) + "件";
	} else {
		$("#receiveProce").hide();
		$("#showreceiveWait").html("<font color='green'>领取工序中....请等待!</font>");
		$("#showreceiveWait").show();
		$
				.ajax( {
					type : "POST",
					url : "ProcardAction!collorProcess.action",
					dataType : "json",
					data : $("#collorProcess").serialize(),
					success : function(msg) {
						/*领取成功后推送led信息*/
						$.ajax( {
							type : "POST",
							url : "ProcardAction!sendLedMs.action?pageStatus=lingqu",
							dataType : "json",
							data : $("#collorProcess").serialize(),
							success : function(msg) {
							}
						});
						alert(msg);
						if (msg == "领取成功") {
							window.location = "ProcardAction!findProcardByRunCard2.action?id=${procard.id}&pageStatus=${pageStatus}";
						} else {
							$('#receiveProce').show();
							$('#showreceiveWait').html("");
							$("#showreceiveWait").hide();
						}
					},
					error : function(msg) {
						alert("领取失败!");
						$('#receiveProce').show();
						$('#showreceiveWait').html("");
						$("#showreceiveWait").hide();
					}
				});
	}
}
var sumNumber = 0;  
//提交工序赋值 
function submitProcess(processId, number, sjstatus, obj,proLgbarcode) {
	sumNumber =number;
<%--	if (isdj(processId, obj, "提交")) {--%>
		$("#submitProId").val(processId);
		$("#subNumber").val(number);
		if (sjstatus == 'sj') {
			$("#subNumber").attr("readonly", "readonly");
		} else {
			$("#subNumber").removeAttr("readonly");
		}
		$("#proLgbarcode").val(proLgbarcode);
		$("#zjProcessDiv").hide();
		$("#submitProcessDiv").show();
		$("#collorProcessDiv").hide();
		getUnit("flUnit0");
		chageDiv("block");
		//单独设置弹出层的高度
		var thisTopHeight = $(obj).offset().top - 160;
		$('#contentDiv').css( {
			'top' : thisTopHeight + 'px'
		});
		
		$("#contentDiv").removeAttr("css");
		$("#contentDiv").css("left", "0");
		$("#contentDiv").addClass("contentDiv100");//关闭统一样式
		
		var main = $("#showProcess", window.parent.document);//找到iframe对象
		if(main!=null){
			var thisheight;
			thisheight = document.body.scrollHeight;
			thisheight =  parseFloat("1000")+parseFloat(thisheight);
			main.height(thisheight);
		}
<%--	}--%>
}
//提交工序
function submitForm() {
	$("#submitProce").hide();
	$("#showWait").html("<font color='green'>提交工序中....请等待!</font>");
	$
			.ajax( {
				type : "POST",
				url : "ProcardAction!submitProcess.action",
				dataType : "json",
				data : $("#submitProcess").serialize(),
				success : function(datas) {
					if (datas.message == "提交工序成功") {
						/*提交成功后推送led信息*/
						$.ajax( {
							type : "POST",
							url : "ProcardAction!sendLedMs.action?pageStatus=submit",
							dataType : "json",
							data : {
								"processIds" : $("#submitProId").val()
							},
							success : function(msg) {
							}
						});
						
						var data = datas.data;
						var process = data.process;
						var procard = data.procard;
						$("#printMarkId").html(procard.markId);
						$("#printProName").html(procard.proName);
						$("#printYwMarkId").html(procard.ywMarkId);
						$("#printSelfCard").html(procard.selfCard);
						$("#printProcessNO").html(process.processNO);
						$("#printProcessName").html(process.processName);
						$("#printSubmmitCount").html(process.submmitCount);
						$("#printUsernames").html(process.usernames);
						$("#printSubmitDate").html(process.submitDate);
						$("#nextProcess").html(process.guding);
						$("#orderNum").html(procard.orderNumber);
						$("#printbanben").html(procard.banBenNumber);
						getQRCode(
								150,
								150,
								'<%=basePath%>/ProcardAction.action?id='+procard.id,
								'erweima');
<%--						var barcode = document.getElementById('bar'), str = barcode, options = {--%>
<%--							format : "CODE128",--%>
<%--							displayValue : true,--%>
<%--							fontSize : 18,--%>
<%--							height : 100--%>
<%--						};--%>
<%--						JsBarcode(barcode, str, options);//原生--%>
						if (process.checkIdea != null){
							if(process.reProductId!=null){
							$("#cxtShow").html(
									"<img  width='150px' height='80px' src='/upload/file/fxtz/"
											+ process.checkIdea + "' />");
							}else{
							$("#cxtShow").html(
									"<img  width='150px' height='80px' src='/upload/file/processTz/"
											+ process.checkIdea + "' />");
							}
							}
						$("#submitProcess").hide();
						$("#printDiv").show();
						//pagePrint('printgxDiv', 'view');
						//window.location = "ProcardAction!findProcardByRunCard2.action?id=${id}&pageStatus=${pageStatus}";
						if(process.measuringNumber!=null){
							$("#printMarkId2").html(procard.markId);
							$("#printProName2").html(procard.proName);
							$("#printYwMarkId2").html(procard.ywMarkId);
							$("#printSelfCard2").html(procard.selfCard);
							$("#printProcessNO2").html(process.processNO);
							$("#printProcessName2").html(process.processName);
							$("#printSubmmitCount2").html(process.submmitCount);
							$("#printUsernames2").html(process.usernames);
							$("#printSubmitDate2").html(process.submitDate);
							$("#orderNum2").html(procard.orderNumber);
							$("#printbanben2").html(procard.banBenNumber);
							getQRCode(
									150,
									150,
									process.measuringNumber,
									'erweima2');
							if (process.checkIdea != null){
								if(process.reProductId!=null){
								$("#cxtShow2").html(
										"<img  width='150px' height='80px' src='/upload/file/fxtz/"
												+ process.checkIdea + "' />");
								}else{
								$("#cxtShow2").html(
										"<img  width='150px' height='80px' src='/upload/file/processTz/"
												+ process.checkIdea + "' />");
								}
								}
							$("#sjmore").show();
							$("#printDiv2").show();
							$("#sjbsbut").show();
						}
					} else {
						/*提交成功后推送led信息*/
						$.ajax( {
							type : "POST",
							url : "ProcardAction!sendLedMs.action",
							dataType : "json",
							data : {
								"processIds" : $("#submitProId").val()
							},
							success : function(msg) {
							}
						});
						$('#submitProce').show();
						$('#showWait').html("");
						alert(datas.message);
					}
				}
			});
}
//PMI自动提交工序
function pmiAutoSubPro(processId) {
	$("#submitProce").hide();
	$("#showWait").html("<font color='green'>自动提交工序中....请等待!</font>");
	$
			.ajax( {
				type : "POST",
				url : "ProcardAction!pmiAutosubPro.action",
				dataType : "json",
				data : {
					"process.id" : processId,
					"id" : "${procard.id}"
				},
				success : function(datas) {
					if (datas.message == "提交工序成功") {
						var data = datas.data;
						var process = data.process;
						var procard = data.procard;
						$("#printMarkId").html(procard.markId);
						$("#printProName").html(procard.proName);
						$("#printYwMarkId").html(procard.ywMarkId);
						$("#printSelfCard").html(procard.selfCard);
						$("#printProcessNO").html(process.processNO);
						$("#printProcessName").html(process.processName);
						$("#printSubmmitCount").html(process.submmitCount);
						$("#printUsernames").html(process.usernames);
						$("#printSubmitDate").html(process.submitDate);
						$("#nextProcess").html(process.guding);
						$("#orderNum").html(procard.orderNumber);
						$("#printbanben").html(procard.banBenNumber);
						getQRCode(
								150,
								150,
								'<%=basePath%>/ProcardAction.action?id='+procard.id,
								'erweima');
<%--						var barcode = document.getElementById('bar'), str = "<%=basePath%>ProcardAction!code.action?barcode="--%>
<%--								+ barcode, options = {--%>
<%--							format : "CODE128",--%>
<%--							displayValue : true,--%>
<%--							fontSize : 18,--%>
<%--							height : 100--%>
<%--						};--%>
<%--						JsBarcode(barcode, str, options);//原生--%>
						pagePrint('printgxDiv', 'view');
						window.location = "ProcardAction!findProcardByRunCard2.action?id=${id}&pageStatus=${pageStatus}";
					} else {
						$('#submitProce').show();
						$('#showWait').html("");
						alert(datas.message);
					}
				}
			});
}
//补打工序
function aginPrintForm() {
	$
			.ajax( {
				type : "POST",
				url : "ProcardAction!printProcess.action",
				dataType : "json",
				data : $("#aginPrintForm").serialize(),
				success : function(datas) {
					if (datas.success) {
						var data = datas.data;
						var process = data.process;
						var procard = data.procard;
						var barcode = data.barcode;
						$("#printMarkId").html(procard.markId);
						$("#printProName").html(procard.proName);
						$("#printYwMarkId").html(procard.ywMarkId);
						$("#printSelfCard").html(procard.selfCard);
						$("#printProcessNO").html(process.processNO);
						$("#printProcessName").html(process.processName);
						$("#printSubmmitCount").html(process.submmitCount);
						$("#printUsernames").html(process.usernames);
						$("#printSubmitDate").html(process.submitDate);
						$("#nextProcess").html(process.guding);
						$("#orderNum").html(procard.orderNumber);
						$("#printbanben").html(procard.banBenNumber);
						getQRCode(
								150,
								150,
								'<%=basePath%>/ProcardAction.action?id='+procard.id,
								'erweima');
<%--						var barcode = document.getElementById('bar'), str = "<%=basePath%>ProcardAction!code.action?barcode="--%>
<%--								+ barcode, options = {--%>
<%--							format : "CODE128",--%>
<%--							displayValue : true,--%>
<%--							fontSize : 18,--%>
<%--							height : 100--%>
<%--						};--%>
<%--						JsBarcode(barcode, str, options);//原生--%>
						if (process.checkIdea != null)
							if(process.reProductId!=null){
							$("#cxtShow").html(
									"<img  width='150px' height='80px' src='/upload/file/fxtz/"
											+ process.checkIdea + "' />");
							}else{
							$("#cxtShow").html(
									"<img  width='150px' height='80px' src='/upload/file/processTz/"
											+ process.checkIdea + "' />");
							}
						pagePrint('printgxDiv', 'view');
						//window.location = "ProcardAction!findProcardByRunCard2.action?id=${id}&pageStatus=${pageStatus}";
					} else {
						alert(datas.message);
					}
				}
			});
}

function aginPrint(printProcessId,obj) {
	$("#printProId").val(printProcessId);
	$("#zjProcessDiv").hide();
	$("#submitProcessDiv").hide();
	$("#collorProcessDiv").hide();
	$("#bhgDiv").hide();
	$("#csblDiv").hide();
	$("#aginPrintDiv").show();
	chageDiv("block");
}
//显示提交不合格div
function showbhg(bhgProcessId) {
	$("#bhgProcessId").val(bhgProcessId);
	$("#zjProcessDiv").hide();
	$("#submitProcessDiv").hide();
	$("#collorProcessDiv").hide();
	$("#aginPrintDiv").hide();
	$("#csblDiv").hide();
	$("#bhgDiv").show();
	chageDiv("block");
	 $.ajax( {
		type : "POST",
		url : "ProcardAction!findbreaksubmitByprocesId.action",
		data : {
			id : bhgProcessId,
		},
		dataType : "json",
		success : function(data) {
			if (data.data!=null) {
				$("#bhgNumber").val(data.tjbreakcount);
				$("#breakgroup1").attr("disabled","disabled");
				$("#breakgroup2").attr("checked","checked");
				$("#zzbreakType").attr("checked","checked");
				$("#wgbreakType").attr("disabled","disabled");
				$("#breaksubmit_id").val(data.id);
				$("#break_pageStatus").val("QR");
				$("#break_button").val("确认");
			}else if(data.success){
				$("#breakgroup1").attr("disabled","disabled");
			}else{
				$("#bhgNumber").val("");
				$("#breakgroup1").removeAttr("disabled");
				$("#wgbreakType").removeAttr("disabled");
				$("#breaksubmit_id").val("");
				$("#break_pageStatus").val("");
				$("#break_button").val("提交");
			}
			
		}
	})
	
}


//工序完成进度
$(".processjindu").each(function(i) {
	var hk_val = $(this).attr('data');
	$(this).radialIndicator( {
		barColor : ( {
			0 : '#FF0000',
			66 : '#FFFF00',
			100 : '#33CC33'
		}),
		barWidth : 3,
		radius : 22,
		initValue : hk_val,
		roundCorner : true,
		percentage : true
	});
});

function showDetail(url, obj) {
	$("#showCollorProcessIf").attr("src", url);
	$("#showCollorProcessDiv").show();
	$("#zjProcessDiv").hide();
	$("#submitProcessDiv").hide();
	$("#collorProcessDiv").hide();
	$("#aginPrintDiv").hide();
	chageDiv("block");
	//单独设置弹出层的高度
	var thisTopHeight = $(obj).offset().top - 100;
	if (thisTopHeight < 0) {
		thisTopHeight = 0;
	}
	$('#contentDiv').css( {
		'top' : thisTopHeight + 'px'
	});

	//popWin.showWin("1024", "700", title, url);
}
function changeMachine(processid, index, obj) {
	$
			.ajax( {
				type : "POST",
				url : "ProcardAction!toChangeMachine.action",
				dataType : "json",
				data : {
					id : processid
				},
				success : function(data) {
					$("#ProcessTab").empty();
					$("#ProcessTab")
							.append(
									"<tr>"
											+ "<th colspan='5'>选择机器(设备编号为manual的设备表示虚拟手工设备可重复选择)</th>"
											+ "</tr>" + "<tr>"
											+ "<th>序号</th><th>设备名称</th>"
											+ "<th>工位</th>" + "<th>设备编号</th>"
											+ "<th>操作</th>" + "</tr>");
					var tableHtml = "";
					var forCount = 0;
					var i = 1;
					$(data.data2)
							.each(
									function() {
										var bntString = '<input type="button" onclick="selectMachine(\''
												+ processid
												+ '\',\''
												+ this.id
												+ '\',\''
												+ this.no
												+ '\',\''
												+ this.workPosition
												+ '\',\''
												+ index + '\')"  value="选择"/>';
										var bgcolor = '';
										if (data.data1.shebeiNo == this.no
												&& data.data1.gongwei == this.workPosition) {
											bntString = "<font color='red'><b>已选择</b></font>";
											bgcolor = 'bgcolor="#e6f3fb"';
										}
										tableHtml += '<tr align="center"'
												+ bgcolor + '><td>' + i
												+ '</td><td>' + this.name
												+ '</td><th>'
												+ this.workPosition
												+ '</th><td>' + this.no
												+ '</td><td>' + bntString
												+ '</td></tr>';
										i++;
									});

					$("#ProcessTab").append(tableHtml);
					$("#zjProcessDiv").hide();
					$("#submitProcessDiv").hide();
					$("#collorProcessDiv").show();
					chageDiv("block");

					//单独设置弹出层的高度
					var thisTopHeight = $(obj).offset().top - 200;
					$('#contentDiv').css( {
						'top' : thisTopHeight + 'px'
					});
				}
			});
}

function selectMachine(processid, machineId, no, workPosition, index) {
	$.ajax( {
		type : "POST",
		url : "ProcardAction!changeMachine.action",
		dataType : "json",
		data : {
			'process.id' : processid,//工序id
			id : machineId
		//机器Id
		},
		success : function(msg) {
			if (msg == "true") {
				$("#sbno" + index).html(no);
				$("#gw" + index).html(workPosition);
				alert("选择成功");
				$("#collorProcessDiv").hide();
				chageDiv("none");
				//return;
	}else{
		alert(msg);
	}
}
	});

}
function isdj(id, obj, tag) {
	var bool = true;
	$.ajax( {
		type : "POST",
		url : "MachineDayYZSJAction_getMachinebyproessId.action",
		dataType : "json",
		async : false,
		data : {
			id : id
		},
		success : function(data) {
			if (data.success && data.message == '是') {
				var mdy = data.data;
				if (mdy == null || mdy.dj_status != '已点检') {
					$("#showZjProcess").attr(
							"src",
							"DJNRAction_getdjnrbyId.action?id="
									+ mdy.machine_id);
					$("#zjProcessDiv").show();
					$("#submitProcessDiv").hide();
					$("#collorProcessDiv").hide();
					chageDiv("block");
					//单独设置弹出层的高度
					var thisTopHeight = $(obj).offset().top - 100;
					if (thisTopHeight < 0) {
						thisTopHeight = 0;
					}
					$('#contentDiv').css( {
						'top' : thisTopHeight + 'px'
					});
					bool = false;
				}
			}else{
				alert("该设备无需点检!");
			}
		}
	});
	return bool;
}
function dj1(machineId, obj, tag) {
	var bool = false;
	$.ajax( {
		type : "POST",
		url : "MachineDayYZSJAction_getmdybymachineId.action",
		dataType : "json",
		async : false,
		data : {
			id : machineId
		},
		success : function(data) {
			if (data != "已点检") {
				$("#showZjProcess").attr("src",
						"DJNRAction_getdjnrbyId.action?id=" + machineId);
				$("#zjProcessDiv").show();
				$("#submitProcessDiv").hide();
				$("#collorProcessDiv").hide();
				chageDiv("block");
				//单独设置弹出层的高度
				var thisTopHeight = $(obj).offset().top - 500;
				if (thisTopHeight < 0) {
					thisTopHeight = 0;
				}
				$('#contentDiv').css( {
					'top' : thisTopHeight + 'px'
				});
			} else {
				bool = true;
			}
		}
	});
	return bool;
}
$(function() {
	addtjbhg();
	var main = $(window.parent.document).find("#showProcess");//找到iframe对象
	if (main != null) {
		var thisheight = document.body.scrollHeight;
		var thisheight2 = parseFloat(thisheight);
		main.height(thisheight2 + 1000);//为iframe高度赋值如果高度小于500，则等于500，反之不限高，自适应
	}
})

function showwldt(id) {
	$("#" + id).show();
}
function hidewldt(id) {
	$("#" + id).hide();
}
function gerenCard(num,id) {
	$("#duorenButton" + num).removeAttr("style","");
	$("#processCards" + num).attr("name", "list["+num+"]");
	$("#processCards" + num).attr("onchange", "panding("+num+","+id+")");
}
function groupCard(num,id) {
	$("#duorenButton" + num).attr("style", "display: none")
	$("#processCards" + num).attr("name", "");
	$("#processCards" + num).attr("onchange", "fuzhi("+num+","+id+")");
 	fuzhi(num,id);
}
function maxlqnum(obj,num){
	if(obj!=null && obj.value!=''){
		var value = obj.value;
		if(value>num){
			alert('领取数量超出可领取数量，请重新输入！');
			obj.value = '';
		}
	}
}
function panding(num,id){
	 var cardNumber = $("#processCards"+num).val();
	 if(cardNumber!=''){
		 $.ajax( {
		type : "POST",
		url : "ProcardAction!qxUserscardBd.action",
		data : {
			cardNumber : cardNumber,
		},
		dataType : "json",
		success : function(data) {
			if (data) {
				
			}
		}
	})
	 }
		
}
function fuzhi(num,id){
	var cardNumber = $("#processCards"+num).val();
	if(cardNumber!='' && id!=''){
		$.ajax( {
		type : "POST",
		url : "ProcardAction!findUserByGroupCard.action",
		data : {
			cardNumber : cardNumber,
			id :id
		},
		dataType : "json",
		success : function(data) {
			var count = 0;
			if (data != null) {
				var fileDiv = document.getElementById("fileDiv_" + num);
					$(data).each(function(){
						count++;
						fileDivHTML = "<div id='file" + count
							+ "' style='padding-left:20px;' align='left'><input name='list["
							+ num +"]' value = '"+this+"'/></div>";
							fileDiv.insertAdjacentHTML("beforeEnd", fileDivHTML);
					});
			}
		}
	})
	}
	
}
<%--function dj(machineId,processid,no,workPosition,index){--%>
<%--	$.ajax({--%>
<%--			type : "POST",--%>
<%--			url : "MachineDayYZSJAction_getmdybymachineId.action",--%>
<%--			dataType : "json",--%>
<%--			data : {id:machineId},--%>
<%--			success : function(data){--%>
<%--				if(data!="点检中" && data!="已点检"){--%>
<%--					$("#xiugaiIframe").attr("src",--%>
<%--						"DJNRAction_getdjnrbyId.action?id="+machineId+"&pageStatus=xz"--%>
<%--			 	 		 +"&processid="+processid+"&no="+no+"&workPosition="+workPosition--%>
<%--			  	 		+"&index="+index);--%>
<%--					$("#djProcessDiv").show();--%>
<%--					$("#collorProcessDiv").hide();--%>
<%--				}--%>
<%--				--%>
<%--			}--%>
<%--			--%>
<%--		--%>
<%--		})--%>
<%--}--%>
<%--function getdjnrbyId(id){--%>
<%--	$.ajax({--%>
<%--		taype:	"POST",--%>
<%--		url:	"DJNRAction_getdjnrbyId.action?id="+id,--%>
<%--		dataType:"json",--%>
<%--		success : function(data){--%>
<%--			$("#ProcessTab").append('<tr align="center"><td>序号</td><td>点检项</td><td>点检结果</td></tr>');--%>
<%--			if(data!=null&&data.length>0){--%>
<%--				for(var i=0;i<data.length;i++){--%>
<%--					$("#ProcessTab").append('<tr align="center"><td>'+(i+1)+'</td><td>'+data[i][1]+'</td><td><input size="200px" id="hege'+i+'" name="mdy.mddList['+i+'].dj_status" type="radio"checked="checked" value="正常">' +--%>
<%--					'<input id="buhege'+i+'"name="mdy.mddList['+i+'].dj_status" type="radio"value="异常"><input id="wx'+i+'"name="mdy.mddList['+i+'].dj_status" type="radio"value="维修中"></td></tr>')--%>
<%--				}--%>
<%--				$("#ProcessTab").append('<tr><td colspan="3" align="center"></td><input class="input" type="submit" value="确定" /></tr>');--%>
<%--			}--%>
<%--		}--%>
<%--	});--%>
<%--}--%>
function updateProcard(id){
	$.ajax( {
		type : "post",
		url : "ProcardAction!procardUpdateFirst.action",
		data : {
			id:id
		},
		dataType : "json",
		success : function(data) {
			if(data=="true"){
				alert("更新完成!");
				window.parent.reload();
			}else if(confirm(data+"是否继续?")){
				$.ajax( {
					type : "post",
					url : "ProcardAction!procardUpdateSecond.action",
					data : {
						id:id
					},
					dataType : "json",
					success : function(data) {alert(data);
						if(data=="true"){
							alert("更新完成!");
							window.parent.reload();
						}else{
							alert(data);
						}
					}
				});
			}
		}
	});
}
function jihuo(id){
	$("#jha").hide();
	$("#jhlabel").show();
	$.ajax( {
		type : "post",
		url : "ProcardAction!jihuoAgain.action",
		data : {
			id:id
		},
		dataType : "json",
		success : function(data) {
			alert(data);
			$("#jha").show();
			$("#jhlabel").hide();
		}
	});
}
function changeFuliao(obj) {
	if (obj == '是') {
		$("#fuliaoTb").show();
	} else {
		$("#fuliaoTb").hide();
	}
}
var fuliaoSize = 1;
var maxFuLiaoIndex = fuliaoSize - 1;//辅料最大下标
function addFuliaoLine() {
	maxFuLiaoIndex++;
	var html = "<tr id='fuliaoTr"
			+ maxFuLiaoIndex
			+ "'><td style='border-top: 0px;border-left: 0px' align='center'>"
			+ "<SELECT id='type"
			+ maxFuLiaoIndex
			+ "' name='process.fuliaoList["
			+ maxFuLiaoIndex
			+ "].type'><option>请选择</option><option >备件</option><option >金属五交材料</option><option>工具</option><option>办公用品</option><option>杂品</option><option>金属五交</option><option>工装</option><option>五金</option><option>包装物</option></SELECT> </td>"
			+ "<td style='border-top: 0px;border-left: 0px' align='center'> "
			+ "<SELECT id='type"
			+ maxFuLiaoIndex
			+ "' name='process.fuliaoList["
			+ maxFuLiaoIndex
			+ "].name'><option>焊丝</option><option >气体</option><option >手套</option><option>砂轮打磨片</option><option>导电嘴</option><option>润滑油</option>" 
			+"<option>油漆性记号笔</option><option>防护玻璃片</option></SELECT> </td>"
			+ "<td style='border-top: 0px;border-left: 0px' align='center'><input id='specification"
			+ maxFuLiaoIndex
			+ "' name='process.fuliaoList["
			+ maxFuLiaoIndex
			+ "].specification'> </td>"
			+ "<td style='border-top: 0px;border-left: 0px' align='center'><input id='unit"
			+ maxFuLiaoIndex
			+ "' name='process.fuliaoList["
			+ maxFuLiaoIndex
			+ "].outCount'> </td>"
			+"<td style='border-top: 0px;border-left: 0px' align='center'><select id='flUnit"+maxFuLiaoIndex+"' name='process.fuliaoList["
			+ maxFuLiaoIndex
			+ "].unit'></select></td>"
			+ "<td style='border-top: 0px;border-left: 0px;border-right: 0px' align='center'><input type='button' value='删除' onclick='deleteFuliaoLine("
			+ maxFuLiaoIndex + ")'> </td>" + "</tr>";
	$("#fuliaoTb>tbody>tr").eq(fuliaoSize).after(html);
	fuliaoSize++;
	getUnit("flUnit"+maxFuLiaoIndex);
}

function deleteFuliaoLine(index) {
	if (fuliaoSize == 1) {
		alert("至少一条辅料");
		return;
	}
	if ((maxFuLiaoIndex - index) == 0) {
		maxFuLiaoIndex--;
	}
	fuliaoSize--;
	$("#fuliaoTr" + index).remove();
}
//得到仓区;
function getcangqu(){
	var warehouse = $("#warehouse").val();
	if(warehouse!=""){
			$.ajax( {
		type : "POST",
		url : "WarehouseAreaAction_findwaListByNO.action",
		data : {
			wareHouseName:warehouse
		},
		dataType : "json",
		success : function(data) {
			if (data != null) {
				$("#goodHouseName").empty();
				$("#goodHouseName").append('<option value="">--请选择--</option>')
				$(data).each(function(){
						$("#goodHouseName").append('<option value='+this.goodHouseName+'>'+this.goodHouseName+'</option>');
					});
			}
				var tinyselect = $("#goodHouseName_td").children(".tinyselect");
						if (tinyselect[0] != null) {
							document.getElementById("goodHouseName_td").removeChild(
									tinyselect[0]);
						}
						$("#goodHouseName").tinyselect();
		}
	});
	}
}
<%----%>
//得到库位
function getkuwei(obj){
	var warehouse = $("#warehouse").val();
	if(warehouse != "" && obj!=null && obj.value != ""){
			$.ajax( {
		type : "POST",
		url : "WarehouseAreaAction_findwnListByNO.action",
		data : {
			wareHouseName:warehouse,
			cangqu:obj.value
		},
		dataType : "json",
		success : function(data) {
			if (data != null) {
				$("#goodsStorePosition").empty();
				$("#goodsStorePosition").append('<option value="">--请选择--</option>')
				$(data).each(function(){
						$("#goodsStorePosition").append('<option value='+this.number+'>'+this.number+'</option>');
					});
			}
			var tinyselect =  $("#goodsStorePosition_td").children(".tinyselect");
						if (tinyselect[0] != null) {
							document.getElementById("goodsStorePosition_td").removeChild(
									tinyselect[0]);
						}
			$("#goodsStorePosition").tinyselect();
		}
	});
	}
}

function selectcq(){
	$("#warehouse").find("option[value='半成品库']").attr("selected",true);
	getcangqu();
}
function hzorfz(type,index){
	if(type=='合作'){
		$("#fztextf"+index).attr("disabled","disabled");
		$("#fztexts"+index).hide();
		$("#fztexts"+index).attr("disabled","disabled");
		$("#fzbls"+index).hide();
	}else if(type=='辅助'){
		$("#fztextf"+index).removeAttr("disabled");
		$("#fztexts"+index).show();
		$("#fztexts"+index).removeAttr("disabled");
		$("#fzbls"+index).show();
	}
}
function caigou(id){
	
}
function showProcessAndWg(num,str,idname){
	var  id =  $("#bhgProcessId").val();
	if(num == '1'){
		id=$("#submitProId").val();
	}
	$.ajax( {
		type : "POST",
		url : "ProcardAction!findProcessAndwgProcard.action",
		data : {id: id},
		dataType : "json",
		success : function(data) {
			var trhtml = '<th colspan="2">';
			if(data!=null && data.length>0){
		for(var i=0;i<data.length;i++){
			var processAndWgProcardTem = data[i];
			var markId = processAndWgProcardTem.wgprocardMardkId;
			trhtml+='<input type="checkbox" value = '+markId+' name="markIds" id="markIds'+i+'" >'+markId+'&nbsp;&nbsp;'
				+str+':<input type = "text"  style="width:75px;" id="breaks'+i+'" onchange= "numpanduan(&apos;'+markId+'&apos;,this,'+id+','+i+','+num+')" >&nbsp;&nbsp; ' 
				+' <input type="hidden" name="breakscount" id="hidebreaks_'+num+''+i+'"><span id="spanbreaks_'+num+''+i+'" style="color:red;"></span><br/>'
		}
		trhtml+="</th>";
	}else{
		$("#showZjProcess").attr('src','ProcardTemplateAction!findwgProcard0.action?id='+id)
		$("#zjProcessDiv").show();
	}
		if(idname!=null && idname!='' ){
			$("#"+idname+num).html(trhtml);
			$("#"+idname+num).show();
		}else{
			$("#wgtr_"+num).html(trhtml);
			$("#wgtr_"+num).show();
			$("#ljbreak_tr_"+num).hide();
		}
		
		}
	})
}
function hidProcessAndWg(num){
		$("#wgtr_"+num).html('');
		$("#wgtr_"+num).hide();
		$("#ljbreak_tr_"+num).show();
}
function jisuan(num){
var value = $("#breaks" + num).val();
var markId =  $("#markIds"+num).val();
if(value !=null && value != ""){
	  	$.ajax( {
		type : "POST",
		url : "yuanclAndWaigjAction!getYWbytrademark.action",
		data : {
			'yuanclAndWaigj.markId':markId,
		},
		dataType : "json",
		success : function(data) {
			if(data!=null && data>0){
					$("#goodsStoreCount").val((value*data).toFixed(3))
			}else{
			alert("该外购件没有相关的单张重量")
		}
		}
	})

  }else{
	  alert("请先输入数量")
  }
}
function hidwgbreakType(num){
	hidProcessAndWg(num);
	$("#zzbreakType_"+num).attr("checked","checked")
	$("#wgbreakType_"+num).hide();
	$("#wgbreakType_span_"+num).hide();
}
function showwgbreakType(num){
	$("#wgbreakType_"+num).show();
	$("#wgbreakType_span_"+num).show();
}
function bhgForm(){
	var str =	$("#wgbreakType_2").attr("checked")
	var bhgNumber = $("#bhgNumber").val();
	if(bhgNumber!="" ||str =="checked" ){ 
		$("#break_button").attr("disabled","disabled")
		$.ajax( {
		type : "POST",
		url : "ProcardAction!submintBreak.action",
		data : $("#bhgForm").serialize(),
		dataType : "json",
		success : function(data) {
			if("true" == data){
				alert('提交不合格成功!~')
				$("#bhgDiv").hide();
				chageDiv("none");
			}
		}
	})
	}else{
		alert("请先填写不合格数量")
	}
}

function numpanduan(markId,obj,id,num0,num1){
	var num =$("#bhgNumber").val();
	num = parseInt(num);
	var processId = id;
	if(markId == "idfuzhi"){
		processId =$("#bhgProcessId").val();
		markId ="";
	}
	if(markId!=null && markId!=""){
		num = $(obj).val();
	}
	if(num>=0){
	var groups = document.getElementsByName("breaksubmit.breakgroup");
	var group = "";
	for(var i=0;i<groups.length;i++){
		if(groups[i].checked == true){
			group = groups[i].value;
		}
	}
	$.ajax( {
		type : "POST",
		url : "ProcardAction!bhgNumPd.action",
		data : {
			'breaksubmit.breakgroup' :group,
			'breaksubmit.processId' :processId,
			'breaksubmit.tjbreakcount':num,
			'breaksubmit.wgmarkId':markId
		},
		dataType : "json",
		success : function(data) {
			if(data.message!=null && data.message!=""){
				if(obj!=null){
					$(obj).val("");
				}else{
					$("#bhgNumber").val("");	
				}
				alert(data.message);
			}else if(data.data!=null && data.data>0){
				$("#hidebreaks_"+num1+num0).val(data.data);	
				$("#spanbreaks_"+num1+num0).html(data.data+"(公斤/个数)");
			}
			
			
		}
	})
	}
}


function changNumber(){
	sumNumber = parseInt(sumNumber);
	var breakcount = parseInt($("#breakCount_1").val())	;
	var subNumeber = parseInt($("#subNumber").val());
	 if(breakcount>sumNumber){
		alert("不合格数量不能大于领取未提交数量,请重新输入.")
		$("#breakCount_1").val(0);
		$("#subNumber").val(sumNumber)
	} else if(breakcount>0){
		$("#subNumber").val(sumNumber-breakcount);
	}else if((breakcount+subNumeber)>sumNumber){
		alert("提交数量和不合格数量之和不能大于领取未提交数量,请重新输入.")
		$("#breakCount_1").val(0);
		$("#subNumber").val(sumNumber)
	}
	
}

function addtjbhg(){
	var pageProcessIds = document.getElementsByName("pageProcessId");
	var pageProcessSubmmitCounts = document.getElementsByName("pageProcessSubmmitCount");
	var totalCount = ${procard.filnalCount}
	if(pageProcessIds!=null && pageProcessIds.length>0 && '${pageStatus}' == 'noCardLingGx'){
		for(var i=0;i<pageProcessIds.length;i++){
			var id = pageProcessIds[i].value;
			var submmitCount = pageProcessSubmmitCounts[i].value;
			if(submmitCount>0 && submmitCount<totalCount){
				$("#th_"+id).append('<br/>/<a href="javascript:;"onclick="showbhg('+id+')">提交不合格</a>');
			}else if(submmitCount == 0){
				$("#th_"+id).append('<br/>/<a href="javascript:;"onclick="showbhg('+id+')">提交不合格</a>');
				break;
			}
		}
	}
}
function upCaigou1(){
	//if (!validateText("lingliaostatus1", "是否领料")) {
	//	return false;
	//}
	//if (!validateText("cgStatus1", "是否采购")) {
	//	return false;
	//}
	var ll = $("#lingliaostatus1").val();
	if(ll=='是'){
		var filnalCount = $("#filnalCount1").val();
		var cgnum = $("#klNumber1").val();
		var kl = $("#klNumber2").val();
		var cgnum2 = $("#hascount2").val();
		if(cgnum==null||cgnum==''){
			alert("需领料数量不能为空。");
			$("#klNumber1").val(kl);
			return false;
		}else if(Number(cgnum)<=0){
			alert("需领料数量不能少于0。")
			$("#klNumber1").val(kl);
			return false;
		}else if(Number(cgnum)>Number(filnalCount)){
			alert("需领料数量不能大于总数："+filnalCount);
			$("#klNumber1").val(kl);
			return false;
		}else if(Number(cgnum)<Number(cgnum2)){
			alert("需领料数量不能小于已领数："+cgnum2);
			$("#klNumber1").val(kl);
			return false;
		}
	}else{
		
	}
}
function isfou(){
	var cg = $("#lingliaostatus1").val();
	if(cg=='是'){
		$("#hascount1").show();
		$("#hascount1").removeAttr("disabled");
	}else{
		$("#hascount1").hide();
		$("#hascount1").attr("disabled", "disabled");
	}
	
}

function validateText(id, textname) {
	var textValue = $.trim($("#" + id).val());
	if (textValue == null || textValue == "") {
		$("#" + id).focus();
		alert(textname + "不能为空");
		return false;
	}
	return true;
}

function zhuye(){
	window.location.href = "ProdEquipmentAction!checkMachiner.action";
}
$(function(){
	if('${procard.sbNumber}'!=''){
		$("#div_shebian").show();
	}
	if('设变锁定' == '${procard.status}'){
			$.ajax( {
		type : "POST",
		url : "ProcardAction!getSbNumByRootId.action",
		data : {
				id:'${procard.id}'
			},
		dataType : "json",
		success : function(data) {
			if(data!=null){
				$("#div_shebian").html('[设变单号:<a href="procardTemplateGyAction_showSbApplyJd2.action?bbAply.id='+data.sbId+'">'+data.sbNumber+'</a>]' +
				'[设变状态:'+data.sbStatus+']');
				$("#div_shebian").show();
			}
		},
		error:function(data){
			console.log(data);
		}
	})
	}
})

changeValue =(obj,processId,num)=>{
	var td =$(obj).closest("td");
	console.log('td:',td);
	var inputs =	$(td).find("input[type='text']");
	var cards = "";
	for(var i=0;i<inputs.length;i++){
		var card =inputs[i].value;
		if(card!=''){
			if(i==0){
				cards+=card;
			}else{
				cards+=","+card;
			}
		}
	}
	$.ajax({
		url:"ProcardAction!findProcessLogByProcessId.action",
		type:"POST",
		data:{id:processId},
		dataType:"json",
		success:function(data){
			if(data!=null){
				if(cards!=data.userCardId){
					$("#totalCount"+num).val(1);
					$("#processNumbers"+num).val(1);
					$("#totalCount"+num).parent().text(1);
				}
			}
		},
		error:function(){
			alert('数据异常!~');
		}
	})
}

<%--	<s:if test="#pageProcess.submmitCount<#pageProcess.totalCount">--%>
<%--											/<a href="javascript:;"--%>
<%--												onclick="showbhg('${pageProcess.id}')">提交不合格</a>--%>
<%--										</s:if>--%>
</script>
	</body>
</html>