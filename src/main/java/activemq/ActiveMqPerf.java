package activemq;

import java.util.Map;
import java.util.Queue;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import static m.util.PrintUtil.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;

import m.tool.net.ServerClient;
import m.util.FPSCounter;

public class ActiveMqPerf extends ServerClient implements MessageListener {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ServerClient.start(new ActiveMqPerf());
	}

	public static class MQQueue {
		MessageConsumer consumer;
		MessageProducer producer;
		Session session;
		ActiveMQQueue queue;
		BrokerService bs;

		public MQQueue(Connection connection, String queueName,
				MessageListener listener) throws JMSException {

			queue = new ActiveMQQueue(queueName);
			session = connection
					.createSession(true, Session.SESSION_TRANSACTED);
			consumer = session.createConsumer(queue);
			consumer.setMessageListener(listener);
			producer = session.createProducer(queue);
		}

		public void send(Message message) throws JMSException {
			producer.send(message);
		}

	}

	Queue<Message> memQueue;

	MQQueue[] queues;

	int threadCount = 1;
	int maxMemQueueSize = 200;

	@Override
	public void startUp() throws Exception {
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(
				"vm:eventLocalQueue");

		Connection connection = factory.createConnection();
		connection.start();
		queues = new MQQueue[threadCount];
		for (int i = 0; i < threadCount; i++) {
			queues[i] = new MQQueue(connection, "queue_" + i, this);

		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 10; i++) {
			sb.append(String.format("arg%d:%d;", i, i));
		}
		text = sb.toString();
		p(text);
		memQueue = Queues.newArrayBlockingQueue(2000);
	}

	@Override
	public void serverRun() throws InterruptedException {
	}

	String text;

	FPSCounter sendFps = new FPSCounter("sendMsg");
	FPSCounter consumerFps = new FPSCounter(
			"----------------------->consumerMsg");

	boolean init = false;

	@Override
	public void clientRun() throws Exception {
		if (init)
			return;
		init = true;
		Thread[] threads = new Thread[8];
		for (int i = 0; i < threadCount; i++) {

			threads[i] = new Thread() {

				public int idx = -1;

				long sendcount = 0;

				public void run() {
					while (true) {

						try {
							// Thread.sleep(10);
							// sendFps.step();
							Message m = queues[idx].session
									.createTextMessage(System
											.currentTimeMillis() + ":" + text);
							// queues[idx].send(m);
							sendcount++;
							if (sendcount % 200 == 0) {
								queues[idx].session.commit();
							}
							Thread.sleep(20);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

				public Thread setIdx(int idx) {
					this.idx = idx;
					return this;
				}
			}.setIdx(i);
			threads[i].start();
			pf("thread %d start!\n", i);
		}
		new Thread() {
			public void run() {
				while (true) {
					Message m = memQueue.poll();
					if (m == null) {
						// p("queue is null");
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						// p(m);

						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						consumerFps.step();
					}
				}
			}
		}.start();
	}

	boolean recv;

	public void onMessage(Message arg0) {

		/*
		 * try { Thread.sleep(100); } catch (InterruptedException e) {
		 * e.printStackTrace(); }
		 */
		// TextMessage tM=(TextMessage) arg0;
		synchronized (this) {
			int size = memQueue.size();
			if (size == 100 || size == 0) {
				recv = true;
				p("##start recv!");
			} else if (size > maxMemQueueSize - 1) {
				recv = false;
				p("##end recv!");
			}

			if (recv) {
				memQueue.add(arg0);
			}
		}
		ObjectMessage m = null;

	}

}
