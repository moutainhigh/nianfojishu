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
		<title></title>
		<%@include file="/util/sonHead.jsp"%>
		<link style="text/css" rel="stylesheet" href="<%=basePath %>/css/bootstrap.css" >
	</head>
	<body>
		<%@include file="/util/sonTop.jsp"%>
		<div id="gongneng" style="width: 100%;">
			<div align="center">
				<form
					action="procardBlAction_applicationTuiliao.action?rootId=${id}"
					method="post" onsubmit="onebi()">
					<table class="table">
						<tr>
							<td colspan="12" align="left">
								<input id="ok" class="input" style="width: 120px;" align="top"
									type="submit" value="申请退料" />
								<font color="red">注意:红色为设变删除物料，请优先退料!</font>
							</td>
						</tr>
						<tr>
							<th>
								<%--								全选--%>

								<%--								<input type="checkbox" id="checkAll" onchange="chageAllCheck()">--%>
								<input type="checkbox" onclick="chageAllCheck(this);changCheckedAll(this)"
									id="checkAll">
								全选
							</th>
							<th>
								件号
							</th>
							<th>
								批次
							</th>
							<th>
								名称
							</th>
							<th>
								类型
							</th>
							<th>
								单位
							</th>
							<th>
								批次数量
							</th>
							<th>
								已发料数量
							</th>
							<th>
								申请退料数量
							</th>
							<th>
								已退料数量
							</th>
							<th>
								申请状态
							</th>
							<th>
								操作
							</th>
						</tr>
						<tr>
							<th align="center" colspan="12">
								可退料信息
							</th>
						</tr>
						<%--						<s:set name="jydclIndex" value="0"></s:set>--%>
						<s:if test="procardTlList.size()==0">
							<tr>
								<td align="center" colspan="15">
									<font color="red" size="4">可退料数据为空</font>
								</td>
							</tr>
						</s:if>
						<s:iterator value="procardTlList" id="procard1" status="blStatus1">
							<s:if test="#blStatus1.index%2==1">
								<tr align="center" bgcolor="#e6f3fb"
									onmouseover="chageBgcolor(this)"
									onmouseout="outBgcolor(this,'#e6f3fb')">
							</s:if>
							<s:else>
								<tr align="center" onmouseover="chageBgcolor(this)"
									onmouseout="outBgcolor(this,'')">
							</s:else>
							<td>
								<%--								<s:if test="#pageProcardbl1.procard.thisAlertCount>0">--%>
								<%--									<input type="text" name="peiqiCount"--%>
								<%--										id="peiqiCount<s:property value="#jydclIndex"/>"--%>
								<%--										value="${pageProcardbl1.thisAlertCount}"--%>
								<%--										style="width: 40px; display: none;" disabled="disabled">--%>
								<%--									<input type="checkbox" name="checkboxs"--%>
								<%--										value="${pageProcardbl1.id}" onchange="chageNum()" />--%>
								<%--									<s:set name="jydclIndex" value="#jydclIndex+1"></s:set>--%>
								<%--								</s:if>--%>
								${blStatus1.index+1}
								<input type="checkbox"  value="${procard1.id}" name="procardList[${blStatus1.index+1}].id"
									onclick="chageNum(this)"  onchange="changChecked(this,${blStatus1.index+1})" >
							</td>
							<s:if test='#procard1.sbStatus=="删除"'>
								<td style="background-color: red; font-weight: bolder;"
									title="设变删除物料,请优先退料">
									${procard1.markId}
									<font color="yellow" style="font-size: 8px;">[优先退料]</font>
								</td>
							</s:if>
							<s:else>
								<td>
									${procard1.sbStatus} ${procard1.markId}
								</td>
							</s:else>
							<td>
								${procard1.selfCard}
							</td>
							<td align="left">
								${procard1.proName}
							</td>
							<td>
								${procard1.procardStyle}
							</td>
							<td>
								${procard1.unit}
							</td>
							<td>
								${procard1.filnalCount}
							</td>
							<td>
								<fmt:formatNumber
									value="${procard1.filnalCount-procard1.hascount}"
									pattern="#.####"></fmt:formatNumber>
							</td>
							<td>
								${procard1.stuiLiaoNumber}
							</td>
							<td>
								${procard1.ytuiLiaoNumber}
							</td>
							<td>
								<a
									href="CircuitRunAction_findAduitPage.action?id=${procard1.epId}">${procard1.tuiLiaoStatus}</a>
							</td>
							<td>

								<%--								<input type="hidden" id="ytuiLiaoNumber_${blStatus1.index+1}" style="width: 60px;" value="${procard1.klNumber-procard1.hascount}"/><!-- 可退料数量=可领数量-已领数量 -->--%>
								<s:if test="#procard1.klNumber>#procard1.filnalCount">
									<input type="text" id="stuiLiaoNumber_${blStatus1.index+1}"
										style="width: 60px;"
										value="<fmt:formatNumber value='${procard1.filnalCount-procard1.hascount}' pattern='#.####'></fmt:formatNumber>"
										disabled="disabled" name="stuiLiaoNumber"
										onkeyup="checkNum('${procard1.filnalCount-procard1.hascount}',this)"
										onblur="checkNum('${procard1.filnalCount-procard1.hascount}',this)" />
								</s:if>
								<s:else>
									<input type="text" id="stuiLiaoNumber_${blStatus1.index+1}"
										style="width: 60px;"
										value="<fmt:formatNumber value='${procard1.klNumber-procard1.hascount}' pattern='#.####'></fmt:formatNumber>"
										disabled="disabled" name="stuiLiaoNumber"
										onkeyup="checkNum('${procard1.klNumber-procard1.hascount}',this)"
										onblur="checkNum('${procard1.klNumber-procard1.hascount}',this)" />
								</s:else>
								<%--								<input type="button" value="申请退料" id="tuiliao_${blStatus1.index+1}" style="height: 22px;width: 80px;" onclick="appli1(${procard1.id},${blStatus1.index+1})">--%>
							</td>
							</tr>
						</s:iterator>
						<tr>
							<th colspan="1" align="left">
								&nbsp;&nbsp;&nbsp;&nbsp;
								<input type="checkbox" onclick="chageAllCheck(this);changCheckedAll(this)"
									id="checkAll2">
								全选
							</th>
							<td colspan="11">
								<input id="ok1" class="input" style="width: 120px;" align="top"
									type="submit" value="申请退料" />
							</td>
						</tr>
						<%--						<tr id="htr2" style="display: none;">--%>
						<%--							<th align="center" colspan="13">--%>
						<%--								<input type="submit" value="确定"--%>
						<%--									style="width: 80px; height: 40px;">--%>
						<%--							</th>--%>
						<%--						</tr>--%>
						<tr>
							<th align="center" colspan="12">
								已申请退料
							</th>
						</tr>
						<s:iterator value="procardYTlList" id="procard2"
							status="blStatus2">
							<s:if test="#blStatus2.index%2==1">
								<tr align="center" bgcolor="#e6f3fb"
									onmouseover="chageBgcolor(this)"
									onmouseout="outBgcolor(this,'#e6f3fb')">
							</s:if>
							<s:else>
								<tr align="center" onmouseover="chageBgcolor(this)"
									onmouseout="outBgcolor(this,'')">
							</s:else>
							<td>
								${blStatus2.index+1}
							</td>
							<td>
								${procard2.markId}
							</td>
							<td>
								${procard2.selfCard}
							</td>
							<td>
								${procard2.proName}
							</td>
							<td>
								${procard2.procardStyle}
							</td>
							<td>
								${procard2.unit}
							</td>
							<td>
								${procard2.filnalCount}
							</td>
							<td>
								${procard2.filnalCount-procard2.hascount}
							</td>
							<td>
								${procard2.stuiLiaoNumber}
							</td>
							<td>
								${procard2.ytuiLiaoNumber}
							</td>
							<td>
								<a
									href="CircuitRunAction_findAduitPage.action?id=${procard2.epId}">${procard2.tuiLiaoStatus}</a>
							</td>
							<td>
								<s:if test="#procard2.epId!=null">
									<input type="button" value="审批动态"
										style="height: 25px; width: 80px;"
										onclick="shenpi(${procard2.epId})">
								</s:if>
							</td>
							</tr>
						</s:iterator>
						<tr>
							<th align="center" colspan="12">
								已退料信息
							</th>
						</tr>
						<s:iterator value="procardYtyTlList" id="procard3"
							status="blStatus3">
							<s:if test="#blStatus3.index%2==1">
								<tr align="center" bgcolor="#e6f3fb"
									onmouseover="chageBgcolor(this)"
									onmouseout="outBgcolor(this,'#e6f3fb')">
							</s:if>
							<s:else>
								<tr align="center" onmouseover="chageBgcolor(this)"
									onmouseout="outBgcolor(this,'')">
							</s:else>
							<td>
								${blStatus3.index+1}
							</td>
							<td>
								${procard3.markId}
							</td>
							<td>
								${procard3.selfCard}
							</td>
							<td>
								${procard3.proName}
							</td>
							<td>
								${procard3.procardStyle}
							</td>
							<td>
								${procard3.unit}
							</td>
							<td>
								${procard3.filnalCount}
							</td>
							<td>
								${procard3.filnalCount-procard3.hascount}
							</td>
							<td>
								${procard3.stuiLiaoNumber}
							</td>
							<td>
								${procard3.ytuiLiaoNumber}
							</td>
							<td>
								<a
									href="CircuitRunAction_findAduitPage.action?id=${procard3.epId}">${procard3.tuiLiaoStatus}</a>
							</td>
							<td>
								<s:if test="#procard3.epId!=null">
									<input type="button" value="审批动态"
										style="height: 25px; width: 80px;"
										onclick="shenpi(${procard3.epId})">
								</s:if>
							</td>
							</tr>
						</s:iterator>
					</table>
				</form>
			</div>
		</div>
		<%@include file="/util/foot.jsp"%>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
		<SCRIPT type="text/javascript">
