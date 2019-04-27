package com.task.test;

import com.task.util.SendMail;

public class MailTest {

	public static void main(String[] args) {
		// String mails =
		// "缈奸<37384@qq.com>; A5 鍥剧帇<470666@qq.com>; 鐜嬪墤鍙�662181@qq.com>; 鍜ㄨVPS锛屾祴璇曢娓�718597@qq.com>; 鐤媯鐥村憜<769898@qq.com>; 缃戠珯鍒朵綔<942233@qq.com>; 榫欏惢宸ヤ綔瀹�榫欏崱<2580249@qq.com>; 涓嶄細娴佹窔銇ラ瓪虏潞鹿鲁<3435786@qq.com>; 鏆村皵鍏堢敓<6036143@qq.com>; 銆庡斧銆忋�鏂�6722108@qq.com>; 涓綉_Seo<8999029@qq.com>; 鍜ㄨ580<9571770@qq.com>; 鐧惧害鍝�12911928@qq.com>; 瓒呯骇濂剁埜<17991763@qq.com>; 鍜ㄨ鍙岀嚎<20777989@qq.com>; 鍙兘璐拱鐨�28216064@qq.com>; 绁鎴戜滑閭ｅ凡閫濈殑闈掓槬<29515520@qq.com>; 浣曡瀹�29967602@qq.com>; 澶у簡浜屾墜缃�dq2s.<46782598@qq.com>; HYWORK<47204400@qq.com>; 鍜ㄨ棣欐腐锛讹及锛�53044621@qq.com>; skyshout<53049711@qq.com>; 鏅熻缃戠粶瀹㈡湇<53463016@qq.com>; 瑕佷拱vps鐨�54694125@qq.com>; 涓浗瀹氬鑱旂洘<56139949@qq.com>; 灞变笢鏋滃搧缃�71951646@qq.com>; 闈掑矝鏂版�缁�75973948@qq.com>; 绌洪棿鎰忓悜<77696024@qq.com>; 鍑嗗涔�宸叉敞鍐�80622820@qq.com>; 銆撶路浣戙�<85192044@qq.com>; 锛ㄣ儭ao^_^<87393198@qq.com>; 00鍙�120028295@qq.com>; 鐙瓙<122294545@qq.com>; 鑾卞枩閾烘垚瀹剁珛涓氱綉<151813456@qq.com>; 娣辩伆 虏潞鹿鲁<176220506@qq.com>; 2B涓栫晫閲岀殑涓�湹濂囪懇<176353371@qq.com>; 鍠勬剰鐨勮皫瑷�181158560@qq.com>; 绔欑兢椤惧<215321475@qq.com>; 鎯充拱20G棣欐腐vps6鍙�253965518@qq.com>; 璁稿磭鏋椻�xu123<275611382@qq.com>; Mr.Winner<281253644@qq.com>; 鎷涚▼搴忓憳<282032032@qq.com>; 鍜ㄨ鍋氫唬鐞�283161530@qq.com>; VPS缁欏缓璁�286528943@qq.com>; 鎲ф啲涓�蹇�290866970@qq.com>; 鍜ㄨ580<313938093@qq.com>; 鐖变笂椋庣殑灏惧反<315679142@qq.com>; 鏈嶅姟鍣�317232723@qq.com>; 鏈畬寰呯画...<327438368@qq.com>; 钃濊壊鐞嗘兂<329117846@qq.com>; Jensh<356495004@qq.com>; 濞佹捣-闂埧缃�363646988@qq.com>; 鎯充簡瑙ｇ┖闂�381737032@qq.com>; 銈炶惤:涔勯洩<383882613@qq.com>; SPDJ206-绛夈仒鈽唙e<396688254@qq.com>; [鎴村瓙鎰廬鐜勫垉<405447793@qq.com>; 鑳″畨杩�407802715@qq.com>; 闆呭垱<409873928@qq.com>; 寰峰窞澶╁ぉ鐗逛环<414771234@qq.com>; 娌堥槼鑸炴洸缃�441792462@qq.com>; 鐟為洩缃戠粶<444177930@qq.com>; 鍩�涔濆ぉ涔嬩笂杞欢寮�彂-闃滄柊<445630333@qq.com>; what锔跺悽<446948559@qq.com>; 楠嗘尟-luozhen<471064145@qq.com>; 缇庢櫙<490019783@qq.com>; 浜偣绉戞妧-閭揜<506720949@qq.com>; 楂樿皟銆呴潚鏄モ暜姘斿満<508784345@qq.com>; 鏆傛椂51z88.com<509748730@qq.com>; Jom<540555934@qq.com>; 鏄庡ぉ<513068857@qq.com>; 鏃犳倲<542300072@qq.com>; 閾佹《<542613222@qq.com>; 鍋歩dc鐨勶紝璇翠細浠嬬粛<571198008@qq.com>; 灏忚殏铓�574330374@qq.com>; 001<583028661@qq.com>; 鑺卞紑鑺辫惤<604728689@qq.com>; 闄堢帀骞充簯<635590136@qq.com>; 褰紞闃�640305543@qq.com>; it's so crazy<643915143@qq.com>; 绗ㄧ<659745729@qq.com>; 浠婃棩鎴戣嚦鍙�663958400@qq.com>; Cassiopeia Siyu<705484410@qq.com>; 鐪嬩竴瀛ｆ畫鑺辫惤骞�705743721@qq.com>; 銆�730502004@qq.com>; 绮も槄缃戝畨鈽呭濞�748984541@qq.com>; 71ju-绁炶瘽<752718920@qq.com>; 涓绘満199鍏�骞村挩璇�759862386@qq.com>; [VIP] 濂曞<761768381@qq.com>; 鎵胯鍙槸婧煍寰岀殑鑽掕獣銈�763635446@qq.com>; A5娑堟伅<784224243@qq.com>; 鎯充拱绌洪棿<839320341@qq.com>; 淇懚<843957928@qq.com>; 灞变笢鑰佷埂缃慱____? outMan?<858285982@qq.com>; 闆锋尟鍗�lzh6334)<859723970@qq.com>; 鐧惧害-鑰侀<871509993@qq.com>; 閲嶅簡瀹忎笁<876891350@qq.com>; 鐙椋庨獨<877749453@qq.com>; 涓�敓鈽呮湁浣�892985745@qq.com>; 娆ф槑鑹�maming8108<910141184@qq.com>; ";
		// String[] mail = mails.split(";");
		// String allMail = "";
		// for (String string : mail) {
		// String a = string.substring(string.indexOf("<") + 1, string
		// .length() - 1);
		// allMail += a + ";";
		// }
		// System.out.println(allMail);13603073383@139.com

		SendMail sm = new SendMail("15921533206@163.com", "订单发送错误通知",
				"尊敬的xxx您好，刚才给您发送的订单号0000000为系统测试发送。请忽略该订单。谢谢!");
	}
}
