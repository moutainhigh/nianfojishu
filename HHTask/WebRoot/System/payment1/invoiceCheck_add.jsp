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
		<style type="text/css">
.tr {
	height: 50px;
}

.ant-input-lg {
	padding: 6px 7px;
	height: 32px;
}

.ant-input {
	position: relative;
	display: inline-block;
	padding: 4px 7px;
	width: 100%;
	width: 280px;
	height: 28px;
	font-size: 12px;
	line-height: 1.5;
	color: rgba(0, 0, 0, 0.65);
	background-color: #fff;
	background-image: none;
	border: 1px solid #d9d9d9;
	border-radius: 4px;
	transition: all .3s;
}

.ant-form-explain,.ant-form-extra {
	color: #f04134;
	line-height: 1.5;
	display: none;
}

.has-error .ant-form-explain,.has-error .ant-form-split {
	color: #f04134;
}

.has-success.has-feedback:after {
	content: '\E630';
	color: #00a854;
}

.ant-btn-lg {
	padding: 0 15px;
	font-size: 14px;
	border-radius: 4px;
	height: 32px;
}

.ant-btn,.ant-btn:active,.ant-btn:focus {
	outline: 0;
}

.ant-btn {
	display: inline-block;
	margin-bottom: 0;
	font-weight: 500;
	text-align: center;
	touch-action: manipulation;
	cursor: pointer;
	background-image: none;
	border: 1px solid transparent;
	white-space: nowrap;
	line-height: 1.15;
	padding: 0 15px;
	font-size: 12px;
	border-radius: 4px;
	height: 28px;
	user-select: none;
	transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
	position: relative;
	color: rgba(0, 0, 0, 0.65);
	background-color: #fff;
	border-color: #d9d9d9;
}

.ant-btn-primary {
	border-color: #fff !important;
}

.ant-btn-primary {
	background: #2397CA !important;
}
<%--input::-webkit-input-placeholder{--%>
<%--    color:red;--%>
<%--}--%>
<%--input::-webkit-input-placeholder{--%>
<%--    color:red;--%>
<%--}--%>
<%--input::-webkit-input-placeholder{--%>
<%--    color:green;--%>
<%--}--%>
input::-moz-placeholder{   /* Mozilla Firefox 19+ */
    color:red;
}
input:-moz-placeholder{    /* Mozilla Firefox 4 to 18 */
    color:red;
}
input:-ms-input-placeholder{  /* Internet Explorer 10-11 */ 
    color:red;
}
</style>
		<%@include file="/util/sonHead.jsp"%>
	</head>
	<body>
		<%@include file="/util/sonTop.jsp"%>
		<div id="gongneng" style="width: 100%; margin-top: 10px;">
			<div id="xitong"
				style="width: 100%; font-weight: bold; height: 50px;" align="left">
				<div
					style="float: left; width: 50%; padding-left: 30px; padding-top: 5px;"
					align="left">
				</div>
				<div style="float: left; width: 45%; padding-top: 5px;"
					align="right">
					<a href="" style="color: rgb(79, 77, 77)"><br /> </a>
				</div>
			</div>

			<div align="center">
				<h2>
