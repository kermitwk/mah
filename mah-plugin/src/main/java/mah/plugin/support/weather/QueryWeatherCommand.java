/**
 * MIT License
 *
 * Copyright (c) 2017 zgqq
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package mah.plugin.support.weather;

import mah.common.json.JsonArr;
import mah.common.json.JsonObj;
import mah.common.json.JsonUtils;
import mah.common.util.HttpUtils;
import mah.common.util.StringUtils;
import mah.common.util.XmlUtils;
import mah.plugin.command.PluginCommandSupport;
import mah.plugin.config.XmlConfigurable;
import mah.ui.icon.Icon;
import mah.ui.layout.ClassicItemListLayout;
import mah.ui.pane.item.FullItemImpl;
import mah.ui.pane.item.TextItem;
import mah.ui.pane.item.TextItemImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zgq on 16-1-02.
 */
public class QueryWeatherCommand extends PluginCommandSupport implements XmlConfigurable {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryWeatherCommand.class);
    private String url = "http://api.map.baidu.com/telematics/v3/weather?output=json&ak=Gy7SGUigZ4HxGYDaq9azWy09&location=";
    private ClassicItemListLayout layout;
    private String defaultCity;

    public QueryWeatherCommand() {
        init();
        layout = getLayoutFactory().createClassicItemListLayout();
    }

    public void init() {
        addTriggerEventHandler(event -> triggerHook());
        addCommonFilterEventHandler(event -> {
            filterHook(event.getContent());
        });
    }

    private InputStream downloadImage(String url) {
        InputStream inputStream = HttpUtils.getInputStream(url);
        return inputStream;
    }

    private FullItemImpl createResult(JsonObj weatherData) {
        String date = weatherData.getString("date");
        String weather = weatherData.getString("weather");
        String wind = weatherData.getString("wind");
        String temperature = weatherData.getString("temperature");
        String dayPictureUrl = weatherData.getString("dayPictureUrl");
        InputStream inputStream = downloadImage(dayPictureUrl);
        return new FullItemImpl.Builder(date + "    " + weather) //
                .icon(Icon.valueOf(dayPictureUrl)) //
                .description(wind + "," + temperature).build();
    }

    private FullItemImpl createQueryingItem() {
        FullItemImpl.Builder builder = new FullItemImpl.Builder("Weather Query");
        return builder.icon(Icon.valueOf("weather/querying.png"))
                .description("querying")
                .build();
    }

    private void showQuerying() {
        layout.updateItems(createQueryingItem());
    }

    private void showError(JsonObj weatherData) {
        FullItemImpl errorItem = new FullItemImpl.Builder("Query error") //
                .description(weatherData.getString("status")) //
                .icon(Icon.valueOf("weather/unknown.png")).build();
        layout.updateItems(errorItem);
    }

    protected void triggerHook() throws Exception {
        if (StringUtils.isNotEmpty(defaultCity)) {
            queryWeather(defaultCity);
        }
    }

    private void queryWeather(String city) {
        showQuerying();
        JsonObj json;
        String api = "";
        try {
            api = url + city;
            json = JsonUtils.getJson(api);
        } catch (Exception e) {
            showQueryError();
            LOGGER.error("Unable to fetch url " + api,e);
            return;
        }
        int code = json.getInt("error");
        if (code == 0) {
            JsonArr results = json.getJsonArr("results");
            if (results != null && results.size() > 0) {
                List<FullItemImpl> resultItems = new ArrayList<>();
                JsonObj result = (JsonObj) results.get(0);
                JsonArr weatherdatas = result.getJsonArr("weather_data");
                for (Object weatherdataObj : weatherdatas) {
                    JsonObj weatherdata = (JsonObj) weatherdataObj;
                    resultItems.add(createResult(weatherdata));
                }
                layout.updateItems(resultItems);
            }
        } else {
            showError(json);
        }
    }

    private void showQueryError() {
        TextItem network = new TextItemImpl.Builder("无法查询天气,请检查网络").build();
        layout.updateItems(network);
    }

    protected void filterHook(String content) throws Exception {
        if (content != null && content.trim().length() > 0) {
            queryWeather(content);
        }
    }

    @Override
    public String getName() {
        return "QueryWeather";
    }

    @Override
    public void configure(Node node) throws Exception {
        if (node != null) {
            this.defaultCity = XmlUtils.getChildNodeText(node, "defaultCity");
        }
    }
}

