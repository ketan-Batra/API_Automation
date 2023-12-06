Feature: Sample API Testing Scenario

  @sample
  Scenario: List all users of reqres.in with Get Call
    Given url https://reqres.in/api/users?page=2
    And  set header accept = application/json
    Then send method Get
    Then assert status code 200

  @sample
  Scenario: Test Sample API with Post Call
    Given url https://reqres.in/api/users
    And  set header accept = application/json
    And set request as {"name": "morpheus","job": "leader"}
    Then send method Post
    Then assert status code 201

  @sample
  Scenario: Test Sample API with Put Call
    Given url https://reqres.in/api/users/2
    And  set header accept = application/json
    And set request as {"name": "morpheus","job": "zion resident"}
    Then send method Put
    Then assert status code 200
