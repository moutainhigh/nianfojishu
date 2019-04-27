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
		<style type="text/css">
ul li {
	width: 33.3%;
	float: left;
}

#fullbg1 {
	background-color: gray;
	left: 0;
	opacity: 0.5;
	position: absolute;
	top: 0;
	z-index: 3;
	filter: alpha(opacity =                         50);
	-moz-opacity: 0.5;
	-khtml-opacity: 0.5;
}

#dialog1 {
	background-color: #fff;
	border: 5px solid rgba(0, 0, 0, 0.4);
	left: 50%;
	margin: -200px 0 0 -200px;
	padding: 1px;
	position: fixed !important; /* 浮动对话框 */
	position: absolute;
	top: 45%;
	width: 400px;
	height: 200px;
	z-index: 5;
	border-radius: 5px;
	display: none;
}

#xiugaiIframe1 {
	background-color: #fff;
	height: 165px;
	line-height: 24px;
	width: 400px;
}
</style>
	</head>
	<body onload="loadValue();f1();">
		<%@include file="/util/sonTop.jsp"%>
		<div id="gongneng">
			<div align="center">
				<s:if test="osTemplate.xjbzId!=null && osTemplate.xjbzId>0">
					<form action="WaigouwaiweiPlanAction!OstbdXjbz.action"
						method="post">
						<div align="center">
							<table>
								<tr>
									<td>
										<SELECT name="osTemplate.xjbzId">
											<option value="${osTemplate.xjbzId}">
												${osTemplate.xjbz}
											</option>
											<s:iterator value="xjbzlist" id="xjbz">
												<option value="${xjbz.id}">
													${xjbz.leixing}
												</option>
											</s:iterator>
										</SELECT>
									</td>
									<td>
										<input type="hidden" value="${osTemplate.id}"
											name="osTemplate.id" />
										<input type="hidden" value="${pageStatus}" name="pageStatus" />
										<input type="hidden" value="${cpage}" name="cpage" />
										<input type="hidden" value="${waigoudd.id}" name="id" />
										<input type="submit" value="更换抽检标准" />
									</td>
								</tr>
							</table>
						</div>
					</form>
					<br />
					<div id="kuwei_div">
						<ul id="kuwei_ul">
						</ul>
					</div>
					<form action="WaigouwaiweiPlanAction!checkwgww.action"
						method="post" onsubmit="return check()">
						<s:if test="waigoudd.jytixing == '是'">
							<h2 style="color:red">
								<b>该供应商该件号已出现两次同种缺陷退货。</b>
							</h2>
						</s:if>
						
						<h2>
							<s:if test='osTemplate.zhonglei == "外购件检验"'>
								外购件检验
							</s:if>
							<s:if test='osTemplate.zhonglei == "外委"'>
								外委检验
							</s:if>
						</h2>
						<s:if test="waigoudd.sbNumber!=null">
						<div
							style="background-color: #00FF00; color: #FF4500; font-weight: bolder; padding-left: 100px; font-size: 15px; font-family: 黑体;">
							批次设变提醒:[设变单号:
							<a
								href="procardTemplateGyAction_showSbApplyJd2.action?bbAply.id=${waigoudd.sbId}">${waigoudd.sbNumber}</a>]
						</div>
						<div align="left">
							<h3>
								设变提示:
								<font color="red">${waigoudd.sbRemark}</font>
							</h3>
						</div>
					</s:if>
					<s:if test="pamList!=null && pamList.size()>0">
						<div id="mea" style="display: none;">
							<div id="mea_pc" style="display: none;">
								<h2>
									选择量、检具
								</h2>
								<form method="POst" id="pamForm">
									<table class="table">
										<s:iterator value="pamList" id="pagepam" status="pageIndex">
											<s:if test="#pageIndex.index%3==0">
												<tr>
											</s:if>
											<td>
												<input type="checkbox" name="ids" onclick="xuanzhe(this)"
													value="${pagepam.measuring_no}">
												${pagepam.measuring_no}、(${pagepam.matetag})
											</td>
											<s:if test="(#pageIndex.index+1)%3==0">
												</tr>
											</s:if>
										</s:iterator>
									</table>
									<input type="button" value="选择" onclick="xuanzeMea(this);" />
								</form>
							</div>
							<div id="mea_phone" style="display: none;">
								<input type="button" value="选择量、检具" class="button0 black"
									onclick="autocheckList()" />
							</div>
						</div>
					</s:if>
					<s:else>
						<font color="red"> 未绑定相关的量、检具信息。请前往工艺工序管理绑定。 </font>
					</s:else>
					<font color="red" size="5" id="msg"></font>
					<form action="WaigouwaiweiPlanAction!checkwgww.action"
						method="post" onsubmit="return check()">
						<h2>
							<s:if test='osTemplate.zhonglei == "外购件检验"'>
								外购件检验
							</s:if>
							<s:if test='osTemplate.zhonglei == "外委"'>
								外委检验
							</s:if>
						</h2>

						<input type="hidden" name="osRecord.measuring_no"
							id="measuring_no">
						<input type="hidden" name="osRecord.wwddId" value="${waigoudd.id}">
						<input type="hidden" name="osRecord.kgliao"
							value="${waigoudd.kgliao}">
						<input type="hidden" readonly="readonly" name="osRecord.unit"
							value="${waigoudd.unit}" />
						<input type="hidden" readonly="readonly"
							name="osRecord.templateId" value="${osTemplate.id}" />
						<table class="table">
							<tr align="center">
								<td colspan="4" style="font-size: 20px;">
									标识类别：
									<s:if test='osTemplate.zhonglei == "外购件检验"'>
								外购件检验
							</s:if>
									<s:if test='osTemplate.zhonglei == "外委"'>
								外委检验
							</s:if>
								</td>
							</tr>
							<tr>
								<td align="right">
									零件号
								</td>
								<td>
									${waigoudd.markId}
									<input id="markId" type="hidden" readonly="readonly"
										name="osRecord.markId" value="${waigoudd.markId}" />
								</td>
								<td align="right">
									零件名
								</td>
								<td>
									${waigoudd.proName}
									<input id="partsName" type="hidden" readonly="readonly"
										name="osRecord.proName" value="${waigoudd.proName}" />
								</td>
							</tr>
							<s:if test='osTemplate.zhonglei == "外委"'>
								<td align="right">
									外委工序
								</td>
								<td>
									${waigoudd.processName}
								</td>
								<td align="right">
									下工序
								</td>
								<td>
									${waigoudd.nextProcessName}
								</td>
							</s:if>
							<tr>
								<td align="right">
									规格
								</td>
								<td>
									${waigoudd.specification}
									<input id="specification" type="hidden" readonly="readonly"
										name="osRecord.specification"
										value="${waigoudd.specification}" />
								</td>
								<td align="right">
									图号
								</td>
								<td>
									${waigoudd.tuhao}
									<input id="tuhao" type="hidden" readonly="readonly"
										name="osRecord.tuhao" value="${waigoudd.tuhao}" />

								</td>
							</tr>
							<tr>
								<td align="right">
									版本号
								</td>
								<td>
									${waigoudd.banben}
									<input id="lotId" type="hidden" value="${waigoudd.banben}"
										name="osRecord.banbenNumber" readonly="readonly" />
									<input id="lotId" type="hidden" value="${waigoudd.banci}"
										name="osRecord.banci" readonly="readonly" />
								</td>
								<td align="right">
									供料属性
								</td>
								<td>
									${waigoudd.kgliao}
								</td>
							</tr>
							<tr>
								<td align="right">
									供应商
								</td>
								<td>
									${waigoudd.gysName}
									<input id="lotId" type="hidden" value="${waigoudd.gysId}"
										name="osRecord.gysId" readonly="readonly" />
									<input id="lotId" type="hidden" value="${waigoudd.gysName}"
										name="osRecord.gysName" readonly="readonly" />
								</td>
								<td align="right">
									到货时间
								</td>
								<td>
									${waigoudd.querenTime}
									<input id="lotId" type="hidden" value="${waigoudd.querenTime}"
										name="osRecord.jinhuoDate" readonly="readonly" />
								</td>
							</tr>
							<tr>
								<td align="right">
									检验员
								</td>
								<td>
									${Users.name}
									<input type="hidden" name="osRecord.userId" value="${Users.id}"
										readonly="readonly" />
									<input type="hidden" name="osRecord.userCode"
										value="${Users.code}" readonly="readonly" />
									<input type="hidden" name="osRecord.username"
										value="${Users.name}" readonly="readonly" />
								</td>
								<td align="right">
									检验批次
								</td>
								<td>
									${waigoudd.examineLot}
									<input id="lotId" type="hidden" value="${waigoudd.examineLot}"
										name="osRecord.jcpc" readonly="readonly" />
								</td>
							</tr>
							<tr>

								<td align="right">
									查看图纸
								</td>
								<td>
									<s:if test="osTemplate.filename!=''&&osTemplate.filename!=null">
										<a target="_showTz"
											<%--											href="<%=basePath%>/upload/file/OsTemplate/${osTemplate.filename}">--%>
											href="FileViewAction.action?FilePath=/upload/file/OsTemplate/${osTemplate.filename}">
											<img alt=""
												src="<%=basePath%>/upload/file/OsTemplate/${osTemplate.filename}"
												width="80px" height="60px"
												onerror="this.src='images/nopic.jpg'"> </a>
									</s:if>
									<s:else>
										<a
											href="WaigouwaiweiPlanAction!gysTzview2.action?id=${waigoudd.waigouPlanId}&pageStatus=noshow">图纸</a>
									</s:else>
								</td>
								<td align="right">
									物料类别
								</td>
								<td>
									${waigoudd.wgType}
								</td>
							</tr>
							<tr>
								<td align="right">
									来料数量
								</td>
								<td>
									${waigoudd.qrNumber}
								</td>
								<td align="right">
									需抽检数量
								</td>
								<td>
									${sumNum}
									<input type="hidden" value="${sumNum}" id="sumNum"
										name="sumNum" />
								</td>
							</tr>
							<tr>
								<td align="right">
									检验不良描述
								</td>
								<td colspan="3">
									<textarea rows="2" cols="80" name="osRecord.type"></textarea>
								</td>
							</tr>
							<table id="quexiantable">
								<tr>
									<td colspan="5">
										<div id="queXianType_class"  class="queXianType_class" style="display: none;">
											<span><b>缺陷类型:</b> </span>
											<select id="buhegeType_cp_0" name=""
												onchange="findBhgByDefId(this,'0')">
											</select>
											<select id="buhegeType0" name="">
												<option value="0">
													--请选择--
												</option>
											</select>
											<select id="buhegeType_class_0"
												name="">
												<option value="CR">
													致命缺陷(CR)
												</option>
												<option value="MA">
													主要缺陷(MA)
												</option>
												<option value="MI">
													次要缺陷(MI)
												</option>
											</select>
											<b>数量:</b>
											<input type="text" value=""
												name="" id="bhg_bhgNum_0"
												style="width: 75px;"
												onchange="numyanzheng(this,'zhengshu');bhg_bhgNumyanzheng(this)"
												class="bhg_bhgNum" />
											<input type="button" onclick="addtr()" value="增加" />
											<input type="button" onclick="deltr()" value="删除" />
										</div>
									</td>
								</tr>
								<tr id="lastTr">
									<th align="right">
										请输入不合格数量
									</th>
									<td align="center" colspan="4">
										<input type="text" name="buhegeNumber" id="bhgNumber"
											onblur="buhegeNum(this);numyanzheng(this,'zhengshu')"
											onkeyup="buhegeNum(this);numyanzheng(this,'zhengshu')" />
										<s:if test="list.size>0">
											<input type="hidden" value="${pageStatus}" name="pageStatus" />
											<input type="hidden" value="${cpage}" name="cpage" />
											<input class="input" id="sub_1" type="submit"
												disabled="disabled" value="加载检验数据中..." style="width: 150px;" />
										</s:if>
									</td>
								</tr>
							</table>
							<tr>
								<td colspan="4" id="table_td">
									<s:iterator value="list" id="pageOsScopes" status="pageIndexs">
										<div id="fp" style="text-align: center;">
											<table class="table" id="mytable_${pageIndexs.index+1}"
												bgcolor="">
												<tr>
													<th colspan="6">
														<input type="hidden"
															value="<s:property value="osTemplate.scopes.size() " />"
															id="ossindex" />
														<input type="hidden"
															value="<s:property value='list.size()' /> " id="ossSize" />
														第${pageIndexs.index+1}${waigoudd.unit} 共需检验
														<input type="hidden" value="${pageIndexs.index+1}"
															name="count_input" />
														<s:property value='list.size()' />
														<input type="hidden" value="count" name="hid_conut"
															id="count${pageIndexs.index+1}" />
														${waigoudd.unit}
													</th>
												</tr>
												<tr>
													<td align="center">
														序号
													</td>
													<td align="center">
														检查条目
													</td>
													<td align="center">
														质量特征
													</td>
													<td align="center">
														检查方法/工具
													</td>
													<td align="center">
														检查结果
													</td>
													<s:if test="tag == 'ty'">
														<td>
														</td>
													</s:if>
												</tr>
												<s:if test="list.size<=0">
													<tr>
														<th colspan="6">
															<font style="color: red; font-weight: bolder;">尚未录入外检模版,请联系质检部门,谢谢!</font>
														</th>
													</tr>
												</s:if>
												<s:iterator value="pageOsScopes" id="pageOsScope"
													status="pageIndex">
													<tr align="center"
														id="tr_${pageIndexs.index+1}_${pageIndex.index}">
														<td id="td_${pageIndexs.index+1}_${pageIndex.index}">
															${pageIndex.index+1}
														</td>
														<td>
															${pageOsScope.content}
														</td>
														<td>
															${pageOsScope.zltz}
														</td>
														<td>
															${pageOsScope.jcff}
														</td>
														<td align="center">
															<input type="hidden" value="${pageOsScope.id}"
																id="scopeId_${pageIndexs.index+1}_${pageIndex.index}"
																name="osRecordList[${pageIndexs.index}].scopes[${pageIndex.index}].scopeId">
															<s:if test="#pageOsScope.type=='OKorNo'">
																<input
																	id="content2_${pageIndexs.index+1}_${pageIndex.index}"
																	name="osRecordList[${pageIndexs.index}].scopes[${pageIndex.index}].content"
																	type="radio" value="ok" checked="checked" onchange="">OK
															<input
																	id="content2_${pageIndexs.index+1}_${pageIndex.index}"
																	name="osRecordList[${pageIndexs.index}].scopes[${pageIndex.index}].content"
																	type="radio" value="no" onchange="">NO
															</s:if>
															<s:else>
																<input
																	id="content2_${pageIndexs.index+1}_${pageIndex.index}"
																	name="osRecordList[${pageIndexs.index}].scopes[${pageIndex.index}].content"
																	value="" onchange="" class="nonull">
															</s:else>
														</td>
														<s:if test="tag == 'ty'">
															<td>
																<input type="button" value="添加" onclick="addLine()"
																	class="adddebutton" />
																<input type="button" value="删除"
																	onclick="delLine(${pageIndex.index})" class="delbutton" />
															</td>
														</s:if>
													</tr>
												</s:iterator>
												<tr id="lastTr_${pageIndexs.index+1}">
													<th colspan="6">
														结果：
														<input type="radio" id="hege_${pageIndexs.index}"
															name="osRecordList[${pageIndexs.index}].verification"
															class="hgradio" value="合格"
															onclick="hege('${pageIndexs.index}')" checked="checked" />
														合格&nbsp;&nbsp;
														<input type="radio" id="buhege_${pageIndexs.index}"
															name="osRecordList[${pageIndexs.index}].verification"
															value="不合格" onclick="buhege('${pageIndexs.index}')"
															class="bhgradio" />
														不合格

														<input type="hidden" value="0"
															name="osRecordList[${pageIndexs.index}].kuweiId"
															id="kuweiId_${pageIndexs.index}" />
													</th>
												</tr>
											</table>
										</div>
									</s:iterator>
									<s:if test="errorMessage!=null">
										<div align="center">
											<font color="red">${errorMessage}</font>
										</div>
									</s:if>
								</td>
							</tr>
							<tr>
								<td colspan="4" style="text-align: center">
									<s:if test="list.size>0">
										<input class="input" id="sub" type="submit" value="确认" />
									</s:if>
								</td>
							</tr>
							<%--						<tr>--%>
							<%--							<td colspan="5" align="center">--%>
							<%--								本批数量：--%>
							<%--								<input type="text" name="osRecord.quantity"--%>
							<%--									value="${waigoudd.qrNumber}" /><br/>--%>
							<%--								检验数量：--%>
							<%--								<input type="text" name="osRecord.jyNumber" value="5" /><br/>--%>
							<%--								合格数量：--%>
							<%--								<input type="text" name="osRecord.hgNumber"--%>
							<%--									value="${waigoudd.qrNumber}" />--%>
							<%--							</td>--%>
							<%--						</tr>--%>
							<%--						<tr>--%>
							<%--							<td colspan="5" align="center">--%>
							<%--								结果：--%>
							<%--								<input type="radio" name="osRecord.verification" value="合格"--%>
							<%--									checked="checked" />--%>
							<%--								合格&nbsp;&nbsp;--%>
							<%--								<input type="radio" name="osRecord.verification" value="不合格" />--%>
							<%--								不合格--%>
							<%--							</td>--%>
							<%--						</tr>--%>
						</table>
					</form>
				</s:if>
				<s:elseif test="ostList.size()>1">
					<div id="bdjybz" style="display: none;">
						<h2>
							未绑定检验标准，请先选择检验标准!
						</h2>
						<form action="WaigouwaiweiPlanAction!OstbdXjbz.action"
							method="post">
							<SELECT name="osTemplate.xjbzId" class="cxselect"
								onchange="showxjbz(this)">
								<s:iterator value="xjbzlist" id="xjbz">
									<option value="${xjbz.id}">
										${xjbz.leixing}
									</option>
								</s:iterator>
							</SELECT>
							<input type="hidden" value="${osTemplate.id}"
								name="osTemplate.id" id="chonseostId" />
							<input type="hidden" value="${waigoudd.id}" name="id" />
							<input type="hidden" value="ty" name="tag" />
							<input type="hidden" value="${pageStatus}" name="pageStatus" />
							<input type="hidden" value="${cpage}" name="cpage" />
							<input type="submit" value="绑定" class="input" />
						</form>
						<div style="display: none;" id="xjbzdiv">
							<iframe id="xiugaiIframe" src="" marginwidth="0" marginheight="0"
								hspace="0" vspace="0" frameborder="0" scrolling="yes"
								style="width: 98%; height: 500px; margin: 0px; padding: 0px;"></iframe>
						</div>
					</div>
					<div id="bdost">
						<h2>
							请先选择一个通用模板，再进行检验!
						</h2>
						<table class="table">
							<tr align="center" bgcolor="#c0dcf2" height="50px">
								<td>
									序号
								</td>
								<td>
									种类
								</td>
								<td>
									车型
								</td>
								<td>
									类型
								</td>
								<td>
									名称
								</td>
								<td>
									图纸
								</td>
								<td>
									检查项
								</td>
								<td>
									操作
								</td>
							</tr>
							<s:iterator value="ostList" id="ost" status="ostStatus">
								<s:if test="#ostStatus.index%2==1">
									<tr align="center" bgcolor="#e6f3fb"
										onmouseover="chageBgcolor(this)"
										onmouseout="outBgcolor(this,'#e6f3fb')">
								</s:if>
								<s:else>
									<tr align="center" onmouseover="chageBgcolor(this)"
										onmouseout="outBgcolor(this,'')">
								</s:else>
								<td>
									<s:property value="#ostStatus.index+1" />
								</td>
								<td>
									${ost.zhonglei}
								</td>
								<td>
									${ost.cmodel}
								</td>
								<td>
									${ost.ctype}
								</td>
								<td>
									${ost.name}
								</td>
								<td>
									<a
										href="DownAction.action?fileName=${ost.filename}&directory=upload/file/OsTemplate/">下载</a>
								</td>
								<td>
									<a
										href="OsTemplate_showScope.action?t.id=${ost.id}&status=show">查看检查项</a>
								</td>
								<td>
									<input type="button" value="选择"
										onclick="chonseost('${ost.id}')" />
								</td>
							</s:iterator>
						</table>
					</div>
				</s:elseif>
				<s:else>
					<h2>
						未绑定检验标准，请先选择检验标准!
					</h2>
					<form action="WaigouwaiweiPlanAction!OstbdXjbz.action"
						method="post">
						<SELECT name="osTemplate.xjbzId" class="cxselect" id="xjbzosT"
							onchange="showxjbz(this)">
							<s:iterator value="xjbzlist" id="xjbz">
								<option value="${xjbz.id}">
									${xjbz.leixing}
								</option>
							</s:iterator>
						</SELECT>
						<input type="hidden" value="${osTemplate.id}" name="osTemplate.id" />
						<input type="hidden" value="${waigoudd.id}" name="id" />
						<input type="hidden" value="${pageStatus}" name="pageStatus" />
						<input type="hidden" value="${cpage}" name="cpage" />
						<input type="submit" value="绑定" class="input" onclick="todisabled(this)"/>
					</form>
					<div style="display: none;" id="xjbzdiv">
						<iframe id="xiugaiIframe" src="" marginwidth="0" marginheight="0"
							hspace="0" vspace="0" frameborder="0" scrolling="yes"
							style="width: 98%; height: 500px; margin: 0px; padding: 0px;"></iframe>
					</div>
				</s:else>
			</div>
			<br>
		</div>

		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
		<script type="text/javascript">
