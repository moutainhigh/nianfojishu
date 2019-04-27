<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE HTML>
<html>
<head>
  <meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7; IE=EDGE"/>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <title>项目进度甘特图</title>

  <link rel=stylesheet href="${pageContext.request.contextPath}/javascript/gantt/platform.css" type="text/css">
  <link rel=stylesheet href="${pageContext.request.contextPath}/javascript/gantt/libs/dateField/jquery.dateField.css" type="text/css">

  <link rel=stylesheet href="${pageContext.request.contextPath}/javascript/gantt/gantt.css" type="text/css">
<!--  <link rel=stylesheet href="ganttPrint.css" type="text/css" media="print">-->

 <script src="${pageContext.request.contextPath}/javascript/calendar/jquery/jquery-1.8.3.js"></script>
<script src="${pageContext.request.contextPath}/javascript/fullcalendar-3.7.0/lib/jquery-ui.min.js"></script>

  <script src="${pageContext.request.contextPath}/javascript/gantt/libs/jquery.livequery.min.js"></script>
  <script src="${pageContext.request.contextPath}/javascript/gantt/libs/jquery.timers.js"></script>
  <script src="${pageContext.request.contextPath}/javascript/gantt/libs/platform.js"></script>
  <script src="${pageContext.request.contextPath}/javascript/gantt/libs/date.js"></script>
  <script src="${pageContext.request.contextPath}/javascript/gantt/libs/i18nJs.js"></script>
  <script src="${pageContext.request.contextPath}/javascript/gantt/libs/dateField/jquery.dateField.js"></script>
  <script src="${pageContext.request.contextPath}/javascript/gantt/libs/JST/jquery.JST.js"></script>
  <script src="${pageContext.request.contextPath}/javascript/DatePicker/WdatePicker.js" type="text/javascript"></script>
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/javascript/DatePicker/skin/WdatePicker.css">
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/javascript/gantt/libs/jquery.svg.css">
  <script type="text/javascript" src="${pageContext.request.contextPath}/javascript/gantt/libs/jquery.svg.min.js"></script>

  <!--In case of jquery 1.7-->
  <!--<script type="text/javascript" src="libs/jquery.svgdom.pack.js"></script>-->

  <!--In case of jquery 1.8-->
  <script type="text/javascript" src="${pageContext.request.contextPath}/javascript/gantt/libs/jquery.svgdom.1.8.js"></script>


  <script src="${pageContext.request.contextPath}/javascript/gantt/ganttUtilities.js"></script>
  <script src="${pageContext.request.contextPath}/javascript/gantt/ganttTask.js"></script>
  <script src="${pageContext.request.contextPath}/javascript/gantt/ganttDrawerSVG.js"></script>
  <!--<script src="ganttDrawer.js"></script>-->
  <script src="${pageContext.request.contextPath}/javascript/gantt/ganttGridEditor.js"></script>
  <script src="${pageContext.request.contextPath}/javascript/gantt/ganttMaster.js"></script>  
</head>
<body style="background-color: #fff;">



<div id="workSpace" style="padding:0px; overflow-y:auto; overflow-x:hidden;border:1px solid #e5e5e5;position:relative;margin:0 5px"></div>

