cd order-common
gradle -Dskip.tests clean build jar publish
cd ..
cd order-manager
gradle -Dskip.tests clean build bootJar
cd ..
cd fulfillment-service
gradle -Dskip.tests clean build bootJar
cd ..