var isjy = false;
$(function() {
	if (!isjy) {
		$("#mea").show();
		if (/Android|webOS|iPhone|iPod|BlackBerry/i.test(navigator.userAgent)) {
			$("#mea_phone").show();
		} else {
			$("#mea_pc").show();
		}
	}
})
function showzhemu(src) {
	$("body").append("<div id='fullbg1'></div>");
	$("body").append(
			"<div id='dialog1' class='loginbox'>"
					+ "<iframe id='xiugaiIframe1' src='" + src + "' "
					+ "marginwidth='0' marginheight='0' hspace='0' vspace='0' "
					+ "frameborder='0' scrolling='yes'"
					+ " style='width: 100%;margin: 0px; padding: 0px;'>"
					+ "</iframe></div>")

	var sWidth, sHeight;
	//sWidth=document.body.offsetWidth;//得出当前屏幕的宽
	sWidth = document.body.clientWidth;//BODY对象宽度

	//sHeight=screen.height; //得到当前屏幕的高
	//sHeight=document.body.clientHeight;//BODY对象高度
	if (window.innerHeight && window.scrollMaxY) {
		sHeight = window.innerHeight + window.scrollMaxY;
	} else if (document.body.scrollHeight > document.body.offsetHeight) {
		sHeight = document.body.scrollHeight;
	} else {
		sHeight = document.body.offsetHeight;
	} //以上得到整个屏幕的高
	//		var bw = $("body").width();
	//		var bh = window.screen.availHeight;
	$("#fullbg1").css( {
		height : sHeight,
		width : sWidth,
		display : "block"
	});
	$("#dialog1").show();
	//弹出遮罩层结束
}
$(function() {
	//弹出遮罩层开始
<%--	if ('${waigoudd.againcheck}' == '待复检' || '${waigoudd.againcheck}' == '待分检') {--%>
<%--		showzhemu('WaigouwaiweiPlanAction!toisAgainCheck.action?id=${waigoudd.id}&pageStatus=${pageStatus}&cpage=${cpage}');--%>
<%--	}--%>
})

