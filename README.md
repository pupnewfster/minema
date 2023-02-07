Minema Resurrection
======

This is a mod about a camera named Stanley... wait, no... that's not right.
Let's try that again. How about a description of a mod?
Yes, that sounds right.
 
Ok, here it is!

Description
======

As of version 2.0, Minema is a cinematic frametime-based video capture and camera pathing tool. Create perfectly fluid video at a locked framerate and even oversampled video resolution with shaders and preloaded chunks. Minema includes shadersync to ensure the shader animations are locked to the captured framerate and includes options for engine time scaling.

Back in 2016, a camera mod was created to perfectly complement Minema. That mod was called BauerCam. BauerCam set out to work seamlessly alongside Minema and provide all the tools necessary for anyone with (almost) any GPU to capture smooth, cinematic video within Minecraft. Many camera mods have tried to take its place but there just wasn't anything like it. Now, BauerCam has been merged into Minema Resurrection to reunite these two mods as they were meant to be. Destined to create cinematic greatness, hand in hand, as one.

Camera
======

NEW for version 2.0: camera and path tools!

Use the 'p' key to add a camera point. To start a recording you must have at least two camera points, which creates a camera path. The path is interpolated between these points with a cubic interpolation by default and can be changes via commands.

Tip: Use the 'J' and 'L' buttons to tilt the camera axis. Use 'K' to reset the axis.

Use the '/minema cam' command and get familiar with the commands. It should be obvious what everything does.

Use '/minema cam start X Y' to run the camera path where X is the number of frames to run and Y is record (either true or false).

Example: You want an asynchronously shot video to last for 10 seconds with a synchronous frame rate of 60fps and you wish to record that camera path, enter: '/minema cam start 600 true'.

![path1](https://i.imgur.com/e95UNPw.png)

Paths
======

Possibly the best feature of Minema 2.0 is the new camera pathing tools. You can see your set points and path in-game with the glorious red line! The red line can be toggled via command but, who are we kidding, the line is probably your best friend.

Use the Minema cam commands to goto specific points along the path.

Unlike other mods, Minema's camera and path tools will create a camera shot guaranteed to last for a specified amount of frames at your specified framerate output.

Features
======

Records the game, both color and, optionally, depth.
Exports frames as TGA or encodes to a single MP4 file using FFMpeg (download & configuration required).
Synchronizes the game engine and shader pipeline to the video recording framerate.
Set any resolution for recording, even higher than your screen resolution!
Brings two techniques to heavily accelerate chunk loading during recording.
Efficiently record with automatic start and stop linked to path and frame limit.

Usage
======

Download the mod and load it with Forge. While in game, you can start/stop recording by pressing F4 (you can also press Shift + F4 for advanced configuration) or using "/minema enable" and "/minema disable". Minema can also be configured via config file.

ffmpeg
======

[FFMpeg MUST be installed if you want to record MP4 files.](https://www.ffmpeg.org/download.html#build-linux)

Linux users should already be able to install FFMpeg using their favourite package manager. Otherwise you will find builds on the FFMpeg website*.

Windows users can get builds on the BtnB GitHub repo*. Unpack ffmpeg.exe (it is in bin/ in this archive) and move it to the root minecraft install folder. (where you would also find options.txt) You can also move it to somewhere else and change the encoder path (via config file) if you prefer it that way. Make sure to enable **'Use video encoder'**.

DISCLAIMER
======

Issues may be reported but do not expect them to be fixed in a timely matter, if at all. This download is provided as a fan service to users that have been waiting for a Minema update, allowing for easier access to downloads rather than requiring manual compilation, and not as a commitment to full time maintenance. Downloads will ONLY be provided for the version(s) shown in the files tab. Any requests to port Minema to other versions will be ignored. You get what you get and don't be upset.

Samples
======

Mekanism V10 teaser trailer created using Minema: 
[![MekanismTrailer](https://i.imgur.com/RMGrZo4.png)](https://youtu.be/sJVO6Gh7rmE)


For developers
======

This setup uses [Gradle](https://gradle.org/) like any other Forge mod, so you should feel right at home.

If you are totally new to Forge: In a nutshell you should import it into your IDE and run the corresponding gen task for your IDE. But I always recommend just reading one of the starter tutorials.
