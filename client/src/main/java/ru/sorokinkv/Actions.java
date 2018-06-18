package ru.sorokinkv;

import java.io.IOException;

import static ru.sorokinkv.Controller.showAlert;

class Actions {
    Controller controller = new Controller();
     public int ActionsAuth(String str)  {

        if (str.startsWith("/error@ ")) {
            String error = str.split("@ ")[1];
            showAlert(error);
        }

        if (str.startsWith("/authok@ ")) {
            System.out.println(controller.getAuthorized());
            return 1;
        }
        if (str.startsWith("/regok@ ")) {
            String info = str.split("@ ")[1];
            showAlert(info);
            System.out.println("RegOK" + controller.getNick());
           return 0;
        }


        System.out.println("Two " + str + " Auth: " + controller.getAuthorized());
        return 2;
    }


    public void Actions(String str) throws IOException {
        if (str.startsWith("/kick@ ")) {
            controller.dropConnection();
        }
    }
}
