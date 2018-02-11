package org.load;

import java.util.HashMap;
import java.util.Map;

public class Report {

	static final String AVG1 = "avg1";
	static final String AVG2 = "avg2";
	static final String RPM = "RPM";

	DefaultTestRunner _runner;

	public Report(DefaultTestRunner runner) {
		_runner = runner;
	}

	public void report() {
		System.out.println();
		System.out.println("s________________________________________________");

		for (int i = 0; i < _runner._dur; i++) {
			Map m = new HashMap();
			m.put(_runner.TIME, 1 + i);
			m.put(AVG1, _runner._be1.getAverage(i));
			m.put(AVG2, _runner._be2.getAverage(i));

			System.out.println(m);
		}
		System.out.println("t________________________________________________");

		Map m = new HashMap();
		m.put(_runner.MAX_LATE + "_1", _runner._be1.getMaxLatency());
		m.put("transacions" + "_1", _runner._be1.getComplemetedCount());

		m.put(_runner.MAX_LATE + "_2", _runner._be2.getMaxLatency());
		m.put("transacions" + "_2", _runner._be2.getComplemetedCount());
		System.out.println(m);
		System.out.println("m________________________________________________");

		int minutes = _runner._dur / 60;
		minutes++;
		for (int i = 0; i < minutes; i++) {
			long rpm1 = _runner._be1.getRPM(i, 7000);
			long rpm2 = _runner._be2.getRPM(i, 7000);
			Map m2 = new HashMap();
			m2.put(_runner.TIME, i + 1);
			m2.put(RPM, rpm1 + rpm2);
			System.out.println(m2);
		}
		System.out.println("________________________________________________");
		System.out.println();
	}
}
