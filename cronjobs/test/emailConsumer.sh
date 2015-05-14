cd /var/lib/batch/bin

trap onexit SIGINT SIGSEGV SIGQUIT SIGTERM

prog="EmailConsumer"
lock="/var/lib/batch/bin/${prog}.lock"

onexit(){
sudo rm -f "${lock}"
exit
}

if [ -f $lock ]; then
#echo "lock present. hence exiting."
exit
fi
#echo "lock is not present. hence starting a new instance."
date > $lock

JAVA_HOME=/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.75.x86_64/jre
export JAVA_HOME
PATH=$JAVA_HOME/bin:$PATH
export PATH

for line in /var/lib/batch/lib/*.jar
do
if [[ $line != *"SSBatch-1.0.jar" ]]
then 
BATCH_CLASSPATH="$BATCH_CLASSPATH:$line"
fi
done

CLASSPATH=.:$BATCH_CLASSPATH:
export CLASSPATH

java -Xms1024m -Xmx2048m com.realtech.socialsurvey.core.starter.EmailConsumer


onexit