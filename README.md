# Circles
Circles was an end-to-end encrypted social network app with the goal of enabling
friends and families to securely share stories and photos while
safeguarding security and privacy.

Prototype mobile apps for [Android](https://github.com/circles-project/circles-android) and [iOS](https://github.com/circles-project/circles-ios) were developed at FUTO between 2022 and 2024.
Unfortunately it became apparent that making this project into a successful commercial product would require a level of effort far beyond what was feasible for our small team.
Active development wrapped up in late 2024, and the code is now available under a very liberal Open Source license to allow for new forks or derivative works.

Circles is built on [Matrix](https://matrix.org/), and as such, it inherits many nice
properties from Matrix, including:
* Federation - Anyone can run their own server, and users on different servers can communicate with each other seamlessly.
* Open APIs and data formats - Circles uses standard Matrix message types, and it works
  with any spec-compliant Matrix server.
* Security - Circles offers the same security guarantees as Matrix, using the same
  E2E encryption code as in Element and other popular Matrix clients.

At the same time, anyone hoping to revive or fork this project should be
aware of some important limitations from Matrix:
* The standard Matrix `/login` API exposes the user's password to their homeserver;
  this almost certainly gives the homeserver a huge head start on cracking the user's *other*
  password, which Matrix uses to protect all of the user's encrypted secrets on the server.
  This is insecure in pratice because most human users will not bother to remember two
  distinct passwords for the same account.
  (We developed a custom authorization framework and implemented the BS-SPEKE PAKE protocol
  to work around this for Circles, but it requires running a custom authorization service
  on the homeserver.)
* Matrix does not cryptographically verify room membership.
  Instead, clients like Circles must trust the server to tell them which user accounts are
  in each room.
  Then when the client sends a message, it provides each of those accounts with the decryption key.
  A malicious server can lie to the client about who is in the room, causing it to send the
  key to an adversary.
  (We did some preliminary work to help with this one, via MSC3917, but the changes
  required are extensive, our time was limited, and unfortunately this does not seem to be
  a top priority for anyone else in the community.)
* Getting good performance out of Matrix servers is something of a dark art, and scalability
  would have been a challenge.
  We expected that we would need to write our own server if we wanted to grow the product.
