export M2_HOME=/opt/maven/apache-maven-2.2.1
PATH=$M2_HOME/bin:$PATH

mvn clean install -Dmaven.test.skip=true
mvn dependency:sources
mvn dependency:resolve -Dclassifier=javadoc
