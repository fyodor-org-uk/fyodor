---
layout: default
tagline: “Talking nonsense is the sole privilege mankind possesses over the other organisms. It's by talking nonsense that one gets to the truth! I talk nonsense, therefore I'm human”
image: dice-splash
title: Other Generators
---

#Fyodor User Guide - Other Generators#

This section describes generators other than the general string and number generators.
  
## RDG.bool() ##
This returns a `Generator<Boolean>` that generates `Boolean.TRUE` or `Boolean.FALSE` at random.

## RDG.percentageChanceOf(int) ##
Given an integer between 1 and 99 this returns a `Generator<Boolean>` that will return true 
approximately that percent of the time.
 
## RDG.postcode() ##
This returns a `Generator<String>` that generates a random UK postcode. These are of the format

```
W = any letter A - Z (upper or lowercase)
S = same as W but excluding I and Z
D = number from 0 - 9
 
WDW DWW
WD DWW
WDD DWW
WSD DWW
WSDD DWW
WSDW DWW
```

except for roughly 1% of the time when it will return the girobank postcode, GIR 0AA.

{% highlight java %}
Generator<String> generator = RDG.postcode();
for (int i = 0; i < 5; i++) {
    System.out.println(generator.next());
}
{% endhighlight %}

```        
eS2K 4kd
l8 6IN
eS8Q 5vW
A8E 3OJ
oL9p 6pk
```
        
## RDG.niNumber() ##
This returns a `Generator<String>` the generates a random UK National Insurance Number. These are
in the form of 2 letters, 6 digits and 1 letter (e.g. AB123456C) with some restrictions about which letters can be 
used, see [this Wikipedia article](http://en.wikipedia.org/wiki/National_Insurance_number#Format) for more details about formats.

{% highlight java %}
Generator<String> generator = RDG.niNumber();
for (int i = 0; i < 5; i++) {
    System.out.println(generator.next());
}
{% endhighlight %}

```
MZ671867D
YY499967A
BX290025C
MT786926B
GP836645B
```

## RDG.domain() and RDG.domain(Range) ##
This returns a `Generator<String>` that generates a name for an internet domain following the rules
for a host name: ASCII letters 'a' to 'z' (case insensitive) the digits 0 to 9 and a hyphen. A domain
also cannot start or end with a hyphen.

If a `Range` is supplied then this determines the length of the domain name to be generated; the default
is `Range.closed(5, 40)` although technically each label making up a domain name can be up to 63 characters long.

**Note** this doesn't generate a fully-qualified internet URL, there's no protocol or TLD, it's 
just a string that's a valid hostname.

{% highlight java %}
Generator<String> generator = RDG.domain();
for (int i = 0; i < 5; i++) {
    System.out.println(generator.next());
}
{% endhighlight %}

```
PEwSjvIZwgLMMAABDL7cbE
Igx4LvIJNgYSnjvhBmy086lMgAcsEc
YAa44IM-ujo2w9J
PedEd1ALblBfp71GxaoAXxEtWLl6hlLzI
yNt7bdv0ct81tOBI1wZZ
```

## RDG.domainSuffix() ##
A domain suffix is the bit at the end of an internet domain, e.g. '.com', '.co.uk' are common ones
you've probably seen before but '.tree.museum', '.sør-aurdal.no', 'சிங்கப்பூர்' or '.慈善'
are also perfectly valid suffixes. Surprisingly there is no authoritative definition of what makes
a valid suffix. Mozilla made an heroic effort to find out all the suffixes out there which became the [Public Suffix List.]
(https://publicsuffix.org) Fyodor's suffix generator is based on a slightly cutdown version of [their list](https://publicsuffix.org/list/effective_tld_names.dat), a massive
array of known suffixes from which one is picked at random.

{% highlight java %}
Generator<String> generator = RDG.domainSuffix();
for (int i = 0; i < 5; i++) {
    System.out.println(generator.next());
}
{% endhighlight %}

```
ostroda.pl
go.jp
mobi.tz
gsm.pl
iide.yamagata.jp
```

## RDG.emailAddress() ##
This returns a `Generator<String>` that generates a random email address, see [this Wikipedia article]
(http://en.wikipedia.org/wiki/Email_address) for more details about what is and isn't a correct email address.
Fyodor's email generator uses the previously mentioned domain and domain suffix generators for the
domain part of an email address (the bit after the '@') and a separate generator for the local part
(the bit before the '@') that takes care of the various fiddly rules about characters allowed in that part.

The local part generator doesn't generate IP addresses or quoted strings, these are technically valid
 formats for the local part and maybe we'll look at extending the local part generator to accomodate 
 them in future releases.
 
 {% highlight java %}
 Generator<String> generator = RDG.emailAddress();
 for (int i = 0; i < 5; i++) {
     System.out.println(generator.next());
 }
{% endhighlight %}

```
4kt*?JS0dpr6px'Cy$R7X49XQF~s$K!smB`/~?J'CMl4eZ|xL@jGkLVSHtPcczjtZg-2-eV2oo4krGGCa5fIUcAfQ6.royken.no
|l*Y$T1@qa6VorbKbQ.rebun.hokkaido.jp
BGf5C.S2QmIsBIB/c1p9onL_jujN4Rt!7KFJ~@eiRoPJAmevXd4uYJnxIWx2ti.kadogawa.miyazaki.jp
oPkP6dndZ3.5ax?aW&%YGpEt#Me'Yo!O@WU879b7x9sIeNfCBTN6xigsDwp9wo1.kanzaki.saga.jp
MGjdCcXLk#s+Bp~rQ46BJrQjNRtiPk=a!w=Viy7+M4cf'U?^@HHi0m.is
```

## RDG.uri() ##
This returns a `Generator<java.net.URI>` that generates a random internet URI. This uses the previously
mentioned domain and suffix generators prefixed with `http://`, `https://`, `http://www.` or
`https://www.`

{% highlight java %}
Generator<String> generator = RDG.uri();
for (int i = 0; i < 5; i++) {
    System.out.println(generator.next());
}
{% endhighlight %}

```
https://www.audDL.pe.it
https://h6xflVRgIPfSCKdVv3QeqmZL8uFHbxOHPgYuDBIG.gov.ml
http://www.fYToPUcTVIHO8CZ1uOU7ENhR-tj-HCWOIPdtNA.net.gt
https://www.xwx0pxTV4XekYZ9bIcldJ.ایران.ir
https://cxAjBGLThKGJT3svYbDDDXvBwYq.網路.tw
```

Next Page: [Collection Generators]({{ site.baseurl }}/user-guide/collections)