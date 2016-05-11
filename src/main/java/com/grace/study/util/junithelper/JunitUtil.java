package com.grace.study.util.junithelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.internal.runners.ErrorReportingRunner;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.manipulation.Sorter;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;

import com.grace.study.common.CaseHelper;
import com.grace.study.util.loghelper.LoggedTestCase;


public class JunitUtil extends LoggedTestCase {
	public static void main(String[] args) throws ClassNotFoundException{
		Map<String, List<String>> map = new HashMap<String,List<String>>();
		List<String> method = new ArrayList<String>();
		method.add("testSoap11");
		@SuppressWarnings("unused")
		Result result = executeExcludedMethod("com.grace.study.projects.weather.test.TestWeatherSoapWebservice1",method);
	}
	
	public static Map<String, Result> executeIncludedMethods(Map<String, List<String>> includedMethodsList)throws ClassNotFoundException {
		Map<String, Result> resultMap = null;
		if (includedMethodsList != null) {
			resultMap = new HashMap<String, Result>();
			Iterator<Map.Entry<String, List<String>>> entries = includedMethodsList.entrySet().iterator();
			while (entries.hasNext()) {
				Map.Entry<String, List<String>> entry = entries.next();
				System.out.println(entry.getKey());
				System.out.println(entry.getValue());
				resultMap.put(entry.getKey(),executeIncludedMethod(entry.getKey(),entry.getValue()));
				}
			}
		return resultMap;
	}

	public static Map<String, Result> executeExcluedMethods(Map<String, List<String>> excludedMethodsList)throws ClassNotFoundException {
		Map<String, Result> resultMap = null;
		if (excludedMethodsList != null) {
			resultMap = new HashMap<String, Result>();
			Iterator<Map.Entry<String, List<String>>> entries = excludedMethodsList.entrySet().iterator();
			while (entries.hasNext()) {
				Map.Entry<String, List<String>> entry = entries.next();
				System.out.println(entry.getKey());
				System.out.println(entry.getValue());
				resultMap.put(entry.getKey(),
						executeExcludedMethod(entry.getKey(),entry.getValue()));
			}
		}
		return resultMap;
	}

	private static Result executeIncludedMethod(String className,List<String> includedMethodNames) throws ClassNotFoundException {
		Result result = null;
		System.out.println(className);
		if (className != null) {
			RunNotifier notifier = new RunNotifier();
			result = new Result();
			notifier.addFirstListener(result.createListener());
			notifier.addListener(new LogRunListener());
			Runner runner = null;
			try {
				runner = new BlockJUnit4ClassRunner(Class.forName(className));
				try {
					((BlockJUnit4ClassRunner) runner)
							.filter(new MethodIncludedNameFilter(
									includedMethodNames));
				} catch (NoTestsRemainException e) {
					System.out.println("All methods are been filtered out");
					return null;
				}
				((BlockJUnit4ClassRunner) runner).sort(new Sorter(
						new AlphabetComparator()));
			} catch (Throwable e) {
				Class<?> c = Class.forName(className);
				runner = new ErrorReportingRunner(c, e);
			}
			notifier.fireTestRunStarted(runner.getDescription());
			runner.run(notifier);
			notifier.fireTestRunFinished(result);
		}
		return result;
	}

	private static Result executeExcludedMethod(String className,List<String> excludedMethodNames) throws ClassNotFoundException {
		Result result = null;
		System.out.println(className);
		if (className != null) {
			RunNotifier notifier = new RunNotifier();
			result = new Result();
			notifier.addFirstListener(result.createListener());
			notifier.addListener(new LogRunListener());
			Runner runner = null;
			try {
				runner = new BlockJUnit4ClassRunner(Class.forName(className));
				try {
					((BlockJUnit4ClassRunner) runner).filter(new MethodExcludedNameFilter(excludedMethodNames));
					System.out.println(excludedMethodNames);
				} catch (NoTestsRemainException e) {
					System.out.println("All methods are been filtered out");
					return null;
				}
				((BlockJUnit4ClassRunner) runner).sort(new Sorter(new AlphabetComparator()));
			} catch (Throwable e) {
				Class<?> c = Class.forName(className);
				runner = new ErrorReportingRunner(c, e);
			}
			notifier.fireTestRunStarted(runner.getDescription());
			runner.run(notifier);
			notifier.fireTestRunFinished(result);
		}
		return result;
	}
}

class MethodExcludedNameFilter extends Filter {
	private final Set<String> excluedeMethods = new HashSet<String>();
	public MethodExcludedNameFilter(List<String> excluedMethods) {
		for (String method : excluedMethods) {
			this.excluedeMethods.add(method);
		}
	}

	@Override
	public String describe() {
		// TODO Auto-generated method stub
		return this.getClass().getSimpleName() + "-excluded methods: "
				+ excluedeMethods;
	}

	@Override
	public boolean shouldRun(Description description) {
		// TODO Auto-generated method stub
		String methodName = description.getMethodName();
		if (excluedeMethods.contains(methodName)) {
			return false;
		}
		return true;
	}
}

class MethodIncludedNameFilter extends Filter {
	private final Set<String> inxcludeMethods = new HashSet<String>();
	public MethodIncludedNameFilter(List<String> inxcludeMethods) {
		for (String method : inxcludeMethods) {
			this.inxcludeMethods.add(method);
		}
	}
	@Override
	public boolean shouldRun(Description description) {
		// TODO Auto-generated method stub
		String methodName = description.getMethodName();
		if (inxcludeMethods.contains(methodName)) {
			return true;
		}
		return false;
	}

	@Override
	public String describe() {
		// TODO Auto-generated method stub
		return this.getClass().getSimpleName() + "-included methods: "
				+ inxcludeMethods;
	}
}

class AlphabetComparator implements Comparator<Description> {
	public int compare(Description desc1, Description desc2) {
		// TODO Auto-generated method stub
		return desc1.getMethodName().compareTo(desc2.getMethodName());
	}
}

class LogRunListener extends RunListener {
	
	public LogRunListener() {
	}

	public void testRunStarted(Description description) {
	}

	public void testRunFinished(Result result) {
	}

	public void testStarted(Description description) {
	}

	public void testFinished(Description description) {
	}

	public void testFailure(Failure failure) {

	}

	public void testAssumptionFailure(Failure failure) {

	}

	public void testIgnored(Description description) {

	}

	private String describe(Result result) {
		StringBuilder builder = new StringBuilder();
		builder.append("\tFailureCount: " + result.getFailureCount()).append(
				"\n");
		builder.append("\tIgnoreCount: " + result.getIgnoreCount())
				.append("\n");
		builder.append("\tRunCount: " + result.getRunCount()).append("\n");
		builder.append("\tRunTime: " + result.getRunTime()).append("\n");
		builder.append("\tFailures: " + result.getFailures()).append("\n");
		return builder.toString();
	}

	private void println() {
		System.out.println();
	}

	private void println(String str) {
		System.out.println(str);
	}
}