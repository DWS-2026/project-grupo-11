# FootLeague

## 👥 Miembros del Equipo
| Nombre y Apellidos | Correo URJC | Usuario GitHub |
|:--- |:--- |:--- |
| Jaime Tejero Fernández | j.tejero.2024@alumnos.urjc.es | Jaime2006cib |
| Rubén Sánchez López | r.sanchezl.2024@alumnos.urjc.es | ruben2393 |
| Sergio Sánchez López | s.sanchezl.2024@alumnos.urjc.es | xxSerbotXX04 |

---

## 🎭 **Preparación: Definición del Proyecto**

### **Descripción del Tema**
La aplicación consiste en una plataforma web para la gestión y visualización de resultados de partidos de fútbol dentro de una liga.

El sistema permite consultar información detallada de equipos, partidos y eventos ocurridos durante los encuentros. Cada partido incluye datos como fecha, equipos participantes, resultado final, estadio y un registro cronológico de eventos (goles, tarjetas y sustituciones).

Los usuarios pueden registrarse e iniciar sesión para seleccionar su equipo favorito y participar en la plataforma mediante valoraciones de los eventos del partido. Estas valoraciones permiten puntuar y comentar el rendimiento de los jugadores en acciones concretas del juego.

Además, la aplicación incluye:

Visualización de calendario de partidos
Detalles completos de cada partido (alineaciones, eventos, resultado)
Clasificación de la liga con estadísticas (puntos, victorias, empates, derrotas, diferencia de goles)
Registro de eventos enriquecidos (incluyendo sustituciones con jugador que entra y sale)

El objetivo es proporcionar una herramienta interactiva para el seguimiento de competiciones deportivas, dirigida a aficionados y usuarios interesados en el análisis de partidos.

### **Entidades**
Indicar las entidades principales que gestionará la aplicación y las relaciones entre ellas:

1. **[Entidad 1]**  Usuario (Periodista): Almacena la información del perfil del usuario (email, contraseña) y el equipo favorito sobre el que ejerce como periodista, pudiendo valorar la actuación de los jugadores en los partidos de dicho equipo.
2. **[Entidad 2]**: Equipo: Información del club (Nombre, Escudo, Estadio) y sus estadísticas acumuladas (puntos, goles).
3. **[Entidad 3]**: Partido: El evento central que une a dos equipos, con fecha, marcador final y estadio.
4. **[Entidad 4]**: Evento de Partido: Registro de cada hito (Gol, Tarjeta Roja/Amarilla, Cambio) vinculado a un minuto y a un jugador (Nombre + Dorsal).
5. **[Entidad 5]**: Valoración: Almacena los comentarios y puntuaciones que los usuarios registrados otorgan a los eventos de un partido. Incluye el texto de la opinión y la calificación numérica.

**Relaciones entre entidades:**
- Usuario - Equipo (N:1): Muchos usuarios pueden tener el mismo "Equipo Favorito".
- Partido - Evento (1:N): Un solo partido puede generar múltiples eventos (ej: 3 goles y 2 tarjetas).
- Equipo - Partido (1:N): Un equipo participa en muchos partidos a lo largo del torneo (ya sea como local o visitante).
- Usuario - Valoración (1:N): Un usuario puede escribir muchas valoraciones, pero cada valoración pertenece a un único autor.
- Evento - Valoración (1:N): Un evento puede recibir muchas valoraciones de distintos usuarios.

### **Permisos de los Usuarios**
Describir los permisos de cada tipo de usuario e indicar de qué entidades es dueño:

* **Usuario Anónimo**: 
  - Permisos:
      - Visualizar resultados, partidos y clasificaciones
      - Consultar equipos, y estadísticas
      - Acceder al panel de novedades
      - Registrarse en la aplicación
  - No es dueño de ninguna entidad

* **Usuario Registrado**: 
  - Permisos:
      - Todas las funcionalidades del usuario anónimo
      - Gestión de su perfil y selección de equipo favorito
      - Valorar eventos de partidos
  - Es dueño de:
      - Su perfil de usuario
      - Sus valoraciones

* **Administrador**: 
  - Permisos:
      - Gestión completa (CRUD) de equipos
      - Creación y edición de partidos y eventos
      - Gestión de usuarios
  - Es dueño de:
      - Todas las entidades del sistema

### **Imágenes**
Indicar qué entidades tendrán asociadas una o varias imágenes:

