# ZulipClient - приложение клиент для мессенджера Zulip

## Описание полученных навыков
Стек технологий в проекте: Kotlin, Git, Gradle, Dagger2, Room, Kotlin Coroutines & Flow, Retrofit, Cicerone, Kotest, Kaspresso, Wiremock, MockK, MVI

- Разработал мобильное приложение на языке Kotlin
- Offline-first app. Кеширование происходит в базу данных и SharedPreferences
- Реализовал взаимодействие с сервером посредством long polling запросов
- Взаимодействие с БД через библиотеку Room. Применял транзакции
- Использовал RecyclerView с применением делегатов адаптера для разных типов элементов
- Приложение многослойно и написано в соответствии с базовыми принципами Clean Architecture
- Архитектурный паттерн презентационного слоя - MVI
- Unit-тесты написаны на фреймворке Kotest. UI-тесты на Kaspresso
- Реализовал поиск, применяя инструменты Kotlin Coroutines & Flow
- Создал кастомные реализации View и ViewGroup для удобства показа сообщений на экране чата

## Особенности запуска
В build.gradle.kts модуля необходимо указать ваш base_url и ваш токен-авторизации в классе AuthHeaderInterceptor в пакете com.viktorger.zulip_client.app.core.network

## Демонстрация работы проекта
https://github.com/user-attachments/assets/d45ad7ab-dcc9-408c-96fb-a3bfa1805c3c

