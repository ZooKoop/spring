package com.whale.controller;


import com.whale.model.Work;
import com.whale.model.WorkConcent;
import com.whale.security.model.SecurityUser;
import com.whale.security.repository.SecurityUserRepository;
import com.whale.services.WorkConcentServices;
import com.whale.services.WorkServices;
import com.whale.tools.UploadUntils;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@Api(value = "Ticket")
@RequestMapping("/api/work")
public class WorkControllerApi {
	/**
	 * 本地字段,只判断是否创建文件夹不写入库
	 */
	@Value("${whale.workSql_D}")
	private String workSql_D;
	/**
	 * 浏览器字段写入库
	 */
	@Value("${whale.workSql_L}")
	private String workSql_L;
	
	@Autowired
	private WorkServices workServices;
	@Autowired
	private SecurityUserRepository securityUserRepository;
	@Autowired
	private WorkConcentServices workConcentServices;

	/**
	 * 按条件查询
	 * @param page
	 * @param pageSize
	 * @param work
	 * @return
	 */
	@GetMapping("/queryInfo")
	 public Page<Work> queryInfo(@RequestParam(value = "page") Integer page,
								@RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
//								Authentication authentication,
								Work work
	 ) {
//		String userName = authentication.getName();
//		SecurityUser byuserName = securityUserRepository.findByuserName(userName);
//		work.setSecurityUser(byuserName);
		return workServices.queryInfo(--page, pageSize, work);
	}
//	public List<Work> queryAll() {
//		List<Work> all = workServices.findAll();
//
//		HashMap<String, Object> m = new HashMap<>();
//			m.put("data", all);
//			return all;
//	}

	/**
	 * 模态框跳转用
	 *
	 * @return
	 */
	@RequestMapping("/toAdd")
	public String toAdd() {
		return "back/work/work_add";
	}

	@PostMapping("/add")
	@ResponseBody
	public Map<String, Object> add(Work work,Authentication authentication) {
		HashMap<String, Object> json = new HashMap<String, Object>();
		if (work.getTicketNumber() != null) {
			if (work.getIsClose().equals("2")) {
				//判断自提唯一，分2个情况一个自提一个开关
				Work noSame = workServices.findByIsCloseAndTicketNumber(work.getIsClose(),work.getTicketNumber());
				if (noSame!=null) {
					json.put("repeat", "222");
				}else {
					work.setSecurityUser(userInfo(authentication));
					if (workServices.add(work)) {
						json.put("success", "200");
					}
				}
			}else {
				//判断开关唯一
				if(workServices.findTikcetAndIsColse(work).size()>0) {
					json.put("repeat", "222");
					return json;
				}
				work.setSecurityUser(userInfo(authentication));
				if (workServices.add(work)) {
					json.put("success", "200");
				}
			}
		} else {
			json.put("fail", "400");
		}
		return json;
	}

	@GetMapping("/delete")
	@ResponseBody
	public String del(String id,Authentication authentication) {
		Work work = workServices.findById(id);
		SecurityUser userInfo = userInfo(authentication);
		Set<WorkConcent> workConcent = work.getWorkConcentList();
		String fileNmae ="";
		try {
			for (WorkConcent w : workConcent) {
				if (!StringUtils.isBlank(w.getSqlUrls())) {
				fileNmae = w.getSqlUrls().substring(w.getSqlUrls().lastIndexOf("/")+1, w.getSqlUrls().length());
				boolean delete = new File(workSql_D+userInfo.getId()+"/"+fileNmae).delete();
				System.out.println("删除结果：" + delete);
				}
			}
			workServices.del(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "400";
		}
		return "200";
	}
//
//	@PostMapping("/delete_All")
//	@ResponseBody
//	public String delete_All(String ids) {
//		try {
//			List<String> idList = Arrays.asList(ids.split(","));
//			for (String id : idList) {// 放置报空指针异常
//				Work findById = workServices.findById(id);
//				if (!StringUtils.isBlank(findById.getSqlUrls())) {
//					String[] sqlUrlLit = findById.getSqlUrls().split(",");
//					for (String f : sqlUrlLit) {
//						File file = new File(f);
//						if (file.exists() && file.isFile()) {
//							File absoluteFile = file.getAbsoluteFile();
//							absoluteFile.delete();
//						}
//					}
//				}
//				workServices.delete_All(idList);
//			}
//			return "200";
//		} catch (Exception e) {
//			e.printStackTrace();
//			return "400";
//		}
//
//	}

	@RequestMapping("/toUpdate")
	public String toUpdate(String id,Model model,HttpServletRequest httpServletRequest) {
		String queryUrl = httpServletRequest.getRequestURI();
		Work work = workServices.findById(id);
		model.addAttribute("queryUrl", queryUrl);
		model.addAttribute("work", work);
		return "back/work/work_edit";
	}

//	@GetMapping("/toUpdateInfo")
//	@ResponseBody
//	public Work toUpdateInfo(String id) {
//		Work work = workServices.findById(id);
//		return work;
//	}

	@PostMapping("/Update")
	@ResponseBody
	public Map<String, Object> update(Work work, Authentication authentication) {
		HashMap<String, Object> json = new HashMap<String, Object>();
		work.setSecurityUser(securityUserRepository.findByuserName(authentication.getName()));
		if (workServices.update(work)) {
			json.put("success", "200");
			return json;
		}
		json.put("fail", "400");
		return json;
	}

	@PostMapping("/sqlUpload")
	@ResponseBody
	public Map<String, Object> sqlUpload(String ticketNumber,MultipartFile sqlurl_font,Authentication authentication) {
		WorkConcent workConcent = null;
		Map<String, Object> upload = new HashMap<String, Object>();
		SecurityUser userInfo = userInfo(authentication);
		if (sqlurl_font!=null && !StringUtils.isBlank(ticketNumber) && !ticketNumber.equals("undefined")) {

			upload = UploadUntils.upload(workSql_D+userInfo.getId()+"/",sqlurl_font);
			/**
			 * 先判断是否上传成功，不成功return
			 */
			if(upload.get("status")!=null) {
				if(upload.get("status").toString()=="false") {
					return upload;
				}
			};
			workConcent = new WorkConcent();
			Work findByTicketNumber = workServices.findByTicketNumber(ticketNumber);
			workConcent.setSqlUrls(workSql_L+userInfo.getId()+"/"+sqlurl_font.getOriginalFilename());
			workConcent.setWork(findByTicketNumber);
			workConcentServices.save(workConcent);
			return upload;
		}
		return null;
	}
	public SecurityUser userInfo(Authentication authentication) {
		String name = authentication.getName();
		return securityUserRepository.findByuserName(name);
	}
};
