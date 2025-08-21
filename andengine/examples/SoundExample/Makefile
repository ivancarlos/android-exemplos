LINUXBREW_HOME = /home/linuxbrew/.linuxbrew
ANT  =  /usr/bin/ant
JENV = $(LINUXBREW_HOME)/bin/jenv
ADB  = /home/ivan/Android/api/16/android-sdk-linux/platform-tools/adb

PACKAGE = org.andengine.examples
MAINACTIVITY = .SoundExample

status:
	$(ANT) -version
	$(JENV) version

build: assets/gfx/tank.png
	$(ANT) debug

assets/gfx/tank.png:
	make -C tools tank

deploy: build install start

install:
	$(ANT) installd
uninstall:
	adb uninstall $(PACKAGE)
start:
	$(ADB) shell am start -n $(PACKAGE)/$(MAINACTIVITY)
stop:
	$(ADB) shell am force-stop $(PACKAGE)
clean:
	rm -rf bin/ libs/ gen/
	make -C tools clean

restart:
	$(ADB) kill-server
	$(ADB) start-server
