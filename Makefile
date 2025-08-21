
debug:
	./new.$@.sh
release:
	./new.$@.sh
main:
	./github.main.expect $(shell git rev-parse --abbrev-ref HEAD)
push:
	./github.$@.expect

