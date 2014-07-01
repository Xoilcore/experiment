package zk;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import static m.util.PrintUtil.*;

public class ZKTest {

	/**
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws KeeperException
	 */
	public static void main(String[] args) throws IOException, KeeperException,
			InterruptedException {
		ZooKeeper zk = new ZooKeeper("127.0.0.1", 3000, null);
		String p = "/a";
		p(zk.exists(p, false));
		//zk.create(p, "hello".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		p(zk.exists(p, false));
		//zk.delete(p, 0);
		p(zk.exists(p, false));
		p="/a/b";
		//zk.create(p, "hello".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		p(zk.exists(p, false));
		Thread.sleep(3000);
		p("over");
	}
}
