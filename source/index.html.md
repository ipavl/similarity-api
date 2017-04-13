---
title: Similarity API Reference

language_tabs:
  - shell
  - php

toc_footers:
  - <a href='./scaladoc'>View Scaladoc</a>
  - <a href="https://github.com/ipavl/similarity-api">View Repository</a>
  - <a href='https://github.com/tripit/slate'>Documentation Powered by Slate</a>

includes:
  - providers
  - languages
  - errors

search: true
---

# Introduction

Similarity API is a RESTful interface to services designed to compare sets of source code for similarity, as often used for detecting software plagiarism in education. It is intended to run alongside some form of learning management system (LMS) or submission portal as a helper service, although it is also possible to interact with it directly.

You can view code examples in the dark area to the right, and you can switch the programming language of the examples with the tabs in the top right.

# Submissions

It is expected that the sets of source code to check will already be on the server where the API resides, extracted, and structured in such a way that each set (e.g. each student's assignment) is within its own directory under a common parent. The current implementation assumes that each student's submission is its own Git repo, of which the current commit on the `master` branch is used as the submission version.

In this example, all student assignments are under a top-level "submissions" directory, with a course-assignment-student hierarchy beneath.

<pre>
submissions
├───CS110_W17
│   ├───A1
│   │   ├───asmithee
│   │   ├───jdoe05
│   │   ├───jsmith12
│   │   └───rroe
│   └───A2
├───CS120_W17
│   └───A1
│       └───jshmoe
└───CS230_W17
</pre>

## Trigger Similarity Check


```shell
curl http://localhost:4910/check \
  -X POST \
  -d '{"assignmentId": "33",
       "threshold": 20,
       "studentId": "jdoe05",
       "directory": "submissions/CS110_W17/A1",
       "language": "c"}' \
  --header "Content-Type:application/json"
```

```php
<?php

$client = new GuzzleHttp\Client(['base_uri' => 'http://localhost:4910/']);
$response = $client->post('check', ['json' =>
  [
    'assignmentId' => '33',
    'threshold' => 20,
    'studentId' => 'jdoe05',
    'directory' => 'submissions/CS110_W17/A1',
    'language' => 'c',
  ]
]);
```

This endpoint signals the similarity checker to run a check using the given settings.

<aside class="notice">Similarity checks are asynchronous and results may not be immediately available upon this request completing depending on the quantity and size of submissions.</aside>

### HTTP Request

`POST http://localhost:4910/check`

### Request Body

Parameter    | Type    | Description
------------ | ------- | -----------
assignmentId | String  | A shared unique identifier for all submissions for this assignment, such as the internal assignment ID from the LMS.
threshold    | Double  | The minimum % similarity to store results for. The highest result will always be stored regardless of this value.
studentId    | String  | The name of the submission to store results for.
directory    | String  | The directory on the server to check.
language     | String  | An identifier for the source code's language. See [Language Identifiers](#language-identifiers) for details.

### Response Code

`202 Accepted`

### Response Body

The response body will contain the path to the future resource:

`/assignments/33/students/jdoe05`

## Retrieve Results for Student

```shell
curl http://localhost:4910/assignments/33/students/jdoe05
```

```php
<?php

$client = new GuzzleHttp\Client(['base_uri' => 'http://localhost:4910/']);
$response = $client->get('assignments/33/students/jdoe05');

echo $response->getBody();
```

> The above command returns JSON structured like this:

```json
[
  {
    "timestamp": 1490761513,
    "assignmentId": "33",
    "jPlagResult": 48.333332,
    "studentB": "jsmith12",
    "studentA": "jdoe05",
    "id": 1,
    "studentBVersion": "b14674def29c3d4b7be24751ea36caf8b540f6d7",
    "studentAVersion": "e8421986310b74a5a486963d2b842188e0aec13b"
  },
  {
    "timestamp": 1490761513,
    "assignmentId": "33",
    "jPlagResult": 25.925926,
    "studentB": "rroe",
    "studentA": "jdoe05",
    "id": 2,
    "studentBVersion": "83fe7cff7b7a18749b55a08f6bffb7c1b7b53a2f",
    "studentAVersion": "e8421986310b74a5a486963d2b842188e0aec13b"
  },
  {
    "timestamp": 1490761528,
    "assignmentId": "33",
    "jPlagResult": 19.67213,
    "studentB": "jdoe05",
    "studentA": "asmithee",
    "id": 3,
    "studentBVersion": "e8421986310b74a5a486963d2b842188e0aec13b",
    "studentAVersion": "36e2a0d57198068423c43eb81257440f17e91a9a"
  }
]
```

This endpoint returns an array of results for a particular student for a given assignment.

### HTTP Request

`GET http://localhost:4910/assignments/<assignmentId>/students/<studentId>`

### Request Parameters

Parameter    | Type    | Description
------------ | ------- | -----------
assignmentId | String  | A shared unique identifier for all submissions for this assignment, such as the internal assignment ID from the LMS.
studentId    | String  | The name of the submission to retrieve results for.

### Response Code

`200 OK`

### Response Body

The response body will contain the matching results as JSON.

## Retrieve Results for All Students

```shell
curl http://localhost:4910/assignments/33/students
```

```php
<?php

$client = new GuzzleHttp\Client(['base_uri' => 'http://localhost:4910/']);
$response = $client->get('assignments/33/students');

echo $response->getBody();
```

> The above command returns JSON structured like this:

```json
[
  {
    "timestamp": 1490761513,
    "assignmentId": "33",
    "jPlagResult": 48.333332,
    "studentB": "jsmith12",
    "studentA": "jdoe05",
    "id": 1,
    "studentBVersion": "b14674def29c3d4b7be24751ea36caf8b540f6d7",
    "studentAVersion": "e8421986310b74a5a486963d2b842188e0aec13b"
  },
  {
    "timestamp": 1490761513,
    "assignmentId": "33",
    "jPlagResult": 25.925926,
    "studentB": "rroe",
    "studentA": "jdoe05",
    "id": 2,
    "studentBVersion": "83fe7cff7b7a18749b55a08f6bffb7c1b7b53a2f",
    "studentAVersion": "e8421986310b74a5a486963d2b842188e0aec13b"
  },
  {
    "timestamp": 1490761528,
    "assignmentId": "33",
    "jPlagResult": 19.67213,
    "studentB": "jdoe05",
    "studentA": "asmithee",
    "id": 3,
    "studentBVersion": "e8421986310b74a5a486963d2b842188e0aec13b",
    "studentAVersion": "36e2a0d57198068423c43eb81257440f17e91a9a"
  }
]
```

This endpoint returns an array of results for all students for a given assignment.

### HTTP Request

`GET http://localhost:4910/assignments/<assignmentId>/students`

### Request Parameters

Parameter    | Type    | Description
------------ | ------- | -----------
assignmentId | String  | A shared unique identifier for all submissions for this assignment, such as the internal assignment ID from the LMS.

### Response Code

`200 OK`

### Response Body

The response body will contain the matching results as JSON.
