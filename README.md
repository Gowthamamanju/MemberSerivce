# MemberService
I want to have a “Member” service so that I can easily:
1. Create a new member
2. Read an existing member
3. Update an existing member
4. Delete members which are no longer used
5. List existing members

## To Build Project

1. ./gradlew clean test

## To Run Project

1. ./gradlew clean bootRun

## Following Apis are exposed using Curl command


1. Create a new member :

curl -X POST -H 'Content-Type: application/json' -i http://localhost:9000/members --data '{ 
  "firstName": "testuser",
  "lastName": "lastname",
  "dob":"2007-02-01T23:00:00.000+0000",
  "postalCode":53110,
  "picture":"RGFuJ3MgVG9vbHMgYXJlIGNvb2wh"
}'

where picture is base64 encoded string and DOB is date format


2. Read an existing member :

  1. By passing Id

curl -X GET -H 'Content-Type: application/json' -i http://localhost:9000/member/id/1001

  2. By passing FirstName :

curl -X GET -H 'Content-Type: application/json' -i http://localhost:9000/member/firstname/testuser


3. Update an existing member :

curl -X PUT -H 'Content-Type: application/json' -i http://localhost:9000/members/id/1001 --data '{ 
  "firstName": "Testuser1",
  "lastName": "Testupdateuser",
  "dob":"2007-02-01T23:00:00.000+0000",
  "postalCode":"12345",
  "picture":"RGFuJ3MgVG9vbHMgYXJlIGNvb2wh"
}'

4. Delete members which are no longer used :

curl -X DELETE -H 'Content-Type: application/json' -i http://localhost:9000/member/id/1001

5. List existing members :

curl -X GET -H 'Content-Type: application/json' -i http://localhost:9000/members
