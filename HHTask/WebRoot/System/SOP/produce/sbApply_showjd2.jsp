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
		.dhlabel {
	width: auto;
	height: 30px;
	border-radius: 5px 5px 0px 0px;
	line-height: 30px;
	text-align: center;
	background: #eaeaea;
	display: inline-block;
	color: #000000;
	font-family: "Lucida Grande", Arial, sans-serif;
	font-size: 12px;
	font-weight: bold;
}</style>
	</head>
	<body>
		<%@include file="/util/sonTop.jsp"%>
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
							<span id="title">设变相关部门</span>
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
				<div id="toclosesbdiv"
					style="background-color: #ffffff; width: 100%;display: none;">
					<form action="procardTemplateGyAction_closeSb.action" method="post">
						<input name="id" id="sbapplyId" type="hidden">
						<input name="cpage" id="cpage" type="hidden" value="${cpage}">
						是否执行:
						<input type="radio" value="已执行" name="type"> 已执行
						<input type="radio" value="不执行" name="type"> 不执行
						<input type="radio" value="不涉及" name="type" checked="checked"> 不涉及<br/>
						备注:
						<textarea  name="type" rows="4" cols="40"></textarea>
						<br/><input type="submit" value="确定"><br/><br/>
					</form>
				</div>
			</div>
		</div>
		<div id="gongneng" style="width: 100%;">
			<div align="right" style="font-size: 13px;color: #000000;font-weight: bolder;">
				<label id="fqsb" onclick="sbprocessStatus(0)" class="dhlabel">
					设变发起
				</label>
				<img alt="" src="<%=basePath%>/images/right-arrow16.png" >
				<label id="ffxmz" onclick="sbprocessStatus(1)" class="dhlabel">
					分发项目组
				</label>
				<img alt="" src="<%=basePath%>/images/right-arrow16.png" >
				<label id="zgcp" onclick="sbprocessStatus(2)" class="dhlabel">
					项目主管初评
				</label>
				<img alt="" src="<%=basePath%>/images/right-arrow16.png" >
				<label id="xzsblj" onclick="sbprocessStatus(3)" class="dhlabel">
					选择设变零件
				</label>
				<img alt="" src="<%=basePath%>/images/right-arrow16.png" >
				<label id="bgsj" onclick="sbprocessStatus(4)" class="dhlabel">
					变更设计
				</label>
				<img alt="" src="<%=basePath%>/images/right-arrow16.png" >
				<label id="gxsps" onclick="sbprocessStatus(5)" class="dhlabel">
					工程师评审
				</label>
				<img alt="" src="<%=basePath%>/images/right-arrow16.png" >
				<label id="zpgbm" onclick="sbprocessStatus(6)" class="dhlabel">
					指派各部门
				</label>
				<img alt="" src="<%=basePath%>/images/right-arrow16.png" >
				<label id="cbsh" onclick="sbprocessStatus(7)" class="dhlabel">
					成本审核
				</label>
				<img alt="" src="<%=basePath%>/images/right-arrow16.png" >
				<label id="bmps" onclick="sbprocessStatus(8)" class="dhlabel">
					各部门评审
				</label>
				<img alt="" src="<%=basePath%>/images/right-arrow16.png" >
				<label id="zlgx" onclick="sbprocessStatus(9)" class="dhlabel">
					资料更新
				</label>
				<img alt="" src="<%=basePath%>/images/right-arrow16.png" >
				<label id="glsc" onclick="sbprocessStatus(10)" class="dhlabel">
					关联并通知生产
				</label>
				<img alt="" src="<%=basePath%>/images/right-arrow16.png" >
				<label id="schx" onclick="sbprocessStatus(11)" class="dhlabel">
					上传佐证
				</label>
				<img alt="" src="<%=basePath%>/images/right-arrow16.png" >
				<label id="gb" onclick="sbprocessStatus(12)" class="dhlabel">
					设变关闭
				</label>
				&nbsp;&nbsp;
			</div>
			<div align="right" style="width: 100%;">
				<font color="red" style="font-size: 11px;">注:设变本身不能对本身删除,只有设变上层才可以删除下层子件(总成除外),修改用量和是否领料只需要对上层设变即可</font>
			</div>
			<br />
			<div style="width: 100%;background-color: #D1EEEE;">
				<table class="table" >
					<tr>
						<th colspan="8">
							设变申请信息
						</th>
					</tr>
					<tr>
						<th align="right">
							设变单号:
						</th>
						<td>
							${bbAply.sbNumber}
						</td>
						<th align="right">
							外部设变单号:
						</th>
						<td>
							${bbAply.outbsNumber}
						</td>
						<th align="right">
							总成件号:
						</th>
						<td>
							${bbAply.markId}
						</td>
						<th align="right">
							业务件号:
						</th>
						<td>
							${bbAply.ywMarkId}
						</td>
					</tr>
					<tr>
						<th align="right">
							总成名称:
						</th>
						<td>
							${bbAply.proName}
						</td>
						<th align="right">
							版本:
						</th>
						<td>
							${bbAply.banbenNumber}
						</td>
						<th align="right">
							发起人:
						</th>
						<td>
							${bbAply.applicantName}
						</td>
						<th align="right">
							发起时间:
						</th>
						<td>
							${bbAply.applyTime}
						</td>
					</tr>
					<tr>
						<th align="right">
							内部计划待转换生产BOM数量:
						</th>
						<td>
							${bbAply.dzcount}
						</td>
						<th align="right">
							生产BOM未运算MRP数量:
						</th>
						<td>
							${bbAply.wjhcount}
						</td>
						<th align="right">
							生产BOM运算MRP未排产数量:
						</th>
						<td>
							${bbAply.wpccount}
						</td>
						<th align="right">
							生产BOM已排产数量:
						</th>
						<td>
							${bbAply.pccount}
						</td>
					</tr>
					<tr>
						<th align="right">
							生产组别:
						</th>
						<td>
							${bbAply.aboutPlace}
						</td>
						<th align="right">
							设变类型:
						</th>
						<td>
							${bbAply.sbType}
						</td>
						<th align="right">
							备注:
						</th>
						<td colspan="3">
							${bbAply.remark}
						</td>
					</tr>
					<tr>
						<th align="right">
							设变状态:
						</th>
						<td style="background-color: green;color: #ffffff">
							${bbAply.processStatus}
						</td>
						<th align="right">
							附件:
						</th>
						<td  id="fujian_td">
						
						</td>
						<th align="right">
							设变原因:
						</th>
						<td colspan="3" id="fujian_td">
							${bbAply.sbreason}
						</td>
					</tr>
				</table>
			</div>
		</div>
		<br />
		<div style="background-color: #BCD2EE;">
			<div >
			<input type="button" onclick="xysbDetail()" value="设变零件展示"/>&nbsp;&nbsp;&nbsp;
			<input type="button" onclick="xysbbgDetail()" value="变更内容展示"/>&nbsp;&nbsp;&nbsp;
			<input type="button" onclick="xysbxgljDetail()" value="变更相关零件展示"/>&nbsp;&nbsp;&nbsp;
			<input id="sbmxtag" type="hidden" value="h">
			<input id="sbbgmxtag" type="hidden" value="h">
			<input id="sbxgljmxtag" type="hidden" value="h">
			<s:if test="bbAply.bbList!=null&&bbAply.bbList.size()>0">
			<a target="_sbBOm" href="procardTemplateSbAction_showbzProcardTemplatesb.action?id=${bbAply.id}">进入设变BOM</a>
					</s:if>
			</div>
			<table id="sbmxdtable" class="table" style="display: none;">
				<tr>
					<th colspan="31">
						设变零件列表
					</th>
				</tr>
				<tr>
					<th rowspan="2">
						件号
					</th>
					<th rowspan="2">
						名称
					</th>
					<th rowspan="2">
						单位
					</th>
					<th rowspan="2">
						零件类型
					</th>
					<th rowspan="2">
						生产类型
					</th>
					<th rowspan="2">
						原版本号
					</th>
					<th rowspan="2">
						升级版本号
					</th>
					<th rowspan="2">
						原版次
					</th>
					<th rowspan="2">
						新版次
					</th>
					<th rowspan="2">
						升级类型
					</th>
					<th rowspan="2">
						单独升版
					</th>
					<th rowspan="2">
						是否修改工序
					</th>
					<th rowspan="2">
						是否修改图纸
					</th>
					<th rowspan="2">
						在制数量
					</th>
					<th rowspan="2">
						在途数量
					</th>
					<th rowspan="2">
						库存数量
					</th>
					<th rowspan="2">
						设变明细
					</th>
					<th rowspan="2">
						编制进度
					</th>
					<th align="center" colspan="13">设变涉及零件</th>
				</tr>
				<tr>
					<th>件号</th>
					<th>名称</th>
					<th>版本</th>
					<th>类型</th>
					<th>规格</th>
					<th>物料类别</th>
					<th>供料属性</th>
					<th>变量(正增负减)</th>
					<th>在制</th>
					<th>在途</th>
					<th>在库</th>
					<th>呆滞</th>
					<th>单位</th>
				</tr>
				<s:iterator value="bbAply.bbList" var="bb" status="bbstatus">
					<s:if test="bbAply.changeremark!=nulll&&bbAply.changeremark=='yes'">
						<form id="pbbremarkForm<s:property value="#bbstatus.index"/>"
							action="procardTemplateGyAction_updatepbbremark.action"
							method="post">
							<input type="hidden" name="ptbb.id" value="${bb.id}" />
							<tr>
								<s:if test="#bb.ptasbList.size()==0">
								<td align="left">
								</s:if>
								<s:else>
									<td align="left" rowspan="<s:property value="#bb.ptasbList.size()"/>">
								</s:else>
									${bb.markId}
								</td>
								<s:if test="#bb.ptasbList.size()==0">
								<td align="left">
								</s:if>
								<s:else>
									<td align="left" rowspan="<s:property value="#bb.ptasbList.size()"/>">
								</s:else>
									${bb.proName}
								</td>
								<s:if test="#bb.ptasbList.size()==0">
								<td align="left">
								</s:if>
								<s:else>
									<td align="left" rowspan="<s:property value="#bb.ptasbList.size()"/>">
								</s:else>
									${bb.unit}
								</td>
								<s:if test="#bb.ptasbList.size()==0">
								<td align="left">
								</s:if>
								<s:else>
									<td align="left" rowspan="<s:property value="#bb.ptasbList.size()"/>">
								</s:else>
									${bb.procardStyle}
								</td>
								<s:if test="#bb.ptasbList.size()==0">
								<td align="left">
								</s:if>
								<s:else>
									<td align="left" rowspan="<s:property value="#bb.ptasbList.size()"/>">
								</s:else>
									${bb.productStyle}
								</td>
								<s:if test="#bb.ptasbList.size()==0">
								<td align="left">
								</s:if>
								<s:else>
									<td align="left" rowspan="<s:property value="#bb.ptasbList.size()"/>">
								</s:else>
									${bb.banBenNumber}
								</td>
								<s:if test="#bb.ptasbList.size()==0">
								<td align="left">
								</s:if>
								<s:else>
									<td align="left" rowspan="<s:property value="#bb.ptasbList.size()"/>">
								</s:else>
									${bb.banci}
								</td>
								<s:if test="#bb.ptasbList.size()==0">
								<td align="left">
								</s:if>
								<s:else>
									<td align="left" rowspan="<s:property value="#bb.ptasbList.size()"/>">
								</s:else>
									${bb.newBanBenNumber}
								</td>
								<s:if test="#bb.ptasbList.size()==0">
								<td align="right">
								</s:if>
								<s:else>
									<td align="right" rowspan="<s:property value="#bb.ptasbList.size()"/>">
								</s:else>
									${bb.newbanci}
								</td>
								<s:if test="#bb.ptasbList.size()==0">
								<td align="left">
								</s:if>
								<s:else>
									<td align="left" rowspan="<s:property value="#bb.ptasbList.size()"/>">
								</s:else>
									${bb.uptype}
								</td>
								<s:if test="#bb.ptasbList.size()==0">
								<td align="left">
								</s:if>
								<s:else>
									<td align="left" rowspan="<s:property value="#bb.ptasbList.size()"/>">
								</s:else>
									${bb.singleSb}
								</td>
								<s:if test="#bb.ptasbList.size()==0">
								<td align="left">
								</s:if>
								<s:else>
									<td align="left" rowspan="<s:property value="#bb.ptasbList.size()"/>">
								</s:else>
									${bb.changeProcess}
								</td>
								<s:if test="#bb.ptasbList.size()==0">
								<td align="left">
								</s:if>
								<s:else>
									<td align="left" rowspan="<s:property value="#bb.ptasbList.size()"/>">
								</s:else>
									${bb.changeTz}
								</td>
								<s:if test="#bb.ptasbList.size()==0">
								<td align="right">
								</s:if>
								<s:else>
									<td align="right" rowspan="<s:property value="#bb.ptasbList.size()"/>">
								</s:else>
									${bb.zaizhiCount}
								</td>
								<s:if test="#bb.ptasbList.size()==0">
								<td align="right">
								</s:if>
								<s:else>
									<td align="right" rowspan="<s:property value="#bb.ptasbList.size()"/>">
								</s:else>
									${bb.zaituCount}
								</td>
								<s:if test="#bb.ptasbList.size()==0">
								<td align="right">
								</s:if>
								<s:else>
									<td align="right" rowspan="<s:property value="#bb.ptasbList.size()"/>">
								</s:else>
									${bb.kucunCount}
								</td>
								<s:if test="#bb.ptasbList.size()==0">
								<td align="left">
								</s:if>
								<s:else>
									<td align="left" rowspan="<s:property value="#bb.ptasbList.size()"/>">
								</s:else>
									<textarea rows="2" name="ptbb.remark" cols="15">${bb.remark}</textarea>
									<s:if test="bbAply.changeremark!=nulll&&bbAply.changeremark=='yes'">
									<input id="bbreamkbtn<s:property value="#bbstatus.index"/>"
										type="button"
										onclick="updatepbbremark(<s:property value="#bbstatus.index"/>)"
										value="修改">
									</s:if>
								</td>
								<s:if test="#bb.ptasbList.size()==0">
								<td align="left">
								</s:if>
								<s:else>
									<td align="left" rowspan="<s:property value="#bb.ptasbList.size()"/>">
								</s:else>
									${bb.bzStatus}
								</td>
								<s:if test="#bb.ptasbList.size()==0">
								<th>/</th>
								<th>/</th>
								<th>/</th>
								<th>/</th>
								<th>/</th>
								<th>/</th>
								<th>/</th>
								<th>/</th>
								<th>/</th>
								<th>/</th>
								<th>/</th>
								<th>/</th>
								<th>/</th>
								</tr>
								</s:if>
								<s:else>
									<s:iterator value="#bb.ptasbList" id="ptasb" status="ptasbstatus">
										<s:if test="#ptasbstatus.index>0">
											<tr>
											
										</s:if>
										<td><s:property value="#ptasb.markId"/></td>
										<td><s:property value="#ptasb.proName"/></td>
										<td><s:property value="#ptasb.banben"/></td>
										<td><s:property value="#ptasb.procardStyle"/></td>
										<td><s:property value="#ptasb.specification"/></td>
										<td><s:property value="#ptasb.wgType"/></td>
										<td> <s:property value="#ptasb.kgliao"/></td>
										<td align="right"><s:property value="#ptasb.yongliao"/></td>
										<td align="right"><s:property value="#ptasb.zaizhiCount"/></td>
										<td align="right"><s:property value="#ptasb.zaituCount"/></td>
										<td align="right"><s:property value="#ptasb.zaikuCount"/></td>
										<td align="right"><s:property value="#ptasb.daizhiCount"/></td>
										<td> <s:property value="#ptasb.unit"/></td>
										</tr>
									</s:iterator>
								</s:else>
							
						</form>
					</s:if>
					<s:else>
						<tr>
							<td align="left">
								${bb.markId}
							</td>
							<td align="left">
								${bb.proName}
							</td>
							<td align="left">
								${bb.unit}
							</td>
							<td align="left">
								${bb.procardStyle}
							</td>
							<td align="left">
								${bb.productStyle}
							</td>
							<td align="left">
								${bb.banBenNumber}
							</td>
							<td align="left">
								${bb.newBanBenNumber}
							</td>
							<td align="right">
								${bb.banci}
							</td>
							<td align="right">
								${bb.newbanci}
							</td>
							<td align="left">
								${bb.uptype}
							</td>
							<td align="left">
								${bb.singleSb}
							</td>
							<td align="left">
								${bb.changeProcess}
							</td>
							<td align="left">
								${bb.changeTz}
							</td>
							<td align="right">
								${bb.zaizhiCount}
							</td>
							<td align="right">
								${bb.zaituCount}
							</td>
							<td align="right">
								${bb.kucunCount}
							</td>
							<td align="left">
								${bb.remark}
							</td>
							<td align="left">
								${bb.bzStatus}
							</td>
						</tr>
					</s:else>
				</s:iterator>
			</table>
		</div>
		<div>
			<table id="sbbgmxdtable" class="table" style="display: none;">
				<tr>
					<td colspan="6">变更内容</td>
				</tr> 
				<tr class="bgnrty">
					<th>件号 </th>
					<th>数据类型</th>
					<th>操作类型</th>
					<th>原值</th>
					<th>新值</th>
					<th>属性名称</th>
				</tr>
				<s:iterator value="bbAply.ptsbdifferenceList" id="pageptsbd" status="ptsbdstatus">
				<tr class="bgnrty" >
					<td rowspan="<s:property value="#pageptsbd.ptsbddList.size()"/>">${pageptsbd.markId}</td>
					<s:iterator value="#pageptsbd.ptsbddList" id="ptsbddetail" status="ptsbddetailStatus">
					<s:if test="#ptsbddetailStatus.index>0">
						<tr class="bgnrty" >
					</s:if>
					<td>${ptsbddetail.datatype}</td>
					<td>${ptsbddetail.optype}</td>
					<s:if test="#ptsbddetail.datatype=='本身'">
						<td>${ptsbddetail.oldData}</td>
						<td>${ptsbddetail.newData}</td>
						<td>${ptsbddetail.sxName}</td>
					</s:if>
					<s:if test="#ptsbddetail.datatype=='子件'">
						<td colspan="3">${ptsbddetail.markId}&nbsp;&nbsp;${ptsbddetail.proname}</td>
					</s:if>
					<s:if test="#ptsbddetail.datatype=='工序'">
						<td>${ptsbddetail.processno1}(${ptsbddetail.processname1})</td>
						<td>${ptsbddetail.processno}(${ptsbddetail.processname})</td>
						<td>/</td>
					</s:if>
					<s:if test="#ptsbddetail.datatype=='图纸'">
					<td colspan="3">
					<a href="/upload/file/processTz/${ptsbddetail.month}/">${ptsbddetail.markId}</a>
					&nbsp;&nbsp;${ptsbddetail.oldfileName}</td>
					</s:if>
					</tr>
					</s:iterator>
				</s:iterator>
			</table>
		</div>
		<br/>
		<div>
			<table id="sbxgljmxdtable" class="table" style="display: none;">
				<tr>
					<th colspan="17">设变相关零件及处理方案</th>
				</tr> 
				<tr>
					<tr>
					<th>件号</th>
					<th>名称</th>
					<th>版本</th>
					<th>类型</th>
					<th>规格</th>
					<th>物料类别</th>
					<th>供料属性</th>
					<th>变量(正增负减)</th>
					<th>在制</th>
					<th>在途</th>
					<th>在库</th>
					<th>呆滞</th>
					<th>单位</th>
					<th>在制处理</th>
					<th>在途处理</th>
					<th>库存处理</th>
					<th>呆滞处理</th>
				</tr>
				</tr>
				<s:iterator value="bbAply.ptasbList" id="pageptasb" status="ptasbStatus">
				<tr >
					<td><s:property value="#pageptasb.markId"/></td>
					<td><s:property value="#pageptasb.proName"/></td>
					<td><s:property value="#pageptasb.banben"/></td>
					<td><s:property value="#pageptasb.procardStyle"/></td>
					<td><s:property value="#pageptasb.specification"/></td>
					<td><s:property value="#pageptasb.wgType"/></td>
					<td> <s:property value="#pageptasb.kgliao"/></td>   
					<td align="right"><s:property value="#pageptasb.yongliao"/></td>
					<td align="right"><s:property value="#pageptasb.zaizhiCount"/></td>
					<td align="right"><s:property value="#pageptasb.zaituCount"/></td>
					<td align="right"><s:property value="#pageptasb.zaikuCount"/></td>
					<td align="right"><s:property value="#pageptasb.daizhiCount"/></td>
					<td> <s:property value="#pageptasb.unit"/></td>
					<td><font size="1"><s:property value="#pageptasb.zaizhicltype"/></font></td>
					<td><font size="1"><s:property value="#pageptasb.zaitucltype"/></font></td>
					<td><font size="1"><s:property value="#pageptasb.zaikucltype"/></font></td>
					<td><font size="1"><s:property value="#pageptasb.daizhicltype"/></font></td>
					</tr>
				</s:iterator>
			</table>
		</div>
		<br />
		<br />
		<div id="fqsbdiv" style="display: none;" align="center">
			<s:if
				test="#session.Users.id==bbAply.applicantId&&(bbAply.processStatus=='设变发起'||bbAply.processStatus=='项目主管初评')
				&&(bbAply.status==null||bbAply.status=='')">
				<input type="button" value="撤销" style="width: 80px; height: 60px;"
					onclick="cxSbapply${bbAply.id})" />
			</s:if>
		</div>
		<div id="ffxmzdiv" style="display: none;background-color:#CAE1FF;" align="center">
			<s:if test="bbAply.processStatus=='分发项目组'">
				<form action="procardTemplateGyAction_sbffxmz.action" method="post"
					onsubmit="disBrn();">
					<input type="hidden" value="${bbAply.id}" name="bbAply.id">
					<table class="table" >
						<tr>
							<th>
								项目主管
							</th>
							<td colspan="5">
								<s:iterator value="list" id="ap">
									<input value="${ap.id}" type="radio" name="id" />
									<s:property value="#ap.userName" />
								</s:iterator>
							</td>
						</tr>
						<tr>
							<th>
								设变备注
							</th>
							<td colspan="2">
								<textarea id="gbremark" name="bbAply.remark" rows="4" cols="40">${bbAply.remark}</textarea>
							</td>
							<th>
								生产组别
							</th>
							<td>
								<input value="${bbAply.aboutPlace}" id="aboutPlace"
									name="bbAply.aboutPlace">
							</td>
						</tr>
						<tr>
							<td colspan="6" align="center">
								<input id="subBtn" type="submit" value="提交"
									style="width: 80px; height: 60px;" />
								<s:if test="ryzbList!=null&&ryzbList.size()>0">
									<s:iterator value="ryzbList" id="ryzb">
										<s:if test="#ryzb=='设变关闭组'">
											<s:if test="bbAply.workItemId!=null&&bbAply.workItemId>0&&bbAply.ecType!=null">
												 <input type="button" value="关闭并反馈"
												style="width: 80px; height: 30px;"
												onclick="toclose('gbremark',${bbAply.id},${bbAply.workItemId})" />
											</s:if>
											<s:else>
												<input type="button" value="关闭"
												style="width: 60px; height: 30px;"
												onclick="toclose('gbremark',${bbAply.id})" />
											</s:else>
										</s:if>
									</s:iterator>
								</s:if>
							</td>
						</tr>
					</table>
				</form>
			</s:if>
			<s:if
				test="#session.Users.id==bbAply.applicantId&&(bbAply.processStatus=='设变发起'||bbAply.processStatus=='项目主管初评')
				&&(bbAply.status==null||bbAply.status=='')">
				<br />
				<input type="button" value="撤销" style="width: 80px; height: 60px;"
					onclick="cxSbapply${bbAply.id})" />
			</s:if>
		</div>
		<div id="zgcpdiv" style="display: none;background-color:#CAE1FF;" align="center">
			<h3>
				项目主管初评
			</h3>
			<table class="table" >
				<tr>
					<th>
						部门
					</th>
					<th>
						姓名
					</th>
					<th>
						评审内容
					</th>
					<th>
						项目工程师
					</th>
				</tr>
				<s:iterator value="bbAply.bbJudegeList" id="cpPepole"
					status="cpStatus">
					<s:if test="#cpPepole.judgeType=='初评'">
						<s:if
							test="bbAply.processStatus=='项目主管初评'&&#session.Users.id==#cpPepole.userId">
							<form id="chupin<s:property value="#cpStatus.index"/>"
								method="post">
								<tr>
									<td align="center">
										<input type="hidden" name="ptbbJudges.id"
											value="<s:property value="#cpPepole.id"/>">
										<s:property value="#cpPepole.dept" />
									</td>
									<td align="center">
										<s:property value="#cpPepole.userName" />
									</td>
									<td align="center">
										<textarea rows="4" cols="70" name="ptbbJudges.remark"
											id="cpremark"><s:property value="#cpPepole.remark" /></textarea>
										<font color="red">*</font>
									</td>
									<td id="gcstd">
									<SELECT id="xmgcs" 
 											name="ptbbJudges.selectUserId">
 											<option></option>
 									</SELECT>
									
