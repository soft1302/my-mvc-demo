package com.ynet.service.impl;

import com.ynet.annotation.Service;
import com.ynet.service.UserService;

@Service("userServiceImpl")
public class UserServiceImpl implements UserService {

	@Override
	public void msg() {
		System.out.println("执行了 service");
	}

}
