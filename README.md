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
La aplicación tendrá como objetivo principal funcionar como una plataforma web de visualización y seguimiento de resultados de partidos de fútbol.
Inicialmente está app estará diseñada para una Liga en el cual representaremos a cada equipo con un Nombre (por ejemplo, Parla FC) y un Estandarte/Escudo asociado. El usuario podrá consultar los resultados por fecha, las alineaciones, las plantillas completas y los detalles de cada partido disputado. Además se incluirá la capacidad del usuario de hacer login y elegir su equipo favorito, de poder reseñar a jugadores en cada partido (siempre y cuando tengan un usuario) y la capacidad de ver que ocurrió en el partido X de su equipo favorito (resultado, formacion inicial, Temporal, Campo en el que se jugó, Goles con el minuto en que se marcó y el dorsal y nombre el jugador goleador asociado, Tarjetas (Rojas o amarillas) con un formato similar al de los goles). Por último incluiremos una tabla de clasificación dinámica que mostrará la posición de cada equipo, su escudo, puntos obtenidos, partidos ganados, empatados y perdidos, además de la diferencia de goles y una tabla de máximos goleadores. 
Está aplicación estará diseñada con la finalidad de ayudar a aficionados, entrenadores, árbitros, jugadores etc a poder ver los resultados de su Liga preferida y tener un mejor control de los resultados de está y poder así seguir a su equipo favorito pudiendo estar informados de las últimas noticias de este.

### **Entidades**
Indicar las entidades principales que gestionará la aplicación y las relaciones entre ellas:

1. **[Entidad 1]**  Usuario (Periodista): Almacena la información del perfil del usuario (email, contraseña) y el equipo favorito sobre el que ejerce como periodista, pudiendo valorar la actuación de los jugadores en los partidos de dicho equipo.
2. **[Entidad 2]**: Equipo: Información del club (Nombre, Escudo, Estadio) y sus estadísticas acumuladas (puntos, goles).
3. **[Entidad 3]**: Partido: El evento central que une a dos equipos, con fecha, marcador final y estadio.
4. **[Entidad 4]**: Evento de Partido: Registro de cada hito (Gol, Tarjeta Roja/Amarilla, Cambio) vinculado a un minuto y a un jugador (Nombre + Dorsal).
5. **[Entidad 5]**: Valoración: Almacena los comentarios y puntuaciones que los usuarios registrados otorgan a los jugadores. Incluye el texto de la opinión y la calificación numérica.

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
      - Valorar evnetos de partidos
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

- **[Entidad 2]**: Equipo : Una imagen para el Escudo o Estandarte oficial del club, que se mostrará en la clasificación, en las fichas de partido y junto a las noticias relacionadas.
- **[Entidad 1]**: Usuario: Una imagen de Avatar para el perfil del usuario, que aparecerá junto a sus reseñas y valoraciones de los partidos.

---

## 🛠 **Práctica 1: Maquetación de páginas con HTML y CSS**