<%--										<SELECT id="xmgcsDept"--%>
<%--											onchange="getgcs(<s:property value="#cpStatus.index"/>)"><s:iterator value="bbAply.bbJudegeList" id="gcsPepole"><s:if--%>
<%--													test="#gcsPepole.judgeType=='工程师'&&#gcsPepole.judgedId==#cpPepole.id"><option value="<s:property value="#gcsPepole.userId"/>"><s:property value="#gcsPepole.dept" /></option></s:if></s:iterator></SELECT>--%>
<%--										<SELECT id="xmgcs<s:property value="#cpStatus.index"/>"--%>
<%--											name="ptbbJudges.selectUserId">--%>
<%--											<s:iterator value="bbAply.bbJudegeList" id="gcsPepole">--%>
<%--												<s:if--%>
<%--													test="#gcsPepole.judgeType=='工程师'&&#gcsPepole.judgedId==#cpPepole.id">--%>
<%--													<option value="<s:property value="#gcsPepole.userId"/>">--%>
<%--														<s:property value="#gcsPepole.userName" />--%>
<%--													</option>--%>
<%--												</s:if>--%>
<%--											</s:iterator>--%>
<%--										</SELECT>--%>
									</td>

								</tr>
								<tr>
									<td colspan="2" align="center">
										设变备注
									</td>
									<td colspan="2">
										<textarea id="dhremark1" name="remark" rows="4" cols="40">${bbAply.remark}</textarea>
									</td>
								</tr>
								<tr>
									<td align="center" colspan="4">
										<input type="button" value="提交"
											onclick="submitchupin(<s:property value="#cpStatus.index"/>)">
										<input type="button" value="打回"
											onclick="todahui('dhremark1',${bbAply.id})">
										<input id="surechangebtn" type="button" value="工作移交"
											onclick="transferWork(${bbAply.id})">
									</td>
								</tr>
							</form>
						</s:if>
						<s:else>
							<tr>
								<td align="left">
									<s:property value="#cpPepole.dept" />
								</td>
								<td align="left">
									<s:property value="#cpPepole.userName" />
								</td>
								<td align="left">
									<textarea rows="4" cols="70" readonly="readonly"><s:property value="#cpPepole.remark" /></textarea>
								</td>
								<td align="left">
									<s:iterator value="bbAply.bbJudegeList" id="gcsPepole">
										<s:if
											test="#gcsPepole.judgeType=='工程师'&&#gcsPepole.judgedId==#cpPepole.id">
											<s:property value="#gcsPepole.dept" />-<s:property
												value="#gcsPepole.userName" />&nbsp;&nbsp;
										</s:if>
									</s:iterator>
								</td>
							</tr>
						</s:else>
					</s:if>
				</s:iterator>
			</table>
			<br />
			<table class="table">
				<tr>
					<th>
						部门
					</th>
					<th>
						类型
					</th>
					<th>
						工号
					</th>
					<th>
						评审人
					</th>
					<th>
						评语
					</th>
					<th>
						评论时间
					</th>
				</tr>
				<s:iterator value="bbAply.bbJudegeList" id="nsPepole">
					<s:if test="#nsPepole.judgeType=='初评'||#nsPepole.judgeType=='工程师'">
						<tr>
							<td align="left">
								${nsPepole.dept}
							</td>
							<td align="left">
								${nsPepole.judgeType}
							</td>
							<td align="left">
								${nsPepole.userCode}
							</td>
							<td align="left">
								${nsPepole.userName}
							</td>
							<td align="left">
								${nsPepole.remark}
							</td>
							<td align="left">
								${nsPepole.plTime}
							</td>
						</tr>
					</s:if>
				</s:iterator>
			</table>
		</div>
		<div id="xzsbljdiv" style="display: none;" align="center">
				<h3>
					选择设变零件
				</h3>
				<s:if
					test="bbAply.processStatus=='选择设变零件'">
						<s:iterator value="bbAply.bbJudegeList" id="gcsPepole2">
								<s:if test="#gcsPepole2.judgeType=='工程师'&&#session.Users.id==#gcsPepole2.userId">
									<div align="center">
									<input id="surechangebtn" type="button" value="工作移交"
											onclick="transferWork(${bbAply.id})">
									</div>
										<iframe id="showProcess" src="procardTemplateGyAction_toSelectSbProcardTemplate.action?id=${bbAply.id}&ptbbJudges.id=${gcsPepole2.id}" marginwidth="0" marginheight="0"
										hspace="0" vspace="0" frameborder="0" scrolling="yes"
										style="width: 98%; height: 1500px; margin: 0px; padding: 0px;">
										</iframe>
								</s:if>
						</s:iterator>
					
				</s:if>
				<s:else>
				<br/>
				<table class="table">
				<tr>
					<td colspan="4" align="center">各部门评审人员</td>
				</tr>
					<tr>
						<th>
							部门
						</th>
						<th>
							工号
						</th>
						<th>
							评审人
						</th>
						<th>
							评语
						</th>
						<th>
							评论时间
						</th>
					</tr>
					<s:iterator value="bbAply.bbJudegeList" id="nsPepole">
					<s:if test="#nsPepole.judgeType=='工程师'">
						<tr>
							<td align="left">
								${nsPepole.dept}
							</td>
							<td align="left">
								${nsPepole.userCode}
							</td>
							<td align="left">
								${nsPepole.userName}
							</td>
							<td align="left">
								${nsPepole.remark}
							</td>
							<td align="left">
								${nsPepole.plTime}
							</td>
						</tr>
					</s:if>
					</s:iterator>
				</table>
				</s:else>
				<div id="addnpDiv" >
				</div>
			</div>
			<div id="bgsjdiv" style="display: none;" align="center">
				<h3>
					设计变更
				</h3>
				<br/>
				<s:if
					test="bbAply.processStatus=='变更设计'">
				<table class="table">
				<tr><td colspan="15" align="center">生产件明细</td></tr>
									<tr>
									<th>序号</th>
									<th>订单号</th>
									<th>总成件号</th>
									<th>业务件号</th>
									<th>总成批次</th>
									<th>总进度</th>
									<th>件号</th>
									<th>类型</th>
									<th>批次</th>
									<th>版本号</th>
									<th>零件数</th>
									<th>工序号</th>
									<th>工序名称</th>
									<th>数量</th>
									<th>评审/处理方案 </th>
									</tr>
									<tr><td colspan="15" align="center" bgcolor="red">同总成相关生产</td>
									</tr>
									<s:iterator value="pabbList" id="pabb1" status="pastatu1">
									<tr>
									<td align="right" rowspan="<s:property value="#pabb1.processabbList.size()"/>">
									<s:property value="#pastatu1.index"/> 
									</td>
									<td align="left" rowspan="<s:property value="#pabb1.processabbList.size()"/>">
									${pabb1.orderNumber}</td>
									<td align="left" rowspan="<s:property value="#pabb1.processabbList.size()"/>">${pabb1.rootMarkId}</td>
									<td align="left" rowspan="<s:property value="#pabb1.processabbList.size()"/>">${pabb1.ywMarkId}</td>
									<td align="left" rowspan="<s:property value="#pabb1.processabbList.size()"/>">${pabb1.rootSelfCard}</td>
									<td align="left" rowspan="<s:property value="#pabb1.processabbList.size()"/>">${pabb1.totaljd}</td>
									<td align="left" rowspan="<s:property value="#pabb1.processabbList.size()"/>"><a href="ProcardAction!findProcardByRunCard2.action?id=${pabb1.procardId}&pageStatus=history&viewStatus=">${pabb1.markId}</a></td>
									<td align="left" rowspan="<s:property value="#pabb1.processabbList.size()"/>">${pabb1.procardStyle}</td>
									<td align="left" rowspan="<s:property value="#pabb1.processabbList.size()"/>">${pabb1.selfCard}</td>
									<td align="left" rowspan="<s:property value="#pabb1.processabbList.size()"/>">${pabb1.banebenNumber}</td>
									<td align="left" rowspan="<s:property value="#pabb1.processabbList.size()"/>">${pabb1.scCount}</td>
									<s:iterator value="#pabb1.processabbList" id="processabb1" status="pabbStatus1">
									 <s:if test="#pabbStatus1.index>0">
									 	<tr>
									 </s:if>
									 <td align="left" >
									 	<s:property value="#processabb1.processNo"/>
									 </td>
									 <td align="left" >
									 	<s:property value="#processabb1.processName"/>
									 </td>
									 <td align="left" >
									 	<s:property value="#processabb1.scCount"/>
									 </td>
									 <td align="left">
										<s:if test="#processabb1.pbbjList!=null&&#processabb1.pbbjList.size()>0">
										 <s:iterator value="#processabb1.pbbjList" id="pbbj1">
										 		${pbbj1.userName}-${pbbj1.remark} 
										 		<s:if test="#pbbj1.bcremark!=null&&#pbbj1.bcremark.length()>0">
										 		<font color="red">(${pbbj1.bcremark})</font>
										 		</s:if>;
										 </s:iterator>
										</s:if>
									</td>
									 </tr>
									</s:iterator>
									</s:iterator>
									<tr><td colspan="15" align="center" bgcolor="red">非同总成相关生产</td>
									</tr>
									<s:iterator value="pabbList2" id="pabb2" status="pastatu2">
									<tr>
									<td align="right"><s:property value="#pastatu2.index"/>
									 </td>
									<td align="left" rowspan="<s:property value="#pabb1.processabbList.size()"/>">
									${pabb2.orderNumber}</td>
									<td align="left" rowspan="<s:property value="#pabb2.processabbList.size()"/>">${pabb2.rootMarkId}</td>
									<td align="left" rowspan="<s:property value="#pabb2.processabbList.size()"/>">${pabb2.ywMarkId}</td>
									<td align="left" rowspan="<s:property value="#pabb2.processabbList.size()"/>">${pabb2.rootSelfCard}</td>
									<td align="left" rowspan="<s:property value="#pabb2.processabbList.size()"/>">${pabb2.totaljd}</td>
									<td align="left" rowspan="<s:property value="#pabb2.processabbList.size()"/>"><a href="ProcardAction!findProcardByRunCard2.action?id=${pabb2.procardId}&pageStatus=history&viewStatus=">${pabb2.markId}</a></td>
									<td align="left" rowspan="<s:property value="#pabb2.processabbList.size()"/>">${pabb21.procardStyle}</td>
									<td align="left" rowspan="<s:property value="#pabb2.processabbList.size()"/>">${pabb2.selfCard}</td>
									<td align="left" rowspan="<s:property value="#pabb2.processabbList.size()"/>">${pabb2.banebenNumber}</td>
									<td align="left" rowspan="<s:property value="#pabb2.processabbList.size()"/>">${pabb2.scCount}</td>
									<s:iterator value="#pabb2.processabbList" id="processabb2" status="pabbStatus2">
									 <s:if test="#pabbStatus2.index>0">
									 	<tr>
									 </s:if>
									 <td align="left" >
									 	<s:property value="#processabb2.processNo"/>
									 </td>
									 <td align="left" >
									 	<s:property value="#processabb2.processName"/>
									 </td>
									 <td align="left" >
									 	<s:property value="#processabb2.scCount"/>
									 </td>
									 <td align="left">
									<s:if test="#processabb2.pbbjList!=null&&#processabb2.pbbjList.size()>0">
										 <s:iterator value="#processabb2.pbbjList" id="pbbj2">
										 		${pbbj2.userName}-${pbbj2.remark}
										 		<s:if test="#pbbj2.bcremark!=null&&#pbbj2.bcremark.length()>0">
										 		<font color="red">(${pbbj2.bcremark})</font>
										 		</s:if>;
										 </s:iterator>
									</s:if>
									</td>
									 </tr>
									</s:iterator>
									</s:iterator>
								</table>
					<s:iterator value="bbAply.bbJudegeList" id="bgPepole">
					<s:if test="#bgPepole.judgeType=='工程师'&&#session.Users.id==#bgPepole.userId">
						<br/>
						<div align="center">
							<input id="surechangebtn" type="button" value="变更完成" onclick="surechange()" >
							<input id="surechangebtn" type="button" value="工作移交" onclick="transferWork(${bbAply.id})" >
						</div>
					</s:if>
					</s:iterator>
				</s:if>
				<br/>
				<table class="table">
				<tr>
					<td colspan="5" align="center">各部门评审人员</td>
				</tr>
					<tr>
						<th>
							部门
						</th>
						<th>
							工号
						</th>
						<th>
							评审人
						</th>
						<th>
							评语
						</th>
						<th>
							评论时间
						</th>
					</tr>
					<s:iterator value="bbAply.bbJudegeList" id="nsPepole">
					<s:if test="#nsPepole.judgeType=='工程师'">
						<tr>
							<td align="left">
								${nsPepole.dept}
							</td>
							<td align="left">
								${nsPepole.userCode}
							</td>
							<td align="left">
								${nsPepole.userName}
							</td>
							<td align="left">
								${nsPepole.remark}
							</td>
							<td align="left">
								${nsPepole.plTime}
							</td>
						</tr>
					</s:if>
					</s:iterator>
				</table>
				<div id="addnpDiv" >
				</div>
			</div>
		<div id="gxspsdiv" style="display: none;background-color:#CAE1FF;" align="center">
			<h3>
				项目工程师评审
			</h3>
			<s:if test="bbAply.processStatus=='工程师评审'">
				<s:set name="jlIndex1" value="0"></s:set>
				<s:set name="jlIndex2" value="0"></s:set>
				<s:iterator value="bbAply.bbJudegeList" id="jlPepole">
					<s:if
						test="#jlPepole.judgeType=='工程师'&&#session.Users.id==#jlPepole.userId">
						<form id="jlform" method="post">
						<input type="hidden" value="${bbAply.id}" name="bbAply.id">
							<table  class="table" >
								<tr bgcolor="yellow">
									<th colspan="17" align="center">设变涉及零件处理方案</th>
								</tr>
								<tr >
									<th>件号</th>
									<th>名称</th>
									<th>版本</th>
									<th>类型</th>
									<th>规格</th>
									<th>物料类别</th>
									<th>供料属性</th>
									<th>变量(正增负减)</th>
									<th>在制</th>
									<th>在途</th>
									<th>在库</th>
									<th>呆滞</th>
									<th>单位</th>
									<th>在制处理</th>
									<th>在途处理</th>
									<th>在库处理</th>
									<th>呆滞处理</th>
								</tr>
								<s:iterator value="bbAply.ptasbList" id="pageptasb" status="ptasbStatus">
										<tr>
										<td><s:property value="#pageptasb.markId"/></td>
										<td><s:property value="#pageptasb.proName"/></td>
										<td><s:property value="#pageptasb.banben"/></td>
										<td><s:property value="#pageptasb.procardStyle"/></td>
										<td><s:property value="#pageptasb.specification"/></td>
										<td><s:property value="#pageptasb.wgType"/></td>
										<td> <s:property value="#pageptasb.kgliao"/></td>
										<td align="right"><s:property value="#pageptasb.yongliao"/></td>
										<td align="right"><s:property value="#pageptasb.zaizhiCount"/></td>
										<td align="right"><s:property value="#pageptasb.zaituCount"/></td>
										<td align="right"><s:property value="#pageptasb.zaikuCount"/></td>
										<td align="right"><s:property value="#pageptasb.daizhiCount"/></td>
										<td> <s:property value="#pageptasb.unit"/></td>
										<td>
											<input type="hidden" name="ptasbclTypeList[<s:property value="#ptasbStatus.index"/>].ptasbId" value="<s:property value="#pageptasb.id"/>">
											<input type="hidden" name="ptasbclTypeList[<s:property value="#ptasbStatus.index"/>].ptbbjId" value="<s:property value="#jlPepole.id"/>">
											<input name="ptasbclTypeList[<s:property value="#ptasbStatus.index"/>].zaizhicltype">
										</td>
										<td>
											<input name="ptasbclTypeList[<s:property value="#ptasbStatus.index"/>].zaitucltype">
										</td>
										<td>
											<input name="ptasbclTypeList[<s:property value="#ptasbStatus.index"/>].zaikucltype">
										</td>
										<td>
											<input name="ptasbclTypeList[<s:property value="#ptasbStatus.index"/>].daizhicltype">
										</td>
										</tr>
								</s:iterator>
							</table>
							
							<br/>
							<input type="hidden" name="ptbbJudges.id" value="${jlPepole.id}">
							<input type="hidden" name="tag" value="2">
							<table class="table">
								<tr>
									<th>
										序号
									</th>
									<th>
										订单号
									</th>
									<th>
										总成件号
									</th>
									<th>
										业务件号
									</th>
									<th>
										总成批次
									</th>
									<th>
										总进度
									</th>
									<th>
										件号
									</th>
									<th>
										类型
									</th>
									<th>
										批次
									</th>
									<th>
										版本号
									</th>
									<th>
										零件数量
									</th>
									<th>
										工序号
									</th>
									<th>
										工序名称
									</th>
									<th>
										数量
									</th>
									<th>
										处理方案
										<SELECT onchange="alltcl(this)">
											<option value="请选择">
														请选择
													</option>
											<option value="顺延">
														顺延
													</option>
													<option value="更新(返修)">
														更新(返修)
													</option>
													<option value="更新(不返修)">
														更新(不返修)
													</option>
													<option value="报废重产">
														报废重产
													</option>
													<option value="待消耗">
														待消耗
													</option>
										</SELECT>
									</th>
									<th>
										备注
									</th>
								</tr>
								<tr>
									<td colspan="16" align="center" bgcolor="red">
										同总成相关生产
									</td>
								</tr>
								<s:iterator value="pabbList" id="pabb1" status="pastatu1">
									<tr>
										<td align="right"
											rowspan="<s:property value="#pabb1.processabbList.size()"/>">
											<s:property value="#pastatu1.index" />
										</td>
										<td align="left"
											rowspan="<s:property value="#pabb1.processabbList.size()"/>">
											${pabb1.orderNumber}
										</td>
										<td align="left"
											rowspan="<s:property value="#pabb1.processabbList.size()"/>">
											${pabb1.rootMarkId}
										</td>
										<td align="left"
											rowspan="<s:property value="#pabb1.processabbList.size()"/>">
											${pabb1.ywMarkId}
										</td>
										<td align="left"
											rowspan="<s:property value="#pabb1.processabbList.size()"/>">
											${pabb1.rootSelfCard}
										</td>
										<td align="left"
											rowspan="<s:property value="#pabb1.processabbList.size()"/>">
											${pabb1.totaljd}
										</td>
										<td align="left"
											rowspan="<s:property value="#pabb1.processabbList.size()"/>">
											<a href="ProcardAction!findProcardByRunCard2.action?id=${pabb1.procardId}&pageStatus=history&viewStatus=">${pabb1.markId}</a>
										</td>
										<td align="left"
											rowspan="<s:property value="#pabb1.processabbList.size()"/>">
											${pabb1.procardStyle}
										</td>
										<td align="left"
											rowspan="<s:property value="#pabb1.processabbList.size()"/>">
											${pabb1.selfCard}
										</td>
										<td align="left"
											rowspan="<s:property value="#pabb1.processabbList.size()"/>">
											${pabb1.banebenNumber}
										</td>
										<td align="left"
											rowspan="<s:property value="#pabb1.processabbList.size()"/>">
											${pabb1.scCount}
										</td>
										<s:iterator value="#pabb1.processabbList" id="processabb1"
											status="pabbStatus1">
											<s:if test="#pabbStatus1.index>0">
												<tr>
											</s:if>
											<td align="left">
												<s:property value="#processabb1.processNo" />
											</td>
											<td align="left">
												<s:property value="#processabb1.processName" />
											</td>
											<td align="left">
												<s:property value="#processabb1.scCount" />
											</td>
											<td align="left">
												<input type="hidden"
													name="pbbJudgeList[<s:property value="#jlIndex1" />].ptbbJudgeId"
													value="${jlPepole.id}" />
												<input type="hidden"
													name="pbbJudgeList[<s:property value="#jlIndex1" />].pabbJudgeId"
													value="${pabb1.id}" />
												<input type="hidden"
													name="pbbJudgeList[<s:property value="#jlIndex1" />].processbbJudgeId"
													value="${processabb1.id}" />
												<select id="clfa<s:property value="#jlIndex1" />"
													name="pbbJudgeList[<s:property value="#jlIndex1" />].remark"
													class="cl1<s:property value="#pabb1.rootSelfCard" /> alltcl">
													<s:if
														test="#processabb1.pbbjList!=null&&#processabb1.pbbjList.size()>0">
														<s:iterator value="#processabb1.pbbjList" id="pbbj1">
															<s:if test="#pbbj1.ptbbJudgeId==#jlPepole.id">
																<option>
																	${pbbj1.remark}
																</option>
															</s:if>
														</s:iterator>
													</s:if>
													<s:else>
														<option>
															请选择
														</option>
													</s:else>
													<option value="顺延">
														顺延
													</option>
													<option value="更新(返修)">
														更新(返修)
													</option>
													<option value="更新(不返修)">
														更新(不返修)
													</option>
													<option value="报废重产">
														报废重产
													</option>
													<option value="待消耗">
														待消耗
													</option>
												</select>
												<input type="button" value="同处理"
													onclick="tcl(<s:property value="#jlIndex1" />,'<s:property value="#pabb1.rootSelfCard" />')" />
											</td>
											<td align="left">
												<textarea id="nsbcremark1" rows="2" cols="30"
													name="pbbJudgeList[<s:property value="#jlIndex1" />].bcremark"><s:if
														test="#processabb1.pbbjList!=null&&#processabb1.pbbjList.size()>0"><s:iterator value="#processabb1.pbbjList" id="pbbj1"><s:if test="#pbbj1.ptbbJudgeId==#nsPepole.id">${pbbj1.bcremark}</s:if></s:iterator></s:if></textarea>
											</td>
									</tr>
									<s:set name="jlIndex1" value="#jlIndex1+1"></s:set>
								</s:iterator>
								</s:iterator>
								<tr>
									<td colspan="16" align="center" bgcolor="red">
										非同总成相关生产
									</td>
								</tr>
								<s:iterator value="pabbList2" id="pabb2" status="pastatu2">
									<tr>
										<td align="right"
											rowspan="<s:property value="#pabb2.processabbList.size()"/>">
											<s:property value="#pastatu2.index" />
										</td>
										<td align="left"
											rowspan="<s:property value="#pabb2.processabbList.size()"/>">
											${pabb2.orderNumber}
										</td>
										<td align="left"
											rowspan="<s:property value="#pabb2.processabbList.size()"/>">
											${pabb2.rootMarkId}
										</td>
										<td align="left"
											rowspan="<s:property value="#pabb2.processabbList.size()"/>">
											${pabb2.ywMarkId}
										</td>
										<td align="left"
											rowspan="<s:property value="#pabb2.processabbList.size()"/>">
											${pabb2.rootSelfCard}
										</td>
										<td align="left"
											rowspan="<s:property value="#pabb2.processabbList.size()"/>">
											${pabb2.totaljd}
										</td>
										<td align="left"
											rowspan="<s:property value="#pabb2.processabbList.size()"/>">
											<a href="ProcardAction!findProcardByRunCard2.action?id=${pabb2.procardId}&pageStatus=history&viewStatus=">${pabb2.markId}</a>
										</td>
										<td align="left"
											rowspan="<s:property value="#pabb2.processabbList.size()"/>">
											${pabb2.procardStyle}
										</td>
										<td align="left"
											rowspan="<s:property value="#pabb2.processabbList.size()"/>">
											${pabb2.selfCard}
										</td>
										<td align="left"
											rowspan="<s:property value="#pabb2.processabbList.size()"/>">
											${pabb2.banebenNumber}
										</td>
										<td align="left"
											rowspan="<s:property value="#pabb2.processabbList.size()"/>">
											${pabb2.scCount}
										</td>
										<s:iterator value="#pabb2.processabbList" id="processabb2"
											status="pabbStatus2">
											<s:if test="#pabbStatus2.index>0">
												<tr>
											</s:if>
											<td align="left">
												<s:property value="#processabb2.processNo" />
											</td>
											<td align="left">
												<s:property value="#processabb2.processName" />
											</td>
											<td align="left">
												<s:property value="#processabb2.scCount" />
											</td>
											<td align="left">
												<input type="hidden"
													name="pbbJudgeList2[<s:property value="#jlIndex2" />].ptbbJudgeId"
													value="${jlPepole.id}" />
												<input type="hidden"
													name="pbbJudgeList2[<s:property value="#jlIndex2" />].pabbJudgeId"
													value="${pabb2.id}" />
												<input type="hidden"
													name="pbbJudgeList2[<s:property value="#jlIndex2" />].processbbJudgeId"
													value="${processabb2.id}" />
												<select class="alltcl"
													name="pbbJudgeList2[<s:property value="#jlIndex2" />].remark">
													<s:if
														test="#processabb2.pbbjList!=null&&#processabb2.pbbjList.size()>0">
														<s:iterator value="#processabb2.pbbjList" id="pbbj2">
															<s:if test="#pbbj2.ptbbJudgeId==#jlPepole.id">
																<option>
																	${pbbj2.remark}
																</option>
															</s:if>
														</s:iterator>
													</s:if>
													<s:else>
														<option>
															请选择
														</option>
													</s:else>
													<option>
														顺延
													</option>
													<option>
														更新(返修)
													</option>
													<option>
														更新(不返修)
													</option>
													<option>
														报废重产
													</option>
													<option>
														待消耗
													</option>
												</select>
											</td>
											<td align="left">
												<textarea id="nsbcremark2" rows="2" cols="30"
													name="pbbJudgeList2[<s:property value="#jlIndex2" />].bcremark"><s:if
														test="#processabb2.pbbjList!=null&&#processabb2.pbbjList.size()>0"><s:iterator value="#processabb2.pbbjList" id="pbbj2"><s:if test="#pbbj2.ptbbJudgeId==#nsPepole.id">${pbbj2.bcremark}</s:if></s:iterator></s:if></textarea>
											</td>
									</tr>
									<s:set name="jlIndex2" value="#jlIndex2+1"></s:set>
								</s:iterator>
								</s:iterator>
								<tr>
									<td align="center" colspan="16">
										评论：
										<textarea rows="4" cols="80" name="remark"
											id="ljremark">${jlPepole.remark}</textarea>
										<font color="red">*</font>
									</td>
								</tr>
								<tr>
									<td align="center" colspan="16">
										<input id="jsSubBtn" type="button" onclick="submitgcsps()"
											value="确定" style="width: 80; height: 60px;">
										<input type="button" id="gcsdhbtn" onclick="backsbApply(${bbAply.id},'gcsdhbtn','ljremark')"
											value="打回" style="width: 80; height: 60px;">
									</td>
								</tr>
							</table>
						</form>
					</s:if>
				</s:iterator>
				</s:if>
				<table class="table">
					<tr>
						<th>
							部门
						</th>
						<th>
							类型
						</th>
						<th>
							工号
						</th>
						<th>
							评审人
						</th>
						<th>
							评语
						</th>
						<th>
							评论时间
						</th>
					</tr>
					<s:iterator value="bbAply.bbJudegeList" id="nsPepole">
						<s:if test="#nsPepole.judgeType=='内审'||#nsPepole.judgeType=='成本'">
							<tr>
								<td align="left">
									${nsPepole.dept}
								</td>
								<td align="left">
									${nsPepole.judgeType}
								</td>
								<td align="left">
									${nsPepole.userCode}
								</td>
								<td align="left">
									${nsPepole.userName}
								</td>
								<td align="left">
									${nsPepole.remark}
								</td>
								<td align="left">
									${nsPepole.plTime}
								</td>
							</tr>
						</s:if>
					</s:iterator>
				</table>
		</div>
		<div id="zpgbmdiv" style="display: none;background-color:#CAE1FF;" align="center">
				<h3>
				指派各部门
				</h3>
				<div align="left">
			&nbsp;&nbsp;&nbsp;&nbsp;必选评审组: <font color="red">
			<s:iterator value="pszbList" id="pszb">
				${pszb};
			</s:iterator>
			</font>
			<br/>
			&nbsp;&nbsp;&nbsp;&nbsp;建议评审组: <font color="red">
			<s:iterator value="pszbList2" id="pszb2">
				${pszb2};
			</s:iterator>
			</font>
			</div>
				<s:if test="bbAply.processStatus=='指派各部门'">
				<s:iterator value="bbAply.bbJudegeList" id="jlPepole">
					<s:if
						test="#jlPepole.judgeType=='工程师'&&#session.Users.id==#jlPepole.userId">
						<form id="zpgbmform" method="post">
						<input type="hidden" value="${bbAply.id}" name="bbAply.id">
							<table class="table" id="pszbtable">
								<tr id="pszbtr" bgcolor="yellow">
									<th align="center" colspan="3">指派评审组</th>
								</tr>
								<tr id="pszbtr">
									<th>组别</th><th>人员</th><th><input type="button" onclick="addpszbTr()" value="添加人员"/> </th>
								</tr>
							<tr id="pszbtr2">
									<td align="center" colspan="3">
										评论：
										<textarea rows="4" cols="80" name="remark"
											id="zpgremark">${jlPepole.remark}</textarea>
										<font color="red">*</font>
									</td>
								</tr>
								<tr>
									<td align="center" colspan="3">
							<input id="zpgbmSubBtn" type="button" onclick="submitzpgbm()"
											value="确定" style="width: 80; height: 60px;">
										<input type="button" id="gcsdhbtn" onclick="backsbApply(${bbAply.id},'zpgbmSubBtn','zpgremark')"
											value="打回" style="width: 80; height: 60px;">
											</tr>
							</table>
						</form>
					</s:if>
				</s:iterator>
				</s:if>
				<s:else>
				<table class="table">
					<tr>
						<th>
							部门
						</th>
						<th>
							类型
						</th>
						<th>
							工号
						</th>
						<th>
							评审人
						</th>
						<th>
							评语
						</th>
						<th>
							评论时间
						</th>
					</tr>
					<s:iterator value="bbAply.bbJudegeList" id="nsPepole">
						<s:if test="#nsPepole.judgeType=='成本'">
							<tr>
								<td align="left">
									${nsPepole.dept}
								</td>
								<td align="left">
									${nsPepole.judgeType}
								</td>
								<td align="left">
									${nsPepole.userCode}
								</td>
								<td align="left">
									${nsPepole.userName}
								</td>
								<td align="left">
									${nsPepole.remark}
								</td>
								<td align="left">
									${nsPepole.plTime}
								</td>
							</tr>
						</s:if>
					</s:iterator>
				</table>
			</s:else>
		</div>
		<div id="cbshdiv" style="display: none;background-color:#CAE1FF;" align="center">
			<h3>
				成本审核
			</h3>
			<s:if test="bbAply.processStatus=='成本审核'">
				<s:iterator value="bbAply.bbJudegeList" id="cpPepole">
					<s:if
						test="#cpPepole.judgeType=='成本'&&#session.Users.id==#cpPepole.userId">
						<form id="cbForm" method="post">
						<table class="table">
										<tr>
											<td>
												<input type="hidden" name="ptbbJudges.id"
													value="<s:property value="#cpPepole.id"/>">
												<s:property value="#cpPepole.dept" />
											</td>
											<td>
												<s:property value="#cpPepole.userName" />
											</td>
											<td>
												<textarea rows="4" cols="70" name="ptbbJudges.remark"
													id="cbpsremark"><s:property value="#cpPepole.remark" /></textarea>
												<font color="red">*</font>
											</td>
											<td>
												<input id="cbpssubmit" type="button" value="提交"
													onclick="submitcbsh()" style="width: 60px; height: 40px;">
													<input type="button" id="cbdhbtn" onclick="backsbApply(${bbAply.id},'cbdhbtn','cbpssubmit')"
												value="打回" style="width: 60px; height: 40px;">
											</td>
										</tr>
									</table>
						</form>
					</s:if>
				</s:iterator>
			</s:if>
			<s:else>
				<table class="table">
					<tr>
						<th>
							部门
						</th>
						<th>
							类型
						</th>
						<th>
							工号
						</th>
						<th>
							评审人
						</th>
						<th>
							评语
						</th>
						<th>
							评论时间
						</th>
					</tr>
					<s:iterator value="bbAply.bbJudegeList" id="nsPepole">
						<s:if test="#nsPepole.judgeType=='成本'">
							<tr>
								<td align="left">
									${nsPepole.dept}
								</td>
								<td align="left">
									${nsPepole.judgeType}
								</td>
								<td align="left">
									${nsPepole.userCode}
								</td>
								<td align="left">
									${nsPepole.userName}
								</td>
								<td align="left">
									${nsPepole.remark}
								</td>
								<td align="left">
									${nsPepole.plTime}
								</td>
							</tr>
						</s:if>
					</s:iterator>
				</table>
			</s:else>
			<br />