<%--					<font size="5px;">增值税发票录入</font><font size="4px;"><a href="FundApplyAction_toAddInvoceQuota.action">定额发票录入</a> </font>--%>
					<input type="button" value="增值税发票" style="background-color: gary; color: #000; width: 100px; height: 25px;">
					<input onclick="location.href='FundApplyAction_toAddInvoceQuota.action'" type="button" value="定额发票" style="background-color: rgb(52, 76, 124); color: #fff; width: 100px; height: 25px;">
					<input onclick="location.href='FundApplyAction_toAddInvoceFile.action'"  type="button" value="电子发票" style="background-color: rgb(52, 76, 124); color: #fff; width: 100px; height: 25px;">
					<br />
					<s:if test="successMessage!=null">
						<font color="red">${successMessage}</font>
					</s:if>
				</h2>
				<br/>
				<form action="FundApplyAction_addInvoce.action" method="post"
					onsubmit="return validate()">
					<table><tr><th colspan="4">增值税发票录入</th></tr>
						<tr class="tr">
							<td align="right">
								发票代码：
								<input type="hidden" name="pagestatus" value="code"/>
							</td>
							<td align="left">
								<input name="invoiceCheckRecording.fpdm" placeholder="请输入发票代码"
									class="ant-input ant-input-lg" id="fpdm" maxlength="12"
									onblur="jianyanfpdm()" />
								<div class="ant-form-explain" id="fa1">
									<font size="1px;">发票代码错误</font>
								</div>
								<div class="ant-form-explain" id="fa2">
									<font size="1px;">发票代码不能为空</font>
								</div>
							</td>
						</tr>
						<tr class="tr">
							<td align="right">
								发票号码：
							</td>
							<td align="left">
								<input name="invoiceCheckRecording.fphm" placeholder="请输入8位发票号码"
									class="ant-input ant-input-lg" maxlength="8" id="fphm"
									onblur="jianyanfphm()" />
								<div class="ant-form-explain" id="fa3">
									<font size="1px;">发票号码只能是8位的数字</font>
								</div>
								<div class="ant-form-explain" id="fa4">
									<font size="1px;">发票号码不能为空</font>
								</div>
							</td>
						</tr>
						<tr class="tr">
							<td align="right">
								开票日期：
							</td>
							<td align="left">
								<input name="invoiceCheckRecording.kprq" placeholder="请选择开票日期"
									class="ant-input ant-input-lg" id="kprq" onblur="bijiaoDate()"
									onclick="WdatePicker({dateFmt:'yyyyMMdd',skin:'whyGreen'})" />
							</td>
						</tr>
						<tr class="tr" id="je6">
							<td align="right">
								税前金额：
							</td>
							<td align="left">
								<input name="invoiceCheckRecording.je" placeholder="增值税专用发票，请输入税前金额"
								style="font-weight: bold;"
									class="ant-input ant-input-lg" id="je" onblur="mustBeNumber('je')"/>
<%--									<font size="1px;">专用发票，请输入税前金额</font>--%>
							</td>
						</tr>
						<tr class="tr" style="display: none;" id="jym6">
							<td align="right">
								校验码后6位：
							</td>
							<td align="left">
								<input name="invoiceCheckRecording.jym" placeholder="其他发票，请输入校验码后6位"
								style="font-weight: red;font-weight: bolder;"
									class="ant-input ant-input-lg" id="jym" maxlength="6" onblur="mustBeNumber('jym')"/>
<%--									<font size="1px;">其他发票请输入校验码后6位</font>--%>
							</td>
						</tr>
						<tr>
							<td align="center" colspan="2">
								<input type="submit" value="提交"
									class="ant-btn ant-btn-primary ant-btn-lg"
									style="width: 56px; height: 28px;" />
							</td>
						</tr>
					</table>
				</form>
			</div>
		</div>
		<%@include file="/util/foot.jsp"%>
		<iframe id="showProcess" src="" marginwidth="0" marginheight="0"
			hspace="0" vspace="0" frameborder="0" scrolling="yes"
			style="width: 98%; height: 400px; margin: 0px; padding: 0px;"></iframe>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
		<script type="text/javascript">
function validate() {
	if (!validateText("fpdm", "发票代码")) {
		return false;
	}
	if (!validateText("fphm", "发票号码")) {
		return false;
	}
	if (!validateText("kprq", "开票日期")) {
		return false;
	}
	if(II == 1){
		if (!validateText("jym", "校验码后6位")) {
			return false;
		}
	}else{
		if (!validateText("je", "税前金额")) {
			return false;
		}
	}
}

function validateText(id, textname) {
	var textValue = $.trim($("#" + id).val());
	if (textValue == null || textValue == "" || textValue == "0") {
		alert(textname + "不能为空");
		return false;
	}
	return true;
}

var II = 0;