### **Vídeo de Demostración**
📹 **[Enlace al vídeo en YouTube](https://youtu.be/ix9c7odGRQ8)**
> Vídeo mostrando las principales funcionalidades de la aplicación web.

### **Diagrama de Navegación**
Diagrama que muestra cómo se navega entre las diferentes páginas de la aplicación:

![Diagrama de Navegación](images/navigation-diagram.png)

> [Descripción opcional del flujo de navegación: Ej: "El usuario puede acceder desde la página principal a todas las secciones mediante el menú de navegación. Los usuarios anónimos solo tienen acceso a las páginas públicas, mientras que los registrados pueden acceder a su perfil y panel de usuario."]

### **Capturas de Pantalla y Descripción de Páginas**

#### **1. Página Principal / Home**
![Página Principal](images/home-page.png)

> [Descripción breve: Ej: "Página de inicio que muestra los productos destacados, categorías principales y un banner promocional. Incluye barra de navegación y acceso a registro/login para usuarios no autenticados."]

#### **AQUÍ AÑADIR EL RESTO DE PÁGINAS**

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
|7| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |
|8| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |
|9| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |
|10| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 2 - Rubén Sánchez López**

Mi aportación se centró en desarrollar la interfaz pública y la experiencia del usuario, implementando la visualización dinámica de partidos, los eventos en tiempo real y el sistema de valoraciones.

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Maquetación inicial de detalle de partido y eventos.](https://github.com/DWS-2026/project-grupo-11/commit/7eb85548ad3438347230cdfd8fb8433d675e794f)  | [Archivo1](URL_archivo_1)   |
|2| [Creada la home con lista de partidos y cards](https://github.com/DWS-2026/project-grupo-11/commit/6d310db6cb3baf4e0c625b1688dff5f18b9af659)  | [index.html](https://github.com/DWS-2026/project-grupo-11/commit/6d310db6cb3baf4e0c625b1688dff5f18b9af659#diff-c9caeff2e64b497a71a0cc0d2ba6f659ff3d653b2913129a6a4ee33d3cf8f868)   |
|3| [Cambio de Liga Proyect a Liga Pro](https://github.com/DWS-2026/project-grupo-11/commit/8801129947b786fdf121545573446750e4631d0d)  | [player-ratings.html](https://github.com/DWS-2026/project-grupo-11/commit/8801129947b786fdf121545573446750e4631d0d#diff-e7cdad96aad0e5ff97fb92912a5800e7df8a6ea761dd181f97ec6a4f2b7b2a63)   |
|4| [Add my-ratings view to show user feedback history](https://github.com/DWS-2026/project-grupo-11/commit/f5e299fa37ff787d713e53d8fe88c1f12f62b398)  | [my-ratings.html](https://github.com/DWS-2026/project-grupo-11/commit/f5e299fa37ff787d713e53d8fe88c1f12f62b398#diff-46aa992c5047cba950f8a2215b7ea5ec32e8bc4115cbfb9ea782d218bfed6eac)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 3 - Sergio Sánchez López**

Me encargué de la autenticación de usuarios (login y registro), del diseño y maquetación del perfil de usuario, y de los formularios asociados, incluyendo la edición de perfil, selección de equipo favorito y uso de campos HTML bien tipados.

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Añadir diseño de la página de inicio de sesión con Bootstrap](https://github.com/DWS-2026/project-grupo-11/commit/e587ba3c9ebf57b68ff796de5aa2efa041aa495a)  | [login.html](URL_archivo_1)   |
|2| [Crear formulario de registro de usuario con tipos de entrada adecuados](https://github.com/DWS-2026/project-grupo-11/commit/e587ba3c9ebf57b68ff796de5aa2efa041aa495a)  | [registration.html](URL_archivo_2)   |
|3| [Implementar el diseño de la página de perfil de usuario](https://github.com/DWS-2026/project-grupo-11/commit/af5881ff9d0d928482aebbd6633f63f27d2c746b)  | [profile.html](URL_archivo_3)   |
|4| [Añadir formulario de edición de perfil y selección de equipo favorito](https://github.com/DWS-2026/project-grupo-11/commit/72ae6eb4f541356c113e9dc314d66011ae6cd737)  | [edit-profile.html](URL_archivo_4)   |
|5| [Commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

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
   git clone https://github.com/[usuario]/[nombre-repositorio].git
   cd [nombre-repositorio]
   ```

2. **AQUÍ INDICAR LO SIGUIENTES PASOS**

#### **Credenciales de prueba**
- **Usuario Admin**: usuario: `admin`, contraseña: `admin`
- **Usuario Registrado**: usuario: `user`, contraseña: `user`

### **Diagrama de Entidades de Base de Datos**

Diagrama mostrando las entidades, sus campos y relaciones:

![Diagrama Entidad-Relación](images/database-diagram.png)

> [Descripción opcional: Ej: "El diagrama muestra las 4 entidades principales: Usuario, Producto, Pedido y Categoría, con sus respectivos atributos y relaciones 1:N y N:M."]

### **Diagrama de Clases y Templates**

Diagrama de clases de la aplicación con diferenciación por colores o secciones:

![Diagrama de Clases](images/classes-diagram.png)

> [Descripción opcional del diagrama y relaciones principales]

### **Participación de Miembros en la Práctica 2**

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
