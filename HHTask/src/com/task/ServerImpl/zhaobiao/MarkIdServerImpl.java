package com.task.ServerImpl.zhaobiao;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsStatics;
import org.springframework.beans.BeanUtils;
import org.springframework.util.FileCopyUtils;

import com.opensymphony.xwork2.ActionContext;
import com.task.Dao.TotalDao;
import com.task.Server.zhaobiao.MarkIdServer;
import com.task.ServerImpl.sys.CircuitRunServerImpl;
import com.task.ServerImpl.zhuseroffer.ZhuserOfferServerImpl;
import com.task.entity.Becoming;
import com.task.entity.Careertrack;
import com.task.entity.Machine;
import com.task.entity.PassReal;
import com.task.entity.Price;
import com.task.entity.Users;
import com.task.entity.sop.ProcardTemplate;
import com.task.entity.sop.ProcessTemplate;
import com.task.entity.system.CircuitRun;
import com.task.util.MD5;
import com.task.util.Util;
import com.tast.entity.zhaobiao.Baofei;
import com.tast.entity.zhaobiao.GysMarkIdjiepai;
import com.tast.entity.zhaobiao.Gyscgbl;
import com.tast.entity.zhaobiao.MarkIdZijian;
import com.tast.entity.zhaobiao.ProcardTemplate_Waigouyanshou;
import com.tast.entity.zhaobiao.ProcessMarkIdZijian;
import com.tast.entity.zhaobiao.QueXian;
import com.tast.entity.zhaobiao.Waigoujianpinci;
import com.tast.entity.zhaobiao.WaigoujianpinciZi;
import com.tast.entity.zhaobiao.ZhUser;
import com.tast.entity.zhaobiao.Zh_CaozuoDengji;
import com.tast.entity.zhaobiao.Zh_CaozuoEmp;
import com.tast.entity.zhaobiao.Zh_caozuo;
import com.tast.entity.zhaobiao.Zh_shebei;
import com.tast.entity.zhaobiao.Zhmoban;

public class MarkIdServerImpl implements MarkIdServer {
	private TotalDao totalDao;

	public void updatewaigoujianpinciZi(WaigoujianpinciZi w) {
		totalDao.update(w);
	}

	public WaigoujianpinciZi byIdWaigoujianpinciZi(WaigoujianpinciZi w) {
		WaigoujianpinciZi pc = (WaigoujianpinciZi) totalDao.getObjectById(
				WaigoujianpinciZi.class, w.getId());
		return pc;
	}

	public void addwaigoujianpinci(Waigoujianpinci w) {
		totalDao.save(w);
	}

	public void addwaigoujianpinciZi(WaigoujianpinciZi w) {
		totalDao.save(w);
	}

	public void deletewaigoujianpinciZi(WaigoujianpinciZi w) {
		totalDao.delete(w);
	}

	public Object[] listWaigoujianpinci(Waigoujianpinci p, Integer cpage,
			Integer PageSize, String status) {
		if (p == null) {
			p = new Waigoujianpinci();
		}
		String hql = totalDao.criteriaQueries(p, null);
		if ("xj".equals(status)) {
			hql += " and type = '巡检'";
		} else {
			hql += " and type is null ";
		}
		List list = totalDao.findAllByPage(hql, cpage, PageSize);
		int count = totalDao.getCount(hql);
		Object[] o = { list, count };
		return o;
	}

	public Object[] listyanshoupincizi(Integer id, WaigoujianpinciZi p,
			Integer parseInt, Integer pageSize) {
		if (p == null) {
			p = new WaigoujianpinciZi();
		}
		String hql = totalDao.criteriaQueries(p, null)
				+ " and waigoujianpinciId=" + id;
		List list = totalDao.findAllByPage(hql, parseInt, pageSize);
		int count = totalDao.getCount(hql);
		Object[] o = { list, count };
		return o;
	}

	public Object[] listGysMarkIdjiepaichakan(GysMarkIdjiepai p,
			Integer parseInt, Integer pageSize) {
		if (p == null) {
			p = new GysMarkIdjiepai();
		}
		String hql = totalDao.criteriaQueries(p, null,"zhuserId");
		if(p.getZhuserId()!=null){
			hql+=" and zhuserId="+p.getZhuserId();
		}
		List list = totalDao.findAllByPage(hql + " order by id desc", parseInt,
				pageSize);
		int count = totalDao.getCount(hql);
		Object[] o = { list, count };
		return o;
	}
	
	/***
	 * 供应商类别占比
	 */
	@Override
	public Object[] SupplyTypeCategory(GysMarkIdjiepai p){
		if (p == null) {
			p = new GysMarkIdjiepai();
		}
//		String hql ="select wgType from GysMarkIdjiepai where gys=? and wgType<>'' and wgType is not NULL"; 
		String hql ="select wgType from GysMarkIdjiepai where zhuserId=? and wgType<>'' and wgType is not NULL"; 
		List<String> list=totalDao.query(hql, p.getZhuserId());
		Object[] o = { list};
		return o;
	}
	
	public Object[] listBOMwaigou(ProcardTemplate p, Integer parseInt,
			Integer pageSize) {
		if (p == null) {
			p = new ProcardTemplate();
		}
		String hql = totalDao.criteriaQueries(p, null)
				+ " and procardStyle='外购' ";
		List list = totalDao.findAllByPage(hql, parseInt, pageSize);
		int count = totalDao.getCount(hql);
		Object[] o = { list, count };
		return o;
	}

	public void updateProcessMarkIdZijian(ProcessMarkIdZijian newp) {
		totalDao.update(newp);
	}

