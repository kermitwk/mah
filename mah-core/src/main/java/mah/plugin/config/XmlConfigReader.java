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
package mah.plugin.config;

import mah.app.config.Config;
import mah.app.config.XmlConfig;
import mah.plugin.PluginException;
import mah.plugin.PluginMetainfo;
import org.w3c.dom.Document;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * Created by zgq on 2017-01-08 19:58
 */
public class XmlConfigReader {
    private XmlPluginMetainfoParser pluginMetainfosParser;
    private XmlPluginConfigParser pluginConfigParser;

    public XmlConfigReader(File pluginDir, Config config) {
        if (!(config instanceof XmlConfig)) {
            throw new IllegalStateException("Config must be XmlConfig!Actual:" + config);
        }
        XmlConfig xmlConfig = (XmlConfig) config;
        try {
            Document document = xmlConfig.getDocument();
            InputStream resource = getClass().getClassLoader().getResourceAsStream("META-INF/plugin.xml");
            this.pluginMetainfosParser = new XmlPluginMetainfoParser(pluginDir,resource);
            this.pluginConfigParser = new XmlPluginConfigParser(document);
        } catch (Exception e) {
            throw new PluginException(e);
        }
    }

    public List<PluginMetainfo> getActivePluginMetainfos() {
        List<String> activePlugins = getActivePluginNames();
        List<PluginMetainfo> activePluginMetainfos = getPluginMetainfos(activePlugins);
        return activePluginMetainfos;
    }

    public List<PluginMetainfo> getPluginMetainfos(List<String> pluginNames) {
        return pluginMetainfosParser.parsePluginMetainfos(pluginNames);
    }

    public List<String> getActivePluginNames() {
        return this.pluginConfigParser.parseActivePlugins();
    }

    public List<? extends PluginConfig> parsePluginConfigs(List<String> pluginNames) {
        return pluginConfigParser.parsePluginConfigs(pluginNames);
    }
}
