Minema Resurrection
======

Minema Resurrection is a combined version of [Minema](https://github.com/ata4/minema) and [BaureCam](https://github.com/daipenger/BauerCam) for modern MC versions.

A Minecraft mod for offline rendering and video capturing.

[Demo video](https://www.youtube.com/watch?v=61XfHB9g6EQ):
Butter smooth 1080p60, supersampled from 4K, up to 64 chunks render distance and up to 4096x4096 shadow maps. Recorded on a single GTX 750 Ti.

**Current features:**
- Records the game, both color and optionally **depth**
- Automatically exports every frame as TGA or encodes it to a single mp4 video file (h264, yuv420p color format) using [FFMpeg](https://www.ffmpeg.org/)
- Synchronizes the game engine and [OptiFine](https://optifine.net/home)'s/[karyonix](https://www.minecraftforum.net/forums/mapping-and-modding-java-edition/minecraft-mods/1286604-shaders-mod-updated-by-karyonix)'s shader pipeline from real time to the video recording framerate
- Possible to set any resolution for recording, even higher than your screen resolution
- Brings two techniques to heavily accelerate chunk loading during recording

For users
======

**Installing and using:**

Download the mod and load it with Forge.
You can start/stop recording by pressing F4 (you can also press Shift + F4 for advanced configuration) or using "/minema enable" and "/minema disable". If you are a mod developer and want to integrate with Minema, just using these commands might very well be enough for you.

Minema can also be configured quite substantially: Mods -> Minema -> Config. If you hover over one option it will display the explanation for this option. If you need further help, feel free to contact me or open an issue if you think that an explanation needs improvement.

There are several camera path tools that work with Minema: Something simple is [BauerCam](https://github.com/daipenger/BauerCam) or if you need something more fancy [Aperture](https://minecraft.curseforge.com/projects/aperture).

**Setting up FFMpeg: (you have to do this if you want mp4 files)**

Linux users should already be able to install FFMpeg using their favourite package manager. Otherwise you will find builds on https://www.ffmpeg.org/download.html#build-linux.

Windows users can get builds here: [32bit](https://ffmpeg.zeranoe.com/builds/win32/static/ffmpeg-3.4.2-win32-static.zip), [64bit](https://ffmpeg.zeranoe.com/builds/win64/static/ffmpeg-3.4.2-win64-static.zip). Unpack ffmpeg.exe (it is in bin/ in this archive) and move it to the root minecraft install folder. (where you would also find options.txt) You can also move it to somewhere else and change the encoder path if you prefer it that way.

Make sure to enable 'Use video encoder'. If you have troubles setting up FFMpeg or Minema, make sure to check out this video:

[![Thumbnail](http://i3.ytimg.com/vi/wBfOn4cmUDw/maxresdefault.jpg)](https://www.youtube.com/watch?v=wBfOn4cmUDw)

For developers
======

This setup uses [Gradle](https://gradle.org/) like any other Forge mod, so you should feel right at home.

If you are totally new to Forge: In a nutshell you should import it into your IDE and run the corresponding gen task for your IDE. But I always recommend just reading one of the starter tutorials.
