---
layout: default
image: butterfly
tagline: “Realists do not fear the results of their study”
title: Latest News from Fyodor
---

<ul class="posts">
    {% for post in site.posts %}
    <li>
        <a href="{{ post.url }}">{{ post.title }}</a> <small>{{ post.date | date_to_string }}</small>
        {{ post.excerpt }}
        
        <a href="{{ post.url }}"><small>(read full post)</small></a>
    </li>
    <br>
    {% endfor %}
</ul>