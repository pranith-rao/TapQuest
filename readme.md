# TapQuest
<div align="center">
    <img src="https://github.com/user-attachments/assets/fb6839c4-a3a7-4f48-81a9-28a5b50cbc79" alt="pic (1)" width="400" />
</div>

TapQuest is an interactive Android quiz app designed to make learning fun and accessible, especially for kids with dyslexia. It combines visual cues, audio guidance, and interactive questions to help learners process information more effectively. By engaging multiple senses—seeing, hearing, and interacting, TapQuest reduces reading barriers, improves focus, and builds confidence, making it an educational tool that is both entertaining and supportive for dyslexic learners.

[📦 Download APK](https://tinyurl.com/TapQuest) | [🎬 Watch Demo](https://tinyurl.com/TapQuestDemo) | [📑 View PPT](https://tinyurl.com/TapQuestPPT) 

## Getting Started
- Clone this repo and open the folder in Android Studio
- On the right top bar click on Sync Project with gradle files (Elephant symbol)
- Once its completed, click on the Assemble app Run configuration (Hammer symbol)
- Once that's also done, click on the Run app button and the app will open in the emulator
- If you want the app's APK on the build menu click on Generate app bundles or APKs -> Generate APKs and the APK will be present inside `TapQuest/app/build/outputs/apk/debug` folder named as app-debug.apk

## Key Features:
- Audio support for themes and questions to aid dyslexic learners.
- Confetti animation for correct answers.
- Engaging UI built with Jetpack Compose.
- Dynamic leaderboard with rank display to encourage healthy competition.

## Screens & Functionality
- **Welcome Screen**: Players enter their name (mandatory) and select a theme (Animals, Birds, Colors) displayed as clickable tiles with visual and audio cues. The chosen theme shows a tick mark, and the quiz starts only when the "Start Quiz" button is pressed.
- **Countdown Screen**: A 3-2-1 countdown builds anticipation before the quiz begins.
- **Game Screen**: Quiz questions appear with multiple options (images or colors). Each question plays an audio cue, provides instant feedback, and shows confetti on correct answers to boost engagement.
- **Leaderboard Screen**: Displays scores and ranks, plays a rank announcement audio, and allows players to replay the game.

## Info
This app was built as part of the Semicolons Lite Hackathon's Dyslexia Challenge, where the problem statement was to develop innovative Android applications to support children with dyslexia and help create engaging, gamified learning solutions that address the unique challenges faced by children with reading difficulties.


