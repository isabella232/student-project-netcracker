/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.netcrackerteam.GUI;

import com.vaadin.ui.*;

import ua.netcrackerteam.DAO.Entities.Form;
import ua.netcrackerteam.configuration.HibernateFactory;
import ua.netcrackerteam.configuration.Logable;
import ua.netcrackerteam.controller.GeneralController;

import java.sql.SQLException;

import static ua.netcrackerteam.validation.SystemMessages.*;

/**
 * Login form
 * @author Anna Kushnirenko
 */
@SuppressWarnings("serial")
class EnterWindow extends Window implements Logable {
    private LoginForm loginForm = null;
    public static final int MODE_GUEST = -1;
    private int mode = MODE_GUEST;
    private String userName;
    private String userPassword;
    private Notification error;
    private Form form;

    public EnterWindow(final MainPage mainPage) {
        setModal(true);
        setWidth(300,UNITS_PIXELS);
        setResizable(false);
        center();
        setCaption("Вход");
        loginForm = new LoginForm();
        loginForm.setWidth("100%");
        loginForm.setHeight("110px");
        loginForm.setLoginButtonCaption("Войти");
        loginForm.setPasswordCaption("Пароль");
        loginForm.setUsernameCaption("Логин");
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("100%");
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.addComponent(loginForm);
        layout.setComponentAlignment(loginForm, Alignment.BOTTOM_CENTER);
        addComponent(layout);
        loginForm.addListener(new LoginForm.LoginListener() {
            public void onLogin(LoginForm.LoginEvent event) {
                userName = event.getLoginParameter("username");
                userPassword = event.getLoginParameter("password");
                try {
                    if(GeneralController.checkUsersAvailability(userName, userPassword)){
                        if(GeneralController.checkUserBan(userName)){
                            //GeneralController.setAuditInterviews(1, "User try to login to application", userName, new Date());
                            getWindow().showNotification("Вы забанены ! Уважаемый, " + userName + ", Вы были забанены. \n" +
                                    "По данному вопросу обращайтесь к Администратору.", Notification.TYPE_TRAY_NOTIFICATION);
                        } else {
                            //GeneralController.setAuditInterviews(1, "User try to login to application", userName, new Date());
                            mode = GeneralController.checkLogin(userName, event.getLoginParameter("password"));
                            mainPage.changeMode(mode, userName);
                            /*form = HibernateFactory.getInstance().getStudentDAO().getFormByUserName(userName);
                            if(form != null){
                                if(form.getInterview() == null){
                                    HibernateFactory.getInstance().getStudentDAO().romoveForm(form);
                                }
                            }*/
                            loginForm.removeListener(this);
                            EnterWindow.this.close();
                        }
                    } else {
                        //GeneralController.setAuditInterviews(1, "User try to login to application", userName, new Date());
                        getWindow().showNotification(LOGIN_ERROR.getNotification());
                    }
                } catch(SQLException e){
                    mainPage.getMainWindow().showNotification(SQL_CONNECTION_ERROR.getNotification());
                    return;
                } catch(RuntimeException e){
                    mainPage.getMainWindow().showNotification(RUNTIME_ERROR.getNotification());
                    e.printStackTrace();
                    mainPage.getMainWindow().executeJavaScript("window.location.reload();");
                    return;
                }

            }
        });
    }
}
