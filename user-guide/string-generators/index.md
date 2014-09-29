---
layout: user-guide
tagline: Above all, avoid falsehood, every kind of falsehood, especially falseness to yourself. Watch over your own deceitfulness and look into it every hour, every minute.
image: dice-splash
title: String Generators
---

#Fyodor User Guide - String Generators#

Fyodor has a simple default random string generator, 
running `RDG.string.next();` right now returns 
                                                     
```
2LWKjDK1o6}@ps9P>!^`B/VEq@4}B7
```

on my machine. You can of course customise this behaviour quite a lot.

##String Length##

The length of the string that a generator will return can be set, either as a fixed value or a `Range`:

{% highlight java %}
RDG.string(Integer)
RDG.string(Range<Integer>)
{% endhighlight %}

So the default string generator is actually calling `RDG.string(30)` under the hood. Running
 `RDG.string(Range.closed(10, 20));` just now on my machine returned
 
 ```
 >^ywmBa2Nl=
 8e{mZj]lq!!![TF
 q=F]}}S]|YZ
 jELPHnNm3pq<}uj@lZ
 ~\;$9^$g5Fi
 q}O<:8W>p!Y_Q9x/.
 16SID?/C=xs^//k
 YLvVdL0f{ddQ_1k:]hS`
 ndwhEp-//K
 Q~;x[aMjWZ
 0J!4l&y#]0+qO70
 bLI/,g<TtY=eTv\<fj
 f~(nd'(\/045,v0U4['
 ,5(iWyKr+(7{7u[0
 ^OH(1mUYy3oe4lR
 )K@0)M<C*)mNDzQ>\'
 PwGb\z{#MQ
 </*9!kJ_F!CS)$5FxQ!
 h-i{F[M(VQBe>K/{,,#
 WI+ldG6T{9nD%0[;KHy;
```

lovely.

##Character Sets##

You can also customise the set of characters that a `StringGenerator` will select from. There are several ways
to do this, the first one is by specifying a range of integers that correspond to Unicode character points.
The default set of characters in Fyodor (used in the examples above) is `Range.closed(33,126)` which
corresponds to the following characters:

```
!#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_`abcdefghijklmnopqrstuvwxyz{|}~
```

this is basically (almost - see later) the ASCII standard character set. You can create a `StringGenerator`
with a different underlying character set by supplying one or more ranges as a parameter in addition to the length of 
the string to generate:

{% highlight java %}
RDG.string(Integer lengthOfString, Range<Integer>... charSetRange)
RDG.string(Integer lengthOfString, CharacterSetRange... charSetRange)
RDG.string(Range<Integer> lengthOfString, Range<Integer>... charSetRange)
RDG.string(Range<Integer> lengthOfString, CharacterSetRange... charSetRange)
{% endhighlight %}

The `CharacterSetRange` in the code above is an enum with some pre-defined chunks of Unicode:

{% highlight java %}
CharacterSetRange.defaultLatinBasic
CharacterSetRange.latin1
CharacterSetRange.latinExtendedA
CharacterSetRange.latinExtendedB
{% endhighlight %}

###`defaultLatinBasic`###
The default character set used (shown earlier) if none specified, this is the ASCII standard without the control
characters (1 - 32, things like backspace, linefeed etc) and without the double quote character (see later)

###`latin1`###
The non-control characters from the second Unicode block (160 - 255) - common international punctuation
marks and accented or novel Latin characters for Western European languages:

```
 ¡¢£¤¥¦§¨©ª«¬­®¯°±²³´µ¶·¸¹º»¼½¾¿ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóôõö÷øùúûüýþÿ
