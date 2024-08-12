.PHONY: build

all: start

run-docker:
	docker run -it --rm -p 8081:8081 --add-host host.docker.internal:host-gateway timetrackerui

setup-docker:
	docker build -t timetrackerui .

# build and trigger restart
classes:
	./gradlew classes

update: update-purecss update-htmx

update-purecss:
	@echo "fetch/update purecss"
	npm pack --quiet purecss@^3.0.0
	tar -xf purecss*.tgz --strip-components 2 -C src/main/resources/static/lib/css package/build/pure.css
	tar -xf purecss*.tgz --strip-components 2 -C src/main/resources/static/lib/css package/build/grids-responsive.css

update-htmx:
	@echo "fetch/update htmx"
	npm pack --quiet htmx.org@^2.0.0
	tar -xf htmx*.tgz --strip-components 2 -C src/main/resources/static/lib/js package/dist/htmx.js

start:
	@echo "\nstarting.."
	docker start timetracker
	./gradlew bootrun

run: setup
	@echo "\ncreating and starting.."
	docker run -d -p 8080:8080 --name timetracker alirizasaral/timetracker:1

status:
	docker ps -a
	@echo
	curl "http://localhost:8080/records?offset=0&length=1"
	@echo # add newline
	@echo "\nlast log entries.."
	docker logs -n 3 timetracker

setup:
	@echo "\nrunning setup.."
	docker pull alirizasaral/timetracker:1

stop:
	@echo "\nshutting down.."
	-docker stop timetracker

clean: stop
	@echo "\ncleaning.."
	-docker rm timetracker
	-rm purecss*.tgz
	-rm htmx*.tgz

prune:
	-docker image prune

purge: clean
	@echo "\npurging.."
	-docker rmi alirizasaral/timetracker:1
