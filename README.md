Приложение находится в разработке
=================================
Для развертывания установить Docker и из консоли вызвать docker-compose up

Приложение висит на порту 8080

Swagger(OpenAPI) доступен по адресу http://localhost:8080/swagger

_______________________________________________________________________

Программное обеспечение biointerface_server предназначено для сбора, хранения, и дальнейшей обработки данных полученных
с контроллера мышенчной активности:

- [ПП контроллера мышенчной активности](https://github.com/itgavrilov/biointerfaceController_pcb),
- [ПО контроллера мышенчной активности](https://github.com/itgavrilov/biointerfaceController_embedded).

Данное ПО является сервером для клиент-серверной версии
приложения [biointerface_standalone](https://github.com/itgavrilov/biointerface_standalone)

В данный момент осталось реализовать стриминговую передачу данных с контроллера и
разработать [клиент](https://github.com/itgavrilov/biointerface_client)

В проекте использован стек:

- Java version 16
- COM-порт: jSerialComm
- Spring Boot MVS + REST API + JSON + Spring Boot Validation
- Mapper DTO: MapStruct
- BD: для runtime PostgreSQL + Spring Data JPA
- Logger: slf4j + log4j
- OpenAPI: Springdoc Openapi-ui
- тесты: Spring Boot Test + junit-vintage
- сахар: lombok
- контейнер: Docker
