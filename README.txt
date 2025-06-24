README - Instrucciones para probar UserAPI
------------------------------------------

1. Requisitos previos:
   - Java 17 instalado
   - Maven instalado

2. Compilar el proyecto:
   mvn clean install

3. Ejecutar la aplicación:
   mvn spring-boot:run

4. Acceder a Swagger UI para probar el endpoint de registro:
   http://localhost:8080/swagger-ui.html

5. Realizar una solicitud POST al endpoint /api/register con el siguiente ejemplo:

   URL: http://localhost:8080/api/register
   Método: POST
   Headers: Content-Type: application/json
   Body (raw JSON):
   {
     "name": "Juan Rodriguez",
     "email": "juan@rodriguez.org",
     "password": "Hunter2024#",
     "phones": [
       {
         "number": "1234567",
         "citycode": "1",
         "contrycode": "57"
       }
     ]
   }

6. Si el correo ya está registrado o el formato es incorrecto, recibirás un JSON de error:
   {
     "mensaje": "Texto descriptivo del error"
   }

7. Ejecutar pruebas unitarias:
   mvn test

------------------------------------------
Desarrollado por Héctor Miguel Vergara Canevaro
