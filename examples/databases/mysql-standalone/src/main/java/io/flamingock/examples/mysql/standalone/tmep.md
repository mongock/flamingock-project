```shell
curl --request POST \
--url http://dieppa-test.wiremockapi.cloud/v1/client-service/execution \
--header 'Accept: application/json' \
--header 'Authorization: Bearer eweraaseeradsa.asdasdaw.asdasd' \
--header 'Content-Type: application/json' \
--data '{
"lockAcquiredForMills": 10000,
"stages": [
{
"name": "system-changes",
"order": -100,
"tasks": [
"system-task11",
"system-task2"
]
},
{
"name": "mongodb-migration",
"order": 1,
"tasks": [
"mongodb-change1",
"mongodb-change2"
]
},
{
"name": "kafka-configuration",
"order": 2,
"tasks": [
"kafka-change1",
"kafaka-change2"
]
}
]
}'
```