<%--			设变零件关联生产数据查看--%>
			<table class="table">
				<tr>
					<td colspan="15" align="center">
						生产件明细
					</td>
				</tr>
				<tr>
					<th>
						序号
					</th>
					<th>
						订单号
					</th>
					<th>
						总成件号
					</th>
					<th>
						业务件号
					</th>
					<th>
						总成批次
					</th>
					<th>
						总进度
					</th>
					<th>
						件号
					</th>
					<th>
						类型
					</th>
					<th>
						批次
					</th>
					<th>
						版本号
					</th>
					<th>
						零件数
					</th>
					<th>
						工序号
					</th>
					<th>
						工序名称
					</th>
					<th>
						数量
					</th>
					<th>
						评审/处理方案
					</th>
				</tr>
				<tr>
					<td colspan="15" align="center" bgcolor="red">
						同总成相关生产
					</td>
				</tr>
				<s:iterator value="pabbList" id="pabb1" status="pastatu1">
					<tr>
						<td align="right"
							rowspan="<s:property value="#pabb1.processabbList.size()"/>">
							<s:property value="#pastatu1.index" />
						</td>
						<td align="left"
							rowspan="<s:property value="#pabb1.processabbList.size()"/>">
							${pabb1.orderNumber}
						</td>
						<td align="left"
							rowspan="<s:property value="#pabb1.processabbList.size()"/>">
							${pabb1.rootMarkId}
						</td>
						<td align="left"
							rowspan="<s:property value="#pabb1.processabbList.size()"/>">
							${pabb1.ywMarkId}
						</td>
						<td align="left"
							rowspan="<s:property value="#pabb1.processabbList.size()"/>">
							${pabb1.rootSelfCard}
						</td>
						<td align="left"
							rowspan="<s:property value="#pabb1.processabbList.size()"/>">
							${pabb1.totaljd}
						</td>
						<td align="left"
							rowspan="<s:property value="#pabb1.processabbList.size()"/>">
							<a href="ProcardAction!findProcardByRunCard2.action?id=${pabb1.procardId}&pageStatus=history&viewStatus=">${pabb1.markId}</a>
						</td>
						<td align="left"
							rowspan="<s:property value="#pabb1.processabbList.size()"/>">
							${pabb1.procardStyle}
						</td>
						<td align="left"
							rowspan="<s:property value="#pabb1.processabbList.size()"/>">
							${pabb1.selfCard}
						</td>
						<td align="left"
							rowspan="<s:property value="#pabb1.processabbList.size()"/>">
							${pabb1.banebenNumber}
						</td>
						<td align="left"
							rowspan="<s:property value="#pabb1.processabbList.size()"/>">
							${pabb1.scCount}
						</td>
						<s:iterator value="#pabb1.processabbList" id="processabb1"
							status="pabbStatus1">
							<s:if test="#pabbStatus1.index>0">
								<tr>
							</s:if>
							<td align="left">
								<s:property value="#processabb1.processNo" />
							</td>
							<td align="left">
								<s:property value="#processabb1.processName" />
							</td>
							<td align="left">
								<s:property value="#processabb1.scCount" />
							</td>
							<td align="left">
								<s:if
									test="#processabb1.pbbjList!=null&&#processabb1.pbbjList.size()>0">
									<s:iterator value="#processabb1.pbbjList" id="pbbj1">
										 		${pbbj1.userName}-${pbbj1.remark}
										 		<s:if
											test="#pbbj1.bcremark!=null&&#pbbj1.bcremark.length()>0">
											<font color="red">(${pbbj1.bcremark})</font>
										</s:if>;
										 </s:iterator>
								</s:if>
							</td>
					</tr>
				</s:iterator>
				</s:iterator>
				<tr>
					<td colspan="15" align="center" bgcolor="red">
						非同总成相关生产
					</td>
				</tr>
				<s:iterator value="pabbList2" id="pabb2" status="pastatu2">
					<tr>
						<td align="right">
							<s:property value="#pastatu2.index" />
						</td>
						<td align="left"
							rowspan="<s:property value="#pabb1.processabbList.size()"/>">
							${pabb2.orderNumber}
						</td>
						<td align="left"
							rowspan="<s:property value="#pabb2.processabbList.size()"/>">
							${pabb2.rootMarkId}
						</td>
						<td align="left"
							rowspan="<s:property value="#pabb2.processabbList.size()"/>">
							${pabb2.ywMarkId}
						</td>
						<td align="left"
							rowspan="<s:property value="#pabb2.processabbList.size()"/>">
							${pabb2.rootSelfCard}
						</td>
						<td align="left"
							rowspan="<s:property value="#pabb2.processabbList.size()"/>">
							${pabb2.totaljd}
						</td>
						<td align="left"
							rowspan="<s:property value="#pabb2.processabbList.size()"/>">
							<a href="ProcardAction!findProcardByRunCard2.action?id=${pabb2.procardId}&pageStatus=history&viewStatus=">${pabb2.markId}</a>
						</td>
						<td align="left"
							rowspan="<s:property value="#pabb2.processabbList.size()"/>">
							${pabb21.procardStyle}
						</td>
						<td align="left"
							rowspan="<s:property value="#pabb2.processabbList.size()"/>">
							${pabb2.selfCard}
						</td>
						<td align="left"
							rowspan="<s:property value="#pabb2.processabbList.size()"/>">
							${pabb2.banebenNumber}
						</td>
						<td align="left"
							rowspan="<s:property value="#pabb2.processabbList.size()"/>">
							${pabb2.scCount}
						</td>
						<s:iterator value="#pabb2.processabbList" id="processabb2"
							status="pabbStatus2">
							<s:if test="#pabbStatus2.index>0">
								<tr>
							</s:if>
							<td align="left">
								<s:property value="#processabb2.processNo" />
							</td>
							<td align="left">
								<s:property value="#processabb2.processName" />
							</td>
							<td align="left">
								<s:property value="#processabb2.scCount" />
							</td>
							<td align="left">
								<s:if
									test="#processabb2.pbbjList!=null&&#processabb2.pbbjList.size()>0">
									<s:iterator value="#processabb2.pbbjList" id="pbbj2">
										 		${pbbj2.userName}-${pbbj2.remark}
										 		<s:if
											test="#pbbj2.bcremark!=null&&#pbbj2.bcremark.length()>0">
											<font color="red">(${pbbj2.bcremark})</font>
										</s:if>;
										 </s:iterator>
								</s:if>
							</td>
					</tr>
				</s:iterator>
				</s:iterator>
			</table>
			
		</div>
		<div align="center" id="bmpsdiv" style="display: none;background-color:#CAE1FF;">
			<br />
			<h3>
				内部评审
			</h3>
			<s:if test="bbAply.processStatus=='各部门评审'">
				<h3>
					指派时间：${bbAply.zpTime}&nbsp;&nbsp;&nbsp;
					<%--			最大评审时长${bbAply.zdpsTime}--%>
				</h3>
				<s:set name="nsIndex1" value="0"></s:set>
				<s:set name="nsIndex2" value="0"></s:set>
				<s:if
					test="(pabbList==null||pabbList.size()==0)&&(pabbList2==null||pabbList2.size()==0)">
					<s:iterator value="bbAply.bbJudegeList" id="nbpsPepole"
						status="cpStatus">
						<s:if test="#nbpsPepole.judgeType=='内审'">
							<s:if test="#session.Users.id==#nbpsPepole.userId">
								<form id="noprocardnsForm" method="post">
								<input type="hidden" name="bbAply.id" value="${bbAply.id}">
								<table  class="table" >
								<tr bgcolor="yellow">
									<th colspan="17" align="center">设变涉及零件处理方案</th>
								</tr>
								<tr >
									<th>件号</th>
									<th>名称</th>
									<th>版本</th>
									<th>类型</th>
									<th>规格</th>
									<th>物料类别</th>
									<th>供料属性</th>
									<th>变量(正增负减)</th>
									<th>在制</th>
									<th>在途</th>
									<th>在库</th>
									<th>呆滞</th>
									<th>单位</th>
									<th>在制处理</th>
									<th>在途处理</th>
									<th>在库处理</th>
									<th>呆滞处理</th>
								</tr>
								<s:iterator value="bbAply.ptasbList" id="pageptasb" status="ptasbStatus">
										<tr>
										<td><s:property value="#pageptasb.markId"/></td>
										<td><s:property value="#pageptasb.proName"/></td>
										<td><s:property value="#pageptasb.banben"/></td>
										<td><s:property value="#pageptasb.procardStyle"/></td>
										<td><s:property value="#pageptasb.specification"/></td>
										<td><s:property value="#pageptasb.wgType"/></td>
										<td> <s:property value="#pageptasb.kgliao"/></td>
										<td align="right"><s:property value="#pageptasb.yongliao"/></td>
										<td align="right"><s:property value="#pageptasb.zaizhiCount"/></td>
										<td align="right"><s:property value="#pageptasb.zaituCount"/></td>
										<td align="right"><s:property value="#pageptasb.zaikuCount"/></td>
										<td align="right"><s:property value="#pageptasb.daizhiCount"/></td>
										<td> <s:property value="#pageptasb.unit"/></td>
										<td>
											<input type="hidden" name="ptasbclTypeList[<s:property value="#ptasbStatus.index"/>].ptasbId" value="<s:property value="#pageptasb.id"/>">
											<input type="hidden" name="ptasbclTypeList[<s:property value="#ptasbStatus.index"/>].ptbbjId" value="<s:property value="#jlPepole.id"/>">
											<input name="ptasbclTypeList[<s:property value="#ptasbStatus.index"/>].zaizhicltype">
										</td>
										<td>
											<input name="ptasbclTypeList[<s:property value="#ptasbStatus.index"/>].zaitucltype">
										</td>
										<td>
											<input name="ptasbclTypeList[<s:property value="#ptasbStatus.index"/>].zaikucltype">
										</td>
										<td>
											<input name="ptasbclTypeList[<s:property value="#ptasbStatus.index"/>].daizhicltype">
										</td>
										</tr>
								</s:iterator>
							</table>
									<table class="table">
										<tr>
											<td>
												<input type="hidden" name="ptbbJudges.id"
													value="<s:property value="#nbpsPepole.id"/>">
												<s:property value="#nbpsPepole.dept" />
											</td>
											<td>
												<s:property value="#nbpsPepole.userName" />
											</td>
											<td>
												<textarea rows="4" cols="70" name="ptbbJudges.remark"
													id="nbpsremark"><s:property value="#nbpsPepole.remark" /></textarea>
												<font color="red">*</font>
											</td>
											<td>
												<input id="surechangebtn" type="button" value="工作移交"
											onclick="transferWork(${bbAply.id})">
												<input id="nbpssubmit" type="button" value="提交"
													onclick="submitnoprocardns()" style="width: 60px; height: 40px;">
													<input type="button" id="noprocardnsbtn" onclick="backsbApply(${bbAply.id},'noprocardnsbtn','nbpsremark')"
												value="打回" style="width: 60px; height: 40px;">
											</td>
										</tr>
									</table>
								</form>
							</s:if>
						</s:if>
						<%--				<s:if test="#nbpsPepole.judgeType=='工程师'&&#session.Users.id==#nbpsPepole.userId&&bbAply.qznext=='yes'">--%>
						<%--					<input id="passnsBtn" type="button" value="强制跳过" onclick="passns(${bbAply.id})"/>--%>
						<%--				</s:if>--%>
					</s:iterator>
				</s:if>
				<s:else>
					<s:iterator value="bbAply.bbJudegeList" id="nsPepole">
						<s:if
							test="#nsPepole.judgeType=='内审'&&#session.Users.id==#nsPepole.userId">
							<form id="npform" method="post">
								<input type="hidden" name="ptbbJudges.id" value="${nsPepole.id}">
								<input type="hidden" name="bbAply.id" value="${bbAply.id}">
								<table  class="table" >
								<tr bgcolor="yellow">
									<th colspan="17" align="center">设变涉及零件处理方案</th>
								</tr>
								<tr >
									<th>件号</th>
									<th>名称</th>
									<th>版本</th>
									<th>类型</th>
									<th>规格</th>
									<th>物料类别</th>
									<th>供料属性</th>
									<th>变量(正增负减)</th>
									<th>在制</th>
									<th>在途</th>
									<th>在库</th>
									<th>呆滞</th>
									<th>单位</th>
									<th>在制处理</th>
									<th>在途处理</th>
									<th>在库处理</th>
									<th>呆滞处理</th>
								</tr>
								<s:iterator value="bbAply.ptasbList" id="pageptasb" status="ptasbStatus">
										<tr>
										<td><s:property value="#pageptasb.markId"/></td>
										<td><s:property value="#pageptasb.proName"/></td>
										<td><s:property value="#pageptasb.banben"/></td>
										<td><s:property value="#pageptasb.procardStyle"/></td>
										<td><s:property value="#pageptasb.specification"/></td>
										<td><s:property value="#pageptasb.wgType"/></td>
										<td> <s:property value="#pageptasb.kgliao"/></td>
										<td align="right"><s:property value="#pageptasb.yongliao"/></td>
										<td align="right"><s:property value="#pageptasb.zaizhiCount"/></td>
										<td align="right"><s:property value="#pageptasb.zaituCount"/></td>
										<td align="right"><s:property value="#pageptasb.zaikuCount"/></td>
										<td align="right"><s:property value="#pageptasb.daizhiCount"/></td>
										<td> <s:property value="#pageptasb.unit"/></td>
										<td>
											<input type="hidden" name="ptasbclTypeList[<s:property value="#ptasbStatus.index"/>].ptasbId" value="<s:property value="#pageptasb.id"/>">
											<input type="hidden" name="ptasbclTypeList[<s:property value="#ptasbStatus.index"/>].ptbbjId" value="<s:property value="#jlPepole.id"/>">
											<input name="ptasbclTypeList[<s:property value="#ptasbStatus.index"/>].zaizhicltype">
										</td>
										<td>
											<input name="ptasbclTypeList[<s:property value="#ptasbStatus.index"/>].zaitucltype">
										</td>
										<td>
											<input name="ptasbclTypeList[<s:property value="#ptasbStatus.index"/>].zaikucltype">
										</td>
										<td>
											<input name="ptasbclTypeList[<s:property value="#ptasbStatus.index"/>].daizhicltype">
										</td>
										</tr>
								</s:iterator>
							</table>
								<table class="table">
									<tr>
										<th>
											序号
										</th>
										<th>
											订单号
										</th>
										<th>
											总成件号
										</th>
										<th>
											业务件号
										</th>
										<th>
											总成批次
										</th>
										<th>
											总进度
										</th>
										<th>
											件号
										</th>
										<th>
											类型
										</th>
										<th>
											批次
										</th>
										<th>
											版本号
										</th>
										<th>
											零件数量
										</th>
										<th>
											工序号
										</th>
										<th>
											工序名称
										</th>
										<th>
											数量
										</th>
										<th>
											处理方案
										</th>
										<th>
											备注
										</th>
									</tr>
									<tr>
										<td colspan="16" align="center" bgcolor="red">
											同总成相关生产
										</td>
									</tr>
									<s:iterator value="pabbList" id="pabb1" status="pastatu1">
										<tr>
											<td align="right"
												rowspan="<s:property value="#pabb1.processabbList.size()"/>">
												<s:property value="#pastatu1.index" />
											</td>
											<td align="left"
												rowspan="<s:property value="#pabb1.processabbList.size()"/>">
												${pabb1.orderNumber}
											</td>
											<td align="left"
												rowspan="<s:property value="#pabb1.processabbList.size()"/>">
												${pabb1.rootMarkId}
											</td>
											<td align="left"
												rowspan="<s:property value="#pabb1.processabbList.size()"/>">
												${pabb1.ywMarkId}
											</td>
											<td align="left"
												rowspan="<s:property value="#pabb1.processabbList.size()"/>">
												${pabb1.rootSelfCard}
											</td>
											<td align="left"
												rowspan="<s:property value="#pabb1.processabbList.size()"/>">
												${pabb1.totaljd}
											</td>
											<td align="left"
												rowspan="<s:property value="#pabb1.processabbList.size()"/>">
												<a href="ProcardAction!findProcardByRunCard2.action?id=${pabb1.procardId}&pageStatus=history&viewStatus=">${pabb1.markId}</a>
											</td>
											<td align="left"
												rowspan="<s:property value="#pabb1.processabbList.size()"/>">
												${pabb1.procardStyle}
											</td>
											<td align="left"
												rowspan="<s:property value="#pabb1.processabbList.size()"/>">
												${pabb1.selfCard}
											</td>
											<td align="left"
												rowspan="<s:property value="#pabb1.processabbList.size()"/>">
												${pabb1.banebenNumber}
											</td>
											<td align="left"
												rowspan="<s:property value="#pabb1.processabbList.size()"/>">
												${pabb1.scCount}
											</td>
											<s:iterator value="#pabb1.processabbList" id="processabb1"
												status="pabbStatus1">
												<s:if test="#pabbStatus1.index>0">
													<tr>
												</s:if>
												<td align="left">
													<s:property value="#processabb1.processNo" />
												</td>
												<td align="left">
													<s:property value="#processabb1.processName" />
												</td>
												<td align="left">
													<s:property value="#processabb1.scCount" />
												</td>
												<td align="left">
													<input type="hidden"
														name="pbbJudgeList[<s:property value="#nsIndex1" />].ptbbJudgeId"
														value="${nsPepole.id}" />
													<input type="hidden"
														name="pbbJudgeList[<s:property value="#nsIndex1" />].pabbJudgeId"
														value="${pabb1.id}" />
													<input type="hidden"
														name="pbbJudgeList[<s:property value="#nsIndex1" />].processbbJudgeId"
														value="${processabb1.id}" />
													<select id="clfa<s:property value="#nsIndex1" />"
														name="pbbJudgeList[<s:property value="#nsIndex1" />].remark"
														class="cl1<s:property value="#pabb1.rootSelfCard" /> alltcl">
														<s:if
															test="#processabb1.pbbjList!=null&&#processabb1.pbbjList.size()>0">
															<s:iterator value="#processabb1.pbbjList" id="pbbj1">
																<s:if test="#pbbj1.ptbbJudgeId==#nsPepole.id">
																	<option>
																		${pbbj1.remark}
																	</option>
																</s:if>
															</s:iterator>
														</s:if>
														<s:else>
															<option>
																请选择
															</option>
														</s:else>
														<option value="顺延">
															顺延
														</option>
														<option value="更新(返修)">
															更新(返修)
														</option>
														<option value="更新(不返修)">
															更新(不返修)
														</option>
														<option value="报废重产">
															报废重产
														</option>
														<option value="待消耗">
															待消耗
														</option>
													</select>
													<input type="button" value="同处理"
														onclick="tcl(<s:property value="#nsIndex1" />,'<s:property value="#pabb1.rootSelfCard" />')" />
												</td>
												<td align="left">
													<textarea id="nsbcremark1" rows="2" cols="30"
														name="pbbJudgeList[<s:property value="#nsIndex1" />].bcremark"><s:if
															test="#processabb1.pbbjList!=null&&#processabb1.pbbjList.size()>0"><s:iterator value="#processabb1.pbbjList" id="pbbj1"><s:if test="#pbbj1.ptbbJudgeId==#nsPepole.id">${pbbj1.bcremark}</s:if></s:iterator>
														</s:if>
													</textarea>
												</td>
										</tr>
										<s:set name="nsIndex1" value="#nsIndex1+1"></s:set>
									</s:iterator>
									</s:iterator>
									<tr>
										<td colspan="16" align="center" bgcolor="red">
											非同总成相关生产
										</td>
									</tr>
									<s:iterator value="pabbList2" id="pabb2" status="pastatu2">
										<tr>
											<td align="right"
												rowspan="<s:property value="#pabb2.processabbList.size()"/>">
												<s:property value="#pastatu2.index" />
											</td>
											<td align="left"
												rowspan="<s:property value="#pabb2.processabbList.size()"/>">
												${pabb2.orderNumber}
											</td>
											<td align="left"
												rowspan="<s:property value="#pabb2.processabbList.size()"/>">
												${pabb2.rootMarkId}
											</td>
											<td align="left"
												rowspan="<s:property value="#pabb2.processabbList.size()"/>">
												${pabb2.ywMarkId}
											</td>
											<td align="left"
												rowspan="<s:property value="#pabb2.processabbList.size()"/>">
												${pabb2.rootSelfCard}
											</td>
											<td align="left"
												rowspan="<s:property value="#pabb2.processabbList.size()"/>">
												${pabb2.totaljd}
											</td>
											<td align="left"
												rowspan="<s:property value="#pabb2.processabbList.size()"/>">
												<a href="ProcardAction!findProcardByRunCard2.action?id=${pabb2.procardId}&pageStatus=history&viewStatus=">${pabb2.markId}</a>
											</td>
											<td align="left"
												rowspan="<s:property value="#pabb2.processabbList.size()"/>">
												${pabb2.procardStyle}
											</td>
											<td align="left"
												rowspan="<s:property value="#pabb2.processabbList.size()"/>">
												${pabb2.selfCard}
											</td>
											<td align="left"
												rowspan="<s:property value="#pabb2.processabbList.size()"/>">
												${pabb2.banebenNumber}
											</td>
											<td align="left"
												rowspan="<s:property value="#pabb2.processabbList.size()"/>">
												${pabb2.scCount}
											</td>
											<s:iterator value="#pabb2.processabbList" id="processabb2"
												status="pabbStatus2">
												<s:if test="#pabbStatus2.index>0">
													<tr>
												</s:if>
												<td align="left">
													-
													<s:property value="#processabb2.processNo" />
												</td>
												<td align="left">
													<s:property value="#processabb2.processName" />
												</td>
												<td align="left">
													<s:property value="#processabb2.scCount" />
												</td>
												<td align="left">
													<input type="hidden"
														name="pbbJudgeList2[<s:property value="#nsIndex2" />].ptbbJudgeId"
														value="${nsPepole.id}" />
													<input type="hidden"
														name="pbbJudgeList2[<s:property value="#nsIndex2" />].pabbJudgeId"
														value="${pabb2.id}" />
													<input type="hidden"
														name="pbbJudgeList2[<s:property value="#nsIndex2" />].processbbJudgeId"
														value="${processabb2.id}" />
													<select class="alltcl"
														name="pbbJudgeList2[<s:property value="#nsIndex2" />].remark">
														<s:if
															test="#processabb2.pbbjList!=null&&#processabb2.pbbjList.size()>0">
															<s:iterator value="#processabb2.pbbjList" id="pbbj2">
																<s:if test="#pbbj2.ptbbJudgeId==#nsPepole.id">
																	<option>
																		${pbbj2.remark}
																	</option>
																</s:if>
															</s:iterator>
														</s:if>
														<s:else>
															<option>
																请选择
															</option>
														</s:else>
														<option>
															顺延
														</option>
														<option>
															更新(返修)
														</option>
														<option>
															更新(不返修)
														</option>
														<option>
															报废重产
														</option>
														<option>
															待消耗
														</option>
													</select>
												</td>
												<td align="left">
													<textarea id="nsbcremark2" rows="2" cols="30"
														name="pbbJudgeList2[<s:property value="#nsIndex2" />].bcremark"><s:if
															test="#processabb2.pbbjList!=null&&#processabb2.pbbjList.size()>0"><s:iterator value="#processabb2.pbbjList" id="pbbj2"><s:if test="#pbbj2.ptbbJudgeId==#nsPepole.id">${pbbj2.bcremark}</s:if></s:iterator></s:if></textarea>
												</td>
										</tr>
										<s:set name="nsIndex2" value="#nsIndex2+1"></s:set>
									</s:iterator>
									</s:iterator>
									<tr>
										<td align="center" colspan="16">
											评论：
											<textarea id="nsremark" rows="4" cols="80"
												name="ptbbJudges.remark">${nsPepole.remark}</textarea>
											<font color="red">*</font>
										</td>
									</tr>
									<tr>
										<td align="center" colspan="16">
										<input id="surechangebtn" type="button" value="工作移交" onclick="transferWork(${bbAply.id})" >
											<input id="nsSubBtn" type="button" onclick="submitnp(1)"
												value="确定" style="width: 80; height: 60px;">
											<input type="button" id="nsbtn"  onclick="backsbApply(${bbAply.id},'nsbtn','nsremark')"
												value="打回" style="width: 60px; height: 40px;">
										</td>
									</tr>
								</table>
							</form>
						</s:if>
						<%--								<s:if test="#nsPepole.judgeType=='工程师'&&#session.Users.id==#nsPepole.userId&&bbAply.qznext=='yes'">--%>
						<%--								<input id="passnsBtn" type="button" value="强制跳过" onclick="passns(${bbAply.id})"/>--%>
						<%--								</s:if>--%>
					</s:iterator>

				</s:else>
				<br />
				<table class="table">
					<tr>
						<th>
							部门
						</th>
						<th>
							类型
						</th>
						<th>
							工号
						</th>
						<th>
							评审人
						</th>
						<th>
							评语
						</th>
						<th>
							评论时间
						</th>
					</tr>
					<s:iterator value="bbAply.bbJudegeList" id="nsPepole">
						<s:if test="#nsPepole.judgeType=='内审'">
							<tr>
								<td align="left">
									${nsPepole.dept}
								</td>
								<td align="left">
									${nsPepole.judgeType}
								</td>
								<td align="left">
									${nsPepole.userCode}
								</td>
								<td align="left">
									${nsPepole.userName}
								</td>
								<td align="left">
									${nsPepole.remark}
								</td>
								<td align="left">
									${nsPepole.plTime}
								</td>
							</tr>
						</s:if>
					</s:iterator>
				</table>
			</s:if>
			<br />
					<table class="table">
						<tr>
							<td colspan="16" align="center">
								生产件明细
							</td>
						</tr>
						<tr>
							<th>
								序号
							</th>
							<th>
								订单号
							</th>
							<th>
								总成件号
							</th>
							<th>
								业务件号
							</th>
							<th>
								总成批次
							</th>
							<th>
								总进度
							</th>
							<th>
								件号
							</th>
							<th>
								类型
							</th>
							<th>
								批次
							</th>
							<th>
								版本号
							</th>
							<th>
								零件数
							</th>
							<th>
								工序号
							</th>
							<th>
								工序名称
							</th>
							<th>
								数量
							</th>
							<th>
								评审/处理方案
							</th>
						</tr>
						<tr>
							<td colspan="16" align="center" bgcolor="red">
								同总成相关生产
							</td>
