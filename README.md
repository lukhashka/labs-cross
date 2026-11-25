# labs-cross — Кросплатформне програмування (Kotlin Multiplatform)

Луханін Богдан, група 344Б, ЧНУ.

| Лаба | Папка | Що всередині |
|---|---|---|
| 03 | [labs/03-mvi](labs/03-mvi) | Проєкт лаб 10–15 (REST-клієнт jsonplaceholder), переведений з MVVM на MVI; use cases у domain; `Result.map` у data |
| 04 | [labs/04-kmp-pager](labs/04-kmp-pager) | CampusTix: новий KMP-проєкт, дизайн ([design/](labs/04-kmp-pager/design)), кастомна тема, Kermit, multiplatform-settings, онбординг на Pager, uk/en |
| 05 | [labs/05-auth-ui](labs/05-auth-ui) | Navigation 3, екран авторизації за MVI, валідація, дії клавіатури |
| 06 | [labs/06-auth-networking](labs/06-auth-networking) | Ktor, KSafe, вхід через dummyjson, автоматичне оновлення токенів, домашня сторінка |
| 07 | [labs/07-adaptive-layouts](labs/07-adaptive-layouts) | Адаптивна розмітка онбордингу та входу за `WindowSizeClass`: одна колонка / дві панелі |
| 08 | [labs/08-paging-list-detail](labs/08-paging-list-detail) | Home: стрічка дописів через Paging 3 (`PagingSource`, `Pager`, стани refresh/append), розмітка «список–деталі» |

Кожна лаба — окремий Gradle-проєкт (Android, Desktop, Web/Wasm). Відкривати в Android Studio саму папку лаби.

```
./gradlew :androidApp:assembleDebug
./gradlew :desktopApp:run
./gradlew :webApp:wasmJsBrowserDevelopmentRun
./gradlew :shared:jvmTest
```
