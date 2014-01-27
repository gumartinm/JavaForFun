package de.remote.agents.clients.app;

import java.net.URL;

import org.apache.log4j.Logger;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;

import de.remote.agents.SnakeEyes;
import de.remote.agents.SnakeEyesImpl;

public class ClientsMainTest {
    private static final Logger logger = Logger.getLogger(ClientsMainTest.class);

    public static void main(final String[] args) throws Throwable {

        final JsonRpcHttpClient client = new JsonRpcHttpClient(new URL(
                "http://127.0.0.1:8080/spring-mainapp/UserService.json"));

        final SnakeEyes user = client.invoke("createUser", new Object[] {
                "Shana M. O'Hara", "Snake Eyes" }, SnakeEyesImpl.class);

        logger.info("Canon: " + user.getCanon());
        logger.info("Name: " + user.getName());


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

    }

}
