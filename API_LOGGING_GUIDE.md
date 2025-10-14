# 📊 API Logging Configuration

## 🔧 Настройка логирования HTTP запросов

В проекте настроено стандартное логирование всех HTTP запросов к NASA API с помощью `HttpLoggingInterceptor`.

### 📋 Компоненты логирования:

**`HttpLoggingInterceptor`** - Стандартный OkHttp interceptor
- Логирует тело запроса и ответа (JSON)
- Показывает детали HTTP протокола
- Уровень логирования: `BODY` (полная информация)

### 🎯 Теги для фильтрации логов:

- `OkHttp` - HTTP запросы и ответы (тело)
- `ApiClient` - Инициализация API клиента
- `ApodPagingSource` - Логи загрузки данных

### 📱 Как просматривать логи в Android Studio:

1. **Откройте Logcat:**
   - `View` → `Tool Windows` → `Logcat`
   - Или нажмите `Alt + 6`

2. **Настройте фильтр:**
   - В поле `Filter` введите: `OkHttp|ApiClient|ApodPagingSource`
   - Или по отдельности: `OkHttp`, `ApiClient`, `ApodPagingSource`
   - Выберите ваш девайс/эмулятор
   - Выберите ваше приложение

3. **Запустите приложение** и смотрите логи в реальном времени

### 🔄 Пример логов:

```
D/ApiClient: ApiClient initialized with base URL: https://api.nasa.gov/
D/ApodPagingSource: Loading page 0 with size 4
D/ApodPagingSource: Found 0 cached items
D/ApodPagingSource: Is online: true
D/ApodPagingSource: Making API request to NASA...
D/ApodPagingSource: API response code: 200
D/OkHttp: --> GET https://api.nasa.gov/planetary/apod?api_key=DEMO_KEY&count=4
D/OkHttp: --> END GET
D/OkHttp: <-- 200 OK https://api.nasa.gov/planetary/apod?api_key=DEMO_KEY&count=4 (1250ms)
D/OkHttp: Content-Type: application/json
D/OkHttp: [
D/OkHttp:   {
D/OkHttp:     "date": "2024-01-15",
D/OkHttp:     "title": "Astronomy Picture of the Day",
D/OkHttp:     "explanation": "...",
D/OkHttp:     "url": "...",
D/OkHttp:     "media_type": "image"
D/OkHttp:   }
D/OkHttp: ]
D/OkHttp: <-- END HTTP
```

### ⚙️ Управление логированием:

В `ApiClient.kt` можно изменить уровень логирования:
- `HttpLoggingInterceptor.Level.BODY` - Полная информация
- `HttpLoggingInterceptor.Level.HEADERS` - Только заголовки
- `HttpLoggingInterceptor.Level.BASIC` - Базовая информация
- `HttpLoggingInterceptor.Level.NONE` - Отключить логирование

### 🚀 Преимущества:

- ✅ Простота настройки
- ✅ Стандартное решение от OkHttp
- ✅ Полная видимость API запросов
- ✅ Отладка проблем с сетью
- ✅ Минимальный код
