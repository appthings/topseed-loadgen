
package org.load.util;

import java.net.URL;
import java.time.Duration;
import java.util.concurrent.Callable;

import org.apache.bro.BrowU;
import org.apache.bro.ICurrentTestTime;
import org.apache.commons.lang3.tuple.Pair;
import org.info.util.Confd;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

public class Browser implements Callable {

	private static final Logger logger = LoggerFactory.getLogger(Browser.class);

	static Confd P = Confd.INSTANCE;

	URL _url;
	Document _doc;

	ICurrentTestTime _curTTime;

	public Browser(URL url, ICurrentTestTime curTTime) {
		_curTTime = curTTime;
		_url = url;
	}

	/**
	 * Returns time started and latency
	 */
	@Override
	public Pair call() {
		long curTime = _curTTime.getCurrentTestTime();

		try {
			Stopwatch timer = Stopwatch.createStarted();
			String doc = (String) BrowU.get(_url, true);
			// _doc = Jsoup.parse(doc);
			// Logger.debug(_doc.text());

			// accessJS();
			// accessStyles();
			// accessAssets();
			timer.stop();
			Duration dur = timer.elapsed();
			try {
				Thread.sleep(2);// 'think time' pause;
			} catch (Exception e) {
			}
			Pair p = Pair.of(curTime, dur.toMillis());
			return p;
		} catch (Throwable e) {
			logger.warn(e.getMessage());
			Pair p = Pair.of(curTime, Long.MAX_VALUE);
			return p;
		} // t

	}// ()

}// class