<%--							<td><textarea rows="4" cols="70" name="ptbbJudges.remark" id="nbpsremark"><s:property value="#nbpsPepole.remark"/></textarea><font color="red">*</font>--%>
<%--							</td>--%>
<%--							<td><input id="nbpssubmit" type="button" value="提交" onclick="submitnoprocardns()">--%>
<%--								<input type="button" onclick="backsbApply(${bbAply.id})" value="打回" style="width: 60px;height: 40px;">--%>
<%--							</td>--%>
						</tr>
						<s:iterator value="pabbList" id="pabb1" status="pastatu1">
							<tr>
								<td align="right"
									rowspan="<s:property value="#pabb1.processabbList.size()"/>">
									<s:property value="#pastatu1.index" />
								</td>
								<td align="left"
									rowspan="<s:property value="#pabb1.processabbList.size()"/>">
									${pabb1.orderNumber}
								</td>
								<td align="left"
									rowspan="<s:property value="#pabb1.processabbList.size()"/>">
									${pabb1.rootMarkId}
								</td>
								<td align="left"
									rowspan="<s:property value="#pabb1.processabbList.size()"/>">
									${pabb1.ywMarkId}
								</td>
								<td align="left"
									rowspan="<s:property value="#pabb1.processabbList.size()"/>">
									${pabb1.rootSelfCard}
								</td>
								<td align="left"
									rowspan="<s:property value="#pabb1.processabbList.size()"/>">
									${pabb1.totaljd}
								</td>
								<td align="left"
									rowspan="<s:property value="#pabb1.processabbList.size()"/>">
									<a href="ProcardAction!findProcardByRunCard2.action?id=${pabb1.procardId}&pageStatus=history&viewStatus=">${pabb1.markId}</a>
								</td>
								<td align="left"
									rowspan="<s:property value="#pabb1.processabbList.size()"/>">
									${pabb1.procardStyle}
								</td>
								<td align="left"
									rowspan="<s:property value="#pabb1.processabbList.size()"/>">
									${pabb1.selfCard}
								</td>
								<td align="left"
									rowspan="<s:property value="#pabb1.processabbList.size()"/>">
									${pabb1.banebenNumber}
								</td>
								<td align="left"
									rowspan="<s:property value="#pabb1.processabbList.size()"/>">
									${pabb1.scCount}
								</td>
								<s:iterator value="#pabb1.processabbList" id="processabb1"
									status="pabbStatus1">
									<s:if test="#pabbStatus1.index>0">
										<tr>
									</s:if>
									<td align="left">
										<s:property value="#processabb1.processNo" />
									</td>
									<td align="left">
										<s:property value="#processabb1.processName" />
									</td>
									<td align="left">
										<s:property value="#processabb1.scCount" />
									</td>
									<td align="left">
										<s:if
											test="#processabb1.pbbjList!=null&&#processabb1.pbbjList.size()>0">
											<s:iterator value="#processabb1.pbbjList" id="pbbj1">
										 		${pbbj1.userName}-${pbbj1.remark}
										 		<s:if
													test="#pbbj1.bcremark!=null&&#pbbj1.bcremark.length()>0">
													<font color="red">(${pbbj1.bcremark})</font>
												</s:if>;
										 </s:iterator>
										</s:if>
									</td>
							</tr>
						</s:iterator>
						</s:iterator>
						<tr>
							<td colspan="16" align="center" bgcolor="red">
								非同总成相关生产
							</td>
						</tr>
						<s:iterator value="pabbList2" id="pabb2" status="pastatu2">
							<tr>
								<td align="right">
									<s:property value="#pastatu2.index" />
								</td>
								<td align="left"
									rowspan="<s:property value="#pabb2.processabbList.size()"/>">
									${pabb2.orderNumber}
								</td>
								<td align="left"
									rowspan="<s:property value="#pabb2.processabbList.size()"/>">
									${pabb2.rootMarkId}
								</td>
								<td align="left"
									rowspan="<s:property value="#pabb2.processabbList.size()"/>">
									${pabb2.ywMarkId}
								</td>
								<td align="left"
									rowspan="<s:property value="#pabb2.processabbList.size()"/>">
									${pabb2.rootSelfCard}
								</td>
								<td align="left"
									rowspan="<s:property value="#pabb2.processabbList.size()"/>">
									${pabb2.totaljd}
								</td>
								<td align="left"
									rowspan="<s:property value="#pabb2.processabbList.size()"/>">
									<a href="ProcardAction!findProcardByRunCard2.action?id=${pabb2.procardId}&pageStatus=history&viewStatus=">${pabb2.markId}</a>
								</td>
								<td align="left"
									rowspan="<s:property value="#pabb2.processabbList.size()"/>">
									${pabb21.procardStyle}
								</td>
								<td align="left"
									rowspan="<s:property value="#pabb2.processabbList.size()"/>">
									${pabb2.selfCard}
								</td>
								<td align="left"
									rowspan="<s:property value="#pabb2.processabbList.size()"/>">
									${pabb2.banebenNumber}
								</td>
								<td align="left"
									rowspan="<s:property value="#pabb2.processabbList.size()"/>">
									${pabb2.scCount}
								</td>
								<s:iterator value="#pabb2.processabbList" id="processabb2"
									status="pabbStatus2">
									<s:if test="#pabbStatus2.index>0">
										<tr>
									</s:if>
									<td align="left">
										<s:property value="#processabb2.processNo" />
									</td>
									<td align="left">
										<s:property value="#processabb2.processName" />
									</td>
									<td align="left">
										<s:property value="#processabb2.scCount" />
									</td>
									<td align="left">
										<s:if
											test="#processabb2.pbbjList!=null&&#processabb2.pbbjList.size()>0">
											<s:iterator value="#processabb2.pbbjList" id="pbbj2">
										 		${pbbj2.userName}-${pbbj2.remark}
										 		<s:if
													test="#pbbj2.bcremark!=null&&#pbbj2.bcremark.length()>0">
													<font color="red">(${pbbj2.bcremark})</font>
												</s:if>;
										 </s:iterator>
										</s:if>
									</td>
							</tr>
						</s:iterator>
						</s:iterator>
					</table>
		</div>
		<div id="zlgxdiv" style="display: none;background-color:#CAE1FF;" align="center">
			<s:if test="bbAply.processStatus=='资料更新'">
				<s:iterator value="bbAply.bbJudegeList" id="gcsPepole">
					<s:if
						test="#gcsPepole.judgeType=='工程师'&&#session.Users.id==#gcsPepole.userId">
						<form id="suresbForm" method="post">
							<input type="hidden" name="id" value="${bbAply.id}" />
							<input type="hidden" id="ptbbJudgesid" name="ptbbJudges.id"
								value="<s:property value="#gcsPepole.id"/>">
							<table class="table">
								<tr>
									<td colspan="5" align="center">
										通知人员选择
									</td>
								</tr>
								<tr>
									<td align="center">
										编制通知人员
									</td>
									<s:if test="bzjdCount=='2'.toString()">
										<td id="cprytd" align="center" colspan="1">
									</s:if>
									<s:elseif test="bzjdCount=='3'.toString()">
										<td id="cprytd" align="center" colspan="2">
									</s:elseif>
									<s:else>
										<td id="cprytd" align="center" colspan="3">
									</s:else>
									<input type="hidden" id="tzUserId"
										name="ptbbJudges.selectUserId" value="">
									<input type="hidden" id="tzUsername"
										name="ptbbJudges.selectUsers" value="">
									<label id="selectUsers1"></label>
									</td>
									<td align="center">
										<input id="selectpjBtn" type="button" value="选择"
											onclick="selectnsPeople(<s:property value="#gcsPepole.id"/>,'tzUserId','tzUsername','selectUsers1')">
									</td>
									</td>
								</tr>
								<tr>
									<td align="center">
										编制完成通知人员
									</td>
									<s:if test="bzjdCount=='2'.toString()">
										<td id="cprytd" align="center" colspan="1">
									</s:if>
									<s:elseif test="bzjdCount=='3'.toString()">
										<td id="cprytd" align="center" colspan="2">
									</s:elseif>
									<s:else>
										<td id="cprytd" align="center" colspan="3">
									</s:else>
									<input type="hidden" id="tzUserId2"
										name="ptbbJudges.selectUserId2" value="">
									<input type="hidden" id="tzUsername2"
										name="ptbbJudges.selectUsers2" value="">
									<label id="selectUsers2"></label>
									</td>
									<td align="center">
										<input id="selectpjBtn" type="button" value="选择"
											onclick="selectnsPeople(<s:property value="#gcsPepole.id"/>,'tzUserId2','tzUsername2','selectUsers2')">
									</td>
									</td>
								</tr>
								<tr>
									<td align="center">
										佐证人员
									</td>
									<s:if test="bzjdCount=='2'.toString()">
										<td id="cprytd" align="center" colspan="1">
									</s:if>
									<s:elseif test="bzjdCount=='3'.toString()">
										<td id="cprytd" align="center" colspan="2">
									</s:elseif>
									<s:else>
										<td id="cprytd" align="center" colspan="3">
									</s:else>
									<input type="hidden" id="tzUserId3"
										name="ptbbJudges.selectUserId3" value="">
									<input type="hidden" id="tzUsername3"
										name="ptbbJudges.selectUsers3" value="">
									<label id="selectUsers3"></label>
									</td>
									<td align="center">
										<input id="selectpjBtn" type="button" value="选择"
											onclick="selectnsPeople(<s:property value="#gcsPepole.id"/>,'tzUserId3','tzUsername3','selectUsers3')">
									</td>
									</td>
								</tr>
								<tr>
									<td align="center">
										件号
									</td>
									<td align="center">
										编制人
									</td>
									<s:if test="bzjdCount!='2'.toString()">
										<td align="center">
											校对人
										</td>
									</s:if>
									<s:if
										test="bzjdCount!='2'.toString()&&bzjdCount!='3'.toString()">
										<td align="center">
											审核人
										</td>
									</s:if>
									<td align="center">
										批准人
									</td>
								</tr>
								<tr>
									<td align="center">
										统一选择
									</td>
									<td align="center">
										<select class="bianzhi" id='bianzhiTem'
											onchange="changepeople('bz')">
											<option></option>
										</select>
									</td>
									<s:if test="bzjdCount!='2'.toString()">
										<td align="center">
											<select class="jiaodui" id='jiaoduiTem'
												onchange="changepeople('jd')">
												<option></option>
											</select>
										</td>
									</s:if>
									<s:if
										test="bzjdCount!='2'.toString()&&bzjdCount!='3'.toString()">
										<td align="center">
											<select class="shenhe" id='shenheTem'
												onchange="changepeople('sh')">
												<option></option>
											</select>
										</td>
									</s:if>
									<td align="center">
										<select class="pizhun" id='pizhunTem'
											onchange="changepeople('pz')">
											<option></option>
										</select>
									</td>
								</tr>
								<s:iterator value="bbAply.bbList" var="bb2" status="zpStatus">
									<tr>
										<td align="center">
											<input type="hidden"
												name="ptbbList[<s:property value="#zpStatus.index"/>].id"
												value="${bb2.id}">
											${bb2.markId}
										</td>
										<td align="center">
											<select
												name="ptbbList[<s:property value="#zpStatus.index"/>].bianzhiId"
												class="bianzhi"
												id='bianzhi<s:property value="#pageStatus.index"/>'>
												<option value="${Users.id}">
													${Users.name}
												</option>
											</select>
										</td>
										<s:if test="bzjdCount!='2'.toString()">
											<td align="center">
												<select
													name="ptbbList[<s:property value="#zpStatus.index"/>].jiaoduiId"
													class="jiaodui"
													id='jiaodui<s:property value="#pageStatus.index"/>'>
													<s:if test="#pgetpt.jiaoduiId!=null">
														<option value="${pgetpt.jiaoduiId}">
															${pgetpt.jiaoduiName}
														</option>
													</s:if>
													<option></option>
												</select>
											</td>
										</s:if>
										<s:if
											test="bzjdCount!='2'.toString()&&bzjdCount!='3'.toString()">
											<td align="center">
												<select
													name="ptbbList[<s:property value="#zpStatus.index"/>].shenheId"
													class="shenhe"
													id='shenhe<s:property value="#pageStatus.index"/>'>
													<s:if test="#pgetpt.shenheId!=null">
														<option value="${pgetpt.shenheId}">
															${pgetpt.shenheName}
														</option>
													</s:if>
													<option></option>
												</select>
											</td>
										</s:if>
										<td align="center">
											<select
												name="ptbbList[<s:property value="#zpStatus.index"/>].pizhunId"
												class="pizhun"
												id='pizhun<s:property value="#pageStatus.index"/>'>
												<s:if test="#pgetpt.pizhunId!=null">
													<option value="${pgetpt.pizhunId}">
														${pgetpt.pizhunName}
													</option>
												</s:if>
												<option></option>
											</select>
										</td>
									</tr>
								</s:iterator>
								<tr>
									<s:if test="bzjdCount=='2'.toString()">
										<td colspan="3" align="center">
									</s:if>
									<s:elseif test="bzjdCount=='3'.toString()">
										<td colspan="4" align="center">
									</s:elseif>
									<s:else>
										<td colspan="5" align="center">
									</s:else>
									<input id="suresbBtn" type="button"
										style="width: 80px; height: 60px;" value="确认设变"
										onclick="suresb()">
									</td>
								</tr>
							</table>
						</form>
					</s:if>
				</s:iterator>
			</s:if>
			<div>
				<h3>
					更改记录
				</h3>
				<table class="table">
					<tr>
						<td align="center" colspan="7">
							<b>本身资料更改</b>
						</td>
					</tr>
					<tr>
						<td align="center">
							件号
						</td>
						<td align="center">
							属性
						</td>
						<td align="center">
							原值
						</td>
						<td align="center">
							新值
						</td>
						<td align="center">
							操作类型
						</td>
						<td align="center">
							操作人
						</td>
						<td align="center">
							操作时间
						</td>
					</tr>
					<s:if test="ptchangeLogList!=null&&ptchangeLogList.size()>0">
						<s:iterator value="ptchangeLogList" id="pageptchangeLog"
							status="logstatus">
							<s:if test="#pageptchangeLog.entityType=='本身'">
								<s:iterator value="#pageptchangeLog.changeLogDetailSet"
									id="pgetdeatil">
									<tr>
										<td>
											<s:property value="#pageptchangeLog.sbMarkId" />
										</td>
										<td>
											<s:property value="#pgetdeatil.sxName" />
										</td>
										<td>
											<s:property value="#pgetdeatil.oldValue" />
										</td>
										<td>
											<s:property value="#pgetdeatil.newValue" />
										</td>
										<td>
											修改
										</td>
										<td>
											<s:property value="#pgetdeatil.addUsername" />
										</td>
										<td>
											<s:property value="#pgetdeatil.addTime" />
										</td>
									</tr>
								</s:iterator>
							</s:if>
						</s:iterator>
					</s:if>
					<tr>
						<td align="center" colspan="7">
							<b>下阶层增删</b>
						</td>
					</tr>
					<tr>
						<td align="center">
							上层件号
						</td>
						<td align="center">
							件号
						</td>
						<td align="center">
							版本
						</td>
						<td align="center">
							权值
						</td>
						<td align="center">
							操作类型
						</td>
						<td align="center">
							操作人
						</td>
						<td align="center">
							操作时间
						</td>
					</tr>
					<s:if test="ptchangeLogList!=null&&ptchangeLogList.size()>0">
						<s:iterator value="ptchangeLogList" id="pageptchangeLog"
							status="logstatus">
							<s:if test="#pageptchangeLog.entityType=='子件'">
								<tr>
									<td>
										<s:property value="#pageptchangeLog.sbMarkId" />
									</td>
									<td>
										<s:property value="#pageptchangeLog.entityData" />
									</td>
									<td>
										<s:property value="#pageptchangeLog.entityBanben" />
									</td>
									<td>
										<s:property value="#pageptchangeLog.xiaohao" />
									</td>
									<td>
										<s:property value="#pageptchangeLog.optype" />
									</td>
									<td>
										<s:property value="#pageptchangeLog.addUsername" />
									</td>
									<td>
										<s:property value="#pageptchangeLog.addTime" />
									</td>
								</tr>
							</s:if>
						</s:iterator>
					</s:if>
					<tr>
						<td align="center" colspan="7"></td>
					</tr>
					<tr>
						<td align="center" colspan="6">
							<b>工序增删</b>
						</td>
					</tr>
					<tr>
						<td align="center">
							工序号
						</td>
						<td align="center" colspan="2">
							工序名称
						</td>
						<td align="center">
							操作类型
						</td>
						<td align="center">
							操作人
						</td>
						<td align="center">
							操作时间
						</td>
					</tr>
					<s:if test="ptchangeLogList!=null&&ptchangeLogList.size()>0">
						<s:iterator value="ptchangeLogList" id="pageptchangeLog"
							status="logstatus">
							<s:if
								test="#pageptchangeLog.entityType=='工序'&&#pageptchangeLog.optype!='修改'">
								<tr>
									<td>
										<s:property value="#pageptchangeLog.entityData" />
									</td>
									<td colspan="2">
										<s:property value="#pageptchangeLog.entityData2" />
									</td>
									<td>
										<s:property value="#pageptchangeLog.optype" />
									</td>
									<td>
										<s:property value="#pageptchangeLog.addUsername" />
									</td>
									<td>
										<s:property value="#pageptchangeLog.addTime" />
									</td>
								</tr>
							</s:if>
						</s:iterator>
					</s:if>
					<tr>
						<td align="center" colspan="6">
							<b>工序更改</b>
						</td>
					</tr>
					<tr>
						<td align="center">
							属性
						</td>
						<td align="center">
							原值
						</td>
						<td align="center">
							新值
						</td>
						<td align="center">
							操作类型
						</td>
						<td align="center">
							操作人
						</td>
						<td align="center">
							操作时间
						</td>
					</tr>
					<s:if test="ptchangeLogList!=null&&ptchangeLogList.size()>0">
						<s:iterator value="ptchangeLogList" id="pageptchangeLog"
							status="logstatus">
							<s:if
								test="#pageptchangeLog.entityType=='工序'&&#pageptchangeLog.optype=='修改'">
								<s:iterator value="#pageptchangeLog.changeLogDetailSet"
									id="pgetdeatil">
									<tr>
										<td>
											<s:property value="#pgetdeatil.sxName" />
										</td>
										<td>
											<s:property value="#pgetdeatil.oldValue" />
										</td>
										<td>
											<s:property value="#pgetdeatil.newValue" />
										</td>
										<td>
											<s:property value="#pgetdeatil.addUsername" />
										</td>
										<td>
											修改
										</td>
										<td>
											<s:property value="#pgetdeatil.addTime" />
										</td>
									</tr>
								</s:iterator>
							</s:if>
						</s:iterator>
					</s:if>
					<tr>
						<td align="center" colspan="6">
							<b>图纸增删</b>
						</td>
					</tr>
					<tr>
						<td align="center" colspan="2">
							查看
						</td>
						<td align="center">
							名称
						</td>
						<td align="center">
							操作类型
						</td>
						<td align="center">
							操作人
						</td>
						<td align="center">
							操作时间
						</td>
					</tr>
					<s:if test="ptchangeLogList!=null&&ptchangeLogList.size()>0">
						<s:iterator value="ptchangeLogList" id="pageptchangeLog"
							status="logstatus">
							<s:if test="#pageptchangeLog.entityType=='图纸'">
								<tr>
									<td colspan="2">
										<a target="_showPri"
											href="<%=path%>/FileViewAction.action?FilePath=/upload/file/processTz/<s:property value="#pageptchangeLog.month"/>/<s:property value="#pageptchangeLog.realFileName"/>">

											<img
												src="<%=path%>/upload/file/processTz/<s:property value="#pageptchangeLog.month"/>/<s:property value="#pageptchangeLog.realFileName"/>"
												style="width: 80px; height: 80px;" /> </a>
									</td>
									<td>
										<s:property value="#pageptchangeLog.oldFileName" />
									</td>
									<td>
										<s:property value="#pageptchangeLog.optype" />
									</td>
									<td>
										<s:property value="#pageptchangeLog.addUsername" />
									</td>
									<td>
										<s:property value="#pageptchangeLog.addTime" />
									</td>
								</tr>
							</s:if>
						</s:iterator>
					</s:if>
				</table>
			</div>
		</div>
		<div id="glscdiv" style="display: none;background-color:#CAE1FF;" align="center">
			<s:if test="bbAply.processStatus=='关联并通知生产'">
				<s:set name="xfgcs" value="'no'"></s:set>
				<s:if test="bbAply.tbProcard=='是'.toString()">
					<s:iterator value="bbAply.bbJudegeList" id="gcsPepole">
					<s:if
						test="#gcsPepole.judgeType=='工程师'&&#session.Users.id==#gcsPepole.userId">
						<s:set name="xfgcs" value="'yes'"></s:set>
					</s:if>
					</s:iterator>
					<s:if test="#xfgcs=='yes'">
					<form id="xfscForm">
						<input type="hidden" name="bbAply.id" value="${bbAply.id}"/>
						<table>
						<tr>
							<td align="center">
								<input type="radio" name="bbAply.needzz" value="是" checked="checked" onclick="needzz('是')"> 是
								<input type="radio" name="bbAply.needzz" value="否"  onclick="needzz('否')" > 否
							</td>
						</tr>
						<tr id="needzztr">
								<td align="center">
									<input id="selectpjBtn" type="button" value="选择佐证人员"
										style="width: 120px;"
										onclick="selectnsPeople(<s:property value="#gcsPepole.id"/>,'tzUserId3','tzUsername3','selectUsers3')">
								</td>
								<td id="addzztd" align="center" colspan="2">
									<input type="hidden" name="id" value="${bbAply.id}" />
									<input type="hidden" id="ptbbJudgesid" name="ptbbJudges.id"
										value="<s:property value="#session.Users.id"/>">
									<input type="hidden" id="tzUserId3"
										name="ptbbJudges.selectUserId3" value="">
									<input type="hidden" id="tzUsername3"
										name="ptbbJudges.selectUsers3" value="">
									<label id="selectUsers3"></label>
								</td>
								
							</tr>
							<tr id="notneedzztr" style="display: none;">
								<td align="center">无需佐证理由</td>
								<td>
									<textarea rows="4" cols="70" name="bbAply.zzremark"
													id="zzremark"><s:property value="#bbAply.zzremark" /></textarea>
								</td>
							</tr>
							<tr>
								<td align="center" colspan="2">
								<input id="tbBtn" value="下发生产" type="button"
									style="width: 120px; height: 60px;" onclick="tbProcard()" />
								</td>
							</tr>
						</table>
					</form>
					</s:if>
					<s:else>
						<h3>
						<font color="gary">等待工程师下发</font>
						</h3>
					</s:else>
				</s:if>
				<s:elseif test="bbAply.tbProcard=='完成'">
					<h3>
						<font color="gary">已下发生产</font>
					</h3>
				</s:elseif>
				<s:else>
					<h3>
						<font color="red">请先完善BOM再同步生产</font>
					</h3>

				</s:else>
			</s:if>
		</div>
		<div id="schxdiv" style="display: none;background-color:#CAE1FF;">
		<s:if test="bbAply.processStatus=='生产后续'">
		<s:if test="ryzbList!=null&&ryzbList.size()>0">
					<s:iterator value="ryzbList" id="ryzb">
						<s:if test="#ryzb=='设变关闭组'">
							<div align="center">
								<input type="button" value="关闭"
									style="width: 80px; height: 60px;"
									onclick="toclosenoremark(${bbAply.id})" />
							</div>
						</s:if>
					</s:iterator>
				</s:if>
		</s:if>
			<s:if test="bbAply.processStatus=='上传佐证'">
				<s:set name="zzps" value="'no'"></s:set>
				<s:if test="bbAply.needzz=='否'.toString()">
					<div align="center">	<h3><font color="red">无需上传佐证，理由：${bbAply.zzremark}</font></h3></div>
				</s:if>
				<s:else>
				<s:iterator value="bbAply.bbJudegeList" id="zzPepole">
					<s:if
						test="#zzPepole.judgeType=='佐证'&&#session.Users.id==#zzPepole.userId">
						<form method="post"
							action="procardTemplateGyAction_uploadzzfile.action"
							enctype="multipart/form-data">
							<input name="id" type="hidden" value="${zzPepole.id}">
							<table class="table">
								<tr>
									<td align="center">
										<input type="button" id="fileButton_1"
											onclick="uploadFile(this,1)" value="上传附件">

										<div id="fileDiv_1" style="display: none;">
										</div>
									</td>
									<td>
										<input type="submit" value="上传">
									</td>
								</tr>
							</table>
						</form>
						<br />
						<table class="table">
							<tr>
								<th colspan="7" align="center">
									本人上传佐证
								</th>
							</tr>
							<tr>
								<th align="center">
									文件
								</th>
								<th align="center">
									添加人
								</th>
								<th align="center">
									添加时间
								</th>
								<th align="center">
									文件名称
								</th>
								<th align="center">
									评语
								</th>
								<th align="center">
									状态
								</th>
								<th align="center">
									操作
								</th>
							</tr>
							<s:iterator value="bbAply.ptbbjFileList" id="zzfileupdate"
								status="zzuindex">
								<s:if test="#zzfileupdate.addUserId==#session.Users.id">
									<tr id="zztr<s:property value="#zzuindex.index"/>">
										<td align="center">
											<a
												href="<%=basePath%>/FileViewAction.action?FilePath=/upload/file/ptbbjfile/<s:property value="#zzfileupdate.filename"/>">
												<img
													src="<%=path%>/upload/file/ptbbjfile/<s:property value="#zzfileupdate.filename"/>"
													style="width: 80px; height: 80px;" /> </a>
										</td>
										<td align="center">
											${zzfileupdate.addUserName}
										</td>
										<td align="center">
											${zzfileupdate.addTime}
										</td>
										<td align="center">
											${zzfileupdate.oldFileName}
										</td>
										<td align="center">
											${zzfileupdate.spRemark}
										</td>
										<td align="center">
											${zzfileupdate.spStatus}
										</td>
										<td align="center">
											<a
												href="procardTemplateGyAction_downloadzzFile.action?id=${zzfileupdate.id}">下载</a>
											<input type="button" value="删除"
												onclick="deletezz(${zzfileupdate.id},<s:property value="#zzuindex.index"/>)">
										</td>
									</tr>
								</s:if>
							</s:iterator>
						</table>
						<br />
						<br />
					</s:if>
					<s:if
						test="#zzPepole.judgeType=='工程师'&&#session.Users.id==#zzPepole.userId">
						<s:set name="zzps" value="'yes'"></s:set>
					</s:if>
				</s:iterator>
				</s:else>
				<table class="table">
					<tr>
						<th colspan="7" align="center">
							佐证文件
						</th>
					</tr>
					<tr>
						<th align="center">
							文件
						</th>
						<th align="center">
							添加人
						</th>
						<th align="center">
							添加时间
						</th>
						<th align="center">
							文件名称
						</th>
						<th align="center">
							评语
						</th>
						<th align="center">
							状态
						</th>
						<th align="center">
							操作
						</th>
					</tr>
					<s:iterator value="bbAply.ptbbjFileList" id="zzfileshow"
						status="zzindex">
						<s:if test="#zzps=='yes'">
							<form method="post"
								id="zzform<s:property value="#zzindex.index"/>">
								<tr>
									<td align="center">
										<input type="hidden" name="ptbbjfile.id"
											value="${zzfileshow.id}">
										<a
											href="<%=basePath%>/FileViewAction.action?FilePath=/upload/file/ptbbjfile/<s:property value="#zzfileshow.filename"/>">
											<img
												src="<%=path%>/upload/file/ptbbjfile/<s:property value="#zzfileshow.filename"/>"
												style="width: 80px; height: 80px;" /> </a>
									</td>
									<td align="center">
										${zzfileshow.addUserName}
									</td>
									<td align="center">
										${zzfileshow.addTime}
									</td>
									<td align="center">
										${zzfileshow.oldFileName}
									</td>
									<td align="center">
										<textarea rows="2" name="ptbbjfile.spRemark" cols="30">${zzfileshow.spRemark}</textarea>
									</td>
									<td align="center">
										<label id="zzpsStatus<s:property value="#zzindex.index"/>">
											${zzfileshow.spStatus}
										</label>
									</td>
									<td align="center">
										<a
											href="procardTemplateGyAction_downloadzzFile.action?id=${zzfileshow.id}">下载</a>
										<input type="hidden"
											id="spStatus<s:property value="#zzindex.index"/>"
											name="ptbbjfile.spStatus" value="">
										<input type="button" value="确定"
											onclick="plzz('agree',<s:property value="#zzindex.index"/>)">
										<input type="button" value="打回"
											onclick="plzz('back',<s:property value="#zzindex.index"/>)">
									</td>
								</tr>
							</form>
						</s:if>
						<s:else>
							<tr>
								<td align="center">
									<a
										href="<%=basePath%>/FileViewAction.action?FilePath=/upload/file/ptbbjfile/<s:property value="#zzfileshow.filename"/>">
										<img
											src="<%=path%>/upload/file/ptbbjfile/<s:property value="#zzfileshow.filename"/>"
											style="width: 80px; height: 80px;" /> </a>
								</td>
								<td align="center">
									${zzfileshow.addUserName}
								</td>
								<td align="center">
									${zzfileshow.addTime}
								</td>
								<td align="center">
									${zzfileshow.oldFileName}
								</td>
								<td align="center">
									${zzfileshow.spRemark}
								</td>
								<td align="center">
									${zzfileshow.spStatus}
								</td>
								<td align="center">
									<a
										href="procardTemplateGyAction_downloadzzFile.action?id=${zzfileshow.id}">下载</a>
								</td>
							</tr>
						</s:else>
					</s:iterator>
				</table>
				<s:if test="ryzbList!=null&&ryzbList.size()>0">
					<s:iterator value="ryzbList" id="ryzb">
						<s:if test="#ryzb=='设变关闭组'">
							<div align="center">
								备注:<textarea id="zzgbremark" rows="4" cols="40">${bbAply.remark}</textarea>
								<br>
								<input type="button" value="关闭"
									style="width: 80px; height: 60px;"
									onclick="toclose('zzgbremark',${bbAply.id})" />
							</div>
						</s:if>
					</s:iterator>
				</s:if>
				<%--					<s:if test="#zzps=='yes'">--%>
				<%--					<div align="center">--%>
				<%--						<input type="button" value="关闭" style="width: 80px;height: 60px;" onclick="toclosenoremark(${bbAply.id})"/>--%>
				<%--					</div>--%>
				<%--					</s:if>--%>

				<table class="table">
					<tr>
						<th colspan="6">
							佐证人员
						</th>
					</tr>
					<s:if test="#zzps=='yes'">
						<form id="addzzryForm" method="post">
							<tr>
								<td align="center">
									<input id="selectpjBtn" type="button" value="选择佐证人员"
										style="width: 120px;"
										onclick="selectnsPeople(<s:property value="#gcsPepole.id"/>,'tzUserId3','tzUsername3','selectUsers3')">
								</td>
								<td id="addzztd" align="center" colspan="2">
									<input type="hidden" name="id" value="${bbAply.id}" />
									<input type="hidden" id="ptbbJudgesid" name="ptbbJudges.id"
										value="<s:property value="#session.Users.id"/>">
									<input type="hidden" id="tzUserId3"
										name="ptbbJudges.selectUserId3" value="">
									<input type="hidden" id="tzUsername3"
										name="ptbbJudges.selectUsers3" value="">
									<label id="selectUsers3"></label>
								</td>
								<td align="center">
									<input id="addzzryBtn" value="提交" type="button"
										onclick="addzzry()" />
								</td>
							</tr>
						</form>
					</s:if>
					<tr>
						<th>
							部门
						</th>
						<th>
							类型
						</th>
						<th>
							工号
						</th>
						<th>
							名称
						</th>
					</tr>
					<s:iterator value="bbAply.bbJudegeList" id="zzPepole">
						<s:if test="#zzPepole.judgeType=='佐证'">
							<tr>
								<td align="left">
									${zzPepole.dept}
								</td>
								<td align="left">
									${zzPepole.judgeType}
								</td>
								<td align="left">
									${zzPepole.userCode}
								</td>
								<td align="left">
									${zzPepole.userName}
								</td>
							</tr>
						</s:if>
					</s:iterator>
				</table>
				<br />

			</s:if>
			<s:else>
			<s:if test="bbAply.needzz=='否'.toString()">
					<div align="center">	<h3><font color="red">无需上传佐证，理由：${bbAply.zzremark}</font></h3></div>
				</s:if>
				<table class="table">
					<tr>
						<th colspan="7" align="center">
							佐证文件
							<s:property value="#zzps" />
						</th>
					</tr>
					<tr>
						<th align="center">
							文件
						</th>
						<th align="center">
							添加人
						</th>
						<th align="center">
							添加时间
						</th>
						<th align="center">
							文件名称
						</th>
						<th align="center">
							评语
						</th>
						<th align="center">
							状态
						</th>
						<th align="center">
							操作
						</th>
					</tr>
					<s:iterator value="bbAply.ptbbjFileList" id="zzfileshow"
						status="zzindex">
						<tr>
							<td align="center">
								<a
									href="<%=basePath%>/FileViewAction.action?FilePath=/upload/file/ptbbjfile/<s:property value="#zzfileshow.filename"/>">
									<img
										src="<%=path%>/upload/file/ptbbjfile/<s:property value="#zzfileshow.filename"/>"
										style="width: 80px; height: 80px;" /> </a>
							</td>
							<td align="center">
								${zzfileshow.addUserName}
							</td>
							<td align="center">
								${zzfileshow.addTime}
							</td>
							<td align="center">
								${zzfileshow.oldFileName}
							</td>
							<td align="center">
								${zzfileshow.spRemark}
							</td>
							<td align="center">
								${zzfileshow.spStatus}
							</td>
							<td align="center">
								<a
									href="procardTemplateGyAction_downloadzzFile.action?id=${zzfileshow.id}">下载</a>
							</td>
						</tr>
					</s:iterator>
				</table>
				<br />
				<table class="table">
					<tr>
						<th colspan="6">
							佐证人员
						</th>
					</tr>
					<tr>
						<th>
							部门
						</th>
						<th>
							类型
						</th>
						<th>
							工号
						</th>
						<th>
							名称
						</th>
					</tr>
					<s:iterator value="bbAply.bbJudegeList" id="zzPepole">
						<s:if test="#zzPepole.judgeType=='佐证'">
							<tr>
								<td align="left">
									${zzPepole.dept}
								</td>
								<td align="left">
									${zzPepole.judgeType}
								</td>
								<td align="left">
									${zzPepole.userCode}
								</td>
								<td align="left">
									${zzPepole.userName}
								</td>
							</tr>
						</s:if>
					</s:iterator>
				</table>
			</s:else>
		</div>
		<div id="gbdiv" style="display: none;background-color:#CAE1FF;"></div>
		</div>
		<br/>
		<br/>
		<br/>
		<%@include file="/util/foot.jsp"%>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
		<SCRIPT type="text/javascript">