<div id="taZone" style="display:none;" class="noprint">
   <textarea rows="8" cols="150" id="ta">
     {"tasks":[
     {"id":-1,"name":"Gantt editor","code":"1","level":0,"status":"STATUS_ACTIVE","canWrite":true,"start":1396994400000,"duration":21,"end":1399672799999,"startIsMilestone":true,"endIsMilestone":false,"collapsed":false,"assigs":[],"hasChild":true}
     ,{"id":-2,"name":"coding","code":"1.1","level":1,"status":"STATUS_ACTIVE","canWrite":true,"start":1396994400000,"duration":10,"end":1398203999999,"startIsMilestone":false,"endIsMilestone":false,"collapsed":false,"assigs":[],"description":"","progress":0,"hasChild":true}
     ,{"id":-3,"name":"gantt part","code":"","level":2,"status":"STATUS_ACTIVE","canWrite":true,"start":1396994400000,"duration":2,"end":1397167199999,"startIsMilestone":false,"endIsMilestone":false,"collapsed":false,"assigs":[],"depends":"","hasChild":false}
     ,{"id":-4,"name":"editor part","code":"","level":2,"status":"STATUS_SUSPENDED","canWrite":true,"start":1397167200000,"duration":4,"end":1397685599999,"startIsMilestone":false,"endIsMilestone":false,"collapsed":false,"assigs":[],"depends":"3","hasChild":false}
     ,{"id":-5,"name":"testing","code":"","level":1,"status":"STATUS_SUSPENDED","canWrite":true,"start":1398981600000,"duration":6,"end":1399672799999,"startIsMilestone":false,"endIsMilestone":false,"collapsed":false,"assigs":[],"depends":"2:5","description":"","progress":0,"hasChild":true}
     ,{"id":-6,"name":"test on safari","code":"","level":2,"status":"STATUS_SUSPENDED","canWrite":true,"start":1398981600000,"duration":2,"end":1399327199999,"startIsMilestone":false,"endIsMilestone":false,"collapsed":false,"assigs":[],"depends":"","hasChild":false}
     ,{"id":-7,"name":"test on ie","code":"","level":2,"status":"STATUS_SUSPENDED","canWrite":true,"start":1399327200000,"duration":3,"end":1399586399999,"startIsMilestone":false,"endIsMilestone":false,"collapsed":false,"assigs":[],"depends":"6","hasChild":false}
     ,{"id":-8,"name":"test on chrome","code":"","level":2,"status":"STATUS_SUSPENDED","canWrite":true,"start":1399327200000,"duration":2,"end":1399499999999,"startIsMilestone":false,"endIsMilestone":false,"collapsed":false,"assigs":[],"depends":"6","hasChild":false}
     ],"selectedRow":0,"canWrite":true,"canWriteOnParent":true}
   </textarea>

  <button onclick="loadGanttFromServer();">load</button>
</div>
<style>
   
    
    </style>
<style>
  .resEdit {
    padding: 15px;
  }

  .resLine {
    width: 95%;
    padding: 3px;
    margin: 5px;
    border: 1px solid #d0d0d0;
  }

  body {
    overflow: hidden;
  }

  .ganttButtonBar h1{
    color: #000000;
    font-weight: bold;
    font-size: 28px;
    margin-left: 10px;
  }

</style>



<script type="text/javascript">

var ge;  //this is the hugly but very friendly global var for the gantt editor
$(function() {

  //load templates
  //$("#ganttemplates").loadTemplates();

  // here starts gantt initialization
  ge = new GanttMaster();
  var workSpace = $("#workSpace");
  workSpace.css({width:$(window).width() - 20,height:$(window).height() - 100});
  ge.init(workSpace);

  //inject some buttons (for this demo only)工具栏
  //$(".ganttButtonBar div").append("<button onclick='clearGantt();' class='button'>清空</button>")
  //        .append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")
  //        .append("<button onclick='getFile();' class='button'>出口</button>");
  
  $(".ganttButtonBar div").addClass('buttons');
  //overwrite with localized ones
  loadI18n();

  //simulate a data load from a server.
  loadGanttFromServer();


  //fill default Teamwork roles if any
  if (!ge.roles || ge.roles.length == 0) {
    setRoles();
  }

  //fill default Resources roles if any
  if (!ge.resources || ge.resources.length == 0) {
    setResource();
  }


  /*/debug time scale
  $(".splitBox2").mousemove(function(e){
    var x=e.clientX-$(this).offset().left;
    var mill=Math.round(x/(ge.gantt.fx) + ge.gantt.startMillis)
    $("#ndo").html(x+" "+new Date(mill))
  });*/

  $(window).resize(function(){
    workSpace.css({width:$(window).width() - 1,height:$(window).height() - workSpace.position().top});
    workSpace.trigger("resize.gantt");
  }).oneTime(150,"resize",function(){$(this).trigger("resize")});

});


function loadGanttFromServer(taskId, callback) {

  //this is a simulation: load data from the local storage if you have already played with the demo or a textarea with starting demo data
  loadFromLocalStorage();

  //this is the real implementation
  /*
  //var taskId = $("#taskSelector").val();
  var prof = new Profiler("loadServerSide");
  prof.reset();

  $.getJSON("ganttAjaxController.jsp", {CM:"LOADPROJECT",taskId:taskId}, function(response) {
    //console.debug(response);
    if (response.ok) {
      prof.stop();

      ge.loadProject(response.project);
      ge.checkpoint(); //empty the undo stack

      if (typeof(callback)=="function") {
        callback(response);
      }
    } else {
      jsonErrorHandling(response);
    }
  });
  */
}


