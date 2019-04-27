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
		<base href="<%=basePath%>">
		<title>时钟</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">
		<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<meta name="viewport" content="width=device-width, initial-scale=1" />
    <meta charset="UTF-8">
		<title>时钟</title>
		<style type="text/css">
			canvas{display: block;margin: 0 auto;}
		</style>
		<script type="text/javascript" src="System/lpanclear/jquery-1.11.3.min.js"></script>
	</head>
  <style type="text/css">
     body{zoom:43%;}
  </style>
  <body>
       <canvas id="myCanvas" width="800" height="800">抱歉，您的浏览器不支持canvas画布</canvas>
  </body>
  <script type="text/javascript">
  
		var myCanvas=document.getElementById("myCanvas");//获取画布
		var cxt=myCanvas.getContext("2d");//给画布创建2d显示环境
		show();
		//第一根线
		var aa=350;
		var za=2;
		var ab=450;
		var zb=2;
		var ac=380;
		var zc=2;
		//第二根线
		var ca=360;
		var ya=2;
		var cb=460;
		var yb=2;
		var cc=390;
		var yc=2;
		//第三根线
		var da=370;
		var xa=2;
		var db=470;
		var xb=2;
		var dc=400;
		var xc=2;
		//第四根线
		var ea=380;
		var ta=2;
		var eb=480;
		var tb=2;
		var ec=410;
		var tc=2;
		//第五根线
		var fa=390;
		var sa=2;
		var fb=490;
		var sb=5;
		var fc=416;
		var sc=2;
		
		setInterval(show,100);//定时器每秒执行一次目的是让秒针同步
		function show(){
			var now=new Date();//获取当地时间
		    var hour=now.getHours();//获取当前时间的小时数
		    var min=now.getMinutes();//获取当前时间的分钟数
		    var sec=now.getSeconds();//获取当地时间的秒数
		    hour+=parseFloat(min/60);//
		    hour=hour>12?hour-12:hour;//将24小时转化为12小时制
		    cxt.clearRect(0,0,800,800);//清除画布
		    //================  绘制表盘   ===================
		    cxt.strokeStyle="red";//设定绘制的颜色
		    cxt.fillStyle="#650665";//设定填充的颜色
		    cxt.beginPath();
		    cxt.lineWidth=20;//设定绘制的线宽
		    cxt.shadowColor="#f546cd";//设定阴影的颜色
		    cxt.shadowBlur=20;//设定阴影的模糊度
		    cxt.arc(400,400,200,0,2*Math.PI);//设置圆心点，元的半径为200，起始点为0度，结束点为360度
		    cxt.stroke();//绘制上面的圆
		    cxt.beginPath();
		    cxt.strokeStyle="yellow";
		    cxt.lineWidth=5;
		    cxt.arc(400,400,200,0,2*Math.PI);
		    cxt.stroke();
		    //=====================  刻度     =========================
		    for(var i=0;i<=60;i++){
		    	if(i%5==0){
		    		cxt.save();//储存之前的绘画环境
		    	    cxt.translate(400,400)//将中心点移动至画布的中心点
		    	    cxt.rotate(i*6*Math.PI/180);//旋转绘画  角度i*60 度
		    	    cxt.beginPath();//重置绘画环境
		    	    cxt.strokeStyle="red";//设定绘画颜色
		    	    cxt.lineWidth=8;//设置绘画线宽
		    	    cxt.moveTo(0,-192)//设定线的起始点
		    	    cxt.lineTo(0,-170)//设定线的结束
		    	    cxt.closePath();//回到起点
		     	    cxt.stroke();//绘制
		    	    cxt.restore();//提取指定点储存的绘画环境
		    	}else{
		    		cxt.save();//储存之前的绘画环境
		    	    cxt.translate(400,400)//将中心点移动至画布的中心点
		    	    cxt.rotate(i*6*Math.PI/180);//旋转绘画  角度i*60 度
		    	    cxt.beginPath();//重置绘画环境
		    	    cxt.strokeStyle="darkorange";//设定绘画颜色
		    	    cxt.lineWidth=5;//设置绘画线宽
		    	    cxt.moveTo(0,-192)//设定线的起始点
		    	    cxt.lineTo(0,-180)//设定线的结束
		    	    cxt.closePath();//
		            cxt.stroke();//绘制
		    	    cxt.restore();//提取指定点储存的绘画环境
		    	}
		    	
		    }
		    for(var i=1;i<13;i++){//循环写入数字
		    	cxt.save();//储存之前的绘画环境
		    	cxt.translate(400,400);//吧0.0点移到表盘中心
		    	cxt.beginPath();//创建新的绘画环境
		    	cxt.font="24px 微软雅黑";
		    	switch(i){//盘点语句
		    		case 1://当i为1时
		    		cxt.fillText(i,66,-116);//填充一个文本，内容为i当前的值  后面的是位置
		    		break;
		    		case 2://当i为1时
		    		cxt.fillText(i,116,-62);//填充一个文本，内容为i当前的值  后面的是位置
		    		break;
		    		case 3://当i为1时
		    		cxt.fillText(i,140,10);//填充一个文本，内容为i当前的值  后面的是位置
		    		break;
		    		case 4://当i为1时
		    		cxt.fillText(i,110,82);//填充一个文本，内容为i当前的值  后面的是位置
		    		break;
		    		case 5://当i为1时
		    		cxt.fillText(i,64,130);//填充一个文本，内容为i当前的值  后面的是位置
		    		break;
		    		case 6://当i为1时
		    		cxt.fillText(i,-6,150);//填充一个文本，内容为i当前的值  后面的是位置
		    		break;
		    		case 7://当i为1时
		    		cxt.fillText(i,-78,130);//填充一个文本，内容为i当前的值  后面的是位置
		    		break;
		    		case 8://当i为1时
		    		cxt.fillText(i,-130,82);//填充一个文本，内容为i当前的值  后面的是位置
		    		break;
		    		case 9://当i为1时
		    		cxt.fillText(i,-150,10);//填充一个文本，内容为i当前的值  后面的是位置
		    		break;
		    		case 10://当i为1时
		    		cxt.fillText(i,-130,-62);//填充一个文本，内容为i当前的值  后面的是位置
		    		break;
		    		case 11://当i为1时
		    		cxt.fillText(i,-80,-114);//填充一个文本，内容为i当前的值  后面的是位置
		    		break;
		    		case 12://当i为1时
		    		cxt.fillText(i,-12,-134);//填充一个文本，内容为i当前的值  后面的是位置
		    		break;
		    	}
		    	cxt.closePath();
		    	cxt.fill();
		    	cxt.restore();//旋转
		    }
		    //=============  圆心的边距   ================
		   
		    cxt.strokeStyle="aqua";//
		    cxt.beginPath();
		    cxt.lineWidth=3;
		    cxt.arc(400,400,9,0,2*Math.PI);
		    cxt.closePath();
		    cxt.stroke();
		    //==============  圆心的颜色  ===============
		    cxt.fillStyle="#d10606";
		    cxt.beginPath();
		    cxt.arc(400,400,8,0,2*Math.PI);
		    cxt.closePath();
		    cxt.fill();
		    //==========  秒针       =======================
		    cxt.beginPath();
		    cxt.save();
		    cxt.strokeStyle="aqua";
		    cxt.translate(400,400);
		    cxt.rotate((sec*6)*Math.PI/180);
		    cxt.fillStyle="aqua";
		    cxt.beginPath();
		    cxt.moveTo(0,0);
		    cxt.lineTo(0,-150);
		    cxt.fill();
		    cxt.stroke();
		    cxt.beginPath();
		    cxt.moveTo(-10,-80);
		    cxt.quadraticCurveTo(10,-105,-10,-130);
		    cxt.stroke();
		    cxt.beginPath();
		    cxt.moveTo(-10,-80);
		    cxt.quadraticCurveTo(30,-105,-10,-130);
		    cxt.stroke();
		    cxt.fill();
		    cxt.restore();
		    //===============  分针     ================
		     cxt.beginPath();
		    cxt.save();
		    cxt.strokeStyle="red";
		    cxt.translate(400,400);
		    cxt.rotate((min*6)*Math.PI/180);
		    cxt.fillStyle="red";
		    cxt.beginPath();
		    cxt.moveTo(0,0);
		    cxt.lineTo(0,-130);
		    cxt.fill();
		    cxt.stroke();
		    cxt.beginPath();
		    cxt.moveTo(-10,-60);
		    cxt.quadraticCurveTo(10,-85,-10,-110);
		    cxt.stroke();
		    cxt.beginPath();
		    cxt.moveTo(-10,-60);
		    cxt.quadraticCurveTo(30,-85,-10,-110);
		    cxt.stroke();
		    cxt.fill();
		    cxt.restore();
		    //============  时针   ==================
		     cxt.beginPath();
		    cxt.save();
		    cxt.strokeStyle="darkred";
		    cxt.translate(400,400);
		    cxt.rotate((hour*30)*Math.PI/180);
		    cxt.fillStyle="darkred";
		    cxt.beginPath();
		    cxt.moveTo(0,0);
		    cxt.lineTo(0,-100);
		    cxt.fill();
		    cxt.stroke();
		    cxt.beginPath();
		    cxt.moveTo(-10,-30);
		    cxt.quadraticCurveTo(10,-55,-10,-80);
		    cxt.stroke();
		    cxt.beginPath();
		    cxt.moveTo(-10,-30);
		    cxt.quadraticCurveTo(30,-55,-10,-80);
		    cxt.stroke();
		    cxt.fill();
		    cxt.restore();
		    //=============  边框    ===================
		    cxt.save();
		    cxt.strokeStyle="red";
		    cxt.beginPath();
		    cxt.moveTo(400,191);
		    cxt.bezierCurveTo(500,170,300,130,400,100);
		    cxt.stroke();
		    cxt.beginPath();
		    cxt.moveTo(380,608);
		    cxt.lineTo(380,624);
		    cxt.lineTo(420,624);
		    cxt.lineTo(420,608);
		    cxt.stroke();
		    cxt.beginPath();
		    cxt.moveTo(384,624);
		    cxt.bezierCurveTo(aa,645,ab,685,ac,700);
		    cxt.stroke();
		    cxt.beginPath();
		    cxt.moveTo(394,624);
		    cxt.bezierCurveTo(ca,645,cb,685,cc,700);
		    cxt.stroke();
		    cxt.beginPath();
		    cxt.moveTo(404,624);
		    cxt.bezierCurveTo(da,645,db,685,dc,700);
		    cxt.stroke();
		    cxt.beginPath();
		    cxt.moveTo(414,624);
		    cxt.bezierCurveTo(ea,645,eb,685,ec,700);
		    cxt.stroke();
		    cxt.beginPath();
		    cxt.moveTo(420,624);
		    cxt.bezierCurveTo(fa,645,fb,685,fc,700);
		    cxt.stroke();
		    //第一根线============
		    aa+=za;
		    if(aa>=440){
		    	za=-2;
		    }else if(aa<=340){
		    	za=2;
		    }
		    ab+=zb;
		    if(ab>=440){
		    	zb=-2;
		    }else if(ab<=340){
		    	zb=2;
		    }
		     ac+=zc;
		    if(ac>=440){
		    	zc=-2;
		    }else if(ac<=340){
		    	zc=2;
		    }
		    //第二根线====
		    ca+=ya;
		    if(ca>=450){
		    	ya=-2;
		    }else if(ca<=350){
		    	ya=2;
		    }
		    cb+=yb;
		    if(cb>=450){
		    	yb=-2;
		    }else if(cb<=350){
		    	yb=2;
		    }
		     cc+=yc;
		    if(cc>=450){
		    	yc=-2;
		    }else if(cc<=350){
		    	yc=2;
		    }
		    //第三根线======
		    da+=xa;
		    if(da>=460){
		    	xa=-2;
		    }else if(da<=360){
		    	xa=2;
		    }
		    db+=xb;
		    if(db>=460){
		    	xb=-2;
		    }else if(db<=360){
		    	xb=2;
		    }
		     dc+=xc;
		    if(dc>=460){
		    	xc=-2;
		    }else if(dc<=360){
		    	xc=2;
		    }
		    //第四根线======
		    ea+=ta;
		    if(ea>=470){
		    	ta=-2;
		    }else if(ea<=370){
		    	ta=2;
		    }
		    eb+=tb;
		    if(eb>=470){
		    	tb=-2;
		    }else if(eb<=370){
		    	tb=2;
		    }
		     ec+=tc;
		    if(ec>=470){
		    	tc=-2;
		    }else if(ec<=370){
		    	tc=2;
		    }
		    //第五根线======
		    fa+=sa;
		    if(fa>=480){
		    	sa=-2;
		    }else if(fa<=380){
		    	sa=2;
		    }
		    fb+=sb;
		    if(fb>=480){
		    	sb=-2;
		    }else if(fb<=380){
		    	sb=2;
		    }
		     fc+=sc;
		    if(fc>=476){
		    	sc=-2;
		    }else if(fc<=376){
		    	sc=2;
		    }
		}
	</script>
</html>