var index=0;
var strs1=new Array("fqsb","ffxmz","zgcp","xzsblj","bgsj","gxsps","zpgbm","cbsh","bmps","zlgx","glsc","schx","gb");
var strs2=new Array("fqsbdiv","ffxmzdiv","zgcpdiv","xzsbljdiv","bgsjdiv","gxspsdiv","zpgbmdiv","cbshdiv","bmpsdiv","zlgxdiv","glscdiv","schxdiv","gbdiv");
$(document).ready(function() {
	var processStatus= "${bbAply.processStatus}";
	if(processStatus=="取消"){
		processStatus = "${bbAply.oldProcessStatus}";
	}
	if(processStatus=="设变发起"){
		index=0;
	}else if(processStatus=="分发项目组"){
		index=1;
	}else if(processStatus=="项目主管初评"){
		//getAllDept();
		getsbtypePepole("xmgcs","设变工程师");
		index=2;
	}else if(processStatus=="选择设变零件"){
		index=3;
	}else if(processStatus=="变更设计"){
		index=4;
	}else if(processStatus=="工程师评审"){
		index=5;
	}else if(processStatus=="指派各部门"){
		index=6;
	}else if(processStatus=="成本审核"){
		index=7;
	}else if(processStatus=="各部门评审"){
		index=8;
	}else if(processStatus=="资料更新"){
		index=9;
		var bzjdCount="${bzjdCount}";
		if(bzjdCount!=null){
		getGyPeople("bz");
		if(bzjdCount!="2"){
			getGyPeople("jd");
		}
		if(bzjdCount!="2"&&bzjdCount!="3"){
			getGyPeople("sh");
		}
		getGyPeople("pz");
		}
	}else if(processStatus=="关联并通知生产"){
		index=10;
	}else if(processStatus=="上传佐证"||processStatus=="生产后续"){
		$("#schx").html(processStatus);
		index=11;
	}else if(processStatus=="关闭"){
		index=12;
	}
	for(var i=0;i<=12;i++){
		if(i<index){
			$("#"+strs1[i]).css("background-color","#5cb85c");
			$("#"+strs2[i]).hide();
		}else if(i==index){
			$("#"+strs1[i]).css("background-color","#ADFF2F");
			$("#"+strs2[i]).show();
			
		}else{
			$("#"+strs1[i]).css("background-color","gray");
			$("#"+strs2[i]).hide();
		}
	}
	fujiancl();
});
function sbprocessStatus(num){
	if(num>index){
		return false;
	}else{
		for(var i=0;i<=12;i++){
		if(i<=index){
			$("#"+strs1[i]).css("background-color","#5cb85c");
		}else{
			$("#"+strs1[i]).css("background-color","gray");
			
		}
		if(num==i){
			$("#"+strs2[i]).show();
			$("#"+strs1[i]).css("background-color","#ADFF2F");
		}else{
			$("#"+strs2[i]).hide();
		}
	}
	}
	
}
//撤销设变
function cxSbapply(id){
	
}

