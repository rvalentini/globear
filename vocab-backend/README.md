# globear-backend

A Clojure backend server build with ring, which provides content like 
marker locations and pictures for the globear-frontend application

## Usage

### Local

Run the following commands inside the REPL to run the Jetty adapter:\
```(use 'ring.adapter.jetty) ``` \
```(use 'globear-backend.core) ``` \
```(run-jetty handler {:port 3000}) ``` 

## License

Copyright © 2018,2019 Riccardo Valentini