var kuweiId = 0
//表示开门中的库位的Id
var bool;
var index = 0;
var count = 0;
var isopen = false;
var num1 = 0;
$(function() {
	var xjbz = document.getElementById("xjbzosT");
	showxjbz(xjbz);
	var aa = $("input");
	count = document.getElementsByName("hid_conut").length;
	$
			.ajax( {
				type : "POST",
				url : "WaigouwaiweiPlanAction!findWNBywaigouddId.action",
				data : {
					id : '${id}'
				},
				dataType : "json",
				success : function(data) {
					if (data != null) {
						$(data)
								.each(
										function() {
											$("#kuwei_ul")
													.append(
															'<li><img	src=${pageContext.request.contextPath}/images/error.jpg id="img_'
																	+ this.kuweiId
																	+ '" >'
																	+ this.kuweiNo
																	+ '&nbsp;&nbsp;<a href="javascript:;" onclick="opendoor('
																	+ this.kuweiId
																	+ ','
																	+ this.number
																	+ ')" >开门</a> <a href="javascript:;" onclick="closedoor('
																	+ this.kuweiId
																	+ ')" >关门<a/><li>');
										});
<%--						if (kuweiId == 0) {--%>
<%--							for ( var i = 1; i <= aa.length; i++) {--%>
<%--								$("#mytable_" + i + " input").attr("disabled",--%>
<%--										"disabled");--%>
<%--							}--%>
<%--						}--%>
						$(".adddebutton").removeAttr("disabled");
					}
				}
			});

})
function subchangdisabled() {
	var bool = true;
	var aa = $("#table_td  input");
	for ( var i = 0; i < aa.length; i++) {
		if (aa[i].value == "") {
			$("#sub").attr("disabled", "disabled");
			bool = false;
			break;
		}
	}
	if (bool) {
		$("#sub").removeAttr("disabled");
	}
}
//开门操作	
function opendoor(id_wn, num) {
	kuweistatus(id_wn);
	if (kuweiId > 0) {//如果有正在开门中的门先把门给关了;
		closedoor(kuweiId);
		if (bool) {//关门成功才能继续开门;
			$.ajax( {
				type : "POST",
				url : "WaigouwaiweiPlanAction!sendTow.action",
				data : {
					id_wn : id_wn,
					id_wdd : '${id}'
				},
				dataType : "json",
				async : false,
				success : function(data) {
					if (data != null) {
						if (data.success) {
							getcheckList2();
							kuweiId = id_wn;
							num1 = num;
						} else {
							alert(data.message)
						}
					}
				}
			});
		}
	} else {
		$.ajax( {
			type : "POST",
			url : "WaigouwaiweiPlanAction!sendTow.action",
			data : {
				id_wn : id_wn,
				id_wdd : '${id}'
			},
			dataType : "json",
			async : false,
			success : function(data) {
				if (data != null) {
					if (data.success) {
						getcheckList2();
						kuweiId = id_wn;
						num1 = num;
					} else {
						alert(data.message)
					}
				}
			}
		});
	}

}

