package net.rebworks.avenyn.lunch.service;

import net.rebworks.avenyn.lunch.domain.Menu;
import net.rebworks.avenyn.lunch.domain.Restaurant;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class MenuFetcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(MenuFetcher.class);

    private final String lunchUrl;

    public MenuFetcher(final String lunchUrl) {
        this.lunchUrl = lunchUrl;
    }

    private static class ParsedMenu {
        private final String url;
        private final String logoUrl;
        private final String[] items;

        private ParsedMenu(final String url, final String logoUrl, final String[] items) {
            this.url = url;
            this.logoUrl = logoUrl;
            this.items = items;
        }

        public String getUrl() {
            return url;
        }

        public String getLogoUrl() {
            return logoUrl;
        }

        public String[] getItems() {
            return items;
        }

        @Override
        public String toString() {
            return "ParsedMenu{" +
                    "url='" + url + '\'' +
                    ", logoUrl='" + logoUrl + '\'' +
                    ", items=" + Arrays.toString(items) +
                    '}';
        }
    }

    private String[] itemize(final Element menu) {
        return menu.childNodes()
                   .stream()
                   .filter(e -> e instanceof TextNode)
                   .map(TextNode.class::cast)
                   .map(TextNode::text)
                   .map(String::trim)
                   .filter(s -> !s.isEmpty())
                   .toArray(String[]::new);
    }

    public List<Menu> fetch(List<Restaurant> restaurants) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder().followRedirects(true).build();
        Request request = new Request.Builder().url(lunchUrl).build();
        final Response response = client.newCall(request).execute();
        if (!response.isSuccessful()){
            LOGGER.warn("Request failed. HTTP Status: {}", response.code());
            LOGGER.warn("Response headers follows");
            response.headers().toMultimap().forEach((k, v) -> v.forEach(value -> LOGGER.warn("{}: {}", k, value)));
            final ResponseBody body = response.body();
            if (body != null) {
                LOGGER.warn("Response body: {}", body.string());
            }
            return Collections.emptyList();
        }
        final ResponseBody body = response.body();
        if (body == null) {
            LOGGER.warn("Cannot fetch the response body. HTTP Status: {}", response.code());
            return Collections.emptyList();
        }

        final Document document = Jsoup.parse(body.string());
        return extractMenusFor(document, restaurants);
    }

    private List<Menu> extractMenusFor(Document document, List<Restaurant> restaurants) {
        final Elements allMenus = document.body().getElementsByClass("company");
        final Map<String, ParsedMenu> parsedMenus = allMenus.stream()
                                                            .flatMap(this::toMenu)
                                                            .collect(toMap(ParsedMenu::getUrl, Function.identity(), (a, b) -> a));
        return restaurants.stream().flatMap(r -> buildMenu(r, parsedMenus)).collect(Collectors.toList());
    }

    private Stream<Menu> buildMenu(final Restaurant restaurant, final Map<String, ParsedMenu> parsedMenus) {
        final ParsedMenu parsedMenu = parsedMenus.get(restaurant.getUrl());
        if (parsedMenu == null) return Stream.empty();
        return Stream.of(new Menu(restaurant, parsedMenu.logoUrl, restaurant.getModifier().apply(parsedMenu.items)));
    }

    private Stream<ParsedMenu> toMenu(Element element) {
        final Element link = element.children().first();
        if (link == null) {
            LOGGER.warn("No child element found for {}", element);
            return Stream.empty();
        }
        final String url = link.attr("href");
        if (url == null) {
            LOGGER.warn("Cannot get the URL from link '{}'", link);
            return Stream.empty();
        }
        final String logoUrl = link.getElementsByClass("logo").first().attr("src");
        if (logoUrl == null){
            LOGGER.debug("No logo image found under '{}'", link);
        }
        final Elements companyIntros = element.getElementsByClass("company-intro");
        if (companyIntros.isEmpty()) {
            LOGGER.warn("Cannot find the menu container element");
            return Stream.empty();
        }
        final Element menu = companyIntros.first().nextElementSibling();
        if (menu == null) {
            LOGGER.warn("Element has no siblings {}", companyIntros.first());
            return Stream.empty();
        }
        final String[] items = itemize(menu);
        return Stream.of(new ParsedMenu(url, logoUrl, items));
    }

}