function submitchupin(cpindex){
	var  cpremark=$("#cpremark").val();
	if(cpremark==null||cpremark==""){
		alert("请填写评语!");
		return false;
	}
	$.ajax( {
		type : "POST",
		url : "procardTemplateGyAction_submitchupin.action",
		dataType : "json",
		data : $("#chupin"+cpindex).serialize(),
		success : function(data) {
				alert(data);
				if(data=="提交成功,初评结束!"){
					window.location.href="procardTemplateGyAction_showSbApplyJd2.action?bbAply.id=${bbAply.id}";
				}
	}
	});
}
function chageAllCheck() {
	var checkAll = document.getElementById("checkAll");
	var checkboxs = document.getElementsByName("checkboxs");
	if (checkAll.checked == true) {
		for ( var i = 0; i < checkboxs.length; i++) {
			checkboxs[i].checked = true;
		}
	} else {
		for ( var i = 0; i < checkboxs.length; i++) {
			checkboxs[i].checked = false;
		}
	}

}
function chageNum() {
	var checkAll = document.getElementById("checkAll");
	var checkboxs = document.getElementsByName("checkboxs");
	var count = 0;
	for ( var i = 0; i < checkboxs.length; i++) {
		if (checkboxs[i].checked == false) {
			checkAll.checked = false;
		} else {
			count++;
		}
	}
	if (count == checkboxs.length) {
		checkAll.checked = true;
	}
}
function getAllDept(){
	if($("#xmgcsDept")==null){
		return false;
	}
	$
				.ajax( {
					type : "post",
					url : "DeptNumberAction!findAllDept.action",
					data : {
						id : $("#xmgcsDept").val()
					},
					dataType : "json",
					success : function(data) {
							$("#username").empty();
							$("<option value='0'>请选择部门</option>").appendTo(
									"#xmgcsDept");

						$(data).each(
								function() {
									$(
											"<option value='" + this.id + "'>"
													+ this.dept + "</option>")
											.appendTo("#xmgcsDept");
								});
						var tinyselect = $(".tinyselect");
						if (tinyselect[1] != null) {
							document.getElementById("#xmgcsDept").removeChild(
									tinyselect[1]);
						}
						$("#xmgcsDept").tinyselect();

					}
				});
}


