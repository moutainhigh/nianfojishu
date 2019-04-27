package com.task.ServerImpl;

import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

import com.task.Dao.TotalDao;
import com.task.Server.ClientManagementServer;
import com.task.entity.ClientManagement;
import com.task.entity.DeptNumber;
import com.task.entity.PassReal;
import com.task.entity.Password;
import com.task.entity.Users;

public class ClientManagementServerImpl implements ClientManagementServer {

	private TotalDao totalDao;

	public TotalDao getTotalDao() {
		return totalDao;
	}

	public void setTotalDao(TotalDao totalDao) {
		this.totalDao = totalDao;
	}

	// 添加客户信息
	public boolean saveClientManagement(ClientManagement clientManagement) {
		if (clientManagement != null) {
			return this.totalDao.save(clientManagement);
		}
		return false;
	}

	// 查询客户信息
	public List findAllClientManagement(int pageNo, int pageSize) {
		String hql = "from ClientManagement order by clientdatatime desc";
		return this.totalDao.findAllByPage(hql, pageNo, pageSize);
	}

	// 查询客户信息总数
	public Integer clientManagementcount() {
		String hql = "from ClientManagement";
		return this.totalDao.getCount(hql);
	}

	// 根据ID查询客户信息
	public ClientManagement findByID(int id) {
		if (id > 0) {
			return (ClientManagement) this.totalDao.getObjectById(
					ClientManagement.class, id);
		}
		return null;
	}

	// 修改客户信息
	public boolean updateClientManagement(ClientManagement clientManagement) {
		if (clientManagement != null) {
			return this.totalDao.update(clientManagement);
		}
		return false;
	}

	// 删除客户信息
	public boolean deleteClientManagement(ClientManagement clientManagement) {
		if (clientManagement != null) {
			return this.totalDao.delete(clientManagement);
		}
		return false;
	}

	// 条件查询
	public List findconditions(ClientManagement clientManagement, int pageNo,
			int pageSize) {
		if (clientManagement != null) {
			String hql = this.totalDao.criteriaQueries(clientManagement, null,
					null);
			return this.totalDao.findAllByPage(hql, pageNo, pageSize);
		}
		return null;
	}

	// 条件查询总数
	public Integer getcount(ClientManagement clientManagement) {
		if (clientManagement != null) {
			String hql = this.totalDao.criteriaQueries(clientManagement, null,
					null);
			return this.totalDao.getCount(hql);
		}
		return null;
	}

	/**
	 * @Title: add
	 * @Description: 添加客户
	 * @param clientManagement
	 * @return void
	 * @throws
	 */
	public void add(ClientManagement cm) {
		String sql = "select max(number) from ClientManagement ";
		String number = (String) totalDao.getObjectByCondition(sql);
		if (number != null && number.length() > 0) {
			Integer maxnumber = Integer.parseInt(number.substring(2, number
					.length())) + 1;

			String result = "CR"
					+ String.format("%06d", maxnumber);
			cm.setNumber(result);
		} else {
			cm.setNumber("CR000001");
		}
		totalDao.save(cm);
	}

	public static void main(String[] args) {
	}

	/**
	 * @Title: delClientManagementById
	 * @Description: 根据ID删除客户
	 * @param id
	 * @return void
	 * @throws
	 */
	public void delClientManagementById(int id) {
		ClientManagement cm = (ClientManagement) totalDao.getObjectById(
				ClientManagement.class, id);
		totalDao.delete(cm);
	}

	/**
	 * @Title: getClientManagementById
	 * @Description: 根据ID获取
	 * @param id
	 * @return ClientManagement
	 * @throws
	 */
	public ClientManagement getClientManagementById(int id) {
		return (ClientManagement) totalDao.getObjectById(
				ClientManagement.class, id);
	}

	/**
	 * @Title: update
	 * @Description: 修改客户
	 * @param cm
	 * @return void
	 * @throws
	 */
	public void update(ClientManagement cm) {
		totalDao.update(cm);
	}

	/**
	 * @Title: queryClientManager
	 * @Description: 初始化客户信息
	 * @param pageNo
	 * @param pageSize
	 * @return Object[]
	 * @throws
	 */
	public Object[] queryClientManager(int pageNo, int pageSize) {
		String hql = "from ClientManagement";
		List list = totalDao.findAllByPage(hql, pageNo, pageSize);
		int count = totalDao.getCount(hql);
		return new Object[] { list, count };
	}

