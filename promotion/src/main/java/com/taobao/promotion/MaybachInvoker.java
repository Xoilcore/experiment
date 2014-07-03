package com.taobao.promotion;

import com.taobao.feng.tools.HsfServiceUtil;
import com.taobao.feng.tools.perf.Data;
import com.taobao.feng.tools.perf.Invoker;
import com.tmall.tmpp.common.exception.TmppException;
import com.tmall.tmpp.common.service.IItemPromotionReadService;

public class MaybachInvoker implements Invoker {

	IItemPromotionReadService readService;

	public MaybachInvoker() {
		String serviceName = "com.tmall.tmpp.common.service.IItemPromotionReadService";
		readService = HsfServiceUtil.createConsummer(serviceName,
				"1.0.0.daily", "HSF");
	}

	@Override
	public String invoke(Data data) {
		Param param = (Param) data.get();
		try {
			readService.getPromotion(param.request);
		} catch (TmppException e) {
			throw new RuntimeException(e);
		}
		return param.method;
	}
}
