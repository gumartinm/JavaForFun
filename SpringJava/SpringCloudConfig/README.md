- SPRING-CLOUD-CONFIG-SERVER-HOOK

org.springframework.cloud.config.monitor.PropertyPathEndpoint    
org.springframework.cloud.config.monitor.GithubPropertyPathNotificationExtractor    
```json
POST /monitor HTTP/1.1
Host: localhost:8888
Content-Type: application/json
Authorization: Basic cm9vdDptZXNzd2l0aHRoZWJlc3Q=
X-Github-Event: push
Cache-Control: no-cache
{
  "commits": [
    {
      "added": [

      ],
      "removed": [

      ],
      "modified": [
        "spring-cloud-config-example-client/development/application.yml"
      ]
    }
  ]
}
```

- SPRING-CLOUD-CONFIG-SERVER

org.springframework.cloud.config.server.environment.EnvironmentController
```json
GET /spring-cloud-config-example-client/development HTTP/1.1
Host: localhost:8888
Content-Type: application/json
Cache-Control: no-cache
```


- SPRING-CLOUD-CONFIG-CLIENT-ACTUATOR-REFRESH  (does not work if we have multiple instances behind some gateway)

org.springframework.cloud.endpoint.RefreshEndpoint

```json
POST /actuator/refresh HTTP/1.1
Host: localhost:8080
Content-Type: application/json
X-Github-Event: push
Authorization: Basic cm9vdDptZXNzd2l0aHRoZWJlc3Q=
Cache-Control: no-cache
```


- SPRING-CLOUD-CONFIG-SERVER-BUS-REFRESH   

org.springframework.cloud.bus.endpoint.RefreshBusEndpoint

```json
POST /actuator/bus-refresh HTTP/1.1
Host: localhost:8888
Content-Type: application/json
X-Github-Event: push
Cache-Control: no-cache
```
