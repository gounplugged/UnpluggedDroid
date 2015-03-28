UnpluggedDroid
==============

This Android messaging application aims to reimplement various exsiting privacy centered technologies with a focus on usability.
Although this application will help protect users against surveillance, no technology is 100% secure and this application in particular aims to optimize a mix of usability and security.
If you only wish to maximize security, there are many other existing tools you should probably use instead.

Metadata obfuscation (aka Tor)
------------------------------

Tor is an awesome tool but usage requires users to download both Tor and another client app.
This can be simplified into a single step by reimplementing Tor like functionality over SMS.

Sorry for the weird naming, its New Orleans themed. A Second Line is a
type of local parade we have here. A Throw is anything that they throw
during a parade like beads. I'm pretty much copying the Tor design
papers I've read. Our application forms a circuit (aka Second Line)
between a subset of known Onion Routers (aka Masks). It then passes
packets (aka Throws) between every Onion Router (in our case Mask) until
it reaches the intended recipient.

Encryption
----------
Using OpenWhisper Systems v2 protocol

Content Management
------------------
Todo

Offline communication
---------------------
Integration with [the edgenet](http://theedg.es)

Key sharing
-----------
Todo



