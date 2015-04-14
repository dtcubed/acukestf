ECHO OFF
ECHO "================================================"
ECHO "Execute Example Test Suite"
ECHO "================================================"
ECHO ON

java -jar target/acukestf-1.3-SNAPSHOT.jar support/test-suites/example.json support/features

GOTO AROUND
ECHO "not gonna see this"
:AROUND
