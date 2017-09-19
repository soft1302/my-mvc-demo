package com.ynet.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ynet.annotation.Autowire;
import com.ynet.annotation.Controller;
import com.ynet.annotation.RequestMapping;
import com.ynet.service.LoginService;
import com.ynet.service.UserService;

@Controller(value = "user")
public class UserController {
	@Autowire
	private UserService userService;
	@Autowire
	private LoginService loginService;

	@RequestMapping("/getUser")
	public void getUser(HttpServletRequest arg0, HttpServletResponse arg1,
			Map contextMap) throws IOException {
		System.out.println("执行controller:->" + contextMap.get("userId"));
		loginService.validate();
		userService.msg();
		arg1.getWriter().write("Welcom MVC->");
	}

	@RequestMapping("/sayHello")
	public void sayHello(HttpServletRequest arg0, HttpServletResponse arg1) {
		System.out.println("Hello");
	}
}