	@SuppressWarnings("unchecked")
	public void jiesuan(Float gongzi, Integer id) {
		// String hql =
		// "from ProcessMarkIdZijian pc where pc.gysMarkIdjiepai.id=? order by pc.processNO";
		// return totalDao.query(hql, fkId);
		String hql = "from GysMarkIdjiepai where rootId=?";
		List list = totalDao.query(hql, id);
		if (list != null && list.size() >= 0) {
			Float num = 0F;
			for (int i = 0; i < list.size(); i++) {
				GysMarkIdjiepai g = (GysMarkIdjiepai) list.get(i);
				// ----------------------------------------------
				String hql2 = "select sum(opcaozuojiepai+opshebeijiepai+gzzhunbeijiepai*gzzhunbeicishu) from ProcessMarkIdZijian pc where pc.gysMarkIdjiepai.id=? ";
				List sumlist = totalDao.query(hql2, g.getId());
				if (sumlist != null && sumlist.size() > 0) {
					num = (Float) sumlist.get(0);
				}
				// List listp=(List) totalDao.query(hql2, g.getId());
				// if (listp!=null&&listp.size()>=0) {
				// for (int j = 0; j < listp.size(); j++) {
				// ProcessMarkIdZijian p=(ProcessMarkIdZijian) listp.get(j);
				// num=num+p.getGzzhunbeijiepai()*p.getGzzhunbeicishu()+p.getOpcaozuojiepai()+p.getOpshebeijiepai();
				// }
				// }

			}
			// -----------
			GysMarkIdjiepai pc = (GysMarkIdjiepai) totalDao.getObjectById(
					GysMarkIdjiepai.class, id);
			pc.setJiepairen1(num * gongzi);// 工资/s
			// ////////--------------------------------------------
			if (pc.getDeliveryDuration() != null) {
				pc.setCapacity(pc.getSingleDuration() * 3600 / num);// 产能
				// pc.setDeliveryRatio(Float.parseFloat(
				// (Math.floor(pc.getDeliveryDuration()/pc.getSingleDuration())
				// )+"" ) ); //deliveryRatio =配送市场/单班时长
				pc.setDeliveryRatio(pc.getDeliveryDuration()
						/ pc.getSingleDuration());// 配送比
				pc.setDeliveryPeriod((int) Math.floor(pc.getDeliveryRatio()));// deliveryPeriod;//
				// 配送周期(X天/次)
				pc.setDeliveryAmount(pc.getCapacity() * pc.getDeliveryPeriod());
			}
			// -----------------
			// ProcardTemplate pt = (ProcardTemplate) totalDao.getObjectById(
			// ProcardTemplate.class,pc.getProcardTemplateId());
			// pt.setCapacity(pc.getCapacity());// 产能
			// pt.setDeliveryDuration(pc.getDeliveryDuration());// 配送时长
			// pt.setDeliveryRatio(pc.getDeliveryRatio());// 配送比
			// pt.setDeliveryPeriod(pc.getDeliveryPeriod());// 配送周期(X天/次)
			// pt.setDeliveryAmount(pc.getDeliveryAmount());// 送货
			// totalDao.update(pt);
			// -------------------
			pc.setAllJiepai(num);
			totalDao.update(pc);
		}

	}

