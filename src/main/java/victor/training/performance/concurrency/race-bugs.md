
- You receive a list of IDs (MANY!)
- Collect all emails with externalSystem#retrieveEmail(id) - takes time (networking) in parallel
- Eliminate duplicated emails (case insensitive)
- ------------------
- You need to check email for validity via an external system: externalSystem#isEmailValid(email) - takes time (networking) in parallel
- !Avoid calling isEmailValid twice for the same email because it costs us $/call
- Return all validated unique emails.




Obs: As elimina duplicatele inainte de verificarea isEmailValid()
