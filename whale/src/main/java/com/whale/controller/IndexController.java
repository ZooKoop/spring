/**
 * 
 */
package com.whale.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Administrator
 *
 */
@Controller
public class IndexController {
	@RequestMapping("/login")
	public String login() {
		return "login";
	}
	@RequestMapping("/")
	public String index() {
		return "index";
	}
	@RequestMapping("/adduser")
	public String user() {
		return "user/userInforAdd";
	}
}
