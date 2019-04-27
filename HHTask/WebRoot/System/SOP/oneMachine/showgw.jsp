<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<%@include file="/util/inc.jsp"%>
	<head>

		<title>领工序</title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">
		<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
		<style>
a {
	color: rgba(255, 255, 255, 1);
	text-decoration: none;
	background-color: rgba(219, 87, 5, 1);
	font-family: 'Yanone Kaffeesatz';
	font-weight: 700;
	font-size: 3em;
	display: block;
	-webkit-border-radius: 8px;
	-moz-border-radius: 8px;
	border-radius: 8px;
	-webkit-box-shadow: 0px 9px 0px rgba(219, 31, 5, 1), 0px 9px 25px
		rgba(0, 0, 0, .7);
	-moz-box-shadow: 0px 9px 0px rgba(219, 31, 5, 1), 0px 9px 25px
		rgba(0, 0, 0, .7);
	box-shadow: 0px 9px 0px rgba(219, 31, 5, 1), 0px 9px 25px
		rgba(0, 0, 0, .7);
	width: 160px;
	text-align: center;
	-webkit-transition: all .1s ease;
	-moz-transition: all .1s ease;
	-ms-transition: all .1s ease;
	-o-transition: all .1s ease;
	transition: all .1s ease;
	float: left;
	margin-left: 10%;
	-moz-box-shadow: 0px 9px 0px rgba(219, 31, 5, 1), 0px 9px 25px
		rgba(0, 0, 0, .7);
}

a:active {
	-webkit-box-shadow: 0px 3px 0px rgba(219, 31, 5, 1), 0px 3px 6px
		rgba(0, 0, 0, .9);
	-moz-box-shadow: 0px 3px 0px rgba(219, 31, 5, 1), 0px 3px 6px
		rgba(0, 0, 0, .9);
	box-shadow: 0px 3px 0px rgba(219, 31, 5, 1), 0px 3px 6px
		rgba(0, 0, 0, .9);
	top: 6px;
}
</style>

		<script type="text/javascript">
