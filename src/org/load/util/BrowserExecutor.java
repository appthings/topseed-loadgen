package org.load.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.bro.AbsBrowserExecutor;
import org.apache.bro.ICurrentTestTime;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrowserExecutor extends AbsBrowserExecutor {
	private static final Logger logger = LoggerFactory.getLogger(BrowserExecutor.class);

	int minPool = 0;
	int maxPool = 1;

	ICurrentTestTime _curTTime;

	public BrowserExecutor(ICurrentTestTime curTTime) {
		_curTTime = curTTime;
		_execs = new ThreadPoolExecutor(minPool, maxPool, 1, TimeUnit.MILLISECONDS, _q);
	}// ()

	/**
	 * Number of call should be higher than number of Browsers
	 */
	public void add2Q(int n, URL url) {
		for (int x = 0; x < n; x++) {
			Browser b = new Browser(url, _curTTime);
			Future<Pair> result = _execs.submit(b);
			_results.add(result);
		} // f
	}// ()

	public void clearQ() throws Throwable {
		_execs.getQueue().clear();
	}

	public long getMaxLatency() {// todo: look at last 20%
		try {
			long max = 0;
			int sz = _results.size();

			int count = 0;
			while (true) {
				if (sz <= 1)
					break;
				Future f = _results.get(sz--);
				if (f == null || !f.isDone())
					continue;
				Pair p = (Pair) f.get();
				count++;
				if (count > 100)// last few get max
					break;
				long latency = (Long) p.getRight();
				if (latency > max)
					max = latency;
			} // w
			return max;
		} catch (Throwable e) {
			logger.warn("", e);
			return -1;
		} // t
	}// ()

	public List<Pair> getEachCompletedResult() throws Throwable {
		List<Pair> res = new ArrayList();
		for (Future f : _results) {
			if (f.isDone()) {
				Pair p = (Pair) f.get();
				res.add(p);
			} // fi
		} // for
		return res;
	}

	/**
	 * Usually you'd filter anything over 7000 ms latency
	 */
	public int getRPM(long minute, long maxLatency) {
		int count = 0;
		try {
			for (Future f : _results) {
				if (f.isDone()) {
					Pair p = (Pair) f.get();
					long latency = (Long) p.getRight();
					if (latency > maxLatency)
						continue;
					long t = (Long) p.getLeft();
					t = t / 60; // to minutes
					if (t != minute)
						continue;
					count++;
				} // fi
			} // for
		} catch (Throwable e) {
			logger.warn("", e);
		}
		return count;
	}// ()

	/**
	 * Average and Count
	 */
	public Pair getAverage(long time) {
		try {
			int count = 0;
			long total = 0;
			for (Future f : _results) {
				if (f.isDone()) {
					Pair p = (Pair) f.get();
					long t = (Long) p.getLeft();
					if (t != time)
						continue;
					count++;
					long latency = (Long) p.getRight();
					total += latency;
				} // fi
			} // for
			if (count == 0)
				return Pair.of(0l, 0l);
			long avg = total / count;
			return Pair.of(avg, count);
		} catch (Throwable e) {
			logger.warn("", e);
			return Pair.of(Long.MAX_VALUE, 0l);
		}
	}// ()

}// class
