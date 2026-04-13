# Google Play Publish Checklist — Cisco Quiz App

## 1. Pre-build preparation

- [ ] Replace test AdMob IDs with production IDs in two places:
  - `BannerAd.kt` → `BANNER_AD_UNIT_ID`
  - `QuizScreen.kt` → `INTERSTITIAL_AD_UNIT_ID`
  - Also update the App ID meta-data in `AndroidManifest.xml`
- [ ] Bump `versionCode` (must be higher than any previously uploaded build) and `versionName` in `app/build.gradle.kts`
- [ ] Fill in real values in `keystore.properties` (never commit this file)

## 2. Generate a signed release AAB

```bash
./gradlew bundleRelease
```

Output: `app/build/outputs/bundle/release/app-release.aab`

> If you don't have a keystore yet, create one first:
> ```bash
> keytool -genkey -v -keystore my-release-key.jks \
>   -alias release -keyalg RSA -keysize 2048 -validity 10000
> ```
> Place `my-release-key.jks` in the project root (it is gitignored).

## 3. Create app in Google Play Console

1. Go to [play.google.com/console](https://play.google.com/console) and sign in.
2. Click **Create app**.
3. Enter app name ("Cisco Quiz"), default language, and select **App** / **Free**.
4. Accept the declarations and click **Create app**.

## 4. Fill store listing

Under **Grow → Store presence → Main store listing**:

- [ ] Short description (≤ 80 chars) — e.g. "Master Cisco networking with topic-based quizzes"
- [ ] Full description (≤ 4000 chars)
- [ ] App icon (512 × 512 px, PNG, ≤ 1 MB)
- [ ] Feature graphic (1024 × 500 px)
- [ ] At least 2 phone screenshots (each ≥ 320 px, ≤ 3840 px on longest side)
- [ ] Category: **Education**
- [ ] Contact email

## 5. Upload AAB

Under **Release → Production** (or start with **Internal testing**):

1. Click **Create new release**.
2. If prompted, opt in to **Play App Signing** (recommended — Google manages your upload key).
3. **Upload** `app/build/outputs/bundle/release/app-release.aab`.
4. Add release notes (What's new).
5. Click **Save**.

## 6. Set content rating

Under **Policy → App content → Content rating**:

1. Click **Start questionnaire**.
2. Choose category: **Reference** (or Education).
3. Answer all questions (no violence, no mature content).
4. Expected result: **Everyone (ESRB) / PEGI 3**.
5. Click **Apply rating**.

## 7. Complete required declarations

- [ ] **Privacy policy URL** — required for any app; host a simple HTML page and paste the URL under **App content → Privacy Policy**.
- [ ] **Ads declaration** — under **App content → Ads**, tick "This app contains ads".
- [ ] **Target audience** — confirm the app is not directed at children under 13 (COPPA / GDPR) or enable family policy if it is.
- [ ] **Data safety** section — declare what data the app collects (AdMob collects device identifiers for ad personalisation).

## 8. Submit for review

1. Navigate back to **Production → [your release]**.
2. Review the release summary; fix any policy warnings.
3. Click **Send X changes for review** (the exact label depends on the release track).
4. Google typically reviews new apps within 3–7 days. You'll receive an email when it's approved or if changes are needed.

## 9. Post-publish

- [ ] Monitor **Android Vitals** (crashes, ANRs) in the Play Console.
- [ ] Replace test AdMob IDs with live IDs **before** the app goes live (if not done in step 1).
- [ ] Set up AdMob payment info at [admob.google.com](https://admob.google.com).