<%--function chageAllCheck() {--%>
<%--	var checkAll = document.getElementById("checkAll");--%>
<%--	var checkboxs = document.getElementsByName("checkboxs");--%>
<%--	if (checkAll.checked == true) {--%>
<%--		for ( var i = 0; i < checkboxs.length; i++) {--%>
<%--			checkboxs[i].checked = true;--%>
<%--			$("#peiqiCount"+i).show();--%>
<%--			$("#peiqiCount"+i).removeAttr("disabled");--%>
<%--		}--%>
<%--		$("#htr1").show();--%>
<%--		$("#htr2").show();--%>
<%--	} else {--%>
<%--		for ( var i = 0; i < checkboxs.length; i++) {--%>
<%--			checkboxs[i].checked = false;--%>
<%--			$("#peiqiCount"+i).hide()--%>
<%--			$("#peiqiCount"+i).attr("disabled","disabled");--%>
<%--		}--%>
<%--		$("#htr1").hide();--%>
<%--		$("#htr2").hide();--%>
<%--	}--%>
<%----%>
<%--}--%>
<%--function chageNum() {--%>
<%--	var checkAll = document.getElementById("checkAll");--%>
<%--	var checkboxs = document.getElementsByName("checkboxs");--%>
<%--	var count = 0;--%>
<%--	for ( var i = 0; i < checkboxs.length; i++) {--%>
<%--		if (checkboxs[i].checked == false) {--%>
<%--			checkAll.checked = false;--%>
<%--			$("#peiqiCount"+i).hide()--%>
<%--			$("#peiqiCount"+i).attr("disabled","disabled");--%>
<%--		} else {--%>
<%--			$("#peiqiCount"+i).show();--%>
<%--			$("#peiqiCount"+i).removeAttr("disabled");--%>
<%--			count++;--%>
<%--		}--%>
<%--	}--%>
<%--	if(count>0){--%>
<%--		$("#htr1").show();--%>
<%--		$("#htr2").show();--%>
<%--	}else{--%>
<%--		$("#htr1").hide();--%>
<%--		$("#htr2").hide();--%>
<%--	}--%>
<%--	if (count == checkboxs.length) {--%>
<%--		checkAll.checked = true;--%>
<%--	}--%>
<%--}--%>
function appli1(id,num){
	var stknum= Number($.trim($("#stuiLiaoNumber_"+num).val()));
	var ytknum= Number($("#ytuiLiaoNumber_"+num).val());
	if(Number(stknum)<=0){
		alert("退料数量不能小于0");
		$("#stuiLiaoNumber_"+num).val(ytknum);
		return false;
	}else{
		if(Number(stknum)>Number(ytknum)){
			alert("退料数量不能大于可退料数量:"+ytknum);
			$("#stuiLiaoNumber_"+num).val(ytknum);
			return false;
		}else{
			$("#tuiliao_"+num).attr("disabled","disabled");
			//alert("申请成功"+id + "+"+stknum);
			window.location.href="procardBlAction_applicationTuiliao.action?procard.id="+id+"&procard.stuiLiaoNumber="+stknum;
		}
	}
}
function shenpi(id){
	window.location.href="CircuitRunAction_findAduitPage.action?id="+id;
}