function getgcs(i){
	$
				.ajax( {
					type : "post",
					url : "GzstoreAction_getusers.action",
					data : {
						id : $("#xmgcsDept").val()
					},
					dataType : "json",
					success : function(data) {
							$("#xmgcs"+i).empty();
							$("<option value='0'>请选择人员</option>").appendTo(
									"#xmgcs"+i);

						$(data).each(
								function() {
									$(
											"<option value='" + this.id + "'>"
													+ this.code + "__"
													+ this.name + "</option>")
											.appendTo("#xmgcs"+i);
								});
						var tinyselect = $(".tinyselect");
						if (tinyselect[1] != null) {
							document.getElementById("gcstd").removeChild(tinyselect[1]);
						}
						$("#xmgcs"+i).tinyselect();

					}
				});
}
//工程师评审
function submitgcsps(){
	var form ; 
	var btn ;
	var ljremark=$("#ljremark").val();
	if(ljremark==null||ljremark==""){
			alert("请填写评语!");
			return false;
	}
		
	btn=$("#jsSubBtn");
	form =$("#jlform"); 
	btn.attr("disabled","disabled");
	$ .ajax( {
					type : "post",
					url : "procardTemplateSbAction_submitgcsps.action",
					data :  form.serialize(),
						
					dataType : "json",
					success : function(data) {
						btn.removeAttr("disabled");
						alert(data);
						if(data=="评审成功"){
							window.location.href="procardTemplateGyAction_showSbApplyJd2.action?bbAply.id=${bbAply.id}";
						}
					}
	});
}
function submitzpgbm(){
	var form ; 
	var btn ;
	var ljremark=$("#zpgremark").val();
	if(ljremark==null||ljremark==""){
			alert("请填写评语!");
			return false;
	}
		
	btn=$("#zpgbmSubBtn");
	form =$("#zpgbmform"); 
	btn.attr("disabled","disabled");
	$ .ajax( {
					type : "post",
					url : "procardTemplateSbAction_submitzpgbm.action",
					data :  form.serialize(),
						
					dataType : "json",
					success : function(data) {
						btn.removeAttr("disabled");
						alert(data);
						if(data=="指派成功"){
							window.location.href="procardTemplateGyAction_showSbApplyJd2.action?bbAply.id=${bbAply.id}";
						}
					}
	});
}
//内审提交
function submitnp(tag){
	var form ; 
	var btn ;
	var nsremark=$("#nsremark").val();
	if(nsremark==null||nsremark==""){
		alert("请填写评语!");
		return false;
	}
	btn=$("#nsSubBtn");
	form =$("#npform"); 
	btn.attr("disabled","disabled");
	$ .ajax( {
					type : "post",
					url : "procardTemplateSbAction_submitnp.action",
					data :  form.serialize(),
						
					dataType : "json",
					success : function(data) {
						btn.removeAttr("disabled");
						alert(data);
						window.location.href="procardTemplateGyAction_showSbApplyJd2.action?bbAply.id=${bbAply.id}";
					}
	});
}
//内审提交
function submitnoprocardns(tag){
	var form ; 
	var btn ;
	var nsremark=$("#nbpsremark").val();
	if(nsremark==null||nsremark==""){
			alert("请填写评语!");
			return false;
	}
	btn=$("#nbpssubmit");
	form =$("#noprocardnsForm"); 
	btn.attr("disabled","disabled");
	$ .ajax( {
					type : "post",
					url : "procardTemplateSbAction_noprocardns.action",
					data :  form.serialize(),
					dataType : "json",
					success : function(data) {
						btn.removeAttr("disabled");
						alert(data);
						window.location.href="procardTemplateGyAction_showSbApplyJd2.action?bbAply.id=${bbAply.id}";
					}
	});
}
//成本审核提交
function submitcbsh(){
	var form ; 
	var btn ;
	var cbpsremark=$("#cbpsremark").val();
	if(cbpsremark==null||cbpsremark==""){
			alert("请填写评语!");
			return false;
	}
	btn=$("#cbpssubmit");
	form =$("#cbForm"); 
	btn.attr("disabled","disabled");
	$ .ajax( {
					type : "post",
					url : "procardTemplateSbAction_submitcbsh.action",
					data :  form.serialize(),
					dataType : "json",
					success : function(data) {
						btn.removeAttr("disabled");
						alert(data);
						window.location.href="procardTemplateGyAction_showSbApplyJd2.action?bbAply.id=${bbAply.id}";
					}
	});
}
function suresb(){
	$("#suresbBtn").attr("disabled","disabled");
	$ .ajax( {
					type : "post",
					url : "procardTemplateSbAction_suresb.action",
					data :  $("#suresbForm").serialize(),
					dataType : "json",
					success : function(data) {
						$("#suresbBtn").removeAttr("disabled");
						alert(data);
						window.location.href="procardTemplateGyAction_showSbApplyJd2.action?bbAply.id=${bbAply.id}";
					}
	});
}