// #killanim is only being used in lab full view, maybe we could
// use it everywhere :D
//if (window !== window.top && window.top.__stop_animations !== false) {
if (window !== window.top && location.hash !== '#dontkillanim') {
	window.is_webkit = /(webkit)[ \/]([\w.]+)/.exec(window.navigator.userAgent
			.toLowerCase())

	window.max_timer = window.is_webkit ? 2000 : 20

	// Let's try to prevent user's from OOM'ing esp. in FX and Op.
	// First, we need to stop CSS Animations, after say 5s ?
	//
	// Ok, so i tried 5s, but there are some problems. Firstly, Firefox
	// and opera behave same. little improvement only. So making it 3s now.
	//
	// Tutorial: https://developer.mozilla.org/en/CSS/CSS_animations
	// Help: http://www.sitepoint.com/css3-animation-javascript-event-handlers

	var pauseCSSAnimations = function() {

		var stopCSSAnimations = function() {
			// Get the Body Element
			var body = document.getElementsByTagName('body')[0];

			// We'll setup animationstart and animationiteration
			// events only. No need for animationend, cuz the
			// animation might be 30minutes long. animationiteration
			// cuz the animation might be .000002ms long.

			// addEventListener is perfectly supported in IE9.
			// and we don't care about IE8 and below. Let those
			// browsers die in a fire!

			body.addEventListener('webkitAnimationStart', stopAnimation, false);
			body.addEventListener('webkitAnimationIteration', stopAnimation,
					false);
			body.addEventListener('animationstart', stopAnimation, false);
			body.addEventListener('animationiteration', stopAnimation, false);
		};

		// e is the event object bro ;)
		var stopAnimation = function(e) {
			// e.srcElement? lulz...
			var target_el = e.target;
			var e_type = e.type.toLowerCase();

			if (e_type.indexOf('animationstart') !== -1
					|| e_type.indexOf('animationiteration') !== -1) {
				// LOL, we need to stop the animation now!
				// setInterval? lulz...

				setTimeout(false, function() {

					if (target_el.style.webkitAnimationPlayState !== 'paused')
						target_el.style.webkitAnimationPlayState = 'paused';

					if (target_el.style.MozAnimationPlayState !== 'paused')
						target_el.style.MozAnimationPlayState = 'paused';

					if (target_el.style.animationPlayState !== 'paused')
						target_el.style.animationPlayState = 'paused';

				}, window.max_timer);
			}
		}

		stopCSSAnimations();

	};

	// Next we need to pause/stop JS Animations

	var pauseJSAnimations = function() {

		// We need to override setInterval, setTimeout
		// in such a way, that all the calls register their
		// ids in an array and we can clear all the ids
		// after a given time.
		//
		// Don't trust me ? Lern2Google:
		// http://stackoverflow.com/a/11374151/1437328
		// http://stackoverflow.com/a/8524313/1437328
		// http://stackoverflow.com/a/8769620/1437328
		//
		// 3rd one is pretty much the code you need!
		//
		// Thank you for building your trust in me now :D

		window.setInterval = (function(oldSetInterval) {
			var registered = [];

			var f = function() {
				var id;
				// .. this!
				var $this = this;
				// setInterval accepts n no. of args
				var args = arguments;
				// What if someone used the awesome Function.bind() ?
				// I am sure we want the awesome apply() then ;)

				// this is my awesome brain usage. if first val is nonsense,
				// then don't register, heh.
				if (typeof args[0] !== 'function' && args[0] === false) {
					args = Array.prototype.slice.call(arguments);
					args = args.slice(1);

					id = oldSetInterval.apply($this, args)
				} else {
					id = oldSetInterval.apply($this, args);
					registered.push(id);
				}

				//console.log(registered);
				// Need to return the Interval ID
				return id;
			};

			f.clearAll = function() {
				var r;
				while (r = registered.pop()) {
					clearInterval(r);
				}
			};

			return f;
		})(window.setInterval);

		window.setTimeout = (function(oldSetTimeout) {
			var registered = [];

			var f = function() {
				var id;
				// .. this!
				var $this = this;
				// setInterval accepts n no. of args
				var args = arguments;
				// What if someone used the awesome Function.bind?
				// I am sure we want the awesome apply then ;)

				// this is my awesome brain usage. if first val is nonsense,
				// then don't register, heh.
				if (typeof args[0] !== 'function' && args[0] === false) {
					args = Array.prototype.slice.call(arguments);
					args = args.slice(1);

					id = oldSetTimeout.apply($this, args)
				} else {
					//console.log('lolzzzzz');
					id = oldSetTimeout.apply($this, args);
					registered.push(id);
				}

				//console.log(registered);
				// Need to return the Timeout ID
				return id;
			};

			f.clearAll = function() {
				var r;
				while (r = registered.pop()) {
					clearTimeout(r);
				}
			};

			return f;
		})(window.setTimeout);

		setTimeout(false, function() {
			setTimeout.clearAll();
			setInterval.clearAll();
		}, window.max_timer);

		// Time to Cancel rAF's Bro :)
		// You know things are harder when people are actually
		// using shims for rAF :/ So we need to test for vendors!

		window.__requestAnimFrame = window.requestAnimationFrame || undefined;
		window.__cancelAnimFrame = window.cancelAnimationFrame || undefined;
		window.__vendors = [ 'webkit', 'moz', 'ms', 'o' ];
		window.__registered_rafs = [];

		// I can't think of a good way to cancel rAF's
		// So maybe lets use something similar to our other canceller's

		window.__requestFrame = function(cb) {
			if (!window.__requestAnimFrame)
				return;

			var req_id = window.__requestAnimFrame(cb);
			__registered_rafs.push(req_id);

			return req_id;
		};

		// Determine the proper VendorPrefixedFunctionName
		if (!window.__requestAnimFrame) {
			for ( var x = 0; x < window.__vendors.length; x++) {
				if (!window.__requestAnimFrame) {
					window.__requestAnimFrame = window[window.__vendors[x] + 'RequestAnimationFrame'];
					window[window.__vendors[x] + 'RequestAnimationFrame'] = __requestFrame;
				}

				if (!window.__cancelAnimFrame) {
					// I came across webkitCancelAnimationFrame and webkitCancelRequestAnimationFrame
					// No idea about the difference, so maybe lets ||'fy it

					window.__cancelAnimFrame = window[window.__vendors[x] + 'CancelAnimationFrame']
							|| window[window.__vendors[x] + 'CancelRequestAnimationFrame'];
				}
			}
		}

		// We have our proper vendor prefixed raf objects now :)
		// So let's go mad!!!
		// Let's Cancel our rAF's
		setTimeout(false, function() {
			if (!window.__requestAnimFrame)
				return;

			var r;
			while (r = window.__registered_rafs.pop()) {
				window.__cancelAnimFrame(r);
			}
		}, window.max_timer);

	};

	// Had to place outside pauseAnimations to work properly
	// else it was getting called afterwards code setTimeout/Interval executed
	if (window !== window.top)
		pauseJSAnimations();

	var __pauseAnimations = function() {
		if (window !== window.top)
			pauseCSSAnimations();
	};
} else {
	__pauseAnimations = function() {
	};
}
</script>
	</head>
	<body>
		<div>
		<input type="hidden" name = "gongwei" value="${gongwei}">
			<a href="javascript:void(0);" onclick="renwu()">任务</a>
			<a href="javascript:void(0);" onclick="lingqu()">领取</a>
			<a href="javascript:void(0);" onclick="tijiao()">提交</a>
			<a href="javascript:void(0);" onclick="tuzhi()">图纸</a>
		</div>
		<div style="clear: both; margin-top: 40px; height: 9px;"></div>
		<div >
			<iframe id="showProcess" src="SuspsomAction_findProcardByGwNum.action?gongwei=${gongwei}" marginwidth="0" marginheight="0"
				hspace="0" vspace="0" frameborder="0"
				style="width: 100%; height: 100%; margin: 0px; padding: 0px;"></iframe>
		</div>
	</body>
	<script type="text/javascript">
function renwu() {
	//window.location.href="AccessAction_showList.action";
	var url = "<%=request.getContextPath()%>/SuspsomAction_findProcardByGwNum.action?gongwei=${gongwei}";
	$("#showProcess").attr("src", url);
}
function lingqu() {
	//window.location.href="AccessAction_showList.action";
	var url = "<%=request.getContextPath()%>/System/SOP/produce/Process_shuaka.jsp?pageStatus=gongweicardLing";
	$("#showProcess").attr("src", url);
}
//System/SOP/produce/Process_shuaka.jsp?pageStatus=noCardLingGx
//ProcardAction!findProcardByCardNum.action?pageStatus=noCardLingGx&cardNumber=
function tijiao() {
	//window.location.href="AccessAction_showList.action";
	var url = "<%=request.getContextPath()%>/System/SOP/produce/Process_shuaka.jsp?pageStatus=gongweitijiao";
	$("#showProcess").attr("src", url);
}
function tuzhi() {
	//window.location.href="AccessAction_showList.action";
	var url = "<%=request.getContextPath()%>/SuspsomAction_tuzhi.action";
	$("#showProcess").attr("src", url);
}
</script>
</html>
