Приложение находится в разработке
=================================
Для развертывания установить Docker и консоли вызвать docker-compose up
приложение висит на порту 8080
_______________________________________________________________________

Программное обеспечение biointerface_server предназначено для сбора, хранения, 
и дальнейшей обработки данных полученных с контроллера мышенчной активности:
- [ПП контроллера мышенчной активности](https://github.com/itgavrilov/biointerfaceController_pcb),
- [ПО контроллера мышенчной активности](https://github.com/itgavrilov/biointerfaceController_embedded).

Данное ПО является сервером для клиент-серверной версии 
приложения [biointerface_standalone](https://github.com/itgavrilov/biointerface_standalone)

Приложение построено по 3-х слойной архитектуре:
1. REST-контроллеры json-запросов (controller)
2. Сервисы(service) + обработчик хоста COM-порта(host)
3. Репазитории JPA

Слои оперируют сущностями(domain.entity), 
а для отправки на клиент сущности конвертируются в dto(domain.dto)

В данный момент осталось реализовать стриминговую передачу данных с 
контроллера и разработать [клиент](https://github.com/itgavrilov/biointerface_client)

В дальнейшем планируется разбить на микросервисы "Сервисы" и "обработчик хоста COM-порта"

В проекте использован стек:
- Java version 16
- Spring Boot
- MVS: Spring MVS + REST API + JSON + DTO
- JPA: Spring Data JPA + Hibernate
- Logger: slf4j + log4j
- BD: для runtime PostgreSQL
- COM-порт: jSerialComm
- тесты: Spring Boot Test + junit-vintage (в разработке)
- сахар: lombok, spring-boot-devtools
- контейнер: Docker