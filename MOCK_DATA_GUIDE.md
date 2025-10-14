# 🧪 Руководство по использованию мок-данных

## 📋 Обзор

В проекте настроена система переключения между реальным NASA API и мок-данными для тестирования и разработки.

При разработке наблюдал сбои работы API, например он не работал целыми днями.

## 🔄 Способы переключения

### 1. **Через BuildConfig (рекомендуемый)**

#### Включить мок-данные:
```kotlin
// В app/build.gradle.kts
buildConfigField("boolean", "USE_MOCK_DATA", "true")
```

#### Отключить мок-данные (использовать реальный API):
```kotlin
// В app/build.gradle.kts
buildConfigField("boolean", "USE_MOCK_DATA", "false")
```

### 2. **Через DataModule (программно)**

Можно изменить логику в `DataModule.kt`:
```kotlin
@Provides
@Singleton
fun provideApodRepository(...): ApodRepository {
    return if (BuildConfig.USE_MOCK_DATA) {
        MockApodRepositoryImpl(...)  // Мок-данные
    } else {
        ApodRepositoryImpl(...)      // Реальный API
    }
}
```

## 📁 Структура мок-файлов

```
app/src/main/java/com/example/planets/data/
├── mock/
│   └── MockApodData.kt              # Тестовые данные (8 APOD)
├── paging/
│   └── MockApodPagingSource.kt     # Мок-пагинация
└── repository/
    └── MockApodRepositoryImpl.kt   # Мок-репозиторий
```

## 🎯 Когда использовать мок-данные

### ✅ **Рекомендуется использовать мок-данные:**
- **Разработка UI** - когда нужно тестировать интерфейс
- **Отладка** - когда API недоступен или работает нестабильно
- **Демонстрация** - для показа приложения без интернета
- **Тестирование** - для unit и UI тестов
- **Презентации** - когда нужны стабильные данные

### ❌ **Не рекомендуется использовать мок-данные:**
- **Продакшн** - всегда использовать реальный API
- **Финальное тестирование** - проверять с реальными данными
- **Интеграционные тесты** - тестировать реальную интеграцию

## 🔧 Настройка мок-данных

### Изменение тестовых данных:
```kotlin
// В MockApodData.kt
val mockApods = listOf(
    Apod(
        date = "2024-01-15",
        title = "Новое название",
        explanation = "Новое описание...",
        url = "https://via.placeholder.com/400x300/4CAF50/FFFFFF?text=New",
        hdUrl = "https://via.placeholder.com/800x600/4CAF50/FFFFFF?text=New+HD",
        mediaType = "image",
        serviceVersion = "v1"
    ),
    // ... другие элементы
)
```

### Изменение задержки загрузки:
```kotlin
// В MockApodPagingSource.kt
override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Apod> {
    delay(1000) // Изменить на нужную задержку (в миллисекундах)
    // ...
}
```

## 🚀 Быстрое переключение

### Для включения мок-данных:
1. Откройте `app/build.gradle.kts`
2. Найдите строку: `buildConfigField("boolean", "USE_MOCK_DATA", "false")`
3. Измените на: `buildConfigField("boolean", "USE_MOCK_DATA", "true")`
4. Синхронизируйте проект (Sync Now)
5. Пересоберите приложение

### Для отключения мок-данных:
1. Откройте `app/build.gradle.kts`
2. Найдите строку: `buildConfigField("boolean", "USE_MOCK_DATA", "true")`
3. Измените на: `buildConfigField("boolean", "USE_MOCK_DATA", "false")`
4. Синхронизируйте проект (Sync Now)
5. Пересоберите приложение

## 📊 Преимущества мок-данных

- ✅ **Быстрая загрузка** - нет сетевых запросов
- ✅ **Стабильные данные** - всегда одинаковый результат
- ✅ **Офлайн работа** - не требует интернета
- ✅ **Предсказуемость** - известные данные для тестирования
- ✅ **Отладка** - легко найти проблемы в UI

## ⚠️ Важные замечания

1. **Не забывайте переключаться обратно** на реальный API перед релизом
2. **Мок-данные не обновляются** - они статичны
3. **Избранное работает** с мок-данными через Room
4. **Кэширование отключено** в мок-режиме
5. **Логирование включено** для отладки

## 🔍 Отладка

### Проверить текущий режим:
```kotlin
Log.d("DataModule", "Using mock data: ${BuildConfig.USE_MOCK_DATA}")
```

### Логи мок-данных:
- `MockApodPagingSource` - логирует загрузку страниц
- `MockApodRepositoryImpl` - логирует операции с данными

---

**💡 Совет:** Используйте мок-данные для быстрой разработки UI, но всегда тестируйте финальную версию с реальным API!
