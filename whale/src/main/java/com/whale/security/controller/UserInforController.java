package com.whale.security.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.whale.param.UserParam;
import com.whale.security.model.SecurityUser;
import com.whale.security.repository.SecurityUserRepository;

@Controller

@RequestMapping("back/user")
public class UserInforController {
	@Value("${whale.rootSrc}")
	private String fileSrc;
	@Autowired
	private SecurityUserRepository userInforRepostitory;
	@Autowired
	private EntityManager entityManager; // 自动启用一级缓存
	@Autowired
	private PasswordEncoder passwordEncoder;

	@RequestMapping("/list")
	public String list(HttpServletRequest request,Model model) {
		model.addAttribute("F_O", "one");
		model.addAttribute("queryUrl", request.getRequestURI());
		return "back/user/list";
	}

	@RequestMapping("/listInfo")
	@ResponseBody
	public Page<SecurityUser> listInfo(
			@RequestParam(value = "page") Integer page,
			@RequestParam(value = "size", defaultValue = "5") Integer size) {
		PageRequest pageable = PageRequest.of(page, size);
		Page<SecurityUser> findList = userInforRepostitory.findAll(pageable);
		return findList;
	}
	// @RequestMapping("/listInfo")
	// @ResponseBody
	// public String listInfo(ModelMap model, @RequestParam(value = "page",
	// defaultValue = "0") Integer page,
	// @RequestParam(value = "size", defaultValue = "5") Integer size) {
	// // Sort sort = new Sort(Sort.Direction.DESC, "id");
	// // PageRequest pageable = new PageRequest(page, size, sort);
	// // Page<UserInfor> findList = userInforRepostitory.findList(pageable);
	// PageRequest pageable = PageRequest.of(page, size);
	// Page<SecurityUser> findList = userInforRepostitory.findAll(pageable);
	// if (findList.getSize() <= 0) {
	// return "user/list";
	// }
	// model.addAttribute("list", findList);
	// return "user/list";
	// }

	@RequestMapping("/toSaveUser")
	public String user() {
		return "back/user/saveUser";
	}

	@PostMapping("/saveUser")
	@ResponseBody // 返回json数据
	public String add(@Valid UserParam userParam, BindingResult result, MultipartFile srcImg) {
		String errorMsg = "";
		boolean hasErrors = result.hasErrors();
		if (hasErrors) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError e : list) {
				errorMsg = errorMsg + e.getCode() + ":" + e.getDefaultMessage();
			}
			return errorMsg;
		}
		SecurityUser u = userInforRepostitory.findByuserName(userParam.getUserName());
		if (u != null) {
			return "用户名已存在";
		}
		// 图片处理
		String subFloder = "/user_head_img";
		String filename = srcImg.getOriginalFilename();
		File file = new File(fileSrc + subFloder);
		if (!file.exists()) {
			file.mkdirs();
		}
		try {
			srcImg.transferTo(new File(file + "/" + filename));
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
			return "图片上传失败！";
		}
		// 存库
		userParam.setId(UUID.randomUUID().toString());
		SecurityUser userInfor = new SecurityUser();
		BeanUtils.copyProperties(userParam, userInfor);
		userInfor.setUserPassword(passwordEncoder.encode(userInfor.getUserPassword()));
		userInfor.setSrcImg("http://localhost:8080/images" + subFloder + "/" + filename);
		System.out.println("--------------------------密码：" + passwordEncoder.encode(userInfor.getUserPassword()));
		userInforRepostitory.save(userInfor);
		return "200";
	}

	@RequestMapping("/delete")
	@ResponseBody
	public String delete(@RequestParam(value = "id") String id) {
		userInforRepostitory.deleteById(id);
		return "200";
		// return "redirect:/user/list";
	}

	@RequestMapping("/toUserUpdate")
	@ResponseBody
	public SecurityUser toUpdate(String id, Model model) {
		SecurityUser userInfor = userInforRepostitory.findById(id).get();
		model.addAttribute("user", userInfor);
		return userInfor;
	}

	@Transactional
	@RequestMapping("/userUpdate")
	public String update(@Valid UserParam userParam, BindingResult result, Model model) {
		String errorMsg = "";
		boolean hasErrors = result.hasErrors();
		if (hasErrors) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError e : list) {
				errorMsg = errorMsg + e.getCode() + ":" + e.getDefaultMessage();
			}
			model.addAttribute("errorMsg", errorMsg);
			return "back/user/userInforAdd";
		}

		SecurityUser userInfor = new SecurityUser();
		BeanUtils.copyProperties(userParam, userInfor);
		entityManager.merge(userInfor);
		return "redirect:back//user/list";
	}
}
