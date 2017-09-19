package com.ynet.service.impl;

import com.ynet.annotation.Service;
import com.ynet.service.LoginService;

@Service("loginService")
public class LoginServiceImpl implements LoginService {

	@Override
	public int validate() {
		System.out.println("loginService Validate:-----");
		return 0;
	}

}