function kuweistatus(id) {
$.ajax( {
		type : "POST",
		url : "WaigouwaiweiPlanAction!oactoWeb.action",
		data : {
			id:id,
		},
		dataType : "json",
		async : false,
		success : function(data) {
			if(data!=null){
					if(data.kwStatus !='开'){
						kuweiId = 0;
					}
				}
			}
	});
	
}
//关门操作
function closedoor(id_wn){
	$.ajax({
		type : "POST",
		url : "WaigouwaiweiPlanAction!oactoWeb.action",
		data : {
			id_wn: id_wn,
		},
		dataType : "json",
		async : false,
		success : function(data) {
			if(data!=null){
					if(!data.success){
						bool = false;
							alert(data.message)
					}else{
						bool = true;
						$("#img_"+id_wn).attr("src","${pageContext.request.contextPath}/images/error.jpg")
						
					}
				
				}
			}
	});
}
function getcheckList2() {
	if (typeof (myObj) != "undefined") {
		//打开扫描服务
		myObj.scanGongWei(1);
	} else {
		alert("无法打开扫描服务,请检查后重试!");
	}
}
function funFromjs(tm,id) {
	$.ajax( {
		type : "POST",
		url : "WaigouwaiweiPlanAction!redTowWeb.action",
		data : {
			bacode:tm
		},
		dataType : "json",
		async : false,
		success : function(data) {
			if(data!=null){
					if(!data.success){
						kuweiId = 0;
						alert(data.message);
					}else{
						$("#img_"+kuweiId).attr("src","${pageContext.request.contextPath}/images/success.jpg");
						if (num1 < ((count - 1) - index)) {
										for ( var i = num1 + 1; i <= (count - 1)
												- index; i++) {
											$("#mytable_" + i).attr("bgcolor",
													"#D5D5D5");
											$("#mytable_" + i + " input").attr(
													"disabled", "disabled");
										}
									} else {
										for ( var i = 1; i <= count; i++) {
											$("#mytable_" + i).removeAttr(
													"bgcolor");
											$("#mytable_" + i + " input")
													.removeAttr("disabled");
										}

									}
					}
				}
			}
		})
}
		
