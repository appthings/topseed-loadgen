# topseed-loadgen
Load generator to test scalability and stability. I deployed a jar to maven central that makes it easy to run this project - the gradle file here will access it.

Video demo: http://youtu.be/aJifYGIVScw


There are several load generators out there, such as jMeter. We found out that they are time consuming. This one should be easy to use to do a simple load test.


To use:

You do not need to build to use - you just need to download the contents of the release folder.
Edit conf.props with a text editor to specify the 2 URL you want to 'load' and the load (concurrent use) you want.
Last, specify how many seconds you want load to take.

Note that topseed4j jar is needed at runtime - it was just easier not to have to include it each time.


To build, change:

      gradle

If you want to customize the way the load works, you can build: run gradle and edit the java src as needed. It needs libs in libs folder, plus topseed4j (topseed4j is needed at runtime as specified by the manifest in gradle).





