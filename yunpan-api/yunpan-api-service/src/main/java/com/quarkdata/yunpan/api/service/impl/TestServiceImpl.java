package com.quarkdata.yunpan.api.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NamedThreadLocal;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSON;
import com.quarkdata.yunpan.api.service.TestService;


@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class TestServiceImpl implements TestService {

	static Logger logger = Logger.getLogger(TestServiceImpl.class);

	@Override
	public void getTest(String out_trade_no, String trade_no, String trade_status) {
		// TODO Auto-generated method stub
		
	}

//	@Autowired
//	private BillMapper billMapper;
	


}
