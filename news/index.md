---
layout: default
image: dice-splash
tagline: “Much unhappiness has come into the world because of bewilderment and things left unsaid.”
         ― Fyodor Dostoyevsky
title: Latest News from Fyodor
---

<ul class="posts">
    {% for post in site.posts %}
    <li>
        <a href="{{ post.url }}">{{ post.title }}</a> <small>{{ post.date | date_to_string }}</small>
        {{ post.excerpt }}
    </li>
    {% endfor %}
</ul>