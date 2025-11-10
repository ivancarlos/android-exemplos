LINUXBREW_HOME = /home/linuxbrew/.linuxbrew
ANT  =  /usr/bin/ant
JENV =	$(LINUXBREW_HOME)/bin/jenv

PACKAGE = eu.nazgee.simple
MAINACTIVITY = .SimpleActivity

status:
	$(ANT) -version
	$(JENV) version

build:
	$(ANT) debug
install:
	$(ANT) installd
uninstall:
	adb uninstall $(PACKAGE)
start:
	adb shell am start -n $(PACKAGE)/$(MAINACTIVITY)
stop:
	adb shell am force-stop $(PACKAGE)
clean:
	$(ANT) clean

