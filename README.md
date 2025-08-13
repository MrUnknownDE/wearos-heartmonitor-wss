# wearos-heartmonitor-wss

Wear OS app that streams heart rate in real time over secure WebSocket.

## Build
- Android Studio Jellyfish/Koala, SDK 34
- Open the project, select **app** run config, deploy to Pixel Watch 3 (Wear OS 4)

## Configure
On the watch:
1. Open the app
2. Set **Server URL**: `wss://watch.puls.mrunk.de/ws`
3. Paste **JWT** token ("Bearer" not needed)
4. Tap **Save** → **Start**

## Server expectations
- Accept `Authorization: Bearer <JWT>`
- Receive JSON like: `{ "type":"hr", "bpm":92, "ts":"2025-08-13T10:15:30Z", "source":"wearos-healthservices" }`

## Notes
- App starts a Foreground Service to keep streaming alive
- Uses Health Services Exercise API for stable, low-latency HR
- No data stored on device; only in-flight streaming