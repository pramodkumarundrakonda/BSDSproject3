# Makefile for compiling and running client and server

# Java compiler
JC = javac

# Java runtime
JVM = java

# Source files
SERVER_SRC = src/server/*.java
CLIENT_SRC = src/client/*.java

# Output directories
SERVER_DIR = src/server
CLIENT_DIR = src/client
LOGS_DIR = logs
RESOURCES_DIR := resources

# Main classes
SERVER_MAIN = server.ServerApp
CLIENT_MAIN = client.ClientApp
CLIENT_TEST = client.MultithreadingTest

# Classpath
CLASSPATH = .:lib/*:resources

# Compilation flags
JFLAGS = -classpath $(CLASSPATH) -d .

# Targets
all: server client

server: $(SERVER_SRC)
	@mkdir -p $(LOGS_DIR)
	$(JC) $(JFLAGS) $(SERVER_SRC)

client: $(CLIENT_SRC)
	$(JC) $(JFLAGS) $(CLIENT_SRC)

run-server:
	$(JVM) -classpath $(CLASSPATH) $(SERVER_MAIN)

run-client:
	$(JVM) -classpath $(CLASSPATH) $(CLIENT_MAIN) $(ARGS)

run-test:
	$(JVM) -classpath $(CLASSPATH) $(CLIENT_TEST)

clean:
	rm -rf $(LOGS_DIR)
