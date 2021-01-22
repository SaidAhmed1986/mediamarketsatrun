package org.mediamarktsaturn.order.order.rest;

import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletionStage;

@Component
@RequiredArgsConstructor
public class OrderManagerServer {

    @Value("${server.address:localhost}")
    private String serverHost;
    @Value("${server.port:8080}")
    private int serverPort;

    private final ActorSystem actorSystem;
    private final OrderRoute orderRoute;

    public void startHttpServer() {
        CompletionStage<ServerBinding> futureBinding =
                Http.get(actorSystem).newServerAt(serverHost, serverPort).bind(orderRoute.getRoute());
        futureBinding.whenComplete((binding, exception) -> {
            if (binding != null) {
                InetSocketAddress address = binding.localAddress();
                actorSystem.log().info("Server online at http://{}:{}/",
                        address.getHostString(),
                        address.getPort());
            } else {
                actorSystem.log().error("Failed to bind HTTP endpoint, terminating system", exception);
                actorSystem.terminate();
            }
        });
    }
}
