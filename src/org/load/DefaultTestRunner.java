package org.load;

import java.net.InetAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.bro.BrowU;
import org.apache.bro.ICurrentTestTime;
import org.info.util.Confd;
import org.info.util.IDaemon;
import org.load.util.BrowserExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

/**
 *
 * A runner. You can write your own.
 *
 */
public enum DefaultTestRunner implements ICurrentTestTime, IDaemon {
	INSTANCE;

	private static final Logger logger = LoggerFactory.getLogger(DefaultTestRunner.class);

	static Confd P = Confd.INSTANCE;

	ScheduledExecutorService _esd = Confd.esLow(6);

	public BrowserExecutor _be1;
	public BrowserExecutor _be2;

	URL _url1;
	URL _url2;

	static {
		INSTANCE._start();
	}

	int _load1;
	int _load2;
	public int _dur;

	static final String URL = "URL";
	static final String LOAD = "load";
	static final String DUR = "duration";

	/**
	 * Config
	 */
	@Override
	public void _start() {
		System.out.println("setup...");
		_be1 = new BrowserExecutor(this);
		_be2 = new BrowserExecutor(this);
		try {
			_url1 = new URL(P.getConf(URL, 1));
			_url2 = new URL(P.getConf(URL, 2));
			_load1 = P.getConfI(LOAD, 1);
			_load2 = P.getConfI(LOAD, 2);
			_dur = P.getConfI(DUR);
		} catch (Throwable e) {
			logger.error("", e);
		}
		System.out.println(_url1);
		System.out.println(_url2);
		logger.info("", _dur);

	}// ()

	static final int _multiple = 5;
	Stopwatch _timer;

	int _curLoad1 = 1;
	int _curLoad2 = 1;

	public void runIt() {
		try {
			System.out.println("starting... ");

			_be1.add2Q(_load1 * _multiple, _url1);
			_be2.add2Q(_load2 * _multiple, _url2);
			_timer = Stopwatch.createStarted();
			_esd.scheduleAtFixedRate(this, 1, 1, TimeUnit.SECONDS);
			System.out.println("started");

			_blockFor(_dur);
			System.out.println("stopping ...");
			_timer.stop();
			try {
				_be1.clearQ();
				_be2.clearQ();
			} catch (Throwable e2) {
				logger.info(e2.getMessage());
			}
			_running = false;
			Thread.sleep(7000); // maybe some current tasks get to finish when we stop adding
			System.out.println("stopped");
			stopAll();
			Thread.sleep(1000);// let it stop

			new Report(this).report();

		} catch (Throwable e) {
			logger.error("", e);
		}
		System.exit(0);
	}

	long _testTime = 0;

	@Override
	public long getCurrentTestTime() {
		return _testTime;
	}// ()

	@Override
	public void _blockFor(int dur) {
		while (_testTime < dur) {// loop this main thread
			_testTime = _timer.elapsed().getSeconds();

			// add load if needed
			if (_be1.getQsize() < _load1) {
				_be1.add2Q(_load1 * 2, _url1);
			}
			if (_be2.getQsize() < _load2) {
				_be2.add2Q(_load2 * 2, _url2);
			}

			// add threads as time goes till max
			if (_curLoad1 < _load1) {
				_curLoad1++;
				_be1.setNumbOfBrowsers(_curLoad1);
			}
			if (_curLoad2 < _load2) {
				_curLoad2++;
				_be2.setNumbOfBrowsers(_curLoad2);
			}

			try {
				Thread.sleep(1000 / 9); // fps
			} catch (InterruptedException e) {
			} // t
		} // w
	}// ()

	boolean _running = true;

	public static final String TIME = "time";
	public static final String DELTA = "delta";
	public static final String MAX_LATE = "max";
	public static final String HOST = "host";

	/**
	 * Just output some data while running so it's not silent
	 */
	@Override
	public void run() {
		try {
			if (!_running) {
				_esd.shutdownNow();
			}

			Map m = new HashMap();
			m.put(TIME, _testTime);

			int l1 = _be1.getActiveCount();
			m.put(LOAD + "_1", l1);
			m.put(DELTA + "_1", _be1.getComplemetedDelta());
			m.put(MAX_LATE + "_1", _be1.getMaxLatency());

			int l2 = _be2.getActiveCount();
			m.put(LOAD + "_2", l2);
			m.put(DELTA + "_2", _be2.getComplemetedDelta());
			m.put(MAX_LATE + "_2", _be2.getMaxLatency());

			m.put(LOAD, l1 + l2);

			InetAddress uip = BrowU.getHostIP(_url1);
			m.put(HOST, uip.toString());
			System.out.println(m);

		} catch (Throwable e) {
			logger.warn("", e);
		} // t
	}//

	public void stopAll() {
		_be1.stop();
		_be2.stop();
	}// ()

}// class
