package com.task.ServerImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
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

import sun.net.util.IPAddressUtil;

import com.opensymphony.xwork2.ActionContext;
import com.task.Dao.TotalDao;
import com.task.Server.PriceServerDao;
import com.task.Server.zhaobiao.ZhaobiaoServer;
import com.task.ServerImpl.sys.CircuitRunServerImpl;
import com.task.action.xinxi.TwoDimensionCode;
import com.task.entity.AppLiPrice;
import com.task.entity.Category;
import com.task.entity.ClientManagement;
import com.task.entity.OaAppDetailTemplate;
import com.task.entity.Price;
import com.task.entity.Users;
import com.task.entity.WareHouseAuth;
import com.task.entity.bargain.BarContract;
import com.task.entity.bargain.BarContractDetails;
import com.task.entity.dangan.ArchiveUnarchiverAplt;
import com.task.entity.menjin.AccessWebcam;
import com.task.entity.sop.ProcardTemplate;
import com.task.entity.sop.ProcessTemplate;
import com.task.entity.sop.ycl.YuanclAndWaigj;
import com.task.entity.zhuseroffer.ZhuserOffer;
import com.task.util.RtxUtil;
import com.task.util.Util;
import com.tast.entity.zhaobiao.GysMarkIdjiepai;
import com.tast.entity.zhaobiao.ZhUser;

@SuppressWarnings("unchecked")
public class PriceServerDaoImpl implements PriceServerDao {
	private TotalDao totalDao;
	private ZhaobiaoServer zhaobiaoServer;
	private StringBuffer strbu = new StringBuffer();

	public TotalDao getTotalDao() {
		return totalDao;
	}

	public void setTotalDao(TotalDao totalDao) {
		this.totalDao = totalDao;
	}

	public ZhaobiaoServer getZhaobiaoServer() {
		return zhaobiaoServer;
	}

	public void setZhaobiaoServer(ZhaobiaoServer zhaobiaoServer) {
		this.zhaobiaoServer = zhaobiaoServer;
	}

	/**
	 * *****************************************价格类*****************************
	 * *********************************************************************
	 */