function f1() {
	var leixing = document.getElementById("leixing").value;
	if ("人员" == leixing) {
		document.getElementById("t1").style.display = "block";
	} else {
		document.getElementById("t1").style.display = "none";
	}

}
function getyuanyin(deptValue) {
	if (deptValue == "人员") {
		document.getElementById("t1").style.display = "block";
	} else {
		document.getElementById("t1").style.display = "none";

	}
	$.ajax( {
		type : "POST",
		url : "caoZuoAction!listBaofeiname.action",
		data : {
			pageStatus : deptValue
		},
		dataType : "json",
		success : function(object) {
			$("#yuefen").empty();
			$("<option></option>").appendTo("#yuefen");
			$(object).each(
					function() {
						$("<option value='" + this + "'>" + this + "</option>")
								.appendTo("#yuefen");
					});
		}
	});
}
//--------------------------------------------------
//提交验证
<%--function checkForm() {--%>
<%--	var listSize = "<s:property value='list.size()'/>";--%>
<%--	for ( var i = 1; i <= listSize.length; i++) {--%>
<%--		for(var j=0;j<ossindex;j++ ){--%>
<%--			var context = $("#content2_" + i+"_"+j).va();--%>
<%--				if (context == null || context == "") {--%>
<%--				alert("首件记录尚未填写完毕!");--%>
<%--				return false;--%>
<%--			}--%>
<%--		}--%>
<%--		--%>
<%--	}--%>
<%--}--%>
function getUserBy(obj, o) {
	if (obj == "co") {//根据工号查询
		var code = o.value;
		$.ajax( {
			type : "POST",
			url : "WaigouwaiweiPlanAction!findUserInfor.action",
			data : {
				tag : obj,
				content : code
			},
			dataType : "json",
			success : function(msg) {
				if (msg.content == "") {
					alert(msg.message);
				} else {
					document.getElementById("operator").value = msg.content;
				}

			}
		});
	} else if (obj == "na") {
		var name = o.value;
		$.ajax( {
			type : "POST",
			url : "WaigouwaiweiPlanAction!findUserInfor.action",
			data : {
				tag : obj,
				content : name
			},
			dataType : "json",
			success : function(msg) {
				if (msg.content == "") {
					alert(msg.message);
				} else {
					document.getElementById("code").value = msg.content;
				}

			}
		});
	}
}
<%--function bxtxhege(num){--%>
<%--	var str= $("#hege_0").attr("disabled");--%>
<%--	if(num>1 && str!="disabled"){--%>
<%--		var obj1 = document.getElementById("hege_"+(num-2));--%>
<%--		var obj2 =	document.getElementById("buhege_"+(num-2));--%>
<%--		if(!(obj1.checked || obj2.checked)){--%>
<%--			for(var i=num;i<=count;i++){--%>
<%--				$("#mytable_"+i+" input").attr("disabled","disabled");--%>
<%--			}--%>
<%--			alert("请选择第"+(num-1)+"件零件合格或者不合格!")--%>
<%--		}else{--%>
<%--			for(var i=num;i<=count;i++){--%>
<%--				$("#mytable_"+i+" input").removeAttr("disabled");--%>
<%--			}--%>
<%--		}--%>
<%--	}--%>
<%--}--%>
function buhege(num){
	var id = $("#kuweiId_"+num).val();
	
	if(num>index){
		index = num;
	}
	if(id == 0){
		$("#kuweiId_"+num).val(kuweiId);
	}
}
function hege(num){
	if(num>index){
		index = num;
	}
	var id= $("#kuweiId_"+num).val();
	if(id == kuweiId){
		$("#kuweiId_"+num).val("0");
	}
}
function chonseost(id){
	$("#chonseostId").val(id);
	$("#bdost").hide();
	$("#bdjybz").show();
}


