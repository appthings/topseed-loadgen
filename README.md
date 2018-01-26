# topseed-loadgen
Load generator to test scalability and stability.


You can use only if you agree to the terms of this license: https://creativecommons.org/licenses/by/4.0
You may make request a different license from the authors.


There are several load generators out there, such as jMeter. We found out that they are time consuming. This one should be easy to use to do a simple load test.


To use:


You do not need to build to use - you just need to download the contents of the release folder.
Edit conf.props with a text editor to specify the 2 URL you want to 'load' and the load (concurrent use) you want.
Last, specify how many seconds you want load to take.

Note that topseed4j jar is needed at runtime - it was just easier not to have to include it each time.


If you want to customize the way the load works, you can build: run gradle and edit the java src as needed. It needs libs in lbis and topseed4j (also topseed4j is needed at runtime as specificed by the manfiest in gradle).