```

(the first character here is a non-breaking space)

###`latinExtendedA`###
The characters from the third Unicode block (256 - 383) - accented Latin characters for mostly 
eastern European languages:

```
ĀāĂăĄąĆćĈĉĊċČčĎďĐđĒēĔĕĖėĘęĚěĜĝĞğĠġĢģĤĥĦħĨĩĪīĬĭĮįİıĲĳĴĵĶķĸĹĺĻļĽľĿŀŁłŃńŅņŇňŉŊŋŌōŎŏŐőŒœŔŕŖŗŘřŚśŜŝŞşŠšŢţŤťŦŧŨũŪūŬŭŮůŰűŲųŴŵŶŷŸŹźŻżŽžſ
```

###`latinExtendedB`###
The characters from the fourth Unicode block (384 - 591) - a whole mish-mash of weird and wonderful
glyphs and characters from around the world:

```
ƀƁƂƃƄƅƆƇƈƉƊƋƌƍƎƏƐƑƒƓƔƕƖƗƘƙƚƛƜƝƞƟƠơƢƣƤƥƦƧƨƩƪƫƬƭƮƯưƱƲƳƴƵƶƷƸƹƺƻƼƽƾƿǀǁǂǃǄǅǆǇǈǉǊǋǌǍǎǏǐǑǒǓǔǕǖǗǘǙǚǛǜǝǞǟǠǡǢǣǤǥǦǧǨǩǪǫǬǭǮǯǰǱǲǳǴǵǶǷǸǹǺǻǼǽǾǿȀȁȂȃȄȅȆȇȈȉȊȋȌȍȎȏȐȑȒȓȔȕȖȗȘșȚțȜȝȞȟȠȡȢȣȤȥȦȧȨȩȪȫȬȭȮȯȰȱȲȳȴȵȶȷȸȹȺȻȼȽȾȿɀɁɂɃɄɅɆɇɈɉɊɋɌɍɎɏ
```

As mentioned earlier you can supply more than one `Range` or `CharacterSetRange` when creating a `StringGenerator`
if you have particularly exotic needs, the generator will choose characters from all of them.

##Character Filters##

A `StringGenerator` can also be created with a `CharacterFilter`.  This is an interface used to fine-tune
the underlying array of characters that a `StringGenerator` can select from:

{% highlight java %}
public interface CharacterFilter {
    public boolean includeCharacter(char c);
}
{% endhighlight %}

This is called before each character from the `Range` is added to the `StringGenerator`'s underlying
character set. This is how the double-quotes are removed from the character set in the default `StringGenerator`
implementation, or you can supply your own filter when you create a `StringGenerator`:
 
{% highlight java %}
RDG.string(Integer, CharacterFilter)
RDG.string(Range<Integer>, CharacterFilter)
RDG.string(Integer, CharacterSetFilter)
RDG.string(Range<Integer>, CharacterSetFilter)
{% endhighlight %}

The `CharacterSetFilter` in the code above is an enum with some pre-defined `CharacterFilter`s:

{% highlight java %}
CharacterSetFilter.AllExceptDoubleQuotes
CharacterSetFilter.DomainName
CharacterSetFilter.EmailLocalPart
CharacterSetFilter.LettersAndDigits
CharacterSetFilter.LettersOnly
{% endhighlight %}

###`AllExceptDoubleQuotes`###
Returns true for everything except double quotes, this is the default filter used if no other is 
specified.

###`LettersAndDigits`###
Only returns true if {% highlight java %}Character.isLetterOrDigit(c){% endhighlight %} returns true.

###`LettersOnly`###
Only returns true if {% highlight java %}Character.isLetter(c){% endhighlight %} returns true.

###`DomainName`###
Only returns true for letters, numbers or hyphens.

###`EmailLocalPart`###
Only returns true for the characters allowed in the local part (the bit before the '@') of an email address.

There is one more implementation of `CharacterFilter` available, which is `RegExCharacterFilter` 
which takes a regular expression string in its constructor. As the name suggests, this filters 
out any character that doesn't return true when matching the given regular expression.
This is how the `DomainName` and `EmailLocalPart` filters are implemented.


### `string(Integer, String)`
### `string(Integer, char[])`

These are both convenience methods if you really want to go crazy if supplying your own 
`Range` and/or `CharacterFilter` is just too fiddly, they'll both just take the 
supplied `Character`s and use them as the underlying source. For instance in Fyodor's `NINumberGenerator`
the last character needs to be an A, B, C or D.  This is generated with 

{% highlight java %}
RDG.string(1, "ABCD")
{% endhighlight %}

You can call `char[] getCharSet()` on any `StringGenerator` if it's easier to just fine tune the 
underlying charset of an existing generator (this returns a copy of the array used by the 
generator so you can add and remove characters without borking anything but it means you'll need 
to create a new generator to use it).