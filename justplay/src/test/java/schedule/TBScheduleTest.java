package schedule;

import static com.taobao.feng.tools.PrintUtil.*;

import java.util.Comparator;
import java.util.List;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import com.google.common.collect.Lists;
import com.taobao.pamirs.schedule.IScheduleTaskDealSingle;
import com.taobao.pamirs.schedule.TaskItemDefine;
import com.taobao.pamirs.schedule.strategy.ScheduleStrategy;
import com.taobao.pamirs.schedule.strategy.ScheduleStrategy.Kind;
import com.taobao.pamirs.schedule.strategy.TBScheduleManagerFactory;
import com.taobao.pamirs.schedule.taskmanager.ScheduleTaskType;

@SpringApplicationContext({ "schedule.xml" })
public class TBScheduleTest extends UnitilsJUnit4 {

	@SpringBeanByName
	private static TBScheduleManagerFactory scheduleManagerFactory;

	public void setScheduleManagerFactory(
			TBScheduleManagerFactory tbScheduleManagerFactory) {
		scheduleManagerFactory = tbScheduleManagerFactory;
	}

	@Test
	public void main() throws Exception {

		
		
		Thread.sleep(100000000);
	}

	private static String basetaskName = "fileReadTask";
	private static String taskStrategyName = basetaskName + "-strategy";

	public void createTask() throws Exception {

		try {
			scheduleManagerFactory.getScheduleDataManager().deleteTaskType(
					basetaskName);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		ScheduleTaskType t = new ScheduleTaskType();
		t.setDealBeanName("fileDealTask");
		t.setBaseTaskType(basetaskName);
		t.setHeartBeatRate(2000);
		t.setJudgeDeadInterval(10000);
		t.setTaskParameter("A=B");
		t.setTaskItems(ScheduleTaskType
				.splitTaskItem("0:{a},1:{b}"));
		t.setTaskKind("filedeal");
		scheduleManagerFactory.getScheduleDataManager().createBaseTaskType(t);
		p("create task success!");

	}

	public void createSchedule() throws Exception {

		try {
			scheduleManagerFactory.getScheduleStrategyManager()
					.deleteMachineStrategy(taskStrategyName, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ScheduleStrategy sche = new ScheduleStrategy();
		sche.setTaskParameter("a=a");
		sche.setTaskName(basetaskName);
		sche.setStrategyName(taskStrategyName);
		sche.setKind(Kind.Schedule);
		sche.setNumOfSingleServer(1);
		sche.setIPList(new String[] { "192.168.2.100" });
		sche.setAssignNum(2);

		scheduleManagerFactory.getScheduleStrategyManager()
				.createScheduleStrategy(sche);
		p("create schedule success!");
		Thread.sleep(100000);
	}

	public static class FileDealTask implements IScheduleTaskDealSingle<String> {

		public List<String> selectTasks(String taskParameter, String ownSign,
				int taskItemNum, List<TaskItemDefine> taskItemList,
				int eachFetchDataNum) throws Exception {

			pf("taskParameter=%s,ownSign=%s\n", taskParameter, ownSign);

			List<String> dirs = Lists.newArrayList();
			for (TaskItemDefine s : taskItemList) {
				pf("taskItemDefine%s:para:%s\n",s.getTaskItemId(),s.getParameter());
				dirs.add(s.getTaskItemId());
			}
			;
			return dirs;
		}

		public Comparator<String> getComparator() {

			return new Comparator<String>() {

				public int compare(String o1, String o2) {
					return o1.compareTo(o2);
				}
			};
		}

		public boolean execute(String task, String ownSign) throws Exception {
			p(task + ":exec");
			return true;
		}

	}

}