function subtodis(obj) {
	$("#subto").attr("disabled", "disabled");
	obj.form.submit();
}
function chageAllCheck(obj) {
	var inputs = document.getElementsByTagName("input");
	for ( var i = 0; i < inputs.length; i++) {
		if (inputs[i].type == "checkbox") {
			var checkBox = inputs[i];
			if (checkBox.checked != obj.checked) {
				checkBox.checked = obj.checked;
				if (checkBox.id != "checkAll2" && checkBox.id != "checkAll") {
					//chageNum(checkBox);
					disprocessNumbers(checkBox);
				}
			}
		}
	}
}

function chageNum(obj){
	//disprocessNumbers(obj);
	var checkAll2=document.getElementById("checkAll2");
	var checkAll =document.getElementById("checkAll");
	var checkboxs=document.getElementsByName("processIds");
	var count=0;
	for(var i=0;i<checkboxs.length;i++){
		if(checkboxs[i].checked==false){
			checkAll.checked=false;
			checkAll2.checked=false;
			return;
		}else{
			count++;
		}
	}
	if(count==checkboxs.length){
		checkAll.checked=true;
		checkAll2.checked=true;
	}
}

/**
 * 改变数量是否失效
 * @param {Object} obj=多选框对象
 */
function disprocessNumbers(obj){
	var processNumbers = $(obj).parent().find("input[type=text]");
	if(obj.checked ){//如果选中
		processNumbers.attr("disabled",false);
	}else{
		processNumbers.attr("disabled",true);
	}
	
}
function checkNum(num, obj) {
	var nownum = parseFloat($(obj).val());
	var oldnum = parseFloat(num);
	if(isNaN(nownum)){
		alert("请输入数字");
		$(obj).val(oldnum);
	}
	if (nownum > oldnum) {
		alert("确认数量不能超过" + oldnum);
		$(obj).val(oldnum);
		$(obj).focus();
		$(obj).select();
	} else if (nownum <= 0) {
		alert("确认数量不能小于0!");
		$(obj).val(oldnum);
		$(obj).focus();
		$(obj).select();
	}
}
function onebi(){
	var sellIds = $(".sellIds");
	var flag =false;
	for(let i=0;i<sellIds.length;i++){
		if(sellIds[i].checked){
			flag = true;
			break;
		}
	}
	if(flag){
		$("#ok").attr("disabled","disabled");
		$("#ok1").attr("disabled","disabled");
	}else{
		alert("请选择对应的供应商和领料批次!~")
	}
	
}
function changChecked(obj,index){
	var tr = $(obj).parent().parent();
	if(obj.checked){
		$.ajax({
		type:'POST',
		dataType:'json',
		url:'ProcardAction!findLingLiaoLotById.action',
		data:{id:obj.value}
		}).then(function(res){
			if(res!=null){
				var newtr ='<tr class="bg-info"><td  colspan="30"><ul class="list-group">';
				res.forEach((v,i)=>{
					if( typeof(v.sellId)!= 'undefined'){
						newtr+='<li class="list-group-item"><input type="checkbox" value='+v.sellId+' onchange="disprocessNumbers(this)" class = "sellIds"'
							+' name="procardList['+index+'].sellList['+i+'].sellId">&nbsp;&nbsp;' 
							+'<labe><b>供应商:</b></labe><labe>'+v.sellSupplier+'</labe>&nbsp;&nbsp;'
							+'<labe><b>批次:</b></labe><labe>'+v.sellLot+'</labe>&nbsp;&nbsp;'
							+'<labe><b>出库数量:</b></labe><input type="text" value='+v.sellCount+' name="procardList['+index+'].sellList['+i+'].tksellCount"' +
							' disabled="disabled" style="width: 60px;" onchange="checkNum('+v.sellCount+',this)">';
					}
					
				})
				newtr+="</ul></td></tr>";
				$(tr).after(newtr);
			}
		})
	}else{
			$($('.table tr')[tr.index()+1]).remove();
	}
	
}

function changCheckedAll(obj){
	var procardIds =document.getElementsByName("procardIds");
	console.log(procardIds.length);
	if(procardIds!=null &&procardIds.length>0){
		for(var i=0;i<procardIds.length;i++){
			changChecked(procardIds[i]);
		}
	}
}

</SCRIPT>
	</body>
</html>
