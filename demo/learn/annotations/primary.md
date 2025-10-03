# Аннотация @Primary

Аннотация `@Primary` — это еще один способ разрешить конфликт при внедрении зависимостей, когда в контексте Spring существует несколько бинов одного типа. Она указывает, что один из бинов является "основным" или "предпочтительным" и должен быть выбран по умолчанию, если не указано иное.

## Ключевые моменты

*   **Основное назначение:** Задать бин по умолчанию, когда существует несколько кандидатов одного типа.
*   **Глобальный выбор:** `@Primary` влияет на все точки внедрения, где не указан `@Qualifier`.
*   **Переопределение:** Выбор, сделанный с помощью `@Primary`, можно переопределить с помощью `@Qualifier` в конкретной точке внедрения.
*   **Простота:** Упрощает конфигурацию, когда один из бинов является очевидным выбором по умолчанию.

## Проблема: Неоднозначность при внедрении

Как и в случае с `@Qualifier`, рассмотрим интерфейс `MessageService` и две его реализации: `EmailService` и `SmsService`.

```java
public interface MessageService {
    String getMessage();
}

@Component
public class EmailService implements MessageService {
    public String getMessage() {
        return "Sending email...";
    }
}

@Component
public class SmsService implements MessageService {
    public String getMessage() {
        return "Sending SMS...";
    }
}
```

При попытке внедрить `MessageService` с помощью `@Autowired`, Spring выбросит исключение `NoUniqueBeanDefinitionException`, так как не знает, какой из двух бинов выбрать.

## Решение: Использование @Primary

Чтобы решить эту проблему, мы можем пометить один из бинов как основной с помощью аннотации `@Primary`. Этот бин будет автоматически выбран для внедрения во всех точках, где требуется зависимость типа `MessageService`.

```java
@Component
@Primary
public class EmailService implements MessageService {
    public String getMessage() {
        return "Sending email...";
    }
}
```

Теперь, когда мы внедряем `MessageService`, Spring без проблем выберет `EmailService`, так как он помечен как основной.

```java
@Component
public class MessageProcessor {

    @Autowired
    private MessageService messageService; // Будет внедрен EmailService

    public void processMessage() {
        System.out.println(messageService.getMessage()); // Выведет: Sending email...
    }
}
```

## @Primary vs @Qualifier

Обе аннотации решают проблему неоднозначности, но делают это по-разному:

*   **`@Primary`**: Определяет **глобальный** выбор по умолчанию для определенного типа. Это стратегия "один основной". Если в 90% случаев вам нужен `EmailService`, имеет смысл пометить его как `@Primary`.
*   **`@Qualifier`**: Является более **локальным** и точечным решением. Он позволяет выбрать конкретный бин прямо в месте внедрения, **переопределяя выбор по умолчанию**, установленный `@Primary`.

Вы можете использовать обе аннотации вместе. Например, даже если `EmailService` помечен как `@Primary`, вы все равно можете явно запросить `SmsService` в определенном месте с помощью `@Qualifier`.

```java
@Component
public class SmsNotificationManager {

    @Autowired
    @Qualifier("smsService")
    private MessageService messageService; // Будет внедрен SmsService, несмотря на @Primary

    public void processMessage() {
        System.out.println(messageService.getMessage()); // Выведет: Sending SMS...
    }
}
```

### Ссылки
* [Аннотация Spring @Qualifier, @Primary](https://www.baeldung.com/spring-qualifier-annotation)

### Связанные статьи
* [autowired.md](autowired.md)
* [qualifier.md](qualifier.md)