var ossindex = $("#ossindex").val();

function addLine() {
	var ossSize = $("#ossSize").val();
	ossindex = parseInt(ossindex);
	for(var i=1; i<=ossSize;i++ ){
			var newLine = '<tr align="center" id="tr_'+i+'_'+ossindex+'"><td id="td_'+i+'_'+ossindex+'">'
			+(ossindex+1)+
			'</td><th><input id="content1_'+i+'_'+ossindex+'"  name="osRecordList['+(i-1)+'].scopes['
			+ ossindex
			+ '].scope.content" class = "content1_'+ossindex+'" onchange = "fuzhi(&apos;content1_'+ossindex+'&apos;,this)" /></th>'
			+'<th><input id="zltz_'+i+'_'+ossindex+'" name="osRecordList['+(i-1)+'].scopes['
			+ ossindex
			+ '].scope.zltz" class = "zltz_'+ossindex+'" onchange = "fuzhi(&apos;zltz_'+ossindex+'&apos;,this)"  /></th>'
			+ '<th><input id="jcff_'+i+'_'+ossindex+'" name="osRecordList['+(i-1)+'].scopes['
			+ ossindex
			+ '].scope.jcff"  class = "jcff_'+ossindex+'" onchange = "fuzhi(&apos;jcff_'+ossindex+'&apos;,this)"  />' 
			+'<th><input id="ontent2_'+i+'_'+ossindex+'" name="osRecordList['+(i-1)+'].scope['
			+ ossindex
			+ '].content"   class = "content2_'+ossindex+'" onchange = "fuzhi(&apos;content2_'+ossindex+'&apos;,this)" /></th>'
			+ '</th><td><input type="button" value="添加" onclick="addLine()" class="adddebutton"/>' +
			'<input type="button" value="删除" onclick="delLine('+ossindex+')" class="delbutton"/> </td></tr>';
			$($('#mytable_'+i+' tr')[$('#mytable_'+i+' tr').length - 2]).insertBefore(newLine);
			$('#lastTr_'+i).before(newLine);
	}
	
																
	ossindex++;
}
function delLine(num) {
	if (ossindex == 1) {
		alert("只剩最后一项了,再删真没了");
		return;
	}
	var ossSize = $("#ossSize").val();
	for(var i=1;i<=ossSize;i++){
		$("#tr_"+i+"_"+num).remove();
		var table = document.getElementById("mytable_"+i);
		
		for(var j=1;j<=(ossindex-1);j++){
			table.rows[j+1].cells[0].innerHTML = j;
			var inputArrays4 =	table.rows[j+1].cells[4].getElementsByTagName("input");
			if(inputArrays4!=null && inputArrays4.length >1 ){
				inputArrays4[0].name = "osRecordList["+(i-1)+"].scopes["+(j-1)+"].scopeId";
				inputArrays4[1].name = "osRecordList["+(i-1)+"].scopes["+(j-1)+"].content";
				if(inputArrays4[2]!=null){
					inputArrays4[2].name = "osRecordList["+(i-1)+"].scopes["+(j-1)+"].content";
				}
			}else{
				inputArrays4[0].name = "osRecordList["+(i-1)+"].scope["+(j-1)+"].content";
			}
			var inputArrays3 = table.rows[j+1].cells[3].getElementsByTagName("input");
			if(inputArrays3!=null && inputArrays3.length >0){
				inputArrays3[0].name = "osRecordList["+(i-1)+"].scopes["+(j-1)+"].scope.jcff";
			}
			var inputArrays2 = table.rows[j+1].cells[2].getElementsByTagName("input");
			if(inputArrays2!=null && inputArrays2.length >0){
				inputArrays2[0].name = "osRecordList["+(i-1)+"].scopes["+(j-1)+"].scope.zltz";
			}
			var inputArrays1 = table.rows[j+1].cells[1].getElementsByTagName("input");
			if(inputArrays1!=null && inputArrays1.length >0){
				inputArrays1[0].name = "osRecordList["+(i-1)+"].scopes["+(j-1)+"].scope.content";
			}
		}
	}
	
	ossindex--;
}


