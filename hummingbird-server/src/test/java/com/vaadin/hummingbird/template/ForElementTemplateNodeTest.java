/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.hummingbird.template;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.hummingbird.StateNode;
import com.vaadin.hummingbird.dom.Element;
import com.vaadin.hummingbird.dom.TemplateElementStateProviderTest;
import com.vaadin.hummingbird.nodefeature.ComponentMapping;
import com.vaadin.hummingbird.nodefeature.ModelList;
import com.vaadin.hummingbird.nodefeature.ModelMap;
import com.vaadin.hummingbird.nodefeature.ParentGeneratorHolder;
import com.vaadin.hummingbird.nodefeature.TemplateMap;
import com.vaadin.hummingbird.nodefeature.TemplateOverridesMap;

public class ForElementTemplateNodeTest {

    @Test
    public void noChildren() {
        TemplateNode templateNode = TemplateParser
                .parse("<div><li *ngFor='let item of items'></li></div>");
        Element div = getElement(templateNode);

        StateNode model = createModel(div, "items");
        Assert.assertEquals(1, templateNode.getGeneratedElementCount(model));
        Assert.assertEquals(0,
                templateNode.getChild(0).getGeneratedElementCount(model));
    }

    @Test
    public void oneChildNoDataBinding() {
        TemplateNode divTemplateNode = TemplateParser.parse(
                "<div><li *ngFor='let item of items' foo='static' /></div>");
        Element div = getElement(divTemplateNode);
        TemplateNode forTemplateNode = divTemplateNode.getChild(0);

        StateNode model = createModel(div, "items", Collections.emptyMap());

        Assert.assertEquals(1, forTemplateNode.getGeneratedElementCount(model));

        Assert.assertEquals(1, div.getChildCount());
        Element li = div.getChild(0);
        Assert.assertEquals("static", li.getAttribute("foo"));
        Assert.assertEquals(div, li.getParent());
    }

    private Element getElement(TemplateNode templateNode) {
        return TemplateElementStateProviderTest.createElement(templateNode);
    }

    @Test
    public void twoChildrenNoDataBinding() {
        TemplateNode divTemplateNode = TemplateParser.parse(
                "<div><li *ngFor='let item of items' foo='static' /></div>");
        Element div = getElement(divTemplateNode);
        TemplateNode forTemplateNode = divTemplateNode.getChild(0);

        StateNode model = createModel(div, "items", Collections.emptyMap(),
                Collections.emptyMap());

        Assert.assertEquals(2, forTemplateNode.getGeneratedElementCount(model));
        Assert.assertEquals("static",
                forTemplateNode.getElement(0, model).getAttribute("foo"));
        Assert.assertEquals("static",
                forTemplateNode.getElement(1, model).getAttribute("foo"));

        Assert.assertEquals(div, div.getChild(0).getParent());
        Assert.assertEquals(div, div.getChild(1).getParent());

    }

    @Test
    public void oneChildWithTextBinding() {
        TemplateNode divTemplateNode = TemplateParser.parse(
                "<div><li *ngFor='let item of items' foo='static'>{{text}}</li></div>");
        TemplateNode forTemplateNode = divTemplateNode.getChild(0);
        Element div = getElement(divTemplateNode);

        HashMap<String, String> data = new HashMap<>();
        data.put("text", "textValue");
        StateNode model = createModel(div, "items", data);

        Assert.assertEquals(1, forTemplateNode.getGeneratedElementCount(model));
        Assert.assertEquals("textValue",
                forTemplateNode.getElement(0, model).getOwnTextContent());
    }

    @Test
    public void twoChildrenWithTextBinding() {
        TemplateNode divTemplateNode = TemplateParser.parse(
                "<div><li *ngFor='let item of items' foo='static'>{{text}}</li></div>");
        TemplateNode templateNode = divTemplateNode.getChild(0);
        Element div = getElement(divTemplateNode);

        HashMap<String, String> data1 = new HashMap<>();
        data1.put("text", "textValue1");
        HashMap<String, String> data2 = new HashMap<>();
        data2.put("text", "textValue2");
        StateNode model = createModel(div, "items", data1, data2);

        Assert.assertEquals(2, templateNode.getGeneratedElementCount(model));
        Assert.assertEquals("textValue1",
                templateNode.getElement(0, model).getOwnTextContent());
        Assert.assertEquals("textValue2",
                templateNode.getElement(1, model).getOwnTextContent());
    }

    @Test
    public void oneChildWithPropertyBinding() {
        TemplateNode divTemplateNode = TemplateParser.parse(
                "<div><li *ngFor='let item of items' [value]='value'></div>");
        TemplateNode templateNode = divTemplateNode.getChild(0);
        Element div = getElement(divTemplateNode);

        HashMap<String, String> data = new HashMap<>();
        data.put("value", "propertyValue");
        StateNode model = createModel(div, "items", data);

        Assert.assertEquals(1, templateNode.getGeneratedElementCount(model));
        Assert.assertEquals("propertyValue",
                templateNode.getElement(0, model).getProperty("value"));
    }

    @Test
    public void twoChildrenWithPropertyBinding() {
        TemplateNode divTemplateNode = TemplateParser.parse(
                "<div><li *ngFor='let item of items' [value]='value'></div>");
        TemplateNode templateNode = divTemplateNode.getChild(0);
        Element div = getElement(divTemplateNode);

        HashMap<String, String> data1 = new HashMap<>();
        data1.put("value", "PropertyValue1");
        HashMap<String, String> data2 = new HashMap<>();
        data2.put("value", "PropertyValue2");
        StateNode model = createModel(div, "items", data1, data2);

        Assert.assertEquals(2, templateNode.getGeneratedElementCount(model));
        Assert.assertEquals("PropertyValue1",
                templateNode.getElement(0, model).getProperty("value"));
        Assert.assertEquals("PropertyValue2",
                templateNode.getElement(1, model).getProperty("value"));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @SafeVarargs
    private final StateNode createModel(Element templateRootElement, String key,
            Map<String, String>... map) {

        Class[] classes = new Class[] { TemplateMap.class,
                TemplateOverridesMap.class, ComponentMapping.class,
                ModelMap.class, ParentGeneratorHolder.class, ModelList.class };
        StateNode modelList = new StateNode(classes);

        for (Map<String, String> item : map) {
            StateNode modelItem = new StateNode(classes);
            ModelMap modelItemMap = modelItem.getFeature(ModelMap.class);
            for (String itemKey : item.keySet()) {
                modelItemMap.setValue(itemKey, item.get(itemKey));
            }
            modelList.getFeature(ModelList.class).add(modelItem);
        }
        StateNode node = templateRootElement.getNode();
        node.getFeature(ModelMap.class).setValue(key, modelList);
        return node;
    }
}
