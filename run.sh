rm -rf ./gpa.output
mvn clean -f "/root/projects/java/hadoop-demo/pom.xml"
mvn package -f "/root/projects/java/hadoop-demo/pom.xml"
hadoop dfs -rm -r -f /local/gpa.output
hadoop jar ~/projects/java/hadoop-demo/target/hadoop-demo-1.0.jar com.example.App /local/gpa.txt /local/gpa.output
hadoop dfs -get /local/gpa.output
cat ./gpa.output/part-r-00000