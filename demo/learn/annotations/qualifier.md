# Аннотация @Qualifier

Аннотация `@Qualifier` используется в Spring для разрешения неоднозначностей при внедрении зависимостей. Она применяется совместно с `@Autowired`, когда в контексте Spring существует несколько бинов одного и того же типа. `@Qualifier` позволяет указать, какой именно бин должен быть внедрен.

## Ключевые моменты

*   **Основное назначение:** Устранение неоднозначности, когда для одного типа зависимости существует несколько кандидатов на внедрение.
*   **Использование с `@Autowired`:** `@Qualifier` является дополнением к `@Autowired`.
*   **Идентификация бина:** В качестве аргумента `@Qualifier` принимает строку, которая соответствует имени (ID) целевого бина.
*   **Гибкость:** Позволяет точно контролировать, какой именно компонент будет внедрен, делая конфигурацию более явной и предсказуемой.

## Проблема: Неоднозначность при внедрении

Предположим, у нас есть интерфейс `MessageService` и две его реализации: `EmailService` и `SmsService`.

```java
public interface MessageService {
    String getMessage();
}

@Component("emailService")
public class EmailService implements MessageService {
    public String getMessage() {
        return "Sending email...";
    }
}

@Component("smsService")
public class SmsService implements MessageService {
    public String getMessage() {
        return "Sending SMS...";
    }
}
```

Если мы попытаемся внедрить `MessageService` с помощью `@Autowired` без уточнений, Spring не сможет определить, какой из двух бинов (`emailService` или `smsService`) использовать, и выбросит исключение `NoUniqueBeanDefinitionException`.

```java
@Component
public class MessageProcessor {

    @Autowired
    private MessageService messageService; // Ошибка: найдено 2 бина типа MessageService

    // ...
}
```

## Решение: Использование @Qualifier

`@Qualifier` позволяет указать имя (или "квалификатор") нужного бина. По умолчанию, имя бина совпадает с именем класса в camelCase (например, `emailService`) или значением, указанным в аннотации `@Component` (например, `@Component("emailService")`).

### Внедрение через поле с @Qualifier - **не рекомендуется**

```java
@Component
public class MessageProcessor {

    @Autowired
    @Qualifier("emailService")
    private MessageService messageService;

    public void processMessage() {
        System.out.println(messageService.getMessage()); // Выведет: Sending email...
    }
}
```

### Внедрение через конструктор с @Qualifier

`@Qualifier` можно применять непосредственно к параметрам конструктора.

```java
@Component
public class MessageProcessor {

    private final MessageService messageService;

    @Autowired
    public MessageProcessor(@Qualifier("smsService") MessageService messageService) {
        this.messageService = messageService;
    }

    public void processMessage() {
        System.out.println(messageService.getMessage()); // Выведет: Sending SMS...
    }
}
```

### Внедрение через сеттер с @Qualifier

Аналогично, аннотацию можно использовать с параметром сеттер-метода.

```java
@Component
public class MessageProcessor {

    private MessageService messageService;

    @Autowired
    public void setMessageService(@Qualifier("emailService") MessageService messageService) {
        this.messageService = messageService;
    }
    // ...
}
```
### Ссылки
* [Аннотация Spring @Qualifier, @Primary](https://www.baeldung.com/spring-qualifier-annotation)

### Связанные статьи
* [autowired.md](autowired.md)
* [primary.md](primary.md)