	public String addPrice(List<Price> priceList, Price price,
			File[] attachment, String[] attachmentFileName,
			String fatherPartNumber, Integer id) {
		StringBuffer mess = new StringBuffer();
		if (price != null) {
			// 上传附件
			String attachmentName = "";
			if (attachment != null && attachment.length > 0) {
				for (int i = 0; i < attachment.length; i++) {
					String fileName = price.getContractNumber().replaceAll("/",
							"").trim()
							+ new SimpleDateFormat("yyyyMMddHHmmss")
									.format(new Date())
							+ (attachmentFileName[i]
									.substring(attachmentFileName[i]
											.lastIndexOf(".")));
					if (i > 0) {
						attachmentName += "|" + fileName;
					} else {
						attachmentName += fileName;
					}
					attachmentName.trim();
					// 上传到服务器
					String fileRealPath = ServletActionContext
							.getServletContext().getRealPath("/upload/file")
							+ "/" + fileName;
					File file = new File(fileRealPath);
					try {
						FileCopyUtils.copy(attachment[i], file);
					} catch (Exception e) {
						return "文件出错!";
					}

					// 备份到项目
					String beiFenfileRealPath = "D:/WorkSpace/HHTask/WebRoot/upload/file"
							+ "/" + fileName;
					File beiFenFile = new File(beiFenfileRealPath);
					try {
						FileCopyUtils.copy(attachment[i], beiFenFile);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else {
				attachmentName = findAttName(price.getContractNumber());
				if (attachmentName == null || attachmentName.equals("")
						|| attachmentName.length() <= 0) {
					if (!"B2BAdd".equals(fatherPartNumber)) {
						return "请上传附件";
					} else {
						fatherPartNumber = null;
					}
				}
			}

			// 为属性赋值
			String date2 = new java.text.SimpleDateFormat("yyyyMMddHHmmss")
					.format(new Date());
			String identityColumn = price.getPartNumber() + date2;

			Users user = (Users) ActionContext.getContext().getSession().get(
					"Users");

			String parent = "";
			if (fatherPartNumber != null && fatherPartNumber.length() > 0) {
				parent = fatherPartNumber;
			} else {
				parent = "root";
			}

			price.setIdentityColumn(identityColumn);// 唯一标识列
			price.setParent(parent);// 父类标识
			price.setAttachmentName(attachmentName);// 附件名称
			price.setInputPeople(user.getName());// 录入人
			price.setWriteDate(Util.getDateTime());// 录入时间
			if (priceList != null && priceList.size() > 0) {
				boolean bool = true;
				out: for (int j = 0; j < priceList.size(); j++) {
					Price price1 = priceList.get(j);
					String qidinngfang = "";
					if ("总成".equals(price.getProductCategory())) {
						BeanUtils.copyProperties(price, price1, new String[] {
								"partNumber", "name", "type", "taxprice",
								"hsPrice", "bhsPrice", "firstnum", "endnum",
								"danwei" });
						ClientManagement c = (ClientManagement) totalDao.get(
								ClientManagement.class, price1.getKehuId());
						qidinngfang = c.getClientcompanyname();
					} else if ("外购".equals(price.getProduceType())) {
						BeanUtils.copyProperties(price, price1, new String[] {
								"partNumber", "name", "specification",
								"taxprice", "hsPrice", "bhsPrice", "firstnum",
								"endnum", "gysId", "kgliao", "wlType", "cgbl",
								"banbenhao", "zdqdl", "zdzxl", "zdqsl",
								"danwei" });
						ZhUser zhuser = (ZhUser) totalDao.get(ZhUser.class,
								price1.getGysId());
						qidinngfang = zhuser.getName();
						String banbensql = "";
						String banbenhao = price1.getBanbenhao();
						if (banbenhao != null && banbenhao.length() > 0) {
							banbensql = " and banbenhao ='" + banbenhao + "'";
						} else {
							banbensql = " and (banbenhao= '' or  banbenhao is null )";
						}
						YuanclAndWaigj yaw = (YuanclAndWaigj) totalDao
								.getObjectByCondition(
										" from YuanclAndWaigj where markId =? "
												+ banbensql
												+ " and  kgliao = ?", price1
												.getPartNumber(), price1
												.getKgliao());
						if (yaw == null) {
							mess.append("请检查,件号:" + price1.getPartNumber()
									+ " 版本:"+price1.getBanbenhao()+" 供料属性:"+price1.getKgliao()+",是否与外购件库一至<br/>");
							continue;
						} else {
							price.setName(yaw.getName());
							price.setSpecification(yaw.getSpecification());
							price.setBanbenhao(yaw.getBanbenhao());
							price.setWlType(yaw.getWgType());
						}
					} else if ("外委".equals(price.getProduceType())) {
						BeanUtils.copyProperties(price, price1, new String[] {
								"partNumber", "name", "specification",
								"taxprice", "hsPrice", "bhsPrice", "firstnum",
								"endnum", "gongxunum", "iscriterion", "danwei",
								"picNo" });
					}

					// 其他没有件号
					if (price1.getPartNumber() != null
							&& price1.getPartNumber().length() > 0) {
						price1.setPartNumber(price1.getPartNumber().replaceAll(
								" ", ""));// 件号去除所有空格
					}
					if ("外委".equals(price1.getProduceType())) {
						// 根据工序号匹配工序名称
						if (price1.getGongxunum() == null
								|| price1.getGongxunum().length() == 0) {
							price1.setGongxunum("待填充");
						} else {
							String gongxunum = price1.getGongxunum()
									.replaceAll("；", ";");
							String[] processNos = gongxunum.split(";");
							if (processNos != null && processNos.length > 0) {

//								if (processNos != null && processNos.length > 0) {
//									Arrays.sort(processNos);
//									for (int i1 = 1; i1 < processNos.length; i1++) {
//										Integer num0 = Integer
//												.parseInt(processNos[i1 - 1]);
//										Integer num1 = Integer
//												.parseInt(processNos[i1]);
//										if (Math.abs(num0 - num1) > 1) {
//											mess
//													.append("件号:"
//															+ price1
//																	.getPartNumber()
//															+ "工序号出现跳层问题。工序号为:"
//															+ Arrays
//																	.toString(processNos)
//															+ "\\n");
//											continue out;
//										}
//									}
//								}
								StringBuffer sb = new StringBuffer();
								StringBuffer sb2 = new StringBuffer();
								int i = 0;
								for (String processNO : processNos) {
									String processName = (String) totalDao
											.getObjectByCondition(
													" select processName from ProcessTemplate where procardTemplate.markId=? and processNO =?",
													price1.getPartNumber(),
													Integer.parseInt(processNO));
									if (processName != null) {
										if (i == 0) {
											sb.append(processName);
											sb2.append(processNO);
										} else {
											sb.append(";" + processName);
											sb2.append(";" + processNO);
										}
									}
									i++;
								}
								price1.setProcessNames(sb.toString());
								price1.setGongxunum(sb2.toString());
							}
						}
					}
					ZhUser zhuser = null;
					if (price1.getGysId() != null && price1.getGysId() > 0) {
						zhuser = (ZhUser) totalDao.get(ZhUser.class, price1
								.getGysId());
						price1.setZdqdl((price1.getZdqdl() == null || price1
								.getZdqdl() == 0) ? zhuser.getZdqdl() : price1
								.getZdqdl());
						price1.setZdzxl((price1.getZdzxl() == null || price1
								.getZdzxl() == 0) ? zhuser.getZdzxl() : price1
								.getZdzxl());
						price1.setZdqsl((price1.getZdqsl() == null || price1
								.getZdqsl() == 0) ? zhuser.getZdqsl() : price1
								.getZdqsl());
					}
					double cgbl1 = 0d;
					if (price1.getCgbl() != null) {
						if (price1.getCgbl() >= 0) {
							cgbl1 = price1.getCgbl();
						}
						String hql_cgbl = "select sum(cgbl) from GysMarkIdjiepai where markId=? and kgliao=?";
						if (price1.getBanbenhao() != null
								&& price1.getBanbenhao().length() >= 0) {
							hql_cgbl += " and banBenNumber = '"
									+ price1.getBanbenhao() + "'";
						} else {
							hql_cgbl += " and (banBenNumber is null or banBenNumber = '')";
						}
						Float sumcgbl = (Float) totalDao.getObjectByCondition(
								hql_cgbl, price1.getPartNumber(), price1
										.getKgliao());
						if (cgbl1 >= 0 && (sumcgbl + cgbl1) <= 100) {
						} else {
							cgbl1 = 0d;
						}
					}

					// 判断档案柜号是否为空
					if (price1.getDanganWeizhi() != null
							&& !"".equals(price1.getDanganWeizhi())
							&& price1.getDanganId() != null) {
						AccessWebcam an = (AccessWebcam) totalDao
								.getObjectById(AccessWebcam.class, Integer
										.parseInt(price1.getDanganId()));
						if (an != null) {
							if (an.getActualNum() != null) {
								an.setActualNum(an.getActualNum() + 1);
							} else {
								an.setActualNum(1);
							}
							totalDao.update(an);
						}
					}

					DecimalFormat df = new DecimalFormat("#.####");
					Double hsprice = Double.valueOf(df.format(price1
							.getHsPrice()));
					Double bhsprice = Double.valueOf(df.format(price1
							.getBhsPrice()));
					price1.setBhsPrice(bhsprice);
					price1.setHsPrice(hsprice);
					// 验证重复
					double aa = price1.getHsPrice();
					String str = price1.getPartNumber() + aa
							+ price1.getGysId() + price1.getKgliao()
							+ price1.getBanbenhao();
					int bb = (int) aa;
					if (bb == aa) {
						str = price1.getPartNumber() + bb + price1.getGysId()
								+ price1.getKgliao() + price1.getBanbenhao();
					}
					String hql_p = "";
					String time = Util.getDateTime();
					// if ("外购".equals(price1.getProduceType())) {
					//
					// // hql_p =
					// //
					// "from Price where partNumber+convert(varchar(50),hsPrice)+convert(varchar(10),gysId)+kgliao= '"
					// // + str + "'";
					// hql_p = " from Price where partNumber='"
					// + price1.getPartNumber()
					// + "' and hsPrice like '%"
					// + aa
					// + "%' and gysId="
					// + price1.getGysId()
					// + " and kgliao='"
					// + price1.getKgliao()
					// + "'"
					// + " and banbenhao='"
					// + price1.getBanbenhao()
					// + "' and  pricePeriodStart<= '"
					// + time
					// + "' and(pricePeriodEnd>= '"
					// + time
					// + "' or pricePeriodEnd is null or pricePeriodEnd = '')";
					// } else if ("外委".equals(price1.getProduceType())) {
					// int bb1 = (int) aa;
					// if (bb1 == aa) {
					// str = price1.getPartNumber() + bb1
					// + price1.getGysId() + price1.getWwType();
					// }
					// hql_p = "from Price where partNumber='"
					// + price1.getPartNumber()
					// + "' and hsPrice like '%"
					// + aa
					// + "%' and gysId="
					// + price1.getGysId()
					// + " and wwType='"
					// + price1.getWwType()
					// + "'"
					// + " and  pricePeriodStart<= '"
					// + time
					// + "' and (pricePeriodEnd>= '"
					// + time
					// + "' or pricePeriodEnd is null or pricePeriodEnd = '')";
					// }
					// if (hql_p.length() > 0) {
					// Integer priceCount = totalDao.getCount(hql_p);
					// if (priceCount > 0) {
					// return "件号:" + price1.getPartNumber() + "名称:"
					// + price1.getName() + "价格重复，添加失败!";
					// }
					// }
					// 同一件号 同一供应商 同一版本 同一供料属性 不同单价 情况,自动把之前的价格失效;
					String str1 = price1.getPartNumber() + price1.getGysId()
							+ price1.getKgliao() + price1.getBanbenhao();
					if (bb == aa) {
						str1 = price1.getPartNumber() + price1.getGysId()
								+ price1.getKgliao() + price1.getBanbenhao();
					}
					String hql_p1 = "";
					if ("外购".equals(price1.getProduceType())) {
						hql_p1 = " from Price where partNumber='"
								+ price1.getPartNumber()
								+ "'"
								+ " and gysId="
								+ price1.getGysId()
								+ " and kgliao='"
								+ price1.getKgliao()
								+ "'"
								+ " and  pricePeriodStart<= '"
								+ time
								+ "' and(pricePeriodEnd>= '"
								+ time
								+ "' or pricePeriodEnd is null or pricePeriodEnd = '')";
						if (price1.getBanbenhao() != null
								&& price1.getBanbenhao().length() > 0) {
							hql_p1 += " and banbenhao = '"
									+ price1.getBanbenhao() + "'";
						} else {
							hql_p1 += " and (banbenhao = '' or banbenhao is null)";
						}
					} else if ("外委".equals(price1.getProduceType())) {
						String hql_gongxu = "";
						if ("待填充".equals(price1.getGongxunum())) {
							hql_gongxu = " and (gongxunum = '待填充' or gongxunum is null or  gongxunum = '' )";
						} else {
							hql_gongxu = " and gongxunum = '"
									+ price1.getGongxunum() + "'";
						}
						hql_p1 = "from Price where partNumber='"
								+ price1.getPartNumber()
								+ "' "
								+ " and gysId="
								+ price1.getGysId()
								+ " and wwType='"
								+ price1.getWwType()
								+ "'"
								+ hql_gongxu
								+ "  and  pricePeriodStart<= '"
								+ time
								+ "' and (pricePeriodEnd>= '"
								+ time
								+ "' or pricePeriodEnd is null or pricePeriodEnd = '')";
						if (price1.getBanbenhao() != null
								&& price1.getBanbenhao().length() > 0) {
							hql_p1 += " and banbenhao = '"
									+ price1.getBanbenhao() + "'";
						} else {
							hql_p1 += " and (banbenhao = '' or banbenhao is null)";
						}
					} else if ("总成".equals(price1.getProductCategory())) {
						hql_p1 = " from Price where partNumber = '"
								+ price1.getPartNumber()
								+ "'"
								+ " and kehuId="
								+ price1.getKehuId()
								+ " and pricePeriodStart<= '"
								+ time
								+ "' and (pricePeriodEnd>= '"
								+ time
								+ "' or  pricePeriodEnd is null or pricePeriodEnd = '')";
					}
					if (hql_p1.length() > 0) {
						Price p1 = (Price) totalDao
								.getObjectByCondition(hql_p1);
						if (p1 != null) {
							Date date;
							try {
								date = Util.getCalendarDate(new Date(), -1);
								p1.setPricePeriodEnd(Util.DateToString(date,
										"yyyy-MM-dd"));
								price1.setZdqdl(price1.getZdqdl() == null ? p1
										.getZdqdl() : price1.getZdqdl());
								price1.setZdzxl(price1.getZdzxl() == null ? p1
										.getZdzxl() : price1.getZdzxl());
								price1.setZdqsl(price1.getZdqsl() == null ? p1
										.getZdqsl() : price1.getZdqsl());
								price1
										.setFirstnum(price1.getFirstnum() == null ? p1
												.getFirstnum()
												: price1.getFirstnum());
								price1
										.setEndnum(price1.getEndnum() == null ? p1
												.getEndnum()
												: price1.getEndnum());
								totalDao.update(p1);
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							price1.setPricePeriodStart(Util
									.getDateTime("yyyy-MM-dd"));
						}
					}
					if(zhuser!=null){
						qidinngfang = zhuser.getName();
					}
					price1.setQidingfang(qidinngfang);
					if (!totalDao.save(price1)) {
						bool = false;
						break;
					} else if ("外购".equals(price1.getProduceType())) {
						String hql_gsm = " from GysMarkIdjiepai where zhuserId =? and markId=? and kgliao=? ";
						if (price1.getBanbenhao() != null
								&& price1.getBanbenhao().length() > 0) {
							hql_gsm += " and banBenNumber = '"
									+ price1.getBanbenhao() + "'";
						} else {
							hql_gsm += " and (banBenNumber is null or banBenNumber = '')";
						}
						GysMarkIdjiepai gsm = (GysMarkIdjiepai) totalDao
								.getObjectByCondition(hql_gsm, zhuser.getId(),
										price1.getPartNumber(), price1
												.getKgliao());
						if (id != null && id > 0) {
							ZhuserOffer zhuseroffer = (ZhuserOffer) totalDao
									.get(ZhuserOffer.class, id);
							zhuseroffer.setStatus("已录入");
							YuanclAndWaigj yj = zhuseroffer.getYuanclAndWaigj();
							if (yj != null) {
								yj.setPricestatus("有效");
								totalDao.update(yj);
							}
							totalDao.update(zhuseroffer);
						}
						// 新添的
						if (gsm == null && "外购".equals(price1.getProduceType())) {
							gsm = new GysMarkIdjiepai();
							gsm.setZhuserId(zhuser.getId());
							gsm.setGys(zhuser.getName());
							gsm.setStatus("等待填充");
							gsm.setUserId(zhuser.getUserid());
							gsm.setMarkId(price1.getPartNumber());
							gsm.setProName(price1.getName());
							gsm.setMaxCount(0f);
							gsm.setProcardStyle("外购");
							gsm.setFatherId(0);
							gsm.setBanBenNumber(price1.getBanbenhao());
							gsm.setBelongLayer(1);
							gsm.setAddDateTime(Util.getDateTime());
							gsm.setDailyoutput(0f);
							gsm.setOnePrice(0D);
							gsm.setWgType(price1.getWlType());// 物料类别
							// gsm.setCgbl((float)
							// cgbl1);
							gsm.setKgliao(price1.getKgliao());
							gsm.setCgbl((float) cgbl1);
							totalDao.save(gsm);
							gsm.setRootId(gsm.getId());
							totalDao.update(gsm);
						} else if ("外购".equals(price1.getProduceType())) {
							gsm.setCgbl((float) cgbl1);
							totalDao.update(gsm);
						}
						// 外购件 自动绑定物料类别和供应商关系;
						String wgType = zhuser.getCclass();
						if (price1.getWlType() != null && wgType != null
								&& wgType.length() > 0
								&& wgType.indexOf(price1.getWlType()) == -1) {
							wgType += ";" + price.getWlType();
						} else {
							wgType = price.getWlType();
						}
						zhuser.setCclass(wgType);
						Set<Category> categorySet = zhuser.getCategoryset();
						Category category = null;
						if (price1.getWlType() != null
								&& price1.getWlType().length() > 0) {
							category = (Category) totalDao
									.getObjectByCondition(
											" from Category where name = ? ",
											price1.getWlType());
						}
						if (categorySet != null && categorySet.size() > 0) {
							if (!categorySet.contains(category)) {
								if (category != null) {
									categorySet.add(category);
								}
							}
						} else {
							categorySet = new HashSet<Category>();
							if (category != null) {
								categorySet.add(category);
							}
						}
						zhuser.setCategoryset(categorySet);
						totalDao.update(zhuser);
					}
				}
				if (mess != null && mess.length() > 0) {
					return mess.toString();
				}
				return bool + "";
			} else {
				if (price.getPartNumber() != null
						&& price.getPartNumber().length() > 0) {
					price.setPartNumber(price.getPartNumber().replaceAll(" ",
							""));// 件号去除所有空格
				}
				if ("外委".equals(price.getProduceType())) {
					// 根据工序号匹配工序名称
					String gongxunum = price.getGongxunum()
							.replaceAll("；", ";");
					String[] processNos = price.getGongxunum().split(";");
					if (processNos != null && processNos.length > 0) {
						StringBuffer sb = new StringBuffer();
						int i = 0;
						for (String processNO : processNos) {
							String processName = (String) totalDao
									.getObjectByCondition(
											" select processName from ProcessTemplate where procardTemplate.markId=? and processNO =?",
											price.getPartNumber(), processNO);
							if (processName != null) {
								if (i == 0) {
									sb.append(processName);
								} else {
									sb.append(";" + processName);
								}
							}
						}
						price.setProcessNames(sb.toString());
					}
				}
				// 判断档案柜号是否为空
				if (price.getDanganWeizhi() != null
						&& !"".equals(price.getDanganWeizhi())
						&& price.getDanganId() != null) {
					AccessWebcam an = (AccessWebcam) totalDao.getObjectById(
							AccessWebcam.class, Integer.parseInt(price
									.getDanganId()));
					if (an != null) {
						if (an.getActualNum() != null) {
							an.setActualNum(an.getActualNum() + 1);
						} else {
							an.setActualNum(1);
						}
						totalDao.update(an);
					}
				}
				if(price.getGysId()!=null){
					ZhUser	zhuser = (ZhUser) totalDao.get(ZhUser.class, price
							.getGysId());
					price.setQidingfang(zhuser.getName());
				}
				return totalDao.save(price) + "";
			}
		} else {
			return false + "";
		}
	}

	// 删除价格
	public boolean deletePrice(Price price) {
		if (price != null) {
			if ("外购".equals(price.getProduceType())) {
				GysMarkIdjiepai gysMarkId = (GysMarkIdjiepai) totalDao
						.getObjectByCondition(
								" from GysMarkIdjiepai where markId =? and zhuserId =? and kgliao =? ",
								price.getPartNumber(), price.getGysId(), price
										.getKgliao());
				if (gysMarkId != null) {
					totalDao.delete(gysMarkId);
				}
			}
			return totalDao.delete(price);
		}
		return false;
	}

	// 分页查询所有价格
	@SuppressWarnings("unchecked")
	public List findAllPrice(int pageNo, int pageSize) {
		String qx = getStr();
		if (qx == null || qx.length() == 0) {
			return null;
		}
		String time = Util.getDateTime("yyyy-MM-dd");
		String hql = "from Price where '"
				+ time
				+ "'>= pricePeriodStart and ('"
				+ time
				+ "'<= pricePeriodEnd or pricePeriodEnd = '' or pricePeriodEnd is null) and ( productCategory in ("
				+ qx + ") or  produceType in (" + qx
				+ ") ) order by writeDate desc ";
		return totalDao.findAllByPage(hql, pageNo, pageSize);
	}

	/**
	 * 接收返回的字符串拼接;
	 */
	private String getStr() {
		String str = "";
		Users user = Util.getLoginUser();
		String hql = "from WareHouseAuth where usercode = ? and type = ?";
		WareHouseAuth w = (WareHouseAuth) totalDao.getObjectByCondition(hql,
				user.getCode(), "价格");
		if (w == null) {
			return "";
		}
		String s = w.getAuth();
		List<String> strList = new ArrayList<String>();
		String[] strArr = s.split(",");
		for (int i = 0; i < strArr.length; i++) {
			if (strArr[i].endsWith("_" + WareHouseAuthServiceImpl.SHOW)) {
				strList.add(strArr[i].substring(0, strArr[i].indexOf('_')));
			}
		}
		Collections.replaceAll(strList, "zc", "总成");
		Collections.replaceAll(strList, "wg", "外购");
		Collections.replaceAll(strList, "ww", "外委");
		Collections.replaceAll(strList, "qt", "其他");
		Collections.replaceAll(strList, "fl", "辅料");
		Collections.replaceAll(strList, "mj", "磨具封装");
		for (String str1 : strList) {
			str += ",'" + str1 + "'";
		}
		if (str.length() > 0) {
			return str.substring(1);
		}
		return "'test'";
	}

	// 条件查询
	public Map<Integer, Object> findPriceByCondition(Price price, int pageNo,
			int pageSize, String statue, String tags) {
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		String other = "";
		if (price != null) {
			if (price.getPricePeriodStart() != null
					&& price.getPricePeriodEnd() != null
					&& price.getPricePeriodStart().length() > 0
					&& price.getPricePeriodEnd().length() > 0) {
				other += " and writeDate>='" + price.getPricePeriodStart()
						+ "' and writeDate<='" + price.getPricePeriodEnd()
						+ "'";
			} else if (price.getPricePeriodStart() != null
					&& price.getPricePeriodStart().length() > 0
					&& "".equals(price.getPricePeriodEnd())) {
				other += " and writeDate>='" + price.getPricePeriodStart()
						+ "'";
			} else if ("".equals(price.getPricePeriodStart())
					&& price.getPricePeriodEnd() != null
					&& price.getPricePeriodEnd().length() > 0) {
				other += " and writeDate<='" + price.getPricePeriodEnd() + "'";
			}
		} else {
			price = new Price();
		}
		String[] otherName = { "contractNumber", "pricePeriodEnd",
				"pricePeriodStart" };
		if (statue != null) {
			List<Price> unPrice = null;
			if (statue.equals("all")) {
				unPrice = totalDao.query("from Price where  isGuiDang = 'no'");
				map.put(3, unPrice);
			} else if (statue.equals("dept")) {
				unPrice = totalDao.query("from Price where  isGuiDang = 'no'");
				map.put(3, unPrice);
				Users user = Util.getLoginUser();
				if (other == null) {
					other += " and chargePerson in (select name from Users where dept='"
							+ user.getDept() + "')";
				} else {
					other += " and chargePerson in (select name from Users where dept='"
							+ user.getDept() + "')";
				}
			} else if (statue.equals("single")) {
				unPrice = totalDao.query("from Price where isGuiDang = 'no'");
				map.put(3, unPrice);
				Users user = Util.getLoginUser();
				if (other == null) {
					other = " chargePerson='" + user.getName() + "'";
				} else {
					other += " and chargePerson='" + user.getName() + "'";
				}
			} else if (statue.equals("dangan")) {
				Users user = Util.getLoginUser();
			} else if (statue.equals("find")) {
				String time = Util.getDateTime();
				String str = getStr();
				other += " and '"
						+ time
						+ "'>= pricePeriodStart and ('"
						+ time
						+ "' <= pricePeriodEnd or pricePeriodEnd = '' or pricePeriodEnd is null) ";

				if (str != null && str.length() > 0) {
					other += " and  ( productCategory in (" + str
							+ ") or  produceType in (" + str + ") ) ";
				} else {
					other += " and  ( productCategory in ('-1') or  produceType in ('-1') ) ";
				}
				if ("admin".equals(tags)) {
					other += " and attachmentName is not null and attachmentName <> '' ";
				}
			} else {
				String str = getStr();
				if (str != null && str.length() > 0) {
					other += " and ( productCategory in (" + str
							+ ") or  produceType in (" + str + ") ) ";
				} else {
					other += " and  ( productCategory in ('-1') or  produceType in ('-1') ) ";
				}

			}
		}
		String str = "";
		if (price.getWlType() != null && !"".equals(price.getWlType())) {
			Category category = (Category) totalDao.getObjectByCondition(
					" from Category where name =? ", price.getWlType());
			if (category != null) {
				getWgtype(category);
			}
			str = " and wlType in (" + strbu.toString().substring(1) + ")";
			price.setWlType(null);
		}

		String hql = totalDao.criteriaQueries(price, null, otherName);
		hql += other;
		if (price != null && price.getContractNumber() != null
				&& price.getContractNumber().length() > 0) {
			hql += " and contractNumber like '%" + price.getContractNumber()
					+ "%'";
		}
		hql += str + " order by id asc";
		HttpServletRequest request = ServletActionContext.getRequest();
		int count = totalDao.getCount(hql);
		List objs = totalDao.findAllByPage(hql, pageNo, pageSize);
		List<Price> priceList = new ArrayList<Price>();
		for (Object obj : objs) {
			Price price1 = (Price) obj;
			String qiandingfang = "";
			if (price1.getGysId() != null && price1.getGysId() > 0) {
				ZhUser zhuser = (ZhUser) totalDao.get(ZhUser.class, price1
						.getGysId());
				if (zhuser != null) {
					qiandingfang = zhuser.getCmp();
				}
			}
			if (price1.getKehuId() != null && price1.getKehuId() > 0) {
				ClientManagement cm = (ClientManagement) totalDao.get(
						ClientManagement.class, price1.getKehuId());
				qiandingfang = cm.getClientcompanyname();
			}
			price1.setQidingfang(qiandingfang);
			priceList.add(price1);
		}
		map.put(1, priceList);
		map.put(2, count);
		strbu = new StringBuffer();
		return map;
	}

	private void getWgtype(Category category) {
		List<Category> list = totalDao.query(
				" from  Category where fatherId = ?", category.getId());
		if (list != null && list.size() > 0) {
			for (Category category2 : list) {
				getWgtype(category2);
			}
		} else {
			strbu.append(",'" + category.getName() + "'");
		}
	}

	// 根据合同编号查找附件名
	public String findAttName(String conNumber) {
		if (conNumber != null && conNumber.length() > 0) {
			String hql = "select distinct attachmentName from Price where contractNumber=?";
			return (String) totalDao.getObjectByCondition(hql, conNumber);
		}
		return null;
	}

	// 通过id查询价格
	public Price findPriceById(int id) {
		if ((Object) id != null && id > 0) {
			Price price = (Price) totalDao.getObjectById(Price.class, id);
			if (price != null && price.getGysId() != null) {
				String gysName = (String) totalDao.getObjectByCondition(
						"select cmp from ZhUser where id=?", price.getGysId());
				price.setGys(gysName);
			}
			return price;
		}
		return null;
	}

	// 更改价格
	public String updatePrice(Price price, File[] attachment,
			String[] attachmentFileName, Price oldPrice) {
		if (price != null) {
			// 上传附件
			String attachmentName = "";
			if (attachment != null && attachment.length > 0) {
				for (int i = 0; i < attachment.length; i++) {
					String fileName = price.getContractNumber().replaceAll("/",
							"").trim()
							+ new SimpleDateFormat("yyyyMMddHHmmss")
									.format(new Date())
							+ (attachmentFileName[i]
									.substring(attachmentFileName[i]
											.lastIndexOf(".")));
					if (i > 0) {
						attachmentName += "|" + fileName;
					} else {
						attachmentName += fileName;
					}
					attachmentName.trim();
					// 上传到服务器
					String fileRealPath = ServletActionContext
							.getServletContext().getRealPath("/upload/file")
							+ "/" + fileName;
					File file = new File(fileRealPath);
					try {
						FileCopyUtils.copy(attachment[i], file);
					} catch (Exception e) {
						e.printStackTrace();
						return "文件异常!~";
					}

					// 备份到项目
					String beiFenfileRealPath = "D:/WorkSpace/HHTask/WebRoot/upload/file"
							+ "/" + fileName;
					File beiFenFile = new File(beiFenfileRealPath);
					try {
						FileCopyUtils.copy(attachment[i], beiFenFile);
						price.setIsGuiDang("yes");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				price.setAttachmentName(attachmentName);// 附件名称
			}else{
				price.setAttachmentName(oldPrice.getAttachmentName());// 附件名称
			}
			// 为属性赋值
			if (price.getParent() == null || price.getParent().length() <= 0) {
				price.setParent("root");// 父类标识
			}

			price.setWriteDate(oldPrice.getWriteDate());// 录入时间

			if ("外购".equals(price.getProduceType()) && price.getCgbl() != null) {
				double cgbl1 = price.getCgbl();
				String hql_cgbl = "select sum(cgbl) from GysMarkIdjiepai where markId=? and kgliao=? and zhuserId <> ?";
				String banbensql = "";
				if (price.getBanbenhao() != null
						&& price.getBanbenhao().length() >= 0) {
					banbensql += " and banBenNumber = '" + price.getBanbenhao()
							+ "'";
				} else {
					banbensql += " and (banBenNumber is null or banBenNumber = '')";
				}
				Float sumcgbl = (Float) totalDao.getObjectByCondition(hql_cgbl
						+ banbensql, price.getPartNumber(), price.getKgliao(),
						price.getGysId());
				if (cgbl1 >= 0 && (sumcgbl + cgbl1) <= 100) {
				} else {
					return "改件号剩余可分配配额为:" + (100 - sumcgbl) + "，请勿超过该数据!~";
				}
				price.setCgbl(cgbl1);
				ZhUser zhuser = (ZhUser) totalDao.get(ZhUser.class, price
						.getGysId());
				String hql_gsm = " from GysMarkIdjiepai where zhuserId =? and markId=? and kgliao=? "
						+ banbensql;
				GysMarkIdjiepai gsm = (GysMarkIdjiepai) totalDao
						.getObjectByCondition(hql_gsm, zhuser.getId(), price
								.getPartNumber(), price.getKgliao());

				// 新添的
				if (gsm == null && "外购".equals(price.getProduceType())) {
					gsm = new GysMarkIdjiepai();
					gsm.setZhuserId(zhuser.getId());
					gsm.setGys(zhuser.getName());
					gsm.setStatus("等待填充");
					gsm.setUserId(zhuser.getUserid());
					gsm.setMarkId(price.getPartNumber());
					gsm.setProName(price.getName());
					gsm.setMaxCount(0f);
					gsm.setProcardStyle("外购");
					gsm.setFatherId(0);
					gsm.setBanBenNumber(price.getBanbenhao());
					gsm.setBelongLayer(1);
					gsm.setAddDateTime(Util.getDateTime());
					gsm.setDailyoutput(0f);
					gsm.setOnePrice(0D);
					gsm.setWgType(price.getWlType());// 物料类别
					gsm.setBanBenNumber(price.getBanbenhao());
					// gsm.setCgbl((float)
					// cgbl1);
					gsm.setKgliao(price.getKgliao());
					gsm.setCgbl((float) cgbl1);
					totalDao.save(gsm);
					gsm.setRootId(gsm.getId());
					totalDao.update(gsm);
				} else if ("外购".equals(price.getProduceType())) {
					gsm.setCgbl((float) cgbl1);
					totalDao.update(gsm);
				}
				// 外购件 自动绑定物料类别和供应商关系;
				String wgType = zhuser.getCclass();
				if (wgType != null && wgType.length() > 0
						&& wgType.indexOf(price.getWlType()) == -1) {
					wgType += ";" + price.getWlType();
				} else {
					wgType = price.getWlType();
				}
				zhuser.setCclass(wgType);
				Set<Category> categorySet = zhuser.getCategoryset();
				Category category = (Category) totalDao.getObjectByCondition(
						" from Category where name = ? ", price.getWlType());
				if (categorySet != null && categorySet.size() > 0) {
					if (!categorySet.contains(category)) {
						if (category != null) {
							categorySet.add(category);
						}
					}
				} else {
					categorySet = new HashSet<Category>();
					if (category != null) {
						categorySet.add(category);
					}
				}
				zhuser.setCategoryset(categorySet);
				totalDao.update(zhuser);
			}

			// 外委修改工序号时 自动修改工序名
			if (price.getGongxunum() != null
					&& price.getGongxunum().length() > 0
					&& !price.getGongxunum().equals(oldPrice.getGongxunum())) {
				String[] gongxuNums = price.getGongxunum().split(";");
				Arrays.sort(gongxuNums);
//				for (int i1 = 1; i1 < gongxuNums.length; i1++) {
//					Integer num0 = Integer.parseInt(gongxuNums[i1 - 1]);
//					Integer num1 = Integer.parseInt(gongxuNums[i1]);
//					if (Math.abs(num0 - num1) > 1) {
//						return "件号:" + price.getPartNumber()
//								+ "工序号出现跳层问题。工序号为:"
//								+ Arrays.toString(gongxuNums) + "<br/>";
//					}
//				}
				if (gongxuNums != null && gongxuNums.length > 0) {
					String hql_pt = "select processName from ProcessTemplate where procardTemplate.id in (select id from ProcardTemplate where markId = ? and procardStyle=?)  ";
					String str = " and processNO in (";
					String str1 = "";
					for (int j = 0; j < gongxuNums.length; j++) {
						str1 += "," + gongxuNums[j];
					}
					if (str1.length() > 1) {
						str1 = str1.substring(1);
						str = str + str1 + ")";
					}
					hql_pt += str + " group by processName order by processNO";
					String procardStyle = price.getProductCategory();
					if ("零件".equals(procardStyle)) {
						procardStyle = "自制";
					}
					List<String> strList = totalDao.query(hql_pt, price
							.getPartNumber(), procardStyle);
					String processNames = "";
					for (String str0 : strList) {
						processNames += ";" + str0;
					}
					if (processNames.length() > 1) {
						processNames = processNames.substring(1);
					}
					price.setProcessNames(processNames);
				}
			}
			boolean bool = totalDao.update(price);
			// 如果修改成功并且更改了文件
			if (bool == true) {
				AttendanceTowServerImpl.updateJilu(4, price, price.getId(),
						price.getName());
				if (attachment != null && attachment.length > 0) {
					String hql_attch = "from  CodeTranslation where type ='sys' and keyCode = '合同价格文件同步修改' and valueCode = '是'";
					int count =	totalDao.getCount(hql_attch);
					if(count>0){
						updateAttachmentName(price, "");
					}
					// String fileRealPath = ServletActionContext
					// .getServletContext().getRealPath("/upload/file/")
					// + "/" + oldPrice.getAttachmentName();
					// File oldFile = new File(fileRealPath);
					// if (oldFile.exists()) {
					// oldFile.delete();
					// }
				}
				return true + "";
			}
		}
		return false + "";
	}

	// 根据合同编号修改附件名称
	public int updateAttachmentName(Price price, String other) {
		if (price != null) {
			String reallyHql = "update Price set ";
			if (other != null && other.length() > 0) {
				String[] temp = other.split("=");
				for (int i = 0; i < temp.length; i++) {
					if (i % 2 == 0) {
						reallyHql += temp[i] + " = ";
					} else {
						reallyHql += "'" + temp[i] + "'";
					}
				}

				reallyHql += " where contractNumber='"
						+ price.getContractNumber() + "'";
			} else {
				reallyHql = "update Price set attachmentName='"
						+ price.getAttachmentName()
						+ "' where contractNumber='"
						+ price.getContractNumber() + "'";
			}
			return totalDao.createQueryUpdate(reallyHql, null);
		}
		return 0;
	}

	// 获得总数量
	public int getCount() {
		String qx = getStr();
		if (qx == null || qx.length() == 0) {
			return 0;
		}
		String time = Util.getDateTime("yyyy-MM-dd");
		String hql = "from Price where '"
				+ time
				+ "'>= pricePeriodStart and ('"
				+ time
				+ "'<= pricePeriodEnd or pricePeriodEnd = '' or pricePeriodEnd is null) and ( productCategory in ("
				+ qx + ") or  produceType in (" + qx
				+ ") ) order by writeDate desc";
		return totalDao.getCount(hql);
	}

	// 修改档案号
	public boolean updateFileName(Price price) {
		if (price != null) {
			return totalDao.update(price);
		}
		return false;
	}

	/**
	 * 根据件号查询合同信息(外委工序使用)
	 * 
	 * @return
	 */
	@Override
	public Price findPriceByMarkId(String markId) {
		String hql = "from Price p where partNumber=?";
		String time = Util.getDateTime();
		hql += " and (p.gysId is not null ) and p.produceType ='外委' and p.productCategory='零件'  and '"
				+ time
				+ "'>=p.pricePeriodStart and ('"
				+ time
				+ "'"
				+ " <=p.pricePeriodEnd "
				+ " or   p.pricePeriodEnd is null or   p.pricePeriodEnd='')";
		return (Price) totalDao.getObjectByCondition(hql, markId);
	}

	@Override
	public Price findPriceByMarkIdMaxTime(Price price) {
		String hql = " from Price where partNumber=?  and productCategory=? "
				+ "and produceType=? and kehuId=? order by  pricePeriodStart desc";

		return (Price) totalDao.getObjectByCondition(hql,
				price.getPartNumber(), price.getProductCategory(), price
						.getProduceType(), price.getKehuId());
	}

	@Override
	public String findMaxfileNumberprice() {
		String year = Util.getDateTime("yyyy");
		String hql = " select max(p.fileNumber) from Price as p where p.fileNumber like '%F_"
				+ year + "%'";
		String num = "";

		String fileNumber = "";
		String a = (String) totalDao.getObjectByQuery(hql);
		String maxNum = "";
		if (a != null && a.length() > 0) {
			maxNum = a;
		}
		try {
			if (maxNum != null && maxNum.length() > 0) {
				Integer number = Integer.parseInt(maxNum.substring(6)) + 1;
				num = number + "";
				String s = "";
				for (int i = num.length(); i < 6; i++) {
					s += "0";
				}
				num = s + number;
			} else {
				num = "000001";
			}
			fileNumber = "F_" + year + num;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			fileNumber = maxNum;
		}

		return fileNumber;
	}

	@Override
	public Map<Integer, Object> findPriceByCondition(Map map, int pageNo,
			int pageSize) {
		Map<Integer, Object> map1 = new HashMap<Integer, Object>();
		List list = new ArrayList();
		String hql = "from Price p where 1 = 1";
		String time = Util.getDateTime();
		String str = "";
		if (map != null && map.size() > 0) {
			if (map.get("name") != null) {
				str += " and p.name like '%" + map.get("name") + "%'";
			}
			if (map.get("partNUmber") != null) {
				str += " and p.partNumber like '%" + map.get("partNUmber")
						+ "%'";
			}
			if (map.get("statue") != null) {
				if ("xgprice".equals(map.get("statue"))) {
					if (map.get("kehuId") != null) {
						str += " and p.kehuId=" + map.get("kehuId");
					}
					hql += str
							+ " and '"
							+ time
							+ "'>=p.pricePeriodStart and ('"
							+ time
							+ "'"
							+ " <=p.pricePeriodEnd "
							+ " or   p.pricePeriodEnd is null or   p.pricePeriodEnd='' )";
				} else if ("KHPrice".equals(map.get("statue"))) {
					str += " and (p.kehuId ='' or p.kehuId is null )";
					hql += str
							+ " and p.produceType ='销售' and p.productCategory='总成'  and '"
							+ time
							+ "'>=p.pricePeriodStart and ('"
							+ time
							+ "'"
							+ " <=p.pricePeriodEnd "
							+ " or   p.pricePeriodEnd is null or   p.pricePeriodEnd='' )";
				}

			}
			hql += " order by id desc ";
		}
		int count = totalDao.getCount(hql);
		list = totalDao.findAllByPage(hql, pageNo, pageSize);
		map1.put(1, list);
		map1.put(2, count);
		return map1;
	}

	@Override
	public boolean updatePrice(Price price) {
		if (price != null) {
			Price oldprice = (Price) totalDao.get(Price.class, price.getId());
			oldprice.setHsPrice(price.getHsPrice());
			oldprice.setBhsPrice(price.getBhsPrice());
			return totalDao.update(oldprice);
		}
		return false;
	}

	@Override
	public Map<Integer, Object> findPriceBykehuId(Map<String, String> map,
			int pageNo, int pageSize) {
		Map map1 = new HashMap();
		String time = Util.getDateTime();
		String hql = "from Price p where 1 = 1";
		String str = "";
		if (map != null && map.size() > 0) {
			if (map.get("id") != null && Integer.parseInt(map.get("id")) > 0) {
				str += " and p.kehuId =" + map.get("id");
			}
			if (map.get("name") != null) {
				str += " and p.name like '%" + map.get("name") + "%'";
			}
			if (map.get("partNUmber") != null) {
				str += " and p.partNumber like '%" + map.get("partNUmber")
						+ "%'";
			}
		}
		hql += str
				+ "and p.produceType ='销售' and p.productCategory='总成'  and '"
				+ time + "'>=p.pricePeriodStart and ('" + time + "'"
				+ " <=p.pricePeriodEnd "
				+ " or   p.pricePeriodEnd is null or   p.pricePeriodEnd='' )";
		hql += " order by id desc ";
		int count = totalDao.getCount(hql);
		List list = totalDao.findAllByPage(hql, pageNo, pageSize);
		map1.put(1, list);
		map1.put(2, count);
		return map1;
	}

	@Override
	public void updatePriceKehuId(int[] idArray, Integer kehuId) {
		if (idArray != null && idArray.length > 0) {
			for (int i = 0; i < idArray.length; i++) {
				Price price = (Price) totalDao.get(Price.class, idArray[i]);
				price.setKehuId(kehuId);
				totalDao.update(price);
			}
		}

	}

	@Override
	public boolean jiechuPriceKehuId(Integer id) {
		if (id != null && id > 0) {
			Price price = (Price) totalDao.get(Price.class, id);
			price.setKehuId(null);
			return totalDao.update(price);
		}
		return false;
	}

	@Override
	public List<ZhUser> findAllZhUser() {
		String hql = "from ZhUser where blackliststauts = '正常使用'";
		return totalDao.find(hql);
	}

	@Override
	public String PladdPrice(File addprice, String statue, String type) {
		String msg = "true";
		boolean flag = true;
		StringBuffer strb = new StringBuffer();
		String fileName = Util.getDateTime("yyyyMMddhhmmss") + ".xls";
		// 上传到服务器
		String fileRealPath = ServletActionContext.getServletContext()
				.getRealPath("/upload/file")
				+ "/" + fileName;
		File file = new File(fileRealPath);
		jxl.Workbook wk = null;
		int i = 0;
		try {
			FileCopyUtils.copy(addprice, file);
			// 开始读取excle表格
			InputStream is = new FileInputStream(fileRealPath);// 创建文件流;
			if (is != null) {
				wk = Workbook.getWorkbook(is);// 创建workbook
			}

			Sheet st = wk.getSheet(0);// 获得第一张sheet表;
			int exclecolums = st.getRows();// 获得excle总行数
			if (exclecolums > 2) {
				List<Integer> strList = new ArrayList<Integer>();
				Integer errorCount = 0;// 错误数量
				Integer cfCount = 0;// 重复数量
				Integer successCount = 0;// 成功数量
				out: for (i = 2; i < exclecolums; i++) {
					Cell[] cells = st.getRow(i);// 获得每i行的所有单元格（返回的是数组）
					if (cells[0].getContents() != null
							&& cells[0].getContents().trim() != "") {
						String j = cells[9].getContents();// 供应商编号
						if (j != null) {
							j = j.trim();
						}
						if (j == null && "".equals(j)) {
							errorCount++;
							strb.append("第" + (i + 1) + "行,无供应商编号。\\n");
							return strb.toString();
						}
						String hql = "select id,name from ZhUser where userid = (select id from Users where code=?)";
						Object[] zhObject = null;
						try {
							zhObject = (Object[]) totalDao
									.getObjectByCondition(hql, j);
						} catch (Exception e3) {
							errorCount++;
							strb.append("第" + (i + 1) + "行,编号为" + j
									+ "的供应商在员工管理中存在多个,请保留一条即可!\\n");
							// return strb.toString();
						}
						if (zhObject == null || zhObject.length < 1) {
							errorCount++;
							strb.append("第" + (i + 1)
									+ "行,供应商编号，查询不到。可能不存在登陆用户信息！\\n");
							// return strb.toString();
						}

						Integer zhuserId = (Integer) zhObject[0];
						String zhuserName = (String) zhObject[1];

						String a = cells[1].getContents();// 件号
						String b = cells[2].getContents();// 名称
						String c = cells[3].getContents();// 规格
						String d = cells[4].getContents();// 税率
						String e = cells[5].getContents();// 含税价
						String f = cells[6].getContents();// 不含税价
						String g = cells[7].getContents();// 型别
						String h = cells[8].getContents();// 合同编号
						String k = cells[10].getContents();// 开始时间
						String l = cells[11].getContents();// 失效时间
						String m = cells[12].getContents();// 起始数量
						String n = cells[13].getContents();// 截止数量
						String cgbl = cells[14].getContents();// 采购比例
						String kgliao = "TK";
						if (cells.length >= 16) {
							kgliao = cells[15].getContents();// 供料属性/工序号)
						}
						if (a != null) {
							a = a.trim();
						}
						if (b != null) {
							b = b.trim();
						}
						if (kgliao != null) {
							kgliao = kgliao.trim();
						}

						// banbenhao
						String remark = null;// 备注
						String q = null;
						String banbenNumber = null;// 版本号/备注
						try {
							banbenNumber = cells[16].getContents();// 版本号
						} catch (Exception e3) {
							// TODO Auto-generated catch block
							e3.printStackTrace();
						}
						if ("外委".equals(type)) {
							try {
								remark = cells[16].getContents();// 备注
								q = cells[17].getContents();// 外委类型
							} catch (Exception e2) {
							}
						}
						Float zdqdl = 0f;
						Float zdzxl = 0f;
						Float zdqsl = 0f;
						try {
							String zdqdl_str = cells[17].getContents();
							if (zdqdl_str != null && zdqdl_str.length() > 0) {
								zdqdl = Float.parseFloat(zdqdl_str);
							}
						} catch (Exception e1) {
							zdqdl = 0F;
						}
						try {
							String zdzxl_str = cells[18].getContents();
							if (zdzxl_str != null && zdzxl_str.length() > 0) {
								zdzxl = Float.parseFloat(zdzxl_str);
							}
						} catch (Exception e1) {
							zdzxl = 0F;
						}
						try {
							String zdqsl_str = cells[19].getContents();
							if (zdqsl_str != null && zdqsl_str.length() > 0) {
								zdqsl = Float.parseFloat(zdqsl_str);
							}
						} catch (Exception e1) {
							zdqsl = 0F;
						}
						Price price = new Price();
						String partNumber = a;
						YuanclAndWaigj yaw = null;
						if ("外购".equals(type) || "辅料".equals(type)) {
							price.setZdqdl(zdqdl);
							price.setZdzxl(zdzxl);
							price.setZdqsl(zdqsl);
							if (partNumber != null && !"".equals(partNumber)) {
								if ("外购".equals(type)) {
									String banbensql = "";
									if (banbenNumber != null
											&& banbenNumber.length() > 0) {
										banbensql = " and banbenhao ='"
												+ banbenNumber + "'";
									}
									yaw = (YuanclAndWaigj) totalDao
											.getObjectByCondition(
													" from YuanclAndWaigj where markId =? "
															+ banbensql
															+ " and  kgliao = ? and (banbenStatus is null or banbenStatus='默认')",
													partNumber, kgliao);
									if (yaw == null) {
										strb
												.append("第"
														+ (i + 1)
														+ "行,件号:"
														+ partNumber
														+ "版本:"
														+ banbenNumber
													  	+ "供料属性:"
														+ kgliao
														+ "。"
														+ "在外购件库中未找到。请检查件号,版本,供料属性是否与外购件库一致。\\n");
										return strb.toString();
									} else {
										price.setName(yaw.getName());
										price.setSpecification(yaw
												.getSpecification());
										price.setBanbenhao(yaw.getBanbenhao());
										price.setWlType(yaw.getWgType());

									}
								}else if("辅料".equals(type)){
									OaAppDetailTemplate oa = (OaAppDetailTemplate) totalDao.getObjectByCondition(" from OaAppDetailTemplate where " +
											" wlcode =? ", partNumber);
									if(oa!=null){
										price.setName(oa.getDetailAppName());
										price.setSpecification(oa.getDetailFormat());
										price.setBanbenhao(oa.getBanben());
										price.setWlType(oa.getDetailChildClass().replaceAll("[0-9.]*", ""));
									}
									

								}

							} else {
								strb.append("第" + (i + 1) + "行,件号未填写\\n");
								return strb.toString();
							}
						}

						if (k != null && !"".equals(k)) {
							k = k.replaceAll("/", "-");
							Date datecheck = Util.StringToDate(k, "yyyy-MM-dd");
							if (datecheck != null) {
								k = Util.DateToString(datecheck, "yyyy-MM-dd");
								if (l != null && !"".equals(l)) {
									l = l.replaceAll("/", "-");
									datecheck = Util.StringToDate(l,
											"yyyy-MM-dd");
									if (datecheck != null) {
										l = Util.DateToString(datecheck,
												"yyyy-MM-dd");
									} else {
										errorCount++;
										strb
												.append("第"
														+ (i + 1)
														+ "行,价格失效时间格式错误,标准格式(yyyy-MM-dd)\\n");
										return strb.toString();
									}
								}
							} else {
								errorCount++;
								strb.append("第" + (i + 1)
										+ "行,价格开始时间格式错误,标准格式(yyyy-MM-dd)\\n");
								return strb.toString();
							}
						} else {
							errorCount++;
							strb.append("第" + (i + 1)
									+ "行,价格开始时间未填写,标准格式(yyyy-MM-dd)\\n");
							return strb.toString();
						}
						if (l != null && l.length() > 0) {
							boolean bool = Util.compareTime(k, "yyyy-MM-dd", l,
									"yyyy-MM-dd");
							if (bool) {
								errorCount++;
								strb.append("第" + (i + 1) + "行,价格结束时间" + l
										+ ",在价格开始时间" + k + "之前\\n");
								return strb.toString();
							}
						}
						price.setPartNumber(partNumber);

						price.setName(b);
						price.setSpecification(c);
						try {
							if (d == null || "".equals(d)) {
								// 读取供应商税率信息
								ZhUser zhuser = (ZhUser) totalDao
										.getObjectById(ZhUser.class, zhuserId);
								if (zhuser != null) {
									d = zhuser.getZzsl().replaceAll(" ", "")
											.replaceAll("%", "");
								}
							}
							if (d != null && !"".equals(d)) {
								double tax = Double.parseDouble(d.replaceAll(
										" ", "").replaceAll("%", ""));
								price.setTaxprice(tax);
								if (e != null && !"".equals(e) && f != null
										&& !"".equals(f)) {
									price.setHsPrice(Double.parseDouble(e));
									price.setBhsPrice(Double.parseDouble(f));
								} else if ("".equals(e) && !"".equals(f)) {
									double bhsprice = Double.parseDouble(f);
									double hsprice = bhsprice
											* (1 + (tax / 100));
									price.setBhsPrice(bhsprice);
									price.setHsPrice(hsprice);
								} else if ("".equals(f) && !"".equals(e)) {
									price.setHsPrice(Double.parseDouble(e));
									double hsprice = Double.parseDouble(e);
									double bhsprice = hsprice
											/ (1 + (tax / 100));
									price.setBhsPrice(bhsprice);
								}
							} else {
								strb.append("第" + (i + 1) + "行,未查询到税率。\\n");
								return strb.toString();
							}
						} catch (Exception e1) {
							errorCount++;
							strb.append("第" + (i + 1) + "行,价格/税率存在问题。\\n");
							e1.printStackTrace();
							return strb.toString();
						}
						if (price.getHsPrice() == null) {
							strb.append("第" + (i + 1) + "行,价格/税率存在问题。\\n");
							return strb.toString();
						}

						String time = Util.getDateTime();
						Float cgbl1 = 100f;
						if (cgbl != null && cgbl.length() > 0) {
							try {
								cgbl1 = Float.parseFloat(cgbl);
							} catch (Exception e2) {
							}
						}
						String hql_cgbl = "select sum(cgbl) from GysMarkIdjiepai where markId=? and kgliao=? and zhuserId<>?";
						if (banbenNumber != null && banbenNumber.length() > 0) {
							hql_cgbl += " and banBenNumber = '" + banbenNumber
									+ "'";
						} else {
							hql_cgbl += " and (banBenNumber = '' or banBenNumber is null)";
						}
						float sumcgbl = (Float) totalDao.getObjectByCondition(
								hql_cgbl, partNumber, kgliao, zhuserId);
						if (sumcgbl > 0) {
							if ((sumcgbl + cgbl1) <= 100) {

							} else {
								cgbl1 = 0F;
							}
						} else {
							if (cgbl1 <= 100) {

							} else {
								cgbl1 = 0F;
							}
						}
						price.setType(g);
						price.setKgliao(kgliao);
						price.setContractNumber(h);
						price.setGysId(zhuserId);
						price.setPricePeriodStart(k);
						price.setPricePeriodEnd(l);
						if ("辅料".equals(type)) {
							price.setProductCategory("辅料");
						} else {
							price.setProductCategory("零件");
						}
						price.setProduceType(type);
						try {
							price.setFirstnum(Integer.parseInt(m));
						} catch (Exception e1) {
							m = "0";
							price.setFirstnum(0);
						}
						try {
							price.setEndnum(Integer.parseInt(n));
						} catch (Exception e1) {
							price.setEndnum(0);
							n = "0";
						}
						price.setWriteDate(Util.getDateTime());
						price.setRmarks(remark);
						price.setWwType(q);
						// 外委模板里面 kgliao 代表工序号
						if ("外委".equals(type) && kgliao != null
								&& kgliao.length() > 0) {
							try {
								Integer procardTId = (Integer) totalDao
										.getObjectByCondition(
												"select id from ProcardTemplate where markId=? and (banbenStatus is null or banbenStatus!='历史') and (dataStatus is null or dataStatus!='删除')",
												a);
								if (procardTId != null) {
									String ragex = "[\\r\\n\\t\040:：;；&$!~@^#,+、]+";
									String[] processNames = kgliao.split(ragex);
									StringBuffer sb = new StringBuffer();
									Map<String, Integer> processNoCountmap = new HashMap<String, Integer>();
									for (String processName : processNames) {
										String hql_gxnum = " select processNO from ProcessTemplate where procardTemplate.id=?  and processName =?";
										List<Integer> gongxuNumList = totalDao
												.query(hql_gxnum, procardTId,
														processName);
										if (gongxuNumList == null
												|| gongxuNumList.size() == 0) {
											strList.add(i + 1);
											price.setGongxunum("待填充");// 在选择工序外委，生成外委的时候出发更改
											break;
										} else {
											Integer processNoCount = processNoCountmap
													.get(processName);
											if (processNoCount == null) {
												processNoCount = 0;
											}
											if (gongxuNumList.size() >= processNoCount) {
												if (sb.length() == 0) {
													sb
															.append(gongxuNumList
																	.get(processNoCount));
												} else {
													sb
															.append(";"
																	+ gongxuNumList
																			.get(processNoCount));
												}
												processNoCount++;
												processNoCountmap.put(
														processName,
														processNoCount);
											} else {
												price.setGongxunum("待填充");// 在选择工序外委，生成外委的时候出发更改
												break;
											}
										}

									}
									if (price.getGongxunum() != null
											&& price.getGongxunum().equals(
													"待填充")) {
									} else {
//										String[] strArry = sb.toString().split(
//												";");
//										if (strArry != null
//												&& strArry.length > 0) {
//											Arrays.sort(strArry);
//											for (int i1 = 1; i1 < strArry.length; i1++) {
//												Integer num0 = Integer
//														.parseInt(strArry[i1 - 1]);
//												Integer num1 = Integer
//														.parseInt(strArry[i1]);
//												if (Math.abs(num0 - num1) > 1) {
//													strb
//															.append("第"
//																	+ (i + 1)
//																	+ "行,工序号出现跳层问题。工序号为:"
//																	+ Arrays
//																			.toString(strArry)
//																	+ "\\n");
//													errorCount++;
//													continue out;
//												}
//											}
//										}
										price.setGongxunum(sb.toString());
									}
								}
								price.setProcessNames(kgliao);
							} catch (Exception e2) {
								// TODO: handle exception
							}
						}

						String hql_p1 = "";
						if ("外购".equals(type)) {
							hql_p1 = " from Price where partNumber='"
									+ partNumber
									+ "'  and gysId="
									+ zhuserId
									+ " and kgliao='"
									+ kgliao
									+ "'"
									+ " and  pricePeriodStart<= '"
									+ time
									+ "' and(pricePeriodEnd>= '"
									+ time
									+ "' or pricePeriodEnd is null or pricePeriodEnd = '') and firstnum="
									+ m + " and endnum=" + n;
							if (banbenNumber != null
									&& banbenNumber.length() > 0) {
								hql_p1 += " and banbenhao = '" + banbenNumber
										+ "'";
							} else {
								hql_p1 += " and (banbenhao = '' or banbenhao is null)";
							}
						} else if ("外委".equals(type)) {
							String hql_gongxu = "";
							if ("待填充".equals(price.getGongxunum())) {
								hql_gongxu = " and (gongxunum = '待填充' or gongxunum is null or  gongxunum = '' )";
							} else {
								hql_gongxu = " and gongxunum = '"
										+ price.getGongxunum() + "'";
							}
							hql_p1 = "from Price where partNumber='"
									+ partNumber
									+ "' and gysId="
									+ zhuserId
									+ " and wwType='"
									+ q
									+ "' "
									+ hql_gongxu
									+ " and  pricePeriodStart<= '"
									+ time
									+ "' and (pricePeriodEnd>= '"
									+ time
									+ "' or pricePeriodEnd is null or pricePeriodEnd = '')";
							if (banbenNumber != null
									&& banbenNumber.length() > 0) {
								hql_p1 += " and banbenhao = '" + banbenNumber
										+ "'";
							} else {
								hql_p1 += " and (banbenhao = '' or banbenhao is null)";
							}
						}
						if (hql_p1.length() > 0) {
							Price p1 = (Price) totalDao
									.getObjectByCondition(hql_p1);
							if (p1 != null) {
								try {
									Date date = Util.getCalendarDate(
											new Date(), -1);// 减一天
									p1.setPricePeriodEnd(Util.DateToString(
											date, "yyyy-MM-dd"));
									totalDao.update(p1);
								} catch (ParseException e1) {
									e1.printStackTrace();

								}
							}
						}
						price.setCgbl(cgbl1.doubleValue());
						if (totalDao.save(price)) {
							ZhUser zhuser = (ZhUser) totalDao.get(ZhUser.class,
									zhuserId);
							if ("外购".equals(type)) {
								String hql_gsm = " from GysMarkIdjiepai where zhuserId =? and markId=? and kgliao=? ";
								if (banbenNumber != null
										&& banbenNumber.length() > 0) {
									hql_gsm += " and  banBenNumber = '"
											+ banbenNumber + "'";
								} else {
									hql_gsm += " and  (banBenNumber = '' or banBenNumber is null ) ";

								}
								GysMarkIdjiepai gsm = (GysMarkIdjiepai) totalDao
										.getObjectByCondition(hql_gsm,
												zhuserId, partNumber, kgliao);
								String wgType = zhuser.getCclass();
								if (wgType != null
										&& wgType.length() > 0
										&& wgType.indexOf(price.getWlType()) == -1) {
									wgType += ";" + price.getWlType();
								} else {
									wgType = price.getWlType();
								}
								zhuser.setCclass(wgType);
								// 外购件 自动绑定物料类别和供应商关系;
								Set<Category> categorySet = zhuser
										.getCategoryset();
								Category category = (Category) totalDao
										.getObjectByCondition(
												" from Category where name = ? ",
												price.getWlType());
								if (categorySet != null
										&& categorySet.size() > 0) {
									if (!categorySet.contains(category)) {
										if (category != null) {
											categorySet.add(category);
										}
									}
								} else {
									categorySet = new HashSet<Category>();
									if (category != null) {
										categorySet.add(category);
									}
								}
								zhuser.setCategoryset(categorySet);
								totalDao.update(zhuser);

								// 新添的
								if (yaw != null) {// 更新外购件库价格状态
									yaw.setPricestatus("有效");
									totalDao.update(yaw);
								}
								if (gsm == null && "外购".equals(type)) {
									gsm = new GysMarkIdjiepai();
									gsm.setZhuserId(zhuserId);
									gsm.setGys(zhuserName);
									gsm.setStatus("等待填充");
									gsm.setUserId(zhuserId);
									gsm.setMarkId(partNumber);
									gsm.setProName(price.getName());
									gsm.setMaxCount(0f);
									gsm.setProcardStyle("总成");
									gsm.setProductStyle(price.getType());
									gsm.setFatherId(0);
									gsm.setBelongLayer(1);
									gsm.setAddDateTime(Util.getDateTime());
									gsm.setDailyoutput(0f);
									gsm.setOnePrice(0D);
									gsm.setCgbl(cgbl1);
									gsm.setBanBenNumber(price.getBanbenhao());
									gsm.setKgliao(kgliao);
									totalDao.save(gsm);
									gsm.setRootId(gsm.getId());
									totalDao.update(gsm);
								} else if ("外购".equals(type)) {
									gsm.setCgbl(cgbl1);
									totalDao.update(gsm);
								}
							}
						}
					} else {
						errorCount++;
						strb.append("第" + (i + 1) + "行,件号无数据。\\n");
						return strb.toString();
					}
					successCount++;
					if (i % 200 == 0) {
						totalDao.clear();
					}
				}

				is.close();// 关闭流
				wk.close();// 关闭工作薄
				msg = "已成功导入" + successCount + "个\\n失败" + errorCount + "个\\n重复"
						+ cfCount + "个\\n失败的行数分别为：\\n" + strb.toString();
			} else {
				msg = "没有获取到行数";
			}

		} catch (Exception e) {
			e.printStackTrace();
			msg = "导入出错,请截屏发给管理员!\\n" + e.getMessage() + (i + 1) + "行" + msg
					+ strb.toString();
		}
		return msg;

	}

	@Override
	public List<String> findSpecification(String markId) {
		String hql = "select specification  from YuanclAndWaigj where markId =? and specification <> ''";
		return totalDao.query(hql, markId);
	}

	@Override
	public float findsumcgbl(String partNumber) {
		String time = Util.getDateTime("yyyy-MM-dd");
		String hql = "select sum(cgbl) from Price where partNumber=? and '"
				+ time
				+ "'>= pricePeriodStart and ('"
				+ time
				+ "'<= pricePeriodEnd or pricePeriodEnd = '' or pricePeriodEnd is null)";
		Float cgbl = (Float) totalDao.getObjectByCondition(hql, partNumber);
		return cgbl;
	}

	@Override
	public Price findeprice(Price price) {
		if (price != null) {
			String hql = "from Price where partNumber=? and name=? ";
			String time = Util.getDateTime("yyyy-MM-dd");
			if ("总成".equals(price.getProductCategory())) {
				hql += " and '"
						+ time
						+ "'>= pricePeriodStart and ('"
						+ time
						+ "'<= pricePeriodEnd or pricePeriodEnd = '' or pricePeriodEnd is null)";
				return (Price) totalDao.getObjectByCondition(hql, price
						.getPartNumber(), price.getName());
			} else {
				hql += " and specification=? and '"
						+ time
						+ "'>= pricePeriodStart and('"
						+ time
						+ "'<= pricePeriodEnd or pricePeriodEnd = '' or pricePeriodEnd is null)";
				return (Price) totalDao.getObjectByCondition(hql, price
						.getPartNumber(), price.getName(), price
						.getSpecification());
			}

		}

		return null;
	}

	@Override
	public List<YuanclAndWaigj> getAllNames(YuanclAndWaigj yuanclAndWaigj) {
		if (yuanclAndWaigj != null) {
			String sql = "";
			if (yuanclAndWaigj.getMarkId() != null
					&& yuanclAndWaigj.getMarkId().length() > 0) {
				sql = "from YuanclAndWaigj where markId = '"
						+ yuanclAndWaigj.getMarkId()
						+ "' and (banbenStatus = '默认' or banbenStatus is null) and (status not in ('禁用') or status is  null)";
			}
			if (yuanclAndWaigj.getProductStyle() != null
					&& yuanclAndWaigj.getProductStyle().length() > 0) {
				sql += " and productStyle = '试制'";
			}
			if (yuanclAndWaigj.getKgliao() != null
					&& yuanclAndWaigj.getKgliao().length() > 0) {
				sql += " and kgliao like '" + yuanclAndWaigj.getKgliao() + "'";
			}
			return (List<YuanclAndWaigj>) totalDao.query(sql);
		}
		return null;
	}

	// 检索原材料外购件信息
	@Override
	public List<YuanclAndWaigj> searchAllNames(YuanclAndWaigj yuanclAndWaigj) {
		if (yuanclAndWaigj.getMarkId() != null) {
			String[] strs = yuanclAndWaigj.getMarkId().split(":");
			String kgliao = "";
			if (null != yuanclAndWaigj.getWgType()
					&& !"".equals(yuanclAndWaigj.getKgliao())) {
				kgliao = " and kgliao = '" + yuanclAndWaigj.getKgliao() + "'";
			}
			if (strs.length >= 2) {
				String markId = strs[0];
				String name = strs[1];
				String sql = "from YuanclAndWaigj where (status is null or status !='禁用') and markId like '%"
						+ markId
						+ "%' and name like '%"
						+ name
						+ "%' "
						+ kgliao;
				return (List<YuanclAndWaigj>) totalDao.query(sql);
			} else {
				String sql = "from YuanclAndWaigj where (status is null or status !='禁用') and (markId like '"
						+ yuanclAndWaigj.getMarkId()
						+ "%' or name like '%"
						+ yuanclAndWaigj.getMarkId() + "%') " + kgliao;
				return (List<YuanclAndWaigj>) totalDao
						.findAllByPage(sql, 1, 15);

			}
		}
		return null;
	}

	@Override
	public Object[] getpdtList(ProcardTemplate procardTemplate) {

		if (procardTemplate == null) {
			procardTemplate = new ProcardTemplate();
		}
		String hql = "from ProcardTemplate where (dataStatus is null or dataStatus!='删除') and (banbenStatus is null or banbenStatus !='历史') ";
		if (procardTemplate.getMarkId() != null
				&& procardTemplate.getMarkId().length() > 0) {
			hql += " and (markId like '%" + procardTemplate.getMarkId()
					+ "%' or ywMarkId like '%" + procardTemplate.getMarkId()
					+ "%')";
		}
		ProcardTemplate pt = (ProcardTemplate) totalDao
				.getObjectByCondition(hql);
		List<ProcessTemplate> list = new ArrayList<ProcessTemplate>();
		list = getgongxunum(pt);
		Object[] obj = { pt, list };
		return obj;
	}

	@Override
	public List<ProcessTemplate> getgongxunum(ProcardTemplate pt) {
		if (pt != null && pt.getMarkId() != null) {
			String hql = " from ProcessTemplate where procardTemplate.id =? order by processNO";
			return totalDao.query(hql, pt.getId());
		}
		return null;

	}

	@Override
	public List<Object[]> findZhUserIdAndName() {
		// TODO Auto-generated method stub
		String hql = "select id,cmp from ZhUser where blackliststauts = '正常使用'";
		return totalDao.find(hql);
	}

	@Override
	public ZhUser finfZhuserByid(Integer id) {
		return (ZhUser) totalDao.get(ZhUser.class, id);
	}

	@Override
	public List<String> getAllHtNum() {
		String hql = "select A.contract_num from BarContract A  join A.SetbarContractDetails B"
				+ " where A.contract_source = '零部件及工序外委采购' and B.gx_producetype ='外购' group by A.contract_num order by A.contract_num desc";
		return (List<String>) totalDao.query(hql);
	}

	@Override
	public BarContract getBarContractByNum(String contract_num) {
		String hql = " from BarContract where contract_num = ?";
		BarContract bt = (BarContract) totalDao.getObjectByCondition(hql,
				contract_num);
		List<BarContractDetails> bdList = new ArrayList<BarContractDetails>();
		Set<BarContractDetails> bdset = bt.getSetbarContractDetails();
		for (BarContractDetails barContractDetails : bdset) {
			YuanclAndWaigj wgj = (YuanclAndWaigj) totalDao
					.getObjectByCondition(
							" from YuanclAndWaigj where markId = ? and wgType is not null ",
							barContractDetails.getGx_number());
			if (wgj != null) {
				barContractDetails.setWgType(wgj.getWgType());
			}
			bdList.add(barContractDetails);
		}
		ZhUser zhuser = null;
		if (bt.getSupplier() != null && bt.getSupplier().length() > 0
				&& bt.getGysId() == null) {
			zhuser = (ZhUser) totalDao.getObjectByCondition(
					" from ZhUser where cmp = ? ", bt.getSupplier());
		}
		if (bt.getGysId() != null) {
			zhuser = (ZhUser) totalDao.get(ZhUser.class, bt.getGysId());
		}
		if (zhuser != null) {
			bt.setGyscode(zhuser.getUsercode());
			bt.setGysId(zhuser.getId());
		}
		bt.setBcdList(bdList);
		return bt;
	}

	@Override
	public Map<String, Object> getProcardMx(Integer id) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (id != null && id > 0) {
			Price price = (Price) totalDao.get(Price.class, id);
			map.put("price", price);
			if (price.getContractNumber() != null
					&& !"".equals(price.getContractNumber())) {
				BarContract barContract = (BarContract) totalDao
						.getObjectByCondition(
								"from BarContract where  contract_num = ? ",
								price.getContractNumber());
				if (barContract != null) {
					map.put("barContract", barContract);
				}
			}
		}
		return map;
	}

	@Override
	public ZhuserOffer findZhuserOfferById(Integer id) {
		if (id != null && id > 0) {
			return (ZhuserOffer) totalDao.get(ZhuserOffer.class, id);
		}
		return null;
	}

	@Override
	public String addAppLIPrice(Integer id) {
		// TODO Auto-generated method stub
		if (id != null && id > 0) {
			Price price = (Price) totalDao.getObjectById(Price.class, id);
			if (price != null) {
				if (price.getAttachmentName() != null
						&& !"".equals(price.getAttachmentName())) {
					AppLiPrice app = new AppLiPrice();
					app.setAddTime(Util.getDateTime());
					app.setAppStratu("未审批");
					app.setMarkId(price.getPartNumber());
					app.setId(id);
					app.setPriceName(price.getName());
					app.setPriceUrl(price.getAttachmentName());
					Users u = Util.getLoginUser();
					app.setShenqingName(u.getName());
					app.setShenqingUserId(u.getId());
					totalDao.save(app);
					// 调用审批流程
					String processName = "合同附件查看申请";
					Integer epId = null;
					try {
						epId = CircuitRunServerImpl.createProcess(processName,
								AppLiPrice.class, app.getId(), "appStratu",
								"id", u.getName() + "申请" + price.getName()
										+ "合同附件查看，请您审批！", true);
						if (epId != null && epId > 0) {
							app.setEpId(epId);
							totalDao.update(app);
							return "申请成功";
						}
					} catch (Exception e) {
						return "没有申请流程，申请失败";
					}
				} else
					return "附件为空，申请失败";
			} else
				return "合同为空，申请失败";
		}
		return "申请失败";
	}

	@Override
	public Map<Integer, Object> findAllAppLiPrice(AppLiPrice appLiPrice,
			int pageNo, int pageSize, String status) {
		if (appLiPrice == null) {
			appLiPrice = new AppLiPrice();
		}
		String sql = null;
		if ("person".equals(status)) {
			Users u = Util.getLoginUser();
			sql = "shenqingUserId = '" + u.getId() + "'";
		} else {
			sql = null;
		}
		String hql = totalDao.criteriaQueries(appLiPrice, sql);
		hql += " order by id desc";
		int count = totalDao.getCount(hql);
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		List objs = totalDao.findAllByPage(hql, pageNo, pageSize);
		map.put(1, objs);
		map.put(2, count);
		return map;
	}

	@Override
	public ZhUser findZhuserBygys(String gys) {
		if (gys != null && !"".equals(gys)) {
			String hql = " from ZhUser where usercode like '%" + gys
					+ "%' or cmp like '%" + gys + "%' ";
			return (ZhUser) totalDao.getObjectByCondition(hql);
		}
		return null;
	}

	@Override
	public ProcardTemplate findZCPtName(String markId) {
		if (markId != null && markId.length() > 0) {
			YuanclAndWaigj yuanclAndWaigj = (YuanclAndWaigj) totalDao
					.getObjectByCondition("from YuanclAndWaigj where markId=?",
							markId);

			String hql = " from ProcardTemplate where ( markId = ? or ywMarkId=?)  and procardStyle in ('总成','自制')";

			ProcardTemplate procardTemplate = (ProcardTemplate) totalDao
					.getObjectByCondition(hql, markId, markId);
			if (yuanclAndWaigj != null) {
				if (procardTemplate == null) {
					procardTemplate = new ProcardTemplate();
					procardTemplate.setMarkId(yuanclAndWaigj.getMarkId());
					procardTemplate.setSpecification(yuanclAndWaigj
							.getSpecification());
					procardTemplate.setProName(yuanclAndWaigj.getName());
					procardTemplate.setBanBenNumber(yuanclAndWaigj
							.getBanbenhao());
				}
				procardTemplate.setUnit(yuanclAndWaigj.getUnit());
			}
			return procardTemplate;
		}
		return null;
	}

	@Override
	public boolean getAccessPermission() {
		// TODO Auto-generated method stub
		// 获取是网络权限设置
		String hql1 = "select valueCode from CodeTranslation where type = 'sys' and keyCode='价格访问权限' and valueName='是否开启外网访问'";
		String valueCode = (String) totalDao.getObjectByCondition(hql1);
		if (valueCode != null && valueCode.equals("否")) {// 不允许外部网络访问
			// 判断网络是否为外部网络
			HttpServletRequest request = ServletActionContext.getRequest();
			// 获得真实ip
			String ip = request.getHeader("x-forwarded-for");
			if (ip == null || ip.length() == 0
					|| "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0
					|| "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0
					|| "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
			}
			byte[] addr = IPAddressUtil.textToNumericFormatV4(ip);
			return internalIp(addr);
		}

		return true;
	}

	public static boolean internalIp(byte[] addr) {
		final byte b0 = addr[0];
		final byte b1 = addr[1];
		// 10.x.x.x/8
		final byte SECTION_1 = 0x0A;
		// 172.16.x.x/12
		final byte SECTION_2 = (byte) 0xAC;
		final byte SECTION_3 = (byte) 0x10;
		final byte SECTION_4 = (byte) 0x1F;
		// 192.168.x.x/16
		final byte SECTION_5 = (byte) 0xC0;
		final byte SECTION_6 = (byte) 0xA8;
		switch (b0) {
		case SECTION_1:
			return true;
		case SECTION_2:
			if (b1 >= SECTION_3 && b1 <= SECTION_4) {
				return true;
			}
		case SECTION_5:
			switch (b1) {
			case SECTION_6:
				return true;
			}
		default:
			return false;
		}
	}

	@Override
	public void exportExcel(Price price, String status) {

		if (price == null) {
			price = new Price();
		}
		Map<Integer, Object> map = findPriceByCondition(price, 0, 0, status,
				null);
		List<Price> priceList = new ArrayList<Price>();
		if (map != null && map.size() > 0) {
			priceList = (List<Price>) map.get(1);
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
			ws.addCell(new Label(1, 0, "产品类别"));
			ws.addCell(new Label(2, 0, "生产类型"));
			ws.addCell(new Label(3, 0, "供料属性"));
			ws.addCell(new Label(4, 0, "件号"));
			ws.addCell(new Label(5, 0, "版本"));
			ws.addCell(new Label(6, 0, "物料类别"));
			ws.addCell(new Label(7, 0, "名称"));
			ws.addCell(new Label(8, 0, "工序号"));
			ws.addCell(new Label(9, 0, "工序名"));
			ws.addCell(new Label(10, 0, "外委类型"));
			ws.addCell(new Label(11, 0, "签订方"));
			ws.addCell(new Label(12, 0, "价格(含税)"));
			ws.addCell(new Label(13, 0, "价格(不含税)"));
			ws.addCell(new Label(14, 0, "合同编号"));
			ws.addCell(new Label(15, 0, "价格有效期起"));
			ws.addCell(new Label(16, 0, "止"));
			ws.addCell(new Label(17, 0, "起始数量"));
			ws.addCell(new Label(18, 0, "截止数量"));
			ws.addCell(new Label(19, 0, "最低起订量"));
			ws.addCell(new Label(20, 0, "最低装箱量"));
			ws.addCell(new Label(21, 0, "最低起送量"));
			ws.addCell(new Label(22, 0, "图号"));
			ws.addCell(new Label(23, 0, "规格"));
			ws.addCell(new Label(24, 0, "单位"));
			for (int i = 0; i < priceList.size(); i++) {
				Price price1 = (Price) priceList.get(i);
				ws.addCell(new Label(0, i + 1, i + 1 + ""));
				ws.addCell(new Label(1, i + 1, price1.getProductCategory()));
				ws.addCell(new Label(2, i + 1, price1.getProduceType()));
				ws.addCell(new Label(3, i + 1, price1.getKgliao()));
				ws.addCell(new Label(4, i + 1, price1.getPartNumber()));
				ws.addCell(new Label(5, i + 1, price1.getBanbenhao()));
				ws.addCell(new Label(6, i + 1, price1.getWlType()));
				ws.addCell(new Label(7, i + 1, price1.getName()));
				ws.addCell(new Label(8, i + 1, price1.getGongxunum()));
				ws.addCell(new Label(9, i + 1, price1.getProcessNames()));
				ws.addCell(new Label(10, i + 1, price1.getWwType()));
				ws.addCell(new Label(11, i + 1, price1.getQidingfang()));
				ws.addCell(new Label(12, i + 1,
						price1.getHsPrice() == null ? "" : price1.getHsPrice()
								+ ""));
				ws.addCell(new Label(13, i + 1,
						price1.getBhsPrice() == null ? "" : price1
								.getBhsPrice()
								+ ""));
				ws.addCell(new Label(14, i + 1, price1.getContractNumber()));
				ws.addCell(new Label(15, i + 1, price1.getPricePeriodStart()));
				ws.addCell(new Label(16, i + 1, price1.getPricePeriodEnd()));
				ws.addCell(new Label(17, i + 1,
						price1.getFirstnum() == null ? "" : price1
								.getFirstnum()
								+ ""));
				ws.addCell(new Label(18, i + 1, price1.getEndnum() == null ? ""
						: price1.getEndnum() + ""));
				ws.addCell(new Label(19, i + 1, price1.getZdqdl() == null ? ""
						: price1.getZdqdl() + ""));
				ws.addCell(new Label(20, i + 1, price1.getZdzxl() == null ? ""
						: price1.getZdzxl() + ""));
				ws.addCell(new Label(21, i + 1, price1.getZdqsl() == null ? ""
						: price1.getZdqsl() + ""));
				ws.addCell(new Label(22, i + 1, price1.getPicNo()));
				ws.addCell(new Label(23, i + 1, price1.getSpecification()));
				ws.addCell(new Label(24, i + 1, price1.getDanwei()));
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
	public String cgblpd(Price price) {
		if ("外购".equals(price.getProduceType()) && price.getCgbl() != null) {
			String hql_cgbl = "select sum(cgbl) from GysMarkIdjiepai where markId=? and kgliao=? and zhuserId <> ?";
			if (price.getBanbenhao() != null
					&& price.getBanbenhao().length() >= 0) {
				hql_cgbl += " and banBenNumber = '" + price.getBanbenhao()
						+ "'";
			} else {
				hql_cgbl += " and (banBenNumber is null or banBenNumber = '')";
			}
			Float sumcgbl = (Float) totalDao.getObjectByCondition(hql_cgbl,
					price.getPartNumber(), price.getKgliao(), price.getGysId());
			return "改件号剩余可分配配额为:" + (100 - sumcgbl) + "，请勿超过该数据!~";
		}
		return "";
	}

	@Override
	public Map<Integer, Object> findPriceWeiCun(Price price, int parseInt,
			int pageSize, String statue, String tags) {
		// TODO Auto-generated method stub
		if (price == null) {
			price = new Price();
		}
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		String hql = totalDao.criteriaQueries(price, null);
		// if (price != null && price.getContractNumber() != null
		// && price.getContractNumber().length() > 0) {
		// hql += " and contractNumber like '%" + price.getContractNumber()
		// + "%'";
		// }
		hql += " and isGuiDang = 'no' order by id desc";
		List objs = totalDao.findAllByPage(hql, parseInt, pageSize);
		int count = totalDao.getCount(hql);
		map.put(1, objs);
		map.put(2, count);
		return map;
	}

	@Override
	public String querenGuihao(Price price) {
		// TODO Auto-generated method stub
		if (price == null)
			return "信息为空，确认失败！";
		Price price2 = (Price) totalDao.getObjectById(Price.class, price
				.getId());
		if (price2 == null)
			return "不存在，请刷新后重试！";
		if (price.getDanganId() != null && !"".equals(price.getDanganId())) {
			AccessWebcam accessWebcam = (AccessWebcam) totalDao.getObjectById(
					AccessWebcam.class, Integer.parseInt(price.getDanganId()));
			if (accessWebcam != null) {
				Users users = Util.getLoginUser();
				ArchiveUnarchiverAplt aplt = new ArchiveUnarchiverAplt();
				aplt.setCardId(users.getCardId());// 卡号
				aplt.setCqType("存档");
				aplt.setUseType("未使用");
				aplt.setDaName(price2.getName());// 名称
				aplt.setDaNum(price2.getFileNumber());// 文件编号
				aplt.setAddTime(Util.getDateTime());
				aplt.setDaGuiId(accessWebcam.getId());
				aplt.setDaId(price.getId());
				String yz1 = Util.yanNumber(10);
				aplt.setInCodes(yz1);
				aplt.setDaGuiposition(accessWebcam.getCabinetOpenInstruction());
				StringBuffer buffer = new StringBuffer();
				buffer.append("已选择档案柜\n" + accessWebcam.getCabinetNum()
						+ " 存档 " + aplt.getDaName());
				String shixiaodate = Util.getSpecifiedDayAfter(Util
						.getDateTime(), 7);// 后七天
				aplt.setDaGuihao(accessWebcam.getCabinetNum());
				aplt.setShixiaoTime(shixiaodate);
				aplt.setAce_Ip(accessWebcam.getCabinetAccessSim());
				TwoDimensionCode code = new TwoDimensionCode();
				System.out.println(yz1 + "二维码生成" + code.danganQRcode(yz1));
				buffer.append("\n" + " 档案柜 " + aplt.getDaGuihao()
						+ "的存档 "// +" 验证码为："+ yz1
						+ " 二维码地址为：" + AlertMessagesServerImpl.pebsUrl
						+ "/upload/file/daQRcode" + "/" + yz1 + ".png");
				totalDao.save(aplt);
				RtxUtil.sendNotify(users.getCode(), buffer.toString(), "系统消息",
						"0", "0");
				price2.setCunStatus("存档中");
				price2.setDanganId(accessWebcam.getId() + "");// 柜子ID
				price2.setDanganWeizhi(accessWebcam.getCabinetNum());// 柜子编号
				price2.setDanganCunQuStatus("待存");
				price2.setCunRen(users.getName());
				totalDao.update(price2);
				return "确认成功！请前往" + accessWebcam.getCabinetNum() + "扫码存档:"
						+ price2.getName();
			}
		}
		return "确认失败！";
	}

	@Override
	public void loseEfficacyPrice(Price price) {
		if (price == null) {
			price = new Price();
		}
		String time = Util.getDateTime();
		String hql = totalDao.criteriaQueries(price, null);
		hql += " and pricePeriodStart <'"
				+ time
				+ "' and (pricePeriodEnd > '"
				+ time
				+ "' "
				+ " or pricePeriodEnd is null or pricePeriodEnd = '' ) "
				+ " and (produceType in('外购','外委') or productCategory = '总成' ) order by id desc ";
		List<Price> priceList = totalDao.query(hql);
		List<String> strList = new ArrayList<String>();
		int count = 0;
		for (Price p : priceList) {
			if (p.getGongxunum() == null || "".equals(p.getGongxunum())) {
				p.setGongxunum("待填充");
			}
			String str = p.getPartNumber() + ";" + p.getGongxunum() + ";"
					+ p.getGysId() + ";" + p.getWwType();
			if (!strList.contains(str)) {
				count++;
				strList.add(str);
				String hql_p1 = "";
				if ("外委".equals(p.getProduceType())) {
					String hql_gongxu = "";
					if ("待填充".equals(p.getGongxunum())) {
						hql_gongxu = " and (gongxunum = '待填充' or gongxunum is null or  gongxunum = '' )";
					} else {
						hql_gongxu = " and gongxunum = '" + p.getGongxunum()
								+ "'";
					}
					hql_p1 = "from Price where partNumber='"
							+ p.getPartNumber()
							+ "' "
							+ " and gysId="
							+ p.getGysId()
							+ " and wwType='"
							+ p.getWwType()
							+ "'"
							+ hql_gongxu
							+ "  and  pricePeriodStart<= '"
							+ time
							+ "' and (pricePeriodEnd>= '"
							+ time
							+ "' or pricePeriodEnd is null or pricePeriodEnd = '')";
					if (p.getBanbenhao() != null
							&& p.getBanbenhao().length() > 0) {
						hql_p1 += " and banbenhao = '" + p.getBanbenhao() + "'";
					} else {
						hql_p1 += " and (banbenhao = '' or banbenhao is null)";
					}
				} else if ("外购".equals(p.getProduceType())) {
					hql_p1 = " from Price where partNumber='"
							+ p.getPartNumber()
							+ "'"
							+ " and gysId="
							+ p.getGysId()
							+ " and kgliao='"
							+ p.getKgliao()
							+ "'"
							+ " and  pricePeriodStart<= '"
							+ time
							+ "' and(pricePeriodEnd>= '"
							+ time
							+ "' or pricePeriodEnd is null or pricePeriodEnd = '')";
					if (p.getBanbenhao() != null
							&& p.getBanbenhao().length() > 0) {
						hql_p1 += " and banbenhao = '" + p.getBanbenhao() + "'";
					} else {
						hql_p1 += " and (banbenhao = '' or banbenhao is null)";
					}
				} else if ("总成".equals(p.getProductCategory())) {
					hql_p1 = " from Price where partNumber = '"
							+ p.getPartNumber()
							+ "'"
							+ " and kehuId="
							+ p.getKehuId()
							+ " and pricePeriodStart<= '"
							+ time
							+ "' and (pricePeriodEnd>= '"
							+ time
							+ "' or  pricePeriodEnd is null or pricePeriodEnd = '')";
				}
				List<Price> p1List = null;
				try {
					p1List = totalDao.query(hql_p1
							+ " order by writeDate desc ");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				if (p1List != null && p1List.size() > 1) {
					for (int i = 0; i < p1List.size(); i++) {
						if (i == 0) {
							continue;
						}
						Price p2 = p1List.get(i);
						Date date;
						try {
							date = Util.getCalendarDate(new Date(), -1);
							p2.setPricePeriodEnd(Util.DateToString(date,
									"yyyy-MM-dd"));
							totalDao.update(p2);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				if (count % 200 == 0) {
					totalDao.clear();
				}
			}

		}
		System.out.println("执行完了啊 哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈 ");

	}

}
