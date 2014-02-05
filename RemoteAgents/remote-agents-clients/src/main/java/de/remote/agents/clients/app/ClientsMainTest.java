package de.remote.agents.clients.app;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import de.remote.agents.services.CurrentDateService;

public class ClientsMainTest {
    private static final Logger logger = Logger.getLogger(ClientsMainTest.class);

    public static void main(final String[] args) throws Throwable {

        // final JsonRpcHttpClient client = new JsonRpcHttpClient(new URL(
        // "http://127.0.0.1:8080/spring-mainapp/UserService.json"));
        //
        // final SnakeEyes user = client.invoke("createUser", new Object[] {
        // "Shana M. O'Hara", "Snake Eyes" }, SnakeEyesImpl.class);
        //
        // logger.info("Canon: " + user.getCanon());
        // logger.info("Name: " + user.getName());


        // I need a way to set the return type parameter, otherwise I am not
        // going to be able to use this JSON-RPC plugin :(

        // logger.info("Starting application");
        // SpringContextLocator.getInstance();
        //
        // final UserService test = (UserService) SpringContextLocator
        // .getInstance().getBean("remoteUserService");
        //
        // final SnakeEyes snakeEyes = test.createUser("Shana M. O'Hara",
        // "Snake Eyes");
        //
        // logger.info("Canon: " + snakeEyes.getCanon());
        // logger.info("Name: " + snakeEyes.getName());

        // I need a way to set the return type parameter, otherwise I am not
        // going to be able to use this JSON-RPC plugin :(

        logger.info("Starting application");
        SpringContextLocator.getInstance();

        final CurrentDateService remoteCurrentDate = (CurrentDateService) SpringContextLocator
                .getInstance().getBean("remoteCurrentDateService");


        final RemoteGUIExample window = new RemoteGUIExample();
        final Runnable task = new Runnable() {

            @Override
            public void run() {
                window.open();
            }

        };

        final Thread GUIThread = new Thread(task, "GUI-Thread");
        // GUIThread.setUncaughtExceptionHandler(new DriverHWUncaughtExceptionHandler());
        GUIThread.start();

        final Timer t = new Timer("Timer-Thread", false);
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                final String remoteDate = remoteCurrentDate.getCurrentDate();
                window.updateTextBox(remoteDate);
            }
        }, 1000, 1000);

    }

    //    private class DriverHWUncaughtExceptionHandler implements UncaughtExceptionHandler {
    //
    //        @Override
    //        public void uncaughtException(final Thread t, final Throwable e) {
    //            logger.warn(
    //                    "Exception not expected while running thread " + t.getName(), e);
    //        }
    //    }

}
