<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<HTML> 
<HEAD> 
<TITLE>WMI Scripting HTML</TITLE> 
<META http-equiv=Content-Type content="text/html; charset=gb2312"> 
<script type="text/javascript" src="getIpAndMac.js"></script> 
<SCRIPT language=JScript 
event="OnCompleted(hResult,pErrorObject, pAsyncContext)" for="foo"> 
document.forms[0].txtMACAddr.value = unescape(MACAddr); 
document.forms[0].txtIPAddr.value = unescape(IPAddr); 
document.forms[0].txtDNSName.value = unescape(sDNSName); 
</SCRIPT> 
<SCRIPT language=JScript event=OnObjectReady(objObject,objAsyncContext) 
for=foo> 
if (objObject.IPEnabled != null && objObject.IPEnabled != "undefined" 
&& objObject.IPEnabled == true) { 
if (objObject.MACAddress != null && objObject.MACAddress != "undefined") 
MACAddr = objObject.MACAddress; 
if (objObject.IPEnabled && objObject.IPAddress(0) != null 
&& objObject.IPAddress(0) != "undefined") 
IPAddr = objObject.IPAddress(0); 
if (objObject.DNSHostName != null 
&& objObject.DNSHostName != "undefined") 
sDNSName = objObject.DNSHostName; 
} 
</SCRIPT> 
</HEAD> 
<BODY> 
<OBJECT id=locator classid=CLSID:76A64158-CB41-11D1-8B02-00600806D9B6 VIEWASTEXT></OBJECT> 
<OBJECT id=foo classid=CLSID:75718C9A-F029-11d1-A1AC-00C04FB6C223></OBJECT> 
<SCRIPT language=JScript> 
var service = locator.ConnectServer(); 
var MACAddr; 
var IPAddr; 
var DomainAddr; 
var sDNSName; 
service.Security_.ImpersonationLevel = 3; 
service.InstancesOfAsync(foo, 'Win32_NetworkAdapterConfiguration'); 
</SCRIPT> 
<FORM id="formfoo" action="" method=""><INPUT 
value="" name="txtMACAddr"> <INPUT value="" name="txtIPAddr"> 
<INPUT value="" name="txtDNSName" onclick="getIpAndMacAddress()"></FORM> 
</BODY> 
</HTML>