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
package com.vaadin.hummingbird.dom.impl;

import java.util.Optional;
import java.util.stream.Stream;

import com.vaadin.hummingbird.StateNode;
import com.vaadin.hummingbird.dom.Element;
import com.vaadin.hummingbird.nodefeature.ModelMap;
import com.vaadin.hummingbird.nodefeature.NodeFeature;
import com.vaadin.hummingbird.nodefeature.TemplateMap;
import com.vaadin.hummingbird.template.TextTemplateNode;
import com.vaadin.ui.Component;

/**
 * Handles storing and retrieval of the state information for a text node
 * defined in a template.
 *
 * @author Vaadin Ltd
 */
public class TemplateTextElementStateProvider
        extends AbstractTextElementStateProvider {

    @SuppressWarnings("unchecked")
    private static Class<? extends NodeFeature>[] features = new Class[] {
            TemplateMap.class, ModelMap.class };

    private final TextTemplateNode templateNode;

    /**
     * Creates a new text element state provider for the given template AST
     * node.
     *
     * @param templateNode
     *            the template AST node
     */
    public TemplateTextElementStateProvider(TextTemplateNode templateNode) {
        assert templateNode != null;
        this.templateNode = templateNode;
    }

    @Override
    public boolean supports(StateNode node) {
        return Stream.of(features).allMatch(node::hasFeature);
    }

    @Override
    public String getTextContent(StateNode node) {
        return templateNode.getTextBinding().getValue(node, "").toString();
    }

    @Override
    public void setTextContent(StateNode node, String textContent) {
        throw new UnsupportedOperationException(
                "Cannot modify text node defined in a template");
    }

    @Override
    public Element getParent(StateNode node) {
        return TemplateElementStateProvider.getParent(node, templateNode);
    }

    @Override
    public void setComponent(StateNode node, Component component) {
        throw new UnsupportedOperationException(
                "Cannot modify text node defined in a template");
    }

    @Override
    public Optional<Component> getComponent(StateNode node) {
        return Optional.empty();
    }

}