	/****
	 * 供应商数据提交操作
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void jiesuan2(Float gongzi, Integer id) {
		GysMarkIdjiepai rootProcard = (GysMarkIdjiepai) totalDao.getObjectById(
				GysMarkIdjiepai.class, id);
		if (rootProcard != null) {
			Float singleDuration = rootProcard.getSingleDuration();// 单班时长

			// 得到最大的层次
			String maxhql = "select max(belongLayer) from GysMarkIdjiepai where rootId=?";
			Integer belongLayer = (Integer) totalDao.getObjectByCondition(
					maxhql, rootProcard.getId());
			// 根据层次计算
			for (int i = belongLayer; i > 0; i--) {
				// 开始计算产品的单班产量
				String hql_pro = "from GysMarkIdjiepai where rootId=? and belongLayer=?";
				List<GysMarkIdjiepai> proList = totalDao.query(hql_pro,
						rootProcard.getId(), i);
				/** 处理自制、外委、组合、总成 **/
				for (GysMarkIdjiepai gysMarkIdjiepai : proList) {
					if ("外购".equals(gysMarkIdjiepai.getProcardStyle())) {
						continue;
					}
					// 获得下层最大总节拍
					String hql_minJiepai = "select max(allJiepai) from GysMarkIdjiepai where fatherId=? and procardStyle <> '外购'";
					Object obj = totalDao.getObjectByCondition(hql_minJiepai,
							gysMarkIdjiepai.getId());
					Float sumjiepai = 0F;
					if (obj != null) {
						sumjiepai = Float.parseFloat(obj.toString());
					}

					// 获得下层总延误时长
					String hql_deliveryDuration = "select sum(deliveryDuration) from GysMarkIdjiepai where fatherId=?  and procardStyle <> '外购'";
					Object obj_deliveryDuration = totalDao
							.getObjectByCondition(hql_deliveryDuration,
									gysMarkIdjiepai.getId());
					Float sumdeliveryDuration = 0F;
					if (obj_deliveryDuration != null) {
						sumdeliveryDuration = Float
								.parseFloat(obj_deliveryDuration.toString());
					}

					List<ProcessMarkIdZijian> ptList = new ArrayList<ProcessMarkIdZijian>();
					ptList.addAll(gysMarkIdjiepai.getProcessMarkIdZijian());
					Float allJiepai = sumjiepai;
					Float maxJiepai = 0F;
					Float allWaiWeiDate = sumdeliveryDuration;// 生产总时长
					Float maxWaiWeiDate = 0F;// 外委最大时长
					for (int j = 0; j < ptList.size(); j++) {
						ProcessMarkIdZijian pt = ptList.get(j);
						Float caozuojiepai = pt.getOpcaozuojiepai() == null ? 0F
								: pt.getOpcaozuojiepai();
						Float shebeijiepai = pt.getOpshebeijiepai() == null ? 0F
								: pt.getOpshebeijiepai();
						Float zhunbeijiepai = pt.getGzzhunbeijiepai() == null ? 0F
								: pt.getGzzhunbeijiepai();
						Float zhunbeicishu = pt.getGzzhunbeicishu() == null ? 0F
								: pt.getGzzhunbeicishu();
						Float nowAllJiepai = caozuojiepai + shebeijiepai
								+ zhunbeijiepai * zhunbeicishu;// 总节拍
						Float rgAllJiepai = caozuojiepai + zhunbeijiepai
								* zhunbeicishu;// 人工节拍
						pt.setAllJiepai(Float.parseFloat(String.format("%.2f",
								nowAllJiepai)));// 更新总节拍
						// 计算工序的产能、配送比
						// 产能
						pt.setCapacity(Float.parseFloat(String.format("%.0f",
								singleDuration * 3600
										/ (nowAllJiepai + sumjiepai))));
						// 产能盈余
						allJiepai += nowAllJiepai;// 节拍
						if (j > 0) {
							ProcessMarkIdZijian topPt = ptList.get(j - 1);// 获得上一道工序
							if ("自制".equals(pt.getProductStyle())) {
								if ("yes".equals(pt.getProcessStatus())) {
									// 连续的并行工序
									if ("yes".equals(topPt.getProcessStatus())) {
										allJiepai -= nowAllJiepai;
										allJiepai += rgAllJiepai;
										maxJiepai = topPt.getOpshebeijiepai();
										// 选择设备节拍更大的工序
										if (maxJiepai < pt.getOpshebeijiepai()) {
											allJiepai -= maxJiepai;
											maxJiepai = pt.getOpshebeijiepai();
											allJiepai += pt.getOpshebeijiepai();
										}
									} else {
										maxJiepai = pt.getAllJiepai();
									}
									// else 上一道工序为不并行 （已累加 ）
								} else {
									maxJiepai = pt.getAllJiepai();
								}
								// else 不并行工序 （已累加 ）
							} else {

								// 不累加外委工序节拍
								allJiepai -= nowAllJiepai;
								allWaiWeiDate += pt.getDeliveryDuration() == null ? 0
										: pt.getDeliveryDuration();// 送货时长
								if ("yes".equals(pt.getProcessStatus())) {
									// 连续的并行工序
									if ("外委".equals(topPt.getProductStyle())
											&& "yes".equals(topPt
													.getProcessStatus())) {
										// 选择节拍更大的工序
										if (maxJiepai < pt.getAllJiepai()) {
											// allJiepai = allJiepai
											// - maxJiepai;
											// maxJiepai =
											// pt.getAllJiepai();
											allWaiWeiDate = allWaiWeiDate
													- maxWaiWeiDate;
											maxWaiWeiDate = pt
													.getDeliveryDuration();
										} else {
											// allJiepai = allJiepai
											// - pt.getAllJiepai();
											allWaiWeiDate = allWaiWeiDate
													- pt.getDeliveryPeriod();

										}
									} else {
										maxJiepai = pt.getDeliveryDuration();
										maxWaiWeiDate = pt
												.getDeliveryDuration();
									}
									// else 上一道工序为不并行 （已累加 ）
								} else {
									maxJiepai = pt.getDeliveryDuration();
									maxWaiWeiDate = pt.getDeliveryDuration();
								}
								// else 不并行工序 （已累加 ）

							}
						} else {
							maxJiepai = pt.getDeliveryDuration();
							maxWaiWeiDate = pt.getDeliveryDuration();
						}
						totalDao.update(pt);
					}
					// 总节拍累加权值
					gysMarkIdjiepai.setAllJiepai(allJiepai
							* (gysMarkIdjiepai.getCorrCount() == null ? 1
									: gysMarkIdjiepai.getCorrCount()));

					if (allJiepai > 0) {
						float capa = singleDuration * 3600 / allJiepai;
						int a = (int) Math.ceil(capa);
						gysMarkIdjiepai.setCapacity((float) a);
					} else {
						gysMarkIdjiepai.setCapacity(0F);
					}
					totalDao.update(gysMarkIdjiepai);
				}
			}
		}
	}

	public void updateZhusers(ZhUser zhUser) {
		totalDao.update(zhUser);
	}

	public ZhUser listByIdZhUserId(Integer id) {
		ZhUser pc = (ZhUser) totalDao.getObjectById(ZhUser.class, id);
		return pc;
	}

	public Waigoujianpinci ByIdWaigoujianpinci(Integer id) {
		Waigoujianpinci pc = (Waigoujianpinci) totalDao.getObjectById(
				Waigoujianpinci.class, id);
		return pc;
	}

	public void updatewaigoujianpinci(Waigoujianpinci w) {
		totalDao.update(w);
	}

	public void deletewaigoujianpinci(Waigoujianpinci w) {
		totalDao.delete(w);
	}

	public ZhUser listByIdZhUser(Integer id) {
		String hql1 = "from ZhUser where userid=?";
		ZhUser zhUser = (ZhUser) totalDao.getObjectByCondition(hql1, id);
		return zhUser;
	}

	public Object[] listtianxiejiepai(GysMarkIdjiepai p, Integer cpage,
			Integer PageSize) {
		Users user = Util.getLoginUser();
		String hql1 = "from ZhUser where userid=?";
		ZhUser zhUser = (ZhUser) totalDao.getObjectByCondition(hql1, user
				.getId());
		if (zhUser != null) {
			if (p == null) {
				p = new GysMarkIdjiepai();
			}
			String hql = totalDao.criteriaQueries(p, null) + " and  zhuserId="
					+ zhUser.getId();
			List list = totalDao.findAllByPage(hql, cpage, PageSize);
			int count = totalDao.getCount(hql);
			Object[] o = { list, count };
			return o;
		}
		return null;
	}

	/***
	 * 根据首层父类id查询流水卡片模板(组装树形结构)
	 * 
	 * @param procardTemplate
	 * @return
	 */
	@Override
	public List findProcardTemByRootId(int rootId) {
		if ((Object) rootId != null && rootId > 0) {
			String hql = "from GysMarkIdjiepai where rootId=?";
			return totalDao.query(hql, rootId);
		}
		return null;
	}

	/***
	 * 查询流水卡片模板(页面显示流水卡片模板详细使用)
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public Object[] findCardTemForShow(int id) {
		if ((Object) id != null && id > 0) {
			GysMarkIdjiepai pc = (GysMarkIdjiepai) totalDao.getObjectById(
					GysMarkIdjiepai.class, id);
			if (pc != null) {
				// 下层流水卡片模板
				Set<GysMarkIdjiepai> pcSet = pc.getProcardTSet();
				List<GysMarkIdjiepai> pclist = new ArrayList<GysMarkIdjiepai>();
				pclist.addAll(pcSet);
				// 对应工序信息
				Set<ProcessMarkIdZijian> pceSet = pc.getProcessMarkIdZijian();
				List<ProcessMarkIdZijian> pcelist = new ArrayList<ProcessMarkIdZijian>();
				pcelist.addAll(pceSet);

				return new Object[] { pc, pclist, pcelist };
			}
		}
		return null;
	}

	/***
	 * 根据id查询流水卡片模板
	 * 
	 * @param procardTemplate
	 * @return
	 */
	@Override
	public GysMarkIdjiepai findProcardTemById(int id) {
		if ((Object) id != null && id > 0) {
			return (GysMarkIdjiepai) totalDao.getObjectById(
					GysMarkIdjiepai.class, id);
		}
		return null;
	}

	/***
	 * 修改流水卡片模板
	 * 
	 * @param procardTemplate
	 * @return
	 */
	@Override
	public boolean updateProcardTemplate(GysMarkIdjiepai procardTemplate) {
		if (procardTemplate != null) {
			return totalDao.update(procardTemplate);
		}
		return false;
	}

	/***
	 * 删除流水卡片模板
	 * 
	 * @param procardTemplate
	 * @return
	 */
	@Override
	public boolean delProcardTemplate(GysMarkIdjiepai procardTemplate) {
		if (procardTemplate != null) {
			return totalDao.delete(procardTemplate);
		}
		return false;
	}

	/***
	 * 添加流水卡片模板
	 * 
	 * @param procardTemplate
	 * @return
	 */
	@Override
	public boolean addProcardTemplate(GysMarkIdjiepai procardTemplate) {
		if (procardTemplate != null) {
			return totalDao.save(procardTemplate);
		}
		return false;
	}

	/***
	 * 添加工序信息
	 * 
	 * @param procardTemplate
	 * @return
	 */
	@Override
	public boolean addProcessTemplate(ProcessMarkIdZijian processTemplate) {
		if (processTemplate != null) {
			boolean bool = totalDao.save(processTemplate);
			if (bool) {
				if ("yes".equals(processTemplate.getProcessStatus())
						&& processTemplate.getParallelId() == null) {
					processTemplate.setParallelId(processTemplate.getId());
				}
				return true;
			}
		}
		return false;
	}

	/***
	 * 通过流水卡片id(外键)查询对应工序信息
	 * 
	 * @param fkId
	 * @return
	 */
	@Override
	public List findProcessByFkId(Integer fkId) {
		if (fkId != null && fkId > 0) {
			String hql = "from ProcessMarkIdZijian pc where pc.gysMarkIdjiepai.id=? order by pc.processNO";
			return totalDao.query(hql, fkId);
		}
		return null;
	}

	public ProcardTemplate ByIdprocardTemplate(Integer id) {
		if (id != null && id > 0) {
			return (ProcardTemplate) totalDao.getObjectById(
					ProcardTemplate.class, id);
		}
		return null;
	}

	/***
	 * 通过id查找工序详细
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public ProcessMarkIdZijian findProcessT(Integer id) {
		if (id != null && id > 0) {
			return (ProcessMarkIdZijian) totalDao.getObjectById(
					ProcessMarkIdZijian.class, id);
		}
		return null;
	}

	/***
	 * 更新工序模板信息
	 * 
	 * @param processT
	 * @return
	 */
	@Override
	public String updateProcessT(ProcessMarkIdZijian processT) {
		if (processT != null) {
			ProcessMarkIdZijian oldProcessT = (ProcessMarkIdZijian) totalDao
					.getObjectById(ProcessMarkIdZijian.class, processT.getId());
			if (oldProcessT != null) {
				// 对应模板
				processT.setGysMarkIdjiepai(oldProcessT.getGysMarkIdjiepai());
				// 并行处理
				String newProcessStatus = processT.getProcessStatus();
				String oldProcessStatus = oldProcessT.getProcessStatus();
				// 判断并行状态是否存在变化
				if ("yes".equals(newProcessStatus)) {
					// 查询上一道工序
					String hql = "from ProcessMarkIdZijian pt where pt.gysMarkIdjiepai.id=? and processNO<? order by processNO desc";
					ProcessMarkIdZijian pt = (ProcessMarkIdZijian) totalDao
							.getObjectByCondition(hql, oldProcessT
									.getGysMarkIdjiepai().getId(), oldProcessT
									.getProcessNO());
					if (pt != null) {
						// 上一道工序已经并行,延续上一道工序的并行id
						if (newProcessStatus.equals(pt.getProcessStatus())) {
							processT.setParallelId(pt.getParallelId());
						} else {
							processT.setParallelId(oldProcessT.getId());
						}
					} else {
						// 说明是第一道工序(并行从自身id开始)
						processT.setParallelId(oldProcessT.getId());
					}
				} else {
					processT.setParallelId(null);
				}
				BeanUtils.copyProperties(processT, oldProcessT);
				totalDao.update(oldProcessT);
				return "true";
			} else {
				return "不存在你要修改的工序信息！";
			}
		} else {
			return "数据异常!";
		}
	}

	/***
	 * 删除工序信息
	 */
	public void delProcessT(ProcessMarkIdZijian processT) {
		totalDao.delete(processT);
	}

	public List listYigongxu(Integer zhuserId, Integer cardId) {
		ProcardTemplate pcard = (ProcardTemplate) totalDao.getObjectById(
				ProcardTemplate.class, cardId);

		// String hql =
		// "from ProcessMarkIdZijian where pm.gysMarkIdjiepai.waiweistatus='外委' pm.gysMarkIdjiepai.markId=? ";

		String hql = "from ProcessTemplate pc where pc.productStyle='外委' and   pc.procardTemplate.id="
				+ cardId
				+ " and "
				+ "pc.id  in (select processTemplateId  from  ProcessMarkIdZijian pm "
				+ "where pm.gysMarkIdjiepai.zhuserId="
				+ zhuserId
				+ " and pm.gysMarkIdjiepai.waiweistatus='外委' and pm.gysMarkIdjiepai.markId='"
				+ pcard.getMarkId() + "' )  " + "order by pc.processNO";
		List lsit = totalDao.query(hql);
		return lsit;
	}

	public Object[] listgongxu(ProcessTemplate p, Integer zhuserId,
			Integer cid, Integer cpage, Integer PageSize) {
		ProcardTemplate pcard = (ProcardTemplate) totalDao.getObjectById(
				ProcardTemplate.class, cid);
		if (p == null) {
			p = new ProcessTemplate();
		}
		// String hql = totalDao.criteriaQueries(p,
		// null)+" and id in (select procardTemplate.id from ProcessTemplate where productStyle='外委')";
		String hql = "from ProcessTemplate pc where pc.productStyle='外委' and   pc.procardTemplate.id="
				+ cid
				+ " and "
				+ "pc.id not in (select processTemplateId  from  ProcessMarkIdZijian pm where pm.gysMarkIdjiepai.zhuserId="
				+ zhuserId
				+ " and  pm.gysMarkIdjiepai.waiweistatus='外委' and pm.gysMarkIdjiepai.markId='"
				+ pcard.getMarkId() + "' )  " + "order by pc.processNO";
		List list = totalDao.findAllByPage(hql, cpage, PageSize);
		int count = totalDao.getCount(hql);
		Object[] o = { list, count };
		return o;
	}

	public Object[] listBom(ProcardTemplate p, Integer cpage, Integer PageSize) {
		if (p == null) {
			p = new ProcardTemplate();
		}
		String hql = totalDao.criteriaQueries(p, null)
				+ " and id in (select procardTemplate.id from ProcessTemplate where productStyle='外委')";
		List list = totalDao.findAllByPage(hql, cpage, PageSize);
		int count = totalDao.getCount(hql);
		Object[] o = { list, count };
		return o;
	}

	/*
 * 
 */
	public Object[] listgysMarkIdjiepai(GysMarkIdjiepai p, Integer cpage,
			Integer PageSize) {
		Users user = Util.getLoginUser();
		if (p == null) {
			p = new GysMarkIdjiepai();
		}
		String hql = totalDao.criteriaQueries(p, null);
		List list = totalDao.findAllByPage(hql, cpage, PageSize);
		int count = totalDao.getCount(hql);
		Object[] o = { list, count };
		return o;
	}

	public void bandinggongxu(int[] selected, Integer zhuserId, Integer pocardId) {
		ProcardTemplate pcard = (ProcardTemplate) totalDao.getObjectById(
				ProcardTemplate.class, pocardId);
		ZhUser zhuser = (ZhUser) totalDao.getObjectById(ZhUser.class, zhuserId);
		if (selected != null) {
			// 是否有这个BOM件号
			String hql = "from GysMarkIdjiepai  where zhuserId=? and  procardTemplateId=? and waiweistatus='外委'";
			GysMarkIdjiepai pt = (GysMarkIdjiepai) totalDao
					.getObjectByCondition(hql, zhuserId, pocardId);
			if (pt == null) {// 产选是否有这个件号的产品 没有的话创建 有的话判断有木有工序
				GysMarkIdjiepai newGMJ = new GysMarkIdjiepai();
				newGMJ.setZhuserId(zhuserId);
				newGMJ.setGys(zhuser.getName());
				newGMJ.setProcardTemplateId(pcard.getId());
				newGMJ.setUserId(zhuser.getUserid());

				newGMJ.setMarkId(pcard.getMarkId());// 件号
				newGMJ.setProName(pcard.getProName());// 名称
				newGMJ.setMaxCount(0F);// 数量
				newGMJ.setProcardStyle("总成");// 卡片类型
				newGMJ.setProductStyle(pcard.getProductStyle());
				newGMJ.setCarStyle(pcard.getCarStyle());// 车型
				// ----
				newGMJ.setFatherId(0);// 父类id设置为0
				newGMJ.setBelongLayer(1);// 设置层数为1
				newGMJ.setAddDateTime(Util.getDateTime());// 设置层数为1
				newGMJ.setDailyoutput(0F);// 日产量归零
				newGMJ.setOnePrice(0D);// 单件价格归零
				newGMJ.setUnit(pcard.getUnit());// 单位
				newGMJ.setSingleDuration(pcard.getSingleDuration() == null ? 8F
						: pcard.getSingleDuration());// 单班时长
				// ------------------
				// -------------------
				newGMJ.setWaiweistatus("外委");
				totalDao.save(newGMJ);
				newGMJ.setRootId(newGMJ.getId());
				totalDao.update(newGMJ);
				pt = newGMJ;

			}
			String hqll2 = "from ProcessMarkIdZijian pc where  pc.gysMarkIdjiepai.id="
					+ pt.getId();
			List lists = totalDao.query(hqll2);
			if (lists != null && lists.size() >= 0) {
				for (int i = 0; i < lists.size(); i++) {
					ProcessMarkIdZijian oldp = (ProcessMarkIdZijian) lists
							.get(i);
					int k = 0;
					for (int j = 0; j < selected.length; j++) {
						if (oldp.getProcessTemplateId() == selected[j]) {
							k = 1;
						}
					}
					if (k != 1) {
						totalDao.delete(oldp);
					}
				}
			}
			// -----------------------------------------------
			for (int i = 0; i < selected.length; i++) {
				ProcessTemplate pcess = (ProcessTemplate) totalDao
						.getObjectById(ProcessTemplate.class, selected[i]);
				// --------
				String hqll = "from ProcessMarkIdZijian pc where  pc.gysMarkIdjiepai.id="
						+ pt.getId()
						+ " and pc.processTemplateId="
						+ pcess.getId();
				ProcessMarkIdZijian pcs = (ProcessMarkIdZijian) totalDao
						.getObjectByCondition(hqll);
				if (pcs == null) {
					ProcessMarkIdZijian newp = new ProcessMarkIdZijian();
					//
					BeanUtils
							.copyProperties(pcess, newp, new String[] { "id" });
					newp.setProcessName(pcess.getProcessName());
					newp.setProcessTemplateId(pcess.getId());
					newp.setGysMarkIdjiepai(pt);
					newp.setZhuserId(zhuser.getUserid());
					totalDao.save(newp);
				}
			}
		} else {
			String hql = "from GysMarkIdjiepai  where markId=? and waiweistatus='外委'";
			GysMarkIdjiepai pt = (GysMarkIdjiepai) totalDao
					.getObjectByCondition(hql, pcard.getMarkId());
			totalDao.delete(pt);
		}

	}

	public Object[] listWaiweiGongxu(ProcessMarkIdZijian p, Integer ggg,
			Integer cpage, Integer PageSize) {
		Users user = Util.getLoginUser();
		String hql1 = "from ZhUser where userid=?";
		ZhUser zhUser = (ZhUser) totalDao.getObjectByCondition(hql1, user
				.getId());
		if (zhUser != null) {
			if (p == null) {
				p = new ProcessMarkIdZijian();
			}
			String hqll = "from ProcessMarkIdZijian pc where  pc.gysMarkIdjiepai.id="
					+ ggg
					+ " and pc.gysMarkIdjiepai.zhuserId="
					+ zhUser.getId();
			List list = totalDao.findAllByPage(hqll, cpage, PageSize);
			int count = totalDao.getCount(hqll);
			Object[] o = { list, count };
			return o;
		}
		return null;
	}

	public TotalDao getTotalDao() {
		return totalDao;
	}

	public void setTotalDao(TotalDao totalDao) {
		this.totalDao = totalDao;
	}

	@Override
	public Object[] listwaigoujianyan(Waigoujianpinci p, Integer parseInt,
			Integer pageSize, Integer pageSize2) {
		if (p == null) {
			p = new Waigoujianpinci();
		}
		String hql = totalDao.criteriaQueries(p, null)
				+ " and  id not in "
				+ "(select waigoujianpinciId  from ProcardTemplate_Waigouyanshou where procardTemplateId="
				+ parseInt + ")";
		// String hql=" from Waigoujianpinci where id not in " +
		// "(select waigoujianpinciId  from ProcardTemplate_Waigouyanshou where procardTemplateId="+parseInt+")";
		List list = totalDao.findAllByPage(hql, pageSize, pageSize2);
		int count = totalDao.getCount(hql);
		Object[] o = { list, count };
		return o;
	}

	public void bandingWaigouyanshou(int[] selected, Integer id, Float jiepai) {
		if (selected != null) {
			String hqll2 = "from ProcardTemplate_Waigouyanshou where procardTemplateId="
					+ id;
			List lists = totalDao.query(hqll2);
			if (lists != null) {
				for (int i = 0; i < lists.size(); i++) {
					ProcardTemplate_Waigouyanshou newpw = (ProcardTemplate_Waigouyanshou) lists
							.get(i);
					int k = 0;
					for (int j = 0; j < selected.length; j++) {
						if (newpw.getWaigoujianpinciId() == selected[j]) {
							k = 1;
						}
					}
					if (k != 1) {
						totalDao.delete(newpw);
					}

				}
			}

			for (int i = 0; i < selected.length; i++) {
				String hql22l2 = "from ProcardTemplate_Waigouyanshou where procardTemplateId="
						+ id + " and waigoujianpinciId=" + selected[i];
				ProcardTemplate_Waigouyanshou newss = (ProcardTemplate_Waigouyanshou) totalDao
						.getObjectByCondition(hql22l2);
				if (newss == null) {
					ProcardTemplate_Waigouyanshou news = new ProcardTemplate_Waigouyanshou();
					news.setProcardTemplateId(id);
					news.setWaigoujianpinciId(selected[i]);
					totalDao.save(news);
				}
			}

		} else {
			String hqllf2 = "from ProcardTemplate_Waigouyanshou where procardTemplateId="
					+ id;
			List lists22 = totalDao.query(hqllf2);
			if (lists22 != null) {
				for (int i = 0; i < lists22.size(); i++) {
					ProcardTemplate_Waigouyanshou ll = (ProcardTemplate_Waigouyanshou) lists22
							.get(i);
					totalDao.delete(ll);
				}
			}
		}
		ProcardTemplate newsss = ByIdprocardTemplate(id);
		newsss.setJianyanjiepai(jiepai);
		totalDao.update(newsss);
	}

	public List bylistAllyiban(Integer id) {
		String hqllf2 = "   from Waigoujianpinci where id in ( select waigoujianpinciId  from ProcardTemplate_Waigouyanshou where procardTemplateId="
				+ id + ")";
		List lists22 = totalDao.query(hqllf2);
		return lists22;
	}

	public Float byIdWaigoujianpinciZiZUida(Integer edi) {
		String hql = "select max(caigoushuliang2) from WaigoujianpinciZi  where waigoujianpinciId=?  ";
		List list = totalDao.query(hql, edi);
		Float a = (Float) list.get(0);
		if (a != null) {
			return a + 1;
		} else {
			return 0F;
		}
	}

	@Override
	public String pladdGysMarkIdjiepai(File addfile, String status) {
		String msg = "true";
		boolean flag = true;
		String fileName = Util.getDateTime("yyyyMMddhhmmss") + ".xls";
		// 上传到服务器
		String fileRealPath = ServletActionContext.getServletContext()
				.getRealPath("/upload/file")
				+ "/" + fileName;
		File file = new File(fileRealPath);
		jxl.Workbook wk = null;
		int i = 0;
		try {
			FileCopyUtils.copy(addfile, file);
			// 开始读取excle表格
			InputStream is = new FileInputStream(fileRealPath);// 创建文件流;
			if (is != null) {
				wk = Workbook.getWorkbook(is);// 创建workbook
			}

			Sheet st = wk.getSheet(0);// 获得第一张sheet表;
			int exclecolums = st.getRows();// 获得excle总行数
			if (exclecolums > 2) {
				int count = 0;
				int succsesscount = 0;
				StringBuffer str = new StringBuffer();
				for (i = 2; i < exclecolums; i++) {
					Cell[] cells = st.getRow(i);// 获得每i行的所有单元格（返回的是数组）

					if (cells[1].getContents() != null
							&& cells[1].getContents() != "") {
						String a = cells[1].getContents();// 件号
						String b = cells[2].getContents();// 名称
						String c = cells[3].getContents();// 规格
						String d = cells[4].getContents();// 供应商编码
						String d1 = cells[5].getContents();// 供应商名称---没有用
						String f = cells[6].getContents();// 配额
						String h = cells[7].getContents();// 是否允许订货
						String g = cells[8].getContents();// 供料属性
						String k = null;
						try {
							k = cells[9].getContents();// 版本
						} catch (Exception e) {

						}
						String hql = " from ZhUser where usercode=?";
						ZhUser zhuser = (ZhUser) totalDao.getObjectByCondition(
								hql, d);
						if (a == null || a.length() == 0) {
							count++;
							str.append("<br>第" + (i+1) + "行,没有件号</br>");
							continue;
						}
						if (zhuser == null) {
							count++;
							str.append("<br>第" + (i+1) + "行,供应商不存在</br>");
							continue;
						}
						Float cgbl1 = Float.parseFloat(f);
						String hql_gsm = "from GysMarkIdjiepai where markId=? and zhuserId=? and kgliao=?";
					
						String pricehql = " from Price where partNumber = ? and gysId = ? and kgliao = ? ";
						if (k != null && k.length() > 0) {
							hql_gsm += " and banBenNumber='" + k + "'";
							pricehql += " and banbenhao='" + k + "'";
						}else{
							hql_gsm += " and (banBenNumber is null or banBenNumber = '') ";
							pricehql += " and (banbenhao is null or banbenhao = '')";
						}
						GysMarkIdjiepai gsm = (GysMarkIdjiepai) totalDao
								.getObjectByCondition(hql_gsm, a, zhuser
										.getId(), g);
						//判断是否存在价格
						Price price =	(Price) totalDao.getObjectByCondition(pricehql, a,zhuser.getId(),g);
						if(price == null){
							count++;
							str.append("<br>第" + (i+1) + "行,"+" 不存在相关价格,请先添加价格!</br>" );
							continue;
						}
						String ids = "";
						if (gsm != null) {
							ids = "and id<>" + gsm.getId();
						}
						String hql_cgbl = "select sum(cgbl) from GysMarkIdjiepai where markId=? and kgliao=? "
								+ ids;
						if (k != null && k.length() > 0) {
							hql_cgbl += " and banBenNumber='" + k + "'";
						}
						Float cgbl = (Float) totalDao.getObjectByCondition(
								hql_cgbl, a, g);
						if ((cgbl1 + cgbl) > 100) {
							cgbl1 = 0f;
							count++;
							str.append("<br>第" + (i+1) + "行," + a + "+" + g + "+" + k
									+ "的供货比例累计超过100，请调整!</br>");
							continue;
						}
						
						if (gsm == null) {
							gsm = new GysMarkIdjiepai();
							gsm.setZhuserId(zhuser.getId());
							gsm.setGys(zhuser.getName());
							gsm.setStatus("等待填充");
							gsm.setUserId(zhuser.getUserid());
							gsm.setMarkId(a);
							gsm.setBanBenNumber(k);
							gsm.setProName(b);
							gsm.setSpecification(c);
							gsm.setMaxCount(0f);
							gsm.setProcardStyle("总成");
							gsm.setFatherId(0);
							gsm.setBelongLayer(1);
							gsm.setAddDateTime(Util.getDateTime());
							gsm.setDailyoutput(0f);
							gsm.setOnePrice(0D);
							gsm.setIsallow(h);
							gsm.setCgbl(cgbl1);
							gsm.setWgType(price.getWlType());//物料类别
							gsm.setKgliao(g);
							// String hql_pt =
							// " from ProcardTemplate where markId=? and proName=? and specification=?";
							// ProcardTemplate pt = (ProcardTemplate) totalDao
							// .getObjectByCondition(hql_pt, a, b, c);
							// if (pt != null) {
							// gsm.setCarStyle(pt.getCarStyle());
							// gsm
							// .setSingleDuration(pt
							// .getSingleDuration() == null ? 8F
							// : pt.getSingleDuration());
							// }
							totalDao.save(gsm);
							gsm.setRootId(gsm.getId());
							if(totalDao.update(gsm)){
								succsesscount++;
							}
						} else {
							gsm.setCgbl(cgbl1);
							if(totalDao.update(gsm)){
								succsesscount++;
							}
						}
					} else {
						str.append("<br>第" + (i+1) + "行,无件号</br>");
						count++;
						continue;
					}
					if (i % 400 == 0) {
						totalDao.clear();
					}
				}

				is.close();// 关闭流
				wk.close();// 关闭工作薄
				if (count == 0) {
					msg = "导入成功";
				} else {
					msg = "<br>导入成功"+succsesscount+"行,导入失败" + count + "行，失败信息如下:</br>" + str.toString();
				}
			} else {
				msg = "没有获取到行数";
			}

		} catch (Exception e) {
			msg = "导入失败,请检查文件格式是否正确!" + e.getMessage();
			e.printStackTrace();
		}
		return msg;

	}

	public List<ZhUser> findAllZhUser() {
		String hql = "from ZhUser where blackliststauts = '正常使用'";

		return totalDao.find(hql);
	}

	public Float getSumCgbl(String markId, String kgliao,String banBenNumber) {
		String hql_cgbl = "select sum(cgbl) from GysMarkIdjiepai where markId=? and kgliao=?" ;
		if(banBenNumber!=null && banBenNumber.length()>0){
			hql_cgbl += "  and banBenNumber = '"+banBenNumber+"'";
		}else{
			hql_cgbl += " and (banBenNumber is null or banBenNumber = '')";
		}
		Float cgbl1 = (Float) totalDao.getObjectByCondition(hql_cgbl, markId,
				kgliao);

		return cgbl1;
	}

	public String changCgbl(Integer id, Float cgbl, String kgliao) {
		if (id != null ) {
			GysMarkIdjiepai gsm = (GysMarkIdjiepai) totalDao.get(
					GysMarkIdjiepai.class, id);
			if(gsm == null ){
				return "数据异常，请刷新后重试!";
			}
			if(cgbl <= 100 && cgbl >= 0){
			if (gsm != null && gsm.getMarkId() != null
					&& !"".equals(gsm.getMarkId())) {
				Float cgbl1 = getSumCgbl(gsm.getMarkId(), kgliao,gsm.getBanBenNumber());
				if (gsm.getCgbl() == null || gsm.getCgbl() < 0) {
					gsm.setCgbl(0f);
				}
				Float sum = cgbl1 + cgbl - gsm.getCgbl();
				if (sum <= 100) {
					Users user = Util.getLoginUser();
					Gyscgbl gyscgbl = new Gyscgbl();
					gyscgbl.setGmId(gsm.getId());// 供应商产品Id
					gyscgbl.setDept(user.getDept());// 申请人部门
					gyscgbl.setUsername(user.getName());// 申请人姓名
					gyscgbl.setZhuserId(gsm.getZhuserId());// 供应商Id
					gyscgbl.setMarkId(gsm.getMarkId());// 件号
					gyscgbl.setProName(gsm.getProName());// 产品名称
					gyscgbl.setEp_status("未审批");// 审批状态
					gyscgbl.setGys(gsm.getGys());// 供应商
					gyscgbl.setSqtime(Util.getDateTime());// 申请时间
					gyscgbl.setCgbl(cgbl);
					if (gsm.getCgbl() == null) {
						gyscgbl.setQcgbl(0f);
					} else {
						gyscgbl.setQcgbl(gsm.getCgbl());
					}
					if (totalDao.save(gyscgbl)) {
						String processName = "供应商产品配额修改申请";
						Integer epId = null;
						try {
							epId = CircuitRunServerImpl.createProcess(
									processName, Gyscgbl.class,
									gyscgbl.getId(), "ep_status", "id", "",
									gyscgbl.getDept() + "部门 "
											+ gyscgbl.getUsername()
											+ "供应商产品配额修改申请", true);
							if (epId != null && epId > 0) {
								gyscgbl.setEpId(epId);
								CircuitRun circuitRun = (CircuitRun) totalDao
										.get(CircuitRun.class, epId);
								if ("同意".equals(circuitRun.getAllStatus())
										&& "审批完成".equals(circuitRun
												.getAuditStatus())) {
									gyscgbl.setEp_status("同意");
//									gsm.setCgbl(cgbl);
//									totalDao.update(gsm);
								} else {
									gyscgbl.setEp_status("未审批");
								}
								return totalDao.update(gyscgbl) + "";
							}
						} catch (Exception e) {
							e.printStackTrace();
							return "没有找到供应商产品配额修改申请的审批流程！";
						}

					}

				} else {
					return "件号" + gsm.getMarkId() + "所分配的配额已超出100%，申请失败!";
				}
			}
				return "配额不在0~100的范围内。";
			}
		}
		
		return "数据异常，请刷新后重试!";
	}

	// 根据供应商产品Id 获得epId

	public Integer getepId(Integer id) {
		String hql = " from  Gyscgbl where gmId =? and username=?";
		Gyscgbl gyscnbl = (Gyscgbl) totalDao.getObjectByCondition(hql, id, Util
				.getLoginUser().getName());
		if (gyscnbl != null && gyscnbl.getEpId() != null) {
			return gyscnbl.getEpId();
		}
		return null;
	}

	@Override
	public String addgysjiepai(GysMarkIdjiepai gysMarkIdjiepai,String gys) {
		if(gysMarkIdjiepai!=null){
			if(gys!=null && !"".equals(gys)){
				String hql = " from ZhUser where usercode like '%"+gys+"%' or cmp like '%"+gys+"%' ";
				ZhUser zhuser =	(ZhUser) totalDao.getObjectByCondition(hql);
				if(zhuser!=null){
					gysMarkIdjiepai.setGys(zhuser.getCmp());
					gysMarkIdjiepai.setZhuserId(zhuser.getId());
				}else{
					return "供应商不存在!";
				}
			}else{
				return "请选择供应商!";
			}
			Float cgbl =	getSumCgbl(gysMarkIdjiepai.getMarkId(),gysMarkIdjiepai.getKgliao(),gysMarkIdjiepai.getBanBenNumber() );
			Float sumcgbl = cgbl+gysMarkIdjiepai.getCgbl();
			//判断是否存在相关价格;
			String pricehql = " from Price where partNumber = ? and gysId = ? and kgliao = ? ";
			String banbensql = "";
			if (gysMarkIdjiepai.getBanBenNumber() != null &&gysMarkIdjiepai.getBanBenNumber().length() > 0) {
				pricehql += " and banbenhao='" +gysMarkIdjiepai.getBanBenNumber() + "'";
				banbensql = " and banBenNumber = '"+gysMarkIdjiepai.getBanBenNumber()+"'";
			}else{
				pricehql += " and (banbenhao is null or banbenhao = '')";
				banbensql = " and (banBenNumber is null or banBenNumber = '')";
			}
			Price price =	(Price) totalDao.getObjectByCondition(pricehql,gysMarkIdjiepai.getMarkId(),gysMarkIdjiepai.getZhuserId(),gysMarkIdjiepai.getKgliao());
			if(price == null){
				return "不存在相关价格，请先添加价格，谢谢!";
			}
			GysMarkIdjiepai old =		(GysMarkIdjiepai) totalDao.getObjectByCondition(" from GysMarkIdjiepai where markId = ? and kgliao =? " +
					" and wgType =? and zhuserId =?  "+banbensql, gysMarkIdjiepai.getMarkId(),gysMarkIdjiepai.getKgliao(),
					gysMarkIdjiepai.getWgType(),gysMarkIdjiepai.getZhuserId());
			if(old!=null){
				return "已存在供应商:"+old.getGys()+"件号:"+old.getMarkId()+"版本:"+old.getBanBenNumber()+" 的供应商产品，无需重复添加。";
			}
			if(sumcgbl<=100){
				gysMarkIdjiepai.setAddDateTime(Util.getDateTime());
				gysMarkIdjiepai.setStatus("等待填充");
				gysMarkIdjiepai.setFatherId(0);
				gysMarkIdjiepai.setBelongLayer(1);
				gysMarkIdjiepai.setDailyoutput(0f);
				gysMarkIdjiepai.setOnePrice(0D);
				gysMarkIdjiepai.setWgType(price.getWlType());
				return	totalDao.save(gysMarkIdjiepai)+"";
			}else{
				return "件号:"+gysMarkIdjiepai.getMarkId()+"，供料属性:"+gysMarkIdjiepai.getKgliao()+" 的分配配额总数已超过100%，请从新分配。";
			}
		}
		return "数据异常，请刷新后重试，谢谢!";
	}

	@Override
	public void exprot(GysMarkIdjiepai p) {
		if ( p == null) {
			 p = new GysMarkIdjiepai();
		}
		Object[] obj=	listGysMarkIdjiepaichakan(p, 0, 0);
		List<GysMarkIdjiepai> pList = new ArrayList<GysMarkIdjiepai>();
		if (obj != null && obj.length > 0) {
			pList = (List<GysMarkIdjiepai>)obj[0];
		}
		try {
			HttpServletResponse response = (HttpServletResponse) ActionContext
					.getContext().get(StrutsStatics.HTTP_RESPONSE);
			OutputStream os = response.getOutputStream();
			response.reset();
			response.setHeader("Content-disposition", "attachment; filename="
					+ new String("价格合同信息".getBytes("GB2312"), "8859_1")
					+ ".xls");
			response.setContentType("application/msexcel");
			WritableWorkbook wwb = Workbook.createWorkbook(os);
			WritableSheet ws = wwb.createSheet("价格合同信息", 0);
			ws.setColumnView(0, 16);
			ws.setColumnView(1, 16);
			ws.setColumnView(2, 18);
			ws.setColumnView(4, 24);
			ws.setColumnView(9, 25);
			ws.setColumnView(6, 12);
			ws.setColumnView(13, 16);
			ws.setColumnView(18, 25);
			ws.addCell(new Label(0, 0, "序号"));
			ws.addCell(new Label(1, 0, "件号"));
			ws.addCell(new Label(2, 0, "名称"));
			ws.addCell(new Label(3, 0, "规格"));
			ws.addCell(new Label(4, 0, "供应商代码"));
			ws.addCell(new Label(5, 0, "应商名称"));
			ws.addCell(new Label(6, 0, "配额"));
			ws.addCell(new Label(7, 0, "允许订货"));
			ws.addCell(new Label(8, 0, "供料属性"));
			ws.addCell(new Label(9, 0, "版本"));
			for (int i = 0; i < pList.size(); i++) {
				GysMarkIdjiepai gysp = pList.get(i);
				ws.addCell(new Label(0, i + 1, i + 1 + ""));
				ws.addCell(new Label(1, i + 1, gysp.getMarkId()));
				ws.addCell(new Label(2, i + 1, gysp.getProName()));
				ZhUser  zhuser = (ZhUser) totalDao.get(ZhUser.class, gysp.getZhuserId());
				ws.addCell(new Label(3, i + 1, gysp.getSpecification()));
				ws.addCell(new Label(4, i + 1, zhuser.getUsercode()));
				ws.addCell(new Label(5, i + 1, zhuser.getCmp()));
				ws.addCell(new jxl.write.Number(6, i + 1, gysp.getCgbl()));
				ws.addCell(new Label(7, i + 1, gysp.getIsallow()));
				ws.addCell(new Label(8, i + 1, gysp.getKgliao()));
				ws.addCell(new Label(9, i + 1, gysp.getBanBenNumber()));
			}
			wwb.write();
			wwb.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}

	
		
	}

	@Override
	public List<Waigoujianpinci> findAllJyPc() {
		return totalDao.query("from Waigoujianpinci");
	}

}