	/****
	 * 将客户信息转换为用户
	 * 
	 * @return
	 */
	@Override
	public boolean zhClientToUsers() {
		String hql = "from ClientManagement where userId is null";
		List<ClientManagement> list = totalDao.query(hql);
		if (list != null && list.size() > 0) {
			boolean bool = false;
			for (ClientManagement clientManagement : list) {
				Users clientUser = new Users();
				clientUser.setCode(clientManagement.getNumber());
				clientUser.setName(clientManagement.getClientcompanyname()
						+ "-" + clientManagement.getClientname());
				clientUser.setMore(clientManagement.getClientcompanyname());
				clientUser.setSex(clientManagement.getClientsex());
				clientUser.setDuty("客户");
				clientUser.setOnWork("在职");
				clientUser.setUid(clientManagement.getClientcardnumber());
				clientUser.setInternal("否");
				String hql_dn = "from DeptNumber where deptNumber=?";
				DeptNumber deptNumber = (DeptNumber) totalDao
						.getObjectByCondition(hql_dn, "KH");// 查询部门编号
				if (deptNumber == null){
					DeptNumber deptNumber_new =new DeptNumber();
					deptNumber_new.setDept("客户");
					deptNumber_new.setDeptNumber("KH");
					String hql_xuhao = "select max(xuhao) from DeptNumber";
					Integer xuhao = (Integer)totalDao.getObjectByCondition(hql_xuhao);
					if(xuhao!=null){
						deptNumber_new.setXuhao(xuhao+1);
					}
					deptNumber_new.setBelongLayer(1);
					totalDao.save(deptNumber_new);
				}
					clientUser.setDept(deptNumber.getDept());
					clientUser.setDeptId(deptNumber.getId());
					Password password = new Password();
					password.setPassword("e10adc3949ba59abbe56e057f20f883e");// 默认密码(123456)
					password.setDeptNumber("KH");
					password.setPhoneNumber(clientManagement
							.getClientmobilenumber());
					password.setPresentAddress(clientManagement.getAddress());
					password.setUserStatus("完成");
					password.setUser(clientUser);
					clientUser.setPassword(password);
					bool = totalDao.save(clientUser);
					if (bool) {
						PassReal passReal = new PassReal();
						passReal.setRealPass("123456");// 如果身份证号为空，则初始密码为"123456"
						passReal.setUid(clientUser.getId());
						bool = totalDao.save(passReal);
					}
					clientManagement.setUserId(clientUser.getId());
				
			}
			return bool;
		}
		return true;
	}

	/**
	 * @Title: queryClientByCondition
	 * @Description: 根据条件查询
	 * @param map
	 * @param pageNo
	 * @param pageSize
	 * @return Object[]
	 * @throws
	 */
	public Object[] queryClientByCondition(Map map, int pageNo, int pageSize) {
		String hql = "from ClientManagement c where 1 = 1";
		if (map != null && map.size() > 0) {
			if (map.get("nature") != null) {
				hql += " and c.natureOfBusiness = '" + map.get("nature") + "'";
			}
			if (map.get("peopleName") != null) {
				hql += " and c.clientname like '%" + map.get("peopleName")
						+ "%'";
			}
			if (map.get("number") != null) {
				hql += " and c.number like '%" + map.get("number") + "%'";
			}
			if (map.get("companyName") != null) {
				hql += " and c.clientcompanyname like '%"
						+ map.get("companyName") + "%'";
			}
		}
		List list = totalDao.findAllByPage(hql, pageNo, pageSize);
		int count = totalDao.getCount(hql);
		return new Object[] { list, count };
	}

	/**
	 * @Title: queryAllClient
	 * @Description: 查询所有用户
	 * @return List
	 * @throws
	 */
	public List queryAllClient() {
		String hql = "from ClientManagement";
		List<ClientManagement> list = totalDao.query(hql, null);
		return list;
	}

	@Override
	public ClientManagement findByUserID(int id) {
		String hql = "from ClientManagement where userId=?";

		return (ClientManagement) totalDao.getObjectByCondition(hql, id);
	}

}