function saveGanttOnServer() {
  if(!ge.canWrite)
    return;


  //this is a simulation: save data to the local storage or to the textarea
  saveInLocalStorage();


}


//-------------------------------------------  Create some demo data ------------------------------------------------------
function setRoles() {
  ge.roles = [
    {
      id:"tmp_1",
      name:"Project Manager"
    },
    {
      id:"tmp_2",
      name:"Worker"
    },
    {
      id:"tmp_3",
      name:"Stakeholder/Customer"
    }
  ];
}

function setResource() {
  var res = [];
  for (var i = 1; i <= 10; i++) {
    res.push({id:"tmp_" + i,name:"Resource " + i});
  }
  ge.resources = res;
}


function editResources(){

}

function clearGantt() {
  ge.reset();
}

function loadI18n() {
  GanttMaster.messages = {
    "CANNOT_WRITE":                  "不能写字",
    "CHANGE_OUT_OF_SCOPE":"变化的范围",
    "START_IS_MILESTONE":"开始是里程碑",
    "END_IS_MILESTONE":"结束时里程碑",
    "TASK_HAS_CONSTRAINTS":"项目有时间限制",
    "GANTT_ERROR_DEPENDS_ON_OPEN_TASK":"甘特图打开错误",
    "GANTT_ERROR_DESCENDANT_OF_CLOSED_TASK":"甘特图关闭错误",
    "TASK_HAS_EXTERNAL_DEPS":"外部DEPS任务",
    "GANTT_ERROR_LOADING_DATA_TASK_REMOVED":"甘特图加载数据被删除",
    "ERROR_SETTING_DATES":"设置日期错误",
    "CIRCULAR_REFERENCE":"参考循环",
    "CANNOT_DEPENDS_ON_ANCESTORS":"CANNOT_DEPENDS_ON_ANCESTORS",
    "CANNOT_DEPENDS_ON_DESCENDANTS":"CANNOT_DEPENDS_ON_DESCENDANTS",
    "INVALID_DATE_FORMAT":"INVALID_DATE_FORMAT",
    "TASK_MOVE_INCONSISTENT_LEVEL":"TASK_MOVE_INCONSISTENT_LEVEL",

    "GANTT_QUARTER_SHORT":"trim.",
    "GANTT_SEMESTER_SHORT":"sem."
  };
}


//-------------------------------------------  LOCAL STORAGE MANAGEMENT (for this demo only) ------------------------------------------------------
Storage.prototype.setObject = function(key, value) {
  this.setItem(key, JSON.stringify(value));
};


Storage.prototype.getObject = function(key) {
  return this.getItem(key) && JSON.parse(this.getItem(key));
};

