## Multithread flow

### Requirements
- You receive a list of IDs (MANY!)
- Collect all emails with external#retrieveEmail(id) - takes time (networking) in parallel -> run on 2 threads
- Eliminate duplicated emails (case insensitive)
- Check email for validity via an external system: external#isEmailValid(email) - takes time (networking) in parallel
- Return all validated unique emails.
- CR: !Avoid calling isEmailValid twice for the same email because it costs us $/call
