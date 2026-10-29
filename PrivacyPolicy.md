# Privacy Policy — What's in Pvot

_Effective 30 October 2026_

What's in Pvot (package `com.prauga.pvot`) is the showcase app for the Pvot design system,
published by Saalim Quadri for PVOT-OSS. This policy explains what the app does with your data.

**In short: the app doesn't collect, store on a server, share or sell any personal data. It has
no accounts, no ads, no analytics and no tracking.**

## What the app keeps on your device

- **Your settings:** whether dynamic color, the new design and how much glass to draw are on.
- **A cache of what it downloads:** responses from the GitHub API and the images it shows, so it
  doesn't download them again every time.

None of this leaves your device, except through Android's own backup if you have turned it on: your
settings may then be backed up to your Google account, under Google's terms. Clearing the app's
storage or uninstalling it deletes all of it.

## What the app downloads

The app needs the internet permission only to show public content. It makes requests to:

| Service | What for |
|---|---|
| GitHub (`api.github.com`, `avatars.githubusercontent.com`, `opengraph.githubassets.com`) | The PVOT-OSS projects, contributors, their profile pictures and project preview images |
| `squadri.me` | The posts shown on the Home screen |

These requests send nothing about you beyond what any app or browser sends to load a page: your IP
address, the time, and basic technical details such as the app's user agent. The services that
receive them may log them under their own policies; see the
[GitHub Privacy Statement](https://docs.github.com/site-policy/privacy-policies/github-general-privacy-statement).
The app never signs in to GitHub and sends no account details.

Links in the app, such as a project's GitHub page, open in your browser and are covered by that
website's own policy.

## Permissions

- `INTERNET`, to download the content above. The app asks for no other permission.

## Children

The app is not directed at children and collects no data from anyone, children included.

## Changes

If the app starts handling data differently, this policy will be updated before that version is
released, with a new effective date at the top.

## Contact

Questions about this policy: danascape@gmail.com, or open an issue at
https://github.com/PVOT-OSS/PvotLib/issues.