function tbProcard(){
	$("#tbBtn").attr("disabled","disabled");
	$ .ajax( {
					type : "post",
					url : "procardTemplateGyAction_tbProcard.action",
					data :  $("#xfscForm").serialize(),
					dataType : "json",
					success : function(data) {
						$("#suresbBtn").removeAttr("disabled");
						alert(data);
						window.location.href="procardTemplateGyAction_showSbApplyJd2.action?bbAply.id=${bbAply.id}";
					}
	});
}
function submituplvForm(){
	var value  = $('input[name="bbAply.needDeptJudege"]:checked').val();
	if(value=="是"){
		if($("#selectUserId").val()==null||$("#selectUserId").val()==""){
		alert("请选中内审人员!");
		return false;
		}
	}
	$("#subFormBtn").attr("disabled","disabled");
	$ .ajax( {
					type : "post",
					url : "procardTemplateGyAction_sbzpgbm.action",
					data :   $("#uplvForm").serialize(),
					dataType : "json",
					success : function(data) {
						$("#subFormBtn").removeAttr("disabled");
						alert(data);
						parent.window.location.href="procardTemplateGyAction_showSbApplyJd2.action?bbAply.id=${bbAply.id}";
			}
	});
}
function selectnsPeople(id,idname1,idname2,idname3){
	chageDiv('block');
			$("#operatingDiv").show();
			$("#toclosesbdiv").hide();
	var ids= $("#selectUserId").val();
	$("#showProcess").attr("src","procardTemplateSbAction_findAboutDept.action?ids="+ids+"&idname1="+idname1+"&idname2="+idname2+"&idname3="+idname3);
}

function backsbApply(id,btnId,csId){
	var backremark = $("#"+csId).val();
	if(confirm("是否确认打回？")){
		$("#"+btnId).attr("disabled","disabled");
		$ .ajax( {
					type : "post",
					url : "procardTemplateSbAction_backsbApply.action",
					data :  {
						id : id,
						remark : backremark
					},
					dataType : "json",
					success : function(data) {
						if(data=="true"||data){
							alert("确认成功!");
							window.location.href="procardTemplateGyAction_showSbApplyJd2.action?bbAply.id="+${bbAply.id};
						}else{
							alert(data);
							$("#"+btnId).removeAttr("disabled");
						}
					}
		});
	}
}
function getGyPeople(type) {
	$.ajax( {
		type : "POST",
		url : "procardTemplateGyAction_getGyPeople.action",
		data : {
			tag : type
		},
		dataType : "json",
		success : function(data) {
			$(data).each(
					function() {
						if (type == "bz") {
							$(".bianzhi").append(
									"<option value='" + $(this).attr('userId')
											+ "'>" + $(this).attr('userName')
											+ "</option>");
						} else if (type == "jd") {
							$(".jiaodui").append(
									"<option value='" + $(this).attr('userId')
											+ "'>" + $(this).attr('userName')
											+ "</option>");
						} else if (type == "sh") {
							$(".shenhe").append(
									"<option value='" + $(this).attr('userId')
											+ "'>" + $(this).attr('userName')
											+ "</option>");
						} else if (type == "pz") {
							$(".pizhun").append(
									"<option value='" + $(this).attr('userId')
											+ "'>" + $(this).attr('userName')
											+ "</option>");

						}
					});
		}
	});
}
function changepeople(type){
	var before="";
	if(type=="bz"){
		before="bianzhi";
	}else if(type=="jd"){
		before="jiaodui";
	}else if(type=="sh"){
		before="shenhe";
	}else if(type=="pz"){
		before="pizhun";
	}
	var value=$("#"+before+"Tem").val();
	if(value==null||value.lenth==0){
		$("."+before).val("");
	}else{
		$("."+before).val(value);
	}
}
function tcl(clindex,pc){
	var value =$("#clfa"+clindex).val();
	$(".cl1"+pc).val(value);
}
function alltcl(obj){
	var value =$(obj).val();
	$(".alltcl").val(value);
}
function disBrn(){
	$("#subBtn").attr("disabled","disabled");
}

function passns(id){
	$("#passnsBtn").attr("disabled","disabled");
	$ .ajax( {
					type : "post",
					url : "procardTemplateGyAction_passns.action",
					data :  {
						id:id
					},
					dataType : "json",
					success : function(data) {
						$("#passnsBtn").removeAttr("disabled");
						if(data=="true"){
							alert("强制跳过成功!");
							parent.window.location.href="procardTemplateGyAction_showSbApplyJd2.action?bbAply.id=${bbAply.id}";
						}else{
							alert(data);
						}
			}
	});
}
function updatepbbremark(pbbremarkIndex){
	$("#bbreamkbtn"+pbbremarkIndex).attr("disabled","disabled");
	$ .ajax( {
					type : "post",
					url : "procardTemplateGyAction_updatepbbremark.action",
					data :   $("#pbbremarkForm"+pbbremarkIndex).serialize(),
					dataType : "json",
					success : function(data) {
						$("#bbreamkbtn"+pbbremarkIndex).removeAttr("disabled");
						alert(data);
					}
	});
}
function todahui(remarkId,id){
	if(confirm("是否确定打回?")){
		$ .ajax( {
					type : "post",
					url : "procardTemplateGyAction_dahuisb.action",
					data :  {
						id : id,
						remark : $("#"+remarkId).val()
					},
					dataType : "json",
					success : function(data) {
						if(data=="true"){
							alert("打回成功!");
							window.location.href="procardTemplateGyAction_showSbApplyJd2.action?bbAply.id="+id;
						}else{
							alert(data);
						}
						
					}
		});
	}
}
function toclose(remarkId,id,outid){
		if(outid!=null){
			$("#sbapplyId").val(id);
			$("#operatingDiv").hide();
			$("#toclosesbdiv").show();
			chageDiv("block");
		}else{
			if(confirm("是否确定关闭?")){
				$ .ajax( {
						type : "post",
						url : "procardTemplateGyAction_closeSbwithremark.action",
						data :  {
							id : id,
							remark : $("#"+remarkId).val()
						},
						dataType : "json",
						success : function(data) {
							if(data=="true"){
								alert("关闭成功!");
								window.location.href="procardTemplateGyAction_showSbApplyJd2.action?bbAply.id="+id;
							}else{
								alert(data);
							}
							
						}
				});							
			}
		}
}
function toclosenoremark(id){
	if(confirm("是否确定关闭?")){
		$ .ajax( {
					type : "post",
					url : "procardTemplateGyAction_closeclosenoremark.action",
					data :  {
						id : id
					},
					dataType : "json",
					success : function(data) {
						if(data=="true"){
							alert("关闭成功!");
							window.location.href="procardTemplateGyAction_showSbApplyJd2.action?bbAply.id="+id;
						}else{
							alert(data);
						}
						
					}
		});
	}
}

function plzz(jl,zzindex){
	var pl = "";
	var alertv = "";
	if(jl=="back"){
		alertv ="是否确定打回此佐证!";
		pl ="打回";
		$("#spStatus"+zzindex).val(pl);
	}else{
		alertv ="是否确定同意此佐证!";
		pl ="同意";
		$("#spStatus"+zzindex).val(pl);
	}
	if(confirm(alertv)){
		$ .ajax( {
					type : "post",
					url : "procardTemplateGyAction_plzz.action",
					data :  $("#zzform"+zzindex).serialize(),
					dataType : "json",
					success : function(data) {
						if(data=="true"){
							alert(pl+"成功!");
							$("#zzpsStatus"+zzindex).html(pl);
<%--							window.location.href="procardTemplateGyAction_showSbApplyJd2.action?bbAply.id="+id;--%>
						}else{
							alert(data);
						}
						
					}
		});
	}
	$("#spStatus"+zzindex).val("");
	
}

var count = 0;
function uploadFile(obj, few) {
	var fileDiv = document.getElementById("fileDiv_" + few);
	if (obj.value == "上传附件") {
		fileDiv.style.display = "block";
		obj.value = "添加文件";
	}
	fileDivHTML = "<div id='file"
			+ count
			+ "'><input type='file' name='attachment'><a href='javascript:delFile("
			+ count + "," + few + ")'>删除</a></div>";
	fileDiv.insertAdjacentHTML("beforeEnd", fileDivHTML);
	count++;
}

function delFile(obj, few) {
	document.getElementById("file" + obj).parentNode.removeChild(document
			.getElementById("file" + obj));
	count--;
	if (count <= 0) {
		count = 0;
		document.getElementById("fileButton_" + few).value = "上传附件";
		document.getElementById("fileDiv_" + few).style.display = "none";
	}
}


function deletezz(id,zzindex){
	$ .ajax( {
					type : "post",
					url : "procardTemplateGyAction_deletezz.action",
					data :  {
						id : id	
					},
					dataType : "json",
					success : function(data) {
						if(data=="true"){
							alert("删除成功!");
							$("zztr"+zzindex).hide();
						}else{
							alert(data);
						}
						
					}
		});
	
}

function addzzry(){
	$ .ajax( {
					type : "post",
					url : "procardTemplateGyAction_addzzry.action",
					data :  $("#addzzryForm").serialize(),
					dataType : "json",
					success : function(data) {
						if(data=="true"){
							alert("指派成功!");
							window.location.href="procardTemplateGyAction_showSbApplyJd2.action?bbAply.id="+${bbAply.id};
						}else{
							alert(data);
							$("#addzzryBtn").removeAttr("disabled");
						}
						
					}
		});
	
}

function getsbtypePepole(eId,typeName){
	$ .ajax( {
					type : "post",
					url : "procardTemplateGyAction_getsbtypePepole.action",
					data :  {
						tag : typeName
					},
					dataType : "json",
					success : function(data) {
						$(data).each(
							function() {
							$("#"+eId).append(
									"<option value='" + $(this).attr('userId')
											+ "'>" + $(this).attr('userName')
											+ "</option>");
						
						});
					}
		});
}
function needzz(isneed){
	if(isneed=="是"){
		$("#needzztr").show();
		$("#notneedzztr").hide();
	}else{
		$("#needzztr").hide();
		$("#notneedzztr").show();
	}
}

function fujiancl(){
	var fileNames = '${bbAply.fileName}';
	if(fileNames!=null && fileNames.length>0){
		var fileNameArry = fileNames.split("|");
		if(fileNameArry!=null && fileNameArry.length>0){
			for(var i=0;i<fileNameArry.length;i++){
				$("#fujian_td").append('<a href="DownAction.action?fileName='+fileNameArry[i]+'&directory=/upload/file/sbFile/">下载文件'+i+'</a><br/>');
			}
		}
	}else{
		$("#fujian_td").html('无附件');
	}
}
function surechange(){
	if(confirm("是否确认变更完成？")){
		$("#surechangebtn").attr("disabled","disabled");;
		$ .ajax( {
					type : "post",
					url : "procardTemplateSbAction_surechange.action",
					data :  {
						id : ${bbAply.id}
					},
					dataType : "json",
					success : function(data) {
						if(data=="true"||data==true){
							alert("确认成功!");
							window.location.href="procardTemplateGyAction_showSbApplyJd2.action?bbAply.id="+${bbAply.id};
						}else{
							alert(data);
							$("#surechangebtn").removeAttr("disabled");
						}
					}
		});
	}
}
function xysbDetail (){
	var sbmxtag = $("#sbmxtag").val();
	if(sbmxtag=="h"){
		$("#sbmxtag").val("s");
		$("#sbmxdtable").show();
		$("#sbbgmxtag").val("h");
		$("#sbbgmxdtable").hide();
		$("#sbxgljmxtag").val("h");
		$("#sbxgljmxdtable").hide();
	}else{
		$("#sbmxtag").val("h");
		$("#sbmxdtable").hide();
	}
}
function xysbbgDetail (){
	var sbbgmxtag = $("#sbbgmxtag").val();
	if(sbbgmxtag=="h"){
		$("#sbbgmxtag").val("s");
		$("#sbbgmxdtable").show();
		$("#sbxgljmxtag").val("h");
		$("#sbxgljmxdtable").hide();
		$("#sbmxtag").val("h");
		$("#sbmxdtable").hide();
	}else{
		$("#sbbgmxtag").val("h");
		$("#sbbgmxdtable").hide();
	}
}
function xysbxgljDetail (){
	var sbxgljmxtag = $("#sbxgljmxtag").val();
	if(sbxgljmxtag=="h"){
		$("#sbxgljmxtag").val("s");
		$("#sbxgljmxdtable").show();
		$("#sbmxtag").val("h");
		$("#sbmxdtable").hide();
		$("#sbbgmxtag").val("h");
		$("#sbbgmxdtable").hide();
	}else{
		$("#sbxgljmxtag").val("h");
		$("#sbxgljmxdtable").hide();
	}
}

	var pszbsize=0;
	var pszbindex=0;
function addpszbTr(){
	var pszbhtml = "<tr id='pszbtr"+pszbindex+"'>" +
		"<td align='center'><select id='pszbxx"+pszbindex+"' onchange='changepszb("+pszbindex+")'></select> </td>" +
		"<td align='center'><select name='ptbbJudgeslist["+pszbindex+"].userId' id='psryxx"+pszbindex+"'></select></td>" +
		"<td align='center'><input id='psryscbtn"+pszbindex+"' value='删除' type='button' onclick='deletepszbTr("+pszbindex+")' /></td></tr>";
	$("#pszbtr2").before(pszbhtml)
	$ .ajax( {
					type : "post",
					url : "procardTemplateSbAction_findsbzbList.action",
					dataType : "json",
					async:false,
					success : function(data) {
						$("#pszbxx"+pszbindex).append("<option value='0'>请选择组别</option>");
						$(data).each(
							function() {
							$("#pszbxx"+pszbindex).append(
									"<option value='" + $(this).attr('id')
											+ "'>" + $(this).attr('groupName')
											+ "</option>");
						
						});
					}
		});
	pszbindex++;
	pszbsize++;
}

function changepszb(thisindex){
	var pszb = $("#pszbxx"+thisindex).val();
	if(pszb>0){
		$ .ajax( {
					type : "post",
					url : "procardTemplateSbAction_findsbrylistbyzb.action",
					dataType : "json",
					data : {
						id:pszb
					},
					async:false,
					success : function(data) {
						$("#psryxx"+thisindex).empty();
						$("#psryxx"+thisindex).append("<option value='0'>请选择组别</option>");
						$(data).each(
							function() {
							$("#psryxx"+thisindex).append(
									"<option value='" + $(this).attr('id')
											+ "'>" + $(this).attr('userName')+"("+$(this).attr('dept')+")"
											+ "</option>");
						
						});
					}
		});
	}
}
function deletepszbTr(thisindex){
	if(pszbsize==0){
		alert("不能再删除了!");
		return false;
	}
	$("#pszbtr"+thisindex).remove();
	pszbsize--;
}
function transferWork(id){
	chageDiv('block');
			$("#operatingDiv").show();
			$("#toclosesbdiv").hide();
	$("#showProcess").attr("src","procardTemplateSbAction_totransferWork.action?id="+id);
}
</SCRIPT>
	</body>
</html>
