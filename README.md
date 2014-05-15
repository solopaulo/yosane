yosane
======

restful API to image scanning devices, using net-SANE

=====

This is just another itch scratcher project. At home we have a multi-function
printer/scanner hooked up to a Linux PVR, so that it can be shared over the 
network. Yosane will provide a RESTful API to net-SANE backend on Linux (using
[JFreeSane](https://github.com/sjamesr/jfreesane)) and simple web client to 
interact with the API.

Network scanning is a bit of a pain however. Even if you get your clients and
daemons set up all nice, you still have to sit and feed (in my case non-ADF)
your scanner each page, then "press" scan (currently means running scanimage 
on a terminal for me!). A lot of back and forth between the scanner and the
computer/laptop. 

My idea with Yosane was that it would be easier to sit next to the scanner with my
smartphone and command the scanner to do my bidding, rather than the aforementioned 
back and forth.

Would love to hear if anyone else but me ever uses it. When it's ready.
