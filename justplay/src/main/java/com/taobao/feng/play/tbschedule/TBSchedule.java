package com.taobao.feng.play.tbschedule;

import static com.taobao.feng.tools.PrintUtil.*;

import java.util.Comparator;
import java.util.List;

import com.google.common.collect.Lists;
import com.taobao.pamirs.schedule.ConsoleManager;
import com.taobao.pamirs.schedule.IScheduleTaskDealSingle;
import com.taobao.pamirs.schedule.TaskItemDefine;
import com.taobao.pamirs.schedule.strategy.ScheduleStrategy;
import com.taobao.pamirs.schedule.strategy.ScheduleStrategy.Kind;
import com.taobao.pamirs.schedule.strategy.TBScheduleManagerFactory;
import com.taobao.pamirs.schedule.taskmanager.ScheduleTaskType;

public class TBSchedule {

	static TBScheduleManagerFactory scheduleManagerFactory;

	public static void main(String[] args) throws Exception {

		ConsoleManager.initial();

		while (!ConsoleManager.getScheduleManagerFactory()
				.isZookeeperInitialSucess()) {
			Thread.sleep(1000);
		}
		ConsoleManager.getScheduleManagerFactory().stopServer(null);

		TBSchedule demo = new TBSchedule();
		try {
			ConsoleManager.getScheduleDataManager()
					.deleteTaskType(basetaskName);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		demo.createTask();
		try {
			ConsoleManager.getScheduleStrategyManager().deleteMachineStrategy(
					taskStrategyName);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		demo.createSchedule();
	}

	private static String basetaskName = "fileReadTask";
	private static String taskStrategyName = basetaskName + "-strategy";

	public void createSchedule() throws Exception {
		ScheduleStrategy sche = new ScheduleStrategy();
		sche.setTaskParameter("a=a");
		sche.setTaskName(basetaskName);
		sche.setStrategyName(taskStrategyName);
		sche.setKind(Kind.Schedule);
		sche.setNumOfSingleServer(1);
		sche.setIPList(new String[] { "127.0.0.1" });
		sche.setAssignNum(2);

		ConsoleManager.getScheduleStrategyManager()
				.createScheduleStrategy(sche);
		p("create schedule success!");
	}

	public void createTask() throws Exception {
		ScheduleTaskType t = new ScheduleTaskType();
		t.setDealBeanName("fileDealTask");
		t.setBaseTaskType(basetaskName);
		t.setHeartBeatRate(2000);
		t.setJudgeDeadInterval(10000);
		t.setTaskParameter("A=B");
		t.setTaskItems(ScheduleTaskType
				.splitTaskItem("0:{TYPE=A,KIND=1},1:{TYPE=A,KIND=2}"));
		ConsoleManager.getScheduleDataManager().createBaseTaskType(t);
		p("create task success!");
	}

	public static class FileDealTask implements IScheduleTaskDealSingle<String> {

		public List<String> selectTasks(String taskParameter, String ownSign,
				int taskItemNum, List<TaskItemDefine> taskItemList,
				int eachFetchDataNum) throws Exception {
			List<String> dirs = Lists.newArrayList();
			for (TaskItemDefine s : taskItemList) {
				dirs.add(s.getTaskItemId());
			}
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

			p(task);
			return true;
		}

	}

	public void setScheduleManagerFactory(
			TBScheduleManagerFactory scheduleManagerFactory2) {
	}

}
