UnpluggedDroid
==============

[![Build Status](https://travis-ci.org/timvanginderen/UnpluggedDroid.svg?branch=master)](https://travis-ci.org/timvanginderen/UnpluggedDroid)

This Android messaging application aims to reimplement various exsiting privacy centered technologies with a focus on usability.
Although this application will help protect users against surveillance, no technology is 100% secure and this application in particular 
aims to optimize a mix of usability and security.
If you only wish to maximize security, there are many other existing tools you should probably use instead. 
Although there are many alternatives, Tor + Chat Secure is a good option. 

Metadata obfuscation (aka Tor)
------------------------------
Tor is an awesome tool but usage requires users to download both Tor and another client app.
This can be simplified into a single step by reimplementing Tor like functionality over SMS.

Sorry for the weird naming, its New Orleans themed. A Second Line is a
type of local parade we have here. A Throw is anything that they throw
during a parade like beads. We're pretty much copying the Tor design
papers. Our application forms a Second Line (aka circuit)
between a subset of known Masks (aka Onion Routers). It then passes
Throws (aka Packets) between every Mask (in our case Onion Router) until
it reaches the intended recipient.

Our design does not have the concept of an exit node vs a relay node.
We do not keep senders and recipient identities anonymous from each other and you may
only communicate with somebody else with the app installed.

Encryption
----------
Plans for OpenWhisper Systems v2 protocol. 

Offline communication
---------------------
Integration with [the edgenet](http://theedg.es)

Key Exchange
------------
When verifying a public key, it is common to share the last 8 bytes as 16 characters of hex encoding called a fingerprint.
Some implementations encode the fingerprint as an image of some kind or QR code. 
We are considering to instead encode the same information as one nibble + five 1.5 byte blocks. 1.5 bytes maps to 4096 different possibilities, a small enough amount of variation to easily be covered by common words. 
So it would look something like "F Watch Bridge Cloud Inspiration Place".
This enables people to still be able to do oral verification with only 6 items to remember (most people can remember up to 7 things in short term memory).

TODO: Automated web of trust

Content Management
------------------
TODO: Recipients need your permission to reopen what you've sent after X time.