function fuzhi(str,obj){
	if(obj!=null && obj.value !=""){
		$("."+str).val(obj.value);
	}
}
function buhegeNum(obj){
	if(obj!=null ){
		showorhid_bhgType(obj.value);
		var bhgnum = parseInt(obj.value);
		var num =$("#sumNum").val();
		var bhgradios =	document.getElementsByClassName("bhgradio");
		var hgradios =	document.getElementsByClassName("hgradio");
		if(obj.value !="" && obj.value>0){
			if(num<bhgnum){
				alert("不合格数量大于检验数量请重新输入");
				$(obj).val('');
			}else{
				for(var i=0;i<bhgnum;i++){
					$(bhgradios[i]).attr('checked','checked');
				}
				for(var i=0;i<num-bhgnum;i++){
					$(hgradios[bhgnum+i]).attr('checked','checked');
				}
				getbhgType('0');
			}
		}else{
			for(var i=0;i<num;i++){
				hgradios[i].checked="checked";
			}
		}
		
	}
}
function check(){
	var bool = true;
	bool = bhg_submint();
	if(!bool){
		return bool;
	}
	var arrayinputs = $(".nonull");
	if(arrayinputs!=null && arrayinputs.length>0 && bool){
		for(var i=0;i<arrayinputs.length;i++){
			if(arrayinputs[i].value == ""){
					bool = false;
				if(confirm("所有尺寸尚未全部填写完毕是否强制提交检验!")){
					bool = true;
				}
				break;
			}
		}
	}
	if(!bool){
		$("#sub_1").removeAttr("disabled");
		$("#sub").removeAttr("disabled");
	}else{
		$("#sub_1").attr("disabled","disabled");
		$("#sub").attr("disabled","disabled");
	}
	return bool;
	
	
}
function showxjbz(obj){
	if(obj!=null && obj.value!=''){
		$("#xiugaiIframe").attr("src","markIdAction!listyanshoupincizi.action?waigoujianpinci.id="+obj.value+"&status=xj&tag=show")
		$("#xjbzdiv").show();
	}
}
$(function(){
	$("#sub_1").removeAttr("disabled");
	$("#sub_1").val("确认");
})


