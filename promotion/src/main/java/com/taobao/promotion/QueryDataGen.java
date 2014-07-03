package com.taobao.promotion;

import java.util.ArrayList;
import java.util.Collection;

import com.google.common.collect.Lists;
import com.taobao.feng.tools.HsfServiceUtil;
import com.taobao.feng.tools.perf.Data;
import com.taobao.feng.tools.perf.DataGenerator;
import com.taobao.item.domain.ItemDO;
import com.taobao.item.domain.query.QueryItemOptionsDO;
import com.taobao.item.domain.result.ItemResultDO;
import com.taobao.item.exception.IcException;
import com.taobao.item.service.ItemQueryService;
import com.tmall.tmpp.common.constants.QueryPageConstants;
import com.tmall.tmpp.common.dto.AppInfo;
import com.tmall.tmpp.common.dto.ChannelInfo;
import com.tmall.tmpp.common.dto.TmpItemDO;
import com.tmall.tmpp.common.dto.TmpUserInfo;
import com.tmall.tmpp.common.dto.ChannelInfo.Platform;
import com.tmall.tmpp.common.promotion.TmpPromotionDef.PromotionLev;
import com.tmall.tmpp.common.request.TmpItemPromotionRequest;

public class QueryDataGen implements DataGenerator {

	private ItemQueryService itemQueryService;

	public QueryDataGen() {
		itemQueryService = HsfServiceUtil.createConsummer(
				"com.taobao.item.service.ItemQueryService",
				"1.0.0.daily");
	}

	@Override
	public Data get() {
		TmpItemPromotionRequest req = buildItemQueryRequest(2000035023576L,
				Platform.PC);
		Data data = new Data();
		Param param = new Param();
		param.method = "getPromotion";
		param.request = req;
		data.set(param);
		return data;
	}

	private TmpItemPromotionRequest buildItemQueryRequest(Long itemId,
			int platform) {
		TmpItemPromotionRequest request = new TmpItemPromotionRequest();

		// Step-1: 设置商品信息及渠道信息
		// @see{ChannelInfo.Platform}
		ChannelInfo channelInfo = new ChannelInfo();
		channelInfo.setPlatform(platform);
		TmpItemDO itemDO;
		try {
			itemDO = getTmpItemDO(itemId, "p_" + itemId, channelInfo);
		} catch (IcException e) {
			return null;
		}
		request.setItemDO(itemDO);

		// Step-2: 设置卖家的信息
		long sellerId = itemDO.getSellerId();
		request.setSellerInfo(new TmpUserInfo().setUserId(sellerId));

		// Step-3: 设置结果集是否只要最优
		// 如果需要两个渠道的优惠信息，请务必设置为false，目前没有接口可以一次拿到两个渠道的最优优惠。
		request.setBestOnly(true);

		// Step-4: 设置调用方应用的信息
		request.setAppInfo(new AppInfo("appName",
				QueryPageConstants.QUERY_FROM_DETAIL));

		// Step-5: 设置不支持的优惠级别，搭配，店铺，跨店
		request.setUnSupportedPromLevList(Lists.newArrayList(
				PromotionLev.COMBINE_PROM, PromotionLev.SHOP_PROM,
				PromotionLev.CROSS_SHOP_PROM));

		return request;
	}

	// 获取TmpItemDO，使用IC标准服务获取对象后构造
	private TmpItemDO getTmpItemDO(long itemId, String outId,
			ChannelInfo channelInfo) throws IcException {

		QueryItemOptionsDO fullOption = new QueryItemOptionsDO();

		fullOption.setIncludeSkus(true);
		fullOption.setIncludeItemExtends(true);

		Collection<Long> itemCats = new ArrayList<Long>();
		ItemResultDO result = itemQueryService
				.queryItemById(itemId, fullOption);
		ItemDO itemDO = result.getItem();
		itemCats.add(itemDO.getCategoryId());
		TmpItemDO tmpItemDO = new TmpItemDO(outId, itemDO, itemCats);
		tmpItemDO.setChannelInfo(channelInfo);
		return tmpItemDO;
	}
}
