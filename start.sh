sh run_redis.sh

if [ "" = "${JAVA_HOME}" ] ; then
  export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
fi

echo "JAVA_HOME=${JAVA_HOME}"

./mvnw spring-boot:run