# План рефакторинга к Clean Architecture

## Этап 1: Создание Domain Layer

### 1.1 Создать структуру папок
```
domain/
├── model/
│   └── Apod.kt                    # Domain модель
├── repository/
│   └── ApodRepository.kt         # Интерфейс репозитория
├── usecase/
│   ├── GetApodListUseCase.kt
│   ├── GetApodDetailUseCase.kt
│   ├── ToggleFavoriteUseCase.kt
│   └── ClearCacheUseCase.kt
└── error/
    └── ApodError.kt              # Domain ошибки
```

### 1.2 Создать Domain модели
```kotlin
// domain/model/Apod.kt
data class Apod(
    val date: String,
    val title: String,
    val explanation: String,
    val url: String,
    val hdUrl: String?,
    val mediaType: String,
    val serviceVersion: String
)
```

### 1.3 Создать интерфейс репозитория
```kotlin
// domain/repository/ApodRepository.kt
interface ApodRepository {
    fun getApodPagingFlow(): Flow<PagingData<Apod>>
    suspend fun getApodByDate(date: String): Result<Apod>
    suspend fun toggleFavorite(apod: Apod)
    suspend fun isFavorite(date: String): Boolean
    fun getFavoritesFlow(): Flow<List<Apod>>
    suspend fun clearCache()
}
```

## Этап 2: Создание Use Cases

### 2.1 GetApodListUseCase
```kotlin
class GetApodListUseCase(
    private val repository: ApodRepository
) {
    operator fun invoke(): Flow<PagingData<Apod>> {
        return repository.getApodPagingFlow()
    }
}
```

### 2.2 ToggleFavoriteUseCase
```kotlin
class ToggleFavoriteUseCase(
    private val repository: ApodRepository
) {
    suspend operator fun invoke(apod: Apod) {
        repository.toggleFavorite(apod)
    }
}
```

## Этап 3: Рефакторинг Data Layer

### 3.1 Переименовать модели
- `ApodItem` → `Apod` (domain)
- `ApodResponse` → `ApodDto` (data)
- `ApodEntity` → `ApodEntity` (database)

### 3.2 Создать мапперы
```kotlin
// data/mapper/ApodMapper.kt
object ApodMapper {
    fun ApodDto.toDomain(): Apod = Apod(...)
    fun ApodEntity.toDomain(): Apod = Apod(...)
    fun Apod.toEntity(): ApodEntity = ApodEntity(...)
}
```

### 3.3 Реализовать интерфейс репозитория
```kotlin
class ApodRepositoryImpl(
    private val apiService: NasaApiService,
    private val apodDao: ApodDao,
    private val networkMonitor: NetworkMonitor
) : ApodRepository {
    // Реализация методов
}
```

## Этап 4: Рефакторинг Presentation Layer

### 4.1 Обновить ViewModel
```kotlin
class ApodViewModel(
    private val getApodListUseCase: GetApodListUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val clearCacheUseCase: ClearCacheUseCase
) : ViewModel() {
    // Использование только Use Cases
}
```

### 4.2 Обновить UI
- Заменить `ApodItem` на `Apod`
- Обновить импорты

## Этап 5: Внедрение Dependency Injection

### 5.1 Добавить Hilt
```kotlin
// build.gradle.kts
implementation("com.google.dagger:hilt-android:2.48")
kapt("com.google.dagger:hilt-compiler:2.48")
```

### 5.2 Создать модули
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideApodRepository(
        apiService: NasaApiService,
        apodDao: ApodDao,
        networkMonitor: NetworkMonitor
    ): ApodRepository = ApodRepositoryImpl(apiService, apodDao, networkMonitor)
}

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {
    @Provides
    fun provideGetApodListUseCase(repository: ApodRepository): GetApodListUseCase {
        return GetApodListUseCase(repository)
    }
}
```

## Этап 6: Тестирование

### 6.1 Unit тесты для Use Cases
```kotlin
class GetApodListUseCaseTest {
    @Test
    fun `should return paging flow from repository`() {
        // Тест
    }
}
```

### 6.2 Unit тесты для Repository
```kotlin
class ApodRepositoryImplTest {
    @Test
    fun `should return cached data when offline`() {
        // Тест
    }
}
```

## Приоритеты выполнения

1. **Высокий приоритет:**
   - Создание Domain Layer
   - Разделение моделей
   - Создание интерфейсов

2. **Средний приоритет:**
   - Создание Use Cases
   - Рефакторинг ViewModel
   - Добавление мапперов

3. **Низкий приоритет:**
   - Внедрение DI
   - Добавление тестов
   - Оптимизация производительности

## Ожидаемые результаты

После рефакторинга проект будет соответствовать Clean Architecture:
- ✅ Четкое разделение слоев
- ✅ Соблюдение Dependency Rule
- ✅ Высокая тестируемость
- ✅ Легкость расширения
- ✅ Независимость от фреймворков
