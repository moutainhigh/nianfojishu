package com.task.entity;

import java.io.Serializable;

/**
 * 项目启动书_技术方案(第二步)
 * @author 马凯
 *
 */
public class ProjectStartClaim  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String claimName;//技术方案上传路径

	private ProjectStart root;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ProjectStart getRoot() {
		return root;
	}

	public void setRoot(ProjectStart root) {
		this.root = root;
	}

	public String getClaimName() {
		return claimName;
	}

	public void setClaimName(String claimName) {
		this.claimName = claimName;
	}
	

}
