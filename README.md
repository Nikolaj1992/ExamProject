## Task 3.3.2 + 3.3.3
Endpoint outputs:

POST http://localhost:7075/api/trips/populate
HTTP/1.1 201 Created
Date: Mon, 04 Nov 2024 09:37:36 GMT
Content-Type: text/plain
Content-Length: 31

Database populated successfully

GET http://localhost:7075/api/trips
HTTP/1.1 200 OK
Date: Mon, 04 Nov 2024 09:52:42 GMT
Content-Type: application/json
Content-Length: 559

[
{
"id": 1,
"startTime": [
10,
0
],
"endTime": [
18,
0
],
"longitude": "45.0",
"latitude": "90.0",
"name": "Beach Adventure",
"price": 500.0,
"category": "BEACH"
},
{
"id": 2,
"startTime": [
8,
30
],
"endTime": [
16,
30
],
"longitude": "46.0",
"latitude": "91.0",
"name": "City Explorer",
"price": 300.0,
"category": "CITY"
},
{
"id": 3,
"startTime": [
9,
0
],
"endTime": [
19,
0
],
"longitude": "47.0",
"latitude": "92.0",
"name": "Forest Expedition",
"price": 400.0,
"category": "FOREST"
},
{
"id": 4,
"startTime": [
11,
15
],
"endTime": [
17,
45
],
"longitude": "48.0",
"latitude": "93.0",
"name": "Lake Retreat",
"price": 350.0,
"category": "LAKE"
}
]

GET http://localhost:7075/api/trips/1
HTTP/1.1 200 OK
Date: Mon, 04 Nov 2024 09:53:18 GMT
Content-Type: application/json
Content-Length: 139

{
"id": 1,
"startTime": [
10,
0
],
"endTime": [
18,
0
],
"longitude": "45.0",
"latitude": "90.0",
"name": "Beach Adventure",
"price": 500.0,
"category": "BEACH"
}

POST http://localhost:7075/api/trips
HTTP/1.1 201 OK
Date: Mon, 04 Nov 2024 09:56:29 GMT
Content-Type: application/json
Content-Length: 134

{
"id": 5,
"startTime": [
10,
0
],
"endTime": [
18,
0
],
"longitude": "45.0",
"latitude": "90.0",
"name": "New Adventure",
"price": 500.0,
"category": "BEACH"
}

PUT http://localhost:7075/api/trips/1
HTTP/1.1 200 OK
Date: Mon, 04 Nov 2024 09:57:23 GMT
Content-Type: application/json
Content-Length: 147

{
"id": 1,
"startTime": [
10,
0
],
"endTime": [
18,
0
],
"longitude": "45.0",
"latitude": "90.0",
"name": "Updated Beach Adventure",
"price": 500.0,
"category": "BEACH"
}

DELETE http://localhost:7075/api/trips/1
HTTP/1.1 204 No Content
Date: Mon, 04 Nov 2024 09:57:55 GMT
Content-Type: text/plain

<Response body is empty>;

PUT http://localhost:7075/api/trips/3/guides/1
HTTP/1.1 204 No Content
Date: Mon, 04 Nov 2024 09:58:19 GMT
Content-Type: text/plain

<Response body is empty>;

GET http://localhost:7075/api/trips/guides/1
HTTP/1.1 200 OK
Date: Mon, 04 Nov 2024 10:01:43 GMT
Content-Type: application/json
Content-Length: 281

[
{
"id": 2,
"startTime": [
8,
30
],
"endTime": [
16,
30
],
"longitude": "46.0",
"latitude": "91.0",
"name": "City Explorer",
"price": 300.0,
"category": "CITY"
},
{
"id": 3,
"startTime": [
9,
0
],
"endTime": [
19,
0
],
"longitude": "47.0",
"latitude": "92.0",
"name": "Forest Expedition",
"price": 400.0,
"category": "FOREST"
}
]

## Task 3.3.5
Put is commonly used when you know the URI of the resource you're trying to modify. It's also idempotent, meaning multiple requests
will have the same effect as a single request. This is useful when we want to update a resource without changing its state.
Also taking an existing guide an adding to a trip is more akin to updating, making put the logical choice.

Something broke my code with the new endpoints.

https://github.com/Nikolaj1992/ExamProject