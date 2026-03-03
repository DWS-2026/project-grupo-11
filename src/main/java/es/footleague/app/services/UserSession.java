package es.footleague.app.services;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import es.footleague.app.model.User;
import java.io.Serializable;

@Component
@SessionScope
public class UserSession implements Serializable {

    private User user;

    // Guarda el usuario cuando hace login o se registra
    public void setUser(User user) {
        this.user = user;
    }

    // Devuelve el usuario actual
    public User getUser() {
        return user;
    }

    // Comprueba si hay alguien logueado
    public boolean isLoggedIn() {
        return user != null;
    }

    // Borra los datos al cerrar sesión
    public void logout() {
        this.user = null;
    }
}
