clear
#export PATH=$PATH:/root/jdk1.8.0_162/bin
# needs dat.jar in same dir. also you can > site1.txt
java -Xmx2800m -XX:+UseStringDeduplication -XX:+OptimizeStringConcat -XX:ThreadStackSize=228k -Xss228k -XX:+UseLargePages -XX:+AggressiveOpts -jar loadGen.jar 