//加载数据
function loadFromLocalStorage() {
  var ret;
  /*if (localStorage) {
    if (localStorage.getObject("teamworkGantDemo")) {
      
    }
  } else {}*/
  //ret = localStorage.getObject("teamworkGantDemo");
    //$("#taZone").show();
  
  if (!ret || !ret.tasks || ret.tasks.length == 0){
    //ret = JSON.parse($("#ta").val());
	$.ajax({
		url : 'projectPoolAction_getSubListById.action',
		type : 'post',
		data : {
			rootIdStr : '${id}'
		},
		dataType : 'json',
		async : false,
		success : function(data) {
			var append = '';
			if(data!=null ){
				for(var i=0;i<data.length;i++){
					if(i!=0){
						append+=',';
					}
					var status = "";//项目状态
					var proStatus = data[i].status;
					if(proStatus="完成"){
						status="STATUS_ACTIVE";
					}else{
						status="";
						//STATUS_ACTIVE积极的
						//STATUS_DONE空
						//STATUS_FAILED失败的
						//STATUS_SUSPENDED暂停的
						//STATUS_UNDEFINED不明确的，未定义的
					}
					var starttime = data[i].startTime.replace(new RegExp("-","gm"),"/");
    				var start= (new Date(starttime)).getTime(); //得到毫秒数
    				var endtime = data[i].reTime.replace(new RegExp("-","gm"),"/");
					var end =(new Date(endtime)).getTime(); //得到毫秒数
					
					var duration=(end-start)/(60*60*24*1000);//持续时间/工期
					var model = data[i].model;//关联模型
					if(model=="null" || model==null){
						model="";
					}
					var depends = data[i].depends;//前置任务
					var gradeStore = data[i].gradeStore;//分数
					var startIsMilestone = data[i].startIsMilestone;
					if(startIsMilestone==null || startIsMilestone=="null" || startIsMilestone=="false"){
						startIsMilestone="";
					}
					var endIsMilestone = data[i].endIsMilestone;
					if(endIsMilestone==null || endIsMilestone=="null" || endIsMilestone=="false"){
						endIsMilestone="";
					}
					var description = data[i].remark;
					var inPeople = data[i].principals;//负责人
					var fatherId = data[i].fatherId;
					//"assigs":[],"hasChild":true,"canWrite":true,,"milestone":"'+milestone+'"
					append+='{"id":'+data[i].id+',"name":"'+data[i].proName+'","code":"'+data[i].proNum+'",' +
					'"level":'+data[i].belongLayer+',"status":"'+status+'","start":'+start+
					',"duration":'+duration+',"end":'+end+',"startIsMilestone":"'+startIsMilestone+'","endIsMilestone":"'+endIsMilestone+'",' +
					'"collapsed":false,"depends":"'+depends+'","model":"'+model+'",' +
					'"gradeStore":"'+gradeStore+'","description":"'+description+'","inPeople":"'+inPeople+'","fatherId":"'+fatherId+'"}';
				}
			}
			var str = '{"tasks":['+append+'],"selectedRow":0,"canWrite":false,"canWriteOnParent":false}';
			ret = JSON.parse(str);
		},
		error : function() {
			alert("服务器异常!");
		}
	});
	//-----------------------------

    //actualiza data
<%--    var offset=new Date().getTime()-ret.tasks[0].start;--%>
<%--    for (var i=0;i<ret.tasks.length;i++)--%>
<%--      ret.tasks[i].start=ret.tasks[i].start+offset;--%>


  }
  ge.loadProject(ret);
  ge.checkpoint(); //empty the undo stack
}


function saveInLocalStorage() {
}

//-------------------------------------------  Open a black popup for managing resources. This is only an axample of implementation (usually resources come from server) ------------------------------------------------------
Date.prototype.toLocaleString = function() {
	var year = this.getFullYear();
	var month = (this.getMonth() + 1);
	var days = this.getDate();
	if(month<10){
		month = "0"+month;
	}
	if(days<10){
		days="0"+days;
	}
      return  year+ "-" + month + "-" +days ;
};

function editResources(){

  //make resource editor
  var resourceEditor = $.JST.createFromTemplate({}, "RESOURCE_EDITOR");
  var resTbl=resourceEditor.find("#resourcesTable");

  for (var i=0;i<ge.resources.length;i++){
    var res=ge.resources[i];
    resTbl.append($.JST.createFromTemplate(res, "RESOURCE_ROW"))
  }


  //bind add resource
  resourceEditor.find("#addResource").click(function(){
    resTbl.append($.JST.createFromTemplate({id:"new",name:"resource"}, "RESOURCE_ROW"))
  });

  //bind save event
  resourceEditor.find("#resSaveButton").click(function(){
    var newRes=[];
    //find for deleted res
    for (var i=0;i<ge.resources.length;i++){
      var res=ge.resources[i];
      var row = resourceEditor.find("[resId="+res.id+"]");
      if (row.size()>0){
        //if still there save it
        var name = row.find("input[name]").val();
        if (name && name!="")
          res.name=name;
        newRes.push(res);
      } else {
        //remove assignments
        for (var j=0;j<ge.tasks.length;j++){
          var task=ge.tasks[j];
          var newAss=[];
          for (var k=0;k<task.assigs.length;k++){
            var ass=task.assigs[k];
            if (ass.resourceId!=res.id)
              newAss.push(ass);
          }
          task.assigs=newAss;
        }
      }
    }

    //loop on new rows
    resourceEditor.find("[resId=new]").each(function(){
      var row = $(this);
      var name = row.find("input[name]").val();
      if (name && name!="")
        newRes.push (new Resource("tmp_"+new Date().getTime(),name));
    });

    ge.resources=newRes;

    closeBlackPopup();
    ge.redraw();
  });


  //var ndo = createBlackPage(400, 500).append(resourceEditor);
}


</script>


