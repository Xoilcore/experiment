package tools.taobao.notify;

import java.util.List;

import com.google.common.collect.Lists;
import com.taobao.hsf.notify.client.Binding;
import com.taobao.hsf.notify.client.MessageListener;
import com.taobao.hsf.notify.client.MessageStatus;
import com.taobao.hsf.notify.client.NotifyManager;
import com.taobao.hsf.notify.client.NotifyManagerBean;
import com.taobao.hsf.notify.client.SendResult;
import com.taobao.hsf.notify.client.message.Message;
import com.taobao.hsf.notify.client.message.StringMessage;

import static m.util.PrintUtil.*;
import m.tool.net.ServerClient;

public class NotifyTest extends ServerClient {

	NotifyManagerBean notifyManager;

	@Override
	public void startUp() throws Exception {
		NotifyManagerBean notifyManagerbean = new NotifyManagerBean();
		List<String> topics = Lists.newArrayList();
		topics.add("TBCTU");
		notifyManagerbean.setPublishTopics(topics);
		notifyManagerbean.setGroupId("test");
		notifyManagerbean.setName("notifyManager");
		notifyManagerbean.setDescription("notifyManager");
		notifyManagerbean.setMessageListener(new MessageListener() {

			public void receiveMessage(Message message, MessageStatus status) {
				pf("receive:%s-%s", message.getTopic(),
						message.getMessageType());
			}
		});
		notifyManager = notifyManagerbean;
		notifyManagerbean.init();
		p("init notifyManager success!");
	}

	@Override
	public void serverRun() throws InterruptedException {
		p("server run!");
		List<Binding> bindings = Lists.newArrayList();
		Binding bingding = Binding.direct("TBCTU", "test1", "send-test", -1,
				false);
		bindings.add(bingding);
		//notifyManager.subscribe(bingding);
		notifyManager.subscriberAfterInited(bindings);
		Thread.sleep(2000);
		p("subscribe over!");
	}

	@Override
	public void clientRun() throws Exception {
		Thread.sleep(1000);
		SendResult sr = sendMessage("hello");
		pf("send:%s,%s", sr.isSuccess(),sr.getErrorMessage());
	}

	public static void main(String[] args) {
		ServerClient.start(1000, new NotifyTest());
	}

	public SendResult sendMessage(String obj) {
		StringMessage msg = new StringMessage();
		msg.setBody(obj);
		msg.setTopic("TBCTU");
		msg.setMessageType("test1");
		return notifyManager.sendMessage(msg);
	}
}
