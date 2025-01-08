package org.example.frontend.BusinessLogic;

import org.example.frontend.dao.UserDAO;
import org.example.frontend.model.User;

import java.sql.SQLException;

public class UserService {
    private static int currentUserID;
    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }
    public int login(String email, String password) {
        this.currentUserID=this.userDAO.login(email, password);
        return this.currentUserID;
    }

    public int getCurrentUserID() {
        return currentUserID;
    }

    public boolean checkIfUserExists(String email) throws SQLException {
        return this.userDAO.userExist(email);
    }
    public void signUp(String firstName,String lastName,String email,String passwordText) throws SQLException {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(passwordText);
        this.userDAO.signup(user);
    }
}
