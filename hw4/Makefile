JAVA = java
JAVAC = javac
JAR = jar
OUTPUT_PATH = out
SRC_PATH = src
MAIN = $(SRC_PATH)/toyir/Driver.java
JAR_NAME = toyir

all: jar

jar: compile
	$(JAR) cmf manifest.txt $(OUTPUT_PATH)/$(JAR_NAME).jar -C $(OUTPUT_PATH) toyir

compile:
	$(JAVAC) -d $(OUTPUT_PATH) -sourcepath $(SRC_PATH) $(MAIN)

clean:
	rm -rf $(OUTPUT_PATH)

.PHONY: all run jar compile clean