function getbhgType( num) {
		$.ajax( {
			type : "POST",
			url : "BuHeGePinAction_ajax_findAlldefType.action",
			data : {},
			dataType : "json",
			success : function(data) {
				if (data == "error") {
					alert("出错了哦!")
				} else if (data != null) {
					$("#buhegeType_cp_" + num).empty();
					$("#buhegespan" + num)
							.html("&nbsp;&nbsp;&nbsp;&nbsp;<b>缺陷类型:</b>");
					$("#buhegeType_cp_" + num).append(
										'<option value="0">--请选择--</option>');
					$(data).each(
							function() {
								$("#buhegeType_cp_" + num).append(
										'<option value=' + this.id + '>'
												+ this.defCode + this.defName
												+ '</option>');
							})
					$("#queXianType_class").show();
				}
			}
		})
}
function findBhgByDefId(obj,num){
	if (obj != null && obj.value>0) {
		$.ajax( {
			type : "POST",
			url : "BuHeGePinAction_ajax_findBhgByDefId.action",
			data : {
				id:obj.value,
				statue:'ybd'
			},
			dataType : "json",
			success : function(data) {
				if (data == "error") {
					alert("出错了哦!")
				} else if (data != null) {
					$("#buhegeType" + num).empty();
					$("#buhegeType" + num).append(
										'<option value="0">--请选择--</option>');
					$(data).each(
							function() {
								$("#buhegeType" + num).append(
										'<option value=' + this.id + '>'
												+ this.code + this.type
												+ '</option>');
							})
				}
			}
		})

	}
}
var bhgindex=1;
function addtr(){
	var newLine = '<tr><td colspan="5"><div class="queXianType_class" id="queXianType_class" ><span><b>缺陷类型:</b> </span>' +
	'<select id="buhegeType_cp_'+bhgindex+'" name="bhgosRecordList['+bhgindex+'].code" onchange="findBhgByDefId(this,&apos;'+bhgindex+'&apos;)"></select> ' +
	'<select id="buhegeType'+bhgindex+'" name="bhgosRecordList['+bhgindex+'].buhegeId"><option value="0">--请选择--</option></select> ' +
	'<select id="buhegeType_class_'+bhgindex+'" name="bhgosRecordList['+bhgindex+'].code"> ' +
	'<option value="CR">致命缺陷(CR)</option><option value="MA">主要缺陷(MA)</option><option value="MI">次要缺陷(MI)</option></select> ' +
	'<b>数量:</b> <input type="text" value="" name="bhgosRecordList['+bhgindex+'].bhgTypeNum" class="bhg_bhgNum" id="bhg_bhgNum_'+bhgindex+'" style="width: 75px;"' +
	'onchange=numyanzheng(this,"zhengshu");bhg_bhgNumyanzheng(this) />' +
	'</div></td></tr>'
	$("#lastTr").before(newLine);
	getbhgType(bhgindex)
	bhgindex++;
}
function deltr(){
	var  bhgNum = $("#bhgNumber").val();
	if(bhgNum>0){
		if(bhgindex==1){
			alert("请至少填写一项缺陷类型。");
			return false;
		}
	}
	var n = $("#quexiantable tr").length;
	$($("#quexiantable tr")[n-2]).remove();
	bhgindex--;
}
function bhg_bhgNumyanzheng(obj){
	var bhgNum = $("#bhgNumber").val();
	if(bhgNum==null||bhgNum==''){
		$("#bhgNumber").val();
		$(obj).val("");
		alert('不合格数量不能为空，请先输入不合格数量！');
	}
	if(bhgNum>0){
		var num=0; 
		var bhg_bhgNums = document.getElementsByClassName("bhg_bhgNum");
		if(bhg_bhgNums!=null && bhg_bhgNums!=undefined
				&& bhg_bhgNums.length>0){
			for(var i=0;i<bhg_bhgNums.length;i++){
				num+=parseFloat(bhg_bhgNums[i].value);
			}
		}
		if(num>bhgNum){
			$(obj).val("");
			alert('缺陷类型数量不得超过不合格数量，请重新输入。');
		}
	}
}
function bhg_submint(){
	var bhgNum = $("#bhgNumber").val();
	var bool = true;
	if(bhgNum>0){
		var num = 0;
		for(var i=0;i<bhgindex;i++){
			var buhegeType_cp = $("#buhegeType_cp_"+i).val();
			var buhegeType = $("#buhegeType"+i).val();
			var bhg_bhgNum = $("#bhg_bhgNum_"+i).val();
			if(buhegeType_cp == '0'){
				$("#buhegeType_cp_"+i).focus();
				alert("请选择缺陷类型!~///");
				bool = false;
				break;
			}else if(buhegeType == '0'){
				$("#buhegeType"+i).focus();
				alert("请选择缺陷类型!~0000");
				bool = false;
				break;
			}else if(bhg_bhgNum == ''||bhg_bhgNum == null){
				$("#bhg_bhgNum_"+i).focus();
				alert("请填写该缺陷类型数量!~");
				bool = false;
				break;
			}else if(bhg_bhgNum != '' && bhg_bhgNum > 0){
				if(bhg_bhgNum>bhgNum){
					$("#bhg_bhgNum_"+i).focus();
					alert("该缺陷类型数量不能大于总的不合格数量!~");
					bool = false;
					break;
				}
			}
			num+= parseInt(bhg_bhgNum);
		}
		if(bool && num!=bhgNum){
			alert("缺陷类型总数与不合格数不相等。请调整!~");
			bool = false;
		}
	}
	return bool;
	
}
function showorhid_bhgType(bhgNum){
	if(bhgNum==0 || bhgNum == ''){
		for(var i=0;i<bhgindex;i++){
			 $("#buhegeType_cp_"+i).attr("name","");
			 $("#buhegeType"+i).attr("name","");
			 $("#buhegeType_class_"+i).attr("name","");;
			 $("#bhg_bhgNum_"+i).attr("name","");
		}
		$(".queXianType_class").hide();
	}else if(bhgNum>0){
		for(var i=0;i<bhgindex;i++){
			 $("#buhegeType_cp_"+i).attr("name","bhgosRecordList["+i+"].code");
			 $("#buhegeType"+i).attr("name","bhgosRecordList["+i+"].buhegeId");
			 $("#buhegeType_class_"+i).attr("name","bhgosRecordList["+i+"].code");;
			 $("#bhg_bhgNum_"+i).attr("name","bhgosRecordList["+i+"].bhgTypeNum");
		}
		$(".queXianType_class").show();
	}
}

function xuanzhe(obj){
	if(obj.value!=null && obj.value!=''){
			var str = obj.value;
			var measuring_no =	$("#measuring_no").val();
			if(obj.checked){
				if(measuring_no == '' || measuring_no == null){
					measuring_no=str;
				}else{
					measuring_no=measuring_no+";"+str;
				}
			}else{
				if(measuring_no!=''){
					measuring_no =measuring_no.replace(str,'');
					measuring_no =measuring_no.replace(";",'');
				}
			}
			if(measuring_no!=''){
				isjy = true;
				$("#msg").html("已选择的量、检具有:"+measuring_no);
			}else{
				isjy = false;
				$("#msg").html("");
			}
			$("#measuring_no").val(measuring_no);
	}
}

function autocheckList() {
if (typeof (myObj) != "undefined") {
		//打开扫描服务
		myObj.scanGongWei(1);
	} else {
		alert("未检测到摄像头!无法扫码!");
	}
}
function funFromjs(tm) {
	if (tm != null && tm != 'null' && tm != '') {
		var strs = tm.split("measuring.id=");
		if (strs != null && strs.length == 2) {
			var id1= strs[1];
			if (id1 != null && id1 > 0) {
				$.ajax( {
					type : "POST",
					url : "MeasuringAction!getMeasuringId.action?",
					dataType : "json",
					data : {
						MeasuringId:id1
					},
					success : function(data) {
						if(data!=null){
							var measuring_no =	$("#measuring_no").val();
							if(measuring_no == '' || measuring_no == null){
								measuring_no=str;
							}else if(measuring_no!=data.measuring_no){
								measuring_no=measuring_no+";"+str;
							}
							if(measuring_no!=''){
								isjy = true;
								$("#msg").html("已选择的量、检具有:"+measuring_no);
							}
							$("#measuring_no").val(measuring_no);
						}
					}
				})
			}
		}

	}
}
</script>
	</body>
</html>