function jianyanfpdm(){
	var fpdm = $.trim($("#fpdm").val());
	if(fpdm!=null&&fpdm!=''){
		$("#fa2").hide();
		$("#jym6").hide();
		$("#je6").show();
		II = 0;
		if(isNaN(fpdm)){
			$("#fa1").show();
		}else{
			if(fpdm.length==10){
				var fpdm8 = fpdm.substring(7,8);
				if(fpdm8==1||fpdm8==2||fpdm8==5||fpdm8==7){
					$("#fa1").hide();
				}else if(fpdm8==3||fpdm8==6){
					$("#fa1").hide();
					$("#jym6").show();
					$("#je6").hide();
					II = 1;
				}else{
					$("#fa1").show();
				}
			}else if(fpdm.length==12){
				var fpdm1 = fpdm.substring(0,1);
				var fpdm11 = fpdm.substring(10,12);
				if(fpdm1==0&&(fpdm11=='04'||fpdm11=='05'||fpdm11=='06'||fpdm11=='07'||fpdm11=='11'||fpdm11=='12')){
					$("#fa1").hide();
					$("#jym6").show();
					$("#je6").hide();
					II = 1;
				}else{
					$("#fa1").show();
				}
			}else{
				$("#fa1").show();
			}
		}
	}else{
		$("#fa1").hide();
		$("#fa2").show();
	}
}
function jianyanfphm(){
	var fpdm = $.trim($("#fphm").val());
	if(fpdm!=null&&fpdm!=''){
		$("#fa4").hide();
		if(isNaN(fpdm)){
			$("#fa3").show();
		}else{
			if(fpdm.length==8){
				$("#fa3").hide();
			}else{
				$("#fa3").show();
			}
		}
	}else{
		$("#fa3").hide();
		$("#fa4").show();
	}
}

function bijiaoDate(){
	var kprq = $.trim($("#kprq").val());
	var Year = 0;
	var Month = 0;
	var Day = 0;
	var day = new Date();
	//初始化时间
	Year = day.getFullYear();
	Month = day.getMonth()+1;
	Day = day.getDate();
	var CurrentDate = "";
	CurrentDate += Year;
	if (Month >= 10 ){
		CurrentDate += Month;
	}else{
		CurrentDate += "0" + Month;
	}
	if (Day >= 10){
		CurrentDate += Day ;
	}else{
		CurrentDate += "0" + Day ;
	}
	if(kprq>CurrentDate){
		alert("开票日期不可大于"+CurrentDate);
		$("#kprq").val(CurrentDate);
	}
}

function date(){
	if(controldate==""){
		alert('日期不能为空');
	return false;}
	else{var day = new Date();
	var Year = 0;
	var Month = 0;
	var Day = 0;
	var CurrentDate = "";
	//初始化时间
	Year = day.getFullYear();
	Month = day.getMonth()+1;
	Day = day.getDate();
	CurrentDate += Year + "-";
	if (Month >= 10 ){
		CurrentDate += Month + "-";
	}else{
		CurrentDate += "0" + Month + "-";
	}
	if (Day >= 10 ){
		CurrentDate += Day ;}
	else{
		CurrentDate += "0" + Day ;
	} //alert(CurrentDate);//当前日期//http://www.unitymanual.com/sitemap.xmlvar startDate = new Date(CurrentDate.replace("-",",")).getTime() ;var endDate = new Date(controldate.replace("-",",")).getTime() ; if( startDate > endDate ){alert('选择日期不能小于当前日期！');document.getElementById("sendDate").focus();return false;}else{return true;}}} 
	}
}

function add() {
	var url = "<%=request.getContextPath()%>/AccessEquipmentAction_toAddResAccessJi.action";
	$("#showProcess").attr("src", url);
}
<%--$("#cabOpenOrder").keyup(function() {--%>
<%--	var tmptxt = $(this).val();--%>
<%--	$(this).val(tmptxt.replace(/\D|^0/g, ''));--%>
<%--})--%>
</script>
	</body>
</html>