# TapQuest
TapQuest is an interactive Android quiz app designed to make learning fun and accessible, especially for kids with dyslexia. It combines visual cues, audio guidance, and interactive questions to help learners process information more effectively. By engaging multiple senses—seeing, hearing, and interacting, TapQuest reduces reading barriers, improves focus, and builds confidence, making it an educational tool that is both entertaining and supportive for dyslexic learners.

#### APK Link: https://tinyurl.com/TapQuest
#### Demo Link: https://tinyurl.com/TapQuestDemo

## Getting Started
1. Clone this repo and open the folder in Android Studio
2. On the right top bar click on Sync Project with gradle files (Elephant symbol)
3. Once its completed, click on the Assemble app Run configuration (Hammer symbol)
4. Once that's also done, click on the Run app button and the app will open in the emulator
5. If you want the app's APK on the build menu click on Generate app bundles or APKs -> Generate APKs and the APK will be present inside `TapQuest/app/build/outputs/apk/debug` folder named as app-debug.apk

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