<div id="gantEditorTemplates" style="display:none;">
  <div class="__template__" type="GANTBUTTONS"><!--
  <div class="ganttButtonBar noprint">
    
    <div class="buttons">
    <button onclick="$('#workSpace').trigger('undo.gantt');" class="button textual" title="撤销"><span class="teamworkIcon">&#39;</span></button>
    <button onclick="$('#workSpace').trigger('redo.gantt');" class="button textual" title="恢复"><span class="teamworkIcon">&middot;</span></button>
    <span class="ganttButtonSeparator"></span>
    <button onclick="$('#workSpace').trigger('addAboveCurrentTask.gantt');" class="button textual" title="上方插入"><span class="teamworkIcon">l</span></button>
    <button onclick="$('#workSpace').trigger('addBelowCurrentTask.gantt');" class="button textual" title="下方插入"><span class="teamworkIcon">X</span></button>
    <span class="ganttButtonSeparator"></span>
    <button onclick="$('#workSpace').trigger('indentCurrentTask.gantt');" class="button textual" title="降级"><span class="teamworkIcon">.</span></button>
    <button onclick="$('#workSpace').trigger('outdentCurrentTask.gantt');" class="button textual" title="升级"><span class="teamworkIcon">:</span></button>
    <span class="ganttButtonSeparator"></span>
    <button onclick="$('#workSpace').trigger('moveUpCurrentTask.gantt');" class="button textual" title="上移"><span class="teamworkIcon">k</span></button>
    <button onclick="$('#workSpace').trigger('moveDownCurrentTask.gantt');" class="button textual" title="下移"><span class="teamworkIcon">j</span></button>
    <span class="ganttButtonSeparator"></span>
    <button onclick="$('#workSpace').trigger('zoomMinus.gantt');" class="button textual" title="缩小"><span class="teamworkIcon">)</span></button>
    <button onclick="$('#workSpace').trigger('zoomPlus.gantt');" class="button textual" title="放大"><span class="teamworkIcon">(</span></button>
    <span class="ganttButtonSeparator"></span>
    <button onclick="$('#workSpace').trigger('deleteCurrentTask.gantt');" class="button textual" title="删除"><span class="teamworkIcon">&cent;</span></button>
    <span class="ganttButtonSeparator"></span>
    <button onclick="print();" class="button textual" title="打印"><span class="teamworkIcon">p</span></button>
    <span class="ganttButtonSeparator"></span>
<%--    <button onclick="ge.gantt.showCriticalPath=!ge.gantt.showCriticalPath; ge.redraw();" class="button textual" title="关键路径"><span class="teamworkIcon">&pound;</span></button>--%>
<%--    <span class="ganttButtonSeparator"></span>--%>
<%--    <button onclick="editResources();" class="button textual" title="编辑资源"><span class="teamworkIcon">M</span></button>--%>
<%--      &nbsp; &nbsp; &nbsp; &nbsp;--%>
<%--      <button onclick="saveGanttOnServer();" class="button first big" title="保存">保存</button>--%>
    </div></div>
  --></div>

  <div class="__template__" type="TASKSEDITHEAD"><!--
  <table class="gdfTable" cellspacing="0" cellpadding="0">
    <thead>
    <tr style="height:40px">
      <th class="gdfColHeader" style="width:35px;"></th>
      <th class="gdfColHeader" style="width:25px;"></th>
      <th class="gdfColHeader gdfResizable" style="width:180px;">任务编码</th>

      <th class="gdfColHeader gdfResizable" style="width:200px;">任务名称</th>
      <th class="gdfColHeader gdfResizable" style="width:80px;">开始时间</th>
      <th class="gdfColHeader gdfResizable" style="width:80px;">完成时间</th>
      <th class="gdfColHeader gdfResizable" style="width:50px;">工期</th>
      <th class="gdfColHeader gdfResizable" style="width:60px;">前置任务</th>
      <th class="gdfColHeader gdfResizable" style="width:30px;">开始是里程碑</th>
	  <th class="gdfColHeader gdfResizable" style="width:30px;">结束是里程碑</th>
	  <th class="gdfColHeader gdfResizable" style="width:100px;">关联模型</th>
	  <th class="gdfColHeader gdfResizable" style="width:100px;">负责人</th>
	  <th class="gdfColHeader gdfResizable" style="width:50px;">分数</th>
    </tr>
    </thead>
  </table>
  --></div>

  <div class="__template__" type="TASKROW"><!--
  <tr taskId="(#=obj.id#)" class="taskEditRow" level="(#=level#)">
    <th class="gdfCell " align="center" style="cursor:pointer;"><span class="taskRowIndex">(#=obj.getRow()+1#)</span> </th>
    <td class="gdfCell noClip" align="center"><div class="taskStatus cvcColorSquare" status="(#=obj.status#)"></div></td>
    <td class="gdfCell"><input type="text" name="code" value="不填自动生成"></td>
    <td class="gdfCell indentCell" style="padding-left:(#=obj.level*10#)px;">
      <div class="(#=obj.isParent()?'exp-controller expcoll exp':'exp-controller'#)" align="center"></div>
      <input type="text" name="name" value="(#=obj.name#)">
    </td>
<%--onClick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'whyGreen'})"--%>
    <td class="gdfCell"><input type="text" name="start" value="(#=obj.start#)"  class="date"></td>
    <td class="gdfCell"><input type="text" name="end" value="(#=obj.end#)"  class="date"></td>
    <td class="gdfCell"><input type="text" name="duration" value="(#=obj.duration#)" style="text-align:right" ></td>
    <td class="gdfCell"><input type="text" name="depends" value="(#=obj.depends#)" (#=obj.hasExternalDep?"readonly":""#)></td>
    <td class="gdfCell"><input type="text" name="startIsMilestone" value="(#=obj.startIsMilestone#)"></td>
    <td class="gdfCell"><input type="text" name="endIsMilestone" value="(#=obj.endIsMilestone#)"></td>
	<td class="gdfCell"><input type="text" name="model" value="(#=obj.model#)"></td>
	<td class="gdfCell"><input type="text" name="inPeople" value="(#=obj.inPeople#)"></td>
	<td class="gdfCell"><input type="text" name="gradeStore" value="(#=obj.gradeStore#)" style="text-align:right"></td>
  </tr>
  --></div>

  <div class="__template__" type="TASKEMPTYROW"><!--
  <tr class="taskEditRow emptyRow" >
    <th class="gdfCell" align="right"></th>
    <td class="gdfCell noClip" align="center"></td>
    <td class="gdfCell"></td>
    <td class="gdfCell"></td>
    <td class="gdfCell"></td>
    <td class="gdfCell"></td>
    <td class="gdfCell"></td>
    <td class="gdfCell"></td>
    <td class="gdfCell"></td>
    <td class="gdfCell"></td>
	<td class="gdfCell"></td>
	<td class="gdfCell"></td>
	<td class="gdfCell"></td>
  </tr>
  --></div>

  <div class="__template__" type="TASKBAR"><!--
  <div class="taskBox taskBoxDiv" taskId="(#=obj.id#)" >
    <div class="layout (#=obj.hasExternalDep?'extDep':''#)">
      <div class="taskStatus" status="(#=obj.status#)"></div>
      <div class="taskProgress" style="width:(#=obj.progress>100?100:obj.progress#)%; background-color:(#=obj.progress>100?'red':'rgb(153,255,51);'#);"></div>
      <div class="milestone (#=obj.startIsMilestone?'active':''#)" ></div>

      <div class="taskLabel"></div>
      <div class="milestone end (#=obj.endIsMilestone?'active':''#)" ></div>
    </div>
  </div>
  --></div>

  <div class="__template__" type="CHANGE_STATUS"><!--
    <div class="taskStatusBox">
      <div class="taskStatus cvcColorSquare" status="STATUS_ACTIVE" title="active"></div>
      <div class="taskStatus cvcColorSquare" status="STATUS_DONE" title="completed"></div>
      <div class="taskStatus cvcColorSquare" status="STATUS_FAILED" title="failed"></div>
      <div class="taskStatus cvcColorSquare" status="STATUS_SUSPENDED" title="suspended"></div>
      <div class="taskStatus cvcColorSquare" status="STATUS_UNDEFINED" title="undefined"></div>
    </div>
  --></div>


  <div class="__template__" type="TASK_EDITOR"><!--
  <div class="ganttTaskEditor">
  <table width="100%">
    <tr>
      <td>
        <table cellpadding="5">
          <tr>
            <td><label for="code">任务编码</label><br><input type="text" name="code" id="code" value="" class="formElements"></td>
          </tr>
          <tr>
            <td><label for="name">任务名称</label><br><input type="text" name="name" id="name" value=""  size="35" class="formElements"></td>
          </tr>
          <tr>
          </tr>
            <td>
              <label for="description">description</label><br>
              <textarea rows="5" cols="30" id="description" name="description" class="formElements"></textarea>
            </td>
          </tr>
        </table>
      </td>
      <td valign="top">
        <table cellpadding="5">
          <tr>
          <td colspan="2"><label for="status">status</label><br><div id="status" class="taskStatus" status=""></div></td>
          <tr>
          <td colspan="2"><label for="progress">progress</label><br><input type="text" name="progress" id="progress" value="" size="3" class="formElements"></td>
          </tr>
          <tr>
          <td><label for="start">start</label><br><input type="text" name="start" id="start"  value="" class="date" size="10" class="formElements"><input type="checkbox" id="startIsMilestone"> </td>
          <td rowspan="2" class="graph" style="padding-left:50px"><label for="duration">dur.</label><br><input type="text" name="duration" id="duration" value=""  size="5" class="formElements"></td>
        </tr><tr>
          <td><label for="end">end</label><br><input type="text" name="end" id="end" value="" class="date"  size="10" class="formElements"><input type="checkbox" id="endIsMilestone"></td>
        </table>
      </td>
    </tr>
    </table>

  <h2>assignments</h2>
  <table  cellspacing="1" cellpadding="0" width="100%" id="assigsTable">
    <tr>
      <th style="width:100px;">name</th>
      <th style="width:70px;">role</th>
      <th style="width:30px;">est.wklg.</th>
      <th style="width:30px;" id="addAssig"><span class="teamworkIcon" style="cursor: pointer">+</span></th>
    </tr>
  </table>

  <div style="text-align: right; padding-top: 20px"><button id="saveButton" class="button big">save</button></div>
  </div>
  --></div>


  <div class="__template__" type="ASSIGNMENT_ROW"><!--
  <tr taskId="(#=obj.task.id#)" assigId="(#=obj.assig.id#)" class="assigEditRow" >
    <td ><select name="resourceId"  class="formElements" (#=obj.assig.id.indexOf("tmp_")==0?"":"disabled"#) ></select></td>
    <td ><select type="select" name="roleId"  class="formElements"></select></td>
    <td ><input type="text" name="effort" value="(#=getMillisInHoursMinutes(obj.assig.effort)#)" size="5" class="formElements"></td>
    <td align="center"><span class="teamworkIcon delAssig" style="cursor: pointer">d</span></td>
  </tr>
  --></div>


  <div class="__template__" type="RESOURCE_EDITOR"><!--
  <div class="resourceEditor" style="padding: 5px;">

    <h2>Project team</h2>
    <table  cellspacing="1" cellpadding="0" width="100%" id="resourcesTable">
      <tr>
        <th style="width:100px;">name</th>
        <th style="width:30px;" id="addResource"><span class="teamworkIcon" style="cursor: pointer">+</span></th>
      </tr>
    </table>

    <div style="text-align: right; padding-top: 20px"><button id="resSaveButton" class="button big">save</button></div>
  </div>
  --></div>


  <div class="__template__" type="RESOURCE_ROW"><!--
  <tr resId="(#=obj.id#)" class="resRow" >
    <td ><input type="text" name="name" value="(#=obj.name#)" style="width:100%;" class="formElements"></td>
    <td align="center"><span class="teamworkIcon delRes" style="cursor: pointer">d</span></td>
  </tr>
  --></div>


</div>
<script type="text/javascript">
  $.JST.loadDecorator("ASSIGNMENT_ROW", function(assigTr, taskAssig) {

    var resEl = assigTr.find("[name=resourceId]");
    for (var i in taskAssig.task.master.resources) {
      var res = taskAssig.task.master.resources[i];
      var opt = $("<option>");
      opt.val(res.id).html(res.name);
      if (taskAssig.assig.resourceId == res.id)
        opt.attr("selected", "true");
      resEl.append(opt);
    }


    var roleEl = assigTr.find("[name=roleId]");
    for (var i in taskAssig.task.master.roles) {
      var role = taskAssig.task.master.roles[i];
      var optr = $("<option>");
      optr.val(role.id).html(role.name);
      if (taskAssig.assig.roleId == role.id)
        optr.attr("selected", "true");
      roleEl.append(optr);
    }

    if(taskAssig.task.master.canWrite && taskAssig.task.canWrite){
      assigTr.find(".delAssig").click(function() {
        var tr = $(this).closest("[assigId]").fadeOut(200, function() {
          $(this).remove();
        });
      });
    }


  });
</script>
</body>
</html>