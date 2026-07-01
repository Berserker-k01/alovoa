# <img src="src/main/resources/static/img/android-chrome-192x192.png" width="70"> EyaLove

EyaLove is a modern dating platform for anonymous, local and intention-based connections
(love, friendship, social meetups, casual). It is built on top of the open-source
[Alovoa](https://github.com/Alovoa/alovoa) project.

Key differences with EyaLove: **anonymity + proximity + clear intentions + an active community**,
paid account activation via Mobile Money (Wave, MTN, Moov, Tmoney, Orange...) through Tchin,
community announcements, and an extended admin panel.

> This repository is a customized fork of Alovoa. Original project credits below.

[![Website](https://img.shields.io/website?url=https%3A%2F%2Falovoa.com%2F)](https://alovoa.com/)
[![Testing Website](https://img.shields.io/website?url=https%3A%2F%2Fbeta.alovoa.com%2F&label=Testing%20website)](https://beta.alovoa.com/)
[![Codeberg](https://img.shields.io/badge/Codeberg-Mirror-blue?logo=codeberg)](https://codeberg.org/Nonononoki/alovoa)
[![GitHub issues](https://img.shields.io/github/issues/Alovoa/Alovoa?color=red)](https://github.com/Alovoa/alovoa/issues)
[![Matrix](https://img.shields.io/matrix/alovoa_love:matrix.org?label=Matrix%20chat)](https://matrix.to/#/#alovoa_love:matrix.org)
[![Mastodon Follow](https://img.shields.io/mastodon/follow/106347928891909537?label=Mastodon&style=social)](https://mastodon.social/@alovoa_love)
[![Twitter Follow](https://img.shields.io/twitter/follow/alovoa_love?label=Twitter&style=social)](https://twitter.com/alovoa_love)
[![Subreddit subscribers](https://img.shields.io/reddit/subreddit-subscribers/Alovoa?label=Subreddit&style=social)](https://www.reddit.com/r/Alovoa/)
[![GitHub license](https://img.shields.io/github/license/Alovoa/Alovoa?color=lightgrey)](/LICENSE)

What makes Alovoa different from other platforms?
- No ads
- No selling of your data
- No paid features (no "pay super-likes", "pay to swipe", "pay to view profile" or "pay to start a chat")
- No unsecure servers
- No closed-source libraries
- No seeing people you don't want to see with advanced filters
- Your most private data is securely encrypted

### Mobile apps

Alovoa is also available as a mobile app. Check out Android app [source code repo](https://github.com/Alovoa/alovoa-expo), download an app on [F-Droid](https://f-droid.org/en/packages/com.alovoa.expo/) or [Google Play](https://play.google.com/store/apps/details?id=com.alovoa.expo)

### Contribute
- Tell your friends about it and share on social media! This is the best way to make it grow.
- Improve the project by posting in [Issues](https://github.com/Alovoa/alovoa/issues) and make a PR upon Issue discussion.
- Translate this project into your preferred language on [Weblate](https://hosted.weblate.org/projects/alovoa/alovoa/)

<details>
  <summary>Translation status</summary>
  
[![Translation Status](https://hosted.weblate.org/widgets/alovoa/-/multi-auto.svg)](https://hosted.weblate.org/engage/alovoa/)
</details>

### Donate
Like this project? Consider making a donation.

| Platform        | Link                                                                                              |
| :-------------: | :----------------------------------------:                                                        |
| Alovoa          | [alovoa.com/donate-list](https://alovoa.com/donate-list)                                          |
| BuyMeACoffee    | [buymeacoffee.com/alovoa](https://www.buymeacoffee.com/alovoa)                                    |
| Ko-fi           | [ko-fi.com/Alovoa](https://ko-fi.com/Alovoa)                                                      |
| Liberapay       | [liberapay.com/alovoa/donate](https://liberapay.com/alovoa/donate)                                |
| Open Collective | [opencollective.com/alovoa](https://opencollective.com/alovoa)                                    |
| BTC             | <details><summary>Click to reveal</summary>`bc1q5yejhe5rv0m7j0euxml7klkd2ummw0gc3vx58p`</details> |


### How to build
- Install OpenJDK 17
- Install maven: https://maven.apache.org/install.html
- Setup a database (MariaDB is officially supported)
- Setup an email server or use an existing one (any provider with IMAP support should work)
- Enter credentials for database server, email server and encryption keys in `application.properties`
- Execute `mvn install` in the root folder

### Run with Docker (recommended)

You can use [Docker](https://docs.docker.com/engine/install/) and [Docker Compose](https://docs.docker.com/compose/).
All configuration is provided via environment variables, so you do **not** need to edit
`application.properties`.

1. Copy the example environment file and fill in your values (database passwords, encryption
   keys, admin account, mail server and Tchin credentials):

```bash
cp .env.example .env
# then edit .env
```

2. Build and start the stack (server + MariaDB):

```bash
docker compose build
docker compose up -d
docker compose logs -f
```

The server starts on the port defined by `SERVER_PORT` (default `8081`), and the database
service starts automatically with a health check before the app boots.

Important variables (see `.env.example` for the full list):
- `APP_TEXT_KEY` (16/24/32 chars) and `APP_TEXT_SALT` (16 chars) — required for database encryption
- `APP_ADMIN_EMAIL` / `APP_ADMIN_KEY` — the default admin account
- `ACCESS_PAYMENT_ENABLED`, `ACCESS_PRICE` (default 11000), `ACCESS_CURRENCY` (default XOF)
- `TCHIN_API_KEY`, `TCHIN_SECRET`, `TCHIN_WEBHOOK_SECRET` — Mobile Money payment provider

### Debugging
- Spring Tool Suite / IntelliJ is recommended for debugging
- Install lombok for your IDE (Not needed for IntelliJ)

### Documentation:
- Please read the [DOCUMENTATION.md](/DOCUMENTATION.md)

### Licenses:
- All code is licensed under the AGPLv3 license, unless stated otherwise. 
- All images are proprietary, unless stated otherwise.
- Third-party web libraries can be found under `resources/css/lib` and `resources/js/lib` and have their own license.
- Third-party Java libraries can be found in the `pom.xml` and have their own license.
