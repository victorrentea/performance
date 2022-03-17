
- You receive a list of IDs
- Collect all emails with dependency#retrieveEmail(id) - takes time (networking) in parallel
- Eliminate duplicated emails (case insensitive)
- You need to check email for validity via an external system: dependency#isEmailValid(email) - takes time (networking) in parallel
- !Avoid calling isEmailValid twice for the same email because it costs us $/call
- Return all validated unique emails.