- **[Entidad 2]**: Equipo : Una imagen para el Escudo o Estandarte oficial del club, que se mostrará en la clasificación y en las fichas de partido.
- **[Entidad 1]**: Usuario: Una imagen de Avatar para el perfil del usuario, que aparecerá junto a sus valoraciones de los partidos.

---

## 🛠 **Práctica 1: Maquetación de páginas con HTML y CSS**

### **Vídeo de Demostración**
📹 **[Enlace al vídeo en YouTube](https://youtu.be/y07HqKPgQR0)**
> Vídeo mostrando las principales funcionalidades de la aplicación web.

### **Diagrama de Navegación**
Diagrama que muestra cómo se navega entre las diferentes páginas de la aplicación:

![Diagrama de Navegación](images/navigationDiagram.png)

> El flujo de navegación se divide en tres niveles de permisos. El Usuario Anónimo accede a la información pública como resultados (match-list), estadísticas (classification) y el registro. El Usuario Registrado dispone de un área privada (profile) para gestionar su cuenta y realizar valoraciones. Por último, el Administrador tiene acceso exclusivo a los paneles de gestión global (Admin_Page), permitiéndole el control total (CRUD) sobre las entidades de equipos, partidos y cuentas de usuario.

### **Capturas de Pantalla y Descripción de Páginas**

#### **1. Página Principal / Home**
![Página Principal](images/index.jpg)

> [Puerta de entrada principal con el listado de partidos, clasificación y acceso global a la navegación.]
#### **2. Página de login**
![Página de login](images/login.jpg)

> [Formulario de acceso para usuarios registrados (periodistas) y administradores.]
#### **3. Página de registro**
![Página de registro](images/registration.jpg)

> [Formulario de registro para nuevos periodistas deportivos.]
#### **4. Página de perfil**
![Página de perfil](images/profile.jpg)

> [Panel personal del usuario donde se visualizan sus datos y valoraciones, además de mostrar un panel para acceder a la página de administrador.]
#### **5. Página de editar perfil**
![Página de editar perfil](images/edit-profile.jpg)

> [Interfaz para que el usuario modifique su información y preferencias.]
#### **6. Página de todas las valoraciones**
![Página de todas las valoraciones](images/my-ratings.jpg)

> [Listado histórico de todas las valoraciones realizadas por el periodista.]
#### **7. Página principal de administrador**
![Página principal de administrador](images/Admin_Page.jpg)

> [Panel central con tarjetas de navegación hacia las distintas áreas de gestión.]
#### **8. Página de administación de cuentas**
![Página de administación de cuentas](images/Account_Management_Screen.jpg)

> [Interfaz donde el administrador puede crear usuarios y ver el listado de usuarios pudiendo editarlos o eliminarlos.]
#### **9. Página de administación de partidos**
![Página de administación de partidos](images/Match_Management_Screen.jpg)

> [Interfaz para que el administrador cree o modifique partidos.]
#### **10. Página de administación de equipos**
![Página de administación de equipos](images/Team_Management_Screen.jpg)

> [Gestión integral de los equipos inscritos en la liga.]
#### **11. Página de cración de partidos**
![Página de cración de partido](images/CreateMatch.jpg)

> [Interfaz donde el administrador crea los partidos de la liga.]
#### **12. Página de cración de equipos**
![Página de cración de equipos](images/CreateTeam.jpg)

> [Interfaz donde el administrador crea los equipos de la liga.]
#### **13. Página para editar partidos**
![Página para editar partidos](images/EditMatchDetails.jpg)

> [Interfaz donde el administrador puede editar los partidos de la liga.]
#### **14. Página para editar equipos**
![Página para editar equipos](images/EditTeamBarcelona.jpg)

> [Interfaz donde el administrador puede editar los equipos de la liga.]
#### **15. Página del listado de usuarios**
![Página del listado de usuarios](images/ModifyAccount.jpg)

> [Interfaz donde se muestra un listado de usuarios registrados pudiendo editarlos o eliminarlos.]
#### **16. Página del listado de partidos**
![Página del listado de partidos](images/ModifyMatch.jpg)

> [Interfaz donde se muestra el listado de partidos de la liga pudiendo editarlos o eliminarlos.]
#### **17. Página del listado de equipos**
![Página del listado de equipos](images/ModifyTeam.jpg)

> [Interfaz donde se muestra el listado de equipos de la liga pudiendo editarlos o eliminarlos.]
#### **18. Página de la clasificación de la liga**
![Página de la clasificación de la liga](images/classification.jpg)

> [Interfaz donde se muestra la clasificación de la liga con todos sus detalles.]
#### **19. Página del acta del partido**
![Página del acta del partido](images/match-details.jpg)

> [Interfaz donde se muestra el acta del partido con todos los eventos ocurridos en él.]
#### **20. Página del calendario de la liga**
![Página del calendario de la liga](images/match-list.jpg)

> [Interfaz donde se muestra el calendario de todos los partidos de la liga.]
#### **21. Página para valorar un evento**
![Página para valorar un evento](images/rating-player.jpg)

> [Interfaz donde se puede realizar una valoración sobre un evento de un partido determinado.]
### **Participación de Miembros en la Práctica 1**

#### **Alumno 1 - Jaime Tejero Fernández**

El alumno Jaime Tejero será encargado de la parte de administrador de la página web, esto incluye creación y modificación de equipos, evento y usuarios

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Creación inicio de Pagina De opciones de Administrador provisional para crear un esqueleto sobre el que trabajar](https://github.com/DWS-2026/project-grupo-11/commit/7baa33387f82ab0e3b71a1e8c4ef68cd4637cbac)  | [Pagina_Admin.html](https://github.com/DWS-2026/project-grupo-11/blob/7baa33387f82ab0e3b71a1e8c4ef68cd4637cbac/HTML/Pagina_Admin.html)   |
|2| [Añadida navegación con SweetAlert debido a conflictos con el CSS, Creacion del CSS comun para la pagina y finalizacion de la pagina panel de administrador añadiendo las opciones necesarias](https://github.com/DWS-2026/project-grupo-11/commit/9897a534d0fa390c21d2f0a6817b2be7ef25349c)  | [Pagina_Admin.html](https://github.com/DWS-2026/project-grupo-11/blob/9897a534d0fa390c21d2f0a6817b2be7ef25349c/HTML/Pagina_Admin.html), [style.css](https://github.com/DWS-2026/project-grupo-11/blob/9897a534d0fa390c21d2f0a6817b2be7ef25349c/css/styles.css)   |
|3| [Añadido la pantalla de Gestion de Cuentas y Partidos con base de la Pantalla Gestion Equipos, eliminacion temporal de alertas por errores con el CSS](https://github.com/DWS-2026/project-grupo-11/commit/e96b80c586507a322a48c98378f632a2b91d29e5)  | [Pantalla_Gestion_Partidos.html](https://github.com/DWS-2026/project-grupo-11/blob/e96b80c586507a322a48c98378f632a2b91d29e5/HTML/Pantalla_Gestion_Partidos.html), [Pantalla_Gestion_Cuentas.html](https://github.com/DWS-2026/project-grupo-11/blob/e96b80c586507a322a48c98378f632a2b91d29e5/HTML/Pantalla_Gestion_Cuentas.html)  |
|4| [Solucion Match_Management_Screen.html mal estructuradamain](https://github.com/DWS-2026/project-grupo-11/commit/fb30af35ca314d777db7023843fa7a652160ffd2)  | [Match_Management_Screen.html](https://github.com/DWS-2026/project-grupo-11/blob/fb30af35ca314d777db7023843fa7a652160ffd2/HTML/Match_Management_Screen.html)   |
|5| [Actualizadas funcionalidades buscar equipo, regresar a pantalla de inicio (cambiada por regreso a perfil), buscar usuario, eliminar usuario, modificar usuario y modificar equipo junto a pagina FC Barcelona](https://github.com/DWS-2026/project-grupo-11/commit/492f5384391b20ea554a4fc9abaff5730bc03085)  | [Admin_Page.html](https://github.com/DWS-2026/project-grupo-11/blob/492f5384391b20ea554a4fc9abaff5730bc03085/HTML/Admin_Page.html), [CreateAccount.html](https://github.com/DWS-2026/project-grupo-11/blob/492f5384391b20ea554a4fc9abaff5730bc03085/HTML/CreateAccount.html), [DeleteAccount.html](https://github.com/DWS-2026/project-grupo-11/blob/492f5384391b20ea554a4fc9abaff5730bc03085/HTML/DeleteAccount.html), [EditTeamBarcelona.html](https://github.com/DWS-2026/project-grupo-11/blob/492f5384391b20ea554a4fc9abaff5730bc03085/HTML/EditTeamBarcelona.html), [ModifyAccount.html](https://github.com/DWS-2026/project-grupo-11/blob/492f5384391b20ea554a4fc9abaff5730bc03085/HTML/ModifyAccount.html),  [ModifyTeam.html](https://github.com/DWS-2026/project-grupo-11/blob/492f5384391b20ea554a4fc9abaff5730bc03085/HTML/ModifyTeam.html) |
|6| [Actualizadas funcionalidades buscar usuario](https://github.com/DWS-2026/project-grupo-11/commit/44345cc115a1ac1ed7fef186b4767bc200ac04b1)  | [ModifyAccount.html](https://github.com/DWS-2026/project-grupo-11/commit/44345cc115a1ac1ed7fef186b4767bc200ac04b1/HTML/ModifyAccount.html)   |
|7| [Refactor: Sustituidos buscadores manuales por tablas de gestión dinámica en Usuarios y Equipos. Unificadas funciones de Modificar/Eliminar y añadido creacion de partidos y  registro de eventos (goles/tarjetas) en la creación de partidos](https://github.com/DWS-2026/project-grupo-11/commit/fb31e50506eb62d40e2b31902ef0bdde42d431ee#diff-64779b39e77a7af020f0fa5f8b836f1663c171b04808a488f5db96547c2ccad1)  | [](*)   |
|8| [Actualizacion resultado dinamico en createMatch.html](https://github.com/DWS-2026/project-grupo-11/commit/c06134949427e37424c7cf0c0e742c380bae92a1#diff-bc4ad04f795105d8dc2153eb5aa19fa12b892a9b057f70d045496e85e8741ff5)  | [CreateMatch.html](https://github.com/DWS-2026/project-grupo-11/commit/c06134949427e37424c7cf0c0e742c380bae92a1#diff-bc4ad04f795105d8dc2153eb5aa19fa12b892a9b057f70d045496e85e8741ff5/HTML/CreateMatch.html)   |
|9| [Finalizacion de la interfaz crear partidos y gestionar partidos](https://github.com/DWS-2026/project-grupo-11/commit/a1e9bcdbbf9bc94166b47ee5aca65902c6c9fe1d)  | [HTML/ModifyMatch.html](https://github.com/DWS-2026/project-grupo-11/commit/a1e9bcdbbf9bc94166b47ee5aca65902c6c9fe1d/HTML/ModifyMatch.html), *   |



---

#### **Alumno 2 - Rubén Sánchez López**

Mi aportación se centró en desarrollar la interfaz pública y la experiencia del usuario, implementando la visualización dinámica de partidos, los eventos en tiempo real y el sistema de valoraciones.

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Maquetación inicial de detalle de partido y eventos.](https://github.com/DWS-2026/project-grupo-11/commit/7eb85548ad3438347230cdfd8fb8433d675e794f)  | [partido-detalle.html](https://github.com/DWS-2026/project-grupo-11/commit/7eb85548ad3438347230cdfd8fb8433d675e794f#diff-b1f380592c7f5433ea91292b2726f95cce071aaf17589c48ec4f3f5c2147ef7f)   |
|2| [Creada la home con lista de partidos y cards](https://github.com/DWS-2026/project-grupo-11/commit/6d310db6cb3baf4e0c625b1688dff5f18b9af659)  | [index.html](https://github.com/DWS-2026/project-grupo-11/commit/6d310db6cb3baf4e0c625b1688dff5f18b9af659#diff-c9caeff2e64b497a71a0cc0d2ba6f659ff3d653b2913129a6a4ee33d3cf8f868)   |
|3| [Cambio de Liga Proyect a Liga Pro](https://github.com/DWS-2026/project-grupo-11/commit/8801129947b786fdf121545573446750e4631d0d)  | [player-ratings.html](https://github.com/DWS-2026/project-grupo-11/commit/8801129947b786fdf121545573446750e4631d0d#diff-e7cdad96aad0e5ff97fb92912a5800e7df8a6ea761dd181f97ec6a4f2b7b2a63)   |
|4| [Add my-ratings view to show user feedback history](https://github.com/DWS-2026/project-grupo-11/commit/f5e299fa37ff787d713e53d8fe88c1f12f62b398)  | [my-ratings.html](https://github.com/DWS-2026/project-grupo-11/commit/f5e299fa37ff787d713e53d8fe88c1f12f62b398#diff-46aa992c5047cba950f8a2215b7ea5ec32e8bc4115cbfb9ea782d218bfed6eac)   |
|5| [Refactor UI y navegación: Clasificación, Listado de Partidos y Valoración de Eventos](https://github.com/DWS-2026/project-grupo-11/commit/fc36d3b38e1c8ddc8172cfd24e1944bd866ed056#diff-9e4be119b5d8791da9edf9d513e3586795b3ece00680fa31d46c8860381738e6)  | [classification.html](https://github.com/DWS-2026/project-grupo-11/commit/fc36d3b38e1c8ddc8172cfd24e1944bd866ed056#diff-9e4be119b5d8791da9edf9d513e3586795b3ece00680fa31d46c8860381738e6), [index.html](https://github.com/DWS-2026/project-grupo-11/commit/fc36d3b38e1c8ddc8172cfd24e1944bd866ed056#diff-c9caeff2e64b497a71a0cc0d2ba6f659ff3d653b2913129a6a4ee33d3cf8f868), [match-list.html](https://github.com/DWS-2026/project-grupo-11/commit/fc36d3b38e1c8ddc8172cfd24e1944bd866ed056#diff-32197a9a681d4b0cff0f309c671bb8481f43fd908d1bb1d3516d01086f56196c), [my-ratings.html](https://github.com/DWS-2026/project-grupo-11/commit/fc36d3b38e1c8ddc8172cfd24e1944bd866ed056#diff-46aa992c5047cba950f8a2215b7ea5ec32e8bc4115cbfb9ea782d218bfed6eac), [player-ratings.html](https://github.com/DWS-2026/project-grupo-11/commit/fc36d3b38e1c8ddc8172cfd24e1944bd866ed056#diff-e7cdad96aad0e5ff97fb92912a5800e7df8a6ea761dd181f97ec6a4f2b7b2a63)   |
|6| [UI: Split matches into columns and added dates to upcoming games](https://github.com/DWS-2026/project-grupo-11/commit/d11b56ea9a22b5ae0736d888bcb072ccbac8933d)  | [index.html](https://github.com/DWS-2026/project-grupo-11/commit/d11b56ea9a22b5ae0736d888bcb072ccbac8933d#diff-c9caeff2e64b497a71a0cc0d2ba6f659ff3d653b2913129a6a4ee33d3cf8f868), [match-details.html](https://github.com/DWS-2026/project-grupo-11/commit/d11b56ea9a22b5ae0736d888bcb072ccbac8933d#diff-d4673f6089deb4a16f4530275aed71cbe57b2218b427477b27d98e18885b3911)   |
|7| [Switched ratings from Players to Match Events (Goal, Cards, Subs)](https://github.com/DWS-2026/project-grupo-11/commit/f368aa4ea730d21621dd0aa9c279e68113e814b1)  | [match-details.html](https://github.com/DWS-2026/project-grupo-11/commit/f368aa4ea730d21621dd0aa9c279e68113e814b1#diff-d4673f6089deb4a16f4530275aed71cbe57b2218b427477b27d98e18885b3911), [my-ratings.html](https://github.com/DWS-2026/project-grupo-11/commit/f368aa4ea730d21621dd0aa9c279e68113e814b1#diff-46aa992c5047cba950f8a2215b7ea5ec32e8bc4115cbfb9ea782d218bfed6eac), [player-ratings.html](https://github.com/DWS-2026/project-grupo-11/commit/f368aa4ea730d21621dd0aa9c279e68113e814b1#diff-e7cdad96aad0e5ff97fb92912a5800e7df8a6ea761dd181f97ec6a4f2b7b2a63)   |

---

#### **Alumno 3 - Sergio Sánchez López**

Me encargué de la autenticación de usuarios (login y registro), del diseño y maquetación del perfil de usuario, y de los formularios asociados, incluyendo la edición de perfil, selección de equipo favorito y uso de campos HTML bien tipados.

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Añadir diseño de la página de inicio de sesión con Bootstrap](https://github.com/DWS-2026/project-grupo-11/commit/e18183fbcd337c4c39c5324c52f2668e9f359187)  | [login.html](https://github.com/DWS-2026/project-grupo-11/commit/e18183fbcd337c4c39c5324c52f2668e9f359187#diff-f6181bdad2f9eb4c11b593518b97d18ed818e3c25f9e249236758c1e47a2d201)   |
|2| [Crear formulario de registro de usuario con tipos de entrada adecuados](https://github.com/DWS-2026/project-grupo-11/commit/e18183fbcd337c4c39c5324c52f2668e9f359187)  | [registration.html](https://github.com/DWS-2026/project-grupo-11/commit/e18183fbcd337c4c39c5324c52f2668e9f359187#diff-678b41f554fc522ea9b2667684636912e6da63d8c6a60ae0f56a84f38b80b578)   |
|3| [Implementar el diseño de la página de perfil de usuario](https://github.com/DWS-2026/project-grupo-11/commit/af5881ff9d0d928482aebbd6633f63f27d2c746b)  | [profile.html](https://github.com/DWS-2026/project-grupo-11/commit/af5881ff9d0d928482aebbd6633f63f27d2c746b#diff-94c8caeb27cc8da815d3b8daeeb0a1bc9628cce47ea1db8e17ecb817535845a1)   |
|4| [Añadir formulario de edición de perfil y selección de equipo favorito](https://github.com/DWS-2026/project-grupo-11/commit/72ae6eb4f541356c113e9dc314d66011ae6cd737)  | [edit-profile.html](https://github.com/DWS-2026/project-grupo-11/commit/72ae6eb4f541356c113e9dc314d66011ae6cd737#diff-b314f77d096679a612f85f27d64ef1d521dfa51e1d410d5d4e7c520e5b9739da)   |
|5| [Añadido panel de administración en la página de perfil de usuario](https://github.com/DWS-2026/project-grupo-11/commit/65252c0c123d43ad9fd2d297b14b1df510073d28)  | [profile.html](github.com/DWS-2026/project-grupo-11/commit/65252c0c123d43ad9fd2d297b14b1df510073d28#diff-94c8caeb27cc8da815d3b8daeeb0a1bc9628cce47ea1db8e17ecb817535845a1)   |
|6| [Mejorados todos los pequeños detalles de la página del login](https://github.com/DWS-2026/project-grupo-11/commit/e587ba3c9ebf57b68ff796de5aa2efa041aa495a)  |  [login.html](https://github.com/DWS-2026/project-grupo-11/commit/e587ba3c9ebf57b68ff796de5aa2efa041aa495a#diff-f6181bdad2f9eb4c11b593518b97d18ed818e3c25f9e249236758c1e47a2d201)  |
|7| [Mejorados todos los pequeños detalles de la página de registro](https://github.com/DWS-2026/project-grupo-11/commit/e587ba3c9ebf57b68ff796de5aa2efa041aa495a)  |  [registration.html](https://github.com/DWS-2026/project-grupo-11/commit/e587ba3c9ebf57b68ff796de5aa2efa041aa495a#diff-678b41f554fc522ea9b2667684636912e6da63d8c6a60ae0f56a84f38b80b578)

---

#### **Alumno 4 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

## 🛠 **Práctica 2: Web con HTML generado en servidor**

### **Vídeo de Demostración**
📹 **[Enlace al vídeo en YouTube](https://www.youtube.com/watch?v=x91MPoITQ3I)**
> Vídeo mostrando las principales funcionalidades de la aplicación web.

### **Navegación y Capturas de Pantalla**

#### **Diagrama de Navegación**

Solo si ha cambiado.

#### **Capturas de Pantalla Actualizadas**

Solo si han cambiado.

### **Instrucciones de Ejecución**

#### **Requisitos Previos**
- **Java**: versión 21 o superior
- **Maven**: versión 3.8 o superior
- **MySQL**: versión 8.0 o superior
- **Git**: para clonar el repositorio

#### **Pasos para ejecutar la aplicación**

1. **Clonar el repositorio**
   ```bash
   git https://github.com/CodeURJC-DAW-2025-26/project-grupo-11.git
   cd project-grupo-11.git
   ```

2. **Configurar la Base de Datos (MySQL)**
   Abre tu terminal de MySQL o MySQL Workbench.
   Crea la base de datos necesaria para el proyecto:
   ```SQL
   CREATE DATABASE footleague
   ```
   Si has cambiado el usuario o la contraseña de tu MySQL local, abre el archivo src/main/resources/application.properties y actualiza estas         líneas:
   ```Properties
   spring.datasource.url=jdbc:mysql://localhost:3306/footleague_db
   spring.datasource.username=tu_usuario
   spring.datasource.password=tu_contraseña
   ```
3. **Compilar e instalar las dependencias**
4. **Ejecutar la aplicación desde el IDE**
   Localiza la clase principal del proyecto: src/main/java/es/footleague/app/FootballApplication.java.
   Haz clic derecho sobre el archivo o busca el icono de "Run" (un triángulo verde ▶️) que aparece junto a la definición de la clase: public        class FootballApplication.
   Selecciona "Run 'FootballApplication'".
   La consola integrada del IDE mostrará el log de Spring Boot indicando que el servidor se ha levantado correctamente.
5. **Acceder a la web**
   Una vez que en la consola aparezca el mensaje Started ... in X seconds, abre tu navegador y entra en:
   https://localhost:8443

#### **Credenciales de prueba**
- **Usuario Admin**: usuario: `admin`, contraseña: `admin123`
- **Usuario Registrado**: usuario: `JuanPerez`, contraseña: `password123`

### **Diagrama de Entidades de Base de Datos**

Diagrama mostrando las entidades, sus campos y relaciones:

![Diagrama Entidad-Relación](images/database-diagram.png)

> [Descripción opcional: Ej: "El diagrama muestra las 4 entidades principales: Usuario, Producto, Pedido y Categoría, con sus respectivos atributos y relaciones 1:N y N:M."]

### **Diagrama de Clases y Templates**

Diagrama de clases de la aplicación con diferenciación por colores o secciones:

![Diagrama de Clases](images/DiagramaClasesTemplates.png)

> [El diagrama muestra la arquitectura de la aplicación FootLeague. La idea de usar colores es básicamente para ver rápido quién hace qué. Las vistas (morado) hablan con los controladores (verde), que son los que organizan toda la lógica del negocio usando los servicios (rojo). Esos servicios, a su vez, tiran de los datos de las entidades (gris) a través de los repositorios (azul). Todo esto funciona dentro de un entorno protegido por Spring Security.]

### **Participación de Miembros en la Práctica 2**

#### **Alumno 1 - Jaime Tejero Fernández**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Cambios grandes en la creacion de equipos del /admin](https://github.com/DWS-2026/project-grupo-11/commit/c355cb36aa5f98d424f967d3bd6f5e355dbc17fc)  | [](src/main/java/es/footleague/app/controller/RatingController.java), [](src/main/java/es/footleague/app/controller/TeamRestController.java), [](src/main/java/es/footleague/app/controller/ViewController.java),  [](src/main/java/es/footleague/app/model/Team.java), [](src/main/java/es/footleague/app/services/TeamService.java), [](src/main/resources/aplication.properties.java), [](src/main/resources/static/js/Team_Management.js), [](src/main/resources/static/create_team.js), [](src/main/resources/static/js/match_logic.js), [](src/main/resources/templates/CreateTeam.html), [](src/main/resources/templates/EditTeam.html), [](src/main/resources/templates/ModifyTeam.html), [](src/main/resources/templates/Team_Mangement_Screen.html),|
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 2 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 3 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 4 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

## 🛠 **Práctica 3: Incorporación de una API REST a la aplicación web, análisis de vulnerabilidades y contramedidas**

### **Vídeo de Demostración**
📹 **[Enlace al vídeo en YouTube](https://www.youtube.com/watch?v=x91MPoITQ3I)**
> Vídeo mostrando las principales funcionalidades de la aplicación web.

### **Documentación de la API REST**

#### **Especificación OpenAPI**
📄 **[Especificación OpenAPI (YAML)](/api-docs/api-docs.yaml)**

#### **Documentación HTML**
📖 **[Documentación API REST (HTML)](https://raw.githack.com/[usuario]/[repositorio]/main/api-docs/api-docs.html)**

> La documentación de la API REST se encuentra en la carpeta `/api-docs` del repositorio. Se ha generado automáticamente con SpringDoc a partir de las anotaciones en el código Java.

### **Diagrama de Clases y Templates Actualizado**

Diagrama actualizado incluyendo los @RestController y su relación con los @Service compartidos:

![Diagrama de Clases Actualizado](images/complete-classes-diagram.png)

#### **Credenciales de Usuarios de Ejemplo**

| Rol | Usuario | Contraseña |
|:---|:---|:---|
| Administrador | admin | admin123 |
| Usuario Registrado | user1 | user123 |
| Usuario Registrado | user2 | user123 |

### **Participación de Miembros en la Práctica 3**

#### **Alumno 1 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 2 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 3 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 4 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |
