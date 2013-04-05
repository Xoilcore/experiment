package ex.banchinvoke;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import static m.util.PrintUtil.*;

import com.google.common.collect.Queues;
import com.google.common.collect.Sets;

public class RequestPool {

	/**
	 * @param args
	 */
	public static class Call {
		Object result;
		Object[] args;
		Class<?>[] argTypes;
	}

	public interface BanchCall {
		void callBanch(Call[] calls);
	}

	public static class CallPool {
		long callTimeOut = 100;
		long cacheCount = 11;
		List<Call> calls;
		BanchCall banchCall;

		BlockingQueue<Runnable> taskQueue;

		ThreadPoolExecutor tpe;
		Thread deamon;
		long lastExecTime;

		void init() {
			taskQueue = Queues.newLinkedBlockingDeque();
			tpe = new ThreadPoolExecutor(5, 10, 10000L, TimeUnit.SECONDS,
					taskQueue);
			deamon = new Thread() {
				public void run() {
					while (true) {
						long tmis = System.currentTimeMillis();
						long diff = 0;
						long sleep = 0;
						synchronized (this) {
							if (calls == null || calls.size() == 0) {
								sleep = callTimeOut;
							} else {
								diff = tmis - lastExecTime;
								if (diff >= callTimeOut) {
									doCall(calls);
									p("执行强制刷新！");
									calls = null;
									sleep = callTimeOut;
								} else {
									sleep = callTimeOut - diff;
								}
							}
						}
						try {
							Thread.sleep(sleep);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			};
			deamon.setDaemon(true);
			deamon.start();
		}

		private void doCall(List<Call> calls) {
			lastExecTime = System.currentTimeMillis();
			tpe.execute(new Runnable() {

				private List<Call> calls;

				private BanchCall banchCall;

				public Runnable init(BanchCall banchCall, List<Call> calls) {
					this.banchCall = banchCall;
					this.calls = calls;
					return this;
				}

				public void run() {
					Call[] callsArray = calls.toArray(new Call[0]);
					banchCall.callBanch(callsArray);
					synchronized (calls) {
						calls.notifyAll();
					}
				}
			}.init(banchCall, calls));
		}

		public void addCall(Call call) throws InterruptedException {

			List<Call> thisCalls = null;
			synchronized (this) {
				if (calls == null) {
					calls = new ArrayList<Call>();
				}
				calls.add(call);
				thisCalls = calls;
				if (calls != null && calls.size() >= cacheCount) {
					doCall(calls);
					calls = null;
				}
			}
			synchronized (thisCalls) {
				thisCalls.wait();
			}
		}
	}

	public static class Keywords {
		private static Set<String> dics;

		static {
			dics = Sets.newHashSet(new String[] { "A", "B" });
		}

		public boolean[] banchContain(String[] words) {
			boolean[] barr = new boolean[words.length];
			for (int i = 0; i < words.length; i++) {
				barr[i] = dics.contains(words[i]);
			}
			return barr;
		}
	}

	public interface SetMatch {
		boolean contain(String str);
	}

	public static class WordBanchCall implements BanchCall {

		Keywords k = new Keywords();

		public void callBanch(Call[] calls) {
			String[] words = new String[calls.length];
			for (int i = 0; i < words.length; i++) {
				words[i] = (String) calls[i].args[0];
			}
			boolean[] br = k.banchContain(words);
			for (int i = 0; i < br.length; i++) {
				calls[i].result = br[i];
			}
		}
	}

	public static Object newRequestAngency(BanchCall banchCall,
			String interfaceName, String methodName, Object target)
			throws IllegalArgumentException, ClassNotFoundException {
		CallPool callPool = new CallPool();
		callPool.banchCall = banchCall;
		callPool.init();
		Object proxyObject = Proxy.newProxyInstance(
				RequestPool.class.getClassLoader(),
				new Class<?>[] { Class.forName(interfaceName) },
				new InvocationHandler() {

					public Object invoke(Object proxy, Method method,
							Object[] args) throws Throwable {
						if (method.getName().equals(methodName)) {
							Call call = new Call();
							call.args = args;
							callPool.addCall(call);
							return call.result;
						} else {
							return method.invoke(target, args);
						}
					}

					private CallPool callPool;
					private String methodName;

					private Object target;

					public InvocationHandler init(CallPool callPool,
							String methodName, Object target) {
						this.callPool = callPool;
						this.methodName = methodName;
						this.target = target;
						return this;
					}
				}.init(callPool, methodName, target));
		return proxyObject;
	}

	public static void main(String[] args) throws InterruptedException,
			IllegalArgumentException, ClassNotFoundException {
		/*
		 * // 初始化调用池 CallPool callPool = new CallPool(); callPool.banchCall =
		 * new WordBanchCall(); callPool.init(); // 匹配代理 SetMatch setMatch =
		 * (SetMatch) Proxy.newProxyInstance(
		 * RequestPool.class.getClassLoader(), new Class<?>[] { SetMatch.class
		 * }, new InvocationHandler() {
		 * 
		 * public Object invoke(Object proxy, Method method, Object[] args)
		 * throws Throwable { if (method.getName().equals("contain")) { Call
		 * call = new Call(); call.args = args; callPool.addCall(call); return
		 * call.result; } return null; }
		 * 
		 * private CallPool callPool;
		 * 
		 * public InvocationHandler init(CallPool callPool) { this.callPool =
		 * callPool; return this; } }.init(callPool));
		 */
		// 获取调用实例

		SetMatch setMatch = (SetMatch) newRequestAngency(new WordBanchCall(),
				"i.RequestPool$SetMatch", "contain", null);

		// 名单匹配线程池
		BlockingQueue<Runnable> taskQueue = Queues.newLinkedBlockingDeque();
		ThreadPoolExecutor tpe = new ThreadPoolExecutor(10, 15, 10000,
				TimeUnit.SECONDS, taskQueue);
		for (int i = 0; i < 1000; i++) {

			long idx = System.currentTimeMillis() % 26;
			char ch = (char) (idx + 'A');

			tpe.execute(new Runnable() {
				public void run() {
					boolean res = setMatch.contain(content);
					pf("idx[%d],word[%s],result[%s]\n", taskId, content, res);
				}

				int taskId;
				String content;
				SetMatch setMatch;

				public Runnable init(SetMatch setMatch, int taskId,
						String content) {
					this.setMatch = setMatch;
					this.taskId = taskId;
					this.content = content;
					return this;
				}
			}.init(setMatch, i, ch + ""));
			Thread.sleep(10);
		}
	}

}
