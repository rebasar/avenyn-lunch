package net.rebworks.avenyn.lunch.service;

import net.rebworks.avenyn.lunch.domain.Menu;
import net.rebworks.avenyn.lunch.domain.Restaurant;
import net.rebworks.avenyn.lunch.service.slack.MenuFormatter;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SlackPushService {

    private final String webHookUrl;
    private final MenuFormatter menuFormatter;

    public SlackPushService(final String webHookUrl, final MenuFormatter menuFormatter) {
        this.webHookUrl = webHookUrl;
        this.menuFormatter = menuFormatter;
    }

    public PushResult push(List<Menu> menus) throws IOException {
        final OkHttpClient client = new OkHttpClient.Builder().build();
        final Stream<String> restaurants = menus.stream().map(Menu::getRestaurant).map(Restaurant::getName).map(s -> "\"" + s + "\"");
        final RequestBody requestBody = RequestBody.create(MediaType.get("application/json"),
                                                           menuFormatter.format(menus));
        final Request request = new Request.Builder().url(webHookUrl).post(requestBody).build();
        final Response response = client.newCall(request).execute();
        return new PushResult(response.isSuccessful(),
                              response.code(),
                              response.headers().toMultimap(),
                              response.body() == null ? "" : response.body().string(),
                              restaurants.collect(Collectors.joining(" ", "/polly \"Where should we go for lunch today?\" ", "")));
    }

    public static class PushResult {
        private final boolean success;
        private final int code;
        private final Map<String, List<String>> headers;
        private final String body;
        private final String pollyCommand;

        public PushResult(final boolean success, final int code, final Map<String, List<String>> headers, final String body, final String pollyCommand) {
            this.success = success;
            this.code = code;
            this.headers = headers;
            this.body = body;
            this.pollyCommand = pollyCommand;
        }

        public boolean isSuccess() {
            return success;
        }

        public int getCode() {
            return code;
        }

        public Map<String, List<String>> getHeaders() {
            return headers;
        }

        public String getBody() {
            return body;
        }

        public String getPollyCommand() {
            return pollyCommand;
        }
    }
}
