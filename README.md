# labs-cross — Кросплатформне програмування (Kotlin Multiplatform)

Луханін Богдан, група 344Б, ЧНУ.

| Лаба | Папка | Що всередині |
|---|---|---|
| 03 | [labs/03-mvi](labs/03-mvi) | Проєкт лаб 10–15 (REST-клієнт jsonplaceholder), переведений з MVVM на MVI; use cases у domain; `Result.map` у data |
| 04 | [labs/04-kmp-pager](labs/04-kmp-pager) | CampusTix: новий KMP-проєкт, дизайн ([design/](labs/04-kmp-pager/design)), кастомна тема, Kermit, multiplatform-settings, онбординг на Pager, uk/en |

Кожна лаба — окремий Gradle-проєкт (Android, Desktop, Web/Wasm). Відкривати в Android Studio саму папку лаби.

```
./gradlew :androidApp:assembleDebug
./gradlew :desktopApp:run
./gradlew :webApp:wasmJsBrowserDevelopmentRun
./gradlew :shared:jvmTest
```
