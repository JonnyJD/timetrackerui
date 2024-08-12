# Time Tracker UI

## Running and Using the App

### 1. Build and Run (legacy) timetracker

Pull the image, create the container and run it in the background (`-d`).
After the container is stopped it will be removed automatically (`--rm`).
Note that all records are lost when the container is stopped,
regardless if it would be removed or not.
```
docker run -d --rm -p 8080:8080 alirizasaral/timetracker:1
```

### 2. Build an Run Time Tracker UI

#### Using Docker

Build the image with gradle inside docker
and then copy the jar file to a smaller layer.
```
docker build -t timetrackerui .
```

Create the container and run it interactively (`-it`).
This container is also removed after it is stopped.
```
docker run -it --rm -p 8081:8081 --add-host host.docker.internal:host-gateway timetrackerui
```
If a different port should be opened on the host like `8082`,
this can be done with `-p 8082:8081`.
The added `host.docker.internal` is only required on Linux.
Windows and Mac should do that automatically with Docker Desktop.

#### Using local Java without Docker

Build and run the App interactively.
```
./gradlew bootRun
```

#### 3. Use Time Tracker UI

Open [http://localhost:8081/](http://localhost:8081/) in a browser.


## Used Technologies (Stack)

* [Java Spring Boot](https://spring.io/projects/spring-boot) as a base ([RestClient](https://docs.spring.io/spring-framework/reference/integration/rest-clients.html#rest-restclient) to use timetracker)
* [Thymeleaf](https://www.thymeleaf.org/) as template Engine to render HTML on the server
* [htmx](https://htmx.org/) to run "AJAX" via HTML attributes
* [Pure.css](https://purecss.io/) for some minimalistic styling

## Notes

Htmx and Pure.css are directly included in the repository,
but can also be fethed/updated with npm.
```
npm pack --quiet htmx.org@^2.0.0
tar -xf htmx*.tgz --strip-components 2 -C src/main/resources/static/lib/js package/dist/htmx.js
npm pack --quiet purecss@^3.0.0
tar -xf purecss*.tgz --strip-components 2 -C src/main/resources/static/lib/css package/build/pure.css
tar -xf purecss*.tgz --strip-components 2 -C src/main/resources/static/lib/css package/build/grids-responsive.css
```
