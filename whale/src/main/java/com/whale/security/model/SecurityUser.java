package com.whale.security.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.OneToMany;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import org.hibernate.annotations.GenericGenerator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.whale.model.AllImages;
import com.whale.model.Work;

@Entity
@Table(name = "SECURITY_USER")
public class SecurityUser implements Serializable {
	private static final long serialVersionUID = 893813341339889229L;

	@Id
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@GeneratedValue(generator = "system-uuid")
	@Column(name = "ID")
	private String id;

	@Basic(optional = false) // 不为空
	@Column(name = "USER_NAME")
	private String userName;

	@Basic(optional = false) // 不为空
	@Column(name = "USER_PASSWORD")
	private String userPassword;

	@Basic(optional = false) // 不为空
	@Column(name = "EMAIL")
	@Email
	private String email;

	@Column(name = "REG_TIME")
	@Temporal(TemporalType.TIMESTAMP)//实体类会封装成完整的时间“yyyy-MM-dd hh:MM:ss”的 Date类型
	private Date regTime = new Date();
	
	@Column(name = "SRC_IMG")
	private String srcImg;
	//@OneToMany (mappedBy = "Articles"),mappedBy指向的是要关联的属性，而不是要关联的类
	@OneToMany(targetEntity=AllImages.class,mappedBy="securityUser")
	@JsonIgnore
	private Set<AllImages> imgList = new HashSet<>();
	//@OneToMany (mappedBy = "Articles"),mappedBy指向的是要关联的属性，而不是要关联的类
	@OneToMany(targetEntity=Work.class,mappedBy="securityUser")
	@JsonIgnore
	private Set<Work> workList = new HashSet<>();

	public Set<AllImages> getImgList() {
		return imgList;
	}

	public void setImgList(Set<AllImages> imgList) {
		this.imgList = imgList;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getRegTime() {
		return regTime;
	}

	public void setRegTime(Date regTime) {
		this.regTime = regTime;
	}

	public String getSrcImg() {
		return srcImg;
	}

	public void setSrcImg(String srcImg) {
		this.srcImg = srcImg;
	}

	public Set<Work> getWorkList() {
		return workList;
	}

	public void setWorkList(Set<Work> workList) {
		this.workList = workList;
	}

	@Override
	public String toString() {
		return "UserInfor [id=" + id + ", userName=" + userName + ", userPassword=" + userPassword + ", email=" + email
				+ "]";
	}

}
